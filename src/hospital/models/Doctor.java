package hospital.models;

public class Doctor {
    private int id;
    private String name;
    private String specialization;
    private String phone;
    private String email;
    private String qualification;
    private String gender;
    private int experience;

    public Doctor() {}

    public Doctor(int id, String name, String specialization, String phone, String email,
                  String qualification, String gender, int experience) {
        this.id = id;
        this.name = name;
        this.specialization = specialization;
        this.phone = phone;
        this.email = email;
        this.qualification = qualification;
        this.gender = gender;
        this.experience = experience;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getQualification() { return qualification; }
    public void setQualification(String qualification) { this.qualification = qualification; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public int getExperience() { return experience; }
    public void setExperience(int experience) { this.experience = experience; }

    @Override
    public String toString() { return name; }
}
