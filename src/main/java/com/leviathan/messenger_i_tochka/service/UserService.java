package com.leviathan.messenger_i_tochka.service;

import com.leviathan.messenger_i_tochka.dto.RegistrationUserDto;
import com.leviathan.messenger_i_tochka.entity.User;
import com.leviathan.messenger_i_tochka.exception.UserAlreadyExistException;
import com.leviathan.messenger_i_tochka.repository.ApplicationRoleRepo;
import com.leviathan.messenger_i_tochka.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepo userRepo;
    private final ApplicationRoleRepo applicationRoleRepo;
    private final PasswordEncoder passwordEncoder;

    public Optional<User> findByUsername(String username) {
        return userRepo.findByUsername(username);
    }

    @Transactional
    public void createNewUser(RegistrationUserDto registrationUserDto) throws UserAlreadyExistException {
        if (findByUsername(registrationUserDto.getUsername()).isPresent()) {
            throw new UserAlreadyExistException("User with username " + registrationUserDto.getUsername() + " already exist");
        }
        User user = new User();
        user.setEmail(registrationUserDto.getEmail());
        user.setUsername(registrationUserDto.getUsername());
        user.setPassword(passwordEncoder.encode(registrationUserDto.getPassword()));
        user.setRoles(List.of(applicationRoleRepo.findByName("ROLE_USER").get()));
        userRepo.save(user);
    }
}
