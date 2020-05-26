package data.lab.ongdb.similarity.simhash;

import org.junit.Test;

import static org.junit.Assert.*;

/*
 *
 * Data Lab - graph database organization.
 *
 */

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.similarity.simhash
 * @Description: TODO
 * @date 2020/5/26 10:11
 */
public class SimHashTest {

    @Test
    public void hash() {
        int dis = SimHash.hamming("0000100011011100010110110000011110110101010000110000111010101111","0000100011011100010110110000011110110101010000110000111010101111");
        System.out.println(dis);
    }
}

