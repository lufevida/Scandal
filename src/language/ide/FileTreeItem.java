package language.ide;

import java.io.File;

import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.text.Font;

public class FileTreeItem extends TreeItem<String> {

	public final File file;
	public final Label label = new Label();
	
	FileTreeItem() {
		super("Computer");
		file = new File("/");
		label.setText(FontAwesome.LAPTOP.unicode);
		label.setFont(Font.font("FontAwesome"));
		setGraphic(label);
		fetch();
		setExpanded(true);
	}
	
	FileTreeItem(File file) {
		super(file.getName());
		this.file = file;
		label.setText(file.isDirectory() ? FontAwesome.FOLDER.unicode : FontAwesome.FILE_TEXT.unicode);
		label.setFont(Font.font("FontAwesome"));
		setGraphic(label);
		if (file.isDirectory()) {
			getChildren().add(new TreeItem<>(""));
			expandedProperty().addListener((obs, old, val) -> update(val));
		}
	}
	
	public void update(boolean val) {
		label.setText(val ? FontAwesome.FOLDER_OPEN.unicode : FontAwesome.FOLDER.unicode);
		if (val) {
			getChildren().clear();
			fetch();
		}
	}

	private void fetch() {
		for (File f : file.listFiles()) if (!f.isHidden()) getChildren().add(new FileTreeItem(f));
	}

}
