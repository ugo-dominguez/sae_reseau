import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;


public class Server {
    private HashMap<String, Socket> joueurs;
    private ServerSocket serverSocket;

    public Server(int port) throws IOException {
        this.joueurs = new HashMap<>();
        serverSocket = new ServerSocket(port);
        System.out.println("Serveur démarré sur le port : " + port);
    }

    public void start() {
        while (true) {
            try {
                Socket client = serverSocket.accept();
                System.out.println("Client connecté : " + client.getInetAddress());

                BufferedReader lobbyReader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                PrintWriter lobbyWriter = new PrintWriter(client.getOutputStream(), true);

                String username = lobbyReader.readLine().toLowerCase();
                System.out.println("Nom d'utilisateur du client : " + username);

                this.joueurs.put(username, client);

                String command = lobbyReader.readLine();
                String[] parts = command.split(" ");

                switch (parts[0].toLowerCase()) {
                    case "connect":
                        if (parts.length > 1) {
                            String connectUsername = parts[1].toLowerCase();

                            if (Client.validUsername(username) && this.joueurs.containsKey(connectUsername)) {
                                System.out.println("Demande de partie à " + connectUsername);
                            } else {lobbyWriter.println("Ce joueur n'existe pas, ou ce nom d'utilisateur est incorrect !");}
                        }
                        break;
                    
                    case "career":
                        // afficher l'historique
                        break;

                    default:
                        break;
                }

            } catch (IOException e) {
                System.out.println("Erreur lors de l'acceptation de la connexion : " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        try {
            Server server = new Server(55555);
            server.start();

        } catch (IOException e) {
            System.out.println("Erreur serveur : " + e.getMessage());
        }
    }
}
