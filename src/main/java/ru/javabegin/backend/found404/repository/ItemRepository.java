package ru.javabegin.backend.found404.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.javabegin.backend.found404.model.AppUser;
import ru.javabegin.backend.found404.model.Item;
import java.util.List;



public interface ItemRepository extends JpaRepository<Item, Long> {

    // Поиск по статусу
    List<Item> findByStatusIgnoreCase(String status);

    List<Item> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String title, String description);


    // Поиск по категории
    List<Item> findByCategory(String category);

    // Поиск по местоположению (не чувствительно к регистру)
    List<Item> findByLocationContainingIgnoreCase(String location);

    // Поиск по статусу и категории
    List<Item> findByStatusAndCategory(String status, String category);

    // Поиск по статусу и местоположению
    List<Item> findByStatusAndLocationContainingIgnoreCase(String status, String location);

    // Поиск по категории и местоположению
    List<Item> findByCategoryAndLocationContainingIgnoreCase(String category, String location);

    // Поиск по статусу, категории и местоположению
    List<Item> findByStatusAndCategoryAndLocationContainingIgnoreCase(String status, String category, String location);

    // Поиск по userId
    List<Item> findByUser_Id(Long userId); // Добавлен метод для поиска по userId

    List<Item> findByStatus(String status);

    List<Item> findByUser(AppUser user);
}
