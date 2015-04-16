package com.xujianguo.util;

import java.io.File;
import java.io.FilenameFilter;

/**
 * 文件辅助类
 * @author xujianguo
 * @email ray_xujianguo@yeah.net
 * @time 2015年4月10日
 */
public class FileUtil {
	/**
	 * 在指定的文件下遍历递归查找匹配文件并返回
	 * @param root 文件
	 * @param mapping 正则表达式
	 * @return 文件类或者null
	 */
	public static File find(File root, String mapping) {
		File[] files = root.listFiles(new MappingFilter(mapping));
		if(files != null && files.length > 0) {
			return files[0];
		} else {
			files = root.listFiles();
			if(files != null && files.length > 0) {
				for(File file : files) {
					if(file.isDirectory()) {
						File result = find(file, mapping);
						if(result != null)
							return result;
					}
				}
			}
			return null;
		}
	}
	
	/**
	 * 匹配过滤器，通过传入的正则表达式匹配文件的过滤器
	 * @author xujianguo
	 * @email ray_xujianguo@yeah.net
	 * @time 2015年4月10日
	 */
	static class MappingFilter implements FilenameFilter {
		private String mapping;
		
		public MappingFilter(String mapping) {
			this.mapping = mapping;
		}
		
		public boolean accept(File dir, String name) {
			return name.matches(mapping);
		}
	}
}
