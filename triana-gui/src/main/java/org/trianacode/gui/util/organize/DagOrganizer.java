package org.trianacode.gui.util.organize;

import org.trianacode.gui.main.TrianaLayoutConstants;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Nov 20, 2010
 */
public class DagOrganizer {

    /**
     * Each vertex has a minimumLevel. Any vertex with no successors has
     * minimumLevel of zero. The minimumLevel of any vertex must be strictly
     * greater than the minimumLevel of its parents. (Vertex A is a parent of
     * Vertex B iff there is an edge from B to A.) Typically, a vertex will
     * have a minimumLevel which is one greater than the minimumLevel of its
     * parent's. However, if the vertex has two parents, its minimumLevel will
     * be one greater than the maximum of the parents'. We need to calculate
     * the minimumLevel for each vertex. When we layout the graph, vertices
     * cannot be drawn any higher than the minimumLevel. The graphHeight of a
     * graph is the greatest minimumLevel that is used. We will modify the
     * SpringLayout calculations so that nodes cannot move above their assigned
     * minimumLevel.
     */
    private Map<OrganizableTask, Integer> minLevels = new HashMap<OrganizableTask, Integer>();
    private OrganizableTaskGraph g;
    // Simpler than the "pair" technique.
    private int graphWidth;
    private int numRoots;
    final double SPACEFACTOR = 1.3;
    // How much space do we allow for additional floating at the bottom.
    final double LEVELATTRACTIONRATE = 0.1;
    private Dimension size;

    protected double stretch = 0.70;
    protected double lengthFunction = TrianaLayoutConstants.DEFAULT_TOOL_SIZE.width;
    protected int repulsion_range_sq = 100 * 100;
    protected double force_multiplier = 1.0 / 3.0;

    /**
     * A bunch of parameters to help work out when to stop quivering.
     * <p/>
     * If the MeanSquareVel(ocity) ever gets below the MSV_THRESHOLD, then we
     * will start a final cool-down phase of COOL_DOWN_INCREMENT increments. If
     * the MeanSquareVel ever exceeds the threshold, we will exit the cool down
     * phase, and continue looking for another opportunity.
     */
    final double MSV_THRESHOLD = 10.0;
    double meanSquareVel;
    boolean stoppingIncrements = false;
    int incrementsLeft;
    final int COOL_DOWN_INCREMENTS = 200;

    /**
     * Creates an instance for the specified graph.
     */
    public DagOrganizer(OrganizableTaskGraph g) {
        this.g = g;
        setRoot();
        setSize(g.getSize());
    }

    /**
     * setRoot calculates the level of each vertex in the graph. Level 0 is
     * allocated to any vertex with no successors. Level n+1 is allocated to
     * any vertex whose successors' maximum level is n.
     */
    public void setRoot() {
        numRoots = 0;
        java.util.List<OrganizableTask> tasks = g.getTasks();
        for (OrganizableTask task : tasks) {
            java.util.List<OrganizableTask> suc = g.getPredecessors(task);
            if (suc.size() == 0) {
                setRoot(task);
                numRoots++;
            }
        }
    }


    /**
     * Set vertex v to be level 0.
     */
    public void setRoot(OrganizableTask t) {
        System.out.println("DagOrganizer.setRoot adding min level:" + t);
        minLevels.put(t, 0);
        // set all the levels.
        propagateMinimumLevel(t);
    }

    /**
     * A recursive method for allocating the level for each vertex. Ensures
     * that all predecessors of v have a level which is at least one greater
     * than the level of v.
     *
     * @param t
     */
    public void propagateMinimumLevel(OrganizableTask t) {
        int level = minLevels.get(t);
        java.util.List<OrganizableTask> predecessors = g.getSuccessors(t);
        for (OrganizableTask child : predecessors) {
            int oldLevel, newLevel;
            Integer o = minLevels.get(child);
            if (o != null)
                oldLevel = o.intValue();
            else
                oldLevel = 0;
            newLevel = Math.max(oldLevel, level + 1);
            minLevels.put(child, newLevel);

            if (newLevel > graphWidth)
                graphWidth = newLevel;
            propagateMinimumLevel(child);
        }
    }

    /**
     * Sets random locations for a vertex within the dimensions of the space.
     * This overrides the method in AbstractLayout
     *
     * @param coord
     * @param d
     */
    private void initializeLocation(OrganizableTask t, Point2D coord, Dimension d) {

        int level = minLevels.get(t);
        int minX = (int) (level * d.getWidth() / (graphWidth * SPACEFACTOR));
        double y = Math.random() * d.getWidth();
        double x = Math.random() * (d.getHeight() - minX) + minX;
        coord.setLocation(x, y);
    }

