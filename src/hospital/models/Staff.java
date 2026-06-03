package hospital.models;

public class Staff {
    private int id;
    private String name;
    private String role;
    private String department;
    private String phone;
    private String shift;
    private double salary;

    public Staff() {}

    public Staff(int id, String name, String role, String department, String phone, String shift, double salary) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.department = department;
        this.phone = phone;
        this.shift = shift;
        this.salary = salary;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getShift() { return shift; }
    public void setShift(String shift) { this.shift = shift; }
    public double getSalary() { return salary; }
    public void setSalary(double salary) { this.salary = salary; }
}
