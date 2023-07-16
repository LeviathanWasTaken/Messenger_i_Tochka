package com.leviathan.messenger_i_tochka.service;

import com.leviathan.messenger_i_tochka.dto.JwtRequest;
import com.leviathan.messenger_i_tochka.entity.JwtBlacklisted;
import com.leviathan.messenger_i_tochka.repository.JwtBlacklistedRepo;
import com.leviathan.messenger_i_tochka.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final UserService userService;
    private final JwtBlacklistedRepo jwtBlacklistedRepo;

    public String createAuthToken(JwtRequest authRequest) throws BadCredentialsException {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                authRequest.getUsername(),
                authRequest.getPassword()));
        UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
        String jwt = jwtUtils.generateToken(userDetails);
        userService.saveToken(jwt, userDetails.getUsername());
        return jwt;
    }

    @Transactional
    public void blacklistTokens(String username) {
        Set<String> userTokens = userService.getTokensForUser(username);
        for (String token : userTokens) {
            JwtBlacklisted jwtBlacklisted = new JwtBlacklisted();
            jwtBlacklisted.setToken(token);
            jwtBlacklistedRepo.save(jwtBlacklisted);
        }
        userService.removeAllTokensForUser(username);
    }
}
