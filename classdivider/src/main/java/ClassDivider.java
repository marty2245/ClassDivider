import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * ClassDivider – Divides a group of students into smaller groups based on specified parameters.
 * 
 * This class is responsible for dividing a list of students into groups of a specified size
 * while considering a permissible deviation in the group sizes.
 * @pre recieves correct input, thus validate() must handle exceptions with incorrect input
 * @post returns correct groups to print(), 
 *      thus must handle exceptions when the division is impossible
 */
public class ClassDivider {
    private int groupSize;
    private int deviation;
    private Group<Student> klas;
    private int nrOfGroups;
    private int overflow;
    private Iterator<Student> students;
    private List<Group<Student>> groupSet = new ArrayList<>();
    private Map<String, Boolean> uniqueFirstName;

    /**
     * Constructs a new ClassDivider with the specified group size, deviation, 
     * list of students, and a map for tracking unique first names.
     *
     * @param groupSize the target size of each group
     * @param deviation the permissible deviation in group sizes
     * @param klas the list of students to be divided into groups
     * @param uniqueFirstName a map to track the uniqueness of first names
     */
    public ClassDivider(int groupSize, int deviation,
            Group<Student> klas, Map<String, Boolean> uniqueFirstName) {
        this.groupSize = groupSize;
        this.deviation = deviation;
        this.klas = klas;
        this.nrOfGroups = klas.size() / groupSize;
        this.overflow = klas.size() % groupSize;
        this.students = klas.iterator();
        this.uniqueFirstName = uniqueFirstName;
    }

    private boolean conditions() {
        boolean overflowCheck = nrOfGroups / deviation > overflow;
        boolean deviationOverflowCheck = groupSize - deviation <= overflow
                && overflow <= groupSize + deviation;
        boolean lastCheck = groupSize - deviation <= overflow + nrOfGroups * deviation && overflow
                + nrOfGroups * deviation <= groupSize + deviation;

        return !(overflowCheck || deviationOverflowCheck || lastCheck);
    }
    
    /**
     * Divides the class of students into groups based on the specified group size and deviation.
     * 
     * This method first checks if the division conditions are met and then proceeds to divide
     * the students into groups. It also updates the map tracking the uniqueness of first names.
     * 
     * @throws IllegalArgumentException if the conditions for dividing the class are not met
     */
    public void divide() {
        if (conditions()) {
            throw new IllegalArgumentException(
                    "Unable to divide the class into groups with the specified parameters.");
        }
        grouping();

        if (nrOfGroups / deviation > overflow) {
            ifMethod();
        } else {
            elseMethod();
        }

        for (Student student : klas) {
            if (uniqueFirstName.containsKey(student.firstName())) {
                uniqueFirstName.put(student.firstName(), false);
            } else {
                uniqueFirstName.put(student.firstName(), true);
            }
        }
    }
    
    /**
    * Groups students into the initial sets based on the specified group size.
    */
    public void grouping() {
        for (int g = 0; g < nrOfGroups; g++) {
            Group<Student> group = new Group<>();

            for (int size = 0; size < groupSize; size++) {
                group.add(students.next());
            }

            groupSet.add(group);
        }
    }

    /**
     * Distributes the overflow students 
     * among the initial groups if the deviation conditions are met.
     */
    public void ifMethod() {
        for (int d = 0; d < deviation && overflow > 0; d++) {
            for (int g = 0; g < nrOfGroups && overflow > 0; g++) {
                Group<Student> group = groupSet.get(g);
                group.add(students.next());
                overflow--;
            }
        }
    }

    /**
     * Creates a separate group for the overflow students if the deviation conditions are not met.
     */
    public void elseMethod() {
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

    /**
     * Returns the list of student groups after the division.
     * 
     * @return a list of groups of students
     */
    public List<Group<Student>> getGroupSet() {
        return groupSet;
    }
}
