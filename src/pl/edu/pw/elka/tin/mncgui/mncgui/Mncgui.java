package pl.edu.pw.elka.tin.mncgui.mncgui;

import pl.edu.pw.elka.tin.mncgui.server.MultiThreadedServer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Main class.
 * Created by Pawel Stepien on 2015-01-08.
 */
public class Mncgui {

    private static void waitForInput() {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            br.readLine();
        } catch (IOException ioe) {
            System.out.println("IO error trying to read");
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        MultiThreadedServer server = new MultiThreadedServer(9000);
        new Thread(server).start();
        System.out.println("Press enter to stop the server");
        waitForInput();
        System.exit(0);
    }
}
