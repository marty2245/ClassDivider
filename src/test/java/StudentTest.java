import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Test Student.
 *
 * @author Huub de Beer
 */
public class StudentTest {

    /**
     * Test of sortName method, of class Student.
     */
    @Test
    public void testSortName() {
        // Dutch style sort names
        // Last name, First name tussenvoegsels
        assertEquals("Beer, Huub de", new Student("Huub", "de Beer", "").sortName());
        assertEquals("Borne, Elsa van der", new Student("Elsa", "van der Borne", "").sortName());
        assertEquals("Jansens, Jan", new Student("Jan", "Jansens", "").sortName());

        // Belgian style sort names only work if tussenvoegsel starts with capital
        assertEquals("Van der Borne, Else", new Student("Else", "Van der Borne", "").sortName());

        // Some other names from around the world, as Dutch style sort names
        assertEquals("Mahamat, Omar", new Student("Omar", "Mahamat", "").sortName());
        assertEquals("Lee, Zhang", new Student("Zhang", "Lee", "").sortName());
        assertEquals("Fernández, Alfredo", new Student("Alfredo", "Fernández", "").sortName());
        assertEquals("Santos, Julia dos", new Student("Julia", "dos Santos", "").sortName());
    }

    /**
     * Test of equals method, of class Student.
     */
    @Test
    public void testEquals() {
        Student s1 = new Student("First name", "Last name", "ID");
        Student s2 = new Student("First name", "Last name", "Other ID");
        Student s3 = new Student("Other first name", "Other last name", "ID");

        assertAll(
                // Every student is equal to themselves
                () -> assertEquals(s1, s1),
                () -> assertEquals(s2, s2),
                () -> assertEquals(s3, s3),

                // A student is equal to another student if they have the same ID,
                // regardless if the other fields match or not
                () -> assertEquals(s1, s3),

                // A student is not equal to another student if their IDs are different,
                // regardless if they have the same name or not
                () -> assertNotEquals(s1, s2),
                () -> assertNotEquals(s3, s2)
        );
    }

}
