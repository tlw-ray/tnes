package tlw.nes.vrom;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileLoader{
	static final int BUFFER_SIZE=32768;
	// Load a file.
	public short[] loadFile(String fileName){
		
		int flen;
		byte[] tmp = new byte[0];
		
		// Read file:
		try{
			
			InputStream in = getClass().getResourceAsStream(fileName);
			
			if(in==null){
				// Try another approach.
				in = new FileInputStream(fileName);
			}
			
			int pos=0;
			int readbyte=0;
			if(in instanceof FileInputStream){
				// This is easy, can find the file size since it's
				// in the local file system.
				File f = new File(fileName);
				int total = (int)(f.length());
				tmp = new byte[total];
				in.read(tmp,0,total);
				pos = total;
			}else{
				//from net work
				while(readbyte != -1){
					readbyte = in.read(tmp,pos,tmp.length-pos);
					if(readbyte != -1){
						if(pos>=tmp.length){
							byte[] newtmp = new byte[tmp.length+BUFFER_SIZE];
							System.arraycopy(tmp, 0, newtmp, 0, tmp.length);
							tmp = newtmp;
						}
						pos+=readbyte;
					}
				}
			}

			// File size:
			flen = tmp.length;
			
		}catch(IOException ioe){
			// Something went wrong.
			ioe.printStackTrace();
			return null;
			
		}
		
		//TO 16 BIT
		short[] ret = new short[flen];
		for(int i=0;i<flen;i++){
			ret[i] = (short)(tmp[i]&255);
		}
		return ret;
		
	}
}	