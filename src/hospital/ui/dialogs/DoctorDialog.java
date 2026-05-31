package hospital.ui.dialogs;

import hospital.models.Doctor;
import hospital.utils.UITheme;
import hospital.utils.Validator;
import javax.swing.*;
import java.awt.*;

public class DoctorDialog extends JDialog {

    private JTextField nameField, phoneField, emailField, qualificationField, experienceField;
    private JComboBox<String> specializationCombo, genderCombo;
    private boolean confirmed = false;
    private Doctor doctor;

    public DoctorDialog(JFrame parent, Doctor existing) {
        super(parent, existing == null ? "Add Doctor" : "Edit Doctor", true);
        this.doctor = existing;
        initUI();
        if (existing != null) populateFields(existing);
    }

    private void initUI() {
        setSize(480, 500);
        setLocationRelativeTo(getOwner());
        setResizable(false);

        JPanel main = UITheme.createCard(null);
        main.setLayout(new BorderLayout());
        main.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JLabel title = new JLabel(doctor == null ? "Add New Doctor" : "Edit Doctor");
        title.setFont(UITheme.FONT_SUBTITLE);
        title.setForeground(UITheme.TEXT_PRIMARY);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        nameField = UITheme.createTextField();
        phoneField = UITheme.createTextField();
        emailField = UITheme.createTextField();
        qualificationField = UITheme.createTextField();
        experienceField = UITheme.createTextField();

        String[] specs = {"Cardiologist", "Dermatologist", "Endocrinologist", "ENT Specialist",
                          "Gastroenterologist", "General Physician", "Gynecologist", "Hematologist",
                          "Nephrologist", "Neurologist", "Oncologist", "Ophthalmologist",
                          "Orthopedic Surgeon", "Pediatrician", "Psychiatrist", "Pulmonologist",
                          "Radiologist", "Surgeon", "Urologist", "Other"};
        specializationCombo = new JComboBox<>(specs);
        UITheme.styleComboBox(specializationCombo);

        genderCombo = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        UITheme.styleComboBox(genderCombo);

        addRow(form, gbc, 0, "Full Name *", nameField);
        addRow(form, gbc, 1, "Specialization *", specializationCombo);
        addRow(form, gbc, 2, "Phone *", phoneField);
        addRow(form, gbc, 3, "Email", emailField);
        addRow(form, gbc, 4, "Qualification", qualificationField);
        addRow(form, gbc, 5, "Gender", genderCombo);
        addRow(form, gbc, 6, "Experience (years)", experienceField);

        // Buttons
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
        JLabel lbl = UITheme.createLabel(label);
        form.add(lbl, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        field.setPreferredSize(new Dimension(240, 34));
        form.add(field, gbc);
    }

    private void populateFields(Doctor d) {
        nameField.setText(d.getName());
        phoneField.setText(d.getPhone());
        emailField.setText(d.getEmail());
        qualificationField.setText(d.getQualification());
        experienceField.setText(String.valueOf(d.getExperience()));
        specializationCombo.setSelectedItem(d.getSpecialization());
        genderCombo.setSelectedItem(d.getGender());
    }

    private void save() {
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();
        String qualification = qualificationField.getText().trim();
        String expStr = experienceField.getText().trim();
        String specialization = (String) specializationCombo.getSelectedItem();
        String gender = (String) genderCombo.getSelectedItem();

        if (Validator.isEmpty(name)) { showError("Name is required."); return; }
        if (Validator.isEmpty(phone)) { showError("Phone is required."); return; }
        if (!email.isEmpty() && !Validator.isValidEmail(email)) { showError("Invalid email format."); return; }
        if (!expStr.isEmpty() && !Validator.isValidExperience(expStr)) { showError("Experience must be 0-60 years."); return; }

        int exp = expStr.isEmpty() ? 0 : Integer.parseInt(expStr);

        if (doctor == null) doctor = new Doctor();
        doctor.setName(name);
        doctor.setSpecialization(specialization);
        doctor.setPhone(phone);
        doctor.setEmail(email);
        doctor.setQualification(qualification);
        doctor.setGender(gender);
        doctor.setExperience(exp);

        confirmed = true;
        dispose();
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Validation Error", JOptionPane.ERROR_MESSAGE);
    }

    public boolean isConfirmed() { return confirmed; }
    public Doctor getDoctor() { return doctor; }
}
