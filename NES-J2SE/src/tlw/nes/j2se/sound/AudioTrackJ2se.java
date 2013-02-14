package tlw.nes.j2se.sound;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import tlw.nes.AudioTrack;

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
//		return sourceDataLine.isActive();
		//TODO 参考实际需要实现
		return false;
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

	@Override
	public void init(float sampleRate, int sampleSizeInBits, int channels,
			boolean signed, boolean bigEndian) {
		AudioFormat audioFormat = new AudioFormat(sampleRate,sampleSizeInBits,channels,signed,bigEndian);
		DataLine.Info info = new DataLine.Info(SourceDataLine.class,audioFormat);
		try {
			sourceDataLine = (SourceDataLine)AudioSystem.getLine(info);
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
		
	}


}
