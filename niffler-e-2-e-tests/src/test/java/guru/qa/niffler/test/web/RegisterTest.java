package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.jupiter.extension.UsersQueueExtension.StaticUser;
import guru.qa.niffler.jupiter.extension.UsersQueueExtension.UserType;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.jupiter.extension.UsersQueueExtension.UserType.Type.WITH_FRIEND;

@WebTest
public class RegisterTest {

    private static final Config CFG = Config.getInstance();

    @Test
    void shouldRegisterNewUser() {
        String username = RandomDataUtils.randomUsername();

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .clickRegisterBtn()
                .setUsername(username)
                .setPassword("qwerty")
                .setSubmitPassword("qwerty")
                .submitRegistration()
                .checkThatRegistrationIsSuccess()
                .clickSignInBtn()
                .login(username, "qwerty")
                .checkThatMainPageIsVisible();
    }

    @Test
    void shouldNotRegisterUserWithExistingUsername(@UserType(WITH_FRIEND) StaticUser user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .clickRegisterBtn()
                .setUsername(user.username())
                .setPassword(user.password())
                .setSubmitPassword(user.password())
                .submitRegistration()
                .checkThatErrorIsVisible("Username `%s` already exists".formatted(user.username()));
    }

    @Test
    void shouldShowErrorWhenPasswordAndSubmitPasswordAreNotEqual() {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .clickRegisterBtn()
                .setUsername(RandomDataUtils.randomUsername())
                .setPassword("asd")
                .setSubmitPassword("qwe")
                .submitRegistration()
                .checkThatErrorIsVisible("Passwords should be equal");
    }

}
