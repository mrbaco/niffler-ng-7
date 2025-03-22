package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.collections.AnyMatch;
import com.codeborne.selenide.collections.NoneMatch;

import static com.codeborne.selenide.Selenide.*;

public class ProfilePage {

    private final SelenideElement avatar = $("form .MuiAvatar-root");
    private final SelenideElement imageBtn = $("[for='image__input']");
    private final SelenideElement usernameInput = $("#username");
    private final SelenideElement nameInput = $("#name");
    private final SelenideElement submitBtn = $("[type='submit']");
    private final SelenideElement showArchivedCheckbox = $("[type='checkbox']");
    private final SelenideElement categoryInput = $("#category");
    private final SelenideElement successAlert = $(".MuiAlert-standardSuccess");

    public static class CategoryItem {

        private static final ElementsCollection categoryLabels = $$(".MuiChip-label");
        private final SelenideElement category;
        private final SelenideElement editCategoryBtn;
        private final SelenideElement editCategoryErrorSpan;
        private final SelenideElement archiveCategoryBtn;

        public CategoryItem(int index) {
            category = categoryLabels.get(index);
            editCategoryBtn = category.$("[aria-label='Edit category']");
            editCategoryErrorSpan = category.$("span.input__helper-text");
            archiveCategoryBtn = category.$("[aria-label='Archive category']");
        }

        public static void categoriesShouldHaveLabel(String categoryName) {
            categoryLabels.shouldHave(new AnyMatch(
                    "Category with name `%s` is presented".formatted(categoryName),
                    category -> category.getText().equals(categoryName)
            ));
        }

        public static void categoriesShouldNotHaveLabel(String categoryName) {
            categoryLabels.shouldHave(new NoneMatch(
                    "Category with name `%s` is not presented".formatted(categoryName),
                    category -> category.getText().equals(categoryName)
            ));
        }

        public ArchiveCategoryConfirmationPopup clickArchiveCategoryBtn() {
            archiveCategoryBtn.click();
            return new ArchiveCategoryConfirmationPopup();
        }

    }

    public static class ArchiveCategoryConfirmationPopup {

        private final SelenideElement popup = $("[role='dialog']");
        private final SelenideElement closeBtn = popup.$x(".//*[contains(text(),'Close')]");
        private final SelenideElement archiveBtn = popup.$x(".//*[contains(text(),'Archive')]");

        public ArchiveCategoryConfirmationPopup clickArchiveBtn() {
            archiveBtn.click();
            return this;
        }

    }

    public ProfilePage clickShowArchivedCheckbox() {
        showArchivedCheckbox.click();
        return this;
    }

    public ProfilePage createCategory(String categoryName) {
        categoryInput.setValue(categoryName);
        categoryInput.pressEnter();
        return this;
    }

    public ProfilePage categoriesShouldHaveLabel(String categoryName) {
        CategoryItem.categoriesShouldHaveLabel(categoryName);
        return this;
    }

    public ProfilePage categoriesShouldNotHaveLabel(String categoryName) {
        CategoryItem.categoriesShouldNotHaveLabel(categoryName);
        return this;
    }

    public ProfilePage archiveCategory(int index) {
        new CategoryItem(index)
                .clickArchiveCategoryBtn()
                .clickArchiveBtn();
        return this;
    }

}
