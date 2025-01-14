class Case {
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
            case Etat.P1:
                res = "O";
                break;

            case Etat.P2:
                res = "X";
                break;

            case Etat.VIDE:
                res = "■";
                break;
        }
        return res;
    }
}
