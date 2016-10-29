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
		test.main(args);
	}

	public int parsetrace(String file1, String file2) {
		String[] args = new String[1];
		args[0] = new String(file1 + " " + file2);
		return parser.main(args);
	}

	public static void main(String[] args) {
		boolean verbose = false;

		// Create a Parser
		CommandLineParser cmdlparser = new DefaultParser();
		Options options = new Options();
		options.addOption("D", "DirPath", true, "input file");
		options.addOption("T", "TraceFile", true, "output file");
		options.addOption("v", "Verbose", false, "verbose debug");
		// Parse the program arguments
		CommandLine commandLine = null;
		try {
			commandLine = cmdlparser.parse(options, args);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Set the appropriate variables based on supplied options
		String DirPath = "/Users/liuxinyuan/DefectRepairing/Time9b/src/main/";
		String TraceFilet = "/Users/liuxinyuan/DefectRepairing/a.txt";

		if (commandLine.hasOption('D')) {
			DirPath = commandLine.getOptionValue('D');
		}
		if (commandLine.hasOption('T')) {
			TraceFilet = commandLine.getOptionValue('T');
		}
		if (commandLine.hasOption('v')) {
			verbose = true;
		}

	}

}
