import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.Map;

public class Main extends JFrame {

    private final ExpenseManager manager = new ExpenseManager();

    private JTextField amountField;
    private JTextField dateField;
    private JTextField descriptionField;
    private JComboBox<String> categoryBox;

    private DefaultTableModel tableModel;
    private JTable expenseTable;

    private JLabel totalValue;
    private JLabel monthlyValue;
    private JLabel countValue;
    private JTextArea categoryReport;

    private final Color background = new Color(18, 25, 42);
    private final Color panelColor = new Color(29, 39, 61);
    private final Color accent = new Color(89, 140, 255);
    private final Color textColor = new Color(235, 240, 250);

    public Main() {
        setTitle("Expense Tracker Pro");
        setSize(1050, 720);
        setMinimumSize(new Dimension(850, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(background);

        setLayout(new BorderLayout(15, 15));

        add(createHeader(), BorderLayout.NORTH);
        add(createMainContent(), BorderLayout.CENTER);
        add(createFooter(), BorderLayout.SOUTH);

        refreshDashboard();
        setVisible(true);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(background);
        header.setBorder(BorderFactory.createEmptyBorder(18, 22, 5, 22));

        JLabel title = new JLabel("EXPENSE TRACKER PRO");
        title.setForeground(textColor);
        title.setFont(new Font("SansSerif", Font.BOLD, 27));

        JLabel subtitle = new JLabel(
            "Manage your money. Understand your spending."
        );
        subtitle.setForeground(new Color(165, 180, 205));
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));

        JPanel titles = new JPanel(new GridLayout(2, 1, 0, 5));
        titles.setOpaque(false);
        titles.add(title);
        titles.add(subtitle);

        header.add(titles, BorderLayout.CENTER);
        return header;
    }

    private JPanel createMainContent() {
        JPanel main = new JPanel(new BorderLayout(15, 15));
        main.setOpaque(false);
        main.setBorder(BorderFactory.createEmptyBorder(5, 22, 10, 22));

        // Dashboard cards
        JPanel cards = new JPanel(new GridLayout(1, 3, 12, 0));
        cards.setOpaque(false);

        totalValue = new JLabel("Rs. 0.00");
        monthlyValue = new JLabel("Rs. 0.00");
        countValue = new JLabel("0");

        cards.add(createCard("TOTAL EXPENSES", totalValue));
        cards.add(createCard("THIS MONTH", monthlyValue));
        cards.add(createCard("TRANSACTIONS", countValue));

        // Left side: expense form and category report
        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.add(createExpenseForm());
        left.add(Box.createVerticalStrut(12));
        left.add(createCategoryPanel());

        // Right side: transaction table
        JPanel right = createTablePanel();

        JPanel content = new JPanel(new BorderLayout(12, 12));
        content.setOpaque(false);
        content.add(left, BorderLayout.WEST);
        content.add(right, BorderLayout.CENTER);

        left.setPreferredSize(new Dimension(310, 400));

        main.add(cards, BorderLayout.NORTH);
        main.add(content, BorderLayout.CENTER);

        return main;
    }

