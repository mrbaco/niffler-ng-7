package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.Databases.XaConsumer;
import guru.qa.niffler.data.Databases.XaFunction;
import guru.qa.niffler.data.dao.impl.AuthAuthorityDaoJdbc;
import guru.qa.niffler.data.dao.impl.AuthUserDaoJdbc;
import guru.qa.niffler.data.dao.impl.UdUserDAOJdbc;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.userdata.UdUserEntity;

import static guru.qa.niffler.data.Databases.xaTransaction;
import static java.sql.Connection.TRANSACTION_READ_UNCOMMITTED;

public class UserDbClient {

    private static final Config CFG = Config.getInstance();

    public AuthUserEntity createUser(AuthUserEntity user) {
        return xaTransaction(
                TRANSACTION_READ_UNCOMMITTED,
                new XaFunction<>(connection -> {
                    UdUserEntity udUserEntity = new UdUserEntity();
                    udUserEntity.setUsername(user.getUsername());

                    new UdUserDAOJdbc(connection).create(udUserEntity);

                    return user;
                }, CFG.userdataJdbcUrl()),
                new XaFunction<>(connection -> {
                    AuthUserEntity createdUser = new AuthUserDaoJdbc(connection).create(user);
                    user.setId(createdUser.getId());

                    if (user.getAuthorities() != null) {
                        user.getAuthorities().forEach(authority ->
                                new AuthAuthorityDaoJdbc(connection).create(authority));
                    }

                    return user;
                }, CFG.authJdbcUrl())
        );
    }

    public void deleteUser(AuthUserEntity user) {
        xaTransaction(
                new XaConsumer(connection -> {
                    if (user.getId() == null) {
                        AuthUserEntity authUserEntity = new AuthUserDaoJdbc(connection)
                                .findByUsername(user.getUsername())
                                .orElseThrow();
                        user.setId(authUserEntity.getId());
                    }

                    new AuthAuthorityDaoJdbc(connection).deleteByUserId(user.getId());
                    new AuthUserDaoJdbc(connection).delete(user.getId());
                }, CFG.authJdbcUrl()),
                new XaConsumer(connection -> {
                    UdUserDAOJdbc userdataUserDAOJdbc = new UdUserDAOJdbc(connection);

                    UdUserEntity udUserEntity = new UdUserEntity();
                    udUserEntity.setUsername(user.getUsername());

                    udUserEntity.setId(userdataUserDAOJdbc
                            .findByUsername(udUserEntity.getUsername())
                            .orElseThrow()
                            .getId());

                    userdataUserDAOJdbc.delete(udUserEntity.getId());
                }, CFG.userdataJdbcUrl())
        );
    }

}
