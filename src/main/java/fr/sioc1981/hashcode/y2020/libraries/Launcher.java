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
//		String fileName = "";
//		fileName = "a_example";
//		fileName = "b_small";
//		fileName = "c_medium";
//		fileName = "d_quite_big";
//		fileName = "e_also_big";
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
				writeOutput(registrationLibs, fileName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		System.out.println("All Scores: " + allScore + " in " + Duration.between(start, Instant.now()));
	}

	private static ArrayList<Integer> process() {
		registrationLibs = libs.stream().sorted(new Comparator<Library>() {

			@Override
			public int compare(Library o1, Library o2) {
				int res = 0;
				res =  o2.books.size() - o1.books.size();
				if (res == 0) {
					res = o2.nbBooksByDay - o1.nbBooksByDay;
				}
				if (res == 0) {
					res =  o2.signupDuration - o1.signupDuration;
				}
				return res;
			}
			
		}).collect(Collectors.toList());
		registrationLibs.forEach(lib -> {
			regitrationTime += lib.signupDuration; 
			lib.startTime = regitrationTime;
//			System.out.println("lib.id: " + lib.id);
//			System.out.println("regitrationTime: " + regitrationTime);
//			System.out.println("bookRegistration: " + bookRegistration);
			lib.orderedBooks = lib.books.stream().filter(b -> {
				return bookRegistration.get(b) == null || bookRegistration.get(b) > regitrationTime;
			}).sorted(new Comparator<Integer>() {
				@Override
				public int compare(Integer lib1, Integer lib2) {
					return bookScores.get(lib1) - bookScores.get(lib2);
				}
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
		});
		
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
		registrationLibs = registrationLibs.stream().filter(lib -> lib.orderedBooks.size() > 0).collect(Collectors.toList());
		return null;
	}

	private static void loadInput(File file) throws FileNotFoundException {
		libs = new ArrayList<Library>();
		regitrationTime = 0;
		bookRegistration = new HashMap<>();
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
				libs.add(lib);
				} catch (NoSuchElementException e) {
					// end of file
				}
			}
		}
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

//	public static class NodeTask extends RecursiveTask<Long> {
//		/**
//		 * 
//		 */
//		private static final long serialVersionUID = 104262222884216920L;
//		
//		final int index;
//		
//		
//		final long score;
//		
//		ArrayList<Integer> currentPizzasToOrder;
//		
//		NodeTask(int index, long score, ArrayList<Integer> currentPizzasToOrder) {
//			this.index = index;
//			this.score = score;
//			this.currentPizzasToOrder = currentPizzasToOrder;
//		}
//
//		public ArrayList<Integer> getCurrentPizzasToOrder() {
//			return currentPizzasToOrder;
//		}
//
//		protected Long compute() {
//			if (index < 0 || maxSlicesFound) {
//				return score;
//			}
//			 
//			long slicePizza = pizzas.get(index);
//			
//			long currentScore = score;
//			long includeScore = score + slicePizza;
//			
//			if ( includeScore == maxSlices) {
//				maxSlicesFound = true;
//				currentPizzasToOrder = (ArrayList<Integer>) currentPizzasToOrder.clone();
//				currentPizzasToOrder.add(index);
//				return (long) maxSlices;
//			}
//			
//			NodeTask nt1 = new NodeTask(index - 1, currentScore, currentPizzasToOrder);
//			nt1.fork();
//			
//			
//			NodeTask nt2 = null;
//			NodeTask nt = null;
//			if (includeScore < maxSlices) {
//				currentPizzasToOrder = (ArrayList<Integer>) currentPizzasToOrder.clone();
//				currentPizzasToOrder.add(index);
//				currentScore = includeScore;
//				nt2  = new NodeTask(index - 1, currentScore, currentPizzasToOrder);
//				nt2.setRawResult(nt2.compute());
//				nt  = nt2.getRawResult() > nt1.join() ? nt2 : nt1;
//			} else {
//				nt1.join();
//				nt = nt1;
//			}
//
//			this.currentPizzasToOrder = nt.getCurrentPizzasToOrder();
//			return nt.getRawResult();
//		}
//	}

}
