package TestCase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import DefectRepairing.parser.Spectrum;

public class classifier {

	
	public static void getFilelist(String DirPath, List<String> FileList) {
		File RootDir = new File(DirPath);
		File[] files = RootDir.listFiles();

		for (File f : files) {
			if (f.isDirectory()) {
				getFilelist(f.getAbsolutePath(), FileList);
			} else {
				if (true)
					FileList.add(f.getAbsolutePath());
			}
		}
	}

	@SuppressWarnings("resource")
	public static List<String> get_failing_tests(String Project,String Bug_id)
	{
		List<String>l=new ArrayList<String>();
		String filepath="/Users/liuxinyuan/DefectRepairing/defects4j/"+"framework/projects/"+Project+"/trigger_tests/"+Bug_id;
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
	
	public static String[] split(String s){
		String[] s_a=null;
		if(s.contains("::"))
			s_a=s.split("::");
		else if(s.contains(":"))
			s_a=s.split(":");
		else if(s.contains("__"))
			s_a=s.split("__");
		return s_a;
	}
	
	public static boolean in_list(List<String>l,String s){
		String[] s_a=split(s);
		
		for(String s0:l){
			String[] s0_a=split(s0);
			if(s_a[0].equals(s0_a[0])&&s_a[1].equals(s0_a[1])){
				return true;
			}
		}
		
		return false;
	}
	
	public static void main(String args[]) throws FileNotFoundException, IOException {
		boolean verbose=true;
		
		String project="Chart";//args[0];
		String bugid="15";//args[1];
		String patch_no="Patch13";//args[2];
		String tracedir="/Volumes/Unnamed";//args[3];
		String patchdir="/Volumes/Unnamed/instr/patches";//args[4]
		tracedir=new File(tracedir, project+bugid+"b_"+patch_no).toString();
		
		List<String>l_buggy=new ArrayList<String>();
		List<String>l_patched=new ArrayList<String>();
		String tracedir_buggy=new File(tracedir, "buggy_e").toString();
		String tracedir_patched=new File(tracedir, "patched_e").toString();
		getFilelist(tracedir_buggy,l_buggy);
		getFilelist(tracedir_patched,l_patched);
		List<String>l=new ArrayList<String>();
		for(String s:l_buggy){
			String[] a=s.split("/");
			l.add(a[a.length-1]);
		}
		List<String> failing_tests=get_failing_tests(project,bugid);
//		for(String s:failing_tests){
//				System.out.println(s);
//		}
//		for(String s:l){
//			if(in_list(failing_tests,s))
//				System.out.println(s);
//		}
		
		int len=l.size();
		double[][] dis=new double[len][len];
		String[] dict=new String[len];//index to string
		Set<Integer> pass=new TreeSet<Integer>();
		Set<Integer> gen=new TreeSet<Integer>();
		Set<Integer> fail=new TreeSet<Integer>();
		int index=0;
		for(String s:l){
			dict[index]=s;
			if(s.startsWith("Randoop")){
				gen.add(index);
			}
			else if(in_list(failing_tests,s)){
				fail.add(index);
			}
			else {
				pass.add(index);
			}
			index++;
		}
		Spectrum[] SpecArray_buggy=new Spectrum[len];
		Spectrum[] SpecArray_patched=new Spectrum[len];
		for(int i=0;i<len;i++) {
			SpecArray_buggy[i]=new Spectrum();
			String TraceFile=new File(tracedir_buggy, dict[i]).toString();;
			SpecArray_buggy[i].form(DefectRepairing.parser.parsetrace(new BufferedReader(new FileReader(TraceFile))));
			
			SpecArray_patched[i]=new Spectrum(new File(patchdir, patch_no).toString());
			TraceFile=new File(tracedir_patched, dict[i]).toString();
			SpecArray_patched[i].form(DefectRepairing.parser.parsetrace(new BufferedReader(new FileReader(TraceFile))));
		}
		for(int i=0;i<len;i++){
			for(int j=0;j<len;j++){
				if(i==j){
					dis[i][j]=0.0;
					continue;
				}
					
				double Length=(SpecArray_buggy[i].values.size()+SpecArray_buggy[j].values.size());
				double LCS=SpecArray_buggy[i].diff(SpecArray_buggy[j],new Spectrum.Mode(Spectrum.Mode.ModeEnum.LCS, 0, 1, 2));
				
				dis[i][j]=LCS/Length;
						
			}
		}
		if(verbose){
			for(int i=0;i<len;i++){
				for(int j=0;j<len;j++){
					System.out.printf("%.6f ",dis[i][j]);
				}
				System.out.println();
			}
			System.out.println();
		}
		
		
		//TODO merge similar execution, KNN or completely equal
		
		double[] dis_2=new double[len];
		for(int i=0;i<len;i++){
			double Length=(SpecArray_buggy[i].values.size()+SpecArray_patched[i].values.size());
			double LCS=SpecArray_buggy[i].diff(SpecArray_patched[i],new Spectrum.Mode(Spectrum.Mode.ModeEnum.LCS, 0, 1, 2));
			dis_2[i]=LCS/Length;
		}
		if(verbose){
			for(int j=0;j<len;j++){
				System.out.printf("%.6f ",dis_2[j]);
			}
			System.out.println();
			for(int j=0;j<len;j++){
				if(pass.contains(j))
					System.out.print("    pass ");
				if(fail.contains(j))
					System.out.print("    fail ");
				if(gen.contains(j))
					System.out.print("     gen ");
			}
			
		}
	}
	
}
