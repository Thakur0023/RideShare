package com.ankit.rideshare.service;

import com.ankit.rideshare.entity.DriverProfile;
import com.ankit.rideshare.repository.DriverProfileRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PricingServiceTest {

    @Mock
    private DriverProfileRepository driverProfileRepository;

    @InjectMocks
    private PricingService pricingService;

    @Test
    void zeroDistanceCostsOnlyTheBaseFare() {
        when(driverProfileRepository.findAvailableInBoundingBox(
                anyDouble(), anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(List.of(new DriverProfile(), new DriverProfile(), new DriverProfile()));

        double fare = pricingService.estimateFare(28.6139, 77.2090, 28.6139, 77.2090);

        assertEquals(40.0, fare, 0.01);
    }

    @Test
    void noSurgeWhenThreeOrMoreDriversAreNearby() {
        when(driverProfileRepository.findAvailableInBoundingBox(
                anyDouble(), anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(List.of(new DriverProfile(), new DriverProfile(), new DriverProfile()));

        double fare = pricingService.estimateFare(28.6139, 77.2090, 28.6500, 77.2300);

        assertEquals(94.09, fare, 0.05);
    }

    @Test
    void surgeAppliesWhenFewerThanThreeDriversAreNearby() {
        when(driverProfileRepository.findAvailableInBoundingBox(
                anyDouble(), anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(List.of(new DriverProfile()));

        double fare = pricingService.estimateFare(28.6139, 77.2090, 28.6500, 77.2300);

        assertEquals(121.13, fare, 0.05);
    }

    @Test
    void surgeAppliesWhenNoDriversAreNearby() {
        when(driverProfileRepository.findAvailableInBoundingBox(
                anyDouble(), anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(List.of());

        double fare = pricingService.estimateFare(28.6139, 77.2090, 28.6500, 77.2300);

        assertEquals(121.13, fare, 0.05);
    }
}