package file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CreateJavaFromSQL {
	
	public static void main(String[] args) {
		
		// 1、读取文件
		File file = new File("E:\\PW\\bots\\create.sql");
		File writerFile = null;
		FileReader fr = null;
		FileWriter fw = null;
		
		BufferedReader br = null;
		BufferedWriter bw= null;
		
		List<String> tables = new ArrayList<>();
		
		try {
			fr = new FileReader(file);
			br = new BufferedReader(fr);
			String single = "";
			String table = "";
			while (!"".equals(single = br.readLine())) {
				if (single.contains("create")) {
					String [] arr = single.split(" ");
					table = arr[2].trim();
					tables.add(table);
				}
				
				
			}
			
			// 记录表
			
			if (single.contains("drop")) {
				String [] arr = single.split(" ");
				table = arr[2].trim();
				tables.add(table);
			}
			
			while (single.contains("create")) {
				
			}
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				fr.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	

}
