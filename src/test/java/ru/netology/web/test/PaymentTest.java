package ru.netology.web.test;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.web.data.DataHelper;
import ru.netology.web.data.SQLHelper;
import ru.netology.web.page.PaymentPage;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PaymentTest {

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @BeforeEach
    void setup() {
        Selenide.open("http://localhost:8080");
    }

    // Позитивные проверки

    @Test
        // успешная оплата, полное имя владельца
    void shouldSuccessPayFullHolderName() {
        PaymentPage paymentPage = new PaymentPage();
        paymentPage.choosePayOption();
        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber().getCardNumber());
        paymentPage.fillMonth(DataHelper.getValidPeriod().getMonth());
        paymentPage.fillYear(DataHelper.getValidPeriod().getYear());
        paymentPage.fillHolder(DataHelper.getValidHolderFullLatin().getHolder());
        paymentPage.fillCvc(DataHelper.getValidCvc().getCvc());
        paymentPage.continueClick();
        paymentPage.checkSuccessNotification();
        assertEquals("APPROVED", SQLHelper.paymentStatusQuery());
    }

    @Test
        // успешная оплата, сокращенное имя владельца
    void shouldSuccessPayShortenedHolderName() {
        PaymentPage paymentPage = new PaymentPage();
        paymentPage.choosePayOption();
        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber().getCardNumber());
        paymentPage.fillMonth(DataHelper.getValidPeriod().getMonth());
        paymentPage.fillYear(DataHelper.getValidPeriod().getYear());
        paymentPage.fillHolder(DataHelper.getValidHolderShortenedName().getHolder());
        paymentPage.fillCvc(DataHelper.getValidCvc().getCvc());
        paymentPage.continueClick();
        paymentPage.checkSuccessNotification();
        assertEquals("APPROVED", SQLHelper.paymentStatusQuery());
    }

    @Test
        // успешная оплата, двойное имя владельца
    void shouldSuccessPayDoubledHolderName() {
        PaymentPage paymentPage = new PaymentPage();
        paymentPage.choosePayOption();
        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber().getCardNumber());
        paymentPage.fillMonth(DataHelper.getValidPeriod().getMonth());
        paymentPage.fillYear(DataHelper.getValidPeriod().getYear());
        paymentPage.fillHolder(DataHelper.getValidHolderDoubleLatin().getHolder());
        paymentPage.fillCvc(DataHelper.getValidCvc().getCvc());
        paymentPage.continueClick();
        paymentPage.checkSuccessNotification();
        assertEquals("APPROVED", SQLHelper.paymentStatusQuery());
    }

    @Test
        // успешная оплата, крайний валидный срок действия карты
    void shouldSuccessPayExtremePeriod() {
        PaymentPage paymentPage = new PaymentPage();
        paymentPage.choosePayOption();
        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber().getCardNumber());
        paymentPage.fillMonth(DataHelper.getValidPeriodCurrentMonthCurrentYear().getMonth());
        paymentPage.fillYear(DataHelper.getValidPeriodCurrentMonthCurrentYear().getYear());
        paymentPage.fillHolder(DataHelper.getValidHolderFullLatin().getHolder());
        paymentPage.fillCvc(DataHelper.getValidCvc().getCvc());
        paymentPage.continueClick();
        paymentPage.checkSuccessNotification();
        assertEquals("APPROVED", SQLHelper.paymentStatusQuery());
    }

    @Test
        // успешная оплата, срок действия карты более чем до 2031 г
    void shouldSuccessPayLongPeriod() {
        PaymentPage paymentPage = new PaymentPage();
        paymentPage.choosePayOption();
        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber().getCardNumber());
        paymentPage.fillMonth(DataHelper.getValidPeriodLong().getMonth());
        paymentPage.fillYear(DataHelper.getValidPeriodLong().getYear());
        paymentPage.fillHolder(DataHelper.getValidHolderFullLatin().getHolder());
        paymentPage.fillCvc(DataHelper.getValidCvc().getCvc());
        paymentPage.continueClick();
        paymentPage.checkSuccessNotification();
        assertEquals("APPROVED", SQLHelper.paymentStatusQuery());
    }

