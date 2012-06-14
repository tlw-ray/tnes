package tlw.nes.core;

import tlw.nes.NES;
import tlw.nes.vmemory.ByteBuffer;
import tlw.nes.vrom.ROM;

public interface MemoryMapper{
	//CPU Memory Map
	static final int CPU_MEM_TOP_LIMIT=0x10000;
	static final int CPU_MEM_BANK_UPPER=0xC000;			//Upper Bank of Cartridge ROM            卡带的上层ROM
	static final int CPU_MEM_BANK_LOWER=0x8000;			//Lower Bank of Cartridge ROM            卡带的下层ROM
	static final int CPU_MEM_RAM_CARTRIDGE=0x6000;		//Cartridge RAM (may be battery-backed)  卡带的RAM（可能有电池支持）
	static final int CPU_MEM_EXPANSION_MODULES=0x5000;	//Expansion Modules                      扩充的模块
	static final int CPU_MEM_IO=0x2000;					//Input/Output                           输入/输出
	static final int CPU_MEM_RAM_INTERNAL=0x0;			//2kB Internal RAM, mirrored 4 times     2KB的内部RAM，做4次镜象

	//PPU Memory Map
	static final int PPU_MEM_TOP_LIMIT=0x4000;
	static final int PPU_MEM_EMPTYP01=0x3F20;			//Empty                                  空
	static final int PPU_MEM_PALETTE_SPRITE=0x3F10;		//Sprite Palette                         动画/精灵调色板
	static final int PPU_MEM_PALETTE_IMAGE=0x3F00;		//Image Palette                          图象调色板
	static final int PPU_MEM_EMPTY02=0x3000;			//Empty                                  空
	static final int PPU_MEM_TABLE_ATTR_3=0x2FC0;		//Attribute Table 3                      属性表3
	static final int PPU_MEM_TABLE_NAME_3=0x2C00;		//Name Table 3 (32x30 tiles)             名字表3（32X30块）
	static final int PPU_MEM_TABLE_ATTR_2=0x2BC0;		//Attribute Table 2                      属性表2
	static final int PPU_MEM_TABLE_NAME_2=0x2800;		//Name Table 2 (32x30 tiles)             名字表2（32X30块）
	static final int PPU_MEM_TABLE_ATTR_1=0x27C0;		//Attribute Table 1                      属性表1
	static final int PPU_MEM_TABLE_NAME_1=0x2400;		//Name Table 1 (32x30 tiles)             名字表1（32X30块）
	static final int PPU_MEM_TABLE_ATTR_0=0x23C0;		//Attribute Table 0                      属性表0
	static final int PPU_MEM_TABLE_NAME_0=0x2000;		//Name Table 0 (32x30 tiles)             名字表0（32X30块）
	static final int PPU_MEM_TABLE_PATTERN_1=0x1000;	//Pattern Table 1 (256x2x8, may be VROM) 调色板表1（256X2X8，可能是VROM）
	static final int PPU_MEM_TABLE_PATTERN_0=0x0000;	//Pattern Table 0 (256x2x8, may be VROM) 调色板表0（256X2X8，可能是VROM）
	
	static final int PPU_CONTROL_REGISTER_1=0x2000;
	
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