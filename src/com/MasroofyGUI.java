package com;

import com.controller.*;
import com.model.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.time.LocalDate;
import java.util.List;

public class MasroofyGUI {

    // ═══════════════════════════════════════════════
    // THEME — Deep Forest Green + Gold
    // ═══════════════════════════════════════════════
    static final Color C_BG        = new Color(14, 24, 18);   // very dark green-black
    static final Color C_SIDEBAR   = new Color(20, 35, 25);   // dark forest green
    static final Color C_CARD      = new Color(22, 40, 28);   // card background
    static final Color C_BORDER    = new Color(40, 70, 48);   // subtle green border
    static final Color C_GOLD      = new Color(212, 175, 55); // rich gold accent
    static final Color C_GOLD2     = new Color(255, 215, 80); // bright gold highlight
    static final Color C_GREEN     = new Color(52, 199, 89);  // mint green positive
    static final Color C_RED       = new Color(255, 80, 80);  // danger red
    static final Color C_TEXT      = new Color(230, 235, 225);// cream white text
    static final Color C_SUBTEXT   = new Color(130, 160, 135);// muted green-grey
    static final Color C_HOVER     = new Color(35, 60, 42);   // button hover

    // ═══════════════════════════════════════════════
    // CONTROLLERS
    // ═══════════════════════════════════════════════
    static AuthController authController       = new AuthController();
    static SetupController setupController     = new SetupController();
    static LimitEngine limitEngine             = new LimitEngine();
   static AlertNotifier alertNotifier = new AlertNotifier() {
    public void notify(BudgetCycle cycle) {
        checkAlert(cycle);
    }
};
    static HistoryController historyController = new HistoryController(limitEngine, alertNotifier);
    static Settingcontroller settingController = new Settingcontroller(authController, historyController, setupController);

    // ═══════════════════════════════════════════════
    // WINDOW
    // ═══════════════════════════════════════════════
    static JFrame frame   = new JFrame("Masroofy");
    static JPanel content = new JPanel(new BorderLayout());

