package com.hotelbooking.controller;

import com.hotelbooking.model.Booking;
import com.hotelbooking.service.BookingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

/**
 * REST Controller for booking-related API endpoints.
 * Handles incoming HTTP requests using Spring MVC annotations.
 */
@RestController // Combines @Controller and @ResponseBody
@RequestMapping("/bookings") // Base path for all endpoints in this controller
public class BookingController {

    private static final Logger logger = LoggerFactory.getLogger(BookingController.class);
    private static final Logger analyticsLogger = LoggerFactory.getLogger("com.hotelbooking.analytics");

    private final BookingService bookingService;

    /**
     * Spring will automatically inject the BookingService instance.
     * @param bookingService The BookingService instance to use for business logic.
     */
    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    /**
     * Handles POST requests to create a new booking.
     * Endpoint: POST /bookings
     *
     * @param booking The booking object from the request body.
     * @return ResponseEntity with the created Booking and HTTP status 201 (Created).
     */
    @PostMapping
    public ResponseEntity<Booking> createBooking(@RequestBody Booking booking) {
        Instant startTime = Instant.now();
        String requestId = UUID.randomUUID().toString();
        analyticsLogger.info("[REQ_START] RequestId: {}, Method: POST, Path: /bookings, Body: {}",
                requestId, booking);

        try {
            Booking createdBooking = bookingService.createBooking(booking);
            analyticsLogger.info("[REQ_END] RequestId: {}, Status: 201, Action: Create, Duration: {}ms",
                    requestId, Duration.between(startTime, Instant.now()).toMillis());
            return new ResponseEntity<>(createdBooking, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            logger.warn("Bad Request for RequestId {}: {}", requestId, e.getMessage());
            analyticsLogger.warn("[REQ_END] RequestId: {}, Status: 400, Action: Create, Duration: {}ms",
                    requestId, Duration.between(startTime, Instant.now()).toMillis());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            logger.error("Internal Server Error for RequestId {}: {}", requestId, e.getMessage(), e);
            analyticsLogger.error("[REQ_END] RequestId: {}, Status: 500, Action: Create, Duration: {}ms",
                    requestId, Duration.between(startTime, Instant.now()).toMillis());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create booking.", e);
        }
    }

    /**
     * Handles GET requests to retrieve all bookings.
     * Endpoint: GET /bookings
     *
     * @return ResponseEntity with a list of all Bookings and HTTP status 200 (OK).
     */
    @GetMapping
    public ResponseEntity<List<Booking>> getAllBookings() {
        Instant startTime = Instant.now();
        String requestId = UUID.randomUUID().toString();
        analyticsLogger.info("[REQ_START] RequestId: {}, Method: GET, Path: /bookings", requestId);

        try {
            List<Booking> bookings = bookingService.getAllBookings();
            analyticsLogger.info("[REQ_END] RequestId: {}, Status: 200, Action: GetAll, Duration: {}ms",
                    requestId, Duration.between(startTime, Instant.now()).toMillis());
            return new ResponseEntity<>(bookings, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Internal Server Error for RequestId {}: {}", requestId, e.getMessage(), e);
            analyticsLogger.error("[REQ_END] RequestId: {}, Status: 500, Action: GetAll, Duration: {}ms",
                    requestId, Duration.between(startTime, Instant.now()).toMillis());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve bookings.", e);
        }
    }

    /**
     * Handles GET requests to retrieve a specific booking by ID.
     * Endpoint: GET /bookings/{id}
     *
     * @param id The ID of the booking to retrieve, from the path variable.
     * @return ResponseEntity with the Booking if found (200 OK), or 404 Not Found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Booking> getBookingById(@PathVariable String id) {
        Instant startTime = Instant.now();
        String requestId = UUID.randomUUID().toString();
        analyticsLogger.info("[REQ_START] RequestId: {}, Method: GET, Path: /bookings/{}", requestId, id);

        try {
            return bookingService.getBookingById(id)
                    .map(booking -> {
                        analyticsLogger.info("[REQ_END] RequestId: {}, Status: 200, Action: GetById, Duration: {}ms",
                                requestId, Duration.between(startTime, Instant.now()).toMillis());
                        return new ResponseEntity<>(booking, HttpStatus.OK);
                    })
                    .orElseGet(() -> {
                        logger.warn("Booking not found for RequestId {}: ID {}", requestId, id);
                        analyticsLogger.warn("[REQ_END] RequestId: {}, Status: 404, Action: GetById, Duration: {}ms",
                                requestId, Duration.between(startTime, Instant.now()).toMillis());
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found with ID: " + id);
                    });
        } catch (IllegalArgumentException e) {
            logger.warn("Bad Request for RequestId {}: {}", requestId, e.getMessage());
            analyticsLogger.warn("[REQ_END] RequestId: {}, Status: 400, Action: GetById, Duration: {}ms",
                    requestId, Duration.between(startTime, Instant.now()).toMillis());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            logger.error("Internal Server Error for RequestId {}: {}", requestId, e.getMessage(), e);
            analyticsLogger.error("[REQ_END] RequestId: {}, Status: 500, Action: GetById, Duration: {}ms",
                    requestId, Duration.between(startTime, Instant.now()).toMillis());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve booking.", e);
        }
    }

    /**
     * Handles GET requests to search for bookings by hotel name.
     * Endpoint: GET /bookings/search?hotelName={hotelName}
     *
     * @param hotelName The hotel name to search for (partial or full, case-insensitive).
     * @return ResponseEntity with a list of matching Bookings and HTTP status 200 (OK).
     */
    @GetMapping("/search")
    public ResponseEntity<List<Booking>> searchBookings(@RequestParam String hotelName) {
        Instant startTime = Instant.now();
        String requestId = UUID.randomUUID().toString();
        analyticsLogger.info("[REQ_START] RequestId: {}, Method: GET, Path: /bookings/search?hotelName={}", requestId, hotelName);

        try {
            List<Booking> bookings = bookingService.searchBookingsByHotelName(hotelName);
            analyticsLogger.info("[REQ_END] RequestId: {}, Status: 200, Action: Search, Duration: {}ms",
                    requestId, Duration.between(startTime, Instant.now()).toMillis());
            return new ResponseEntity<>(bookings, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            logger.warn("Bad Request for RequestId {}: {}", requestId, e.getMessage());
            analyticsLogger.warn("[REQ_END] RequestId: {}, Status: 400, Action: Search, Duration: {}ms",
                    requestId, Duration.between(startTime, Instant.now()).toMillis());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            logger.error("Internal Server Error for RequestId {}: {}", requestId, e.getMessage(), e);
            analyticsLogger.error("[REQ_END] RequestId: {}, Status: 500, Action: Search, Duration: {}ms",
                    requestId, Duration.between(startTime, Instant.now()).toMillis());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to search bookings.", e);
        }
    }

    /**
     * Handles PUT requests to update an existing booking.
     * Endpoint: PUT /bookings/{id}
     *
     * @param id The ID of the booking to update, from the path variable.
     * @param booking The updated booking object from the request body.
     * @return ResponseEntity with the updated Booking (200 OK), or 404 Not Found.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Booking> updateBooking(@PathVariable String id, @RequestBody Booking booking) {
        Instant startTime = Instant.now();
        String requestId = UUID.randomUUID().toString();
        analyticsLogger.info("[REQ_START] RequestId: {}, Method: PUT, Path: /bookings/{}, Body: {}",
                requestId, id, booking);

        try {
            return bookingService.updateBooking(id, booking)
                    .map(updatedBooking -> {
                        analyticsLogger.info("[REQ_END] RequestId: {}, Status: 200, Action: Update, Duration: {}ms",
                                requestId, Duration.between(startTime, Instant.now()).toMillis());
                        return new ResponseEntity<>(updatedBooking, HttpStatus.OK);
                    })
                    .orElseGet(() -> {
                        logger.warn("Booking not found for RequestId {}: ID {}", requestId, id);
                        analyticsLogger.warn("[REQ_END] RequestId: {}, Status: 404, Action: Update, Duration: {}ms",
                                requestId, Duration.between(startTime, Instant.now()).toMillis());
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found with ID: " + id);
                    });
        } catch (IllegalArgumentException e) {
            logger.warn("Bad Request for RequestId {}: {}", requestId, e.getMessage());
            analyticsLogger.warn("[REQ_END] RequestId: {}, Status: 400, Action: Update, Duration: {}ms",
                    requestId, Duration.between(startTime, Instant.now()).toMillis());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            logger.error("Internal Server Error for RequestId {}: {}", requestId, e.getMessage(), e);
            analyticsLogger.error("[REQ_END] RequestId: {}, Status: 500, Action: Update, Duration: {}ms",
                    requestId, Duration.between(startTime, Instant.now()).toMillis());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update booking.", e);
        }
    }

    /**
     * Handles DELETE requests to cancel a booking.
     * Endpoint: DELETE /bookings/{id}
     *
     * @param id The ID of the booking to cancel, from the path variable.
     * @return ResponseEntity with no content (204 No Content) if successful, or 404 Not Found.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelBooking(@PathVariable String id) {
        Instant startTime = Instant.now();
        String requestId = UUID.randomUUID().toString();
        analyticsLogger.info("[REQ_START] RequestId: {}, Method: DELETE, Path: /bookings/{}", requestId, id);

        try {
            boolean cancelled = bookingService.cancelBooking(id);
            if (cancelled) {
                analyticsLogger.info("[REQ_END] RequestId: {}, Status: 204, Action: Cancel, Duration: {}ms",
                        requestId, Duration.between(startTime, Instant.now()).toMillis());
                return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content
            } else {
                logger.warn("Booking not found or already cancelled for RequestId {}: ID {}", requestId, id);
                analyticsLogger.warn("[REQ_END] RequestId: {}, Status: 404, Action: Cancel, Duration: {}ms",
                        requestId, Duration.between(startTime, Instant.now()).toMillis());
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found or already cancelled with ID: " + id);
            }
        } catch (IllegalArgumentException e) {
            logger.warn("Bad Request for RequestId {}: {}", requestId, e.getMessage());
            analyticsLogger.warn("[REQ_END] RequestId: {}, Status: 400, Action: Cancel, Duration: {}ms",
                    requestId, Duration.between(startTime, Instant.now()).toMillis());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            logger.error("Internal Server Error for RequestId {}: {}", requestId, e.getMessage(), e);
            analyticsLogger.error("[REQ_END] RequestId: {}, Status: 500, Action: Cancel, Duration: {}ms",
                    requestId, Duration.between(startTime, Instant.now()).toMillis());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to cancel booking.", e);
        }
    }

    /**
     * Custom exception handler for IllegalArgumentException.
     * This ensures a consistent 400 Bad Request response.
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.warn("Handling IllegalArgumentException: {}", ex.getMessage());
        return ex.getMessage();
    }

    /**
     * Custom exception handler for general Exceptions (catch-all).
     * This ensures a consistent 500 Internal Server Error response.
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public String handleGeneralException(Exception ex) {
        logger.error("Handling general exception: {}", ex.getMessage(), ex);
        return "An unexpected error occurred: " + ex.getMessage();
    }
}
