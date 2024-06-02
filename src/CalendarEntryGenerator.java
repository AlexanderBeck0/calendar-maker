import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class CalendarEntryGenerator {
	private static int[] terms;
	private static final Map<String, Integer> TERM_MAP = new HashMap<>(8);

	public static void setTerms(int[] terms) {
		CalendarEntryGenerator.terms = terms;
	}

	static {
		// Update the seasons to the term map
		TERM_MAP.put("Fall", 0);   // Fall starts at index 0
		TERM_MAP.put("Spring", 4); // Spring starts at index 4
	}

	/**
	 * Generates a calendar event in the ICS format:
	 * <code><br>
	 * BEGIN:VEVENT<br>
	 * DTSTART;TZID=America/New_York:20230824T{@link Course#getStartTime() start time}<br>
	 * DTEND;TZID=America/New_York:20230824T{@link Course#getEndTime() end time}<br>
	 * RRULE:FREQ=WEEKLY;UNTIL={END DATE + 1}T035959Z;BYDAY={@link Course#getDays() days}<br>
	 * DTSTAMP:20230813T211907Z<br>
	 * CREATED:20230805T145653Z<br>
	 * DESCRIPTION:{@link Course#getCourse() course title}\n{@link Course#getMeetings() meetings} | {@link Course#getLocation() location}\n{@link Course#getInstructor() instructor}<br>
	 * LAST-MODIFIED:20230805T145653Z<br>
	 * SEQUENCE:0<br>
	 * STATUS:CONFIRMED<br>
	 * SUMMARY:{@link Course#getDisplayName() display name}<br>
	 * TRANSP:OPAQUE<br>
	 * END:VEVENT<br>
	 * </code>
	 *
	 * @param course The course to generate the event of
	 * @return A String of a generated event in the proper ICS format
	 */
	public static String generateEvent(Course course) {
		if (CalendarEntryGenerator.terms == null) {
			throw new RuntimeException("An error has occurred. A calendar entry was attempted to be created without defining terms");
		}

		// The days of the week of which the course is held
		String days = course.getDays();

		String term = course.getTerm();
		int termIndex = CalendarEntryGenerator.getTermIndex(term);

		if (termIndex == -1) {
			throw new IllegalArgumentException("Invalid term: " + term);
		}

		// Determine the first day after the starting day of the term that the class has
		// This prevents all the classes being present on the first day of the term, regardless if that class is actually happening that day
		String firstDayOfTerm;
		int endTermIndex;
		if (term.length() > 2) {
			int startTermIndex = TERM_MAP.get(term);
			endTermIndex = startTermIndex + 3; // Fall ends at index 3, Spring ends at index 7
			firstDayOfTerm = CalendarEntryGenerator.getDayOfWeek(terms[startTermIndex]);
			termIndex = startTermIndex;
		} else {
			firstDayOfTerm = CalendarEntryGenerator.getDayOfWeek(terms[termIndex]);
			endTermIndex = termIndex + 1;
		}

		int dayShift = 0;
		while (!days.contains(firstDayOfTerm)) {
			firstDayOfTerm = CalendarEntryGenerator.getDayOfWeek(terms[termIndex] + dayShift);
			dayShift++;
		}

		// Get rid of the extra shift at the end
		if (dayShift > 0) dayShift--;

		// Get the current date and time
		String currentDate = String.valueOf(java.time.LocalDateTime.now());
		currentDate = currentDate.substring(0, currentDate.indexOf("."));
		currentDate = currentDate.replaceAll("-", "").replaceAll(":", "");

		// Create the description
		String description = course.getCourse() + "\\n" + course.getMeetings() + " | " + course.getLocation() + "\\n" + course.getInstructor();
		// Make sure that the description is not longer than 75 characters (75 - 13 for DESCRIPTION:) to follow ICS guidelines
		// ICS requires that new lines be CRLF instead of \n.
		final String delim = "\r\n";
		description = "DESCRIPTION:" + description;
		description = CalendarEntryGenerator.delimiterEveryNCharacters(description, 75, delim + " ");
		description = description.replace("DESCRIPTION:", "");

		return "BEGIN:VEVENT" + delim +
				"DTSTART;TZID=America/New_York:" + (terms[termIndex] + dayShift) + "T" + course.getStartTime() + delim +
				"DTEND;TZID=America/New_York:" + (terms[termIndex] + dayShift) + "T" + course.getEndTime() + delim +
				"RRULE:FREQ=WEEKLY;UNTIL=" + (terms[endTermIndex] + 1) + "T035959Z;BYDAY=" + days + delim +
				"DTSTAMP:" + currentDate + "Z" + delim +
				"UID:" + UUID.randomUUID() + delim +
				"CREATED:" + currentDate + "Z" + delim +
				"DESCRIPTION:" + description + delim +
				"LAST-MODIFIED:" + currentDate + "Z" + delim +
				"SEQUENCE:0" + delim +
				"STATUS:CONFIRMED" + delim +
				"SUMMARY:" + course.getDisplayName() + delim +
				"TRANSP:OPAQUE" + delim +
				"END:VEVENT";
	}

	/**
	 * Uses {@link CalendarEntryGenerator#TERM_MAP}
	 *
	 * @param term The term to get the term index of
	 * @return The index of either a {@link CalendarEntryGenerator#TERM_MAP} index, an index to the terms, or -1 if it is not found
	 */
	private static int getTermIndex(String term) {
		if (term.length() == 1) {
			// Multiply by 2 since there is a start and end of every term
			// 65 represents 'A'
			return (term.charAt(0) - 65) * 2;
		} else {
			Integer index = TERM_MAP.get(term);
			return index != null ? index : -1;
		}
	}

	/**
	 * This is to fold text to be compliant with ICS guidelines
	 *
	 * @param inputString The input string to split
	 * @param n           The number of characters to split after
	 * @param delimiter   The delimiter to join the split string with
	 * @return The input string with the delimiter every n characters
	 */
	private static String delimiterEveryNCharacters(String inputString, int n, String delimiter) {
		String outputString = inputString;
		if (inputString.length() > n) {
			StringBuilder newString = new StringBuilder();
			String[] splitString = outputString.split("(?<=\\G.{" + n + "})");
			for (int i = 0; i < splitString.length; i++) {
				String s = splitString[i];
				newString.append(s);
				if (i < splitString.length - 1) newString.append(delimiter);
			}
			outputString = newString.toString();
		}
		return outputString;
	}

	/**
	 * @param date The date from which to get the day of the week
	 * @return The day of the week shortened (i.e. MO for Monday)
	 */
	private static String getDayOfWeek(int date) {
		// Get the date in a LocalDate object
		String termString = String.valueOf(date);
		int year = Integer.parseInt(termString.substring(0, 4));
		int month = Integer.parseInt(termString.substring(4, 6));
		int day = Integer.parseInt(termString.substring(6));
		LocalDate localDate = LocalDate.of(year, month, day);

		// Get the day of the week of the date
		Locale locale = new Locale("en");
		DayOfWeek dayOfWeek = localDate.getDayOfWeek();
		String displayName = dayOfWeek.getDisplayName(TextStyle.FULL, locale);

		// Turn the display name into the same format as it is in the calendar
		displayName = displayName.replace("Monday", "MO");
		displayName = displayName.replace("Tuesday", "TU");
		displayName = displayName.replace("Wednesday", "WE");
		displayName = displayName.replace("Thursday", "TH");
		displayName = displayName.replace("Friday", "FR");
		return displayName;
	}
}
