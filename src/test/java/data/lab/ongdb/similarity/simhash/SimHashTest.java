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
        int dis = SimHash.hamming("1010111110111100110011010100110010000110101111010001000011100011","1010111110010100101100010100110010000110000111010000101000101111");
        System.out.println(dis);
    }
}