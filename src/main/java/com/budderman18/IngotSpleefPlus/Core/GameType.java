package com.budderman18.IngotSpleefPlus.Core;

/**
 *
 * This enum declares the 3 different game types for SpleefPLuis
 * 
 */
public enum GameType {
    /**
     * 
     * This enum declares a spleef type
     * 
     */ 
    SPLEEF(),
    /**
     * 
     * This enum declares a splegg type
     * 
     */
    SPLEGG(),
    /**
     * 
     * This enum declares a tntspleef type 
     *
     */
    TNTSPLEEF();
    /**
     * 
     * This method gets the given type from a string
     * 
     * @param string The string to read
     * @return The type found, if there is one.
     */
    public static GameType getFromString(String string) {
        //check for skyfall
        if (string.equalsIgnoreCase("spleef")) {
            return SPLEEF;
        }
        //check for delivery
        else if (string.equalsIgnoreCase("splegg")) {
            return SPLEGG;
        }
        //check for beacon
        else if (string.equalsIgnoreCase("tntspleef")) {
            return TNTSPLEEF;
        }
        return null;
    }
}
