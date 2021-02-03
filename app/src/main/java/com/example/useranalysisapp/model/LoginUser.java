package com.example.useranalysisapp.model;

public class LoginUser extends User {
    private User user;

    private static LoginUser loginUser = new LoginUser();

    public static LoginUser getLoginUser() {
        return loginUser;
    }

    private LoginUser() {
        user = new User();
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
