/*
 * Copyright (c) 2011-2015 Marat Gubaidullin. 
 *
 * This file is part of HYBRIDBPM.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 */
package com.hybridbpm.ui.component.chart.color;

import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.addon.charts.themes.VaadinTheme;
import com.vaadin.shared.ui.colorpicker.Color;

import java.util.Random;

@SuppressWarnings("serial")
public class ColourUtil {
    final static SolidColor[] predefinedColours = {
            (SolidColor)new VaadinTheme().getColors()[0], (SolidColor)new VaadinTheme().getColors()[1], (SolidColor)new VaadinTheme().getColors()[2], (SolidColor)new VaadinTheme().getColors()[3],
            (SolidColor)new VaadinTheme().getColors()[4], (SolidColor)new VaadinTheme().getColors()[5], (SolidColor)new VaadinTheme().getColors()[6], (SolidColor)new VaadinTheme().getColors()[7],
    SolidColor.BLUE,
    SolidColor.BLUEVIOLET,
    SolidColor.BROWN,
    SolidColor.BURLYWOOD,
    SolidColor.CADETBLUE,
    SolidColor.CHARTREUSE,
    SolidColor.CHOCOLATE,
    SolidColor.CORAL,
    SolidColor.CORNFLOWERBLUE,
    SolidColor.CORNSILK,
    SolidColor.CRIMSON,
    SolidColor.CYAN,
    SolidColor.DARKBLUE,
    SolidColor.DARKCYAN,
    SolidColor.DARKGOLDENROD,
    SolidColor.DARKGRAY,
    SolidColor.DARKGREY,
    SolidColor.DARKGREEN,
    SolidColor.DARKKHAKI,
    SolidColor.DARKMAGENTA,
    SolidColor.DARKOLIVEGREEN,
    SolidColor.DARKORANGE,
    SolidColor.DARKORCHID,
    SolidColor.DARKRED,
    SolidColor.DARKSALMON,
    SolidColor.DARKSEAGREEN,
    SolidColor.DARKSLATEBLUE,
    SolidColor.DARKSLATEGRAY,
    SolidColor.DARKSLATEGREY,
    SolidColor.DARKTURQUOISE,
    SolidColor.DARKVIOLET,
    SolidColor.DEEPPINK,
    SolidColor.DEEPSKYBLUE,
    SolidColor.DIMGRAY,
    SolidColor.DIMGREY,
    SolidColor.DODGERBLUE,
    SolidColor.FIREBRICK,
    SolidColor.FORESTGREEN,
    SolidColor.FUCHSIA,
    SolidColor.GAINSBORO,
    SolidColor.GOLD,
    SolidColor.GOLDENROD,
    SolidColor.GREY,
    SolidColor.GREEN,
    SolidColor.GREENYELLOW,
    SolidColor.HONEYDEW,
    SolidColor.HOTPINK,
    SolidColor.INDIANRED,
    SolidColor.INDIGO,
    SolidColor.KHAKI,
    SolidColor.LAVENDER,
    SolidColor.LAVENDERBLUSH,
    SolidColor.LAWNGREEN,
    SolidColor.LEMONCHIFFON,
    SolidColor.LIGHTCORAL,
    SolidColor.LIGHTCYAN,
    SolidColor.LIGHTGOLDENRODYELLOW,
    SolidColor.LIGHTGRAY,
    SolidColor.LIGHTGREY,
    SolidColor.LIGHTGREEN,
    SolidColor.LIGHTPINK,
    SolidColor.LIGHTSALMON,
    SolidColor.LIGHTSEAGREEN,
    SolidColor.LIGHTSKYBLUE,
    SolidColor.LIGHTSLATEGRAY,
    SolidColor.LIGHTSLATEGREY,
    SolidColor.LIGHTSTEELBLUE,
    SolidColor.LIGHTYELLOW,
    SolidColor.LIME,
    SolidColor.LIMEGREEN,
    SolidColor.LINEN,
    SolidColor.MAGENTA,
    SolidColor.MAROON,
    SolidColor.MEDIUMAQUAMARINE,
    SolidColor.MEDIUMBLUE,
    SolidColor.MEDIUMORCHID,
    SolidColor.MEDIUMPURPLE,
    SolidColor.MEDIUMSEAGREEN,
    SolidColor.MEDIUMSLATEBLUE,
    SolidColor.MEDIUMSPRINGGREEN,
    SolidColor.MEDIUMTURQUOISE,
    SolidColor.MEDIUMVIOLETRED,
    SolidColor.MIDNIGHTBLUE,
    SolidColor.MINTCREAM,
    SolidColor.MISTYROSE,
    SolidColor.MOCCASIN,
    SolidColor.NAVY,
    SolidColor.OLDLACE,
    SolidColor.OLIVE,
    SolidColor.OLIVEDRAB,
    SolidColor.ORANGE,
    SolidColor.ORANGERED,
    SolidColor.ORCHID,
    SolidColor.PALEGOLDENROD,
    SolidColor.PALEGREEN,
    SolidColor.PALETURQUOISE,
    SolidColor.PALEVIOLETRED,
    SolidColor.PAPAYAWHIP,
    SolidColor.PEACHPUFF,
    SolidColor.PERU,
    SolidColor.PINK,
    SolidColor.PLUM,
    SolidColor.POWDERBLUE,
    SolidColor.PURPLE,
    SolidColor.RED,
    SolidColor.ROSYBROWN,
    SolidColor.ROYALBLUE,
    SolidColor.SADDLEBROWN,
    SolidColor.SALMON,
    SolidColor.SANDYBROWN,
    SolidColor.SEAGREEN,
    SolidColor.SIENNA,
    SolidColor.SILVER,
    SolidColor.SKYBLUE,
    SolidColor.SLATEBLUE,
    SolidColor.SLATEGRAY,
    SolidColor.SLATEGREY,
    SolidColor.SPRINGGREEN,
    SolidColor.STEELBLUE,
    SolidColor.TAN,
    SolidColor.TEAL,
    SolidColor.THISTLE,
    SolidColor.TOMATO,
    SolidColor.TURQUOISE,
    SolidColor.YELLOW,
    SolidColor.YELLOWGREEN};

    public static SolidColor generateNewColor(int colorIndex){
        Random r = new Random(colorIndex);
        int red = r.nextInt(255);
        int green = r.nextInt(255);
        int blue = r.nextInt(255);

        return new SolidColor(red, green, blue);
    }

    public static int[] decode(String s){
        int[] toReturn = new int[3];
        String cStr = s.toString().substring(1);

        int r = Integer.parseInt(cStr.substring(0, 2), 16);
        int g = Integer.parseInt(cStr.substring(2, 4), 16);
        int b = Integer.parseInt(cStr.substring(4, 6), 16);

        toReturn[0] = r;
        toReturn[1] = g;
        toReturn[2] = b;
        return toReturn;
    }

    public static Color decodeToColorpicker(String s){
        int[] broken = decode(s);

        return new Color(broken[0], broken[1], broken[2]);
    }

    public static SolidColor[] getColourList(){
        return predefinedColours;
    }

    public static SolidColor getNextColour(int i){
        if(i < predefinedColours.length){
            return predefinedColours[i];
        }
        return generateNewColor(i);
    }

}
