package one.java;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import one.java.ONEPosition.ONE_COORDINATE_SYSTEM;
import one.java.ONEPosition.ONE_DISTANCE;
import one.java.events.ONEChangeEvent;
import one.java.events.ONEChangeEventType;
import one.java.events.ONEChangeListener;

/**
 *
 * @author Nico
 */
public abstract class ONEScene<V extends ONEVolume, T extends ONETexture> extends ONEObject
{

    //The serial version for deserializing
    private static final long serialVersionUID = 1L;

    //The version of our ONE format
    public static final int ONE_VERSION = 2;
    //This identifies files as a ONE format file
    public static final int ONE_ID = 102380;

    //The list of groups
    protected ArrayList<ONEGroup> groups = new ArrayList<>();
    //The list of textures
    protected ArrayList<T> textures = new ArrayList<>();
    //The list of volumes
    protected ArrayList<V> volumes = new ArrayList<>();

    //listens for property changes from its children
    private transient ONEChangeListener changeListener;

    /**
     * Creates a new ONEScene object
     */
    public ONEScene()
    {
        this.initialize();
    } //end of constructor

    /**
     * Initializes the object
     */
    private void initialize()
    {
        //Add some default parameters
        this.addParameter("AUTO_EXPOSURE", "true");
        this.addParameter("EXPOSURE", "1");
        this.addParameter("EMISSION", "1");
        this.addParameter("OPACITY", "1");
        this.addParameter("SCALE_POWER", "1");
        this.addParameter("ROT_X", "0");
        this.addParameter("ROT_Y", "0");
        this.addParameter("ROT_Z", "0");

        this.initializeTransient();

    } //end of initialize function

    /**
     * Initializes the object
     */
    private void initializeTransient()
    {
        // Older Java-serialized scenes do not have a groups field.
        if (this.groups == null)
        {
            this.groups = new ArrayList<>();
        }

        this.sortVolumes();

        this.changeListener = (ONEChangeEvent evt) ->
        {
            //If the order of a volume has changed, resort
            if (evt.getSource() instanceof ONEVolume && evt.getParameterName().equalsIgnoreCase("ORDER"))
            {
                this.sortVolumes();
            }

            if (evt.getSource() instanceof ONEVolume && evt.getParameterName().contains("TEXTURE_ID"))
            {
                this.sortVolumes();
            }

            if (evt.getSource() instanceof ONEGroup && "CHILD_IDS".equalsIgnoreCase(evt.getParameterName()))
            {
                this.fireChangeEvent(new ONEChangeEvent(this, ONEChangeEventType.STRUCTURE_CHANGED, "CHILD_IDS", null, evt.getSource()));
                return;
            }

            this.fireChangeEvent(evt); //just propogate the event to any listeners
        };

        //Add the listener to all our children
        for (ONEGroup group : this.groups)
        {
            group.addChangeListener(changeListener);
        }

        for (int i = 0; i < this.textures.size(); i++)
        {
            this.textures.get(i).addChangeListener(changeListener);
        }

        for (int i = 0; i < this.volumes.size(); i++)
        {
            this.volumes.get(i).addChangeListener(changeListener);
        }

    } //end of initialize function

    /**
     * Returns true if this header matches the one given
     *
     * @param s
     * @return
     */
    @Override
    protected boolean matches(ONEObject o)
    {
        if (!(o instanceof ONEScene))
        {
            return (false);
        }

        if (!super.matches(o))
        {
            return (false);
        }

        ONEScene s = (ONEScene) o;

        if (this.getGroups().size() != s.getGroups().size())
        {
            return false;
        }

        for (ONEGroup group : this.getGroups())
        {
            ONEGroup otherGroup = s.getGroup(group.getID());
            if (otherGroup == null || !group.matches(otherGroup))
            {
                return false;
            }
        }

        //Check each volume and texture
        if (this.getVolumes().size() != s.getVolumes().size())
        {
            return (false);
        }
        if (this.getTextures().size() != s.getTextures().size())
        {
            return (false);
        }

        for (int i = 0; i < this.getVolumes().size(); i++)
        {
            ONEVolume v = this.getVolumes().get(i);
            ONEVolume sV = s.getVolume(v.getID());
            if (!v.matches(sV))
            {
                return (false);
            }
        }

        for (int i = 0; i < this.getTextures().size(); i++)
        {
            ONETexture t = this.getTextures().get(i);
            ONETexture sT = s.getTexture(t.getID());
            if (!t.matches(sT))
            {
                return (false);
            }
        }

        //Passed all the tests
        return (true);
    }

