/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package one.java;

import java.util.ArrayList;
import one.java.voxels.ONEByteVoxel;
import one.java.voxels.ONEFloatVoxel;
import one.java.voxels.ONEVoxel;

/**
 *
 * @author Nico
 */
public class ONETexture extends ONEObject
{
    public enum ONE_TEXTURE_TYPE
    {
        RGBA_BYTE, RGBA_FLOAT
    }

    //The serial version for deserializing
    private static final long serialVersionUID = 1L;    
      
    //List of non-zero voxels
    protected ArrayList<ONEVoxel> voxelList = new ArrayList<>();
    
    /**
     * Creates a new ONETexture object of the given type
     */
    public ONETexture(ONE_TEXTURE_TYPE type)
    {
        this.setType(type);
    } //end of constructor
    
     /**
     * Creates a new ONETexture object, default to BYTE 
     */
    public ONETexture()
    {
        this.setType(ONE_TEXTURE_TYPE.RGBA_BYTE);
    } //end of constructor

    /**
     * Clears all the voxel data
     */ 
    public void clearData()
    {
        this.voxelList.clear();
    }
    
    /**
     * Copies all the data from the given texture to this one.
     * @param texture 
     */
    public void copyData(ONETexture texture)
    {
        if(texture == null)
            return;
        
        this.clearData();
        this.voxelList.addAll(texture.voxelList);
    }

    /**
     * Removes all zero voxels
     */
    public void purge()
    {
        for (int i = 0; i < this.voxelList.size(); i++)
        {
            ONEVoxel v = this.voxelList.get(i);
            if (v.isZero())
            {
                this.voxelList.remove(i--);
            }
        }
    }

    /**
     * Creates a new voxel that is compatible with this texture
     */
    public ONEVoxel newVoxel() throws Exception
    {
        switch (this.getType())
        {
            case RGBA_FLOAT ->
            {
                return (new ONEFloatVoxel());
            }
            case RGBA_BYTE ->
            {
                return (new ONEByteVoxel());
            }

            default ->
                throw new Exception("Voxel type is unknown.");
        }
    }
    
    //--------------------------------------------------------------------------
    // Getter and Setter methods
    //--------------------------------------------------------------------------
   
    /**
     * @return the type
     */
    public ONE_TEXTURE_TYPE getType()
    {
        return ONE_TEXTURE_TYPE.valueOf(this.getParameter("TYPE"));
    }

    /**
     * @return the list of non-zero voxels
     */
    public ArrayList<ONEVoxel> getVoxels()
    {
        return voxelList;
    }
    
    /**
     * @param type the type to set
     */
    public final void setType(ONE_TEXTURE_TYPE type)
    {
        this.setParameter("TYPE", type.name());
    }

    /**
     * @return the res
     */
    public int getResolution()
    {
        return this.getIntParameter("RESOLUTION");
    }
    
    /**
     * @return the res
     */
    public void setResolution(int res)
    {
        this.setParameter("RESOLUTION", Integer.toString(res));
    }
    
} //end of ONETexture class
