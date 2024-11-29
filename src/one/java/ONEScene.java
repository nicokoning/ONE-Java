package one.java;

import java.util.ArrayList;
import one.java.ONEPropertyChangeSupport.ONEPropertyChangeEvent;

/**
 *
 * @author Nico
 */
public abstract class ONEScene<V extends ONEVolume, T extends ONETexture> extends ONEObject
{

    //The serial version for deserializing
    private static final long serialVersionUID = 1L;

    //The version of our ONE format
    public static final int ONE_VERSION = 1;
    //This identifies files as a ONE format file
    public static final int ONE_ID = 102380;

    //The list of textures
    protected ArrayList<T> textures = new ArrayList<>();
    //The list of volumes
    protected ArrayList<V> volumes = new ArrayList<>();

    //listens for property changes from its children
    private transient ONEPropertyChangeListener propertyChangedListener;

    /**
     * Creates a new ONEScene object
     */
    public ONEScene()
    {
        this.initialize();
    } //end of constructor

    /**
     * Initializes the object
     */
    private void initialize()
    {
        //Add some default parameters
        this.addParameter("AUTO_EXPOSURE", "true");
        this.addParameter("EXPOSURE", "1");
        this.addParameter("EMISSION", "1");
        this.addParameter("OPACITY", "1");
        this.addParameter("SCALE_POWER", "1");
        this.addParameter("ROT_X", "0");
        this.addParameter("ROT_Y", "0");
        this.addParameter("ROT_Z", "0");

        this.initializeTransient();

    } //end of initialize function

    /**
     * Initializes the object
     */
    private void initializeTransient()
    {
        this.propertyChangedListener = (ONEPropertyChangeEvent evt) ->
        {
            this.firePropertyChange(evt.getSource(), evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
        };

    } //end of initialize function

    @Override
    public void dispose()
    {
        super.dispose();
        this.clear();
    }

    @Override
    protected Object readResolve()
    {
        this.initializeTransient();
        return (this);
    }

    /**
     * Clears the scene of all volumes and textures
     */
    public void clear()
    {
        while (!this.textures.isEmpty())
        {
            this.removeTexture(this.textures.get(0));
        }

        while (!this.volumes.isEmpty())
        {
            this.removeVolume(this.volumes.get(0));
        }

    }

    /**
     * Clears all of the voxel data, may not want to store it all if we are just
     * interested in the header
     */
    public void clearData()
    {
        for (int i = 0; i < this.textures.size(); i++)
        {
            this.textures.get(i).clearData();
        }
    }

    /**
     * Creates a new volume of the correct type for this scene
     *
     * @return
     */
    public abstract V newVolume();

    /**
     * Creates a new volume of the correct type for this scene
     *
     * @return
     */
    public abstract T newTexture();

    public void addVolume(V v)
    {
        //If it already exists (same ID) replace it
        int index = volumes.indexOf(v);

        if (index >= 0)
        {
            ONEVolume currentVolume = volumes.get(index);
            currentVolume.copyHeader(v);
        }
        else
        {
            this.volumes.add(v);
        }

        //Listen for property changes
        v.addPropertyChangeListener(propertyChangedListener);
        this.firePropertyChange(this, "ADD", null, v);
    }

    public void addTexture(T t)
    {
        //If it already exists (same ID) replace it
        int index = textures.indexOf(t);

        if (index >= 0)
        {
            T currentTexture = textures.get(index);
            currentTexture.copyHeader(t);
        }
        else
        {
            this.textures.add(t);
        }

        //Listen for property changes
        t.addPropertyChangeListener(propertyChangedListener);
        this.firePropertyChange(this, "ADD", null, t);
    }

    public void removeVolume(V volume)
    {
        this.volumes.remove(volume);

        volume.dispose();
        this.firePropertyChange(this, "REMOVE", volume, null);
    }

    public void removeTexture(T texture)
    {
        //Now remove the texture
        this.textures.remove(texture);

        //Get the volume that holds this texture
        ArrayList<V> volumes = this.getVolumes(texture);
        for (int i = 0; i < volumes.size(); i++)
        {
            ONEVolume volume = volumes.get(i);
            volume.removeTextureIDs(texture.getID());
        }

        texture.dispose();
        this.firePropertyChange(this, "REMOVE", texture, null);
    }

    /**
     * Returns the texture with the given ID
     */
    public T getTexture(long ID)
    {
        for (int i = 0; i < this.textures.size(); i++)
        {
            T texture = this.textures.get(i);
            if (texture.getID() == ID)
            {
                return (texture);
            }
        }

        return (null);
    }

    /**
     * Returns the volume with the given ID
     *
     * @param ID
     * @return
     */
    public V getVolume(long ID)
    {
        for (int i = 0; i < this.volumes.size(); i++)
        {
            V volume = this.volumes.get(i);
            if (volume.getID() == ID)
            {
                return (volume);
            }
        }

        return (null);
    }

    /**
     * Returns a list of all volumes that are mapped to the given texture
     */
    public ArrayList<V> getVolumes(T t)
    {
        ArrayList<V> mappedVolumes = new ArrayList<>();
        for (int i = 0; i < volumes.size(); i++)
        {
            V volume = volumes.get(i);
            long[] tIds = volume.getTextureIDs();
            for (int j = 0; j < tIds.length; j++)
            {
                if (tIds[j] == t.getID())
                {
                    mappedVolumes.add(volume);
                    break;
                }
            }
        }

        return (mappedVolumes);
    }

    /**
     * Returns a list of all textures that are mapped to the given volume
     */
    public ArrayList<T> getTextures(V v)
    {
        ArrayList<T> mappedTextures = new ArrayList<>();

        long[] tIds = v.getTextureIDs();
        for (int i = 0; i < tIds.length; i++)
        {
            T tex = this.getTexture(tIds[i]);
            if (tex != null)
            {
                mappedTextures.add(tex);
            }
        }

        return (mappedTextures);
    }

    //--------------------------------------------------------------------------
    // Getter and Setter methods
    //--------------------------------------------------------------------------
    /**
     * @return the textureList
     */
    public ArrayList<T> getTextures()
    {
        return textures;
    }

    /**
     * @return the volumesList
     */
    public ArrayList<V> getVolumes()
    {
        return volumes;
    }

} //end of ONEScene class
