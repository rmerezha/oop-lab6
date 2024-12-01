package rmerezha;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.apache.commons.math3.linear.MatrixUtils;

import java.awt.datatransfer.Clipboard;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class App extends Application {

    private static final ServerSocket SERVER_SOCKET;

    static {
        try {
            SERVER_SOCKET = new ServerSocket(3333);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        TextArea textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setWrapText(false);
        textArea.setStyle("-fx-font-size: 20px;");

        Scene scene = new Scene(textArea, 800, 800);

        primaryStage.setOnCloseRequest(event -> {
            try {
                SERVER_SOCKET.close();
                System.exit(0);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        startSocketServer(textArea);
        primaryStage.setTitle("obj3");
        primaryStage.setScene(scene);
        primaryStage.show();
//        ClipboardReader.readEvent((textMatrix) -> {
//            var matrix = MatrixManager.parseMatrix(textMatrix);
//            double determinant = MatrixManager.getDeterminant(matrix);
//            textArea.setText(determinant + "");
//        });
    }

    private void startSocketServer(TextArea textArea) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            while (!executorService.isShutdown()) {
                try {
                    Socket clientSocket = SERVER_SOCKET.accept();
                    handleClient(clientSocket, textArea);
                    clientSocket.close();
                } catch (IOException e) {
                    if (!SERVER_SOCKET.isClosed()) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void handleClient(Socket clientSocket, TextArea textArea) {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()))) {
            Platform.runLater(() -> {
                var textMatrix = ClipboardReader.read();
                if (textMatrix.isEmpty()) return;
                var matrix = MatrixManager.parseMatrix(textMatrix);
                double determinant = MatrixManager.getDeterminant(matrix);
                textArea.setText(determinant + "");
            });


            writer.write("0\n");


            } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

}
