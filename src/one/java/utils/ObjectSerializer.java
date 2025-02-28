/*
 * ObjectSerializer.java
 *
 * Created on August 11, 2006, 10:40 AM
 *
 */
package one.java.utils;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class ObjectSerializer
{

    //Constructor
    private ObjectSerializer()
    {
    } //end of constructor

    /**
     * Returns the size of the object in bytes, uses serializable to do this
     */
    public static int sizeOf(Object o)
    {
        try
        {
            //Write the object to a binary stream
            CountableByteArrayStream bos = new CountableByteArrayStream();

            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(o);
            oos.flush();

            return (bos.getByteCount());
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }

        return (0);

    }

    //Performs a deep copy of the object and returns the copy
    static public Object deepCopy(Object oldObj)
    {
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        //The object we return
        Object rObject = null;

        try
        {
            //Write the object to a binary stream
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(bos);
            oos.writeObject(oldObj);
            oos.flush();

            //Read the object from the binary strea into a new object
            ByteArrayInputStream bin = new ByteArrayInputStream(bos.toByteArray());
            ois = new ObjectInputStream(bin);
            rObject = ois.readObject();

            //Close the streams
            oos.close();
            ois.close();

        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }

        return (rObject);
    } //end of deepCopy function  

    //Serialize the object and return the ByteArrayOutputStream it is contained in
    static public ByteArrayOutputStream serialize(Object object)
    {
        //The output stream we use for writing
        ObjectOutputStream oos = null;
        //The byte array we return
        ByteArrayOutputStream bos = null;

        try
        {
            //Create the byte array we will return
            bos = new ByteArrayOutputStream();
            //Open and output stream
            oos = new ObjectOutputStream(bos);
            //Write the object
            oos.writeObject(object);
            //Flush the data to the bytearray
            oos.flush();
            //Close the streams
            oos.close();
            //Return the bytearray
            return (bos);
        }
        catch (Exception e)
        {
        }

        return (bos);

    } //end of serialize function

    //Deserializes the array of bytes into an object and returns it
    static public Object deserialize(byte[] array)
    {
        //The object we return
        Object object = null;
        //The input stream
        ObjectInputStream ois = null;

        try
        {
            //Read the object from the binary strea into a new object
            ByteArrayInputStream bin = new ByteArrayInputStream(array);
            ois = new ObjectInputStream(bin);
            object = ois.readObject();

            //Close the stream            
            ois.close();
        }
        catch (Exception e)
        {
        }

        //Return the object
        return (object);

    } //end of deserialize function

    //Given the data output, we serialize the object to the output
    static public void serialize(Object object, DataOutput output)
    {
        //The size in bytes of the object
        int size = 0;

        try
        {

            //Serialize the object into a stream
            ByteArrayOutputStream stream = serialize(object);
            //Get the size of the byte array
            size = stream.size();
            //Write out the size to the output
            output.writeInt(size);
            //output the array of bytes
            output.write(stream.toByteArray());
        }
        catch (Exception e)
        {
        }

    } //end of serialize function

    //Returns the next object in the input if it was encoded with serialize
    static public Object deserialize(DataInput input)
    {
        //The object we return
        Object object = null;
        //The size of the byte array
        int size = 0;

        try
        {
            //The first read should be the size
            size = input.readInt();
            //Create our array to hold the value
            byte[] array = new byte[size];

            //Read the array in
            input.readFully(array);

            //Deserialize the array into an object
            object = deserialize(array);
        }
        catch (Exception e)
        {
        }

        //Return the object
        return (object);

    } //end of deserialize function

    /**
     * Compresses the object into a byte array and return it
     */
    static public byte[] compress(Object o)
    {
        try
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            GZIPOutputStream gzipOut = new GZIPOutputStream(baos);
            ObjectOutputStream objectOut = new ObjectOutputStream(gzipOut);
            objectOut.writeObject(o);
            objectOut.close();
            byte[] bytes = baos.toByteArray();
            return (bytes);
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }

        return (null);

    } //end of compress function

    /**
     * Decompresses the byte array back into an object
     */
    static public Object decompress(byte[] bytes)
    {
        try
        {
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            GZIPInputStream gzipIn = new GZIPInputStream(bais);
            ObjectInputStream objectIn = new ObjectInputStream(gzipIn);
            Object o = objectIn.readObject();
            objectIn.close();
            return (o);
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }

        return (null);

    } //end of compress function

    private static class CountableByteArrayStream extends ByteArrayOutputStream
    {
        protected int byteCount = 0;

        @Override
        public synchronized void write(byte[] b, int off, int len)
        {
            super.write(b, off, len);
            byteCount += len;
        }

        @Override
        public synchronized void write(int b)
        {
            super.write(b);
            byteCount += 1;
        }

        @Override
        public void write(byte[] b) throws IOException
        {
            super.write(b);
            byteCount += b.length;
        }

        /**
         * @return the byteCount
         */
        public int getByteCount()
        {
            return byteCount;
        }

    };
} //end of class
