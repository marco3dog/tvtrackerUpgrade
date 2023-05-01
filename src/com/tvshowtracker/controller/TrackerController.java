package com.tvshowtracker.controller;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;

import com.tvshowtracker.dao.TVTrackerDaoSql;
import com.tvshowtracker.model.Show;
import com.tvshowtracker.model.User;
import com.tvshowtracker.model.UserShow;
import com.tvshowtracker.utils.ConsoleColors;
import com.tvshowtracker.utils.ConsoleScanner;

public class TrackerController {

	private static User currentUser;

	public static void run() {
		
		while (true) {
			
			System.out.println(ConsoleColors.CYAN_BOLD + "+---------------------+");
			System.out.println("+------ Welcome ------+");
			System.out.println("+---------------------+\n" + ConsoleColors.RESET);
			System.out.println("1. Create account");
			System.out.println("2. Login");
			System.out.println("3. Exit program\n");
			System.out.print(ConsoleColors.ITALIC + "Choose an option (1-3): " + ConsoleColors.RESET);
			String choice = ConsoleScanner.getString();
			
			while (!choice.matches("^[1-3]$")) {
				System.out.println(ConsoleColors.RED + "Not a valid choice." + ConsoleColors.RESET);
				System.out.print(ConsoleColors.ITALIC + "Choose an option (1-3): "+ ConsoleColors.RESET);
				choice = ConsoleScanner.getString();
			}
			
			if (choice.equals("1")) {
				
				System.out.println(ConsoleColors.CYAN_BOLD + "+---------------------+");
				System.out.println("+------ Register -----+");
				System.out.println("+---------------------+\n" + ConsoleColors.RESET);
				System.out.print("Enter a username: ");
				String username = ConsoleScanner.getString();
				System.out.println("Enter a password: ");
				String password = ConsoleScanner.getString();
				TVTrackerDaoSql.addUser(username, password);
			}
			
			else if (choice.equals("2")) {
				
				while (true) {
					
					System.out.println(ConsoleColors.CYAN_BOLD +"+---------------------+");
					System.out.println("+------- Login -------+");
					System.out.println("+---------------------+" + ConsoleColors.RESET);
					System.out.print("Username: ");
					String username = ConsoleScanner.getString();
					System.out.print("Password: ");
					String password = ConsoleScanner.getString();
					currentUser = TVTrackerDaoSql.login(username, password);
					
					while (currentUser == null) {
						
						System.out.println(ConsoleColors.RED + "Invalid credentials. Try again." + ConsoleColors.RESET);
						System.out.print("Username: ");
						username = ConsoleScanner.getString();
						System.out.print("Password: ");
						password = ConsoleScanner.getString();
						currentUser = TVTrackerDaoSql.login(username, password);
					}
					
					if (currentUser.getUserRole() == User.Role.ADMIN) {
						adminSession(currentUser);
						return;
					}
					else {
						userSession(currentUser);
						return;
					}
				}
			}
			else {
				System.out.println(ConsoleColors.ITALIC + ConsoleColors.GREEN + "Have a great day!" + ConsoleColors.RESET);
				return;
			}
		}
	}
		
	public static void adminSession(User user) {
		
		System.out.println("+-----------------------+");
		System.out.println("+----- ADMIN MENU ------+");
		System.out.println("+-----------------------+");
		System.out.println("1. Add a show to list");
		System.out.println("2. Remove a show");
		System.out.println("3. Edit show info");
		System.out.println("4. Logout");
		System.out.print("Choose an option (1-4): ");
		String option = ConsoleScanner.getString();
		
		if (option.equals("1")) {
			
			System.out.print("Enter the name of the show you wish to add: ");
			String showName = ConsoleScanner.getString();
			System.out.print("How many episodes does it have?: ");
			int episodes = ConsoleScanner.getInt();
			TVTrackerDaoSql.addShow(showName, episodes);
		}
		
		else if (option.equals("2")) {
			
			List<Show> allShows = TVTrackerDaoSql.getAllShows();
			List<Integer> showIds = new ArrayList<>();
			
			for (Show show : allShows) {
				showIds.add(show.getShowId());
			}
			
			for (int i = 0; i < allShows.size(); i++) {
				System.out.println(allShows.get(i).getShowId() + ": " + allShows.get(i).getName());
			}
			System.out.print("Enter the id of the show you want to delete: ");
			int idChoice = ConsoleScanner.getInt();
			
			while (!showIds.contains(idChoice)) {
				ConsoleScanner.getString();
				System.out.println("That's not one of the available ids.");
				System.out.print("Enter the id of the show you want to delete: ");
				idChoice = ConsoleScanner.getInt();
			}
			TVTrackerDaoSql.deleteShow(idChoice);
		}
		
		else if (option.equals("3")) {
			
		}
		
		else {
			System.out.println("You're now logged out!");
			return;
		}
	}
	

