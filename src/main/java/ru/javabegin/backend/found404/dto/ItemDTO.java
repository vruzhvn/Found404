package ru.javabegin.backend.found404.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDTO {
    private String title;
    private String description;
    private String imageUrl;
    private String status;    // например: "FOUND" или "LOST"
    private String category;  // например: "Phone", "Bag", "Keys"
    private String location;  // например: "Library", "Cafeteria"
    private Long userId;      // ID пользователя, который создал объявление
    private Double reward;
}
