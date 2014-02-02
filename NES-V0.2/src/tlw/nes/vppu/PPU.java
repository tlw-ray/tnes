package tlw.nes.vppu;

//import java.awt.Image;
//import java.awt.image.BufferedImage;
import java.util.logging.Logger;

import tlw.nes.NES;
import tlw.nes.vcpu.CPU6502;
import tlw.nes.vmemory.ByteBuffer;
import tlw.nes.vmemory.Memory;
import tlw.nes.vrom.ROM;

public class PPU{
	
//	--------------------------------------- $4000
//	 Empty
//	--------------------------------------- $3F20
//	 Sprite Palette
//	--------------------------------------- $3F10
//	 Image Palette
//	--------------------------------------- $3F00
//	 Empty
//	--------------------------------------- $3000
//	 Attribute Table 3
//	--------------------------------------- $2FC0
//	 Name Table 3 (32x30 tiles)
//	--------------------------------------- $2C00
//	 Attribute Table 2
//	--------------------------------------- $2BC0
//	 Name Table 2 (32x30 tiles)
//	--------------------------------------- $2800
//	 Attribute Table 1
//	--------------------------------------- $27C0
//	 Name Table 1 (32x30 tiles)
//	--------------------------------------- $2400
//	 Attribute Table 0
//	--------------------------------------- $23C0
//	 Name Table 0 (32x30 tiles)
//	--------------------------------------- $2000
//	 Pattern Table 1 (256x2x8, may be VROM)
//	--------------------------------------- $1000
//	 Pattern Table 0 (256x2x8, may be VROM)
//	--------------------------------------- $0000
	
	//PPU Memory Map FROM 0x4000
	public static final int MM_EMPTY01=			0x4000;
	public static final int MM_SPRITE_PALETTE=	0x3F20;
	public static final int MM_IMAGE_PALETTE=	0x3F10;
	public static final int MM_EMPTY02=			0x3F00;
	public static final int MM_ATTRIBUTE_TABLE3=0x3000;
	public static final int MM_NAME_TABLE3=		0x2FC0;		//32x30 tiles
	public static final int MM_ATTRIBUTE_TABLE2=0x2C00;
	public static final int MM_NAME_TABLE2=		0x2BC0;		//32x30 tiles
	public static final int MM_ATTRIBUTE_TABLE1=0x2800;
	public static final int MM_NAME_TABLE1=		0x27C0;		//32x30 tiles
	public static final int MM_ATTRIBUTE_TABLE0=0x2400;
	public static final int MM_NAME_TABLE0=		0x23C0;		//32x30 tiles
	public static final int MM_PATTERN_TABLE1=	0x2000;		//256x2x8，可能是VROM
	public static final int MM_PATTERN_TABLE0=	0x1000;		//256x2x8，可能是VROM
	
	public static final int PIXEL_Y=240;
	public static final int PIXEL_X=256;
	
	public static final int NAME_TABLE_WIDTH=32;
	public static final int NAME_TABLE_HEIGHT=30;
	
	public static final int NAME_PATTERN_X=256;
	public static final int NAME_PATTERN_Y=2;
	public static final int NAME_PATTERN_Z=8;
	
	public static final int MODEL_SPRITE_SIZE_8_8=0;
	public static final int MODEL_SPRITE_SIZE_8_16=1;
	
	public static final int MODEL_VBLANK_INTERRUPTS=1;
	
	public static final int BUFFER_SIZE=PPU.PIXEL_X*PPU.PIXEL_Y;
	
	//showSpr0Hit
	public static boolean showSpr0Hit = true;
	//display sprite
	public static boolean disableSprites = false;
	//每秒60帧，一帧耗费的时间
	public static int frameTimeN =16666667;
	
	private NES nes;

	// Control Flags Register 1:
	private int     f_nmiOnVblank;    	// NMI on VBlank. 0=disable, 1=enable
	private int     f_spriteSize;     	// Sprite size. 0=8x8, 1=8x16
	private int     f_bgPatternTable; 	// Background Pattern Table address. 0=0x0000,1=0x1000
	private int     f_spPatternTable; 	// Sprite Pattern Table address. 0=0x0000,1=0x1000
	private int     f_addrInc;        	// PPU Address Increment. 0=1,1=32
	private int     f_nTblAddress;    	// Name Table Address. 0=0x2000,1=0x2400,2=0x2800,3=0x2C00

	// Control Flags Register 2:
	private int     f_color;	   	 	// Background color. 0=black, 1=blue, 2=green, 4=red
	private int     f_spVisibility;   	// Sprite visibility. 0=not displayed,1=displayed
	private int     f_bgVisibility;   	// Background visibility. 0=Not Displayed,1=displayed
	private int     f_spClipping;     	// Sprite clipping. 0=Sprites invisible in left 8-pixel column,1=No clipping
	private int     f_bgClipping;     	// Background clipping. 0=BG invisible in left 8-pixel column, 1=No clipping
	private int     f_dispType;       	// Display type. 0=color, 1=monochrome

	// Status flags:
	protected int     STATUS_VRAMWRITE = 4;
	protected int     STATUS_SLSPRITECOUNT = 5;
	protected int     STATUS_SPRITE0HIT = 6;
	protected int     STATUS_VBLANK = 7;

	// VRAM I/O:
	private int vramAddress;
	private int vramTmpAddress;
	private short vramBufferedReadValue;
	private boolean firstWrite=true; 		// VRAM/Scroll Hi/Lo latch

	private int[] vramMirrorTable; 			// Mirroring Lookup Table.

	// SPR-RAM I/O:
	private short sramAddress; 				// 8-bit only.

	// Counters:
	private int cntFV;
	private int cntV;
	private int cntH;
	private int cntVT;
	private int cntHT;

	// Registers:
	private int regFV;
	private int regV;
	private int regH;
	private int regVT;
	private int regHT;
	private int regFH;
	private int regS;

	// VBlank extension for PAL emulation:
	private int vblankAdd = 0;

	private int curX;
	private int scanline;
	private int lastRenderedScanline;
//	private int mapperIrqCounter;

	// Sprite data:
	private int[] sprX;				// X coordinate
	private int[] sprY;				// Y coordinate
	private int[] sprTile;			// Tile Index (into pattern table)
	private int[] sprCol;			// Upper two bits of color
	private boolean[] vertFlip;		// Vertical Flip
	private boolean[] horiFlip;		// Horizontal Flip
	private boolean[] bgPriority;	// Background priority
	private int spr0HitX;			// Sprite #0 hit X coordinate
	private int spr0HitY;			// Sprite #0 hit Y coordinate
	private boolean hitSpr0;

	// Tiles:
	private Tile[] ptTile;

	// Name table data:
	private int[] ntable1 = new int[4];
	private NameTable[] nameTable;
	private int currentMirroring=-1;

	// Palette data:
	private int[] sprPalette = new int[16];
	private int[] imgPalette = new int[16];

	// Misc:
	private boolean scanlineAlreadyRendered;
	private boolean requestEndFrame;
	private boolean nmiOk;
	private int nmiCounter;
	private short tmp;
	private boolean dummyCycleToggle;

	// Vars used when updating regs/address:
	private int address,b1,b2;

