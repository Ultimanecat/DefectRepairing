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
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BodyDeclaration;
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
import org.eclipse.jdt.core.dom.LabeledStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;

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
        //System.out.println(preChar+","+pos);
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
        String DirPath ="/Users/liuxinyuan/DefectRepairing/Lang11b/src/main/";
        String TraceFilet="/Users/liuxinyuan/DefectRepairing/a.txt";
        
        if( commandLine.hasOption('D') ) {
            DirPath=commandLine.getOptionValue('D');
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
            filelist.add(new String("/Users/liuxinyuan/DefectRepairing/Math1b/src/main/java/org/apache/commons/math3/linear/SparseRealVector.java"));
        else
            getFilelist(DirPath,filelist);
        
        
        ASTParser parser = ASTParser.newParser(AST.JLS3);
        final AST ast = AST.newAST(AST.JLS3);
        
        int TotalNum=filelist.size();
        int CurNum=0;
        
        
        
        for(final String FilePath:filelist)
            
        {
            System.out.println(FilePath);
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
                
                public boolean judgePrint(MethodDeclaration node)
                {
                    if(((MethodDeclaration) node).getName().toString().equals("toString")
                       ||((MethodDeclaration) node).getName().toString().startsWith("print"))//解决一个print函数无限递归的编译错误
                        return true;
                    return false;
                }
                
                public boolean isinMethod(ASTNode node)
                {
                    while(node!=null)
                    {
                        node=node.getParent();
                        if(node instanceof MethodDeclaration)
                        {
                            if(judgePrint((MethodDeclaration) node))
                                return false;
                            return true;
                        }
                    }
                    return false;
                }
                
                public void insertprint(String printMSG)
                {
                    if(verbose)
                        outputBuffer += "\ndebug:"+printMSG+"\n";
                    else
                        //						outputBuffer += "\ntry {\n"
                        //							+"RandomAccessFile randomFile = new RandomAccessFile(\""+TraceFile+"\", \"rw\");\n"
                        //							+"long fileLength = randomFile.length();\n"
                        //							+"randomFile.seek(fileLength);\n"
                        //							+"randomFile.writeBytes("+ printMSG +"+\","+FilePath+"\""+"+ \"\\n\");\n"
                        //							+"randomFile.close();\n"
                        //							+"} catch (IOException e__e__e) {\n"
                        //							+"e__e__e.printStackTrace();\n"
                        //							+"}\n";
                        outputBuffer += "\nprintRuntimeMSG("+ printMSG +");\n";
                }
                
                
                
                
                //变量声明
                public void endVisit(VariableDeclarationFragment node) {
                    String name=node.getName().toString();
                    if(node.getInitializer()!=null)
                    {
                        if(!isinMethod(node))return;
                        ASTNode ParentStatement=getparentstatement(node);
                        if(ParentStatement instanceof WhileStatement||
                           ParentStatement instanceof DoStatement||
                           ParentStatement instanceof ForStatement||
                           ParentStatement instanceof IfStatement||
                           ParentStatement instanceof ReturnStatement)
                            return;
                        int line=cu.getLineNumber(node.getStartPosition());
                        
                        if(verbose)System.out.println("VariableDeclaration:"+"line " + line + ","+name);
                        copyto(ParentStatement.getStartPosition()+ParentStatement.getLength());
                        String printMSG = "\"<VariableDeclaration> " + name + "=\"+" + name + "+\",type:\"+getType_("+name+")+\",Line:"+line+"\"";
                        
                        insertprint(printMSG);
                        return;
                    }
                    if(verbose)System.out.println("Declaration of '"+name+"' at line"+cu.getLineNumber(node.getStartPosition()));
                    return;
                }
                
                public boolean visit(MethodDeclaration node)
                {
                    if(node.isConstructor())//
                        return true;
                    Block body=node.getBody();
                    if(body==null)
                        return true;
                    
                    int line=cu.getLineNumber(node.getStartPosition());
                    if(verbose)System.out.println("MethodDeclaration:"+node.getName().toString()+",Line "+line);
                    
                    List<SingleVariableDeclaration> parameters=node.parameters();
                    copyto(body.getStartPosition()+1);
                    
					String printMSG="\"<Method_invoked,"+node.getName().toString()+","+parameters.size()+"> \"";                    
                    boolean firstVar=true;
					if(!judgePrint(node))
                        for(SingleVariableDeclaration FormalParameter:parameters)
                        {
                            String name=FormalParameter.getName().toString();
                            if(verbose)System.out.println(name);
                            if(firstVar)
							{
								printMSG+="+\""+name+"=\"+"+name+"+\",type:\"+getType_("+name+")";
								firstVar=false;
							}
							else printMSG+="+\","+name+"=\"+"+name+"+\",type:\"+getType_("+name+")";
                        }
                    
                    printMSG+="+\","+"Line:"+line+"\"";
                    insertprint(printMSG);

                    return true;
                }
                
                public boolean isInnerClass(ASTNode node)
                {
                    while(node!=null)
                    {
                        node=node.getParent();
                        if(node instanceof TypeDeclaration)
                            if(((TypeDeclaration) node).isInterface()==false)
                                return true;
                    }
                    return false;
                }
                
                public void CopytoLabel(ASTNode node)
                {
                    if(node.getParent() instanceof LabeledStatement)
                        node=node.getParent();
                    copyto(node.getStartPosition());
                }
                
                public boolean visit (TypeDeclaration node)
                {
                    if(verbose)System.out.println("TypeDeclaration:Line "+cu.getLineNumber(node.getStartPosition()));
                    if(node.isInterface())
                        return false;
                    else
                    {
                        if(isInnerClass(node))return true;
                        if(node.bodyDeclarations().isEmpty())return false;
                        
                        
                        copyto(((BodyDeclaration)(node.bodyDeclarations().get(0))).getStartPosition());
                        outputBuffer+="\nstatic public void printRuntimeMSG (String printMSG)\n"
                        +"{\n"
                        +"\ttry {\n"
                        +"\tRandomAccessFile randomFile = new RandomAccessFile(\""+TraceFile+"\", \"rw\");\n"
                        +"\tlong fileLength = randomFile.length();\n"
                        +"\trandomFile.seek(fileLength);\n"
                        +"\trandomFile.writeBytes(printMSG"  +"+\",File:"+FilePath+"\""+"+ \"\\n\");\n"
                        +"\trandomFile.close();\n"
                        +"\t} catch (IOException e__e__e) {\n"
                        +"\te__e__e.printStackTrace();\n"
                        +"\t}\n"
                        +"}\n"
                        +"static public String getType_(Object o){return \"Object\";}\n"
                        +"static public String getType_(byte i){return \"byte\";}\n"
                        +"static public String getType_(short s){return \"short\";}\n"
                        +"static public String getType_(int i){return \"int\";}\n"
                        +"static public String getType_(long l){return \"long\";}\n"
                        +"static public String getType_(boolean b){return \"boolean\";}\n"
                        +"static public String getType_(char c){return \"char\";}\n"
                        +"static public String getType_(float f){return \"float\";}\n"
                        +"static public String getType_(double d){return \"double\";}\n"
                        +"static public String getType_(String str){return \"String\";}\n";
                        
                        return true;
                    }
                }
                
                //				public void endvisit (ExpressionStatement Node)
                //				{
                //
                //
                //					if(curChar==Node.getStartPosition()+Node.getLength())
                //						return;
                //
                //					int line=cu.getLineNumber(Node.getStartPosition());
                //					copyto(Node.getStartPosition()+Node.getLength());
                //					insertprint("\"Line "+line+"\"");
                //					//return true;
                //
                //				}
                
                public boolean visit (PackageDeclaration node)
                {
                    int lineEnd=cu.getLineNumber(node.getStartPosition()+node.getLength());
                    copyLines(lineEnd);
                    outputBuffer+="import java.io.IOException; \n import java.io.RandomAccessFile;\n";
                    return true;
                    
                }
                
                public void endVisit(Assignment node) {
                    if(!isinMethod(node))return;
                    ASTNode ParentStatement=getparentstatement(node);
                    if(ParentStatement instanceof WhileStatement||
                       ParentStatement instanceof DoStatement||
                       ParentStatement instanceof ForStatement||
                       ParentStatement instanceof IfStatement||
                       ParentStatement instanceof ReturnStatement)
                        return;
                    
                    int line=cu.getLineNumber(node.getStartPosition());
                    
                    
                    String name=node.getLeftHandSide().toString();
                    if(verbose)System.out.println("Assignment:"+"line " + line + ","+name);
                    copyto(ParentStatement.getStartPosition()+ParentStatement.getLength());
                    String printMSG = "\"<Assignment> assign:"+name+"=\"+"+name+"+\",type:\"+getType_("+name+")+\",Line:"+line+"\"";
                    insertprint(printMSG);
                    return;
                }
                
                public void endVisit(PostfixExpression node) {
                    if(!isinMethod(node))return;
                    ASTNode ParentStatement=getparentstatement(node);
                    if(ParentStatement instanceof WhileStatement||
                       ParentStatement instanceof DoStatement||
                       ParentStatement instanceof ForStatement||
                       ParentStatement instanceof IfStatement||
                       ParentStatement instanceof ReturnStatement)
                        return;
                    int line=cu.getLineNumber(node.getStartPosition());
                    
                    String name=node.getOperand().toString();
                    if(verbose)System.out.println("Assignment:"+"line " + line +","+name);
                    copyto(ParentStatement.getStartPosition()+ParentStatement.getLength());
                    String printMSG = "\"<Assignment> assign:"+name+"=\"+"+name+"+\",type:\"+getType_("+name+")+\",Line:"+line+"\"";
                    insertprint(printMSG);
                    
                    return;
                }
                public void endVisit(PrefixExpression node) {
                    if(!isinMethod(node))return;
                    if( (((PrefixExpression) node).getOperator()) == PrefixExpression.Operator.INCREMENT
                       || (((PrefixExpression) node).getOperator()) == PrefixExpression.Operator.DECREMENT)
                    {
                        ASTNode ParentStatement=getparentstatement(node);
                        if(ParentStatement instanceof WhileStatement||
                           ParentStatement instanceof DoStatement||
                           ParentStatement instanceof ForStatement||
                           ParentStatement instanceof IfStatement||
                           ParentStatement instanceof ReturnStatement)
                            return;
                        int line=cu.getLineNumber(node.getStartPosition());
                        
                        String name=node.getOperand().toString();
                        if(verbose)System.out.println("Assignment:"+"line " + line +","+name);
                        copyto(ParentStatement.getStartPosition()+ParentStatement.getLength());
                        String printMSG = "\"<Assignment> assign:"+name+"=\"+"+name+"+\",type:\"+getType_("+name+")+\",Line:"+line+"\"";
                        insertprint(printMSG);
                    }
                    return;
                }
                
                
                public boolean visit(ForStatement node) {
                    int line=cu.getLineNumber(node.getStartPosition());
                    if(verbose)System.out.print("ForStatement:"+"line " + line);
                    
                    String printMSG="\"<ForStatement,taken> Line:"+line+" to "+cu.getLineNumber(node.getStartPosition()+node.getLength())+"\"";
                    CopytoLabel(node);
                    insertprint("\"<ForStatement,reached> Line:"+line+" to "+cu.getLineNumber(node.getStartPosition()+node.getLength())+"\"");
                    List<Expression> l=node.updaters();
                    for(Expression e:l)
                    {
                        if(e instanceof Assignment)
                        {
                            String name=((Assignment) e).getLeftHandSide().toString();
                            if(verbose)System.out.print(","+name);
                            printMSG += "+\",assign:"+name+"=\"+"+name+"+\",type:\"+getType_("+name+")";
                        }
                        else if(e instanceof PostfixExpression)
                        {
                            String name=((PostfixExpression) e).getOperand().toString();
                            if(verbose)System.out.print(","+ name);
                            printMSG += "+\",assign:"+name+"=\"+"+name+"+\",type:\"+getType_("+name+")";
                            
                        }
                        else if(e instanceof PrefixExpression)
                        {
                            if( (((PrefixExpression) e).getOperator()) == PrefixExpression.Operator.INCREMENT 
                               || (((PrefixExpression) e).getOperator()) == PrefixExpression.Operator.DECREMENT)
                            {
                                String name=((PrefixExpression) e).getOperand().toString();
                                if(verbose)System.out.print(","+name);
                                printMSG += "+\",assign:"+name+"=\"+"+name+"+\",type:\"+getType_("+name+")";
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
                    String printMSG="\"<DoStatement,taken> Line:"+cu.getLineNumber(node.getStartPosition())+" to "+cu.getLineNumber(node.getStartPosition()+node.getLength())+"\"";
                    CopytoLabel(node);
                    insertprint("\"<DoStatement,reached> Line:"+cu.getLineNumber(node.getStartPosition())+" to "+cu.getLineNumber(node.getStartPosition()+node.getLength())+"\"");
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
                    String printMSG="\"<WhileStatement,taken> Line:"+cu.getLineNumber(node.getStartPosition())+" to "+cu.getLineNumber(node.getStartPosition()+node.getLength())+"\"";
                    CopytoLabel(node);
                    insertprint("\"<WhileStatement,reached> Line:"+cu.getLineNumber(node.getStartPosition())+" to "+cu.getLineNumber(node.getStartPosition()+node.getLength())+"\"");
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
                
                public void endVisit(IfStatement node)
                {
                    copyto(node.getStartPosition()+node.getLength());
                    outputBuffer+='}';
                }
                
                public boolean visit(IfStatement node) {
                    if(verbose)System.out.print("IfStatement:line " + cu.getLineNumber(node.getStartPosition())+",else: ");
                    String ElseMSG=",Else:";
                    if(node.getElseStatement()!=null)
                    {
                        ElseMSG+=cu.getLineNumber(node.getElseStatement().getStartPosition()) + " to " + cu.getLineNumber(node.getElseStatement().getStartPosition()+node.getElseStatement().getLength());
                    }
                    else ElseMSG+="null";
                    
                    Statement body=node.getThenStatement();
                    String printMSG="\"<IfStatement,taken> Then:"+cu.getLineNumber(node.getThenStatement().getStartPosition())+" to "+cu.getLineNumber(node.getThenStatement().getStartPosition()+node.getThenStatement().getLength())+ElseMSG+"\"";
                    
                    
                    copyto(node.getStartPosition());
                    outputBuffer+='{';
                    insertprint("\"<IfStatement,reached> Then:"+cu.getLineNumber(node.getThenStatement().getStartPosition())+" to "+cu.getLineNumber(node.getThenStatement().getStartPosition()+node.getThenStatement().getLength())+ElseMSG+"\"");
                    
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
                
                public boolean visit(ReturnStatement node) {
					int line=cu.getLineNumber(node.getStartPosition());
					if(verbose)System.out.print("ReturnStatement:line "+line);
					
					
					copyto(node.getStartPosition());
					outputBuffer+="{";
					String printMSG = "\"<ReturnStatement> ReturnValue=\"+("+node.getExpression()+")+\",type:\"+getType_("+node.getExpression()+")+\",Line "+line+"\"";
					insertprint(printMSG);
					copyto(node.getStartPosition()+node.getLength());
					outputBuffer+="}";
					return false;
				}
                
                
                
                
            });
            copytoEnd();
            if(verbose)System.out.print(outputBuffer);
            
            if(!verbose)
            {
                FileWriter fw = new FileWriter(FilePath);
                fw.write(outputBuffer);
                fw.close();
                CurNum++;
                
                
                System.out.println(CurNum+"/"+TotalNum);
                //if(CurNum==16)
                //	break;
            }
        }
    }
    
}
// return
// if
// SQL
// every-line
