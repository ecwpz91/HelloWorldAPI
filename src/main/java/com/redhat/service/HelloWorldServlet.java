package com.redhat.service;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@WebServlet("/hello")
public class HelloWorldServlet extends HttpServlet {
  private static final long serialVersionUID = 1L;
  final org.apache.logging.log4j.Logger logger = LogManager.getLogger(HelloWorldServlet.class);

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    System.out.println("doGet fired");
    logger.info("doGet fired");

    try {
      // Ensure we have mssql Driver in classpath
      Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

      // Create our mssql database connection
      String host = "localhost";
      String dbname = "master";
      String port = "1433";
      String username = "sa";
      String password = "yourStrong@Password";
      String connectionUrl = "jdbc:sqlserver://" + host + ":" + port + ";databaseName=" + dbname + ";user=" + username
          + ";password=" + password;

      ResultSet resultSet = null;

      System.out.print("Connecting to SQL Server ... ");
      try (Connection connection = DriverManager.getConnection(connectionUrl)) {
        System.out.println("Done.");

        // Get database meta information
        if (connection != null) {
          DatabaseMetaData meta = connection.getMetaData();
          System.out.println("\nDriver Information");
          System.out.println("Driver Name: " + meta.getDriverName());
          System.out.println("Driver Version: " + meta.getDriverVersion());
          System.out.println("\nDatabase Information ");
          System.out.println("Database Name: " + meta.getDatabaseProductName());
          System.out.println("Database Version: " + meta.getDatabaseProductVersion());

          try (Statement statement = connection.createStatement();) {
            // Create and execute a SELECT SQL statement.
            String selectSql = "SELECT * FROM DemoData.dbo.Products FOR JSON AUTO";
            resultSet = statement.executeQuery(selectSql);

            // Print results from select statement
            while (resultSet.next()) {
              System.out.println("Printing results from selecting DemoData database product information:");
              System.out.println(resultSet.getString(1));
            }
          }
          // Handle any errors that may have occurred.
          catch (SQLException e) {
            e.printStackTrace();
          }

          connection.close();
          System.out.println("All done.");
        }
      }
    } catch (Exception e) {
      System.out.println();
      e.printStackTrace();
    }

    request.getRequestDispatcher("hello.jsp").forward(request, response);
  }

  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    doGet(request, response);
  }

}
