package tlw.nes;
import tlw.nes.vcpu.CPU6502;
import tlw.nes.vmemory.ByteBuffer;
import tlw.nes.vmemory.Memory;
import tlw.nes.vpapu.PAPU;
import tlw.nes.vppu.PPU;
import tlw.nes.vppu.PaletteTable;
import tlw.nes.vrom.ROM;

public class NES{
	
	//显示声音缓冲
	public static boolean showSoundBuffer = true;
	//是否声音 TODO 可以优化对声音的处理在统一的位置
	public static boolean enableSound = true;
	
	private NesShell gui;
	private CPU6502 cpu;
	private PPU ppu;
	private PAPU papu;
	private Memory cpuMem;
	private Memory ppuMem;
	private Memory sprMem;
	private MemoryMapper memMapper;
	private PaletteTable paletteTable;
	private ROM rom;
//	private GameGenie gameGenie;
	private boolean isRunning = false;
	
	// Creates the NES system.
	public NES(NesShell gui){
		this.gui = gui;
		
		// Create memory:
		cpuMem = new Memory(0x10000);	// Main memory (internal to CPU)
		ppuMem = new Memory(0x8000);	// VRAM memory (internal to PPU)
		sprMem = new Memory(0x100);		// Sprite RAM  (internal to PPU)
		
		// Create system units:
		cpu = new CPU6502(this);
		paletteTable = new PaletteTable();
		ppu = new PPU(this);
		papu = new PAPU(this);
//		gameGenie = new GameGenie();
		
		// Init sound registers:
		for(int i=0;i<0x14;i++){
			if(i==0x10){
				papu.writeReg(0x4010,(short)0x10);
			}else{
				papu.writeReg(0x4000+i,(short)0);
			}
		}

		//TODO 这里无论PAL还是NTSC颜色都不正常，只有DefaultPalette是正常的色彩。可能需要深入了解
		paletteTable.loadDefaultPalette();
		
		// Initialize units:
		cpu.init();
		ppu.init();
		
		// Enable sound:
//		enableSound(Globals.enableSound);
		
		//TODO 这里去掉貌似没有什么问题，参见cpuMem.reset()方法与这里功能不一致。
		// Clear CPU memory:
//		clearCPUMemory();
	}
	
	public boolean stateLoad(ByteBuffer buf){

		boolean continueEmulation = false;
		boolean success;
		
		// Pause emulation:
		if(cpu.isRunning()){
			continueEmulation = true;
			stopEmulation();
		}
		
		buf.goTo(0);
		// Check version:
		if(buf.readByte()==1){
			// Let units load their state from the buffer:
			cpuMem.stateLoad(buf);
			ppuMem.stateLoad(buf);
			sprMem.stateLoad(buf);
			cpu.stateLoad(buf);
			memMapper.stateLoad(buf);
			ppu.stateLoad(buf);
			success = true;
		}else{
			System.err.println("State file has wrong format. version="+buf.readByte(0));
			success = false;
		}
		
		// Continue emulation:
		if(continueEmulation){
			startEmulation();
		}
		
		return success;
		
	}
	
	public void stateSave(ByteBuffer buf){
		boolean continueEmulation = isRunning();
		stopEmulation();
		
		buf.goTo(0);
		// Version:
		buf.putByte((short)1);
		
		// Let units save their state:
		cpuMem.stateSave(buf);
		ppuMem.stateSave(buf);
		sprMem.stateSave(buf);
		cpu.stateSave(buf);
		memMapper.stateSave(buf);
		ppu.stateSave(buf);
		
		// Continue emulation:
		if(continueEmulation){
			startEmulation();
		}
		
	}
	
	public boolean isRunning(){
		return isRunning;
	}
	
	public void startEmulation(){
		if(NES.enableSound && !papu.isRunning()){
			papu.start();
		}		
		if(rom!=null && rom.isValid() && !cpu.isRunning()){
			cpu.beginExecution();
			isRunning = true;
		}
	}
	
	public void stopEmulation(){
		if(cpu.isRunning()){
			cpu.endExecution();
			isRunning = false;
		}
		if(NES.enableSound && papu.isRunning()){
			papu.stop();
		}
	}
	
