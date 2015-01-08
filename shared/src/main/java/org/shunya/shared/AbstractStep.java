package org.shunya.shared;

import org.shunya.shared.annotation.InputParam;
import org.shunya.shared.annotation.OutputParam;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.*;

public abstract class AbstractStep {
    private Map<String, Object> sessionMap;
    private FieldPropertiesMap outputParams;
    private FieldPropertiesMap inputParams;
    private FieldPropertiesMap overrideInputParams;
    protected TaskStepDTO taskStepData;
    private boolean doVariableSubstitution = true;
    protected StringBuilder strLogger;
    private transient ConsoleHandler cHandler = null;
    private transient MemoryHandler mHandler = null;
    private transient Level loggingLevel = Level.FINE;
    private LogListener logListener;

    public static final ThreadLocal<Logger> LOGGER = new ThreadLocal<Logger>() {
        @Override
        protected Logger initialValue() {
            Logger logger = Logger.getLogger("Logger for " + Thread.currentThread().getName());
            return logger;
        }
    };

    public void beforeTaskStart() {
        strLogger = new StringBuilder();
        Handler logHandler = new Handler() {
            public void publish(LogRecord record) {
                //                    String msg = new Date(record.getMillis()) + " [" + record.getLevel() + "] " + record.getMessage();
                strLogger.append(record.getMessage() + "\n");
                if (logListener != null) {
                    logListener.publish(record.getMessage() + "\n");
                }
                System.out.println(record.getMessage());
            }

            @Override
            public void flush() {
            }

            @Override
            public void close() throws SecurityException {
            }
        };
        mHandler = new MemoryHandler(logHandler, 2, loggingLevel);
        cHandler = new ConsoleHandler();
        cHandler.setFormatter(new SimpleFormatter());
        cHandler.setLevel(Level.ALL);
        LOGGER.get().addHandler(mHandler);
//        LOGGER.get().addHandler(cHandler);
        LOGGER.get().setUseParentHandlers(false);
    }

    private void setLoggingLevel(Logger taskLogger) {
        try {
            taskLogger.setLevel(loggingLevel);
        } catch (Exception e) {
            taskLogger.setLevel(Level.INFO);
        }
    }

    public static FieldPropertiesMap listInputParams(Class<? extends Object> task, Map<String, String> values) {
        Field[] fields = task.getDeclaredFields();
        Map<String, FieldProperties> fieldPropertiesMap = new HashMap<>();
        for (Field field : fields) {
            if (field.isAnnotationPresent(InputParam.class)) {
                InputParam ann = field.getAnnotation(InputParam.class);
//				System.out.println(ann.required()==true?"*"+field.getName():""+field.getName());
                fieldPropertiesMap.put(field.getName(), new FieldProperties(field.getName(), ann.displayName(), values.get(field.getName()), ann.description(), ann.required(), ann.type()));
            }
        }
        return new FieldPropertiesMap(fieldPropertiesMap);
    }

    public static FieldPropertiesMap listOutputParams(Class<? extends Object> task, Map<String, String> values) {
        Field[] fields = task.getDeclaredFields();
//		System.out.println("Listing output params");
        Map<String, FieldProperties> fieldPropertiesMap = new HashMap<>();
        for (Field field : fields) {
            if (field.isAnnotationPresent(OutputParam.class)) {
                OutputParam ann = field.getAnnotation(OutputParam.class);
                fieldPropertiesMap.put(field.getName(), new FieldProperties(field.getName(), ann.displayName(), values.get(field.getName()), "", false, ann.type()));
            }
        }
        return new FieldPropertiesMap(fieldPropertiesMap);
    }

    public static AbstractStep getTask(TaskStepDTO stepDTO) {
        try {
            Class<?> clz = Class.forName(stepDTO.getTaskClass());
            AbstractStep task = (AbstractStep) clz.newInstance();
            task.setOutputParams(FieldPropertiesMap.parseStringMap(stepDTO.getOutputParamsMap()));
            task.setInputParams(FieldPropertiesMap.parseStringMap(stepDTO.getInputParamsMap()));
            return task;
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public abstract boolean run();

    public boolean execute() throws Exception {
        substituteParams();
        boolean status = run();
        substituteResult();
        return status;
    }

    private void substituteParams() throws Exception {
        substituteParams(getInputParams());
        if (getOverrideInputParams() != null)
            substituteParams(getOverrideInputParams());
    }

    private void substituteParams(FieldPropertiesMap inputParams) throws Exception {
        Field[] fields = getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(InputParam.class)) {
                try {
                    field.setAccessible(true);
                    if (inputParams.get(field.getName()) != null) {
                        String fieldValue = inputParams.get(field.getName()).getValue();
                        if (fieldValue.length() >= 1) {
                            if (fieldValue.startsWith("$")) {
                                fieldValue = fieldValue.substring(1);
                                fieldValue = (String) getSessionObject(fieldValue);
                            }
                            if (doVariableSubstitution && fieldValue.contains("#{")) {
                                fieldValue = substituteVariables(fieldValue, sessionMap);
                            }
                            if (field.getType().getSimpleName().equals("String")) {
                                field.set(this, fieldValue);
                            } else if (field.getType().getSimpleName().equals("int")) {
                                int tmp = Integer.parseInt(fieldValue);
                                field.set(this, tmp);
                            } else if (field.getType().getSimpleName().equals("Date")) {
                                SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
                                field.set(this, sdf.parse(fieldValue));
                            } else if (field.getType().getSimpleName().equals("double")) {
                                double tmp = Double.parseDouble(fieldValue);
                                field.set(this, tmp);
                            } else if (field.getType().getSimpleName().equals("boolean")) {
                                boolean tmp = Boolean.parseBoolean(fieldValue);
                                field.set(this, tmp);
                            }
                        }
                    }
                } catch (IllegalArgumentException | IllegalAccessException | ParseException e) {
                    e.printStackTrace();
                    LOGGER.get().severe(e.toString());
                    throw e;
                }
            }
        }
    }

