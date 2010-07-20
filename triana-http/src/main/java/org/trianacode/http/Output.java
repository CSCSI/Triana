package org.trianacode.http;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Map;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Jul 20, 2010
 */

public class Output {

    static {
        try {
            Velocity.init("velocity.properties");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void output(Map<String, Object> parameters, String templatePath, OutputStream out) {
        VelocityContext context = new VelocityContext();
        for (String s : parameters.keySet()) {
            context.put(s, parameters.get(s));
        }
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
            Template template = Velocity.getTemplate(templatePath);
            template.merge(context, writer);
            writer.flush();
        }
        catch (ResourceNotFoundException rnfe) {
            System.out.println("Example : error : cannot find template " + templatePath);
        }
        catch (ParseErrorException pee) {
            System.out.println("Example : Syntax error in template " + templatePath + ":" + pee);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
