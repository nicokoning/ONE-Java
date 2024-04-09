/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package one.java;

import java.util.ArrayList;

/**
 *
 * @author Nico
 */
public class ONEScene extends ONEObject
{

    //The serial version for deserializing
    private static final long serialVersionUID = 1L;

    //The version of our ONE format
    public static final int ONE_VERSION = 1;
    //This identifies files as a ONE format file
    public static final int ONE_ID = 102380;

    //The list of textures
    protected ArrayList<ONETexture> textures = new ArrayList<>();
    //The list of volumes
    protected ArrayList<ONEVolume> volumes = new ArrayList<>();

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

    } //end of initialize function

    /**
     * Clears the scene of all volumes and textures
     */
    public void clear()
    {
        for (ONETexture t : this.textures)
        {
            t.clearData();
        }
        this.textures.clear();

        for (ONEVolume v : this.volumes)
        {
            v.clearTextureIDs();
        }
        this.volumes.clear();

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
     * Adds the object to this scene
     *
     * @param object
     */
    public void add(ONEObject object)
    {
        if (object instanceof ONETexture oneTexture)
        {
            this.addTexture(oneTexture);
        }

        if (object instanceof ONEVolume oneVolume)
        {
            this.addVolume(oneVolume);
        }
    }

    private void addVolume(ONEVolume v)
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
    }

    private void addTexture(ONETexture t)
    {
        //If it already exists (same ID) replace it
        int index = textures.indexOf(t);

        if (index >= 0)
        {
            ONETexture currentTexture = textures.get(index);
            currentTexture.copyHeader(t);
        }
        else
        {
            this.textures.add(t);
        }
    }

    /**
     * Removes the object from our scene
     *
     * @param object
     */
    public void remove(ONEObject object)
    {
        if (object == null)
        {
            return;
        }

        if (object instanceof ONETexture oneTexture)
        {
            //Now remove the texture
            this.textures.remove(oneTexture);
            oneTexture.clearData();
        }

        if (object instanceof ONEVolume oneVolume)
        {
            this.volumes.remove(oneVolume);
        }

    }

    /**
     * Returns the texture with the given ID
     */
    public ONETexture getTexture(long ID)
    {
        for (int i = 0; i < this.textures.size(); i++)
        {
            ONETexture texture = this.textures.get(i);
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
    public ONEVolume getVolume(long ID)
    {
        for (int i = 0; i < this.volumes.size(); i++)
        {
            ONEVolume volume = this.volumes.get(i);
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
    public ArrayList<ONEVolume> getVolumes(ONETexture t)
    {
        ArrayList<ONEVolume> mappedVolumes = new ArrayList<>();
        for (int i = 0; i < volumes.size(); i++)
        {
            ONEVolume volume = volumes.get(i);
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
    public ArrayList<ONETexture> getTextures(ONEVolume v)
    {
        ArrayList<ONETexture> mappedTextures = new ArrayList<>();

        long[] tIds = v.getTextureIDs();
        for (int i = 0; i < tIds.length; i++)
        {
            ONETexture tex = this.getTexture(tIds[i]);
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
    public ArrayList<ONETexture> getTextures()
    {
        return textures;
    }

    /**
     * @return the volumesList
     */
    public ArrayList<ONEVolume> getVolumes()
    {
        return volumes;
    }

} //end of ONEScene class
