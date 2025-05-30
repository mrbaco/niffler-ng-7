package guru.qa.niffler.data.repository;

import guru.qa.niffler.data.entity.spend.CategoryEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository {

    CategoryEntity create(CategoryEntity category);
    Optional<CategoryEntity> findById(UUID id);
    List<CategoryEntity> findAllByUsername(String username);
    List<CategoryEntity> findAll();
    void archiveCategory(UUID id);

}
