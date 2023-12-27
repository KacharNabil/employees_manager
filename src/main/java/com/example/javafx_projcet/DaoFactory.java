package com.example.javafx_projcet;
import java.sql.*;
import java.util.*;

public class DaoFactory {
    private static Connection connection =null;
    public static Connection connnect_to_db() {
        String URL="jdbc:postgresql://localhost:5432/javaFX";
        String username ="postgres";
        String passowrd = "nabil123";
        try {
            connection = DriverManager.getConnection(URL,username,passowrd);
            System.out.println("database connection is established");

        }catch (SQLException e){
            System.out.println(e.getMessage());

        }
        return connection;
    }
}
