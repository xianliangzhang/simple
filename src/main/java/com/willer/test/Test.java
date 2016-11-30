package com.willer.test;

import com.willer.common.ConfigHelper;
import com.willer.common.DESHelper;
import org.apache.commons.codec.binary.Hex;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by Hack on 2016/11/30.
 */
public class Test {
    private static final String komeX = ConfigHelper.get("KOME_X");
    private static final String komeY = ConfigHelper.get("KOME_Y");

    public static void main(String[] args) throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        Connection conn = DriverManager.getConnection("jdbc:mysql://www.kome.sexy:8306/kome", komeX, komeY);
        System.out.println(conn);
    }
}
