package com.tracku.chris.tracku.Entities;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Data
@Table(name="Users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long userId;
    @Column(nullable = false)
    private String fullName;
    @Column(nullable = false, unique = true, length = 100)
    private String email;
    @Column(nullable = false, length = 60)
    private String userPassword;
    private String imagePath;
    @Column(nullable = false)
    private LocalDate createdOn = LocalDate.now();
}
