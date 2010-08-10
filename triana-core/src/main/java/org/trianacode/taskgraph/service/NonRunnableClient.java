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

package org.trianacode.taskgraph.service;

import org.trianacode.taskgraph.clipin.HistoryClipIn;
import org.trianacode.taskgraph.tool.ToolTable;

/**
 * Class Description Here...
 *
 * @author Andrew Harrison
 * @version $Revision:$
 */

public class NonRunnableClient implements TrianaClient {


    private ToolTable tools;


    public NonRunnableClient(ToolTable tools) {
        this.tools = tools;
    }


    /**
     * @return a table of the available tools
     */
    public ToolTable getToolTable() {
        return tools;
    }


    /**
     * Sends a message to the sever to run the taskgraph.
     */
    public void run() throws ClientException {
        throw (new ClientException("Client Error: Cannot run non-executable taskgraph"));
    }

    /**
     * Sends a message to the sever to run the taskgraph. The specfied history clip-ins is attached to every input task
     */
    public void run(HistoryClipIn history) throws ClientException {
        throw (new ClientException("Client Error: Cannot run non-executable taskgraph"));
    }

    /**
     * Sends a message to the server to stop running the taskgraph.
     */
    public void pause() throws ClientException {
    }

    /**
     * Sends a message to the server to reset the taskgraph.
     */
    public void reset() throws ClientException {
    }

    /**
     * Sends a message to the server to flush the taskgraph
     */
    public void flush() throws ClientException {
    }


    /**
     * Dispose of the client, cleaning up server connections
     */
    public void dispose() throws ClientException {
    }

}
