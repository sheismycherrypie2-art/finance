package app;

import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class FinanceServiceTest {
    private FinanceService finance;
    private Wallet wallet;

    @BeforeEach
    void setUp() {
        finance = new FinanceService();
        wallet = new Wallet();
        finance.addCategory(wallet, "Еда");
        finance.addCategory(wallet, "Такси");
        wallet.getBudgets().put("Еда", 100.0);
    }

    @Test
    void добавитьРасходОк() {
        var alerts = finance.addExpense(wallet, "Еда", 10, LocalDate.now(), "");
        assertEquals(1, wallet.getTransactions().size());
        assertNotNull(alerts);
    }

    @Test
    void расходСНеизвестнойКатегориейОшибка() {
        assertThrows(IllegalArgumentException.class, () -> finance.addExpense(wallet, "Х", 10, LocalDate.now(), ""));
    }

    @Test
    void суммаНеПоложительнаяОшибка() {
        assertThrows(IllegalArgumentException.class, () -> finance.addIncome(wallet, "Еда", 0, LocalDate.now(), ""));
    }

    @Test
    void подсчётИтоговРаботает() {
        finance.addIncome(wallet, "Еда", 100, LocalDate.now(), "");
        finance.addExpense(wallet, "Такси", 30, LocalDate.now(), "");
        assertEquals(100, finance.getTotalIncome(wallet), 0.0001);
        assertEquals(30, finance.getTotalExpense(wallet), 0.0001);
    }

    @Test
    void алертПревышениеБюджета() {
        var alerts = finance.addExpense(wallet, "Еда", 150, LocalDate.now(), "");
        assertTrue(alerts.stream().anyMatch(x -> x.contains("Превышен бюджет")));
    }

    @Test
    void фильтрПоКатегориямВозвращаетПропуски() {
        finance.addIncome(wallet, "Еда", 100, LocalDate.now(), "");
        Set<String> cats = new LinkedHashSet<>();
        cats.add("Еда");
        cats.add("НетТакой");
        var res = finance.filteredStats(wallet, null, null, cats);
        assertTrue(res.missingCategories().contains("НетТакой"));
    }

    @Test
    void фильтрПоПериоду() {
        finance.addIncome(wallet, "Еда", 100, LocalDate.of(2026, 1, 1), "");
        finance.addExpense(wallet, "Еда", 10, LocalDate.of(2026, 2, 1), "");

        var res = finance.filteredStats(wallet, LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31), null);
        assertEquals(1, res.operationsCount());
        assertEquals(100, res.totalIncome(), 0.0001);
    }
}