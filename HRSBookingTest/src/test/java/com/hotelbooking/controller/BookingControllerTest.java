package com.hotelbooking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hotelbooking.HotelBookingSpringbootApplication;
import com.hotelbooking.model.Booking;
import com.hotelbooking.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration-style tests for the BookingController using Spring Boot's MockMvc.
 * This sets up a Spring context but does not start a full HTTP server, making tests faster.
 */
@WebMvcTest(BookingController.class) // Focuses on testing the web layer
@ContextConfiguration(classes = HotelBookingSpringbootApplication.class) // Load necessary Spring context
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc; // Used to perform HTTP requests without a running server

    @MockBean // Creates a mock instance of BookingService and adds it to the Spring context
    private BookingService bookingService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    @DisplayName("Should create a new booking via POST /bookings")
    void shouldCreateBooking() throws Exception {
        String bookingId = UUID.randomUUID().toString();
        Booking newBooking = new Booking(null, "Test Hotel", "Jane Doe",
                LocalDate.now().plusDays(10), LocalDate.now().plusDays(15), "PENDING");
        Booking createdBooking = new Booking(bookingId, "Test Hotel", "Jane Doe",
                LocalDate.now().plusDays(10), LocalDate.now().plusDays(15), "PENDING");

        // Mock the service call
        when(bookingService.createBooking(any(Booking.class))).thenReturn(createdBooking);

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newBooking)))
                .andExpect(status().isCreated()) // Expect HTTP 201 Created
                .andExpect(jsonPath("$.id", is(bookingId)))
                .andExpect(jsonPath("$.hotelName", is("Test Hotel")))
                .andExpect(jsonPath("$.guestName", is("Jane Doe")))
                .andExpect(jsonPath("$.status", is("PENDING")));
    }

    @Test
    @DisplayName("Should return 400 for invalid booking data on POST")
    void shouldReturn400ForInvalidBookingDataOnPost() throws Exception {
        Booking invalidBooking = new Booking(null, "Invalid Hotel", "Invalid Guest",
                LocalDate.now().plusDays(10), LocalDate.now().plusDays(5), "PENDING"); // Invalid dates

        when(bookingService.createBooking(any(Booking.class)))
                .thenThrow(new IllegalArgumentException("Check-in date cannot be after check-out date."));

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidBooking)))
                .andExpect(status().isBadRequest()) // Expect HTTP 400 Bad Request
                .andExpect(content().string("Check-in date cannot be after check-out date."));
    }

    @Test
    @DisplayName("Should get all bookings via GET /bookings")
    void shouldGetAllBookings() throws Exception {
        Booking booking1 = new Booking(UUID.randomUUID().toString(), "Hotel A", "Guest A", LocalDate.now().plusDays(1), LocalDate.now().plusDays(2), "CONFIRMED");
        Booking booking2 = new Booking(UUID.randomUUID().toString(), "Hotel B", "Guest B", LocalDate.now().plusDays(3), LocalDate.now().plusDays(4), "PENDING");
        List<Booking> mockBookings = Arrays.asList(booking1, booking2);

        when(bookingService.getAllBookings()).thenReturn(mockBookings);

        mockMvc.perform(get("/bookings")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Expect HTTP 200 OK
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].hotelName", is("Hotel A")))
                .andExpect(jsonPath("$[1].hotelName", is("Hotel B")));
    }

    @Test
    @DisplayName("Should get a booking by ID via GET /bookings/{id}")
    void shouldGetBookingById() throws Exception {
        String bookingId = UUID.randomUUID().toString();
        Booking mockBooking = new Booking(bookingId, "Unique Hotel", "Unique Guest",
                LocalDate.now().plusDays(5), LocalDate.now().plusDays(7), "CONFIRMED");

        when(bookingService.getBookingById(bookingId)).thenReturn(Optional.of(mockBooking));

        mockMvc.perform(get("/bookings/{id}", bookingId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Expect HTTP 200 OK
                .andExpect(jsonPath("$.id", is(bookingId)))
                .andExpect(jsonPath("$.hotelName", is("Unique Hotel")));
    }

    @Test
    @DisplayName("Should return 404 for non-existent booking ID on GET")
    void shouldReturn404ForNonExistentBookingId() throws Exception {
        String nonExistentId = UUID.randomUUID().toString();
        when(bookingService.getBookingById(nonExistentId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/bookings/{id}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()) // Expect HTTP 404 Not Found
                .andExpect(content().string("Booking not found with ID: " + nonExistentId));
    }

    @Test
    @DisplayName("Should update an existing booking via PUT /bookings/{id}")
    void shouldUpdateBooking() throws Exception {
        String bookingId = UUID.randomUUID().toString();
        Booking updatedDetails = new Booking(null, "New Hotel Name", "New Guest Name",
                LocalDate.now().plusDays(2), LocalDate.now().plusDays(6), "CONFIRMED");
        Booking returnedBooking = new Booking(bookingId, "New Hotel Name", "New Guest Name",
                LocalDate.now().plusDays(2), LocalDate.now().plusDays(6), "CONFIRMED");

        when(bookingService.updateBooking(eq(bookingId), any(Booking.class))).thenReturn(Optional.of(returnedBooking));

        mockMvc.perform(put("/bookings/{id}", bookingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDetails)))
                .andExpect(status().isOk()) // Expect HTTP 200 OK
                .andExpect(jsonPath("$.id", is(bookingId)))
                .andExpect(jsonPath("$.hotelName", is("New Hotel Name")))
                .andExpect(jsonPath("$.status", is("CONFIRMED")));
    }

    @Test
    @DisplayName("Should return 404 for non-existent booking ID on PUT")
    void shouldReturn404ForNonExistentBookingIdOnPut() throws Exception {
        String nonExistentId = UUID.randomUUID().toString();
        Booking updatedDetails = new Booking(null, "Non Existent Update", "Guest",
                LocalDate.now().plusDays(1), LocalDate.now().plusDays(2), "CONFIRMED");

        when(bookingService.updateBooking(eq(nonExistentId), any(Booking.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/bookings/{id}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDetails)))
                .andExpect(status().isNotFound()) // Expect HTTP 404 Not Found
                .andExpect(content().string("Booking not found with ID: " + nonExistentId));
    }

    @Test
    @DisplayName("Should cancel a booking via DELETE /bookings/{id}")
    void shouldCancelBooking() throws Exception {
        String bookingId = UUID.randomUUID().toString();
        when(bookingService.cancelBooking(bookingId)).thenReturn(true);

        mockMvc.perform(delete("/bookings/{id}", bookingId))
                .andExpect(status().isNoContent()); // Expect HTTP 204 No Content
    }

    @Test
    @DisplayName("Should return 404 for non-existent booking ID on DELETE")
    void shouldReturn404ForNonExistentBookingIdOnDelete() throws Exception {
        String nonExistentId = UUID.randomUUID().toString();
        when(bookingService.cancelBooking(nonExistentId)).thenReturn(false);

        mockMvc.perform(delete("/bookings/{id}", nonExistentId))
                .andExpect(status().isNotFound()) // Expect HTTP 404 Not Found
                .andExpect(content().string("Booking not found or already cancelled with ID: " + nonExistentId));
    }

    @Test
    @DisplayName("Should search bookings by hotel name via GET /bookings/search?hotelName={name}")
    void shouldSearchBookingsByHotelName() throws Exception {
        String searchName = "Grand";
        Booking booking1 = new Booking(UUID.randomUUID().toString(), "Grand Hyatt", "Guest A", LocalDate.now().plusDays(1), LocalDate.now().plusDays(2), "CONFIRMED");
        Booking booking2 = new Booking(UUID.randomUUID().toString(), "Grand Plaza", "Guest B", LocalDate.now().plusDays(3), LocalDate.now().plusDays(4), "PENDING");
        List<Booking> mockBookings = Arrays.asList(booking1, booking2);

        when(bookingService.searchBookingsByHotelName(searchName)).thenReturn(mockBookings);

        mockMvc.perform(get("/bookings/search")
                        .param("hotelName", searchName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Expect HTTP 200 OK
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].hotelName", is("Grand Hyatt")))
                .andExpect(jsonPath("$[1].hotelName", is("Grand Plaza")));
    }

    @Test
    @DisplayName("Should return empty list if no bookings found for search")
    void shouldReturnEmptyListForNotFoundSearch() throws Exception {
        String searchName = "NonExistentHotel";
        when(bookingService.searchBookingsByHotelName(searchName)).thenReturn(List.of()); // Return empty list

        mockMvc.perform(get("/bookings/search")
                        .param("hotelName", searchName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Still 200 OK, but with an empty array
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("Should return 400 for empty hotel name on search")
    void shouldReturn400ForEmptyHotelNameOnSearch() throws Exception {
        String searchName = ""; // Empty string
        when(bookingService.searchBookingsByHotelName(searchName))
                .thenThrow(new IllegalArgumentException("Hotel name for search cannot be null or empty."));

        mockMvc.perform(get("/bookings/search")
                        .param("hotelName", searchName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()) // Expect HTTP 400 Bad Request
                .andExpect(content().string("Hotel name for search cannot be null or empty."));
    }
}
