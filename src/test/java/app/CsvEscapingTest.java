package app;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class CsvEscapingTest {

    @Test
    void exportImportСохраняетТочкиЗапятой() throws Exception {
        CsvUtil csv = new CsvUtil();
        Path dir = Files.createTempDirectory("csv-esc");
        Path file = dir.resolve("esc.csv");

        Wallet w = new Wallet();
        w.getCategories().add("Еда;Дом");
        w.getBudgets().put("Еда;Дом", 123.0);

        w.getTransactions().add(new Transaction("Еда;Дом", 10, false, LocalDate.now(), "тест;описание"));

        csv.exportCsv(file, w);
        Wallet w2 = csv.importCsv(file);

        assertTrue(w2.getCategories().contains("Еда;Дом"));
        assertEquals(123.0, w2.getBudgets().get("Еда;Дом"), 0.0001);
        assertEquals(1, w2.getTransactions().size());

        Transaction t = w2.getTransactions().get(0);
        assertEquals("Еда;Дом", t.getCategory());
        assertNotNull(t.getDescription());
        assertTrue(t.getDescription().contains("тест;"));
    }

    @Test
    void importCsvСПропускамиСтрокНеПадает() throws Exception {
        CsvUtil csv = new CsvUtil();
        Path dir = Files.createTempDirectory("csv-esc2");
        Path file = dir.resolve("skip.csv");

        Files.writeString(file, """
                ТИП;ДАТА;КАТЕГОРИЯ;СУММА;ОПИСАНИЕ

                ДОХОД;2026-01-01;Зарплата;1000;ok

                БЮДЖЕТ;КАТЕГОРИЯ;СУММА
                БЮДЖЕТ;Еда;100
                """);

        Wallet w = csv.importCsv(file);
        assertNotNull(w);
        assertEquals(1, w.getTransactions().size());
        assertTrue(w.getBudgets().containsKey("Еда"));
    }
}