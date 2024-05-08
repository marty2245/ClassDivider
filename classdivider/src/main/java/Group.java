import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * {@code Group} of things.
 *
 * A {@code Group} is a set.
 *
 * @author Huub de Beer
 * @param <T> group member type
 */
public class Group<T> implements Set<T> {

    private static Random RNG = new Random();

    /**
     * Set the seed for the random number generator used to randomly pick members
     * from this group.
     *
     * Note. Use for testing purposes.
     *
     * @param seed seed for random number generator
     */
    protected static void setRandomSeed(long seed) {
        Group.RNG = new Random(seed);
    }

    private final List<T> members;

    /*
     * Model: {@code {m|this.contains(m)}}
     *
     * Abstraction function: AF(this) = {m|members.contains(m)}
     */

    /**
     * Create a new empty group.
     *
     * @pre true
     */
    public Group() {
        members = new ArrayList<>();
    }

    /**
     * Group's size.
     *
     * @pre {@code true}
     * @post {@code \result == |this|}
     * @return this group's size
     */
    @Override
    public int size() {
        return members.size();
    }

    /**
     * Determine if this group is empty or not.
     *
     * @pre {@code true}
     * @post {@code \result == (size() == 0)}
     * @return true if this group is empty, false otherwise.
     */
    @Override
    public boolean isEmpty() {
        return members.isEmpty();
    }

    /**
     * Pick a member from this group at random.
     *
     * @pre {@code !this.isEmpty()}
     * @post {@code this.contains(\result)}
     * @return picked member
     * @throws IllegalStateException when {@code this.isEmpty()}
     */
    public T pick() {
        if (isEmpty()) {
            throw new IllegalStateException("Cannot pick an member from an empty group.");
        }
        return members.get(Group.RNG.nextInt(0, size()));
    }

    @Override
    public String toString() {
        return members.stream().map(Object::toString).collect(Collectors.joining("; "));
    }

    /**
     * Add a member to this group.
     *
     * @pre {@code true}
     * @param member member to add
     * @post {@code this == {member} union \old(this)}
     * @return true when member wasn't already in this group. False otherwise.
     */
    @Override
    public boolean add(T member) {
        if (!members.contains(member)) {
            members.add(member);
            return true;
        } else {
            return false;
        }
    }

    // Iterate over the members in this group in a random order.
    private class RandomIterator implements Iterator<T> {
        List<T> alreadyPicked;

        public RandomIterator() {
            alreadyPicked = new ArrayList<>();
        }

        @Override
        public boolean hasNext() {
            return members.size() != alreadyPicked.size();
        }

        @Override
        public T next() {
            List<T> candidates = members
                .stream()
                .filter(s -> !alreadyPicked.contains(s))
                .toList();

            T pick = candidates.get(RNG.nextInt(0, candidates.size()));
            alreadyPicked.add(pick);
            return pick;
        }
    }

    /**
     * Iterate over the members in this group in random order.
     *
     * @return random iterator
     */
    @Override
    public Iterator<T> iterator() {
        return new RandomIterator();
    }

    /**
     * Add all members.
     *
     * @pre {@code true}
     * @param members the members to add to this group
     * @post {@code this == members union \old(this)}
     * @return {@code true}
     */
    @Override
    public boolean addAll(Collection<? extends T> members) {
        for (T member : members) {
            add(member);
        }
        return true;
    }

    /**
     * Clear this group.
     *
     * @pre {@code true}
     * @post {@code isEmpty()}
     */
    @Override
    public void clear() {
        this.members.clear();
    }

    /**
     * Determine if member is in this group.
     *
     * @pre {@code true}
     * @param member the member to check membership for
     * @post {@code \result == member \in this}
     * @return true if member in this group, false otherwise
     */
    @Override
    public boolean contains(Object member) {
        return this.members.contains(member);
    }

    /**
     * Determine if all members are in this group.
     *
     * @pre {@code true}
     * @param members the members to check membership for
     * @post {@code (\forall m; members.contains(m); this.contains(m))}
     * @return true if all members are in this group, false otherwise
     */
    @Override
    public boolean containsAll(Collection<?> members) {
        return this.members.containsAll(members);
    }

    /**
     * Remove member from this group.
     *
     * @pre {@code true}
     * @param member the member to remove from this group
     * @post <ul>
     * <li>{@code this == \old(this) - {member}}</li>
     * <li>{@code \result == \old{this}.contains(member)}</li>
     * </ul>
     * @return true if removed member was in this group, false otherwise
     */
    @Override
    public boolean remove(Object member) {
        return this.members.remove(member);
    }

    /**
     * Remove members from this group.
     *
     * @pre {@code true}
     * @param members the members to remove from this group
     * @post <ul>
     * <li>{@code this == \old(this) - members}</li>
     * <li>{@code \result == (\old{this} != this)}</li>
     * </ul>
     * @return true if group changed, false otherwise
     */
    @Override
    public boolean removeAll(Collection<?> members) {
        return this.members.removeAll(members);
    }

    /**
     * Intersect this with members.
     *
     * @pre {@code true}
     * @param members the members to retain in this group
     * @post <ul>
     * <li>{@code this == \old(this) intersect members}</li>
     * <li>{@code \result == (\old(this) != this)}</li>
     * </ul>
     * @return true if group changed, false otherwise
     */
    @Override
    public boolean retainAll(Collection<?> members) {
        return this.members.retainAll(members);
    }

    /**
     * Convert this group to an array.
     *
     * @pre {@code true}
     * @post <ul>
     * <li>{@code (\forall i; \result.has(i); this.contains(\result[i]))}</li>
     * <li>{@code (\forall m; this.contains(m); (\exists i; \result.has(i); \result[i] == m))}</li>
     * <li>{@code (\forall m; this.contains(m); m instanceof Object)}</li>
     * </ul>
     * @return an array of all members in this group
     */
    @Override
    public Object[] toArray() {
        return this.members.toArray();
    }

    /**
     * Convert this group to an array.
     *
     * @pre {@code true}
     * @param <U> type of return array members
     * @param memberArray array to return values in
     * @post <ul>
     * <li>{@code (\forall i; \result.has(i); this.contains(\result[i]))}</li>
     * <li>{@code (\forall m; this.contains(m); (\exists i; \result.has(i); \result[i] == m))}</li>
     * </ul>
     * @return an array of all members in this group
     */
    @Override
    public <U> U[] toArray(U[] memberArray) {
        return this.members.toArray(memberArray);
    }

    /**
     * Determine if other is equal to this group.
     *
     * @pre {@code true}
     * @param other to compare with this group
     * @post {@code group.containsAll(this) && this.containsAll(group)}
     * @return true if other is a group and contains exactly the same members, false otherwise.
     */
    @Override
    public boolean equals(Object other) {
        if (other instanceof Group<?> group) {
            return group.size() == size() // For efficiency
                    && group.containsAll(this) && containsAll(group);
        }

        return false;
    }

}
