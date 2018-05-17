package language.ide;

import javafx.scene.control.MenuItem;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.FileChooser;

public enum OperationMenu {
	
	NEW("New", KeyCode.N),
	OPEN("Open", KeyCode.O),
	SAVE("Save", KeyCode.S),
	CLOSE("Close", KeyCode.W),
	RUN("Run", KeyCode.R),
	PAUSE("Pause", KeyCode.P);
	
	final MenuItem item;
	final FileChooser chooser = new FileChooser();
	final TabPane pane = MainView.pane;
	
	OperationMenu(String name, KeyCode key) {
		item = new MenuItem(name);
		item.setAccelerator(new KeyCodeCombination(key, KeyCombination.SHORTCUT_DOWN));
		item.setOnAction(e -> action());
	}
	
	void action() {
		switch (this) {
		case NEW:
			MainView.addScandalTab(chooser.showSaveDialog(pane.getScene().getWindow()));
			return;
		case OPEN:
			MainView.addScandalTab(chooser.showOpenDialog(pane.getScene().getWindow()));
			return;
		default: break;
		}
		if (pane.getTabs().size() == 0) return;
		int t = pane.getSelectionModel().getSelectedIndex();
		FileTab tab = (FileTab) pane.getTabs().get(t);
		switch (this) {
		case SAVE:
			tab.save();
			return;
		case CLOSE:
			tab.save();
			tab.pause();
			pane.getTabs().remove(t);
			return;
		case RUN:
			tab.save();
			tab.run();
			return;
		case PAUSE:
			tab.pause();
			return;
		default: return;
		}
	}

}
