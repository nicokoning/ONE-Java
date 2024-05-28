/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package one.java.io;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import one.java.ONEParameter;
import one.java.ONEScene;
import static one.java.ONEScene.ONE_ID;
import one.java.ONETexture;
import one.java.ONEVolume;
import one.java.voxels.ONEVoxel;

/**
 *
 * @author Nico
 */
public class ONEFileReader
{

    /**
     * Creates a new ONEFileReader object
     */
    public ONEFileReader()
    {
    } //end of constructor

    /**
     * Reads the entire file and returns the scene
     */
    public ONEScene read(String filename) throws Exception
    {
        //The scene we are reading to
        ONEScene scene = new ONEScene();
        this.read(scene, filename);

        return (scene);
    }

    /**
     * Read the contents of the file into the one scene provided
     *
     * @param scene
     * @param filename
     * @throws Exception
     */
    public void read(ONEScene scene, String filename) throws Exception
    {
        File file = new File(filename);
        if (!file.exists())
        {
            throw new Exception("ONE file does not exist");
        }

        //First read the header into the scene object
        this.readHeader(scene, filename);
        this.readData(scene, filename);
    }

    /**
     * Reads the texture data into the scene. The header is needed before we can
     * do this (the scene has a valid header)
     */
    private void readData(ONEScene scene, String filename) throws Exception
    {
        BufferedInputStream stream = new BufferedInputStream(new FileInputStream(filename));
        for (int i = 0; i < scene.getTextures().size(); i++)
        {
            ONETexture texture = scene.getTextures().get(i);
            this.readData(texture, false, stream);
        }

        stream.close();
    }

    /**
     * Reads the data for the given texture from the input stream
     *
     * @param texture
     * @param stream
     */
    public void readTexture(ONEScene scene, ONETexture texture, String filename) throws Exception
    {
        BufferedInputStream stream = new BufferedInputStream(new FileInputStream(filename));
        for (int i = 0; i < scene.getTextures().size(); i++)
        {
            ONETexture sceneTexture = scene.getTextures().get(i);
            //Skip any textures that we don't want
            boolean skip = sceneTexture.getID() != texture.getID();

            this.readData(sceneTexture, skip, stream);
            //If this was the one we wanted, break.
            if (!skip)
            {
                break;
            }
        }

        stream.close();

    }

    /**
     * Reads the data for the given texture from the input stream
     *
     * @param texture
     * @param stream
     */
    private void readData(ONETexture texture, boolean skip, BufferedInputStream stream) throws Exception
    {
        long textureID = ONEByteReader.nextLong(stream);
        if (textureID != texture.getID())
        {
            throw new Exception("Texture ID of data ("+textureID+") does not match texture ID of object to read ("+texture.getID()+").");
        }

        //Number of voxels to read
        int numVoxels = ONEByteReader.nextInt(stream);
        //The size of each voxel in bytes
        int voxelByteSize = texture.newVoxel().sizeInBytes();
        //How many bytes do we have to read to get all the voxel data?
        long bytesToRead = numVoxels * voxelByteSize;

        //If we want to skip this texture, just leave, we are at the right place
        if (skip)
        {
            stream.skip(bytesToRead);
            return;
        }

        //how many voxels to read at a time?
        int dVoxel = 10000;
        int bufferSize = dVoxel * voxelByteSize;
        byte[] buffer = new byte[bufferSize];

        byte[] voxelBuffer = new byte[voxelByteSize];

        ONEByteReader byteReader = new ONEByteReader();

        //The list of voxels we add to
        ArrayList<ONEVoxel> voxelList = texture.getVoxels();
        //Grow the array here, once
        voxelList.ensureCapacity(voxelList.size() + numVoxels);

        long bytesRead = 0;
        while (bytesRead < bytesToRead)
        {
            //Figure out how many bytes we should read, make sure we don't read too many
            long readLength = Math.min(bufferSize, bytesToRead-bytesRead);
            //Read the bytes
            long read = stream.read(buffer, 0, (int)readLength);
            //How many voxels did we just read in?
            int readVoxels = (int) (read / voxelByteSize);
            //Increment our read bytes
            bytesRead += read;

            //Add all of the read voxels to our list
            for (int j = 0; j < readVoxels; j++)
            {
                ONEVoxel voxel = texture.newVoxel();
                System.arraycopy(buffer, j * voxelByteSize, voxelBuffer, 0, voxelBuffer.length);
                byteReader.setBytes(voxelBuffer);
                voxel.read(byteReader);
                voxelList.add(voxel);
            }
        }
    }

