package defects4j;

public class BuggyVersion {
	String project;
	int bug_id;
	String workdir;
	BuggyVersion(String project_,int bug_id_,String workdir_)
	{
		project=project_;
		bug_id=bug_id_;
		workdir=workdir_;
	}
	
	public void checkout()
	{
		StringBuilder stdout=new StringBuilder();
		StringBuilder stderr=new StringBuilder();
		defects4j.run(new String[]{"checkout","-p",project,"-v",bug_id+"b","-w",workdir},stdout, stderr);
		System.out.print(stdout);
		System.out.print(stderr);
	}
	
	
}
