package fr.puissance4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.InputMismatchException;
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

    public Partie(Socket client1, Socket client2) {
        this.joueurs = new HashMap<>();
        this.joueurs.put(Etat.P1, "Joueur 1");
        this.joueurs.put(Etat.P2, "Joueur 2");

        this.client1 = client1;
        this.client2 = client2;
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

    public void setNomP1(String newNom) {
        this.joueurs.put(Etat.P1, newNom);
    }

    public void setNomP2(String newNom) {
        this.joueurs.put(Etat.P2, newNom);
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

    public Etat getInitiateur() {
        Random random = new Random();
        
        int tirage = random.nextInt(2);
        return (tirage == 0) ? Etat.P1 : Etat.P2;
    }

    public Etat prochainJoueur(Etat joueur) {
        switch (joueur) {
            case P1:
                return Etat.P2;
        
            case P2:
                return Etat.P1;

            default:
                return this.getInitiateur();
        }
    }

    public int demanderColonne(Etat joueur) throws IOException, InvalidColumnException {
        int colChoisie = -1;
        BufferedReader currentReader;
        PrintWriter currentWriter;
        if (joueur.equals(Etat.P1)) {
            currentReader = this.readerJ1;
            currentWriter = this.writerJ1;
        } else {
            currentReader = this.readerJ2;
            currentWriter = this.writerJ2;
        }
        
        while (true) {
            try {
                currentWriter.println(ASKCOLUMN);
                colChoisie = Integer.valueOf(currentReader.readLine());

                if (!(this.grille.colonneValide(colChoisie - 1))) {
                    throw new InvalidColumnException();
                }
                break;
            } catch (InputMismatchException e) {
                currentWriter.println(INVALIDCOLUMN);
            }
        }
        return colChoisie;
    }

    public void startGame() throws IOException, InvalidColumnException {
        this.writerJ1.println("Début de la partie");
        this.writerJ2.println("Début de la partie");
        
        Etat joueurActuel = this.getInitiateur();
        Etat joueurGagnant = null;

        while (true) {
            writerJ1.println(grille.toString());
            writerJ2.println(grille.toString());
            
            String nom = this.getNomJoueur(joueurActuel);
            char symbole = this.getSymboleJoueur(joueurActuel);
            
            this.writerJ1.println("Au tour de " + nom + " (" + symbole + ")");
            this.writerJ2.println("Au tour de " + nom + " (" + symbole + ")");

            int indiceCol = demanderColonne(joueurActuel);
            this.grille.ajouterPiece(joueurActuel, indiceCol - 1);

            if (this.grille.verifierAlignement(joueurActuel)) {
                joueurGagnant = joueurActuel;
                break;
            } 

            if (this.grille.estPleine()) {
                joueurGagnant = null;
                break;
            }

            joueurActuel = this.prochainJoueur(joueurActuel);
        }

        this.writerJ1.println(grille.toString());
        this.writerJ2.println(grille.toString());
        if (joueurGagnant != null) {
            this.writerJ1.println("Félicitations ! " + this.getNomJoueur(joueurGagnant) + " a gagné !");
            this.writerJ2.println("Félicitations ! " + this.getNomJoueur(joueurGagnant) + " a gagné !");
        } else {
            this.writerJ1.println("La partie est terminée, Égalité !.");
            this.writerJ2.println("La partie est terminée, Égalité !.");
        }
    }

    @Override
    public void run() {
        try {
            Grille grille = new Grille();
            this.setGrille(grille);

            this.readerJ1 = new BufferedReader(new InputStreamReader(this.client1.getInputStream()));
            this.writerJ1 = new PrintWriter(this.client1.getOutputStream(), true);
            
            this.readerJ2 = new BufferedReader(new InputStreamReader(this.client2.getInputStream()));
            this.writerJ2 = new PrintWriter(this.client2.getOutputStream(), true);
            
            this.startGame();
        } catch (IOException e) {
            System.out.println(e);
        } catch (InvalidColumnException e) {
            System.out.println(e);
        }
    }
}
