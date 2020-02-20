package books;

import java.util.ArrayList;

public class Launcher {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Book book0 = new Book(0,1);
		Book book1 = new Book(1,2);
		Book book2 = new Book(2,3);
		Book book3 = new Book(3,6);
		Book book4 = new Book(4,5);
		Book book5 = new Book(5,4);
		
		ArrayList<Book> allBooks = new ArrayList<Book>();
		
		ArrayList<Integer> lib0Books = new ArrayList<Integer>();
		lib0Books.add(0);
		lib0Books.add(1);
		lib0Books.add(2);
		lib0Books.add(3);
		lib0Books.add(4);
		Library lib0 = new Library(2, 2, allBooks, lib0Books);
		
		ArrayList<Integer> lib1Books = new ArrayList<Integer>();
		lib0Books.add(1);
		lib0Books.add(3);
		lib0Books.add(4);
		Library lib1 = new Library(2, 2, allBooks, lib1Books);
		
	}

}
