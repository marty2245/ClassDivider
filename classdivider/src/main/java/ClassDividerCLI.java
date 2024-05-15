import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

/**
 * ClassDividerCLI – Divide a class of students into groups.
 * 
 * This CLI tool takes a CSV file with student data and divides the students
 * into groups of a specified size, allowing for a specified deviation.
 *
 * @version 0.7
 */
@Command(
        name = "classdivider",
        mixinStandardHelpOptions = true,
        version = "classdivider 0.6",
        description = "Divide a class of students into groups.")
public class ClassDividerCLI implements Callable<Integer> {

    /**
     * The target group size.
     * This option is required.
     */
    @CommandLine.Option(
            names = {"-g", "--group-size"},
            description = "target group size.",
            required = true
    )
    private int groupSize;

    /**
     * The permitted difference between the number of students in a group and the target group size.
     * Defaults to 1.
     */
    @CommandLine.Option(
            names = {"-d", "--deviation"},
            description = "permitted difference of number of students in a group "
            + " and the target group size. Defaults to ${DEFAULT-VALUE}.")
    public int deviation = 1;

    /**
     * The path to the file with students data in CSV format.
     * This parameter is required.
     */
    @Parameters(
            index = "0",
            description = "path to file with students data in CSV format."
    )
    private Path studentsFile;

    @Spec
    CommandSpec commandSpec; // injected by picocli

    private Group<Student> klas;
    private final Map<String, Boolean> uniqueFirstName = new HashMap<>();

    /**
     * Checks for valid group size and deviation.
     * Throws a ParameterException if the conditions are not met.
     */
    private void exceptionCheck() {
        if (groupSize <= 0) {
            throw new ParameterException(commandSpec.commandLine(),
                    "group size must be a positive integer number.");
        }

        if (deviation >= groupSize || deviation < 0) {
            throw new ParameterException(commandSpec.commandLine(),
                    "deviation must be a positive number smaller than group size.");
        }
    }
    
    /**
     * Validates the input CSV file and initializes the student group.
     * Throws a ParameterException if the file cannot be read or parsed.
     */
    private void validate() {
        try {
            klas = StudentsFile.fromCSV(studentsFile);
        } catch (IOException e) {
            throw new ParameterException(commandSpec.commandLine(),
                    "Unable to open or read students file '%s': %s."
                            .formatted(studentsFile, e));
        }
        exceptionCheck();
    }
    
    /**
     * Prints the groups of students.
     * @param groupSet List of student groups to print
     */
    private void print(List<Group<Student>> groupSet) {
        int groupNr = 0;

        for (Group<Student> group : groupSet) {
            groupNr++;

            System.out.printf("Group %d:%n", groupNr);

            for (Student student : group) {
                String name = student.firstName();

                if (!uniqueFirstName.get(student.firstName())) {
                    name += " " + student.sortName().charAt(0);
                }

                System.out.println("- " + name);
            }

            System.out.println();
        }
    }

    /**
    * The main execution method of the CLI.
    * Validates inputs, divides the class into groups, and prints the groups.
    *
    * @return Exit code, 0 if successful.
    */
    @Override
    public Integer call() {
        validate();

        ClassDivider divider = new ClassDivider(groupSize, deviation, klas, uniqueFirstName);
        divider.divide();

        List<Group<Student>> groupSet = divider.getGroupSet();
        print(groupSet);

        return 0;
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new ClassDividerCLI()).execute(args);
        System.exit(exitCode);
    }
}
