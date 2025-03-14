package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(BrowserExtension.class)
public class LoginTest {

    private static final Config CFG = Config.getInstance();

    @User(
            username = "mrbaco",
            password = "test"
    )
    @Test
    void mainPageShouldBeDisplayedAfterSuccessLogin(UserJson user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.password())
                .checkThatMainPageIsVisible();
    }

    @User(
            username = "mrbaco",
            password = "test2"
    )
    @Test
    void userShouldStayOnLoginPageAfterLoginWithBadCredentials(UserJson user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.password())
                .checkThatMainPageIsNotVisible();
    }

}
