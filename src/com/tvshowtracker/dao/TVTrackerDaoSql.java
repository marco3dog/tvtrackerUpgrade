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

public class TVTrackerDaoSql {
	private static Connection conn = BetterConnectionManager.getConnection();

	private static User login(String username, String password) {
		List<User> users = new ArrayList<>();
		try(PreparedStatement ps = conn.prepareStatement("select * from user where username = ?, password = ?")){
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





}
