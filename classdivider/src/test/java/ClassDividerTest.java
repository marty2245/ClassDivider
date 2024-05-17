import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests methods in class ClassDivider.
 */
public class ClassDividerTest {

    private List<Student> students;
    private Group<Student> klas;
    private Map<String, Boolean> uniqueFirstName;
    private ClassDivider classDivider;

    /**
    * .
    */
    @BeforeEach
    public void setUp() {
        students = new ArrayList<>();
        // Assuming Student has a constructor that takes a first name and a last name
        students.add(new Student("Mitko", "Dimitrov", "1234567"));
        students.add(new Student("Sam", "Smith", "1234597"));
        students.add(new Student("Emilyana", "Ilieva", "1232567"));
        students.add(new Student("Krustio", "Ilieaw", "2309832"));

        klas = new Group<>();
        for (Student student : students) {
            klas.add(student);
        }

        uniqueFirstName = new HashMap<>();
        classDivider = new ClassDivider(2, 1, klas, uniqueFirstName);
    }

    @Test
    public void testGrouping() {
        classDivider.grouping();
        List<Group<Student>> groups = classDivider.getGroupSet();
        assertEquals(2, groups.size());

        Group<Student> group1 = groups.get(0);
        Group<Student> group2 = groups.get(1);

        assertEquals(2, group1.size());
        assertEquals(2, group2.size());
    }

    @Test
    public void testIfMethod() {
        // Adjust the setup to have overflow
        students.add(new Student("Mishel", "Rioder", "3578909"));
        klas.add(new Student("Martina", "Markova", "1942026"));
        classDivider = new ClassDivider(2, 1, klas, uniqueFirstName);
        classDivider.grouping();
        classDivider.ifMethod();

        List<Group<Student>> groups = classDivider.getGroupSet();
        assertEquals(2, groups.size()); // One group for overflow

        Group<Student> overflowGroup = groups.get(1);
        assertEquals(2, overflowGroup.size()); // Only one overflow student
    }

    @Test
    public void testElseMethod() {
        // Adjust the setup to have overflow and specific deviation handling
        students.add(new Student("Kosta", "Conev", "1678543"));
        students.add(new Student("Hil", "Smith", "9776547"));
        klas.add(new Student("Kosta", "Conev", "1678543"));
        klas.add(new Student("Hil", "Smith", "9776547"));
        classDivider = new ClassDivider(2, 1, klas, uniqueFirstName);
        classDivider.grouping();
        classDivider.elseMethod();

        List<Group<Student>> groups = classDivider.getGroupSet();
        assertEquals(4, groups.size()); // One group for overflow

        Group<Student> overflowGroup = groups.get(2);
        assertTrue(overflowGroup.size() <= 2); // Handle overflow within the deviation
    }

    @Test
    public void testDivide() {
        classDivider.divide();
        List<Group<Student>> groups = classDivider.getGroupSet();
        assertEquals(2, groups.size());

        Group<Student> group1 = groups.get(0);
        Group<Student> group2 = groups.get(1);

        assertEquals(2, group1.size());
        assertEquals(2, group2.size());

        assertTrue(uniqueFirstName.containsKey("Mitko"));
        assertTrue(uniqueFirstName.containsKey("Sam"));
        assertTrue(uniqueFirstName.containsKey("Emilyana"));
        assertTrue(uniqueFirstName.containsKey("Krustio"));
    }

    @Test
    public void testConditions() {
        // Conditions should return false with current setup
        assertFalse(classDivider.conditions());
    }
}
