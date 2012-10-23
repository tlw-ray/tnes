package tlw.android.util;

import android.media.AudioTrack;

public class SourceDataLine extends AudioTrack{

	public SourceDataLine(int streamType, int sampleRateInHz,
			int channelConfig, int audioFormat, int bufferSizeInBytes, int mode)
			throws IllegalArgumentException {
		super(streamType, sampleRateInHz, channelConfig, audioFormat,
				bufferSizeInBytes, mode);
	
	}
	public boolean isActive(){
		return getState()==AudioTrack.PLAYSTATE_PLAYING;
	}

	public boolean isOpen(){
		return getState()==AudioTrack.PLAYSTATE_PLAYING;
	}
	public int available(){
		return this.getNotificationMarkerPosition();
	}
	public void close(){
		super.stop();
	}
}
