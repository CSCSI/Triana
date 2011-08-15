package org.trianacode.enactment.logging;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: 15/08/2011
 * Time: 11:00
 * To change this template use File | Settings | File Templates.
 */
public class ErrorDetail {
    String name;
    String detail;

    public ErrorDetail(String name, String detail) {
        this.name = name;
        this.detail = detail;
    }

    public String getName() {
        return name;
    }

    public String getDetail() {
        return detail;
    }
}
