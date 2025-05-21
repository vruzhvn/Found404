package ru.javabegin.backend.found404.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.javabegin.backend.found404.dto.ProfileUpdateDto;
import ru.javabegin.backend.found404.model.AppUser;
import ru.javabegin.backend.found404.repository.UserRepository;

@Service
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;

    public ProfileServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public AppUser getProfileByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    @Transactional
    public AppUser updateProfile(String email, ProfileUpdateDto updateDto) {
        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setName(updateDto.getName());
        return userRepository.save(user);
    }
}
