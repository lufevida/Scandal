package framework.utilities;

import javafx.application.Platform;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import language.ide.MainView;

public class PlotUtility {
	
	private final String title;
	private final float[] array;

	public PlotUtility(String title, float[] array) {
		this.title = title;
		this.array = array;
		getScene();
	}

	public PlotUtility(String title, float[] array, int size) {
		this.title = title;
		this.array = new float[size];
		float increment = (float) array.length / size;
		for (int i = 0; i < size; i++) this.array[i] = array[(int) (i * increment)];
		getScene();
	}
	
	private void getScene() {
		XYChart.Series<Number, Number> series = new XYChart.Series<Number, Number>();		
		for (int i = 0; i < array.length; i++) series.getData().add(new XYChart.Data<Number, Number>(i, array[i]));
		series.setName(title);
		final NumberAxis xAxis = new NumberAxis();
		final NumberAxis yAxis = new NumberAxis();
		//xAxis.setLabel("x-Axis Label");
		//yAxis.setLabel("y-Axis Label");
		final LineChart<Number,Number> lineChart = new LineChart<Number,Number>(xAxis,yAxis);
		lineChart.getData().add(series);
		lineChart.setCreateSymbols(false);
		//lineChart.setTitle(title);
		Platform.runLater(() -> MainView.addTab(title, lineChart));
	}

}
