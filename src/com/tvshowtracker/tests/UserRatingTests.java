package com.tvshowtracker.tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.tvshowtracker.dao.TVTrackerDaoSql;
import com.tvshowtracker.model.Show;
import com.tvshowtracker.model.User;

class UserRatingTests {

	@Test
	void testGetUserRatingForShow() {
		int userId = 1;
		int showId = 1;
		int rating = TVTrackerDaoSql.getUserRatingForShow(userId, showId);
		System.out.println("Rating for user " + userId + ", show " + showId + ": " + rating);
	}

	@Test
	void testGetAverageRatingForShow() {
		int showId = 1;
		int rating = TVTrackerDaoSql.getAverageRatingForShow(showId);
		System.out.println("Average rating for show " + showId + ": " + rating);
	}

	@Test
	void testUpdateShowRating() {
		User user = new User(1, "", "");
		int showId = 1;
		int newRating = 4;
		boolean updated = TVTrackerDaoSql.updateShowRating(user, showId, newRating);
		assertTrue(updated);
		System.out.println("Successfully updated rating for user " + user.getId() 
		+ ", show " + showId);
	}

}