	// Variables used when rendering:
	private int[] attrib = new int[32];
	private int[] bufferBG = new int[PPU.PIXEL_X*PPU.PIXEL_Y];
	private int[] bufferPixRendered = new int[PPU.PIXEL_X*PPU.PIXEL_Y];
	private int[] buffer = new int[PPU.PIXEL_X*PPU.PIXEL_Y];
	private int[] screen = new int[PPU.PIXEL_X*PPU.PIXEL_Y];
	private int[] tpix;
	
	private boolean[] scanlineChanged = new boolean[PPU.PIXEL_Y];
	private boolean validTileData;
	private int att;

	private Tile[] scantile = new Tile[32];
	private Tile t;

	// These are temporary variables used in rendering and sound procedures.
	// Their states outside of those procedures can be ignored.
	private int curNt;
	private int destIndex;
	private int x;
	private int sx;
	private int si,ei;
	private int tile;
	private int col;
	private int baseTile;
	private int tscanoffset;
	private int srcy1, srcy2;
	private int bufferSize;
	private int available;

	private int cycles=0;


	public PPU(NES nes){
		this.nes = nes;
	}

	public void init(){
		// Get the memory:
		updateControlReg1(0);
		updateControlReg2(0);

		// Initialize misc vars:
		scanline = 0;
//		timer = nes.getGui().getTimer();

		// Create sprite arrays:
		sprX = new int[64];
		sprY = new int[64];
		sprTile = new int[64];
		sprCol = new int[64];
		vertFlip = new boolean[64];
		horiFlip = new boolean[64];
		bgPriority = new boolean[64];

		// Create pattern table tile buffers:
		if(ptTile==null){
			ptTile = new Tile[512];
			for(int i=0;i<512;i++){
				ptTile[i] = new Tile();
			}
		}

		// Create nametable buffers:
		nameTable = new NameTable[4];
		for(int i=0;i<4;i++){
			nameTable[i] = new NameTable(32,32,"Nt"+i);
		}

		// Initialize mirroring lookup table:
		vramMirrorTable = new int[0x8000];
		for(int i=0;i<0x8000;i++){
			vramMirrorTable[i] = i;
		}

		lastRenderedScanline = -1;
		curX = 0;

	}

	// Sets Nametable mirroring.
	public void setMirroring(int mirroring){
		if(mirroring == currentMirroring){
			return;
		}

		currentMirroring = mirroring;
		triggerRendering();

		// Remove mirroring:
		if(vramMirrorTable==null){
			vramMirrorTable = new int[0x8000];
		}
		for(int i=0;i<0x8000;i++){
			vramMirrorTable[i] = i;
		}

		// Palette mirroring:
		defineMirrorRegion(0x3f20,0x3f00,0x20);
		defineMirrorRegion(0x3f40,0x3f00,0x20);
		defineMirrorRegion(0x3f80,0x3f00,0x20);
		defineMirrorRegion(0x3fc0,0x3f00,0x20);

		// Additional mirroring:
		defineMirrorRegion(0x3000,0x2000,0xf00);
		defineMirrorRegion(0x4000,0x0000,0x4000);

		if(mirroring == ROM.MIRRORING_HORIZONTAL){
			// Horizontal mirroring.
			ntable1[0] = 0;
			ntable1[1] = 0;
			ntable1[2] = 1;
			ntable1[3] = 1;

			defineMirrorRegion(0x2400,0x2000,0x400);
			defineMirrorRegion(0x2c00,0x2800,0x400);
		}else if(mirroring == ROM.MIRRORING_VERTICAL){
			// Vertical mirroring.
			ntable1[0] = 0;
			ntable1[1] = 1;
			ntable1[2] = 0;
			ntable1[3] = 1;

			defineMirrorRegion(0x2800,0x2000,0x400);
			defineMirrorRegion(0x2c00,0x2400,0x400);
		}else if(mirroring == ROM.MIRRORING_SINGLE){
			// Single Screen mirroring
			ntable1[0] = 0;
			ntable1[1] = 0;
			ntable1[2] = 0;
			ntable1[3] = 0;

			defineMirrorRegion(0x2400,0x2000,0x400);
			defineMirrorRegion(0x2800,0x2000,0x400);
			defineMirrorRegion(0x2c00,0x2000,0x400);
		}else if(mirroring == ROM.MIRRORING_SINGLE2){
			ntable1[0] = 1;
			ntable1[1] = 1;
			ntable1[2] = 1;
			ntable1[3] = 1;

			defineMirrorRegion(0x2400,0x2400,0x400);
			defineMirrorRegion(0x2800,0x2400,0x400);
			defineMirrorRegion(0x2c00,0x2400,0x400);
		}else{
			// Assume Four-screen mirroring.
			ntable1[0] = 0;
			ntable1[1] = 1;
			ntable1[2] = 2;
			ntable1[3] = 3;
		}
	}
	// Define a mirrored area in the address lookup table.
	// Assumes the regions don't overlap.
	// The 'to' region is the region that is physically in memory.
	private void defineMirrorRegion(int fromStart, int toStart, int size){
		for(int i=0;i<size;i++){
			vramMirrorTable[fromStart+i] = toStart+i;
		}
	}

	// Emulates PPU cycles
	public void emulateCycles(){
		//int n = (!requestEndFrame && curX+cycles<341 && (scanline-20 < spr0HitY || scanline-22 > spr0HitY))?cycles:1;
		for(;cycles>0;cycles--){
			if(scanline-21 == spr0HitY){
				if((curX == spr0HitX) && (f_spVisibility==1)){
					// Set sprite 0 hit flag:
					setStatusFlag(STATUS_SPRITE0HIT,true);
				}
			}
			if(requestEndFrame){
				nmiCounter--;
				if(nmiCounter == 0){
					requestEndFrame = false;
					startVBlank();
				}
			}
			curX++;
			if(curX==341){
				curX = 0;
				endScanline();
			}
		}
	}
	long t0;

