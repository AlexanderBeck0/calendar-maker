import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Main {
	private static List<Course> courses;

	public static void main(String[] args) {
		// Make the selector look nicer
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		// Ask the user to choose a file
		JFileChooser jFileChooser = new JFileChooser("e:/downloads", FileSystemView.getFileSystemView());
		File inputFile = XLSXReader.getInputFile(jFileChooser);
		if (inputFile != null) {
			Main.courses = XLSXReader.readXLSXFile(inputFile);
		}

		// Get the terms
		int[] terms = TermDateGetter.getTerms();
		CalendarEntryGenerator.setTerms(terms);

//		Main.printCalendar();
		Main.saveCalendar("calendar.ics");
	}

	/**
	 * Prints the calendar using the ICS format
	 */
	private static void printCalendar() {
		System.out.println("""
				BEGIN:VCALENDAR
				PRODID:-//EN
				VERSION:2.0
				BEGIN:VTIMEZONE
				TZID:America/New_York
				BEGIN:DAYLIGHT
				TZOFFSETFROM:-0500
				TZOFFSETTO:-0400
				TZNAME:EDT
				END:DAYLIGHT
				END:VTIMEZONE""");
		for (Course course : courses) {
			System.out.println(CalendarEntryGenerator.generateEvent(course));
		}
		System.out.println("END:VCALENDAR");
	}

	/**
	 * Saves the calendar in fileName
	 *
	 * @param fileName The name of the file to save the calendar in. Preferably in ICS format (.ics)
	 */
	private static void saveCalendar(String fileName) {
		System.out.print("Saving the calendar...");
		File file;
		try {
			file = new File(fileName);
			if (!file.createNewFile()) {
				// The calendar exists, so delete the file
				System.out.println("calendar already exists.");
				System.out.print("Overriding...");
				if (!file.delete()) {
					System.out.println("error deleting previous calendar.");
					System.out.println("Please manually delete the previous calendar and try again");
					return;
				}
			}
			// ICS requires that new lines be CRLF instead of \n.
			final String delim = "\r\n";

			// Write the calendar to the file
			Writer fileWriter = new OutputStreamWriter(new FileOutputStream(fileName), StandardCharsets.UTF_8);
			fileWriter.write("BEGIN:VCALENDAR" + delim +
					"VERSION:2.0" + delim +
					"PRODID:<My Calendar>" + delim +
					"BEGIN:VTIMEZONE" + delim +
					"TZID:America/New_York" + delim +
					"BEGIN:DAYLIGHT" + delim +
					"TZOFFSETFROM:-0500" + delim +
					"TZOFFSETTO:-0400" + delim +
					"TZNAME:EDT" + delim +
					"END:DAYLIGHT" + delim +
					"END:VTIMEZONE" + delim);
			int i = 0;
			for (Course course : courses) {
				fileWriter.write(CalendarEntryGenerator.generateEvent(course));
				if (i < courses.size() - 1) fileWriter.write(delim);
				i++;
			}
			fileWriter.write("\r\nEND:VCALENDAR");
			fileWriter.close();
			System.out.println("saved!");

		} catch (IOException e) {
			System.err.println("An error has occurred while saving the calendar.");
			e.printStackTrace();
		}
	}
}

