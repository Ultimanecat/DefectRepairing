package DefectRepairing;

import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Framework {

	public static void insertprint(String dir, String tracefile) {
		String[] args = { "-D", dir, "-T", tracefile };
		// args[0] = new String("-D " + dir + " -T ;" +
		// tracefile);//不能把参数全放arg[0]吧？
		Instrumenter.main(args);
	}

	public double getdiff(String file1, String file2) {
		String[] args = { file1, file2 };
		return parser.main(args);
	}

	public static void main(String[] args) {
		// parse cmdline
		CommandLineParser cmdlparser = new DefaultParser();
		Options options = new Options();
		options.addOption("s", "srcdir", true, "source file directory");
		options.addOption("w", "workdir", true, "d4j working directory");
		options.addOption("t", "testcase", true, "the Instrumenter case to mutate and run");
		options.addOption("n", "name", true, "test method name");
		options.addOption("v", "Verbose", false, "verbose debug");
		CommandLine commandLine = null;
		try {
			commandLine = cmdlparser.parse(options, args);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		String srcdir = "";
		String workdir = "";
		String testcase = "";
		String testmethodname = "";
		boolean verbose = false;
		if (commandLine.hasOption('s')) {
			srcdir = commandLine.getOptionValue('s');
		}
		if (commandLine.hasOption('w')) {
			workdir = commandLine.getOptionValue('w');
		}
		if (commandLine.hasOption('t')) {
			testcase = commandLine.getOptionValue('t');
		}
		if (commandLine.hasOption('v')) {
			verbose = true;
		}
		if (commandLine.hasOption('n')) {
			testmethodname = commandLine.getOptionValue('n');
		}

		// TODO mutate and get testcase list//不同程序，数据流权重适当增大；不同test，数据流权重小，甚至不考虑
		TestCaseMutation.Mutator.process(testcase,testmethodname);// TODO get method name list
		// TODO insert print
		LineNumberPreProcessor.process(srcdir);
		insertprint(srcdir, srcdir + "_ori.txt");
		// TODO run testcases and get spectrum

		// TODO compare with t0 and determine positive/negative Instrumenter

		/*
		 * TODO for each patch remove working dir checkout apply patch insert
		 * print message for each Instrumenter run -- collect spectrum compare
		 * with t0(no patch) give out score sort patches
		 */
	}

}
