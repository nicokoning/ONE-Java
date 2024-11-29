

package one.java;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import one.java.ONEPropertyChangeSupport.ONEPropertyChangeEvent;

/**
 *
 * @author Nico
 */
public interface ONEPropertyChangeListener extends PropertyChangeListener
{
    @Override
    public default void propertyChange(PropertyChangeEvent evt)
    {
        if(evt instanceof ONEPropertyChangeEvent)
            this.propertyChange((ONEPropertyChangeEvent)evt);
    }
    
    public void propertyChange(ONEPropertyChangeEvent evt);
}
