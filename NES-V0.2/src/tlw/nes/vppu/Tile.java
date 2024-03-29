package tlw.nes.vppu;
import tlw.nes.vmemory.ByteBuffer;

public class Tile{
	
	// Tile data:
	private int[] pix;
	
	private int fbIndex;
	private int tIndex;
	private int x,y;
	private int palIndex;
	private int tpri;
	private boolean initialized = false;
	private boolean[] opaque = new boolean[8];
	
	public Tile(){
		pix = new int[64];
	}
	
	public void setScanline(int sline, short b1, short b2){
		initialized = true;
		tIndex = sline<<3;
		for(x=0;x<8;x++){
			pix[tIndex+x] = ((b1>>(7-x))&1) + (((b2>>(7-x))&1)<<1);
			if(pix[tIndex+x]==0)opaque[sline]=false;
		}
	}
	
	public void render(int srcx1, int srcy1, int srcx2, int srcy2, int dx, int dy, int[] fBuffer, int palAdd, int[] palette, boolean flipHorizontal, boolean flipVertical, int pri, int[] priTable){
	
		if(dx<-7 || dx>=256 || dy<-7 || dy>=240){
			return;
		}
	
//		w=srcx2-srcx1;
//		h=srcy2-srcy1;
		
		if(dx<0){
			srcx1-=dx;
		}
		if(dx+srcx2>=256){
			srcx2=256-dx;
		}
		
		if(dy<0){
			srcy1-=dy;
		}
		if(dy+srcy2>=240){
			srcy2=240-dy;
		}
		
		if(!flipHorizontal && !flipVertical){
			
			fbIndex = (dy<<8)+dx;
			tIndex = 0;
			for(y=0;y<8;y++){
				for(x=0;x<8;x++){
					if(x>=srcx1 && x<srcx2 && y>=srcy1 && y<srcy2){
						palIndex = pix[tIndex];
						tpri = priTable[fbIndex];
						if(palIndex!=0 && pri<=(tpri&0xFF)){
							fBuffer[fbIndex] = palette[palIndex+palAdd];
							tpri = (tpri&0xF00)|pri;
							priTable[fbIndex] =tpri;
						}
					}
					fbIndex++;
					tIndex++;
				}
				fbIndex-=8;
				fbIndex+=256;
			}
			
		}else if(flipHorizontal && !flipVertical){
			
			fbIndex = (dy<<8)+dx;
			tIndex = 7;
			for(y=0;y<8;y++){
				for(x=0;x<8;x++){
					if(x>=srcx1 && x<srcx2 && y>=srcy1 && y<srcy2){
						palIndex = pix[tIndex];
						tpri = priTable[fbIndex];
						if(palIndex!=0 && pri<=(tpri&0xFF)){
							fBuffer[fbIndex] = palette[palIndex+palAdd];
							tpri = (tpri&0xF00)|pri;
							priTable[fbIndex] =tpri;
						}
					}
					fbIndex++;
					tIndex--;
				}
				fbIndex-=8;
				fbIndex+=256;
				tIndex+=16;
			}
			
		}else if(flipVertical && !flipHorizontal){
			
			fbIndex = (dy<<8)+dx;
			tIndex = 56;
			for(y=0;y<8;y++){
				for(x=0;x<8;x++){
					if(x>=srcx1 && x<srcx2 && y>=srcy1 && y<srcy2){
						palIndex = pix[tIndex];
						tpri = priTable[fbIndex];
						if(palIndex!=0 && pri<=(tpri&0xFF)){
							fBuffer[fbIndex] = palette[palIndex+palAdd];
							tpri = (tpri&0xF00)|pri;
							priTable[fbIndex] =tpri;
						}
					}
					fbIndex++;
					tIndex++;
				}
				fbIndex-=8;
				fbIndex+=256;
				tIndex-=16;
			}
			
		}else{
			
			fbIndex = (dy<<8)+dx;
			tIndex = 63;
			for(y=0;y<8;y++){
				for(x=0;x<8;x++){
					if(x>=srcx1 && x<srcx2 && y>=srcy1 && y<srcy2){
						palIndex = pix[tIndex];
						tpri = priTable[fbIndex];
						if(palIndex!=0 && pri<=(tpri&0xFF)){
							fBuffer[fbIndex] = palette[palIndex+palAdd];
							tpri = (tpri&0xF00)|pri;
							priTable[fbIndex] =tpri;
						}
					}
					fbIndex++;
					tIndex--;
				}
				fbIndex-=8;
				fbIndex+=256;
			}
			
		}
		
	}
	
	public void stateSave(ByteBuffer buf){
		
		buf.putBoolean(initialized);
		for(int i=0;i<8;i++){
			buf.putBoolean(opaque[i]);
		}
		for(int i=0;i<64;i++){
			buf.putByte((byte)pix[i]);
		}
		
	}
	
	public void stateLoad(ByteBuffer buf){
		
		initialized = buf.readBoolean();
		for(int i=0;i<8;i++){
			opaque[i] = buf.readBoolean();
		}
		for(int i=0;i<64;i++){
			pix[i] = buf.readByte();
		}
		
	}

	public int[] getPix() {
		return pix;
	}

	public boolean[] getOpaque() {
		return opaque;
	}
	public String toString(){
		String result=super.toString()+"\n";
		for(int x=0;x<8;x++){
			for(int y=0;y<8;y++){
				int id=x*8+y;
				result+=pix[id];
			}
			result+="\n";
		}
		return result;
	}
}