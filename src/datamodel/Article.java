package datamodel;

/**
 * Article
 */
public class Article {
	private final String id;
	private final String description;
	
	public Article(String id, String description) {
		this.id = id;
		this.description = description;
	}

	public String getId() {
		return id;
	}
	public String getDescription() {
		return description;
	}
}
