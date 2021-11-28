/*
 * QUEST SOFTWARE PROPRIETARY INFORMATION
 *
 * This software is confidential.  Quest Software Inc., or one of its
 * subsidiaries, has supplied this software to you under terms of a
 * license agreement, nondisclosure agreement or both.
 *
 * You may not copy, disclose, or use this software except in accordance with
 * those terms.
 *
 *
 * Copyright 2020 Quest Software Inc.
 * ALL RIGHTS RESERVED.
 *
 * QUEST SOFTWARE INC. MAKES NO REPRESENTATIONS OR
 * WARRANTIES ABOUT THE SUITABILITY OF THE SOFTWARE,
 * EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR
 * NON-INFRINGEMENT.  QUEST SOFTWARE SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE
 * AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 */

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;

import model.Canvas;

public class Main {
    private static Point selected;

    public static void main(String[] args) {
        JFrame window = new JFrame("Paint");
        window.setLayout(new FlowLayout());

        final JTextField x = new JTextField(3);
        final JTextField y = new JTextField(3);

        final Canvas canvas = new Canvas();

        canvas.setBackground(Color.LIGHT_GRAY);
        canvas.setPreferredSize(new Dimension(1200, 800));
        window.add(canvas);
        window.add(x);
        window.add(y);
        JButton move = new JButton("Move");
        window.add(move);
        JButton clear = new JButton("Clear");
        window.add(clear);
        canvas.addMouseListener(
                new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        selected = canvas.getSelected();
                        updateTextFields(x, y, move);
                    }
                });

        canvas.addMouseMotionListener(
                new MouseAdapter() {
                    @Override
                    public void mouseDragged(MouseEvent e) {
                        updateTextFields(x, y, move);
                    }
                });
        move.addActionListener(
                e -> canvas.moveSelected(Integer.parseInt(x.getText()), Integer.parseInt(y.getText())));

        clear.addActionListener(
                e -> {
                    canvas.clear();
                    x.setText("");
                    y.setText("");
                    move.setEnabled(false);
                    selected = null;
                });

        window.setSize(new Dimension(1200, 1000));
        window.setVisible(true);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private static void updateTextFields(JTextField x, JTextField y, JButton move) {
        if (selected != null) {
            x.setText(String.valueOf(selected.x));
            y.setText(String.valueOf(selected.y));
            move.setEnabled(true);
        } else {
            x.setText("");
            y.setText("");
            move.setEnabled(false);
        }
    }
}
