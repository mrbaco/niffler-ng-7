package guru.qa.niffler.test;

import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.service.UserDbClient;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static guru.qa.niffler.data.entity.auth.AuthorityEntity.Authority.read;
import static guru.qa.niffler.data.entity.auth.AuthorityEntity.Authority.write;

public class JdbcTest {

    private static final UserDbClient userDbClient = new UserDbClient();

    @Test
    void createUserInDatabase() {
        AuthUserEntity authUserEntity = new AuthUserEntity();

        AuthorityEntity authorityEntity1 = new AuthorityEntity();
        AuthorityEntity authorityEntity2 = new AuthorityEntity();

        authorityEntity1.setAuthority(read);
        authorityEntity2.setAuthority(write);

        authUserEntity.setUsername("i_am_test_user");
        authUserEntity.setPassword("test");
        authUserEntity.setEnabled(true);
        authUserEntity.setAccountNonExpired(true);
        authUserEntity.setAccountNonLocked(true);
        authUserEntity.setCredentialsNonExpired(true);
        authUserEntity.setAuthorities(Arrays.asList(authorityEntity1, authorityEntity2));

        AuthUserEntity user = userDbClient.createUser(authUserEntity);
        System.out.println(user);
    }

    @Test
    void removeUserFromDatabase() {
        AuthUserEntity authUserEntity = new AuthUserEntity();
        authUserEntity.setUsername("i_am_test_user");

        userDbClient.deleteUser(authUserEntity);
    }

}
