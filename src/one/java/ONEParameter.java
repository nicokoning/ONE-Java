/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package one.java;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * The ONE parameter contains a key/value pair.  Each ONE object contains a list of these parameters.
 * @author Nico
 */
public class ONEParameter implements Serializable
{
    //The serial version for deserializing
    private static final long serialVersionUID = 1L;

    //The key for this parameter
    protected String key = "";
    //The value for this parameter
    protected String value = "";

    /**
     * Creates a new ONEParameter object with the given key and value
     */
    public ONEParameter(String key, String value)
    {
        this.key = key;
        this.value = value;
    } //end of constructor

    /**
     * Creates a new ONEParameter object from the given string ('KEY:VALUE')
     */
    public ONEParameter(String buffer)
    {
        String[] s = buffer.split(":");
        if (s.length > 0)
        {
            this.key = s[0];
        }
        if (s.length > 1)
        {
            this.value = s[1];
        }
    } //end of constructor

    @Override
    public String toString()
    {
        return (this.key + ":" + this.value);
    }

    /**
     * Returns true if the key of the given parameter equals the key of this
     * parameter
     *
     * @param o
     * @return
     */
    public boolean equals(Object o)
    {
        if (!(o instanceof ONEParameter))
        {
            return (false);
        }

        ONEParameter p = (ONEParameter) o;

        return (key.equals(p.key));
    }

    /**
     * @return the key for the parameter
     */
    public String getKey()
    {
        return key;
    }

    /**
     * @return the value for the parameter
     */
    public String getValue()
    {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value)
    {
        this.value = value;
    }

    /**
     * @param key the key to set
     */
    public void setKey(String key)
    {
        this.key = key;
    }

} //end of ONEParameter class
