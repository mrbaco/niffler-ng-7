package guru.qa.niffler.test;

import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.auth.UserEntity;
import guru.qa.niffler.service.UserDbClient;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static guru.qa.niffler.data.entity.auth.AuthorityEntity.Authority.read;
import static guru.qa.niffler.data.entity.auth.AuthorityEntity.Authority.write;

public class JdbcTest {

    private static final UserDbClient userDbClient = new UserDbClient();

    @Test
    void createUserInDatabase() {
        UserEntity userEntity = new UserEntity();

        AuthorityEntity authorityEntity1 = new AuthorityEntity();
        AuthorityEntity authorityEntity2 = new AuthorityEntity();

        authorityEntity1.setUser(userEntity);
        authorityEntity1.setAuthority(read);

        authorityEntity2.setUser(userEntity);
        authorityEntity2.setAuthority(write);

        userEntity.setUsername("i_am_test_user");
        userEntity.setPassword("test");
        userEntity.setEnabled(true);
        userEntity.setAccountNonExpired(true);
        userEntity.setAccountNonLocked(true);
        userEntity.setCredentialsNonExpired(true);
        userEntity.setAuthorities(Arrays.asList(authorityEntity1, authorityEntity2));

        UserEntity user = userDbClient.createUser(userEntity);
        System.out.println(user);
    }

    @Test
    void removeUserFromDatabase() {
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername("i_am_test_user");

        userDbClient.deleteUser(userEntity);
    }

}
