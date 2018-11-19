package TestCase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.wickedsource.diffparser.api.DiffParser;
import org.wickedsource.diffparser.api.UnifiedDiffParser;
import org.wickedsource.diffparser.api.model.Diff;
import org.wickedsource.diffparser.api.model.Hunk;
import org.wickedsource.diffparser.api.model.Line;


import DefectRepairing.jPickle;
public class TraceParser {
	
	public static int diff(ArrayList<Integer>spec1,ArrayList<Integer>spec2){
		int difftype = 1;
                Iterator<Integer> it1 = spec1.iterator(), it2 = spec2.iterator();
                if(difftype == 0)
                {
                        Map<Integer,Integer> map1 = new HashMap<Integer,Integer>();
                        Map<Integer,Integer> map2 = new HashMap<Integer,Integer>();
                        int ans = 0;
                        while(it1.hasNext()) {
                                Integer l1 = it1.next();
                                if(map1.containsKey(l1))map1.put(l1,map1.get(l1)+1);
                                else map1.put(l1,1);
                        }
                        while(it2.hasNext()) {
                                Integer l2 = it2.next();
                                if(map2.containsKey(l2))map2.put(l2,map2.get(l2)+1);
                                else map2.put(l2,1);
                        }
                        for(Integer i : map2.keySet())
                        {
                                if(map1.containsKey(i))
                                {
                                        if(map1.get(i)<map2.get(i))ans+=map1.get(i);
                                        else ans+=map2.get(i);
                                }
                        }
                        return ans;
                }
                int f[][]= new int[2][spec2.size()+1];
		int min = spec2.size() < spec1.size() ? spec2.size() : spec1.size();
		
		for (int i = 1; it1.hasNext(); i++) {
			Integer l1 = it1.next();
			it2=spec2.iterator();
			for (int j = 1; it2.hasNext(); j++) {
				Integer l2 = it2.next();
				if (l1.compareTo(l2)==0) {
					f[i%2][j] = f[(i - 1)%2][j - 1] + 1;
				} else if (f[(i - 1)%2][j] <= f[i%2][j - 1]) {// 优先让spec2失配
					f[i%2][j] = f[i%2][j - 1];
				} else {
					f[i%2][j] = f[(i - 1)%2][j];
				}
			}
		}
		return f[spec1.size()%2][spec2.size()];
	}
	
	public static ArrayList<Integer> form(BufferedReader reader) throws IOException{
		ArrayList<Integer> spec = new ArrayList<Integer>();
		for (String line = reader.readLine(); line != null; line = reader.readLine()) {
			line=line.trim();
			if(line.startsWith("---")){
				line=line.split(":")[1];
				spec.add(Integer.valueOf(line));
			}
		}
		return spec;
	}
	
	public static ArrayList<Integer> form(BufferedReader reader, Map<Integer, Integer> LNMap) throws IOException{
		ArrayList<Integer> spec = new ArrayList<Integer>();
		for (String line = reader.readLine(); line != null; line = reader.readLine()) {
			line=line.trim();
			if(line.startsWith("---")){
				line=line.split(":")[1];
				spec.add(LNMap.get(Integer.valueOf(line)));
			}
		}
		return spec;
	}
	