    private JPanel createCard(String heading, JLabel value) {
        JPanel card = new JPanel(new BorderLayout(5, 12));
        card.setBackground(panelColor);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(
                new Color(53, 67, 96), 1
            ),
            BorderFactory.createEmptyBorder(18, 16, 18, 16)
        ));

        JLabel label = new JLabel(heading);
        label.setForeground(new Color(170, 188, 215));
        label.setFont(new Font("SansSerif", Font.BOLD, 12));

        value.setForeground(Color.WHITE);
        value.setFont(new Font("SansSerif", Font.BOLD, 23));

        card.add(label, BorderLayout.NORTH);
        card.add(value, BorderLayout.CENTER);

        return card;
    }

    private JPanel createExpenseForm() {
        JPanel form = new JPanel(new GridLayout(0, 1, 7, 7));
        form.setBackground(panelColor);
        form.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(
                    new Color(65, 82, 115)
                ),
                "  ADD NEW EXPENSE  ",
                0, 0,
                new Font("SansSerif", Font.BOLD, 13),
                textColor
            ),
            BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        amountField = new JTextField();
        dateField = new JTextField(LocalDate.now().toString());
        descriptionField = new JTextField();

        categoryBox = new JComboBox<>(new String[] {
            "Food", "Travel", "Shopping",
            "Education", "Bills", "Health", "Other"
        });

        styleInput(amountField);
        styleInput(dateField);
        styleInput(descriptionField);
        categoryBox.setFont(new Font("SansSerif", Font.PLAIN, 13));

        form.add(createField("Amount (Rs.)", amountField));
        form.add(createField("Category", categoryBox));
        form.add(createField("Date (YYYY-MM-DD)", dateField));
        form.add(createField("Description", descriptionField));

        JButton addButton = createButton(" +  Add Expense", accent);
        addButton.addActionListener(e -> addExpense());

        form.add(addButton);
        return form;
    }

    private JPanel createField(String label, JComponent input) {
        JPanel field = new JPanel(new BorderLayout(0, 5));
        field.setOpaque(false);

        JLabel name = new JLabel(label);
        name.setForeground(textColor);
        name.setFont(new Font("SansSerif", Font.PLAIN, 12));

        field.add(name, BorderLayout.NORTH);
        field.add(input, BorderLayout.CENTER);

        return field;
    }

    private void styleInput(JTextField field) {
        field.setFont(new Font("SansSerif", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(
                new Color(75, 91, 120)
            ),
            BorderFactory.createEmptyBorder(7, 8, 7, 8)
        ));
    }

    private JPanel createCategoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 8));
        panel.setBackground(panelColor);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(
                new Color(53, 67, 96)
            ),
            BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        JLabel heading = new JLabel("CATEGORY SUMMARY");
        heading.setForeground(textColor);
        heading.setFont(new Font("SansSerif", Font.BOLD, 13));

        categoryReport = new JTextArea(6, 18);
        categoryReport.setEditable(false);
        categoryReport.setBackground(panelColor);
        categoryReport.setForeground(new Color(195, 210, 235));
        categoryReport.setFont(new Font("Monospaced", Font.PLAIN, 12));
        categoryReport.setText("No expenses recorded yet.");

        panel.add(heading, BorderLayout.NORTH);
        panel.add(categoryReport, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 10));
        panel.setBackground(panelColor);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel heading = new JLabel("RECENT TRANSACTIONS");
        heading.setForeground(textColor);
        heading.setFont(new Font("SansSerif", Font.BOLD, 14));

        tableModel = new DefaultTableModel(
            new String[] {
                "Category", "Amount", "Date", "Description"
            }, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        expenseTable = new JTable(tableModel);
        expenseTable.setRowHeight(27);
        expenseTable.setSelectionMode(
            ListSelectionModel.SINGLE_SELECTION
        );
        expenseTable.setFillsViewportHeight(true);
        expenseTable.setAutoCreateRowSorter(true);
        expenseTable.setFont(
            new Font("SansSerif", Font.PLAIN, 12)
        );
        expenseTable.getTableHeader().setFont(
            new Font("SansSerif", Font.BOLD, 12)
        );

        JScrollPane scroll = new JScrollPane(expenseTable);

        JButton deleteButton = createButton(
            "Delete Selected", new Color(205, 76, 91)
        );
        deleteButton.addActionListener(e -> deleteExpense());

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setOpaque(false);
        actions.add(deleteButton);

        panel.add(heading, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        panel.add(actions, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setBackground(background);

        JLabel label = new JLabel(
            "Java Swing  |  OOP  |  CSV Storage"
        );
        label.setForeground(new Color(145, 163, 190));
        label.setFont(new Font("SansSerif", Font.PLAIN, 11));

        footer.add(label);
        return footer;
    }

    private JButton createButton(String title, Color color) {
        JButton button = new JButton(title);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setCursor(
            new Cursor(Cursor.HAND_CURSOR)
        );
        button.setBorder(
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        );

        return button;
    }

    private void addExpense() {
        try {
            double amount = Double.parseDouble(
                amountField.getText().trim()
            );

            if (!Double.isFinite(amount) || amount <= 0) {
                showMessage("Amount must be greater than zero.");
                return;
            }

            LocalDate date = LocalDate.parse(
                dateField.getText().trim()
            );

            String category =
                (String) categoryBox.getSelectedItem();

            String description =
                descriptionField.getText().trim();

            Expense expense = new Expense(
                category, amount, date, description
            );

            manager.addExpense(expense);
            refreshDashboard();

            amountField.setText("");
            descriptionField.setText("");

            showMessage("Expense added and saved successfully!");

        } catch (NumberFormatException ex) {
            showMessage("Enter a valid numeric amount.");
        } catch (DateTimeParseException ex) {
            showMessage("Use date format YYYY-MM-DD.");
        } catch (IllegalStateException ex) {
            showMessage("Could not save expense: " +
                ex.getMessage());
        }
    }

    private void deleteExpense() {
        int viewRow = expenseTable.getSelectedRow();

        if (viewRow == -1) {
            showMessage("Please select an expense first.");
            return;
        }

        int modelRow = expenseTable.convertRowIndexToModel(viewRow);

        int answer = JOptionPane.showConfirmDialog(
            this,
            "Delete this expense?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION
        );

        if (answer == JOptionPane.YES_OPTION) {
            try {
                manager.deleteExpense(modelRow);
                refreshDashboard();
            } catch (IllegalStateException ex) {
                showMessage("Could not save changes: " +
                    ex.getMessage());
            }
        }
    }

    private void refreshDashboard() {
        tableModel.setRowCount(0);

        for (int i = manager.getExpenseCount() - 1; i >= 0; i--) {
            Expense expense = manager.getExpense(i);

            tableModel.addRow(new Object[] {
                expense.getCategory(),
                String.format("%.2f", expense.getAmount()),
                expense.getDate(),
                expense.getDescription()
            });
        }

        totalValue.setText(
            String.format("Rs. %.2f", manager.getTotalExpense())
        );

        LocalDate today = LocalDate.now();

        monthlyValue.setText(
            String.format(
                "Rs. %.2f",
                manager.getMonthlyExpense(
                    today.getYear(), today.getMonthValue()
                )
            )
        );

        countValue.setText(
            String.valueOf(manager.getExpenseCount())
        );

        StringBuilder report = new StringBuilder();

        for (Map.Entry<String, Double> entry :
                manager.getCategorySummary().entrySet()) {
            report.append(String.format(
                "%-12s Rs. %,.2f%n",
                entry.getKey(), entry.getValue()
            ));
        }

        categoryReport.setText(
            report.length() == 0
                ? "No expenses recorded yet."
                : report.toString()
        );
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}