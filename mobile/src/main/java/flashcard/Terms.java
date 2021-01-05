package flashcard;

public class Terms {
	private String definition;
	private Number id;
	private Image image;
	private Number rank;
	private String term;

	public String getDefinition() {
		return this.definition;
	}

	public void setDefinition(String definition) {
		this.definition = definition;
	}

	public Number getId() {
		return this.id;
	}

	public void setId(Number id) {
		this.id = id;
	}

	public Image getImage() {
		return this.image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public Number getRank() {
		return this.rank;
	}

	public void setRank(Number rank) {
		this.rank = rank;
	}

	public String getTerm() {
		return this.term;
	}

	public void setTerm(String term) {
		this.term = term;
	}
}
