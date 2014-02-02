package tlw.nes.interf;

public interface AudioTrack {
	//TODO ¿É¼ò»¯
	static final int SAMPLE_RATE_11025=11025;
	static final int SAMPLE_RATE_22050=22050;
	static final int SAMPLE_RATE_44100=44100;
	static final int CHANNEL_MONO=2;
	static final int CHANNEL_STEREO=3;
	static final int BIT_8=8;
	static final int BIT_16=16;
	void write(byte[] audioData, int off, int len);
	void play();
	boolean isActive();
	boolean isOpen();
	void close();
	int available();
	int getBufferSize();
	
	void init(float sampleRate,
		    int sampleSizeInBits,
		    int channels,
		    boolean signed,
		    boolean bigEndian);
}
