package org.shunya.shared.taskSteps;

import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipOutputStream;

import static org.junit.Assert.*;

/**
 * Created by munichan on 4/10/2015.
 */
public class ZipStepTest {

    @Test
    @Ignore
    public void testRun2() throws Exception {
        ZipStep zipStep = new ZipStep();
        try {
//            zipStep.generateFileList("C:\\mfg\\test",  new File("C:\\mfg\\test"));
            zipStep.zipIt("C:\\mfg\\output-2.zip", "C:\\mfg\\test", 2);
            System.out.println("Program ended successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}