// Негативные проверки

    @Test
        // Отправки заяки, поле "Номер карты" более 16-ти символов.
    void shouldFailWithLongCardNumber() {
        PaymentPage paymentPage = new PaymentPage();
        paymentPage.choosePayOption();
        paymentPage.fillCardNumber(DataHelper.getInvalidCardNumberLong().getCardNumber());
        paymentPage.fillMonth(DataHelper.getValidPeriod().getMonth());
        paymentPage.fillYear(DataHelper.getValidPeriod().getYear());
        paymentPage.fillHolder(DataHelper.getValidHolderFullLatin().getHolder());
        paymentPage.fillCvc(DataHelper.getValidCvc().getCvc());
        paymentPage.continueClick();
        paymentPage.checkErrorWarningCardNumberField();

    }

    @Test
        // Отправки заяки с валидными данными заблокированной карты DECLINE (5555666677778888)
    void shouldFailWithDeclineCard() {
        PaymentPage paymentPage = new PaymentPage();
        paymentPage.choosePayOption();
        paymentPage.fillCardNumber(DataHelper.getDeclinedCardNumber().getCardNumber());
        paymentPage.fillMonth(DataHelper.getValidPeriod().getMonth());
        paymentPage.fillYear(DataHelper.getValidPeriod().getYear());
        paymentPage.fillHolder(DataHelper.getValidHolderFullLatin().getHolder());
        paymentPage.fillCvc(DataHelper.getValidCvc().getCvc());
        paymentPage.continueClick();
        paymentPage.checkFailureNotification();
        assertEquals("DECLINED", SQLHelper.paymentStatusQuery());
    }

    @Test
        // Отправки заяки, поле "Номер карты" пустое
    void shouldFailWithEmptyCardField() {
        PaymentPage paymentPage = new PaymentPage();
        paymentPage.choosePayOption();
        paymentPage.fillMonth(DataHelper.getValidPeriod().getMonth());
        paymentPage.fillYear(DataHelper.getValidPeriod().getYear());
        paymentPage.fillHolder(DataHelper.getValidHolderFullLatin().getHolder());
        paymentPage.fillCvc(DataHelper.getValidCvc().getCvc());
        paymentPage.continueClick();
        paymentPage.checkErrorWarningCardNumberField();

    }

    @Test
        // Отправки заяки, с нетестовой картой
    void shouldFailWithNonTestCard() {
        PaymentPage paymentPage = new PaymentPage();
        paymentPage.choosePayOption();
        paymentPage.fillCardNumber(DataHelper.getInvalidCardNumber().getCardNumber());
        paymentPage.fillMonth(DataHelper.getValidPeriod().getMonth());
        paymentPage.fillYear(DataHelper.getValidPeriod().getYear());
        paymentPage.fillHolder(DataHelper.getValidHolderFullLatin().getHolder());
        paymentPage.fillCvc(DataHelper.getValidCvc().getCvc());
        paymentPage.continueClick();
        paymentPage.checkFailureNotification();
        paymentPage.checkForAbsenceSuccessNotification(); //??? есть сообщ об успехе и неуспехе - не записывает в базу данных
        assertEquals("DECLINED", SQLHelper.paymentStatusQuery());
    }

    @Test
        // Отправки заяки, поле "Номер карты" менее 16-ти символов
    void shouldFailWithShortCardNumber() {
        PaymentPage paymentPage = new PaymentPage();
        paymentPage.choosePayOption();
        paymentPage.fillCardNumber(DataHelper.getInvalidCardNumberShort().getCardNumber());
        paymentPage.fillMonth(DataHelper.getValidPeriod().getMonth());
        paymentPage.fillYear(DataHelper.getValidPeriod().getYear());
        paymentPage.fillHolder(DataHelper.getValidHolderFullLatin().getHolder());
        paymentPage.fillCvc(DataHelper.getValidCvc().getCvc());
        paymentPage.continueClick();
        paymentPage.checkErrorWarningCardNumberField();


    }

    @Test
        // отправка заявки, имя владельца кириллицей
    void shouldFailHolderNameInCyrillic() {
        PaymentPage paymentPage = new PaymentPage();
        paymentPage.choosePayOption();
        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber().getCardNumber());
        paymentPage.fillMonth(DataHelper.getValidPeriod().getMonth());
        paymentPage.fillYear(DataHelper.getValidPeriod().getYear());
        paymentPage.fillHolder(DataHelper.getInvalidHolderCyrillic().getHolder());
        paymentPage.fillCvc(DataHelper.getValidCvc().getCvc());
        paymentPage.continueClick();
        paymentPage.checkFailureNotification();

    }

    @Test
        // Отправки заяки, имя владельца со спец. символами, за исключением точки (.)
    void shouldFailHolderNameWithSymbols() {
        PaymentPage paymentPage = new PaymentPage();
        paymentPage.choosePayOption();
        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber().getCardNumber());
        paymentPage.fillMonth(DataHelper.getValidPeriod().getMonth());
        paymentPage.fillYear(DataHelper.getValidPeriod().getYear());
        paymentPage.fillHolder(DataHelper.getInvalidHolderSymbols().getHolder());
        paymentPage.fillCvc(DataHelper.getValidCvc().getCvc());
        paymentPage.continueClick();
        paymentPage.checkFailureNotification();

    }

    @Test
        // Отправки заяки, имя владельца пустое
    void shouldFailWithEmptyHolderName() {
        PaymentPage paymentPage = new PaymentPage();
        paymentPage.choosePayOption();
        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber().getCardNumber());
        paymentPage.fillMonth(DataHelper.getValidPeriod().getMonth());
        paymentPage.fillYear(DataHelper.getValidPeriod().getYear());
        paymentPage.fillCvc(DataHelper.getValidCvc().getCvc());
        paymentPage.continueClick();
        paymentPage.checkErrorWarningHolderField();

    }

    @Test
        // Отправки заяки, проверяем невалидные месяца (прошедшие) в текущем году
    void shouldFailWithInvalidPeriod() {
        PaymentPage paymentPage = new PaymentPage();
        paymentPage.choosePayOption();
        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber().getCardNumber());
        paymentPage.fillMonth(DataHelper.getInvalidPeriodCurrentYearButEarlierMonth().getMonth());
        paymentPage.fillYear(DataHelper.getInvalidPeriodCurrentYearButEarlierMonth().getYear());
        paymentPage.fillHolder(DataHelper.getValidHolderFullLatin().getHolder());
        paymentPage.fillCvc(DataHelper.getValidCvc().getCvc());
        paymentPage.continueClick();

    }

    @Test
        // Отправки заяки, поле "Месяц" 00
    void shouldFailWithInvalidMonth00() {
        PaymentPage paymentPage = new PaymentPage();
        paymentPage.choosePayOption();
        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber().getCardNumber());
        paymentPage.fillMonth(DataHelper.getInvalidMonth00().getMonth());
        paymentPage.fillYear(DataHelper.getValidPeriod().getYear());
        paymentPage.fillHolder(DataHelper.getValidHolderFullLatin().getHolder());
        paymentPage.fillCvc(DataHelper.getValidCvc().getCvc());
        paymentPage.continueClick();
        paymentPage.checkErrorWarningMonthField("Неверный формат");

    }

    @Test
        // Отправки заяки, поле "Месяц" 13
    void shouldFailWithInvalidMonth13() {
        PaymentPage paymentPage = new PaymentPage();
        paymentPage.choosePayOption();
        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber().getCardNumber());
        paymentPage.fillMonth(DataHelper.getInvalidMonth13().getMonth());
        paymentPage.fillYear(DataHelper.getValidPeriod().getYear());
        paymentPage.fillHolder(DataHelper.getValidHolderFullLatin().getHolder());
        paymentPage.fillCvc(DataHelper.getValidCvc().getCvc());
        paymentPage.continueClick();
        paymentPage.checkErrorWarningMonthField("Неверно указан срок действия карты");

    }

    @Test
        // Отправки заяки, в поле "Месяц" ввести текст
    void shouldFailWithInvalidMonthText() {
        PaymentPage paymentPage = new PaymentPage();
        paymentPage.choosePayOption();
        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber().getCardNumber());
        paymentPage.fillMonth(DataHelper.getInvalidMonthText().getMonth());
        paymentPage.fillYear(DataHelper.getValidPeriod().getYear());
        paymentPage.fillHolder(DataHelper.getValidHolderFullLatin().getHolder());
        paymentPage.fillCvc(DataHelper.getValidCvc().getCvc());
        paymentPage.continueClick();
        paymentPage.checkErrorWarningMonthField("Неверный формат");

    }

    @Test
        // Отправки заяки,  поле "Месяц" пустое
    void shouldFailWithEmptyMonth() {
        PaymentPage paymentPage = new PaymentPage();
        paymentPage.choosePayOption();
        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber().getCardNumber());
        paymentPage.fillYear(DataHelper.getValidPeriod().getYear());
        paymentPage.fillHolder(DataHelper.getValidHolderFullLatin().getHolder());
        paymentPage.fillCvc(DataHelper.getValidCvc().getCvc());
        paymentPage.continueClick();
        paymentPage.checkErrorWarningMonthField("Неверный формат");

    }

    @Test
        // Отправки заяки, в поле "Месяц" спецсимволы
    void shouldFailWithInvalidMonthSymbols() {
        PaymentPage paymentPage = new PaymentPage();
        paymentPage.choosePayOption();
        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber().getCardNumber());
        paymentPage.fillMonth(DataHelper.getInvalidMonthSymbols().getMonth());
        paymentPage.fillYear(DataHelper.getValidPeriod().getYear());
        paymentPage.fillHolder(DataHelper.getValidHolderFullLatin().getHolder());
        paymentPage.fillCvc(DataHelper.getValidCvc().getCvc());
        paymentPage.continueClick();
        paymentPage.checkErrorWarningMonthField("Неверный формат");

    }

    @Test
        // Отправки заяки, поле "Год" 25
    void shouldFailWithInvalidYear25() {
        PaymentPage paymentPage = new PaymentPage();
        paymentPage.choosePayOption();
        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber().getCardNumber());
        paymentPage.fillMonth(DataHelper.getValidPeriod().getMonth());
        paymentPage.fillYear(DataHelper.getInvalidYear25().getYear());
        paymentPage.fillHolder(DataHelper.getValidHolderFullLatin().getHolder());
        paymentPage.fillCvc(DataHelper.getValidCvc().getCvc());
        paymentPage.continueClick();
        paymentPage.checkErrorWarningYearFieldWrongFormat("Истёк срок действия карты");

    }

    @Test
        // Отправки заяки, поле "Год" пустое
    void shouldFailWithEmptyYear() {
        PaymentPage paymentPage = new PaymentPage();
        paymentPage.choosePayOption();
        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber().getCardNumber());
        paymentPage.fillMonth(DataHelper.getValidPeriod().getMonth());
        paymentPage.fillHolder(DataHelper.getValidHolderFullLatin().getHolder());
        paymentPage.fillCvc(DataHelper.getValidCvc().getCvc());
        paymentPage.continueClick();
        paymentPage.checkErrorWarningYearFieldWrongFormat("Неверный формат");

    }

    @Test
        // Отправки заяки, в поле "Год" ввести текст
    void shouldFailYearWithText() {
        PaymentPage paymentPage = new PaymentPage();
        paymentPage.choosePayOption();
        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber().getCardNumber());
        paymentPage.fillMonth(DataHelper.getValidPeriod().getMonth());
        paymentPage.fillYear(DataHelper.getInvalidYearText().getYear());
        paymentPage.fillHolder(DataHelper.getValidHolderFullLatin().getHolder());
        paymentPage.fillCvc(DataHelper.getValidCvc().getCvc());
        paymentPage.continueClick();
        paymentPage.checkErrorWarningYearFieldWrongFormat("Неверный формат");

    }

    @Test
        // Отправки заяки, в поле "Год" ввести спецсимволы
    void shouldFailYearWithSymbols() {
        PaymentPage paymentPage = new PaymentPage();
        paymentPage.choosePayOption();
        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber().getCardNumber());
        paymentPage.fillMonth(DataHelper.getValidPeriod().getMonth());
        paymentPage.fillYear(DataHelper.getInvalidYearSymbols().getYear());
        paymentPage.fillHolder(DataHelper.getValidHolderFullLatin().getHolder());
        paymentPage.fillCvc(DataHelper.getValidCvc().getCvc());
        paymentPage.continueClick();
        paymentPage.checkErrorWarningYearFieldWrongFormat("Неверный формат");

    }

    @Test
        // Отправки заяки, поле "CVC/CVV" пустое
    void shouldFailEmptyCvc() {
        PaymentPage paymentPage = new PaymentPage();
        paymentPage.choosePayOption();
        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber().getCardNumber());
        paymentPage.fillMonth(DataHelper.getValidPeriod().getMonth());
        paymentPage.fillYear(DataHelper.getValidPeriod().getYear());
        paymentPage.fillHolder(DataHelper.getValidHolderFullLatin().getHolder());
        paymentPage.continueClick();
        paymentPage.checkErrorWarningCvcFieldWrongFormat();

    }

    @Test
        // Отправки заяки, в поле "CVC/CVV" текст латиница
    void shouldFailCvcWithText1() {
        PaymentPage paymentPage = new PaymentPage();
        paymentPage.choosePayOption();
        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber().getCardNumber());
        paymentPage.fillMonth(DataHelper.getValidPeriod().getMonth());
        paymentPage.fillYear(DataHelper.getValidPeriod().getYear());
        paymentPage.fillHolder(DataHelper.getValidHolderFullLatin().getHolder());
        paymentPage.fillCvc(DataHelper.getInvalidCvcText1().getCvc());
        paymentPage.continueClick();
        paymentPage.checkErrorWarningCvcFieldWrongFormat();

    }

    @Test
        // Отправки заяки, в поле "CVC/CVV" текст кириллица
    void shouldFailCvcWithText2() {
        PaymentPage paymentPage = new PaymentPage();
        paymentPage.choosePayOption();
        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber().getCardNumber());
        paymentPage.fillMonth(DataHelper.getValidPeriod().getMonth());
        paymentPage.fillYear(DataHelper.getValidPeriod().getYear());
        paymentPage.fillHolder(DataHelper.getValidHolderFullLatin().getHolder());
        paymentPage.fillCvc(DataHelper.getInvalidCvcText2().getCvc());
        paymentPage.continueClick();
        paymentPage.checkErrorWarningCvcFieldWrongFormat();

    }

    @Test
        // Отправки заяки, в поле "CVC/CVV" спецсимволы
    void shouldFailCvcWithSymbols() {
        PaymentPage paymentPage = new PaymentPage();
        paymentPage.choosePayOption();
        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber().getCardNumber());
        paymentPage.fillMonth(DataHelper.getValidPeriod().getMonth());
        paymentPage.fillYear(DataHelper.getValidPeriod().getYear());
        paymentPage.fillHolder(DataHelper.getValidHolderFullLatin().getHolder());
        paymentPage.fillCvc(DataHelper.getInvalidCvcSymbols().getCvc());
        paymentPage.continueClick();
        paymentPage.checkErrorWarningCvcFieldWrongFormat();

    }

    @Test
        // Отправки заяки, в поле "CVC/CVV" менее 3-х цифр
    void shouldFailCvcShort() {
        PaymentPage paymentPage = new PaymentPage();
        paymentPage.choosePayOption();
        paymentPage.fillCardNumber(DataHelper.getApprovedCardNumber().getCardNumber());
        paymentPage.fillMonth(DataHelper.getValidPeriod().getMonth());
        paymentPage.fillYear(DataHelper.getValidPeriod().getYear());
        paymentPage.fillHolder(DataHelper.getValidHolderFullLatin().getHolder());
        paymentPage.fillCvc(DataHelper.getInvalidCvcShort().getCvc());
        paymentPage.continueClick();
        paymentPage.checkErrorWarningCvcFieldWrongFormat();

    }
}



