package com.tracku.chris.tracku.Services;

import com.tracku.chris.tracku.Entities.User.CustomUserDetails;
import com.tracku.chris.tracku.Entities.User.UserEntity;
import com.tracku.chris.tracku.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepo;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepo.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User does not exists"));
        return new CustomUserDetails(user);
    }
}
