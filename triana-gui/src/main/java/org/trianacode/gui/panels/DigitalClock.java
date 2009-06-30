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

package org.trianacode.gui.panels;

import javax.swing.*;
import java.awt.*;
import java.util.Calendar;

/**
 * Created by IntelliJ IDEA.
 * User: Andrew
 * Date: 22-May-2004
 * Time: 08:31:00
 * To change this template use File | Settings | File Templates.
 */
public class DigitalClock extends JPanel {

    private int alarm = Integer.MAX_VALUE;
    private RetroDigit[] digits;
    private int hour;
    private int min;
    private int secs;
    private long millis;
    private Calendar cal;
    private Colons colons = new Colons();
    private long realTime;

    public DigitalClock(Color bgColor, Color fgColor) {
        super();
        setLayout(new DigitalLayout(4, 4));
        this.digits = new RetroDigit[4];
        setForeground(fgColor);
        setBackground(bgColor);
        for (int i = 0; i < digits.length; i++) {
            digits[i] = new RetroDigit();

        }
        add(digits[0], DigitalLayout.DIGIT);
        add(digits[1], DigitalLayout.DIGIT);
        add(colons, DigitalLayout.SEPARATOR);
        add(digits[2], DigitalLayout.DIGIT);
        add(digits[3], DigitalLayout.DIGIT);
        initTime();
        run();
    }


    public DigitalClock() {
        this(Color.BLACK, Color.GREEN);
    }

    private void initTime() {
        cal = Calendar.getInstance();
        hour = cal.get(Calendar.HOUR_OF_DAY);
        min = cal.get(Calendar.MINUTE);
        secs = (60 - cal.get(Calendar.SECOND));
        millis = cal.get(Calendar.MILLISECOND);
        realTime = cal.getTimeInMillis();
        setHours(hour);
        setMins(min);
    }

    private void setMins(int min) {
        if(min < 10) {
            digits[2].setDigit(0);
            digits[3].setDigit(min);
        } else {
            int time1 = min % 10;
            int time2 = (min % 100) / 10;
            digits[2].setDigit(time2);
            digits[3].setDigit(time1);
        }

    }

    private void setHours(int hour) {
        if(hour < 10) {
            digits[0].setDigit(0);
            digits[1].setDigit(hour);
        } else {
            int time1 = hour % 10;
            int time2 = (hour % 100) / 10;
            digits[0].setDigit(time2);
            digits[1].setDigit(time1);
        }
    }

    public boolean isAlarmSet() {
        return (this.alarm != Integer.MAX_VALUE);
    }

    public int getAlarm() {
        return alarm;
    }

    public void setAlarm(int alarm) {
        if(alarm > 2459 || alarm < 0) {
            alarm = 0;
        }
        this.alarm = alarm;
    }

    public void clearAlarm() {
        this.alarm = Integer.MAX_VALUE;
    }

    private void playAlarm() {
        for (int i = 0; i < digits.length; i++) {
            digits[i].blink(10, 200);
        }
    }

    private void checkAlarm() {
        if(alarm  == (digits[0].getDigit() * 1000) +
                    (digits[1].getDigit() * 100) +
                    (digits[2].getDigit() * 10) +
                    digits[3].getDigit()) {
            playAlarm();
        }
    }

    private void inc() {
        digits[3].setDigit((digits[3].getDigit() + 1) % 10);
        if(digits[3].getDigit() == 0) {
            int min = digits[2].getDigit();
            if(min < 5) {
                digits[2].setDigit(++min);
            } else {
                digits[2].setDigit(0);
                int hour = (digits[0].getDigit() * 10) + digits[1].getDigit();
                if(hour < 24) {
                    hour++;
                } else {
                    hour = 0;
                }
                setHours(hour);
            }
        }
        checkAlarm();
    }

    public void run() {
        Thread thread = new Thread() {
            public void run() {
                while (true) {
                    try {
                        sleep(1000 - millis);
                    } catch (InterruptedException except) {}
                    secs--;
                    if(secs == 0) {
                        secs = 60;
                        inc();
                        repaint();
                    }
                    colons.blink(1, 250);
                    long newReal = System.currentTimeMillis();
                    millis = newReal - realTime - (1000 - millis);
                    realTime = newReal;
                }
            }
        };
        thread.setPriority(Thread.NORM_PRIORITY);
        thread.start();
    }

    public static void main(String[] args) {
        DigitalClock counter = new DigitalClock();
        JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(counter, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        counter.setAlarm(1215);
    }
}
