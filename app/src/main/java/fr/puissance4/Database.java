package fr.puissance4;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

public class Database {
    public Database() {
        // Définir le chemin de la base de données
        String dbPath = "data/db.sqlite3";

        // Créer le dossier 'data' s'il n'existe pas
        File dbDir = new File("data");
        if (!dbDir.exists()) {
            dbDir.mkdirs();
            System.out.println("Dossier 'data' créé.");
        }

        String url = "jdbc:sqlite:" + dbPath;

        // Connexion et création de la base
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                System.out.println("Connexion établie avec SQLite.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
