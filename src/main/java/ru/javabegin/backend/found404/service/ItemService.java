package ru.javabegin.backend.found404.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.javabegin.backend.found404.dto.ItemDTO;
import ru.javabegin.backend.found404.model.AppUser;
import ru.javabegin.backend.found404.model.Item;
import ru.javabegin.backend.found404.repository.ItemRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Service
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;

    public Item saveItem(Item item) {
        return itemRepository.save(item);
    }

    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    public List<Item> getLostItems() {
        return itemRepository.findByStatusIgnoreCase("lost");
    }

    public List<Item> getFoundItems() {
        return itemRepository.findByStatusIgnoreCase("found");
    }

    // Находит Item по id
    public Optional<Item> findById(Long id) {
        return itemRepository.findById(id);
    }

    // Удаляет Item по id
    public void deleteById(Long id) {
        itemRepository.deleteById(id);
    }

    public String saveImage(MultipartFile imageFile) throws IOException {
        try {
            // Генерация уникального имени для изображения
            String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();

            // Определяем путь для сохранения изображения
            Path uploadDir = Paths.get("uploads");

            // Проверяем, существует ли директория, если нет — создаем её
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // Путь для сохранения файла
            Path filePath = uploadDir.resolve(fileName);

            // Копируем файл в указанное место
            Files.copy(imageFile.getInputStream(), filePath);

            // Возвращаем относительный путь к файлу
            return "uploads/" + fileName;
        } catch (IOException e) {
            throw new IOException("Ошибка при сохранении изображения", e);
        }
    }


    // Обновление объявления (с возможностью замены изображения)
    public Optional<Item> updateItem(Long itemId, ItemDTO itemDTO, Long userId, MultipartFile imageFile) {
        Optional<Item> itemOpt = itemRepository.findById(itemId);
        if (itemOpt.isPresent() && itemOpt.get().getUser().getId().equals(userId)) {
            Item item = itemOpt.get();
            item.setTitle(itemDTO.getTitle());
            item.setDescription(itemDTO.getDescription());
            item.setStatus(itemDTO.getStatus());
            item.setCategory(itemDTO.getCategory());
            item.setLocation(itemDTO.getLocation());

            // Если предоставлено новое изображение, сохраняем его и обновляем путь
            if (imageFile != null && !imageFile.isEmpty()) {
                try {
                    String imageUrl = saveImage(imageFile);  // saveImage() now throws IOException
                    item.setImageUrl(imageUrl);
                } catch (IOException e) {
                    throw new RuntimeException("Ошибка при сохранении нового изображения", e);
                }
            }

            itemRepository.save(item);
            return Optional.of(item);
        }
        return Optional.empty();
    }


    // Удаление объявления (проверка владельца)
    public boolean deleteItem(Long itemId, Long userId) {
        Optional<Item> itemOpt = itemRepository.findById(itemId);
        if (itemOpt.isPresent() && itemOpt.get().getUser().getId().equals(userId)) {
            itemRepository.deleteById(itemId);
            return true;
        }
        return false;
    }

    // Обновление объявления
    public Optional<Item> updateItem(Long itemId, ItemDTO itemDTO, Long userId) {
        Optional<Item> itemOpt = itemRepository.findById(itemId);
        if (itemOpt.isPresent() && itemOpt.get().getUser().getId().equals(userId)) {
            Item item = itemOpt.get();
            item.setTitle(itemDTO.getTitle());
            item.setDescription(itemDTO.getDescription());
            item.setStatus(itemDTO.getStatus());
            item.setCategory(itemDTO.getCategory());
            item.setLocation(itemDTO.getLocation());
            itemRepository.save(item);
            return Optional.of(item);
        }
        return Optional.empty();
    }

    // Поиск объявлений по тексту в названии или описании
    public List<Item> searchByText(String text) {
        return itemRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(text, text);
    }



    public List<Item> search(String status, String category, String location) {
        if (status != null && category != null) {
            return itemRepository.findByStatusAndCategory(status, category);
        } else if (status != null) {
            return itemRepository.findByStatusIgnoreCase(status);

        } else if (category != null) {
            return itemRepository.findByCategory(category);
        } else if (location != null) {
            return itemRepository.findByLocationContainingIgnoreCase(location);
        } else {
            return itemRepository.findAll();
        }
    }

    public List<Item> findByUserId(Long userId) {
        return itemRepository.findByUser_Id(userId);
    }

    // В сервисе ItemService
    public Iterable<Item> findAllItems() {
        return itemRepository.findAll();  // возвращает все элементы
    }

    public List<Item> findByStatus(String status) {
        return itemRepository.findByStatus(status);  // возвращает элементы по статусу
    }

    public List<Item> findByUser(AppUser user) {
        return itemRepository.findByUser(user);
    }


}
