package com.xujianguo.model;

import java.util.List;

public class Person {
	private int id;
	private String name;
	private Address address;
	private List<Car> cars;
	
	public Person() {}
	
	public Person(String name, Address address) {
		this.name = name;
		this.address = address;
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

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public List<Car> getCars() {
		return cars;
	}

	public void setCars(List<Car> cars) {
		this.cars = cars;
	}

	@Override
	public String toString() {
		return id + " " + name + " " + address + " " + cars;
	}
}