    public void setSize(Dimension size) {
        if (size != null) {
            Dimension oldSize = this.size;
            this.size = size;

            if (oldSize != null) {
                adjustLocations(oldSize, size);
            }
        }
        this.size = size;
        java.util.List<OrganizableTask> tasks = g.getTasks();
        for (OrganizableTask task : tasks) {
            //initializeLocation(task, task.getPoint(), getSize());
        }
    }

    private void adjustLocations(Dimension oldSize, Dimension size) {

        int xOffset = (size.width - oldSize.width) / 2;
        int yOffset = (size.height - oldSize.height) / 2;

        // now, move each vertex to be at the new screen center
        while (true) {
            try {
                for (OrganizableTask v : g.getTasks()) {
                    offsetVertex(v, xOffset, yOffset);
                }
                break;
            } catch (ConcurrentModificationException cme) {
            }
        }
    }

    protected void offsetVertex(OrganizableTask v, double xOffset, double yOffset) {
        Point2D c = v.getPoint();
        c.setLocation(c.getX() + xOffset, c.getY() + yOffset);
        setLocation(v, c);
    }

    public Dimension getSize() {
        return size;
    }

    /**
     * Override the moveNodes() method from SpringLayout. The only change we
     * need to make is to make sure that nodes don't float higher than the minY
     * coordinate, as calculated by their minimumLevel.
     */
    protected void moveNodes() {
        // Dimension d = currentSize;
        double oldMSV = meanSquareVel;
        meanSquareVel = 0;

        synchronized (getSize()) {
            java.util.List<OrganizableTask> tasks = g.getTasks();
            for (OrganizableTask task : tasks) {
                Point2D xyd = task.getPoint();
                System.out.println("DagOrganizer.moveNodes before x:" + xyd.getX() + " y:" + xyd.getY());

                int width = getSize().width;
                int height = getSize().height;

                // (JY addition: three lines are new)
                int level = minLevels.get(task);
                int minX = (int) (level * width / (graphWidth * SPACEFACTOR));
                int maxX = level == 0
                        ? (int) (width / (graphWidth * SPACEFACTOR * 2))
                        : width;

                // JY added 2* - double the sideways repulsion.
                task.dx += 2 * task.repulsiondx + task.edgedx;
                task.dy += task.repulsiondy;// + task.edgedy;

                // JY Addition: Attract the vertex towards it's minimumLevel
                // width.
                double delta = xyd.getX() - minX;
                task.dx -= delta * LEVELATTRACTIONRATE;
                if (level == 0)
                    task.dx -= delta * LEVELATTRACTIONRATE;
                // twice as much at the top.

                // JY addition:
                meanSquareVel += (task.dx * task.dx + task.dy * task.dy);

                // keeps nodes from moving any faster than 5 per time unit
                xyd.setLocation(xyd.getX() + Math.max(-5, Math.min(5, task.dx)), xyd.getY() + Math.max(-1, Math.min(1, task.dy)));

                if (xyd.getX() < 0) {
                    xyd.setLocation(0, xyd.getY());
                }
//                } else if (xyd.getX() > width) {
//                    xyd.setLocation(width, xyd.getY());
//                }

                // (JY addition: These two lines replaced 0 with minY)
                if (xyd.getX() < minX) {
                    xyd.setLocation(minX, xyd.getY());
                }
                // (JY addition: replace height with maxY)
//                } else if (xyd.getX() > maxX) {
//                    xyd.setLocation(maxX, xyd.getY());
//                }

                // (JY addition: if there's only one root, anchor it in the
                // middle-top of the screen)
                if (numRoots == 1 && level == 0) {
                    xyd.setLocation(xyd.getX(), height / 2);
                }
                System.out.println("DagOrganizer.moveNodes after x:" + xyd.getX() + " y:" + xyd.getY());

            }

        }
        //System.out.println("MeanSquareAccel="+meanSquareVel);
        if (!stoppingIncrements
                && Math.abs(meanSquareVel - oldMSV) < MSV_THRESHOLD) {
            stoppingIncrements = true;
            incrementsLeft = COOL_DOWN_INCREMENTS;
        } else if (
                stoppingIncrements
                        && Math.abs(meanSquareVel - oldMSV) <= MSV_THRESHOLD) {
            incrementsLeft--;
            if (incrementsLeft <= 0)
                incrementsLeft = 0;
        }
    }

