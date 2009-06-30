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

package org.trianacode.taskgraph.clipin;

import org.trianacode.taskgraph.Node;
import org.trianacode.taskgraph.Task;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

/**
 * Class Description Here...
 *
 * @author Andrew Harrison
 * @version $Revision:$
 * @created Jun 28, 2009: 5:46:53 PM
 * @date $Date:$ modified by $Author:$
 */

public class ClipInBucket implements Serializable {


    /**
     * The task this bucket is associated with
     */
    private Task task = null;

    /**
     * A hashtable of the current clip-ins, keyed on clip-in name
     */
    private Hashtable bucket = new Hashtable();


    /**
     * Construct a bucket attached to the specified task
     */
    public ClipInBucket(Task task) {
        this.task = task;
    }

    /**
     * Constructs a clone of the specified bucket
     */
    ClipInBucket(ClipInBucket bucket, Node node) {
        String names[] = bucket.getClipInNames();

        for (int count = 0; count < names.length; count++)
            this.bucket.put(names[count], bucket.getClipIn(names[count], node));
    }


    /**
     * @return the task this bucket is attached to
     */
    public Task getTask() {
        return task;
    }

    /**
     * @return true if this bucket is attached to a task
     */
    public boolean isAttached() {
        return task != null;
    }


    /**
     * Inserts the clip-ins in the specified clip-in bucket into this bucket,
     * intializing all the clip-ins in the insertion bucket. This method is
     * called when data is input by a unit.
     *
     * @param insert the clip-ins to be inserted into the bucket
     * @param node   the node the data arrived on
     */
    public void insert(ClipInBucket insert, Node node) {
        String[] names = insert.getClipInNames();
        Object[] clipins = new Object[names.length];

        for (int count = 0; count < clipins.length; count++)
            clipins[count] = insert.getClipIn(names[count]);

        for (int count = 0; count < names.length; count++)
            initialize(names[count], clipins[count], node);

        for (int count = 0; count < names.length; count++)
            bucket.put(names[count], clipins[count]);
    }

    /**
     * Initialises the specified clip-in (but doesn't place it in the bucket)
     */
    private void initialize(String name, Object clipin, Node node) {
        Object exist = null;

        if (bucket.containsKey(name))
            exist = bucket.get(name);

        if (exist != clipin) {
            if ((clipin != null) && (clipin instanceof ClipIn))
                ((ClipIn) clipin).initializeAttach(new AttachInfo(task, node, this));

            if ((exist != null) && (exist instanceof ClipIn))
                ((ClipIn) exist).finalizeAttach(new AttachInfo(task, null, this));
        }
    }

    /**
     * Extracts a copy of the clip-in bucket for attaching to the data being
     * output from the task, finializing all the clip-ins in the extracted bucket
     * This method is called when data is output by a unit.
     *
     * @param node the node the data is being output from
     */
    public ClipInBucket extract(Node node) {
        return new ClipInBucket(this, node);
    }

    /**
     * Removes and finalizes all the clip-ins currently in the bucket. This
     * method is usually called after a unit has finished processing
     */
    public void empty() {
        Object clipin;

        for (Iterator iter = bucket.values().iterator(); iter.hasNext();) {
            clipin = iter.next();

            if (clipin instanceof ClipIn)
                ((ClipIn) clipin).finalizeAttach(new AttachInfo(task, null, this));
        }

        bucket.clear();
    }


    /**
     * Puts the specified clip-in into the bucket, initializing the clip-in
     * being inserted. If there is already a clip-in for the specified name then
     * that clip-in is finialized and removed.
     */
    public void putClipIn(String name, Object clipin) {
        initialize(name, clipin, null);
        bucket.put(name, clipin);
    }

    /**
     * Removes the clip-in for the specified name from the bucket. The clip-in
     * being removed is finalized.
     *
     * @return the removed clip-in (or null if unknown)
     */
    public Object removeClipIn(String name) {
        if (bucket.containsKey(name)) {
            Object clipin = bucket.get(name);

            if (clipin instanceof ClipIn)
                ((ClipIn) clipin).finalizeAttach(new AttachInfo(task, null, this));
        }

        return bucket.remove(name);
    }

    /**
     * @return the clip-in with the specified name, or null if none exists.
     *         If the clip-in object is an instance of the ClipIn interface then
     *         a finalized copy is returned.
     */
    public Object getClipIn(String name) {
        return getClipIn(name, null);
    }

    /**
     * @return the clip-in with the specified name, or null if none exists.
     *         If the clip-in object is an instance of the ClipIn interface then
     *         a finalized copy is returned.
     */
    private Object getClipIn(String name, Node node) {
        if (bucket.containsKey(name)) {
            Object clipin = bucket.get(name);

            if (clipin instanceof ClipIn) {
                clipin = ((ClipIn) clipin).clone();

                if (isAttached())
                    ((ClipIn) clipin).finalizeAttach(new AttachInfo(task, node, this));
            }

            return clipin;
        } else
            return null;
    }

    /**
     * @return an array of names of the clip-ins stored in this
     */
    public String[] getClipInNames() {
        return (String[]) bucket.keySet().toArray(new String[bucket.size()]);
    }

    /**
     * @return true if a clip-in with the specified name exists in the bucket
     */
    public boolean isClipInName(String name) {
        return bucket.containsKey(name);
    }


    /**
     * @return a store of the current clipin bucket state
     */
    public ClipInStore extractClipInStore() {
        ClipInStore store = new ClipInStore(task);
        Enumeration enumeration = bucket.keys();
        String key;
        Object clipin;

        while (enumeration.hasMoreElements()) {
            key = (String) enumeration.nextElement();
            clipin = bucket.get(key);

            if (clipin instanceof ClipIn)
                store.setClipIn(key, ((ClipIn) clipin).clone());
            else
                store.setClipIn(key, clipin);
        }

        return store;
    }

    /**
     * Restores the state stored in a clip-in store
     */
    public void restoreClipInStore(ClipInStore store) {
        if (!store.getTask().getInstanceID().equals(task.getInstanceID()))
            throw (new RuntimeException("ClipIn Bucket Error: Attempt to restore ClipInStore to different task"));

        bucket.clear();

        String[] names = store.getClipInNames();
        for (int count = 0; count < names.length; count++)
            bucket.put(names[count], store.getClipIn(names[count]));
    }
}
