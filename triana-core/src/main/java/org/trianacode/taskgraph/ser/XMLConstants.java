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

package org.trianacode.taskgraph.ser;

/**
 * Class Description Here...
 *
 * @author Andrew Harrison
 * @version $Revision:$
 * @created Jun 7, 2009: 4:12:18 PM
 * @date $Date:$ modified by $Author:$
 */
public interface XMLConstants {

    static final String NS_TRIANA = "http://www.trianacode.org/tool";


    static final String TASKGRAPH_TAG = "taskgraph";
    static final String TASKGRAPH_LAYOUT_TAG = "taskgraphlayout";
    static final String TOOL_TAG = "tool";
    static final String NAME_TAG = "name";
    static final String PACKAGE_TAG = "package";
    static final String INSTANCE_ID_TAG = "instanceID";
    static final String PROXY_TAG = "proxy";
    static final String RENDERING_HINTS_TAG = "renderingHints";
    static final String RENDERING_HINT_TAG = "renderingHint";
    static final String HINT_TAG = "hint";
    static final String PROXY_DEPENDENT_TAG = "proxyDependent";
    static final String DESC_TAG = "description";
    static final String PARAM_LIST_TAG = "parameters";
    static final String DATA_LIST_TAG = "data";
    static final String PARAM_TAG = "param";
    static final String VALUE_TAG = "value";
    static final String BASE64_ENCODED = "base64";
    static final String PARAM_TYPE_TAG = "type";
    static final String SERIALIZER_TAG = "serializer";
    static final String EXTENSIONS_TAG = "extensions";
    static final String TASK_LIST_TAG = "tasks";
    static final String TASK_TAG = "task";
    static final String TOOL_NAME_TAG = "toolname";
    static final String TASK_NAME_TAG = "taskname";
    static final String TASK_ID_TAG = "taskid";
    static final String INPORT_NUM_TAG = "inportnum";
    static final String OUTPORT_NUM_TAG = "outportnum";
    static final String INPARAM_TAG = "inparam";
    static final String OUTPARAM_TAG = "outparam";
    static final String INDEX_TAG = "index";
    static final String TRIGGER_TAG = "trigger";
    static final String INPUT_TAG = "input";
    static final String TYPE_TAG = "type";
    static final String OUTPUT_TAG = "output";
    static final String CONTROL_TASK_TAG = "controltask";
    static final String CONNECTION_LIST_TAG = "connections";
    static final String CONNECTION_TAG = "connection";
    static final String SOURCE_TAG = "source";
    static final String TARGET_TAG = "target";
    static final String NODE_TAG = "node";
    static final String PARAM_NAME_TAG = "paramname";
    static final String GROUP_MAPING_TAG = "groupnodemapping";
    static final String EXTERNAL_NODE_TAG = "externalnode";
    static final String VERSION_TAG = "version";
    static final String DEFAULT_VERSION = "0.1-SNAPSHOT";
    // Deprecated XML tags
    static final String UNIT_NAME_TAG = "unitName";
    static final String UNIT_PACKAGE_TAG = "unitPackage";
    static final String TOOL_CLASSES_TAG = "toolClasses";
    static final String TOOL_CLASS_TAG = "toolClass";
    static final String CLASS_NAME_TAG = "className";

    static final String XML_FILE_SUFFIX = ".xml";

    static String DOC_INDENT_ = "   ";
    static boolean DOC_NEW_LINE_ = true;
    static String TAB = "    ";
    static String NEWLINE = "\n";

    static final String CLASSNAME_TAG = "classname";

}
