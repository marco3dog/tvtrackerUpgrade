package com.tvshowtracker.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

import com.tvshowtracker.connection.BetterConnectionManager;
import com.tvshowtracker.dao.TVTrackerDaoSql;
import com.tvshowtracker.exception.PasswordNotFoundException;
import com.tvshowtracker.exception.UsernameNotFoundException;
import com.tvshowtracker.model.Show;
import com.tvshowtracker.model.User;
import com.tvshowtracker.model.UserShow;

public class TrackerController {
	
	private static TVTrackerDaoSql dao = new TVTrackerDaoSql();
	
	
	public static void run() {
		
		User currentUser = dao.login(scan);
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
	
	
	
}















