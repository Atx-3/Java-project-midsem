import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.*;
import java.text.*;
import java.time.*;
import java.time.format.*;
import java.util.*;
import java.util.List;
import java.util.stream.*;

public class ExpenseTracker extends JFrame {

    static final String DATA_FILE = "expenses.dat";
    static final String[] CATEGORIES = {
        "Food", "Transport", "Housing", "Health",
        "Entertainment", "Shopping", "Education", "Utilities", "Travel", "Other"
    };
    static final Color BG        = new Color(0x0F0F1A);
    static final Color CARD      = new Color(0x1A1A2E);
    static final Color CARD2     = new Color(0x16213E);
    static final Color ACCENT    = new Color(0x00D4FF);
    static final Color ACCENT2   = new Color(0x7B2FBE);
    static final Color GREEN     = new Color(0x00E676);
    static final Color RED       = new Color(0xFF5252);
    static final Color TEXT      = new Color(0xE8EAF6);
    static final Color MUTED     = new Color(0x7986CB);
    static final Color BORDER    = new Color(0x2A2A4A);

    List<Expense> expenses = new ArrayList<>();
    DefaultTableModel tableModel;
    JTable table;
    JLabel totalDayLbl, totalMonthLbl, totalYearLbl, grandTotalLbl;
    JComboBox<String> filterCat;
    JComboBox<String> filterMonth;
    JTextField amountField, descField;
    JComboBox<String> catCombo;
    JSpinner dateSpinner;

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); }
        catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new ExpenseTracker().setVisible(true));
    }

    @Override
    public void transferFocus() {
        auto generate the expense when Enter is pressed in the description field
        if (getFocusOwner) imageUpdate(getIconImage(), ALLBITS, MAXIMIZED_BOTH, ABORT, WIDTH, HEIGHT)
        )
    }
    ExpenseTracker() {
        setTitle("ðŸ’³  Expense Tracker");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 720);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG);

        loadData();
        buildUI();
        refreshAll();
    }

    void buildUI() {
        setLayout(new BorderLayout(0, 0));

        add(buildHeader(), BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                buildLeftPanel(), buildRightPanel());
        split.setDividerLocation(380);
        split.setDividerSize(4);
        split.setBackground(BG);
        split.setBorder(null);
        add(split, BorderLayout.CENTER);

        add(buildStatusBar(), BorderLayout.SOUTH);
    }

    JPanel buildHeader() {
        JPanel p = new GradientPanel(ACCENT2, new Color(0x0A0A15), false);
        p.setLayout(new BorderLayout());
        p.setPreferredSize(new Dimension(0, 64));
        p.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));

        JLabel title = new JLabel("ðŸ’³  EXPENSE TRACKER");
        title.setFont(new Font("Monospaced", Font.BOLD, 22));
        title.setForeground(ACCENT);
        p.add(title, BorderLayout.WEST);

        JLabel sub = new JLabel(LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy")));
        sub.setFont(new Font("SansSerif", Font.PLAIN, 13));
        sub.setForeground(MUTED);
        p.add(sub, BorderLayout.EAST);
        return p;
    }

    JPanel buildLeftPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 16));
        p.setBackground(BG);
        p.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 8));

        p.add(buildSummaryCards(), BorderLayout.NORTH);
        p.add(buildAddForm(), BorderLayout.CENTER);
        return p;
    }

    JPanel buildSummaryCards() {
        JPanel p = new JPanel(new GridLayout(2, 2, 10, 10));
        p.setOpaque(false);

        totalDayLbl   = new JLabel("â‚¹0.00", SwingConstants.CENTER);
        totalMonthLbl = new JLabel("â‚¹0.00", SwingConstants.CENTER);
        totalYearLbl  = new JLabel("â‚¹0.00", SwingConstants.CENTER);
        grandTotalLbl = new JLabel("â‚¹0.00", SwingConstants.CENTER);

        p.add(summaryCard("TODAY",   totalDayLbl,   "ðŸ“…", ACCENT));
        p.add(summaryCard("THIS MONTH", totalMonthLbl, "ðŸ“†", GREEN));
        p.add(summaryCard("THIS YEAR",  totalYearLbl,  "ðŸ—“", new Color(0xFFD740)));
        p.add(summaryCard("ALL TIME",   grandTotalLbl, "ðŸ’°", RED));
        return p;
    }

    JPanel summaryCard(String label, JLabel valueLabel, String icon, Color accent) {
        JPanel card = new RoundPanel(12, CARD);
        card.setLayout(new BorderLayout(4, 4));
        card.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));

        JLabel iconLbl = new JLabel(icon + "  " + label);
        iconLbl.setFont(new Font("SansSerif", Font.BOLD, 10));
        iconLbl.setForeground(accent);
        card.add(iconLbl, BorderLayout.NORTH);

        valueLabel.setFont(new Font("Monospaced", Font.BOLD, 18));
        valueLabel.setForeground(TEXT);
        valueLabel.setHorizontalAlignment(SwingConstants.LEFT);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    JPanel buildAddForm() {
        JPanel card = new RoundPanel(14, CARD2);
        card.setLayout(new GridBagLayout());
        card.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(5, 4, 5, 4);
        g.weightx = 1;

        JLabel heading = new JLabel("âž•  ADD EXPENSE");
        heading.setFont(new Font("Monospaced", Font.BOLD, 14));
        heading.setForeground(ACCENT);
        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        card.add(heading, g);
        g.gridwidth = 1;

        g.gridy = 1; g.gridx = 0; card.add(fmtLabel("Amount (â‚¹)"), g);
        amountField = styledField("e.g. 250.00");
        g.gridx = 1; card.add(amountField, g);

        g.gridy = 2; g.gridx = 0; card.add(fmtLabel("Description"), g);
        descField = styledField("e.g. Lunch at cafÃ©");
        g.gridx = 1; card.add(descField, g);

        g.gridy = 3; g.gridx = 0; card.add(fmtLabel("Category"), g);
        catCombo = new JComboBox<>(CATEGORIES);
        styleCombo(catCombo);
        g.gridx = 1; card.add(catCombo, g);

        g.gridy = 4; g.gridx = 0; card.add(fmtLabel("Date"), g);
        SpinnerDateModel dm = new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH);
        dateSpinner = new JSpinner(dm);
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy"));
        styleSpinner(dateSpinner);
        g.gridx = 1; card.add(dateSpinner, g);

        JPanel btnRow = new JPanel(new GridLayout(1, 2, 10, 0));
        btnRow.setOpaque(false);
        JButton addBtn = accentButton("âœ…  Add", ACCENT2, TEXT);
        JButton clrBtn = accentButton("ðŸ—‘  Clear", CARD, MUTED);
        addBtn.addActionListener(e -> addExpense());
        clrBtn.addActionListener(e -> clearForm());
        btnRow.add(addBtn);
        btnRow.add(clrBtn);
        g.gridy = 5; g.gridx = 0; g.gridwidth = 2;
        card.add(btnRow, g);

        JLabel hint = new JLabel("Press Enter in Amount field to add quickly");
        hint.setFont(new Font("SansSerif", Font.ITALIC, 10));
        hint.setForeground(MUTED);
        hint.setHorizontalAlignment(SwingConstants.CENTER);
        g.gridy = 6; card.add(hint, g);

        amountField.addActionListener(e -> descField.requestFocus());
        descField.addActionListener(e -> addExpense());

        return card;
    }

    JPanel buildRightPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(BG);
        p.setBorder(BorderFactory.createEmptyBorder(16, 8, 16, 16));

        p.add(buildFilterBar(), BorderLayout.NORTH);
        p.add(buildTable(), BorderLayout.CENTER);
        p.add(buildTableButtons(), BorderLayout.SOUTH);
        return p;
    }

    JPanel buildFilterBar() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        p.setOpaque(false);

        JLabel lbl = new JLabel("ðŸ” Filter:");
        lbl.setForeground(MUTED);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        p.add(lbl);

        String[] cats = new String[CATEGORIES.length + 1];
        cats[0] = "All Categories";
        System.arraycopy(CATEGORIES, 0, cats, 1, CATEGORIES.length);
        filterCat = new JComboBox<>(cats);
        styleCombo(filterCat);
        filterCat.addActionListener(e -> refreshTable());
        p.add(filterCat);

        String[] months = new String[14];
        months[0] = "All Months";
        months[1] = "This Month";
        String[] mNames = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
        for (int i = 0; i < 12; i++) months[i+2] = mNames[i] + " " + LocalDate.now().getYear();
        filterMonth = new JComboBox<>(months);
        styleCombo(filterMonth);
        filterMonth.addActionListener(e -> refreshTable());
        p.add(filterMonth);

        JButton exportBtn = accentButton("ðŸ“¤ Export CSV", CARD2, ACCENT);
        exportBtn.addActionListener(e -> exportCSV());
        p.add(exportBtn);
        return p;
    }

    JScrollPane buildTable() {
        String[] cols = {"Date", "Description", "Category", "Amount (â‚¹)"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        styleTable(table);

        JScrollPane sp = new JScrollPane(table);
        sp.setBackground(CARD);
        sp.getViewport().setBackground(CARD);
        sp.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        return sp;
    }

    JPanel buildTableButtons() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        p.setOpaque(false);

        JButton delBtn = accentButton("ðŸ—‘  Delete Selected", new Color(0x3A0020), RED);
        delBtn.addActionListener(e -> deleteSelected());
        p.add(delBtn);

        JButton clearAllBtn = accentButton("âš   Clear All Data", new Color(0x1A0A0A), new Color(0xFF8A65));
        clearAllBtn.addActionListener(e -> clearAll());
        p.add(clearAllBtn);
        return p;
    }

    JPanel buildStatusBar() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 4));
        p.setBackground(new Color(0x0A0A14));
        p.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER));
        JLabel l = new JLabel("ðŸ’¾ Data auto-saved to expenses.dat  |  Press Enter in Description to add expense  |  Select row & click Delete to remove");
        l.setFont(new Font("Monospaced", Font.PLAIN, 11));
        l.setForeground(MUTED);
        p.add(l);
        return p;
    }

    void addExpense() {
        String amtTxt = amountField.getText().trim();
        String desc   = descField.getText().trim();
        if (amtTxt.isEmpty() || desc.isEmpty()) {
            showError("Please fill in Amount and Description.");
            return;
        }
        double amount;
        try { amount = Double.parseDouble(amtTxt); }
        catch (NumberFormatException ex) { showError("Invalid amount. Use numbers like 250 or 49.99"); return; }
        if (amount <= 0) { showError("Amount must be positive."); return; }

        Date d = (Date) dateSpinner.getValue();
        LocalDate date = d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        String cat = (String) catCombo.getSelectedItem();

        expenses.add(new Expense(date, desc, cat, amount));
        saveData();
        refreshAll();
        clearForm();
        amountField.requestFocus();
    }

    void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { showError("Select a row to delete."); return; }
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this expense?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        String dateStr = (String) tableModel.getValueAt(row, 0);
        String descStr = (String) tableModel.getValueAt(row, 1);
        String amtStr  = (String) tableModel.getValueAt(row, 3);
        double amt = Double.parseDouble(amtStr.replace(",", ""));
        expenses.removeIf(e -> e.date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")).equals(dateStr)
                && e.description.equals(descStr)
                && Math.abs(e.amount - amt) < 0.001);
        saveData();
        refreshAll();
    }

    void clearAll() {
        int c = JOptionPane.showConfirmDialog(this, "Delete ALL expense data? This cannot be undone!", "Danger", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (c == JOptionPane.YES_OPTION) { expenses.clear(); saveData(); refreshAll(); }
    }

    void clearForm() {
        amountField.setText("");
        descField.setText("");
        catCombo.setSelectedIndex(0);
        dateSpinner.setValue(new Date());
    }

    void refreshAll() {
        refreshSummary();
        refreshTable();
    }

    void refreshSummary() {
        LocalDate today = LocalDate.now();
        double day = expenses.stream().filter(e -> e.date.equals(today)).mapToDouble(e -> e.amount).sum();
        double month = expenses.stream().filter(e -> e.date.getMonth() == today.getMonth() && e.date.getYear() == today.getYear()).mapToDouble(e -> e.amount).sum();
        double year = expenses.stream().filter(e -> e.date.getYear() == today.getYear()).mapToDouble(e -> e.amount).sum();
        double all = expenses.stream().mapToDouble(e -> e.amount).sum();

        NumberFormat fmt = NumberFormat.getNumberInstance(new Locale("en", "IN"));
        fmt.setMinimumFractionDigits(2); fmt.setMaximumFractionDigits(2);
        totalDayLbl.setText("â‚¹" + fmt.format(day));
        totalMonthLbl.setText("â‚¹" + fmt.format(month));
        totalYearLbl.setText("â‚¹" + fmt.format(year));
        grandTotalLbl.setText("â‚¹" + fmt.format(all));
    }

    void refreshTable() {
        tableModel.setRowCount(0);
        String selCat   = (String) filterCat.getSelectedItem();
        String selMonth = (String) filterMonth.getSelectedItem();
        LocalDate today = LocalDate.now();
        NumberFormat fmt = NumberFormat.getNumberInstance(new Locale("en", "IN"));
        fmt.setMinimumFractionDigits(2); fmt.setMaximumFractionDigits(2);

        List<Expense> filtered = expenses.stream()
            .filter(e -> selCat.equals("All Categories") || e.category.equals(selCat))
            .filter(e -> {
                if (selMonth.equals("All Months")) return true;
                if (selMonth.equals("This Month")) return e.date.getMonth() == today.getMonth() && e.date.getYear() == today.getYear();
                String[] parts = selMonth.split(" ");
                String[] mNames = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
                int m = Arrays.asList(mNames).indexOf(parts[0]) + 1;
                int y = Integer.parseInt(parts[1]);
                return e.date.getMonthValue() == m && e.date.getYear() == y;
            })
            .sorted(Comparator.comparing((Expense e) -> e.date).reversed())
            .collect(Collectors.toList());

        for (Expense e : filtered) {
            tableModel.addRow(new Object[]{
                e.date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                e.description,
                e.category,
                fmt.format(e.amount)
            });
        }

        table.repaint();
    }

    void exportCSV() {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File("expenses_export.csv"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (PrintWriter pw = new PrintWriter(fc.getSelectedFile())) {
                pw.println("Date,Description,Category,Amount");
                for (Expense e : expenses)
                    pw.printf("%s,\"%s\",\"%s\",%.2f%n",
                        e.date.format(DateTimeFormatter.ISO_LOCAL_DATE), e.description, e.category, e.amount);
                JOptionPane.showMessageDialog(this, "Exported successfully!", "Done", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) { showError("Export failed: " + ex.getMessage()); }
        }
    }

    @SuppressWarnings("unchecked")
    void loadData() {
        File f = new File(DATA_FILE);
        if (!f.exists()) return;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            expenses = (List<Expense>) ois.readObject();
        } catch (Exception ignored) {}
    }

    void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(expenses);
        } catch (Exception ex) { showError("Save failed: " + ex.getMessage()); }
    }

    void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Oops", JOptionPane.ERROR_MESSAGE);
    }

    JLabel fmtLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(MUTED);
        l.setFont(new Font("SansSerif", Font.BOLD, 12));
        return l;
    }

    JTextField styledField(String placeholder) {
        JTextField tf = new JTextField() {
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty()) {
                    g.setColor(MUTED);
                    g.setFont(new Font("SansSerif", Font.ITALIC, 12));
                    g.drawString(placeholder, 8, getHeight() / 2 + 5);
                }
            }
        };
        tf.setBackground(new Color(0x0D0D1F));
        tf.setForeground(TEXT);
        tf.setCaretColor(ACCENT);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1),
            BorderFactory.createEmptyBorder(6, 8, 6, 8)));
        tf.setFont(new Font("SansSerif", Font.PLAIN, 13));
        return tf;
    }

    void styleCombo(JComboBox<?> cb) {
        cb.setBackground(new Color(0x0D0D1F));
        cb.setForeground(TEXT);
        cb.setFont(new Font("SansSerif", Font.PLAIN, 12));
        cb.setBorder(BorderFactory.createLineBorder(BORDER));
    }

    void styleSpinner(JSpinner sp) {
        sp.setBackground(new Color(0x0D0D1F));
        sp.setForeground(TEXT);
        sp.setBorder(BorderFactory.createLineBorder(BORDER));
        JFormattedTextField tf = ((JSpinner.DefaultEditor) sp.getEditor()).getTextField();
        tf.setBackground(new Color(0x0D0D1F));
        tf.setForeground(TEXT);
        tf.setCaretColor(ACCENT);
        tf.setFont(new Font("SansSerif", Font.PLAIN, 13));
    }

    JButton accentButton(String text, Color bg, Color fg) {
        JButton b = new JButton(text) {
            public void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? bg.darker() : getModel().isRollover() ? bg.brighter() : bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(fg);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        b.setPreferredSize(new Dimension(0, 36));
        b.setFont(new Font("SansSerif", Font.BOLD, 12));
        b.setForeground(fg);
        b.setBackground(bg);
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    void styleTable(JTable t) {
        t.setBackground(CARD);
        t.setForeground(TEXT);
        t.setGridColor(BORDER);
        t.setFont(new Font("SansSerif", Font.PLAIN, 13));
        t.setRowHeight(34);
        t.setShowHorizontalLines(true);
        t.setShowVerticalLines(false);
        t.setSelectionBackground(new Color(0x2A2A5A));
        t.setSelectionForeground(ACCENT);
        t.setFillsViewportHeight(true);

        JTableHeader h = t.getTableHeader();
        h.setBackground(new Color(0x0D0D1F));
        h.setForeground(ACCENT);
        h.setFont(new Font("Monospaced", Font.BOLD, 12));
        h.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, ACCENT2));
        h.setPreferredSize(new Dimension(0, 38));

        int[] widths = {100, 280, 160, 110};
        for (int i = 0; i < widths.length; i++)
            t.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        DefaultTableCellRenderer rightAlign = new DefaultTableCellRenderer();
        rightAlign.setHorizontalAlignment(SwingConstants.RIGHT);
        t.getColumnModel().getColumn(3).setCellRenderer(rightAlign);

        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable tbl, Object val,
                    boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(tbl, val, sel, foc, row, col);
                if (!sel) {
                    c.setBackground(row % 2 == 0 ? CARD : new Color(0x1F1F38));
                    c.setForeground(col == 3 ? GREEN : TEXT);
                }
                if (col == 3) ((JLabel) c).setHorizontalAlignment(SwingConstants.RIGHT);
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return c;
            }
        });
    }

    static class Expense implements Serializable {
        private static final long serialVersionUID = 1L;
        LocalDate date; String description, category; double amount;
        Expense(LocalDate date, String desc, String cat, double amt) {
            this.date = date; this.description = desc; this.category = cat; this.amount = amt;
        }
    }

    static class RoundPanel extends JPanel {
        int radius; Color bg;
        RoundPanel(int radius, Color bg) { this.radius = radius; this.bg = bg; setOpaque(false); }
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    static class GradientPanel extends JPanel {
        Color c1, c2; boolean horizontal;
        GradientPanel(Color c1, Color c2, boolean horizontal) {
            this.c1 = c1; this.c2 = c2; this.horizontal = horizontal; setOpaque(false);
        }
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setPaint(new GradientPaint(0, 0, c1, horizontal ? getWidth() : 0, horizontal ? 0 : getHeight(), c2));
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
