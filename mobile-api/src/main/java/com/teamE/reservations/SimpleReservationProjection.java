package com.teamE.reservations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

@Projection(name = "simple", types = Reservation.class)
public interface SimpleReservationProjection {
    LocalDateTime getDateTime();

    Duration getDuration();

    boolean isAccepted();

    int getNumberOfPeople();

    long getId();

    @Value("#{target.getUser().getId()}")
    long getUserId();

    @Value("#{target.getPrice()}")
    BigDecimal getPrice();

    @Value("#{target.getRoom().getId()}")
    long getRoomId();

    @Value("#{target.getRoom().getName()}")
    String getRoomName();
}
