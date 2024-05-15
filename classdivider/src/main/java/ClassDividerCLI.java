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
 * @version 0.7
 */
@Command(
        name = "classdivider",
        mixinStandardHelpOptions = true,
        version = "classdivider 0.6",
        description = "Divide a class of students into groups.")
public class ClassDividerCLI implements Callable<Integer> {

    @CommandLine.Option(
            names = {"-g", "--group-size"},
            description = "target group size.",
            required = true
    )
    private int groupSize;

    @CommandLine.Option(
            names = {"-d", "--deviation"},
            description = "permitted difference of number of students in a group "
            + " and the target group size. Defaults to ${DEFAULT-VALUE}.")
    public int deviation = 1;

    @Parameters(
            index = "0",
            description = "path to file with students data in CSV format."
    )
    private Path studentsFile;

    @Spec
    CommandSpec commandSpec; // injected by picocli

    private Group<Student> klas;
    private final Map<String, Boolean> uniqueFirstName = new HashMap<>();

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
