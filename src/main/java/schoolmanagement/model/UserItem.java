package schoolmanagement.model;

public class UserItem {
    public final User user;

    public UserItem(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return user.fullName() + " (" + user.idNo + ")";
    }
}
