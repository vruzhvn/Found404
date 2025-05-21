package ru.javabegin.backend.found404.service;

import ru.javabegin.backend.found404.dto.ProfileUpdateDto;
import ru.javabegin.backend.found404.model.AppUser;

public interface ProfileService {
    AppUser getProfileByEmail(String email);
    AppUser updateProfile(String email, ProfileUpdateDto updateDto);
}
