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
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Launcher {

	private static int dayThreshold;
	private static List<Integer> bookScores;
	private static List<Library> libs;
	
	private static List<Library> registrationLibs;
	private static Map<Integer, Integer> bookRegistration;
	
	private static long allScore;
	private static ForkJoinPool pool;
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
				loadInput(new File("in", fileName + ".txt"));
				process();
				computeScore();
				writeOutput(registrationLibs, fileName);
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

//	private static ArrayList<Integer> process() {
//		registrationLibs = libs.stream().sorted(new Comparator<Library>() {
//
//			@Override
//			public int compare(Library o2, Library o1) {
//				int res = 0;
//				res = o1.nbBooksByDay - o2.nbBooksByDay;
//				if (res == 0) {
//					res =  o1.books.size() - o2.books.size();
//				}
//				if (res == 0) {
//					res =  o1.signupDuration - o2.signupDuration;
//				}
////				res = (o1.signupDuration + o1.books.size() / o1.nbBooksByDay * o1.maxScore) - (o2.signupDuration + o2.books.size() / o2.nbBooksByDay * o2.maxScore);
//				return res;
//			}
//			
//		}).collect(Collectors.toList());
//		registrationLibs.forEach(lib -> {
//			regitrationTime += lib.signupDuration; 
//			lib.startTime = regitrationTime;
////			System.out.println("lib.id: " + lib.id);
////			System.out.println("regitrationTime: " + regitrationTime);
////			System.out.println("bookRegistration: " + bookRegistration);
//			lib.orderedBooks = lib.scoredBooks.stream().filter(b -> {
//				return bookRegistration.get(b) == null || bookRegistration.get(b) > dayThreshold ;//regitrationTime;
//			}).collect(Collectors.toList());
////			System.out.println("lib.orderedBooks: " + lib.orderedBooks);
//			int scanDay = regitrationTime;
//			int index = 0;
//			for (Integer b : lib.orderedBooks) {
//				if (index % lib.nbBooksByDay == 0) {
//					scanDay++;
//				}
//				bookRegistration.put(b, scanDay);
//				index++;
//			}
//		});
//		
//		registrationLibs.forEach(lib -> {
//			int scanDay = lib.startTime;
//			int index = 0;
//			ArrayList<Integer> bookToSkip = new ArrayList<>();
//			for (Integer b : lib.orderedBooks) {
//				if (index % lib.nbBooksByDay == 0) {
//					scanDay++;
//				}
//				if (scanDay > bookRegistration.get(b) && scanDay < dayThreshold) {
//					bookToSkip.add(b);
//				}
//				index++;
//			}
//			lib.orderedBooks.removeAll(bookToSkip);
//		});
//		registrationLibs = registrationLibs.stream().filter(lib -> lib.orderedBooks.size() > 0).collect(Collectors.toList());
//		return null;
//	}
	
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
		lib.orderedBooks = lib.scoredBooks.stream().filter(b -> {
			return bookRegistration.get(b) == null || bookRegistration.get(b) > dayThreshold ;//regitrationTime;
		}).collect(Collectors.toList());
	//			System.out.println("lib.orderedBooks: " + lib.orderedBooks);
		int scanDay = regitrationTime;
		int index = 0;
		for (Integer b : lib.orderedBooks) {
			if (index % lib.nbBooksByDay == 0) {
				scanDay++;
			}
			bookRegistration.put(b, scanDay);
			index++;
		}
		libs.forEach(l -> { 
			l.scoredBooks.removeAll(bookRegistration.keySet());
			computeLibBooksScore(l);
		});
	}
		
	private static Library findNextLib() {
		return libs.stream().sorted(new Comparator<Library>() {

			@Override
			public int compare(Library o2, Library o1) {
				int res = 0;
				res =  o1.maxScore - o2.maxScore;
				if (res == 0) {
					res = o1.nbBooksByDay - o2.nbBooksByDay;
				}
				if (res == 0) {
					res =  o1.signupDuration - o2.signupDuration;
				}
				if (res == 0) {
					res =  o1.scoredBooks.size() - o2.scoredBooks.size();
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
		registrationLibs = new ArrayList<Library>();
		try (final Scanner scanner = new Scanner(file)) {
			scanner.nextInt();
			scanner.nextInt();
			dayThreshold = scanner.nextInt();
			System.out.println(dayThreshold);
			scanner.nextLine();
			bookScores = Arrays.asList(scanner.nextLine().split(" ")).stream().map(Integer::parseInt)
					.collect(Collectors.toList());

//			System.out.println(bookScores);
			int id = 0;
			while (scanner.hasNextLine()) {
				Library lib = new Library();
				lib.id = id++;
				try {
				scanner.nextInt(); // nb Books
				lib.signupDuration = scanner.nextInt();
				lib.nbBooksByDay = scanner.nextInt();
				scanner.nextLine();
				lib.books = Arrays.asList(scanner.nextLine().split(" ")).stream().map(Integer::parseInt)
						.collect(Collectors.toList());
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
		}
	}

	private static void computeLibBooksScore(Library lib) {
		lib.maxScore = lib.scoredBooks.stream().mapToInt(b -> bookScores.get(b)).sum();
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

	public static class NodeTask extends RecursiveTask<Void> {
		/**
		 * 
		 */
		private static final long serialVersionUID = 104262222884216920L;
		
		final int index;
		
		
		final long score;
		
		ArrayList<Integer> currentPizzasToOrder;
		
		NodeTask(int index, long score, ArrayList<Integer> currentPizzasToOrder) {
			this.index = index;
			this.score = score;
			this.currentPizzasToOrder = currentPizzasToOrder;
		}

		public ArrayList<Integer> getCurrentPizzasToOrder() {
			return currentPizzasToOrder;
		}

		protected Void compute() {
			if (regitrationTime >= dayThreshold) {
				return null;
			}
			 
			Library lib = findNextLib();
			
			regitrationTime += lib.signupDuration; 
			lib.startTime = regitrationTime;
//			System.out.println("lib.id: " + lib.id);
//			System.out.println("regitrationTime: " + regitrationTime);
//			System.out.println("bookRegistration: " + bookRegistration);
			lib.orderedBooks = lib.scoredBooks.stream().filter(b -> {
				return bookRegistration.get(b) == null; // || bookRegistration.get(b) > dayThreshold ;//regitrationTime;
			}).collect(Collectors.toList());
//			System.out.println("lib.orderedBooks: " + lib.orderedBooks);
			int scanDay = regitrationTime;
			int index = 0;
			for (Integer b : lib.orderedBooks) {
				if (index % lib.nbBooksByDay == 0) {
					scanDay++;
				}
				if(scanDay < dayThreshold) {
					bookRegistration.put(b, scanDay);
				}
				index++;
			}
			
			return null;
		}

		private Library findNextLib() {
			return libs.stream().sorted(new Comparator<Library>() {

				@Override
				public int compare(Library o2, Library o1) {
					int res = 0;
					res = o1.nbBooksByDay - o2.nbBooksByDay;
					if (res == 0) {
						res =  o1.books.size() - o2.books.size();
					}
					if (res == 0) {
						res =  o1.signupDuration - o2.signupDuration;
					}
//					res = (o1.signupDuration + o1.books.size() / o1.nbBooksByDay * o1.maxScore) - (o2.signupDuration + o2.books.size() / o2.nbBooksByDay * o2.maxScore);
					return res;
				}
				
			}).findFirst().orElse(null);
		}
	}

}
