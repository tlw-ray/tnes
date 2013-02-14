package tlw.nes;

import java.io.InputStream;


public interface NesShell {
	InputHandler getJoy1();
	InputHandler getJoy2();
	AudioTrack getAudioTrack();
	void display(int[] frameBuffer);
	void play(int[] soundBuffer);
	void load(InputStream rom);
	void reset();
}
