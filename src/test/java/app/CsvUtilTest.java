package app;

import org.junit.jupiter.api.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class CsvUtilTest {
    private CsvUtil csv;
    private Path dir;

    @BeforeEach
    void setUp() throws Exception {
        csv = new CsvUtil();
        dir = Files.createTempDirectory("finance-test");
    }

    @Test
    void экспортИмпортCsvСохраняетОперации() throws Exception {
        Wallet w = new Wallet();
        w.getCategories().add("Еда");
        w.getTransactions().add(new Transaction("Еда", 10, false, LocalDate.now(), "обед"));
        w.getBudgets().put("Еда", 100.0);

        Path file = dir.resolve("r.csv");
        csv.exportCsv(file, w);

        Wallet w2 = csv.importCsv(file);
        assertEquals(1, w2.getTransactions().size());
        assertEquals("Еда", w2.getTransactions().get(0).getCategory());
        assertEquals(100.0, w2.getBudgets().get("Еда"), 0.0001);
    }

    @Test
    void импортПустогоФайлаНеПадает() throws Exception {
        Path file = dir.resolve("empty.csv");
        Files.writeString(file, "ТИП;ДАТА;КАТЕГОРИЯ;СУММА;ОПИСАНИЕ\n");
        Wallet w = csv.importCsv(file);
        assertNotNull(w);
    }
}