package com.example.demo;

import com.google.gson.Gson;
import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

class RequestHandler implements Runnable {

  private Socket clientSocket;

  public RequestHandler(Socket clientSocket) {
    this.clientSocket = clientSocket;
  }

  public void run() {
    StringBuilder stringBuilder;
    System.out.println("Servidor switch iniciado en el puerto 5000.");

    // Realiza operaciones en la base de datos aquí
    // Parámetros de conexión postgres
    String postgresIp = "192.168.3.4";
    String postgresPort = "5432";
    String postgresUsername = "admin";
    String postgresPassword = "masterkey";
    stringBuilder =
      new StringBuilder("jdbc:postgresql://")
        .append(postgresIp)
        .append(":")
        .append(postgresPort)
        .append("/personal");
    String postgresUrl = stringBuilder.toString();

    // Parámetros de conexión firebird
    String firebirdIp = "192.168.3.3";
    String firebirdPort = "3050";
    String firebirdUsername = "sysdba";
    String firebirdPassword = "masterkey";
    stringBuilder =
      new StringBuilder("jdbc:firebirdsql:")
        .append(firebirdIp)
        .append("/")
        .append(firebirdPort)
        .append(":/firebird/data/facturacion.fdb");
    String firebirdUrl = stringBuilder.toString();

    Connection connection;
    Statement statement;

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

      connection =
        DriverManager.getConnection(
          firebirdUrl,
          firebirdUsername,
          firebirdPassword
        );
      statement = connection.createStatement();
      System.out.println("Conexión exitosa a la base de datos FIREBIRD.");

      connection =
        DriverManager.getConnection(
          postgresUrl,
          postgresUsername,
          postgresPassword
        );

      System.out.println("Conexión exitosa a la base de datos PostgreSQL.");

      // Devuelvo resultado del query
      PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
      out.println("Resultado del query: " + query.getQuery());

      // Cierro streams y socket
      in.close();
      out.close();
      connection.close();
      statement.close();
      clientSocket.close();
    } catch (IOException e) {
      System.out.println(
        "Error al manejar la conexión del cliente: " + e.getMessage()
      );
    } catch (SQLException e) {
      System.err.println(
        "Error al conectar a la base de datos FIREBIRD: " + e.getMessage()
      );
    }
  }
}
