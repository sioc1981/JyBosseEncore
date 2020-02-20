package books;

import java.util.ArrayList;

public class LauncherDCO {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ArrayList<Book> allBooks = new ArrayList<Book>();
		Book book0 = new Book(0,1); allBooks.add(book0);
		Book book1 = new Book(1,2); allBooks.add(book1);
		Book book2 = new Book(2,3); allBooks.add(book2);
		Book book3 = new Book(3,6); allBooks.add(book3);
		Book book4 = new Book(4,5); allBooks.add(book4);
		Book book5 = new Book(5,4); allBooks.add(book5);
		
		
		
		
		ArrayList<Integer> lib0Books = new ArrayList<Integer>();
		lib0Books.add(0);
		lib0Books.add(1);
		lib0Books.add(2);
		lib0Books.add(3);
		lib0Books.add(4);
		LibraryDCO lib0 = new LibraryDCO(0, 2, 2, allBooks, lib0Books);
		
		ArrayList<Integer> lib1Books = new ArrayList<Integer>();
		lib1Books.add(3);
		lib1Books.add(2);
		lib1Books.add(5);
		lib1Books.add(0);
		LibraryDCO lib1 = new LibraryDCO(1, 2, 2, allBooks, lib1Books);
		
		ArrayList<LibraryDCO> libraries= new ArrayList<LibraryDCO>();
		libraries.add(lib0);
		libraries.add(lib1);
		
		GoogleScanner gScanner = new GoogleScanner(7, libraries);
		
		gScanner.compute();
		
		System.out.println(gScanner.getOutput());
		
	}

}
