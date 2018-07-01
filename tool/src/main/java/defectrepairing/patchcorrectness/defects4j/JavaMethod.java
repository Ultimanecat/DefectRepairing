package defectrepairing.patchcorrectness.defects4j;

public class JavaMethod {
	public String FilePath;
	public String MethodName;
	public String str;
	JavaMethod(String FilePath_,String MethodName_,String str_)
	{
		FilePath=FilePath_;
		MethodName=MethodName_;
		str=str_;
	}
	public String toString()
	{
		return str;
	}
}