package thin.blog.ibts;

import com.google.gson.Gson;

/**
 * Created by jmprathab on 31/03/16.
 */
public class User {
    int userId;
    double balance;
    String name, mobile, email, address, password;

    public User() {
    }

    public User(int userId, double balance, String name, String mobile, String email, String address, String password) {
        this.userId = userId;
        this.name = name;
        this.password = password;
        this.balance = balance;
        this.mobile = mobile;
        this.email = email;
        this.address = address;
    }

    public static String getUserJson(User user) {
        return new Gson().toJson(user);
    }

    public static User getUserObject(String json) {
        return new Gson().fromJson(json, User.class);
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        if (this.name == null || this.name.equals("-")) {
            return "-";
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        if (this.mobile == null || this.mobile.equals("-")) {
            return "-";
        }
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        if (this.email == null || this.email.equals("-")) {
            return "-";
        }
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        if (this.address == null || this.address.equals("-")) {
            return "-";
        }
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPassword() {
        if (this.password == null || this.password.equals("-")) {
            return "-";
        }
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;

        User user = (User) o;

        return userId == user.userId;

    }

    @Override
    public int hashCode() {
        return userId;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", balance=" + balance +
                ", name='" + name + '\'' +
                ", mobile='" + mobile + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
