package com.teamE.rooms;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

@Entity
@Table(name = "room_configuration")
public class RoomConfiguration {
    private final static RoomConfiguration defaultConfiguration = RoomConfiguration.builder()
            .id(1L)
            .name("DEFAULT")
            .openFrom(LocalTime.of(12, 0))
            .openTo(LocalTime.of(22, 0))
            .rentInterval(Duration.ofMinutes(15))
            .pricePerInterval(new BigDecimal("1.20"))
            .build();

    @NotNull
    @Size(max = 100)
    private String name;

    @NotNull
    private LocalTime openFrom;

    @NotNull
    private LocalTime openTo;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal pricePerInterval;
    @NotNull
    private Duration rentInterval;

    public static RoomConfiguration getDefaultConfiguration() {
        return defaultConfiguration;
    }

}
