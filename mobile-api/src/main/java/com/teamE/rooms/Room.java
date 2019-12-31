package com.teamE.rooms;

import com.teamE.users.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "rooms")
public class Room {
    @Id
    @GeneratedValue
    private long id;

    @Column(nullable = false)
    private int dsNumber;

    @Column(nullable = false)
    private String name;

    private String description;

    @OneToOne
    @JoinColumn(name = "keyholder_id")
    private User user;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "configuration_id", nullable = false)
    private RoomConfiguration configuration = RoomConfiguration.getDefaultConfiguration();
}


