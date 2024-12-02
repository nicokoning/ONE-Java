

package one.java.events;

import one.java.ONEObject;
import one.java.ONEParameter;

/**
 *
 * @author Nico
 */
public class ONEChangeEvent 
{  
    private ONEChangeEventType type = ONEChangeEventType.UNKNOWN;
    private ONEObject source = null; //the source object that was changed
    private String parameterName = null; //the parameter (if any) that was changed
    private Object oldValue = null;
    private Object newValue = null;
    
    public ONEChangeEvent(ONEObject source, ONEChangeEventType type, String parameterName, Object oldValue, Object newValue)
    {
        this.source = source;
        this.type = type;
        this.parameterName = parameterName;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }
    
    public void dispose()
    {
        newValue = null;
        oldValue = null;
        source = null;
    }

    /**
     * @return the type
     */
    public ONEChangeEventType getType()
    {
        return type;
    }

    /**
     * @return the source
     */
    public ONEObject getSource()
    {
        return source;
    }

    /**
     * @return the parameter
     */
    public String getParameterName()
    {
        return parameterName;
    }

    /**
     * @return the oldValue
     */
    public Object getOldValue()
    {
        return oldValue;
    }

    /**
     * @return the newValue
     */
    public Object getNewValue()
    {
        return newValue;
    }
    
    
}
