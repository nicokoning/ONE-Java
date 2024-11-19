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

    private byte r, g, b, a;

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

    @Override
    public void scaleColor(double rFactor, double gFactor, double bFactor, double aFactor)
    {
        this.r *= rFactor;
        this.g *= gFactor;
        this.b *= bFactor;
        this.a *= aFactor;
    }

    @Override
    public void addColor(Number r, Number g, Number b, Number a)
    {
        this.r += r.byteValue();
        this.g += g.byteValue();
        this.b += b.byteValue();
        this.a += a.byteValue();
    }

    @Override
    public Byte getR()
    {
        return (r);
    }

    @Override
    public Byte getG()
    {
        return (g);
    }

    @Override
    public Byte getB()
    {
        return (b);
    }

    @Override
    public Byte getA()
    {
        return (a);
    }

    @Override
    public void setR(Number r)
    {
        this.r = r.byteValue();
    }

    @Override
    public void setG(Number g)
    {
        this.g = g.byteValue();
    }

    @Override
    public void setB(Number b)
    {
        this.b = b.byteValue();
    }

    @Override
    public void setA(Number a)
    {
        this.a = a.byteValue();
    }
}
