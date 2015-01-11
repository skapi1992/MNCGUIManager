package pl.edu.pw.elka.tin.mncgui.testClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Pawel on 2015-01-09.
 */
public class Main {

    public static void main(String[] args) {
        final String host = "localhost";
        final int portNumber = 9000;
        String userInput = "";
        Socket socket = null;
        System.out.println("Creating socket to '" + host + "' on port " + portNumber);
        BufferedReader br = null;
        PrintWriter out = null;

        while (true) {
            try {
                socket = new Socket(host, portNumber);
                br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
            } catch (Exception e){

            }


            try {
                System.out.println("server says:" + br.readLine());
            } catch (Exception e){

            }
            BufferedReader userInputBR = new BufferedReader(new InputStreamReader(System.in));
            try {
                userInput = userInputBR.readLine();
            } catch(Exception e){

            }
            out.println(userInput);
            try {
                System.out.println("server says:" + br.readLine());
            } catch (Exception e){

            }
            if ("exit".equalsIgnoreCase(userInput)) {
                try {
                    socket.close();
                } catch (Exception e){

                }
                break;
            }
        }
    }
}