    // ═══════════════════════════════════════════════
    // MAIN
    // ═══════════════════════════════════════════════
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); }
        catch (Exception ignored) {}

        frame.setSize(1100, 700);
        frame.setMinimumSize(new Dimension(900, 600));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setContentPane(buildLogin());
        frame.setVisible(true);
    }

    // ═══════════════════════════════════════════════
    // ALERT
    // ═══════════════════════════════════════════════
    static void checkAlert(BudgetCycle cycle) {
        if (cycle == null || cycle.getTotalAllowance() == 0) return;
        double pct = ((cycle.getTotalAllowance() - cycle.getRemainingBalance()) / cycle.getTotalAllowance()) * 100;
        if (pct >= 100)
            JOptionPane.showMessageDialog(frame, "🚨 Budget fully exhausted!", "Alert", JOptionPane.ERROR_MESSAGE);
        else if (pct >= 80)
            JOptionPane.showMessageDialog(frame, "⚠  You've used 80% of your budget.", "Warning", JOptionPane.WARNING_MESSAGE);
    }

    // ═══════════════════════════════════════════════
    // LOGIN SCREEN
    // ═══════════════════════════════════════════════
    static JPanel buildLogin() {
        JPanel bg = new JPanel(new GridBagLayout());
        bg.setBackground(C_BG);

        JPanel card = new JPanel(null);
        card.setPreferredSize(new Dimension(420, 420));
        card.setBackground(C_CARD);
        card.setBorder(new LineBorder(C_BORDER, 1, true));

        // Gold top bar accent
        JPanel topBar = new JPanel();
        topBar.setBackground(C_GOLD);
        topBar.setBounds(0, 0, 420, 4);
        card.add(topBar);

        JLabel icon = new JLabel("◈", SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 42));
        icon.setForeground(C_GOLD);
        icon.setBounds(0, 30, 420, 50);
        card.add(icon);

        JLabel title = new JLabel("MASROOFY", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(C_TEXT);
        title.setBounds(0, 85, 420, 40);
        card.add(title);

        JLabel sub = new JLabel("Budget Management System", SwingConstants.CENTER);
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(C_SUBTEXT);
        sub.setBounds(0, 128, 420, 20);
        card.add(sub);

        // separator line
        JPanel line = new JPanel();
        line.setBackground(C_BORDER);
        line.setBounds(60, 160, 300, 1);
        card.add(line);

        JLabel pinLbl = new JLabel("ENTER PIN");
        pinLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        pinLbl.setForeground(C_GOLD);
        pinLbl.setBounds(60, 180, 300, 18);
        card.add(pinLbl);

        JPasswordField pinField = new JPasswordField();
        pinField.setBounds(60, 202, 300, 46);
        pinField.setBackground(new Color(30, 50, 35));
        pinField.setForeground(C_TEXT);
        pinField.setCaretColor(C_GOLD);
        pinField.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        pinField.setBorder(new CompoundBorder(
            new LineBorder(C_BORDER, 1),
            new EmptyBorder(0, 12, 0, 12)));
        card.add(pinField);

        JLabel msg = new JLabel(" ", SwingConstants.CENTER);
        msg.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        msg.setForeground(C_RED);
        msg.setBounds(60, 252, 300, 18);
        card.add(msg);

        JButton btn = styledButton(authController.isPinSet() ? "UNLOCK" : "SET PIN & ENTER");
        btn.setBounds(60, 278, 300, 48);
        card.add(btn);

        JLabel footer = new JLabel("Secure • Private • Simple", SwingConstants.CENTER);
        footer.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        footer.setForeground(C_SUBTEXT);
        footer.setBounds(0, 345, 420, 20);
        card.add(footer);

        btn.addActionListener(e -> {
            String pin = new String(pinField.getPassword()).trim();
            if (pin.isEmpty()) { msg.setText("Please enter a PIN."); return; }
            if (!authController.isPinSet()) {
                authController.updatePIN(pin);
                openApp();
            } else {
                String r = authController.validatePIN(pin);
                if ("SUCCESS".equals(r)) { openApp(); }
                else if ("LOCKED".equals(r)) msg.setText("Locked! Wait 30 seconds.");
                else msg.setText("Incorrect PIN. Try again.");
            }
            pinField.setText("");
        });
        pinField.addActionListener(btn.getActionListeners()[0]);

        bg.add(card);
        return bg;
    }

    static void openApp() {
        content.removeAll();
        JPanel app = new JPanel(new BorderLayout());
        app.add(buildSidebar(), BorderLayout.WEST);
        app.add(content, BorderLayout.CENTER);
        frame.setContentPane(app);
        frame.revalidate();
        showDashboard();
    }

    // ═══════════════════════════════════════════════
    // SIDEBAR
    // ═══════════════════════════════════════════════
    static JPanel buildSidebar() {
        JPanel side = new JPanel();
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setPreferredSize(new Dimension(230, 0));
        side.setBackground(C_SIDEBAR);
        side.setBorder(new EmptyBorder(28, 18, 28, 18));

        JLabel logo = new JLabel("◈  MASROOFY");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        logo.setForeground(C_GOLD);
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel tagline = new JLabel("Smart Budget Manager");
        tagline.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tagline.setForeground(C_SUBTEXT);
        tagline.setAlignmentX(Component.LEFT_ALIGNMENT);

        side.add(logo);
        side.add(Box.createVerticalStrut(4));
        side.add(tagline);
        side.add(Box.createVerticalStrut(6));

        JPanel divider = new JPanel();
        divider.setBackground(C_BORDER);
        divider.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        divider.setAlignmentX(Component.LEFT_ALIGNMENT);
        side.add(divider);
        side.add(Box.createVerticalStrut(28));

        String[][] navItems = {
            {"📊", "Dashboard"},
            {"💰", "Create Budget"},
            {"➕", "Add Expense"},
            {"📋", "Transactions"},
        };

        for (String[] item : navItems) {
            JButton b = navButton(item[0] + "  " + item[1]);
            String label = item[1];
            b.addActionListener(e -> {
                switch (label) {
                    case "Dashboard":     showDashboard(); break;
                    case "Create Budget": showSetup();     break;
                    case "Add Expense":   showExpense();   break;
                    case "Transactions":  showHistory();   break;
                }
            });
            side.add(b);
            side.add(Box.createVerticalStrut(6));
        }

        side.add(Box.createVerticalGlue());

        JPanel divider2 = new JPanel();
        divider2.setBackground(C_BORDER);
        divider2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        divider2.setAlignmentX(Component.LEFT_ALIGNMENT);
        side.add(divider2);
        side.add(Box.createVerticalStrut(14));

        JButton resetBtn = navButton("🗑   Reset Data");
        resetBtn.setForeground(C_RED);
        resetBtn.addActionListener(e -> {
            int ok = JOptionPane.showConfirmDialog(frame, "Delete all data?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (ok == JOptionPane.YES_OPTION) {
                settingController.requestReset();
                JOptionPane.showMessageDialog(frame, "All data cleared.");
                showDashboard();
            }
        });

        JButton logoutBtn = navButton("🚪  Logout");
        logoutBtn.addActionListener(e -> {
            frame.setContentPane(buildLogin());
            frame.revalidate();
        });

        side.add(resetBtn);
        side.add(Box.createVerticalStrut(6));
        side.add(logoutBtn);

        return side;
    }

    // ═══════════════════════════════════════════════
    // DASHBOARD
    // ═══════════════════════════════════════════════
    static void showDashboard() {
        content.removeAll();
        content.setBackground(C_BG);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(C_BG);
        panel.setBorder(new EmptyBorder(36, 36, 36, 36));

        JLabel title = sectionTitle("Financial Dashboard");
        panel.add(title);
        panel.add(Box.createVerticalStrut(6));

        BudgetCycle c = setupController.getSavedCycle();

        if (c == null) {
            JLabel empty = new JLabel("No active budget. Click 'Create Budget' to get started.");
            empty.setFont(new Font("Segoe UI", Font.ITALIC, 15));
            empty.setForeground(C_SUBTEXT);
            empty.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(Box.createVerticalStrut(30));
            panel.add(empty);
        } else {
            double spent = c.getTotalAllowance() - c.getRemainingBalance();
            double pct   = c.getTotalAllowance() > 0 ? (spent / c.getTotalAllowance()) * 100 : 0;

            JLabel sub = new JLabel("Cycle ends: " + c.getEndDate() + "  ·  " + c.getRemainingDays() + " days remaining");
            sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            sub.setForeground(C_SUBTEXT);
            sub.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(sub);
            panel.add(Box.createVerticalStrut(28));

            // Cards row
            JPanel cards = new JPanel(new GridLayout(1, 4, 16, 0));
            cards.setBackground(C_BG);
            cards.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
            cards.setAlignmentX(Component.LEFT_ALIGNMENT);
            cards.add(dashCard("TOTAL BUDGET",   String.format("%.0f EGP", c.getTotalAllowance()), C_GOLD));
            cards.add(dashCard("SPENT",           String.format("%.0f EGP", spent),                  C_RED));
            cards.add(dashCard("REMAINING",       String.format("%.0f EGP", c.getRemainingBalance()), C_GREEN));
            cards.add(dashCard("DAILY LIMIT",     String.format("%.0f EGP", c.getSafeDailyLimit()),   C_GOLD2));
            panel.add(cards);
            panel.add(Box.createVerticalStrut(28));

            // Progress bar
            JLabel progLabel = new JLabel(String.format("Budget Used: %.1f%%", pct));
            progLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            progLabel.setForeground(pct >= 80 ? C_RED : C_TEXT);
            progLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(progLabel);
            panel.add(Box.createVerticalStrut(8));

            JPanel barBg = new JPanel(new BorderLayout());
            barBg.setBackground(C_BORDER);
            barBg.setMaximumSize(new Dimension(Integer.MAX_VALUE, 14));
            barBg.setAlignmentX(Component.LEFT_ALIGNMENT);
            JPanel barFill = new JPanel();
            barFill.setBackground(pct >= 100 ? C_RED : pct >= 80 ? new Color(255,160,50) : C_GREEN);
            double clamp = Math.min(pct, 100);
            barFill.setPreferredSize(new Dimension((int)(700 * clamp / 100), 14));
            barBg.add(barFill, BorderLayout.WEST);
            panel.add(barBg);

            // Recent transactions
            panel.add(Box.createVerticalStrut(30));
            panel.add(sectionTitle("Recent Transactions"));
            panel.add(Box.createVerticalStrut(12));

            List<Transaction> txns = historyController.getAll(c.getCycleId());
            if (txns.isEmpty()) {
                JLabel none = new JLabel("No transactions yet.");
                none.setForeground(C_SUBTEXT);
                none.setFont(new Font("Segoe UI", Font.ITALIC, 13));
                none.setAlignmentX(Component.LEFT_ALIGNMENT);
                panel.add(none);
            } else {
                int max = Math.min(4, txns.size());
                for (int i = 0; i < max; i++) {
                    Transaction t = txns.get(i);
                    panel.add(txnRow(t));
                    panel.add(Box.createVerticalStrut(8));
                }
            }
        }

        JScrollPane sp = new JScrollPane(panel);
        sp.setBorder(null);
        sp.getViewport().setBackground(C_BG);
        content.add(sp);
        refresh();
    }

    static JPanel dashCard(String label, String value, Color accent) {
        JPanel card = new JPanel(null);
        card.setBackground(C_CARD);
        card.setBorder(new LineBorder(C_BORDER, 1, true));

        JPanel accentBar = new JPanel();
        accentBar.setBackground(accent);
        accentBar.setBounds(0, 0, 300, 3);
        card.add(accentBar);

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(C_SUBTEXT);
        lbl.setBounds(16, 18, 200, 16);
        card.add(lbl);

        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", Font.BOLD, 26));
        val.setForeground(accent);
        val.setBounds(16, 40, 250, 36);
        card.add(val);

        return card;
    }

    static JPanel txnRow(Transaction t) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setBackground(C_CARD);
        row.setBorder(new CompoundBorder(
            new LineBorder(C_BORDER, 1, true),
            new EmptyBorder(10, 16, 10, 16)));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        String cat = getCategoryName(t.getCategoryId());
        JLabel left = new JLabel(cat + (t.getNote() != null && !t.getNote().isBlank() ? "  —  " + t.getNote() : ""));
        left.setForeground(C_TEXT);
        left.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JLabel right = new JLabel(String.format("- %.2f EGP", t.getAmount()));
        right.setForeground(C_RED);
        right.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JLabel date = new JLabel(t.getTimestamp().toLocalDate().toString());
        date.setForeground(C_SUBTEXT);
        date.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JPanel rightSide = new JPanel(new BorderLayout(8, 0));
        rightSide.setBackground(C_CARD);
        rightSide.add(date, BorderLayout.WEST);
        rightSide.add(right, BorderLayout.EAST);

        row.add(left,      BorderLayout.WEST);
        row.add(rightSide, BorderLayout.EAST);
        return row;
    }

    // ═══════════════════════════════════════════════
    // SETUP
    // ═══════════════════════════════════════════════
    static void showSetup() {
        content.removeAll();
        content.setBackground(C_BG);

        JPanel wrap = new JPanel(new GridBagLayout());
        wrap.setBackground(C_BG);

        JPanel card = new JPanel(null);
        card.setPreferredSize(new Dimension(480, 360));
        card.setBackground(C_CARD);
        card.setBorder(new LineBorder(C_BORDER, 1, true));

        JPanel topBar = new JPanel();
        topBar.setBackground(C_GOLD);
        topBar.setBounds(0, 0, 480, 3);
        card.add(topBar);

        JLabel title = new JLabel("Create Budget Cycle");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(C_TEXT);
        title.setBounds(36, 24, 400, 30);
        card.add(title);

        JLabel sub = new JLabel("Set your allowance and number of days");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(C_SUBTEXT);
        sub.setBounds(36, 58, 400, 18);
        card.add(sub);

        JLabel l1 = formLabel("ALLOWANCE (EGP)");
        l1.setBounds(36, 102, 300, 16);
        card.add(l1);
        JTextField amountField = formField();
        amountField.setBounds(36, 122, 400, 44);
        card.add(amountField);

        JLabel l2 = formLabel("NUMBER OF DAYS");
        l2.setBounds(36, 182, 300, 16);
        card.add(l2);
        JTextField daysField = formField();
        daysField.setText("30");
        daysField.setBounds(36, 202, 400, 44);
        card.add(daysField);

        JLabel msg = new JLabel(" ");
        msg.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        msg.setForeground(C_RED);
        msg.setBounds(36, 254, 400, 16);
        card.add(msg);

        JButton saveBtn = styledButton("CREATE BUDGET");
        saveBtn.setBounds(36, 278, 400, 48);
        card.add(saveBtn);

        saveBtn.addActionListener(e -> {
            try {
                double a = Double.parseDouble(amountField.getText().trim());
                int d    = Integer.parseInt(daysField.getText().trim());
                if (a <= 0 || d <= 0) { msg.setText("Values must be positive."); return; }
                setupController.startNewCycle(a, LocalDate.now(), LocalDate.now().plusDays(d));
                JOptionPane.showMessageDialog(frame, "✅ Budget cycle created!");
                showDashboard();
            } catch (Exception ex) {
                msg.setText("Invalid input. Please enter numbers.");
            }
        });

        wrap.add(card);
        content.add(wrap);
        refresh();
    }

    // ═══════════════════════════════════════════════
    // ADD EXPENSE
    // ═══════════════════════════════════════════════
    static void showExpense() {
        content.removeAll();
        content.setBackground(C_BG);

        JPanel wrap = new JPanel(new GridBagLayout());
        wrap.setBackground(C_BG);

        JPanel card = new JPanel(null);
        card.setPreferredSize(new Dimension(480, 400));
        card.setBackground(C_CARD);
        card.setBorder(new LineBorder(C_BORDER, 1, true));

        JPanel topBar = new JPanel();
        topBar.setBackground(C_GREEN);
        topBar.setBounds(0, 0, 480, 3);
        card.add(topBar);

        JLabel title = new JLabel("Record Expense");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(C_TEXT);
        title.setBounds(36, 24, 400, 30);
        card.add(title);

        JLabel sub = new JLabel("Log a new spending transaction");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(C_SUBTEXT);
        sub.setBounds(36, 58, 400, 18);
        card.add(sub);

        JLabel l1 = formLabel("AMOUNT (EGP)");
        l1.setBounds(36, 98, 300, 16);
        card.add(l1);
        JTextField amountField = formField();
        amountField.setBounds(36, 118, 400, 44);
        card.add(amountField);

        JLabel l2 = formLabel("CATEGORY");
        l2.setBounds(36, 178, 300, 16);
        card.add(l2);
        String[] cats = {"🍔  Food", "🚕  Transport", "🎮  Entertainment", "📦  Other"};
        JComboBox<String> catBox = new JComboBox<>(cats);
        catBox.setBounds(36, 198, 400, 44);
        catBox.setBackground(new Color(30, 50, 35));
        catBox.setForeground(C_TEXT);
        catBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        card.add(catBox);

        JLabel l3 = formLabel("NOTE (OPTIONAL)");
        l3.setBounds(36, 258, 300, 16);
        card.add(l3);
        JTextField noteField = formField();
        noteField.setBounds(36, 278, 400, 44);
        card.add(noteField);

        JLabel msg = new JLabel(" ");
        msg.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        msg.setForeground(new Color(100, 200, 120));
        msg.setBounds(36, 328, 400, 16);
        card.add(msg);

        JButton saveBtn = styledButton("SAVE EXPENSE");
        saveBtn.setBounds(36, 348, 400, 48);
        card.add(saveBtn);

        saveBtn.addActionListener(e -> {
            BudgetCycle c = setupController.getSavedCycle();
            if (c == null) { msg.setForeground(C_RED); msg.setText("No active budget. Create one first."); return; }
            try {
                double amount = Double.parseDouble(amountField.getText().trim());
                if (amount <= 0) { msg.setForeground(C_RED); msg.setText("Amount must be positive."); return; }
                historyController.logExpense(amount, catBox.getSelectedIndex() + 1, noteField.getText(), c.getCycleId());
                msg.setForeground(C_GREEN);
                msg.setText("✓  Expense saved successfully.");
                amountField.setText("");
                noteField.setText("");
            } catch (Exception ex) {
                msg.setForeground(C_RED);
                msg.setText("Invalid amount. Please enter a number.");
            }
        });

        wrap.add(card);
        content.add(wrap);
        refresh();
    }

    // ═══════════════════════════════════════════════
    // HISTORY
    // ═══════════════════════════════════════════════
    static void showHistory() {
        content.removeAll();
        content.setBackground(C_BG);

        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBackground(C_BG);
        panel.setBorder(new EmptyBorder(36, 36, 36, 36));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(C_BG);
        header.setBorder(new EmptyBorder(0, 0, 20, 0));
        header.add(sectionTitle("Transaction History"), BorderLayout.WEST);
        panel.add(header, BorderLayout.NORTH);

        BudgetCycle c = setupController.getSavedCycle();
        if (c == null) {
            JLabel empty = new JLabel("No active budget cycle.");
            empty.setForeground(C_SUBTEXT);
            empty.setFont(new Font("Segoe UI", Font.ITALIC, 15));
            panel.add(empty, BorderLayout.CENTER);
        } else {
            List<Transaction> txns = historyController.getAll(c.getCycleId());
            String[] cols = {"#", "Amount", "Category", "Note", "Date"};
            String[][] data = new String[txns.size()][5];
            for (int i = 0; i < txns.size(); i++) {
                Transaction t = txns.get(i);
                data[i][0] = String.valueOf(t.getTransactionId());
                data[i][1] = String.format("%.2f EGP", t.getAmount());
                data[i][2] = getCategoryName(t.getCategoryId());
                data[i][3] = t.getNote() == null ? "" : t.getNote();
                data[i][4] = t.getTimestamp().toLocalDate().toString();
            }

            JTable table = new JTable(data, cols) {
                public boolean isCellEditable(int r, int c2) { return false; }
            };

            // Style the table
            table.setBackground(C_CARD);
            table.setForeground(C_TEXT);
            table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            table.setRowHeight(40);
            table.setShowGrid(false);
            table.setIntercellSpacing(new Dimension(0, 1));
            table.setSelectionBackground(C_HOVER);
            table.setSelectionForeground(C_TEXT);
            table.setFillsViewportHeight(true);

            JTableHeader th = table.getTableHeader();
            th.setBackground(C_SIDEBAR);
            th.setForeground(C_GOLD);
            th.setFont(new Font("Segoe UI", Font.BOLD, 12));
            th.setBorder(new EmptyBorder(0,0,0,0));
            th.setPreferredSize(new Dimension(0, 40));

            // Column widths
            table.getColumnModel().getColumn(0).setPreferredWidth(40);
            table.getColumnModel().getColumn(1).setPreferredWidth(120);
            table.getColumnModel().getColumn(2).setPreferredWidth(130);
            table.getColumnModel().getColumn(3).setPreferredWidth(200);
            table.getColumnModel().getColumn(4).setPreferredWidth(110);

            // Default cell renderer with padding
            DefaultTableCellRenderer r = new DefaultTableCellRenderer() {
                public Component getTableCellRendererComponent(JTable t2, Object v, boolean sel, boolean foc, int row, int col) {
                    super.getTableCellRendererComponent(t2, v, sel, foc, row, col);
                    setBorder(new EmptyBorder(0, 14, 0, 14));
                    setBackground(sel ? C_HOVER : (row % 2 == 0 ? C_CARD : new Color(26, 46, 32)));
                    setForeground(col == 1 ? C_RED : C_TEXT);
                    return this;
                }
            };
            for (int i = 0; i < cols.length; i++) table.getColumnModel().getColumn(i).setCellRenderer(r);

            JScrollPane sp = new JScrollPane(table);
            sp.setBorder(new LineBorder(C_BORDER, 1));
            sp.getViewport().setBackground(C_CARD);
            panel.add(sp, BorderLayout.CENTER);
        }

        content.add(panel);
        refresh();
    }

    // ═══════════════════════════════════════════════
    // SHARED UI HELPERS
    // ═══════════════════════════════════════════════
    static JButton styledButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(C_GOLD);
        b.setForeground(new Color(14, 24, 18));
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(C_GOLD2); }
            public void mouseExited(MouseEvent e)  { b.setBackground(C_GOLD);  }
        });
        return b;
    }

    static JButton navButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(C_SIDEBAR);
        b.setForeground(C_TEXT);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(C_HOVER); }
            public void mouseExited(MouseEvent e)  { b.setBackground(C_SIDEBAR); }
        });
        return b;
    }

    static JLabel sectionTitle(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 26));
        l.setForeground(C_TEXT);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    static JLabel formLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 10));
        l.setForeground(C_GOLD);
        return l;
    }

    static JTextField formField() {
        JTextField f = new JTextField();
        f.setBackground(new Color(30, 50, 35));
        f.setForeground(C_TEXT);
        f.setCaretColor(C_GOLD);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        f.setBorder(new CompoundBorder(
            new LineBorder(C_BORDER, 1),
            new EmptyBorder(0, 12, 0, 12)));
        return f;
    }

    static void refresh() {
        frame.revalidate();
        frame.repaint();
    }

    static String getCategoryName(int id) {
        switch (id) {
            case 1: return "🍔 Food";
            case 2: return "🚕 Transport";
            case 3: return "🎮 Entertainment";
            case 4: return "📦 Other";
            default: return "Category " + id;
        }
    }
}