	public void reloadRom(){
		// Can't load ROM while still running.
		if(isRunning){
			stopEmulation();
		}
		
		// Load ROM file:
		rom.reload();
		if(rom.isValid()){
			// The CPU will load
			// the ROM into the CPU
			// and PPU memory.
			reset();
			
			memMapper = rom.createMapper();
			memMapper.init(this);
			memMapper.loadROM(rom);
			ppu.setMirroring(rom.getMirroringType());
			
//			if(gameGenie.getCodeCount()>0){
//				memMapper.setGameGenieState(true);
//			}
		}
		rom.isValid();
	}
	
//	private void clearCPUMemory(){
//		short flushval = Globals.memoryFlushValue;
//		for(int i=0;i<0x2000;i++){
//			cpuMem.getMem()[i] = flushval;
//		}
//		for(int p=0;p<4;p++){
//			int i = p*0x800;
//			cpuMem.getMem()[i+0x008] = 0xF7;
//			cpuMem.getMem()[i+0x009] = 0xEF;
//			cpuMem.getMem()[i+0x00A] = 0xDF;
//			cpuMem.getMem()[i+0x00F] = 0xBF;
//		}
//	}
	
//	public void setGameGenieState(boolean enable){
//		if(memMapper!=null){
//			memMapper.setGameGenieState(enable);
//		}
//	}
	
	// Returns CPU object.
	public CPU6502 getCpu(){
		return cpu;
	}
	
	// Returns PPU object.
	public PPU getPpu(){
		return ppu;
	}
	
	// Returns pAPU object.
	public PAPU getPapu(){
		return papu;
	}
	
	// Returns CPU Memory.
	public Memory getCpuMemory(){
		return cpuMem;
	}
	
	// Returns PPU Memory.
	public Memory getPpuMemory(){
		return ppuMem;
	}
	
	// Returns Sprite Memory.
	public Memory getSprMemory(){
		return sprMem;
	}
	
	// Returns the currently loaded ROM.
	public ROM getRom(){
		return rom;
	}
	
	// Returns the GUI.
	public NesShell getGui(){
		return gui;
	}
	
	// Returns the memory mapper.
	public MemoryMapper getMemoryMapper(){
		return memMapper;
	}
	
//	// Returns the Game Genie:
//	public GameGenie getGameGenie(){
//		return gameGenie;
//	}
	
	public PaletteTable getPaletteTable() {
		return paletteTable;
	}

	// Loads a ROM file into the CPU and PPU.
	// The ROM file is validated first.
	public boolean loadRom(String file){
		// Can't load ROM while still running.
		if(isRunning){
			stopEmulation();
		}
		
		// Load ROM file:
		rom = new ROM();
		rom.load(file, this);
		if(rom.isValid()){
			// The CPU will load
			// the ROM into the CPU
			// and PPU memory.
			reset();
			
			memMapper = rom.createMapper();
			memMapper.init(this);
			memMapper.loadROM(rom);
			ppu.setMirroring(rom.getMirroringType());
			
//			if(gameGenie.getCodeCount()>0){
//				memMapper.setGameGenieState(true);
//			}
		}
		return rom.isValid();
	}
	
	// Resets the system.
	public void reset(){
		if(rom!=null){
			rom.close();
		}
		if(memMapper != null){
			memMapper.reset();
		}
		
		cpuMem.reset();
		ppuMem.reset();
		sprMem.reset();
		
//		clearCPUMemory();
		
		cpu.reset();
		cpu.init();
		ppu.reset();
		paletteTable.reset();	
		papu.reset();
		
		if(gui!=null){
			InputHandler joy1 = gui.getJoy1();
			if(joy1!=null){
				joy1.reset();
			}
		}
	}
	
	// Enable or disable sound playback.
	public void enableSound(boolean enable){
		boolean wasRunning = isRunning();
		if(wasRunning){
			stopEmulation();
		}
		
		if(enable){
			papu.start();
		}else{
			papu.stop();
		}
		
		//System.out.println("** SOUND ENABLE = "+enable+" **");
		NES.enableSound = enable;
		
		if(wasRunning){
			startEmulation();
		}
	}
	
//	public void setFramerate(int rate){
//		Globals.preferredFrameRate = rate;
//		Globals.frameTime = 1000000/rate;
//		papu.setSampleRate(papu.getSampleRate(),false);
//	}
	
//	public void destroy(){
//		if(cpu!=null)cpu.destroy();
//		if(ppu!=null)ppu.destroy();
//		if(papu!=null)papu.destroy();
//		if(cpuMem!=null)cpuMem.destroy();
//		if(ppuMem!=null)ppuMem.destroy();
//		if(sprMem!=null)sprMem.destroy();
//		if(memMapper!=null)memMapper.destroy();
//		if(rom!=null)rom.destroy();
//		
//		gui = null;
//		cpu = null;
//		ppu = null;
//		papu = null;
//		cpuMem = null;
//		ppuMem = null;
//		sprMem = null;
//		memMapper = null;
//		rom = null;
//		gameGenie = null;
//		paletteTable = null;
//	}
}