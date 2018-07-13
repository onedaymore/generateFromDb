package file;

public class Tests {
	
	public static void main(String[] args) {
		
		String s  ="BOT_T_DEVICE_INFO";
		
		System.out.println(s.substring(6, s.length()));
//		char [] cc = s.toCharArray();
//		cc [0] = (char) (cc [0] + 32); 
//		
//		s= String.valueOf(cc);
//	System.out.println(s);
		
		System.out.println(table2Entity(s));
	}
	
	
	private static String table2Entity(String table) {
		
		table = table.substring(6, table.length()).toLowerCase();
		char [] cc = table.toCharArray();
		
		for (int i = 0; i < cc.length; i ++) {
			int c = cc[i];
			if (c == '_') {
				cc[i+1] = (char) ((char) cc[i+1] - 32);
			}
		}
		
		table = String.valueOf(cc);
		table = table.replace("_", "") + "Entity";
		return table;
	}

}
