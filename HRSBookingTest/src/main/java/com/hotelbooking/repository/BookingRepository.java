package com.hotelbooking.repository;

import com.hotelbooking.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA Repository for Booking entities.
 * Provides standard CRUD operations automatically.
 */
@Repository
public interface BookingRepository extends JpaRepository<Booking, String> {
    // Spring Data JPA automatically provides:
    // save(Booking)
    // findById(String)
    // findAll()
    // deleteById(String)
    // existsById(String)
    // etc.

    // Custom query methods can be added here if needed, e.g.:
    // List<Booking> findByGuestName(String guestName);
    List<Booking> findByHotelNameContainingIgnoreCase(String hotelName);
}
