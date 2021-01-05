package flashcard;

public class Creator {
	private String account_type;
	private Number id;
	private String profile_image;
	private String username;

	public String getAccount_type() {
		return this.account_type;
	}

	public void setAccount_type(String account_type) {
		this.account_type = account_type;
	}

	public Number getId() {
		return this.id;
	}

	public void setId(Number id) {
		this.id = id;
	}

	public String getProfile_image() {
		return this.profile_image;
	}

	public void setProfile_image(String profile_image) {
		this.profile_image = profile_image;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}
