package file;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


public class CreateJavaFromDb {
	
	private static String packageOutPath = "com.bottos.community.entity";
	private static String authorName = "Tools";// 作者名字
	private String tablename = "";// 表名
	private static String[] colnames; // 列名数组
	private static String[] colTypes; // 列名类型数组
	private static int[] colSizes; // 列名大小数组

 
	// 数据库连接
	private static final String URL = "jdbc:oracle:thin:@localhost:1521:orcl";
	private static final String NAME = "bot_test";
	private static final String PASS = "bot_test";
	private static final String DRIVER = "oracle.jdbc.driver.OracleDriver";
	
	static String [] colsExc = {"id", "memo","record_status", "version",
			"create_date", "create_user","last_update_date", "last_update_user"};
 
	public CreateJavaFromDb() {
		
	}
	public CreateJavaFromDb(String tablename) {
		this.tablename = tablename.toUpperCase();
		
		Connection con = null;
		// 查要生成实体类的表
		String sql = "select * from " + tablename;
		PreparedStatement pStemt = null;
		try {
			
			try {
				Class.forName(DRIVER);
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
			}
			
			//链接数据库
			con = DriverManager.getConnection(URL, NAME, PASS);
			//执行sql
			pStemt = con.prepareStatement(sql);
			ResultSetMetaData rsmd = pStemt.getMetaData();
			int size = rsmd.getColumnCount(); // 统计列
			colnames = new String[size];
			colTypes = new String[size];
			colSizes = new int[size];
			
			boolean f_util = false; // 是否需要导入包java.util.*
			boolean f_sql = false; // 是否需要导入包java.sql.*
			boolean f_math = false; // 是否需要导入包java.sql.*
			
			for (int i = 0; i < size; i++) {
				if (!Arrays.asList(colsExc).contains(rsmd.getColumnName(i + 1).toLowerCase())) {
					colnames[i] = rsmd.getColumnName(i + 1);
					colTypes[i] = rsmd.getColumnTypeName(i + 1);
	 
					if (colTypes[i].equalsIgnoreCase("datetime") || colTypes[i].equalsIgnoreCase("Date")) {
						f_util = true;
					}
					if (colTypes[i].equalsIgnoreCase("image") || colTypes[i].equalsIgnoreCase("text")) {
						f_sql = true;
					}

					
					colSizes[i] = rsmd.getColumnDisplaySize(i + 1);
					
					
					if (colTypes[i].equalsIgnoreCase("NUMBER") && colSizes[i] < 39) {
						f_math = true;
					}
				}	
				
			}
			
			List<String> newCol = new ArrayList<>();
			for (String s : colnames) {
				if (s!=null) {
					newCol.add(s);
				}
			}
			colnames = newCol.toArray(new String[0]);
			
			List<String> newCoT = new ArrayList<>();
			for (String s : colTypes) {
				if (s!=null) {
					newCoT.add(s);
				}
			}
			colTypes = (String[]) newCoT.toArray(new String[0]);
			
			List<Integer> newcolS = new ArrayList<>();
			for (Integer s : colSizes) {
				if (s!=0) {
					newcolS.add(s);
				}
			}
			
			int [] arr = new int[newcolS.size()];
			for (int i = 0; i < newcolS.size(); i++) {
				arr[i] = newcolS.get(i);
			}
			colSizes = arr;
			
			//String content = parse(colnames, colTypes, colSizes, f_util, f_sql, f_math);
			String content = parseRep();
			
			try {
				FileWriter fw = new FileWriter(
						"E:\\PW\\bots\\rep\\"
								+ table2Rep(tablename) + ".java");
				PrintWriter pw = new PrintWriter(fw);
				pw.println(content);
				pw.flush();
				pw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		} catch (Exception e) {
			
			System.out.println(tablename +"ERRRO");
			e.printStackTrace();
		} finally {
			try {
				pStemt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			try {
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}
	
	/**
	 * 功能：生成实体类主体代码
	 * 
	 * @param colnames
	 * @param colTypes
	 * @param colSizes
	 * @return
	 */
	private String parse(String[] colnames, String[] colTypes, int[] colSizes, 
			boolean f_util, boolean f_sql, boolean f_math) {
		StringBuffer sb = new StringBuffer();
 
		// 判断是否导入工具包
		sb.append("package " + this.packageOutPath + ";\r\n");
		if (f_util) {
			sb.append("\r\nimport java.util.Date;\r\n");
		}
		if (f_sql) {
			sb.append("\r\nimport java.sql.*;\r\n");
		}
		if (f_math) {
			sb.append("\r\nimport java.math.BigDecimal;\r\n");
		}
		
		sb.append("\r\nimport lombok.Data;\r\n");
		sb.append("import lombok.EqualsAndHashCode;\r\n");
		sb.append("\r\n");
		sb.append("import javax.persistence.Entity;\r\n");
		sb.append("import javax.persistence.Table;\r\n");
		sb.append("\r\n");
		// 注释部分
		sb.append("/**\r\n");
		sb.append(" * " + tablename + " Entity\r\n");
		sb.append(" * " + new Date() + " " + this.authorName + "\r\n");
		sb.append(" */ \r\n");
		// 注解映射表
		sb.append("\r\n@Data\r\n");
		sb.append("@EqualsAndHashCode(callSuper = true)\r\n");
		sb.append("@Entity\r\n");
		sb.append("@Table(name = \""+ tablename +"\")\r\n");
		// 实体部分
		sb.append("public class " + table2Entity(tablename) + " extends BaseEntity {\r\n");
		sb.append("\r\n");
		processAllAttrs(sb);// 属性
		//processAllMethod(sb);// get set方法
		sb.append("}\r\n");
 
		// System.out.println(sb.toString());
		return sb.toString();
	}
	
	
	/**
	 * 功能：生成实体类主体代码
	 * 
	 * @param colnames
	 * @param colTypes
	 * @param colSizes
	 * @return
	 */
	private String parseRep() {
		StringBuffer sb = new StringBuffer();
 
		// 判断是否导入工具包
		sb.append("package " + "com.bottos.community.repository" + ";\r\n");
		//
		sb.append("\r\nimport org.springframework.data.repository.PagingAndSortingRepository;\r\n");
		sb.append("\r\nimport com.bottos.community.entity."+ table2Entity(tablename)  +";\r\n");
		sb.append("\r\n");
		// 注释部分
		sb.append("/**\r\n");
		sb.append(" * \r\n");
		sb.append(" * @author Tools \r\n");
		sb.append(" * @version $Revision:1.0.0, $Date: " + new Date() + " \r\n");
		sb.append(" */");
		// 实体部分
		sb.append("\r\npublic interface " + table2Rep(tablename) + " extends PagingAndSortingRepository<"+table2Entity(tablename)+", String> {\r\n");
		sb.append("\r\n");
		sb.append("\r\n");
		sb.append("}\r\n");
 
		// System.out.println(sb.toString());
		return sb.toString();
	}
	
	/**
	 * 功能：生成所有属性
	 * 
	 * @param sb
	 */
	private static void processAllAttrs(StringBuffer sb) {
 
		for (int i = 0; i < colnames.length; i++) {
			sb.append("\tprivate " + sqlType2JavaType(colTypes[i], colSizes[i]) + " " + column2Hump(colnames[i]) + ";\r\n");
		}
 
	}
	
	/**
	 * 功能：生成所有方法
	 * 
	 * @param sb
	 */
/*	private static void processAllMethod(StringBuffer sb) {
 
		for (int i = 0; i < colnames.length; i++) {
			sb.append("\tpublic void set" + initcap(colnames[i]) + "(" + sqlType2JavaType(colTypes[i]) + " "
					+ colnames[i] + "){\r\n");
			sb.append("\tthis." + colnames[i] + "=" + colnames[i] + ";\r\n");
			sb.append("\t}\r\n");
			sb.append("\tpublic " + sqlType2JavaType(colTypes[i]) + " get" + initcap(colnames[i]) + "(){\r\n");
			sb.append("\t\treturn " + colnames[i] + ";\r\n");
			sb.append("\t}\r\n");
		}
 
	}*/
	
	/**
	 * 功能：将输入字符串的首字母改成大写
	 * 
	 * @param str
	 * @return
	 */
	private static String initcap(String str) {
 
		char[] ch = str.toCharArray();
		if (ch[0] >= 'a' && ch[0] <= 'z') {
			ch[0] = (char) (ch[0] - 32);
		}
 
		return new String(ch);
	}
	
	/**
	 * 去除前缀"BOT_T_"
	 * @param table
	 * @return
	 */
	private static String table2Entity(String table) {
		
		table = table.substring(6, table.length()).toLowerCase();
		char [] cc = table.toCharArray();
		
		for (int i = 0; i < cc.length; i ++) {
			int c = cc[i];
			if (c == '_') {
				cc[i+1] = (char) ((char) cc[i+1] - 32);
			}
		}
		
		cc[0] = (char) (cc[0] -32);
		
		table = String.valueOf(cc);
		table = table.replace("_", "") + "Entity";
		return table;
	}
	
	private static String table2Rep(String table) {
		
		table = table.substring(6, table.length()).toLowerCase();
		char [] cc = table.toCharArray();
		
		for (int i = 0; i < cc.length; i ++) {
			int c = cc[i];
			if (c == '_') {
				cc[i+1] = (char) ((char) cc[i+1] - 32);
			}
		}
		
		cc[0] = (char) (cc[0] -32);
		
		table = String.valueOf(cc);
		table = table.replace("_", "") + "Repository";
		return table;
	}
	
	private static String column2Hump(String column) {
		column = column.toLowerCase();
		
		char [] cc = column.toCharArray();
		for (int i = 0; i < cc.length; i ++) {
			int c = cc[i];
			if (c == '_') {
				cc[i+1] = (char) ((char) cc[i+1] - 32);
			}
		}
		
		column = String.valueOf(cc);
		column = column.replace("_", "");
		
		return column;
	}
	
	/**
	 * 功能：获得列的数据类型
	 * 
	 * @param sqlType
	 * @return
	 */
	private static String sqlType2JavaType(String sqlType, int size) {
 
		if (sqlType.equalsIgnoreCase("bit")) {
			return "boolean";
		} else if (sqlType.equalsIgnoreCase("tinyint")) {
			return "byte";
		} else if (sqlType.equalsIgnoreCase("smallint")) {
			return "short";
		} else if (sqlType.equalsIgnoreCase("int")) {
			return "int";
		} else if (sqlType.equalsIgnoreCase("bigint")) {
			return "long";
		} else if (sqlType.equalsIgnoreCase("float")) {
			return "float";
		} else if (sqlType.equalsIgnoreCase("decimal") || sqlType.equalsIgnoreCase("numeric")
				|| sqlType.equalsIgnoreCase("real") || sqlType.equalsIgnoreCase("money")
				|| sqlType.equalsIgnoreCase("smallmoney")) {
			return "double";
		} else if (sqlType.equalsIgnoreCase("varchar") || sqlType.equalsIgnoreCase("char")
				|| sqlType.equalsIgnoreCase("nvarchar") || sqlType.equalsIgnoreCase("nchar")
				|| sqlType.equalsIgnoreCase("text")) {
			return "String";
		} else if (sqlType.equalsIgnoreCase("datetime")) {
			return "Date";
		} else if (sqlType.equalsIgnoreCase("image")) {
			return "Blod";
		} else if (sqlType.equalsIgnoreCase("Date")) {
			return "Date";
		} else if (sqlType.equalsIgnoreCase("varchar2")) {
			return "String";
		} else if (sqlType.equalsIgnoreCase("INTEGER")) {
			return "Integer";
		} else if (sqlType.equalsIgnoreCase("Number") && size < 39) {
			return "BigDecimal";
		} else if (sqlType.equalsIgnoreCase("Number") && size == 39) {
			return "Integer";
		}
		
		return null;
	}
	
	
	/**
	 * 出口 TODO
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
 
//		String []arr= {
//				"BOT_T_AFFICHE_INFO","BOT_T_ATTACHMENT_INFO","BOT_T_AUDIT_INFO","BOT_T_BANNER_INFO",
//				"BOT_T_CUST_INFO","BOT_T_CUST_MISSION_INFO","BOT_T_DEVICE_INFO","BOT_T_DTO_INFO",
//				"BOT_T_DTO_TRADE_INFO","BOT_T_FEED_BACK_INFO","BOT_T_INTEGRAL_INFO","BOT_T_INTEGRAL_TRADE_INFO",
//				"BOT_T_LOGIN_LOG_INFO","BOT_T_LOG_INFO","BOT_T_MEDAL_INFO","BOT_T_MISSION_INFO",
//				"BOT_T_REPLY_INFO","BOT_T_SMS_INFO","BOT_T_SMS_LOG_INFO","BOT_T_STEP_INFO","BOT_T_TOPIC_INFO",
//				"BOT_T_WALLET_ADDRESS_INFO","BOT_T_WATT_TRADE_INFO","BOT_T_Watt_INFO","COM_T_PARAM","COM_T_USER"
//				
//		};
//		
//		for (String a : arr) {
//			new CreateJavaFromDb(a);	
//		}
		
		new CreateJavaFromDb("BOT_T_AIR_DROP_INFO");	
		
		
		//System.out.println(table2Entity("BOT_T_DTO_TRADE_INFO"));
 
	}

}
