package hospital.utils;

import hospital.models.User;

public class Session {
    private static final Session INSTANCE = new Session();
    private User user;

    private Session() {}

    public static Session getInstance() {
        return INSTANCE;
    }

    public void login(User user) {
        this.user = user;
    }

    public void logout() {
        this.user = null;
    }

    public User getUser() {
        return user;
    }

    public String getFullName() {
        return user == null ? "Guest" : user.getFullName();
    }

    public String getRole() {
        return user == null || user.getRole() == null ? "Admin" : user.getRole();
    }

    public boolean isAdmin() {
        return "Admin".equalsIgnoreCase(getRole());
    }

    public boolean isDoctor() {
        return "Doctor".equalsIgnoreCase(getRole());
    }

    public boolean isReceptionist() {
        return "Receptionist".equalsIgnoreCase(getRole());
    }
}
