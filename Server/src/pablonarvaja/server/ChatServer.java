package pablonarvaja.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

// The server is a runnable, the constructor creates a thread and send it self to run
// in it making the constructor return fast to the main thread which then exits
public class ChatServer implements Runnable {
    private int clientCount = 0;
    private Thread thread = null; // Where we run (becomes main thread)
    private ServerSocket server = null;
    private ChatServerThread clients[] = new ChatServerThread[50];

    public ChatServer(int port) {
        try {
            System.out.println("Binding to port " + port + ", please wait  ...");
            server = new ServerSocket(port);
            System.out.println("Server started: " + server);
            start();
        } catch (IOException ioe) {
            System.out.println(ioe);
        }
    }

    // Starts the server main thread
    public void start() {
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }
    }

    @Override
    public void run() {
        while (thread != null) {
            try {
                System.out.println("Waiting for a client ...");
                this.addThread(server.accept());
            } catch(IOException ie) {
                System.out.println("Acceptance Error: " + ie);
                this.stop();
            }
        }
    }

    private int findClient(int ID)
    {
        for (int i = 0; i < clientCount; i++)
            if (clients[i].getID() == ID)
                return i;
        return -1;
    }

    // hanlde is a synchronized method meaning it lock the thread and run
    // we need this in case we need to remove the client which is a sync method too
    public synchronized void handle(int ID, String input) {
        if (input.equals(".bye")) {
            clients[findClient(ID)].send(".bye");
            remove(ID);
        } else
            for (int i = 0; i < clientCount; i++)
                clients[i].send(ID + ": " + input);
    }

    // Remove is synchronize meaning when a thread try too run this code
    // it will lock this thread so we can safely decrease the clientCount
    public synchronized void remove(int ID)
    {
        int pos = findClient(ID);
        if (pos >= 0) {
            ChatServerThread toTerminate = clients[pos];
            System.out.println("Removing client thread " + ID + " at " + pos);
            if (pos < clientCount-1)
                for (int i = pos+1; i < clientCount; i++)
                    clients[i-1] = clients[i];
            clientCount--;
            toTerminate.close();
        }
    }

    // stops the server main thread
    private void stop() {
        if (thread != null) {
            thread = null;
        }
    }

    // Once we accept a connection and get the socket
    // If we have room (we hava a max clients count) add a
    // new thread that manage the new socket (client connection)
    private void addThread(Socket socket) {
        if (clientCount < clients.length) {
            System.out.println("Client accepted: " + socket);
            clients[clientCount] = new ChatServerThread(this, socket);
            try {
                clients[clientCount].open();
                clients[clientCount].start();
                clientCount++;
            } catch(IOException ioe) {
                System.out.println("Error opening thread: " + ioe);
            }
        } else
            System.out.println("Client refused: maximum " + clients.length + " reached.");
    }


}

