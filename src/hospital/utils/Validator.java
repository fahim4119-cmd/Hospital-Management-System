package hospital.utils;

public class Validator {

    public static boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static boolean isValidEmail(String email) {
        return email != null && email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && phone.matches("^[0-9\\-\\+\\s]{7,15}$");
    }

    public static boolean isValidAge(String age) {
        try {
            int a = Integer.parseInt(age.trim());
            return a > 0 && a < 150;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidPrice(String price) {
        try {
            double p = Double.parseDouble(price.trim());
            return p >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidQuantity(String qty) {
        try {
            int q = Integer.parseInt(qty.trim());
            return q >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidExperience(String exp) {
        try {
            int e = Integer.parseInt(exp.trim());
            return e >= 0 && e <= 60;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidDate(String date) {
        return date != null && date.matches("^\\d{4}-\\d{2}-\\d{2}$");
    }

    public static boolean isValidTime(String time) {
        return time != null && time.matches("^\\d{2}:\\d{2}$");
    }

    public static boolean isPasswordStrong(String password) {
        return password != null && password.length() >= 6;
    }
}
