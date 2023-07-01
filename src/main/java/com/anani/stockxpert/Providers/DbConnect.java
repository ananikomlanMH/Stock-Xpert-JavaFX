package com.anani.stockxpert.Providers;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DbConnect {

    private String host = "localhost";
    private String username = "root";
    private String password = "";
    private String database = "auditax-ng";
    private Connection db;

    public void DBConnect() {
        DBConnect(null, null, null, null);
    }

    public void DBConnect(String host, String username, String password, String database) {
        if (host != null) {
            this.host = host;
            this.username = username;
            this.password = password;
            this.database = database;
        }


        try {
            String url = "jdbc:mysql://" + this.host + "/" + this.database;
            this.db = DriverManager.getConnection(url, this.username, this.password);
        } catch (SQLException e) {
            System.out.println("Impossible de se connecter à la base de données");
            e.printStackTrace();
        }
    }

    public List<Object> query(String sql, Object[] data, Class<?> clazz) {
        List<Object> results = new ArrayList<>();
        try {
            PreparedStatement stmt = this.db.prepareStatement(sql);
            for (int i = 0; i < data.length; i++) {
                stmt.setObject(i + 1, data[i]);
            }
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Object obj = clazz.newInstance();
                // Mappez ici les colonnes de la table aux propriétés de l'objet obj
                results.add(obj);
            }
        } catch (SQLException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return results;
    }

    public boolean queryBuild(String sql, Object[] data) {
        try {
            PreparedStatement stmt = this.db.prepareStatement(sql);
            for (int i = 0; i < data.length; i++) {
                stmt.setObject(i + 1, data[i]);
            }
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