	public static Map<Integer, Integer> getLineMap(int alllines, String filepath) {
		DiffParser parser = new UnifiedDiffParser();
		InputStream in = null;
		try {
			in = new FileInputStream(filepath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		List<Diff> diff = parser.parse(in);
		Diff d = diff.get(0);
		Map<Integer, Integer> lnmap = new HashMap<Integer, Integer>();
		List<Hunk> hunks = d.getHunks();
		int fromp = -1, top = -1;
		for (Iterator<Hunk> it = hunks.iterator(); it.hasNext();) {
			Hunk h = it.next();
			List<Line> lines = h.getLines();
			Iterator<Line> lit = lines.iterator();

			if (fromp != -1) {
				while (fromp < h.getFromFileRange().getLineStart() - 1) {
					lnmap.put(++top, ++fromp);
				}
			} else {
				fromp = h.getFromFileRange().getLineStart() - 1;
				top = h.getToFileRange().getLineStart() - 1;
			}
			while (lit.hasNext()) {
				Line l = lit.next();
				switch (l.getLineType()) {
				case FROM:
					fromp++;
					break;
				case TO:
					lnmap.put(++top, -1);
					break;
				case NEUTRAL:
					lnmap.put(++top, ++fromp);
					break;
				}
			}
		}
		while (top < alllines) {
			lnmap.put(++top, ++fromp);
		}
		for(int i=1;i<=alllines;i++)
		{
			if(!lnmap.containsKey(i)){
				lnmap.put(i, i);
			}
			
		}
		return lnmap;
	}

	
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
        public static String Path_to_d4j;
	@SuppressWarnings("resource")
	public static List<String> get_failing_tests(String Project,String Bug_id)
	{
		List<String>l=new ArrayList<String>();
		String filepath=Path_to_d4j+Project+"/trigger_tests/"+Bug_id;
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
		boolean verbose=false;
		String project,bugid,patch_no;
		String tracedir=args[3];
                String patchdir=args[4];
		project=args[0];
		bugid=args[1];
		patch_no=args[2];
                Path_to_d4j=args[5];
                
		run( project, bugid, patch_no, tracedir, patchdir, verbose);
//		run( "Chart", "15", "Patch13", tracedir, patchdir, verbose);
		

		
		
		
	}
	
	public static void run(String project,String bugid,String patch_no,String tracedir,String patchdir,boolean verbose) throws FileNotFoundException, IOException{

                tracedir=new File(tracedir, project+bugid+"b_"+patch_no).toString();
                
                List<String>l_buggy=new ArrayList<String>();
                List<String>l_patched=new ArrayList<String>();
                String tracedir_buggy=new File(tracedir, "buggy_e").toString();
                String tracedir_patched=new File(tracedir, "patched_e").toString();
                getFilelist(tracedir_buggy,l_buggy);
                getFilelist(tracedir_patched,l_patched);
                
                //Match traces of tests in both patched and buggy version
                List<String>l=new ArrayList<String>();
                for(String s:l_buggy){
                        String[] a=s.split("/");
                        String testname=a[a.length-1];
                        for(String s1:l_patched){
                                if(s1.endsWith(testname)){
                                        
                                        l.add(testname);
                                        break;
                                }
                        }
                        
                }
                List<String> failing_tests=get_failing_tests(project,bugid);
                
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
                if(verbose){
                System.out.println(fail);
                System.out.println(pass);
                }
                
                List<Integer>remove_list=new ArrayList<Integer>();
                ArrayList[] SpecArray_buggy=new ArrayList[len];
                ArrayList[] SpecArray_patched=new ArrayList[len];
                
                for(int i=0;i<len;i++) {
                        
                        String TraceFile=new File(tracedir_buggy, dict[i]).toString();
                        
                        try {
                                SpecArray_buggy[i]=form(new BufferedReader(new FileReader(TraceFile)));
                        } catch (Exception e) {
                                e.printStackTrace();
                                remove_list.add(i);
                                continue;
                        }
                        
                        Map<Integer, Integer> LNMap = getLineMap(3000,new File(patchdir, patch_no).toString());
                        
                        
                        TraceFile=new File(tracedir_patched, dict[i]).toString();
                        
                        try {
                                SpecArray_patched[i]=form(new BufferedReader(new FileReader(TraceFile)),LNMap);
                        } catch (Exception e) {
                                e.printStackTrace();
                                remove_list.add(i);
                                continue;
                        }
                }
                if(verbose)
                System.out.println("1 "+remove_list);
                for(int i=0;i<len;i++){
                        for(int j=0;j<len;j++){
                                if(i==j){
                                        dis[i][j]=0.0;
                                        continue;
                                }
                                        
                                double Length=Math.max(SpecArray_buggy[i].size(),SpecArray_buggy[j].size());
                                if(Length==0){
                                        dis[i][j]=1;
                                        continue;
                                }
                                double LCS;
                                if(SpecArray_buggy[i].size()*SpecArray_buggy[j].size()>2147483647){
                                        remove_list.add(i);
                                        continue;
                                }
                                try{
                                        LCS=diff(SpecArray_buggy[i],SpecArray_buggy[j]);
                                } catch(Exception e){
                                        e.printStackTrace();
                                        remove_list.add(i);
                                        continue;
                                }
                                dis[i][j]=1-LCS/Length;
                                                
                        }
                }
                if(verbose)
                System.out.println(2+" "+remove_list);
                //filter
                
//              for(int j=0;j<len;j++){
//                      if(remove_list.contains(j)){
//                              continue;
//                      }
//                      if(SpecArray_buggy[j].values.size()==0 && SpecArray_patched[j].values.size()==0)
//                      {
//                              remove_list.add(j);
//                      }
//              }
                //System.out.println(remove_list);

                if(verbose)
                System.out.println(3+" "+remove_list);
                if(false)
                for(Iterator<Integer> it1=gen.iterator();it1.hasNext();){
                        int i=it1.next();
                        for(Iterator<Integer> it2=gen.iterator();it2.hasNext();){
                                int j=it2.next();
                                if(i==j)
                                        continue;
                                //TODO completely equal on patched trace
                                if(dis[i][j]<0.00001 && (!remove_list.contains(i)) && (!remove_list.contains(j)))
                                        remove_list.add(j);
                        }
                }
                //System.out.println(4+" "+remove_list);
                //System.out.println(remove_list);
                double length_array[]=new double[len];
                double LCS_array[]=new double[len];
                double[] dis_2=new double[len];
                for(int i=0;i<len;i++){
                        if(remove_list.contains(i)){
                                continue;
                        }
                        if(verbose)System.out.println(dict[i]);
                        ArrayList<Integer> spec1=null;
                        String TraceFile=new File(new File(tracedir, "buggy").toString(), dict[i]).toString();
                        //System.out.println(TraceFile);
                        try{
                                spec1=form(new BufferedReader(new FileReader(TraceFile)));
                        } catch(Exception e){
                                e.printStackTrace();
                                System.out.println(i);
                                System.out.println(TraceFile);
                                remove_list.add(i);
                                continue;
                        }
                        
                        
                        ArrayList<Integer> spec2=null;
                        TraceFile=new File(new File(tracedir, "patched").toString(), dict[i]).toString();
                        try{
                                spec2=form(new BufferedReader(new FileReader(TraceFile)),getLineMap(3000,new File(patchdir, patch_no).toString()));
                        } catch(Exception e){
                                e.printStackTrace();
                                
                                remove_list.add(i);
                                continue;
                        }
                        if((double)spec1.size()*(double)spec2.size()>5e9 && ! (fail.contains(i) && fail.size()==1)){
                                remove_list.add(i);
                                continue;
                        }
                        double Length=Math.max(spec1.size(),spec2.size());
                        double LCS=diff(spec1,spec2);
                        dis_2[i]=1-LCS/Length;
                        length_array[i]=Length;
                        LCS_array[i]=LCS;
                        if(verbose)System.out.println(LCS);
                        
                }
                
                if(verbose){
                        for(int i=0;i<len;i++){
                                if(remove_list.contains(i)){
                                        continue;
                                }
                                for(int j=0;j<len;j++){
                                        if(remove_list.contains(j)){
                                                continue;
                                        }
                                        System.out.printf("%.6f ",dis[i][j]);
                                }
                                System.out.println();
                        }
                        System.out.println();
                }
                
                if(verbose){
                        for(int j=0;j<len;j++){
                                if(remove_list.contains(j)){
                                        continue;
                                }
                                System.out.printf("%.6f ",dis_2[j]);
                        }
                        System.out.println();
                        for(int j=0;j<len;j++){
                                if(remove_list.contains(j)){
                                        continue;
                                }
                                if(pass.contains(j))
                                        System.out.print("    pass ");
                                if(fail.contains(j))
                                        System.out.print("    fail ");
                                if(gen.contains(j))
                                        System.out.print("     gen ");
                        }
                        System.out.println();
                }
                for (Integer j:remove_list){
                        if(pass.contains(j))
                                pass.remove(j);
                        if(fail.contains(j))
                                fail.remove(j);
                        if(gen.contains(j))
                                gen.remove(j);
                }
              jPickle.dump(pass, patch_no+"/pass");
              jPickle.dump(fail, patch_no+"/fail");
              jPickle.dump(gen, patch_no+"/gen");
              jPickle.dump(dis, patch_no+"/dis");
              jPickle.dump(dis_2, patch_no+"/dis_2");
              jPickle.dump(dict, patch_no+"/dict");
              jPickle.dump(length_array, patch_no+"/Length_array");
              jPickle.dump(LCS_array, patch_no+"/LCS_array");  
                
                double dis_pass=0,dis_fail=0,w_pass=0,w_fail=0;
                if(pass.size()!=0&&fail.size()!=0){
                        for(int i:pass){
                                dis_pass+=dis_2[i];
                                w_pass+=1;
                        }
                        for(int i:fail){
                                dis_fail+=dis_2[i]/fail.size();
                                w_fail+=1;
                        }
                        if(gen.size()!=0){
                                for(int i:gen){
                                        double dis_p=1,dis_f=1;
                                        for(int j:pass){
                                                if(dis_2[j]<dis_p)
                                                        dis_p=dis[i][j];
                                        }
                                        for(int j:fail){
                                                if(dis_2[j]<dis_f)
                                                        dis_f=dis[i][j];
                                        }
                                        if(dis_p<dis_f){
                                                //pass
                                                dis_pass+=dis_2[i]*(1-dis_p);
                                                w_pass+=1-dis_p;
                                        } else {
                                                //fail
                                                dis_fail+=dis_2[i]*(1-dis_f);
                                                w_fail+=1-dis_f;
                                        }
                                }
                        }
                }
                
                dis_pass=dis_pass/w_pass;
                dis_fail=dis_fail/w_fail;
                
                if(dis_pass>dis_fail){
                        System.out.println("Incorrect");
                } else System.out.println("Correct");
                System.out.printf("%.4f %.4f\n",dis_pass,dis_fail);
        }
        
}

