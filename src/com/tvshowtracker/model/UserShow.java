package com.tvshowtracker.model;

public class UserShow extends Show {

	private int episodesWatched;
	private int rating;

	public UserShow(int showId, String name, int episodes, int episodesWatched, int rating) {
		super(showId, name, episodes);
		this.episodesWatched = episodesWatched;
		this.rating = rating;
	}

	public int getEpisodesWatched() {
		return episodesWatched;
	}

	public void setEpisodesWatched(int episodesWatched) {
		this.episodesWatched = episodesWatched;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}
	
}
