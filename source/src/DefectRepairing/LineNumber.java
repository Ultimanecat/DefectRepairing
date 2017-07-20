package DefectRepairing;


public  class LineNumber implements Cloneable, Comparable<LineNumber> {
	int line;
	int addedline;

	public LineNumber(int _line, int _addedline) {
		line = _line;
		
		addedline = _addedline;
	}
	
	public LineNumber clone() {
		LineNumber o = null;
		try {
			o = (LineNumber) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return o;
	}
	
	LineNumber() {
		line = 0;
		addedline = 0;
	}

	public static LineNumber parserLineNumber(String s) {
		int line;// = Integer.parseInt(s);
		int addedline = 0;
		if (s.indexOf(".") != -1) {
			addedline = Integer.parseInt(s.substring(s.indexOf(".")+1));
			line=Integer.parseInt(s.substring(0,s.indexOf(".")));
		}
		else {
			line=Integer.parseInt(s);
		}
		return new LineNumber(line, addedline);
	}

	@Override
	public int compareTo(LineNumber o) {
		if (this.line == o.line) {
			if (this.addedline < o.addedline)
				return -1;
			else if (this.addedline > o.addedline)
				return 1;
			else
				return 0;
		} else {
			if (this.line < o.line)
				return -1;
			else
				return 1;
		}

	}

	@Override
	public String toString() {
		return line+"."+addedline;
	}
}