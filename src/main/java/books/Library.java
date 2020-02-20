package books;

import java.util.ArrayList;
import java.util.HashMap;

public class Library {

	private HashMap<Integer, Book> books = new HashMap<Integer, Book>();
	
	private GoogleScanner scanner;
	





	private int registeringTime;
	
	private int shippableBooksPerDay;
	
	private int libraryId;
	
	
	public Library(int libraryId, int registeringTime, int shippableBooksPerDay, ArrayList<Book> allBooks, ArrayList<Integer> libraryBooks) {
		this.setLibraryId(libraryId);
		this.setRegisteringTime(registeringTime);
		this.setShippableBooksPerDay(shippableBooksPerDay);
		
		for(Integer bookId : libraryBooks) {
			books.put(bookId, allBooks.get(bookId));
		}
	}


	public int getRegisteringTime() {
		return registeringTime;
	}


	public void setRegisteringTime(int registeringTime) {
		this.registeringTime = registeringTime;
	}
	
	public boolean hasBook(Integer bookId) {
		return books.containsKey(bookId);
	}
	
	
	public int getPotentialValue() {
		int score = 0;
		for(Book book : books.values()) {
			if(!scanner.isBookAlreadyScanned(book.getId())) {
				score += book.getScore();
			}
		}
		return score;
	}


	public int getShippableBooksPerDay() {
		return shippableBooksPerDay;
	}


	public void setShippableBooksPerDay(int shippableBooksPerDay) {
		this.shippableBooksPerDay = shippableBooksPerDay;
	}


	public int getLibraryId() {
		return libraryId;
	}


	public void setLibraryId(int libraryId) {
		this.libraryId = libraryId;
	}
	
	public GoogleScanner getScanner() {
		return scanner;
	}


	public void setScanner(GoogleScanner scanner) {
		this.scanner = scanner;
	}
	
}
