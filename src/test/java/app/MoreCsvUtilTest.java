package app;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class MoreCsvUtilTest {

    @Test
    void импортСЛевымиСтрокамиНеПадает() throws Exception {
        CsvUtil csv = new CsvUtil();
        Path dir = Files.createTempDirectory("csv-more");
        Path file = dir.resolve("bad.csv");

        Files.writeString(file, """
                ТИП;ДАТА;КАТЕГОРИЯ;СУММА;ОПИСАНИЕ
                ЛЕВОЕ;2026-01-01;Еда;10;ok
                                
                БЮДЖЕТ;КАТЕГОРИЯ;СУММА
                БЮДЖЕТ;Еда;100
                """);

        Wallet w = csv.importCsv(file);
        assertNotNull(w);
        assertTrue(w.getBudgets().containsKey("Еда"));
    }

    @Test
    void импортCsvБезБюджетовРаботает() throws Exception {
        CsvUtil csv = new CsvUtil();
        Path dir = Files.createTempDirectory("csv-more2");
        Path file = dir.resolve("only_ops.csv");

        Files.writeString(file, """
                ТИП;ДАТА;КАТЕГОРИЯ;СУММА;ОПИСАНИЕ
                ДОХОД;2026-01-01;Зарплата;1000;-
                """);

        Wallet w = csv.importCsv(file);
        assertEquals(1, w.getTransactions().size());
    }
}