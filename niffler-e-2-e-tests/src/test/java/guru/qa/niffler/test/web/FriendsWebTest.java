package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.jupiter.extension.UsersQueueExtension.StaticUser;
import guru.qa.niffler.jupiter.extension.UsersQueueExtension.UserType;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.jupiter.extension.UsersQueueExtension.UserType.Type.EMPTY;
import static guru.qa.niffler.jupiter.extension.UsersQueueExtension.UserType.Type.WITH_FRIEND;
import static guru.qa.niffler.jupiter.extension.UsersQueueExtension.UserType.Type.WITH_INCOME_REQUEST;
import static guru.qa.niffler.jupiter.extension.UsersQueueExtension.UserType.Type.WITH_OUTCOME_REQUEST;

@WebTest
public class FriendsWebTest {

    private static final Config CFG = Config.getInstance();

    @Test
    void friendsShouldBePresentInFriendsTable(@UserType(WITH_FRIEND) StaticUser user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.password())
                .clickAvatar()
                .clickFriendsLink()
                .getUserRow(0)
                .usernameShouldBe(user.friend())
                .unfriendBtnShouldBeVisible();
    }

    @Test
    void friendsTableShouldBeEmptyForNewUser(@UserType(EMPTY) StaticUser user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.password())
                .clickAvatar()
                .clickFriendsLink()
                .noUsersLabelShouldBeVisible()
                .clickAllPeopleLink()
                .getUserRow(0)
                .waitingLabelShouldBeHidden();
    }

    @Test
    void incomeInvitationBePresentInFriendsTable(@UserType(WITH_INCOME_REQUEST) StaticUser user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.password())
                .clickAvatar()
                .clickFriendsLink()
                .getUserRow(0)
                .usernameShouldBe(user.income())
                .acceptBtnShouldBeVisible()
                .declineBtnShouldBeVisible();
    }

    @Test
    void outcomeInvitationBePresentInAllPeoplesTable(@UserType(WITH_OUTCOME_REQUEST) StaticUser user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.password())
                .clickAvatar()
                .clickFriendsLink()
                .noUsersLabelShouldBeVisible()
                .clickAllPeopleLink()
                .getUserRow(0)
                .usernameShouldBe(user.outcome())
                .waitingLabelShouldBeVisible();
    }

}
