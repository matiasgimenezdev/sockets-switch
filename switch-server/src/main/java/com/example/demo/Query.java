package com.example.demo;

public class Query {

  private String database;
  private String query;

  public void setDatabase(String database) {
    this.database = database.toUpperCase();
  }

  public void setQuery(String query) {
    if (!String.valueOf(query.charAt(query.length() - 1)).equals(";")) {
      query += ";";
    }
    this.query = query.toUpperCase();
  }

  public String getDatabase() {
    return this.database;
  }

  public String getQuery() {
    return this.query;
  }
}
