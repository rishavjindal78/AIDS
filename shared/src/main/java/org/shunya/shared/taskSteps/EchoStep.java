package org.shunya.shared.taskSteps;

import org.shunya.shared.AbstractStep;
import org.shunya.shared.annotation.InputParam;
import org.shunya.shared.annotation.OutputParam;
import org.shunya.shared.annotation.PunterTask;

import java.util.logging.Level;

@PunterTask(author = "munishc", name = "EchoTaskStep", description = "Echo's the input data to SOP", documentation = "docs/TextSamplerDemoHelp.html")
public class EchoStep extends AbstractStep {
    @InputParam(required = true, displayName = "Name", type = "input", description = "enter your name here")
    private String name;

    @OutputParam(displayName = "Out Name")
    private String outName;

    @Override
    public boolean run() {
        outName = "Hello " + name;
        LOGGER.get().log(Level.INFO, outName);
        return true;
    }
}