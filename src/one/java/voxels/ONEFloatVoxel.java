package one.java.voxels;

import one.java.io.ONEByteReader;
import one.java.io.ONEByteWriter;

/**
 *
 * @author Nico
 */
public class ONEFloatVoxel extends ONEVoxel<Float>
{

    //The serial version for deserializing
    private static final long serialVersionUID = 1L;

    public ONEFloatVoxel()
    {
    }

    public ONEFloatVoxel(int x, int y, int z, float r, float g, float b, float a)
    {
        this.setColor(r, g, b, a);
    }

    @Override
    public ONEVoxel copy()
    {
        ONEFloatVoxel voxel = new ONEFloatVoxel();
        voxel.setIndex(xIndex, yIndex, zIndex);
        voxel.setColor(r, g, b, a);
        return (voxel);
    }

    @Override
    public int dataByteSize()
    {
        int size = ONEByteReader.FLOAT_SIZE;
        return (size);
    }

    @Override
    public Float getValue(Number n)
    {
        return (n.floatValue());
    }

    @Override
    public void write(ONEByteWriter writer) throws Exception
    {
        writer.writeInt(this.xIndex);
        writer.writeInt(this.yIndex);
        writer.writeInt(this.zIndex);

        writer.writeFloat(this.r);
        writer.writeFloat(this.g);
        writer.writeFloat(this.b);
        writer.writeFloat(this.a);

    }

    @Override
    public void read(ONEByteReader reader)
    {
        this.xIndex = reader.nextInt();
        this.yIndex = reader.nextInt();
        this.zIndex = reader.nextInt();

        this.r = reader.nextFloat();
        this.g = reader.nextFloat();
        this.b = reader.nextFloat();
        this.a = reader.nextFloat();
    }
}
