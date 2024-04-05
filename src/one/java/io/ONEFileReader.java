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
            this.readData(texture, stream);
        }

        stream.close();
    }
    
       /**
     * Reads the data for the given texture from the input stream
     *
     * @param texture
     * @param stream
     */
    private void readData(ONETexture texture, BufferedInputStream stream) throws Exception
    {
        long textureID = ONEByteReader.nextLong(stream);
        if (textureID != texture.getID())
        {
            throw new Exception("Texture ID of data does not match texture ID of object to read.");
        }

        //Number of voxels to read
        int numVoxels = ONEByteReader.nextInt(stream);
        //The size of each voxel in bytes
        int voxelByteSize = texture.newVoxel().sizeInBytes();

        //Read the voxels (THIS CAN BE SPED UP! use buffering)  
        for (int i = 0; i < numVoxels; i++)
        {
            //Create a new voxel
            ONEVoxel voxel = texture.newVoxel();

            //Read the next voxel from the stream
            byte[] buffer = new byte[voxelByteSize];
            stream.read(buffer);

            ONEByteReader byteReader = new ONEByteReader(buffer);            
            voxel.read(byteReader);

            texture.getVoxels().add(voxel);
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
    }

 

} //end of ONEFileReader class
