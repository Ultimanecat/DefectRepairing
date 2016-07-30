package demo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.WhileStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import java.io.PrintStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.apache.commons.cli.CommandLineParser;  
import org.apache.commons.cli.BasicParser;  
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.CommandLine;  

public class test {
	
	public static String readFileToString(String filePath) throws IOException {
		StringBuilder fileData = new StringBuilder(1000);
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
 
		char[] buf = new char[10];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			//System.out.println(numRead);
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}
 
		reader.close();
 
		return  fileData.toString();	
	}
	public static int curLine = 1;
	public static int curChar = 0;
	public static String outputBuffer=new String();
	public static String source=new String();
	public static void copyaLine()
	{
		curLine++;
		int preChar=curChar;
		
		while(true)
		{
			int index=curChar;
			char c=source.charAt(curChar);
			
			if(c=='\n')
			{
				curChar++;
				break;
			}
			curChar++;
		}
		outputBuffer+=source.substring(preChar, curChar);
	}
	

	public static void copyLines (int LineNumber)
	{
		while(curLine<=LineNumber)
			copyaLine();
	}
	
	public static void copytoEnd()
	{
		outputBuffer+=source.substring(curChar);
	}
	
	
	
	public static void main(String args[]) throws IOException, ParseException{
		// Create a Parser  
		  CommandLineParser cmdlparser = new BasicParser( );  
		  Options options = new Options( );  
		  options.addOption("F","FilePath",true,"input file");
		  options.addOption("T", "TraceFile", true, "output file");  
		  options.addOption("v","Verbose",false,"verbose debug");
		  // Parse the program arguments  
		  CommandLine commandLine = cmdlparser.parse( options, args );  
		  // Set the appropriate variables based on supplied options  
		  String FilePath ="/home/akarin/Documents/DefectRepairing/math1f/src/main/java/org/apache/commons/math3/complex/Complex.java";
		  String TraceFilet="/home/akarin/Documents/DefectRepairing/Math1f/a.txt";
		  boolean verboset=false;
		  if( commandLine.hasOption('F') ) {  
		    FilePath=commandLine.getOptionValue('F');
		  }  
		  if( commandLine.hasOption('T') ) {  
		    TraceFilet = commandLine.getOptionValue('T');  
		  }  
		  if(commandLine.hasOption('v')){
			verboset=true;
		  }
		final String TraceFile=TraceFilet;
		final boolean verbose=verboset;
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		source = readFileToString(FilePath);
		//source=new String("public class A { int i = 9;  \n int j; \n void foo() { j=1; \n for(int i=0;i<100;i=i+1,j++);\n do \n { \n i++; \n }while (i<10);\n if(a==b)return 1;\n else if(a==c) return 2; \n else if(a==d) return 3;} ");
		
		parser.setSource(source.toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		//ASTNode node = parser.createAST(null);
		

		final CompilationUnit cu = (CompilationUnit) parser.createAST(null);
		cu.accept(new ASTVisitor() {
 
			
			
			public void insertprint(String printMSG)
			{
				outputBuffer += "try {\n"
						+"RandomAccessFile randomFile = new RandomAccessFile(\""+TraceFile+"\", \"rw\");\n"
						+"long fileLength = randomFile.length();\n"
						+"randomFile.seek(fileLength);\n"
						+"randomFile.writeBytes("+ printMSG +"+ \"\\n\");\n"
						+"randomFile.close();\n"
						+"} catch (IOException e) {\n"
						+"e.printStackTrace();\n"
						+"}\n";
			}
			
			//变量声明
//			public boolean visit(VariableDeclarationFragment node) {
//				SimpleName name = node.getName();
//				this.names.add(name.getIdentifier());
//				System.out.println("Declaration of '"+name+"' at line"+cu.getLineNumber(name.getStartPosition()));
//				return true; // do not continue to avoid usage info
//			}
 
//			public boolean visit(SimpleName node) {
//				if (this.names.contains(node.getIdentifier())) {
//				System.out.println("Usage of '" + node + "' at line " +	cu.getLineNumber(node.getStartPosition()));
//				}
//				return true;
//			}
			

//			public boolean visit(Assert node) {
//			Todo
//		}			
			
			public boolean visit (PackageDeclaration node)
			{
				int lineEnd=cu.getLineNumber(node.getStartPosition()+node.getLength());
				copyLines(lineEnd);
				outputBuffer+="import java.io.IOException; \n import java.io.RandomAccessFile;\n";
				return true;
				
			}
			
			public boolean visit(Assignment node) {
				int line=cu.getLineNumber(node.getStartPosition());
				int lineEnd=cu.getLineNumber(node.getStartPosition()+node.getLength());
				String name=node.getLeftHandSide().toString();
				if(verbose)System.out.println("Assignment:"+"line " + line + ","+name);
				copyLines(lineEnd);
				String printStatement = "\"Assignment:"+name+"=\"+"+name+"+\",Line "+line+"\"";
				insertprint(printStatement);
				return true;
			}
			public boolean visit(PostfixExpression node) {
				int line=cu.getLineNumber(node.getStartPosition());
				int lineEnd=cu.getLineNumber(node.getStartPosition()+node.getLength());
				String name=node.getOperand().toString();
				if(verbose)System.out.println("Assignment:"+"line " + line +","+name);
				copyLines(lineEnd);
				String printStatement = "\"Assignment:"+name+"=\"+"+name+"+\",Line "+line+"\"";
				insertprint(printStatement);
				return true;
			}
			public boolean visit(PrefixExpression node) {
				if( (((PrefixExpression) node).getOperator()) == PrefixExpression.Operator.INCREMENT 
						|| (((PrefixExpression) node).getOperator()) == PrefixExpression.Operator.DECREMENT)
				{
					int line=cu.getLineNumber(node.getStartPosition());
					int lineEnd=cu.getLineNumber(node.getStartPosition()+node.getLength());
					String name=node.getOperand().toString();
					if(verbose)System.out.println("Assignment:"+"line " + line +","+name);
					copyLines(lineEnd);
					String printStatement = "\"Assignment:"+name+"=\"+"+name+"+\",Line "+line+"\"";
					insertprint(printStatement);
				}
				return true;
			}
			
			
			public boolean visit(ForStatement node) {
				int line=cu.getLineNumber(node.getStartPosition());
				if(verbose)System.out.print("ForStatement:"+"line " + line);
//				String printStatement="\"ForStatement:Line "+line+"\"";
//				List<Expression> l=node.updaters();
//				for(Expression e:l)
//				{
//					//System.out.print(e.toString());
//					if(e instanceof Assignment)
//					{
//						String name=((Assignment) e).getLeftHandSide().toString();
//						System.out.print(","+name);
//						printStatement += "+\",update:"+name+"=\"+"+name;
//					}
//					else if(e instanceof PostfixExpression)
//					{
//						String name=((PostfixExpression) e).getOperand().toString();
//						System.out.print(","+ name);
//						printStatement += "+\",update:"+name+"=\"+"+name;
//						
//					}
//					else if(e instanceof PrefixExpression)
//					{
//						if( (((PrefixExpression) e).getOperator()) == PrefixExpression.Operator.INCREMENT 
//							|| (((PrefixExpression) e).getOperator()) == PrefixExpression.Operator.DECREMENT)
//						{
//							String name=((PrefixExpression) e).getOperand().toString();
//							System.out.print(","+name);
//							printStatement += "+\",update:"+name+"=\"+"+name;
//						}
//						
//					}
//					
//				}
//				System.out.println();
//				
//				copyLines(line);
//				insertprint(printStatement);
//				return false;
				return true;		
			}
			
			public boolean visit(DoStatement node) {
				if(verbose)System.out.println( "DoStatement:line "+cu.getLineNumber(node.getStartPosition()) +","+ cu.getLineNumber((node.getStartPosition()+node.getLength())));
				
				return true;
				
			}
			
			public boolean visit(WhileStatement node) {
				if(verbose)System.out.println("WhileStatement:line " + cu.getLineNumber(node.getStartPosition()));
				
				return true;
			}
			
			public boolean visit(IfStatement node) {
				if(verbose)System.out.print("IfStatement:line " + cu.getLineNumber(node.getStartPosition())+",else: ");
				if(node.getElseStatement()!=null)
				{
					if(verbose)System.out.println(cu.getLineNumber(node.getElseStatement().getStartPosition()));
				}
				else if(verbose)System.out.println("null");
				
				return true;
			}
			
			
			
			
		});
		copytoEnd();
		//System.out.print(outputBuffer);
		
		File output=new File(FilePath);
		FileOutputStream fos=new FileOutputStream(output);
		fos.write(outputBuffer.getBytes(),0,outputBuffer.length());
		fos.close();
	}

}
