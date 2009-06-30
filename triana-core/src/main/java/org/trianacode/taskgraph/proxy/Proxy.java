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

package org.trianacode.taskgraph.proxy;

import java.util.Map;

/**
 * Class Description Here...
 *
 * @author Andrew Harrison
 * @version $Revision:$
 * @created Jun 6, 2009: 8:06:33 PM
 * @date $Date:$ modified by $Author:$
 */

public interface Proxy {

    // the default instance detail key (used for backward compatibility)
    public static final String DEFAULT_INSTANCE_DETAIL = "Default";


    /**
     * @return the type of the proxy
     */
    public String getType();


    /**
     * @return a map of the instance details for this proxy
     */
    public Map<String, Object> getInstanceDetails();

}
