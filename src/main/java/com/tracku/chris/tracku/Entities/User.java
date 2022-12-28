package com.tracku.chris.tracku.Entities;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Data
@Table(name="Users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long userId;
    @Column(nullable = false)
    @NotBlank(message = "Please, provide a full name")
    private String fullName;
    @Column(nullable = false, unique = true)
    @Email(message="Please, provide a valid email")
    private String email;
    @Column(nullable = false, length = 60)
    @NotBlank(message="Please, provide a password")
    private String userPassword;
    @Column(nullable = false)
    private String imagePath;
}
