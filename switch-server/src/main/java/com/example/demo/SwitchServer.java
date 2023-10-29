package com.example.demo;

import java.io.*;
import java.net.*;

public class SwitchServer {

  public void run() {
    ServerSocket socket = null;
    try {
      socket = new ServerSocket(5000);
      System.out.println("Servidor switch iniciado en el puerto 5000.");

      while (true) {
        Socket clientSocket = socket.accept();
        System.out.println("Conexión entrante aceptada.");

        Thread thread = new Thread(new RequestHandler(clientSocket));
        thread.start();
      }
    } catch (IOException e) {
      System.out.println("Error: " + e.getMessage());
    } finally {
      try {
        if (socket != null && !socket.isClosed()) {
          socket.close();
        }
      } catch (IOException e) {
        System.out.println("Error al cerrar el socket: " + e.getMessage());
      }
    }
  }
}
