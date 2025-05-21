package ru.javabegin.backend.found404.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.javabegin.backend.found404.dto.LoginRequest;
import ru.javabegin.backend.found404.model.AppUser;
import ru.javabegin.backend.found404.repository.AppUserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import ru.javabegin.backend.found404.security.JwtTokenProvider;
import ru.javabegin.backend.found404.security.AppUserDetails;


import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AppUserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public String authenticateUser(LoginRequest loginRequest) {
        if (loginRequest == null || loginRequest.getEmail() == null || loginRequest.getPassword() == null) {
            throw new BadCredentialsException("Неверный email или пароль");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Исправление начинается здесь:
        Object principal = authentication.getPrincipal();
        String email;
        if (principal instanceof AppUserDetails) {
            email = ((AppUserDetails) principal).getUsername();  // getUsername() возвращает email
        } else if (principal instanceof String) {
            // Если вдруг используется просто строка
            email = (String) principal;
        } else {
            throw new IllegalStateException("Unexpected principal type: " + principal.getClass());
        }

        return jwtTokenProvider.generateToken(email);
    }



}
