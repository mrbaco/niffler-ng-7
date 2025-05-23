package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.Connections.holder;

public class AuthAuthorityDaoJdbc implements AuthAuthorityDao {

    private static final Config CFG = Config.getInstance();

    @Override
    public List<AuthorityEntity> create(List<AuthorityEntity> authorities) {
        for (AuthorityEntity authority : authorities) {
            try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                    "INSERT INTO authority (user_id, authority)" +
                            "VALUES (?, ?)"
            )) {
                ps.setObject(1, authority.getUser().getId());
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
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        return authorities;
    }

    @Override
    public List<AuthorityEntity> findByUserId(UUID id) {
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM authority WHERE user_id = ?"
        )) {
            ps.setObject(1, id);

            ps.execute();

            List<AuthorityEntity> result = new ArrayList<>();

            try (ResultSet rs = ps.getResultSet()) {
                while (rs.next()) {
                    AuthorityEntity authorityEntity = new AuthorityEntity();

                    authorityEntity.setId(rs.getObject("id", UUID.class));
                    authorityEntity.getUser().setId(rs.getObject("user_id", UUID.class));
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
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
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
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM authority"
        )) {
            ps.execute();

            List<AuthorityEntity> result = new ArrayList<>();

            try (ResultSet rs = ps.getResultSet()) {
                while (rs.next()) {
                    AuthorityEntity authorityEntity = new AuthorityEntity();

                    authorityEntity.setId(rs.getObject("id", UUID.class));
                    authorityEntity.getUser().setId(rs.getObject("user_id", UUID.class));
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
