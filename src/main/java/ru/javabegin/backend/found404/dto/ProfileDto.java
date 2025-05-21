package ru.javabegin.backend.found404.dto;

public class ProfileDto {
    private Long id;
    private String name;
    private String email;

    public ProfileDto(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    // геттеры и сеттеры
    public Long getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getEmail() {
        return email;
    }
}
