package one.java;

import java.util.ArrayList;
import java.util.Comparator;
import one.java.events.ONEChangeEvent;
import one.java.events.ONEChangeEventType;
import one.java.events.ONEChangeListener;

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
    private transient ONEChangeListener changeListener;

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
        this.sortVolumes();

        this.changeListener = (ONEChangeEvent evt) ->
        {
            //If the order of a volume has changed, resort
            if (evt.getSource() instanceof ONEVolume && evt.getParameterName().equalsIgnoreCase("ORDER"))
            {
                this.sortVolumes();
            }

            this.fireChangeEvent(evt); //just propogate the event to any listeners
        };

        //Add the listener to all our children
        for (int i = 0; i < this.textures.size(); i++)
        {
            this.textures.get(i).addChangeListener(changeListener);
        }

        for (int i = 0; i < this.volumes.size(); i++)
        {
            this.volumes.get(i).addChangeListener(changeListener);
        }

    } //end of initialize function
    
    /**
     * Returns true if this header matches the one given
     * @param s
     * @return 
     */ 
    @Override
    protected boolean matches(ONEObject o)
    {
        if(!super.matches(o) || !(o instanceof ONEScene))
            return(false);
        
        ONEScene s = (ONEScene)o;
        
        //Check each volume and texture
        if(this.getVolumes().size() != s.getVolumes().size())
            return(false);
        if(this.getTextures().size() != s.getTextures().size())
            return(false);        
                
        for(int i = 0; i < this.getVolumes().size(); i++)
        {
            ONEVolume v = this.getVolumes().get(i);
            ONEVolume sV = s.getVolume(v.getID());
            if(!v.matches(sV))            
                return(false);
        }
        
        for(int i = 0; i < this.getTextures().size(); i++)
        {
            ONETexture t = this.getTextures().get(i);
            ONETexture sT = s.getTexture(t.getID());
            if(!t.matches(sT))            
                return(false);
        }
        
        //Passed all the tests
        return(true);
    }


    /**
     * Copies the given scene to this one
     */
    public void copy(ONEScene<V, T> scene)
    {
        this.clear();

        //Copy our header
        this.copyHeader(scene);

        for (int i = 0; i < scene.getVolumes().size(); i++)
        {
            V volume = scene.getVolumes().get(i);
            V newVolume = this.newVolume();
            newVolume.copy(volume);
            this.add(newVolume);
        }

        for (int i = 0; i < scene.getTextures().size(); i++)
        {
            T texture = scene.getTextures().get(i);
            T newTexture = this.newTexture();
            newTexture.copy(texture);
            this.add(newTexture);
        }

    }

    /**
     * Sorts the volumes according to order.
     */
    protected void sortVolumes()
    {
        this.volumes.sort((V o1, V o2) -> (Integer.compare(o1.getOrder(), o2.getOrder())));
        this.fireChangeEvent(new ONEChangeEvent(this, ONEChangeEventType.STRUCTURE_CHANGED, "SORTED", null, volumes));
    }

    @Override
    public void dispose()
    {
        super.dispose();
        this.clear();
    }

    @Override
    protected Object readResolve()
    {
        super.readResolve();
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
            this.remove(this.textures.get(0));
        }

        while (!this.volumes.isEmpty())
        {
            this.remove(this.volumes.get(0));
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

    public void add(ONEObject o)
    {
        if (this.newTexture().getClass().equals(o.getClass()))
        {
            this.add((T) o);
        }
        else if (this.newVolume().getClass().equals(o.getClass()))
        {
            this.add((V) o);
        }
    }

    public void add(V volume)
    {
        //If it already exists (same ID) replace it
        int index = volumes.indexOf(volume);

        if (index >= 0)
        {
            ONEVolume currentVolume = volumes.get(index);
            currentVolume.copyHeader(volume);
            return;
        }

        this.volumes.add(volume);
        //Listen for property changes
        volume.addChangeListener(changeListener);
        this.sortVolumes();

        this.fireChangeEvent(new ONEChangeEvent(this, ONEChangeEventType.STRUCTURE_CHANGED, "", null, volume));

    }

    public void add(T texture)
    {
        //If it already exists (same ID) replace it
        int index = textures.indexOf(texture);

        if (index >= 0)
        {
            T currentTexture = textures.get(index);
            currentTexture.copyHeader(texture);
            return;
        }
        this.textures.add(texture);
        //Listen for property changes
        texture.addChangeListener(changeListener);
        this.fireChangeEvent(new ONEChangeEvent(this, ONEChangeEventType.STRUCTURE_CHANGED, "", null, texture));

    }

    public void remove(ONEObject o)
    {
        if (o == null)
        {
            return;
        }

        if (this.newTexture().getClass().equals(o.getClass()))
        {
            this.remove((T) o);
        }
        else if (this.newVolume().getClass().equals(o.getClass()))
        {
            this.remove((V) o);
        }

    }

    public void remove(V volume)
    {
        this.volumes.remove(volume);
        volume.removeChangeListener(changeListener);
        this.fireChangeEvent(new ONEChangeEvent(this, ONEChangeEventType.STRUCTURE_CHANGED, "", volume, null));
        volume.dispose(); //do this after the fire event
    }

    public void remove(T texture)
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

        texture.removeChangeListener(changeListener);
        this.fireChangeEvent(new ONEChangeEvent(this, ONEChangeEventType.STRUCTURE_CHANGED, "", texture, null));
        texture.dispose(); //do this after the fire event
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
