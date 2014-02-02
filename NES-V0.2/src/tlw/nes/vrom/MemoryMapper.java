package tlw.nes.vrom;

import tlw.nes.NES;
import tlw.nes.vmemory.ByteBuffer;

public interface MemoryMapper{
	static final int PPU_REGISTER_CONTROL1=0x2000;		// | RW  | PPU Control Register 1
	static final int PPU_REGISTER_CONTROL2=0x2001;		// | RW  | PPU Control Register 2
	static final int PPU_REGISTER_STAUTS=0x2002;		// | R   | PPU Status Register
	static final int PPU_SPRITE_ADDRESS=0x2003;			// | W   | Sprite Memory Address
	static final int PPU_SPRITE_MEMORY_DATA=0x2004;		// | RW  | Sprite Memory Data
	static final int PPU_SCREEN_SCROLL_OFFSET=0x2005;	// | W   | Screen Scroll Offsets
	static final int PPU_MEMORY_ADDRESS=0x2006;			// | W   | PPU Memory Address					
	static final int PPU_MEMORY_DATA=0x2007;			// | RW  | PPU Memory Data
	static final int PPU_SOUND_REGISTERS=0x4000;		//0x4000-$4013 | Sound Registers
	static final int PPU_DMA_ACCESS_SPRINT=0x4014;		// | W   | DMA Access to the Sprite Memory
	static final int PPU_SOUND_CHANNEL_SWITCH=0x4015;	// | W   | Sound Channel Switch
	static final int PPU_JOYSTICK1=0x4016;				// | RW  | Joystick1 + Strobe
	static final int PPU_JOYSTICK2=0x4017;				// | RW  | Joystick2 + Strobe
	
	public void init(NES nes);
	public void loadROM(ROM rom);
	public void write(int address, short value);
	public short load(int address);
	public short joy1Read();
	public short joy2Read();
	public void reset();
//	public void setGameGenieState(boolean value);
	public void clockIrqCounter();
	public void loadBatteryRam();
	public void stateLoad(ByteBuffer buf);
	public void stateSave(ByteBuffer buf);
	public void setMouseState(boolean pressed, int x, int y);
	public void latchAccess(int address);
	
}