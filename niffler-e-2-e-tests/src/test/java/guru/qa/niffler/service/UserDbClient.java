package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.Databases.XaConsumer;
import guru.qa.niffler.data.Databases.XaFunction;
import guru.qa.niffler.data.dao.impl.AuthAuthorityDaoJdbc;
import guru.qa.niffler.data.dao.impl.AuthUserDaoJdbc;
import guru.qa.niffler.data.dao.impl.UserdataUserDAOJdbc;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.auth.UserEntity;
import guru.qa.niffler.data.entity.userdata.UdUserEntity;

import static guru.qa.niffler.data.Databases.xaTransaction;
import static java.sql.Connection.TRANSACTION_READ_UNCOMMITTED;

public class UserDbClient {

    private static final Config CFG = Config.getInstance();

    public UserEntity createUser(UserEntity user) {
        return xaTransaction(
                TRANSACTION_READ_UNCOMMITTED,
                new XaFunction<>(connection -> {
                    UdUserEntity udUserEntity = new UdUserEntity();
                    udUserEntity.setUsername(user.getUsername());

                    new UserdataUserDAOJdbc(connection).create(udUserEntity);

                    return user;
                }, CFG.userdataJdbcUrl()),
                new XaFunction<>(connection -> {
                    UserEntity createdUser = new AuthUserDaoJdbc(connection).create(user);
                    user.setId(createdUser.getId());

                    if (user.getAuthorities() != null) {
                        user.getAuthorities().forEach(authority -> {
                            AuthorityEntity createdAuthorities = new AuthAuthorityDaoJdbc(connection).create(authority);
                            authority.setId(createdAuthorities.getId());
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
                    new AuthUserDaoJdbc(connection).delete(user);
                }, CFG.authJdbcUrl()),
                new XaConsumer(connection -> {
                    UserdataUserDAOJdbc userdataUserDAOJdbc = new UserdataUserDAOJdbc(connection);

                    UdUserEntity udUserEntity = new UdUserEntity();
                    udUserEntity.setUsername(user.getUsername());

                    udUserEntity.setId(userdataUserDAOJdbc
                            .findByUsername(udUserEntity.getUsername())
                            .orElseThrow()
                            .getId());

                    userdataUserDAOJdbc.delete(udUserEntity);
                }, CFG.userdataJdbcUrl())
        );
    }

}
