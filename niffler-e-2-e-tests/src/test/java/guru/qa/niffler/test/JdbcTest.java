package guru.qa.niffler.test;

import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.UserChainedXaSpringDbClient;
import guru.qa.niffler.service.UserDbClient;
import guru.qa.niffler.service.UserSpringDbClient;
import io.qameta.allure.Description;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class JdbcTest {

    private static final UserDbClient userDbClient = new UserDbClient();
    private static final UserSpringDbClient userSpringDbClient = new UserSpringDbClient();
    private static final UserChainedXaSpringDbClient userChainedXaSpringDbClient = new UserChainedXaSpringDbClient();

    @Test
    @Description("Создание пользователя, JDBC")
    void createUser() {
        UserJson user = userDbClient.createUser(new UserJson(
                null,
                "test_user",
                "",
                "",
                "",
                CurrencyValues.RUB,
                null,
                null
        ));

        Assertions.assertNotEquals(user.id(), null);
    }

    @Test
    void removeUser() {
        userDbClient.deleteUser(new UserJson(
                null,
                "test_user",
                "",
                "",
                "",
                CurrencyValues.RUB,
                null,
                null
        ));
    }

    @Test
    void createUserBySpring() {
        UserJson user = userSpringDbClient.createUser(new UserJson(
                null,
                "test_user_spring",
                "",
                "",
                "",
                CurrencyValues.RUB,
                null,
                null
        ));

        Assertions.assertNotEquals(user.id(), null);
    }

    @Test
    void removeUserBySpring() {
        userSpringDbClient.deleteUser(new UserJson(
                null,
                "test_user_spring",
                "",
                "",
                "",
                CurrencyValues.RUB,
                null,
                null
        ));
    }



}
