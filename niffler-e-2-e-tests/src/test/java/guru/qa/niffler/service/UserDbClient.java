package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.Databases.XaConsumer;
import guru.qa.niffler.data.Databases.XaFunction;
import guru.qa.niffler.data.dao.impl.AuthAuthorityDaoJdbc;
import guru.qa.niffler.data.dao.impl.AuthUserDaoJdbc;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.auth.AuthorityEntity.Authority;
import guru.qa.niffler.data.entity.auth.UserEntity;

import java.util.ArrayList;
import java.util.List;

import static guru.qa.niffler.data.Databases.xaTransaction;
import static java.sql.Connection.TRANSACTION_READ_UNCOMMITTED;

public class UserDbClient {

    private static final Config CFG = Config.getInstance();

    public UserEntity createUser(UserEntity user) {
        return xaTransaction(
                TRANSACTION_READ_UNCOMMITTED,
                new XaFunction<>(connection -> {
                    UserEntity created = new AuthUserDaoJdbc(connection).create(user);
                    user.setId(created.getId());

                    return user;
                }, CFG.authJdbcUrl()),
                new XaFunction<>(connection -> {
                    if (user.getAuthorities() != null) {
                        user.getAuthorities().forEach(authority -> {
                            AuthorityEntity created = new AuthAuthorityDaoJdbc(connection).create(authority);
                            authority.setId(created.getId());
                        });
                    }

                    return user;
                }, CFG.authJdbcUrl())
        );
    }

    public void deleteUser(UserEntity user) {
        xaTransaction(
                new XaConsumer(connection -> {
                    if (user.getId() == null) {
                        UserEntity userEntity = new AuthUserDaoJdbc(connection)
                                .findByUsername(user.getUsername())
                                .orElseThrow();
                        user.setId(userEntity.getId());
                    }

                    new AuthAuthorityDaoJdbc(connection).deleteByUserId(user.getId());
                }, CFG.authJdbcUrl()),
                new XaConsumer(connection -> new AuthUserDaoJdbc(connection).delete(user), CFG.authJdbcUrl())
        );
    }

}
