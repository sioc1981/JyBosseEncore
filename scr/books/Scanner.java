package books;

import java.util.ArrayList;

public class Scanner {
	
	ArrayList<Library> registeredLibraries = new ArrayList<Library>();
	
	private int maxDays;
	
	public Scanner(int maxDays) {
		this.maxDays = maxDays;
	}

	public boolean register(Library library) {
		
		if(getNextPossibleRegisterDay() + library.getRegisteringTime() < maxDays)
		{
			registeredLibraries.add(library);
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public int getNextPossibleRegisterDay() {
		int retour = 0;
		for(Library library : registeredLibraries) {
			retour += library.getRegisteringTime();
		}
		return retour;
	}
	
	public boolean isBookAlreadyScanned(Integer bookId) {
		for(Library library : registeredLibraries) {
			if(library.hasBook(bookId)) {
				return true;
			}
		}
		return false;
	}
	
}
