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
import ru.netology.web.page.CreditPage;
import ru.netology.web.page.PaymentPage;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CreditTest {

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
        CreditPage creditPage = new CreditPage();
        creditPage.chooseCreditOption();
        creditPage.fillCardNumber(DataHelper.getApprovedCardNumber().getCardNumber());
        creditPage.fillMonth(DataHelper.getValidPeriod().getMonth());
        creditPage.fillYear(DataHelper.getValidPeriod().getYear());
        creditPage.fillHolder(DataHelper.getValidHolderFullLatin().getHolder());
        creditPage.fillCvc(DataHelper.getValidCvc().getCvc());
        creditPage.continueClick();
        creditPage.checkSuccessNotification();
        assertEquals("APPROVED", SQLHelper.paymentStatusQuery());
    }

    @Test
        // успешная оплата, сокращенное имя владельца
    void shouldSuccessPayShortenedHolderName() {
        CreditPage creditPage = new CreditPage();
        creditPage.chooseCreditOption();
        creditPage.fillCardNumber(DataHelper.getApprovedCardNumber().getCardNumber());
        creditPage.fillMonth(DataHelper.getValidPeriod().getMonth());
        creditPage.fillYear(DataHelper.getValidPeriod().getYear());
        creditPage.fillHolder(DataHelper.getValidHolderShortenedName().getHolder());
        creditPage.fillCvc(DataHelper.getValidCvc().getCvc());
        creditPage.continueClick();
        creditPage.checkSuccessNotification();
        assertEquals("APPROVED", SQLHelper.paymentStatusQuery());
    }

    @Test
        // успешная оплата, двойное имя владельца
    void shouldSuccessPayDoubledHolderName() {
        CreditPage creditPage = new CreditPage();
        creditPage.chooseCreditOption();
        creditPage.fillCardNumber(DataHelper.getApprovedCardNumber().getCardNumber());
        creditPage.fillMonth(DataHelper.getValidPeriod().getMonth());
        creditPage.fillYear(DataHelper.getValidPeriod().getYear());
        creditPage.fillHolder(DataHelper.getValidHolderDoubleLatin().getHolder());
        creditPage.fillCvc(DataHelper.getValidCvc().getCvc());
        creditPage.continueClick();
        creditPage.checkSuccessNotification();
        assertEquals("APPROVED", SQLHelper.paymentStatusQuery());
    }

    @Test
        // успешная оплата, крайний валидный срок действия карты
    void shouldSuccessPayExtremePeriod() {
        CreditPage creditPage = new CreditPage();
        creditPage.chooseCreditOption();
        creditPage.fillCardNumber(DataHelper.getApprovedCardNumber().getCardNumber());
        creditPage.fillMonth(DataHelper.getValidPeriodCurrentMonthCurrentYear().getMonth());
        creditPage.fillYear(DataHelper.getValidPeriodCurrentMonthCurrentYear().getYear());
        creditPage.fillHolder(DataHelper.getValidHolderFullLatin().getHolder());
        creditPage.fillCvc(DataHelper.getValidCvc().getCvc());
        creditPage.continueClick();
        creditPage.checkSuccessNotification();
        assertEquals("APPROVED", SQLHelper.paymentStatusQuery());
    }

    @Test
        // успешная оплата, срок действия карты более чем до 2031 г
    void shouldSuccessPayLongPeriod() {
        CreditPage creditPage = new CreditPage();
        creditPage.chooseCreditOption();
        creditPage.fillCardNumber(DataHelper.getApprovedCardNumber().getCardNumber());
        creditPage.fillMonth(DataHelper.getValidPeriodLong().getMonth());
        creditPage.fillYear(DataHelper.getValidPeriodLong().getYear());
        creditPage.fillHolder(DataHelper.getValidHolderFullLatin().getHolder());
        creditPage.fillCvc(DataHelper.getValidCvc().getCvc());
        creditPage.continueClick();
        creditPage.checkSuccessNotification();
        assertEquals("APPROVED", SQLHelper.paymentStatusQuery());
    }

    @Test
        // Отправки заяки, поле "Номер карты" более 16-ти символов.
        // Операция должна быть успешной, т.к. поле принимает первые 16 цифр, в данном случае в итоге получится валидный номер
    void shouldNotFailWithLongCardNumber() {
        CreditPage creditPage = new CreditPage();
        creditPage.chooseCreditOption();
        creditPage.fillCardNumber(DataHelper.getInvalidCardNumberLong().getCardNumber());
        creditPage.fillMonth(DataHelper.getValidPeriod().getMonth());
        creditPage.fillYear(DataHelper.getValidPeriod().getYear());
        creditPage.fillHolder(DataHelper.getValidHolderFullLatin().getHolder());
        creditPage.fillCvc(DataHelper.getValidCvc().getCvc());
        creditPage.continueClick();
        creditPage.checkSuccessNotification();
        assertEquals("APPROVED", SQLHelper.paymentStatusQuery());
    }

