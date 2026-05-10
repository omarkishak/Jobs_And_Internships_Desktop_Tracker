import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;

import java.io.*;
import java.util.*;


public class JobTracker extends JFrame {

    private static final String DATA_FILE = "job_tracker_data.txt";
    private static final Color BG_BLACK = new Color(28, 28, 28);
    private static final Color BG_CARD = new Color(43, 43, 43);
    private static final Color BG_ROW_ALT = new Color(35, 35, 35);
    private static final Color ACCENT = new Color(255, 255, 255);
    private static final Color ACCENT_DIM = new Color(160, 160, 160);
    private static final Color HEADER_BG = new Color(0, 0, 0);
    private static final Color BORDER_COLOR = new Color(50, 50, 50);
    private static final Color BTN_ADD_BG = new Color(255, 255, 255);
    private static final Color BTN_ADD_FG = new Color(28, 28, 28);
    private static final Color BTN_DEL_BG = new Color(40, 15, 15);
    private static final Color BTN_DEL_FG = new Color(220, 80, 80);
    private static final Color STATUS_WAITING = new Color(236, 172, 31);
    private static final Color STATUS_PROGRESS = new Color(50, 130, 220);
    private static final Color STATUS_ACCEPTED = new Color(50, 180, 100);
    private static final Color STATUS_REJECTED = new Color(231, 46, 46);

    private JPanel entriesPanel;
    private JScrollPane scrollPane;
    private final java.util.List<JobEntry> entries = new ArrayList<>();

    public JobTracker() {
        setTitle("Job Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setMinimumSize(new Dimension(950, 500));
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_BLACK);
        setLayout(new BorderLayout());
        buildUI();
        loadData();
        setVisible(true);
    }

    private void buildUI() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(BG_BLACK);
        topBar.setBorder(new EmptyBorder(28, 36, 0, 36));

        JLabel title = new JLabel("JOB TRACKER");
        title.setForeground(ACCENT);
        title.setFont(new Font("Courier New", Font.BOLD, 22));

        JButton addBtn = new JButton("+ New Application");
        styleAddButton(addBtn);
        addBtn.addActionListener(e -> addNewEntry(null, null, null, null, null));

