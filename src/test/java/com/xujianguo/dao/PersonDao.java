package com.xujianguo.dao;

import java.util.Map;

import com.xujianguo.model.Person;

public interface PersonDao {
	public Person findById(int id);
	
	public Person findOne(Map map);
}