    /**
     * Copies the given scene to this one
     */
    public void copy(ONEScene<V, T> scene)
    {
        if (scene == this)
        {
            return;
        }

        this.clear();

        //Copy our header
        this.copyHeader(scene);
        this.setID(scene.getID());

        for (int i = 0; i < scene.getVolumes().size(); i++)
        {
            V volume = scene.getVolumes().get(i);
            V newVolume = this.newVolume();
            newVolume.copy(volume);
            this.add(newVolume);
        }

        for (int i = 0; i < scene.getTextures().size(); i++)
        {
            T texture = scene.getTextures().get(i);
            T newTexture = (T) texture.copy();
            this.add(newTexture);
        }

        for (ONEGroup group : scene.getGroups())
        {
            ONEGroup newGroup = this.newGroup();
            newGroup.copy(group);
            this.add(newGroup);
        }

    }

    /**
     * Sorts the volumes according to order.
     */
    protected void sortVolumes()
    {
        this.volumes.sort((V o1, V o2) -> (Integer.compare(o1.getOrder(), o2.getOrder())));
        this.fireChangeEvent(new ONEChangeEvent(this, ONEChangeEventType.STRUCTURE_CHANGED, "SORTED", null, volumes));
    }

    @Override
    public void dispose()
    {
        super.dispose();
        this.clear();
    }

    @Override
    protected Object readResolve()
    {
        super.readResolve();
        this.initializeTransient();
        return (this);
    }

    /**
     * Clears the scene of all groups, volumes, and textures
     */
    public void clear()
    {
        // Remove group containers first; they do not own child object lifetimes.
        while (!this.groups.isEmpty())
        {
            this.remove(this.groups.get(0));
        }

        while (!this.textures.isEmpty())
        {
            this.remove(this.textures.get(0));
        }

        while (!this.volumes.isEmpty())
        {
            this.remove(this.volumes.get(0));
        }

    }

    /**
     * Clears all of the voxel data, may not want to store it all if we are just
     * interested in the header
     */
    public void clearData()
    {
        for (int i = 0; i < this.textures.size(); i++)
        {
            this.textures.get(i).clearData();
        }
    }

    /**
     * Creates a new volume of the correct type for this scene
     *
     * @return
     */
    public abstract V newVolume();

    /**
     * Creates a new volume of the correct type for this scene
     *
     * @return
     */
    public abstract T newTexture();

    public ONEGroup newGroup()
    {
        return new ONEGroup();
    }

    public void add(ONEObject o)
    {
        if (o instanceof ONEGroup)
        {
            this.add((ONEGroup) o);
        }
        else if (this.newTexture().getClass().equals(o.getClass()))
        {
            this.add((T) o);
        }
        else if (this.newVolume().getClass().equals(o.getClass()))
        {
            this.add((V) o);
        }
    }

    public void add(V volume)
    {
        ONEObject existing = this.getObject(volume.getID());
        if (existing != null && !(existing instanceof ONEVolume))
        {
            throw new IllegalArgumentException("Volume ID is already used by another object type: " + volume.getID());
        }

        //If it already exists (same ID) replace it
        int index = volumes.indexOf(volume);

        if (index >= 0)
        {
            ONEVolume currentVolume = volumes.get(index);
            currentVolume.copyHeader(volume);
            return;
        }

        this.volumes.add(volume);
        //Listen for property changes
        volume.addChangeListener(changeListener);
        this.sortVolumes();

        this.fireChangeEvent(new ONEChangeEvent(this, ONEChangeEventType.STRUCTURE_CHANGED, "", null, volume));

    }

    public void add(T texture)
    {
        ONEObject existing = this.getObject(texture.getID());
        if (existing != null && !(existing instanceof ONETexture))
        {
            throw new IllegalArgumentException("Texture ID is already used by another object type: " + texture.getID());
        }

        //If it already exists (same ID) replace it
        int index = textures.indexOf(texture);

        if (index >= 0)
        {
            T currentTexture = textures.get(index);
            currentTexture.copyHeader(texture);
            currentTexture.setVoxels(texture.getVoxels());
            return;
        }
        this.textures.add(texture);
        //Listen for property changes
        texture.addChangeListener(changeListener);
        this.fireChangeEvent(new ONEChangeEvent(this, ONEChangeEventType.STRUCTURE_CHANGED, "", null, texture));

    }

