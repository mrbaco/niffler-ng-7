package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.dao.UdUserDAO;
import guru.qa.niffler.data.dao.impl.*;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.auth.AuthorityEntity.Authority;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.UserJson;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class UserSpringDbClient {

    private static final Config CFG = Config.getInstance();

    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private final AuthUserDao authUserDao = new AuthUserDaoSpringJdbc();
    private final AuthAuthorityDao authAuthorityDao = new AuthAuthorityDaoSpringJdbc();
    private final UdUserDAO udUserDAO = new UdUserDAOSpringJdbc();

    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
            CFG.authJdbcUrl(),
            CFG.userdataJdbcUrl()
    );

    public UserJson createUser(UserJson user) {
        return xaTransactionTemplate.execute(() -> {
            AuthUserEntity authUser = new AuthUserEntity();

            authUser.setUsername(user.username());
            authUser.setPassword(pe.encode("test"));
            authUser.setEnabled(true);
            authUser.setAccountNonExpired(true);
            authUser.setAccountNonLocked(true);
            authUser.setCredentialsNonExpired(true);

            authUserDao.create(authUser);

            List<AuthorityEntity> authorityEntities = Arrays.stream(Authority.values()).map(e -> {
                AuthorityEntity authorityEntity = new AuthorityEntity();

                authorityEntity.setUserId(null);
                authorityEntity.setAuthority(e);

                return authorityEntity;
            }).collect(Collectors.toList());

            authAuthorityDao.create(authorityEntities);

            return udUserDAO.create(user.toUdUserEntity()).toJson();
        });
    }

    public void deleteUser(UserJson user) {
        xaTransactionTemplate.execute(() -> {
            udUserDAO.delete(user.id());
            authUserDao.delete(user.id());
            authAuthorityDao.deleteByUserId(user.id());
            return user;
        });
    }

}
