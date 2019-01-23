import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class Rule {

	// These are not private to increase performance (no getters, only
	// references to these fields)
	public int[] s; // survival, number of ON cells that cause an ON cell to
					// remain ON
	public int[] b; // birth, number of ON cells that cause an OFF cell to turn
					// ON
	String info;

	public Rule(String rule) {
		try {
			fromString(rule);
		} catch (Exception e) {
			s = null;
			b = null;
		}
		info = rule;
	}

	public Rule(FileReader file) {
		BufferedReader br = new BufferedReader(file);
		try {
			String rule = br.readLine();
			fromString(rule);
			if (br.ready())
				info = br.readLine();
			else
				info = rule;
		} catch (Exception e) {
			s = null;
			b = null;
		}
	}

	public Rule(int[] b, int[] s) {
		this.s = s;
		this.b = b;
	}

	private void fromString(String rule) throws Exception {
		String sSt, bSt;
		rule = rule.toLowerCase().trim().replaceAll("[bs]", "").replaceAll(" ", "");
		sSt = rule.substring(rule.indexOf("/") + 1);
		bSt = rule.substring(0, rule.indexOf("/"));
		s = new int[sSt.length()];
		b = new int[bSt.length()];
		for (int i = 0; i < s.length; i++)
			s[i] = Integer.parseInt(String.valueOf(sSt.charAt(i)));
		for (int i = 0; i < b.length; i++)
			b[i] = Integer.parseInt(String.valueOf(bSt.charAt(i)));
	}

	public String toString() {
		if (s == null && b == null)
			return "N/A";
		if (s.length == 0 && b.length == 0)
			return "N/A";
		String s = toRuleString();
		if (s.equals(info))
			return s;
		return "<html><div style='text-align: center;'><b>" + s + "</b><br>" + info + "</div></html>";
	}
	
	public String toRuleString(){
		String sSt = "S";
		String bSt = "B";
		for (int i = 0; i < s.length; i++)
			sSt += s[i];
		for (int i = 0; i < b.length; i++)
			bSt += b[i];
		return bSt + "/" + sSt;
	}

	public String getInfo() {
		return info;
	}

	public int[] getS() {
		return s;
	}

	public int[] getB() {
		return b;
	}
}
