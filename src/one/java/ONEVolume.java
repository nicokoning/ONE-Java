package one.java;

import java.util.ArrayList;

/**
 *
 * @author Nico
 */
public class ONEVolume extends ONEObject
{
    //The serial version for deserializing
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new ONEVolume object
     */
    public ONEVolume()
    {
    } //end of constructor

    /**
     * Sets the emission parameter
     */ 
    public void setEmission(double value)
    {
        this.setParameter("EMISSION", "" + value);
    }

    /**
     * Sets the opacity parameter
     */ 
    public void setOpacity(double value)
    {
        this.setParameter("OPACITY", "" + value);
    }

    /**
     * Return the opacity parameter value
     * @return the opacity parameter value if it exists
     */
    public double getOpacity()
    {
        return (this.getDoubleParameter("OPACITY"));
    }

    /**
     * Return the emission parameter value
     * @return the emission parameter value if it exists
     */
    public double getEmission()
    {
        return (this.getDoubleParameter("EMISSION"));

    }

    /**
     * @return the volume order
     */
    public int getOrder()
    {
        return (this.getIntParameter("ORDER"));
    }

    /**
     * @param order the order to set
     */
    public void setOrder(int order)
    {
        this.setParameter("ORDER", "" + order);
    }

    /**
     * @return the replace parameter value
     */
    public boolean isReplace()
    {
        return (this.getBooleanParameter("REPLACE"));
    }

    /**
     * @param replace the replace to set
     */
    public void setReplace(boolean replace)
    {
        this.setParameter("REPLACE", "" + replace);
    }

    /**
     * @return the scale parameter value
     */
    public double getScale()
    {
        return (this.getDoubleParameter("SCALE"));
    }

    /**
     * @param scale the scale to set
     */
    public void setScale(double scale)
    {
        this.setParameter("SCALE", Double.toString(scale));
    }

    /**
     * Sets the offsets for this volume
     * @param x the x offset
     * @param y the y offset
     * @param z the z offset
     */
    public void setOffsets(double x, double y, double z)
    {
        this.setParameter("OFFSET_X", "" + x);
        this.setParameter("OFFSET_Y", "" + y);
        this.setParameter("OFFSET_Z", "" + z);
    }

    /**
     * Returns the offsets for this volume
     * @return an array containing the x,y,z (0,1,2) offsets.
     */
    public double[] getOffsets()
    {
        double[] offsets = new double[3];
        offsets[0] = this.getDoubleParameter("OFFSET_X");
        offsets[1] = this.getDoubleParameter("OFFSET_Y");
        offsets[2] = this.getDoubleParameter("OFFSET_Z");

        return (offsets);
    }

    /**
     * Sets the volume rotation in deg
     * @param x x rotation in deg
     * @param y y rotation in deg
     * @param z z rotation in deg
     */
    public void setRotations(double x, double y, double z)
    {
        this.setParameter("ROT_X", "" + x);
        this.setParameter("ROT_Y", "" + y);
        this.setParameter("ROT_Z", "" + z);
    }

    /**
     * Returns an array of rotation values for this volume (x,y,z -> 0,1,2)
     * @return 
     */
    public double[] getRotations()
    {
        double[] offsets = new double[3];
        offsets[0] = this.getDoubleParameter("ROT_X");
        offsets[1] = this.getDoubleParameter("ROT_Y");
        offsets[2] = this.getDoubleParameter("ROT_Z");

        return (offsets);
    }

    /**
     * Clears all of the texture IDs that are mapped to this volume
     */
    public void clearTextureIDs()
    {
        int num = 0;
        while (true)
        {
            String key = "TEXTURE_ID_" + num++;
            long id = this.getLongParameter(key);
            if (id <= 0)
            {
                break;
            }

            this.removeParameter(key);
        }
    }

    /**
     * Sets a list of texture IDs that are mapped to this volume
     * @param ids 
     */
    public void setTextureIDs(long... ids)
    {
        //First clear all the IDs
        this.clearTextureIDs();

        for (int i = 0; i < ids.length; i++)
        {
            String key = "TEXTURE_ID_" + i;
            this.setParameter(key, "" + ids[i]);
        }
    }

    /**
     * Adds the given texture IDs to this volume mapping
     * @param ids 
     */
    public void addTextureIDs(long... ids)
    {
        //Get the starting texture ID        
        int num = 0;
        while (true)
        {
            String key = "TEXTURE_ID_" + num;
            long id = this.getLongParameter(key);
            if (id <= 0)
            {
                break;
            }

            num++; //move to the next
        }

        for (int i = 0; i < ids.length; i++)
        {
            String key = "TEXTURE_ID_" + (num + i);
            this.setParameter(key, "" + ids[i]);
        }
    }

    /**
     * Returns a list of textures that are mapped by this volume
     */ 
    public long[] getTextureIDs()
    {
        int num = 0;

        ArrayList<Long> textureIDs = new ArrayList<>();
        while (true)
        {
            long id = this.getLongParameter("TEXTURE_ID_" + num++);
            if (id <= 0)
            {
                break;
            }

            textureIDs.add(id);
        }

        long[] rArray = new long[textureIDs.size()];
        for (int i = 0; i < textureIDs.size(); i++)
        {
            rArray[i] = textureIDs.get(i);
        }

        return (rArray);
    }

} //end of ONEVolume class