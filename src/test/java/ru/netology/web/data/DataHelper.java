package ru.netology.web.data;

import com.github.javafaker.Faker;
import lombok.Value;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Random;


public class DataHelper {

    private static Faker faker;


    public static CardNumber getApprovedCardNumber() {
        return new CardNumber("1111222233334444");
    }

    public static CardNumber getDeclinedCardNumber() {
        return new CardNumber("5555666677778888");
    }

    public static CardNumber getInvalidCardNumber() {
        return new CardNumber("1234123412341234");
    }

    public static CardNumber getInvalidCardNumberShort() {
        return new CardNumber("111122223333444");
    }

    public static CardNumber getInvalidCardNumberLong() {
        return new CardNumber("11112222333344445555");
    }

    public static ValidPeriod getValidPeriod() { // валидный период, будущие значения, год следующий за текущим
        LocalDate today = LocalDate.now();
        Random random = new Random();
        int month = random.nextInt(12) + 1;
        int year = today.getYear() + 1;
        LocalDate futureDate = LocalDate.of(year, month, 1);
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MM");
        DateTimeFormatter yearFormatter = DateTimeFormatter.ofPattern("yy");
        String validMonth = futureDate.format(monthFormatter);
        String validYear = futureDate.format(yearFormatter);
        return new ValidPeriod(validMonth, validYear);
    }

    public static ValidPeriod getValidPeriodLong() { // валидный период, будущие даты, год > 31 (т.к. при ручном тестировании эти значения вызывали ошибку)
        LocalDate today = LocalDate.now();
        Random random = new Random();
        int month = random.nextInt(12) + 1;
        int year = today.getYear() + 10; // берем год >31
        LocalDate futureDate = LocalDate.of(year, month, 1);
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MM");
        DateTimeFormatter yearFormatter = DateTimeFormatter.ofPattern("yy");
        String validMonth = futureDate.format(monthFormatter);
        String validYear = futureDate.format(yearFormatter);
        return new ValidPeriod(validMonth, validYear);
    }

    public static ValidPeriod getValidPeriodCurrentMonthCurrentYear() { // текущий месяц и год как крайний валидный период
        LocalDate today = LocalDate.now();
        int month = today.getMonthValue();
        int year = today.getYear();
        LocalDate currentDate = LocalDate.of(year, month, 1);
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MM");
        DateTimeFormatter yearFormatter = DateTimeFormatter.ofPattern("yy");
        String validMonth = currentDate.format(monthFormatter);
        String validYear = currentDate.format(yearFormatter);
        return new ValidPeriod(validMonth, validYear);
    }

    public static InvalidPeriod getInvalidPeriodCurrentYearButEarlierMonth() { // проверяем невалидные месяца (прошедшие) в текущем году
        LocalDate today = LocalDate.now();
        int monthNow = today.getMonthValue();
        int yearNow = today.getYear(); // текущий год
        if (monthNow == 1) {
            return new InvalidPeriod("12", String.valueOf(yearNow - 1));
        }
        Random random = new Random();
        int month = random.nextInt(monthNow - 1) + 1;
        String invalidMonth = String.format("%02d", month);
        String invalidYear = String.valueOf(yearNow).substring(2);
        return new InvalidPeriod(invalidMonth, invalidYear);
    }

    public static Holder getValidHolderFullLatin() {
        return new Holder("Ivan Ivanov");
    }

    public static Holder getValidHolderDoubleLatin() {
        return new Holder("Anna-Maria Pavlova-Demidova");
    }

    public static Holder getValidHolderShortenedName() {
        return new Holder("I. Ivanov");
    }

    public static Holder getInvalidHolderCyrillic() {
        return new Holder("Иван Иванов");
    }

    public static Holder getInvalidHolderSymbols() {
        return new Holder("Ivan Iv@nov");
    }

    public static Cvc getValidCvc() {
        Random random = new Random();
        int number = random.nextInt(1000); // 0–999
        String validCvc = String.format("%03d", number); // "%03d" → 3 знака, заполнение нулями
        return new Cvc(validCvc);
    }

    public static Cvc getInvalidCvcShort() {
        Random random = new Random();
        int number = random.nextInt(100); // 0–99
        return new Cvc(String.valueOf(number));
    }

    public static Cvc getInvalidCvcText1() {
        return new Cvc("cat");
    }

    public static Cvc getInvalidCvcText2() {
        return new Cvc("кот");
    }

    public static Cvc getInvalidCvcSymbols() {
        return new Cvc("@#!");
    }

    public static Month getInvalidMonth00() {
        return new Month("00");
    }

    public static Month getInvalidMonth13() {
        return new Month("13");
    }

    public static Month getInvalidMonthText() {
        return new Month("ян");
    }

    public static Month getInvalidMonthSymbols() {
        return new Month("@#");
    }

    public static Year getInvalidYear25() {
        return new Year("25");
    }

    public static Year getInvalidYearText() {
        return new Year("ян");
    }

    public static Year getInvalidYearSymbols() {
        return new Year("@#");
    }

    @Value
    public static class CardNumber {
        String cardNumber;
    }

    @Value
    public static class ValidPeriod {
        String month;
        String year;
    }

    @Value
    public static class InvalidPeriod {
        String month;
        String year;
    }

    @Value
    public static class Month {
        String month;
    }

    @Value
    public static class Year {
        String year;
    }

    @Value
    public static class Holder {
        String holder;
    }

    @Value
    public static class Cvc {
        String cvc;
    }

}


