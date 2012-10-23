package tlw.nes2;

import java.io.InputStream;

public interface NES {
	void reset();
	void loadRom(InputStream is);
	void save();
	void load(InputStream is);
}