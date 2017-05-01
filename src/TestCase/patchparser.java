package TestCase;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.wickedsource.diffparser.api.DiffParser;
import org.wickedsource.diffparser.api.UnifiedDiffParser;
import org.wickedsource.diffparser.api.model.Diff;
import org.wickedsource.diffparser.api.model.Hunk;
import org.wickedsource.diffparser.api.model.Line;

import DefectRepairing.LineNumber;


public class patchparser {

	static LineNumber getLineNumber(int line,int addedline){
		return new LineNumber(line,addedline);
	}
	
	public static Map<Integer, LineNumber> process(int alllines, String filepath) {
		// TODO Auto-generated method stub
		DiffParser parser = new UnifiedDiffParser();
		InputStream in = null;
		try {
			in = new FileInputStream(filepath);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<Diff> diff = parser.parse(in);
		Diff d = diff.get(0);
		Map<Integer, LineNumber> lnmap = new HashMap<Integer, LineNumber>();
		List<Hunk> hunks = d.getHunks();
		int fromp = -1, top = -1, addedlines = -1, lastfromp = -1;
		for (Iterator<Hunk> it = hunks.iterator(); it.hasNext();) {
			Hunk h = it.next();
			List<Line> lines = h.getLines();
			Iterator<Line> lit = lines.iterator();
			/*
			 * fromp = h.getFromFileRange().getLineStart()-1; top =
			 * h.getToFileRange().getLineStart()-1;
			 * addedlines=0;lastfromp=fromp;
			 */
			if (fromp != -1) {
				while (fromp < h.getFromFileRange().getLineStart() - 1) {
					lnmap.put(++top, getLineNumber(++fromp, 0));
				}
			} else {
				fromp = h.getFromFileRange().getLineStart() - 1;
				top = h.getToFileRange().getLineStart() - 1;
			}
			while (lit.hasNext()) {
				Line l = lit.next();
				switch (l.getLineType()) {
				case FROM:
					fromp++;
					break;
				case TO:
					lnmap.put(++top, getLineNumber(lastfromp, ++addedlines));
					break;
				case NEUTRAL:
					lnmap.put(++top, getLineNumber(++fromp, 0));
					lastfromp = fromp;
					addedlines = 0;
					break;
				}
			}
		}
		// run to end
		while (top < alllines) {
			lnmap.put(++top, getLineNumber(++fromp, 0));
		}
		// debug
//		for (Integer i : lnmap.keySet()) {
//			System.out.println(i + " " + lnmap.get(i).toString());
//		}
		//System.out.println(diff.size());
		for(int i=1;i<=alllines;i++)
		{
			
			if(!lnmap.containsKey(i)){
				lnmap.put(i, getLineNumber(i,0));
			}
			
		}
		return lnmap;
	}

	public static void main(String[] args) {
		Map<Integer, LineNumber>m=process(2000, "/Volumes/Unnamed/instr/patches/Patch1");
		System.out.println(m.size());
		for (Integer i : m.keySet()) {
			System.out.println(i + " " + m.get(i).toString());
		}
	}
}