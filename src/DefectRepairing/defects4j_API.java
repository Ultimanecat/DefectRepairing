package DefectRepairing;

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

public class defects4j_API {
	public static String PathToD4j;
	
	public static void execcommand(String [] command,StringBuilder STDOUT,StringBuilder STDERR)
	{
		//command=new String[]{"sh","-c","which defects4j"};
		try{
			Process p = Runtime.getRuntime().exec(command);
			p.waitFor();

			BufferedReader stderr = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			BufferedReader stdout = new BufferedReader(new InputStreamReader(p.getInputStream()));
			
			String line = "";           
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
		//StringBuilder stdout=new StringBuilder();
		//execcommand(new String[]{"sh","-c","ls"},stdout,new StringBuilder());
//		init();
//		List<String>l=get_failing_tests("Math",3);
//		for(String str:l)
//			System.out.println(str);
		init();
		StringBuilder stdout=new StringBuilder();
		StringBuilder stderr=new StringBuilder();
		execcommand(new String[]{"perl",PathToD4j+"framework/bin/defects4j","info","-p","Math"},stdout,stderr);
		System.out.print(stdout);
		System.out.print(stderr);
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
