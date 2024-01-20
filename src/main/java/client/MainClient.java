package client;
import client.*;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class MainClient {
    public static void main(String[] args) {
        Client client = new Client();
        client.inscription(args);
    }
}

