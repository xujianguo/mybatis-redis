package com.xujianguo.util;

import com.xujianguo.util.PropertyParser.PropertyFilter;

/**
 * 基于正则表达式的属性过滤器
 * @author xujianguo
 * @email ray_xujianguo@yeah.net
 * @time 2015年4月11日
 */
public class RegexFilter implements PropertyFilter {
	private String regex;
	
	public RegexFilter(String regex) {
		this.regex = regex;
	}
	
	/**
	 * 用regex去匹配key
	 */
	public boolean accept(String key) {
		return key.matches(regex);
	}
}
