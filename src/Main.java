public class Main {
    public static void main(String[] args) {
        System.out.print("\033[2J");
        System.out.flush();

        Grille grille = new Grille();
        Partie game = new Partie(grille);

        game.start();
    }
}
