package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class MainPage {

    private final SelenideElement avatar = $(".MuiAvatar-root");
    private final SelenideElement statBlock = $("#stat");
    private final SelenideElement spendingsBlock = $("#spendings");
    private final ElementsCollection tableRows = $("#spendings tbody").$$("tr");

    public static class DropdownMenu {

        private final SelenideElement profileLink = $("[href='/profile']");

        public ProfilePage clickProfileLink() {
            profileLink.click();
            return new ProfilePage();
        }

    }

    public void checkThatMainPageIsVisible() {
        statBlock.shouldBe(visible);
        spendingsBlock.shouldBe(visible);
    }

    public DropdownMenu clickAvatar() {
        avatar.click();
        return new DropdownMenu();
    }

    public void checkThatMainPageIsNotVisible() {
        statBlock.shouldNotBe(visible);
        spendingsBlock.shouldNotBe(visible);
    }

    public EditSpendingPage editSpending(String spendingDescription) {
        tableRows.find(text(spendingDescription)).$$("td").get(5).click();
        return new EditSpendingPage();
    }

    public void checkThatTableContainsSpending(String spendingDescription) {
        tableRows.find(text(spendingDescription)).should(visible);
    }

}
