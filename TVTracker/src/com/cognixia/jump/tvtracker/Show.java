package com.cognixia.jump.tvtracker;

public class Show {
	private int showId;
	private String name;
	private int episodes;
	
	
	public Show() {}
	
	public Show(int showId, String name, int episodes) {
		this.showId = showId;
		this.name = name;
		this.episodes = episodes;
	}

	public int getShowId() {
		return showId;
	}
	public void setShowId(int showId) {
		this.showId = showId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getEpisodes() {
		return episodes;
	}

	public void setEpisodes(int episodes) {
		this.episodes = episodes;
	}


	@Override
	public String toString() {
		return "[showId=" + showId + ", name=" + name + ", episodes=" + episodes + "]";
	}
	
}
