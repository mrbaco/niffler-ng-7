package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.dao.UdUserDAO;
import guru.qa.niffler.data.dao.impl.AuthAuthorityDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.AuthUserDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.UdUserDAOSpringJdbc;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.auth.AuthorityEntity.Authority;
import guru.qa.niffler.model.UserJson;
import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static guru.qa.niffler.data.tpl.DataSources.dataSource;

@Deprecated
public class UserChainedXaSpringDbClient {

    private static final Config CFG = Config.getInstance();

    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private final AuthUserDao authUserDao = new AuthUserDaoSpringJdbc();
    private final AuthAuthorityDao authAuthorityDao = new AuthAuthorityDaoSpringJdbc();
    private final UdUserDAO udUserDAO = new UdUserDAOSpringJdbc();

    private final TransactionTemplate chainedTransaction = new TransactionTemplate(new ChainedTransactionManager(
            new JdbcTransactionManager(dataSource(CFG.authJdbcUrl())),
            new JdbcTransactionManager(dataSource(CFG.userdataUrl()))
    ));

    public UserJson createUser(UserJson user) {
        return chainedTransaction.execute(connection -> {
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

}
