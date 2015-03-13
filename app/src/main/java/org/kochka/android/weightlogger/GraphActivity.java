/*
  Copyright 2015 Sébastien Vrillaud
  
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
  
      http://www.apache.org/licenses/LICENSE-2.0
  
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/
package org.kochka.android.weightlogger;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.LinkedList;

import org.kochka.android.weightlogger.data.Measurement;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.ViewTreeObserver;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;


public class GraphActivity extends ActionBarActivity {
  
  LinearLayout graphLayout;
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.graph);

    Toolbar actionBar = (Toolbar) findViewById(R.id.actionbar);
    setSupportActionBar(actionBar);
    actionBar.setTitle(R.string.graph_title);
    actionBar.setNavigationIcon(getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha));
    actionBar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        onBackPressed();
      }
    });
    
    graphLayout = (LinearLayout) findViewById(R.id.graph);

    loadGraph(R.id.item_graph_weight);
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.graph_actionbar, menu);
    Toolbar actionBar = (Toolbar) findViewById(R.id.actionbar);
    actionBar.getMenu().getItem(0).setVisible(false);
    return true;
  } 
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    loadGraph(item.getItemId());
    return super.onOptionsItemSelected(item);
  }
  
  private void loadGraph(int item_id) {
    Toolbar actionBar = (Toolbar) findViewById(R.id.actionbar);
    ImageView logo = (ImageView) findViewById(R.id.graph_pic);
    
    // Hide active menu and show others
    for (int i=0; i < actionBar.getMenu().size(); i++) {
      MenuItem item = actionBar.getMenu().getItem(i);
      item.setVisible(item.getItemId() != item_id);
    }

    switch (item_id) {
      case R.id.item_graph_weight:
        actionBar.setSubtitle(R.string.weight);
        logo.setImageResource(R.drawable.ic_weight);
        break;
      case R.id.item_graph_body_fat:
        actionBar.setSubtitle(R.string.body_fat);
        logo.setImageResource(R.drawable.ic_body_fat);
        break;
      case R.id.item_graph_body_water:
        actionBar.setSubtitle(R.string.body_water);
        logo.setImageResource(R.drawable.ic_body_water);
        break;
      case R.id.item_graph_muscle_mass:
        actionBar.setSubtitle(R.string.muscle_mass);
        logo.setImageResource(R.drawable.ic_muscle_mass);
        break;
    }
    
    // Load data
    LinkedList<Measurement> measurements = Measurement.getAll(this, null, false);
    Measurement measurement;
    long dt;
    
    ArrayList<DataPoint> data = new ArrayList<DataPoint>();
    
    for (int i=0; i < measurements.size(); i++) {
      measurement = measurements.get(i);
      dt = measurement.getRecordedAt().getTime().getTime();
      switch (item_id) {
        case R.id.item_graph_weight:
          data.add(new DataPoint(dt, measurement.getConvertedWeight()));
          break;
        case R.id.item_graph_body_fat:
          if (measurement.getBodyFat() != null)
            data.add(new DataPoint(dt, measurement.getBodyFat()));
          break;
        case R.id.item_graph_body_water:
          if (measurement.getBodyWater() != null)
            data.add(new DataPoint(dt, measurement.getBodyWater()));
          break;
        case R.id.item_graph_muscle_mass:
          if (measurement.getMuscleMass() != null)
            data.add(new DataPoint(dt, measurement.getConvertedMuscleMass()));
          break;
      }
    }
    
    // Series
    GraphView graphView = new GraphView(this);
    LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(data.toArray(new DataPoint[data.size()]));
    series.setColor(Color.rgb(0, 171, 188));
    series.setDrawDataPoints(true);
    series.setDrawBackground(true);
    series.setBackgroundColor(Color.argb(150, 0, 171, 188));
    series.setDataPointsRadius(3);
    series.setThickness(3);
    graphView.addSeries(series);
    
    // Labels
    graphView.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(GraphActivity.this, DateFormat.getDateInstance(DateFormat.SHORT)));
    graphView.getGridLabelRenderer().setNumHorizontalLabels(3);
    
    // Viewport
    graphView.getViewport().setScrollable(true);
    graphView.getViewport().setScalable(true);
    graphView.getViewport().setXAxisBoundsManual(true);
    graphView.getViewport().setMinX(0);
    graphView.getViewport().setMaxX(1000 * 3600 * 24 * 10);
    graphView.getViewport().scrollToEnd();
    
    graphLayout.removeAllViews();
    graphLayout.addView(graphView);
  }
}