package fr.sioc1981.hashcode.y2020.libraries;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Launcher {

	private static int dayThreshold;
	private static List<Integer> bookScores;
	private static List<Library> libs;
	
	private static List<Library> registrationLibs;
	private static Map<Integer, Integer> bookRegistration;
	private static Map<Integer, Integer> bookFrequency;
	
	private static long allScore;
	private static int regitrationTime;


	public static void main(String[] args) throws Exception {
		Instant start = Instant.now();

		Stream.of("a_example"
				, 
				"b_read_on"
				, 
				"c_incunabula"
				, 
				"d_tough_choices"
				,
				"e_so_many_books"
				,
				"f_libraries_of_the_world"
		).forEach(fileName -> {
			try {
				System.out.println(fileName);
				loadInput(new File("in", fileName + ".txt"));
				process();
				computeScore();
				writeOutput(registrationLibs, fileName);
				System.out.println();
				System.out.println();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		System.out.println("All Scores: " + allScore + " in " + Duration.between(start, Instant.now()));
	}

	private static void computeScore() {
		int score = bookRegistration.keySet().stream().filter(b -> bookRegistration.getOrDefault(b, Integer.MAX_VALUE) < dayThreshold).mapToInt(b -> bookScores.get(b)).sum();
		long nmBooks = bookRegistration.keySet().stream().filter(b -> bookRegistration.getOrDefault(b, Integer.MAX_VALUE) < dayThreshold).count();
		System.out.println("score: " + score + " from " + nmBooks + " books");
		allScore += score;
	}
	
	private static ArrayList<Integer> process() {
		
		while (
//				regitrationTime < dayThreshold
//				|| 
				!libs.isEmpty()
				) {
			Library lib = findNextLib();
			if( lib == null) {
				break;
			}
			registrationLibs.add(lib);
			libs.remove(lib);
			processLib(lib);
		}
		
		registrationLibs.forEach(lib -> {
			int scanDay = lib.startTime;
			int index = 0;
			ArrayList<Integer> bookToSkip = new ArrayList<>();
			for (Integer b : lib.orderedBooks) {
				if (index % lib.nbBooksByDay == 0) {
					scanDay++;
				}
				if (scanDay > bookRegistration.get(b) && scanDay < dayThreshold) {
					bookToSkip.add(b);
				}
				index++;
			}
			lib.orderedBooks.removeAll(bookToSkip);
		});
		registrationLibs = registrationLibs.stream().filter(lib -> lib.orderedBooks.size() > 0).collect(Collectors.toList());
		return null;
	}

	private static void processLib(Library lib) {
		regitrationTime += lib.signupDuration; 
		lib.startTime = regitrationTime;
	//			System.out.println("lib.id: " + lib.id);
	//			System.out.println("regitrationTime: " + regitrationTime);
	//			System.out.println("bookRegistration: " + bookRegistration);
		lib.orderedBooks = lib.scoredBooks.stream()
				.filter(b -> {
			return bookRegistration.get(b) == null || bookRegistration.get(b) > 
			dayThreshold 
			//regitrationTime
			;
		})
				.collect(Collectors.toList());
	//			System.out.println("lib.orderedBooks: " + lib.orderedBooks);
		int scanDay = regitrationTime;
		int index = 0;
		final ArrayList<Integer> toRemove = new ArrayList<Integer>();
 		for (Integer b : lib.orderedBooks) {
			if (index % lib.nbBooksByDay == 0) {
				scanDay++;
			}
			bookRegistration.put(b, scanDay);
			toRemove.add(b);
			index++;
		}
 		
 		final ArrayList<Library> libsToRemove = new ArrayList<>();
		libs.forEach(l -> {
			l.scoredBooks.removeAll(toRemove);
			if(l.scoredBooks.isEmpty()) {
				libsToRemove.add(l);
			} else {
				computeLibBooksScore(l);
			}
		});
		libs.removeAll(libsToRemove);
	}
		
	private static Library findNextLib() {
		return libs.stream().sorted(new Comparator<Library>() {

			@Override
			public int compare(Library o1, Library o2) {
				int res = 0;
//				int a1 = dayThreshold - regitrationTime - o1.signupDuration - o1.scoredBooks.size()  / o1.nbBooksByDay;
//				int a2 = dayThreshold - regitrationTime - o2.signupDuration - o2.scoredBooks.size()  / o2.nbBooksByDay;
				
//				if (a1 < 0) {
//					a1 = Integer.MAX_VALUE; 
//				}
//				
//				if (a2 < 0) {
//					a2 = Integer.MAX_VALUE; 
//				}
//				res = a1 - a2;
				
				if (res == 0) {
					// registration: shorter first
					res =  o1.signupDuration - o2.signupDuration;
				}
				if (res == 0) {
					// registration: higher score first
					res =  o2.maxScore - o1.maxScore;
				}
				if (res == 0) {
					// registration: more scans by day first
					res = o2.nbBooksByDay - o1.nbBooksByDay;
				}
				if (res == 0) {
					// more books first
					res =  o2.scoredBooks.size() - o1.scoredBooks.size();
				}
//				res = (o1.signupDuration + o1.books.size() / o1.nbBooksByDay * o1.maxScore) - (o2.signupDuration + o2.books.size() / o2.nbBooksByDay * o2.maxScore);
				return res;
			}
			
		}).findFirst().orElse(null);
	}


	private static void loadInput(File file) throws FileNotFoundException {
		libs = new ArrayList<Library>();
		regitrationTime = -1;
		bookRegistration = new HashMap<>();
		bookFrequency = new HashMap<>();
		registrationLibs = new ArrayList<Library>();
		try (final Scanner scanner = new Scanner(file)) {
			int nbBooks = scanner.nextInt();
			int nbLib = scanner.nextInt();
			dayThreshold = scanner.nextInt();
			scanner.nextLine();
			bookScores = Arrays.asList(scanner.nextLine().split(" ")).stream().map(Integer::parseInt)
					.collect(Collectors.toList());
			for (int i = 0; i < nbBooks; i++) {
				bookFrequency.put(i, 0);
			}
			int maxScore = bookScores.stream().mapToInt(b -> b).sum();
			System.out.println("nbDays: " + dayThreshold + " books: " + nbBooks  + " maxScore: " + maxScore );

//			System.out.println(bookScores);
			int id = 0;
			
			long allRegDuration = 0; 
			long allBooksByDay = 0;
			while (scanner.hasNextLine()) {
				Library lib = new Library();
				lib.id = id++;
				try {
				scanner.nextInt(); // nb Books
				lib.signupDuration = scanner.nextInt();
				allRegDuration += lib.signupDuration;
				lib.nbBooksByDay = scanner.nextInt();
				allBooksByDay += lib.nbBooksByDay;
				scanner.nextLine();
				lib.books = Arrays.asList(scanner.nextLine().split(" ")).stream().map(Integer::parseInt)
						.collect(Collectors.toList());
				lib.books.forEach(b -> bookFrequency.put(b, bookFrequency.get(b)+1));
				lib.scoredBooks = lib.books.stream().sorted(new Comparator<Integer>() {
					@Override
					public int compare(Integer lib1, Integer lib2) {
						return bookScores.get(lib1) - bookScores.get(lib2);
					}
				}).collect(Collectors.toList());
				computeLibBooksScore(lib);
				libs.add(lib);
				} catch (NoSuchElementException e) {
					// end of file
				}
			}
			System.out.println("allRegDuration: " + allRegDuration + " avg: " + allRegDuration / nbLib);
			System.out.println("allBooksByDay: " + allBooksByDay + " avg: " + allBooksByDay / nbLib);
//			System.out.println("bookFrequency: ");
//			bookFrequency.forEach((b,f) -> {
//				System.out.println("b: " + b + " f: " + f + " score:" + bookScores.get(b));
//			});
		}
	}

	private static void computeLibBooksScore(Library lib) {
//		lib.maxScore = lib.scoredBooks.stream().mapToInt(b -> bookScores.get(b)).sum();
		int newScore = 0;
		int day = regitrationTime + lib.signupDuration;
		int index = 0;
 		for (Integer b : lib.scoredBooks) {
			if (index % lib.nbBooksByDay == 0) {
				day++;
			}
			if(day == dayThreshold) {
				break;
			}
			newScore += bookScores.get(b);
			index++;
		}
		lib.maxScore = newScore;
	}

	private static void writeOutput(List<Library> libs, String fileName) throws Exception {
//		System.out.println(libs);
		FileWriter fwriter = new FileWriter(new File("out", fileName + ".out"));
		try (BufferedWriter bwriter = new BufferedWriter(fwriter)) {
			bwriter.write(Integer.toString(libs.size()));
			bwriter.write('\n');
			for (Library lib : libs) {
				bwriter.write(Integer.toString(lib.id));
				bwriter.write(" ");
				bwriter.write(Integer.toString(lib.orderedBooks.size()));
				bwriter.write('\n');
				bwriter.write(lib.orderedBooks.stream().map(i -> Integer.toString(i)).collect(Collectors.joining(" ")));
				bwriter.write('\n');
			}
		}
	}

}
