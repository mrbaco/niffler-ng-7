package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.mapper.AuthorityEntityRowMapper;
import guru.qa.niffler.data.tpl.DataSources;
import org.jetbrains.annotations.NotNull;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class AuthAuthorityDaoSpringJdbc implements AuthAuthorityDao {

    private static final Config CFG = Config.getInstance();

    @Override
    public List<AuthorityEntity> create(List<AuthorityEntity> authorities) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));
        KeyHolder kh = new GeneratedKeyHolder();

        jdbcTemplate.batchUpdate(
                con -> con.prepareStatement(
                        "INSERT INTO authority (user_id, authority) VALUES (?, ?)",
                        Statement.RETURN_GENERATED_KEYS
                ),
                new BatchPreparedStatementSetter() {

                    @Override
                    public void setValues(@NotNull PreparedStatement ps, int i) throws SQLException {
                        ps.setObject(1, authorities.get(i).getUserId());
                        ps.setString(2, authorities.get(i).getAuthority().name());
                    }

                    @Override
                    public int getBatchSize() {
                        return authorities.size();
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
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));

        return jdbcTemplate.queryForStream(
                "SELECT * FROM authority WHERE user_id = ?",
                AuthorityEntityRowMapper.instance,
                id
        ).collect(Collectors.toList());
    }

    @Override
    public void deleteByUserId(UUID id) {
        new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl())).update("DELETE FROM authority WHERE user_id = ?", id);
    }

    @Override
    public List<AuthorityEntity> findAll() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));

        return jdbcTemplate.queryForStream(
                "SELECT * FROM authority",
                AuthorityEntityRowMapper.instance
        ).collect(Collectors.toList());
    }

}
