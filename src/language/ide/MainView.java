package language.ide;

import java.io.File;
import java.nio.file.FileSystems;

import javafx.application.Application;
import javafx.scene.Node;
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
	
	// TODO change all counters to double to increase precision

	public static final TabPane pane = new TabPane();
	static final Accordion accordion = new Accordion();
	public static final TextArea console = new TextArea(); // TODO print exceptions, make it a text flow
	static final TitledPane consolePane = new TitledPane("Console", console);

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
		console.textProperty().addListener((obs, old, val) -> accordion.setExpandedPane(consolePane));
		accordion.getPanes().addAll(getBrowser(), getPane("lib", "Examples"), getSamples(), consolePane);
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
	
	private TitledPane getSamples() {
		File f = FileSystems.getDefault().getPath("wav").toFile();
	    TreeView<String> tree = new TreeView<>(new FileTreeItem(f));
	    tree.setShowRoot(false);
		tree.getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> addMediaTab(((FileTreeItem) val).file));
		return new TitledPane("Samples", tree);
	}

	static void addMediaTab(File file) {
		if (alreadyOpen(file)) return;
		pauseOthers();
		pane.getTabs().add(new MediaTab(file));
		pane.getSelectionModel().select(pane.getTabs().size() - 1);
	}
	
	private TitledPane getPane(String path, String title) {
		File f = FileSystems.getDefault().getPath(path).toFile();
	    TreeView<String> tree = new TreeView<>(new FileTreeItem(f));
	    tree.setShowRoot(false);
		tree.getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> addTab(((FileTreeItem) val).file));
		return new TitledPane(title, tree);
	}

	static void addTab(File file) {
		if (file == null || skipFile(file) || alreadyOpen(file)) return;
		pane.getTabs().add(new ScandalTab(file));
		pane.getSelectionModel().select(pane.getTabs().size() - 1);
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
	
	static void pauseOthers() {
		for (Tab tab : pane.getTabs()) if (tab instanceof MediaTab) ((MediaTab) tab).pause();
	}
	
	public static void addTab(String title, Node node) {
		Tab tab = new Tab(title);
		tab.setContent(node);
		MainView.pane.getTabs().add(tab);
		MainView.pane.getSelectionModel().select(MainView.pane.getTabs().size() - 1);
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
