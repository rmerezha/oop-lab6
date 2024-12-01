package rmerezha;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;

public class App extends Application {

    private Process proc2;
    private Process proc3;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(10);
        grid.setHgap(10);

        Label nLabel = new Label("n:");
        TextField nField = new TextField();
        nLabel.setStyle("-fx-font-size: 20px;");
        nField.setStyle("-fx-font-size: 20px;");
        nField.setPromptText("Введіть n");

        Label minLabel = new Label("Min:");
        TextField minField = new TextField();
        minLabel.setStyle("-fx-font-size: 20px;");
        minField.setStyle("-fx-font-size: 20px;");
        minField.setPromptText("Введіть Min");

        Label maxLabel = new Label("Max:");
        TextField maxField = new TextField();
        maxLabel.setStyle("-fx-font-size: 20px;");
        maxField.setStyle("-fx-font-size: 20px;");
        maxField.setPromptText("Введіть Max");

        grid.add(nLabel, 0, 0);
        grid.add(nField, 1, 0);
        grid.add(minLabel, 0, 1);
        grid.add(minField, 1, 1);
        grid.add(maxLabel, 0, 2);
        grid.add(maxField, 1, 2);

        Button processButton = new Button("Обробити");
        grid.add(processButton, 1, 3);

        Label resultLabel = new Label();

        VBox vbox = new VBox(10, grid, resultLabel);
        vbox.setPadding(new Insets(10));

        processButton.setOnAction(event -> {
            if (nField.getText().isEmpty() || minField.getText().isEmpty() || maxField.getText().isEmpty()) {
                resultLabel.setText("Помилка: всі поля мають бути заповнені!");
                return;
            }

            try {
                int n = Integer.parseInt(nField.getText());
                int min = Integer.parseInt(minField.getText());
                int max = Integer.parseInt(maxField.getText());

                if (min > max) {
                    resultLabel.setText("Помилка: Min більше Max");
                } else {
                    exec(n, min, max, resultLabel);
                }
            } catch (NumberFormatException e) {
                resultLabel.setText("Помилка: введіть лише числові значення!");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        primaryStage.setOnCloseRequest(event -> {
            if (proc2 != null) {
                proc2.destroy();
            }
            if (proc3 != null) {
                proc3.destroy();
            }
        });

        Scene scene = new Scene(vbox, 400, 250);
        primaryStage.setTitle("lab6");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    private void exec(int n, int min, int max, Label resultLabel) throws IOException {
        int portObj2 = 2222;
        int portObj3 = 3333;

        var newProc2 = startProcessIfNotRunning("/home/rmerezha/oop-lab6/obj2/target/obj2-1.0-SNAPSHOT-shaded.jar", portObj2);
        if (newProc2 != null) {
            proc2 = newProc2;
        }
        String response = communicateWithService(portObj2, String.format("%d %d %d", n, min, max));
        if (response == null || !response.startsWith("0")) {
            resultLabel.setText("Помилка obj2");
            return;
        }

        var newProc3 = startProcessIfNotRunning("/home/rmerezha/oop-lab6/obj3/target/obj3-1.0-SNAPSHOT-shaded.jar", portObj3);
        if (newProc3 != null) {
            proc3 = newProc3;
        }
        String responseObj3 = communicateWithService(portObj3, "SIGNAL");
        if (responseObj3 == null) {
            resultLabel.setText("Помилка при підключенні або обміні даними [obj3]");
        } else {
            resultLabel.setText("Good");
        }
    }

    private Process startProcessIfNotRunning(String jarPath, int port) throws IOException {
        return WindowManager.findOrExecute(jarPath, port);
    }

    private String communicateWithService(int port, String request) {
        try (
                Socket socket = new Socket("localhost", port);
                OutputStream output = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(output, true);
                InputStream input = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input))
        ) {
            writer.println(request);
            return reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


}