/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package java2ddrawingapplication;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import javax.swing.*;

/**
 *
 * @author acv
 */
public class DrawingApplicationFrame extends JFrame
{
    private JTextField textField;
    private JLabel resultLabel;
    // Create the panels for the top of the application. One panel for each
    // line and one to contain both of those panels.
    private JPanel subPanel1 = new JPanel();
    private JPanel subPanel2 = new JPanel();
    private JPanel topPanel = new JPanel(new GridLayout(2, 1));;
    // create the widgets for the firstLine Panel.
    private JLabel shape = new JLabel("Shape:");
    private String[] shapeOptions = {"Rectangle", "Line", "Oval"};
    private JComboBox<String> shapeComboBox = new JComboBox<>(shapeOptions);
    private JButton firstColorButton = new JButton("1st Color");
    private JButton secondColorButton = new JButton("2nd Color");
    private JButton undoButton = new JButton("Undo");
    private JButton clearButton = new JButton("Clear");
    //create the widgets for the secondLine Panel.
    private JLabel options = new JLabel("Options:");
    private JCheckBox filledCheckBox = new JCheckBox("Filled");
    private JCheckBox useGradientCheckBox = new JCheckBox("Use Gradient");
    private JCheckBox dashedCheckBox = new JCheckBox("Dashed");
    private JLabel lineWidth = new JLabel("Line Width");
    private JSpinner lineWidthSpinner = new JSpinner();
    private JLabel dashLength = new JLabel("Dash Length");
    JSpinner dashLengthSpinner = new JSpinner();
    // Variables for drawPanel.
    private JPanel drawPanel = new DrawPanel();;
    // add status label
    private JLabel statusLabel = new JLabel();

    // Constructor for DrawingApplicationFrame
    public DrawingApplicationFrame()
    {
        setLayout(new GridLayout(3, 1));
        setLayout(new BorderLayout());
        // add widgets to panels
        // firstLine widgets
        subPanel1.add(shape);
        subPanel1.add(shapeComboBox);
        subPanel1.add(firstColorButton);
        subPanel1.add(secondColorButton);
        subPanel1.add(undoButton);
        subPanel1.add(clearButton);
        // secondLine widgets
        subPanel2.add(options);
        subPanel2.add(filledCheckBox);
        subPanel2.add(useGradientCheckBox);
        subPanel2.add(dashedCheckBox);
        subPanel2.add(lineWidth);
        subPanel2.add(lineWidthSpinner);
        lineWidthSpinner.setValue(1.0);
        subPanel2.add(dashLength);
        subPanel2.add(dashLengthSpinner);
        dashLengthSpinner.setValue(1);
        // add top panel of two panels
        topPanel.add(subPanel1);
        topPanel.add(subPanel2);
        subPanel2.setBackground(new Color(193, 228, 233));
        subPanel1.setBackground(new Color(193, 228, 233));
        // add topPanel to North, drawPanel to Center, and statusLabel to South
        add(topPanel, BorderLayout.NORTH);
        DrawPanel drawPanel = new DrawPanel();
        add(drawPanel, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
        //add listeners and event handlers
        DrawPanel.MouseHandler mouseHandler = drawPanel.new MouseHandler();
        drawPanel.addMouseListener(mouseHandler);
        drawPanel.addMouseMotionListener(mouseHandler);
        firstColorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color c = JColorChooser.showDialog(DrawingApplicationFrame.this, "Choose 1st Color", Color.BLACK);
                drawPanel.setChosenColor1(c);
            }
        });
        secondColorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color c = JColorChooser.showDialog(DrawingApplicationFrame.this, "Choose 2nd Color", Color.BLACK);
                drawPanel.setChosenColor2(c);
            }
        });
        undoButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                drawPanel.undoLast();
            }
        });
        clearButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                drawPanel.clear();
            }
        });
    }

    // Create event handlers, if needed
    private void updateStatusLabel(int x, int y) {
        // Update the statusLabel text with the current mouse position
        String statusText = "(" + x + ", " + y + ")";
        statusLabel.setText(statusText);
    }
    // Create a private inner class for the DrawPanel.
    private class DrawPanel extends JPanel
    {
        private Point startPt;
        private Point endPt;
        private Color chosenColor1;
        private Color chosenColor2;
        private ArrayList<MyShapes> allShapes = new ArrayList<MyShapes>();
        private int lastIndex;

        public DrawPanel()
        {
            chosenColor1 = Color.BLACK;
            chosenColor2 = Color.BLACK;
            lastIndex = 0;
        }

        public void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            //loop through and draw each shape in the shapes arraylist
            for (int i = 0; i < allShapes.size(); i++) {
                allShapes.get(i).draw(g2d);
            }

        }
        public void setChosenColor1(Color c) {
            this.chosenColor1 = c;
        }
        public void setChosenColor2(Color c) {
            this.chosenColor2 = c;
        }
        public void undoLast() {
            if (allShapes.size() > 0) {
                allShapes.remove(--lastIndex);
            }
            repaint();
        }
        public void clear() {
            allShapes = new ArrayList<>();
            lastIndex = 0;
            repaint();
        }
        public MyShapes getShape() {
            String type = shapeComboBox.getSelectedItem().toString();
            Boolean filled = filledCheckBox.isSelected();
            Paint p;
            if (useGradientCheckBox.isSelected()) {
                p = new GradientPaint(startPt.x, startPt.y, chosenColor1, endPt.x, endPt.y, chosenColor2);
            }
            else {
                p = chosenColor1;
            }
            Number lineWidthValue = (Number) lineWidthSpinner.getValue();
            float lineWidth = lineWidthValue.floatValue();
            Stroke strk;
            if (dashedCheckBox.isSelected()) {
                strk = new BasicStroke(lineWidth, BasicStroke.CAP_BUTT,
                        BasicStroke.JOIN_BEVEL, 0, new float[]{(int) dashLengthSpinner.getValue()}, 0);
            }
            else {
                strk = new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
            }
            MyShapes shape;
            if (type.equals("Rectangle")) {
                shape = new MyRectangle(startPt, endPt, p, strk, filled);
            } else if (type.equals("Line")) {
                shape = new MyLine(startPt, endPt, p, strk);
            }
            else {
                shape = new MyOval(startPt, endPt, p, strk, filled);
            }
            return shape;
        }


        private class MouseHandler extends MouseAdapter implements MouseMotionListener
        {

            public void mousePressed(MouseEvent event)
            {
                startPt = new Point(event.getX(), event.getY());
            }

            public void mouseReleased(MouseEvent event)
            {
                endPt = new Point(event.getX(), event.getY());
                if (allShapes.size() <= lastIndex) {
                    allShapes.add(getShape());
                }
                else {
                    allShapes.set(lastIndex, getShape());
                }
                lastIndex++;
                repaint();
            }

            @Override
            public void mouseDragged(MouseEvent event)
            {
                endPt = new Point(event.getX(), event.getY());
                if (allShapes.size() <= lastIndex) {
                    allShapes.add(getShape());
                }
                else {
                    allShapes.set(lastIndex, getShape());
                }
                repaint();
            }

            @Override
            public void mouseMoved(MouseEvent event)
            {
                DrawingApplicationFrame.this.updateStatusLabel(event.getX(), event.getY());
            }
        }

    }
}
