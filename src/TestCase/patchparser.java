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

import DefectRepairing.parser.LineNumber;

public class patchparser {

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
					lnmap.put(++top, new LineNumber(++fromp, 0));
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
					lnmap.put(++top, new LineNumber(lastfromp, ++addedlines));
					break;
				case NEUTRAL:
					lnmap.put(++top, new LineNumber(++fromp, 0));
					lastfromp = fromp;
					addedlines = 0;
					break;
				}
			}
		}
		// run to end
		while (top < alllines - 1) {
			lnmap.put(++top, new LineNumber(++fromp, 0));
		}
		// debug
		for (Integer i : lnmap.keySet()) {
			System.out.println(i + " " + lnmap.get(i));
		}
		System.out.println(diff.size());
		return lnmap;
	}

	public static void main(String[] args) {
		process(-1, "/home/akarin/Documents/DefectRepairing/patches/Patch6");
	}

}
