package ru.javabegin.backend.found404.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private LocalDateTime date = LocalDateTime.now();

    @Column(length = 255)
    private String imageUrl;

    @Column(nullable = false, length = 255)
    private String status;  // LOST или FOUND

    @Column(length = 50)
    private String category;

    @Column(length = 100)
    private String location;

    @Column
    private Double reward;

    // Связь с пользователем
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference // предотвращает бесконечную сериализацию
    private AppUser user;
}
