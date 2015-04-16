package com.xujianguo.model;

public class User {
	private int id;
	private String name;
	private String password;
	private int age;
	
	public User() {}
	
	public User(String name, String password, int age) {
		this.name = name;
		this.password = password;
		this.age = age;
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

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}
	
	@Override
	public String toString() {
		return id + " " + name + " " + password + " " + age;
	}
}
