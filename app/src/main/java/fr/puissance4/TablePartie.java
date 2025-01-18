package fr.puissance4;

public class TablePartie {
    private int id;
    private String joueur1;
    private String joueur2;
    private String gagnant;

    public TablePartie(int id, String joueur1, String joueur2, String gagnant) {
        this.id = id;
        this.joueur1 = joueur1;
        this.joueur2 = joueur2;
        this.gagnant = gagnant;
    }

    public int getId() {
        return id;
    }

    public String getJoueur1() {
        return joueur1;
    }

    public String getJoueur2() {
        return joueur2;
    }

    public String getGagnant() {
        return gagnant;
    }

    @Override
    public String toString() {
        return "TablePartie{" +
                "id=" + id +
                ", joueur1='" + joueur1 + '\'' +
                ", joueur2='" + joueur2 + '\'' +
                ", gagnant='" + gagnant + '\'' +
                '}';
    }
}
