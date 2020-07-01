package pablonarvaja.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

// This class is a thread for managing client connection
public class ChatServerThread extends Thread {
    private final int        ID;
    private final ChatServer server;
    private final Socket     socket;
    private DataOutputStream streamOut;
    private DataInputStream  streamIn  =  null;

    public ChatServerThread(ChatServer server, Socket socket) {
        super();
        this.server = server;
        this.socket = socket;
        this.ID = socket.getPort();
    }

    // We get the msg from the server main thread and send it to the client
    public void send(String msg) {
        try{
            streamOut.writeUTF(msg);
            streamOut.flush();
        } catch(IOException ioe) {
            System.out.println(ID + " ERROR sending: " + ioe.getMessage());
            server.remove(ID);
            this.close();
        }
    }

    // getter to the thread ID
    public int getID() {
        return this.ID;
    }

    @Override
    // In here we get the line from the socket and pass it to the serer to handle (remove client or send to others)
    public void run() {
        System.out.println("Server Thread " + ID + " running.");
        while (!this.isInterrupted()) {
            try {
                server.handle(ID, streamIn.readUTF());
            } catch(IOException ioe) {
                System.out.println(ID + " ERROR reading: " + ioe.getMessage());
                server.remove(ID);
                return;
            }
        }
    }

    // Get the data streams from the socket
    public void open() throws IOException {
        streamIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        streamOut = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
    }

    // close the socket streams and socket and close this thread
    public void close() {
        try {
            if (socket != null) socket.close();
            if (streamIn != null) streamIn.close();
            if (streamOut != null) streamOut.close();
        } catch (IOException ioe) {
            System.out.println(ID + " ERROR reading: " + ioe.getMessage());
            server.remove(ID);
        }
        this.interrupt();
    }
}
