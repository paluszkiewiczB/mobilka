package com.teamE.users;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@AllArgsConstructor
@Data
@Builder(access = AccessLevel.PUBLIC)
@Entity
@Table(name = "users")
public class User implements Serializable {

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ROLE_ID")
    UserRole userRole;
    int indexNumber;
    @Enumerated(EnumType.STRING)
    StudentHouse studentHouse;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @NonNull
    private String password;
    @Email
    @NotBlank
    @Column(unique = true)
    private String email;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;

    public User() {
    }
}



