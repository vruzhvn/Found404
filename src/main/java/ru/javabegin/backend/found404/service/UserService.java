package ru.javabegin.backend.found404.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.javabegin.backend.found404.model.AppUser;
import ru.javabegin.backend.found404.repository.AppUserRepository;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AppUser registerUser(AppUser user) {
        // Проверка обязательных полей
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }

        // Проверка существования пользователя по email
        userRepository.findByEmail(user.getEmail())
                .ifPresent(existingUser -> {
                    throw new IllegalArgumentException("User with this email already exists");
                });

        // Кодирование пароля и сохранение пользователя
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Убедитесь, что ID не установлен вручную и равен null
        user.setId(null); // Это гарантирует, что ID будет сгенерирован автоматически

        return userRepository.save(user);
    }

    // Поиск пользователя по email
    public AppUser findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
    }

    public Optional<AppUser> findUserById(Long id) {

        return userRepository.findById(id);
    }

    @Transactional
    public void saveProfileImage(Long userId, MultipartFile file) throws IOException {
        // Проверка на пустой файл
        if (file.isEmpty()) {
            throw new IOException("File is empty");
        }

        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new IOException("User not found with id: " + userId));

        user.setProfileImage(file.getBytes());  // Сохраняем фото как массив байтов
        userRepository.save(user);
    }

    // Получение изображения профиля
    public byte[] getProfileImage(Long userId) throws IOException {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new IOException("User not found with id: " + userId));

        byte[] profileImage = user.getProfileImage();
        if (profileImage != null) {
            return profileImage;  // Возвращаем фото пользователя
        } else {
            throw new IOException("Profile image not found for user with id: " + userId);
        }
    }

    public Optional<AppUser> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
