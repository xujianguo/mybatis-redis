package com.xujianguo.util;

import java.util.Properties;

class AA {
	public static class BB {
		private String name;
		private Integer age;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public Integer getAge() {
			return age;
		}
		public void setAge(Integer age) {
			this.age = age;
		}
	}
	
	public static void main(String[] args) {
		Properties prop = new Properties();	
		prop.put("name", "xujianguo");
		prop.put("age", 21);
		BB b = PropertyReflect.reflect(AA.BB.class, prop);
		System.out.println(b.getName());
		System.out.println(b.getAge());
	}
}

public class Demo {
	
}
