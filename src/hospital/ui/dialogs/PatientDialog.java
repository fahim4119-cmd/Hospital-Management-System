package hospital.ui.dialogs;

import hospital.models.Patient;
import hospital.utils.UITheme;
import hospital.utils.Validator;
import javax.swing.*;
import java.awt.*;

public class PatientDialog extends JDialog {

    private JTextField nameField, ageField, phoneField, addressField, diseaseField, photoField;
    private JComboBox<String> genderCombo, bloodGroupCombo;
    private boolean confirmed = false;
    private Patient patient;

    public PatientDialog(JFrame parent, Patient existing) {
        super(parent, existing == null ? "Add Patient" : "Edit Patient", true);
        this.patient = existing;
        initUI();
        if (existing != null) populateFields(existing);
    }

    private void initUI() {
        setSize(480, 470);
        setLocationRelativeTo(getOwner());
        setResizable(false);

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(Color.WHITE);
        main.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JLabel title = new JLabel(patient == null ? "Add New Patient" : "Edit Patient");
        title.setFont(UITheme.FONT_SUBTITLE);
        title.setForeground(UITheme.TEXT_PRIMARY);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        nameField = UITheme.createTextField();
        ageField = UITheme.createTextField();
        phoneField = UITheme.createTextField();
        addressField = UITheme.createTextField();
        diseaseField = UITheme.createTextField();
        photoField = UITheme.createTextField();

        genderCombo = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        genderCombo.setFont(UITheme.FONT_BODY);

        bloodGroupCombo = new JComboBox<>(new String[]{"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-", "Unknown"});
        bloodGroupCombo.setFont(UITheme.FONT_BODY);

        addRow(form, gbc, 0, "Full Name *", nameField);
        addRow(form, gbc, 1, "Age *", ageField);
        addRow(form, gbc, 2, "Gender", genderCombo);
        addRow(form, gbc, 3, "Blood Group", bloodGroupCombo);
        addRow(form, gbc, 4, "Phone *", phoneField);
        addRow(form, gbc, 5, "Address", addressField);
        addRow(form, gbc, 6, "Disease / Condition", diseaseField);
        addRow(form, gbc, 7, "Profile Photo Path", photoField);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setBackground(Color.WHITE);
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

    private void populateFields(Patient p) {
        nameField.setText(p.getName());
        ageField.setText(String.valueOf(p.getAge()));
        phoneField.setText(p.getPhone());
        addressField.setText(p.getAddress());
        diseaseField.setText(p.getDisease());
        photoField.setText(p.getPhotoPath());
        genderCombo.setSelectedItem(p.getGender());
        bloodGroupCombo.setSelectedItem(p.getBloodGroup());
    }

    private void save() {
        String name = nameField.getText().trim();
        String ageStr = ageField.getText().trim();
        String phone = phoneField.getText().trim();

        if (Validator.isEmpty(name)) { showError("Name is required."); return; }
        if (!Validator.isValidAge(ageStr)) { showError("Please enter a valid age (1-149)."); return; }
        if (Validator.isEmpty(phone)) { showError("Phone is required."); return; }

        if (patient == null) patient = new Patient();
        patient.setName(name);
        patient.setAge(Integer.parseInt(ageStr));
        patient.setGender((String) genderCombo.getSelectedItem());
        patient.setBloodGroup((String) bloodGroupCombo.getSelectedItem());
        patient.setPhone(phone);
        patient.setAddress(addressField.getText().trim());
        patient.setDisease(diseaseField.getText().trim());
        patient.setPhotoPath(photoField.getText().trim());

        confirmed = true;
        dispose();
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Validation Error", JOptionPane.ERROR_MESSAGE);
    }

    public boolean isConfirmed() { return confirmed; }
    public Patient getPatient() { return patient; }
}
