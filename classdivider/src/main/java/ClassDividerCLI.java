import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
 * ClassDivider–Divide a class of students into groups.
 *
 * @version 0.6
 * @author Huub de Beer
 */
@Command(
        name = "classdivider",
        mixinStandardHelpOptions = true,
        version = "classdivider 0.6",
        description = "Divide a class of students into groups.")
public class ClassDividerCLI implements Callable<Integer> {

    /*
     * Size of the groups to create.
     */
    @CommandLine.Option(
            names = {"-g", "--group-size"},
            description = "target group size.",
            required = true
    )
    private int groupSize;

    /*
     * Number of students that a group can deviate from the target group size.
     *
     * For example, if the group size is 10 and the deviation is 2, all groups in the group set will
     * have sizes between 8 and 12. Defaults to 1.
     */
    @CommandLine.Option(
            names = {"-d", "--deviation"},
            description = "permitted difference of number of students in a group "
            + " and the target group size. Defaults to ${DEFAULT-VALUE}.")
    private int deviation = 1;

    /*
     * Path to file containing student data
     */
    @Parameters(
            index = "0",
            description = "path to file with students data in CSV format."
    )
    private Path studentsFile;

    @Spec
    CommandSpec commandSpec; // injected by picocli

    @Override
    public Integer call() {
        // Note. "klas" is Dutch for "class". We cannot use "class" because it is a Java keyword.
        Group<Student> klas;

        // Validate user input
        try {
            klas = StudentsFile.fromCSV(studentsFile);
        } catch (IOException e) {
            throw new ParameterException(
                    commandSpec.commandLine(),
                    "Unable to open or read students file '%s': %s."
                            .formatted(studentsFile, e));
        }

        if (groupSize
                <= 0) {
            throw new ParameterException(
                    commandSpec.commandLine(),
                    "group size must be a positive integer number.");
        }

        if (deviation >= groupSize || deviation
                < 0) {
            throw new ParameterException(
                    commandSpec.commandLine(),
                    "deviation must be a positive number smaller than group size.");
        }

        int nrOfGroups = klas.size() / groupSize;
        int overflow = klas.size() % groupSize;

        if (!((nrOfGroups / deviation > overflow) || (groupSize - deviation <= overflow && overflow <= groupSize + deviation) || (groupSize - deviation <= overflow + nrOfGroups * deviation && overflow + nrOfGroups * deviation <= groupSize + deviation))) {
            throw new ParameterException(
                    commandSpec.commandLine(),
                    "Unable to divide a class of %d into groups of %d+/-%d students.".formatted(klas.size(), groupSize, deviation)
            );
        }

        // Divide class into groups
        Iterator<Student> students = klas.iterator();
        List<Group<Student>> groupSet = new ArrayList<>();

        for (int g = 0; g < nrOfGroups; g++) {
            Group<Student> group = new Group<>();

            for (int size = 0; size < groupSize; size++) {
                group.add(students.next());
            }

            groupSet.add(group);
        }

        if (nrOfGroups / deviation > overflow) {
            for (int d = 0; d < deviation && overflow > 0; d++) {
                for (int g = 0; g < nrOfGroups && overflow > 0; g++) {
                    Group<Student> group = groupSet.get(g);
                    group.add(students.next());
                    overflow--;
                }
            }
        } else {
            Group<Student> separateGroup = new Group<>();

            for (int i = 0; i < overflow; i++) {
                separateGroup.add(students.next());
            }

            for (int d = 0; d < deviation && separateGroup.size() < groupSize - deviation; d++) {
                int g = groupSet.size();

                while (separateGroup.size() < groupSize - deviation) {
                    g--;
                    Group<Student> group = groupSet.get(g);
                    Student student = group.pick();
                    separateGroup.add(student);
                    group.remove(student);
                }
            }

            groupSet.add(separateGroup);
        }

        Map<String, Boolean> uniqueFirstName = new HashMap<>();

        for (Student student : klas) {
            if (uniqueFirstName.containsKey(student.firstName())) {
                uniqueFirstName.put(student.firstName(), false);
            } else {
                uniqueFirstName.put(student.firstName(), true);
            }
        }

        // Print group set to standard output
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

        return 0;
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new ClassDividerCLI()).execute(args);
        System.exit(exitCode);
    }

}
