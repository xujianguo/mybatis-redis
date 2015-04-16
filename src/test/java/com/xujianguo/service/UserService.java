package com.xujianguo.service;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.junit.Assert;
import org.junit.Test;

import com.xujianguo.dao.UserDao;
import com.xujianguo.model.User;
import com.xujianguo.util.SessionFactory;

public class UserService {
    @Test
    public void findUserById() {
        SqlSession sqlSession = SessionFactory.getSessionFactory("test").openSession();  
        UserDao userMapper = sqlSession.getMapper(UserDao.class);  
        User user = userMapper.findUserById(1);  
        Assert.assertNotNull("没找到数据", user);
        System.out.println(user);
    }
    
    @Test
    public void insert() {
    	SqlSession sqlSession = SessionFactory.getSessionFactory("main").openSession();  
        UserDao userMapper = sqlSession.getMapper(UserDao.class);  
        System.out.println(userMapper.insert(new User("xxx", "123", 21)));
        sqlSession.commit();
    }
    
    @Test
    public void update() {
        SqlSession sqlSession = SessionFactory.getSessionFactory("main").openSession();  
        UserDao userMapper = sqlSession.getMapper(UserDao.class);  
        User user = userMapper.findUserById(4); 
        user.setName("xox");
        System.out.println(userMapper.update(user));
        sqlSession.commit();
    }
    
    @Test
    public void delete() {
    	SqlSession sqlSession = SessionFactory.getSessionFactory("main").openSession();  
        UserDao userMapper = sqlSession.getMapper(UserDao.class);  
        System.out.println(userMapper.delete(7));
        sqlSession.commit();
    }
    
    @Test
    public void findAll() {
        SqlSession sqlSession = SessionFactory.getSessionFactory("main").openSession();  
        UserDao userMapper = sqlSession.getMapper(UserDao.class);  
        List<User> userList = userMapper.findAll();
        for(int i = 0; i < userList.size(); i++) {
        	System.out.println(userList.get(i));
        }
    }
    
    @Test
    public void findName() {
        SqlSession sqlSession = SessionFactory.getSessionFactory("main").openSession();  
        UserDao userMapper = sqlSession.getMapper(UserDao.class);  
        List<String> names = userMapper.findName();
        for(int i = 0; i < names.size(); i++) {
        	System.out.println(names.get(i));
        }
    }
}
