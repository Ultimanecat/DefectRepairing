package defects4j;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.ArrayUtils;

public class defects4j {
	public static String PathToD4j;
	
	public static void execcommand(String [] command,StringBuilder STDOUT,StringBuilder STDERR)
	{
		//command=new String[]{"sh","-c","which defects4j"};
		try{
			Process p = Runtime.getRuntime().exec(command);
			p.waitFor();

			BufferedReader stderr = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			BufferedReader stdout = new BufferedReader(new InputStreamReader(p.getInputStream()));
			
			String line;           
			while ((line = stderr.readLine())!= null) {
				STDERR.append(line+"\n");
			}
			
			while ((line = stdout.readLine())!= null) {
				STDOUT.append(line+"\n");
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void run(String [] command,StringBuilder STDOUT,StringBuilder STDERR)
	{
		execcommand((String[])ArrayUtils.addAll(new String[]{"perl",PathToD4j+"framework/bin/defects4j"}, command),STDOUT,STDERR);
	}
	
	public static List<String> get_failing_tests(String Project,int Bug_id)
	{
		List<String>l=new ArrayList<String>();
		String filepath=PathToD4j+"framework/projects/"+Project+"/trigger_tests/"+Bug_id;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filepath));
			String line;
			Pattern pattern=Pattern.compile("^--- (.)*$");
			
			while((line=reader.readLine())!=null) {
				Matcher matcher = pattern.matcher(line);
				if(matcher.matches())
					l.add(line.substring(4,line.length()));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return l;
	}
	
	
	
	public static void main(String[] args) {
		init();
		
		BuggyVersion bug=new BuggyVersion("Math",3,"/Users/liuxinyuan/DefectRepairing/Math3b");
		bug.checkout();
		
	}
	
	public static void init(){
		try {
			BufferedReader reader = new BufferedReader(new FileReader("config"));
			String line;
			if((line=reader.readLine())!=null){
				PathToD4j=line;
				if(!PathToD4j.endsWith("/")){
					PathToD4j+="/";
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
