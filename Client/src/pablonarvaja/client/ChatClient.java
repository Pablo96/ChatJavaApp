package pablonarvaja.client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ChatClient implements Runnable {
    private Socket socket               = null;
    private Scanner console             = null;
    private DataOutputStream streamOut  = null;
    private Thread thread               = null;
    private ChatClientThread client     = null;

    public ChatClient(String serverName, int serverPort) {
        System.out.println("Establishing connection. Please wait ...");
        try {
            socket = new Socket(serverName, serverPort);
            System.out.println("Connected: " + socket);
            start();
        }
        catch(UnknownHostException uhe) {
            System.out.println("Host unknown: " + uhe.getMessage());
        }
        catch(IOException ioe) {
            System.out.println("Unexpected exception: " + ioe.getMessage());
        }
    }

    public void start() throws IOException {
        console   = new Scanner(System.in);
        streamOut = new DataOutputStream(socket.getOutputStream());
        if (thread == null) {
            client = new ChatClientThread(this, socket);
            thread = new Thread(this);
            thread.start();
        }
    }

    @Override
    public void run() {
        while (thread != null) {
            try {
                streamOut.writeUTF(console.nextLine());
                streamOut.flush();
            } catch(IOException ioe) {
                System.out.println("Sending error: " + ioe.getMessage());
                this.stop();
            }
        }
    }

    public void handle(String msg) {
        if (msg.equals(".bye")) {
            System.out.println("Good bye. Press RETURN to exit ...");
            this.stop();
        } else
            System.out.println(msg);
    }

    public void stop() {
        if (thread != null) {
            thread = null;
        }
        try {
            if (console   != null)  console.close();
            if (streamOut != null)  streamOut.close();
            if (socket    != null)  socket.close();
        } catch(IOException ioe) {
            System.out.println("Error closing ...");
        }
        client.close();
    }
}

