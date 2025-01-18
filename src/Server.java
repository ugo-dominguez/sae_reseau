import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;


public class Server {
    private ServerSocket serverSocket;

    public Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("Serveur démarré sur le port : " + port);
    }

    public void start() {
        while (true) {
            try {
                Socket client = serverSocket.accept();
                System.out.println("Client connecté : " + client.getInetAddress());

                BufferedReader lobbyReader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                String username = lobbyReader.readLine();
                System.out.println("Nom d'utilisateur du client : " + username);

                switch (lobbyReader.readLine()) {
                    case "connect":
                        // demander une partie à un autre client
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