    public void add(ONEGroup group)
    {
        java.util.Objects.requireNonNull(group, "Group cannot be null.");
        ONEObject existing = this.getObject(group.getID());
        if (existing != null && !(existing instanceof ONEGroup))
        {
            throw new IllegalArgumentException("Group ID is already used by another object type: " + group.getID());
        }

        ONEGroup current = this.getGroup(group.getID());
        if (current != null)
        {
            if (current != group)
            {
                long[] previousIDs = current.getChildIDs();
                current.copyHeader(group);
                if (!java.util.Arrays.equals(previousIDs, current.getChildIDs()))
                {
                    this.fireChangeEvent(new ONEChangeEvent(this, ONEChangeEventType.STRUCTURE_CHANGED, "CHILD_IDS", null, current));
                }
            }
            return;
        }

        this.groups.add(group);
        group.addChangeListener(changeListener);
        this.fireChangeEvent(new ONEChangeEvent(this, ONEChangeEventType.STRUCTURE_CHANGED, "", null, group));
    }

    /**
     * Recursively removes the object from the scene. child textures and volumes
     * of groups will be removed.
     *
     * @param obj
     * @throws Exception
     */
    public int removeRecursive(ONEObject obj) throws Exception
    {
        List<ONEObject> toBeRemoved = new ArrayList<>();
        Set<Long> visited = new HashSet<>();

        collectForRemoval(obj, visited, toBeRemoved);
        if (toBeRemoved.isEmpty())
        {
            return (0);
        }

        int numRemoved = 0;
        for (ONEObject object : toBeRemoved)
        {
            if (this.getObject(object.getID()) == object)
            {
                this.remove(object);
                numRemoved++;
            }
        }

        return (numRemoved);
    }

    private void collectForRemoval(ONEObject object, Set<Long> visited, List<ONEObject> result)
    {
        // Never remove the scene itself.
        if (object == null || object == this || this.getObject(object.getID()) != object)
        {
            return;
        }
        // Prevent duplicate removal and infinite recursion through malformed cycles.
        if (!visited.add(object.getID()))
        {
            return;
        }
        if (object instanceof ONEGroup)
        {
            ONEGroup group = (ONEGroup) object;
            for (long childID : group.getChildIDs())
            {
                collectForRemoval(this.getObject(childID), visited, result);
            }
        }
        else if (object instanceof ONEVolume)
        {
            ONEVolume volume = (ONEVolume) object;
            for (long textureID : volume.getChildIDs())
            {
                collectForRemoval(this.getTexture(textureID), visited, result);
            }
        }
        // Postorder: descendants are removed before their parent.
        result.add(object);
    }

    public void remove(ONEObject o)
    {
        if (o == null)
        {
            return;
        }

        if (o instanceof ONEGroup)
        {
            this.remove((ONEGroup) o);
        }
        else if (this.newTexture().getClass().isAssignableFrom(o.getClass()))
        {
            this.remove((T) o);
        }
        else if (this.newVolume().getClass().isAssignableFrom(o.getClass()))
        {
            this.remove((V) o);
        }

    }

    public void remove(V volume)
    {
        if (volume == null)
        {
            return;
        }
        volume = this.getVolume(volume.getID());
        if (volume == null)
        {
            return;
        }

        this.volumes.remove(volume);
        this.removeGroupReferences(volume.getID());
        volume.removeChangeListener(changeListener);
        this.fireChangeEvent(new ONEChangeEvent(this, ONEChangeEventType.STRUCTURE_CHANGED, "", volume, null));
        volume.dispose(); //do this after the fire event
    }

    public void remove(T texture)
    {
        if (texture == null)
        {
            return;
        }
        texture = this.getTexture(texture.getID());
        if (texture == null)
        {
            return;
        }

        //Now remove the texture
        this.textures.remove(texture);
        this.removeGroupReferences(texture.getID());

        //Get the volume that holds this texture
        ArrayList<V> volumes = this.getVolumes(texture);
        for (int i = 0; i < volumes.size(); i++)
        {
            ONEVolume volume = volumes.get(i);
            volume.removeTextureIDs(texture.getID());
        }

        texture.removeChangeListener(changeListener);
        this.fireChangeEvent(new ONEChangeEvent(this, ONEChangeEventType.STRUCTURE_CHANGED, "", texture, null));
        texture.dispose(); //do this after the fire event
    }