        topBar.add(title, BorderLayout.WEST);
        topBar.add(addBtn, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        JPanel header = new JPanel(new GridBagLayout());
        header.setBackground(HEADER_BG);
        header.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(1, 1, 1, 1, BORDER_COLOR),
                new EmptyBorder(10, 14, 10, 14)
        ));

        String[] cols   = {"#", "Company Name", "Type", "Field", "Status", "Link", ""};
        int[]    widths = {40,  200,             110,    140,     130,      200,    80};
        GridBagConstraints ghc = new GridBagConstraints();
        ghc.fill = GridBagConstraints.HORIZONTAL;
        ghc.insets = new Insets(0, 4, 0, 4);
        for (int i = 0; i < cols.length; i++) {
            ghc.gridx = i;
            ghc.weightx = (i == 5) ? 1.0 : 0;
            JLabel lbl = new JLabel(cols[i], SwingConstants.CENTER);
            lbl.setForeground(ACCENT_DIM);
            lbl.setFont(new Font("Courier New", Font.BOLD, 11));
            lbl.setPreferredSize(new Dimension(widths[i], 16));
            header.add(lbl, ghc);
        }

        entriesPanel = new JPanel();
        entriesPanel.setLayout(new BoxLayout(entriesPanel, BoxLayout.Y_AXIS));
        entriesPanel.setBackground(BG_BLACK);

        scrollPane = new JScrollPane(entriesPanel);
        scrollPane.setBorder(null);
        scrollPane.setBackground(BG_BLACK);
        scrollPane.getViewport().setBackground(BG_BLACK);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setBackground(BG_BLACK);
        scrollPane.getHorizontalScrollBar().setBackground(BG_BLACK);

        JPanel centerArea = new JPanel(new BorderLayout());
        centerArea.setBackground(BG_BLACK);
        centerArea.setBorder(new EmptyBorder(20, 36, 20, 36));
        centerArea.add(header, BorderLayout.NORTH);
        centerArea.add(scrollPane, BorderLayout.CENTER);
        add(centerArea, BorderLayout.CENTER);

        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 36, 8));
        statusBar.setBackground(new Color(15, 15, 15));
        statusBar.setBorder(new MatteBorder(1, 0, 0, 0, BORDER_COLOR));
        JLabel statusLbl = new JLabel("All data is saved automatically to job_tracker_data.txt");
        statusLbl.setForeground(new Color(80, 80, 80));
        statusLbl.setFont(new Font("Courier New", Font.PLAIN, 10));
        statusBar.add(statusLbl);
        add(statusBar, BorderLayout.SOUTH);
    }

    private void styleAddButton(JButton btn) {
        btn.setBackground(BTN_ADD_BG);
        btn.setForeground(BTN_ADD_FG);
        btn.setFont(new Font("Courier New", Font.BOLD, 12));
        btn.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(8, 18, 8, 18)
        ));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(230, 230, 230)); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(BTN_ADD_BG); }
        });
    }

    private void renumberRows() {
        for (int i = 0; i < entries.size(); i++) {
            entries.get(i).numLabel.setText(String.valueOf(i + 1));
        }
    }

    private void addNewEntry(String company, String type, String field, String status, String link) {
        JobEntry entry = new JobEntry();
        entries.add(entry);

        boolean alt = (entries.size() % 2 == 0);
        Color rowBg = alt ? BG_ROW_ALT : BG_CARD;

        JPanel row = new JPanel(new GridBagLayout());
        row.setBackground(rowBg);
        row.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 1, 1, 1, BORDER_COLOR),
                new EmptyBorder(8, 14, 8, 14)
        ));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));

        GridBagConstraints rc = new GridBagConstraints();
        rc.fill = GridBagConstraints.HORIZONTAL;
        rc.insets = new Insets(0, 4, 0, 4);
        rc.gridy = 0;

        // Row number — stored in entry so renumberRows() can update it
        JLabel numLabel = new JLabel(String.valueOf(entries.size()), SwingConstants.CENTER);
        numLabel.setForeground(new Color(100, 100, 100));
        numLabel.setFont(new Font("Courier New", Font.BOLD, 12));
        numLabel.setPreferredSize(new Dimension(40, 32));
        entry.numLabel = numLabel;

        // Company
        JTextField companyField = styledTextField(company != null ? company : "", 200);
        entry.companyField = companyField;

        // Type
        String[] types = {"Job", "Internship"};
        JComboBox<String> typeBox = styledCombo(types);
        if (type != null) typeBox.setSelectedItem(type);
        entry.typeBox = typeBox;

        // Field
        String[] fields = {"Tech", "Design", "Finance", "Marketing", "HR", "Sales", "Engineering", "Other"};
        JComboBox<String> fieldBox = styledCombo(fields);
        if (field != null) fieldBox.setSelectedItem(field);
        entry.fieldBox = fieldBox;

        // Status
        String[] statuses = {"Waiting", "In Progress", "Accepted", "Rejected"};
        JComboBox<String> statusBox = styledComboStatus(statuses);
        if (status != null) statusBox.setSelectedItem(status);
        entry.statusBox = statusBox;

        // Link
        JTextField linkField = styledTextField(link != null ? link : "", 200);
        linkField.setFont(new Font("Courier New", Font.PLAIN, 11));
        linkField.setForeground(new Color(100, 160, 255));
        linkField.setHorizontalAlignment(JTextField.CENTER);
        entry.linkField = linkField;

        // Delete
        JButton delBtn = new JButton("Delete");
        delBtn.setBackground(BTN_DEL_BG);
        delBtn.setForeground(BTN_DEL_FG);
        delBtn.setFont(new Font("Courier New", Font.BOLD, 10));
        delBtn.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(100, 30, 30), 1),
                new EmptyBorder(5, 10, 5, 10)
        ));
        delBtn.setFocusPainted(false);
        delBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        delBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { delBtn.setBackground(new Color(80, 20, 20)); }
            public void mouseExited(MouseEvent e)  { delBtn.setBackground(BTN_DEL_BG); }
        });

        final JPanel rowRef = row;
        final JobEntry entryRef = entry;
        delBtn.addActionListener(e -> {
            entries.remove(entryRef);
            entriesPanel.remove(rowRef);
            entriesPanel.revalidate();
            entriesPanel.repaint();
            renumberRows(); // ← update all numbers after deletion
            saveData();
        });

        SimpleDocListener dl = () -> saveData();
        companyField.getDocument().addDocumentListener(dl);
        linkField.getDocument().addDocumentListener(dl);
        typeBox.addActionListener(e -> saveData());
        fieldBox.addActionListener(e -> saveData());
        statusBox.addActionListener(e -> saveData());

        int[] widths = {40, 200, 110, 140, 130, -1, 80};
        Component[] comps = {numLabel, companyField, typeBox, fieldBox, statusBox, linkField, delBtn};
        for (int i = 0; i < comps.length; i++) {
            rc.gridx = i;
            rc.weightx = (i == 5) ? 1.0 : 0;
            if (widths[i] > 0) comps[i].setPreferredSize(new Dimension(widths[i], 32));
            row.add(comps[i], rc);
        }

        entriesPanel.add(row);
        entriesPanel.revalidate();
        entriesPanel.repaint();

        SwingUtilities.invokeLater(() -> {
            JScrollBar bar = scrollPane.getVerticalScrollBar();
            bar.setValue(bar.getMaximum());
        });

        if (company == null) saveData();
    }

    private JTextField styledTextField(String text, int width) {
        JTextField f = new JTextField(text);
        f.setBackground(new Color(35, 35, 35));
        f.setForeground(ACCENT);
        f.setCaretColor(ACCENT);
        f.setFont(new Font("Courier New", Font.PLAIN, 12));
        f.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1),
                new EmptyBorder(4, 8, 4, 8)
        ));
        f.setPreferredSize(new Dimension(width, 32));
        f.setHorizontalAlignment(JTextField.CENTER);
        return f;
    }

    private JComboBox<String> styledCombo(String[] items) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setBackground(new Color(35, 35, 35));
        cb.setForeground(ACCENT);
        cb.setFont(new Font("Courier New", Font.PLAIN, 11));
        cb.setBorder(new LineBorder(BORDER_COLOR, 1));
        cb.setFocusable(false);
        DefaultListCellRenderer r = new DefaultListCellRenderer();
        r.setHorizontalAlignment(SwingConstants.CENTER);
        cb.setRenderer(r);
        return cb;
    }

    private JComboBox<String> styledComboStatus(String[] items) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setBackground(new Color(35, 35, 35));
        cb.setForeground(STATUS_WAITING);
        cb.setFont(new Font("Courier New", Font.BOLD, 11));
        cb.setBorder(new LineBorder(BORDER_COLOR, 1));
        cb.setFocusable(false);
        DefaultListCellRenderer r = new DefaultListCellRenderer();
        r.setHorizontalAlignment(SwingConstants.CENTER);
        cb.setRenderer(r);
        cb.addActionListener(e -> {
            String sel = (String) cb.getSelectedItem();
            if ("Waiting".equals(sel))          cb.setForeground(STATUS_WAITING);
            else if ("In Progress".equals(sel)) cb.setForeground(STATUS_PROGRESS);
            else if ("Accepted".equals(sel))    cb.setForeground(STATUS_ACCEPTED);
            else if ("Rejected".equals(sel))    cb.setForeground(STATUS_REJECTED);
        });
        return cb;
    }

    private void saveData() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(DATA_FILE))) {
            for (JobEntry e : entries) {
                String company = e.companyField.getText().replace("|", "\\|");
                String type    = ((String) e.typeBox.getSelectedItem()).replace("|", "\\|");
                String field   = ((String) e.fieldBox.getSelectedItem()).replace("|", "\\|");
                String status  = ((String) e.statusBox.getSelectedItem()).replace("|", "\\|");
                String link    = e.linkField.getText().replace("|", "\\|");
                pw.println(company + "|" + type + "|" + field + "|" + status + "|" + link);
            }
        } catch (IOException ex) { ex.printStackTrace(); }
    }

    private void loadData() {
        File f = new File(DATA_FILE);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split("(?<!\\\\)\\|", -1);
                String company = parts.length > 0 ? parts[0].replace("\\|", "|") : "";
                String type    = parts.length > 1 ? parts[1].replace("\\|", "|") : "Job";
                String field   = parts.length > 2 ? parts[2].replace("\\|", "|") : "Tech";
                String status  = parts.length > 3 ? parts[3].replace("\\|", "|") : "Waiting";
                String link    = parts.length > 4 ? parts[4].replace("\\|", "|") : "";
                addNewEntry(company, type, field, status, link);
            }
        } catch (IOException ex) { ex.printStackTrace(); }
    }

    static class JobEntry {
        JLabel numLabel;
        JTextField companyField;
        JComboBox<String> typeBox;
        JComboBox<String> fieldBox;
        JComboBox<String> statusBox;
        JTextField linkField;
    }

    interface SimpleDocListener extends javax.swing.event.DocumentListener {
        void onChange();
        default void insertUpdate(javax.swing.event.DocumentEvent e)  { onChange(); }
        default void removeUpdate(javax.swing.event.DocumentEvent e)  { onChange(); }
        default void changedUpdate(javax.swing.event.DocumentEvent e) { onChange(); }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            UIManager.put("ScrollBar.background", new Color(20, 20, 20));
            UIManager.put("ScrollBar.thumb", new Color(60, 60, 60));
            UIManager.put("ScrollBar.track", new Color(20, 20, 20));
        } catch (Exception ignored) {}
        SwingUtilities.invokeLater(JobTracker::new);
    }
}