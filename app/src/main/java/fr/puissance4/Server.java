
package fr.puissance4;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;


public class Server {
    static final String INVALID_USERNAME = "ERR : Ce joueur n'existe pas, ou ce nom d'utilisateur est incorrect !";

    private HashMap<String, Socket> joueurs;
    private ServerSocket serverSocket;

    public Server(int port) throws IOException {
        this.joueurs = new HashMap<>();
        serverSocket = new ServerSocket(port);
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

            new Thread(() -> {
                try {
                    game.start();
                } catch (Exception e) {
                    System.out.println("ERR Jeu : " + e.getMessage());
                }
            }).start();
        } else {this.getWriter(client).println(INVALID_USERNAME);}
    }

    public void displayClients(String username) throws Exception {
        Socket client = this.joueurs.get(username);
        PrintWriter writer = this.getWriter(client);
        writer.println("Joueurs connectés :");
        for (String player : this.joueurs.keySet()) {
            if (player != username) writer.println("- " + player);
        }
        writer.println(""); 
    }

    public void checkCommands(String username, String command) throws Exception {
        String[] parts = command.split(" ");

        switch (parts[0].toLowerCase()) {
            case "connect":
                if (parts.length > 1) {
                    String requestedUsername = parts[1].toLowerCase();
                    this.connect(this.joueurs.get(username), username, requestedUsername);
                }
                break;
            
            case "career":
                // afficher l'historique
                break;

            default:
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
            System.out.println("Client connecté : " + client.getInetAddress());

            BufferedReader lobbyReader = this.getReader(client);

            String username = lobbyReader.readLine().toLowerCase();
            System.out.println("Nom d'utilisateur du client : " + username);

            this.joueurs.put(username, client);
            this.displayClients(username);

            String command;
            while ((command = lobbyReader.readLine()) != null) {
                this.checkCommands(username, command);
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
