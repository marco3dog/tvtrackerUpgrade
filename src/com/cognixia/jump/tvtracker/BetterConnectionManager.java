package com.cognixia.jump.tvtracker;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class BetterConnectionManager {
	private static Connection connection;
	
	private static void makeConnection() throws FileNotFoundException, IOException, ClassNotFoundException, SQLException {
		Properties props = new Properties();
		
		//load in the data from properties file using FileInputStream
		props.load(new FileInputStream("resources/config.properties"));
		
		//save the values as variables from the properties file
		String url = props.getProperty("url");
		String username = props.getProperty("username");
		String password = props.getProperty("password");
		
		//establish the connection
		Class.forName("com.mysql.cj.jdbc.Driver");
		connection = DriverManager.getConnection(url, username, password);
	}
	
	//returns the single connection object stored in the class
	public static Connection getConnection() throws FileNotFoundException, IOException, ClassNotFoundException, SQLException{
		//connection only gets made once
		if(connection == null) {
			makeConnection();
		}
		return connection;
	}
	
//	public static void main(String[] args) {
//		System.out.println("Welcome to our program");
//		System.out.println("Establishing db connection....");
//		
//		try {
//			Connection connection = BetterConnectionManager.getConnection();
//		} catch (FileNotFoundException e) {
//			System.out.println("Couldn't load detail for connection");
//		} catch (ClassNotFoundException e) {
//			System.out.println("Couldn't load driver for connection");
//		} catch (IOException e) {
//			System.out.println("Couldn't connection details");
//		} catch (SQLException e) {
//			System.out.println("Couldn't connect to the db");
//		}
//		System.out.println("Connected with no errors");
//	}
}
