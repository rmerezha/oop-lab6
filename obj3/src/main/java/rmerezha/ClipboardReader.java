package rmerezha;

import javafx.application.Platform;
import javafx.scene.input.Clipboard;

import java.util.function.Consumer;

public class ClipboardReader {
    public static String oldValue = "";
    public static String newValue = "";
    public static boolean isFirst = true;

    public static String read() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        return clipboard.hasString() ? clipboard.getString() : "";
    }

    public static void readEvent(Consumer<String> consumer) {
        Platform.runLater(() -> consumer.accept(newValue));
        Thread thread = new Thread(() -> {
            while (true) {
                try {
                    Platform.runLater(() -> set(read()));

                    if (!newValue.equals(oldValue)) {
                        oldValue = newValue;
                        Platform.runLater(() -> consumer.accept(newValue));
                    }

                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();

    }

    private static void set(String value) {
        newValue = value;
    }

}

