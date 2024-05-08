import com.aspose.cells.LoadOptions;
import com.aspose.cells.Workbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class XLSXReader {
	private final static String FIXED_FILE_NAME = "fixed_xlsx.xlsx";

	/**
	 * @param jFileChooser The file chooser to get the file from
	 * @return The file that the user selects
	 */
	public static File getInputFile(JFileChooser jFileChooser) {
		FileNameExtensionFilter filter = new FileNameExtensionFilter("XLSX", "xlsx");
		jFileChooser.setFileFilter(filter);

		// Get the input file
		int returnVal = jFileChooser.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			return jFileChooser.getSelectedFile();
		}
		return null;
	}

	/**
	 * @param corruptFile The corrupt file
	 * @throws Exception Thrown if there is an error
	 * @apiNote Uses aspose-cells. Required to throw Exception because of said API. Locate said API here: <a href="https://releases.aspose.com/cells/java/">Aspose Cells</a>
	 */
	private static void fixCorruptXLSXFile(File corruptFile) throws Exception {
		// Since the main file that this entire project is for is corrupt, assume that the xlsx is corrupt and fix it
		// Store the output in FIXED_FILE_NAME
		FileInputStream fileInputStream = new FileInputStream(corruptFile);
		LoadOptions options = new LoadOptions();
		com.aspose.cells.Workbook workbookFixer = new Workbook(fileInputStream, options);
		workbookFixer.save(XLSXReader.FIXED_FILE_NAME);
	}

	/**
	 * Deletes the file named in {@link XLSXReader#FIXED_FILE_NAME}
	 */
	private static void deleteFixedXLSXFile() {
		// Delete the fixed file
		File fixedFile = new File(XLSXReader.FIXED_FILE_NAME);
		if (!fixedFile.delete()) {
			throw new RuntimeException("Error deleting the fixed XLSX file. Please try deleting it manually and try again.");
		}
	}

	/**
	 * Stores each class in a {@link Course}
	 *
	 * @param file The file to read
	 * @apiNote Uses all the following: poi-3.17, poi-ooxml-3.17, poi-ooxml-schemas-3.17, xmlbeans-2.6.0, commons-collections4-4.1
	 */
	public static List<Course> readXLSXFile(File file) {
		List<Course> courses = new ArrayList<>();
		// Assume that file is corrupt, and fix it
		try {
			XLSXReader.fixCorruptXLSXFile(file);
		} catch (Exception e) {
			System.err.println("An error occurred when fixing the corrupted XLSX file.");
			e.printStackTrace();
		}

		try {
			// Read the file
			FileInputStream fixedFileInputStream = new FileInputStream(XLSXReader.FIXED_FILE_NAME);
			XSSFWorkbook workbook = new XSSFWorkbook(fixedFileInputStream);
			XSSFSheet sheet = workbook.getSheetAt(0);

			boolean firstRowFlag = true;
			// Loop through the rows
			for (Row row : sheet) {
				// Ignore the first row, where the titles are
				if (firstRowFlag) {
					firstRowFlag = false;
					continue;
				}
				CourseBuilder courseBuilder = new CourseBuilder();
				// Loop through the cells of the row
				Iterator<Cell> cellIterator = row.cellIterator();
				for (int i = 0; i < 7; i++) {
					Cell cell = cellIterator.next();
					String cellValue = cell.getStringCellValue();
					// Add the cell's value to the appropriate field in Course
					XLSXReader.courseDataFactory(courseBuilder, i, cellValue);
				}
				// Add the courses to an ArrayList
				courses.add(courseBuilder.toCourse());
			}

			XLSXReader.deleteFixedXLSXFile();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return courses;
	}

	/**
	 * 0 is term, 1 is course, 2 is format, 3 is meetings, 4 is location, 5 is instructor, and 6 is delivery
	 *
	 * @param courseBuilder The course builder of which to insert the data
	 * @param column        The column of data from which to determine what the data is
	 * @param cellValue     The value of the data
	 */
	private static void courseDataFactory(CourseBuilder courseBuilder, int column, String cellValue) {
		switch (column) {
			case 0 -> courseBuilder.setTerm(cellValue);
			case 1 -> courseBuilder.setCourse(cellValue);
			case 2 -> courseBuilder.setFormat(cellValue);
			case 3 -> courseBuilder.setMeetings(cellValue);
			case 4 -> courseBuilder.setLocation(cellValue);
			case 5 -> courseBuilder.setInstructor(cellValue);
			case 6 -> courseBuilder.setDelivery(cellValue);
			default -> System.err.println("An error has occurred when determining when determining the type of data");
		}
	}
}
