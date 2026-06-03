package hospital.ui;

import hospital.db.*;
import hospital.models.*;
import hospital.utils.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.GeneralPath;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

public class BillingPanel extends JPanel implements Exportable {
    private final BillingDAO billingDAO = new BillingDAO();
    private final PatientDAO patientDAO = new PatientDAO();
    private final DoctorDAO doctorDAO = new DoctorDAO();
    private final MedicineDAO medicineDAO = new MedicineDAO();
    
    private final DefaultTableModel billModel = new DefaultTableModel(new String[]{"ID", "Patient", "Doctor", "Total", "Created"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable billTable = new JTable(billModel);
    private List<Bill> bills = new ArrayList<>();
    private JPanel metricsPanel;
    private JPanel chartsPanel;

    public BillingPanel() {
        setLayout(new BorderLayout(0, 14));
        setBackground(UITheme.BACKGROUND);
        initUI();
        loadBills();
    }

    private void initUI() {
        JPanel page = new JPanel(new BorderLayout(0, 14));
        page.setOpaque(false);
        page.add(createToolbar(), BorderLayout.NORTH);

        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.add(createNotice());
        body.add(Box.createVerticalStrut(12));

        metricsPanel = new JPanel(new GridLayout(1, 4, 12, 0));
        metricsPanel.setOpaque(false);
        body.add(metricsPanel);
        body.add(Box.createVerticalStrut(12));

        chartsPanel = new JPanel(new GridLayout(1, 2, 12, 0));
        chartsPanel.setOpaque(false);
        body.add(chartsPanel);
        body.add(Box.createVerticalStrut(12));

        body.add(createTableCard());
        page.add(body, BorderLayout.CENTER);

        add(page, BorderLayout.CENTER);
    }

    private JPanel createToolbar() {
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setOpaque(false);

        JPanel titleBlock = new JPanel();
        titleBlock.setOpaque(false);
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("Billing & Invoices");
        title.setFont(new Font("Segoe UI", Font.BOLD, 21));
        title.setForeground(UITheme.TEXT_PRIMARY);
        JLabel subtitle = new JLabel("Issue new patient invoices, track transaction receipts, and export statements.");
        subtitle.setFont(UITheme.FONT_BODY);
        subtitle.setForeground(UITheme.TEXT_MUTED);
        titleBlock.add(title);
        titleBlock.add(Box.createVerticalStrut(3));
        titleBlock.add(subtitle);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        JButton createBtn = UITheme.createSuccessButton("+ New Invoice");
        createBtn.setPreferredSize(new Dimension(135, 36));
        actions.add(createBtn);

        toolbar.add(titleBlock, BorderLayout.WEST);
        toolbar.add(actions, BorderLayout.EAST);

        createBtn.addActionListener(e -> showInvoiceDialog());
        return toolbar;
    }

    private JPanel createNotice() {
        JPanel notice = new JPanel(new BorderLayout());
        notice.setBackground(new Color(254, 253, 237)); // Light Yellow
        notice.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(253, 224, 71)),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)));
        JLabel text = new JLabel("Invoice PDF export requires iText package; CSV exporting is supported natively.");
        text.setFont(UITheme.FONT_BODY);
        text.setForeground(new Color(161, 98, 7));
        JLabel details = new JLabel("ACCOUNTS AUDIT");
        details.setFont(new Font("Segoe UI", Font.BOLD, 11));
        details.setForeground(new Color(161, 98, 7));
        notice.add(text, BorderLayout.WEST);
        notice.add(details, BorderLayout.EAST);
        return notice;
    }

    private JPanel createTableCard() {
        JPanel card = UITheme.createCard("");
        card.setLayout(new BorderLayout(0, 12));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 360));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("Billing Transactions");
        title.setFont(UITheme.FONT_SUBTITLE);
        title.setForeground(UITheme.TEXT_PRIMARY);

        JPanel searchActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        searchActions.setOpaque(false);
        JButton pdfBtn = UITheme.createPrimaryButton("Export PDF");
        JButton csvBtn = UITheme.createPrimaryButton("Export CSV");
        JButton refreshBtn = UITheme.createSuccessButton("Refresh");
        pdfBtn.setPreferredSize(new Dimension(105, 34));
        csvBtn.setPreferredSize(new Dimension(105, 34));
        refreshBtn.setPreferredSize(new Dimension(88, 34));

        searchActions.add(pdfBtn);
        searchActions.add(csvBtn);
        searchActions.add(refreshBtn);
        header.add(title, BorderLayout.WEST);
        header.add(searchActions, BorderLayout.EAST);

        UITheme.styleTable(billTable);
        billTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(billTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        scrollPane.getViewport().setBackground(Color.WHITE);

        card.add(header, BorderLayout.NORTH);
        card.add(scrollPane, BorderLayout.CENTER);

        refreshBtn.addActionListener(e -> loadBills());
        csvBtn.addActionListener(e -> chooseCsv());
        pdfBtn.addActionListener(e -> exportSelectedPdf());

        return card;
    }

    private void showInvoiceDialog() {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "New Invoice", true);
        dialog.setSize(760, 560);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.add(dialogHeader("New Invoice", UITheme.WARNING), BorderLayout.NORTH);

        JPanel form = new JPanel(new BorderLayout(10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(15, 18, 15, 18));
        form.setBackground(Color.WHITE);

        JPanel top = new JPanel(new GridLayout(1, 2, 10, 0));
        top.setBackground(Color.WHITE);
        JComboBox<Patient> patients = new JComboBox<>();
        for (Patient p : patientDAO.getAllPatients()) patients.addItem(p);
        JComboBox<Doctor> doctors = new JComboBox<>();
        for (Doctor d : doctorDAO.getAllDoctors()) doctors.addItem(d);
        top.add(labeled("Patient", patients));
        top.add(labeled("Doctor", doctors));

        DefaultTableModel itemModel = new DefaultTableModel(new String[]{"Medicine", "Qty", "Unit Price", "Subtotal"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable itemTable = new JTable(itemModel);
        UITheme.styleTable(itemTable);
        List<BillItem> items = new ArrayList<>();
        JLabel totalLabel = new JLabel("Total: Rs 0.00");
        totalLabel.setFont(UITheme.FONT_SUBTITLE);

        JPanel itemControls = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        itemControls.setBackground(Color.WHITE);
        JComboBox<Medicine> medicines = new JComboBox<>();
        for (Medicine m : medicineDAO.getAllMedicines()) medicines.addItem(m);
        JSpinner qty = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
        JButton addItem = UITheme.createSuccessButton("Add Item");
        itemControls.add(medicines);
        itemControls.add(qty);
        itemControls.add(addItem);

        addItem.addActionListener(e -> {
            Medicine med = (Medicine) medicines.getSelectedItem();
            if (med == null) return;
            int quantity = (Integer) qty.getValue();
            if (quantity > med.getQuantity()) {
                JOptionPane.showMessageDialog(dialog, "Not enough stock for " + med.getName());
                return;
            }
            double subtotal = med.getPrice() * quantity;
            BillItem item = new BillItem(0, 0, med.getId(), med.getName(), quantity, med.getPrice(), subtotal);
            items.add(item);
            itemModel.addRow(new Object[]{med.getName(), quantity, String.format("%.2f", med.getPrice()), String.format("%.2f", subtotal)});
            totalLabel.setText("Total: Rs " + String.format("%.2f", total(items)));
        });

        JPanel center = new JPanel(new BorderLayout(0, 10));
        center.setBackground(Color.WHITE);
        center.add(itemControls, BorderLayout.NORTH);
        center.add(new JScrollPane(itemTable), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        bottom.setBackground(Color.WHITE);
        JButton cancel = UITheme.createDangerButton("Cancel");
        JButton save = UITheme.createSuccessButton("Save Bill");
        bottom.add(totalLabel);
        bottom.add(cancel);
        bottom.add(save);

        form.add(top, BorderLayout.NORTH);
        form.add(center, BorderLayout.CENTER);
        form.add(bottom, BorderLayout.SOUTH);
        dialog.add(form, BorderLayout.CENTER);

        cancel.addActionListener(e -> dialog.dispose());
        save.addActionListener(e -> {
            if (items.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Add at least one medicine item.");
                return;
            }
            Patient p = (Patient) patients.getSelectedItem();
            Doctor d = (Doctor) doctors.getSelectedItem();
            Bill bill = new Bill();
            bill.setPatientId(p.getId());
            bill.setDoctorId(d.getId());
            bill.setPatientName(p.getName());
            bill.setDoctorName(d.getName());
            bill.setItems(items);
            bill.setTotalAmount(total(items));
            if (billingDAO.add(bill)) {
                dialog.dispose();
                loadBills();
                JOptionPane.showMessageDialog(this, "Invoice saved successfully.");
            }
        });
        dialog.setVisible(true);
    }

    private JPanel labeled(String label, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(0, 4));
        panel.setBackground(Color.WHITE);
        panel.add(UITheme.createLabel(label), BorderLayout.NORTH);
        field.setPreferredSize(new Dimension(240, 36));
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private JPanel dialogHeader(String title, Color color) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(color);
        panel.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));
        JLabel label = new JLabel(title);
        label.setFont(UITheme.FONT_SUBTITLE);
        label.setForeground(Color.WHITE);
        panel.add(label, BorderLayout.WEST);
        return panel;
    }

    private double total(List<BillItem> items) {
        double total = 0;
        for (BillItem item : items) total += item.getSubtotal();
        return total;
    }

    private void loadBills() {
        bills = billingDAO.getAll();
        billModel.setRowCount(0);
        double totalRevenue = 0;
        double maxInvoice = 0;
        for (Bill b : bills) {
            billModel.addRow(new Object[]{b.getId(), b.getPatientName(), b.getDoctorName(),
                    String.format("%.2f", b.getTotalAmount()), b.getCreatedAt()});
            totalRevenue += b.getTotalAmount();
            maxInvoice = Math.max(maxInvoice, b.getTotalAmount());
        }

        refreshStats(totalRevenue, bills.size(), maxInvoice);
    }

    private void refreshStats(double totalRev, int count, double maxInv) {
        metricsPanel.removeAll();
        double avgInv = count > 0 ? totalRev / count : 0.0;

        metricsPanel.add(metricCard("Total Billings", "Rs " + String.format("%,.0f", totalRev), "+6.4%", UITheme.MEDICINE_ORANGE));
        metricsPanel.add(metricCard("Invoice Count", String.valueOf(count), "+3.8%", UITheme.PRIMARY));
        metricsPanel.add(metricCard("Average Invoice", "Rs " + String.format("%,.0f", avgInv), "+1.2%", UITheme.PATIENT_GREEN));
        metricsPanel.add(metricCard("Max Invoice", "Rs " + String.format("%,.0f", maxInv), "+0.5%", UITheme.APPOINTMENT_PURPLE));

        chartsPanel.removeAll();
        chartsPanel.add(chartCard("Daily Accounts Revenue Trend", new DailyRevenueChart(bills)));
        chartsPanel.add(chartCard("Clinician Share Distribution", new DoctorShareChart(bills)));

        metricsPanel.revalidate();
        metricsPanel.repaint();
        chartsPanel.revalidate();
        chartsPanel.repaint();
    }

    private JPanel metricCard(String label, String value, String trend, Color color) {
        JPanel card = UITheme.createCard("");
        card.setLayout(new BorderLayout(0, 6));
        JLabel title = new JLabel(label);
        title.setFont(UITheme.FONT_SMALL);
        title.setForeground(UITheme.TEXT_MUTED);
        JLabel number = new JLabel(value);
        number.setFont(new Font("Segoe UI", Font.BOLD, 22));
        number.setForeground(UITheme.TEXT_PRIMARY);
        JLabel trendLabel = new JLabel(trend + " since last month");
        trendLabel.setFont(UITheme.FONT_SMALL);
        trendLabel.setForeground(color);
        card.add(title, BorderLayout.NORTH);
        card.add(number, BorderLayout.CENTER);
        card.add(trendLabel, BorderLayout.SOUTH);
        return card;
    }

    private JPanel chartCard(String title, JComponent chart) {
        JPanel card = UITheme.createCard(title);
        card.setPreferredSize(new Dimension(360, 220));
        card.add(chart, BorderLayout.CENTER);
        return card;
    }

    private void exportSelectedPdf() {
        int row = billTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select an invoice first.");
            return;
        }
        Bill bill = bills.get(row);
        bill.setItems(billingDAO.getItems(bill.getId()));
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("invoice-" + bill.getId() + ".pdf"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                InvoicePdfExporter.export(bill, chooser.getSelectedFile());
                JOptionPane.showMessageDialog(this, "Invoice PDF exported.");
            } catch (ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(this, "Add iText to lib to enable PDF export. Expected package: com.itextpdf.text");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "PDF export failed: " + ex.getMessage());
            }
        }
    }

    private void chooseCsv() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("billing.csv"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) exportToCsv(chooser.getSelectedFile());
    }

    @Override
    public void exportToCsv(File file) {
        try {
            CsvExporter.exportTable(billTable, file);
            JOptionPane.showMessageDialog(this, "CSV exported.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "CSV export failed: " + e.getMessage());
        }
    }

    // --- Daily Revenue Chart ---
    private static class DailyRevenueChart extends JPanel {
        private final Map<String, Double> data = new LinkedHashMap<>();

        DailyRevenueChart(List<Bill> list) {
            setOpaque(false);
            setPreferredSize(new Dimension(360, 180));
            if (list != null) {
                for (Bill b : list) {
                    String date = b.getCreatedAt();
                    if (date != null && date.length() >= 10) {
                        String key = date.substring(5, 10);
                        data.put(key, data.getOrDefault(key, 0.0) + b.getTotalAmount());
                    }
                }
            }
            if (data.size() > 7) {
                List<String> keys = new java.util.ArrayList<>(data.keySet());
                for (int i = 0; i < keys.size() - 7; i++) data.remove(keys.get(i));
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            double max = 1.0;
            for (double val : data.values()) max = Math.max(max, val);

            int w = getWidth() - 40;
            int h = getHeight() - 40;
            int count = data.size();
            if (count == 0) return;

            int[] px = new int[count];
            int[] py = new int[count];
            int i = 0;
            for (Map.Entry<String, Double> entry : data.entrySet()) {
                px[i] = 20 + i * (w / Math.max(1, count - 1));
                int barH = (int) ((entry.getValue() / max) * (h - 20));
                py[i] = h - barH + 10;
                i++;
            }

            GeneralPath path = new GeneralPath();
            path.moveTo(px[0], h);
            for (int k = 0; k < count; k++) path.lineTo(px[k], py[k]);
            path.lineTo(px[count - 1], h);
            path.closePath();
            g2.setPaint(new GradientPaint(0, 10, new Color(245, 158, 11, 100), 0, h, new Color(245, 158, 11, 0)));
            g2.fill(path);

            g2.setColor(new Color(245, 158, 11));
            g2.setStroke(new BasicStroke(2.0f));
            for (int k = 0; k < count - 1; k++) {
                g2.drawLine(px[k], py[k], px[k + 1], py[k + 1]);
            }

            i = 0;
            for (Map.Entry<String, Double> entry : data.entrySet()) {
                g2.setColor(Color.WHITE);
                g2.fillOval(px[i] - 4, py[i] - 4, 8, 8);
                g2.setColor(new Color(245, 158, 11));
                g2.drawOval(px[i] - 4, py[i] - 4, 8, 8);

                g2.setColor(UITheme.TEXT_MUTED);
                g2.setFont(UITheme.FONT_SMALL);
                g2.drawString(entry.getKey(), px[i] - 12, h + 24);
                i++;
            }
            g2.dispose();
        }
    }

    // --- Doctor Share Chart ---
    private static class DoctorShareChart extends JPanel {
        private final Map<String, Double> data = new LinkedHashMap<>();

        DoctorShareChart(List<Bill> list) {
            setOpaque(false);
            setPreferredSize(new Dimension(360, 180));
            if (list != null) {
                for (Bill b : list) {
                    String doc = b.getDoctorName();
                    if (doc == null || doc.trim().isEmpty()) doc = "Other";
                    data.put(doc, data.getOrDefault(doc, 0.0) + b.getTotalAmount());
                }
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            double total = 0.0;
            for (double v : data.values()) total += v;
            if (total == 0) total = 1.0;

            Color[] colors = {new Color(245, 158, 11), new Color(20, 184, 166), new Color(99, 102, 241), new Color(244, 63, 94), new Color(34, 197, 94)};
            int size = Math.min(getHeight() - 28, getWidth() / 2 - 20);
            int x = 24;
            int y = 20;
            int start = 90;
            int idx = 0;
            for (double val : data.values()) {
                int arc = (int) Math.round(val * 360.0 / total);
                g2.setColor(colors[idx % colors.length]);
                g2.fillArc(x, y, size, size, start, -arc);
                start -= arc;
                idx++;
            }
            g2.setColor(Color.WHITE);
            g2.fillOval(x + size / 4, y + size / 4, size / 2, size / 2);

            int lx = x + size + 24;
            int ly = y + 10;
            idx = 0;
            for (Map.Entry<String, Double> entry : data.entrySet()) {
                if (idx >= 5) break;
                g2.setColor(colors[idx % colors.length]);
                g2.fillOval(lx, ly + idx * 24, 8, 8);
                g2.setColor(UITheme.TEXT_MUTED);
                g2.setFont(UITheme.FONT_SMALL);
                String name = entry.getKey();
                if (name.length() > 10) name = name.substring(0, 9) + ".";
                g2.drawString(name, lx + 16, ly + 8 + idx * 24);
                g2.setColor(UITheme.TEXT_PRIMARY);
                g2.drawString(String.format("Rs %.0f", entry.getValue()), lx + 110, ly + 8 + idx * 24);
                idx++;
            }
            g2.dispose();
        }
    }
}
