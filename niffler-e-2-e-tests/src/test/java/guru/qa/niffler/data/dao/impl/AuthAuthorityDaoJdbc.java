package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.auth.AuthorityEntity.Authority;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AuthAuthorityDaoJdbc implements AuthAuthorityDao {

    private final Connection connection;

    public AuthAuthorityDaoJdbc(Connection connection) {
        this.connection = connection;
    }

    @Override
    public AuthorityEntity[] create(AuthorityEntity... authorities) {
        for (AuthorityEntity authority : authorities) {
            try (PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO authority (user_id, authority)" +
                            "VALUES (?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            )) {
                ps.setObject(1, authority.getUserId());
                ps.setString(2, authority.getAuthority().name());

                ps.executeUpdate();

                final UUID generatedKey;
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedKey = rs.getObject("id", UUID.class);
                    } else {
                        throw new SQLException("Can`t find id in ResultSet");
                    }
                }

                authority.setId(generatedKey);

                return authorities;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        return authorities;
    }

    @Override
    public List<AuthorityEntity> findByUserId(UUID id) {
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM authority WHERE user_id = ?"
        )) {
            ps.setObject(1, id);

            ps.execute();

            List<AuthorityEntity> result = new ArrayList<>();

            try (ResultSet rs = ps.getResultSet()) {
                while (rs.next()) {
                    AuthorityEntity authorityEntity = new AuthorityEntity();

                    authorityEntity.setId(rs.getObject("id", UUID.class));
                    authorityEntity.setUserId(rs.getObject("user_id", UUID.class));
                    authorityEntity.setAuthority(Authority.valueOf(rs.getString("authority")));

                    result.add(authorityEntity);
                }
            }

            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteByUserId(UUID id) {
        try (PreparedStatement ps = connection.prepareStatement(
                "DELETE FROM authority WHERE user_id = ?"
        )) {
            ps.setObject(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<AuthorityEntity> findAll() {
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM authority"
        )) {
            ps.execute();

            List<AuthorityEntity> result = new ArrayList<>();

            try (ResultSet rs = ps.getResultSet()) {
                while (rs.next()) {
                    AuthorityEntity authorityEntity = new AuthorityEntity();

                    authorityEntity.setId(rs.getObject("id", UUID.class));
                    authorityEntity.setUserId(rs.getObject("user_id", UUID.class));
                    authorityEntity.setAuthority(Authority.valueOf(rs.getString("authority")));

                    result.add(authorityEntity);
                }
            }

            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
