import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Random;

public class Partie extends Thread {
    static final String ASKCOLUMN = "Dans quelle colonne voulez-vous placer votre pièce ? : ";
    static final String INVALIDCOLUMN = "Cette colonne n'est pas valide !";

    private Grille grille;
    private HashMap<Etat, String> joueurs;

    private Socket client1;
    private Socket client2;

    private BufferedReader readerJ1;
    private BufferedReader readerJ2;

    private PrintWriter writerJ1;
    private PrintWriter writerJ2;

    private boolean gameActive;

    public Partie(Socket client1, Socket client2, String user1, String user2) throws IOException {
        this.joueurs = new HashMap<>();
        this.joueurs.put(Etat.P1, user1);
        this.joueurs.put(Etat.P2, user2);

        this.client1 = client1;
        this.client2 = client2;
        this.writerJ1 = new PrintWriter(client1.getOutputStream(), true);
        this.writerJ2 = new PrintWriter(client2.getOutputStream(), true);
        this.readerJ1 = new BufferedReader(new InputStreamReader(client1.getInputStream()));
        this.readerJ2 = new BufferedReader(new InputStreamReader(client2.getInputStream()));

        this.gameActive = true;
    }

    public void setGrille(Grille grille) {
        this.grille = grille;
    }

    public Grille getGrille() {
        return this.grille;
    }

    public String getNomJoueur(Etat joueur) {
        return this.joueurs.getOrDefault(joueur, "Inconnu");
    }

    public char getSymboleJoueur(Etat joueur) {
        switch (joueur) {
            case P1:
                return 'O';
            case P2:
                return 'X';
            default:
                return '■';
        }
    }

    public Etat prochainJoueur(Etat joueur) {
        return joueur == Etat.P1 ? Etat.P2 : Etat.P1;
    }

    private synchronized boolean placePiece(Etat joueur, int col) {
        if (grille.colonneValide(col)) {
            grille.ajouterPiece(joueur, col);
            return true;
        }
        return false;
    }

    private void handlePlayerInput(
        BufferedReader reader, PrintWriter userWriter,
        PrintWriter opponentWriter, Etat joueur) {

        try {
            while (gameActive) {
                synchronized (this) {
                    userWriter.println(grille.toString());
                    opponentWriter.println(grille.toString());
                    userWriter.println("C'est votre tour, " + getNomJoueur(joueur) + " (" + getSymboleJoueur(joueur) + ")");
                    opponentWriter.println("C'est au tour de " + getNomJoueur(joueur) + " (" + getSymboleJoueur(joueur) + ")");
                }

                userWriter.println(ASKCOLUMN);
                try {
                    int colChoisie = Integer.parseInt(reader.readLine()) - 1;

                    if (placePiece(joueur, colChoisie)) {
                        synchronized (this) {
                            if (grille.verifierAlignement(joueur)) {
                                userWriter.println("Félicitations ! Vous avez gagné !");
                                opponentWriter.println("Vous avez perdu !\n" + getNomJoueur(joueur) + " a gagné !");
                                gameActive = false;
                            } else if (grille.estPleine()) {
                                userWriter.println("La partie est terminée, Égalité !");
                                opponentWriter.println("La partie est terminée, Égalité !");
                                gameActive = false;
                            }
                        }
                        break;
                    } else {
                        userWriter.println(INVALIDCOLUMN);
                    }
                } catch (NumberFormatException e) {
                    userWriter.println("Entrée invalide. Veuillez entrer un numéro de colonne.");
                }
            }
        } catch (IOException e) {
            System.out.println("ERR de communication avec le joueur : " + e.getMessage());
            gameActive = false;
        }
    }

    @Override
    public void run() {
        try {
            Grille grille = new Grille();
            this.setGrille(grille);

            writerJ1.println("Début de la partie !");
            writerJ2.println("Début de la partie !");

            Etat joueurActuel = new Random().nextBoolean() ? Etat.P1 : Etat.P2;

            while (gameActive) {
                if (joueurActuel == Etat.P1) {
                    handlePlayerInput(readerJ1, writerJ1, writerJ2, joueurActuel);
                } else {
                    handlePlayerInput(readerJ2, writerJ2, writerJ1, joueurActuel);
                }
                joueurActuel = prochainJoueur(joueurActuel);
            }

            writerJ1.println("Fin de la partie !");
            writerJ2.println("Fin de la partie !");
        } catch (Exception e) {
            System.out.println("ERR dans le déroulement de la partie: " + e.getMessage());
        }
    }
}
