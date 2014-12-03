package org.shunya.shared.taskSteps;

import org.shunya.shared.AbstractStep;
import org.shunya.shared.StringUtils;
import org.shunya.shared.annotation.InputParam;
import org.shunya.shared.annotation.OutputParam;
import org.shunya.shared.annotation.PunterTask;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;

@PunterTask(author="munishc",name="DeclareVariablesStep",description="Load process properties into the system.",documentation= "src/main/resources/docs/TextSamplerDemoHelp.html")
public class DeclareVariableStep extends AbstractStep {
	private static final long serialVersionUID = 1L;

    @InputParam(required = true, displayName = "Key Value Pairs", type = "textarea", description = "Propertries")
	private String properties;

    @OutputParam(displayName = "outName")
	private String outName;

	@Override
	public boolean run() {
		boolean status=false;
		try {
			LOGGER.get().log(Level.FINE, "Loading properties into process.");
			InputStream is = new ByteArrayInputStream(properties.getBytes());  
	        Properties prop = new Properties();  
	        prop.load(is);  
			super.loadSessionVariables((Map) prop);
		} catch (Exception e) {
			LOGGER.get().log(Level.WARNING, StringUtils.getExceptionStackTrace(e));
			e.printStackTrace();
		}
		LOGGER.get().log(Level.FINE, "Properties loaded successfully into Process.");
		status=true;
		outName="success";
		return status;
	}
}