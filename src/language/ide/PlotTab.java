package language.ide;

import java.io.File;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

public class PlotTab extends FileTab {

	public PlotTab(String title, float[] array, int size) {
		super(new File(title));
		float increment = (float) array.length / size;
		XYChart.Series<Number, Number> series = new XYChart.Series<Number, Number>();		
		series.setName(title);
		for (int i = 0; i < size; i++)
			series.getData().add(new XYChart.Data<Number, Number>(i, array[(int) (i * increment)]));
		LineChart<Number,Number> lineChart = new LineChart<Number,Number>(new NumberAxis(), new NumberAxis());
		lineChart.getData().add(series);
		lineChart.setCreateSymbols(size < 100);
		setContent(lineChart);
		add();
	}

}
