package one.java.events;

import java.util.ArrayList;

/**
 *
 * @author Nico
 */
public class ONEChangeSupport
{

    private ArrayList<ONEChangeListener> changeListeners = new ArrayList<>();

    public void addChangeListener(ONEChangeListener l)
    {
        if (changeListeners.contains(l))
        {
            return;
        }

        changeListeners.add(l);
    }

    public void removeChangeListener(ONEChangeListener l)
    {
        changeListeners.remove(l);
    }

    /**
     * Returns true if the parameter given has changed
     * @param event
     * @return 
     */
    private boolean hasChanged(ONEChangeEvent event)
    {
        //Both are same, including both null
        if (event.getOldValue() == event.getNewValue())
        {
            return (false);
        }

        //Since both can't be null, if one of them are then the other isn't.  So changed.
        if (event.getOldValue() == null || event.getNewValue() == null)
        {
            return (true);
        }

        //Neither are NULL, so check for equals
        return (!event.getOldValue().equals(event.getNewValue()));

    }

    public void fireChangeEvent(ONEChangeEvent event)
    {
        if (!this.hasChanged(event))
        {
            return;
        }

        for (int i = 0; i < this.changeListeners.size(); i++)
        {
            ONEChangeListener l = this.changeListeners.get(i);
            l.onChange(event);
        }
    }

    public void clear()
    {
        this.changeListeners.clear();
    }
}
