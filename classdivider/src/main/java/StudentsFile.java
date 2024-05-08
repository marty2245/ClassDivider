import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

/**
 * Read and write CSV files with student information.
 *
 * @author Huub de Beer
 */
public class StudentsFile {

    private static final String FIRST_NAME = "first name";
    private static final String LAST_NAME = "last name";
    private static final String ID = "ID";

    private static final CSVFormat CSV_FORMAT = CSVFormat.Builder
            .create(CSVFormat.DEFAULT)
            .setHeader(FIRST_NAME, LAST_NAME, ID)
            .setSkipHeaderRecord(true)
            .build();

    /**
     * Create a group of students from a CSV file.
     *
     * @pre true
     * @param file path to file with CSV data
     * @return group of students
     * @throws IOException when an I/O error occurs
     */
    public static Group<Student> fromCSV(Path file) throws IOException {
        return fromCSV(Files.readString(file));
    }

    /**
     * Create a group of students from CSV data.
     *
     * @pre true
     * @param csv student data in CSV format
     * @return group of students
     * @throws IOException when an I/O error occurs
     */
    public static Group<Student> fromCSV(String csv) throws IOException {
        CSVParser records = CSVParser.parse(csv, CSV_FORMAT);
        Group<Student> students = new Group<>();

        // TODO: actual validation of the records

        for (CSVRecord record : records) {
            students.add(new Student(
                    record.get(FIRST_NAME),
                    record.get(LAST_NAME),
                    record.get(ID)
            ));
        }

        return students;
    }

    /**
     * Convert a group of students as CSV string.
     *
     * @pre true
     * @param students group of students
     * @return CSV string
     * @throws java.io.IOException when an I/O error occurs
     */
    public static String toCSV(Group<Student> students) throws IOException {
        StringBuilder csv = new StringBuilder();

        try (CSVPrinter printer = new CSVPrinter(csv, CSV_FORMAT)) {
            for (Student student : students) {
                printer.printRecord(student.firstName(), student.lastName(), student.id());
            }
        }

        return csv.toString();
    }

    /**
     * Write a group of students to a CSV file.
     *
     * @pre true
     * @param file path to CSV file
     * @param students group of students
     * @throws java.io.IOException when an I/O error occurs
     */
    public static void toCSV(Path file, Group<Student> students) throws IOException {
        Files.writeString(file, toCSV(students));
    }

}
