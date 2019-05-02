package at.fhv.jn.googleMaps;

import javafx.scene.paint.Color;

/**
 * Created by Johannes on 01.02.2017.
 */
public class ColorUtil {


    public static String ConvertToRGBString(Color color)
    {
        double red =  color.getRed();
        double green=  color.getGreen();
        double blue=  color.getBlue();

        String hex = "#"+to2DigitHex(red)+to2DigitHex(green)+to2DigitHex(blue);
        return hex;
    }

    private static String to2DigitHex(double value){
        int intValue =(int)(value*255);
        String hex =Integer.toHexString(intValue);
        if(hex.length() == 1){
            hex = "0"+hex;
        }
        return hex;
    }

}
