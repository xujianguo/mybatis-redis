package com.xujianguo.dao;

import java.util.List;

import com.xujianguo.model.User;

public interface UserDao {
	public User findUserById(int id);
	public int insert(User user);
	public int update(User user);
	public int delete(int id);
	public List<User> findAll();
	public List<String> findName();
}
