package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.data.dao.UserdataUserDAO;
import guru.qa.niffler.data.entity.userdata.UdUserEntity;
import guru.qa.niffler.model.CurrencyValues;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.UUID;

public class UserdataUserDAOJdbc implements UserdataUserDAO {

    private final Connection connection;

    public UserdataUserDAOJdbc(Connection connection) {
        this.connection = connection;
    }

    @Override
    public UdUserEntity create(UdUserEntity user) {
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO user (username, currency, firstname, surname, photo)" +
                        "VALUES (?, ?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
        )) {
            ps.setObject(1, user.getUsername());
            ps.setString(2, user.getCurrency().name());
            ps.setString(3, user.getFirstname());
            ps.setString(4, user.getSurname());
            ps.setBytes(5, user.getPhoto());

            ps.executeUpdate();

            final UUID generatedKey;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedKey = rs.getObject("id", UUID.class);
                } else {
                    throw new SQLException("Can`t find id in ResultSet");
                }
            }

            user.setId(generatedKey);

            return user;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<UdUserEntity> findById(UUID id) {
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM user WHERE id = ?"
        )) {
            ps.setObject(1, id);

            ps.execute();

            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    UdUserEntity entity = new UdUserEntity();

                    entity.setId(rs.getObject("id", UUID.class));
                    entity.setUsername(rs.getString("username"));
                    entity.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
                    entity.setFirstname(rs.getString("firstname"));
                    entity.setSurname(rs.getString("surname"));
                    entity.setPhoto(rs.getBytes("photo"));

                    return Optional.of(entity);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<UdUserEntity> findByUsername(String username) {
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM user WHERE username = ?"
        )) {
            ps.setString(1, username);

            ps.execute();

            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    UdUserEntity entity = new UdUserEntity();

                    entity.setId(rs.getObject("id", UUID.class));
                    entity.setUsername(rs.getString("username"));
                    entity.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
                    entity.setFirstname(rs.getString("firstname"));
                    entity.setSurname(rs.getString("surname"));
                    entity.setPhoto(rs.getBytes("photo"));

                    return Optional.of(entity);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(UdUserEntity user) {
        try (PreparedStatement ps = connection.prepareStatement(
                "DELETE FROM user WHERE id = ?"
        )) {
            ps.setObject(1, user.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
