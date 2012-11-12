package tlw.sound;

import android.media.AudioManager;

public class AudioTrackAndroid implements AudioTrack{
	android.media.AudioTrack audioTrack;
	@Override
	public void write(byte[] audioData, int offsetInBytes, int sizeInBytes) {
		audioTrack.write(audioData, offsetInBytes, sizeInBytes);
	}

	@Override
	public void play() {
		audioTrack.play();
	}

	@Override
	public boolean isActive() {
		return audioTrack.getState()==android.media.AudioTrack.PLAYSTATE_PLAYING;
	}

	@Override
	public boolean isOpen() {
		return audioTrack.getState()==android.media.AudioTrack.PLAYSTATE_PLAYING;
	}

	@Override
	public void close() {
		audioTrack.stop();
	}

	@Override
	public int available() {
		return audioTrack.getPositionNotificationPeriod();
	}

	@Override
	public int getBufferSize() {
		return android.media.AudioTrack.getMinBufferSize(
				audioTrack.getSampleRate(), 
				audioTrack.getChannelConfiguration(), 
				audioTrack.getAudioFormat());
	}

	@Override
	public void init(float sampleRate, int sampleSizeInBits, int channels,
			boolean signed, boolean bigEndian) {
		if(sampleSizeInBits==16){
			sampleSizeInBits=android.media.AudioFormat.ENCODING_PCM_16BIT;
		}else{
			sampleSizeInBits=android.media.AudioFormat.ENCODING_PCM_8BIT;
		}
		int minBufferSize=android.media.AudioTrack.getMinBufferSize((int)sampleRate,channels,sampleSizeInBits);//TODO Android-API3 support;
//		int minBufferSize=2048;
		audioTrack=new android.media.AudioTrack(
				AudioManager.STREAM_MUSIC,
				(int)sampleRate,
				channels,
				sampleSizeInBits,
				minBufferSize,
				android.media.AudioTrack.MODE_STREAM);
	}

}
