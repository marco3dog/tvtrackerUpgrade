package com.tvshowtracker.model;

import java.util.ArrayList;
import java.util.List;

public class User {
	
	public enum Role {ADMIN, USER};
	
	private int id;
	private String name;
	private String password;
	private List<UserShow> list;
	private Role userRole;
	
	public User(int id, String name, String password) {
		super();
		this.id = id;
		this.name = name;
		this.password = password;
		this.list = new ArrayList<UserShow>();
		this.userRole = Role.USER;
	}
	
	public User(int id, String name, String password, Role userRole) {
		super();
		this.id = id;
		this.name = name;
		this.password = password;
		this.list = new ArrayList<UserShow>();
		this.userRole = userRole;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public List<UserShow> getList() {
		return list;
	}
	public void setList(List<UserShow> list) {
		this.list = list;
	}
	public Role getUserRole() {
		return userRole;
	}
	public void setUserRole(Role userRole) {
		this.userRole = userRole;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", password=" + password + ", list=" + list + "]";
	}
	
	

}
