package language.ide;

import java.io.File;

import framework.generators.MidiKeyboardController;
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
import javafx.scene.text.Font;
import javafx.stage.Stage;
import language.ide.widget.WidgetMenu;

public class MainView extends Application {

	public static final TabPane pane = new TabPane();
	static final Accordion accordion = new Accordion();
	public static final TextArea console = new TextArea();
	static final TitledPane consolePane = new TitledPane("Console", console);

	public void start(Stage stage) {
		Font.loadFont(getClass().getResourceAsStream("/language/ide/fontawesome-webfont.ttf"), 0);
		stage.setTitle("Scandal");
		stage.setMaximized(false);
		stage.setScene(getScene());
		stage.setOnCloseRequest(e -> deinit());
		stage.show();
	}
	
	private void deinit() {
		if (MidiKeyboardController.device != null) MidiKeyboardController.device.close();
		System.exit(0);
	}
	
	private Scene getScene() {
		HBox box = new HBox();
		box.widthProperty().addListener((obs, old, val) -> resize(val));
		console.textProperty().addListener((obs, old, val) -> accordion.setExpandedPane(consolePane));
		accordion.getPanes().addAll(getSettings(), getBrowser(), getLib(), getSamples(), consolePane);
		accordion.setExpandedPane(accordion.getPanes().get(2));
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
	
	private TitledPane getSettings() {
		return new TitledPane("Settings", new SettingsBox());
	}
	
	private TitledPane getBrowser() {
		TreeView<String> tree = new TreeView<>(new FileTreeItem());
		tree.getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> addScandalTab(((FileTreeItem) val).file));
		return new TitledPane("Browser", tree);
	}
	
	private TitledPane getLib() {
		File f = new File(System.getProperty("user.dir") + "/lib");
		TreeView<String> tree = new TreeView<>(new FileTreeItem(f));
	    tree.setShowRoot(false);
		tree.getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> addScandalTab(((FileTreeItem) val).file));
		return new TitledPane("Examples", tree);
	}
	
	private TitledPane getSamples() {
		//File f = FileSystems.getDefault().getPath("wav").toFile();
		File f = new File(System.getProperty("user.dir") + "/wav");
	    TreeView<String> tree = new TreeView<>(new FileTreeItem(f));
	    tree.setShowRoot(false);
		tree.getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> addMediaTab(((FileTreeItem) val).file));
		return new TitledPane("Samples", tree);
	}
	
	static void addMediaTab(File file) {
		if (alreadyOpen(file)) return;
		for (Tab tab : pane.getTabs()) if (tab instanceof MediaTab) ((MediaTab) tab).pause();
		new MediaTab(file).add();
	}

	static void addScandalTab(File file) {
		if (file == null || skipFile(file) || alreadyOpen(file)) return;
		new ScandalTab(file).add();
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
		fileMenu.getItems().add(OperationMenu.NEW.item);
		fileMenu.getItems().add(OperationMenu.OPEN.item);
		fileMenu.getItems().add(OperationMenu.SAVE.item);
		fileMenu.getItems().add(OperationMenu.CLOSE.item);
		fileMenu.getItems().add(new SeparatorMenuItem());
		fileMenu.getItems().add(OperationMenu.RUN.item);
		fileMenu.getItems().add(OperationMenu.PAUSE.item);
		MenuBar bar = new MenuBar();
		bar.setUseSystemMenuBar(true);
		bar.getMenus().addAll(fileMenu, getWidgetsMenu());
		return bar;
	}

	private Menu getWidgetsMenu() {
		Menu widgets = new Menu("Widgets");
		widgets.getItems().add(WidgetMenu.CLASSIC.item);
		widgets.getItems().add(WidgetMenu.KARPLUS.item);
		widgets.getItems().add(WidgetMenu.ADDITIVE.item);
		widgets.getItems().add(WidgetMenu.GRANULAR.item);
		widgets.getItems().add(WidgetMenu.GRANULATOR.item);
		return widgets;
	}

	public static void main(String[] args) { launch(args); }

}
