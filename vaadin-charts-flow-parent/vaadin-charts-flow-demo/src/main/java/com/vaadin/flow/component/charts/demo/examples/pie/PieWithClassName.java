package com.vaadin.flow.component.charts.demo.examples.pie;

import com.vaadin.flow.component.charts.demo.SkipFromDemo;

@SkipFromDemo
public class PieWithClassName extends PieWithLegend {

    public static final String CLASS_NAME = "redchart";

    @Override
    public void initDemo() {
        super.initDemo();
        chart.addClassName(CLASS_NAME);
    }

}
