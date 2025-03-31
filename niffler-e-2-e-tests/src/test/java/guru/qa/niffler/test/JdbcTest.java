package guru.qa.niffler.test;

import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.UserDbClient;
import org.junit.jupiter.api.Test;

public class JdbcTest {

    private static final UserDbClient userDbClient = new UserDbClient();

    @Test
    void createUserInDatabase() {
        UserJson user = userDbClient.createUser(new UserJson(
                null,
                "i_am_test_user",
                "",
                "",
                "",
                CurrencyValues.RUB,
                null,
                null
        ));

        System.out.println(user);
    }

    @Test
    void removeUserFromDatabase() {
        userDbClient.deleteUser(new UserJson(
                null,
                "i_am_test_user",
                "",
                "",
                "",
                CurrencyValues.RUB,
                null,
                null
        ));
    }

}
