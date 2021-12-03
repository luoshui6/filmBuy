package com.example.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DBUtil {
    public static Connection getConn() {
        String driver = "com.mysql.cj.jdbc.Driver";
        String url = "jdbc:mysql://106.55.173.219:13306/b_filmBuy?useSSL=false&useUnicode=true&characterEncoding=UTF8&serverTimezone=Asia/Shanghai";
        String username = "root";
        String password = "root";
        Connection conn = null;
        try {
            Class.forName(driver); //classLoader,加载对应驱动
            conn =DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }
}
