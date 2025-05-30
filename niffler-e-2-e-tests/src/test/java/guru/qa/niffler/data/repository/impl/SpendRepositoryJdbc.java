package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.mapper.CategoryEntityRowMapper;
import guru.qa.niffler.data.mapper.SpendEntityRowMapper;
import guru.qa.niffler.data.repository.SpendRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.Connections.holder;

public class SpendRepositoryJdbc implements SpendRepository {

    private static final Config CFG = Config.getInstance();

    @Override
    public SpendEntity create(SpendEntity spend) {
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "INSERT INTO spend (username, spend_date, currency, amount, description, category_id)" +
                        "VALUES (?, ?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
        )) {
            ps.setString(1, spend.getUsername());
            ps.setDate(2, new java.sql.Date(spend.getSpendDate().getTime()));
            ps.setString(3, spend.getCurrency().name());
            ps.setDouble(4, spend.getAmount());
            ps.setString(5, spend.getDescription());
            ps.setObject(6, spend.getCategory().getId());

            ps.executeUpdate();

            final UUID generatedKey;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedKey = rs.getObject("id", UUID.class);
                } else {
                    throw new SQLException("Can`t find id in ResultSet");
                }
            }

            spend.setId(generatedKey);

            return spend;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public SpendEntity create(SpendEntity spend, CategoryEntity category) {
        try (
                PreparedStatement categoryPs = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                        "INSERT INTO category (username, name, archived)" +
                                "VALUES (?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS
                );

                PreparedStatement spendPs = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                        "INSERT INTO spend (username, spend_date, currency, amount, description, category_id)" +
                                "VALUES (?, ?, ?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS
                )
    ) {
            categoryPs.setString(1, category.getUsername());
            categoryPs.setString(2, category.getName());
            categoryPs.setBoolean(3, category.isArchived());

            categoryPs.executeUpdate();

            final UUID categoryGeneratedKey;
            try (ResultSet rs = categoryPs.getGeneratedKeys()) {
                if (rs.next()) {
                    categoryGeneratedKey = rs.getObject("id", UUID.class);
                } else {
                    throw new SQLException("Can`t find id in ResultSet");
                }
            }

            category.setId(categoryGeneratedKey);

            spendPs.setString(1, spend.getUsername());
            spendPs.setDate(2, new java.sql.Date(spend.getSpendDate().getTime()));
            spendPs.setString(3, spend.getCurrency().name());
            spendPs.setDouble(4, spend.getAmount());
            spendPs.setString(5, spend.getDescription());
            spendPs.setObject(6, category.getId());

            spendPs.executeUpdate();

            final UUID spendGeneratedKey;
            try (ResultSet rs = spendPs.getGeneratedKeys()) {
                if (rs.next()) {
                    spendGeneratedKey = rs.getObject("id", UUID.class);
                } else {
                    throw new SQLException("Can`t find id in ResultSet");
                }
            }

            spend.setId(spendGeneratedKey);

            return spend;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<SpendEntity> findById(UUID id) {
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                """
                            SELECT
                                  c.id as category_id,
                                  name,
                                  archived,
                                  s.id as id,
                                  s.username,
                                  s.spend_date,
                                  s.currency,
                                  s.amount,
                                  s.description
                            FROM spend s
                            JOIN category c
                            ON s.category_id = c.id
                            WHERE s.id = ?
                """
        )) {
            ps.setObject(1, id);

            ps.execute();

            try (ResultSet rs = ps.getResultSet()) {
                SpendEntity spendEntity = null;
                CategoryEntity categoryEntity = null;

                if (rs.next()) {
                    spendEntity = SpendEntityRowMapper.instance.mapRow(rs, 1);
                    categoryEntity = CategoryEntityRowMapper.instance.mapRow(rs, 1);
                }

                if (spendEntity == null || categoryEntity == null) {
                    return Optional.empty();
                } else {
                    spendEntity.setCategory(categoryEntity);
                    return Optional.of(spendEntity);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<SpendEntity> findAllByUsername(String username) {
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                """
                            SELECT
                                  c.id as category_id,
                                  name,
                                  archived,
                                  s.id as id,
                                  s.username,
                                  s.spend_date,
                                  s.currency,
                                  s.amount,
                                  s.description
                            FROM spend s
                            JOIN category c
                            ON s.category_id = c.id
                            WHERE s.id = ?
                """
        )) {
            ps.setString(1, username);

            ps.execute();

            List<SpendEntity> result = new ArrayList<>();

            try (ResultSet rs = ps.getResultSet()) {
                while (rs.next()) {
                    SpendEntity spendEntity = SpendEntityRowMapper.instance.mapRow(rs, 1);
                    if (spendEntity != null) {
                        CategoryEntity categoryEntity = CategoryEntityRowMapper.instance.mapRow(rs, 1);
                        spendEntity.setCategory(categoryEntity);

                        result.add(SpendEntityRowMapper.instance.mapRow(rs, 1));
                    }
                }
            }

            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<SpendEntity> findAll() {
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                """
                                SELECT
                                      c.id as category_id,
                                      name,
                                      archived,
                                      s.id as id,
                                      s.username,
                                      s.spend_date,
                                      s.currency,
                                      s.amount,
                                      s.description
                                FROM spend s
                                JOIN category c
                                ON s.category_id = c.id
                                WHERE s.id = ?
                    """
        )) {
            ps.execute();

            List<SpendEntity> result = new ArrayList<>();

            try (ResultSet rs = ps.getResultSet()) {
                while (rs.next()) {
                    SpendEntity spendEntity = SpendEntityRowMapper.instance.mapRow(rs, 1);
                    if (spendEntity != null) {
                        CategoryEntity categoryEntity = CategoryEntityRowMapper.instance.mapRow(rs, 1);
                        spendEntity.setCategory(categoryEntity);

                        result.add(SpendEntityRowMapper.instance.mapRow(rs, 1));
                    }
                }
            }

            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(UUID id) {
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "DELETE FROM spend WHERE id = ?"
        )) {
            ps.setObject(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
