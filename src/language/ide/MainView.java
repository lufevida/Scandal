package language.ide;

import java.io.File;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class MainView extends Application {

	static final TabPane pane = new TabPane();
	static final Accordion accordion = new Accordion();
	public static final TextArea console = new TextArea(); // TODO print exceptions

	public void start(Stage stage) throws Exception {
		Font.loadFont(getClass().getResourceAsStream("/language/ide/fontawesome-webfont.ttf"), 0);
		stage.setScene(getScene());
		stage.setTitle("Scandal");
		//stage.setMaximized(true);
		stage.show();
	}
	
	private Scene getScene() {
		HBox box = new HBox();
		box.widthProperty().addListener((obs, old, val) -> resize(val));
		HBox.setHgrow(pane, Priority.ALWAYS);
		HBox.setHgrow(accordion, Priority.ALWAYS);
		console.textProperty().addListener((obs, old, val) -> accordion.setExpandedPane(accordion.getPanes().get(1)));
		accordion.getPanes().addAll(getBrowser(), new TitledPane("Console", console));
		accordion.setExpandedPane(accordion.getPanes().get(0));
		box.getChildren().addAll(pane, accordion);
		BorderPane root = new BorderPane();
		root.setCenter(box);
		root.setTop(getMenus());
		return new Scene(root, 1024, 768);
	}
	
	private void resize(Number val) {
		pane.setPrefWidth((double) val * 0.7);
		accordion.setPrefWidth((double) val * 0.3);
	}
	
	private TitledPane getBrowser() {
		TreeView<String> tree = new TreeView<>(new FileTreeItem());
		tree.getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> addTab(((FileTreeItem) val).file));
		return new TitledPane("Browser", tree);
	}
	
	static void addTab(File file) {
		if (file == null || skipFile(file) || alreadyOpen(file)) return;
		pane.getTabs().add(new FileTab(file));
		pane.getSelectionModel().select(pane.getTabs().size() - 1);
		return;
	}
	
	static boolean skipFile(File file) {
		if (file.isDirectory()) return true;
		String name = file.getPath();
		int dot = name.lastIndexOf('.') + 1;
		return !name.substring(dot, name.length()).equals("scandal");
	}
	
	static boolean alreadyOpen(File file) {
		for (Tab tab : pane.getTabs()) if (((FileTab) tab).file.getPath().equals(file.getPath())) {
			pane.getSelectionModel().select(pane.getTabs().indexOf(tab));
			return true;
		}
		return false;
	}
	
	private MenuBar getMenus() {
		Menu fileMenu = new Menu("File");
		fileMenu.getItems().add(MenuOperation.NEW.item);
		fileMenu.getItems().add(MenuOperation.OPEN.item);
		fileMenu.getItems().add(MenuOperation.SAVE.item);
		fileMenu.getItems().add(MenuOperation.CLOSE.item);
		fileMenu.getItems().add(new SeparatorMenuItem());
		fileMenu.getItems().add(MenuOperation.RUN.item);
		fileMenu.getItems().add(MenuOperation.PAUSE.item);
		MenuBar bar = new MenuBar();
		bar.setUseSystemMenuBar(true);
		bar.getMenus().add(fileMenu);
		return bar;
	}

	/*private ToolBar getToolbar() {
		Button buttonLeft = new Button();
		buttonLeft.setText(FontAwesome.STOP.unicode);
		Button buttonRight = new Button();
		buttonRight.setText(FontAwesome.PLAY.unicode);
		ToolBar toolbar = new ToolBar();
		toolbar.getItems().addAll(buttonLeft, buttonRight);
		return toolbar;
	}*/

	public static void main(String[] args) {
		launch(args);
	}

}
