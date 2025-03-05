package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.*;

public class ProfilePage {

    private final SelenideElement avatar = $("form .MuiAvatar-root");
    private final SelenideElement imageBtn = $("[for='image__input']");
    private final SelenideElement usernameInput = $("#username");
    private final SelenideElement nameInput = $("#name");
    private final SelenideElement submitBtn = $("[type='submit']");
    private final SelenideElement showArchivedCheckbox = $("[type='checkbox']");
    private final SelenideElement categoryInput = $("#category");
    private final ElementsCollection categoryLabels = $$(".MuiChip-label");
    private final SelenideElement editCategoryBtn = $("[aria-label='Edit category']");
    private final SelenideElement editCategoryErrorSpan = $("span.input__helper-text");
    private final SelenideElement archiveCategoryBtn = $("[aria-label='Archive category']");
    private final SelenideElement successAlert = $(".MuiAlert-standardSuccess");

    public static class ArchiveCategoryConfirmationPopup {

        private final SelenideElement popup = $("[role='dialog']");
        private final SelenideElement closeBtn = popup.$x(".//*[contains(text(),'Close')]");
        private final SelenideElement archiveBtn = popup.$x(".//*[contains(text(),'Archive')]");

    }

}
