package ru.netology.web.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.$;

public class CreditPage {

    private final SelenideElement creditPayButton = $(".button_view_extra.button_size_m.button_theme_alfa-on-white");

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
        successMessage.shouldBe(Condition.visible, Duration.ofSeconds(10));
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
        invalidFormatCardField.shouldBe(Condition.visible, Duration.ofSeconds(10));
        invalidFormatCardField.shouldHave(Condition.text("Неверный формат"));
    }

    public void checkErrorWarningHolderField() {
        invalidFormatHolderField.shouldBe(Condition.visible, Duration.ofSeconds(10));
        invalidFormatHolderField.shouldHave(Condition.text("Поле обязательно для заполнения"));
    }

    public void checkErrorWarningMonthField() {
        invalidFormatMonthField.shouldBe(Condition.visible, Duration.ofSeconds(10));
        invalidFormatMonthField.shouldHave(Condition.text("Неверно указан срок действия карты"));
    }

    public void checkErrorWarningMonthFieldWrongFormat() {
        invalidFormatMonthField.shouldBe(Condition.visible, Duration.ofSeconds(10));
        invalidFormatMonthField.shouldHave(Condition.text("Неверный формат"));
    }

    public void checkErrorWarningYearFieldWrongPeriod() {
        invalidFormatYearField.shouldBe(Condition.visible, Duration.ofSeconds(10));
        invalidFormatYearField.shouldHave(Condition.text("Истёк срок действия карты"));
    }

    public void checkErrorWarningYearFieldWrongFormat() {
        invalidFormatYearField.shouldBe(Condition.visible, Duration.ofSeconds(10));
        invalidFormatYearField.shouldHave(Condition.text("Неверный формат"));
    }

    public void checkErrorWarningCvcFieldWrongFormat() {
        invalidFormatCvcField.shouldBe(Condition.visible, Duration.ofSeconds(10));
        invalidFormatCvcField.shouldHave(Condition.text("Неверный формат"));
    }
}