	protected void startVBlank(){
		// Start VBlank period:
		// Do VBlank.

		// Do NMI:
		nes.getCpu().requestIrq(CPU6502.IRQ_NMI);
		
		// Make sure everything is rendered:
		if(lastRenderedScanline < 239){
			renderFramePartially(buffer,lastRenderedScanline+1,PPU.PIXEL_Y-lastRenderedScanline);
		}
		
		endFrame();
		
		//NOTE: 这里触发显示器重绘
		nes.getGui().playFrame(screen);
		
//		screen=buffer.clone();
		for(int i=0;i<buffer.length;i++){
			screen[i]=buffer[i];
		}
		
		if(NES.enableSound){
			nes.getPapu().stuff();
		}else{
			//如果不通过声音来控制节奏则通过帧等待
			long timeSpend=System.nanoTime()-t0;
			if(timeSpend < frameTimeN){
				try {
					long toWaite=frameTimeN-timeSpend;
					long waiteM=toWaite / 1000000;
					int waiteN=(int)(toWaite % 1000000);
					Thread.sleep(waiteM,waiteN);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			t0=System.nanoTime();
		}
		
		// Reset scanline counter:
		lastRenderedScanline = -1;
		
		startFrame();
		
	}

	protected void endScanline(){
		if(scanline<19+vblankAdd){
			// VINT
			// do nothing.
		}else if(scanline==19+vblankAdd){
			// Dummy scanline.
			// May be variable length:
			if(dummyCycleToggle){
				// Remove dead cycle at end of scanline,
				// for next scanline:
				curX = 1;
				dummyCycleToggle = !dummyCycleToggle;
			}
		}else if(scanline==20+vblankAdd){
			// Clear VBlank flag:
			setStatusFlag(STATUS_VBLANK,false);
			// Clear Sprite #0 hit flag:
			setStatusFlag(STATUS_SPRITE0HIT,false);
			hitSpr0 = false;
			spr0HitX = -1;
			spr0HitY = -1;
			if(f_bgVisibility == 1 || f_spVisibility==1){
				// Update counters:
				cntFV = regFV;
				cntV = regV;
				cntH = regH;
				cntVT = regVT;
				cntHT = regHT;
				if(f_bgVisibility==1){
					// Render dummy scanline:
					renderBgScanline(buffer,0);
				}
			}
			if(f_bgVisibility==1 && f_spVisibility==1){
				// Check sprite 0 hit for first scanline:
				checkSprite0(0);
			}
			if(f_bgVisibility==1 || f_spVisibility==1){
				// Clock mapper IRQ Counter:
				nes.getMemoryMapper().clockIrqCounter();
			}
		}else if(scanline>=21+vblankAdd && scanline<=260){
			// Render normally:
			if(f_bgVisibility == 1){
				if(!scanlineAlreadyRendered){
					// update scroll:
					cntHT = regHT;
					cntH = regH;
					renderBgScanline(bufferBG,scanline+1-21);
				}
				scanlineAlreadyRendered=false;
				// Check for sprite 0 (next scanline):
				if(!hitSpr0 && f_spVisibility==1){
					if(sprX[0]>=-7 && sprX[0]<PPU.PIXEL_X && sprY[0]+1<=(scanline-vblankAdd+1-21) && (sprY[0]+1+(f_spriteSize==0?8:16))>=(scanline-vblankAdd+1-21)){
						if(checkSprite0(scanline+vblankAdd+1-21)){
							////System.out.println("found spr0. curscan="+scanline+" hitscan="+spr0HitY);
							hitSpr0 = true;
						}
					}
				}
			}
			if(f_bgVisibility==1 || f_spVisibility==1){
				// Clock mapper IRQ Counter:
				nes.getMemoryMapper().clockIrqCounter();
			}
		}else if(scanline==261+vblankAdd){
			// Dead scanline, no rendering.
			// Set VINT:
			setStatusFlag(STATUS_VBLANK,true);
			requestEndFrame = true;
			nmiCounter = 9;
			// Wrap around:
			scanline = -1;	// will be incremented to 0
		}
		scanline++;
		regsToAddress();
		cntsToAddress();
	}

	protected void startFrame(){
//		int[] buffer = nes.getGui().getScreenView().getBuffer();
		// Set background color:
		int bgColor=0;
		if(f_dispType == 0){
			// Color display.
			// f_color determines color emphasis.
			// Use first entry of image palette as BG color.
			bgColor = imgPalette[0];
		}else{
			// Monochrome display.
			// f_color determines the bg color.
			switch(f_color){
				case 0:{
					// Black
					bgColor = 0x00000;
					break;
				}
				case 1:{
					// Green
					bgColor = 0x00FF00;
				}
				case 2:{
					// Blue
					bgColor = 0xFF0000;
				}
				case 3:{
					// Invalid. Use black.
					bgColor = 0x000000;
				}
				case 4:{
					// Red
					bgColor = 0x0000FF;
				}
				default:{
					// Invalid. Use black.
					bgColor = 0x0;
				}
			}
		}
		for(int i=0;i<buffer.length;i++)buffer[i]=bgColor;
		for(int i=0;i<bufferPixRendered.length;i++)bufferPixRendered[i]=65;
	}

	protected void endFrame(){
		
		// Draw spr#0 hit coordinates:
		showSpr0Hit();
		
		// if either the sprites or the background should be clipped,
		// both are clipped after rendering is finished.
//		showClipToTvSize();
		
		// Show sound buffer:
		showSoundBuffer();
	}
	public void updateControlReg1(int value){

		triggerRendering();

		f_nmiOnVblank =    (value>>7)&1;
		f_spriteSize =     (value>>5)&1;
		f_bgPatternTable = (value>>4)&1;
		f_spPatternTable = (value>>3)&1;
		f_addrInc =        (value>>2)&1;
		f_nTblAddress =     value&3;

		regV = (value>>1)&1;
		regH = value&1;
		regS = (value>>4)&1;

	}



	public void updateControlReg2(int value){

		triggerRendering();

		f_color = 	     (value>>5)&7;
		f_spVisibility = (value>>4)&1;
		f_bgVisibility = (value>>3)&1;
		f_spClipping =   (value>>2)&1;
		f_bgClipping =   (value>>1)&1;
		f_dispType =      value&1;

		if(f_dispType == 0){
			nes.getPaletteTable().setEmphasis(f_color);
		}
		updatePalettes();

	}

	protected void setStatusFlag(int flag, boolean value){

		int n = 1<<flag;
		int memValue = nes.getCpuMemory().load(0x2002);
		memValue = ((memValue&(255-n))|(value?n:0));
		nes.getCpuMemory().write(0x2002,(short)memValue);

	}


	// CPU Register $2002:
	// Read the Status Register.
	public short readStatusRegister(){

		tmp = nes.getCpuMemory().load(0x2002);

		// Reset scroll & VRAM Address toggle:
		firstWrite = true;

		// Clear VBlank flag:
		setStatusFlag(STATUS_VBLANK,false);

		// Fetch status data:
		return tmp;

	}


	// CPU Register $2003:
	// Write the SPR-RAM address that is used for sramWrite (Register 0x2004 in CPU memory map)
	public void writeSRAMAddress(short address){
		sramAddress = address;
	}


	// CPU Register $2004 (R):
	// Read from SPR-RAM (Sprite RAM).
	// The address should be set first.
	public short sramLoad(){
		short tmp = nes.getSprMemory().load(sramAddress);
		/*sramAddress++; // Increment address
		sramAddress%=0x100;*/
		return tmp;
	}


	// CPU Register $2004 (W):
	// Write to SPR-RAM (Sprite RAM).
	// The address should be set first.
	public void sramWrite(short value){
		nes.getSprMemory().write(sramAddress,value);
		spriteRamWriteUpdate(sramAddress,value);
		sramAddress++; // Increment address
		sramAddress%=0x100;
	}


	// CPU Register $2005:
	// Write to scroll registers.
	// The first write is the vertical offset, the second is the
	// horizontal offset:
	public void scrollWrite(short value){

		triggerRendering();
		if(firstWrite){

			// First write, horizontal scroll:
			regHT = (value>>3)&31;
			regFH = value&7;

		}else{

			// Second write, vertical scroll:
			regVT = (value>>3)&31;
			regFV = value&7;

		}
		firstWrite = !firstWrite;

	}



	// CPU Register $2006:
	// Sets the adress used when reading/writing from/to VRAM.
	// The first write sets the high byte, the second the low byte.
	public void writeVRAMAddress(int address){

		if(firstWrite){

			regFV = (address>>4)&3;
			regV = (address>>3)&1;
			regH = (address>>2)&1;
			regVT = (regVT&7) | ((address&3)<<3);

		}else{

			triggerRendering();

			regVT = (regVT&24) | ((address>>5)&7);
			regHT = address&31;

			cntFV = regFV;
			cntV = regV;
			cntH = regH;
			cntVT = regVT;
			cntHT = regHT;

			checkSprite0(scanline-vblankAdd+1-21);

		}

		firstWrite = !firstWrite;

		// Invoke mapper latch:
		cntsToAddress();
		if(vramAddress < 0x2000){
			nes.getMemoryMapper().latchAccess(vramAddress);
		}

	}



	// CPU Register $2007(R):
	// Read from PPU memory. The address should be set first.
	public short vramLoad(){

		cntsToAddress();
		regsToAddress();

		// If address is in range 0x0000-0x3EFF, return buffered values:
		if(vramAddress <= 0x3EFF){

			short tmp = vramBufferedReadValue;

			// Update buffered value:
			if(vramAddress < 0x2000){
				vramBufferedReadValue = nes.getPpuMemory().load(vramAddress);
			}else{
				vramBufferedReadValue = mirroredLoad(vramAddress);
			}

			// Mapper latch access:
			if(vramAddress < 0x2000){
				nes.getMemoryMapper().latchAccess(vramAddress);
			}

			// Increment by either 1 or 32, depending on d2 of Control Register 1:
			vramAddress += (f_addrInc==1?32:1);

			cntsFromAddress();
			regsFromAddress();
			return tmp; // Return the previous buffered value.

		}

		// No buffering in this mem range. Read normally.
		short tmp = mirroredLoad(vramAddress);

		// Increment by either 1 or 32, depending on d2 of Control Register 1:
		vramAddress += (f_addrInc==1?32:1);

		cntsFromAddress();
		regsFromAddress();

		return tmp;
	}



	// CPU Register $2007(W):
	// Write to PPU memory. The address should be set first.
	public void vramWrite(short value){

		triggerRendering();
		cntsToAddress();
		regsToAddress();

		if(vramAddress >= 0x2000){
			// Mirroring is used.
			mirroredWrite(vramAddress,value);
		}else{

			// Write normally.
			writeMem(vramAddress,value);

			// Invoke mapper latch:
			nes.getMemoryMapper().latchAccess(vramAddress);

		}

		// Increment by either 1 or 32, depending on d2 of Control Register 1:
		vramAddress += (f_addrInc==1?32:1);
		regsFromAddress();
		cntsFromAddress();

	}



	// CPU Register $4014:
	// Write 256 bytes of main memory
	// into Sprite RAM.
	public void sramDMA(short value){

		Memory cpuMem = nes.getCpuMemory();
		int baseAddress = value * 0x100;
		short data;
		for(int i=sramAddress;i<PPU.PIXEL_X;i++){
			data = cpuMem.load(baseAddress+i);
			nes.getSprMemory().write(i,data);
			spriteRamWriteUpdate(i,data);
		}

		nes.getCpu().haltCycles(513);

	}

	// Updates the scroll registers from a new VRAM address.
	private void regsFromAddress(){
		address = (vramTmpAddress>>8)&0xFF;
		regFV = (address>>4)&7;
		regV = (address>>3)&1;
		regH = (address>>2)&1;
		regVT = (regVT&7) | ((address&3)<<3);

		address = vramTmpAddress&0xFF;
		regVT = (regVT&24) | ((address>>5)&7);
		regHT = address&31;
	}

	// Updates the scroll registers from a new VRAM address.
	private void cntsFromAddress(){
		address = (vramAddress>>8)&0xFF;
		cntFV = (address>>4)&3;
		cntV = (address>>3)&1;
		cntH = (address>>2)&1;
		cntVT = (cntVT&7) | ((address&3)<<3);

		address = vramAddress&0xFF;
		cntVT = (cntVT&24) | ((address>>5)&7);
		cntHT = address&31;
	}

	private void regsToAddress(){
		b1  = (regFV&7)<<4;
		b1 |= (regV&1)<<3;
		b1 |= (regH&1)<<2;
		b1 |= (regVT>>3)&3;

		b2  = (regVT&7)<<5;
		b2 |= regHT&31;

		vramTmpAddress = ((b1<<8) | b2)&0x7FFF;
	}

	private void cntsToAddress(){

		b1  = (cntFV&7)<<4;
		b1 |= (cntV&1)<<3;
		b1 |= (cntH&1)<<2;
		b1 |= (cntVT>>3)&3;

		b2  = (cntVT&7)<<5;
		b2 |= cntHT&31;

		vramAddress = ((b1<<8) | b2)&0x7FFF;

	}

//	private void incTileCounter(int count){
//
//		for(i=count;i!=0;i--){
//			cntHT++;
//			if(cntHT==32){
//				cntHT=0;
//				cntVT++;
//				if(cntVT>=30){
//					cntH++;
//					if(cntH==2){
//						cntH=0;
//						cntV++;
//						if(cntV==2){
//							cntV=0;
//							cntFV++;
//							cntFV&=0x7;
//						}
//					}
//				}
//			}
//		}
//
//	}

	// Reads from memory, taking into account
	// mirroring/mapping of address ranges.
	private short mirroredLoad(int address){

		return nes.getPpuMemory().load(vramMirrorTable[address]);

	}



	// Writes to memory, taking into account
	// mirroring/mapping of address ranges.
	private void mirroredWrite(int address, short value){

		if(address>=0x3f00 && address<0x3f20){

			// Palette write mirroring.

			if(address==0x3F00 || address==0x3F10){

				writeMem(0x3F00,value);
				writeMem(0x3F10,value);

			}else if(address==0x3F04 || address==0x3F14){

				writeMem(0x3F04,value);
				writeMem(0x3F14,value);

			}else if(address==0x3F08 || address==0x3F18){

				writeMem(0x3F08,value);
				writeMem(0x3F18,value);

			}else if(address==0x3F0C || address==0x3F1C){

				writeMem(0x3F0C,value);
				writeMem(0x3F1C,value);

			}else{

				writeMem(address,value);

			}

		}else{

			// Use lookup table for mirrored address:
			if(address<vramMirrorTable.length){
				writeMem(vramMirrorTable[address],value);
			}else{
				Logger.getAnonymousLogger().severe("Invalid VRAM address: "+NES.hex16(address));
				nes.getCpu().setCrashed(true);
			}
		}
	}

	public void triggerRendering(){
		if(scanline-vblankAdd>=21 && scanline-vblankAdd<=260){
			// Render sprites, and combine:
			renderFramePartially(buffer,lastRenderedScanline+1,scanline-vblankAdd-21-lastRenderedScanline);

			// Set last rendered scanline:
			lastRenderedScanline = scanline-vblankAdd-21;
		}
	}

	private void renderFramePartially(int[] buffer, int startScan, int scanCount){

//		if(f_spVisibility == 1 && !Globals.disableSprites){
//			renderSpritesPartially(startScan,scanCount,true);
//		}

		if(f_bgVisibility == 1){
			si = startScan<<8;
			ei = (startScan+scanCount)<<8;
			if(ei>0xF000)ei=0xF000;
			for(destIndex=si;destIndex<ei;destIndex++){
				if(bufferPixRendered[destIndex]>0xFF){
					buffer[destIndex] = bufferBG[destIndex];
				}
			}
		}

		if(f_spVisibility == 1 && !disableSprites){
			renderSpritesPartially(startScan,scanCount,false);
		}

//		IBufferView screen = nes.getGui().getScreenView();
//		if(screen.scalingEnabled() && !screen.useHWScaling() && !requestRenderAll){
//
//			// Check which scanlines have changed, to try to
//			// speed up scaling:
//			int j,jmax;
//			if(startScan+scanCount>Globals.PIXEL_Y)scanCount=Globals.PIXEL_Y-startScan;
//			for(int i=startScan;i<startScan+scanCount;i++){
//				scanlineChanged[i]=false;
//				si = i<<8;
//				jmax = si+Globals.PIXEL_X;
//				for(j=si;j<jmax;j++){
//					if(buffer[j]!=oldFrame[j]){
//						scanlineChanged[i]=true;
//						break;
//					}
//					oldFrame[j]=buffer[j];
//				}
//				System.arraycopy(buffer,j,oldFrame,j,jmax-j);
//			}
//
//		}

		validTileData = false;

	}

	private void renderBgScanline(int[] buffer, int scan){
		baseTile = (regS==0?0:PPU.PIXEL_X);
		destIndex = (scan<<8)-regFH;
		curNt = ntable1[cntV+cntV+cntH];

		cntHT = regHT;
		cntH = regH;
		curNt = ntable1[cntV+cntV+cntH];

		if(scan<PPU.PIXEL_Y && (scan-cntFV)>=0){
			tscanoffset = cntFV<<3;
//			y = scan-cntFV;
			for(tile=0;tile<32;tile++){
				if(scan>=0){
					// Fetch tile & attrib data:
					if(validTileData){
						// Get data from array:
						t = scantile[tile];
						tpix = t.getPix();
						att = attrib[tile];
					}else{
						// Fetch data:
						t = ptTile[baseTile+nameTable[curNt].getTileIndex(cntHT,cntVT)];
						tpix = t.getPix();
						att = nameTable[curNt].getAttrib(cntHT,cntVT);
						scantile[tile] = t;
						attrib[tile] = att;
					}

					// Render tile scanline:
					sx = 0;
					x = (tile<<3)-regFH;
					if(x>-8){
						if(x<0){
							destIndex-=x;
							sx = -x;
						}
						if(t.getOpaque()[cntFV]){
							for(;sx<8;sx++){
								buffer[destIndex] = imgPalette[tpix[tscanoffset+sx]+att];
								bufferPixRendered[destIndex] |= PPU.PIXEL_X;
								destIndex++;
							}
						}else{
							for(;sx<8;sx++){
								col = tpix[tscanoffset+sx];
								if(col != 0){
									buffer[destIndex] = imgPalette[col+att];
									bufferPixRendered[destIndex] |= PPU.PIXEL_X;
								}
								destIndex++;
							}
						}
					}

				}

				// Increase Horizontal Tile Counter:
				cntHT++;
				if(cntHT==32){
					cntHT=0;
					cntH++;
					cntH%=2;
					curNt = ntable1[(cntV<<1)+cntH];
				}
			}
			// Tile data for one row should now have been fetched,
			// so the data in the array is valid.
			validTileData = true;
		}

		// update vertical scroll:
		cntFV++;
		if(cntFV==8){
			cntFV = 0;
			cntVT++;
			if(cntVT==30){
				cntVT = 0;
				cntV++;
				cntV%=2;
				curNt = ntable1[(cntV<<1)+cntH];
			}else if(cntVT==32){
				cntVT = 0;
			}
			// Invalidate fetched data:
			validTileData = false;
		}
	}

	private void renderSpritesPartially(int startscan, int scancount, boolean bgPri){
//		buffer = nes.getGui().getScreenView().getBuffer();
		if(f_spVisibility==1){

//			int sprT1,sprT2;

			for(int i=0;i<64;i++){
				if(bgPriority[i]==bgPri && sprX[i]>=0 && sprX[i]<PPU.PIXEL_X && sprY[i]+8>=startscan && sprY[i]<startscan+scancount){
					// Show sprite.
					if(f_spriteSize == 0){
						// 8x8 sprites

						srcy1 = 0;
						srcy2 = 8;

						if(sprY[i]<startscan){
							srcy1 = startscan - sprY[i]-1;
						}

						if(sprY[i]+8 > startscan+scancount){
							srcy2 = startscan+scancount-sprY[i]+1;
						}

						if(f_spPatternTable==0){
							ptTile[sprTile[i]].render(0,srcy1,8,srcy2,sprX[i],sprY[i]+1,buffer,sprCol[i],sprPalette,horiFlip[i],vertFlip[i],i,bufferPixRendered);
						}else{
							ptTile[sprTile[i]+PPU.PIXEL_X].render(0,srcy1,8,srcy2,sprX[i],sprY[i]+1,buffer,sprCol[i],sprPalette,horiFlip[i],vertFlip[i],i,bufferPixRendered);
						}
					}else{
						// 8x16 sprites
						int top = sprTile[i];
						if((top&1)!=0){
							top = sprTile[i]-1+PPU.PIXEL_X;
						}

						srcy1 = 0;
						srcy2 = 8;

						if(sprY[i]<startscan){
							srcy1 = startscan - sprY[i]-1;
						}

						if(sprY[i]+8 > startscan+scancount){
							srcy2 = startscan+scancount-sprY[i];
						}

						ptTile[top+(vertFlip[i]?1:0)].render(0,srcy1,8,srcy2,sprX[i],sprY[i]+1,buffer,sprCol[i],sprPalette,horiFlip[i],vertFlip[i],i,bufferPixRendered);

						srcy1 = 0;
						srcy2 = 8;

						if(sprY[i]+8<startscan){
							srcy1 = startscan - (sprY[i]+8+1);
						}

						if(sprY[i]+16 > startscan+scancount){
							srcy2 = startscan+scancount-(sprY[i]+8);
						}

						ptTile[top+(vertFlip[i]?0:1)].render(0,srcy1,8,srcy2,sprX[i],sprY[i]+1+8,buffer,sprCol[i],sprPalette,horiFlip[i],vertFlip[i],i,bufferPixRendered);

					}
				}
			}
		}

	}

	private boolean checkSprite0(int scan){

		spr0HitX = -1;
		spr0HitY = -1;

		int toffset;
		int tIndexAdd = (f_spPatternTable==0?0:PPU.PIXEL_X);
		int x,y;
		int bufferIndex;
//		int col;
//		boolean bgPri;
		Tile t;

		x = sprX[0];
		y = sprY[0]+1;


		if(f_spriteSize==0){

			// 8x8 sprites.

			// Check range:
			if(y<=scan && y+8>scan && x>=-7 && x<PPU.PIXEL_X){

				// Sprite is in range.
				// Draw scanline:
				t = ptTile[sprTile[0]+tIndexAdd];
				col = sprCol[0];
//				bgPri = bgPriority[0];

				if(vertFlip[0]){
					toffset = 7-(scan-y);
				}else{
					toffset = scan-y;
				}
				toffset*=8;

				bufferIndex = scan*PPU.PIXEL_X+x;
				if(horiFlip[0]){
					for(int i=7;i>=0;i--){
						if(x>=0 && x<PPU.PIXEL_X){
							if(bufferIndex>=0 && bufferIndex<BUFFER_SIZE && bufferPixRendered[bufferIndex]!=0){
								if(t.getPix()[toffset+i] != 0){
									spr0HitX = bufferIndex%PPU.PIXEL_X;
									spr0HitY = scan;
									return true;
								}
							}
						}
						x++;
						bufferIndex++;
					}

				}else{

					for(int i=0;i<8;i++){
						if(x>=0 && x<PPU.PIXEL_X){
							if(bufferIndex>=0 && bufferIndex<BUFFER_SIZE && bufferPixRendered[bufferIndex]!=0){
								if(t.getPix()[toffset+i] != 0){
									spr0HitX = bufferIndex%PPU.PIXEL_X;
									spr0HitY = scan;
									return true;
								}
							}
						}
						x++;
						bufferIndex++;
					}

				}

			}


		}else{

			// 8x16 sprites:

			// Check range:
			if(y<=scan && y+16>scan && x>=-7 && x<PPU.PIXEL_X){

				// Sprite is in range.
				// Draw scanline:

				if(vertFlip[0]){
					toffset = 15-(scan-y);
				}else{
					toffset = scan-y;
				}

				if(toffset<8){
					// first half of sprite.
					t = ptTile[sprTile[0]+(vertFlip[0]?1:0)+((sprTile[0]&1)!=0?255:0)];
				}else{
					// second half of sprite.
					t = ptTile[sprTile[0]+(vertFlip[0]?0:1)+((sprTile[0]&1)!=0?255:0)];
					if(vertFlip[0]){
						toffset = 15-toffset;
					}else{
						toffset -= 8;
					}
				}
				toffset*=8;
				col = sprCol[0];
//				bgPri = bgPriority[0];

				bufferIndex = scan*PPU.PIXEL_X+x;
				if(horiFlip[0]){

					for(int i=7;i>=0;i--){
						if(x>=0 && x<PPU.PIXEL_X){
							if(bufferIndex>=0 && bufferIndex<BUFFER_SIZE && bufferPixRendered[bufferIndex]!=0){
								if(t.getPix()[toffset+i] != 0){
									spr0HitX = bufferIndex%PPU.PIXEL_X;
									spr0HitY = scan;
									return true;
								}
							}
						}
						x++;
						bufferIndex++;
					}

				}else{

					for(int i=0;i<8;i++){
						if(x>=0 && x<PPU.PIXEL_X){
							if(bufferIndex>=0 && bufferIndex<BUFFER_SIZE && bufferPixRendered[bufferIndex]!=0){
								if(t.getPix()[toffset+i] != 0){
									spr0HitX = bufferIndex%PPU.PIXEL_X;
									spr0HitY = scan;
									return true;
								}
							}
						}
						x++;
						bufferIndex++;
					}

				}

			}

		}

		return false;

	}

	// Renders the contents of the
	// pattern table into an image.
//	public void renderPattern(){
//
//		IBufferView scr = nes.getGui().getPatternView();
//		int[] buffer = scr.getBuffer();
//
//		int tIndex = 0;
//		for(int j=0;j<2;j++){
//			for(int y=0;y<16;y++){
//				for(int x=0;x<16;x++){
//					ptTile[tIndex].renderSimple(j*128+x*8,y*8,buffer,0,sprPalette);
//					tIndex++;
//				}
//			}
//		}
//		nes.getGui().getPatternView().drawFrame();
//
//	}


//	public void renderNameTables(){
//
//		int[] buffer = nes.getGui().getNameTableView().getBuffer();
//		if(f_bgPatternTable == 0){
//			baseTile = 0;
//		}else{
//			baseTile = Globals.PIXEL_X;
//		}
//
//		int ntx_max = 2;
//		int nty_max = 2;
//
//		if(currentMirroring == ROM.HORIZONTAL_MIRRORING){
//			ntx_max = 1;
//		}else if(currentMirroring == ROM.VERTICAL_MIRRORING){
//			nty_max = 1;
//		}
//
//		for(int nty=0;nty<nty_max;nty++){
//			for(int ntx=0;ntx<ntx_max;ntx++){
//
//				int nt = ntable1[nty*2+ntx];
//				int x = ntx*128;
//				int y = nty*120;
//
//				// Render nametable:
//				for(int ty=0;ty<30;ty++){
//					for(int tx=0;tx<32;tx++){
//						//ptTile[baseTile+nameTable[nt].getTileIndex(tx,ty)].render(0,0,4,4,x+tx*4,y+ty*4,buffer,nameTable[nt].getAttrib(tx,ty),imgPalette,false,false,0,dummyPixPriTable);
//						ptTile[baseTile+nameTable[nt].getTileIndex(tx,ty)].renderSmall(x+tx*4,y+ty*4,buffer,nameTable[nt].getAttrib(tx,ty),imgPalette);
//					}
//				}
//
//			}
//		}
//
//		if(currentMirroring == ROM.HORIZONTAL_MIRRORING){
//			// double horizontally:
//			for(int y=0;y<Globals.PIXEL_Y;y++){
//				for(int x=0;x<128;x++){
//					buffer[(y<<8)+128+x] = buffer[(y<<8)+x];
//				}
//			}
//		}else if(currentMirroring == ROM.VERTICAL_MIRRORING){
//			// double vertically:
//			for(int y=0;y<120;y++){
//				for(int x=0;x<Globals.PIXEL_X;x++){
//					buffer[(y<<8)+0x7800+x] = buffer[(y<<8)+x];
//				}
//			}
//		}
//
//		nes.getGui().getNameTableView().drawFrame();
//
//	}

//	private void renderPalettes(){
//
//		int[] buffer = nes.getGui().getImgPalView().getBuffer();
//		for(int i=0;i<16;i++){
//			for(int y=0;y<16;y++){
//				for(int x=0;x<16;x++){
//					buffer[y*Globals.PIXEL_X+i*16+x] = imgPalette[i];
//				}
//			}
//		}
//
//		buffer = nes.getGui().getSprPalView().getBuffer();
//		for(int i=0;i<16;i++){
//			for(int y=0;y<16;y++){
//				for(int x=0;x<16;x++){
//					buffer[y*Globals.PIXEL_X+i*16+x] = sprPalette[i];
//				}
//			}
//		}
//
//		nes.getGui().getImgPalView().imageReady(false);
//		nes.getGui().getSprPalView().imageReady(false);
//
//	}


	// This will write to PPU memory, and
	// update internally buffered data
	// appropriately.
	private void writeMem(int address, short value){

		nes.getPpuMemory().write(address,value);

		// Update internally buffered data:
		if(address < 0x2000){

			nes.getPpuMemory().write(address,value);
			patternWrite(address,value);

		}else if(address >=0x2000 && address <0x23c0){

			nameTableWrite(ntable1[0],address-0x2000,value);

		}else if(address >=0x23c0 && address <0x2400){

			attribTableWrite(ntable1[0],address-0x23c0,value);

		}else if(address >=0x2400 && address <0x27c0){

			nameTableWrite(ntable1[1],address-0x2400,value);

		}else if(address >=0x27c0 && address <0x2800){

			attribTableWrite(ntable1[1],address-0x27c0,value);

		}else if(address >=0x2800 && address <0x2bc0){

			nameTableWrite(ntable1[2],address-0x2800,value);

		}else if(address >=0x2bc0 && address <0x2c00){

			attribTableWrite(ntable1[2],address-0x2bc0,value);

		}else if(address >=0x2c00 && address <0x2fc0){

			nameTableWrite(ntable1[3],address-0x2c00,value);

		}else if(address >=0x2fc0 && address <0x3000){

			attribTableWrite(ntable1[3],address-0x2fc0,value);

		}else if(address >=0x3f00 && address <0x3f20){

			updatePalettes();

		}

	}



	// Reads data from $3f00 to $f20
	// into the two buffered palettes.
	protected void updatePalettes(){

		for(int i=0;i<16;i++){
			if(f_dispType == 0){
				imgPalette[i] = nes.getPaletteTable().getEntry(nes.getPpuMemory().load(0x3f00+i)&63);
			}else{
				imgPalette[i] = nes.getPaletteTable().getEntry(nes.getPpuMemory().load(0x3f00+i)&32);
			}
		}
		for(int i=0;i<16;i++){
			if(f_dispType == 0){
				sprPalette[i] = nes.getPaletteTable().getEntry(nes.getPpuMemory().load(0x3f10+i)&63);
			}else{
				sprPalette[i] = nes.getPaletteTable().getEntry(nes.getPpuMemory().load(0x3f10+i)&32);
			}
		}

		//renderPalettes();

	}


	// Updates the internal pattern
	// table buffers with this new byte.
	protected void patternWrite(int address, short value){
		int tileIndex = address/16;
		int leftOver = address%16;
		if(leftOver<8){
			ptTile[tileIndex].setScanline(leftOver,value,nes.getPpuMemory().load(address+8));
		}else{
			ptTile[tileIndex].setScanline(leftOver-8,nes.getPpuMemory().load(address-8),value);
		}
	}

	// Updates the internal name table buffers
	// with this new byte.
	protected void nameTableWrite(int index, int address, short value){
		nameTable[index].writeTileIndex(address,value);

		// Update Sprite #0 hit:
		//updateSpr0Hit();
		checkSprite0(scanline+1-vblankAdd-21);

	}



	// Updates the internal pattern
	// table buffers with this new attribute
	// table byte.
	protected void attribTableWrite(int index, int address, short value){
		nameTable[index].writeAttrib(address,value);
	}



	// Updates the internally buffered sprite
	// data with this new byte of info.
	protected void spriteRamWriteUpdate(int address, short value){

		int tIndex = address/4;

		if(tIndex == 0){
			//updateSpr0Hit();
			checkSprite0(scanline+1-vblankAdd-21);
		}

		if(address%4 == 0){

			// Y coordinate
			sprY[tIndex] = value;

		}else if(address%4 == 1){

			// Tile index
			sprTile[tIndex] = value;

		}else if(address%4 == 2){

			// Attributes
			vertFlip[tIndex] = ((value&0x80)!=0);
			horiFlip[tIndex] = ((value&0x40)!=0);
			bgPriority[tIndex] = ((value&0x20)!=0);
			sprCol[tIndex] = (value&3)<<2;

		}else if(address%4 == 3){

			// X coordinate
			sprX[tIndex] = value;

		}

	}

//	public void doNMI(){
//
//		// Set VBlank flag:
//		setStatusFlag(STATUS_VBLANK,true);
//		//nes.getCpu().doNonMaskableInterrupt();
//		nes.getCpu().requestIrq(CPU.IRQ_NMI);
//
//	}

	protected int statusRegsToInt(){

		int ret=0;
		ret = 	(f_nmiOnVblank) |
				(f_spriteSize<<1) |
				(f_bgPatternTable<<2) |
				(f_spPatternTable<<3) |
				(f_addrInc<<4) |
				(f_nTblAddress<<5) |

				(f_color<<6) |
				(f_spVisibility<<7) |
				(f_bgVisibility<<8) |
				(f_spClipping<<9) |
				(f_bgClipping<<10) |
				(f_dispType<<11);

		return ret;

	}

	protected void statusRegsFromInt(int n){

		f_nmiOnVblank     = (n    )&0x1;
		f_spriteSize      = (n>>1 )&0x1;
		f_bgPatternTable  = (n>>2 )&0x1;
		f_spPatternTable  = (n>>3 )&0x1;
		f_addrInc         = (n>>4 )&0x1;
		f_nTblAddress     = (n>>5 )&0x1;

		f_color           = (n>>6 )&0x1;
		f_spVisibility    = (n>>7 )&0x1;
		f_bgVisibility    = (n>>8 )&0x1;
		f_spClipping      = (n>>9 )&0x1;
		f_bgClipping      = (n>>10)&0x1;
		f_dispType        = (n>>11)&0x1;

	}

	public void stateLoad(ByteBuffer buf){
		// Check version:
		if(buf.readByte()==1){

			// Counters:
			cntFV = buf.readInt();
			cntV = buf.readInt();
			cntH = buf.readInt();
			cntVT = buf.readInt();
			cntHT = buf.readInt();

			// Registers:
			regFV = buf.readInt();
			regV = buf.readInt();
			regH = buf.readInt();
			regVT = buf.readInt();
			regHT = buf.readInt();
			regFH = buf.readInt();
			regS = buf.readInt();

			// VRAM address:
			vramAddress = buf.readInt();
			vramTmpAddress = buf.readInt();

			// Control/Status registers:
			statusRegsFromInt(buf.readInt());

			// VRAM I/O:
			vramBufferedReadValue = (short)buf.readInt();
			firstWrite = buf.readBoolean();
			//System.out.println("firstWrite: "+firstWrite);

			// Mirroring:
			//currentMirroring = -1;
//			setMirroring(buf.readInt());
			for(int i=0;i<vramMirrorTable.length;i++){
				vramMirrorTable[i] = buf.readInt();
			}

			// SPR-RAM I/O:
			sramAddress = (short)buf.readInt();

			// Rendering progression:
			curX = buf.readInt();
			scanline = buf.readInt();
			lastRenderedScanline = buf.readInt();

			// Misc:
			requestEndFrame = buf.readBoolean();
			nmiOk = buf.readBoolean();
			dummyCycleToggle = buf.readBoolean();
			nmiCounter = buf.readInt();
			tmp = (short)buf.readInt();

			// Stuff used during rendering:
			for(int i=0;i<bufferBG.length;i++){
				bufferBG[i] = buf.readByte();
			}
			for(int i=0;i<bufferPixRendered.length;i++){
				bufferPixRendered[i] = buf.readByte();
			}

			// Name tables:
			for(int i=0;i<4;i++){
				ntable1[i] = buf.readByte();
				nameTable[i].stateLoad(buf);
			}

			// Pattern data:
			for(int i=0;i<ptTile.length;i++){
				ptTile[i].stateLoad(buf);
			}

			// Update internally stored stuff from VRAM memory:
			/*short[] mem = ppuMem.mem;

			// Palettes:
			for(int i=0x3f00;i<0x3f20;i++){
				writeMem(i,mem[i]);
			}
			*/
			// Sprite data:
			short[] sprmem = nes.getSprMemory().getMem();
			for(int i=0;i<sprmem.length;i++){
				spriteRamWriteUpdate(i,sprmem[i]);
			}
		}
	}

	public void stateSave(ByteBuffer buf){
		// Version:
		buf.putByte((short)1);

		// Counters:
		buf.putInt(cntFV);
		buf.putInt(cntV);
		buf.putInt(cntH);
		buf.putInt(cntVT);
		buf.putInt(cntHT);

		// Registers:
		buf.putInt(regFV);
		buf.putInt(regV);
		buf.putInt(regH);
		buf.putInt(regVT);
		buf.putInt(regHT);
		buf.putInt(regFH);
		buf.putInt(regS);

		// VRAM address:
		buf.putInt(vramAddress);
		buf.putInt(vramTmpAddress);

		// Control/Status registers:
		buf.putInt(statusRegsToInt());

		// VRAM I/O:
		buf.putInt(vramBufferedReadValue);
		//System.out.println("firstWrite: "+firstWrite);
		buf.putBoolean(firstWrite);

		// Mirroring:
//		buf.putInt(currentMirroring);
		for(int i=0;i<vramMirrorTable.length;i++){
			buf.putInt(vramMirrorTable[i]);
		}

		// SPR-RAM I/O:
		buf.putInt(sramAddress);

		// Rendering progression:
		buf.putInt(curX);
		buf.putInt(scanline);
		buf.putInt(lastRenderedScanline);

		// Misc:
		buf.putBoolean(requestEndFrame);
		buf.putBoolean(nmiOk);
		buf.putBoolean(dummyCycleToggle);
		buf.putInt(nmiCounter);
		buf.putInt(tmp);

		// Stuff used during rendering:
		for(int i=0;i<bufferBG.length;i++){
			buf.putByte((short)bufferBG[i]);
		}
		for(int i=0;i<bufferPixRendered.length;i++){
			buf.putByte((short)bufferPixRendered[i]);
		}

		// Name tables:
		for(int i=0;i<4;i++){
			buf.putByte((short)ntable1[i]);
			nameTable[i].stateSave(buf);
		}

		// Pattern data:
		for(int i=0;i<ptTile.length;i++){
			ptTile[i].stateSave(buf);
		}

	}

	// Reset PPU:
	public void reset(){

		nes.getPpuMemory().reset();
		nes.getSprMemory().reset();

		vramBufferedReadValue = 0;
		sramAddress           = 0;
		curX                  = 0;
		scanline              = 0;
		lastRenderedScanline  = 0;
		spr0HitX              = 0;
		spr0HitY              = 0;
//		mapperIrqCounter	  = 0;

		currentMirroring = -1;

		firstWrite = true;
		requestEndFrame = false;
		nmiOk = false;
		hitSpr0 = false;
		dummyCycleToggle = false;
		validTileData = false;
		nmiCounter = 0;
		tmp = 0;
		att = 0;

		// Control Flags Register 1:
		f_nmiOnVblank = 0;    // NMI on VBlank. 0=disable, 1=enable
		f_spriteSize = 0;     // Sprite size. 0=8x8, 1=8x16
		f_bgPatternTable = 0; // Background Pattern Table address. 0=0x0000,1=0x1000
		f_spPatternTable = 0; // Sprite Pattern Table address. 0=0x0000,1=0x1000
		f_addrInc = 0;        // PPU Address Increment. 0=1,1=32
		f_nTblAddress = 0;    // Name Table Address. 0=0x2000,1=0x2400,2=0x2800,3=0x2C00

		// Control Flags Register 2:
		f_color = 0;	   	  // Background color. 0=black, 1=blue, 2=green, 4=red
		f_spVisibility = 0;   // Sprite visibility. 0=not displayed,1=displayed
		f_bgVisibility = 0;   // Background visibility. 0=Not Displayed,1=displayed
		f_spClipping = 0;     // Sprite clipping. 0=Sprites invisible in left 8-pixel column,1=No clipping
		f_bgClipping = 0;     // Background clipping. 0=BG invisible in left 8-pixel column, 1=No clipping
		f_dispType = 0;       // Display type. 0=color, 1=monochrome


		// Counters:
		cntFV = 0;
		cntV = 0;
		cntH = 0;
		cntVT = 0;
		cntHT = 0;

		// Registers:
		regFV = 0;
		regV = 0;
		regH = 0;
		regVT = 0;
		regHT = 0;
		regFH = 0;
		regS = 0;

		java.util.Arrays.fill(scanlineChanged,true);
//		java.util.Arrays.fill(bufferOldFrame,-1);

		// Initialize stuff:
		init();

	}
	private void showSpr0Hit(){
		// Draw spr#0 hit coordinates:
		if(showSpr0Hit){
			// Spr 0 position:
			if(sprX[0]>=0 && sprX[0]<PPU.PIXEL_X && sprY[0]>=0 && sprY[0]<PPU.PIXEL_Y){
				for(int i=0;i<PPU.PIXEL_X;i++){
					buffer[(sprY[0]<<8)+i] = 0xFF5555;
				}
				for(int i=0;i<PPU.PIXEL_Y;i++){
					buffer[(i<<8)+sprX[0]] = 0xFF5555;
				}
			}
			// Hit position:
			if(spr0HitX>=0 && spr0HitX<PPU.PIXEL_X && spr0HitY>=0 && spr0HitY<PPU.PIXEL_Y){
				for(int i=0;i<PPU.PIXEL_X;i++){
					buffer[(spr0HitY<<8)+i] = 0x55FF55;
				}
				for(int i=0;i<PPU.PIXEL_Y;i++){
					buffer[(i<<8)+spr0HitX] = 0x55FF55;
				}
			}
		}
	}
//	private void showClipToTvSize(){
//		// This is a bit lazy..
//		// if either the sprites or the background should be clipped,
//		// both are clipped after rendering is finished.
//		if(NES.clipToTvSize || f_bgClipping==0 || f_spClipping==0){
//			for(int y=0;y<PPU.PIXEL_Y;y++){
//				for(int x=0;x<8;x++){
//					// Clip left 8-pixels column:
//					buffer[(y<<8)+x] = 3;
//					// Clip right 8-pixels column too:
//					buffer[(y<<8)+255-x] = 3;
//				}
//			}
//		}
//
//		// Clip top and bottom 8 pixels:
//		if(NES.clipToTvSize){
//			for(int y=0;y<8;y++){
//				for(int x=0;x<PPU.PIXEL_X;x++){
//					buffer[(y<<8)+x] = 0;
//					buffer[((239-y)<<8)+x] = 0;
//				}
//			}
//		}
//	}
	private void showSoundBuffer(){
		if(NES.showSoundBuffer && nes.getPapu().getLine()!=null){

			bufferSize = nes.getPapu().getLine().getBufferSize();
			available = nes.getPapu().getLine().available();
			int scale = bufferSize/PPU.PIXEL_X;

			for(int y=0;y<4;y++){
				scanlineChanged[y] = true;
				for(int x=0;x<PPU.PIXEL_X;x++){
					if(x>=(available/scale)){
						buffer[y*PPU.PIXEL_X+x] = 0xFFFFFF;
					}else{
						buffer[y*PPU.PIXEL_X+x] = 0;
					}
				}
			}
		}
	}
	public int[] getBuffer() {
//		if(Globals.doubleBuffer){
//			if(img!=null){
//				DataBufferInt bufferInt=(DataBufferInt)img.getData().getDataBuffer();
//				return bufferInt.getData();
//			}
//			return null;
//		}else{
			return screen;
//		}
	}
	
//	public void setBuffer(int[] buffer) {
//		this.buffer = buffer;
//	}

	public void setCycles(int cycles) {
		this.cycles = cycles;
	}

	public int getScanline() {
		return scanline;
	}

	public Tile[] getPtTile() {
		return ptTile;
	}
//	public Image getImage(){
//		return img;
//	}
}