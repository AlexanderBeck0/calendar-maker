public class CourseBuilder {
	private final Course aCourse = new Course();

	public CourseBuilder setTerm(String term) {
		aCourse.setTerm(cleanTerm(term));
		return this;
	}

	public CourseBuilder setCourse(String course) {
		aCourse.setCourse(course);
		return this;
	}

	public CourseBuilder setFormat(String format) {
		aCourse.setFormat(format);
		aCourse.setDisplayName(aCourse.getDisplayName());
		return this;
	}

	public CourseBuilder setMeetings(String meetings) {
		aCourse.setMeetings(meetings);
		return this;
	}

	public CourseBuilder setLocation(String location) {
		aCourse.setLocation(location);
		return this;
	}

	public CourseBuilder setInstructor(String instructor) {
		aCourse.setInstructor(instructor);
		return this;
	}

	public CourseBuilder setDelivery(String delivery) {
		aCourse.setDelivery(delivery);
		return this;
	}

	private static String cleanTerm(String uncleanedTerm) {
		// uncleaned term format is:
		// YYYY Semester TERM Term
		// i.e. 2023 Fall A Term
		return uncleanedTerm.substring(uncleanedTerm.length() - 6, uncleanedTerm.length() - 5);
	}

	public Course toCourse() {
		return aCourse;
	}


}
