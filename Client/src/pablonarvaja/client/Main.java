package pablonarvaja.client;

// Main thread starts the ChatClient in another thread and exits
public final class Main {
    public static void main(String args[]) {
        if (args.length != 2) {
            System.out.println("Usage: java ChatClient host port");
            return;
        }
        new ChatClient(args[0], Integer.parseInt(args[1]));
    }
}
