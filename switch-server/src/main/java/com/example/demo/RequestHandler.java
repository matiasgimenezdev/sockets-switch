package com.example.demo;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

class RequestHandler implements Runnable {

  private Socket clientSocket;

  public RequestHandler(Socket clientSocket) {
    this.clientSocket = clientSocket;
  }

  public void run() {
    StringBuilder stringBuilder;

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

    Connection connection = null;
    Statement statement = null;
    ResultSet queryResult = null;

    try {
      // Leo la peticion desde el socket del cliente
      BufferedReader in = new BufferedReader(
        new InputStreamReader(clientSocket.getInputStream())
      );
      PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

      String json = in.readLine();
      System.out.println("JSON recibido: " + json);

      // Inicio conexion con la BDD adecuada y ejecuto el query
      Gson gson = new Gson();
      Query query = gson.fromJson(json, Query.class);

      if (query.getDatabase().equals("FACTURACION")) {
        connection =
          DriverManager.getConnection(
            firebirdUrl,
            firebirdUsername,
            firebirdPassword
          );
        statement = connection.createStatement();
        System.out.println(
          "Conexión exitosa a la base de datos FACTURACION en servidor Firebird."
        );
      }

      if (query.getDatabase().equals("PERSONAL")) {
        connection =
          DriverManager.getConnection(
            postgresUrl,
            postgresUsername,
            postgresPassword
          );
        System.out.println(
          "Conexión exitosa a la base de datos PERSONAL en servidor Postgres."
        );
        statement = connection.createStatement();
      }

      try {
        JsonObject response = new JsonObject();
        if (query.getQuery().contains("SELECT")) {
          queryResult = statement.executeQuery(query.getQuery());
          ResultSetMetaData metadata = queryResult.getMetaData();
          int columnCount = metadata.getColumnCount();
          JsonArray jsonArray = new JsonArray();
          while (queryResult.next()) {
            JsonObject row = new JsonObject();
            for (int i = 1; i <= columnCount; i++) {
              String columnName = metadata.getColumnName(i);
              String value = queryResult.getObject(i).toString();
              row.addProperty(columnName, value);
            }
            jsonArray.add(row);
          }
          response.add("response", jsonArray);
          out.println(response.toString());
          System.out.println(response.toString());
        } else {
          String message = "";
          int rowCount = statement.executeUpdate(query.getQuery());
          if (rowCount > 0) {
            message =
              "Actualización exitosa sobre la base de datos " +
              query.getDatabase() +
              ". Se actualizaron " +
              rowCount +
              " filas.";
          } else {
            message =
              "No se pudo actualizar la base de datos " + query.getDatabase();
          }
          out.println(message);
          System.out.println(message);
        }
      } catch (SQLException e) {
        out.println("ERROR al ejecutar el query: " + e.getMessage());
        System.out.println("ERROR al ejecutar el query: " + e.getMessage());
      } finally {
        queryResult.close();
      }

      if (connection != null) {
        connection.close();
        statement.close();
      }
      out.close();
      in.close();
      clientSocket.close();
    } catch (IOException e) {
      System.err.println(
        "Error al manejar la conexión del cliente: " + e.getMessage()
      );
    } catch (SQLException e) {
      System.err.println(
        "Error al conectar a la base de datos FIREBIRD: " + e.getMessage()
      );
    }
  }
}
