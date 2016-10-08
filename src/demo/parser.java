package demo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class parser {
	
	private static class Variable implements Cloneable{
		String Name;
		String Type;
		Object Value;
		Boolean Defined;
		Variable(){}
		Variable(String _Name,String _Type,Object _Value)
		{
			Name=_Name;
			Type=_Type;
			Value=_Value;
		}
		public Object clone() 
		{   
	        Variable o = null;   
	        try {   
	            o = (Variable) super.clone();   
	        } catch (CloneNotSupportedException e) {   
	            e.printStackTrace();   
	        }   
	        return o; 
	    } 
	}
	private static class VarInt extends Variable{
		int Value;
		VarInt(String _Name,String _Type,int _Value)
		{
			Name=_Name;
			Type=_Type;
			Value=_Value;
		}
	}
	private static class VarString extends Variable{
		String Value;
		VarString(String _Name,String _Type,String _Value)
		{
			Name=_Name;
			Type=_Type;
			Value=_Value;
		}
	}
	private static class VarShort extends Variable{
		short Value;
		VarShort(String _Name,String _Type,short _Value)
		{
			Name=_Name;
			Type=_Type;
			Value=_Value;
		}
	}
	private static class VarChar extends Variable{
		char Value;
		VarChar(String _Name,String _Type,char _Value)
		{
			Name=_Name;
			Type=_Type;
			Value=_Value;
		}
	}
	private static class VarFloat extends Variable{
		float Value;
		VarFloat(String _Name,String _Type,float _Value)
		{
			Name=_Name;
			Type=_Type;
			Value=_Value;
		}
	}
	private static class VarDouble extends Variable{
		double Value;
		VarDouble(String _Name,String _Type,double _Value)
		{
			Name=_Name;
			Type=_Type;
			Value=_Value;
		}
	}
	private static class VarBoolean extends Variable{
		boolean Value;
		VarBoolean(String _Name,String _Type,boolean _Value)
		{
			Name=_Name;
			Type=_Type;
			Value=_Value;
		}
	}
	private static class VarObject extends Variable{
		VarObject(String _Name,String _Type,Object _Value)
		{
			Name=_Name;
			Type=_Type;
			Value=_Value;
		}
	}
	private static class Statement{
		String StmtType;
		String Filename;
		void set(String file,String type)
		{
			Filename=file;
			StmtType=type;
		}
	}
	private static class IfStatement extends Statement{
		boolean taken;//0 for reached, 1 for taken
		int startLine;
		int endLine;
		IfStatement(boolean _taken, int _startLine, int _endLine)
		{
			taken = _taken;
			startLine = _startLine;
			endLine = _endLine; 
		}
	}
	private static class WhileStatement extends Statement{
		boolean taken;//0 for reached, 1 for taken
		int startLine;
		int endLine;
		WhileStatement(boolean _taken, int _startLine, int _endLine)
		{
			taken = _taken;
			startLine = _startLine;
			endLine = _endLine; 
		}
	}
	private static class VariableDeclaration extends Statement{
		Variable var;
		int Line;
		VariableDeclaration(Variable _var, int _Line)
		{
			var= _var;
			Line=_Line;
		}
	}
	private static class MethodInvoked extends Statement{
		String MethodName;
		Set<Variable> Parameters;
		int Line;
		MethodInvoked(String _MethodName, Set<Variable> _Parameters,int _Line)
		{
			MethodName=_MethodName;
			Parameters=_Parameters;
			Line=_Line;
		}
	}
	private static class Assignment extends Statement{
		Variable var;
		int Line;
		Assignment(Variable _var,int _Line)
		{
			var=_var;
			Line=_Line;
		}
	}
	private static class ReturnStatement extends Statement
	{
		//TODO : return value
		
		
		int Line;
		ReturnStatement(int _Line)
		{
			Line=_Line;
		}
	}
	private static class Spectrum
	{
		Map<Integer,Set<Variable>> values;
		
		Spectrum(List<Statement> Stmts)
		{
			values=new TreeMap<Integer,Set<Variable>>();
			//Set<Variable> allvars= new HashSet<Variable>();
			Iterator<Statement> it= Stmts.iterator();
			int curLine=1;
			while(it.hasNext())
			{
				Statement st=it.next();
				if(st instanceof IfStatement)
				{
					int t=((IfStatement) st).startLine;
					fill(values,curLine,t);
					if(!((IfStatement)st).taken)
						curLine=((IfStatement)st).endLine;
				}
				if(st instanceof WhileStatement)
				{
					int t=((WhileStatement) st).startLine;
					fill(values,curLine,t);
					if(!((WhileStatement)st).taken)
						curLine=((WhileStatement)st).endLine;
				}
				if(st instanceof VariableDeclaration)
				{
					int t=((VariableDeclaration) st).Line;
					fill(values,curLine,t-1);
					Set<Variable> tmp=new HashSet<Variable>();
					tmp.addAll(values.get(curLine));
					tmp.add(((VariableDeclaration) st).var);
					values.put(t, tmp);
					curLine=t;
				}
				if(st instanceof MethodInvoked)
				{
					int t=((MethodInvoked) st).Line;
					curLine=t;
					Set<Variable> tmp=new HashSet<Variable>();
					tmp.addAll(((MethodInvoked) st).Parameters);
					values.put(t, tmp);
				}
				if(st instanceof Assignment)
				{
					int t=((Assignment) st).Line;
					fill(values, curLine,t-1);
					Set<Variable> tmp=values.get(curLine);
					Set<Variable> add= new HashSet<Variable>();
					String s=((Assignment) st).var.Name;
					for(Variable v:tmp)
					{
						if(v.Name==s)
						{
							add.add(((Assignment) st).var);
						}
						else add.add(v);
					}
					values.put(t, add);
					curLine=t;
				}
				if(st instanceof ReturnStatement)
				{
					int t=((ReturnStatement) st).Line;
					fill(values,curLine,t);
					//TODO
				}
			}
		}
		public int diff(Spectrum spec2) 
		{
			// TODO Auto-generated method stub
			int ret=0;
			ret+=java.lang.Math.abs(spec2.values.size()-values.size());
			Iterator<Integer> it1=values.keySet().iterator(),it2=spec2.values.keySet().iterator();
			while(it1.hasNext()&&it2.hasNext())
			{
				int l1=it1.next(),l2=it2.next();
				if(l1!=l2)ret+=2;
				else
				{
					if(values.get(l1)!=spec2.values.get(l2))ret++;
				}
			}
			return ret;
		}
		
	}
	public static void fill(Map<Integer, Set<Variable>> values, int curLine, int targetline)
	{
		Set<Variable>last=values.get(curLine);
		for(int i=curLine+1;i<=targetline;i++)values.put(i, last);
		if(targetline<curLine)values.put(targetline, last);
		targetline=curLine;
	}
	public static int getLine(String s)
	{
		return Integer.parseInt(s.substring(s.indexOf(":")+1));
	}
	public static String getFile(String s)
	{
		return s.substring(s.indexOf(":")+1);
	}
	public static void getBranchLines(Scanner sc,int startLine,int endLine)
	{
		String t1=sc.next();
		sc.next();
		sc.useDelimiter(",");
		endLine=sc.nextInt();
		sc.reset();
		startLine=Integer.parseInt(t1.substring(t1.indexOf(':')+1));
	}
	public static Variable getVariable(Scanner sc)
	{
		String t1=sc.next();
		String t2=sc.next();
		String type=t2.substring(t2.indexOf(':'));
		Scanner sc2= new Scanner(t1).useDelimiter("=");
		Variable ret = null;
		switch(type)
		{
			case "int":
				ret=new VarInt(sc2.next(),type,sc2.nextInt());
				break;
			case "short":
				ret=new VarShort(sc2.next(),type,sc2.nextShort());
				break;
			case "object":
				ret=new VarObject(sc2.next(),type,sc2.next());
				break;
			case "char":
				ret=new VarChar(sc2.next(),type,sc2.next().charAt(0));
				break;
			case "boolean":
				ret=new VarBoolean(sc2.next(),type,sc2.nextBoolean());
				break;
			case "float":
				ret=new VarFloat(sc2.next(),type,sc2.nextFloat());
				break;
			case "double":
				ret=new VarDouble(sc2.next(),type,sc2.nextDouble());
				break;
			case "string":
				ret=new VarString(sc2.next(),type,sc2.next());
				break;
		}
		sc2.close();
		return ret;
	}
	
	public static Statement getStatement(String src)
	{
		Scanner sc= new Scanner(src);
		String label = sc.next();
		Scanner labelsc = new Scanner(label.substring(1,label.indexOf(">"))).useDelimiter(",");
		String type=labelsc.next();
		
		Statement ret=null;
		String file=null;
		switch(type)
		{
			case "Method_invoked" :
				String funcname=labelsc.next();
				int parac=labelsc.nextInt();
				labelsc.close();
				labelsc= new Scanner(sc.next()).useDelimiter(",");
				Set<Variable> Parameters=new HashSet<Variable>();
				for(int i=0;i<parac;i++)
				{
					Variable par = getVariable(labelsc);
					Parameters.add(par);
				}
				int line=getLine(labelsc.next());
				file=getFile(labelsc.next());
				ret=new MethodInvoked(funcname,Parameters,line);
				break;
			case "IfStatement":
				String temp=labelsc.next();
				boolean taken=(temp=="taken"?true:false);
				int startLine = 0,endLine = 0;
				getBranchLines(sc,startLine,endLine);
				labelsc.close();
				labelsc= new Scanner(sc.next()).useDelimiter(",");
				file=getFile(labelsc.next());
				ret=new IfStatement(taken,startLine,endLine);
				break;
			case "WhileStatement":
				temp=labelsc.next();
				taken=(temp=="taken"?true:false);
				startLine = 0;endLine = 0;
				getBranchLines(sc,startLine,endLine);
				labelsc.close();
				labelsc= new Scanner(sc.next()).useDelimiter(",");
				file=getFile(labelsc.next());
				ret=new WhileStatement(taken,startLine,endLine);
				break;
			case "Assignment":
				labelsc.close();
				labelsc=new Scanner(sc.next()).useDelimiter(",");
				Variable var=getVariable(labelsc);
				line=getLine(labelsc.next());
				file=getFile(labelsc.next());
				ret=new Assignment(var,line);
				break;
			case "ReturnStatement":
				//TODO
				break;
			case "VariableDeclaration":
				labelsc.close();
				labelsc=new Scanner(sc.next()).useDelimiter(",");
				var=getVariable(labelsc);
				line=getLine(labelsc.next());
				file=getFile(labelsc.next());
				ret=new VariableDeclaration(var,line);
				break;
		}
		sc.close();
		labelsc.close();
		ret.set(file,type);
		return ret;
	}
	
	public static String readFileToString(String filePath) throws IOException {
        StringBuilder fileData = new StringBuilder(1000);
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        char[] buf = new char[10];
        int numRead = 0;
        while ((numRead = reader.read(buf)) != -1) {
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
            buf = new char[1024];
        }
        
        reader.close();
        
        return  fileData.toString();
    }
	public static List<Statement> parsetrace(String Filename) throws IOException
	{
		List<Statement> Stmts=new ArrayList<Statement>();
		BufferedReader reader = new BufferedReader(new FileReader(Filename));
        String str = null;
        while((str=reader.readLine())!=null)
        {
        	Statement st=getStatement(str);
        	if(st instanceof IfStatement)
        	{
        		if(((IfStatement) st).taken){
        			Stmts.remove(Stmts.size()-1);
        		}
        	}
        	if(st instanceof WhileStatement)
        	{
        		if(((WhileStatement) st).taken){
        			Stmts.remove(Stmts.size()-1);
        		}
        	}
        	Stmts.add(st);
        }
        	
        return Stmts;
	}
	public static void main(String args[])throws ParseException, IOException
	{
		CommandLineParser cmdlparser = new BasicParser( );
        Options options = new Options( );
        options.addOption("T", "TraceFile", true, "input file1");
        CommandLine commandLine = cmdlparser.parse(options, args);
        String TraceFile1="",TraceFile2="";
        TraceFile1=args[1];
        TraceFile2=args[2];
        /*if( commandLine.hasOption('T') ) {
            TraceFilet = commandLine.getOptionValue('T');
        }*/
        
        Spectrum spec1 = new Spectrum(parsetrace(TraceFile1));
        Spectrum spec2 = new Spectrum(parsetrace(TraceFile2));
        System.out.println(spec1.diff(spec2));
		return;
	}
	
}
