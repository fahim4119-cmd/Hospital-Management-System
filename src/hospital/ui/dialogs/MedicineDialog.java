package hospital.ui.dialogs;

import hospital.models.Medicine;
import hospital.utils.UITheme;
import hospital.utils.Validator;
import javax.swing.*;
import java.awt.*;

public class MedicineDialog extends JDialog {

    private JTextField nameField, manufacturerField, priceField, quantityField, expiryField, descriptionField;
    private JComboBox<String> categoryCombo;
    private boolean confirmed = false;
    private Medicine medicine;

    public MedicineDialog(JFrame parent, Medicine existing) {
        super(parent, existing == null ? "Add Medicine" : "Edit Medicine", true);
        this.medicine = existing;
        initUI();
        if (existing != null) populateFields(existing);
    }

    private void initUI() {
        setSize(480, 470);
        setLocationRelativeTo(getOwner());
        setResizable(false);

        JPanel main = UITheme.createCard(null);
        main.setLayout(new BorderLayout());
        main.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JLabel title = new JLabel(medicine == null ? "Add New Medicine" : "Edit Medicine");
        title.setFont(UITheme.FONT_SUBTITLE);
        title.setForeground(UITheme.TEXT_PRIMARY);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        nameField = UITheme.createTextField();
        manufacturerField = UITheme.createTextField();
        priceField = UITheme.createTextField();
        quantityField = UITheme.createTextField();
        expiryField = UITheme.createTextField();
        expiryField.setToolTipText("Format: YYYY-MM-DD");
        descriptionField = UITheme.createTextField();

        String[] categories = {"Antibiotic", "Analgesic", "Antacid", "Antihistamine", "Antihypertensive",
                               "Antidiabetic", "Antidepressant", "Antiviral", "Cardiovascular",
                               "Dermatology", "Gastrointestinal", "Neurological", "Respiratory",
                               "Supplement/Vitamin", "Vaccine", "Other"};
        categoryCombo = new JComboBox<>(categories);
        UITheme.styleComboBox(categoryCombo);

        addRow(form, gbc, 0, "Medicine Name *", nameField);
        addRow(form, gbc, 1, "Category *", categoryCombo);
        addRow(form, gbc, 2, "Manufacturer", manufacturerField);
        addRow(form, gbc, 3, "Price (Rs) *", priceField);
        addRow(form, gbc, 4, "Quantity *", quantityField);
        addRow(form, gbc, 5, "Expiry Date (YYYY-MM-DD)", expiryField);
        addRow(form, gbc, 6, "Description", descriptionField);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);
        JButton cancelBtn = UITheme.createDangerButton("Cancel");
        JButton saveBtn = UITheme.createSuccessButton("Save");
        btnPanel.add(cancelBtn);
        btnPanel.add(saveBtn);

        main.add(title, BorderLayout.NORTH);
        main.add(form, BorderLayout.CENTER);
        main.add(btnPanel, BorderLayout.SOUTH);
        add(main);

        saveBtn.addActionListener(e -> save());
        cancelBtn.addActionListener(e -> dispose());
    }

    private void addRow(JPanel form, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        form.add(UITheme.createLabel(label), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        field.setPreferredSize(new Dimension(240, 34));
        form.add(field, gbc);
    }

    private void populateFields(Medicine m) {
        nameField.setText(m.getName());
        manufacturerField.setText(m.getManufacturer());
        priceField.setText(String.valueOf(m.getPrice()));
        quantityField.setText(String.valueOf(m.getQuantity()));
        expiryField.setText(m.getExpiryDate());
        descriptionField.setText(m.getDescription());
        categoryCombo.setSelectedItem(m.getCategory());
    }

    private void save() {
        String name = nameField.getText().trim();
        String priceStr = priceField.getText().trim();
        String qtyStr = quantityField.getText().trim();
        String expiry = expiryField.getText().trim();

        if (Validator.isEmpty(name)) { showError("Medicine name is required."); return; }
        if (!Validator.isValidPrice(priceStr)) { showError("Please enter a valid price."); return; }
        if (!Validator.isValidQuantity(qtyStr)) { showError("Please enter a valid quantity."); return; }
        if (!expiry.isEmpty() && !Validator.isValidDate(expiry)) { showError("Expiry date must be YYYY-MM-DD format."); return; }

        if (medicine == null) medicine = new Medicine();
        medicine.setName(name);
        medicine.setCategory((String) categoryCombo.getSelectedItem());
        medicine.setManufacturer(manufacturerField.getText().trim());
        medicine.setPrice(Double.parseDouble(priceStr));
        medicine.setQuantity(Integer.parseInt(qtyStr));
        medicine.setExpiryDate(expiry);
        medicine.setDescription(descriptionField.getText().trim());

        confirmed = true;
        dispose();
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Validation Error", JOptionPane.ERROR_MESSAGE);
    }

    public boolean isConfirmed() { return confirmed; }
    public Medicine getMedicine() { return medicine; }
}
