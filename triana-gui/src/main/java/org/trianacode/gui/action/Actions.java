/*
 * The University of Wales, Cardiff Triana Project Software License (Based
 * on the Apache Software License Version 1.1)
 *
 * Copyright (c) 2007 University of Wales, Cardiff. All rights reserved.
 *
 * Redistribution and use of the software in source and binary forms, with
 * or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1.  Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 * 2.  Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 * 3. The end-user documentation included with the redistribution, if any,
 *    must include the following acknowledgment: "This product includes
 *    software developed by the University of Wales, Cardiff for the Triana
 *    Project (http://www.trianacode.org)." Alternately, this
 *    acknowledgment may appear in the software itself, if and wherever
 *    such third-party acknowledgments normally appear.
 *
 * 4. The names "Triana" and "University of Wales, Cardiff" must not be
 *    used to endorse or promote products derived from this software
 *    without prior written permission. For written permission, please
 *    contact triana@trianacode.org.
 *
 * 5. Products derived from this software may not be called "Triana," nor
 *    may Triana appear in their name, without prior written permission of
 *    the University of Wales, Cardiff.
 *
 * 6. This software may not be sold, used or incorporated into any product
 *    for sale to third parties.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN
 * NO EVENT SHALL UNIVERSITY OF WALES, CARDIFF OR ITS CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 *
 * ------------------------------------------------------------------------
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Triana Project. For more information on the
 * Triana Project, please see. http://www.trianacode.org.
 *
 * This license is based on the BSD license as adopted by the Apache
 * Foundation and is governed by the laws of England and Wales.
 *
 */

package org.trianacode.gui.action;

/**
 * An interface defining all the action keys for the action table
 *
 * @author      Ian Wang
 * @created     22nd June 2004
 * @version     $Revision: 4051 $
 * @date        $Date: 2007-10-31 17:51:40 +0000 (Wed, 31 Oct 2007) $ modified by $Author: spxmss $

 */

public interface Actions {

    public static final String NEW_ACTION = "new";
    public static final String OPEN_ACTION = "open";
    public static final String OPEN_FILE_ACTION = "openFile";
    public static final String SAVE_ACTION = "save";
    public static final String SAVE_AS_ACTION = "saveAs";
    public static final String CLOSE_ACTION = "close";
    public static final String FIND_ACTION = "find";
    public static final String IMPORT_ACTION = "import";
    public static final String EXPORT_ACTION = "export";
    public static final String PRINT_ACTION = "print";
    public static final String HELP_ACTION = "help";
    public static final String RENDER_ACTION = "render";

    public static final String CUT_ACTION = "cut";
    public static final String COPY_ACTION = "copy";
    public static final String PASTE_ACTION = "paste";
    public static final String PASTE_INTO_ACTION = "pasteInto";
    public static final String DELETE_ACTION = "delete";
    public static final String RENAME_ACTION = "rename";

    public static final String PROERTIES_ACTION = "properties";
    public static final String CONTROL_PROERTIES_ACTION = "controlProperties";
    public static final String NODE_EDITOR_ACTION = "nodeEditor";
    public static final String HISTORY_TRACKING_ACTION = "historyTracking";

    public static final String GROUP_ACTION = "group";
    public static final String UNGROUP_ACTION = "ungroup";
    public static final String SELECT_ALL_ACTION = "selectAll";
    public static final String CLEAR_ACTION = "clear";
    public static final String ORGANIZE_ACTION = "organize";

    public static final String ZOOMIN_ACTION = "zoomIn";
    public static final String ZOOMOUT_ACTION = "zoomOut";

    public static final String RUN_ACTION = "run";
    public static final String RUN_HISTORY_ACTION = "runHistory";
    public static final String PAUSE_ACTION = "pause";
    public static final String RESET_ACTION = "reset";
    public static final String FLUSH_ACTION = "flush";

    public static final String COMPILE_ACTION = "compile";
    public static final String EDIT_DESC_ACTION = "editDesc";
    public static final String EDIT_HTML_ACTION = "editHTML";
    public static final String EDIT_SOURCE_ACTION = "editSource";
    public static final String EDIT_XML_ACTION = "editXML";
    public static final String EDIT_GUI_ACTION = "editGUI";

    public static final String CREATE_SERVICE_ACTION = "createService";
    public static final String RETRACT_SERVICE_ACTION = "retractService";
    public static final String RETRACT_GROUP_ACTION = "retractGroup";
    public static final String IMPORT_SERVICE_ACTION = "importService";
    public static final String DISCOVER_SERVICES_ACTION = "discoverServices";
    public static final String DISTRIBUTE_PROTOSERVICES_ACTION = "distributeProtoServices";
    public static final String CONFIGURE_PEER_ACTION = "configurePeer";

    public static final String DART_DISTRIBUTE_ACTION = "dartDistribute";

    public static final String RUN_SCRIPT_ACTION = "runScript";

    public static final String DEC_INPUT_NODES_ACTION = "decreaseInputNodes";
    public static final String DEC_OUTPUT_NODES_ACTION = "decreaseOutputNodes";
    public static final String INC_INPUT_NODES_ACTION = "increaseInputNodes";
    public static final String INC_OUTPUT_NODES_ACTION = "increaseOutputNodes";
    public static final String ADD_TRIGGER_NODE_ACTION = "addTriggerNode";
    public static final String REMOVE_TRIGGER_NODE_ACTION = "removeTriggerNode";
    public static final String TOGGLE_ERROR_NODE_ACTION = "toggleErrorNode";

}
