package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.dao.UdUserDAO;
import guru.qa.niffler.data.dao.impl.AuthAuthorityDaoJdbc;
import guru.qa.niffler.data.dao.impl.AuthAuthorityDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.AuthUserDaoJdbc;
import guru.qa.niffler.data.dao.impl.AuthUserDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.UdUserDaoJdbc;
import guru.qa.niffler.data.dao.impl.UdUserDaoSpringJdbc;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.repository.impl.AuthUserRepositoryJdbc;
import guru.qa.niffler.data.tpl.JdbcTransactionTemplate;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.UserJson;
import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static guru.qa.niffler.data.tpl.DataSources.dataSource;

public class UserDbClient {

    private static final Config CFG = Config.getInstance();

    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private final AuthUserRepository authUserRepository = new AuthUserRepositoryJdbc();

    private final AuthUserDao authUserDaoJdbc = new AuthUserDaoJdbc();
    private final AuthAuthorityDao authAuthorityDaoJdbc = new AuthAuthorityDaoJdbc();
    private final UdUserDAO udUserDaoJdbc = new UdUserDaoJdbc();

    private final AuthUserDao authUserDaoSpringJdbc = new AuthUserDaoSpringJdbc();
    private final AuthAuthorityDao authAuthorityDaoSpringJdbc = new AuthAuthorityDaoSpringJdbc();
    private final UdUserDAO udUserDaoSpringJdbc = new UdUserDaoSpringJdbc();

    private final JdbcTransactionTemplate jdbcTxTemplate = new JdbcTransactionTemplate(
            CFG.spendJdbcUrl()
    );

    private final XaTransactionTemplate xaTxTemplate = new XaTransactionTemplate(
            CFG.authJdbcUrl(),
            CFG.userdataJdbcUrl()
    );

    @Deprecated
    private final TransactionTemplate chainedTxTemplate = new TransactionTemplate(new ChainedTransactionManager(
            new JdbcTransactionManager(dataSource(CFG.authJdbcUrl())),
            new JdbcTransactionManager(dataSource(CFG.userdataJdbcUrl()))
    ));

    public boolean existAuthUser(String username) {
        return authUserDaoJdbc.findByUsername(username).orElse(null) != null;
    }

    public boolean existAuthAuthorityEntities(String username) {
        AuthUserEntity authUser = authUserDaoJdbc.findByUsername(username).orElse(null);

        List<AuthorityEntity> authorityEntities = new ArrayList<>();
        if (authUser != null) {
            authorityEntities = authAuthorityDaoJdbc.findByUserId(authUser.getId());
        }

        return authorityEntities.size() == 2;
    }

    public boolean existUdUser(String username) {
        return udUserDaoJdbc.findByUsername(username).orElse(null) != null;
    }

    public void deleteUdUser(String username) {
        udUserDaoJdbc.findByUsername(username).ifPresent(user -> udUserDaoJdbc.deleteByUsername(username));
    }

    public boolean existUser(String username) {
        return existAuthUser(username) && existAuthAuthorityEntities(username)  && existUdUser(username);
    }

    public UserJson createUserJdbcTx(UserJson user) {
        return jdbcTxTemplate.execute(() -> {
            AuthUserEntity authUser = new AuthUserEntity();

            authUser.setUsername(user.username());
            authUser.setPassword(pe.encode("test"));
            authUser.setEnabled(true);
            authUser.setAccountNonExpired(true);
            authUser.setAccountNonLocked(true);
            authUser.setCredentialsNonExpired(true);

            authUserDaoJdbc.create(authUser);

            List<AuthorityEntity> authorityEntities = Arrays.stream(Authority.values()).map(e -> {
                AuthorityEntity authorityEntity = new AuthorityEntity();

                authorityEntity.setUser(authUser);
                authorityEntity.setAuthority(e);

                return authorityEntity;
            }).collect(Collectors.toList());

            if (user.error()) {
                throw new RuntimeException("parameter `error` for user");
            }

            authAuthorityDaoJdbc.create(authorityEntities);

            return udUserDaoJdbc.create(user.toUdUserEntity()).toJson();
        });
    }

    public void deleteUserJdbcTx(UserJson user) {
        jdbcTxTemplate.execute(() -> {
            AuthUserEntity userEntity = authUserDaoJdbc.findByUsername(user.username()).orElse(null);

            if (userEntity != null) {
                authAuthorityDaoJdbc.deleteByUserId(userEntity.getId());
                udUserDaoJdbc.deleteByUsername(user.username());

                if (user.error()) {
                    throw new RuntimeException("parameter `error` for user");
                }

                authUserDaoJdbc.delete(userEntity.getId());
            }

            return user;
        });
    }

    public UserJson createUserJdbc(UserJson user) {
        AuthUserEntity authUser = new AuthUserEntity();

        authUser.setUsername(user.username());
        authUser.setPassword(pe.encode("test"));
        authUser.setEnabled(true);
        authUser.setAccountNonExpired(true);
        authUser.setAccountNonLocked(true);
        authUser.setCredentialsNonExpired(true);

        authUserDaoJdbc.create(authUser);

        List<AuthorityEntity> authorityEntities = Arrays.stream(Authority.values()).map(e -> {
            AuthorityEntity authorityEntity = new AuthorityEntity();

            authorityEntity.setUser(authUser);
            authorityEntity.setAuthority(e);

            return authorityEntity;
        }).collect(Collectors.toList());

        if (user.error()) {
            throw new RuntimeException("parameter `error` for user");
        }

        authAuthorityDaoJdbc.create(authorityEntities);

        return udUserDaoJdbc.create(user.toUdUserEntity()).toJson();
    }

