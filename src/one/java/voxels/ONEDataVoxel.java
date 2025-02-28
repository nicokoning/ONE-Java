package one.java.voxels;

import one.java.io.ONEByteReader;
import one.java.io.ONEByteWriter;

/**
 * This type of voxel can hold more than just rgba data types
 *
 * @author Nico
 */
public class ONEDataVoxel extends ONEVoxel<Float>
{

    //The serial version for deserializing
    private static final long serialVersionUID = 1L;

    protected float[] variables;

    public ONEDataVoxel(float[] variables)
    {
        this.variables = variables;
    }

    @Override
    public int dataByteSize()
    {
        int size = ONEByteReader.FLOAT_SIZE;
        return (size);
    }

    @Override
    public void write(ONEByteWriter writer) throws Exception
    {
        writer.writeInt(this.xIndex);
        writer.writeInt(this.yIndex);
        writer.writeInt(this.zIndex);

        for (int i = 0; i < this.variables.length; i++)
        {
            writer.writeFloat(variables[i]);
        }
    }

    @Override
    public void read(ONEByteReader reader)
    {
        this.xIndex = reader.nextInt();
        this.yIndex = reader.nextInt();
        this.zIndex = reader.nextInt();

        for (int i = 0; i < this.variables.length; i++)
        {
            variables[i] = reader.nextFloat();
        }
    }

    @Override
    public void scaleColor(double rFactor, double gFactor, double bFactor, double aFactor)
    {
    }

    @Override
    public void addColor(Number r, Number g, Number b, Number a)
    {
    }

    @Override
    public Float getR()
    {
        return (0.0f);
    }

    @Override
    public Float getG()
    {
        return (0.0f);
    }

    @Override
    public Float getB()
    {
        return (0.0f);
    }

    @Override
    public Float getA()
    {
        return (0.0f);
    }

    @Override
    public void setR(Number r)
    {
    }

    @Override
    public void setG(Number g)
    {
    }

    @Override
    public void setB(Number b)
    {
    }

    @Override
    public void setA(Number a)
    {
    }

    @Override
    public Float getGrey()
    {
        return (0.0f);
    }

    /**
     * @return the variables
     */
    public float[] getVariables()
    {
        return variables;
    }

    @Override
    public ONEVoxel copy()
    {
        float[] newVars = new float[this.variables.length];
        System.arraycopy(variables, 0, newVars, 0, newVars.length);
        return(new ONEDataVoxel(newVars));
    }
    
     /**
     * Returns the size of the voxel in bytes for storage and reading purposes
     */
    public final int sizeInBytes()
    {
        int size = 3 * ONEByteReader.INT_SIZE + this.variables.length * dataByteSize();
        return (size);
    }

    @Override
    public boolean isZero()
    {
        return(false);
    }
    
    
}
