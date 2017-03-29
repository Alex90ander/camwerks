package com.shamwerks.camwerks.gui;


import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.PolarPlot;
import org.jfree.chart.renderer.DefaultPolarItemRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.shamwerks.camwerks.CamWerks;
import com.shamwerks.camwerks.config.Lang;
import com.shamwerks.camwerks.config.LangEntry;
import com.shamwerks.camwerks.pojo.Cam;
import com.shamwerks.camwerks.pojo.Camshaft;
import com.shamwerks.camwerks.pojo.Coord;
import com.shamwerks.camwerks.pojo.LinearFunction;

public class CwPanelCamRadialChart extends JPanel{

	private static final long serialVersionUID = -9125241701760001557L;

	double baseCircle = 15.0;
	
	private ChartPanel chartPanel;
	private JFreeChart chart;
	private XYSeriesCollection series = new XYSeriesCollection();
	
	public JFreeChart getChart() {
		return chart;
	}

	public CwPanelCamRadialChart() {
		super();
System.out.println("---> SHAM 1.0");
        chart = createChart( (XYDataset)series );
        chart.setBackgroundPaint(Color.white); 

		this.chartPanel = new ChartPanel(chart);

		//To avoid changing size of the font on title/legends
		chartPanel.setMaximumDrawHeight(5000);
		chartPanel.setMinimumDrawHeight(0);
		chartPanel.setMaximumDrawWidth(5000);
		chartPanel.setMinimumDrawWidth(0);
		
        setLayout(new GridLayout(1, 0, 0, 0));
		add(this.chartPanel);
	}

	private JFreeChart createChart(XYDataset paramXYDataset) {
		JFreeChart localJFreeChart = ChartFactory.createPolarChart(null, paramXYDataset, true, true, true);
		//JFreeChart localJFreeChart = ChartFactory.createXYLineChart(null, Lang.getText(Lang.CHART_LEGEND_DEGREES), Lang.getText(Lang.CHART_LEGEND_LIFT), paramXYDataset);
		//JFreeChart localJFreeChart = ChartFactory.createXYLineChart(null, "degrés", "levée", paramXYDataset);
		return localJFreeChart;
	}

	public void updateDatasetFromCamshaft(){
		Camshaft camshaft = CamWerks.getInstance().getCamshaft();
		series.removeAllSeries();
		
	    //   Rendering serie ( handeling null values )
	    /*
	    DefaultPolarItemRenderer ren = new DefaultPolarItemRenderer () {
			private static final long serialVersionUID = 1L;

			@Override
	        public void drawSeries (Graphics2D g2, Rectangle2D dataArea,
	            PlotRenderingInfo info, PolarPlot plot, XYDataset dataset, int seriesIndex)
	        {
	            boolean newPath = true;
	            GeneralPath polyline = new GeneralPath ();
	            int numPoints = dataset.getItemCount (seriesIndex);
	            for (int i = 0 ; i < numPoints - 1 ; i++)
	            {
	                double theta = dataset.getXValue (seriesIndex, i);
	                double radius = dataset.getYValue (seriesIndex, i);

	                Point p = plot.translateToJava2D(theta, radius, plot.getAxis(), dataArea);
	                
	                if (p.x == 0 && p.y == 0)
	                {
	                    newPath = true;
	                }
	                else
	                {
	                    if (newPath)
	                    {
	                        polyline.moveTo (p.x, p.y);
	                        newPath = false;
	                    }
	                    else
	                    {
	                        polyline.lineTo (p.x, p.y);
	                    }
	                }
	            }
	            g2.setPaint (lookupSeriesPaint (seriesIndex));
	            g2.setStroke (lookupSeriesStroke (seriesIndex));
	            g2.draw (polyline);
	        }
	    };
	    */

		
		PolarPlot polarPlot = (PolarPlot) chart.getPlot();
//		polarPlot.setRenderer(ren);
        //PolarItemRenderer renderer = xyPlot.getRenderer();
        polarPlot.setBackgroundPaint (Color.white);
        polarPlot.setAngleGridlinePaint (Color.lightGray);
        polarPlot.setRadiusGridlinePaint(Color.lightGray);
        //polarPlot.
       
        //int i=0; 
		for (String key : camshaft.getKeys() ) {
			Cam cam = camshaft.getCam(key);
			cam.normalizeValues();
//	        renderer.setSeriesPaint(i, Color.blue);
	        String legend = Lang.getText(LangEntry.CHART_LEGEND_CYL) +" " + cam.getCylNumber() + "/"+ Lang.getText(LangEntry.CHART_LEGEND_INT) + " " + cam.getCamNumber(); //(i+1)
			if(camshaft.getCam(key).isExhaust()){
//		        renderer.setSeriesPaint(i, Color.red);
		        legend = Lang.getText(LangEntry.CHART_LEGEND_CYL) +" " + cam.getCylNumber() + "/" + Lang.getText(LangEntry.CHART_LEGEND_EXH) + " " + cam.getCamNumber(); //(i+1)
			}

		
			
			
/*
**** ALGO JB : ****

base = 12;

N = size(raw, 1);
dth = .6;
th = dth * raw(:, 1)' / 180 * pi;
l = raw(:, 2)' + base;
dl = diff(l) ./ diff(th);
dl = [ dl ( l(1)-l(end) )/( th(1)+360-th(end) ) ];

cth = cos(th);
sth = sin(th);
x = l.*cth - dl.*sth;
y = l.*sth + dl.*cth;

*/
			XYSeries  xys = new XYSeries(legend );
			for (int j = 0; j < camshaft.getNbSteps() ; j=j+1) { //camshaft.getNbSteps()

				double l = cam.getValue(j)+baseCircle;

				int prev_j = j-1;
				if (prev_j==-1) prev_j=camshaft.getNbSteps()-1;
//System.out.println("J="+j + "  prevJ=" + prev_j);

				double th = j * (360.0F / camshaft.getNbSteps()) ; 
				double prev_th = prev_j * (360.0F / camshaft.getNbSteps()) ; 
				//double radA = Math.toRadians(degA);


				double dl =  (cam.getValue(j) - cam.getValue(prev_j)) / (th - prev_th);
				double cth = Math.cos(Math.toRadians(th));
				double sth = Math.sin(Math.toRadians(th));
                
				double x = l * cth - dl * sth;
				double y = l * sth + dl * cth;

// now, let's convert this shit to polar diagram...
   				double distance = Math.sqrt(x*x + y*y);
				double angle = Math.toDegrees( Math.atan2(y,x) );

				//xys.add( th, l );
				xys.add( angle, distance );
			}

		    series.addSeries(xys);
		
		}



		
		chart.setTitle(camshaft.getName());
		
		chart.fireChartChanged();
	}
	

}

