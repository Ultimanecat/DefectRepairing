package demo;

import java.util.ArrayList;
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
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.ReturnStatement;
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
    
    public static void copyto(int pos)
    {
        int preChar=curChar;
        for(int i=preChar;i<=pos;i++)
        {
            if(source.charAt(i)=='\n')
            {
                curLine++;
            }
        }
        curChar=pos;
        outputBuffer+=source.substring(preChar, pos);
    }
    
    public static void copytoEnd()
    {
        outputBuffer+=source.substring(curChar);
    }
    
    public static void getFilelist(String DirPath, List<String> FileList)
    {
        File RootDir=new File(DirPath);
        File[] files = RootDir.listFiles();
        
        for(File f:files)
        {
            if(f.isDirectory())
            {
                getFilelist(f.getAbsolutePath(),FileList);
            }
            else
            {
                if(f.getName().endsWith(".java"))
                    FileList.add(f.getAbsolutePath());
            }
        }
    }
    
    
    public static void main(String args[]) throws IOException, ParseException{
        boolean verboset=false;
        
        // Create a Parser
        CommandLineParser cmdlparser = new BasicParser( );
        Options options = new Options( );
        options.addOption("D","DirPath",true,"input file");
        options.addOption("T", "TraceFile", true, "output file");
        options.addOption("v","Verbose",false,"verbose debug");
        // Parse the program arguments
        CommandLine commandLine = cmdlparser.parse( options, args );
        // Set the appropriate variables based on supplied options
        String DirPath ="/Users/liuxinyuan/DefectRepairing/Math2b/src/main/";
        String TraceFilet="/Users/liuxinyuan/DefectRepairing/a.txt";
        
        if( commandLine.hasOption('F') ) {
            DirPath=commandLine.getOptionValue('F');
        }
        if( commandLine.hasOption('T') ) {
            TraceFilet = commandLine.getOptionValue('T');
        }
        if(commandLine.hasOption('v')){
            verboset=true;
        }
        final String TraceFile=TraceFilet;
        final boolean verbose=verboset;
        List<String> filelist=new ArrayList<String> ();;
        if(verbose)
            filelist.add(new String("/Users/liuxinyuan/DefectRepairing/Math1b/src/main/java/org/apache/commons/math3/optim/nonlinear/scalar/noderiv/AbstractSimplex.java"));
        else
            getFilelist(DirPath,filelist);
        
        
        ASTParser parser = ASTParser.newParser(AST.JLS3);
        final AST ast = AST.newAST(AST.JLS3);
        
        int TotalNum=filelist.size();
        int CurNum=0;
        
        
        
        for(String FilePath:filelist)
            
        {
            source = readFileToString(FilePath);
            //else source="public class A{\nvoid foo(){\nfor(int i=0;i<5;i++)\ni++;\n}\n}";
            curLine = 1;
            curChar = 0;
            outputBuffer=new String();
            
            parser.setSource(source.toCharArray());
            parser.setKind(ASTParser.K_COMPILATION_UNIT);
            
            
            final CompilationUnit cu = (CompilationUnit) parser.createAST(null);
            cu.accept(new ASTVisitor() {
                
                public ASTNode getparentstatement (ASTNode node)
                {
                    while(!(node instanceof Statement))
                    {
                        node=node.getParent();
                    }
                    return node;
                }
                
                public void insertprint(String printMSG)
                {
                    if(verbose)
                        outputBuffer += "\ndebug\n";
                    else
                        outputBuffer += "\ntry {\n"
                        +"RandomAccessFile randomFile = new RandomAccessFile(\""+TraceFile+"\", \"rw\");\n"
                        +"long fileLength = randomFile.length();\n"
                        +"randomFile.seek(fileLength);\n"
                        +"randomFile.writeBytes("+ printMSG +"+ \"\\n\");\n"
                        +"randomFile.close();\n"
                        +"} catch (IOException e__e__e) {\n"
                        +"e__e__e.printStackTrace();\n"
                        +"}\n";
                }
                
                //变量声明
                //			public boolean visit(VariableDeclarationFragment node) {
                //				SimpleName name = node.getName();
                //				this.names.add(name.getIdentifier());
                //				System.out.println("Declaration of '"+name+"' at line"+cu.getLineNumber(name.getStartPosition()));
                //				return true; // do not continue to avoid usage info
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
                    ASTNode ParentStatement=getparentstatement(node);
                    if(ParentStatement instanceof WhileStatement||
                       ParentStatement instanceof DoStatement||
                       ParentStatement instanceof ForStatement||
                       ParentStatement instanceof IfStatement||
                       ParentStatement instanceof ReturnStatement)
                        return true;
                    
                    int line=cu.getLineNumber(node.getStartPosition());
                    
                    
                    String name=node.getLeftHandSide().toString();
                    if(verbose)System.out.println("Assignment:"+"line " + line + ","+name);
                    copyto(ParentStatement.getStartPosition()+ParentStatement.getLength());
                    String printMSG = "\"Assignment:"+name+"=\"+"+name+"+\",Line "+line+"\"";
                    insertprint(printMSG);
                    return true;
                }
                public boolean visit(PostfixExpression node) {
                    ASTNode ParentStatement=getparentstatement(node);
                    if(ParentStatement instanceof WhileStatement||
                       ParentStatement instanceof DoStatement||
                       ParentStatement instanceof ForStatement||
                       ParentStatement instanceof IfStatement||
                       ParentStatement instanceof ReturnStatement)
                        return true;
                    int line=cu.getLineNumber(node.getStartPosition());
                    
                    String name=node.getOperand().toString();
                    if(verbose)System.out.println("Assignment:"+"line " + line +","+name);
                    copyto(ParentStatement.getStartPosition()+ParentStatement.getLength());
                    String printMSG = "\"Assignment:"+name+"=\"+"+name+"+\",Line "+line+"\"";
                    insertprint(printMSG);
                    //
                    return true;
                }
                public boolean visit(PrefixExpression node) {
                    if( (((PrefixExpression) node).getOperator()) == PrefixExpression.Operator.INCREMENT
                       || (((PrefixExpression) node).getOperator()) == PrefixExpression.Operator.DECREMENT)
                    {
                        ASTNode ParentStatement=getparentstatement(node);
                        if(ParentStatement instanceof WhileStatement||
                           ParentStatement instanceof DoStatement||
                           ParentStatement instanceof ForStatement||
                           ParentStatement instanceof IfStatement||
                           ParentStatement instanceof ReturnStatement)
                            return true;
                        int line=cu.getLineNumber(node.getStartPosition());
                        
                        String name=node.getOperand().toString();
                        if(verbose)System.out.println("Assignment:"+"line " + line +","+name);
                        copyto(ParentStatement.getStartPosition()+ParentStatement.getLength());
                        String printMSG = "\"Assignment:"+name+"=\"+"+name+"+\",Line "+line+"\"";
                        insertprint(printMSG);
                    }
                    return true;
                }
                
                
                public boolean visit(ForStatement node) {
                    int line=cu.getLineNumber(node.getStartPosition());
                    if(verbose)System.out.print("ForStatement:"+"line " + line);
                    
                    String printMSG="\"ForStatement:Line "+line+" to "+cu.getLineNumber(node.getStartPosition()+node.getLength())+"\"";
                    List<Expression> l=node.updaters();
                    for(Expression e:l)
                    {
                        if(e instanceof Assignment)
                        {
                            String name=((Assignment) e).getLeftHandSide().toString();
                            if(verbose)System.out.print(","+name);
                            printMSG += "+\",update:"+name+"=\"+"+name;
                        }
                        else if(e instanceof PostfixExpression)
                        {
                            String name=((PostfixExpression) e).getOperand().toString();
                            if(verbose)System.out.print(","+ name);
                            printMSG += "+\",update:"+name+"=\"+"+name;
                            
                        }
                        else if(e instanceof PrefixExpression)
                        {
                            if( (((PrefixExpression) e).getOperator()) == PrefixExpression.Operator.INCREMENT
                               || (((PrefixExpression) e).getOperator()) == PrefixExpression.Operator.DECREMENT)
                            {
                                String name=((PrefixExpression) e).getOperand().toString();
                                if(verbose)System.out.print(","+name);
                                printMSG += "+\",update:"+name+"=\"+"+name;
                            }
                            
                        }
                        
                    }
                    if(verbose)System.out.println();
                    Statement body=node.getBody();
                    if(body instanceof Block)
                    {
                        copyto(body.getStartPosition()+1);
                        insertprint(printMSG);
                        return true;
                    }
                    else
                    {
                        copyto(body.getStartPosition());
                        outputBuffer+="{\n";
                        insertprint(printMSG);
                        copyto(body.getStartPosition()+body.getLength());
                        //TODO
                        outputBuffer+="\n}";
                        return false;
                    }
                    
                    
                }
                
                public boolean visit(DoStatement node) {
                    if(verbose)System.out.println( "DoStatement:line "+cu.getLineNumber(node.getStartPosition()) +","+ cu.getLineNumber((node.getStartPosition()+node.getLength())));
                    Statement body=node.getBody();
                    String printMSG="\"DoStatement:Line "+cu.getLineNumber(node.getStartPosition())+" to "+cu.getLineNumber(node.getStartPosition()+node.getLength())+"\"";
                    if(body instanceof Block)
                    {
                        copyto(body.getStartPosition()+1);
                        insertprint(printMSG);
                        return true;
                    }
                    else
                    {
                        copyto(body.getStartPosition());
                        outputBuffer+="{\n";
                        insertprint(printMSG);
                        copyto(body.getStartPosition()+body.getLength());
                        //TODO
                        outputBuffer+="\n}";
                        return false;
                    }
                    
                    
                }
                
                public boolean visit(WhileStatement node) {
                    if(verbose)System.out.println("WhileStatement:line " + cu.getLineNumber(node.getStartPosition()));
                    Statement body=node.getBody();
                    String printMSG="\"WhileStatement:Line "+cu.getLineNumber(node.getStartPosition())+" to "+cu.getLineNumber(node.getStartPosition()+node.getLength())+"\"";
                    if(body instanceof Block)
                    {
                        copyto(body.getStartPosition()+1);
                        insertprint(printMSG);
                        return true;
                    }
                    else
                    {
                        copyto(body.getStartPosition());
                        outputBuffer+="{\n";
                        insertprint(printMSG);
                        copyto(body.getStartPosition()+body.getLength());
                        //TODO
                        outputBuffer+="\n}";
                        return false;
                    }
                    
                }
                
                public boolean visit(IfStatement node) {
                    if(verbose)System.out.print("IfStatement:line " + cu.getLineNumber(node.getStartPosition())+",else: ");
                    if(node.getElseStatement()!=null)
                    {
                        if(verbose)System.out.println(cu.getLineNumber(node.getElseStatement().getStartPosition()));
                    }
                    else if(verbose)System.out.println("null");
                    
                    Statement body=node.getThenStatement();
                    String printMSG="\"IfStatement:Line "+cu.getLineNumber(node.getStartPosition())+" to "+cu.getLineNumber(node.getStartPosition()+node.getLength())+"\"";
                    //TODO
                    if(body instanceof Block)
                    {
                        copyto(body.getStartPosition()+1);
                        insertprint(printMSG);
                        return true;
                    }
                    else
                    {
                        copyto(body.getStartPosition());
                        outputBuffer+="{\n";
                        insertprint(printMSG);
                        copyto(body.getStartPosition()+body.getLength());
                        //TODO
                        outputBuffer+="\n}";
                        return false;
                    }
                    
                }
                
                
                //				public boolean visit(ReturnStatement node) {
                //					int line=cu.getLineNumber(node.getStartPosition());
                //					if(verbose)System.out.print("ReturnStatement:line "+line);
                //					
                //					System.out.println("a"+node.getStartPosition());
                //					copyto(node.getStartPosition());
                //					System.out.println("b");
                //					String printMSG = "\"ReturnStatement:value=\"+"+node.getExpression()+"+\",Line "+line+"\"";
                //					insertprint(printMSG);
                //					return false;
                //				}
                
                
                
                
            });
            copytoEnd();
            if(verbose)System.out.print(outputBuffer);
            
            if(!verbose)
            {
                FileWriter fw= new FileWriter(FilePath);
                fw.write(outputBuffer);
                fw.close();
                CurNum++;
                System.out.println(FilePath);
                System.out.println(CurNum+"/"+TotalNum);
            }
        }
    }
    
}
