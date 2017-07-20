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
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class GetSingleTest_Chart {
	
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
		final String MethodName= args[1];
		init();


		final boolean verbose = verboset;
		List<String> filelist = new ArrayList<String>();

		

		ASTParser parser = ASTParser.newParser(AST.JLS3);
		final AST ast = AST.newAST(AST.JLS3);

		int TotalNum = filelist.size();
		int CurNum = 0;

		
			init();
			System.out.println(FilePath);
			source = readFileToString(FilePath);

			parser.setSource(source.toCharArray());
			parser.setKind(ASTParser.K_COMPILATION_UNIT);

			final CompilationUnit cu = (CompilationUnit) parser.createAST(null);
			
			insertimport(cu);
			final String PackageName=cu.getPackage().getName().toString();
			cu.accept(new ASTVisitor() {

				String ClassName;
				
				
				public boolean visit(TypeDeclaration node) {
					ClassName=node.getName().toString();
					if (node.isInterface())
						return false;
					else {
						MethodDeclaration[] l=node.getMethods();
						for(MethodDeclaration mthd : l){
							if(!mthd.getName().toString().equals(MethodName)){
								if(mthd.getName().toString().startsWith("test")){
									mthd.delete();
								} else {
									List<ASTNode> list=node.modifiers();
									for(ASTNode n:list){
										if(n.toString().startsWith("@Test"))
										{
											mthd.delete();
											break;
										}
									}
								}
								
							}
						}
						return true;
					}
				}


				

				

			});
			//System.out.print(cu);
			copytoEnd();
			if (verbose)
				System.out.print(cu.toString());

			if (!verbose) {
				writeStringToFile(FilePath, cu.toString());

			
		}
	}
}
