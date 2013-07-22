package com.untamedears.recivchat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.bukkit.configuration.file.FileConfiguration;

public class LogCompressionRunnable implements Runnable {
	@Override
	public void run() {
		File logFolder = CivChat.instance.getLogFolder();
		
		File[] files = logFolder.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				if(name.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}[.]txt"))
					return true;
				else
					return false;
			}
		});
		
		TreeMap<Date, File> logs = new TreeMap<Date, File>();
		
		for(File file : files) {
			Date date = getDateFromFileName(file.getName());
			
			if(date != null) {
				logs.put(date, file);
			}
		}
		
		FileConfiguration config = CivChat.instance.getConfig();
		
		int zipSize = config.getInt("logs.compression.zipSize", 5);
		int keepMinimum = config.getInt("logs.compression.keepMinimum", 5);
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		
		byte[] buffer = new byte[1024];
		
		int compressed = 0;
		
		while(logs.size() >= zipSize + keepMinimum) {
			Date firstDate = logs.firstKey();

			try {
				File zipFile = new File(logFolder, format.format(firstDate) + ".zip");
				zipFile.createNewFile();
				FileOutputStream fos = new FileOutputStream(zipFile);
				ZipOutputStream zos = new ZipOutputStream(fos);

				for(int i = 0; i < zipSize; i++) {
					Entry<Date, File> entry = logs.firstEntry();
					logs.remove(entry.getKey());
					
					File file = entry.getValue();
					ZipEntry zipEntry = new ZipEntry(file.getName());
					zos.putNextEntry(zipEntry);
					
					FileInputStream fis = new FileInputStream(file);
					
					int len;
					
					while((len = fis.read(buffer)) > 0) {
						zos.write(buffer, 0, len);
					}
					
					fis.close();
					zos.closeEntry();
					zos.flush();
					
					file.delete();
					
					compressed++;
				}
				
				zos.close();
			}
			catch(IOException e) {
				e.printStackTrace();
			}
		}
		
		CivChat.instance.getLogger().info("Compressed " + compressed + " log files.");
	}
	
	private static Date getDateFromFileName(String fileName) {
		String[] nameParts = removeSuffix(fileName).split("-");
		
		if(nameParts.length != 3)
			return null;
		
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		
		try {
			calendar.set(Integer.parseInt(nameParts[0]), Integer.parseInt(nameParts[1]), Integer.parseInt(nameParts[2]));
		}
		catch(NumberFormatException e) {
			return null;
		}
		
		return calendar.getTime();
	}
	
	private static String removeSuffix(String fileName) {
		int suffixLoc = fileName.lastIndexOf('.');
		
		if(suffixLoc >= 0)
			return fileName.substring(0, suffixLoc);
		else
			return fileName;
	}
}
