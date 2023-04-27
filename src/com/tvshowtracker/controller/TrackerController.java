package com.tvshowtracker.controller;

import java.util.InputMismatchException;
import java.util.List;
import com.tvshowtracker.dao.TVTrackerDaoSql;
import com.tvshowtracker.model.Show;
import com.tvshowtracker.model.User;
import com.tvshowtracker.model.UserShow;
import com.tvshowtracker.utils.ConsoleScanner;

public class TrackerController {

	private static User currentUser;


	public static void run() {
		System.out.println("Welcome to your TV Show Tracker");

		while (true) {
			System.out.print("Username: ");
			String username = ConsoleScanner.getString();
			System.out.print("Password: ");
			String password = ConsoleScanner.getString();
			currentUser = TVTrackerDaoSql.login(username, password);

			if (currentUser == null) {
				System.out.println("Invalid credentials.");
			}
			else {
				session();
				return;
			}
		}
	}

	public static void session() {

		TVTrackerDaoSql.createList(currentUser);
		System.out.println("------------");
		System.out.println("Your Shows:");
		for(int i = 0; i < currentUser.getList().size(); i++) {
			System.out.println(currentUser.getList().get(i).getName() + ": " + currentUser.getList().get(i).getEpisodesWatched() + "/" + currentUser.getList().get(i).getEpisodes() + " episodes watched");
		}
		System.out.println("------------");

		int option = 0;

		while(true) {
			System.out.println("Select an option by entering 1, 2, 3, or 4.");
			System.out.println("------------");
			System.out.println("1.) Add a show.");
			System.out.println("2.) Update a show's progress.");
			System.out.println("3.) View all your shows.");
			System.out.println("4.) Exit.");
			System.out.println("------------");

			try {
				option = ConsoleScanner.getInt();
				if (option < 1 || option > 4) {
					throw new Exception();
				}
			}
			catch (InputMismatchException e) {
				System.out.println("Enter a number.");
				option = 0;
				ConsoleScanner.getString();
				continue;
			}
			catch (Exception e) {
				System.out.println("Not a valid option.");
				option = 0;
				continue;
			}

			switch(option) {

			case 1: 
			{ //add a show
				addShow();
				break;
			}
			case 2: 
			{
				updateShow();
				break;
			}
			case 3: 
			{
				viewShows();
				break;
			}
			case 4: 
			{
				System.out.println("Goodbye!");
				return;
			}
			default: {
				continue;
			}
			}

		}
	}

	public static void addShow() {
		System.out.println("Enter the showId of the show you want to add.");
		displayShowsToAdd();

		boolean goodInput;
		int showId = 0;
		int episodesWatched = 0;
		do {
			try {
				showId = ConsoleScanner.getInt();
				goodInput = true;
			}
			catch (InputMismatchException e) {
				System.out.println("Enter a number.");
				ConsoleScanner.getString();
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
				episodesWatched = ConsoleScanner.getInt();
				goodInput = true;
			}
			catch (InputMismatchException e) {
				System.out.println("Enter a number.");
				ConsoleScanner.getString();
				goodInput = false;
			}
			catch(Exception e) {
				System.out.println("Not a valid option");
				goodInput = false;
			}
		}


		while(!goodInput);
		boolean success = TVTrackerDaoSql.addShowToList(currentUser, showId, episodesWatched);
		List<UserShow> temp = currentUser.getList();
		List<Show> listOfShows = TVTrackerDaoSql.getAllShows();
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
	}

	public static void displayShowsToAdd() {
		List<Show> arr = TVTrackerDaoSql.displayShowsToAdd(currentUser);
		System.out.printf("%-10s %-20s %-10s\n", "Show ID", "Name", "Total Episodes");
		for(int i = 0; i < arr.size(); i++) {
			System.out.printf("%-10d %-20s %-10d\n", arr.get(i).getShowId(), arr.get(i).getName(), arr.get(i).getEpisodes());
//			System.out.println(arr.get(i));
		}
	}

	public static void updateShow() {
		System.out.println("Which show would you like to update?");
		boolean goodInput;
		int menuOption = 0;
		boolean success;
		for(int i = 1; i <= currentUser.getList().size(); i++) {
			System.out.println(i + ".) " + currentUser.getList().get(i-1).getName());
		}
		do {
			try {
				menuOption = ConsoleScanner.getInt();
				if(menuOption < 0 || menuOption > currentUser.getList().size()) {
					throw new Exception();
				}
				goodInput = true;
			}
			catch (InputMismatchException e) {
				System.out.println("Enter a number.");
				ConsoleScanner.getString();
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
				numberOfEpisodesEntered = ConsoleScanner.getInt();
				goodInput = true;
			}
			catch (InputMismatchException e) {
				System.out.println("Enter a number.");
				ConsoleScanner.getString();
				goodInput = false;
			}
			catch(Exception e) {
				System.out.println("Not a valid option");
				goodInput = false;
			}
		}
		while(!goodInput);
		success = TVTrackerDaoSql.updateShowInList(currentUser, 
				currentUser.getList().get(menuOption-1).getShowId(), numberOfEpisodesEntered);
		if (success) {
			currentUser.getList().get(menuOption - 1).setEpisodesWatched(numberOfEpisodesEntered);
		}
	}

	public static void viewShows() {
		System.out.println("------------");
		System.out.println("Your Shows:");
		for(int i = 0; i < currentUser.getList().size(); i++) {
			System.out.println(currentUser.getList().get(i).getName() + ": " + currentUser.getList().get(i).getEpisodesWatched() + "/" + currentUser.getList().get(i).getEpisodes() + " episodes watched");
		}
	}
}















