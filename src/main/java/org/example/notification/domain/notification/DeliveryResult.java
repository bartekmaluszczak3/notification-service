package org.example.notification.domain.notification;

public record DeliveryResult(DeliveryStatus deliveryStatus, String message) {

    public static DeliveryResult success() {
        return new DeliveryResult(DeliveryStatus.SUCCESS, null);
    }

    public static DeliveryResult failed(String message) {
        return new DeliveryResult(DeliveryStatus.FAILED, message);
    }

    public static DeliveryResult skipped(String reason) {
        return new DeliveryResult(DeliveryStatus.SKIPPED, reason);
    }
}