    public void deleteUserJdbc(UserJson user) {
        AuthUserEntity userEntity = authUserDaoJdbc.findByUsername(user.username()).orElse(null);

        if (userEntity != null) {
            authAuthorityDaoJdbc.deleteByUserId(userEntity.getId());
            udUserDaoJdbc.deleteByUsername(user.username());
            authUserDaoJdbc.delete(userEntity.getId());
        }
    }

    public UserJson createUserSpringJdbcTx(UserJson user) {
        return xaTxTemplate.execute(() -> {
            AuthUserEntity authUser = new AuthUserEntity();

            authUser.setUsername(user.username());
            authUser.setPassword(pe.encode("test"));
            authUser.setEnabled(true);
            authUser.setAccountNonExpired(true);
            authUser.setAccountNonLocked(true);
            authUser.setCredentialsNonExpired(true);
            authUser.setAuthorities(
                    Arrays.stream(Authority.values()).map(e -> {
                        AuthorityEntity authorityEntity = new AuthorityEntity();

                        authorityEntity.setUser(authUser);
                        authorityEntity.setAuthority(e);

                        return authorityEntity;
                    }).collect(Collectors.toList())
            );

            authUserRepository.create(authUser);

            return udUserDaoSpringJdbc.create(user.toUdUserEntity()).toJson();
        });
    }

    public void deleteUserSpringJdbcTx(UserJson user) {
        xaTxTemplate.execute(() -> {
            AuthUserEntity userEntity = authUserDaoSpringJdbc.findByUsername(user.username()).orElse(null);

            if (userEntity != null) {
                authAuthorityDaoSpringJdbc.deleteByUserId(userEntity.getId());
                udUserDaoSpringJdbc.deleteByUsername(user.username());

                if (user.error()) {
                    throw new RuntimeException("parameter `error` for user");
                }

                authUserDaoSpringJdbc.delete(userEntity.getId());
            }

            return user;
        });
    }

    public UserJson createUserSpringJdbc(UserJson user) {
        AuthUserEntity authUser = new AuthUserEntity();

        authUser.setUsername(user.username());
        authUser.setPassword(pe.encode("test"));
        authUser.setEnabled(true);
        authUser.setAccountNonExpired(true);
        authUser.setAccountNonLocked(true);
        authUser.setCredentialsNonExpired(true);

        authUserDaoSpringJdbc.create(authUser);

        List<AuthorityEntity> authorityEntities = Arrays.stream(Authority.values()).map(e -> {
            AuthorityEntity authorityEntity = new AuthorityEntity();

            authorityEntity.setUser(authUser);
            authorityEntity.setAuthority(e);

            return authorityEntity;
        }).collect(Collectors.toList());

        if (user.error()) {
            throw new RuntimeException("parameter `error` for user");
        }

        authAuthorityDaoSpringJdbc.create(authorityEntities);

        return udUserDaoSpringJdbc.create(user.toUdUserEntity()).toJson();
    }

    public void deleteUserSpringJdbc(UserJson user) {
        AuthUserEntity userEntity = authUserDaoSpringJdbc.findByUsername(user.username()).orElse(null);

        if (userEntity != null) {
            authAuthorityDaoSpringJdbc.deleteByUserId(userEntity.getId());
            udUserDaoSpringJdbc.deleteByUsername(user.username());
            authUserDaoSpringJdbc.delete(userEntity.getId());
        }
    }

    @Deprecated
    public UserJson createUserSpringJdbcChainedTx(UserJson user) {
        return chainedTxTemplate.execute(status -> {
            AuthUserEntity authUser = new AuthUserEntity();

            authUser.setUsername(user.username());
            authUser.setPassword(pe.encode("test"));
            authUser.setEnabled(true);
            authUser.setAccountNonExpired(true);
            authUser.setAccountNonLocked(true);
            authUser.setCredentialsNonExpired(true);

            authUserDaoJdbc.create(authUser);

            List<AuthorityEntity> authorityEntities = Arrays.stream(Authority.values()).map(e -> {
                AuthorityEntity authorityEntity = new AuthorityEntity();

                authorityEntity.setUser(authUser);
                authorityEntity.setAuthority(e);

                return authorityEntity;
            }).collect(Collectors.toList());

            authAuthorityDaoJdbc.create(authorityEntities);

            if (user.error()) {
                throw new RuntimeException("parameter `error` for user");
            }

            return udUserDaoJdbc.create(user.toUdUserEntity()).toJson();
        });
    }

    @Deprecated
    public void deleteUserSpringJdbcChainedTx(UserJson user) {
        chainedTxTemplate.execute(status -> {
            AuthUserEntity userEntity = authUserDaoJdbc.findByUsername(user.username()).orElse(null);

            if (userEntity != null) {
                authAuthorityDaoJdbc.deleteByUserId(userEntity.getId());
                udUserDaoJdbc.deleteByUsername(user.username());
                authUserDaoJdbc.delete(userEntity.getId());
            }

            return user;
        });
    }

}
