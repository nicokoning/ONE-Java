package one.java.coordinates;

import one.java.ONEScene;

/**
 * Versioned ICRS placement metadata for a cubic ONEScene.
 *
 * The scene-local coordinate cube is centered at its origin and spans [-0.5,
 * +0.5] on every axis. The stored rotation maps ONEScene-local Cartesian
 * coordinates to heliocentric ICRS Cartesian coordinates.
 *
 * @author Nico
 */
public final class ICRSSceneCoordinates
{
    public static final int CURRENT_PROTOCOL_VERSION = 1;

    public static final String PROTOCOL_VERSION = "ICRS_PROTOCOL_VERSION";

    public static final String CENTER_RA_DEG = "ICRS_CENTER_RA_DEG";
    public static final String CENTER_DEC_DEG = "ICRS_CENTER_DEC_DEG";
    public static final String CENTER_DISTANCE_PC = "ICRS_CENTER_DISTANCE_PC";

    public static final String SIZE_PC = "ICRS_SIZE_PC";
    public static final String ROTATION = "ICRS_ROTATION";

    private static final double ROTATION_TOLERANCE = 1.0e-6;

    private int version = CURRENT_PROTOCOL_VERSION;
    private double raDeg = 0.0;
    private double decDeg = 0.0;
    private double distancePc = 1.0;
    private double sizePc = 1.0;

    private double[][] rotation =
    {
        {
            1.0, 0.0, 0.0
        },
        {
            0.0, 1.0, 0.0
        },
        {
            0.0, 0.0, 1.0
        }
    };

    /**
     * Creates validated Version 1 ICRS scene-placement metadata.
     *
     * @param centerRaDeg ICRS right ascension in degrees
     * @param centerDecDeg ICRS declination in degrees
     * @param centerDistancePc heliocentric center distance in parsecs
     * @param sizePc physical side length of the cubic ONEScene in parsecs
     * @param localToIcrsRotation 3x3 row-major local-to-ICRS rotation
     */
    private ICRSSceneCoordinates()
    {
    }

    /**
     * Reads valid Version 1 ICRS coordinates from an object's parameters.
     *
     * @param object source ONE header object
     * @return parsed placement metadata, or null when missing or invalid
     */
    public static ICRSSceneCoordinates read(ONEScene scene)
    {
        if (scene == null)
        {
            return null;
        }

        try
        {
            String versionValue = scene.getParameter(PROTOCOL_VERSION);

            if (versionValue == null)
            {
                return null;
            }

            int version = Integer.parseInt(versionValue.trim());

            if (version != CURRENT_PROTOCOL_VERSION)
            {
                return null;
            }

            Double raDeg = getFiniteDoubleParameter(scene, CENTER_RA_DEG);
            Double decDeg = getFiniteDoubleParameter(scene, CENTER_DEC_DEG);
            Double distancePc = getFiniteDoubleParameter(scene, CENTER_DISTANCE_PC);
            Double sizePc = getFiniteDoubleParameter(scene, SIZE_PC);
            double[][] rotation = scene.getMatrix3DParameter(ROTATION);

            if (raDeg == null
                    || decDeg == null
                    || distancePc == null
                    || sizePc == null
                    || rotation == null)
            {
                return null;
            }
            return(new ICRSSceneCoordinates.Builder()
                    .version(version)
                    .ra(raDeg)
                    .dec(decDeg)
                    .distancePc(distancePc)
                    .sizePc(sizePc)
                    .rotation(rotation).build());           
        }
        catch (Exception e)
        {
            return null;
        }
    }

    /**
     * Writes this placement metadata to the supplied ONE header object.
     *
     * @param object destination ONE header object
     */
    public void writeTo(ONEScene scene)
    {
        if (scene == null)
        {
            throw new IllegalArgumentException(
                    "Cannot write ICRS coordinates to a null object."
            );
        }

        scene.setParameter(PROTOCOL_VERSION, Integer.toString(CURRENT_PROTOCOL_VERSION));

        scene.setParameter(CENTER_RA_DEG, Double.toString(this.raDeg));
        scene.setParameter(CENTER_DEC_DEG, Double.toString(this.decDeg));
        scene.setParameter(CENTER_DISTANCE_PC, Double.toString(this.distancePc));

        scene.setParameter(SIZE_PC, Double.toString(sizePc));
        scene.setParameter(ROTATION, matrixToString(this.rotation));
    }

    /**
     * Returns the heliocentric ICRS Cartesian center position in parsecs.
     *
     * @return [x, y, z] in parsecs
     */
    public double[] getCenterIcrsPc()
    {
        double raRadians = Math.toRadians(this.raDeg);
        double decRadians = Math.toRadians(this.decDeg);

        double cosDec = Math.cos(decRadians);

        return new double[]
        {
            this.distancePc * cosDec * Math.cos(raRadians),
            this.distancePc * cosDec * Math.sin(raRadians),
            this.distancePc * Math.sin(decRadians)
        };
    }

