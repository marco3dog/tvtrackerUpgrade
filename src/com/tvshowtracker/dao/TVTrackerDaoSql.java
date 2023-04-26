package com.tvshowtracker.dao;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import com.tvshowtracker.connection.BetterConnectionManager;
import com.tvshowtracker.exception.PasswordNotFoundException;
import com.tvshowtracker.exception.UsernameNotFoundException;
import com.tvshowtracker.model.Show;
import com.tvshowtracker.model.User;

public class TVTrackerDaoSql implements TVTrackerDao {
	
	private Connection conn;

	@Override
	public void setConnection() throws FileNotFoundException, ClassNotFoundException, IOException, SQLException {
		conn = BetterConnectionManager.getConnection();
		
	}

	@Override
	public List<Show> getAllShows() {
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
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return showList;
	}
	
	@Override
	public Optional<Show> getShowById(int id) {
		
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
			} else {
				rs.close();
				return Optional.empty();
			}
		} catch (SQLException e) {
			return Optional.empty();
		}
	}

	@Override
	public boolean createShow(String showName, int episodes) {
		
		try (PreparedStatement pstmt = conn.prepareStatement("INSERT INTO show values(null, name = ?, episodes = ?)");) {
			
			pstmt.setString(1, showName);
			pstmt.setInt(2, episodes);
			
			int updated = pstmt.executeUpdate();
			
			if (updated == 1)
				return true;
			
		} catch (SQLException e) {
			return false;
		}
		return false;
	}

	@Override
	public boolean deleteShow(int id) {
		
		try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM show WHERE showid = ?");) {
			
			pstmt.setInt(1, id);
			
			int updated = pstmt.executeUpdate();
			
			if (updated == 1)
				return true;
			
		} catch (SQLException e) {
			return false;
		}
		return false;
	}

	@Override
	public boolean updateShow(String showName, int episodes) {
		
		
		try (PreparedStatement pstmt = conn.prepareStatement("UPDATE show SET name = ?, episodes = ?");) {
			
			pstmt.setString(1, showName);
			pstmt.setInt(2, episodes);
			
			int updated = pstmt.executeUpdate();
			
			if (updated == 1)
				return true;
			
		} catch (SQLException e) {
			return false;
		}
		return false;
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
