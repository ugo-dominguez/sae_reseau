import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


class Client {
    static final String ASKIP = "Entrez l'IP de la machine du serveur de Jeu : ";
    static final String ASKUSERNAME = "Entrez votre nom d'utilisateur :  ";

    private String username;
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;

    public Client(String username, String host, int port) throws IOException {
        this.username = username;
        this.socket = new Socket(host, port);
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.writer = new PrintWriter(socket.getOutputStream(), true);
        this.writer.println(this.username);
        System.out.println("Connecté au serveur sur " + host + ":" + port);
    }

    public String getUsername() {
        return this.username;
    }

    public void start() {
        Thread readThread = new Thread(() -> {
            try {
                String serverMessage;
                while ((serverMessage = this.reader.readLine()) != null) {
                    System.out.println(serverMessage);
                }

            } catch (IOException e) {
                System.out.println("Erreur lors de la lecture depuis le serveur : " + e.getMessage());
            }
        });

        readThread.start();

        try (BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in))) {
            String userInput;
            while ((userInput = consoleReader.readLine()) != null) {
                this.writer.println(userInput);
            }

        } catch (IOException e) {
            System.out.println("Erreur lors de l'envoi au serveur : " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            BufferedReader preScanner = new BufferedReader(new InputStreamReader(System.in));

            System.out.println(ASKIP);
            String host = preScanner.readLine();

            System.out.println(ASKUSERNAME);
            String username = preScanner.readLine();

            Client client = new Client(username, host, 55555);
            client.start();

        } catch (IOException e) {
            System.out.println("Erreur client : " + e.getMessage());
        }
    }
}
