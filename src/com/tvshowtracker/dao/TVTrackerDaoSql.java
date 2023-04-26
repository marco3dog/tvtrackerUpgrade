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

public class TVTrackerDaoSql {

	private static Connection conn = BetterConnectionManager.getConnection();

	public static User login(String username, String password) {
		
		try(PreparedStatement ps = conn.prepareStatement("select * from user where username = ? and password = ?")){
			
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

	public static boolean deleteShow(int id) {

		try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM show WHERE showid = ?");) {

			pstmt.setInt(1, id);

			int updated = pstmt.executeUpdate();

			if (updated == 1)
				return true;
		
		}
		
		catch (SQLException e) {
			return false;
		}
		return false;
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
			System.out.println("Invalid amount of episodes watched. Please check your input and try again.");
			return false;
		}
		try (Statement stmt = conn.createStatement()) {
			int updated = stmt.executeUpdate("INSERT INTO user_shows values(" + user.getId() + ", " + showId + ", " + episodesWatched + ")");
			
			if (updated != 0)
				System.out.println("Show successfully added to list.");
				return true;
			
		} catch (SQLException e) {
			System.out.println("Show cannot be added to list. Try again.");
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
			System.out.println("Invalid amount of episodes watched. Please check your input and try again.");
			return false;
		}
		// If the value of episodesWatched is valid, then continue on with the method
		try (Statement stmt = conn.createStatement();) {
			
			int updated = stmt.executeUpdate("UPDATE user_shows SET episodes = " + episodesWatched + " WHERE showid = " + showId + " AND userid = " + user.getId());
			
			if (updated != 0)
				System.out.println("List entry successfully updated.");
				return true;
			
		} catch (SQLException e) {
			System.out.println("List cannot be updated. Try again.");
			return false;
		}
	}
}
