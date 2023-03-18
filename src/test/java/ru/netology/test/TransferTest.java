package ru.netology.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.data.DataHelper;
import ru.netology.page.DashboardPage;
import ru.netology.page.LoginPage;
import ru.netology.page.TransferPage;
import ru.netology.page.VerificationPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransferTest {
    LoginPage loginPage;
    DashboardPage dashboardPage;

    @BeforeEach
    public void setup() {
        loginPage = open("http://localhost:9999", LoginPage.class);
        DataHelper.AuthInfo authInfo = DataHelper.getAuthInfo();
        VerificationPage verificationPage = loginPage.validLogin(authInfo);
        DataHelper.VerificationCode verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        dashboardPage = verificationPage.validVerify(verificationCode);
    }

    @Test
    public void shouldValidTransferFromFirstToSecond () {
        DataHelper.CardInfo cardFirst = DataHelper.getFirstCardInfo();
        DataHelper.CardInfo cardSecond = DataHelper.getSecondCardInfo();
        int balanceFirst = dashboardPage.getCardBalance(cardFirst);
        int balanceSecond = dashboardPage.getCardBalance(cardSecond);
        int amount = DataHelper.getValidAmount(balanceFirst);
        int expectedBalanceFirst = balanceFirst - amount;
        int expectedBalanceSecond = balanceSecond + amount;
        TransferPage transferPage = dashboardPage.selectCardForTransfer(cardSecond);
        dashboardPage = transferPage.makeValidTransfer(String.valueOf(amount), cardFirst);
        int actualBalanceFirst = dashboardPage.getCardBalance(cardFirst);
        int actualBalanceSecond = dashboardPage.getCardBalance(cardSecond);
        assertEquals (expectedBalanceFirst, actualBalanceFirst);
        assertEquals (expectedBalanceSecond, actualBalanceSecond);
    }


    @Test
    public void shouldInvalidTransferFromFirstToNonexistent () {
        DataHelper.CardInfo cardFirst = DataHelper.getFirstCardInfo();
        DataHelper.CardInfo cardSecond = DataHelper.getSecondCardInfo();
        int balanceFirst = dashboardPage.getCardBalance(cardFirst);
        int balanceSecond = dashboardPage.getCardBalance(cardSecond);
        int amount = DataHelper.getValidAmount(balanceFirst);
        TransferPage transferPage = dashboardPage.selectCardForTransfer(cardSecond);
        transferPage.makeTransfer(String.valueOf(amount), DataHelper.getNonexistentCardInfo());
        transferPage.findErrorMessage("Выберите карту списания из Ваших существующих");
        int actualBalanceFirst = dashboardPage.getCardBalance(cardFirst);
        int actualBalanceSecond = dashboardPage.getCardBalance(cardSecond);
        assertEquals (balanceFirst, actualBalanceFirst);
        assertEquals (balanceSecond, actualBalanceSecond);
    }
}
