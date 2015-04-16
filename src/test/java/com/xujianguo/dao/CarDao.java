package com.xujianguo.dao;

import java.util.List;

import com.xujianguo.model.Car;

public interface CarDao {
	public List<Car> findByOwner(int owner);
}