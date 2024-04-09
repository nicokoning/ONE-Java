/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package one.java;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.ArrayList;

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
    private PropertyChangeSupport support;

    public ONEObject()
    {
        ID = System.nanoTime();
        support = new PropertyChangeSupport(this);
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl)
    {
        support.addPropertyChangeListener(pcl);
    }

    public void removePropertyChangeListener(PropertyChangeListener pcl)
    {
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
     * Sets the name of our object
     */
    public void setName(String s)
    {
        String oldValue = this.name;
        this.name = s;
        this.support.firePropertyChange("name", oldValue, this.name);
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
        this.support.firePropertyChange("id", oldID, this.ID);
        
    }

    /**
     * Returns a list of parameters for this object
     */
    public ArrayList<ONEParameter> getParameters()
    {
        return (this.parameters);
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
            this.parameters.get(index).setValue(value);
        }

        //Otherwise add it
        else
        {
            //Add the new one
            getParameters().add(searchParam);
        }

        this.support.firePropertyChange(key, null, value);
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
                this.parameters.remove(i--);
            }
        }

        this.support.firePropertyChange(key, null, null);
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
