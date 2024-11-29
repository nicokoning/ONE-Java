/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package one.java;

import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.ArrayList;
import one.java.ONEPropertyChangeSupport.ONEPropertyChangeEvent;

/**
 *
 * @author Nico
 */
public abstract class ONEObject implements Serializable
{
    //The serial version for deserializing
    private static final long serialVersionUID = 1L;

    //The ID of this object
    private long ID = 0;
    //The user given name of this object
    private String name = "";
    //A list of parameters for this object
    private ArrayList<ONEParameter> parameters = new ArrayList<>();
    //For property change notifications
    private transient ONEPropertyChangeSupport support;

    public ONEObject()
    {
        ID = System.nanoTime();
        this.initializeTransient();
    }

    /**
     * Called when we no longer need this object
     */
    public void dispose()
    {
        PropertyChangeListener[] allListeners = this.support.getPropertyChangeListeners();
        for (int i = 0; i < allListeners.length; i++)
        {
            PropertyChangeListener l = allListeners[i];
            this.support.removePropertyChangeListener(l);
        }

        this.support = null;
    }
    
    private void initializeTransient()
    {
        this.support = new ONEPropertyChangeSupport(this);
    }

    public void addPropertyChangeListener(ONEPropertyChangeListener pcl)
    {
        if(support != null)
        support.addPropertyChangeListener(pcl);
    }

    public void removePropertyChangeListener(ONEPropertyChangeListener pcl)
    {
        if(support != null)
            support.removePropertyChangeListener(pcl);
    }

    /**
     * Copies the header info from the object to this object
     *
     * @param object
     */
    public void copyHeader(ONEObject object)
    {
        this.setName(object.getName());
        this.parameters.clear();
        for (int i = 0; i < object.getParameters().size(); i++)
        {
            ONEParameter param = object.getParameters().get(i);
            this.setParameter(param.key, param.value);
        }
    }

    /**
     * Returns true if the ID of the given object matches our ID
     *
     * @param o
     * @return
     */
    public boolean equals(Object o)
    {
        if (!(o instanceof ONEObject))
        {
            return (false);
        }

        ONEObject s = (ONEObject) o;
        return (this.ID == s.ID);
    }

    @Override
    public String toString()
    {
        return (this.getName());
    }

    /**
     * Returns the name of the object
     *
     * @return
     */
    public String getName()
    {
        return (this.name);
    }

    /**
     * Removes obsolete parameters, adds new required ones etc
     */
    public void clean()
    {
    }

    /**
     * Called when we deserialize this object
     *
     * @return
     */
    protected Object readResolve()
    {
        this.initializeTransient();
        this.clean();        
        return (this);
    }    
   
    protected void firePropertyChange(ONEObject source, String name, Object oldValue, Object newValue)
    {
        if (oldValue == newValue) //takes care of both null case
        {
            return;
        }

        //No change
        if(oldValue != null && newValue != null && oldValue.equals(newValue))
            return;
        
        ONEPropertyChangeEvent event = new ONEPropertyChangeEvent(source, name, oldValue, newValue);
        if(this.support == null)
            return;
        
        this.support.firePropertyChange(event);
    }

    protected void firePropertyChange(String name, Object oldValue, Object newValue)
    {
        this.firePropertyChange(this, name, oldValue, newValue);
    }

    /**
     * Sets the name of our object
     */
    public void setName(String s)
    {
        String oldValue = this.name;
        this.name = s;
        this.firePropertyChange("name", oldValue, this.name);
    }

    /**
     * Returns our ID
     */
    public long getID()
    {
        return (this.ID);
    }

    /**
     * @param ID the ID to set
     */
    public void setID(long ID)
    {
        long oldID = this.ID;
        this.ID = ID;
        this.firePropertyChange("id", oldID, this.ID);

    }

    /**
     * Returns a list of parameters for this object
     */
    public ArrayList<ONEParameter> getParameters()
    {
        return (this.parameters);
    }

    /**
     * Adds the parameter if it does not exist, and gives it the default value.
     * If it already exists, it will not change it.
     */
    public ONEParameter addParameter(String key, String defaultValue)
    {
        ONEParameter searchParam = new ONEParameter(key, defaultValue);

        int index = this.parameters.indexOf(searchParam);

        //If it exists, return it
        if (index >= 0)
        {
            return (this.parameters.get(index));
        }

        getParameters().add(searchParam);

        this.firePropertyChange(key, null, defaultValue);

        return (searchParam);
    }

    /**
     * Sets the parameter given by the key to the given value. If the parameter
     * does not exist, it will be added.
     *
     * @param key
     * @param value
     */
    public void setParameter(String key, String value)
    {
        ONEParameter searchParam = new ONEParameter(key, value);
        int index = this.parameters.indexOf(searchParam);

        //If it exists, update it
        if (index >= 0)
        {
            ONEParameter param =  this.parameters.get(index);
            String oldValue = param.value;
            if(oldValue.equals(value))
                return;
            
            param.setValue(value);
            this.firePropertyChange(key, oldValue, value);
        }

        //Otherwise add it
        else
        {
            //Add the new one
            this.addParameter(key, value);
            return;
        }

    }

    /**
     * Removes the parameters with the given key
     */
    public void removeParameter(String key)
    {
        for (int i = 0; i < this.parameters.size(); i++)
        {
            ONEParameter param = this.parameters.get(i);
            if (param.key.equals(key))
            {
                String oldValue = param.value;
                this.parameters.remove(i--);
                this.firePropertyChange(key, oldValue, null);
            }
        }

    }

    /**
     * Returns the value of the parameter given by the key. If the key does not
     * exist null is returned.
     *
     * @param key
     * @return the value of the key, or NULL if the key was not found.
     */
    public String getParameter(String key)
    {
        for (int i = 0; i < this.getParameters().size(); i++)
        {
            ONEParameter param = this.getParameters().get(i);
            if (param.getKey().equals(key))
            {
                return (param.getValue());
            }
        }

        //We didn't find it
        return (null);
    }

    /**
     * Returns a boolean representation of the value for the given key
     *
     * @param key
     * @return the boolean representation of the value for the key, or false if
     * not found or the value is not the right type.
     */
    public Boolean getBooleanParameter(String key)
    {
        try
        {
            return (Boolean.parseBoolean(this.getParameter(key)));
        }
        catch (Exception e)
        {
        }

        return (false);
    }

    /**
     * Returns a integer representation of the value for the given key
     *
     * @param key
     * @return the integer representation of the value for the key, or 0 if not
     * found or the value is not the right type.
     */
    public Integer getIntParameter(String key)
    {
        try
        {
            return (Integer.parseInt(this.getParameter(key)));
        }
        catch (Exception e)
        {
        }

        return (0);
    }

    /**
     * Returns a long representation of the value for the given key
     *
     * @param key
     * @return the long representation of the value for the key, or 0 if not
     * found or the value is not the right type.
     */
    public Long getLongParameter(String key)
    {
        try
        {
            return (Long.parseLong(this.getParameter(key)));
        }
        catch (Exception e)
        {
        }

        return (0l);
    }

    /**
     * Returns a double representation of the value for the given key
     *
     * @param key
     * @return the double representation of the value for the key, or 0 if not
     * found or the value is not the right type.
     */
    public Double getDoubleParameter(String key)
    {
        try
        {
            return (Double.parseDouble(this.getParameter(key)));
        }
        catch (Exception e)
        {
        }

        return (0.0);
    }

} //end of ONEObject class
