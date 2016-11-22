package defects4j;

public class Main {

	public static void main(String[] args) {
		defects4j.init();
		BuggyVersion bug=new BuggyVersion("Math",3,"/Users/liuxinyuan/DefectRepairing/Math3b");
		
		for(JavaMethod t:bug.FailingTests)
		{
			bug.test(t.toString());
		}
		
		

	}

}
