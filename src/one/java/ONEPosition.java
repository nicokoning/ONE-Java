/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package one.java;

/**
 *
 * @author konic
 */
public class ONEPosition
{

    //The serial version for deserializing
    private static final long serialVersionUID = 1L;

    public enum ONE_COORDINATE_SYSTEM
    {
        EQUATORIAL,
        GALACTIC,
        SUPERGALACTIC;

        /**
         * Returns an array of string representations of all coordinate systems.
         */
        public static String[] stringValues()
        {
            ONE_COORDINATE_SYSTEM[] values = values();
            String[] strings = new String[values.length];
            for (int i = 0; i < values.length; i++)
            {
                strings[i] = values[i].name();
            }
            return strings;
        }
    }

    // The Distance Enum (Base unit: Parsec)
    public enum ONE_DISTANCE
    {
        PC(1.0),
        KPC(1000.0),
        LY(0.306601394); // 1 Light Year = ~0.3066 Parsecs

        private final double conversionToPc;

        ONE_DISTANCE(double conversionToPc)
        {
            this.conversionToPc = conversionToPc;
        }

        public double toPc(double value)
        {
            return value * this.conversionToPc;
        }

        public double fromPc(double valueInPc)
        {
            return valueInPc / this.conversionToPc;
        }

        /**
         * Returns an array of string representations of all coordinate systems.
         */
        public static String[] stringValues()
        {
            ONE_DISTANCE[] values = values();
            String[] strings = new String[values.length];
            for (int i = 0; i < values.length; i++)
            {
                strings[i] = values[i].name();
            }
            return strings;
        }
    }

    private final ONE_COORDINATE_SYSTEM coordSys;
    private final double lon; // Longitude / RA in degrees (0.0 to 360.0)
    private final double lat; // Latitude / Dec in degrees (-90.0 to 90.0)
    private final double distance;
    private final ONE_DISTANCE distanceUnit;

    public ONEPosition(ONE_COORDINATE_SYSTEM coordSys, double lon, double lat, double distance, ONE_DISTANCE distanceUnit)
    {
        this.coordSys = coordSys;
        this.lon = (lon % 360.0 + 360.0) % 360.0; // Normalize 0-360
        this.lat = Math.max(-90.0, Math.min(90.0, lat)); // Clamp -90 to 90
        this.distance = distance;
        this.distanceUnit = distanceUnit;
    }

    // Getters
    public ONE_COORDINATE_SYSTEM getCoordSys()
    {
        return coordSys;
    }

    public double getLon()
    {
        return lon;
    }

    public double getLat()
    {
        return lat;
    }

    public double getDistance()
    {
        return distance;
    }

    public ONE_DISTANCE getDistanceUnit()
    {
        return distanceUnit;
    }

    /**
     * Converts the current spherical representation into a 3D Cartesian array
     * [X, Y, Z] in Parsecs.
     */
    public double[] toCartesianPc()
    {
        double r = distanceUnit.toPc(this.distance);
        double lonRad = Math.toRadians(this.lon);
        double latRad = Math.toRadians(this.lat);

        double x = r * Math.cos(latRad) * Math.cos(lonRad);
        double y = r * Math.cos(latRad) * Math.sin(lonRad);
        double z = r * Math.sin(latRad);
        return new double[]
        {
            x, y, z
        };
    }

    /**
     * Creates a ONEPosition object from a Cartesian array [X, Y, Z] in Parsecs.
     */
    public static ONEPosition fromCartesianPc(ONE_COORDINATE_SYSTEM coordSys, double[] xyz, ONE_DISTANCE targetUnit)
    {
        double rPc = Math.sqrt(xyz[0] * xyz[0] * xyz[1] * xyz[1] * xyz[2] * xyz[2]);
        if (rPc == 0)
        {
            return new ONEPosition(coordSys, 0, 0, 0, targetUnit);
        }

        double lat = Math.toDegrees(Math.asin(xyz[2] / rPc));
        double lon = Math.toDegrees(Math.atan2(xyz[1], xyz[0]));
        double distance = targetUnit.fromPc(rPc);

        return new ONEPosition(coordSys, lon, lat, distance, targetUnit);
    }

    /**
     * Transforms coordinates safely into another target system and unit scale.
     */
    public ONEPosition convertTo(ONE_COORDINATE_SYSTEM targetSys, ONE_DISTANCE targetUnit)
    {
        double[] xyz = this.toCartesianPc();

        if (this.coordSys == targetSys)
        {
            return fromCartesianPc(targetSys, xyz, targetUnit);
        }

        // Standard J2000 transformation matrices (IAU 1958 standards)
        double[][] R_eq_to_gal =
        {
            {
                -0.05487556, -0.87343709, -0.48383502
            },
            {
                0.49410943, -0.44482963, 0.74698224
            },
            {
                -0.86766615, -0.19807637, 0.45598378
            }
        };

        double[][] R_gal_to_sg =
        {
            {
                -0.73516695, 0.67788400, 0.00000000
            },
            {
                -0.07459121, -0.08089915, 0.99392259
            },
            {
                0.67375344, 0.73069150, 0.11008126
            }
        };

        // First: Bring whatever we have back to Equatorial Cartesian baseline
        double[] eqXyz;
        if (this.coordSys == ONE_COORDINATE_SYSTEM.EQUATORIAL)
        {
            eqXyz = xyz;
        }
        else if (this.coordSys == ONE_COORDINATE_SYSTEM.GALACTIC)
        {
            eqXyz = multiplyTranspose(R_eq_to_gal, xyz); // Gal -> Eq
        }
        else
        { // Current is Supergalactic
            double[] galXyz = multiplyTranspose(R_gal_to_sg, xyz); // Sg -> Gal
            eqXyz = multiplyTranspose(R_eq_to_gal, galXyz); // Gal -> Eq
        }

        // Second: Route the Equatorial baseline forward to the target system
        double[] targetXyz;
        if (targetSys == ONE_COORDINATE_SYSTEM.EQUATORIAL)
        {
            targetXyz = eqXyz;
        }
        else if (targetSys == ONE_COORDINATE_SYSTEM.GALACTIC)
        {
            targetXyz = multiply(R_eq_to_gal, eqXyz);
        }
        else
        { // Target is Supergalactic
            double[] galXyz = multiply(R_eq_to_gal, eqXyz);
            targetXyz = multiply(R_gal_to_sg, galXyz);
        }

        return fromCartesianPc(targetSys, targetXyz, targetUnit);
    }

    // Matrix Multiplication helpers
    private double[] multiply(double[][] M, double[] v)
    {
        return new double[]
        {
            M[0][0] * v[0] + M[0][1] * v[1] + M[0][2] * v[2],
            M[1][0] * v[0] + M[1][1] * v[1] + M[1][2] * v[2],
            M[2][0] * v[0] + M[2][1] * v[1] + M[2][2] * v[2]
        };
    }

    private double[] multiplyTranspose(double[][] M, double[] v)
    {
        return new double[]
        {
            M[0][0] * v[0] + M[1][0] * v[1] + M[2][0] * v[2],
            M[0][1] * v[0] + M[1][1] * v[1] + M[2][1] * v[2],
            M[0][2] * v[0] + M[1][2] * v[1] + M[2][2] * v[2]
        };
    }

    @Override
    public String toString()
    {
        return String.format("%s [Lon/RA: %.4f°, Lat/Dec: %.4f°, Dist: %.4f %s]",
                coordSys, lon, lat, distance, distanceUnit);
    }

}
