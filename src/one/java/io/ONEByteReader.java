
package one.java.io;

import java.io.BufferedInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author Nico
 */
public class ONEByteReader
{
    protected ByteBuffer buffer;

    public static final int BYTE_SIZE = 1;
    public static final int INT_SIZE = 4;
    public static final int FLOAT_SIZE = 4;
    public static final int LONG_SIZE = 8;
    public static final int DOUBLE_SIZE = 8;

    public ONEByteReader()
    {
    }

    public ONEByteReader(byte[] bytes)
    {
        this.setBytes(bytes);
    }

    public ONEByteReader(ByteBuffer buffer)
    {
        this.setBuffer(buffer);
    }

    public void setByteOrder(ByteOrder order)
    {
        this.buffer.order(order);
    }

    /**
     * Resets our reader
     */
    public void reset()
    {
        getBuffer().rewind();
    }

    /**
     * Returns true if there is more data left
     */
    public boolean hasNext()
    {
        return (getBuffer().remaining() > 0);
    }

    public final byte nextByte()
    {
        return (getBuffer().get());
    }

    public final String nextString(int bytesToRead, Charset charSet)
    {
        String s = new String(getBuffer().array(), 0, bytesToRead, charSet);
        return (s);
    }

    public final short nextShort()
    {
        short value = getBuffer().getShort();
        return value;
    }

    public final int nextUnsignedShort()
    {
        return (nextShort() & 0xffff);
    }

    public final long nextLong()
    {
        long value = getBuffer().getLong();
        return value;
    }

    public final int nextInt()
    {
        int value = getBuffer().getInt();
        return value;
    }

    public final double nextDouble()
    {
        double value = getBuffer().getDouble();
        return value;
    }

    public final float nextFloat()
    {
        float value = getBuffer().getFloat();
        return value;
    }

    //--------------------------------------------------------------------------
    // Getter and Setter methods
    //--------------------------------------------------------------------------
    /**
     * @param bytes the bytes to set
     */
    public final void setBytes(byte[] bytes)
    {
        this.setBuffer(ByteBuffer.wrap(bytes));
    }

    public final void setBuffer(ByteBuffer buffer)
    {
        this.buffer = buffer;
    }

    /**
     * @return the buffer
     */
    public ByteBuffer getBuffer()
    {
        return buffer;
    }

    public static long nextLong(BufferedInputStream stream) throws Exception
    {
        byte[] buffer = new byte[ONEByteReader.LONG_SIZE];
        stream.read(buffer);
        ByteBuffer b = ByteBuffer.wrap(buffer);
        return (b.getLong());
    }

    public static int nextInt(BufferedInputStream stream) throws Exception
    {
        byte[] buffer = new byte[ONEByteReader.INT_SIZE];
        stream.read(buffer);
        ByteBuffer b = ByteBuffer.wrap(buffer);
        return (b.getInt());
    }

    /**
     * Reads in the next String from the stream, assuming it was put there by
     * the ONEByteWriter our read
     */
    public String nextString() throws Exception
    {
        //The first in will be the number of bytes in the string to read
        int byteInString = this.nextInt();
        String text = this.nextString(byteInString, StandardCharsets.UTF_8);
        return (text);
    }

} //end of ONEByteWriter class
