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
			System.out.println("+---------------------+" + ConsoleColors.RESET);
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
			System.out.println();
			
			if (choice.equals("1")) {
				
				while(true) {
					
					System.out.println(ConsoleColors.CYAN_BOLD + "+---------------------+");
					System.out.println("+------ Register -----+");
					System.out.println("+---------------------+\n" + ConsoleColors.RESET);
					System.out.print("Enter a username: ");
					String username = ConsoleScanner.getString();
					
					while (!username.matches("^\\w+$")) {
						System.out.println("Not a valid username.");
						System.out.print("Enter a username: ");
						username = ConsoleScanner.getString();
					}
					
					System.out.print("Enter a password: ");
					String password = ConsoleScanner.getString();
					
					while (!password.matches("^.{1,}$")) {
						System.out.println("Not a valid password.");
						System.out.print("Enter a password: ");
						password = ConsoleScanner.getString();
					}
					
					if (TVTrackerDaoSql.getUser(username,password) != null) 
						System.out.println(ConsoleColors.RED + "Username already taken" + ConsoleColors.RESET);
					
					else
						TVTrackerDaoSql.addUser(username, password);
						break;
				}
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
						break;
					}
					else {
						userSession(currentUser);
						break;
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
		
		while (true) {
			System.out.println(ConsoleColors.CYAN_BOLD + "\n+---------------------+");
			System.out.println("+---- ADMIN MENU -----+");
			System.out.println("+---------------------+" + ConsoleColors.RESET);
			System.out.println("1. Add a show to list");
			System.out.println("2. Remove a show");
			System.out.println("3. Edit show info");
			System.out.println("4. Logout\n");
			System.out.print(ConsoleColors.ITALIC + "Choose an option (1-4): " + ConsoleColors.RESET);
			String option = ConsoleScanner.getString();
			System.out.println();
			
			if (option.equals("1")) {
				
				System.out.print(ConsoleColors.ITALIC + "Enter the name of the show you wish to add: " + ConsoleColors.RESET);
				String showName = ConsoleScanner.getString();
				System.out.print(ConsoleColors.ITALIC + "How many episodes does it have?: "+ ConsoleColors.RESET);
				String episodes = ConsoleScanner.getString();
				
				while (!episodes.matches("^\\d+$")) {
					System.out.println(ConsoleColors.RED + "Not valid numeric input." + ConsoleColors.RESET);
					System.out.print(ConsoleColors.ITALIC + "How many episodes does it have?: " + ConsoleColors.RESET);
					episodes = ConsoleScanner.getString();
				}
				TVTrackerDaoSql.addShow(showName, Integer.parseInt(episodes));
			}
			
			else if (option.equals("2")) {
				
				List<Show> allShows = TVTrackerDaoSql.getAllShows();
				List<Integer> showIds = new ArrayList<>();
				
				for (Show show : allShows) {
					showIds.add(show.getShowId());
				}
				
				for (int i = 0; i < allShows.size(); i++) {
					System.out.println(allShows.get(i).getShowId() + ": " + allShows.get(i).getShortenedName());
				}
				System.out.println();
				System.out.print(ConsoleColors.ITALIC + "Enter the id of the show you want to delete: " + ConsoleColors.RESET);
				int idChoice = ConsoleScanner.getInt();
				
				while (!showIds.contains(idChoice)) {
					ConsoleScanner.getString();
					System.out.println(ConsoleColors.RED + "That's not one of the available ids." + ConsoleColors.RESET);
					System.out.print(ConsoleColors.ITALIC + "Enter the id of the show you want to delete: " + ConsoleColors.RESET);
					idChoice = ConsoleScanner.getInt();
				}
				TVTrackerDaoSql.deleteUserShow(idChoice);
				TVTrackerDaoSql.deleteShow(idChoice);
			}
			
			else if (option.equals("3")) {
				
				List<Show> allShows = TVTrackerDaoSql.getAllShows();
				String showName = "";
				List<Integer> showIds = new ArrayList<>();
				for (Show show : allShows) {
					showIds.add(show.getShowId());
				}
				
				for (int i = 0; i < allShows.size(); i++) {
					System.out.println(allShows.get(i).getShowId() + ": " + allShows.get(i).getShortenedName());
				}
				System.out.println();
				System.out.print(ConsoleColors.ITALIC + "Enter the id of the show you want to edit: " + ConsoleColors.RESET);
				int idChoice = ConsoleScanner.getInt();
				while (!showIds.contains(idChoice)) {
					ConsoleScanner.getString();
					System.out.println(ConsoleColors.RED + "That's not one of the available ids." + ConsoleColors.RESET);
					System.out.print(ConsoleColors.ITALIC + "Enter the id of the show you want to edit: " + ConsoleColors.RESET);
					idChoice = ConsoleScanner.getInt();
				}
				
				for (Show show : allShows) {
					if (show.getShowId() == idChoice) {
						showName = show.getName();
					}
				}
				
				System.out.print(ConsoleColors.ITALIC + "What did you want to edit (1 - episode count or 2 - name): " + ConsoleColors.RESET);
				String op = ConsoleScanner.getString();
				
				while (!op.matches("^[1-2]$")) {
					System.out.println(ConsoleColors.RED + "Not a valid choice." + ConsoleColors.RESET);
					System.out.print(ConsoleColors.ITALIC + "What did you want to edit (1 - episode count or 2 - name): " + ConsoleColors.RESET);
					op = ConsoleScanner.getString();
				}
				
				if (op.equals("1")) {
					System.out.print(ConsoleColors.ITALIC + "How many episodes for this show are able to be watched now: " + ConsoleColors.RESET);
					String amount = ConsoleScanner.getString();
					
					while (!amount.matches("^\\d+$")) {
						System.out.println(ConsoleColors.RED + "Not a valid numeric input." + ConsoleColors.RESET);
						System.out.print(ConsoleColors.ITALIC + "How many episodes for this show are able to be watched now: " + ConsoleColors.RESET);
						amount = ConsoleScanner.getString();
					}
					int episodeCount = Integer.parseInt(amount);
					TVTrackerDaoSql.updateShow(showName, episodeCount);
					System.out.println(ConsoleColors.GREEN + showName + " now has " + episodeCount + " episodes!" + ConsoleColors.RESET);
				}
				
				else if (op.equals("2")) {
					
					System.out.print(ConsoleColors.ITALIC + "What is the new name for the show: " + ConsoleColors.RESET);
					String newName = ConsoleScanner.getString();
					TVTrackerDaoSql.updateShow(showName, newName);
					System.out.println(ConsoleColors.GREEN + showName + " is now renamed to " + newName + "!" + ConsoleColors.RESET);
				}
				
			}
			
			else if (option.equals("4")) {
				System.out.println(ConsoleColors.GREEN + "You're now logged out!" + ConsoleColors.RESET);
				return;
			}
		}
	}
	

	public static void userSession(User user) {
		
		System.out.println(ConsoleColors.GREEN + ConsoleColors.ITALIC + "\nWelcome to your TV Show Tracker!\n" + ConsoleColors.RESET);
		TVTrackerDaoSql.createList(currentUser);
		System.out.println(ConsoleColors.CYAN_BOLD +"+---------------------+");
		System.out.println("+----- Your Shows ----+");
		System.out.println("+---------------------+\n" + ConsoleColors.RESET);
		if(user.getList().isEmpty()) {
			System.out.println(ConsoleColors.YELLOW + ConsoleColors.ITALIC + "Currently not watching any shows\n" + ConsoleColors.RESET);
		}
		
		else {
			
			System.out.printf(ConsoleColors.YELLOW_UNDERLINED + "%-20s %-20s %-8s\n", "Name", "Episodes Watched", "Your Rating" + ConsoleColors.RESET);
			for(int i = 0; i < currentUser.getList().size(); i++) {
				int rating = currentUser.getList().get(i).getRating();
				String ratingToDisplay = rating <= 0 ? "N / A" : Integer.toString(rating)  + " / 5";
				String episodesWatched = currentUser.getList().get(i).getEpisodesWatched() + " / " + currentUser.getList().get(i).getEpisodes();
				System.out.printf("%-20s %-20s %s\n", currentUser.getList().get(i).getShortenedName(), 
						episodesWatched, ratingToDisplay);
			}
			System.out.println();
			System.out.println(ConsoleColors.WHITE_UNDERLINED + "                                     \n" + ConsoleColors.RESET);
		}

		int option = 0;
		while(true) {

			System.out.println("1. Add a show.");
			System.out.println("2. Update a show's progress.");
			System.out.println("3. Rate a show.");
			System.out.println("4. View all your shows.");
			System.out.println("5. Exit.");
			System.out.print(ConsoleColors.ITALIC + "Choose an option (1-5): " + ConsoleColors.RESET);

			try {
				option = ConsoleScanner.getInt();
				if (option < 1 || option > 5) {
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
			System.out.println();

			switch(option) {

			case 1: 
			{ //add a show
				addShow();
				break;
			}
			case 2: 
			{
				if(user.getList().isEmpty()) {
					System.out.println(ConsoleColors.RED + "No shows to update" + ConsoleColors.RESET);
					break;
				}
				updateShow();
				break;
			}
			case 3: 
			{
				if(user.getList().isEmpty()) {
					System.out.println(ConsoleColors.RED + "No shows to rate" + ConsoleColors.RESET);
					break;
				}
				rateShow();
				break;
			}
			case 4:
			{
				viewShows();
				break;
			}
			case 5: 
			{
				System.out.println(ConsoleColors.ITALIC + ConsoleColors.GREEN + "Goodbye!\n"+ ConsoleColors.RESET );
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
		System.out.println();
		boolean showsToAdd = displayShowsToAdd();
		if(showsToAdd != true) {
			return;
		}
		
		boolean goodInput;
		int showId = 0;
		int episodesWatched = 0;
		do {
			System.out.print(ConsoleColors.ITALIC + "Enter the Show ID of the show you want to add: " + ConsoleColors.RESET);
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
				ConsoleScanner.getString();
				goodInput = false;
			}
		}


		while(!goodInput);
		System.out.print(ConsoleColors.ITALIC + "How many episodes have you seen: " + ConsoleColors.RESET);
		do {
			try {
				episodesWatched = ConsoleScanner.getInt();
				goodInput = true;
			}
			catch (InputMismatchException e) {
				System.out.println(ConsoleColors.RED + "Enter a number." + ConsoleColors.RESET);
				goodInput = false;
			}
			catch(Exception e) {
				System.out.println(ConsoleColors.RED + "Not a valid option" + ConsoleColors.RESET);
				goodInput = false;
			}
		}


		while(!goodInput);
		System.out.println();
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

		int defaultRating = 0;
		if (addedShow != null && success) {
			temp.add(new UserShow(addedShow.getShowId(), addedShow.getName(), addedShow.getEpisodes(),
					episodesWatched, defaultRating));
			currentUser.setList(temp);
		}
	}

	public static boolean displayShowsToAdd() {
		List<Show> arr = TVTrackerDaoSql.displayShowsToAdd(currentUser);
		if(arr.size() == 0) {
			System.out.println(ConsoleColors.YELLOW + ConsoleColors.ITALIC + "No more shows to add." + ConsoleColors.RESET);
			return false;
		}
		System.out.printf(ConsoleColors.YELLOW_UNDERLINED + "%-10s %-20s %-10s\n", "Show ID", "Name", "Total Episodes" + ConsoleColors.RESET);
		for(int i = 0; i < arr.size(); i++) {
			System.out.printf("%-10d %-20s %-10d\n", arr.get(i).getShowId(), arr.get(i).getShortenedName(), arr.get(i).getEpisodes());
		}
		System.out.println();
		return true;
	}

	public static void updateShow() {
		boolean goodInput;
		int menuOption = 0;
		boolean success;
		
		System.out.printf(ConsoleColors.YELLOW_UNDERLINED + "%-10s %-30s \n", "ID", "Name      " + ConsoleColors.RESET);
		for(int i = 1; i <= currentUser.getList().size(); i++) {
			System.out.printf("%-10s %-30s \n", i, currentUser.getList().get(i-1).getShortenedName());
		}
		System.out.println();
		System.out.print(ConsoleColors.ITALIC + "Enter the id of the show you'd like to update: " + ConsoleColors.RESET);
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
				System.out.print(ConsoleColors.ITALIC + "Enter the id of the show you'd like to update: " + ConsoleColors.RESET);
				ConsoleScanner.getString();
				goodInput = false;
			}
			catch(Exception e) {
				System.out.println(ConsoleColors.RED + "Not a valid option" + ConsoleColors.RESET);
				System.out.print(ConsoleColors.ITALIC + "Enter the id of the show you'd like to update: " + ConsoleColors.RESET);
				goodInput = false;
			}
		}
		while(!goodInput);
		
		System.out.print(ConsoleColors.ITALIC + "How many episodes have you seen: " + ConsoleColors.RESET);
		int numberOfEpisodesEntered = 0;
		do {
			try {
				numberOfEpisodesEntered = ConsoleScanner.getInt();
				goodInput = true;
			}
			catch (InputMismatchException e) {
				System.out.println(ConsoleColors.RED + "Enter a number."+ ConsoleColors.RESET);
				System.out.print(ConsoleColors.ITALIC + "How many episodes have you seen: " + ConsoleColors.RESET);
				ConsoleScanner.getString();
				goodInput = false;
			}
			catch(Exception e) {
				System.out.println(ConsoleColors.RED + "Not a valid option"+ ConsoleColors.RESET);
				System.out.print(ConsoleColors.ITALIC + "How many episodes have you seen: " + ConsoleColors.RESET);
				goodInput = false;
			}
		}
		while(!goodInput);
		System.out.println();
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
		System.out.printf(ConsoleColors.YELLOW_UNDERLINED + "%-20s %-20s %-15s %-15s %-20s %-8s\n", "Name", "Episodes Watched", "Your Rating", "Average Rating", "Users Watching", "Users Finished" + ConsoleColors.RESET);
		for(int i = 0; i < currentUser.getList().size(); i++) {
			
			int rating = currentUser.getList().get(i).getRating();
			int avgRating = TVTrackerDaoSql.getAverageRatingForShow(currentUser.getList().get(i).getShowId());
			String ratingToDisplay = rating <= 0 ? "N / A" : Integer.toString(rating) + " / 5";
			String avgRatingToDisplay = (avgRating <= 0) ? "N / A" : Integer.toString(avgRating)  + " / 5";
			
			// Store episodes watched in variable
			String episodesWatched = currentUser.getList().get(i).getEpisodesWatched() + " / " + currentUser.getList().get(i).getEpisodes();
			
			System.out.printf("%-20s %-20s %-15s %-15s %-20d %-8d\n", 
					currentUser.getList().get(i).getShortenedName(), 
					episodesWatched, 
					ratingToDisplay,
					avgRatingToDisplay, 
					TVTrackerDaoSql.getUsersWhoAreWatching(currentUser.getList().get(i).getShowId()),
					TVTrackerDaoSql.getUsersWhoAreFinished(currentUser.getList().get(i).getShowId()));
		}
		System.out.println(ConsoleColors.WHITE_UNDERLINED + "                                    \n" + ConsoleColors.RESET);
	}
	
	public static void rateShow() {

		boolean goodInput;
		int menuOption = 0;
		boolean success;
		
		// Print shows to rate 
		System.out.printf(ConsoleColors.YELLOW_UNDERLINED + "%-5s %-20s %-8s \n", "ID", "Name", "Your Rating" + ConsoleColors.RESET);
		for(int i = 1; i <= currentUser.getList().size(); i++) {
			int rating = currentUser.getList().get(i-1).getRating();
			String ratingToDisplay = rating <= 0 ? "N / A" : Integer.toString(rating)  + " / 5";
			
			System.out.printf("%-5d %-20s %-8s \n",i, currentUser.getList().get(i-1).getShortenedName(),ratingToDisplay );
		}
		System.out.println();
		System.out.print(ConsoleColors.ITALIC + "Which show would you like to rate: " + ConsoleColors.RESET);
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
				System.out.print(ConsoleColors.ITALIC + "Which show would you like to rate: " + ConsoleColors.RESET);
				ConsoleScanner.getString();
				goodInput = false;
			}
			catch(Exception e) {
				System.out.println(ConsoleColors.RED + "Not a valid option" + ConsoleColors.RESET);
				System.out.print(ConsoleColors.ITALIC + "Which show would you like to rate: " + ConsoleColors.RESET);
				ConsoleScanner.getString();
				goodInput = false;
			}
		}
		while(!goodInput);
		System.out.print(ConsoleColors.ITALIC + "What rating do you give the show (1-5): "+ ConsoleColors.RESET);
		int rating = 0;
		do {
			try {
				rating = ConsoleScanner.getInt();
				if (rating < 1 || rating > 5) {
					throw new Exception();
				}
				goodInput = true;
			}
			catch (InputMismatchException e) {
				System.out.println(ConsoleColors.RED + "Enter a number (1-5)."+ ConsoleColors.RESET);
				System.out.print(ConsoleColors.ITALIC + "What rating do you give the show (1-5): "+ ConsoleColors.RESET);
				goodInput = false;
			}
			catch(Exception e) {
				System.out.println(ConsoleColors.RED + "Not a valid option"+ ConsoleColors.RESET);
				System.out.print(ConsoleColors.ITALIC + "What rating do you give the show (1-5): "+ ConsoleColors.RESET);
				goodInput = false;
			}
		}
		while(!goodInput);
		success = TVTrackerDaoSql.updateShowRating(currentUser, 
				currentUser.getList().get(menuOption-1).getShowId(), rating);
		if (success) {
			currentUser.getList().get(menuOption - 1).setRating(rating);
		}
	}
}















