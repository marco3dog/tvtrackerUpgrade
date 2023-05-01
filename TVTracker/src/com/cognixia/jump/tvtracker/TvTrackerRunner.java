package com.cognixia.jump.tvtracker;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

import com.cognixia.jump.dao.TVTrackerDaoSql;
import com.cognixia.jump.exceptions.PasswordNotFoundException;
import com.cognixia.jump.exceptions.UsernameNotFoundException;

public class TvTrackerRunner {

	public static void main(String[] args) {
		Connection conn = null;
		try {
			conn = BetterConnectionManager.getConnection();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Connection failed.");
		}
		Scanner scan = new Scanner(System.in);
		User currentUser = login(scan, conn);
		if(currentUser.getList().size() > 0) {
			System.out.println("------------");
			System.out.println("Your Shows:");
			for(int i = 0; i < currentUser.getList().size(); i++) {
				System.out.println(currentUser.getList().get(i).getName() + ": " + currentUser.getList().get(i).getEpisodesWatched() + "/" + currentUser.getList().get(i).getEpisodes() + " episodes watched");
			}
		}
		else {
			System.out.println("You have no tracked shows.");
		}
		System.out.println("------------");
		int option = 0;
		TVTrackerDaoSql dao = new TVTrackerDaoSql();
		try {
			dao.setConnection();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
		while(true) {
			System.out.println("Select an option by entering 1, 2, 3, or 4.");
			System.out.println("------------");
			System.out.println("1.) Add a show.");
			System.out.println("2.) Update a show's progress.");
			System.out.println("3.) View all your shows.");
			System.out.println("4.) Exit.");
			System.out.println("------------");
			try {
				option = scan.nextInt();
				if(option < 1 || option > 4) {
					throw new Exception();
				}
			}
			catch (InputMismatchException e) {
				System.out.println("Enter a number.");
				option = 0;
				scan.next();
				continue;
			}
			catch(Exception e) {
				System.out.println("Not a valid option.");
				option = 0;
				continue;
			}
			switch(option) {
			case 1: { //add a show
				System.out.println("Enter the showId of the show you want to add.");
				displayShowsToAdd(dao);
				boolean goodInput;
				int showId = 0;
				int episodesWatched = 0;
				do {
					try {
						showId = scan.nextInt();
						goodInput = true;
					}
					catch (InputMismatchException e) {
						System.out.println("Enter a number.");
						scan.next();
						goodInput = false;
					}
					catch(Exception e) {
						System.out.println("Not a valid option.");
						goodInput = false;
					}
				}
				while(!goodInput);
				System.out.println("How many episodes have you seen?");
				do {
					try {
						episodesWatched = scan.nextInt();
						goodInput = true;
					}
					catch (InputMismatchException e) {
						System.out.println("Enter a number.");
						scan.next();
						goodInput = false;
					}
					catch(Exception e) {
						System.out.println("Not a valid option");
						goodInput = false;
					}
				}
				while(!goodInput);
				boolean success = currentUser.addShowToList(showId, episodesWatched);
				ArrayList<UserShow> temp = (ArrayList<UserShow>) currentUser.getList();
				ArrayList<Show> listOfShows = (ArrayList<Show>) dao.getAllShows();
				Show addedShow = null;
				for(int i = 0; i<listOfShows.size();i++) {
					if(showId == listOfShows.get(i).getShowId()) {
						addedShow = listOfShows.get(i);
						break;
					}
				}
				
				if (addedShow != null && success) {
					temp.add(new UserShow(addedShow.getShowId(), addedShow.getName(), addedShow.getEpisodes(),
							episodesWatched));
					currentUser.setList(temp);
				}
				break;
			}
			case 2:{
				System.out.println("Which show would you like to update?");
				boolean goodInput;
				int menuOption = 0;
				boolean success;
				for(int i = 1; i <= currentUser.getList().size(); i++) {
					System.out.println(i + ".) " + currentUser.getList().get(i-1).getName());
				}
				do {
					try {
						menuOption = scan.nextInt();
						if(menuOption < 0 || menuOption > currentUser.getList().size()) {
							throw new Exception();
						}
						goodInput = true;
					}
					catch (InputMismatchException e) {
						System.out.println("Enter a number.");
						scan.next();
						goodInput = false;
					}
					catch(Exception e) {
						System.out.println("Not a valid option");
						goodInput = false;
					}
				}
				while(!goodInput);
				System.out.println("How many episodes have you seen?");
				int numberOfEpisodesEntered = 0;
				do {
					try {
						numberOfEpisodesEntered = scan.nextInt();
						goodInput = true;
					}
					catch (InputMismatchException e) {
						System.out.println("Enter a number.");
						scan.next();
						goodInput = false;
					}
					catch(Exception e) {
						System.out.println("Not a valid option");
						goodInput = false;
					}
				}
				while(!goodInput);
				success = currentUser.updateShowInList(currentUser.getList().get(menuOption-1).getShowId(), numberOfEpisodesEntered);
				if (success) {
					currentUser.getList().get(menuOption - 1).setEpisodesWatched(numberOfEpisodesEntered);
				}
				break;
			}
			case 3:{
				System.out.println("------------");
				System.out.println("Your Shows:");
				for(int i = 0; i < currentUser.getList().size(); i++) {
					System.out.println(currentUser.getList().get(i).getName() + ": " + currentUser.getList().get(i).getEpisodesWatched() + "/" + currentUser.getList().get(i).getEpisodes() + " episodes watched");
				}
				break;
			}
			case 4:{System.out.println("Goodbye!"); return;}
			default: {continue;}
			}
		}
			
	}
	
	//Functions
	private static boolean isUser(String username, Connection conn) {
		try(PreparedStatement ps = conn.prepareStatement("select * from user where username = ?")){
			ps.setString(1, username);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				return true;
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		

		return false;
	}
	
	private static boolean isCorrectPassword(String username, String password, Connection conn) {
		try(PreparedStatement ps = conn.prepareStatement("select * from user where username = ? and password = ?")){
			ps.setString(1, username);
			ps.setString(2, password);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				return true;
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private static User login(Scanner scan, Connection conn) {
		System.out.println("Welcome to your TV Show Tracker");	
		boolean valid = false;
		int id = 0;
		String usernameEntered = "";
		String passwordEntered = "";
		do {
			try {
				System.out.println("Enter your username: ");
				usernameEntered = scan.nextLine();
				if(!isUser(usernameEntered, conn)) {
					throw new UsernameNotFoundException(""); //make a custom exception for when entry is not found in the db
				}
				valid = true;
				System.out.println("Welcome, " + usernameEntered + ".");
			}
			catch(UsernameNotFoundException e) {
				System.out.println("Entry not found in the database.");
				valid = false;
			}
			catch(Exception e) {
				e.printStackTrace();
				valid = false;
			}
		}
		while(!valid);
		do {
			try {
				System.out.println("Enter your password: ");
				passwordEntered = scan.nextLine();
				if(!isCorrectPassword(usernameEntered, passwordEntered, conn)) {
					throw new PasswordNotFoundException("");
				}
				valid = true;
				System.out.println("Loading your shows...");
			}
			catch(PasswordNotFoundException e) {
				System.out.println("Incorrect password for " + usernameEntered + ". Try Again.");
				valid = false;
			}
			catch(Exception e) {
				e.printStackTrace();
				valid = false;
			}
		}
		while(!valid);
		try (Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery("select userid from user where username = '" + usernameEntered  +"'");)
		{
			while (rs.next()) {
				id = rs.getInt("userid");
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return new User(id, usernameEntered, passwordEntered);
		
	}
	
	public static void displayShowsToAdd(TVTrackerDaoSql dao) {
		ArrayList<Show> arr = (ArrayList<Show>) dao.getAllShows();
		for(int i = 0; i < arr.size(); i++) {
			System.out.println(arr.get(i));
		}
	}

}