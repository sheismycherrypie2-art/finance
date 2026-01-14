package app;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;

public class CsvUtil {

    public void exportCsv(Path file, Wallet wallet) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add("ТИП;ДАТА;КАТЕГОРИЯ;СУММА;ОПИСАНИЕ");
        for (Transaction t : wallet.getTransactions()) {
            String type = t.isIncome() ? "ДОХОД" : "РАСХОД";
            lines.add(type + ";" + t.getDate() + ";" + esc(t.getCategory()) + ";" + t.getAmount() + ";" + esc(t.getDescription()));
        }
        lines.add("");
        lines.add("БЮДЖЕТ;КАТЕГОРИЯ;СУММА");
        for (var e : wallet.getBudgets().entrySet()) {
            lines.add("БЮДЖЕТ;" + esc(e.getKey()) + ";" + e.getValue());
        }
        Files.write(file, lines);
    }

    public Wallet importCsv(Path file) throws IOException {
        List<String> lines = Files.readAllLines(file);
        Wallet wallet = new Wallet();
        boolean ops = false;
        boolean budgets = false;

        for (String raw : lines) {
            String line = raw.trim();
            if (line.isEmpty()) continue;

            if (line.startsWith("ТИП;")) { ops = true; budgets = false; continue; }
            if (line.startsWith("БЮДЖЕТ;КАТЕГОРИЯ")) { ops = false; budgets = true; continue; }

            String[] p = split(line);
            if (ops) {
                if (p.length < 5) continue;
                boolean income = p[0].equals("ДОХОД");
                LocalDate date = LocalDate.parse(p[1]);
                String cat = unesc(p[2]);
                double amount = Double.parseDouble(p[3]);
                String desc = unesc(p[4]);

                wallet.getCategories().add(cat);
                wallet.getTransactions().add(new Transaction(cat, amount, income, date, desc));
            } else if (budgets) {
                if (p.length < 3) continue;
                if (!p[0].equals("БЮДЖЕТ")) continue;
                String cat = unesc(p[1]);
                double amount = Double.parseDouble(p[2]);

                wallet.getCategories().add(cat);
                wallet.getBudgets().put(cat, amount);
            }
        }

        return wallet;
    }

    private String esc(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace(";", "\\;").replace("\n", " ").replace("\r", " ");
    }

    private String unesc(String s) {
        if (s == null) return "";
        StringBuilder r = new StringBuilder();
        boolean slash = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (slash) { r.append(c); slash = false; }
            else if (c == '\\') slash = true;
            else r.append(c);
        }
        return r.toString();
    }

    private String[] split(String line) {
        List<String> parts = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean slash = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (slash) { cur.append(c); slash = false; continue; }
            if (c == '\\') { slash = true; continue; }
            if (c == ';') { parts.add(cur.toString()); cur.setLength(0); continue; }
            cur.append(c);
        }
        parts.add(cur.toString());
        return parts.toArray(new String[0]);
    }
}