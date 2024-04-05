/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package one.java.voxels;

import one.java.io.ONEByteReader;
import one.java.io.ONEByteWriter;


/**
 *
 * @author Nico
 */
public abstract class ONEVoxel<T extends Number>
{
    protected int xIndex;
    protected int yIndex;
    protected int zIndex;

    //The components of the voxel
    protected T r;
    //The components of the voxel
    protected T g;
    //The components of the voxel
    protected T b;
    //The components of the voxel
    protected T a;

    /**
     * Creates a new ONEVoxel object
     */
    public ONEVoxel()
    {
        this.clear();
    } //end of constructor

    /**
     * Clears the pixel values of this voxel
     */ 
    protected final void clear()
    {
        this.setColor(0, 0, 0, 0);
    }

    /**
     * Returns a copy of this voxel
     */ 
    public abstract ONEVoxel copy();

    @Override
    public String toString()
    {
        return ("(" + this.xIndex + "," + this.yIndex + "," + this.zIndex + ") : [" + this.r.toString() + "," + this.g.toString() + "," + this.b.toString() + "," + this.a.toString() + "]");
    }

    /**
     * Returns true if all the components of the voxel are 0
     */
    public boolean isZero()
    {
        return (r.equals(0) && g.equals(0) && b.equals(0) && a.equals(0));
    }

    /**
     * Returns true if the index of the given voxel matches ours, as well as the colors.
     * @param o
     * @return 
     */
    public boolean equals(Object o)
    {
        if (!(o instanceof ONEVoxel))
        {
            return (false);
        }

        ONEVoxel v = (ONEVoxel) o;
        if (!(this.xIndex == v.xIndex && this.yIndex == v.yIndex && this.zIndex == v.zIndex))
        {
            return (false);
        }

        if (!(this.r.equals(v.r) && this.g.equals(v.g) && this.b.equals(v.b) && this.a.equals(v.a)))
        {
            return (false);
        }

        return (true);
    }

    /**
     * Returns the size of the voxel in bytes for storage and reading purposes
     */
    public abstract int sizeInBytes();

    /**
     * Sets the voxel color
     */
    public abstract void setColor(Number r, Number g, Number b, Number a);

    /**
     * Sets the 3D index of the voxel in the texture cube.
     * @param x
     * @param y
     * @param z 
     */
    public void setIndex(int x, int y, int z)
    {
        this.xIndex = x;
        this.yIndex = y;
        this.zIndex = z;               
    }
    
    /**
     * Returns the 3D index of this voxel in the texture
     * @return 
     */
    public int[] getIndex()
    {
        return(new int[]{this.xIndex, this.yIndex, this.zIndex});
    }

    /**
     * Returns the grey value (r+g+b)/3 for the voxel
     */ 
    public double getGrey()
    {
        return ((this.getR().doubleValue() + this.getG().doubleValue() + this.getB().doubleValue()) / 3.0);
    }

    /**
     * @return the r
     */
    public T getR()
    {
        return r;
    }

    /**
     * @return the g
     */
    public T getG()
    {
        return g;
    }

    /**
     * @return the b
     */
    public T getB()
    {
        return b;
    }

    /**
     * @return the a
     */
    public T getA()
    {
        return a;
    }
    
     /**
     * Writes this voxel values to the given writer
     *
     * @param writer
     */
    public abstract void write(ONEByteWriter writer)  throws Exception;

    /**
     * Writes this voxel values to the given writer
     *
     * @param writer
     */
    public abstract void read(ONEByteReader reader);

} //end of ONEVoxel class
