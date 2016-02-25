package pl.mkapiczynski.dron.helpers;

public enum GeoidCorrectionKind {
    /** Elevation values remain unchanged */
    None,
    /** Automatic correction by geoid lookup table */
    Auto,
    /** Fixed value */
    Fixed
}
