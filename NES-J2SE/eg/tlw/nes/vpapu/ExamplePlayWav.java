package tlw.nes.vpapu;

import java.io.File;
import java.io.FileInputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

public class ExamplePlayWav {
	static File file=new File("test-wav.wav");
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		play0();
//		play1();
		
		System.out.println("finish...");
		
		
		
	}
	public static void play0()throws Exception{
		FileInputStream fis=new FileInputStream(file);
		int bufferSize=1024;
//		int sampleRate = 44100;
		int sampleRate = 11025;
		AudioFormat audioFormat = new AudioFormat(sampleRate,16,1,true,false);
		DataLine.Info info = new DataLine.Info(SourceDataLine.class,audioFormat);
		SourceDataLine line = (SourceDataLine)AudioSystem.getLine(info);
		line.open(audioFormat,bufferSize);
		line.start();
		
		byte[] buffer=new byte[bufferSize];
		while(fis.read(buffer)>0){
			line.write(buffer, 0, bufferSize);
		}
		
		line.close();
		fis.close();
	}
	public static void play1() throws Exception{
		AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file); 
		AudioFormat audioFormat = audioInputStream.getFormat(); 
		DataLine.Info dataLine_info = new DataLine.Info(SourceDataLine.class,audioFormat); 
		SourceDataLine sourceDataLine = (SourceDataLine)AudioSystem.getLine(dataLine_info);
		
		System.out.println(dataLine_info);
		
		int bufferSize=2048;	//缓冲区不能小于1024
		
		byte[] b = new byte[bufferSize]; 
		int len = 0; 
		sourceDataLine.open(audioFormat, bufferSize);
		sourceDataLine.start();
		
		while ((len = audioInputStream.read(b)) > 0){ 
			sourceDataLine.write(b, 0, len);
//			System.out.println("cc");//阻塞的
		} 
		
		audioInputStream.close(); 
		sourceDataLine.drain(); 
		sourceDataLine.close(); 
	}
}
