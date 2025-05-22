package guru.qa.niffler.test;

import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.UserDbClient;
import io.qameta.allure.Description;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.model.CurrencyValues.RUB;

public class JdbcTest {

    private static final UserDbClient userDbClient = new UserDbClient();

    private static final UserJson validUser = new UserJson(
            null,
            "test_user",
            "",
            "",
            "",
            RUB,
            null,
            null
    );
    private static final UserJson userWithError = new UserJson(
            true,
            null,
            "test_user_error",
            "",
            "",
            "",
            RUB,
            null,
            null
    );

    @Test
    @Description("Проверить создание и удаление пользователя JDBC транзакция")
    void createAndDeleteJdbcTx() {
        userDbClient.createUserJdbcTx(validUser);
        Assertions.assertTrue(userDbClient.existUser(validUser.username()));

        userDbClient.deleteUserJdbcTx(validUser);
        Assertions.assertFalse(userDbClient.existUdUser(validUser.username()));
        Assertions.assertFalse(userDbClient.existAuthUser(validUser.username()));
        Assertions.assertFalse(userDbClient.existAuthAuthorityEntities(validUser.username()));
    }

    @Test
    @Description("Проверить успешный откат транзакции при ошибке создания пользователя JDBC транзакция")
    void createJdbcTxFailWithRollback() {
        Assertions.assertThrows(RuntimeException.class, () -> userDbClient.createUserJdbc(userWithError));
        Assertions.assertFalse(userDbClient.existUdUser(validUser.username()));
        Assertions.assertFalse(userDbClient.existAuthUser(validUser.username()));
        Assertions.assertFalse(userDbClient.existAuthAuthorityEntities(validUser.username()));
    }

    @Test
    @Description("Проверить создание и удаление пользователя Spring JDBC транзакция")
    void createAndDeleteSpringJdbcTx() {
        userDbClient.createUserSpringJdbcTx(validUser);
        Assertions.assertTrue(userDbClient.existUser(validUser.username()));

        userDbClient.deleteUserSpringJdbcTx(validUser);
        Assertions.assertFalse(userDbClient.existUdUser(validUser.username()));
        Assertions.assertFalse(userDbClient.existAuthUser(validUser.username()));
        Assertions.assertFalse(userDbClient.existAuthAuthorityEntities(validUser.username()));
    }

    @Test
    @Description("Проверить успешный откат транзакции при ошибке создания пользователя Spring JDBC транзакция")
    void createSpringJdbcTxFailWithRollback() {
        Assertions.assertThrows(RuntimeException.class, () -> userDbClient.createUserSpringJdbcTx(userWithError));
        Assertions.assertFalse(userDbClient.existUdUser(validUser.username()));
        Assertions.assertFalse(userDbClient.existAuthUser(validUser.username()));
        Assertions.assertFalse(userDbClient.existAuthAuthorityEntities(validUser.username()));
    }

    @Test
    @Description("Проверить создание и удаление пользователя JDBC")
    void createAndDeleteJdbc() {
        userDbClient.createUserJdbc(validUser);
        Assertions.assertTrue(userDbClient.existUser(validUser.username()));

        userDbClient.deleteUserJdbc(validUser);
        Assertions.assertFalse(userDbClient.existUdUser(validUser.username()));
        Assertions.assertFalse(userDbClient.existAuthUser(validUser.username()));
        Assertions.assertFalse(userDbClient.existAuthAuthorityEntities(validUser.username()));
    }

    @Test
    @Description("Проверить невозможность отката изменений в базу данных при ошибке создания пользователя JDBC")
    void createJdbcFailWithNoRollback() {
        Assertions.assertThrows(RuntimeException.class, () -> userDbClient.createUserJdbc(userWithError));
        Assertions.assertTrue(userDbClient.existAuthUser(userWithError.username()));
        Assertions.assertFalse(userDbClient.existUdUser(userWithError.username()));
        Assertions.assertFalse(userDbClient.existAuthAuthorityEntities(userWithError.username()));
        userDbClient.deleteUserJdbc(userWithError);
        Assertions.assertFalse(userDbClient.existAuthUser(userWithError.username()));
    }

    @Test
    @Description("Проверить создание и удаление пользователя Spring JDBC")
    void createAndDeleteSpringJdbc() {
        userDbClient.createUserSpringJdbc(validUser);
        Assertions.assertTrue(userDbClient.existUser(validUser.username()));

        userDbClient.deleteUserSpringJdbc(validUser);
        Assertions.assertFalse(userDbClient.existUdUser(validUser.username()));
        Assertions.assertFalse(userDbClient.existAuthUser(validUser.username()));
        Assertions.assertFalse(userDbClient.existAuthAuthorityEntities(validUser.username()));
    }

    @Test
    @Description("Проверить невозможность отката изменений в базу данных при ошибке создания пользователя Spring JDBC")
    void createSpringJdbcFailWithNoRollback() {
        Assertions.assertThrows(RuntimeException.class, () -> userDbClient.createUserSpringJdbc(userWithError));
        Assertions.assertTrue(userDbClient.existAuthUser(userWithError.username()));
        Assertions.assertFalse(userDbClient.existUdUser(userWithError.username()));
        Assertions.assertFalse(userDbClient.existAuthAuthorityEntities(userWithError.username()));
        userDbClient.deleteUserSpringJdbc(userWithError);
        Assertions.assertFalse(userDbClient.existAuthUser(userWithError.username()));
    }

    @Deprecated
    @Test
    @Description("Проверить создание и удаление пользователя Spring JDBC Chained транзакция")
    void createAndDeleteSpringJdbcChainedTx() {
        userDbClient.createUserSpringJdbcChainedTx(validUser);
        Assertions.assertTrue(userDbClient.existUser(validUser.username()));

        userDbClient.deleteUserSpringJdbcChainedTx(validUser);
        Assertions.assertFalse(userDbClient.existUdUser(validUser.username()));
        Assertions.assertFalse(userDbClient.existAuthUser(validUser.username()));
        Assertions.assertFalse(userDbClient.existAuthAuthorityEntities(validUser.username()));
    }

    @Deprecated
    @Test
    @Description("Проверить невозможность отката изменений в базу данных при ошибке создания пользователя Spring JDBC Chained транзакция")
    void createSpringJdbcChainedTxFailWithNoRollback() {
        Assertions.assertThrows(RuntimeException.class, () -> userDbClient.createUserSpringJdbcChainedTx(userWithError));
        Assertions.assertTrue(userDbClient.existAuthUser(userWithError.username()));
        Assertions.assertFalse(userDbClient.existUdUser(userWithError.username()));
        Assertions.assertFalse(userDbClient.existAuthAuthorityEntities(userWithError.username()));
        userDbClient.deleteUserSpringJdbcChainedTx(userWithError);
        Assertions.assertFalse(userDbClient.existAuthUser(userWithError.username()));
    }

}