// Негативные проверки

    @Test
        // Отправки заяки с валидными данными заблокированной карты DECLINE (5555666677778888)
    void shouldFailWithDeclineCard() {
        CreditPage creditPage = new CreditPage();
        creditPage.chooseCreditOption();
        creditPage.fillCardNumber(DataHelper.getDeclinedCardNumber().getCardNumber());
        creditPage.fillMonth(DataHelper.getValidPeriod().getMonth());
        creditPage.fillYear(DataHelper.getValidPeriod().getYear());
        creditPage.fillHolder(DataHelper.getValidHolderFullLatin().getHolder());
        creditPage.fillCvc(DataHelper.getValidCvc().getCvc());
        creditPage.continueClick();
        creditPage.checkFailureNotification();
        assertEquals("DECLINED", SQLHelper.paymentStatusQuery());
    }

    @Test
        // Отправки заяки, поле "Номер карты" пустое
    void shouldFailWithEmptyCardField() {
        CreditPage creditPage = new CreditPage();
        creditPage.chooseCreditOption();
        creditPage.fillMonth(DataHelper.getValidPeriod().getMonth());
        creditPage.fillYear(DataHelper.getValidPeriod().getYear());
        creditPage.fillHolder(DataHelper.getValidHolderFullLatin().getHolder());
        creditPage.fillCvc(DataHelper.getValidCvc().getCvc());
        creditPage.continueClick();
        creditPage.checkErrorWarningCardNumberField();
        creditPage.checkForAbsenceSuccessNotification();
    }

    @Test
        // Отправки заяки, с нетестовой картой
    void shouldFailWithNonTestCard() {
        CreditPage creditPage = new CreditPage();
        creditPage.chooseCreditOption();
        creditPage.fillCardNumber(DataHelper.getInvalidCardNumber().getCardNumber());
        creditPage.fillMonth(DataHelper.getValidPeriod().getMonth());
        creditPage.fillYear(DataHelper.getValidPeriod().getYear());
        creditPage.fillHolder(DataHelper.getValidHolderFullLatin().getHolder());
        creditPage.fillCvc(DataHelper.getValidCvc().getCvc());
        creditPage.continueClick();
        creditPage.checkFailureNotification();
        creditPage.checkForAbsenceSuccessNotification(); //??? есть сообщ о успехе и неуспехе
        assertEquals("DECLINED", SQLHelper.paymentStatusQuery());
    }

    @Test
        // Отправки заяки, поле "Номер карты" менее 16-ти символов
    void shouldFailWithShortCardNumber() {
        CreditPage creditPage = new CreditPage();
        creditPage.chooseCreditOption();
        creditPage.fillCardNumber(DataHelper.getInvalidCardNumberShort().getCardNumber());
        creditPage.fillMonth(DataHelper.getValidPeriod().getMonth());
        creditPage.fillYear(DataHelper.getValidPeriod().getYear());
        creditPage.fillHolder(DataHelper.getValidHolderFullLatin().getHolder());
        creditPage.fillCvc(DataHelper.getValidCvc().getCvc());
        creditPage.continueClick();
        creditPage.checkErrorWarningCardNumberField();
        creditPage.checkForAbsenceSuccessNotification();

    }

    @Test
        // отправка заявки, имя владельца кириллицей
    void shouldFailHolderNameInCyrillic() {
        CreditPage creditPage = new CreditPage();
        creditPage.chooseCreditOption();
        creditPage.fillCardNumber(DataHelper.getApprovedCardNumber().getCardNumber());
        creditPage.fillMonth(DataHelper.getValidPeriod().getMonth());
        creditPage.fillYear(DataHelper.getValidPeriod().getYear());
        creditPage.fillHolder(DataHelper.getInvalidHolderCyrillic().getHolder());
        creditPage.fillCvc(DataHelper.getValidCvc().getCvc());
        creditPage.continueClick();
        creditPage.checkFailureNotification();
        creditPage.checkForAbsenceSuccessNotification();
    }

    @Test
        // Отправки заяки, имя владельца со спец. символами, за исключением точки (.)
    void shouldFailHolderNameWithSymbols() {
        CreditPage creditPage = new CreditPage();
        creditPage.chooseCreditOption();
        creditPage.fillCardNumber(DataHelper.getApprovedCardNumber().getCardNumber());
        creditPage.fillMonth(DataHelper.getValidPeriod().getMonth());
        creditPage.fillYear(DataHelper.getValidPeriod().getYear());
        creditPage.fillHolder(DataHelper.getInvalidHolderSymbols().getHolder());
        creditPage.fillCvc(DataHelper.getValidCvc().getCvc());
        creditPage.continueClick();
        creditPage.checkFailureNotification();
        creditPage.checkForAbsenceSuccessNotification();
    }

    @Test
        // Отправки заяки, имя владельца пустое
    void shouldFailWithEmptyHolderName() {
        CreditPage creditPage = new CreditPage();
        creditPage.chooseCreditOption();
        creditPage.fillCardNumber(DataHelper.getApprovedCardNumber().getCardNumber());
        creditPage.fillMonth(DataHelper.getValidPeriod().getMonth());
        creditPage.fillYear(DataHelper.getValidPeriod().getYear());
        creditPage.fillCvc(DataHelper.getValidCvc().getCvc());
        creditPage.continueClick();
        creditPage.checkErrorWarningHolderField();
        creditPage.checkForAbsenceSuccessNotification();
    }

    @Test
        // Отправки заяки, проверяем невалидные месяца (прошедшие) в текущем году
    void shouldFailWithInvalidPeriod() {
        CreditPage creditPage = new CreditPage();
        creditPage.chooseCreditOption();
        creditPage.fillCardNumber(DataHelper.getApprovedCardNumber().getCardNumber());
        creditPage.fillMonth(DataHelper.getInvalidPeriodCurrentYearButEarlierMonth().getMonth());
        creditPage.fillYear(DataHelper.getInvalidPeriodCurrentYearButEarlierMonth().getYear());
        creditPage.fillHolder(DataHelper.getValidHolderFullLatin().getHolder());
        creditPage.fillCvc(DataHelper.getValidCvc().getCvc());
        creditPage.continueClick();
        creditPage.checkForAbsenceSuccessNotification();
    }


    @Test
        // Отправки заяки, поле "Месяц" 00
    void shouldFailWithInvalidMonth00() {
        CreditPage creditPage = new CreditPage();
        creditPage.chooseCreditOption();
        creditPage.fillCardNumber(DataHelper.getApprovedCardNumber().getCardNumber());
        creditPage.fillMonth(DataHelper.getInvalidMonth00().getMonth());
        creditPage.fillYear(DataHelper.getValidPeriod().getYear());
        creditPage.fillHolder(DataHelper.getValidHolderFullLatin().getHolder());
        creditPage.fillCvc(DataHelper.getValidCvc().getCvc());
        creditPage.continueClick();
        creditPage.checkErrorWarningMonthFieldWrongFormat();
        creditPage.checkForAbsenceSuccessNotification();
    }

    @Test
        // Отправки заяки, поле "Месяц" 13
    void shouldFailWithInvalidMonth13() {
        CreditPage creditPage = new CreditPage();
        creditPage.chooseCreditOption();
        creditPage.fillCardNumber(DataHelper.getApprovedCardNumber().getCardNumber());
        creditPage.fillMonth(DataHelper.getInvalidMonth13().getMonth());
        creditPage.fillYear(DataHelper.getValidPeriod().getYear());
        creditPage.fillHolder(DataHelper.getValidHolderFullLatin().getHolder());
        creditPage.fillCvc(DataHelper.getValidCvc().getCvc());
        creditPage.continueClick();
        creditPage.checkErrorWarningMonthField();
        creditPage.checkForAbsenceSuccessNotification();
    }

    @Test
        // Отправки заяки, в поле "Месяц" ввести текст
    void shouldFailWithInvalidMonthText() {
        CreditPage creditPage = new CreditPage();
        creditPage.chooseCreditOption();
        creditPage.fillCardNumber(DataHelper.getApprovedCardNumber().getCardNumber());
        creditPage.fillMonth(DataHelper.getInvalidMonthText().getMonth());
        creditPage.fillYear(DataHelper.getValidPeriod().getYear());
        creditPage.fillHolder(DataHelper.getValidHolderFullLatin().getHolder());
        creditPage.fillCvc(DataHelper.getValidCvc().getCvc());
        creditPage.continueClick();
        creditPage.checkErrorWarningMonthFieldWrongFormat();
        creditPage.checkForAbsenceSuccessNotification();
    }

    @Test
        // Отправки заяки,  поле "Месяц" пустое
    void shouldFailWithEmptyMonth() {
        CreditPage creditPage = new CreditPage();
        creditPage.chooseCreditOption();
        creditPage.fillCardNumber(DataHelper.getApprovedCardNumber().getCardNumber());
        creditPage.fillYear(DataHelper.getValidPeriod().getYear());
        creditPage.fillHolder(DataHelper.getValidHolderFullLatin().getHolder());
        creditPage.fillCvc(DataHelper.getValidCvc().getCvc());
        creditPage.continueClick();
        creditPage.checkErrorWarningMonthFieldWrongFormat();
        creditPage.checkForAbsenceSuccessNotification();
    }

    @Test
        // Отправки заяки, в поле "Месяц" спецсимволы
    void shouldFailWithInvalidMonthSymbols() {
        CreditPage creditPage = new CreditPage();
        creditPage.chooseCreditOption();
        creditPage.fillCardNumber(DataHelper.getApprovedCardNumber().getCardNumber());
        creditPage.fillMonth(DataHelper.getInvalidMonthSymbols().getMonth());
        creditPage.fillYear(DataHelper.getValidPeriod().getYear());
        creditPage.fillHolder(DataHelper.getValidHolderFullLatin().getHolder());
        creditPage.fillCvc(DataHelper.getValidCvc().getCvc());
        creditPage.continueClick();
        creditPage.checkErrorWarningMonthFieldWrongFormat();
        creditPage.checkForAbsenceSuccessNotification();
    }

    @Test
        // Отправки заяки, поле "Год" 25
    void shouldFailWithInvalidYear25() {
        CreditPage creditPage = new CreditPage();
        creditPage.chooseCreditOption();
        creditPage.fillCardNumber(DataHelper.getApprovedCardNumber().getCardNumber());
        creditPage.fillMonth(DataHelper.getValidPeriod().getMonth());
        creditPage.fillYear(DataHelper.getInvalidYear25().getYear());
        creditPage.fillHolder(DataHelper.getValidHolderFullLatin().getHolder());
        creditPage.fillCvc(DataHelper.getValidCvc().getCvc());
        creditPage.continueClick();
        creditPage.checkErrorWarningYearFieldWrongPeriod();
        creditPage.checkForAbsenceSuccessNotification();
    }

    @Test
        // Отправки заяки, поле "Год" пустое
    void shouldFailWithEmptyYear() {
        CreditPage creditPage = new CreditPage();
        creditPage.chooseCreditOption();
        creditPage.fillCardNumber(DataHelper.getApprovedCardNumber().getCardNumber());
        creditPage.fillMonth(DataHelper.getValidPeriod().getMonth());
        creditPage.fillHolder(DataHelper.getValidHolderFullLatin().getHolder());
        creditPage.fillCvc(DataHelper.getValidCvc().getCvc());
        creditPage.continueClick();
        creditPage.checkErrorWarningYearFieldWrongFormat();
        creditPage.checkForAbsenceSuccessNotification();
    }

    @Test
        // Отправки заяки, в поле "Год" ввести текст
    void shouldFailYearWithText() {
        CreditPage creditPage = new CreditPage();
        creditPage.chooseCreditOption();
        creditPage.fillCardNumber(DataHelper.getApprovedCardNumber().getCardNumber());
        creditPage.fillMonth(DataHelper.getValidPeriod().getMonth());
        creditPage.fillYear(DataHelper.getInvalidYearText().getYear());
        creditPage.fillHolder(DataHelper.getValidHolderFullLatin().getHolder());
        creditPage.fillCvc(DataHelper.getValidCvc().getCvc());
        creditPage.continueClick();
        creditPage.checkErrorWarningYearFieldWrongFormat();
        creditPage.checkForAbsenceSuccessNotification();
    }

    @Test
        // Отправки заяки, в поле "Год" ввести спецсимволы
    void shouldFailYearWithSymbols() {
        CreditPage creditPage = new CreditPage();
        creditPage.chooseCreditOption();
        creditPage.fillCardNumber(DataHelper.getApprovedCardNumber().getCardNumber());
        creditPage.fillMonth(DataHelper.getValidPeriod().getMonth());
        creditPage.fillYear(DataHelper.getInvalidYearSymbols().getYear());
        creditPage.fillHolder(DataHelper.getValidHolderFullLatin().getHolder());
        creditPage.fillCvc(DataHelper.getValidCvc().getCvc());
        creditPage.continueClick();
        creditPage.checkErrorWarningYearFieldWrongFormat();
        creditPage.checkForAbsenceSuccessNotification();
    }

    @Test
        // Отправки заяки, поле "CVC/CVV" пустое
    void shouldFailEmptyCvc() {
        CreditPage creditPage = new CreditPage();
        creditPage.chooseCreditOption();
        creditPage.fillCardNumber(DataHelper.getApprovedCardNumber().getCardNumber());
        creditPage.fillMonth(DataHelper.getValidPeriod().getMonth());
        creditPage.fillYear(DataHelper.getValidPeriod().getYear());
        creditPage.fillHolder(DataHelper.getValidHolderFullLatin().getHolder());
        creditPage.continueClick();
        creditPage.checkErrorWarningCvcFieldWrongFormat();
        creditPage.checkForAbsenceSuccessNotification();
    }

    @Test
        // Отправки заяки, в поле "CVC/CVV" текст латиница
    void shouldFailCvcWithText1() {
        CreditPage creditPage = new CreditPage();
        creditPage.chooseCreditOption();
        creditPage.fillCardNumber(DataHelper.getApprovedCardNumber().getCardNumber());
        creditPage.fillMonth(DataHelper.getValidPeriod().getMonth());
        creditPage.fillYear(DataHelper.getValidPeriod().getYear());
        creditPage.fillHolder(DataHelper.getValidHolderFullLatin().getHolder());
        creditPage.fillCvc(DataHelper.getInvalidCvcText1().getCvc());
        creditPage.continueClick();
        creditPage.checkErrorWarningCvcFieldWrongFormat();
        creditPage.checkForAbsenceSuccessNotification();
    }

    @Test
        // Отправки заяки, в поле "CVC/CVV" текст кириллица
    void shouldFailCvcWithText2() {
        CreditPage creditPage = new CreditPage();
        creditPage.chooseCreditOption();
        creditPage.fillCardNumber(DataHelper.getApprovedCardNumber().getCardNumber());
        creditPage.fillMonth(DataHelper.getValidPeriod().getMonth());
        creditPage.fillYear(DataHelper.getValidPeriod().getYear());
        creditPage.fillHolder(DataHelper.getValidHolderFullLatin().getHolder());
        creditPage.fillCvc(DataHelper.getInvalidCvcText2().getCvc());
        creditPage.continueClick();
        creditPage.checkErrorWarningCvcFieldWrongFormat();
        creditPage.checkForAbsenceSuccessNotification();
    }

    @Test
        // Отправки заяки, в поле "CVC/CVV" спецсимволы
    void shouldFailCvcWithSymbols() {
        CreditPage creditPage = new CreditPage();
        creditPage.chooseCreditOption();
        creditPage.fillCardNumber(DataHelper.getApprovedCardNumber().getCardNumber());
        creditPage.fillMonth(DataHelper.getValidPeriod().getMonth());
        creditPage.fillYear(DataHelper.getValidPeriod().getYear());
        creditPage.fillHolder(DataHelper.getValidHolderFullLatin().getHolder());
        creditPage.fillCvc(DataHelper.getInvalidCvcSymbols().getCvc());
        creditPage.continueClick();
        creditPage.checkErrorWarningCvcFieldWrongFormat();
        creditPage.checkForAbsenceSuccessNotification();
    }

    @Test
        // Отправки заяки, в поле "CVC/CVV" менее 3-х цифр
    void shouldFailCvcShort() {
        CreditPage creditPage = new CreditPage();
        creditPage.chooseCreditOption();
        creditPage.fillCardNumber(DataHelper.getApprovedCardNumber().getCardNumber());
        creditPage.fillMonth(DataHelper.getValidPeriod().getMonth());
        creditPage.fillYear(DataHelper.getValidPeriod().getYear());
        creditPage.fillHolder(DataHelper.getValidHolderFullLatin().getHolder());
        creditPage.fillCvc(DataHelper.getInvalidCvcShort().getCvc());
        creditPage.continueClick();
        creditPage.checkErrorWarningCvcFieldWrongFormat();
        creditPage.checkForAbsenceSuccessNotification();
    }
}
