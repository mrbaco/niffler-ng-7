package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(BrowserExtension.class)
public class ProfileTest {

    private static final Config CFG = Config.getInstance();

    @Category(
            username = "mrbaco"
    )
    @Test
    void archivedCategoryShouldPresentInCategoriesList(CategoryJson category) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login("mrbaco", "test")
                .clickAvatar()
                .clickProfileLink()
                .categoriesShouldNotHaveLabel(category.name())
                .clickShowArchivedCheckbox()
                .categoriesShouldHaveLabel(category.name());
    }

    @Category(
            username = "mrbaco"
    )
    @Test
    void activeCategoryShouldPresentInCategoriesList(CategoryJson category) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login("mrbaco", "test")
                .clickAvatar()
                .clickProfileLink()
                .categoriesShouldHaveLabel(category.name());
    }

}
