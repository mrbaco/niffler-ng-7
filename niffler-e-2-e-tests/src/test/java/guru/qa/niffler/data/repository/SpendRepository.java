package guru.qa.niffler.data.repository;

import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpendRepository {

    SpendEntity create(SpendEntity spend);
    SpendEntity create(SpendEntity spend, CategoryEntity category);
    Optional<SpendEntity> findById(UUID id);
    List<SpendEntity> findAllByUsername(String username);
    List<SpendEntity> findAll();
    void delete(UUID id);

}
