package ru.flamexander.december.chat.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InMemoryUserService implements UserService {
    class User {
        private String login;
        private String password;
        private String username;
        private String userRole;



        public User(String login, String password, String username, String userRole) {
            this.login = login;
            this.password = password;
            this.username = username;
            this.userRole = userRole;
        }
    }

    private List<User> users;

    public InMemoryUserService() {
        this.users = new ArrayList<>(Arrays.asList(
                new User("admin", "admin", "admin", "admin"),
                new User("login2", "pass2", "user2", "user"),
                new User("login3", "pass3", "user3", "user")
        ));
    }


    public String getUserRoleFromMemory(String login){
        for (User u : users) {
            if (u.login.equals(login)) {
                return u.userRole;
            }
        }
        return null;
    }

    @Override
    public String getUsernameByLoginAndPassword(String login, String password) {
        for (User u : users) {
            if (u.login.equals(login) && u.password.equals(password)) {
                return u.username;
            }
        }
        return null;
    }

    @Override
    public void createNewUser(String login, String password, String username) {
        users.add(new User(login, password, username, "user"));
    }

    @Override
    public boolean isLoginAlreadyExist(String login) {
        for (User u : users) {
            if (u.login.equals(login)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isUsernameAlreadyExist(String username) {
        for (User u : users) {
            if (u.username.equals(username)) {
                return true;
            }
        }
        return false;
    }
}
