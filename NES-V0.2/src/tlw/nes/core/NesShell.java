package tlw.nes.core;

import java.io.InputStream;


public interface NesShell {
	InputHandler getJoy1();
	InputHandler getJoy2();
	void display(int[] frameBuffer);
	void play(int[] soundBuffer);
	void load(InputStream rom);
	void reset();
}
