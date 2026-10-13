import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

public class ExpenseManager {

    private final ArrayList<Expense> expenses = new ArrayList<>();
    private final File dataFile = new File("expenses.csv");

    public ExpenseManager() {
        loadExpenses();
    }

    public void addExpense(Expense expense) {
        expenses.add(expense);
        saveExpenses();
    }

    public boolean deleteExpense(int index) {
        if (index < 0 || index >= expenses.size()) {
            return false;
        }

        expenses.remove(index);
        saveExpenses();
        return true;
    }

    public Expense getExpense(int index) {
        return expenses.get(index);
    }

    public int getExpenseCount() {
        return expenses.size();
    }

    public double getTotalExpense() {
        double total = 0;

        for (Expense expense : expenses) {
            total += expense.getAmount();
        }

        return total;
    }

    public double getMonthlyExpense(int year, int month) {
        double total = 0;

        for (Expense expense : expenses) {
            LocalDate date = expense.getDate();

            if (date.getYear() == year &&
                date.getMonthValue() == month) {
                total += expense.getAmount();
            }
        }

        return total;
    }

    public Map<String, Double> getCategorySummary() {
        Map<String, Double> summary = new TreeMap<>();

        for (Expense expense : expenses) {
            summary.merge(
                expense.getCategory(),
                expense.getAmount(),
                Double::sum
            );
        }

        return summary;
    }

    private String encode(String value) {
        return Base64.getEncoder().encodeToString(
            value.getBytes(StandardCharsets.UTF_8)
        );
    }

    private String decode(String value) {
        return new String(
            Base64.getDecoder().decode(value),
            StandardCharsets.UTF_8
        );
    }

    private void saveExpenses() {
        try (PrintWriter writer = new PrintWriter(
                new FileWriter(dataFile, StandardCharsets.UTF_8))) {

            for (Expense e : expenses) {
                writer.println(
                    encode(e.getCategory()) + "," +
                    e.getAmount() + "," +
                    e.getDate() + "," +
                    encode(e.getDescription())
                );
            }

        } catch (IOException ex) {
            throw new IllegalStateException(
                "Could not save expenses.", ex
            );
        }
    }

    private void loadExpenses() {
        if (!dataFile.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(
                new FileReader(dataFile, StandardCharsets.UTF_8))) {

            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);

                if (parts.length != 4) {
                    continue;
                }

                try {
                    expenses.add(new Expense(
                        decode(parts[0]),
                        Double.parseDouble(parts[1]),
                        LocalDate.parse(parts[2]),
                        decode(parts[3])
                    ));
                } catch (IllegalArgumentException ex) {
                    System.err.println(
                        "Skipping invalid expense record."
                    );
                }
            }

        } catch (IOException ex) {
            throw new IllegalStateException(
                "Could not load saved expenses.", ex
            );
        }
    }
}