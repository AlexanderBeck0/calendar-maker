public class Course {
	private String term;
	private String course;
	private String format;
	private String meetings;
	private String location;
	private String instructor;
	private String delivery;
	private String displayName;
	private boolean isSemester;

	public Course() {
		this(null, null, null, null, null, null, null);
	}

	public Course(String term, String course, String format, String meetings, String location, String instructor, String delivery) {
		this.term = term;
		this.course = course;
		this.format = format;
		this.meetings = meetings;
		this.location = location;
		this.instructor = instructor;
		this.delivery = delivery;
		this.isSemester = false;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public String getCourse() {
		return course;
	}

	public void setCourse(String course) {
		this.course = course;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getMeetings() {
		return meetings;
	}

	public void setMeetings(String meetings) {
		this.meetings = meetings;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getInstructor() {
		return instructor;
	}

	public void setInstructor(String instructor) {
		this.instructor = instructor;
	}

	public String getDelivery() {
		return delivery;
	}

	public void setDelivery(String delivery) {
		this.delivery = delivery;
	}

	public String getDisplayName() {
		if (format == null || course == null) return null;
		String newDisplayName = course.substring(0, course.indexOf('-'));

		if (format.equals("Laboratory") || format.equals("Discussion")) {
			// Lab/Discussion format (includes group in the name)
			newDisplayName = course.substring(0, course.indexOf('-', course.indexOf('-') + 1) - 1);
			if (course.contains("/")) {
				newDisplayName = course.substring(0, course.indexOf("/"));
			}
		}

		return newDisplayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/**
	 * The ICS format of {@code BYDAY}:<br>
	 * {@code BYDAY=MO,TU,WE,TH,FR}
	 * <br>Input is {@code M-T-W-R-F}
	 *
	 * @return The days of the course following the ICS format of {@code BYDAY}
	 */
	public String getDays() {
		String days = meetings.substring(0, meetings.indexOf('|') - 1);
		if (days.contains("S")) {
			throw new RuntimeException("Error! There is either a Saturday or a Sunday within the meeting dates, which is unsupported.");
		}

		days = days.replace('-', ',');
		days = days.replace("M", "MO");
		days = days.replace("T", "TU");
		days = days.replace("W", "WE");
		days = days.replace("R", "TH");
		days = days.replace("F", "FR");
		return days;
	}

	/**
	 * The ICS format is as follows:<br>
	 * {@code HRMMSS}<br>
	 *
	 * @return The start time in the ICS format
	 */
	public String getStartTime() {
		// Get the start time of the course with AM/PM included
		String startTime = meetings.substring(meetings.indexOf('|') + 2, meetings.lastIndexOf('-') - 1);
		int hour = Integer.parseInt(startTime.substring(0, startTime.indexOf(':')));
		int minute = Integer.parseInt(startTime.substring(startTime.indexOf(':') + 1, (startTime.indexOf("A") > 0 ? startTime.indexOf("A") : startTime.indexOf("P")) - 1));
		if (!startTime.contains("A")) {
			// It is PM so turn into military time
			hour += 12;
			// 12 PM is 12 and not 24
			if (hour == 24) hour = 12;
		}

		return String.valueOf(hour >= 10 ? hour : "0" + hour) + (minute > 0 ? minute : "00") + "00";
	}

	/**
	 * The ICS format is as follows:<br>
	 * {@code HRMMSS}<br>
	 *
	 * @return The end time in the ICS format
	 */
	public String getEndTime() {
		// Get the end time of the course with AM/PM included
		String endTime = meetings.substring(meetings.lastIndexOf('-') + 2);
		int hour = Integer.parseInt(endTime.substring(0, endTime.indexOf(':')));
		int minute = Integer.parseInt(endTime.substring(endTime.indexOf(':') + 1, (endTime.indexOf("A") > 0 ? endTime.indexOf("A") : endTime.indexOf("P")) - 1));
		if (!endTime.contains("A")) {
			// It is PM so turn into military time
			hour += 12;
			// 12 PM is 12 and not 24
			if (hour == 24) hour = 12;
		}

		return String.valueOf(hour >= 10 ? hour : "0" + hour) + (minute > 0 ? minute : "00") + "00";
	}

	public boolean isSemester() {
		return isSemester;
	}

	public void setSemester(boolean semester) {
		isSemester = semester;
	}

	@Override
	public String toString() {
		return "Course{" +
				"term='" + term + '\'' +
				", course='" + course + '\'' +
				", format='" + format + '\'' +
				", meetings='" + meetings + '\'' +
				", location='" + location + '\'' +
				", instructor='" + instructor + '\'' +
				", delivery='" + delivery + '\'' +
				", displayName='" + displayName + '\'' +
				'}';
	}
}
