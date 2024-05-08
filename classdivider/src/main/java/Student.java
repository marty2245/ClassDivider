/**
 * Student.
 *
 * @see
 * <a href="https://shinesolutions.com/2018/01/08/falsehoods-programmers-believe-about-names-with-examples/">This
 * page about potential issues with names</a> that we ignore in this Student record. We modeled
 * student names from a Dutch perspective.
 *
 * @author Huub de Beer
 *
 * @param firstName student's first name
 * @param lastName student's last name
 * @param id student's ID
 */
public record Student(String firstName, String lastName, String id) {

    /**
     * Return the sort name, Dutch style.
     *
     * In the Netherlands, we sort a names starting with the last name, then the
     * first name, and then optional "tussenvoegsels" like "de" or "van der".
     *
     * Examples:
     *
     * <ul>
     * <li> Beer, Huub de
     * <li> Jansens, Jan
     * <li> Borne, Lisa van der
     * </ul>
     *
     * @pre true
     * @return This student's name reformatted for sorting
     */
    public String sortName() {
        int i = 0;

        while (i < lastName.length() && !Character.isUpperCase(lastName.charAt(i))) {
            i++;
        }

        // Note. When no capital letter in lastName, prefix is whole lastName and last
        // is empty.
        String prefix = lastName.substring(0, i).trim();
        String last = lastName.substring(i);

        return last + ", " + firstName + (prefix.isBlank() ? "" : " " + prefix);
    }

    /**
     * Two students are equal when they have the same ID.
     *
     * @pre true
     * @param other object to compare this student with
     * @post {@code \result == (other instanceof Student && other.id() == this.id())}
     * @return true if other's ID is equal to this student's ID, false otherwise
     */
    @Override
    public boolean equals(Object other) {
        if (other instanceof Student student) {
            return this.id().equals(student.id());
        }
        return false;
    }

}
