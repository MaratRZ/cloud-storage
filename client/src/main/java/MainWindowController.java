import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import lombok.extern.slf4j.Slf4j;
import model.FileMessage;
import model.SimpleMessage;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.util.ResourceBundle;

import static model.MessageType.SIMPLE;

@Slf4j
public class MainWindowController implements Initializable {
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
    private ObjectEncoderOutputStream os;
    private ObjectDecoderInputStream is;
    private NettyNetwork network;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fileProperties = FXCollections.observableArrayList();
        fileNameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        fileSizeColumn.setCellValueFactory(cellData -> cellData.getValue().sizeProperty().asObject());
        fileSizeColumn.setStyle("-fx-alignment: CENTER-RIGHT");
        fillFileList(ROOT_DIR);
        openConnection(HOST, PORT);
    }

    private void openConnection(String host, int port) {
        network = new NettyNetwork(message -> {
            log.info("message received: {}", message);
            if (message.messageType() == SIMPLE) {
                SimpleMessage simpleMsg = (SimpleMessage) message;
                Platform.runLater(() -> statusBar.setText(simpleMsg.getMessage()));
            }
        }, host, port);
    }

    private void fillFileList(String pathName) {
        try {
            File dir = new File(pathName);
            FileFilter fileFilter = File::isFile;
            this.files = dir.listFiles(fileFilter);
            for (File file : files) {
                fileProperties.add(new FileProperty(file.getName(), Files.size(file.toPath())));
            }
            fileTableView.setItems(fileProperties);
        } catch (Exception e) {
            log.error("", e);
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

    public void sendFile(ActionEvent event) {
        try {
            File file = getSelectedFile();
            network.writeMessage(new FileMessage(file.toPath()));
            statusBar.setText("Файл " + file.getName() + " отправлен");
        } catch (Exception e) {
            log.error("", e);
            showAlert("Ошибка отправки", e.getMessage());
        }
    }
}
