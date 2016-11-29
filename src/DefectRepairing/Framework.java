package DefectRepairing;

import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Framework {

	public static void insertprint(String dir, String tracefile) {
		String[] args = { "-D", dir, "-T", tracefile };
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
		options.addOption("w", "workdir", true, " working directory");
		options.addOption("t", "testcase", true, "the Instrumenter case to mutate and run");
		options.addOption("n", "name", true, "test method name");
		options.addOption("v", "Verbose", false, "verbose debug");
		options.addOption("i", "id", true, "bugid");
		options.addOption("p", "project", true, "project name");
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
		String project = "";
		int bug_id = 0;
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
		if (commandLine.hasOption('i')) {
			bug_id = Integer.parseInt(commandLine.getOptionValue('i'));
		}
		if (commandLine.hasOption('p')) {
			project = commandLine.getOptionValue('p');
		}

		defects4j.BuggyVersion bugv = new defects4j.BuggyVersion(project, bug_id, workdir);
		// TODO mutate and get testcase list//不同程序，数据流权重适当增大；不同test，数据流权重小，甚至不考虑
		TestCaseMutation.Mutator.process(testcase, testmethodname);
		// TODO get method name list
		// TODO insert print
		LineNumberPreProcessor.process(bugv.sourcedir);
		insertprint(bugv.sourcedir, bugv.sourcedir + "_ori.txt");
		// TODO run testcases and get spectrum

		// TODO compare with t0 and determine positive/negative Instrumenter

		/*
		 * TODO for each patch remove working dir checkout apply patch insert
		 * print message for each Instrumenter run -- collect spectrum compare
		 * with t0(no patch) give out score sort patches
		 */
	}

}
