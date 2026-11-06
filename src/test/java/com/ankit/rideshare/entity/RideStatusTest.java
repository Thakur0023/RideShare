package com.ankit.rideshare.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RideStatusTest {

    @Test
    void happyPathTransitionsAreAllowed() {
        assertTrue(RideStatus.REQUESTED.canTransitionTo(RideStatus.ACCEPTED));
        assertTrue(RideStatus.ACCEPTED.canTransitionTo(RideStatus.DRIVER_ARRIVED));
        assertTrue(RideStatus.DRIVER_ARRIVED.canTransitionTo(RideStatus.IN_PROGRESS));
        assertTrue(RideStatus.IN_PROGRESS.canTransitionTo(RideStatus.COMPLETED));
    }

    @Test
    void rideCanBeCancelledBeforeItEnds() {
        assertTrue(RideStatus.REQUESTED.canTransitionTo(RideStatus.CANCELLED));
        assertTrue(RideStatus.ACCEPTED.canTransitionTo(RideStatus.CANCELLED));
        assertTrue(RideStatus.DRIVER_ARRIVED.canTransitionTo(RideStatus.CANCELLED));
        assertTrue(RideStatus.IN_PROGRESS.canTransitionTo(RideStatus.CANCELLED));
    }

    @Test
    void finalStatesCannotMove() {
        assertFalse(RideStatus.COMPLETED.canTransitionTo(RideStatus.REQUESTED));
        assertFalse(RideStatus.COMPLETED.canTransitionTo(RideStatus.IN_PROGRESS));
        assertFalse(RideStatus.CANCELLED.canTransitionTo(RideStatus.REQUESTED));
        assertFalse(RideStatus.CANCELLED.canTransitionTo(RideStatus.ACCEPTED));
    }

    @Test
    void rideCannotSkipSteps() {
        assertFalse(RideStatus.REQUESTED.canTransitionTo(RideStatus.IN_PROGRESS));
        assertFalse(RideStatus.REQUESTED.canTransitionTo(RideStatus.COMPLETED));
        assertFalse(RideStatus.ACCEPTED.canTransitionTo(RideStatus.COMPLETED));
        assertFalse(RideStatus.DRIVER_ARRIVED.canTransitionTo(RideStatus.COMPLETED));
    }

    @Test
    void rideCannotGoBackwards() {
        assertFalse(RideStatus.IN_PROGRESS.canTransitionTo(RideStatus.DRIVER_ARRIVED));
        assertFalse(RideStatus.ACCEPTED.canTransitionTo(RideStatus.REQUESTED));
    }

    @Test
    void noDriverFoundCanBeRetried() {
        assertTrue(RideStatus.NO_DRIVER_FOUND.canTransitionTo(RideStatus.REQUESTED));
        assertFalse(RideStatus.NO_DRIVER_FOUND.canTransitionTo(RideStatus.ACCEPTED));
    }
}