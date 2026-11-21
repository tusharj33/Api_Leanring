package com.tusharjoshi.automation.testdata;

import com.tusharjoshi.automation.pojo.Booking;
import com.tusharjoshi.automation.pojo.BookingDates;

public class BookingData {

    public static Booking validBooking() {
        return new Booking("Tushar", "Joshi", 5000, true,
                new BookingDates("2026-12-20", "2026-12-25"), "Breakfast");
    }

    public static Booking bookingWithReversedDates() {
        Booking b = validBooking();
        b.setBookingdates(new BookingDates("2026-12-25", "2026-12-20"));
        return b;
    }

    public static Booking bookingWithNegativePrice() {
        Booking b = validBooking();
        b.setTotalprice(-100);
        return b;
    }
}