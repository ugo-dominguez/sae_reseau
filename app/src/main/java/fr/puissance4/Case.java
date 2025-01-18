package fr.puissance4;

public class Case {
    private Etat etat;

    public Case() {
        this.etat = Etat.VIDE;
    }

    public Case(Etat e) {
        this.etat = e;
    }

    public Etat getEtat() {
        return this.etat;
    }

    public void setEtat(Etat newEtat) {
        this.etat = newEtat;
    }

    @Override
    public String toString() {
        String res = "";

        switch (this.etat) {
            case P1:
                res = "O";
                break;

            case P2:
                res = "X";
                break;

            case VIDE:
                res = "■";
                break;
        }
        return res;
    }
}
