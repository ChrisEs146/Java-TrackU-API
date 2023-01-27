package com.tracku.chris.tracku.Repositories;
import com.tracku.chris.tracku.Entities.User.UserEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.time.LocalDateTime;
import java.util.Optional;

@DataJpaTest
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepo;
    private UserEntity user;

    @BeforeEach
    public void init() {
        user = UserEntity.builder()
                .full_name("Roger Clark")
                .email("roger@email.com")
                .user_password("password22#%")
                .build();
    }

    @Test
    public void userRepository_SaveUser_ReturnsSavedUser() {
        String userEmail = user.getEmail();
        String username = user.getFull_name();

        UserEntity savedUser = userRepo.save(user);

        Assertions.assertThat(savedUser).isNotNull();
        Assertions.assertThat(savedUser.getCreated_at()).isNotNull();
        Assertions.assertThat(savedUser.getUser_Id()).isGreaterThan(0);
        Assertions.assertThat(savedUser.getFull_name()).isEqualTo(username);
        Assertions.assertThat(savedUser.getEmail()).isEqualTo(userEmail);
    }

    @Test
    public void userRepository_UpdateUser_ReturnsUpdatedUser() {
        userRepo.save(user);
        String newName = "Robert Smith";
        LocalDateTime createdAtDate = user.getCreated_at();
        Long userId = user.getUser_Id();

        UserEntity originalUser = userRepo.findById(userId).get();
        originalUser.setFull_name(newName);
        UserEntity updatedUser = userRepo.save(originalUser);

        Assertions.assertThat(updatedUser).isNotNull();
        Assertions.assertThat(updatedUser.getCreated_at()).isEqualTo(createdAtDate);
        Assertions.assertThat(updatedUser.getUser_Id()).isGreaterThan(0);
        Assertions.assertThat(updatedUser.getFull_name()).isEqualTo(newName);
    }

    @Test
    public void userRepository_findByEmail_ReturnsUser() {
        userRepo.save(user);
        String userEmail = user.getEmail();
        String username = user.getFull_name();

        Optional<UserEntity> foundUser = userRepo.findByEmail(userEmail);

        Assertions.assertThat(foundUser).isPresent();
        Assertions.assertThat(foundUser.get().getUser_Id()).isGreaterThan(0);
        Assertions.assertThat(foundUser.get().getEmail()).isEqualTo(userEmail);
        Assertions.assertThat(foundUser.get().getFull_name()).isEqualTo(username);
    }

    @Test
    public void userRepository_delete_ReturnsVoid() {
        userRepo.save(user);
        Long userId = user.getUser_Id();

        userRepo.deleteById(userId);
        Optional<UserEntity> foundUser = userRepo.findById(userId);

        Assertions.assertThat(foundUser).isEmpty();
    }
}
