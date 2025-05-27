package com.hotelbooking.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents a hotel booking.
 * This is a simple POJO (Plain Old Java Object) for data transfer and in-memory storage.
 */
public class Booking {
    private String id;
    private String hotelName;
    private String guestName;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private String status; // e.g., CONFIRMED, PENDING, CANCELLED

    /**
     * Default constructor for Jackson deserialization.
     */
    public Booking() {
    }

    /**
     * Constructs a new Booking instance.
     *
     * @param id The unique identifier for the booking.
     * @param hotelName The name of the hotel.
     * @param guestName The name of the guest.
     * @param checkInDate The check-in date.
     * @param checkOutDate The check-out date.
     * @param status The current status of the booking.
     */
    public Booking(String id, String hotelName, String guestName, LocalDate checkInDate, LocalDate checkOutDate, String status) {
        this.id = id;
        this.hotelName = hotelName;
        this.guestName = guestName;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.status = status;
    }

    // Getters and Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHotelName() {
        return hotelName;
    }

    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(LocalDate checkInDate) {
        this.checkInDate = checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(LocalDate checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return Objects.equals(id, booking.id) &&
                Objects.equals(hotelName, booking.hotelName) &&
                Objects.equals(guestName, booking.guestName) &&
                Objects.equals(checkInDate, booking.checkInDate) &&
                Objects.equals(checkOutDate, booking.checkOutDate) &&
                Objects.equals(status, booking.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, hotelName, guestName, checkInDate, checkOutDate, status);
    }

    @Override
    public String toString() {
        return "Booking{" +
                "id='" + id + '\'' +
                ", hotelName='" + hotelName + '\'' +
                ", guestName='" + guestName + '\'' +
                ", checkInDate=" + checkInDate +
                ", checkOutDate=" + checkOutDate +
                ", status='" + status + '\'' +
                '}';
    }
}
