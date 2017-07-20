import java.io.File;
import java.util.*;

public class Utils {

	public static void getFilelist(String DirPath, List<String> FileList) {
		File RootDir = new File(DirPath);
		File[] files = RootDir.listFiles();

		for (File f : files) {
			if (f.isDirectory()) {
				getFilelist(f.getAbsolutePath(), FileList);
			} else {
					FileList.add(f.getAbsolutePath());
			}
		}
	}
	

}