    public boolean done() {
        if (stoppingIncrements && incrementsLeft == 0) {
            System.out.println("DagOrganizer.done DONE");
            return true;
        } else
            return false;
    }

    /**
     * Override forceMove so that if someone moves a node, we can re-layout
     * everything.
     */
    public void setLocation(OrganizableTask picked, double x, double y) {
        Point2D coord = picked.getPoint();
        coord.setLocation(x, y);
        stoppingIncrements = false;
    }

    /**
     * Override forceMove so that if someone moves a node, we can re-layout
     * everything.
     */
    public void setLocation(OrganizableTask picked, Point2D p) {
        Point2D coord = picked.getPoint();
        coord.setLocation(p);
        stoppingIncrements = false;
    }

    /**
     * Overridden relaxEdges. This one reduces the effect of edges between
     * greatly different levels.
     */
    protected void relaxEdges() {
        for (OrganizableTaskGraph.Edge e : g.getEdges()) {

            OrganizableTask v1 = e.getSource();
            OrganizableTask v2 = e.getDest();


            Point2D p1 = v1.getPoint();
            Point2D p2 = v2.getPoint();
            double vx = p1.getX() - p2.getX();
            double vy = p1.getY() - p2.getY();
            double len = Math.sqrt(vx * vx + vy * vy);

            // JY addition.
            int level1 = minLevels.get(v1);
            int level2 = minLevels.get(v2);

            // desiredLen *= Math.pow( 1.1, (v1.degree() + v2.degree()) );
//          double desiredLen = getLength(e);
            double desiredLen = lengthFunction;

            // round from zero, if needed [zero would be Bad.].
            len = (len == 0) ? .0001 : len;

            // force factor: optimal length minus actual length,
            // is made smaller as the current actual length gets larger.
            // why?

            // System.out.println("Desired : " + getLength( e ));
            double f = force_multiplier * (desiredLen - len) / len;

            f = f * Math.pow(stretch / 100.0,
                    (v1.getConnectionCount() + v2.getConnectionCount() - 2));

            // JY addition. If this is an edge which stretches a long way,
            // don't be so concerned about it.
            if (level1 != level2)
                f = f / Math.pow(Math.abs(level2 - level1), 1.5);

            // f= Math.min( 0, f );

            // the actual movement distance 'dx' is the force multiplied by the
            // distance to go.
            double dx = f * vx;
            double dy = f * vy;


//			SpringEdgeData<E> sed = getSpringEdgeData(e);
//			sed.f = f;

            v1.edgedx += dx;
            v1.edgedy += dy;
            v2.edgedx += -dx;
            v2.edgedy += -dy;

        }
    }

    private int stepCount = 0;

    public void step() {
        stepCount++;
        System.out.println("DagOrganizer.step " + stepCount);
        try {
            java.util.List<OrganizableTask> tasks = g.getTasks();
            for (OrganizableTask task : tasks) {

                if (task == null) {
                    continue;
                }
                task.dx /= 4;
                task.dy /= 4;
                task.edgedx = task.edgedy = 0;
                task.repulsiondx = task.repulsiondy = 0;
            }
        } catch (ConcurrentModificationException cme) {
            cme.printStackTrace();
            step();
        }

        relaxEdges();
        calculateRepulsion();
        moveNodes();
        g.repaint();
    }


    protected void calculateRepulsion() {
        try {
            java.util.List<OrganizableTask> tasks = g.getTasks();
            for (OrganizableTask task : tasks) {
                if (task == null) continue;
                double dx = 0, dy = 0;

                for (OrganizableTask task2 : g.getTasks()) {
                    if (task == task2) continue;
                    Point2D p = task.getPoint();
                    Point2D p2 = task2.getPoint();
                    if (p == null || p2 == null) continue;
                    double vx = p.getX() - p2.getX();
                    double vy = p.getY() - p2.getY();
                    double distanceSq = p.distanceSq(p2);
                    if (distanceSq == 0) {
                        dx += Math.random();
                        dy += Math.random();
                    } else if (distanceSq < repulsion_range_sq) {
                        double factor = 1;
                        dx += factor * vx / distanceSq;
                        dy += factor * vy / distanceSq;
                    }
                }
                double dlen = dx * dx + dy * dy;
                if (dlen > 0) {
                    dlen = Math.sqrt(dlen) / 2;
                    task.repulsiondx += dx / dlen;
                    task.repulsiondy += (dy / dlen);
                }
            }
        } catch (ConcurrentModificationException cme) {
            calculateRepulsion();
        }
    }
}
