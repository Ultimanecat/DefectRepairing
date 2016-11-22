package defects4j;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;



public class BuggyVersion {
	String project;
	int bug_id;
	String workdir;
	public String sourcedir;
	public String testdir;
	public List<JavaMethod>FailingTests;
	
	BuggyVersion(String project_,int bug_id_,String workdir_)
	{
		project=project_;
		bug_id=bug_id_;
		workdir=workdir_;
		checkout();
		getsrcdir();
		getFailingTests();
	}
	
	public void checkout()
	{
		StringBuilder stdout=new StringBuilder();
		StringBuilder stderr=new StringBuilder();
		defects4j.run(new String[]{"checkout","-p",project,"-v",bug_id+"b","-w",workdir},stdout, stderr);
	}
	
	public void test(String testcase)
	{
		StringBuilder stdout=new StringBuilder();
		StringBuilder stderr=new StringBuilder();
		defects4j.run(new String[]{"test","-t",testcase,"-w",workdir},stdout, stderr);
	}
	
	public void getFailingTests()
	{
		FailingTests=new ArrayList<JavaMethod>();
		List<String>FailingTestsString=defects4j.get_failing_tests(project, bug_id);
		for(String test:FailingTestsString)
		{
			String [] tmp=test.split("::");
			tmp[0]=tmp[0].replace('.', '/');
			FailingTests.add(new JavaMethod(Paths.get(testdir,tmp[0]+".java").toString(),tmp[1],test));
		}
	}
	
	public void getsrcdir()
	{
		SAXBuilder saxBuilder = new SAXBuilder();
		try {
			Document doc = saxBuilder.build(Paths.get(workdir, "build.xml").toString());
			Element root = doc.getRootElement();
			List<Element> l=root.getChildren("property");
			for(Element e:l){
				if(e.getAttributeValue("name")!=null)
					if(e.getAttributeValue("name").equals("source.home"))
						sourcedir=Paths.get(workdir, e.getAttributeValue("value")).toString();
					else if(e.getAttributeValue("name").equals("test.home"))
						testdir=Paths.get(workdir,e.getAttributeValue("value")).toString();
            }
		} catch (JDOMException | IOException e) {
			e.printStackTrace();
		}
	}
	
	
}
