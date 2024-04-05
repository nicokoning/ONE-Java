/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package one.java.io;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author Nico
 */
public class ONEByteWriter
{
    //Our byte stream
    protected ByteArrayOutputStream stream = new ByteArrayOutputStream();

    public ONEByteWriter()
    {
    }

    public void clear()
    {
        stream.reset();
    }

    public static byte[] getUnsignedInt(int v)
    {
        ByteBuffer b = ByteBuffer.allocate(1);
        ONEByteWriter.putUnsignedByte(b, v);
        return (b.array());
    }

    public static byte[] getInt(int v)
    {
        ByteBuffer b = ByteBuffer.allocate(4).putInt(v);
        return (b.array());

    }

    public static byte[] getDouble(double v)
    {
        ByteBuffer b = ByteBuffer.allocate(8).putDouble(v);
        return (b.array());
    }

    public static byte[] getFloat(float v)
    {
        ByteBuffer b = ByteBuffer.allocate(4).putFloat(v);
        return (b.array());
    }

    public static byte[] getString(String s)
    {
        ByteBuffer b = ByteBuffer.wrap(s.getBytes(StandardCharsets.UTF_8));
        return (b.array());
    }

    public static byte[] getLong(long s)
    {
        ByteBuffer b = ByteBuffer.allocate(8).putLong(s);
        return (b.array());
    }
    
    public static byte[] getByte(byte b)
    {
        return (new byte[]{b});
    }

    public static byte[] getBoolean(boolean v)
    {
        ByteBuffer b = ByteBuffer.allocate(1).put((byte) (v ? 1 : 0));
        return (b.array());
    }

    public final void writeBytes(byte[] bytes) throws Exception
    {
        stream.write(bytes);
    }

    public final void writeBoolean(boolean v) throws Exception
    {
         stream.write(getBoolean(v));
    }

    public final void writeUnsignedInt(int v) throws Exception
    {
         stream.write(getUnsignedInt(v));
    }

    public final void writeInt(int v) throws Exception
    {
        stream.write(getInt(v));
    }

    public final void writeDouble(double v) throws Exception
    {
         stream.write(getDouble(v));
    }

    public final void writeFloat(float v) throws Exception
    { 
        stream.write(getFloat(v));
    }

    public void writeString(String s) throws Exception
    {
        byte[] sBytes = s.getBytes(StandardCharsets.UTF_8);
        this.writeInt(sBytes.length); //first write how long the string is
        this.writeBytes(sBytes); //now write the bytes
    }

    public final void writeLong(long s) throws Exception
    {
         stream.write(getLong(s));
    }
    
    public final void writeByte(byte b) throws Exception
    {
         stream.write(getByte(b));
    }

    public final byte[] getByteArray()
    {
        return (this.stream.toByteArray());
    }

    public static short getUnsignedByte(ByteBuffer bb)
    {
        return ((short) (bb.get() & 0xff));
    }

    public static void putUnsignedByte(ByteBuffer bb, int value)
    {
        byte v = (byte) (value & 0xff);
        bb.put(v);
    }

    public static short getUnsignedByte(ByteBuffer bb, int position)
    {
        return ((short) (bb.get(position) & (short) 0xff));
    }

    public static void putUnsignedByte(ByteBuffer bb, int position,
            int value)
    {
        bb.put(position, (byte) (value & 0xff));
    }

    /**
     * @return the stream
     */
    public ByteArrayOutputStream getStream()
    {
        return stream;
    }
    
    /**
     * Flushes contents of this writer to the stream
     *
     * @param stream
     */
    public void flush(DataOutputStream dos) throws Exception
    {
        dos.write(this.getByteArray());
        this.clear();
    }

} //end of ONEByteWriter class
