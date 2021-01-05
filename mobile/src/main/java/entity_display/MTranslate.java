package entity_display;

import entity.Translate;

public class MTranslate extends Translate implements Comparable<MTranslate> {

	private String locale;
    public int box = -1; // -1 No Answers yet, 0 Often miss, 1 Sometimes miss, 2 Seldom miss, 3 Never miss
    public String langfrom;
    public String langto;


	public int status = 0; //-1 la group

	// status con duoc dung trong BaseCardListActivity -1 la group 0 la normal


	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	@Override
	public int compareTo(MTranslate another) {
		if (this.box > another.box)
			return 1;
		else if (this.box < another.box)
			return -1;
		else
			return 0;
	}
}
