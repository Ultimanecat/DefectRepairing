package defectrepairing.patchcorrectness.TestCaseMutation;

import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.SimpleName;

import defectrepairing.patchcorrectness.DefectRepairing.Instrumenter;

import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.CommandLine;

public class Mutator {

	public static void main(String[] args) {

		process(args[0], args[1]);

	}

	public static MethodDeclaration get_method(String filepath, String methodname) {
		String source = null;
		source = Instrumenter.readFileToString(filepath);
		int time = 10;

		final CompilationUnit cu = getCompilationUnit(source);
		final List<NumberLiteral> l = new ArrayList<NumberLiteral>();
		MutateOperator mutateop = new MutateOperator();

		MethodDeclaration method = getTarget(cu, l, methodname);
		return method;
	}

	public static void process(String filepath, String methodname) {
		String source = null;
		source = Instrumenter.readFileToString(filepath);
		int time = 10;

		final CompilationUnit cu = getCompilationUnit(source);
		final List<NumberLiteral> l = new ArrayList<NumberLiteral>();
		MutateOperator mutateop = new MutateOperator();

		MethodDeclaration method = getTarget(cu, l, methodname);

		int len = l.size();
		List<String> l_bak = new ArrayList<String>(len);
		for (NumberLiteral node : l)
			l_bak.add(node.toString());

		String mutateoutput = "";
		for (int num = 0; num < time; num++) {
			for (int i = 0; i < len; i++)
				l.get(i).setToken(mutateop.randommutate(l_bak.get(i)));

			method.getName().setIdentifier(methodname + "__" + num);

			mutateoutput += method.toString();
		}

		String output = insert(source, method.getStartPosition() + method.getLength(), mutateoutput);
		Instrumenter.writeStringToFile(filepath, output);
	}

	public static MethodDeclaration getTarget(CompilationUnit cu, final List<NumberLiteral> l, final String method) {
		final List<MethodDeclaration> l_ = new ArrayList<MethodDeclaration>();
		cu.accept(new ASTVisitor() {
			public boolean visit(MethodDeclaration node) {
				if (node.getName().toString().equals(method)) {
					l_.add(node);
					return true;
				}
				return false;
			}

			public boolean visit(NumberLiteral node) {
				l.add(node);
				return true;
			}

		});
		return l_.get(0);
	}

	public static CompilationUnit getCompilationUnit(String source) {
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		// final AST ast = AST.newAST(AST.JLS3);
		parser.setSource(source.toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		return (CompilationUnit) parser.createAST(null);
	}

	public static String insert(String source, int loc, String toinsert) {
		return source.substring(0, loc) + "\n" + toinsert + source.substring(loc);
	}
}
