package books;

import java.util.ArrayList;
import java.util.HashMap;

public class GoogleScanner {
	
	HashMap<Integer, Library> registeredLibraries = new HashMap<Integer, Library>();
	
	HashMap<Integer, Library> remainingLibraries = new HashMap<Integer, Library>();
	
	private int maxDays;
	
	public GoogleScanner(int maxDays, ArrayList<Library> allLibraries) {
		this.maxDays = maxDays;
		for(Library library : allLibraries) {
			library.setScanner(this);
			this.remainingLibraries.put(library.getLibraryId(), library);
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
	
	public int getRemainingDays() {
		return maxDays - getNextPossibleRegisterDay();
	}
	
	public boolean isBookAlreadyScanned(Integer bookId) {
		for(Library library : registeredLibraries.values()) {
			if(library.hasBook(bookId)) {
				return true;
			}
		}
		return false;
	}
	
	public int getNextPossibleLibraryWithBestScore() {
		int remainingDays = getRemainingDays();
		int bestNextLibraryId = -1;
		int score = 0;
		for(Library library : remainingLibraries.values()) {
			if(library.getPotentialValue() > score && library.getRegisteringTime() + 1 <= remainingDays) {
				bestNextLibraryId = library.getLibraryId();
				score = library.getPotentialValue();
			}
		}
		return bestNextLibraryId;
	}
	
	
	public String getLibrariesOrder() {
		String retour = "";
		for(Integer libraryId : registeredLibraries.keySet()) {
			retour += libraryId;
		}
		return retour;
	}
	
}
