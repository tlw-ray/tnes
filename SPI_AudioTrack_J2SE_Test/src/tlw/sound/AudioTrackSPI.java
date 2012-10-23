package tlw.sound;

public class AudioTrackSPI {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		AudioTrack at=AudioTrackFactory.getAudioTrack(
				AudioTrack.SAMPLE_RATE_11025, 
				AudioTrack.BIT_16, 
				AudioTrack.CHANNEL_MONO, 
				true, false);
		System.out.println(at);
	}

}