    /**
     * Removes a group container, leaving all its children in the scene.
     */
    public void remove(ONEGroup group)
    {
        if (group == null)
        {
            return;
        }

        ONEGroup current = this.getGroup(group.getID());
        if (current == null)
        {
            return;
        }

        this.groups.remove(current);
        this.removeGroupReferences(current.getID());
        current.removeChangeListener(changeListener);
        this.fireChangeEvent(new ONEChangeEvent(this, ONEChangeEventType.STRUCTURE_CHANGED, "", current, null));
        current.dispose();
    }

    private void removeGroupReferences(long id)
    {
        for (ONEGroup group : this.groups)
        {
            for (long childID : group.getChildIDs())
            {
                if (childID == id)
                {
                    group.removeChildIDs(id);
                    break;
                }
            }
        }
    }

    public ArrayList<ONEGroup> getGroups()
    {
        return this.groups;
    }

    /**
     * Returns a list of all the objects in the scene
     */
    public ArrayList<ONEObject> getAllObjects()
    {
        ArrayList<ONEObject> allObjects = new ArrayList<>();
        allObjects.addAll(this.groups);
        allObjects.addAll(this.textures);
        allObjects.addAll(this.volumes);

        return (allObjects);
    }

    /**
     * Returns a list of all parent objects (grand parents, great grand etc) to
     * the one given
     */
    public ArrayList<ONEObject> getParents(ONEObject object)
    {
        ArrayList<ONEObject> parents = new ArrayList<>();
        HashSet<Long> visited = new HashSet<>();
        ONEObject parent = this.getParent(object);

        while (parent != null && visited.add(parent.getID()))
        {
            parents.add(parent);
            parent = this.getParent(parent);
        }

        return parents;
    }

    /**
     * Returns the parent to the given object
     */
    public ONEObject getParent(ONEObject object)
    {
        if (object == this || object == null)
        {
            return (null);
        }

        long objectID = object.getID();

        //make sure this object is actually part of the scene
        if (this.getObject(objectID) == null)
        {
            return (null);
        }

        ArrayList<ONEObject> allObjects = this.getAllObjects();

        for (int i = 0; i < allObjects.size(); i++)
        {
            ONEObject p = allObjects.get(i);
            long[] childIDs = p.getChildIDs();
            boolean contains = Arrays.stream(childIDs).anyMatch(x -> x == objectID);
            if (contains)
            {
                return (p);
            }
        }

        return (this); //no parent, return us
    }

    @Override
    public long[] getChildIDs()
    {
        ArrayList<Long> rootIDs = new ArrayList<>();
        HashSet<Long> referencedChildIDs = new HashSet<>();

        ArrayList<ONEObject> allObjects = this.getAllObjects();

        for (ONEObject object : allObjects)
        {
            for (long childID : object.getChildIDs())
            {
                referencedChildIDs.add(childID);
            }
        }

        for (ONEObject object : allObjects)
        {
            if (!referencedChildIDs.contains(object.getID()))
            {
                rootIDs.add(object.getID());
            }
        }

        long[] result = new long[rootIDs.size()];

        for (int i = 0; i < rootIDs.size(); i++)
        {
            result[i] = rootIDs.get(i);
        }

        return result;
    }

    public ONEGroup getGroup(ONEObject object)
    {
        return this.getGroup(object, new java.util.HashSet<>());
    }

    private ONEGroup getGroup(ONEObject object, java.util.Set<Long> visited)
    {
        if (object == null || !visited.add(object.getID()))
        {
            return null;
        }

        long objectID = object.getID();

        // Prefer a direct parent group.
        for (ONEGroup group : this.groups)
        {
            for (long childID : group.getChildIDs())
            {
                if (childID == objectID)
                {
                    return group;
                }
            }
        }

        // A texture may belong to a group through its volume.
        if (object instanceof ONETexture)
        {
            for (V volume : this.volumes)
            {
                for (long textureID : volume.getChildIDs())
                {
                    if (textureID == objectID)
                    {
                        ONEGroup group = this.getGroup(volume, visited);
                        if (group != null)
                        {
                            return group;
                        }

                        break;
                    }
                }
            }
        }

        return null;
    }

