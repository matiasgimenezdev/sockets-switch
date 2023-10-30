package com.example.demo;

import com.google.gson.Gson;
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {

  public void run() {
    Scanner sc = new Scanner(System.in);

    try {
      String op = "-1";
      Query query = new Query();
      BufferedReader in = null;
      PrintWriter out = null;

      while (!(op.equals("0"))) {
        System.out.println("");
        System.out.println("# Bases de datos disponibles: ");
        System.out.println("");
        System.out.println("1. FACTURACION (Firebird)");
        System.out.println("2. PERSONAL (Postgres)");
        System.out.println("");
        System.out.println("0. Salir");
        System.out.println("");
        System.out.print("Seleccione con cual quiere interactuar: ");
        op = sc.next();
        sc.nextLine();

        if (!op.equals("0")) {
          Socket socket = new Socket("localhost", 5000);
          System.out.println("");
          System.err.println("Conexión establecida con el servidor switch");
          System.out.println("");

          System.out.print("Ingrese el query SQL que quiere ejecutar: ");
          String sql = sc.nextLine();
          query.setQuery(sql.trim());
          if (op.equals("1")) {
            query.setDatabase("facturacion");
          } else {
            query.setDatabase("personal");
          }
          Gson gson = new Gson();
          String json = gson.toJson(query);
          out = new PrintWriter(socket.getOutputStream(), true);
          out.println(json);
          in =
            new BufferedReader(new InputStreamReader(socket.getInputStream()));
          System.out.println("");
          System.out.println(in.readLine());
          System.out.println("");

          socket.close();
          in.close();
          out.close();
          System.out.println("");
          System.err.println("Conexión terminada con el servidor switch");
          System.out.println("");
        }
      }
    } catch (IOException e) {
      System.out.println("Error: " + e.getMessage());
    } finally {
      sc.close();
    }
  }
}
