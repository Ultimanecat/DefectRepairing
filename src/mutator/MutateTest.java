package mutator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.SimpleName;

public class MutateTest {

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
    
	
	public static void main(String[] args) throws IOException {
		String method=readFileToString("/Users/liuxinyuan/DefectRepairing/foo.txt");
		int time=10;
		
		String source=toCompilationUnit(method);
		final CompilationUnit cu =getCompilationUnit(source);
        final List<NumberLiteral> l = new ArrayList();
        MutateOperator mutateop=new MutateOperator();
        
        
        getnumberlist(cu,l);
        SimpleName methodname=getmethodname(cu);
        
        int len=l.size();
        List<String> l_bak=new ArrayList(len);
        for(NumberLiteral node:l)
        	l_bak.add(node.toString());
        String methodname_bak=methodname.toString();
        
        
        for(int num=0;num<time;num++)
        {
        	for(int i=0;i<len;i++)
            	l.get(i).setToken(mutateop.randommutate(l_bak.get(i)));
            
            methodname.setIdentifier(methodname_bak+"__"+num);
            System.out.println(getmethod(cu));
            System.out.println();
        }
        
        
        
		
	}
	
	public static SimpleName getmethodname(CompilationUnit cu)
	{
		final List<SimpleName> l=new ArrayList();
		cu.accept(new ASTVisitor() {
        	public boolean visit( MethodDeclaration node)
        	{
        		l.add(node.getName());
				return true;
        	}
        });
		return l.get(0);
	}
	
	public static String getmethod(CompilationUnit cu)
	{
		final List<String> l=new ArrayList();
		cu.accept(new ASTVisitor() {
        	public boolean visit( MethodDeclaration node)
        	{
        		l.add(node.toString());
				return true;
        	}
        });
		return l.get(0);
	}
	
	public static void getnumberlist(CompilationUnit cu,final List<NumberLiteral> l)
	{
		cu.accept(new ASTVisitor() {
        	public boolean visit( MethodInvocation node)
        	{
//        		System.out.println(node);
//        		List<Expression> l= node.arguments();
//        		for(Expression e:l)
//        		{
//        			System.out.println(e);
//        			
//        		}
				return true;
        	}
        	
        	public boolean visit( NumberLiteral node)
        	{
        		l.add(node);
				return true;
        	}
        	
        });
	}
	
	
	public static String toCompilationUnit(String method)
	{
		return "public class A {\n"+method+"\n}";
	}
	
	public static CompilationUnit getCompilationUnit( String source )
	{
		ASTParser parser = ASTParser.newParser(AST.JLS3);
        //final AST ast = AST.newAST(AST.JLS3);
        parser.setSource(source.toCharArray());
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        return (CompilationUnit) parser.createAST(null);
	}

}
