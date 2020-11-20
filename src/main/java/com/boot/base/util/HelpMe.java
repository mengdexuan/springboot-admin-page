package com.boot.base.util;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONUtil;
import com.google.common.base.*;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import lombok.extern.slf4j.Slf4j;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by meng on 15-11-16.
 */
@Slf4j
public class HelpMe {


	public static String yyyy_MM_dd_HH_mm_ss = "yyyy-MM-dd HH:mm:ss";//年月日时分秒
	public static String yyyy_MM_dd = "yyyy-MM-dd";//年月日
	public static String yyyy_MM = "yyyy-MM";//年月
	public static String yyyy = "yyyy";//年
	public static String MM = "MM";//月


	/**
	 * 封装JDK自带的UUID, 通过Random数字生成, 中间无-分割.  32位
	 *
	 * @return
	 */
	public static String uuid() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}

	/**
	 * 以父目录为基础创建目录，并返回创建的子目录
	 *
	 * @param parentDir
	 * @param subDir
	 * @return
	 */
	public static String createDir(String parentDir, String subDir) {
		String dir = parentDir + File.separator + subDir;
		FileUtil.mkdir(dir);
		return dir;
	}

	public static String getEncoding(String str) {
		String encode = "GB2312";
		try {
			if (str.equals(new String(str.getBytes(encode), encode))) {
				String s = encode;
				return s;
			}
		} catch (Exception exception) {
		}
		encode = "ISO-8859-1";
		try {
			if (str.equals(new String(str.getBytes(encode), encode))) {
				String s1 = encode;
				return s1;
			}
		} catch (Exception exception1) {
		}
		encode = "UTF-8";
		try {
			if (str.equals(new String(str.getBytes(encode), encode))) {
				String s2 = encode;
				return s2;
			}
		} catch (Exception exception2) {
		}
		encode = "GBK";
		try {
			if (str.equals(new String(str.getBytes(encode), encode))) {
				String s3 = encode;
				return s3;
			}
		} catch (Exception exception3) {
		}
		return "";
	}


	/**
	 * 将 text 写入到一个文本文件中（以追加的方式）
	 *
	 * @param file
	 * @param text
	 */
	public static void write2Txt(String file, String text) {
		try {
			Writer writer = new FileWriter(file, true);
			BufferedWriter bw = new BufferedWriter(writer);
			bw.write(text);
			bw.newLine();
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 是否是数字
	 *
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(str).matches();
	}


	/**
	 * 去除集合中的空元素
	 *
	 * @param in
	 * @return
	 */
	public static Iterator<String> skipNulls(final Iterator<String> in) {
		return new AbstractIterator<String>() {
			protected String computeNext() {
				while (in.hasNext()) {
					String s = in.next();
					if (isNotNull(s)) {
						return s;
					}
				}
				return endOfData();
			}
		};
	}


	public static <T> AbstractIterator<T> skipNulls2(final Iterator<T> in) {
		return new AbstractIterator<T>() {
			protected T computeNext() {
				while (in.hasNext()) {
					T s = in.next();
					if (s != null)
						return s;
				}
				return endOfData();
			}
		};
	}


	/**
	 * 若输入str为：***a, 3 ,b,,d,A,   ,***,
	 * List为：[***a,3,b,d,A,***]
	 *
	 * @param str
	 * @param on  可变参数，默认用 , 切割。
	 * @return
	 */
	public static List<String> easySplit(String str, char... on) {
		if (isNotNull(str)) {
			char on_ = ',';
			if (on != null && on.length > 0) {
				on_ = on[0];
			}
			Iterable<String> result = Splitter.on(on_)
					.trimResults()
					.omitEmptyStrings()
					.split(str);

			return Lists.newArrayList(result.iterator());
		} else {
			return null;
		}
	}


	/**
	 * 如果输入 String input = "apple.banana,,orange,,.apple2 |apple3|";
	 * String pattern = ",.|";
	 * 则结果为：List<String> = "[apple,banana,orange,apple2,apple3]";
	 *
	 * @param str
	 * @param pattern
	 * @return
	 */
	public static List<String> easyStr2List(String str, String pattern) {

		if (isNotNull(str)) {
			Iterable<String> result = Splitter.onPattern("[" + pattern + "]")
					.omitEmptyStrings()
					.trimResults().split(str);
			return Lists.newArrayList(result.iterator());
		} else {


			return null;
		}
	}


	/**
	 * 如果输入：List<String> strList = Lists.newArrayList("a", "", "b", null);
	 * 则输出：a,,b
	 *
	 * @param list
	 * @return
	 */
	public static <T> String easyList2Str(List<T> list) {
		if (isNotNull(list)) {
			return Joiner.on(",").skipNulls().join(list);
		} else {
			return null;
		}
	}


	/**
	 * 该方法与本类中的  easyList2Str(List<String> list) 方法类似，请参看 easyList2Str(List<String> list)
	 *
	 * @param split 分割符
	 * @param list
	 * @return
	 */
	public static String easyList2Str(String split, List<String> list) {
		if (isNotNull(list)) {
			return Joiner.on(split).skipNulls().join(list);
		} else {
			return null;
		}
	}


	/**
	 * 如果输入：String[] strArr = new String[]{"a","","b",null};
	 * 则输出：a,,b
	 *
	 * @param arr
	 * @return
	 */
	public static String easyArr2Str(String[] arr) {
		if (isNotNull(arr)) {
			return Joiner.on(",").skipNulls().join(arr);
		} else {
			return null;
		}
	}


	/**
	 * 如果输入：String[] strArr = new String[]{"a","","b",null};
	 * 则输入：List[a,b]
	 *
	 * @param strArr
	 * @return
	 */
	public static List<String> easyArr2List(String[] strArr) {
		if (isNotNull(strArr)) {
			Joiner joiner = Joiner.on(",").skipNulls();
			String str = joiner.join(strArr);
			return easySplit(str);
		} else {
			return null;
		}
	}

	/**
	 * 将 List 转换为 Arr
	 *
	 * @param list
	 * @return
	 */
	public static String[] easyList2Arr(List<String> list) {
		if (isNotNull(list)) {
			String[] arr = new String[list.size()];
			for (int i = 0; i < list.size(); i++) {
				arr[i] = list.get(i);
			}
			return arr;
		} else {
			return null;
		}
	}


	/**
	 * Map<String,Object> map1 = new HashMap<String,Object>;
	 * map1.put("name",1);
	 * Map<String,Object> map2 = new HashMap<String,Object>;
	 * map2.put("name",2);
	 * Map<String,Object> map3 = new HashMap<String,Object>;
	 * map3.put("name",3);
	 * <p/>
	 * List<Map<String,Object>> mapList = new ArrayList<Map<String,Object>>();
	 * mapList.add(map1);
	 * mapList.add(map2);
	 * mapList.add(map3);
	 * <p/>
	 * 如果 flag = true
	 * 转换结果为：vals = "'1','2','3'";
	 * <p/>
	 * 如果 flag = false
	 * 转换结果为：vals = "1,2,3";
	 *
	 * @param mapList
	 * @param mapKey
	 * @return
	 */
	public static String easyMapList2Str(List<Map<String, Object>> mapList, String mapKey, boolean flag) {
		if (isNotNull(mapList)) {
			String vals = "";
			for (Map<String, Object> map : mapList) {
				String val = String.valueOf(map.get(mapKey));
				if (flag)
					vals += "'";
				vals += val;
				if (flag)
					vals += "'";
				vals += ",";
			}
			vals = vals.substring(0, vals.length() - 1);
			return vals;
		}
		return null;
	}


	/**
	 * 如果输入 String = "a,b,c";
	 * 则输出  String = "'a','b','c'";
	 *
	 * @param str
	 * @param ch  输入字符串之间的分割符号，如上面的逗号，不传默认是英文逗号
	 * @return
	 */
	public static String swapStr(String str, char... ch) {
		if (isNull(str)) {
			return null;
		} else {
			List<String> list = easySplit(str, ch);
			String result = "";
			for (String s : list) {
				result += "'";
				result += s;
				result += "'";
				result += ",";
			}
			result = result.substring(0, result.length() - 1);
			return result;
		}
	}


	/**
	 * 将字符串List转换为整形List
	 *
	 * @param list
	 * @return
	 */
	public static List<Integer> strList2IntList(List<String> list) {
		if (isNull(list)) {
			return null;
		} else {
			Function<String, Integer> function = new Function<String, Integer>() {
				@Override
				public Integer apply(String input) {
					if (isNull(input)) return null;
					return Integer.parseInt(input);
				}
			};
			Iterable<Integer> result = Iterables.transform(list, function);
			return Lists.newArrayList(result.iterator());
		}
	}


	/**
	 * 通过前缀从一个 Map 中获得另一个 Map
	 *
	 * @param map
	 * @param prefix
	 * @return 示例
	 * map.put("a",1);
	 * map.put("b",2);
	 * map.put("ext.e",1);
	 * map.put("ext.f",2);
	 * prefix = "ext";
	 * 则得到的 swapMap 为：
	 * key      val
	 * e        1
	 * f        2
	 */
	public static Map<String, Object> swapMap(Map<String, Object> map, String prefix) {

		Map<String, Object> swapMap = new HashMap<>();
		if (isNotNull(map)) {
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				String key = entry.getKey();
				if (key.contains(".")) {
					String[] keys = key.split("\\.");
					if (prefix.equals(keys[0])) {
						swapMap.put(keys[1], entry.getValue());
					}
				}
			}
		}

		return swapMap;
	}


	/**
	 * 将 map 的 key 转为小写形式，如果 addition 不为空则转为大写形式
	 *
	 * @param map
	 * @param addition
	 * @return
	 */
	public static Map<String, Object> swapMapKeyCase(Map<String, Object> map, Object... addition) {

		Set<Map.Entry<String, Object>> entrySet = map.entrySet();
		Map<String, Object> result = new HashMap<>();
		if (isNull(addition)) {
			for (Map.Entry<String, Object> entry : entrySet) {
				result.put(entry.getKey().toLowerCase(), entry.getValue());
			}
		} else {
			for (Map.Entry<String, Object> entry : entrySet) {
				result.put(entry.getKey().toUpperCase(), entry.getValue());
			}
		}
		return result;
	}

	/**
	 * 同本类中的 swapMapKeyCase 方法
	 *
	 * @param mapList
	 * @param addition
	 * @return
	 */
	public static List<Map<String, Object>> swapMapKeyCase(List<Map<String, Object>> mapList, Object... addition) {
		if (isNull(mapList)) return null;
		List<Map<String, Object>> result = new ArrayList<>();
		for (Map<String, Object> map : mapList) {
			result.add(swapMapKeyCase(map, addition));
		}
		return result;
	}

	/**
	 * 验证邮箱
	 *
	 * @param email
	 * @return
	 */
	public static boolean checkEmail(String email) {
		boolean flag = false;
		try {
			String check = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
			Pattern regex = Pattern.compile(check);
			Matcher matcher = regex.matcher(email);
			flag = matcher.matches();
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}

	/**
	 * 验证手机号码
	 *
	 * @param mobileNumber
	 * @return
	 */
	public static boolean checkMobileNumber(String mobileNumber) {
		boolean flag = false;
		try {
			Pattern regex = Pattern.compile("^(((13[0-9])|(15([0-3]|[5-9]))|(18[0,5-9]))\\d{8})|(0\\d{2}-\\d{8})|(0\\d{3}-\\d{7})$");
			Matcher matcher = regex.matcher(mobileNumber);
			flag = matcher.matches();
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}


	/**
	 * 将整形List转换为字符串List
	 *
	 * @param list
	 * @return
	 */
	public static List<String> intList2StrList(List<Integer> list) {
		if (isNull(list)) {
			return null;
		} else {
			Function<Integer, String> function = new Function<Integer, String>() {
				@Override
				public String apply(Integer input) {
					if (input == null) return null;
					return String.valueOf(input);
				}
			};
			Iterable<String> result = Iterables.transform(list, function);
			return Lists.newArrayList(result.iterator());
		}
	}


	/**
	 * distinct（）不提供按照属性对对象列表进行去重的直接实现。它是基于hashCode（）和equals（）工作的。如果我们想要按照对象的属性，对对象列表进行去重，我们可以通过这个方法来实现
	 *
	 * distinctByKey()方法返回一个使用ConcurrentHashMap 来维护先前所见状态的 Predicate 实例
	 *
	 * 使用示例如下：
	 * 	List<Person> list = Lists.newArrayList();
	 * 	list.add(new Person("a",1));
	 * 	list.add(new Person("a",2));
	 * 	list.add(new Person("b",2));
	 *
	 *  list = list.stream().filter(HelpMe.distinctByKey(p -> p.getName())).collect(Collectors.toList());
	 *
	 * 	会按 Person 对象的name属性去重
	 *
	 * @param keyExtractor
	 * @param <T>
	 * @return
	 */

	public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
		Map<Object,Boolean> seen = new ConcurrentHashMap<>();
		return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}


	/**
	 * obj形式的List转换为字符串形式的List
	 *
	 * @param list
	 * @return
	 */
	public static List<String> objList2StrList(List<Object> list) {
		if (isNull(list)) {
			return null;
		} else {
			Function<Object, String> function = new Function<Object, String>() {
				@Override
				public String apply(Object input) {
					return input == null ? null : input.toString();
				}
			};
			Iterable<String> result = Iterables.transform(list, function);
			return Lists.newArrayList(result.iterator());
		}
	}


	/**
	 * obj 形式的 list 转换为 Integer 形式的 list
	 *
	 * @param list
	 * @return
	 */
	public static List<Integer> objList2IntList(List<Object> list) {
		if (isNull(list)) {
			return null;
		} else {
			Function<Object, Integer> function = new Function<Object, Integer>() {
				@Override
				public Integer apply(Object input) {
					if (input == null) return null;
					if (isNull(input.toString())) return null;
					return Integer.parseInt(input.toString());
				}
			};
			Iterable<Integer> result = Iterables.transform(list, function);
			return Lists.newArrayList(result.iterator());
		}
	}


	/**
	 * 如果输入：
	 * Map<String,Object> map = new HashMap<String,Object>();
	 * map.put("a","b");
	 * map.put("1",123);
	 * map.put("1.1",5.5);
	 * map.put("flag",false);
	 * 则输出：1=123,flag=false,a=b,1.1=5.5
	 *
	 * @param map
	 * @return
	 */
	public static String easyMap2Str(Map<String, Object> map) {
		return Joiner.on(",").withKeyValueSeparator("=").join(map);
	}

	/**
	 * 如果 str = "a=b;c=d,e=f|g=h";
	 * 则 pattern 应该输入：";,|"
	 * separator 应该输入："="
	 * 转换成 Map 的 key-->value 形式为：
	 * a-->b
	 * c-->d
	 * e-->f
	 * g-->h
	 *
	 * @param str
	 * @param pattern
	 * @param separator
	 * @return
	 */
	public static Map<String, String> easyStr2Map(String str, String pattern, String separator) {

		return Splitter.onPattern("[" + pattern + "]{1,}").withKeyValueSeparator(separator).split(str);
	}


	/**
	 * 将类路径下的配置文件转为 Map
	 * eg: keyVal.properties文件
	 * key1=value1
	 * key2=value2
	 * key3=value3
	 *
	 * @param configFileName  配置文件名
	 * @param keyValSplitWith 通过什么分割key和value，可变参数，默认用等号，如本例中所示。
	 * @return 转成Map后即：
	 * key1-->value1
	 * key2-->value2
	 * key3-->value3
	 * @throws Exception
	 */
	public static Map<String, String> easyClassPathConfig2Map(String configFileName, String... keyValSplitWith) throws Exception {

		String splitWith = "=";
		if (isNotNull(keyValSplitWith)) {
			splitWith = keyValSplitWith[0];
		}

		URL url = Resources.getResource(configFileName);
		String strInfo = Resources.toString(url, Charsets.UTF_8);
		String separator = System.getProperty("line.separator");//当前系统换行符

		return easyStr2Map(strInfo, separator, splitWith);
	}


	/**
	 * 将类路径下的配件文件的每一行加入List集合当中
	 *
	 * @param configFileName classpath下的文件名
	 * @return
	 * @throws Exception
	 */
	public static List<String> easyClassPathConfig2List(String configFileName) throws Exception {
		return Files.readLines(new File(Resources.getResource(configFileName).getFile()), Charsets.UTF_8);
	}


	/**
	 * 通过实体Bean获得对应的表名，若 Bean为：SlideShow  则表名为：slide_show，Bean和表的设计要
	 * 有这种对应关系，否则表不存在！
	 *
	 * @param beanName
	 * @return
	 */
	public static String getTableNameByBeanName(String beanName) {

		String b = beanName;
		char[] c = b.toCharArray();
		String d = "";
		for (char ch : c) {
			if (ch >= 'A' && ch <= 'Z') {
				d += "_";
				d += (char) (ch + 32);
			} else {
				d += ch;
			}
		}
		String e = d.substring(1);
		return e;
	}

	/**
	 * 通过Bean中的属性名，得到对应的表中的列名，如 通过 SlideShow 类中的 fileName 字段，可以得到 file_name 列
	 *
	 * @param fieldName
	 * @return
	 */
	public static String getColumnNameByFieldName(String fieldName) {
		String columnName = "";
		if (isNull(fieldName))
			return null;
		char first = fieldName.toCharArray()[0];
		fieldName = getTableNameByBeanName(fieldName);
		columnName += first;
		columnName += fieldName;
		return columnName;
	}


	/**
	 * 将两个集合合并成一个，顺序不变
	 *
	 * @param list1
	 * @param list2
	 * @return
	 */

	public static <T> List<T> mergeList(List<T> list1, List<T> list2) {

		LinkedList<T> linkedList = Lists.newLinkedList();
		if (isNotNull(list1)) {
			for (T t : list1) {
				linkedList.add(t);
			}
		}
		if (isNotNull(list2)) {
			for (T t : list2) {
				linkedList.add(t);
			}
		}
		return linkedList;
	}


	/**
	 * 将 map2 合并到 map1中
	 *
	 * @param map1
	 * @param map2
	 * @return
	 */
	public static Map<String, Object> mergeMap(Map<String, Object> map1, Map<String, Object> map2) {

		if (isNull(map1)) return map2;
		if (isNull(map2)) return map1;
		for (Map.Entry<String, Object> entry : map2.entrySet()) {
			map1.put(entry.getKey(), entry.getValue());
		}
		return map1;
	}


	/**
	 * 通过执行list集合中 T1 元素的 methodName（通常为getXXX） 方法，得到类型为 dataType 的 T2 集合，并返回
	 *
	 * @param list
	 * @param dataType
	 * @param methodName
	 * @return
	 */
	public static <T1, T2> List<T2> filterList(List<T1> list, Class<T2> dataType, String methodName) {

		LinkedList<T2> linkedList = Lists.newLinkedList();
		try {
			for (T1 t1 : list) {
				Class<? extends Object> cla = t1.getClass();
				Method method = cla.getMethod(methodName);
				@SuppressWarnings("unchecked")
				T2 result = (T2) method.invoke(t1);
				linkedList.add(result);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return linkedList;
	}


	/**
	 * 将数据以json的格式写入某个文件中
	 *
	 * @param list     数据集合
	 * @param fileName 文件名称,eg:    abc.txt , config/test.json
	 * @param append   true:追加写入   false:覆盖写入
	 * @param <T>
	 * @return 返回是否写入成功
	 */
	public static <T> boolean write2File(List<T> list, String fileName, boolean append) {

		try {

			File file = new File(fileName);

			List<String> jsonList = list.stream().map(item -> {
				return JSONUtil.toJsonStr(item);
			}).collect(Collectors.toList());

			if (append) {
				//以追加的方式写入文件
				FileUtil.appendUtf8Lines(jsonList, file);
			} else {
				//以覆盖的方式写入文件
				FileUtil.writeUtf8Lines(jsonList, file);
			}

		} catch (Exception e) {
			log.error("写入{}失败!", fileName, e);
			return false;
		}

		return true;
	}


	/**
	 * 从某个文件中读取数据并返回
	 *
	 * @param dataType 返回的数据类型
	 * @param fileName 文件名称,eg:    abc.txt , config/test.json
	 * @param <T>
	 * @return
	 */
	public static <T> List<T> readFromFile(Class<T> dataType, String fileName) {

		List<T> list = Lists.newArrayList();

		try {

			File file = new File(fileName);

			if (FileUtil.exist(file)) {
				List<String> jsonList = FileUtil.readUtf8Lines(file);

				list = jsonList.stream().map(item -> {
					return JSONUtil.toBean(item, dataType);
				}).collect(Collectors.toList());
			}

		} catch (Exception e) {
			log.error("读取{}失败！", fileName, e);
		}


		return list;
	}


	/**
	 * 方法作用说明：
	 * <p/>
	 * 将 obj 对象中的属性（String和Integer类型的）及该属性对应的值（如果get方法返回值不为空）存入 Map 中
	 * 注意：如果 obj 继承父类，那么父类中的属性及对应的值不能存入 Map 中
	 *
	 * @param obj
	 * @return
	 */
	public static Map<String, Object> beanToMap(Object obj) {

		Map<String, Object> map = new HashMap<String, Object>();

		Class<?> objClass = obj.getClass();
		Field[] fields = objClass.getDeclaredFields();
		Method[] methods = objClass.getDeclaredMethods();
		for (int i = 0; i < fields.length; i++) {
			for (int j = 0; j < methods.length; j++) {
				Field field = fields[i];
				Method method = methods[j];

				String fieldName = field.getName();
				String methodName = method.getName();

				if (methodName.startsWith("get")) {
					String noGetMethodName = methodName.substring(3);//去掉方法名前面的 get

					Class<?> type = field.getType();
					String typeSimpleName = type.getSimpleName();
					if (typeSimpleName.equals("String") || typeSimpleName.equals("Integer")) {
						if (fieldName.toUpperCase().equalsIgnoreCase(noGetMethodName)) {
							try {
								Object result = method.invoke(obj);
								if (result != null) {
									if (typeSimpleName.equals("String")) {
										String str = result.toString();
										if (isNotNull(str)) {
											map.put(fieldName, str.trim());
										}
									}
									if (typeSimpleName.equals("Integer")) {
										int anInt = Integer.parseInt(result.toString());
										map.put(fieldName, anInt);
									}
								}

							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
		return map;
	}


	public static <T> T map2Bean(Class<T> type, Map map) {
		if (HelpMe.isNull(map)) {
			return null;
		}
		map = MapUtil.toCamelCaseMap(map);

		T bean = BeanUtil.mapToBean(map, type, true);

		return bean;
	}


	public static <T> List<T> mapList2BeanList(Class<T> type, List<Map<String, Object>> mapList) {
		if (isNull(mapList)) return null;
		List<T> beanList = new ArrayList<>();
		for (Map<String, Object> map : mapList) {
			beanList.add(map2Bean(type, map));
		}
		return beanList;
	}

	/**
	 * 将一个 JavaBean 对象转化为一个  Map
	 *
	 * @param bean 要转化的JavaBean 对象
	 * @return 转化出来的  Map 对象
	 * @throws IntrospectionException    如果分析类属性失败
	 * @throws IllegalAccessException    如果实例化 JavaBean 失败
	 * @throws InvocationTargetException 如果调用属性的 setter 方法失败
	 */
	public static Map<String, Object> convertBean(Object bean)
			throws IntrospectionException, IllegalAccessException, InvocationTargetException {
		Class type = bean.getClass();
		Map<String, Object> returnMap = new HashMap();
		BeanInfo beanInfo = Introspector.getBeanInfo(type);

		PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
		for (int i = 0; i < propertyDescriptors.length; i++) {
			PropertyDescriptor descriptor = propertyDescriptors[i];
			String propertyName = descriptor.getName();
			if (!propertyName.equals("class")) {
				Method readMethod = descriptor.getReadMethod();
				Object result = readMethod.invoke(bean, new Object[0]);
				if (result != null) {
					returnMap.put(propertyName, result);
				} else {
					returnMap.put(propertyName, "");
				}
			}
		}
		return returnMap;
	}


	// 如果不是空返回 true
	public static boolean isNotNull(List list) {
		if (list != null && list.size() > 0)
			return true;
		else
			return false;
	}

	// 如果不是空返回 true
	public static boolean isNotNull(Object[] arr) {
		if (arr != null && arr.length > 0)
			return true;
		else
			return false;
	}

	// 如果不是空返回 true
	public static boolean isNotNull(Map map) {
		if (map != null && map.size() > 0)
			return true;
		else
			return false;
	}

	//数组中任意一个为空则返回 true
	public static boolean anyIsNull(Object... obj) {
		if (isNotNull(obj)) {
			for (Object one : obj) {
				if (one == null) return true;
				if (one instanceof String) {
					String temp = (String) one;
					if (isNull(temp)) return true;
				} else if (one instanceof Integer) {
					Integer temp = (Integer) one;
					if (isNull(temp)) return true;
				} else if (one instanceof Map) {
					Map temp = (Map) one;
					if (isNull(temp)) return true;
				} else if (one instanceof List) {
					List temp = (List) one;
					if (isNull(temp)) return true;
				}
			}
		}
		return false;
	}

	//数据中所有都不为空
	public static boolean allIsNotNull(Object... obj) {
		if (isNotNull(obj)) return !anyIsNull(obj);
		return false;
	}

	public static boolean isNull(Integer intt) {
		if (intt == null) return true;
		return false;
	}

	public static boolean isNotNull(Integer intt) {
		return !isNull(intt);
	}

	// 如果是空返回 true
	public static boolean isNull(Map map) {
		return !isNotNull(map);
	}

	// 如果是空返回 true
	public static boolean isNull(List list) {
		return !isNotNull(list);
	}

	// 如果是空返回 true
	public static boolean isNull(Object[] arr) {
		return !isNotNull(arr);
	}

	// 如果不是空返回 true
	public static boolean isNotNull(String str) {
		if (!Strings.isNullOrEmpty(str))
			return true;
		else
			return false;
	}


	/**
	 * map 按 key 升序排序
	 */
	public static <T>  Map<String, T> sortByKey(Map<String, T> map) {
		Map<String, T> result = new LinkedHashMap(map.size());
		map.entrySet().stream()
				.sorted(Map.Entry.comparingByKey())
				.forEachOrdered(e -> result.put(e.getKey(), e.getValue()));
		return result;
	}


	// 如果是空返回 true
	public static boolean isNull(String str) {
		return !isNotNull(str);
	}

	public static String long2StrDate(DateFormat dateFormat, Long millSec) {
		Date date = new Date(millSec);
		return dateFormat.format(date);
	}


	/**
	 * 比较两个日期之间的大小
	 *
	 * @param d1
	 * @param d2
	 * @return 前者大于后者返回true 反之false
	 */
	public static boolean compareDate(Date d1, Date d2) {
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		c1.setTime(d1);
		c2.setTime(d2);
		int result = c1.compareTo(c2);
		if (result >= 0)
			return true;
		else
			return false;
	}

	/**
	 * 删除字符串的最后一个字符
	 * eg: 输入 "abc,123,"
	 * 输出 "abc,123"
	 *
	 * @param str
	 * @return
	 */
	public static String removeLastChar(String str) {
		if (isNull(str)) return null;
		return str.substring(0, str.length() - 1);
	}

	/**
	 * 删除字符串的第一个字符
	 * eg: 输入 ",abc,123"
	 * 输出 "abc,123"
	 *
	 * @param str
	 * @return
	 */
	public static String removeFirstChar(String str) {
		if (isNull(str)) return null;
		return str.substring(1, str.length());
	}


	public static Object getValFromMap(String key, Map<String, Object> map) {
		if (isNull(key) || isNull(map))
			return null;
		if (map.containsKey(key)) {
			return map.get(key);
		} else return null;
	}


	public static Integer getIntFromMap(String key, Map<String, Object> map) {
		if (isNull(key) || isNull(map))
			return null;
		if (map.containsKey(key)) {
			return Integer.parseInt(getStrFromMap(key, map));
		} else return null;
	}

	/**
	 * 转换 map 中的 key
	 *
	 * @param key1 待转换的 key
	 * @param key2 转换后的 key
	 * @param map  map中对应的值不变
	 */
	public static void changeMapKey(String key1, String key2, Map<String, Object> map) {
		map.put(key2, map.remove(key1));
	}


	public static String getStrFromMap(String key, Map<String, Object> map) {
		if (isNull(key) || isNull(map))
			return null;
		if (map.containsKey(key)) {
			Object val = map.get(key);
			return val == null ? null : val.toString();
		} else return null;
	}

	public static Object removeKeyFromMap(String key, Map<String, Object> map) {
		if (isNull(key) || isNull(map))
			return null;
		if (map.containsKey(key)) {
			return map.remove(key);
		} else return null;
	}


	public static List<Object> getInsertSqlFromMap(String tableName, Map<String, Object> map) {

		if (map != null) {
			List<Object> param = new ArrayList<Object>();
			String columns = "";
			String vals = "";
			for (String key : map.keySet()) {
				columns += "`";
				columns += key;
				columns += "`";
				columns += ",";

				vals += "?";
				vals += ",";
				param.add(map.get(key));
			}
			String sql = "";
			if (isNotNull(columns) && isNotNull(vals)) {
				columns = columns.substring(0, columns.length() - 1);
				vals = vals.substring(0, vals.length() - 1);

				sql = "insert into " + tableName + " (" + columns + ") values(" + vals + ")";

				Object[] array = param.toArray();

				List<Object> result = new ArrayList<>();

				result.add(sql);
				result.add(array);
				return result;
			}
		}

		return null;
	}


	public static String getUpdateSqlFromMap(String tableName, String primaryKey, Map<String, Object> map, String... ignoreKey) {

		if (isNull(map))
			return null;

		if (HelpMe.isNotNull(ignoreKey)) {
			for (String key : ignoreKey) {
				HelpMe.removeKeyFromMap(key, map);//去除不参与更新的字段列
			}
		}

		String primaryVal = getStrFromMap(primaryKey, map);
//        removeKeyFromMap(primaryKey,map);

		String sql = "update " + tableName;
		Set<Map.Entry<String, Object>> entrySet = map.entrySet();

		int i = 0;
		for (Map.Entry<String, Object> entry : entrySet) {
			i++;
			String key = entry.getKey();
			Object val = entry.getValue();
			if (val == null) continue;
			val = val.toString();
			if (i == 1) {
				sql += " set " + key + " = '" + val + "'";
			} else {
				sql += " , " + key + " = '" + val + "'";
			}
		}


		sql += " where " + primaryKey + " = '" + primaryVal + "'";

		return sql;
	}


	/**
	 * 将 xml 标签节点转成大/小写：如果 obj 不为空，将大写转为小写，否则，将小写转为大写
	 *
	 * @param xml
	 * @param obj（任意可变参数）
	 * @return
	 */
	public static String caseXmlNode(String xml, Object... obj) {
		if (isNotNull(xml)) {

			Pattern pattern = Pattern.compile("<.+?>");
			StringBuilder res = new StringBuilder();
			int lastIdx = 0;
			Matcher matchr = pattern.matcher(xml);

			if (obj != null && obj.length > 0) {//将大写转为小写
				while (matchr.find()) {
					String str = matchr.group();
					res.append(xml.substring(lastIdx, matchr.start()));
					res.append(str.toLowerCase());
					lastIdx = matchr.end();
				}
				res.append(xml.substring(lastIdx));
			} else {//将小写转为大写
				while (matchr.find()) {
					String str = matchr.group();
					res.append(xml.substring(lastIdx, matchr.start()));
					if (str.equals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>")) continue;//头信息不转换
					res.append(str.toUpperCase());
					lastIdx = matchr.end();
				}
				res.append(xml.substring(lastIdx));
			}
			return res.toString();
		}
		return null;
	}


	public static List<String> readFile(String filepath)
			throws FileNotFoundException, IOException {

		List<String> xmlFileList = Lists.newArrayList();

		try {

			File file = new File(filepath);
			if (!file.isDirectory()) {

				String xmlFile = file.getName();
				if (xmlFile.endsWith("xml")) {
					xmlFileList.add(xmlFile);
				}

			} else if (file.isDirectory()) {
				// System.out.println("文件夹");
				String[] filelist = file.list();
				for (int i = 0; i < filelist.length; i++) {
					File readfile = new File(filepath + "\\" + filelist[i]);
					if (!readfile.isDirectory()) {
						/*
						 * System.out.println("path=" + readfile.getPath());
						 * System.out.println("absolutepath=" +
						 * readfile.getAbsolutePath());
						 * System.out.println("name=" + readfile.getName());
						 */

						String xmlFile = readfile.getName();
						if (xmlFile.endsWith("xml")) {
							xmlFileList.add(xmlFile);
						}

					} else if (readfile.isDirectory()) {
						readFile(filepath + "\\" + filelist[i]);
					}
				}

			}

		} catch (FileNotFoundException e) {
			System.out.println("readfile()   Exception:" + e.getMessage());
		}

		return xmlFileList;
	}


	/**
	 * 对来自 recentlyDayList(Date currDate,Integer recentlyDay) 方法的数据进行转换
	 *
	 * @param recentlyDayList
	 * @return
	 */
	public static List<String> castRecentlyDayList(List<String> recentlyDayList) {
		if (isNotNull(recentlyDayList)) {
			List<String> castList = Lists.newArrayList();
			String yearStr = "";
			String monthStr = "";
			String dayStr = "";
			for (String str : recentlyDayList) {
				List<String> list = easySplit(str, '-');
				yearStr += list.get(0) + "|";
				monthStr += list.get(1) + "|";
				dayStr += list.get(2) + "|";
			}
			yearStr = yearStr.substring(0, yearStr.length() - 1);
			monthStr = monthStr.substring(0, monthStr.length() - 1);
			dayStr = dayStr.substring(0, dayStr.length() - 1);

			castList.add(0, yearStr);
			castList.add(1, monthStr);
			castList.add(2, dayStr);

			return castList;
		}
		return null;
	}

	/**
	 * 根据当前日期，获取本周的第一天
	 *
	 * @param date
	 * @param firstDayOfWeek
	 * @return
	 */
	public static Date getFirstDayOfWeek(Date date, int firstDayOfWeek) {
		Calendar cal = Calendar.getInstance();
		if (date != null)
			cal.setTime(date);
		cal.setFirstDayOfWeek(firstDayOfWeek);// 设置一星期的第一天是哪一天

		// 指示一个星期中的某天  Calendar.SUNDAY（以周日为首日）  Calendar.MONDAY（以周一为首日）
		cal.set(Calendar.DAY_OF_WEEK, firstDayOfWeek);
		cal.set(Calendar.HOUR_OF_DAY, 0);// 指示一天中的小时。HOUR_OF_DAY 用于 24
		// 小时制时钟。例如，在 10:04:15.250 PM
		// 这一时刻，HOUR_OF_DAY 为 22。
		cal.set(Calendar.MINUTE, 0);// 指示一小时中的分钟。例如，在 10:04:15.250 PM
		// 这一时刻，MINUTE 为 4。
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}


	/**
	 * 根据当前时间获取当前月份的天数
	 *
	 * @param currDate
	 * @return
	 */
	public static int getDayCountByMonth(Date currDate) {
		Calendar a = Calendar.getInstance();
		a.setTime(currDate);
		a.set(Calendar.DATE, 1);// 把日期设置为当月第一天
		a.roll(Calendar.DATE, -1);// 日期回滚一天，也就是最后一天
		int maxDate = a.get(Calendar.DATE);
		return maxDate;
	}

	/**
	 * 获取当前月份的第一天
	 *
	 * @param currDate
	 * @return
	 */
	public static Date getFirstDayOfMonth(Date currDate) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(currDate);
		calendar.set(Calendar.DAY_OF_MONTH,
				calendar.getActualMinimum(Calendar.DAY_OF_MONTH));

		Date date = calendar.getTime();
		return date;
	}


	public static String inputStream2Str(InputStream in) throws Exception {
		if (in == null) return null;

		BufferedReader bf = new BufferedReader(new InputStreamReader(in, "UTF-8"));
		StringBuffer buffer = new StringBuffer();
		String line = "";
		while ((line = bf.readLine()) != null) {
			buffer.append(line);
		}

		return buffer.toString();
	}


	public static class GuavaFiles {
		//复制文件
		public static void copy(String from, String to) throws IOException {
			Files.copy(new File(from), new File(to));
		}

//        String extension = Files.getFileExtension("abc.log");   //扩展名
//        String withoutExtension = Files.getNameWithoutExtension("abc.log"); //文件名，不包含扩展名
	}


	/**
	 * 产生 len 位的随机数
	 *
	 * @param len 随机数长度
	 * @param str 如果为空返回的是随机数字，否则返回的是随机字符
	 * @return
	 */
	public static String randomCode(int len, String... str) {
		Random rd = new Random();
		String n = "";
		int getNum;
		boolean flag = false;
		if (isNull(str)) {
			flag = true;
		}
		do {
			if (flag) {
				getNum = Math.abs(rd.nextInt()) % 10 + 48;//产生数字0-9的随机数
			} else {
				getNum = Math.abs(rd.nextInt()) % 26 + 97;//产生字母a-z的随机数
			}
			char num1 = (char) getNum;
			String dn = Character.toString(num1);
			n += dn;
		} while (n.length() < len);
		return n;
	}

	public static List<String> getTablesFromConnection(Connection conn) {
		List<String> list = new ArrayList<String>();
		try {
			DatabaseMetaData metaData = conn.getMetaData();
			ResultSet rs = metaData.getTables(conn.getCatalog(), null, null, new String[]{"TABLE"});
			while (rs.next()) {
				list.add(rs.getString("TABLE_NAME"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}


	/**
	 * 将 list 转化为 sql in 的字符串返回。 若 list 值为 1，2 则返回结果是 '1','2'
	 * @param list
	 * @param <T>
	 * @return
	 */
	public static <T> String swap2SqlIn(List<T> list){
		List<String> strList = list.stream().map(item -> {
			return "'" + item + "'";
		}).collect(Collectors.toList());

		String str = easyList2Str(strList);

		return str;
	}


	public static void main(String[] args) {

		String tableName = extractTableNameFromSql("select id,id,a from test  where id =2");

		System.out.println(tableName);
	}


	/*public static void main(String[] args)throws Exception {

        String code = randomCode(6);
        System.out.println(code);

        HelpMe helpMe = new HelpMe();

        helpMe.add(1).add(2).add(3);
        List myList = helpMe.getList();
        helpMe.put("key1", 1).put("key2", 2).put("key3", 3);
        Map myMap = helpMe.getMap();

        Date date = new Date();

		int count = getDayCountByMonth(date);
		Date firstDay = getFirstDayOfMonth(date);

		List<String> listasdf = recentlyDayList(firstDay,count,"asdf");

		System.out.println("本月："+listasdf);

		Date prevDate = prevDate(firstDay, 1);
		int count2 = getDayCountByMonth(prevDate);

		Date firstDay2 = getFirstDayOfMonth(parseStrTime2Date("2015-12-10", yyyy_MM_dd));


		List<String> listasdf2 = recentlyDayList(firstDay2,count2,"asdf");
		System.out.println("上月："+listasdf2);
	}*/


	public void test1() {
		String s = "tooth";
		int len = s.length();
		if (len <= 0) System.out.println("null");
		;
		boolean repeated = false;
		for (int i = 0; i < s.length(); i++) {
			repeated = false;
			int j = 0;
			for (; j < s.length(); j++) {
				if (j != i && s.charAt(j) == s.charAt(i)) {
					repeated = true;
					break;
				}
			}
			if (!repeated) System.out.println(s.charAt(i));
		}
	}

	//房子的前面有一个花园，花园里有许多花。


	/**
	 * 将 map 的 key 转换成下划线形式的
	 * map.put("classId","aa");
	 * map.put("userName","aa");
	 * <p>
	 * 转换后 map 中的 key 为 class_id:aa    user_name : aa
	 * <p>
	 * 如果 addition 不为空，转换方式相反。
	 *
	 * @param map
	 */
	public static Map<String, Object> swapMapKey(Map<String, Object> map, Object... addition) {
		if (isNull(map)) return null;

		Map<String, Object> result = new HashMap<>();

		Set<Map.Entry<String, Object>> entrySet = map.entrySet();
		if (isNull(addition)) {
			for (Map.Entry<String, Object> entry : entrySet) {
				result.put(StrKit.toUnderlineCase(entry.getKey()), entry.getValue());
			}
		} else {
			for (Map.Entry<String, Object> entry : entrySet) {
				result.put(StrKit.toCamelCase(entry.getKey()), entry.getValue());
			}
		}

		return result;
	}


	public static Integer long2Int(Long num) {
		int intVal = new Long(num).intValue();
		return intVal;
	}

	public static List<Integer> longList2IntList(List<Long> longList) {
		if (isNotNull(longList)) {
			List<Integer> intList = new LinkedList<>();
			for (long longVal : longList) {
				intList.add(long2Int(longVal));
			}
			return intList;
		}
		return null;
	}


	public static class StrKit {

		public static final String UNDERLINE = "_";

		/**
		 * 将下划线方式命名的字符串转换为驼峰式。如果转换前的下划线大写方式命名的字符串为空，则返回空字符串。</br>
		 * 例如：hello_world->HelloWorld
		 *
		 * @param name 转换前的下划线大写方式命名的字符串
		 * @return 转换后的驼峰式命名的字符串
		 */
		public static String toCamelCase(String name) {
			if (name == null) {
				return null;
			}
			if (name.contains(UNDERLINE)) {
				name = name.toLowerCase();

				StringBuilder sb = new StringBuilder(name.length());
				boolean upperCase = false;
				for (int i = 0; i < name.length(); i++) {
					char c = name.charAt(i);

					if (c == '_') {
						upperCase = true;
					} else if (upperCase) {
						sb.append(Character.toUpperCase(c));
						upperCase = false;
					} else {
						sb.append(c);
					}
				}
				return sb.toString();
			} else
				return name;
		}


		/**
		 * 将驼峰式命名的字符串转换为下划线方式。如果转换前的驼峰式命名的字符串为空，则返回空字符串。</br>
		 * 例如：HelloWorld->hello_world
		 *
		 * @param camelCaseStr 转换前的驼峰式命名的字符串
		 * @return 转换后下划线大写方式命名的字符串
		 */
		public static String toUnderlineCase(String camelCaseStr) {
			if (camelCaseStr == null) {
				return null;
			}

			final int length = camelCaseStr.length();
			StringBuilder sb = new StringBuilder();
			char c;
			boolean isPreUpperCase = false;
			for (int i = 0; i < length; i++) {
				c = camelCaseStr.charAt(i);
				boolean isNextUpperCase = true;
				if (i < (length - 1)) {
					isNextUpperCase = Character.isUpperCase(camelCaseStr.charAt(i + 1));
				}
				if (Character.isUpperCase(c)) {
					if (!isPreUpperCase || !isNextUpperCase) {
						if (i > 0) sb.append(UNDERLINE);
					}
					isPreUpperCase = true;
				} else {
					isPreUpperCase = false;
				}
				sb.append(Character.toLowerCase(c));
			}
			return sb.toString();
		}


	}


	/**
	 * 从sql语句中提取表名
	 *
	 * @param sql
	 * @return
	 */
	public static String extractTableNameFromSql(String sql) {
		sql = sql.toLowerCase();
		List<String> tempList = easyStr2List(sql, " ");

		//SELECT 列名称 FROM 表名称
		//SELECT * FROM 表名称
		if (sql.startsWith("select")) {
			int index = 0;
			for (int i = 0; i < tempList.size(); i++) {
				if ("from".equals(tempList.get(i))) {
					index = i;
					break;
				}
			}
			return tempList.get(index + 1);
		}
		//INSERT INTO 表名称 VALUES (值1, 值2,....)
		//INSERT INTO table_name (列1, 列2,...) VALUES (值1, 值2,....)
		if (sql.startsWith("insert")) {
			String temp = tempList.get(2);
			if (temp.contains("(")) {
				return easyStr2List(temp, "(").get(0);
			}
			return temp;
		}
		//UPDATE 表名称 SET 列名称 = 新值 WHERE 列名称 = 某值
		if (sql.startsWith("update")) {
			return tempList.get(1);
		}
		//DELETE FROM 表名称 WHERE 列名称 = 值
		if (sql.startsWith("delete")) {
			return tempList.get(2);
		}
		return null;
	}


	public static void toZip(String srcDir, OutputStream out, boolean KeepDirStructure) throws RuntimeException {
		long start = System.currentTimeMillis();
		ZipOutputStream zos = null;

		try {
			zos = new ZipOutputStream(out);
			File sourceFile = new File(srcDir);
			compress(sourceFile, zos, sourceFile.getName(), KeepDirStructure);
			long end = System.currentTimeMillis();
			System.out.println("压缩完成，耗时：" + (end - start) + " ms");
			log.info("压缩完成，耗时：" + (end - start) + " ms");
		} catch (Exception var16) {
			throw new RuntimeException("zip error from ZipUtils", var16);
		} finally {
			if (zos != null) {
				try {
					zos.close();
				} catch (IOException var15) {
					var15.printStackTrace();
				}
			}

		}

	}

	private static void compress(File sourceFile, ZipOutputStream zos, String name, boolean KeepDirStructure) throws Exception {
		byte[] buf = new byte[2048];
		if (sourceFile.isFile()) {
			zos.putNextEntry(new ZipEntry(name));
			FileInputStream in = new FileInputStream(sourceFile);

			int len;
			while ((len = in.read(buf)) != -1) {
				zos.write(buf, 0, len);
			}

			zos.closeEntry();
			in.close();
		} else {
			File[] listFiles = sourceFile.listFiles();
			if (listFiles != null && listFiles.length != 0) {
				File[] var11 = listFiles;
				int var7 = listFiles.length;

				for (int var8 = 0; var8 < var7; ++var8) {
					File file = var11[var8];
					if (KeepDirStructure) {
						compress(file, zos, name + "/" + file.getName(), KeepDirStructure);
					} else {
						compress(file, zos, file.getName(), KeepDirStructure);
					}
				}
			} else if (KeepDirStructure) {
				zos.putNextEntry(new ZipEntry(name + "/"));
				zos.closeEntry();
			}
		}

	}
}
