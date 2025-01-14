import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;


public class Partie {
    static final String ASKCOLUMN = "Dans quelle colonne voulez-vous placer votre pièce ? : ";
    static final String INVALIDCOLUMN = "Cette colonne n'est pas valide !";

    private Grille grille;
    private HashMap<Etat, String> joueurs;
    private Scanner scanner;

    public Partie(Grille grille) {
        this.grille = grille;
        this.joueurs = new HashMap<>();
        this.joueurs.put(Etat.P1, "Joueur 1");
        this.joueurs.put(Etat.P2, "Joueur 2");
        this.scanner = new Scanner(System.in);
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
            case Etat.P1:
                return 'O';

            case Etat.P2:
                return 'X';

            default:
                return '■';
        }
    }

    public void demanderNoms() {
        System.out.println("Entrez un nom pour le Joueur 1 : ");
        this.setNomP1(this.scanner.nextLine());

        System.out.println("Entrez un nom pour le Joueur 2 : ");
        this.setNomP2(this.scanner.nextLine());
    }

    public Etat getInitiateur() {
        Random random = new Random();
        
        int tirage = random.nextInt(2);
        return (tirage == 0) ? Etat.P1 : Etat.P2;
    }

    public Etat prochainJoueur(Etat joueur) {
        switch (joueur) {
            case Etat.P1:
                return Etat.P2;
        
            case Etat.P2:
                return Etat.P1;

            default:
                return this.getInitiateur();
        }
    }

    public int demanderColonne(Etat joueur) {
        int colChoisie = -1;
        while (true) {
            try {
                System.out.println(ASKCOLUMN);
                colChoisie = scanner.nextInt();
                if (!(this.grille.colonneValide(colChoisie - 1))) {
                    throw new InvalidColumnException();
                }
                break;
            } catch (InputMismatchException e) {
                System.out.println(INVALIDCOLUMN);
                scanner.next();
            } catch (InvalidColumnException e) {
                System.out.println(INVALIDCOLUMN);
            }
        }
        return colChoisie;
    }

    public void start() {
        this.demanderNoms();
        Etat joueurActuel = this.getInitiateur();
        Etat joueurGagnant = null;

        while (true) {
            System.out.println(grille);
            
            String nom = this.getNomJoueur(joueurActuel);
            char symbole = this.getSymboleJoueur(joueurActuel);
            System.out.println("Au tour de " + nom + " (" + symbole + ")");

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

        System.out.println(grille);
        if (joueurGagnant != null) {
            System.out.println("Félicitations ! " + this.getNomJoueur(joueurGagnant) + " a gagné !");
        } else {
            System.out.println("La partie est terminée, Égalité !.");
        }
    }
}
