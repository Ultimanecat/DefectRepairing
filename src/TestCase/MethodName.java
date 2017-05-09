package TestCase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class MethodName {
	
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
    public static String methodname="";
    public static String StartLine="";
    public static String EndLine="";
    public static String ClassName="";
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
				if (f.getName().endsWith("Tests.java"))
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
		
		String FilePath = args[0];
		String TraceFilet = args[1];
		final int TargetLine=Integer.valueOf(args[2]);
		init();

		final String TraceFile = TraceFilet;
		final boolean verbose = verboset;
		List<String> filelist = new ArrayList<String>();

		

		ASTParser parser = ASTParser.newParser(AST.JLS3);
		final AST ast = AST.newAST(AST.JLS3);

		int TotalNum = filelist.size();
		int CurNum = 0;

		
			init();

			source = readFileToString(FilePath);

			parser.setSource(source.toCharArray());
			parser.setKind(ASTParser.K_COMPILATION_UNIT);

			final CompilationUnit cu = (CompilationUnit) parser.createAST(null);
			
			insertimport(cu);
			final String PackageName=cu.getPackage().getName().toString();
			if(PackageName!=null)
				ClassName=PackageName+".";
			cu.accept(new ASTVisitor() {

				
				public void insertprint(String printMSG) {
					if (verbose)
						outputBuffer += "\ndebug:" + printMSG + "\n";
					else
						outputBuffer += "\nprintRuntimeMSG(" + printMSG + ");\n";
				}
				
				public boolean visit(TypeDeclaration node) {
					ClassName+=node.getName().toString();
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
					

					if( cu.getLineNumber(node.getStartPosition())<=TargetLine && cu.getLineNumber(node.getStartPosition()+node.getLength())>=TargetLine){
						copyto(node.getBody().getStartPosition()+1);
						
						
						methodname=node.getName().toString();
						StartLine=String.valueOf(cu.getLineNumber(node.getStartPosition()));
						EndLine=String.valueOf(cu.getLineNumber(node.getStartPosition()+node.getLength()));
//						List<SingleVariableDeclaration> l=node.parameters();
//						for(SingleVariableDeclaration o:l){
//							methodname+="_"+o.getType();
//						}
//						methodname+="," + node.parameters().size();
//						methodname=methodname.replace('<', '(');
//						methodname=methodname.replace('>', ')');
						
						
					}
					

					return true;
				}

				

			});
			
			
writeStringToFile(TraceFilet, methodname+"\n"+ClassName+"\n"+StartLine+"\n"+EndLine);			
		}
	}

