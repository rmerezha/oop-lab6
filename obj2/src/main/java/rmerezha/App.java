package rmerezha;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class App extends Application {

    private static final ServerSocket SERVER_SOCKET;
    private static boolean isFinish = false;

    static {
        try {
            SERVER_SOCKET = new ServerSocket(2222);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        TextArea textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setWrapText(false);
        textArea.setStyle("-fx-font-size: 20px;");


        Scene scene = new Scene(textArea, 400, 400);

        primaryStage.setOnCloseRequest(event -> {
            try {
                SERVER_SOCKET.close();
                System.exit(0);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        startSocketServer(textArea);
        primaryStage.setTitle("obj2");
        primaryStage.setScene(scene);
        primaryStage.show();

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
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()))) {
            var buffer = new char[128];
            reader.read(buffer);
            var arr = new String(buffer).trim().split(" ");
            if (arr.length != 3) {
                writer.write("1\n");
                writer.flush();
                return;
            }

            Platform.runLater(() -> {
                int n = Integer.parseInt(arr[0]);
                int min = Integer.parseInt(arr[1]);
                int max = Integer.parseInt(arr[2]);
                int[][] matrix = MatrixGenerator.generateMatrix(n, min, max);
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < n; j++) {
                        sb.append(matrix[i][j]).append("\t");
                    }
                    sb.append("\n");

                }
                textArea.setText(sb.toString());
                ClipboardWriter.write(sb.toString());
                set(true);
            });

            while (!isFinish) {
                Thread.sleep(100);
            }

            writer.write("0\n");
            writer.flush();
            isFinish = false;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void set(boolean b) {
        isFinish = b;
    }
}
