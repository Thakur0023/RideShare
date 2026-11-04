package com.ankit.rideshare.websocket;

import com.ankit.rideshare.dto.LocationUpdateRequest;
import com.ankit.rideshare.entity.Ride;
import com.ankit.rideshare.repository.RideRepository;
import com.ankit.rideshare.service.DriverService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
public class LocationWebSocketHandler extends TextWebSocketHandler {

    private final RideRepository rideRepository;
    private final DriverService driverService;
    private final ObjectMapper objectMapper;

    private final Map<Long, Set<WebSocketSession>> rooms = new ConcurrentHashMap<>();

    public LocationWebSocketHandler(RideRepository rideRepository,
                                    DriverService driverService,
                                    ObjectMapper objectMapper) {
        this.rideRepository = rideRepository;
        this.driverService = driverService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {
        Long userId = Long.valueOf(session.getPrincipal().getName());
        Long rideId = extractRideId(session);

        Ride ride = rideRepository.findById(rideId).orElse(null);
        boolean allowed = ride != null
                && (ride.getRiderId().equals(userId)
                || (ride.getDriverId() != null && ride.getDriverId().equals(userId)));

        if (!allowed) {
            session.close(CloseStatus.POLICY_VIOLATION);
            return;
        }
        rooms.computeIfAbsent(rideId, id -> new CopyOnWriteArraySet<>()).add(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        Long userId = Long.valueOf(session.getPrincipal().getName());
        Long rideId = extractRideId(session);

        Ride ride = rideRepository.findById(rideId).orElse(null);
        if (ride == null || ride.getDriverId() == null || !ride.getDriverId().equals(userId)) {
            return;
        }

        LocationUpdateRequest location;
        try {
            location = objectMapper.readValue(message.getPayload(), LocationUpdateRequest.class);
        } catch (JacksonException e) {
            return;
        }

        driverService.updateLocation(userId, location.lat(), location.lng());

        Set<WebSocketSession> room = rooms.get(rideId);
        if (room == null) {
            return;
        }
        for (WebSocketSession peer : room) {
            if (peer.isOpen() && !peer.getId().equals(session.getId())) {
                synchronized (peer) {
                    peer.sendMessage(message);
                }
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Set<WebSocketSession> room = rooms.get(extractRideId(session));
        if (room != null) {
            room.remove(session);
        }
    }

    private Long extractRideId(WebSocketSession session) {
        String[] parts = session.getUri().getPath().split("/");
        return Long.valueOf(parts[3]);
    }
}