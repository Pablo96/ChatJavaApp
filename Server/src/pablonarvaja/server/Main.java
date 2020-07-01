package pablonarvaja.server;

// Main thread starts the ChatServer in another thread and exits
public final class Main {
    public static void main(String args[]) {
        if (args.length != 1) {
            System.out.println("Usage: java ChatServer port");
            return;
        }
        new ChatServer(Integer.parseInt(args[0]));
    }
}
