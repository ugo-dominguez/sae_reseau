import java.io.IOException;
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
                Socket client1 = serverSocket.accept();
                System.out.println("Client 1 connecté : " + client1.getInetAddress());

                Socket client2 = serverSocket.accept();
                System.out.println("Client 2 connecté : " + client2.getInetAddress());

                Partie partie = new Partie(client1, client2);
                partie.start();
                break;
            } catch (IOException e) {
                System.out.println("Erreur lors de l'acceptation de la connexion : " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        try {
            Server server = new Server(12345);
            server.start();
        } catch (IOException e) {
            System.out.println("Erreur serveur : " + e.getMessage());
        }
    }
}
