package TestCase;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class seperator {
	
	public static int curLine = 1;
	public static int curChar = 0;
	public static String outputBuffer = new String();
	public static String source="";
	public static String TraceFile,FilePath;
	public static void copyaLine() {
		curLine++;
		int preChar = curChar;

		while (true) {
			char c = source.charAt(curChar);

			if (c == '\n') {
				curChar++;
				break;
			}
			curChar++;
		}
		outputBuffer += source.substring(preChar, curChar);
	}
	
	public static void copyLines(int LineNumber) {
		while (curLine <= LineNumber)
			copyaLine();
	}
	
	public static void insertimport(CompilationUnit cu) {
		PackageDeclaration pkgdec=cu.getPackage();
		if(pkgdec!=null)
		{
			int lineEnd = cu.getLineNumber(pkgdec.getStartPosition() + pkgdec.getLength());
			copyLines(lineEnd);
		}
		outputBuffer += "import java.io.IOException; \nimport java.io.RandomAccessFile;\n";
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
	
	public static void process(){
		curLine = 1;
		curChar = 0;
		outputBuffer = new String();
		
		source="";
		TraceFile="";
		FilePath="";
		
		
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(source.toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);

		final CompilationUnit cu = (CompilationUnit) parser.createAST(null);
		
		insertimport(cu);
		
		cu.accept(new ASTVisitor() {
			
			public boolean isInnerClass (ASTNode node) {
				while (node != null) {
					node = node.getParent();
					if (node instanceof TypeDeclaration)
						if (((TypeDeclaration) node).isInterface() == false)
							return true;
				}
				return false;
			}
			
			public void endVisit(MethodDeclaration a){
				copyto(a.getStartPosition()+a.getLength()-1);
				outputBuffer += "\nprintRuntimeMSG(" + "**************" + ");\n";
			}
			
			public boolean visit(TypeDeclaration node) {
				if (node.isInterface())
					return false;
				else {
					if (isInnerClass(node))
						return true;
					if (node.bodyDeclarations().isEmpty())
						return false;

					copyto(((BodyDeclaration) (node.bodyDeclarations().get(0))).getStartPosition());
					outputBuffer += "\nstatic boolean flag__lxy=false;\n"
							+ "static public void printRuntimeMSG (String printMSG)\n" + "{\n"
							+ "if(flag__lxy)return;\n" + "flag__lxy=true;\n" + "\ttry {\n"
							+ "\tRandomAccessFile randomFile = new RandomAccessFile(\"" + TraceFile
							+ "\", \"rw\");\n" + "\tlong fileLength = randomFile.length();\n"
							+ "\trandomFile.seek(fileLength);\n" + "\trandomFile.writeBytes(printMSG" + "+\",File:"
							+ FilePath + "\"" + "+ \"\\n\");\n" + "\trandomFile.close();\n"
							+ "\t} catch (IOException e__e__e) {\n" + "\te__e__e.printStackTrace();\n" + "\n"
							+ "\t}\n" + "flag__lxy=false;\n}\n";

					return true;
				}
			}
		});
		copytoEnd();
		
		System.out.print(outputBuffer);
	}
}
