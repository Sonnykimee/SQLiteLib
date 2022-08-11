package me.sonny.SQLiteLib;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.sql.PreparedStatement;

public class Test {
	public static void main(String[] args) {
		String url = "D:\\Users\\Sonny Kim\\Documents\\06. Game\\Minecraft\\YouTubeServer_1_18_2\\plugins\\TriggerReactor\\SavedData\\DB\\test.db";
		
		SQLite testDb = new SQLite();
		
		if (testDb.connect(url)) {
			try {
				File file = new File("D:\\Users\\Sonny Kim\\Documents\\06. Game\\Minecraft\\YouTubeServer_1_18_2\\plugins\\cat.jpg");
				
				if (file.exists()) {
					FileInputStream fis = new FileInputStream(file);
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					byte[] buffer = new byte[1024];
					
					for (int len; (len = fis.read(buffer)) != -1;) {
		                bos.write(buffer, 0, len);
		            }
					
					PreparedStatement ps = testDb.connection().prepareStatement("UPDATE TEST SET PROFILE = ?");
					ps.setBytes(1, bos.toByteArray());
					ps.executeUpdate();
					ps.close();
					
					fis.close();
					bos.close();
				}
			} catch (Exception e) {
				System.out.println("Test Error: " + e.getMessage());
			}
		}
		
		System.out.println("END TEST");
	}
}
