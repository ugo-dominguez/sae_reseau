import java.util.List;
import java.util.ArrayList;


public class Grille {
    static final int NB_LIGNES = 6;
    static final int NB_COLONNES = 7;
    static final int WIN_CONDITION = 4;
    
    private List<List<Case>> matrice;

    public Grille() {
        this.matrice = new ArrayList<>(NB_LIGNES);

        for (int i = 0; i < NB_LIGNES; i++) {
            List<Case> ligne = new ArrayList<>(NB_COLONNES);
            for (int j = 0; j < NB_COLONNES; j++) {
                ligne.add(new Case());
            }
            this.matrice.add(ligne);
        }
    }

    public Case getCase(int ligne, int colonne) {
        return this.matrice.get(ligne).get(colonne);
    }

    public Etat getEtatCase(int ligne, int colonne) {
        return this.getCase(ligne, colonne).getEtat();
    }

    public void setCase(int ligne, int colonne, Case newCase) {
        this.matrice.get(ligne).set(colonne, newCase);
    }

    public int getTailleColonne(int colonne) {
        int cpt = 0;
        for (int i = 0; i < NB_LIGNES; i++) {
            if (!(this.getEtatCase(i, colonne).equals(Etat.VIDE))) {
                cpt++;
            } 
        }
        return cpt;
    }

    public boolean colonneValide(int colonne) {
        return colonne >= 0 && colonne < NB_COLONNES && this.getTailleColonne(colonne) < NB_LIGNES;
    }

    public boolean ajouterPiece(Etat joueur, int colonne) {
        if (!(colonneValide(colonne))) return false;
        int tailleColonne = this.getTailleColonne(colonne);
        this.setCase(tailleColonne, colonne, new Case(joueur));
        return true;
    }

    public boolean estPleine() {
        int cpt = 0;
        for (int colonne = 0; colonne < NB_COLONNES; colonne++) {
            if (!(this.colonneValide(colonne))) {
                cpt++;
            }
        }
        return (cpt == NB_COLONNES);
    }

    public boolean verifierAlignement(Etat joueur) {
        for (int ligne = 0; ligne < NB_LIGNES; ligne++) {
            if (this.verifierVertical(ligne, joueur)) {
                return true;
            }
        }
    
        for (int colonne = 0; colonne < NB_COLONNES; colonne++) {
            if (this.verifierHorizontal(colonne, joueur)) {
                return true;
            }
        }
    
        for (int i = 0; i < NB_LIGNES; i++) {
            if (this.verifierDiagonaleDesc(i, 0, joueur)) return true;
            if (this.verifierDiagonaleAsc(i, 0, joueur)) return true;
        }
        
        for (int j = 1; j < NB_COLONNES; j++) {
            if (this.verifierDiagonaleDesc(0, j, joueur)) return true;
            if (this.verifierDiagonaleAsc(NB_LIGNES - 1, j, joueur)) return true;
        }
    
        return false;
    }

    private boolean verifierVertical(int ligne, Etat joueur) {
        int cpt = 0;
        for (int i = 0; i < NB_COLONNES; i++) {
            if (this.getEtatCase(ligne, i).equals(joueur)) {
                cpt++;
                if (cpt == WIN_CONDITION) return true;
            } else {
                cpt = 0;
            }
        }
        return false;
    }
    
    private boolean verifierHorizontal(int colonne, Etat joueur) {
        int cpt = 0;
        for (int i = 0; i < NB_LIGNES; i++) {
            if (this.getEtatCase(i, colonne).equals(joueur)) {
                cpt++;
                if (cpt == WIN_CONDITION) return true;
            } else {
                cpt = 0;
            }
        }
        return false;
    }

    private boolean verifierDiagonaleDesc(int ligne, int colonne, Etat joueur) {
        int cpt = 0;
        while (ligne < NB_LIGNES && colonne < NB_COLONNES) {
            if (this.getEtatCase(ligne, colonne).equals(joueur)) {
                cpt++;
                if (cpt == WIN_CONDITION) return true;
            } else {
                cpt = 0;
            }
            ligne++;
            colonne++;
        }
        return false;
    }

    private boolean verifierDiagonaleAsc(int ligne, int colonne, Etat joueur) {
        int count = 0;
        while (ligne >= 0 && colonne < NB_COLONNES) {
            if (this.getEtatCase(ligne, colonne).equals(joueur)) {
                count++;
                if (count == WIN_CONDITION) return true;
            } else {
                count = 0;
            }
            ligne--;
            colonne++;
        }
        return false;
    }

    @Override
    public String toString() {
        String res = "1 2 3 4 5 6 7\n";

        for (int i = matrice.size() - 1; i >= 0; i--) {
            List<Case> ligne = matrice.get(i);
            for (Case c : ligne) {
                res += c + " ";
            }
            res += "\n";
        }
        return res;
    }
}