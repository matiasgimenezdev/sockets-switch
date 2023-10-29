package com.example.demo;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SwitchServer {

  public void run() {
    ServerSocket socket = null;
    StringBuilder stringBuilder;
    try {
      socket = new ServerSocket(5000);
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

      try {
        Thread.sleep(8000); // Pausa la ejecución para esperar a que los contenedores esten corriendo.
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      Connection connection;
      Statement statement;
      String query;

      try {
        connection =
          DriverManager.getConnection(
            firebirdUrl,
            firebirdUsername,
            firebirdPassword
          );

        statement = connection.createStatement();
        System.out.println("Conexión exitosa a la base de datos FIREBIRD.");
        query =
          """
          CREATE TABLE FACTURA
          (
            NUMERO INTEGER NOT NULL,
            FECHA DATE NOT NULL,
            MONTO DOUBLE PRECISION DEFAULT 0.0,
            CONSTRAINT PK_FACTURA PRIMARY KEY (NUMERO)
          );
        """;

        statement.execute(query);
        query =
          """
          CREATE TABLE PRODUCTO
          (
            CODIGO INTEGER NOT NULL,
            DESCRIPCION VARCHAR(100) NOT NULL,
            STOCK INTEGER DEFAULT 0 NOT NULL,
            PRECIO DOUBLE PRECISION DEFAULT 0 NOT NULL,
            CONSTRAINT PK_PRODUCTO PRIMARY KEY (CODIGO)          
          );
          """;
        statement.execute(query);
        query =
          """
          CREATE TABLE DETALLE
          (
            NUMERO INTEGER NOT NULL,
            CODIGO INTEGER NOT NULL,
            CANTIDAD INTEGER DEFAULT 1 NOT NULL,
            PRECIO DOUBLE PRECISION DEFAULT 0 NOT NULL,
            SUBTOTAL DOUBLE PRECISION,
            CONSTRAINT PK_DETALLE PRIMARY KEY (NUMERO,CODIGO),
            CONSTRAINT FK_DETALLE_PRODUCTO FOREIGN KEY (CODIGO) REFERENCES PRODUCTO,
            CONSTRAINT FK_DETALLE_FACTURA FOREIGN KEY (NUMERO) REFERENCES FACTURA
          );   
          """;
        statement.execute(query);

        System.out.println("Tablas en FACTURACION creadas con éxito.");

        statement.close();
        connection.close();
      } catch (SQLException e) {
        System.err.println(
          "Error al conectar a la base de datos FIREBIRD: " + e.getMessage()
        );
      }

      try {
        connection =
          DriverManager.getConnection(
            postgresUrl,
            postgresUsername,
            postgresPassword
          );

        System.out.println("Conexión exitosa a la base de datos PostgreSQL.");

        query =
          """
          CREATE TABLE EMPLEADO (
            ID serial PRIMARY KEY,
            NOMBRE VARCHAR(120) NOT  NULL,
            APELLIDO VARCHAR(120) NOT  NULL,
            SALARIO NUMERIC(10, 2) DEFAULT 0
          );
        """;

        statement = connection.createStatement();
        statement.execute(query);
        statement.close();
        connection.close();

        System.out.println("Tablas en PERSONAL creadas con éxito.");
      } catch (SQLException e) {
        System.err.println(
          "Error al conectar a la base de datos POSTGRES: " + e.getMessage()
        );
      }

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
