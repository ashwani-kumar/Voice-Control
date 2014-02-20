package com.parser;

import java.io.BufferedReader;

public class InstalledAppInfoParser {
	
	public void readInstalledAppInfoFile(){
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;
		while ((line = br.readLine()) != null) {
		   // process the line.
		}
		br.close();
	}
	public static final String displayName = "DisplayName";	
	public static final String pathString = "UninstallString";
	public static final String dataStartString = "HKEY_LOCAL_MACHINE";
	

}
