package org.shunya.shared.taskSteps;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by munichan on 3/26/2015.
 */
public class SFTPStepTest {

    @Test
    public void testRun() throws Exception {
        SFTPStep sftpStep = new SFTPStep();
        sftpStep.putFile("admin", "10.66.4.32", "admin", "/munish/test.txt", "C:\\Users\\munichan\\Desktop\\rsa-priv");
    }
}