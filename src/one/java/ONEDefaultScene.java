package one.java;

/**
 *
 * @author Nico
 */
public class ONEDefaultScene extends ONEScene<ONEVolume, ONETexture>
{
    //The serial version for deserializing
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new ONEScene object
     */
    public ONEDefaultScene()
    {
    } //end of constructor

    @Override
    public ONEVolume newVolume()
    {
        return(new ONEVolume());
    }

    @Override
    public ONETexture newTexture()
    {
        return(new ONETexture());
    }

} //end of ONEScene class
