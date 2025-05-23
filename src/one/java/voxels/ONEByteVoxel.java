package one.java.voxels;

import one.java.io.ONEByteReader;
import one.java.io.ONEByteWriter;

/**
 *
 * @author Nico
 */
public class ONEByteVoxel extends ONEVoxel<Integer>
{
    //The serial version for deserializing
    private static final long serialVersionUID = 1L;

    //use ints as the backing type so we can have unsigned bytes
    private int r, g, b, a;

    @Override
    public ONEVoxel copy()
    {
        ONEByteVoxel voxel = new ONEByteVoxel();
        voxel.setIndex(xIndex, yIndex, zIndex);
        voxel.setColor(r, g, b, a);
        return (voxel);
    }

    @Override
    public void write(ONEByteWriter writer) throws Exception
    {
        writer.writeInt(this.xIndex);
        writer.writeInt(this.yIndex);
        writer.writeInt(this.zIndex);

        writer.writeByte((byte)this.r);
        writer.writeByte((byte)this.g);
        writer.writeByte((byte)this.b);
        writer.writeByte((byte)this.a);

    }

    @Override
    public void read(ONEByteReader reader)
    {
        this.xIndex = reader.nextInt();
        this.yIndex = reader.nextInt();
        this.zIndex = reader.nextInt();

        //This converts the incoming byte to integer so we can represent unsigned bytes
        this.r = reader.nextByte() & 0xff; 
        this.g = reader.nextByte() & 0xff;
        this.b = reader.nextByte() & 0xff;
        this.a = reader.nextByte() & 0xff;
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
        this.r += r.intValue();
        this.g += g.intValue();
        this.b += b.intValue();
        this.a += a.intValue();
    }

    @Override
    public Integer getR()
    {
        return (r);
    }

    @Override
    public Integer getG()
    {
        return (g);
    }

    @Override
    public Integer getB()
    {
        return (b);
    }

    @Override
    public Integer getA()
    {
        return (a);
    }

    @Override
    public void setR(Number r)
    {
        this.r = r.intValue();
    }

    @Override
    public void setG(Number g)
    {
        this.g = g.intValue();
    }

    @Override
    public void setB(Number b)
    {
        this.b = b.intValue();
    }

    @Override
    public void setA(Number a)
    {
        this.a = a.intValue();
    }

    @Override
    public Integer getGrey()
    {          
        int grey = (int)((r + g + b) / 3.0);
        return(grey);
    }
}
