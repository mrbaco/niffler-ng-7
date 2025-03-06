package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.BrowserExtension;
import guru.qa.niffler.jupiter.User;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.page.RegisterPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(BrowserExtension.class)
public class RegisterTest {

    private static final Config CFG = Config.getInstance();

    @User(
            password = "test"
    )
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

    @User(
            password = "test"
    )
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
