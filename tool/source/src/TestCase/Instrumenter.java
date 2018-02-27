package TestCase;
//tar -c bin | bzip2 > bin.tar.bz2

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.WhileStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.LabeledStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;

import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.CommandLine;

public class Instrumenter {

	public static String readFileToString(String filePath) {
		StringBuilder fileData = new StringBuilder(1000);
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(filePath));
			char[] buf = new char[10];
			int numRead = 0;
			while ((numRead = reader.read(buf)) != -1) {
				String readData = String.valueOf(buf, 0, numRead);
				fileData.append(readData);
				buf = new char[1024];
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return fileData.toString();
	}

	public static void writeStringToFile(String FilePath, String output) {
		try {
			FileWriter fw = new FileWriter(FilePath);
			fw.write(output);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static int curLine = 1;
	public static int curChar = 0;
	public static String outputBuffer = new String();
	public static String source = new String();


	public static void init() {
		curLine = 1;
		curChar = 0;
		outputBuffer = new String();
		// LineNumberMap=new HashMap<Integer,String>();
	}



	public static void copyto(int pos) {

		int preChar = curChar;
		for (int i = preChar; i <= pos; i++) {
			if (source.charAt(i) == '\n') {
				curLine++;
			}
		}
		curChar = pos;
		// System.out.println(preChar+","+pos);
		outputBuffer += source.substring(preChar, pos);
	}

	public static void copytoEnd() {
		outputBuffer += source.substring(curChar);
	}

	public static void getFilelist(String DirPath, List<String> FileList) {
		File RootDir = new File(DirPath);
		File[] files = RootDir.listFiles();

		for (File f : files) {
			if (f.isDirectory()) {
				getFilelist(f.getAbsolutePath(), FileList);
			} else {
				if (f.getName().endsWith(".java") && (f.getName().contains("Test")|| f.getName().contains("test")))
					FileList.add(f.getAbsolutePath());
			}
		}
	}

	public static void insertimport(CompilationUnit cu) {
		PackageDeclaration pkgdec=cu.getPackage();
		if(pkgdec!=null)
		{
			copyto(pkgdec.getStartPosition() + pkgdec.getLength());
		}
		outputBuffer += "import java.io.IOException; \nimport java.io.RandomAccessFile;\n";
	}
	
	public static void main(String args[]) {
		boolean verboset = false;
		
		String DirPath = args[0];
		String TraceFilet = args[1];
		final String Project = args[2];
		init();

		final String TraceFile = TraceFilet;
		final boolean verbose = verboset;
		List<String> filelist = new ArrayList<String>();

		if (verbose)
			filelist.add(new String(
					"/Volumes/Unnamed/Math2b/src/test/java/org/apache/commons/math3/fraction/FractionTest.java"));
		else
			getFilelist(DirPath, filelist);

		ASTParser parser = ASTParser.newParser(AST.JLS3);
		final AST ast = AST.newAST(AST.JLS3);

		int TotalNum = filelist.size();
		int CurNum = 0;

		for (final String FilePath : filelist)

		{
			init();
			System.out.println(FilePath);
			source = readFileToString(FilePath);

			parser.setSource(source.toCharArray());
			parser.setKind(ASTParser.K_COMPILATION_UNIT);

			final CompilationUnit cu = (CompilationUnit) parser.createAST(null);
			
			insertimport(cu);
			String PackageNamet = null;
			if(!Project.equals("Randoop")) {
				PackageNamet=cu.getPackage().getName().toString();
			}
			final String PackageName=PackageNamet;
			cu.accept(new ASTVisitor() {

				String ClassName;
				public void insertprint(String printMSG) {
					if (verbose)
						outputBuffer += "\ndebug:" + printMSG + "\n";
					else
						outputBuffer += "\nprintRuntimeMSG(" + printMSG + ");\n";
				}
				public boolean isInnerClass(ASTNode node) {
					while (node != null) {
						node = node.getParent();
						if (node instanceof TypeDeclaration)
							if (((TypeDeclaration) node).isInterface() == false)
								return true;
					}
					return false;
				}
				
				public boolean visit(TypeDeclaration node) {
					ClassName=node.getName().toString();
					if (isInnerClass(node))
						return true;
					if (node.isInterface())
						return false;
					else {
						copyto(((BodyDeclaration) (node.bodyDeclarations().get(0))).getStartPosition());
						outputBuffer += "\nstatic boolean flag__lxy=false;\n"
								+ "static public void printRuntimeMSG (String printMSG)\n" + "{\n"
								+ "if(flag__lxy)return;\n" + "flag__lxy=true;\n" + "\ttry {\n"
								+ "\tRandomAccessFile randomFile = new RandomAccessFile(\"" + TraceFile
								+ "\", \"rw\");\n" + "\tlong fileLength = randomFile.length();\n"
								+ "\trandomFile.seek(fileLength);\n" 
								+ "\trandomFile.writeBytes(printMSG+\"\\n\");\n" 
								+ "\trandomFile.close();\n"
								+ "\t} catch (IOException e__e__e) {\n" + "\te__e__e.printStackTrace();\n" + "\n"
								+ "\t}\n" + "flag__lxy=false;\n}\n";

						return true;
					}
				}


				public boolean visit(MethodDeclaration node) {
					if (node.isConstructor())//
						return false;

					boolean flag=true;
					if(node.getName().toString().startsWith("test"))
						flag=false;
					else{
						List<ASTNode> l=node.modifiers();
						for(ASTNode n:l){
							if(n.toString().startsWith("@Test"))
							{
								flag=false;
								break;
							}
						}
					}
					
					if(flag)
						return false;
					
					copyto(node.getBody().getStartPosition()+1);
					String printMSG;
					if(Project.equals("Randoop"))
						printMSG = "\"---"+ClassName+":"+node.getName()+"\"";
					else {
						printMSG = "\"---"+PackageName+"."+ClassName+":"+node.getName()+"\"";
					}
					insertprint(printMSG);

					return true;
				}



			});
			copytoEnd();
			if (verbose)
				System.out.print(outputBuffer);

			if (!verbose) {
				writeStringToFile(FilePath, outputBuffer);
				CurNum++;

				System.out.println(CurNum + "/" + TotalNum);
			}
		}
	}

}
