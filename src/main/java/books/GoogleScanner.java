package books;

import java.util.ArrayList;
import java.util.HashMap;

public class GoogleScanner {
	
	HashMap<Integer, LibraryDCO> registeredLibraries = new HashMap<Integer, LibraryDCO>();
	ArrayList<LibraryDCO> orderedRegisteredLibraries = new ArrayList<LibraryDCO>();
	
	HashMap<Integer, LibraryDCO> remainingLibraries = new HashMap<Integer, LibraryDCO>();
	
	private int maxDays;
	
	public GoogleScanner(int maxDays, ArrayList<LibraryDCO> allLibraries) {
		this.maxDays = maxDays;
		for(LibraryDCO library : allLibraries) {
			library.setScanner(this);
			this.remainingLibraries.put(library.getLibraryId(), library);
		}	
	}

	public boolean register(LibraryDCO library) {
		
		if(getNextPossibleRegisterDay() + library.getRegisteringTime() < maxDays)
		{
			library.register(getRemainingDays());
			registeredLibraries.put(library.getLibraryId(), library);
			orderedRegisteredLibraries.add(library);
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
		for(LibraryDCO library : registeredLibraries.values()) {
			retour += library.getRegisteringTime();
		}
		return retour;
	}
	
	public int getRemainingDays() {
		return maxDays - getNextPossibleRegisterDay();
	}
	
	public boolean isBookAlreadyScanned(Integer bookId) {
		for(LibraryDCO library : registeredLibraries.values()) {
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
		for(LibraryDCO library : remainingLibraries.values()) {
			if(library.getPotentialValue() > score && library.getRegisteringTime() + 1 <= remainingDays) {
				bestNextLibraryId = library.getLibraryId();
				score = library.getPotentialValue();
			}
		}
		return bestNextLibraryId;
	}
	
	
	public String getOutput() {
		String retour = "";
		for(LibraryDCO library : orderedRegisteredLibraries) {
			retour += library.getLibraryId();
			retour += ":";
			retour += library.getBookOrderAsString();
		}
		return retour;
	}
	
	
	public void compute() {
		int nextBestLibrary = getNextPossibleLibraryWithBestScore();
		while(nextBestLibrary != -1) {
			register(remainingLibraries.get(nextBestLibrary));
			nextBestLibrary = getNextPossibleLibraryWithBestScore();
		}
	}

	public ArrayList<LibraryDCO> getOrderedRegisteredLibraries() {
		return orderedRegisteredLibraries;
	}

	public void setOrderedRegisteredLibraries(ArrayList<LibraryDCO> orderedRegisteredLibraries) {
		this.orderedRegisteredLibraries = orderedRegisteredLibraries;
	}
	
}
