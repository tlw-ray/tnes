package tlw.sound;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;


public class AudioTrackFactory {
	public static AudioTrack createAudioTrack(float sampleRate,
            int sampleSizeInBits,
            int channels,
            boolean signed,
            boolean bigEndian){
		try{
			URL u=AudioTrackFactory.class.getResource("tlw.sound.AudioTrack");
			InputStream is=u.openStream();
			InputStreamReader isr=new InputStreamReader(is);
			int ch;
			String implName="";
			while((ch=isr.read())>0){
				implName+=(char)ch;
			}
			Class<? extends AudioTrack> audioTrackClass=(Class<? extends AudioTrack>) Class.forName(implName);
			AudioTrack audioTrack=(AudioTrack) audioTrackClass.newInstance();
			audioTrack.init(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
			return audioTrack;
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return null;
	}
}
