package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.mapper.AuthorityEntityRowMapper;
import org.jetbrains.annotations.NotNull;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class AuthAuthorityDaoSpringJdbc implements AuthAuthorityDao {

    private final DataSource dataSource;

    public AuthAuthorityDaoSpringJdbc(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public AuthorityEntity[] create(AuthorityEntity... authorities) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        KeyHolder kh = new GeneratedKeyHolder();

        jdbcTemplate.batchUpdate(
                con -> con.prepareStatement(
                        "INSERT INTO authority (user_id, authority) VALUES (?, ?)",
                        Statement.RETURN_GENERATED_KEYS
                ),
                new BatchPreparedStatementSetter() {

                    @Override
                    public void setValues(@NotNull PreparedStatement ps, int i) throws SQLException {
                        ps.setObject(i, authorities[i].getUserId());
                        ps.setString(i, authorities[i].getAuthority().name());
                    }

                    @Override
                    public int getBatchSize() {
                        return authorities.length;
                    }

                },
                kh
        );

        List<Map<String, Object>> keys = kh.getKeyList();

        for (AuthorityEntity authority : authorities) {
            authority.setId((UUID) keys.remove(0).get("id"));
        }

        return authorities;
    }

    @Override
    public List<AuthorityEntity> findByUserId(UUID id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        return jdbcTemplate.queryForStream(
                "SELECT * FROM \"user\" WHERE id = ?",
                AuthorityEntityRowMapper.instance,
                id
        ).collect(Collectors.toList());
    }

    @Override
    public void deleteByUserId(UUID id) {
        new JdbcTemplate(dataSource).update("DELETE FROM \"user\" WHERE id = ?", id);
    }

    @Override
    public List<AuthorityEntity> findAll() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        return jdbcTemplate.queryForStream(
                "SELECT * FROM \"user\"",
                AuthorityEntityRowMapper.instance
        ).collect(Collectors.toList());
    }

}
