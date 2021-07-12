import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.ResourceBundle;

public class FileManagerController implements Initializable {
    @FXML
    private TableView<FileProperty> fileTableView;
    @FXML
    private TableColumn<FileProperty, String> fileNameColumn;
    @FXML
    private TableColumn<FileProperty, Long> fileSizeColumn;
    private ObservableList<FileProperty> fileProperties;
    @FXML
    private Label statusBar;
    private File[] files;

    private static final int PORT = 8189;
    private static String ROOT_DIR = "client/files";
    private static String HOST = "localhost";
    private Socket socket;
    private DataInputStream is;
    private DataOutputStream os;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fileProperties = FXCollections.observableArrayList();
        fileNameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        fileSizeColumn.setCellValueFactory(cellData -> cellData.getValue().sizeProperty().asObject());
        fileSizeColumn.setStyle("-fx-alignment: CENTER-RIGHT");
        fillFileList(ROOT_DIR);
        openConnection(HOST, PORT);
        startListener();
    }

    private void openConnection(String host, int port) {
        try {
            socket = new Socket(host, port);
            is = new DataInputStream(socket.getInputStream());
            os = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Ошибка подключения", "Сервер недоступен");
            System.exit(0);
        }
    }

    private void startListener() {
        Thread readThread = new Thread(() -> {
            try {
                while (true) {
                    String msg = is.readUTF();
                    Platform.runLater(() -> statusBar.setText(msg));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        readThread.setDaemon(true);
        readThread.start();
    }

    private void fillFileList(String pathName) {
        try {
            File dir = new File(pathName);
            FileFilter fileFilter = pathname -> pathname.isFile();
            this.files = dir.listFiles(fileFilter);
            for (File file : files) {
                fileProperties.add(new FileProperty(file.getName(), Files.size(file.toPath())));
            }
            fileTableView.setItems(fileProperties);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private File getSelectedFile() throws Exception {
        File result = null;
        if (fileTableView.getItems().isEmpty()) {
            throw new Exception("Отсутствуют файлы");
        } else if (fileTableView.getSelectionModel().getSelectedCells().isEmpty()) {
            throw new Exception("Файл не выбран");
        } else {
            TablePosition pos = fileTableView.getSelectionModel().getSelectedCells().get(0);
            int row = pos.getRow();
            FileProperty fileProperty = fileTableView.getItems().get(row);
            TableColumn col = pos.getTableColumn();
            String fileName = (String) col.getCellObservableValue(fileProperty).getValue();
            for (File file : files) {
                if (file.getName().equals(fileName)) {
                    result = file;
                }
            }
        }
        return result;
    }

    private void showAlert(String title, String headerText) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.showAndWait();
    }

    public void sendFile(ActionEvent event) throws IOException {
        File file = null;
        try {
            file = getSelectedFile();
        } catch (Exception e) {
            showAlert("Ошибка отправки", e.getMessage());
            return;
        }
        statusBar.setText(file.getName());
        os.writeUTF(file.getName());
        os.writeLong(Files.size(file.toPath()));
        Files.copy(file.toPath(), os);
        os.flush();
        statusBar.setText("Файл " + file.getName() + " отправлен");
    }
}
