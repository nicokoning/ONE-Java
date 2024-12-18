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
        return ("(" + this.xIndex + "," + this.yIndex + "," + this.zIndex + ") : [" + this.getR().toString() + "," + this.getG().toString() + "," + this.getB().toString() + "," + this.getA().toString() + "]");
    }

    /**
     * Returns true if all the components of the voxel are 0
     */
    public boolean isZero()
    {        
        return (getR().doubleValue() == 0 
                && getG().doubleValue() == 0 
                && getB().doubleValue() == 0 
                && getA().doubleValue() == 0);
    }

    /**
     * Returns true if the index of the given voxel matches ours, as well as the
     * colors.
     *
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

        if (!(
                this.getR().equals(v.getR()) 
                && this.getG().equals(v.getG()) 
                && this.getB().equals(v.getB()) 
                && this.getA().equals(v.getA())))
        {
            return (false);
        }

        return (true);
    }

    /**
     * How many bytes is each of the rgba data?
     *
     * @return
     */
    public abstract int dataByteSize();

    /**
     * Returns the size of the voxel in bytes for storage and reading purposes
     */
    public final int sizeInBytes()
    {
        int size = 3 * ONEByteReader.INT_SIZE + 4 * dataByteSize();
        return (size);
    }

    /**
     * Scales each voxel in this texture by the appropriate value given
     */
    public abstract void scaleColor(double rFactor, double gFactor, double bFactor, double aFactor);

    /**
     * Sets the voxel color
     */
    public abstract void addColor(Number r, Number g, Number b, Number a);

    /**
     * Sets the voxel color
     */
    public final void setColor(Number r, Number g, Number b, Number a)
    {
        this.setR(r);
        this.setG(g);
        this.setB(b);
        this.setA(a);
    }

    /**
     * Sets the 3D index of the voxel in the texture cube.
     *
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
     *
     * @return
     */
    public int[] getIndex()
    {
        return (new int[]
        {
            this.xIndex, this.yIndex, this.zIndex
        });
    }

    /**
     * Returns the grey value (r+g+b)/3 for the voxel
     */
    public abstract T getGrey();

    /**
     * @return the r
     */
    public abstract T getR();

    /**
     * @return the g
     */
    public abstract T getG();

    /**
     * @return the b
     */
    public abstract T getB();

    /**
     * @return the a
     */
    public abstract T getA();

    /**
     *
     * @param r
     */
    public abstract void setR(Number r);

    /**
     *
     * @param r
     */
    public abstract void setG(Number g);

    /**
     *
     * @param r
     */
    public abstract void setB(Number b);

    /**
     *
     * @param r
     */
    public abstract void setA(Number a);

    /**
     * Writes this voxel values to the given writer
     *
     * @param writer
     */
    public abstract void write(ONEByteWriter writer) throws Exception;

    /**
     * Writes this voxel values to the given writer
     *
     * @param writer
     */
    public abstract void read(ONEByteReader reader);

} //end of ONEVoxel class
