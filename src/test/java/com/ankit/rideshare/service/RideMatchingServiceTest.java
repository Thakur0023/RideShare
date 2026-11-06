package com.ankit.rideshare.service;

import com.ankit.rideshare.entity.DriverProfile;
import com.ankit.rideshare.repository.DriverProfileRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RideMatchingServiceTest {

    @Mock
    private DriverProfileRepository driverProfileRepository;

    @InjectMocks
    private RideMatchingService matchingService;

    private DriverProfile driverAt(Long id, double lat, double lng) {
        return DriverProfile.builder()
                .userId(id)
                .vehicleType("Sedan")
                .isAvailable(true)
                .currentLat(lat)
                .currentLng(lng)
                .build();
    }

    @Test
    void nearestDriverIsClaimedFirst() {
        DriverProfile near = driverAt(1L, 28.6140, 77.2091);
        DriverProfile far = driverAt(2L, 28.6300, 77.2300);

        when(driverProfileRepository.findAvailableInBoundingBox(
                anyDouble(), anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(List.of(far, near)); // deliberately in the wrong order
        when(driverProfileRepository.claimDriver(1L)).thenReturn(1);

        Optional<DriverProfile> result = matchingService.assignNearestDriver(28.6139, 77.2090);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getUserId());
        verify(driverProfileRepository, never()).claimDriver(2L);
    }

    @Test
    void nextDriverIsTriedWhenTheNearestIsAlreadyTaken() {
        DriverProfile near = driverAt(1L, 28.6140, 77.2091);
        DriverProfile far = driverAt(2L, 28.6300, 77.2300);

        when(driverProfileRepository.findAvailableInBoundingBox(
                anyDouble(), anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(List.of(near, far));
        when(driverProfileRepository.claimDriver(1L)).thenReturn(0); // someone else got them
        when(driverProfileRepository.claimDriver(2L)).thenReturn(1);

        Optional<DriverProfile> result = matchingService.assignNearestDriver(28.6139, 77.2090);

        assertTrue(result.isPresent());
        assertEquals(2L, result.get().getUserId());
    }

    @Test
    void emptyResultWhenNoDriverCanBeClaimed() {
        DriverProfile only = driverAt(1L, 28.6140, 77.2091);

        when(driverProfileRepository.findAvailableInBoundingBox(
                anyDouble(), anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(List.of(only));
        when(driverProfileRepository.claimDriver(1L)).thenReturn(0);

        Optional<DriverProfile> result = matchingService.assignNearestDriver(28.6139, 77.2090);

        assertTrue(result.isEmpty());
    }

    @Test
    void emptyResultWhenNoDriversAreNearby() {
        when(driverProfileRepository.findAvailableInBoundingBox(
                anyDouble(), anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(List.of());

        Optional<DriverProfile> result = matchingService.assignNearestDriver(28.6139, 77.2090);

        assertTrue(result.isEmpty());
    }
}