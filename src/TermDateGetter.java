import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class TermDateGetter {
	private final static String TERM_DATES_FILE_NAME = "term_dates.txt";

	/**
	 * Checks if there exists a file containing the term start dates, and if not, creates one
	 *
	 * @return An integer array containing the start and end of the terms in the format {@code YYYYMMDD}
	 */
	public static int[] getTerms() {
		int[] dates = new int[8];
		File file = new File(TERM_DATES_FILE_NAME);
		// Checks if there exists a file containing the term start dates, and if not, creates one by asking the user for the information
		try {
			System.out.print("Checking for term data...");
			Scanner scanner = new Scanner(file);

			// Loops through the file to get the dates
			// Includes a fail-safe of 8 terms to prevent index out of bounds
			for (int i = 0; scanner.hasNextLine(); i++) {
				// If there is too much data, just scrap it and ask the user to input again
				if (i >= 8) {
					file.delete();
					System.out.println("too much data found.");
					throw new FileNotFoundException("too much data");
				}

				String line = scanner.nextLine();
				int value;
				try {
					// Get the integer value of the line
					value = Integer.parseInt(line);
					// Adds the value to the dates array. Will not reach here if there is a problem with the value
					dates[i] = value;
				} catch (NumberFormatException e) {
					// Get rid of the faulty file
					file.delete();
					System.out.println("faulty data found.");
					// Ask user for new data by throwing exception
					throw new FileNotFoundException("faulty data");
				}
			}
			System.out.println("data found!");
		} catch (FileNotFoundException e) {
			// Wipe any and all data in dates
			dates = new int[8];
			// Error message if the data isn't found
			if (!e.getMessage().equals("too much data") && !e.getMessage().equals("faulty data")) {
				System.out.println("data not found.");
			}
			TermDateGetter.askUserForDates(dates);
			TermDateGetter.saveDates(dates);
		}

		return dates;
	}

	/**
	 * Asks the user for dates by using {@link TermDateGetter#getNTermDatesFromUser(char)} and adding the data to {@code dates}
	 *
	 * @param dates An integer array containing dates. Is modified by the method.
	 */
	private static void askUserForDates(int[] dates) {
		System.out.println("Please respond to the following prompts to enter the term dates: ");
		int[] aTerm = getNTermDatesFromUser('A');
		int[] bTerm = getNTermDatesFromUser('B');
		int[] cTerm = getNTermDatesFromUser('C');
		int[] dTerm = getNTermDatesFromUser('D');

		dates[0] = aTerm[0];
		dates[1] = aTerm[1];

		dates[2] = bTerm[0];
		dates[3] = bTerm[1];

		dates[4] = cTerm[0];
		dates[5] = cTerm[1];

		dates[6] = dTerm[0];
		dates[7] = dTerm[1];
	}

	/**
	 * @param term The term to get the start and end dates of
	 * @return an integer array containing the first index as the start date and the second index as the end date
	 */
	private static int[] getNTermDatesFromUser(char term) {
		int[] dates = new int[2];
		boolean validDate = false;
		boolean start = true;
		Scanner scanner = new Scanner(System.in);
		// Loop until both dates are entered and both are valid
		do {
			System.out.println("Enter the " + (start ? "start " : "end ") + "date of " + term + " term (MM/DD/YYYY): ");
			String date = scanner.next();
			if (TermDateGetter.isValidDate(date)) {
				if (start) {
					start = false;
					dates[0] = TermDateGetter.convertDateFormat(date);
				} else {
					validDate = true;
					dates[1] = TermDateGetter.convertDateFormat(date);
				}
			} else {
				System.out.println("Invalid date entered. Please try again.");
			}
		} while (!validDate);
		return dates;
	}

	/**
	 * @param date The date in the form {@code MM/DD/YYYY}
	 * @return True if the date is a valid date (assumes all months have 31 days)
	 */
	private static boolean isValidDate(String date) {
		boolean isProperLength = date.length() == 10;
		int firstSlashIndex = date.indexOf('/');
		int month = Integer.parseInt(date.substring(0, firstSlashIndex));
		int day = Integer.parseInt(date.substring(firstSlashIndex + 1, date.indexOf('/', firstSlashIndex + 1)));
		int year = Integer.parseInt(date.substring(date.lastIndexOf('/') + 1));

		boolean isValidMonth = month > 0 && month <= 12;
		boolean isValidDay = day > 0 && day <= 31;
		boolean isValidYear = year > 2022 && year < 2050; // because I can
		return isProperLength && isValidMonth && isValidDay && isValidYear;
	}

	/**
	 * @param date The date to change in the format of {@code MM/DD/YYYY}
	 * @return An int in the form {@code YYYYMMDD}
	 */
	private static int convertDateFormat(String date) {
		int firstSlashIndex = date.indexOf('/');
		int month = Integer.parseInt(date.substring(0, firstSlashIndex));
		int day = Integer.parseInt(date.substring(firstSlashIndex + 1, date.indexOf('/', firstSlashIndex + 1)));
		int year = Integer.parseInt(date.substring(date.lastIndexOf('/') + 1));
		return Integer.parseInt(String.valueOf(year) + (month < 10 ? "0" + month : month) + (day < 10 ? "0" + day : day));
	}

	/**
	 * Saves the dates in the file {@link TermDateGetter#TERM_DATES_FILE_NAME}
	 *
	 * @param dates The dates to save
	 */
	private static void saveDates(int[] dates) {
		System.out.print("Saving the dates...");
		File file;
		try {
			file = new File(TERM_DATES_FILE_NAME);
			if (!file.createNewFile()) {
				// The data exists, so delete the file
				System.out.println("dates already exists.");
				System.out.print("Overriding...");
				// Deletes file
				if (!file.delete()) {
					// If the file wasn't deleted, say there was an error
					System.out.println("error deleting previous dates.");
					System.out.println("Please manually delete the previous dates and try again");
					return;
				}
			}

			// Write the date to the file
			FileWriter fileWriter = new FileWriter(TERM_DATES_FILE_NAME);
			int i = 0;
			for (int date : dates) {
				fileWriter.write(String.valueOf(date));
				if (i < 7) fileWriter.write('\n');
				i++;
			}
			fileWriter.close();
			System.out.println("saved!");

		} catch (IOException e) {
			System.err.println("An error has occurred while saving the dates.");
			e.printStackTrace();
		}
	}
}
