package com.example.demo;

import com.google.gson.Gson;
import java.io.*;
import java.net.*;

class RequestHandler implements Runnable {

  private Socket clientSocket;

  public RequestHandler(Socket clientSocket) {
    this.clientSocket = clientSocket;
  }

  public void run() {
    try {
      // Leo la peticion desde el socket del cliente
      BufferedReader in = new BufferedReader(
        new InputStreamReader(clientSocket.getInputStream())
      );

      String json = in.readLine();
      System.out.println("JSON recibido: " + json);

      // Inicio conexion con la BDD adecuada y ejecuto el query
      Gson gson = new Gson();
      Query query = gson.fromJson(json, Query.class);

      // Devuelvo resultado del query
      PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
      out.println("Resultado del query: " + query.getQuery());

      // Cierro streams y socket
      in.close();
      out.close();
      clientSocket.close();
    } catch (IOException e) {
      System.out.println(
        "Error al manejar la conexión del cliente: " + e.getMessage()
      );
    }
  }
}
