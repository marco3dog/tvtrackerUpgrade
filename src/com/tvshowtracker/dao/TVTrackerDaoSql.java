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

import com.tvshowtracker.connection.BetterConnectionManager;
import com.tvshowtracker.model.Show;

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
	
	

}
