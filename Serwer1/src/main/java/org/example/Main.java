package org.example;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main
{
    public static void main(String[] args) {
        try {
            ServerSocket server = new ServerSocket(8932);
            System.out.println("Serwer wystartował.");
            while(true)
            {
                Socket socket = server.accept();
                Watek watek = new Watek(socket);
                watek.start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}