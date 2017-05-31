package CIS6905;

import java.io.File;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

import CIS6905.waveforms.ClassicSawtooth;

public class MIDIController implements Receiver {
	
	WavetableOscillator sine = new WavetableOscillator(new ClassicSawtooth());
	double[] envelope = new BreakpointFunction(512, new double[]{1, 0.5, 0.5, 0.5, 0.5, 0.5, 0}).get();

	/*
	 * noteon = -112
	 * noteoff = -128
	 * pitchBend = -32
	 * volume, pan, modWheel = -80
	 * 
	 * volume = 7
	 * pan = 10
	 * pitchBend = garbage
	 * modWheel = 1
	 */
	@Override
	public void send(MidiMessage message, long timeStamp) {

		if (message.getMessage()[0] == -112) { // filter note off
			sine.get(1000, envelope, midiToFrequency(message.getMessage()[1]));
		}

	}

	@Override
	public void close() {}

	public double midiToFrequency(int note) {
		return 440 * Math.pow(2, ((double) note - 69) / 12);
	}

	void open() throws Exception {
		final int NOTE_ON = 0x90;
		final int NOTE_OFF = 0x80;
		final String[] NOTE_NAMES = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};

		Sequence sequence = MidiSystem.getSequence(new File("test.mid"));

		int trackNumber = 0;
		for (Track track :  sequence.getTracks()) {
			trackNumber++;
			System.out.println("Track " + trackNumber + ": size = " + track.size());
			System.out.println();
			for (int i=0; i < track.size(); i++) { 
				MidiEvent event = track.get(i);
				System.out.print("@" + event.getTick() + " ");
				MidiMessage message = event.getMessage();
				if (message instanceof ShortMessage) {
					ShortMessage sm = (ShortMessage) message;
					System.out.print("Channel: " + sm.getChannel() + " ");
					if (sm.getCommand() == NOTE_ON) {
						int key = sm.getData1();
						int octave = (key / 12)-1;
						int note = key % 12;
						String noteName = NOTE_NAMES[note];
						int velocity = sm.getData2();
						System.out.println("Note on, " + noteName + octave + " key=" + key + " velocity: " + velocity);
					} else if (sm.getCommand() == NOTE_OFF) {
						int key = sm.getData1();
						int octave = (key / 12)-1;
						int note = key % 12;
						String noteName = NOTE_NAMES[note];
						int velocity = sm.getData2();
						System.out.println("Note off, " + noteName + octave + " key=" + key + " velocity: " + velocity);
					} else {
						System.out.println("Command:" + sm.getCommand());
					}
				} else {
					System.out.println("Other message: " + message.getClass());
				}
			}
		}
	}

}