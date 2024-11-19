/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package one.java.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
public class ONEFileWriter
{
    //Monitors that progress of tasks

    private ONETaskMonitor taskMonitor;

    /**
     * Creates a new ONEFileWriter object
     */
    public ONEFileWriter()
    {
        this.taskMonitor = new ONETaskMonitor();
    } //end of constructor

    /**
     * Reads the entire file and returns the scene
     */
    public void write(ONEScene scene, String filename) throws Exception
    {
        //First we have to write the data out at the beginning of the file
        this.writeData(scene, filename);
        //Now write the header out
        this.writeHeader(scene, filename);
    }

    /**
     * Writes the list of parameters as a single string, each split by '!@'
     */
    private void writeParameters(RandomAccessFile raf, List<ONEParameter> parameters) throws Exception
    {
        String rString = "";
        for (int i = 0; i < parameters.size(); i++)
        {
            ONEParameter param = parameters.get(i);
            rString = rString + param.toString();

            //If there are more to come, put a separator string
            if (i < parameters.size() - 1)
            {
                rString = rString + "!@";
            }
        }
        raf.writeUTF(rString);
    }

    /**
     * Writes the data to the given file, will overwrite it if it exists
     */
    private void writeData(ONEScene scene, String filename) throws Exception
    {
        ONETask task = new ONETask("Writing ONE textures...", scene.getTextures().size());
        this.taskMonitor.add(task);

        DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(filename)));

        try
        {
            for (int i = 0; i < scene.getTextures().size(); i++)
            {
                ONETexture texture = scene.getTextures().get(i);
                this.writeData(texture, dos);
                task.setCurrentStep(i);
            }
        }
        finally
        {
            dos.close();
            //Remove the top task, should be ours
            this.taskMonitor.remove();
        }
    }

    /**
     * Reads the data for the given texture from the input stream
     *
     * @param texture
     * @param stream
     */
    private void writeData(ONETexture texture, DataOutputStream dos) throws Exception
    {
        //First purge the data in the texture
        texture.purge();

        ArrayList<ONEVoxel> voxels = texture.getVoxels();

        ONEByteWriter writer = new ONEByteWriter();
        writer.writeLong(texture.getID());
        writer.writeInt(voxels.size());
        writer.flush(dos);

        int dVoxel = 10000;
        int numVoxelsWritten = 0;

        Iterator<ONEVoxel> iterator = voxels.iterator();
        while (iterator.hasNext())
        {
            ONEVoxel v = iterator.next();
            v.write(writer);

            //Write out every 1000 voxels
            if (numVoxelsWritten++ > dVoxel)
            {
                writer.flush(dos);
                numVoxelsWritten = 0;
            }
        }

        //Write any remaining things out
        writer.flush(dos);
    }

    /**
     * Writes the header out to the given file. Will overwrite the existing
     * header if it exists
     *
     * @param scene
     * @param filename
     */
    public void writeHeader(ONEScene scene, String filename) throws Exception
    {
        File file = new File(filename);
        if (!file.exists())
        {
            throw new Exception("Unable to write header to " + filename + ". The file does not exist.");
        }

        //The header will start at the end for a blank file
        long headerStart = file.length();

        //Check to see if there is already a header and if so we need to replace it                
        try
        {
            ONEScene dummyScene = new ONEScene(); //we don't want to overwrite the one we are about to write!
            headerStart = new ONEFileReader().readHeader(dummyScene, filename);
        }
        catch (Exception e)
        {
        }

        RandomAccessFile raf = new RandomAccessFile(filename, "rw");
        raf.seek(headerStart); //goto the start of the header        
        this.writeHeader(scene, raf);
        raf.close();

    }

    private void writeHeader(ONEScene scene, RandomAccessFile raf) throws Exception
    {
        long headerStart = raf.getFilePointer();

        raf.writeInt(ONE_ID);
        raf.writeInt(ONEScene.ONE_VERSION);

        //SCENE
        {
            raf.writeLong(scene.getID());
            raf.writeUTF(scene.getName());
            this.writeParameters(raf, scene.getParameters());
        }

        //VOLUMES
        {
            raf.writeInt(scene.getVolumes().size());
            for (int i = 0; i < scene.getVolumes().size(); i++)
            {
                ONEVolume volume = scene.getVolumes().get(i);
                this.write(volume, raf);
            }
        }

        //TEXTURES
        {
            raf.writeInt(scene.getTextures().size());
            for (int i = 0; i < scene.getTextures().size(); i++)
            {
                ONETexture texture = scene.getTextures().get(i);
                this.write(texture, raf);
            }
        }

        //Write out the length of the header
        long headerLength = raf.getFilePointer() - headerStart;
        raf.writeLong(headerLength);

        //Truncate the file if necessary        
        raf.setLength(raf.getFilePointer());
    }

    /**
     * Read the next volume from the stream.
     *
     * @param scene
     * @param bStream
     */
    private void write(ONEVolume volume, RandomAccessFile raf) throws Exception
    {
        raf.writeLong(volume.getID());
        raf.writeUTF(volume.getName());
        this.writeParameters(raf, volume.getParameters());
    }

    /**
     * Read the next texture from the stream.
     *
     * @param scene
     * @param bStream
     */
    private void write(ONETexture texture, RandomAccessFile raf) throws Exception
    {
        raf.writeLong(texture.getID());
        raf.writeUTF(texture.getName());
        this.writeParameters(raf, texture.getParameters());
    }
    
     /**
     * @return the taskMonitor
     */
    public ONETaskMonitor getTaskMonitor()
    {
        return taskMonitor;
    }

} //end of ONEFileWriter class
