package entity_display;

import entity.Term;

public class MTerms extends Term implements Comparable<MTerms> {

	private String locale;

    public String langfrom;
    public String langto;


	public int status = 0; // 0 la chua chon, 1 la chon dung, 2 la chon sai

	// status con duoc dung trong BaseCardListActivity -1 la group 0 la normal


	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	@Override
	public int compareTo(MTerms another) {
		if (this.box > another.box)
			return 1;
		else if (this.box < another.box)
			return -1;
		else
			return 0;
	}
}
