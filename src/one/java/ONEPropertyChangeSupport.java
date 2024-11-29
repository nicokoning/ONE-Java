package one.java;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 *
 * @author Nico
 */
public class ONEPropertyChangeSupport extends PropertyChangeSupport
{
    public ONEPropertyChangeSupport(ONEObject source)
    {
        super(source);
    }

    @Override
    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener)
    {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener)
    {
        throw new UnsupportedOperationException("Not supported");
    }
    
    

    /**
     * Fires a property change event to listeners that have been registered to
     * track updates of all properties or a property with the specified name.
     * <p>
     * No event is fired if the given event's old and new values are equal and
     * non-null.
     *
     * @param event the {@code PropertyChangeEvent} to be fired
     * @since 1.2
     */
    public void firePropertyChange(ONEPropertyChangeEvent event)
    {
        Object oldValue = event.getOldValue();
        Object newValue = event.getNewValue();
        if (oldValue == null || newValue == null || !oldValue.equals(newValue))
        {
            PropertyChangeListener[] common = this.getPropertyChangeListeners();
            fire(common, event);
        }
    }

    private static void fire(PropertyChangeListener[] listeners, ONEPropertyChangeEvent event)
    {
        if (listeners != null)
        {
            for (PropertyChangeListener listener : listeners)
            {
                listener.propertyChange((ONEPropertyChangeEvent)event);
            }
        }
    }

    @Override
    public void firePropertyChange(PropertyChangeEvent event)
    {
        //If this is already a ONE event fire it
        if (event instanceof ONEPropertyChangeEvent)
        {
            this.firePropertyChange((ONEPropertyChangeEvent)event);
            return;
        }

        //Otherwise we have to convert
        if (event.getSource() == null || !(event.getSource() instanceof ONEObject))
        {
            return;
        }

        ONEPropertyChangeEvent oneEvent = new ONEPropertyChangeEvent((ONEObject) event.getSource(), event.getPropertyName(), event.getOldValue(), event.getNewValue());
        this.firePropertyChange((ONEPropertyChangeEvent)oneEvent);
    }

    public static class ONEPropertyChangeEvent extends PropertyChangeEvent
    {

        public ONEPropertyChangeEvent(ONEObject source, String propertyName, Object oldValue, Object newValue)
        {
            super(source, propertyName, oldValue, newValue);
        }

        /**
         * The object on which the Event initially occurred.
         *
         * @return the object on which the Event initially occurred
         */
        @Override
        public ONEObject getSource()
        {
            return (ONEObject) super.getSource();
        }

        /**
         * Does this event represent an event where something was added?
         *
         * @return
         */
        public boolean isAdded()
        {
            return (this.getOldValue() == null && this.getNewValue() != null);
        }

        /**
         * Does this event represent an event where something was removed?
         *
         * @return
         */
        public boolean isRemoved()
        {
            return (this.getOldValue() != null && this.getNewValue() == null);
        }

    }
}
