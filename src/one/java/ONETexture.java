/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package one.java;

import java.util.ArrayList;
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
    
    public enum ONE_TEXTURE_CONTENT_TYPE
    {
        VOLUME, PROCEDURAL, DATA
    }

    //The serial version for deserializing
    private static final long serialVersionUID = 1L;

    //List of non-zero voxels
    private ArrayList<ONEVoxel> voxelList = new ArrayList<>();

    /**
     * Creates a new ONETexture object of the given type
     */
    public ONETexture(ONE_TEXTURE_TYPE type)
    {
        this.setType(type);
        this.addParameter("WIDTH", "0");
        this.addParameter("HEIGHT", "0");
        this.addParameter("DEPTH", "0");
    } //end of constructor

    /**
     * Creates a new ONETexture object, default to BYTE
     */
    public ONETexture()
    {
        this(ONE_TEXTURE_TYPE.RGBA_BYTE);
    } //end of constructor

    public void dispose()
    {
        super.dispose();
        this.clearData();
    }

    /**
     * Removes obsolete parameters, adds new required ones etc
     */
    public void clean()
    {
        super.clean();

        //Check to see if this is an older version that used RESOLUTION
        int res = this.getIntParameter("RESOLUTION");
        this.removeParameter("RESOLUTION");
        if (this.getWidth() == 0 && this.getHeight() == 0 && this.getDepth() == 0 && res != 0)
        {
            this.setSize(res, res, res);
        }
    }
    
    public void setWidth(int w)
    {
        this.setParameter("WIDTH", ""+w);
    }
    
     public void setHeight(int h)
    {
        this.setParameter("HEIGHT", ""+h);
    }
     
      public void setDepth(int d)
    {
        this.setParameter("DEPTH", ""+d);
    }
    
     
    /**
     * Copies the given volume to this one
     */
    public void copy(ONETexture texture)
    {
        this.copyHeader(texture);
        this.setID(texture.getID());
    }    
    
    /**
     * Returns true if there are no voxels in this texture
     */
    public boolean isEmpty()
    {
        return (this.voxelList.isEmpty());
    }

    /**
     * Clears all the voxel data
     */
    public void clearData()
    {
        this.voxelList.clear();
    }

    /**
     * Copies all the data from the given texture to this one.
     *
     * @param texture
     */
    public void copyData(ONETexture texture)
    {
        if (texture == null)
        {
            return;
        }

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
     * Creates a new voxel that is compatible with this texture, preassigns the index
     */
    public ONEVoxel newVoxel(int x, int y, int z) throws Exception
    {
        ONEVoxel v = ONEVoxel.newVoxel(this.getType());        
        v.setIndex(x, y, z);
        return(v);
    }

    /**
     * Returns true if the given index is valid for this texture
     *
     * @param x
     * @param y
     * @param z
     * @return
     */
    public boolean isValid(int x, int y, int z)
    {
        if (x < 0 || y < 0 || z < 0)
        {
            return (false);
        }

        if (x >= this.getWidth() || y >= this.getHeight() || z >= this.getDepth())
        {
            return (false);
        }

        return (true);
    }

    
    /**
     * Scales each voxel in this texture by the appropriate value given
     */ 
    public void scale(double rFactor, double gFactor, double bFactor, double aFactor)
    {
        for(int i = 0; i < this.voxelList.size(); i++)
        {
            ONEVoxel v = this.voxelList.get(i);
            v.scaleColor(rFactor, gFactor, bFactor, aFactor);
        }
    }
    
    /**
     * Creates an index array for fast lookups into the voxel list.  Each array element represents the index into the voxel list + 1 (an index value of 0 means it does not exist)
     * @return 
     */
    public int[][][] createIndexArray()
    {
        //Index our voxels
        int[][][] indexArray = new int[this.getWidth()][this.getHeight()][this.getDepth()];
        for(int i = 0; i < this.voxelList.size(); i++)
        {
            ONEVoxel v = this.voxelList.get(i);
            int x = v.getIndex()[0];
            int y = v.getIndex()[1];
            int z = v.getIndex()[2];
            
            indexArray[x][y][z] = i+1;
        }
        
        return(indexArray);
    }
    /**
     * Adds the given texture to this one (cell index by cell index)
     *
     * @param tex
     */
    public void add(ONETexture tex) throws Exception
    {
        //Index our voxels
        int[][][] indexArray = this.createIndexArray();
        
        for (int i = 0; i < tex.getVoxels().size(); i++)
        {
            ONEVoxel v1 = tex.getVoxels().get(i);
            int x = v1.getIndex()[0];
            int y = v1.getIndex()[1];
            int z = v1.getIndex()[2];

            //make sure we can set this voxel
            if (!this.isValid(x, y, z))
            {
                continue;
            }

            int index = indexArray[x][y][z]-1;
            if (index < 0)
            {
                ONEVoxel newVoxel = this.newVoxel(v1.getIndex()[0], v1.getIndex()[1], v1.getIndex()[2]);
                newVoxel.setColor(v1.getR(), v1.getG(), v1.getB(), v1.getA());
                this.getVoxels().add(newVoxel);
                indexArray[x][y][z] = this.voxelList.size();
                continue;
            }

            else
            {
                ONEVoxel v0 = this.voxelList.get(index);
                v0.addColor(v1.getR(), v1.getG(), v1.getB(), v1.getA());                
            }

        }
    }

    /**
     * Returns the voxel with the given index. NULL means it doesn't exist (i.e.
     * empty)
     */
    public ONEVoxel getVoxel(int x, int y, int z)
    {
        for (int i = 0; i < this.voxelList.size(); i++)
        {
            ONEVoxel v = this.voxelList.get(i);
            if (v.getIndex()[0] == x && v.getIndex()[1] == y && v.getIndex()[2] == z)
            {
                return (v);
            }
        }

        return (null);
    }
    
    /**
     * Returns the voxel with the given index in the list
     */
    public ONEVoxel getVoxel(int index)
    {
        if(index < 0 || index >= this.voxelList.size())
            return(null);
        
        return(this.voxelList.get(index));
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
     * @param type the type to set
     */
    public final void setContentType(ONE_TEXTURE_CONTENT_TYPE type)
    {
        this.setParameter("CONTENT_TYPE", type.name());
    }
    
    public ONE_TEXTURE_CONTENT_TYPE getContentType()
    {
        String param = this.getParameter("CONTENT_TYPE");
        if(param == null)
            return(ONE_TEXTURE_CONTENT_TYPE.VOLUME);
        
        return(ONE_TEXTURE_CONTENT_TYPE.valueOf(param));
    }

    /**
     * @return the res
     */
    public int getWidth()
    {
        return this.getIntParameter("WIDTH");
    }

    /**
     * @return the res
     */
    public int getHeight()
    {
        return this.getIntParameter("HEIGHT");
    }

    /**
     * @return the res
     */
    public int getDepth()
    {
        return this.getIntParameter("DEPTH");
    }

    /**
     * @return the res
     */
    public void setSize(int width, int height, int depth)
    {
        this.setParameter("WIDTH", Integer.toString(width));
        this.setParameter("HEIGHT", Integer.toString(height));
        this.setParameter("DEPTH", Integer.toString(depth));
    }

    /**
     * @param voxelList the voxelList to set
     */
    public void setVoxels(ArrayList<ONEVoxel> voxelList)
    {
        this.voxelList = voxelList;
    }

} //end of ONETexture class
