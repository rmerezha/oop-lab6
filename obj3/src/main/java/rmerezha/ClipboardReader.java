package rmerezha;

import javafx.scene.input.Clipboard;

public class ClipboardReader {

    public static String read() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        return clipboard.hasString() ? clipboard.getString() : "";
    }

}

