package com.ankit.rideshare.entity;

public enum RideStatus {
    REQUESTED,
    ACCEPTED,
    DRIVER_ARRIVED,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED,
    NO_DRIVER_FOUND;

    public boolean canTransitionTo(RideStatus target) {
        return switch (this) {
            case REQUESTED -> target == ACCEPTED || target == CANCELLED || target == NO_DRIVER_FOUND;
            case ACCEPTED -> target == DRIVER_ARRIVED || target == CANCELLED;
            case DRIVER_ARRIVED -> target == IN_PROGRESS || target == CANCELLED;
            case IN_PROGRESS -> target == COMPLETED || target == CANCELLED;
            case NO_DRIVER_FOUND -> target == REQUESTED;
            case COMPLETED, CANCELLED -> false;
        };
    }
}
