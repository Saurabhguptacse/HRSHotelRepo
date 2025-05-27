package com.hotelbooking.service;

import com.hotelbooking.model.Booking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Service layer for managing hotel bookings.
 * Stores booking data in-memory using a ConcurrentHashMap.
 */
@Service
public class BookingService {

    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);

    // In-memory store for bookings. Using ConcurrentHashMap for thread-safety.
    private final ConcurrentHashMap<String, Booking> bookings = new ConcurrentHashMap<>();

    /**
     * Initializes the in-memory store with some sample data.
     */
    public BookingService() {
        // Add some initial sample data
        Booking booking1 = new Booking(UUID.randomUUID().toString(), "Grand Hyatt", "Alice Smith", LocalDate.now().plusDays(5), LocalDate.now().plusDays(10), "CONFIRMED");
        Booking booking2 = new Booking(UUID.randomUUID().toString(), "Hilton Garden Inn", "Bob Johnson", LocalDate.now().plusMonths(1), LocalDate.now().plusMonths(1).plusDays(3), "PENDING");
        Booking booking3 = new Booking(UUID.randomUUID().toString(), "Marriott Marquis", "Charlie Brown", LocalDate.now().plusMonths(2), LocalDate.now().plusMonths(2).plusDays(7), "CONFIRMED");
        Booking booking4 = new Booking(UUID.randomUUID().toString(), "Grand Hotel & Casino", "David Lee", LocalDate.now().plusDays(15), LocalDate.now().plusDays(20), "PENDING");

        bookings.put(booking1.getId(), booking1);
        bookings.put(booking2.getId(), booking2);
        bookings.put(booking3.getId(), booking3);
        bookings.put(booking4.getId(), booking4);

        logger.info("In-memory booking store initialized with {} sample bookings.", bookings.size());
    }

    /**
     * Creates a new booking in the in-memory store.
     *
     * @param booking The booking object to create.
     * @return The created booking with its ID.
     * @throws IllegalArgumentException If booking data is invalid.
     */
    public Booking createBooking(Booking booking) {
        if (booking == null || booking.getHotelName() == null || booking.getGuestName() == null ||
                booking.getCheckInDate() == null || booking.getCheckOutDate() == null) {
            throw new IllegalArgumentException("Booking details cannot be null or empty.");
        }
        if (booking.getCheckInDate().isAfter(booking.getCheckOutDate())) {
            throw new IllegalArgumentException("Check-in date cannot be after check-out date.");
        }
        if (booking.getCheckInDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Check-in date cannot be in the past.");
        }

        // Set initial status if not provided
        if (booking.getStatus() == null || booking.getStatus().isEmpty()) {
            booking.setStatus("PENDING");
        }

        // Generate a unique ID for the booking if not already set
        if (booking.getId() == null || booking.getId().isEmpty()) {
            booking.setId(UUID.randomUUID().toString());
        }

        bookings.put(booking.getId(), booking);
        logger.info("Booking created successfully: {}", booking.getId());
        return booking;
    }

    /**
     * Retrieves a booking by its ID from the in-memory store.
     *
     * @param id The ID of the booking.
     * @return An Optional containing the Booking if found, or empty if not found.
     */
    public Optional<Booking> getBookingById(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Booking ID cannot be null or empty.");
        }
        logger.debug("Attempting to retrieve booking by ID: {}", id);
        return Optional.ofNullable(bookings.get(id));
    }

    /**
     * Retrieves all bookings from the in-memory store.
     *
     * @return A list of all bookings.
     */
    public List<Booking> getAllBookings() {
        logger.debug("Attempting to retrieve all bookings.");
        return new ArrayList<>(bookings.values());
    }

    /**
     * Searches for bookings by hotel name in the in-memory store.
     *
     * @param hotelName The partial or full hotel name to search for.
     * @return A list of bookings matching the criteria.
     * @throws IllegalArgumentException If hotelName is null or empty.
     */
    public List<Booking> searchBookingsByHotelName(String hotelName) {
        if (hotelName == null || hotelName.trim().isEmpty()) {
            throw new IllegalArgumentException("Hotel name for search cannot be null or empty.");
        }
        final String lowerCaseHotelName = hotelName.toLowerCase();
        logger.debug("Searching for bookings with hotel name containing: {}", hotelName);
        return bookings.values().stream()
                .filter(booking -> booking.getHotelName().toLowerCase().contains(lowerCaseHotelName))
                .collect(Collectors.toList());
    }

    /**
     * Updates an existing booking in the in-memory store.
     *
     * @param id The ID of the booking to update.
     * @param updatedBooking The booking object with updated details.
     * @return The updated booking, or Optional.empty() if not found.
     * @throws IllegalArgumentException If booking data is invalid.
     */
    public Optional<Booking> updateBooking(String id, Booking updatedBooking) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Booking ID cannot be null or empty for update.");
        }
        if (updatedBooking == null || updatedBooking.getHotelName() == null || updatedBooking.getGuestName() == null ||
                updatedBooking.getCheckInDate() == null || updatedBooking.getCheckOutDate() == null) {
            throw new IllegalArgumentException("Updated booking details cannot be null or empty.");
        }
        if (updatedBooking.getCheckInDate().isAfter(updatedBooking.getCheckOutDate())) {
            throw new IllegalArgumentException("Check-in date cannot be after check-out date for update.");
        }

        return Optional.ofNullable(bookings.get(id)).map(existingBooking -> {
            // Update fields from the provided updatedBooking
            existingBooking.setHotelName(updatedBooking.getHotelName());
            existingBooking.setGuestName(updatedBooking.getGuestName());
            existingBooking.setCheckInDate(updatedBooking.getCheckInDate());
            existingBooking.setCheckOutDate(updatedBooking.getCheckOutDate());
            existingBooking.setStatus(updatedBooking.getStatus());

            // No explicit save needed for in-memory map, just update the object in place
            logger.info("Booking updated successfully for ID: {}", id);
            return existingBooking;
        });
    }

    /**
     * Cancels a booking by setting its status to "CANCELLED" in the in-memory store.
     *
     * @param id The ID of the booking to cancel.
     * @return True if the booking was cancelled, false if not found or already cancelled.
     * @throws IllegalArgumentException If booking ID is invalid.
     */
    public boolean cancelBooking(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Booking ID cannot be null or empty for cancellation.");
        }

        return Optional.ofNullable(bookings.get(id)).map(bookingToCancel -> {
            if ("CANCELLED".equalsIgnoreCase(bookingToCancel.getStatus())) {
                logger.info("Booking with ID {} is already cancelled.", id);
                return false; // Already cancelled
            }
            bookingToCancel.setStatus("CANCELLED");
            logger.info("Booking cancelled successfully for ID: {}", id);
            return true;
        }).orElseGet(() -> {
            logger.warn("Booking with ID {} not found for cancellation.", id);
            return false;
        });
    }

    /**
     * Deletes a booking from the in-memory store.
     *
     * @param id The ID of the booking to delete.
     * @return True if the booking was deleted, false if not found.
     * @throws IllegalArgumentException If booking ID is invalid.
     */
    public boolean deleteBooking(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Booking ID cannot be null or empty for deletion.");
        }
        if (bookings.containsKey(id)) {
            bookings.remove(id);
            logger.info("Booking deleted successfully for ID: {}", id);
            return true;
        } else {
            logger.warn("Booking with ID {} not found for deletion.", id);
            return false;
        }
    }
}
