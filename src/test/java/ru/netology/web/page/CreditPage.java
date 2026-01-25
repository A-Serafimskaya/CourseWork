package ru.netology.web.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import java.time.Duration;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

public class CreditPage {

    private final SelenideElement creditPayButton = $(byText("Купить в кредит"));

    private final SelenideElement cardNumberField = $(".form-field:nth-child(1) input");
    private final SelenideElement monthFieldField = $(".form-field:nth-child(2) .input-group__input-case:nth-child(1) input");
    private final SelenideElement yearField = $(".form-field:nth-child(2) .input-group__input-case:nth-child(2) input");
    private final SelenideElement holderField = $(".form-field:nth-child(3) .input-group__input-case:nth-child(1) input");
    private final SelenideElement cvcField = $(".form-field:nth-child(3) .input-group__input-case:nth-child(2) input");

    private final SelenideElement continueButton = $(".form-field:nth-child(4) button");

    private final SelenideElement successMessage = $(".notification_status_ok .notification__content");
    private final SelenideElement failureMessage = $(".notification_status_error .notification__content");

    private final SelenideElement invalidFormatCardField = $(".form-field:nth-child(1) .input__sub");
    private final SelenideElement invalidFormatMonthField = $(".form-field:nth-child(2) .input-group__input-case:nth-child(1) .input__sub");
    private final SelenideElement invalidFormatHolderField = $(".form-field:nth-child(3) .input-group__input-case:nth-child(1) .input__sub");
    private final SelenideElement invalidFormatYearField = $(".form-field:nth-child(2) .input-group__input-case:nth-child(2) .input__sub");
    private final SelenideElement invalidFormatCvcField = $(".form-field:nth-child(3) .input-group__input-case:nth-child(2) .input__sub");

    public void chooseCreditOption() {
        creditPayButton.click();
    }

    public void fillCardNumber(String cardNumber) {
        cardNumberField.setValue(cardNumber);
    }

    public void fillMonth(String month) {
        monthFieldField.setValue(month);
    }

    public void fillYear(String year) {
        yearField.setValue(year);
    }

    public void fillHolder(String holder) {
        holderField.setValue(holder);
    }

    public void fillCvc(String cvc) {
        cvcField.setValue(cvc);
    }

    public void continueClick() {
        continueButton.click();
    }

    public void checkSuccessNotification() {
        successMessage.shouldBe(Condition.visible, Duration.ofSeconds(15));
        successMessage.shouldHave(Condition.text("Операция одобрена банком"));
    }

    public void checkForAbsenceSuccessNotification() { // проверка отсутствия сообщения от банка об успешной операции
        successMessage.shouldNotBe(Condition.visible, Duration.ofSeconds(10));
        successMessage.shouldNotHave(Condition.text("Операция одобрена банком"));
    }

    public void checkFailureNotification() {
        failureMessage.shouldBe(Condition.visible, Duration.ofSeconds(10));
        failureMessage.shouldHave(Condition.text("Ошибка! Банк отказал в проведении операции."));
    }

    public void checkErrorWarningCardNumberField() {
        invalidFormatCardField.shouldBe(Condition.visible);
        invalidFormatCardField.shouldHave(Condition.text("Неверный формат"));
    }

    public void checkErrorWarningHolderField() {
        invalidFormatHolderField.shouldBe(Condition.visible);
        invalidFormatHolderField.shouldHave(Condition.text("Поле обязательно для заполнения"));
    }

    public void checkErrorWarningMonthField(String textError) {
        invalidFormatMonthField.shouldBe(Condition.visible);
        invalidFormatMonthField.shouldHave(Condition.text(textError));
    }

    public void checkErrorWarningYearFieldWrongFormat(String textError) {
        invalidFormatYearField.shouldBe(Condition.visible);
        invalidFormatYearField.shouldHave(Condition.text(textError));
    }

    public void checkErrorWarningCvcFieldWrongFormat() {
        invalidFormatCvcField.shouldBe(Condition.visible);
        invalidFormatCvcField.shouldHave(Condition.text("Неверный формат"));
    }
}


