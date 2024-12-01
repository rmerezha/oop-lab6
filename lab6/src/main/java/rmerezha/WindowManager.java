package rmerezha;

import java.io.IOException;
import java.net.Socket;

public class WindowManager {

    public static Process findOrExecute(String jarName, int port) throws IOException {
        if (findWindow(port)) {
            return null;
        }

        return winExec(jarName, port);
    }

    public static boolean findWindow(int port) {
        return isPortOpen("localhost", port);
    }

    public static Process winExec(String jarName, int port) throws IOException {
        var proc = Runtime.getRuntime().exec(new String[]{"java", "-jar", jarName});
        while(!findWindow(port)) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return proc;
    }
    
    private static boolean isPortOpen(String host, int port) {
        try (Socket socket = new Socket(host, port)) {
            return true; 
        } catch (IOException e) {
            return false; 
        }
    }

}
