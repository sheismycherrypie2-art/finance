package app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class MoreFinanceServiceTest {

    private FinanceService finance;
    private Wallet wallet;

    @BeforeEach
    void setUp() {
        finance = new FinanceService();
        wallet = new Wallet();
        finance.addCategory(wallet, "Еда");
        finance.addCategory(wallet, "Развлечения");
    }

    @Test
    void установитьБюджетНаНеизвестнуюКатегориюОшибка() {
        assertThrows(IllegalArgumentException.class, () -> finance.setBudget(wallet, "НетТакой", 100));
    }

    @Test
    void установитьБюджетСНулёмОшибка() {
        assertThrows(IllegalArgumentException.class, () -> finance.setBudget(wallet, "Еда", 0));
    }

    @Test
    void алертНа80ПроцентовБюджета() {
        finance.setBudget(wallet, "Еда", 100);
        var alerts = finance.addExpense(wallet, "Еда", 80, LocalDate.now(), "");
        assertTrue(alerts.stream().anyMatch(a -> a.contains("80%")));
    }

    @Test
    void алертБалансМеньшеЛибоРавенНулю() {
        finance.addExpense(wallet, "Еда", 10, LocalDate.now(), "");
        var alerts = finance.addExpense(wallet, "Еда", 5, LocalDate.now(), "");
        assertTrue(alerts.stream().anyMatch(a -> a.contains("Баланс")));
    }

    @Test
    void алертРасходыБольшеДоходов() {
        finance.addIncome(wallet, "Еда", 10, LocalDate.now(), "");
        var alerts = finance.addExpense(wallet, "Еда", 20, LocalDate.now(), "");
        assertTrue(alerts.stream().anyMatch(a -> a.contains("Расходы превысили доходы")));
    }

    @Test
    void добавитьКатегориюПустуюОшибка() {
        assertThrows(IllegalArgumentException.class, () -> finance.addCategory(wallet, "   "));
    }

    @Test
    void добавитьДоходСNullДатойОшибка() {
        assertThrows(IllegalArgumentException.class, () -> finance.addIncome(wallet, "Еда", 10, null, ""));
    }
}