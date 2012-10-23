package tlw.sound;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class AudioTrackJ2se implements AudioTrack {
	SourceDataLine sourceDataLine;
	public void write(byte[] audioData, int off, int len) {
		sourceDataLine.write(audioData, off, len);
	}
	
	public void play() {
		try {
			sourceDataLine.open();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
		sourceDataLine.start();
	}

	public boolean isActive() {
		return sourceDataLine.isActive();
	}

	public boolean isOpen() {
		return sourceDataLine.isOpen();
	}
	public int available(){
		return sourceDataLine.available();
	}
	public void close() {
		sourceDataLine.close();
	}

	public int getBufferSize() {
		return sourceDataLine.getBufferSize();
	}

	public SourceDataLine getSourceDataLine() {
		return sourceDataLine;
	}
	public void setSourceDataLine(SourceDataLine sourceDataLine) {
		this.sourceDataLine = sourceDataLine;
	}


}
