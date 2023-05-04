package com.tvshowtracker.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.tvshowtracker.connection.BetterConnectionManager;
import com.tvshowtracker.model.Show;
import com.tvshowtracker.model.User;
import com.tvshowtracker.model.UserShow;
import com.tvshowtracker.utils.ConsoleColors;

public class TVTrackerDaoSql {

	private static Connection conn = BetterConnectionManager.getConnection();
	
	// CREATE operations
	public static void addUser(String username, String password) {
		
		try (PreparedStatement ps = conn.prepareStatement("INSERT INTO user VALUES(null, ?, ?, 'USER');")) {
			
			ps.setString(1, username);
			ps.setString(2, password);
			ps.execute();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println( ConsoleColors.GREEN + "User added!\n" + ConsoleColors.RESET);
	}
	
	public static void addShow(String name, int episodes) {
		
		String insertStmt = "INSERT INTO shows VALUES(null, ?, ?);";
		
		try (PreparedStatement ps = conn.prepareStatement(insertStmt)) {
			
			ps.setString(1, name);
			ps.setInt(2, episodes);
			ps.execute();
			System.out.println(ConsoleColors.GREEN + "Show added to master list!" + ConsoleColors.RESET);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean createShow(String showName, int episodes) {

		try (PreparedStatement pstmt = conn.prepareStatement("INSERT INTO show VALUES(null, name = ?, episodes = ?)");) {

			pstmt.setString(1, showName);
			pstmt.setInt(2, episodes);

			int updated = pstmt.executeUpdate();

			if (updated == 1)
				return true;
		}

		catch (SQLException e) {
			return false;
		}
		return false;
	}
	
	public static void createList(User user){

		try (Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT us.rating, s.showid, s.name, us.episodes, s.episodes FROM user_shows us JOIN user u ON us.userid = u.userid JOIN shows s ON us.showid = s.showid WHERE us.userid = " + user.getId());
				) {
			while (rs.next()) {
				int id = rs.getInt("showid");
				String name = rs.getString("name");
				int episodesWatched = rs.getInt("us.episodes");
				int episodesTotal = rs.getInt("s.episodes");
				int rating = rs.getInt("rating");
				UserShow show = new UserShow(id, name, episodesTotal, episodesWatched, rating);
				user.getList().add(show);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static boolean addShowToList(User user, int showId, int episodesWatched) {
		int totalEpisodes = 0;

		try (Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT episodes FROM shows WHERE showid = " + showId);
				) {
			while (rs.next()) {
				totalEpisodes = rs.getInt("episodes");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		// Checks if episodesWatched is greater than the amount of episodes the show has or less than 0
		if (episodesWatched > totalEpisodes || episodesWatched < 0) {
			System.out.println(ConsoleColors.RED + "Invalid amount of episodes watched. Please check your input and try again." + ConsoleColors.RESET);
			return false;
		}
		try (Statement stmt = conn.createStatement()) {
			int updated = stmt.executeUpdate("INSERT INTO user_shows values(" + user.getId() + ", " + showId + ", " + episodesWatched 
					+ ", 0)");

			if (updated != 0)
				System.out.println(ConsoleColors.GREEN + "Show successfully added to list.\n" + ConsoleColors.RESET);
			return true;

		} catch (SQLException e) {
			System.out.println(ConsoleColors.RED + "Show cannot be added to list. Try again." + ConsoleColors.RESET);
			return false;
		}
	}
	
	
	// READ operations
	public static User getUser(String username, String password) {
		
		String getStmt = "SELECT * FROM user";
		
		try (PreparedStatement ps = conn.prepareStatement(getStmt);
			 ResultSet rs = ps.executeQuery();) {
			
			while (rs.next()) {
				
				String usr = rs.getString("username");
				String pass = rs.getString("password");
				String role = rs.getString("Role");
				
				if (usr.equals(username) && pass.equals(password)) {
					
					if (role.equals("ADMIN"))
						return new User(rs.getInt("userid"), usr, pass, User.Role.ADMIN);
					else {
						return new User(rs.getInt("userid"), usr, pass, User.Role.USER);
					}
				}
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static List<Show> getAllShows() {
		List<Show> showList = new ArrayList<Show>();

		try (Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT * FROM shows");
				) {
			while (rs.next()) {

				int showId = rs.getInt("showid");
				String name = rs.getString("name");
				int episodes = rs.getInt("episodes");

				Show show = new Show(showId, name, episodes);
				showList.add(show);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}

		return showList;
	}
	
	public static Optional<Show> getShowById(int id) {

		try (PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM show WHERE showid = ?");) {
			pstmt.setInt(1, id);

			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				int showId = rs.getInt("showid");
				String name = rs.getString("name");
				int episodes = rs.getInt("episodes");

				Show show = new Show(showId, name, episodes);
				Optional<Show> foundShow = Optional.of(show);
				rs.close();
				return foundShow;
			}

			else {
				rs.close();
				return Optional.empty();
			}
		}

		catch (SQLException e) {
			return Optional.empty();
		}
	}
	
	public static List<Show> displayShowsToAdd(User user) {

		List<Show> allShows = new ArrayList<>();

		String query = "select s.showid, s.name, s.episodes from " +
				"shows s LEFT JOIN user_shows us on us.showid = s.showid " +
				"and us.userid = ? where us.userid is null;";

		try (PreparedStatement ps = conn.prepareStatement(query)) {

			ps.setInt(1, user.getId());
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				int id = rs.getInt("showid");
				String name = rs.getString("name");
				int episodes = rs.getInt("episodes");
				allShows.add(new Show(id, name, episodes));
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}

		return allShows;
	}

	public static int getUserRatingForShow(int userId, int showId) {
		String query = "select rating "
				+ "from user_shows us "
				+ "join shows s on us.showid = s.showid "
				+ "where us.userid = ? and us.showid = ?";

		try (PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setInt(1, userId);
			ps.setInt(2, showId);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				int rating = rs.getInt("rating");
				return rating;
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}

		return -1;
	}
	
	public static int getAverageRatingForShow(int showId) {
		String query = "select avg(rating) as 'avg_rating' from user_shows where showid = ? group by showid";

		try (PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setInt(1, showId);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				int rating = rs.getInt("avg_rating");
				return rating;
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}

		return -1;
	}
	
	public static int getUsersWhoAreWatching(int showId) {
		try(PreparedStatement pstmt = conn.prepareStatement(
				"select COUNT(*) FROM user_shows WHERE showid = ?")
				){
			pstmt.setInt(1, showId);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				int watchers = rs.getInt("COUNT(*)");
				rs.close();
				return watchers - getUsersWhoAreFinished(showId);
			}

			else {
				rs.close();
				return 0;
			}
		}
		catch(SQLException e) {
			return 0;
		}
	}
	
	public static int getUsersWhoAreFinished(int showId) {
		try(PreparedStatement pstmt = conn.prepareStatement(
				"select COUNT(*) from user_shows us join shows s on us.showid = s.showid where us.episodes = s.episodes and us.showid = ?")
				){
			pstmt.setInt(1, showId);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				int watchers = rs.getInt("COUNT(*)");
				rs.close();
				return watchers;
			}

			else {
				rs.close();
				return 0;
			}
		}
		catch(SQLException e) {
			return 0;
		}
	}
	
	public static User login(String username, String password) {

		try(PreparedStatement ps = conn.prepareStatement(
				"SELECT * from user WHERE username = ? AND password = ?")){

			ps.setString(1, username);
			ps.setString(2, password);

			User foundUser = null;
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {

				int accountId = rs.getInt("userid");
				String accountUsername = rs.getString("username");
				String accountPassword = rs.getString("password");
				String role = rs.getString("Role");
				
				if (role.equals("USER"))
					foundUser = new User(accountId, accountUsername, accountPassword);
				else
					foundUser = new User(accountId, accountUsername, accountPassword, User.Role.ADMIN);
			}
			rs.close();
			return foundUser;
		}
		catch(SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	
	// UPDATE operations
	public static void updateShow(String showName, int episodes) {

		try (PreparedStatement pstmt = conn.prepareStatement("UPDATE shows SET episodes = ? WHERE name = ?")) {
			
			pstmt.setInt(1, episodes);
			pstmt.setString(2, showName);
			pstmt.execute();
		}

		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void updateShow(String oldName, String newName) {
		
		try (PreparedStatement ps = conn.prepareStatement("UPDATE shows SET name = ? WHERE name = ?;")) {
			
			ps.setString(1, newName);
			ps.setString(2, oldName);
			ps.execute();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean updateShowInList(User user, int showId, int episodesWatched) {

		// Get total amount of episodes the show has
		int totalEpisodes = 0;

		try (Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT episodes FROM shows WHERE showid = " + showId);
				) {
			while (rs.next()) {
				totalEpisodes = rs.getInt("episodes");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		// Checks if episodesWatched is greater than the amount of episodes the show has or less than 0
		if (episodesWatched > totalEpisodes || episodesWatched < 0) {
			System.out.println(ConsoleColors.RED + "Invalid amount of episodes watched. Please check your input and try again." + ConsoleColors.RESET);
			return false;
		}
		// If the value of episodesWatched is valid, then continue on with the method
		try (Statement stmt = conn.createStatement();) {

			int updated = stmt.executeUpdate("UPDATE user_shows SET episodes = " + episodesWatched + " WHERE showid = " + showId + " AND userid = " + user.getId());

			if (updated != 0)
				System.out.println(ConsoleColors.GREEN + "List entry successfully updated.\n" + ConsoleColors.RESET);
			return true;

		} catch (SQLException e) {
			System.out.println(ConsoleColors.RED + "List cannot be updated. Try again." + ConsoleColors.RESET);
			return false;
		}
	}

	public static boolean updateShowRating(User user, int showId, int rating) {
		try (PreparedStatement pstmt = conn.prepareStatement(
				"UPDATE user_shows SET rating = ? WHERE showid = ? AND userid = ?")
				) {

			pstmt.setInt(1, rating);
			pstmt.setInt(2, showId);
			pstmt.setInt(3, user.getId());

			int updated = pstmt.executeUpdate();
			if (updated == 1) {
				System.out.println( ConsoleColors.GREEN + "Show rating successfully updated.\n" + ConsoleColors.RESET);
				return true;
			}
		}
		catch (SQLException e) {
			System.out.println(ConsoleColors.RED + "Show rating could not be updated. Try again." + ConsoleColors.RESET);
			return false;
		}
		return false;
	}
	

	// DELETE operations
	public static void deleteUserShow(int showId) {
		
		try (PreparedStatement pstmt = conn.prepareStatement("delete from user_shows where showid = ?;");) {

			pstmt.setInt(1, showId);
			pstmt.execute();
//			System.out.println(ConsoleColors.GREEN + "Show deleted!" + ConsoleColors.RESET);
		}

		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void deleteShow(int id) {

		try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM shows WHERE showid = ?");) {

			pstmt.setInt(1, id);
			pstmt.execute();
			System.out.println(ConsoleColors.GREEN + "Show deleted!" + ConsoleColors.RESET);
		}

		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}
