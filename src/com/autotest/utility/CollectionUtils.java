package com.autotest.utility;

import java.util.ArrayList;
import java.util.Map;

/**
 * 集合框架类工具类
 * 
 * @author wb0002
 * 
 */
public class CollectionUtils {

	/**
	 * 功能：将Map的value转换为数组
	 * 
	 * @author xudashen
	 * @param map
	 * @return String[]
	 * @date 2016年9月8日 下午2:12:05
	 */
	public static String[] mapConverArray(Map<String, String> map) {
		ArrayList<String> list = new ArrayList<>();
		for (Map.Entry<String, String> entry : map.entrySet()) {
			list.add(entry.getValue());
		}
		String[] array = new String[list.size()];
		for (int i = 0; i < list.size(); i++) {
			array[i] = list.get(i);
		}
		return array;
	}

}