    public void substituteResult() {
        Field[] fields = getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(OutputParam.class)) {
                try {
                    field.setAccessible(true);
                    Object value = field.get(this);
                    if (outputParams.get(field.getName()) != null) {
                        sessionMap.put(outputParams.get(field.getName()).getValue(), value);
                        LOGGER.get().info(() -> field.getName() + " bound to " + outputParams.get(field.getName()).getValue() + " == " + value);
                    }
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String substituteVariables(String inputString, Map<String, Object> variables) {
        List<String> vars = getVariablesFromString(inputString);
        for (String key : vars) {
            String variable = "#{" + key + "}";
            if (null != System.getenv(key)) {
                String variableBinding = System.getenv(key);
                inputString = inputString.replace(variable, variableBinding);
            } else if (null != variables.get(key)) {
                String variableBinding = variables.get(key).toString();
                inputString = inputString.replace(variable, variableBinding);
            } else if (null != System.getProperty(key)) {
                String variableBinding = System.getProperty(key);
                inputString = inputString.replace(variable, variableBinding);
            } else {
                LOGGER.get().severe(() -> "Variable Binding not Found :" + variable + " TaskStepName : " + taskStepData.getName());
                throw new RuntimeException("Variable Binding not Found :" + variable + " TaskStepName : " + taskStepData.getName());
            }
        }
        return inputString;
    }

    private static List<String> getVariablesFromString(String test) {
        char prevChar = ' ';
        String var = "";
        List<String> vars = new ArrayList<>();
        boolean found = false;
        for (int i = 0; i < test.length(); i++) {
            char ch = test.charAt(i);
            if (ch == '{' && prevChar == '#') {
                var = "";
                found = true;
            } else if (ch == '}') {
                found = false;
                if (!var.isEmpty())
                    vars.add(var);
                var = "";
            } else if (found) {
                var += ch;
            }
            prevChar = ch;
        }
        return vars;
    }

    public String getMemoryLogs() {
        if (strLogger != null)
            return strLogger.toString();
        return "";
    }

    protected void loadSessionVariables(Map<String, String> variableMap) {
        sessionMap.putAll(variableMap);
    }

    public void afterTaskFinish() {
        LOGGER.get().removeHandler(mHandler);
        LOGGER.get().removeHandler(cHandler);
    }

    public Map<String, Object> getSessionMap() {
        return sessionMap;
    }

    public void setSessionMap(Map<String, Object> sessionMap) {
        this.sessionMap = sessionMap;
    }

    public FieldPropertiesMap getOutputParams() {
        return outputParams;
    }

    public void setOutputParams(FieldPropertiesMap outputParams) {
        this.outputParams = outputParams;
    }

    public FieldPropertiesMap getInputParams() {
        return inputParams;
    }

    public void setInputParams(FieldPropertiesMap inputParams) {
        this.inputParams = inputParams;
    }

    public FieldPropertiesMap getOverrideInputParams() {
        return overrideInputParams;
    }

    public void setOverrideInputParams(FieldPropertiesMap overrideInputParams) {
        this.overrideInputParams = overrideInputParams;
    }

    public Object getSessionObject(String key) {
        return sessionMap.get(key);
    }

    public void setSessionObject(String key, Object obj) {
        sessionMap.put(key, obj);
    }

    public void setTaskStepData(TaskStepDTO taskStepData) {
        this.taskStepData = taskStepData;
    }

    public TaskStepDTO getTaskStepData() {
        return taskStepData;
    }

    public void addLogListener(LogListener logListener) {
        this.logListener = logListener;
    }

    public void removeLogListener(LogListener logListener) {
        this.logListener = null;
    }
}
