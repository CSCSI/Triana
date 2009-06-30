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

/**
 * Created by IntelliJ IDEA.
 * User: Andrew
 * Date: 22-May-2004
 * Time: 08:31:00
 * To change this template use File | Settings | File Templates.
 */
public class DigitalCounter extends JPanel {

    private int counter = 0;
    private int alarm = Integer.MAX_VALUE;
    private RetroDigit[] digits;
    private int max;

    public DigitalCounter(int digitCount, Color bgColor, Color fgColor) {
        super();
        setLayout(new DigitalLayout(2, 2));
        setPreferredSize(new Dimension(200, 50));
        this.digits = new RetroDigit[digitCount];
        calculateMax();
        setForeground(fgColor);
        setBackground(bgColor);
        for (int i = 0; i < digits.length; i++) {
            digits[i] = new RetroDigit();
            add(digits[i], DigitalLayout.DIGIT);
        }
        calculateDigits(counter);
    }

    public DigitalCounter(int digitCount) {
        this(digitCount, Color.BLACK, Color.GREEN);
    }

    public DigitalCounter() {
        this(4, Color.BLACK, Color.GREEN);
    }

    public void inc() {
        checkCounter(++counter);
        calculateDigits(counter);
    }

    public void dec() {
        checkCounter(--counter);
        calculateDigits(counter);
    }


    private void calculateDigits(int num) {
        int pos = 1;
        for (int i = digits.length - 1; i >= 0; i--) {
            if (pos > num) {
                digits[i].setDigit(0);
            } else {
                pos *= 10;
                digits[i].setDigit((num % pos) / (pos / 10));
            }
        }
    }

    private void calculateMax() {
        int total = 0;
        int pos = 1;
        for (int i = 0; i < digits.length; i++) {
            total += pos * 9;
            pos *= 10;
        }
        max = total;
    }

    private void checkCounter(int count) {
        count = count % (max + 1);
        if (count == alarm)
            playAlarm();
    }


    public void setCounter(int counter) {
        this.counter = counter;
    }

    public int getCounter() {
        return counter;
    }

    public int getDigitCount() {
        return digits.length;
    }


    public boolean isAlarmSet() {
        return (this.alarm != Integer.MAX_VALUE);
    }

    public int getAlarm() {
        return alarm;
    }

    public void setAlarm(int alarm) {
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


    public void countSeconds() {
        Thread thread = new Thread() {
            public void run() {
                while (true) {
                    inc();
                    repaint();

                    try {
                        sleep(1000);
                    } catch (InterruptedException except) {
                    }
                }
            }
        };
        thread.setPriority(Thread.NORM_PRIORITY);
        thread.start();
    }

    public static void main(String[] args) {
        final DigitalCounter counter = new DigitalCounter();
        JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(counter, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        counter.setAlarm(10);
        counter.countSeconds();
    }
}
