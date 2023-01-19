package com.tracku.chris.tracku.Entities.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long user_Id;

    @Column(nullable = false)
    private String full_name;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 60)
    private String user_password;

    @Column(nullable = false)
    private LocalDateTime created_at;

    @PrePersist
    private void onPrePersist() {
        created_at = LocalDateTime.now();
    }

}
