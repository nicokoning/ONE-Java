package one.java.voxels;

import one.java.io.ONEByteReader;
import one.java.io.ONEByteWriter;

/**
 *
 * @author Nico
 */
public class ONEByteVoxel extends ONEVoxel<Byte>
{

    //The serial version for deserializing
    private static final long serialVersionUID = 1L;

    @Override
    public ONEVoxel copy()
    {
        ONEByteVoxel voxel = new ONEByteVoxel();
        voxel.setIndex(xIndex, yIndex, zIndex);
        voxel.setColor(r, g, b, a);
        return (voxel);
    }

    @Override
    public int dataByteSize()
    {
        int size = ONEByteReader.BYTE_SIZE;
        return (size);
    }

    @Override
    public void setColor(Number r, Number g, Number b, Number a)
    {
        this.r = r.byteValue();
        this.g = g.byteValue();
        this.b = b.byteValue();
        this.a = a.byteValue();
    }

    @Override
    public void write(ONEByteWriter writer) throws Exception
    {
        writer.writeInt(this.xIndex);
        writer.writeInt(this.yIndex);
        writer.writeInt(this.zIndex);
        
        writer.writeByte(this.r);
        writer.writeByte(this.g);
        writer.writeByte(this.b);
        writer.writeByte(this.a);

    }

    @Override
    public void read(ONEByteReader reader)
    {
        this.xIndex = reader.nextInt();
        this.yIndex = reader.nextInt();
        this.zIndex = reader.nextInt();

        this.r = reader.nextByte();
        this.g = reader.nextByte();
        this.b = reader.nextByte();
        this.a = reader.nextByte();
    }
}