    /**
     * Converts a normalized ONEScene-local point into heliocentric ICRS
     * Cartesian coordinates in parsecs.
     *
     * @param x local x coordinate
     * @param y local y coordinate
     * @param z local z coordinate
     * @return [x, y, z] in heliocentric ICRS parsecs
     */
    public double[] transformLocalPoint(double x, double y, double z)
    {
        double[] center = getCenterIcrsPc();

        return new double[]
        {
            center[0] + sizePc * (this.rotation[0][0] * x
            + this.rotation[0][1] * y
            + this.rotation[0][2] * z),
            center[1] + sizePc * (this.rotation[1][0] * x
            + this.rotation[1][1] * y
            + this.rotation[1][2] * z),
            center[2] + sizePc * (this.rotation[2][0] * x
            + this.rotation[2][1] * y
            + this.rotation[2][2] * z)
        };
    }

    public int getProtocolVersion()
    {
        return this.version;
    }

    public double getCenterRaDeg()
    {
        return this.raDeg;
    }

    public double getCenterDecDeg()
    {
        return this.decDeg;
    }

    public double getCenterDistancePc()
    {
        return this.distancePc;
    }

    public double getSizePc()
    {
        return sizePc;
    }

    /**
     * Returns a copy of the 3x3 row-major local-to-ICRS rotation matrix.
     *
     * @return copied 3x3 matrix
     */
    public double[][] getLocalToIcrsRotation()
    {
        return copyMatrix(this.rotation);
    }

    /**
     * Returns true when all Version 1 placement metadata is present and valid.
     *
     * @param object source ONE header object
     * @return true when a valid Version 1 placement can be read
     */
    public static boolean isPresent(ONEScene scene)
    {
        return read(scene) != null;
    }

    private static Double getFiniteDoubleParameter(ONEScene object, String key)
    {
        String value = object.getParameter(key);

        if (value == null)
        {
            return null;
        }

        try
        {
            double parsed = Double.parseDouble(value);

            if (!Double.isFinite(parsed))
            {
                return null;
            }

            return parsed;
        }
        catch (NumberFormatException e)
        {
            return null;
        }
    }

    private static boolean isRotationMatrix(double[][] matrix)
    {
        if (matrix == null || matrix.length != 3)
        {
            return false;
        }

        for (int row = 0; row < 3; row++)
        {
            if (matrix[row] == null || matrix[row].length != 3)
            {
                return false;
            }

            for (int column = 0; column < 3; column++)
            {
                if (!Double.isFinite(matrix[row][column]))
                {
                    return false;
                }
            }
        }

        for (int row = 0; row < 3; row++)
        {
            for (int column = 0; column < 3; column++)
            {
                double dot = 0.0;

                for (int index = 0; index < 3; index++)
                {
                    dot += matrix[row][index] * matrix[column][index];
                }

                double expected = row == column ? 1.0 : 0.0;

                if (Math.abs(dot - expected) > ROTATION_TOLERANCE)
                {
                    return false;
                }
            }
        }

        double determinant
                = matrix[0][0] * (matrix[1][1] * matrix[2][2] - matrix[1][2] * matrix[2][1])
                - matrix[0][1] * (matrix[1][0] * matrix[2][2] - matrix[1][2] * matrix[2][0])
                + matrix[0][2] * (matrix[1][0] * matrix[2][1] - matrix[1][1] * matrix[2][0]);

        return Math.abs(determinant - 1.0) <= ROTATION_TOLERANCE;
    }

    private static String matrixToString(double[][] matrix)
    {
        return "["
                + Double.toString(matrix[0][0]) + ", "
                + Double.toString(matrix[0][1]) + ", "
                + Double.toString(matrix[0][2]) + ", "
                + Double.toString(matrix[1][0]) + ", "
                + Double.toString(matrix[1][1]) + ", "
                + Double.toString(matrix[1][2]) + ", "
                + Double.toString(matrix[2][0]) + ", "
                + Double.toString(matrix[2][1]) + ", "
                + Double.toString(matrix[2][2])
                + "]";
    }

    private static double normalizeRa(double raDeg)
    {
        double normalized = raDeg % 360.0;

        if (normalized < 0.0)
        {
            normalized += 360.0;
        }

        return normalized;
    }

    private static double[][] copyMatrix(double[][] matrix)
    {
        return new double[][]
        {
            {
                matrix[0][0], matrix[0][1], matrix[0][2]
            },
            {
                matrix[1][0], matrix[1][1], matrix[1][2]
            },
            {
                matrix[2][0], matrix[2][1], matrix[2][2]
            }
        };
    }

    public Builder toBuilder()
    {
        return new Builder()
                .version(this.version)
                .ra(this.raDeg)
                .dec(this.decDeg)
                .distancePc(this.distancePc)
                .sizePc(sizePc)
                .rotation(this.rotation);
    }

    public static final class Builder
    {
        private ICRSSceneCoordinates coords = new ICRSSceneCoordinates();

        public Builder version(int value)
        {
            this.coords.version = value;
            return (this);
        }

        public Builder ra(double value)
        {
            this.coords.raDeg = value;
            return this;
        }

        public Builder dec(double value)
        {
            this.coords.decDeg = value;
            return this;
        }

        public Builder distancePc(double value)
        {
            this.coords.distancePc = value;
            return this;
        }

        public Builder sizePc(double value)
        {
            this.coords.sizePc = value;
            return this;
        }

        public Builder rotation(double[][] value)
        {
            this.coords.rotation = copyMatrix(value);
            return this;
        }

        public ICRSSceneCoordinates build()
        {
            return this.coords;
        }
    }
}
