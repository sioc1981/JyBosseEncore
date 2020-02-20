package books;

public class Book {

	private int id;
	
	private int score;
	
	public Book(int id, int score) {
		this.setId(id);
		this.setScore(score);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}
	
}
