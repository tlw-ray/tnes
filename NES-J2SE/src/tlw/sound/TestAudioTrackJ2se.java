package tlw.sound;

public class TestAudioTrackJ2se {

	/**
	 * @param args
	 */
	public static void main(String[] args){
		AudioTrack at=AudioTrackFactory.createAudioTrack(
				AudioTrack.SAMPLE_RATE_11025, 
				AudioTrack.BIT_16, 
				AudioTrack.CHANNEL_MONO, 
				true, false);
		System.out.println(at);
	}

}
