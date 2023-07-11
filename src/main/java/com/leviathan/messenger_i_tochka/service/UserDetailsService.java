package com.leviathan.messenger_i_tochka.service;

import com.leviathan.messenger_i_tochka.entity.User;
import com.leviathan.messenger_i_tochka.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final UserRepo userRepo;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("User with username " + username + " doesn't exist")
        );
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getRoles().stream().map(
                        applicationRole -> new SimpleGrantedAuthority(applicationRole.getName())
                ).collect(Collectors.toList())
        );
    }

    public Optional<User> findByUsername(String username) {
        return userRepo.findByUsername(username);
    }
}
