import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ClientHandler implements Runnable {

    private static String ROOT_DIR = "server/files";
    private static final int BUFFER_SIZE = 512;
    private byte[] buffer;
    private Socket socket;
    private DataInputStream is;
    private DataOutputStream os;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        this.buffer = new byte[BUFFER_SIZE];
    }

    @Override
    public void run() {
        try {
            is = new DataInputStream(socket.getInputStream());
            os = new DataOutputStream(socket.getOutputStream());
            while (true) {
                readFileMessage();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void readFileMessage() throws IOException {
        String fileName = is.readUTF();
        System.out.println("Получен файл " + fileName);
        long fileSize = is.readLong();
        System.out.println("Размер файла " + fileSize);
        Path file = Paths.get(ROOT_DIR, fileName);
        try(FileOutputStream fos = new FileOutputStream(ROOT_DIR + "/" + fileName)) {
            for (int i = 0; i < (fileSize + BUFFER_SIZE - 1) / BUFFER_SIZE; i++) {
                int read = is.read(buffer);
                fos.write(buffer, 0, read);
            }
            fos.flush();
        }
        os.writeUTF("Файл " + fileName + " загружен на сервер");
    }
}
