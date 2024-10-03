package com.devsync.domain.entity;

import com.devsync.domain.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false,length = 120)
    private String firstName;

    @Column(nullable = false,length = 120)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.INDIVIDUAL;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime registeredAt;


}