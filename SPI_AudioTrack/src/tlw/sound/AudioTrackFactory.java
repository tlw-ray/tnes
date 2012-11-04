package tlw.sound;

import java.util.ServiceLoader;

public class AudioTrackFactory {
	public static void main(String[] args){
		AudioTrack at=AudioTrackFactory.getAudioTrack(
				AudioTrack.SAMPLE_RATE_11025, 
				AudioTrack.BIT_16, 
				AudioTrack.CHANNEL_MONO, 
				true, false);
		System.out.println(at);
	}
	private static ServiceLoader<? extends AudioTrack> serviceLoader = ServiceLoader.load(AudioTrack.class); 
	//interface SourceDataLine supporting format PCM_SIGNED 11025.0 Hz, 16 bit, mono, 2 bytes/frame, little-endian
	public static AudioTrack getAudioTrack(float sampleRate,
            int sampleSizeInBits,
            int channels,
            boolean signed,
            boolean bigEndian){
		for(AudioTrack service : serviceLoader){
			service.init(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
			return service;
		}
		return null;
		
//		AudioTrack audioTrack=null;
		
		//swing
//		try {
//			AudioFormat audioFormat=new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
//			DataLine.Info info = new DataLine.Info(SourceDataLine.class,audioFormat);
//			SourceDataLine line = (SourceDataLine)AudioSystem.getLine(info);
//			AudioTrackJ2se audioTrackJ2se=new AudioTrackJ2se();
//			audioTrackJ2se.setSourceDataLine(line);
//			audioTrack=audioTrackJ2se;
//		} catch (LineUnavailableException e) {
//			e.printStackTrace();
//		}
//		
//		return audioTrack;
	}
}
