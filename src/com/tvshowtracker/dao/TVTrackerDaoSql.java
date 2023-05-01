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
				foundUser = new User(accountId, accountUsername, accountPassword);
			}
			rs.close();
			return foundUser;
		}
		catch(SQLException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	public static void addUser(String username, String password) {
		
		try (PreparedStatement ps = conn.prepareStatement("INSERT INTO user VALUES(null, ?, ?, 'USER');")) {
			
			ps.setString(1, username);
			ps.setString(2, password);
			ps.execute();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println( ConsoleColors.GREEN + "User added!" + ConsoleColors.RESET);
	}
	
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

	
	public static void deleteShow(int id) {

		try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM show WHERE showid = ?");) {

			pstmt.setInt(1, id);
			pstmt.execute();
			System.out.println(ConsoleColors.GREEN + "Show deleted!" + ConsoleColors.RESET);
		}

		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	

	public static boolean updateShow(String showName, int episodes) {

		try (PreparedStatement pstmt = conn.prepareStatement("UPDATE show SET name = ?, episodes = ?");) {

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
				ResultSet rs = stmt.executeQuery("SELECT s.showid, s.name, us.episodes, s.episodes FROM user_shows us JOIN user u ON us.userid = u.userid JOIN shows s ON us.showid = s.showid WHERE us.userid = " + user.getId());
				) {
			while (rs.next()) {
				int id = rs.getInt("showid");
				String name = rs.getString("name");
				int episodesWatched = rs.getInt("us.episodes");
				int episodesTotal = rs.getInt("s.episodes");
				UserShow show = new UserShow(id, name, episodesTotal, episodesWatched);
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
			int updated = stmt.executeUpdate("INSERT INTO user_shows values(" + user.getId() + ", " + showId + ", " + episodesWatched + ")");

			if (updated != 0)
				System.out.println(ConsoleColors.GREEN + "Show successfully added to list." + ConsoleColors.RESET);
			return true;

		} catch (SQLException e) {
			System.out.println(ConsoleColors.RED + "Show cannot be added to list. Try again." + ConsoleColors.RESET);
			return false;
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
				System.out.println(ConsoleColors.GREEN + "List entry successfully updated." + ConsoleColors.RESET);
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
			if (updated == 1)
				return true;
		}
		catch (SQLException e) {
			System.out.println(ConsoleColors.RED + "Show rating could not be updated. Try again." + ConsoleColors.RESET);
			return false;
		}
		return false;
	}
}
