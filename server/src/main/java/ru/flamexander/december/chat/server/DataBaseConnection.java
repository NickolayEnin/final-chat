package ru.flamexander.december.chat.server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataBaseConnection {
    private static final String DATABASE_URL = "jdbc:postgresql://localhost:5432/postgres";

    private static final String SELECT_USERS_SQL = "select u.login ,u.\"password\" ,u.\"name\"  from users u;";
    private static final String SELECT_ROLES_FOR_USERS = "select *  from users_roles ur left join \"role\" r on r.id = ur.role_id  where user_login = ?";

    public List<InMemoryUserService.User> getUser() {
        String login = null;
        String password = null;
        String name = null;
        String role = null;
        List<InMemoryUserService.User> users1 = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, "postgres", "")) {
            try (Statement statement = connection.createStatement()) {
                try (ResultSet resultSet = statement.executeQuery(SELECT_USERS_SQL)) {
                    while (resultSet.next()) {
                        login = resultSet.getString(1);
                        password = resultSet.getString(2);
                        name = resultSet.getString(3);
                        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ROLES_FOR_USERS)) {
                            preparedStatement.setString(1, login);
                            try (ResultSet rs = preparedStatement.executeQuery()) {
                                while (rs.next()) {
                                    role = rs.getString(4);
                                    users1.add(new InMemoryUserService.User(login,password,name,role));
                                }
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users1;
    }
}
