package ru.javabegin.backend.found404.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.javabegin.backend.found404.dto.ProfileDto;
import ru.javabegin.backend.found404.dto.ProfileUpdateDto;
import ru.javabegin.backend.found404.model.AppUser;
import ru.javabegin.backend.found404.service.ProfileService;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping
    public ResponseEntity<ProfileDto> getProfile() {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        AppUser profile = profileService.getProfileByEmail(email);

        ProfileDto dto = new ProfileDto(profile.getId(), profile.getName(), profile.getEmail());
        return ResponseEntity.ok(dto);
    }

    @PutMapping
    public ResponseEntity<ProfileDto> updateProfile(@RequestBody ProfileUpdateDto updateDto) {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        AppUser updatedProfile = profileService.updateProfile(email, updateDto);

        ProfileDto dto = new ProfileDto(updatedProfile.getId(), updatedProfile.getName(), updatedProfile.getEmail());
        return ResponseEntity.ok(dto);
    }
}
