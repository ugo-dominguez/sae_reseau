import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

class Client {
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;

    public Client(String host, int port) throws IOException {
        socket = new Socket(host, port);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(socket.getOutputStream(), true);
        System.out.println("Connecté au serveur sur " + host + ":" + port);
    }

    public void start() {
        Thread readThread = new Thread(() -> {
            try {
                String serverMessage;
                while ((serverMessage = reader.readLine()) != null) {
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
                writer.println(userInput);
            }
        } catch (IOException e) {
            System.out.println("Erreur lors de l'envoi au serveur : " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            System.out.println("Entre l'IP de la machine du serveur de Jeu : ");
            BufferedReader scanner = new BufferedReader(new InputStreamReader(System.in));
            String host = scanner.readLine();

            Client client = new Client(host, 12345);
            client.start();
        } catch (IOException e) {
            System.out.println("Erreur client : " + e.getMessage());
        }
    }
}
