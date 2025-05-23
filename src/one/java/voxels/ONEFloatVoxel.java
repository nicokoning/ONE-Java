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
    
    private float r,g,b,a;

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
        this.r += r.floatValue();
        this.g += g.floatValue();
        this.b += b.floatValue();
        this.a += a.floatValue();
    }

    @Override
    public Float getR()
    {
        return (r);
    }

    @Override
    public Float getG()
    {
        return (g);
    }

    @Override
    public Float getB()
    {
        return (b);
    }

    @Override
    public Float getA()
    {
        return (a);
    }

    @Override
    public void setR(Number r)
    {
        this.r = r.floatValue();
    }

    @Override
    public void setG(Number g)
    {
        this.g = g.floatValue();
    }

    @Override
    public void setB(Number b)
    {
        this.b = b.floatValue();
    }

    @Override
    public void setA(Number a)
    {
        this.a = a.floatValue();
    }

    @Override
    public Float getGrey()
    {
        float grey = (this.getR() + this.getG() + this.getB()) / 3.0f;
        return(grey);
    }
}
