package org.shunya.shared.taskSteps;

import org.shunya.shared.AbstractStep;
import org.shunya.shared.StringUtils;
import org.shunya.shared.annotation.InputParam;
import org.shunya.shared.annotation.OutputParam;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Scanner;
import java.util.logging.Level;

public class UpdateDBStep extends AbstractStep {
    @InputParam(required = true, type = "text", description = "jdbc:jtds:sqlserver://10.66.4.47/test;sendStringParametersAsUnicode=false")
    private String conURL;
    @InputParam(required = false, type = "text", description = "username")
    private String username;
    @InputParam(required = false, type = "text", description = "password")
    private String password;
    @InputParam(required = false,  type = "textarea", displayName = "Line Delimited SQL Strings", description = "sql strings line delimited")
    private String sql;
    @OutputParam
    private String output = "";

    @Override
    public boolean run() {
        boolean status = false;
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            Connection conn = DriverManager.getConnection(conURL, username, password);
            conn.setReadOnly(false);
            LOGGER.get().log(Level.INFO, "Connected to DB");
            Scanner stk = new Scanner(sql).useDelimiter("\r\n|\n\r|\r|\n");
            while (stk.hasNext()) {
                String sqlLine = stk.next().trim();
                Statement s = conn.createStatement();
                {
                    s.setQueryTimeout(2 * 60);
                    output += "executing statement : " + sqlLine + "\n";
                    LOGGER.get().log(Level.INFO, "executing statement : " + sqlLine);
                    int count = s.executeUpdate(sqlLine);
                    s.close();
                    output += "records affected :" + count + "\n";
                    LOGGER.get().log(Level.INFO, "records affected :" + count);
                }
            }
            conn.close();
            LOGGER.get().log(Level.INFO, "Connection to DB Closed.");
            status = true;
        } catch (Exception ee) {
            LOGGER.get().log(Level.SEVERE, "Exception occurred " + StringUtils.getExceptionStackTrace(ee));
        }
        return status;
    }
}
