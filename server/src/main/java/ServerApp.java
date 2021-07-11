import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerApp {
    private static final int PORT = 8189;

    public static void main(String[] args) throws IOException {
        startServer();
    }

    private static void startServer() throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);

        System.out.println("Сервер запущен..");
        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println("Client accepted...");
            new Thread(new ClientHandler(socket)).start();
        }

    }
}
