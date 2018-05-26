package language.ide.widget;

import javafx.scene.control.MenuItem;

public enum WidgetMenu {

	CLASSIC("Analog Synthesizer"),
	KARPLUS("Karplus-Strong Synthesizer"),
    ADDITIVE("Additive Synthesizer"),
    GRANULAR("Granular Synthesizer"),
    GRANULATOR("Granulator Effect");

    public final MenuItem item;

    WidgetMenu(String name) {
        item = new MenuItem(name);
        item.setOnAction(e -> action());
    }

    void action() {
        try {
            switch (this) {
            case CLASSIC:
            	new ClassicSynth().add();
            	return;
            case KARPLUS:
                new KarplusSynth().add();
                return;
            case ADDITIVE:
            	new AdditiveSynth().add();
            	return;
            case GRANULAR:
            	new GranularSynth().add();
            	return;
            case GRANULATOR:
                new GranulatorEffect().add();
                return;
            default: return;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