	public static void userSession(User user) {
		
		System.out.println("\nWelcome to your TV Show Tracker\n");
		TVTrackerDaoSql.createList(currentUser);
		System.out.println(ConsoleColors.CYAN_BOLD +"+---------------------+");
		System.out.println("+----- Your Shows ----+");
		System.out.println("+---------------------+\n" + ConsoleColors.RESET);
		System.out.printf(ConsoleColors.YELLOW_UNDERLINED + "%-20s %-10s\n", "Name", "Episodes Watched" + ConsoleColors.RESET);
		for(int i = 0; i < currentUser.getList().size(); i++) {
			System.out.printf("%-20s %-1d / %-1d\n",currentUser.getList().get(i).getName(), currentUser.getList().get(i).getEpisodesWatched(), currentUser.getList().get(i).getEpisodes());
		}
		System.out.println();
//		System.out.println("--------------------------\n");
		System.out.println(ConsoleColors.WHITE_UNDERLINED + "                                     \n" + ConsoleColors.RESET);

		int option = 0;

		while(true) {
			System.out.println(ConsoleColors.ITALIC + "Select an option by entering 1, 2, 3, or 4\n" + ConsoleColors.RESET);

			System.out.println("1. Add a show.");
			System.out.println("2. Update a show's progress.");
			System.out.println("3. View all your shows.");
			System.out.println("4. Exit.");


			try {
				option = ConsoleScanner.getInt();
				if (option < 1 || option > 4) {
					throw new Exception();
				}
			}
			catch (InputMismatchException e) {
				System.out.println(ConsoleColors.RED + "Enter a number." + ConsoleColors.RESET);
				option = 0;
				ConsoleScanner.getString();
				continue;
			}
			catch (Exception e) {
				System.out.println(ConsoleColors.RED + "Not a valid option." + ConsoleColors.RESET);
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
			default:
			{
				continue;
			}
			
			}
		}
	}

	public static void addShow() {
		System.out.println(ConsoleColors.ITALIC + "Enter the Show ID of the show you want to add\n" + ConsoleColors.RESET);
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
				System.out.println(ConsoleColors.RED + "Enter a number." + ConsoleColors.RESET);
				ConsoleScanner.getString();
				goodInput = false;
			}
			catch(Exception e) {
				System.out.println(ConsoleColors.RED + "Not a valid option." + ConsoleColors.RESET);
				goodInput = false;
			}
		}


		while(!goodInput);
		System.out.println(ConsoleColors.ITALIC + "How many episodes have you seen?" + ConsoleColors.RESET);
		do {
			try {
				episodesWatched = ConsoleScanner.getInt();
				goodInput = true;
			}
			catch (InputMismatchException e) {
				System.out.println(ConsoleColors.RED + "Enter a number." + ConsoleColors.RESET);
				ConsoleScanner.getString();
				goodInput = false;
			}
			catch(Exception e) {
				System.out.println(ConsoleColors.RED + "Not a valid option" + ConsoleColors.RESET);
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
		System.out.printf(ConsoleColors.YELLOW_UNDERLINED + "%-10s %-20s %-10s\n", "Show ID", "Name", "Total Episodes" + ConsoleColors.RESET);
		for(int i = 0; i < arr.size(); i++) {
			System.out.printf("%-10d %-20s %-10d\n", arr.get(i).getShowId(), arr.get(i).getName(), arr.get(i).getEpisodes());
		}
	}

	public static void updateShow() {
		System.out.println(ConsoleColors.ITALIC + "Which show would you like to update?\n" + ConsoleColors.RESET);
		boolean goodInput;
		int menuOption = 0;
		boolean success;
		for(int i = 1; i <= currentUser.getList().size(); i++) {
			System.out.println(i + ". " + currentUser.getList().get(i-1).getName());
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
				System.out.println(ConsoleColors.RED + "Enter a number." + ConsoleColors.RESET);
				ConsoleScanner.getString();
				goodInput = false;
			}
			catch(Exception e) {
				System.out.println(ConsoleColors.RED + "Not a valid option" + ConsoleColors.RESET);
				goodInput = false;
			}
		}
		while(!goodInput);
		System.out.println(ConsoleColors.ITALIC + "How many episodes have you seen?"+ ConsoleColors.RESET);
		int numberOfEpisodesEntered = 0;
		do {
			try {
				numberOfEpisodesEntered = ConsoleScanner.getInt();
				goodInput = true;
			}
			catch (InputMismatchException e) {
				System.out.println(ConsoleColors.RED + "Enter a number."+ ConsoleColors.RESET);
				ConsoleScanner.getString();
				goodInput = false;
			}
			catch(Exception e) {
				System.out.println(ConsoleColors.RED + "Not a valid option"+ ConsoleColors.RESET);
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
		System.out.println(ConsoleColors.CYAN_BOLD + "+---------------------+");
		System.out.println("+----- Your Shows ----+");
		System.out.println("+---------------------+\n" + ConsoleColors.RESET);
		System.out.printf(ConsoleColors.YELLOW_UNDERLINED + "%-20s %-10s\n", "Name", "Episodes Watched" + ConsoleColors.RESET);
		for(int i = 0; i < currentUser.getList().size(); i++) {
			System.out.printf("%-20s %-1d / %-1d\n",currentUser.getList().get(i).getName(), currentUser.getList().get(i).getEpisodesWatched(), currentUser.getList().get(i).getEpisodes());
		}
		System.out.println();
		System.out.println(ConsoleColors.WHITE_UNDERLINED + "                                     \n" + ConsoleColors.RESET);
	}
}















