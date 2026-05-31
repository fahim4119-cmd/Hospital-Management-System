package hospital.ui.dialogs;

import hospital.db.DoctorDAO;
import hospital.db.PatientDAO;
import hospital.models.Appointment;
import hospital.models.Doctor;
import hospital.models.Patient;
import hospital.utils.UITheme;
import hospital.utils.Validator;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AppointmentDialog extends JDialog {

    private JComboBox<Patient> patientCombo;
    private JComboBox<Doctor> doctorCombo;
    private JTextField dateField, timeField, notesField;
    private JComboBox<String> statusCombo;
    private boolean confirmed = false;
    private Appointment appointment;

    public AppointmentDialog(JFrame parent, Appointment existing) {
        super(parent, existing == null ? "Schedule Appointment" : "Edit Appointment", true);
        this.appointment = existing;
        initUI();
        if (existing != null) populateFields(existing);
    }

    private void initUI() {
        setSize(500, 430);
        setLocationRelativeTo(getOwner());
        setResizable(false);

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(Color.WHITE);
        main.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JLabel title = new JLabel(appointment == null ? "Schedule New Appointment" : "Edit Appointment");
        title.setFont(UITheme.FONT_SUBTITLE);
        title.setForeground(UITheme.TEXT_PRIMARY);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Load patients and doctors
        PatientDAO pDAO = new PatientDAO();
        DoctorDAO dDAO = new DoctorDAO();
        List<Patient> patients = pDAO.getAllPatients();
        List<Doctor> doctors = dDAO.getAllDoctors();

        patientCombo = new JComboBox<>();
        patientCombo.setFont(UITheme.FONT_BODY);
        for (Patient p : patients) patientCombo.addItem(p);

        doctorCombo = new JComboBox<>();
        doctorCombo.setFont(UITheme.FONT_BODY);
        for (Doctor d : doctors) doctorCombo.addItem(d);

        dateField = UITheme.createTextField();
        dateField.setToolTipText("Format: YYYY-MM-DD");

        timeField = UITheme.createTextField();
        timeField.setToolTipText("Format: HH:MM (e.g. 09:30)");

        notesField = UITheme.createTextField();

        statusCombo = new JComboBox<>(new String[]{"Pending", "Confirmed", "Completed", "Cancelled"});
        statusCombo.setFont(UITheme.FONT_BODY);

        addRow(form, gbc, 0, "Patient *", patientCombo);
        addRow(form, gbc, 1, "Doctor *", doctorCombo);
        addRow(form, gbc, 2, "Date * (YYYY-MM-DD)", dateField);
        addRow(form, gbc, 3, "Time * (HH:MM)", timeField);
        addRow(form, gbc, 4, "Status", statusCombo);
        addRow(form, gbc, 5, "Notes", notesField);

        JLabel hint = new JLabel("* Required fields. Date example: 2025-03-15");
        hint.setFont(UITheme.FONT_SMALL);
        hint.setForeground(UITheme.TEXT_MUTED);
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        form.add(hint, gbc);

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
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.35; gbc.gridwidth = 1;
        form.add(UITheme.createLabel(label), gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        field.setPreferredSize(new Dimension(240, 34));
        form.add(field, gbc);
    }

    private void populateFields(Appointment a) {
        for (int i = 0; i < patientCombo.getItemCount(); i++) {
            if (patientCombo.getItemAt(i).getId() == a.getPatientId()) {
                patientCombo.setSelectedIndex(i); break;
            }
        }
        for (int i = 0; i < doctorCombo.getItemCount(); i++) {
            if (doctorCombo.getItemAt(i).getId() == a.getDoctorId()) {
                doctorCombo.setSelectedIndex(i); break;
            }
        }
        dateField.setText(a.getAppointmentDate());
        timeField.setText(a.getAppointmentTime());
        statusCombo.setSelectedItem(a.getStatus());
        notesField.setText(a.getNotes());
    }

    private void save() {
        String date = dateField.getText().trim();
        String time = timeField.getText().trim();

        if (patientCombo.getSelectedItem() == null) { showError("Please select a patient."); return; }
        if (doctorCombo.getSelectedItem() == null) { showError("Please select a doctor."); return; }
        if (!Validator.isValidDate(date)) { showError("Date must be in YYYY-MM-DD format."); return; }
        if (!Validator.isValidTime(time)) { showError("Time must be in HH:MM format (e.g. 09:30)."); return; }

        Patient selectedPatient = (Patient) patientCombo.getSelectedItem();
        Doctor selectedDoctor = (Doctor) doctorCombo.getSelectedItem();

        if (appointment == null) appointment = new Appointment();
        appointment.setPatientId(selectedPatient.getId());
        appointment.setDoctorId(selectedDoctor.getId());
        appointment.setAppointmentDate(date);
        appointment.setAppointmentTime(time);
        appointment.setStatus((String) statusCombo.getSelectedItem());
        appointment.setNotes(notesField.getText().trim());

        confirmed = true;
        dispose();
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Validation Error", JOptionPane.ERROR_MESSAGE);
    }

    public boolean isConfirmed() { return confirmed; }
    public Appointment getAppointment() { return appointment; }
}
