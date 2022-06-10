/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Lenovo
 */
public class FontUtil {

    public static String zg12uni51(String input_text) {
        String output_text = input_text;
        output_text = output_text.replaceAll("\\u106a", "\u1009");
        output_text = output_text.replaceAll("\\u1025(?=[\\u1039\\u102c])", "\u1009");
        output_text = output_text.replaceAll("\\u1025\\u102e", "\u1026");
        output_text = output_text.replaceAll("\\u106b", "\u100a");
        output_text = output_text.replaceAll("\\u1090", "\u101b");
        output_text = output_text.replaceAll("\\u1040", "\u1040");
        output_text = output_text.replaceAll("\\u108f", "\u1014");
        output_text = output_text.replaceAll("\\u1012", "\u1012");
        output_text = output_text.replaceAll("\\u1013", "\u1013");
        output_text = output_text.replaceAll("[\\u103d\\u1087]", "\u103e");
        output_text = output_text.replaceAll("\\u103c", "\u103d");
        output_text = output_text.replaceAll("[\\u103b\\u107e\\u107f\\u1080\\u1081\\u1082\\u1083\\u1084]", "\u103c");
        output_text = output_text.replaceAll("[\\u103a\\u107d]", "\u103b");
        output_text = output_text.replaceAll("\\u103d\\u103b", "\u103b\u103d");
        output_text = output_text.replaceAll("\\u108a", "\u103d\u103d");
        output_text = output_text.replaceAll("\\u103d\\u103d", "\u103d\u103d");
        output_text = output_text.replaceAll("((?:\\u1031)?)((?:\\u103c)?)([\\u1000-\\u1021])\\u1064", "\u1064$1$2$3");
        output_text = output_text.replaceAll("((?:\\u1031)?)((?:\\u103c)?)([\\u1000-\\u1021])\\u108b", "\u1064$1$2$3\u102d");
        output_text = output_text.replaceAll("((?:\\u1031)?)((?:\\u103c)?)([\\u1000-\\u1021])\\u108c", "\u1064$1$2$3\u102e");
        output_text = output_text.replaceAll("((?:\\u1031)?)((?:\\u103c)?)([\\u1000-\\u1021])\\u108d", "\u1064$1$2$3\u1036");
        output_text = output_text.replaceAll("\\u105a", "\u102b\u103a");
        output_text = output_text.replaceAll("\\u108e", "\u102d\u1036");
        output_text = output_text.replaceAll("\\u1033", "\u102f");
        output_text = output_text.replaceAll("\\u1034", "\u1030");
        output_text = output_text.replaceAll("\\u1088", "\u103d\u102f");
        output_text = output_text.replaceAll("\\u1089", "\u103d\u1030");
        output_text = output_text.replaceAll("\\u1039", "\u103a");
        output_text = output_text.replaceAll("[\\u1094\\u1095]", "\u1037");
        output_text = output_text.replaceAll("([\\u1000-\\u1021])([\\u102c\\u102d\\u102e\\u1032\\u1036]){1,2}([\\u1060\\u1061\\u1062\\u1063\\u1065\\u1066\\u1067\\u1068\\u1069\\u1070\\u1071\\u1072\\u1073\\u1074\\u1075\\u1076\\u1077\\u1078\\u1079\\u107a\\u107b\\u107c\\u1085])", "$1$3$2");
        output_text = output_text.replaceAll("\\u1064", "\u1004\u103a\u1039");
        output_text = output_text.replaceAll("\\u104e", "\u104e\u1004\u103a\u1038");
        output_text = output_text.replaceAll("\\u1086", "\u103f");
        output_text = output_text.replaceAll("\\u1060", "\u1039\u1000");
        output_text = output_text.replaceAll("\\u1061", "\u1039\u1001");
        output_text = output_text.replaceAll("\\u1062", "\u1039\u1002");
        output_text = output_text.replaceAll("\\u1063", "\u1039\u1003");
        output_text = output_text.replaceAll("\\u1065", "\u1039\u1005");
        output_text = output_text.replaceAll("[\\u1066\\u1067]", "\u1039\u1006");
        output_text = output_text.replaceAll("\\u1068", "\u1039\u1007");
        output_text = output_text.replaceAll("\\u1069", "\u1039\u1008");
        output_text = output_text.replaceAll("\\u106c", "\u1039\u100b");
        output_text = output_text.replaceAll("\\u1070", "\u1039\u100f");
        output_text = output_text.replaceAll("[\\u1071\\u1072]", "\u1039\u1010");
        output_text = output_text.replaceAll("[\\u1073\\u1074]", "\u1039\u1011");
        output_text = output_text.replaceAll("\\u1075", "\u1039\u1012");
        output_text = output_text.replaceAll("\\u1076", "\u1039\u1013");
        output_text = output_text.replaceAll("\\u1077", "\u1039\u1014");
        output_text = output_text.replaceAll("\\u1078", "\u1039\u1015");
        output_text = output_text.replaceAll("\\u1079", "\u1039\u1016");
        output_text = output_text.replaceAll("\\u107a", "\u1039\u1017");
        output_text = output_text.replaceAll("\\u107b", "\u1039\u1018");
        output_text = output_text.replaceAll("\\u107c", "\u1039\u1019");
        output_text = output_text.replaceAll("\\u1085", "\u1039\u101c");
        output_text = output_text.replaceAll("\\u106d", "\u1039\u100c");
        output_text = output_text.replaceAll("\\u1091", "\u100f\u1039\u100d");
        output_text = output_text.replaceAll("\\u1092", "\u100b\u1039\u100c");
        output_text = output_text.replaceAll("\\u1097", "\u100b\u1039\u100b");
        output_text = output_text.replaceAll("\\u106f", "\u100e\u1039\u100d");
        output_text = output_text.replaceAll("\\u106e", "\u100d\u1039\u100d");
        output_text = output_text.replaceAll("(\\u103c)([\\u1000-\\u1021])((?:\\u1039[\\u1000-\\u1021])?)", "$2$3$1");
        output_text = output_text.replaceAll("(\\u103d)(\\u103d)([\\u103b\\u103c])", "$3$2$1");
        output_text = output_text.replaceAll("(\\u103d)([\\u103b\\u103c])", "$2$1");
        output_text = output_text.replaceAll("(\\u103d)([\\u103b\\u103c])", "$2$1");
        output_text = output_text.replaceAll("(?<=([\\u1000-\\u101c\\u101e-\\u102a\\u102c\\u102e-\\u103d\\u104c-\\u109f]))(\\u1040)(?=\\s)?", "\u101d");
        output_text = output_text.replaceAll("(?<=(\\u101d))(\\u1040)(?=\\s)?", "\u101d");
        output_text = output_text.replaceAll("(?<=([\\u1000-\\u101c\\u101e-\\u102a\\u102c\\u102e-\\u103d\\u104c-\\u109f\\s]))(\\u1047)", "\u101b");
        output_text = output_text.replaceAll("(\\u1047)(?=[\\u1000-\\u101c\\u101e-\\u102a\\u102c\\u102e-\\u103d\\u104c-\\u109f\\s])", "\u101b");
        output_text = output_text.replaceAll("((?:\\u1031)?)([\\u1000-\\u1021])((?:\\u1039[\\u1000-\\u1021])?)((?:[\\u102d\\u102e\\u1032])?)([\\u1036\\u1037\\u1038]{0,2})([\\u103b-\\u103d]{0,3})((?:[\\u102f\\u1030])?)([\\u1036\\u1037\\u1038]{0,2})((?:[\\u102d\\u102e\\u1032])?)", "$2$3$6$1$4$9$7$5$8");
        output_text = output_text.replaceAll("\\u1036\\u102f", "\u102f\u1036");
        output_text = output_text.replaceAll("(\\u103a)(\\u1037)", "$2$1");
        return output_text;
    }

    public static void main(String[] args) throws IOException {
        try {
            File ex = new File("C:\\Users\\Lenovo\\Documents\\dumps\\cv_inv_sanhe_c.sql");
            FileWriter writer = new FileWriter(ex);
            File file = new File("C:\\Users\\Lenovo\\Documents\\dumps\\cv_inv_sanhe.sql");
            Scanner s = new Scanner(file);
            while (s.hasNextLine()) {
                String data = zg12uni51(s.nextLine());
                writer.write(data);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FontUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
