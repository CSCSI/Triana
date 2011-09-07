package org.trianacode.enactment.logging.stampede;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: 05/09/2011
 * Time: 10:30
 * To change this template use File | Settings | File Templates.
 */
public interface StampedeLoggerInterface {

    public void error(StampedeEvent o);

    public void info(StampedeEvent o);

    public void warn(StampedeEvent o);

    public void debug(StampedeEvent o);

    public void trace(StampedeEvent o);

    public void fatal(StampedeEvent o);
}
