package fr.puissance4;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private Connection conn;
    private static Database database = null;

    private Database() {
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
        try {
            this.conn = DriverManager.getConnection(url);

            if (conn != null) {
                System.out.println("Connexion établie avec SQLite.");
                creerTables();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Database getDatabase() {
        if (Database.database == null) {
            Database.database = new Database();
        }
        
        return Database.database;
    }

    public void creerTables() {
        String query = """
            CREATE TABLE IF NOT EXISTS partie (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                joueur1 TEXT NOT NULL,
                joueur2 TEXT NOT NULL,
                gagnant TEXT
            );
        """;

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(query);
            System.out.println("Base de données prête");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insererPartie(String joueur1, String joueur2, String gagnant) {
        String query = "INSERT INTO partie (joueur1, joueur2, gagnant) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, joueur1);
            pstmt.setString(2, joueur2);
            pstmt.setString(3, gagnant);
            pstmt.executeUpdate();
            System.out.println("Partie insérée avec succès.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insererPartie(TablePartie partie) {
        insererPartie(partie.getJoueur1(), partie.getJoueur2(), partie.getGagnant());
    }

    public List<TablePartie> getPartieJoueur(String joueur) {
        String query = "SELECT * FROM partie WHERE joueur1 = ? OR joueur2 = ? OR gagnant = ?";
        List<TablePartie> parties = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, joueur);
            pstmt.setString(2, joueur);
            pstmt.setString(3, joueur);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                TablePartie partie = new TablePartie(
                        rs.getInt("id"),
                        rs.getString("joueur1"),
                        rs.getString("joueur2"),
                        rs.getString("gagnant")
                );
                parties.add(partie);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return parties;
    }
}
