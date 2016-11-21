package com.willer;

import com.willer.wc.WordCount;
import com.willer.weather.MaxTemperature;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(AppTest.class);
    }

    public void testMaxTemperature() {
        try {
            MaxTemperature.main("/Users/Hack/lab/input/", "/Users/Hack/lab/output");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testWordCount() {
        try {
            WordCount.main("/Users/Hack/lab/input/", "/Users/Hack/lab/output");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