    public ONEGroup getGroup(long id)
    {
        for (ONEGroup group : this.groups)
        {
            if (group.getID() == id)
            {
                return group;
            }
        }

        return null;
    }

    /**
     * Resolves a child ID across the scene's groups, volumes, and textures.
     */
    public ONEObject getObject(long id)
    {
        ONEObject object = this.getGroup(id);
        if (object == null)
        {
            object = this.getVolume(id);
        }
        if (object == null)
        {
            object = this.getTexture(id);
        }

        return object;
    }

    /**
     * Returns direct children in membership order; unresolved IDs are skipped.
     */
    public ArrayList<ONEObject> getChildren(ONEGroup group)
    {
        java.util.Objects.requireNonNull(group, "Group cannot be null.");
        ArrayList<ONEObject> children = new ArrayList<>();
        for (long id : group.getChildIDs())
        {
            ONEObject child = this.getObject(id);
            if (child != null)
            {
                children.add(child);
            }
        }

        return children;
    }

    /**
     * Returns the texture with the given ID
     */
    public T getTexture(long ID)
    {
        for (int i = 0; i < this.textures.size(); i++)
        {
            T texture = this.textures.get(i);
            if (texture.getID() == ID)
            {
                return (texture);
            }
        }

        return (null);
    }

    /**
     * Returns the volume with the given ID
     *
     * @param ID
     * @return
     */
    public V getVolume(long ID)
    {
        for (int i = 0; i < this.volumes.size(); i++)
        {
            V volume = this.volumes.get(i);
            if (volume.getID() == ID)
            {
                return (volume);
            }
        }

        return (null);
    }

    /**
     * Returns the first mapped volume for the given texture
     *
     * @param t
     * @return
     */
    public V getVolume(T t)
    {
        ArrayList<V> mappedVolumes = this.getVolumes(t);
        if (mappedVolumes.isEmpty())
        {
            return (null);
        }

        return (mappedVolumes.get(0));
    }

    /**
     * Returns a list of all volumes that are mapped to the given texture
     */
    public ArrayList<V> getVolumes(T t)
    {
        ArrayList<V> mappedVolumes = new ArrayList<>();
        for (int i = 0; i < volumes.size(); i++)
        {
            V volume = volumes.get(i);
            long[] tIds = volume.getChildIDs();
            for (int j = 0; j < tIds.length; j++)
            {
                if (tIds[j] == t.getID())
                {
                    mappedVolumes.add(volume);
                    break;
                }
            }
        }

        return (mappedVolumes);
    }

    /**
     * Returns the position of this scene in astronomical coordinates determined
     * from the parameters
     */
    public ONEPosition getPosition()
    {
        try
        {
            ONEPosition.ONE_COORDINATE_SYSTEM coordinateSystem = ONE_COORDINATE_SYSTEM.valueOf(this.getParameter("POS_COORDINATE_SYSTEM"));
            ONEPosition.ONE_DISTANCE distanceUnit = ONE_DISTANCE.valueOf(this.getParameter("POS_DISTANCE_UNIT"));
            double lon = this.getDoubleParameter("POS_LON");
            double lat = this.getDoubleParameter("POS_LAT");
            double dist = this.getDoubleParameter("POS_DISTANCE");

            ONEPosition pos = new ONEPosition(coordinateSystem, lon, lat, dist, distanceUnit);
            return (pos);
        }
        catch (Exception e)
        {
            System.out.println("Unable to generate ONE position from scene parameters: " + e.getMessage());
        }

        return (null);
    }

    /**
     * Returns a list of all textures that are mapped to the given volume
     */
    public ArrayList<T> getTextures(V v)
    {
        ArrayList<T> mappedTextures = new ArrayList<>();

        long[] tIds = v.getChildIDs();
        for (int i = 0; i < tIds.length; i++)
        {
            T tex = this.getTexture(tIds[i]);
            if (tex != null)
            {
                mappedTextures.add(tex);
            }
        }

        return (mappedTextures);
    }

    //--------------------------------------------------------------------------
    // Getter and Setter methods
    //--------------------------------------------------------------------------
    /**
     * @return the textureList
     */
    public ArrayList<T> getTextures()
    {
        return textures;
    }

    /**
     * @return the volumesList
     */
    public ArrayList<V> getVolumes()
    {
        return volumes;
    }

} //end of ONEScene class

