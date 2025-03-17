package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.exactTextCaseSensitive;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.$x;

public class PeoplePage {

    private final SelenideElement friendsLink = $x("//h2[contains(text(),'Friends')]");
    private final SelenideElement allPeopleLink = $x("//h2[contains(text(),'All people')]");
    private final SelenideElement searchInput = $("[aria-label='search']");
    private final SelenideElement noUsersLabel = $x("//p[contains(text(),'There are no users yet')]");

    public static class UserRow {

        private static final ElementsCollection rows = $$("tr.MuiTableRow-hover");
        private final SelenideElement row;
        private final SelenideElement username;
        private final SelenideElement waitingLabel;
        private final SelenideElement addFriendBtn;
        private final SelenideElement unfriendBtn;
        private final SelenideElement acceptBtn;
        private final SelenideElement declineBtn;

        public UserRow(int index) {
            row = rows.get(index);
            username = row.$("td");
            waitingLabel = row.$x(".//span[contains(text(), 'Waiting...')]");
            addFriendBtn = row.$x(".//button[contains(text(), 'Add friend')]");
            unfriendBtn = row.$x(".//button[contains(text(), 'Unfriend')]");
            acceptBtn = row.$x(".//button[contains(text(), 'Accept')]");
            declineBtn = row.$x(".//button[contains(text(), 'Decline')]");
        }

        public UserRow usernameShouldBe(String value) {
            username.shouldHave(exactTextCaseSensitive(value));
            return this;
        }

        public UserRow waitingLabelShouldBeVisible() {
            waitingLabel.shouldBe(visible);
            return this;
        }

        public UserRow waitingLabelShouldBeHidden() {
            waitingLabel.shouldNotBe(visible);
            return this;
        }

        public UserRow addFriendBtnShouldBeVisible() {
            addFriendBtn.shouldBe(visible);
            return this;
        }

        public UserRow unfriendBtnShouldBeVisible() {
            unfriendBtn.shouldBe(visible);
            return this;
        }

        public UserRow acceptBtnShouldBeVisible() {
            acceptBtn.shouldBe(visible);
            return this;
        }

        public UserRow declineBtnShouldBeVisible() {
            declineBtn.shouldBe(visible);
            return this;
        }

    }

    public PeoplePage clickFriendsLink() {
        friendsLink.click();
        return this;
    }

    public PeoplePage clickAllPeopleLink() {
        allPeopleLink.click();
        return this;
    }

    public PeoplePage searchUser(String username) {
        searchInput
                .val(username)
                .pressEnter();
        return this;
    }

    public PeoplePage noUsersLabelShouldBeVisible() {
        noUsersLabel.shouldBe(visible);
        return this;
    }

    public UserRow getUserRow(int index) {
        return new UserRow(index);
    }

}
