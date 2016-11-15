package demo;

import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Framework {

	public void insertprint(String dir, String tracefile) {
		String[] args = new String[1];
		args[0] = new String("-D " + dir + " -T " + tracefile);
		instrumenter.main(args);
	}

	public int getdiff(String file1, String file2) {
		String[] args = new String[1];
		args[0] = new String(file1 + " " + file2);
		return parser.main(args);
	}

	public static void main(String[] args) {
		// parse cmdline
		CommandLineParser cmdlparser = new DefaultParser();
		Options options = new Options();
		options.addOption("s", "srcdir", true, "source file directory");
		options.addOption("w", "workdir", true, "d4j working directory");
		options.addOption("t", "testcase", true, "the test case to mutate and run");
		options.addOption("v", "Verbose", false, "verbose debug");
		CommandLine commandLine = null;
		try {
			commandLine = cmdlparser.parse(options, args);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String srcdir = "";
		String workdir = "";
		String testcase = "";
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

		// TODO mutate and get testcase list

		// TODO insert print

		// TODO run testcases and get spectrum

		// TODO compare with t0 and determine positive/negative test

		/*
		 * TODO for each patch remove working dir checkout apply patch insert
		 * print message for each test run -- collect spectrum compare with
		 * t0(no patch) give out score sort patches
		 */
	}

}
