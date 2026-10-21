package one.java;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.StringJoiner;

/**
 *
 * @author Nico
 */
public class ONEGroup extends ONEObject
{

    //The serial version for deserializing
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new ONEGroup object
     */
    public ONEGroup()
    {
    } //end of constructor

    public void dispose()
    {
        this.clearChildIDs();
        super.dispose();
    }

    /**
     * Removes obsolete parameters, adds new required ones etc
     */
    public void clean()
    {
        super.clean();
    }

    /**
     * Copies the given volume to this one
     */
    public void copy(ONEGroup group)
    {
        this.copyHeader(group);
        this.setID(group.getID());
    }
    
    public void addChild(ONEObject object)
    {
        this.addChildIDs(object.getID());
    }

    public long[] getChildIDs()
    {
        String value = this.getParameter("CHILD_IDS");
        if (value == null || value.isBlank())
        {
            return new long[0];
        }

        LinkedHashSet<Long> ids = new LinkedHashSet<>();

        for (String token : value.split(",", -1))
        {
            long id;

            try
            {
                id = Long.parseLong(token.trim());
            }
            catch (NumberFormatException e)
            {
                throw new IllegalArgumentException("Invalid CHILD_IDS entry: '" + token + "'", e);
            }

            this.validateChildID(id);
            ids.add(id);
        }

        return ids.stream().mapToLong(Long::longValue).toArray();
    }

    public void setChildIDs(long... ids)
    {
        Objects.requireNonNull(ids, "Child IDs cannot be null.");

        LinkedHashSet<Long> uniqueIDs = new LinkedHashSet<>();
        for (long id : ids)
        {
            this.validateChildID(id);
            uniqueIDs.add(id);
        }

        StringJoiner value = new StringJoiner(",");
        for (long id : uniqueIDs)
        {
            value.add(Long.toString(id));
        }

        this.setParameter("CHILD_IDS", value.toString());
    }

    public void addChildIDs(long... ids)
    {
        Objects.requireNonNull(ids, "Child IDs cannot be null.");

        LinkedHashSet<Long> children = new LinkedHashSet<>();
        for (long id : this.getChildIDs())
        {
            children.add(id);
        }

        for (long id : ids)
        {
            this.validateChildID(id);
            children.add(id);
        }

        this.setChildIDs(children.stream().mapToLong(Long::longValue).toArray());
    }

    public void removeChildIDs(long... ids)
    {
        Objects.requireNonNull(ids, "Child IDs cannot be null.");

        LinkedHashSet<Long> children = new LinkedHashSet<>();
        for (long id : this.getChildIDs())
        {
            children.add(id);
        }

        for (long id : ids)
        {
            this.validateChildID(id);
            children.remove(id);
        }

        this.setChildIDs(children.stream().mapToLong(Long::longValue).toArray());
    }

    public void clearChildIDs()
    {
        this.setChildIDs();
    }

    private void validateChildID(long id)
    {
        if (id <= 0)
        {
            throw new IllegalArgumentException("Child ID must be positive: " + id);
        }

        if (id == this.getID())
        {
            throw new IllegalArgumentException("A group cannot contain itself: " + id);
        }
    }

} //end of ONEVolume class
