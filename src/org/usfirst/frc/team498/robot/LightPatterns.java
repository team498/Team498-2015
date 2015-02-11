package org.usfirst.frc.team498.robot;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List; 

public class LightPatterns {
	public LightParser lightPattern1;
	public LightParser lightPattern2;
	public LightParser lightPattern3;
	public LightParser lightPattern4;
	public LightParser strobe;
	public LightParser off;
	public LightPatterns() {
		//lightPattern1=new LightParser("1 0 0 0 1000\n0 0 0 1 1000\n0 0 1 0 1000\n0 0 0 1 1000");
		try {
			List<String> Pattern1 = Files.readAllLines(Paths.get("/home/lvuser/Pattern1.txt"));
			List<String> Pattern2 = Files.readAllLines(Paths.get("/home/lvuser/Pattern2.txt"));
			List<String> Pattern3 = Files.readAllLines(Paths.get("/home/lvuser/Pattern3.txt"));
			List<String> Pattern4 = Files.readAllLines(Paths.get("/home/lvuser/Pattern4.txt"));
			List<String> strobePattern = Files.readAllLines(Paths.get("/home/lvuser/Strobe.txt"));
			List<String> offPattern = Files.readAllLines(Paths.get("/home/lvuser/Off.txt"));
			lightPattern1=new LightParser(Pattern1);
			lightPattern2=new LightParser(Pattern2);
			lightPattern3=new LightParser(Pattern3);
			lightPattern4=new LightParser(Pattern4);
			strobe=new LightParser(strobePattern);
			off=new LightParser(offPattern);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
