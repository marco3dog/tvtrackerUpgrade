package com.cognixia.jump.tvtracker;

public class UserShow extends Show {

	private int episodesWatched;

	public UserShow(int showId, String name, int episodes, int episodesWatched) {
		super(showId, name, episodes);
		this.episodesWatched = episodesWatched;
	}

	public int getEpisodesWatched() {
		return episodesWatched;
	}

	public void setEpisodesWatched(int episodesWatched) {
		this.episodesWatched = episodesWatched;
	}
	
	
	
}
