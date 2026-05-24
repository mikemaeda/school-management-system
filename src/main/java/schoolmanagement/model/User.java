package schoolmanagement.model;

public class User {
    public final int id;
    public final String firstName;
    public final String lastName;
    public final String idNo;
    public final String email;
    public final String role;
    public final boolean passwordChanged;

    public User(int id, String firstName, String lastName, String idNo, String email, String role, boolean passwordChanged) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.idNo = idNo;
        this.email = email;
        this.role = role;
        this.passwordChanged = passwordChanged;
    }

    public String fullName() {
        return firstName + " " + lastName;
    }
}
