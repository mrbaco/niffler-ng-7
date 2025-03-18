package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;

@WebTest
public class ProfileTest {

    private static final Config CFG = Config.getInstance();

    @User(
            username = "mrbaco",
            categories = @Category(
                    archived = true
            )
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

    @User(
            username = "mrbaco",
            categories = @Category
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