    /**
     * Adds all of the values in the array to the list, starting at the
     * startIndex of the array and ending at startIndex + length
     */
    private void addArrayToList(ArrayList<ONEVoxel> list, ONEVoxel[] array, int startIndex, int length)
    {
        //First expand the list size
        list.ensureCapacity(list.size() + length);

        int endIndex = startIndex + length;
        for (int i = startIndex; i < endIndex; i++)
        {
            ONEVoxel v = array[i];
            if (v == null)
            {
                continue;
            }

        }
    }

    /**
     * Reads the header from the filename and stores the info in the scene. The
     * byte location of the start of the header in the file is returned.
     */
    public long readHeader(ONEScene scene, String filename) throws Exception
    {
        File file = new File(filename);
        if (!file.exists())
        {
            throw new Exception("Unable to read ONE header from: " + filename + ". The file does not exist.");
        }

        long fileSize = file.length();
        RandomAccessFile raf = new RandomAccessFile(filename, "r");

        try
        {
            //Move to the last INT in the file, this will represent the header length
            raf.seek(fileSize - ONEByteReader.LONG_SIZE);
            long headerLength = raf.readLong();

            //The start of the header
            long headerStart = fileSize - headerLength - ONEByteReader.LONG_SIZE;
            raf.seek(headerStart);

            this.readHeader(scene, raf);

            return (headerStart);
        }
        finally
        {
            raf.close();
        }
    }

    private void readHeader(ONEScene scene, RandomAccessFile raf) throws Exception
    {
        int id = raf.readInt();
        if (id != ONE_ID)
        {
            throw new Exception("The input file is not the correct ONE format.");
        }

        int version = raf.readInt();

        long sceneID = raf.readLong();
        scene.setID(sceneID);

        String sceneName = raf.readUTF();
        scene.setName(sceneName);

        //Read in the parameters for the scene
        scene.getParameters().clear();
        scene.getParameters().addAll(this.readParameters(raf));

        //VOLUMES
        {
            int numVolumes = raf.readInt();
            for (int i = 0; i < numVolumes; i++)
            {
                ONEVolume volume = new ONEVolume();
                this.read(volume, raf, version);
                scene.add(volume);
            }
        }

        //TEXTURES
        {
            int numTextures = raf.readInt();
            for (int i = 0; i < numTextures; i++)
            {
                ONETexture texture = new ONETexture();
                this.read(texture, raf, version);
                scene.add(texture);
            }
        }
    }

    /**
     * Reads the parameter list from the next point in the stream
     *
     * @param bStream
     * @return
     * @throws Exception
     */
    private ArrayList<ONEParameter> readParameters(RandomAccessFile raf) throws Exception
    {
        ArrayList<ONEParameter> params = new ArrayList<>();

        //Read in the parameters string
        String parametersString = raf.readUTF();
        String[] pairs = parametersString.split("!@");
        for (int i = 0; i < pairs.length; i++)
        {
            String pair = pairs[i];
            String[] s = pair.split(":");
            if (s.length != 2)
            {
                continue;
            }

            ONEParameter param = new ONEParameter(s[0], s[1]);
            params.add(param);
        }

        return (params);
    }

    /**
     * Read the next volume from the stream.
     *
     * @param scene
     * @param bStream
     */
    private void read(ONEVolume volume, RandomAccessFile raf, int version) throws Exception
    {
        volume.setID(raf.readLong());
        volume.setName(raf.readUTF());
        volume.getParameters().clear();
        volume.getParameters().addAll(this.readParameters(raf));
        volume.clean();
    }

    /**
     * Read the next texture from the stream.
     *
     * @param scene
     * @param bStream
     */
    private void read(ONETexture texture, RandomAccessFile raf, int version) throws Exception
    {
        texture.setID(raf.readLong());
        texture.setName(raf.readUTF());
        texture.getParameters().clear();
        texture.getParameters().addAll(this.readParameters(raf));
        texture.clean();
    }

} //end of ONEFileReader class
