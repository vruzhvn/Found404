package ru.javabegin.backend.found404.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.javabegin.backend.found404.dto.ItemDTO;
import ru.javabegin.backend.found404.model.AppUser;
import ru.javabegin.backend.found404.model.Item;
import ru.javabegin.backend.found404.service.ItemService;
import ru.javabegin.backend.found404.service.UserService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/api/items")
@CrossOrigin
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService itemService;
    private final UserService userService;

    // Add new item with image
    @PostMapping(value = "/add", consumes = {"multipart/form-data"})
    public ResponseEntity<?> addItem(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("status") String status,
            @RequestParam("category") String category,
            @RequestParam("location") String location,
            @RequestParam("userId") Long userId,
            @RequestParam(value = "reward", required = false) Double reward,
            @RequestParam("image") MultipartFile image
    ) {
        log.info("Received request to add item for user ID: {}", userId);

        Optional<AppUser> userOpt = userService.findUserById(userId);
        if (userOpt.isEmpty()) {
            log.error("User with ID {} not found", userId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        String imageUrl;
        try {
            imageUrl = saveImage(image);
        } catch (IOException e) {
            log.error("Error saving image: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unable to save image");
        }

        // Create new Item
        Item newItem = new Item();
        newItem.setTitle(title);
        newItem.setDescription(description);
        newItem.setStatus(status);
        newItem.setCategory(category);
        newItem.setLocation(location);
        newItem.setImageUrl(imageUrl);
        newItem.setUser(userOpt.get());
        newItem.setReward(reward);

        // Save to the database
        Item savedItem = itemService.saveItem(newItem);
        ItemDTO dto = mapToDTO(savedItem);

        return ResponseEntity.ok(dto);
    }

    // Update an existing item
    @PutMapping("/{id}")
    public ResponseEntity<?> updateItem(
            @PathVariable Long id,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("status") String status,
            @RequestParam("category") String category,
            @RequestParam("location") String location,
            @RequestParam("userId") Long userId,
            @RequestParam(value = "reward", required = false) Double reward,
            @RequestParam(value = "image", required = false) MultipartFile imageFile
    ) {
        log.info("Attempting to update item with ID {} by user ID {}", id, userId);

        Optional<Item> optionalItem = itemService.findById(id);
        if (optionalItem.isEmpty()) {
            log.error("Item with ID {} not found", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item not found");
        }

        Item existingItem = optionalItem.get();
        if (!existingItem.getUser().getId().equals(userId)) {
            log.error("User with ID {} is attempting to edit another user's item", userId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You cannot edit someone else's item");
        }

        // Update item details
        existingItem.setTitle(title);
        existingItem.setDescription(description);
        existingItem.setStatus(status);
        existingItem.setCategory(category);
        existingItem.setLocation(location);
        existingItem.setReward(reward);

        // Handle image if provided
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String imageUrl = saveImage(imageFile);
                existingItem.setImageUrl(imageUrl);
            } catch (IOException e) {
                log.error("Error saving image: {}", e.getMessage(), e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unable to save image");
            }
        }

        // Save updated item
        Item updatedItem = itemService.saveItem(existingItem);
        ItemDTO responseDto = mapToDTO(updatedItem);

        return ResponseEntity.ok(responseDto);
    }

    // Get items by user
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getItemsByUser(@PathVariable Long userId) {
        log.info("Request to get items for user with ID: {}", userId);

        Optional<AppUser> userOpt = userService.findUserById(userId);
        if (userOpt.isEmpty()) {
            log.error("User with ID {} not found", userId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        List<Item> items = itemService.findByUser(userOpt.get());

        List<ItemDTO> itemDTOs = items.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(itemDTOs);
    }

    // Get all items
    @GetMapping
    public ResponseEntity<?> getAllItems() {
        log.info("Request to get all items");

        Iterable<Item> items = itemService.findAllItems();

        List<ItemDTO> itemDTOs = StreamSupport.stream(items.spliterator(), false)
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(itemDTOs);
    }

    // Get items by status
    @GetMapping("/status/{status}")
    public ResponseEntity<?> getItemsByStatus(@PathVariable String status) {
        log.info("Request to get items with status: {}", status);

        List<Item> items = itemService.findByStatus(status);

        List<ItemDTO> itemDTOs = items.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(itemDTOs);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id, @RequestParam Long userId) {
        boolean isDeleted = itemService.deleteItem(id, userId);
        if (isDeleted) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    // Helper method to save image
    private String saveImage(MultipartFile image) throws IOException {
        String imageDirectory = "uploads/";

        // Check if the directory exists, if not create it
        File directory = new File(imageDirectory);
        if (!directory.exists() && !directory.mkdir()) {
            throw new IOException("Failed to create image directory");
        }

        // Generate a unique filename using timestamp
        String filename = System.currentTimeMillis() + "_" + image.getOriginalFilename();
        Path path = Paths.get(imageDirectory + filename);
        Files.write(path, image.getBytes());

        return imageDirectory + filename;
    }

    // Helper method to map Item to ItemDTO
    private ItemDTO mapToDTO(Item item) {
        return new ItemDTO(
                item.getTitle(),
                item.getDescription(),
                item.getImageUrl(),
                item.getStatus(),
                item.getCategory(),
                item.getLocation(),
                item.getUser() != null ? item.getUser().getId() : null,
                item.getReward()
        );
    }
}
