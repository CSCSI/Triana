package org.trianacode.enactment.logging.stampede;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: 15/08/2011
 * Time: 10:15
 * To change this template use File | Settings | File Templates.
 */
public class StampedeLogger implements Log, StampedeLoggerInterface {
    private static StampedeLogger stampedeLogger = new StampedeLogger();
    private static Log log4j;

    public static StampedeLogger getLog() {
        log4j = LogFactory.getLog("TRIANA.STAMPEDE");
        return stampedeLogger;
    }

    @Override
    public boolean isDebugEnabled() {
        return log4j.isDebugEnabled();
    }

    @Override
    public boolean isErrorEnabled() {
        return log4j.isErrorEnabled();
    }

    @Override
    public boolean isFatalEnabled() {
        return log4j.isFatalEnabled();
    }

    @Override
    public boolean isInfoEnabled() {
        return log4j.isInfoEnabled();
    }

    @Override
    public boolean isTraceEnabled() {
        return log4j.isTraceEnabled();
    }

    @Override
    public boolean isWarnEnabled() {
        return log4j.isWarnEnabled();
    }

    public void trace(StampedeEvent o) {
        log4j.trace(o);
    }

    public void debug(StampedeEvent o) {
        log4j.debug(o);
    }

    public void info(StampedeEvent o) {
        log4j.info(o);
    }

    public void warn(StampedeEvent o) {
        log4j.warn(o);
    }

    public void error(StampedeEvent o) {
        log4j.error(o);
    }

    public void fatal(StampedeEvent o) {
        log4j.fatal(o);
    }

    @Override
    public void trace(Object o) {
        log4j.trace(o);
    }

    @Override
    public void trace(Object o, Throwable throwable) {
        log4j.trace(o, throwable);
    }

    @Override
    public void debug(Object o) {
        log4j.debug(o);
    }

    @Override
    public void debug(Object o, Throwable throwable) {
        log4j.debug(o, throwable);
    }

    @Override
    public void info(Object o) {
        log4j.info(o);
    }

    @Override
    public void info(Object o, Throwable throwable) {
        log4j.info(o, throwable);
    }

    @Override
    public void warn(Object o) {
        log4j.warn(o);
    }

    @Override
    public void warn(Object o, Throwable throwable) {
        log4j.warn(o, throwable);
    }

    @Override
    public void error(Object o) {
        log4j.error(o);
    }

    @Override
    public void error(Object o, Throwable throwable) {
        log4j.error(o, throwable);
    }

    @Override
    public void fatal(Object o) {
        log4j.fatal(o);
    }

    @Override
    public void fatal(Object o, Throwable throwable) {
        log4j.fatal(o, throwable);
    }
}
