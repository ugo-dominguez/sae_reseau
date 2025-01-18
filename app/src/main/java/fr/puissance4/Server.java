
package fr.puissance4;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class Server {
    static final String INVALID_USERNAME = "ERR : Ce joueur n'existe pas, ou ce nom d'utilisateur est incorrect !";

    private HashMap<String, Socket> joueurs;
    private Set<String> joueursAuMenu;
    private ServerSocket serverSocket;

    private Database database;

    public Server(int port) throws IOException {
        this.joueurs = new HashMap<>();
        this.serverSocket = new ServerSocket(port);
        this.database = Database.getDatabase();
        this.joueursAuMenu = new HashSet<>();
        System.out.println("Serveur démarré sur le port : " + port);
    }

    public BufferedReader getReader(Socket client) throws Exception {
        return new BufferedReader(new InputStreamReader(client.getInputStream()));
    }

    public PrintWriter getWriter(Socket client) throws Exception {
        return new PrintWriter(client.getOutputStream(), true);
    }

    public void connect(Socket client, String username, String requested) throws Exception {
        if (Client.validUsername(username) && this.joueurs.containsKey(username) && !(username.equals(requested))) {
            Partie game = new Partie(this.joueurs.get(username), this.joueurs.get(requested), username, requested);
            
            joueursAuMenu.remove(username);
            joueursAuMenu.remove(requested);

            new Thread(() -> {
                try {
                    game.start();
                } catch (Exception e) {
                    System.out.println("ERR Jeu : " + e.getMessage());
                }
            }).start();
        } else {this.getWriter(client).println(INVALID_USERNAME);}
    }

    public void showClients(String username) throws Exception {
        Socket client = this.joueurs.get(username);
        PrintWriter writer = this.getWriter(client);
        writer.println("Joueurs connectés :");
        for (String player : this.joueurs.keySet()) {
            if (player != username) writer.println("- " + player);
        }
        writer.println(""); 
    }

    public void showHelp(String username) throws Exception {
        sendMessage(username, """
            Liste des commandes :
            * help: Pour afficher ce message.
            * career: Pour afficher votre historique de parties.
            * ls: Pour afficher la liste des joueurs.
            * connect <username>: Pour lancer une partie avec un joueur.
            """);
    }

    public void showCareer(String username) throws Exception {
        List<TablePartie> parties = database.getPartieJoueur(username);

        if (parties.isEmpty()) {
            sendMessage(username, "Aucune partie enregistrée.");
        }

        for (TablePartie partie : parties) {
            sendMessage(username, "* " + partie.getJoueur1() + " VS " + partie.getJoueur2() + " : " + ((partie.getGagnant() == username) ? " Gagné" : " Perdu") );
        }
    }

    public void sendMessage(String username, String message) throws Exception {
        getWriter(this.joueurs.get(username)).println(message);
    }

    public void checkCommands(String username, String command) throws Exception {
        String[] parts = command.split(" ");

        switch (parts[0].toLowerCase()) {
            case "connect":
                if (parts.length > 1 && this.joueurs.containsKey(parts[1].toLowerCase())) {
                    String requestedUsername = parts[1].toLowerCase();
                    this.connect(this.joueurs.get(username), username, requestedUsername);
                } else {
                    sendMessage(username, "Nom d'utilisateur invalide.");
                }
                break;
            
            case "help":
                showHelp(username);
                break;
            
            case "ls":
                showClients(username);

            case "career":
                showCareer(username);
                break;

            default:
                sendMessage(username, "Commande inconnue");
                break;
        }
    }

    public void start() {
        while (true) {
            try {
                Socket client = serverSocket.accept();
                new Thread(() -> handleClient(client)).start();

            } catch (Exception e) {
                System.out.println("ERR : " + e.getMessage());
            }
        }
    }

    private void handleClient(Socket client) {
        try {
            BufferedReader lobbyReader = this.getReader(client);
            
            String username = lobbyReader.readLine().toLowerCase();
            System.out.println("Nom d'utilisateur du client : " + username);
            
            if (joueurs.containsKey(username)) {
                PrintWriter writer = this.getWriter(client);
                writer.println("Joueur déjà connecté avec ce nom. Veuillez relancer votre client.");
                return;
            }

            System.out.println("Client connecté : " + client.getInetAddress());
            
            this.joueurs.put(username, client);
            this.joueursAuMenu.add(username);
            this.showHelp(username);

            String command;
            while (lobbyReader != null) {
                while (joueursAuMenu.contains(username)) {
                    command = lobbyReader.readLine();
                    this.checkCommands(username, command);
                }
            }
        } catch (Exception e) {
            System.out.println("ERR : " + e.getMessage());
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                System.out.println("ERR lors de la fermeture du client : " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        try {
            Server server = new Server(55555);
            server.start();

        } catch (IOException e) {
            System.out.println("ERR serveur : " + e.getMessage());
        }
    }
}
