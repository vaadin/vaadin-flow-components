package com.vaadin.flow.component.charts.events;

/*-
 * #%L
 * Vaadin Charts for Flow
 * %%
 * Copyright (C) 2014 - 2019 Vaadin Ltd
 * %%
 * This program is available under Commercial Vaadin Add-On License 3.0
 * (CVALv3).
 * 
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 * 
 * You should have received a copy of the CVALv3 along with this program.
 * If not, see <https://vaadin.com/license/cval-3>.
 * #L%
 */

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.DomEvent;
import com.vaadin.flow.component.EventData;
import com.vaadin.flow.component.charts.Chart;

/**
 * The PointSelectEvent class stores data for select events on the points of the
 * chart.
 */
@DomEvent("point-select")
public class PointSelectEvent extends ComponentEvent<Chart> implements HasItem {

    private final int seriesIndex;
    private final String category;
    private final int pointIndex;

    public PointSelectEvent(Chart source, boolean fromClient,
            @EventData("event.detail.originalEvent.target.series.index") int seriesIndex,
            @EventData("event.detail.originalEvent.target.category") String category,
            @EventData("event.detail.originalEvent.target.index") int pointIndex) {
        super(source, fromClient);
        this.seriesIndex = seriesIndex;
        this.category = category;
        this.pointIndex = pointIndex;
    }

    @Override
    public int getSeriesItemIndex() {
        return seriesIndex;
    }

    @Override
    public String getCategory() {
        return category;
    }

    @Override
    public int getItemIndex() {
        return pointIndex;
    }
}
