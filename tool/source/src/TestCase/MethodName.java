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
import org.apache.commons.lang.StringUtils;

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
    public static String result="";
    public static String StartLine="";
    public static String EndLine="";
    public static String ClassName="";
    public static Boolean ClassNameflag=true;
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
		final List<Integer>TargetLineList=new ArrayList<Integer>();
		
		String[] a=args[2].split(",");
		for(String line :a)
			TargetLineList.add(Integer.valueOf(line));
		init();

		

		ASTParser parser = ASTParser.newParser(AST.JLS3);


		
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

				public boolean visit(TypeDeclaration node) {
					if(ClassNameflag){
						ClassName+=node.getName().toString();
						ClassNameflag=false;
					}
					
					return true;
				}


				public boolean visit(MethodDeclaration node) {
					if (node.isConstructor())//
						return false;
					
					for(Integer TargetLine: TargetLineList)
					if( cu.getLineNumber(node.getStartPosition())<=TargetLine && cu.getLineNumber(node.getStartPosition()+node.getLength())>=TargetLine){
						copyto(node.getBody().getStartPosition()+1);
						
						
						methodname=node.getName().toString();
						StartLine=String.valueOf(cu.getLineNumber(node.getStartPosition()));
						EndLine=String.valueOf(cu.getLineNumber(node.getStartPosition()+node.getLength()));
						String signature=node.getReturnType2().toString();
						List<SingleVariableDeclaration> l=node.parameters();
						List<String> types=new ArrayList<String>();
						for(SingleVariableDeclaration o:l){
							types.add(o.getType().toString());
						}

						if(types.size()!=0)
							signature=signature+" ("+StringUtils.join(types.toArray(), ", ")+")";
						result+=methodname+"\t"+signature+"\t"+StartLine+"\t"+EndLine+"\n";
						
					}
					

					return true;
				}

				

			});
			
			
writeStringToFile(TraceFilet, result+ClassName);			
		}
	}


