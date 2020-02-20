package books;

import java.util.ArrayList;
import java.util.HashMap;

public class GoogleScanner {
	
	HashMap<Integer, Library> registeredLibraries = new HashMap<Integer, Library>();
	
	ArrayList<Library> remainingLibraries = new ArrayList<Library>();
	
	private int maxDays;
	
	public GoogleScanner(int maxDays, ArrayList<Library> allLibraries) {
		this.maxDays = maxDays;
		for(Library library : allLibraries) {
			this.remainingLibraries.add(library);
		}	
	}

	public boolean register(Library library) {
		
		if(getNextPossibleRegisterDay() + library.getRegisteringTime() < maxDays)
		{
			registeredLibraries.put(library.getLibraryId(), library);
			remainingLibraries.remove(library.getLibraryId());
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public int getNextPossibleRegisterDay() {
		int retour = 0;
		for(Library library : registeredLibraries.values()) {
			retour += library.getRegisteringTime();
		}
		return retour;
	}
	
	public boolean isBookAlreadyScanned(Integer bookId) {
		for(Library library : registeredLibraries.values()) {
			if(library.hasBook(bookId)) {
				return true;
			}
		}
		return false;
	}
	
	public int getNextLibraryWithBestScore() {
		int bestNextLibraryId = -1;
		int score = 0;
		for(Library library : remainingLibraries) {
			if(library.getPotentialValue() > score) {
				bestNextLibraryId = library.getLibraryId();
				score = library.getPotentialValue();
			}
		}
		return bestNextLibraryId;
	}
	
}
