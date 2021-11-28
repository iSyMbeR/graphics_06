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

package model;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.swing.JPanel;

public class Canvas extends JPanel {
    private final double STEPS = 5000.0;
    private final List<Point> points = new ArrayList<>();
    private final List<Point> bezierCurvePoints = new ArrayList<>();
    private Point selected;
    private boolean pressedOnSelected;

    public Canvas() {
        addMouseListener(
                new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (selected != null) {
                            selected = null;
                        } else {
                            final Optional<Point> point =
                                    points.stream()
                                            .filter(p -> new Rectangle(p.x - 3, p.y - 3, 12, 12).contains(e.getPoint()))
                                            .findFirst();
                            if (point.isPresent()) {
                                selected = point.get();
                            } else {
                                points.add(new Point(e.getX(), e.getY()));
                            }
                        }
                        repaint();
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (selected != null
                                && new Rectangle(selected.x - 3, selected.y - 3, 12, 12).contains(e.getPoint())) {
                            pressedOnSelected = true;
                        }
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        if (pressedOnSelected) {
                            pressedOnSelected = false;
                        }
                    }
                });

        addMouseMotionListener(
                new MouseAdapter() {

                    @Override
                    public void mouseDragged(MouseEvent e) {
                        if (pressedOnSelected) {
                            selected.setLocation(e.getPoint());
                            repaint();
                        }
                    }
                });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        bezierCurvePoints.clear();
        bezier(1 / STEPS);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setPaint(Color.BLUE);
        points.forEach(p -> g2d.fillOval(p.getLocation().x, p.getLocation().y, 6, 6));
        g2d.setPaint(Color.RED);
        bezierCurvePoints.forEach(p -> g2d.fillOval(p.getLocation().x, p.getLocation().y, 1, 1));
        if (selected != null) {
            g2d.setPaint(Color.BLACK);
            g2d.drawRect(selected.x - 3, selected.y - 3, 12, 12);
        }
    }

    private void bezier(double step) {
        int xEnd = points.stream().map(p -> p.x).max(Integer::compareTo).orElse(1000);
        int degree = points.size() - 1;
        for (double p = 0; p <= 1; p += step) {
            double xPoint = 0;
            for (int i = 0; i <= points.size() - 1; ++i) {
                double newton = calculateNewton(degree, i);
                double ti = Math.pow(p, i);
                double lti = Math.pow((1 - p), (degree - i));
                xPoint = xPoint + (newton * ti * lti * points.get(i).x);
            }
            if (xPoint > xEnd) {
                break;
            }
            double yPoint = 0;
            for (int i = 0; i <= degree; ++i) {
                double yNewton = calculateNewton(degree, i);
                double yti = Math.pow(p, i);
                double ylti = Math.pow((1 - p), (degree - i));
                yPoint = yPoint + (yNewton * yti * ylti * points.get(i).y);
            }
            bezierCurvePoints.add(new Point((int) xPoint, (int) yPoint));
        }
    }

    private double calculateNewton(int n, int k) {
        long result = 1;
        for (int i = 1; i <= k; i++) {
            result = result * (n - i + 1) / i;
        }
        return result;
    }

    public void moveSelected(int x, int y) {
        if (selected != null) {
            selected.setLocation(x, y);
            repaint();
        }
    }

    public Point getSelected() {
        return selected;
    }

    public void clear() {
        bezierCurvePoints.clear();
        points.clear();
        repaint();
        selected = null;
        pressedOnSelected = false;
    }
}
