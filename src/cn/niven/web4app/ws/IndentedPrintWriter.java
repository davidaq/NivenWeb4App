package cn.niven.web4app.ws;

import java.io.PrintWriter;

public class IndentedPrintWriter {

	public int indent = 0;
	private int prevIndent = 0;
	private String indentStr = "\n";
	private boolean prevLn = false;

	private final PrintWriter printWriter;

	public IndentedPrintWriter(PrintWriter printWriter) {
		this.printWriter = printWriter;
	}

	public void print(Object o) {
		String s = o.toString();
		if (s.indexOf('\n') == -1) {
			printWriter.print(s);
			return;
		}
		if (prevIndent != indent) {
			prevIndent = indent;
			indentStr = "\n";
			for (int i = 0; i < indent; i++) {
				indentStr += "    ";
			}
		}
		if (prevLn)
			s = "\n" + s;
		if (s.charAt(s.length() - 1) == '\n') {
			prevLn = true;
			s = s.substring(0, s.length() - 1);
		}
		s = s.replaceAll("\n", indentStr);
		printWriter.print(s);
	}

	public void println(Object s) {
		print(s.toString() + "\n");
	}

	public void println() {
		print("\n");
	}

}
