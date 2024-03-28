/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.common;

import com.inventory.entity.CFont;
import com.inventory.entity.FontKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Athu Sint
 */
@Slf4j
public class FontUtil {

    public static List<CFont> generateCFonts() {
        List<CFont> cFonts = new ArrayList<>();

        int[] winKeyCodes = {
            33, 34, 35, 38, 39, 42, 44, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 63, 64, 65, 66, 67, 68, 69,
            70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 83, 84, 85, 86, 88, 89, 90, 91, 92, 96, 97, 98, 99, 100, 101,
            102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122,
            123, 123, 124, 126, 161, 162, 163, 164, 165, 166, 167, 168, 169, 170, 172, 178, 180, 185, 189, 190, 193,
            197, 198, 199, 201, 208, 209, 214, 216, 218, 220, 228, 230, 233, 237, 241, 243, 244, 246, 248, 250, 252,
            254
        };

        int[] zawgyiKeyCodes = {
            4109, 4115, 4107, 4123, 4114, 4098, 4122, 4171, 4160, 4161, 4162, 4163, 4164, 4165, 4166, 4167, 4168, 4169,
            4186, 4152, 4170, 4241, 4119, 4224, 4099, 4142, 4239, 4196, 4156, 4150, 4232, 4146, 4147, 4148, 4222, 4223,
            4133, 4111, 4157, 4234, 4245, 4128, 4108, 4244, 4103, 4127, 4175, 4225, 4145, 4120, 4097, 4141, 4116, 4153,
            4139, 4151, 4100, 4155, 4143, 4144, 4140, 4106, 4126, 4101, 4102, 4121, 4154, 4129, 4096, 4124, 4112, 4113,
            4117, 4118, 4135, 4243, 4242, 4226, 4110, 4195, 4131, 4174, 4247, 4212, 4231, 4214, 4193, 4233, 4211, 4205,
            4213, 4207, 4240, 4194, 4218, 4210, 4200, 4219, 4246, 4236, 4201, 4208, 4235, 4105, 4216, 4198, 4217, 4215,
            4173, 4203, 4230, 4229, 4197, 4237, 4192, 4172, 4132
        };

        int[] integraKeyCodes = {
            174, 180, 172, 188, 179, 163, 187, 255, 240, 241, 242, 243, 244, 245, 246, 247, 248, 249, 239, 252, 254, 203,
            184, 175, 164, 227, 216, 8220, 238, 234, 8216, 233, 229, 231, 8218, 195, 220, 176, 251, 338, 212, 193, 173,
            197, 168, 192, 732, 207, 232, 185, 162, 226, 204, 253, 225, 250, 165, 237, 228, 230, 224, 171, 191, 166,
            167, 186, 236, 194, 161, 189, 177, 178, 182, 181, 222, 222, 154, 215, 175, 198, 218, 376, 172, 208, 219,
            210, 196, 144, 339, 202, 209, 173, 223, 197, 214, 207, 201, 215, 208, 8220, 169, 206, 8217, 170, 212, 200,
            213, 211, 8482, 171, 221, 238, 199, 149, 218, 8250, 353
        };

        for (int i = 0; i < winKeyCodes.length; i++) {
            FontKey fontKey = new FontKey();
            fontKey.setWinKeyCode(winKeyCodes[i]);
            fontKey.setZwKeyCode(zawgyiKeyCodes[i]);

            CFont cFont = new CFont();
            cFont.setFontKey(fontKey);
            cFont.setIntCode(integraKeyCodes[i]);

            cFonts.add(cFont);
        }

        return cFonts;
    }

    public static String getZawgyiText(String text, HashMap<Integer, Integer> hmZG) {
        String tmpStr = "";

        if (text != null) {
            for (int i = 0; i < text.length(); i++) {
                String tmpS = Character.toString(text.charAt(i));
                int tmpChar = (int) text.charAt(i);

                if (hmZG.containsKey(tmpChar)) {
                    char tmpc = (char) hmZG.get(tmpChar).intValue();
                    if (tmpStr.isEmpty()) {
                        tmpStr = Character.toString(tmpc);
                    } else {
                        tmpStr = tmpStr + Character.toString(tmpc);
                    }
                } else if (tmpS.equals("ƒ")) {
                    if (tmpStr.isEmpty()) {
                        tmpStr = "ႏ";
                    } else {
                        tmpStr = tmpStr + "ႏ";
                    }
                } else if (tmpStr.isEmpty()) {
                    tmpStr = tmpS;
                } else {
                    tmpStr = tmpStr + tmpS;
                }
            }
        }

        return tmpStr;
    }

}
