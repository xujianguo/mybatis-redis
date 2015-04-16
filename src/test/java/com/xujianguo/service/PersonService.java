package com.xujianguo.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import com.xujianguo.dao.PersonDao;
import com.xujianguo.model.Person;
import com.xujianguo.util.SessionFactory;

public class PersonService {
    @Test
    public void findById() {
        SqlSession sqlSession = SessionFactory.getSessionFactory("main").openSession();  
        PersonDao mapper = sqlSession.getMapper(PersonDao.class);  
        Person person = mapper.findById(1);  
        System.out.println(person);
    }
    
    @Test
    public void findOne() {
    	SqlSession sqlSession = SessionFactory.getSessionFactory("main").openSession();  
        PersonDao mapper = sqlSession.getMapper(PersonDao.class);  
        Map map = new HashMap();
        map.put("tableName", "person");
        Person person = mapper.findOne(map);
        System.out.println(person);
    }
    
    public static void main(String[] args) {
		new PersonService().findById();
	}
}
