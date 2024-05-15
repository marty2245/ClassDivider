import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * .
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
     * .
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
     * .
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
     * .
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
     * .
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
     * .
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

    public List<Group<Student>> getGroupSet() {
        return groupSet;
    }
}
