package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;

@WebTest
public class RegisterTest {

    private static final Config CFG = Config.getInstance();

    @User(password = "test")
    @Test
    void shouldRegisterNewUser(UserJson user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .clickRegisterBtn()
                .setUsername(user.username())
                .setPassword(user.password())
                .setSubmitPassword(user.password())
                .submitRegistration()
                .checkThatRegistrationIsSuccess()
                .clickSignInBtn()
                .login(user.username(), user.password())
                .checkThatMainPageIsVisible();
    }

    @User(
            username = "mrbaco",
            password = "test2"
    )
    @Test
    void shouldNotRegisterUserWithExistingUsername(UserJson user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .clickRegisterBtn()
                .setUsername(user.username())
                .setPassword(user.password())
                .setSubmitPassword(user.password())
                .submitRegistration()
                .checkThatErrorIsVisible("Username `%s` already exists".formatted(user.username()));
    }

    @User(password = "test")
    @Test
    void shouldShowErrorWhenPasswordAndSubmitPasswordAreNotEqual(UserJson user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .clickRegisterBtn()
                .setUsername(user.username())
                .setPassword(user.password())
                .setSubmitPassword("qwe")
                .submitRegistration()
                .checkThatErrorIsVisible("Passwords should be equal");
    }

}
