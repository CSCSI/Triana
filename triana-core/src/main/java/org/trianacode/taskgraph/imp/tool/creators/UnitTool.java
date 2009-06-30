/*
 * Copyright 2004 - 2009 University of Cardiff.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.trianacode.taskgraph.imp.tool.creators;

import org.trianacode.taskgraph.TaskException;
import org.trianacode.taskgraph.Unit;
import org.trianacode.taskgraph.imp.ToolImp;
import org.trianacode.taskgraph.proxy.Proxy;
import org.trianacode.taskgraph.proxy.java.JavaProxy;


/**
 * Class Description Here...
 *
 * @author Andrew Harrison
 * @version $Revision:$
 * @created Jun 28, 2009: 2:30:00 PM
 * @date $Date:$ modified by $Author:$
 */

public class UnitTool extends ToolImp {


    private Unit unit;

    private String version = "2.0";

    private JavaProxy proxy;

    public UnitTool(Unit unit) {
        this.unit = unit;
        this.proxy = new JavaProxy(getToolName(), getToolPackage());
        this.proxy.setUnit(this.unit);
        setDataInputTypes(this.unit.getInputTypes());
        setDataOutputTypes(this.unit.getOutputTypes());
        String[][] inTypes = this.unit.getNodeInputTypes();
        if(inTypes != null) {
            for(int i = 0; i < inTypes.length; i++) {
                String[] inType = inTypes[i];
                if(inType != null && inType.length > 0) {
                    setDataInputTypes(i, inType);
                }
            }
        }
        String[][] outTypes = this.unit.getNodeOutputTypes();
        if (outTypes != null) {
            for (int i = 0; i < outTypes.length; i++) {
                String[] inType = outTypes[i];
                if (inType != null && inType.length > 0) {
                    setDataOutputTypes(i, inType);
                }
            }
        }
        
    }



    public String getVersion() {
        return version;
    }

    /**
     * @return the name of the unit defined by this Tool
     */
    public String getToolName() {
        return unit.getToolName();
    }

    /**
     * Used to set the tool name of this Tool.
     */
    public void setToolName(String toolName) {

    }

    /**
     * @return a Java style package name for this tool in the form [package].[package].
     *         i.e. Common.Input
     */
    public String getToolPackage() {
        return unit.getToolPackage();
    }

    /**
     * Set the package name for this tool.
     */
    public void setToolPackage(String packageName) {

    }

    /**
     * @return the proxies represented by this tool
     */
    public Proxy getProxy() {
        return proxy;
    }

    /**
     * Adds a proxy this tool
     * can't change the proxy, sorry
     */
    public void setProxy(Proxy proxy) throws TaskException {

    }

    /**
     * Removes the proxy for this tool
     * can't do it, sorry
     */
    public void removeProxy() throws TaskException {

    }

    public String toString() {
        return getToolName();
    }

    


}
