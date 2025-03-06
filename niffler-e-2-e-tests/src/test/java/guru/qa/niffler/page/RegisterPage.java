package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.exactTextCaseSensitive;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class RegisterPage {

    private final SelenideElement usernameInput = $("#username");
    private final SelenideElement passwordInput = $("#password");
    private final SelenideElement submitPasswordInput = $("#passwordSubmit");
    private final SelenideElement formErrorSpan = $(".form__error");
    private final SelenideElement submitBtn = $("[type='submit']");
    private final SelenideElement formSuccessSpan = $(".form__paragraph_success");
    private final SelenideElement signInBtn = $(".form_sign-in");

    public RegisterPage setUsername(String username) {
        usernameInput.setValue(username);
        return this;
    }

    public RegisterPage setPassword(String password) {
        passwordInput.setValue(password);
        return this;
    }

    public RegisterPage setSubmitPassword(String submitPassword) {
        submitPasswordInput.setValue(submitPassword);
        return this;
    }

    public RegisterPage checkThatErrorIsVisible(String... error) {
        formErrorSpan.shouldHave(exactTextCaseSensitive(String.join("<br>", error)));
        return this;
    }

    public RegisterPage submitRegistration() {
        submitBtn.click();
        return this;
    }

    public RegisterPage checkThatRegistrationIsSuccess() {
        formSuccessSpan.shouldBe(visible);
        return this;
    }

    public LoginPage clickSignInBtn() {
        signInBtn.click();
        return new LoginPage();
    }

}
