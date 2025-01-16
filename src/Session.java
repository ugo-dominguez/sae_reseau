import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

class Session extends Thread {
    private Socket client1;
    private Socket client2;

    public Session(Socket client1, Socket client2) {
        this.client1 = client1;
        this.client2 = client2;
    }

    @Override
    public void run() {
        try (
            BufferedReader reader1 = new BufferedReader(new InputStreamReader(client1.getInputStream()));
            PrintWriter writer1 = new PrintWriter(client1.getOutputStream(), true);
            BufferedReader reader2 = new BufferedReader(new InputStreamReader(client2.getInputStream()));
            PrintWriter writer2 = new PrintWriter(client2.getOutputStream(), true)
        ) {
            writer1.println("Vous êtes connecté au serveur.");
            writer2.println("Vous êtes connecté au serveur.");

            String message1, message2;
            while (true) {
                if ((message1 = reader1.readLine()) != null) {
                    writer2.println("Client 1 : " + message1);
                }
                if ((message2 = reader2.readLine()) != null) {
                    writer1.println("Client 2 : " + message2);
                }
            }
        } catch (IOException e) {
            System.out.println("Erreur dans la session : " + e.getMessage());
        } finally {
            try {
                client1.close();
                client2.close();
            } catch (IOException e) {
                System.out.println("Erreur lors de la fermeture des connexions client : " + e.getMessage());
            }
        }
    }
}
