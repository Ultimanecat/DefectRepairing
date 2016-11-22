package DefectRepairing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LineNumberPreProcessor {

	static String source;
	public static int curLine = 0;
	public static int curChar = 0;
	public static String outputBuffer = new String();

	public static void copyaLine() {
		curLine++;
		int preChar = curChar;
		while (true) {
			char c = source.charAt(curChar);
			if (c == '\n')
				break;
			curChar++;
		}
		outputBuffer += source.substring(preChar, curChar)+"    //"+curLine+"\n";
		curChar++;
	}
	
	
	public static void process(String DirPath) 
	{
		List<String>filelist=new ArrayList<String>();
		Instrumenter.getFilelist(DirPath, filelist);
		for(String filepath:filelist)
		{
			outputBuffer = new String();
			curChar = 0;
			curLine = 0;
			System.out.println(filepath);
			source=Instrumenter.readFileToString(filepath);
			while(curChar<source.length())
				copyaLine();
			Instrumenter.writeStringToFile(filepath,outputBuffer);
		}
	}
	
	public static void main(String[] args) {
		
		process("/Users/liuxinyuan/DefectRepairing/Math3b/src/main/");
		
	}
	
	
}
