package tlw.nes.vrom;

import tlw.nes.Globals;
import tlw.nes.NES;
import tlw.nes.core.InputHandler;
import tlw.nes.core.MemoryMapper;
import tlw.nes.vcpu.CPU6502;
import tlw.nes.vmemory.ByteBuffer;
import tlw.nes.vmemory.Memory;
import tlw.nes.vppu.PPU;
import tlw.nes.vppu.Tile;

public class MapperDefault implements MemoryMapper {

    public NES nes;
    public int joy1StrobeState;
    public int joy2StrobeState;
    public int joypadLastWrite;
    public boolean mousePressed;
    public boolean gameGenieActive;
    public int mouseX;
    public int mouseY;
    int tmp;
    long crc;

    public void init(NES nes) {

        this.nes = nes;
        joypadLastWrite = -1;

    }

    public void stateLoad(ByteBuffer buf) {

        // Check version:
        if (buf.readByte() == 1) {

            // Joypad stuff:
            joy1StrobeState = buf.readInt();
            joy2StrobeState = buf.readInt();
            joypadLastWrite = buf.readInt();

            // Mapper specific stuff:
            mapperInternalStateLoad(buf);

        }

    }

    public void stateSave(ByteBuffer buf) {

        // Version:
        buf.putByte((short) 1);

        // Joypad stuff:
        buf.putInt(joy1StrobeState);
        buf.putInt(joy2StrobeState);
        buf.putInt(joypadLastWrite);

        // Mapper specific stuff:
        mapperInternalStateSave(buf);

    }

    protected void mapperInternalStateLoad(ByteBuffer buf) {

        buf.putByte((short) joy1StrobeState);
        buf.putByte((short) joy2StrobeState);
        buf.putByte((short) joypadLastWrite);

    }

    protected void mapperInternalStateSave(ByteBuffer buf) {

        joy1StrobeState = buf.readByte();
        joy2StrobeState = buf.readByte();
        joypadLastWrite = buf.readByte();

    }

    public void setGameGenieState(boolean enable) {
        gameGenieActive = enable;
    }

//    public boolean getGameGenieState() {
//        return gameGenieActive;
//    }

    public void write(int address, short value) {
    	ROM rom=nes.getRom();
        if (address < 0x2000) {
            // Mirroring of RAM:
            nes.getCpuMemory().getMem()[address & 0x7FF] = value;

        } else if (address > 0x4017) {

        	nes.getCpuMemory().getMem()[address] = value;
            if (address >= CPU_MEM_RAM_CARTRIDGE && address < CPU_MEM_BANK_LOWER) {

                // Write to SaveRAM. Store in file:
                if (rom != null) {
                	rom.writeBatteryRam(address, value);
                }

            }

        } else if (address > 0x2007 && address < ROM.ROM_SIZE) {

            regWrite(0x2000 + (address & 0x7), value);

        } else {

            regWrite(address, value);

        }

    }

    public void writelow(int address, short value) {

        if (address < CPU_MEM_IO) {
            // Mirroring of RAM:
            nes.getCpuMemory().getMem()[address & 0x7FF] = value;

        } else if (address > 0x4017) {
        	nes.getCpuMemory().getMem()[address] = value;

        } else if (address > 0x2007 && address < ROM.ROM_SIZE) {
            regWrite(CPU_MEM_IO + (address & 0x7), value);

        } else {
            regWrite(address, value);
        }

    }

    public short load(int address) {

        // Game Genie codes active?
//        if (gameGenieActive) {
//            if (nes.getGameGenie().addressMatch[address]) {
//
//                tmp = nes.getGameGenie().getCodeIndex(address);
//
//                // Check the code type:
//                if (nes.getGameGenie().getCodeType(tmp) == GameGenie.TYPE_6CHAR) {
//
//                    // Return the code value:
//                    return (short) nes.getGameGenie().getCodeValue(tmp);
//
//                } else {
//
//                    // Check whether the actual value equals the compare value:
//                    if (nes.getCpuMemory().getMem()[address] == nes.getGameGenie().getCodeCompare(tmp)) {
//
//                        // The values match, so use the supplied game genie value:
//                        return (short) nes.getGameGenie().getCodeValue(tmp);
//
//                    }
//
//                }
//            }
//        }

        // Wrap around:
        address &= 0xFFFF;

        // Check address range:
        if (address > 0x4017) {

            // ROM:
            return nes.getCpuMemory().getMem()[address];

        } else if (address >= CPU_MEM_IO) {

            // I/O Ports.
            return regLoad(address);

        } else {

            // RAM (mirrored)
            return nes.getCpuMemory().getMem()[address & 0x7FF];

        }

    }

    protected short regLoad(int address) {

        switch (address >> 12) { // use fourth nibble (0xF000)

            case 0: {
                break;
            }
            case 1: {
                break;
            }
            case 2: {
                // Fall through to case 3
            }
            case 3: {

                // PPU Registers
                switch (address & 0x7) {
                    case 0x0: {

                        // 0x2000:
                        // PPU Control Register 1.
                        // (the value is stored both
                        // in main memory and in the
                        // PPU as flags):
                        // (not in the real NES)
                        return nes.getCpuMemory().getMem()[0x2000];

                    }
                    case 0x1: {

                        // 0x2001:
                        // PPU Control Register 2.
                        // (the value is stored both
                        // in main memory and in the
                        // PPU as flags):
                        // (not in the real NES)
                        return nes.getCpuMemory().getMem()[0x2001];

                    }
                    case 0x2: {

                        // 0x2002:
                        // PPU Status Register.
                        // The value is stored in
                        // main memory in addition
                        // to as flags in the PPU.
                        // (not in the real NES)
                        return nes.getPpu().readStatusRegister();

                    }
                    case 0x3: {
                        return 0;
                    }
                    case 0x4: {

                        // 0x2004:
                        // Sprite Memory read.
                        return nes.getPpu().sramLoad();

                    }
                    case 0x5: {
                        return 0;
                    }
                    case 0x6: {
                        return 0;
                    }
                    case 0x7: {

                        // 0x2007:
                        // VRAM read:
                        return nes.getPpu().vramLoad();

                    }
                }
                break;

            }
            case 4: {


                // Sound+Joypad registers

                switch (address - 0x4015) {
                    case 0: {

                        // 0x4015:
                        // Sound channel enable, DMC Status
                        return nes.getPapu().readReg(address);

                    }
                    case 1: {

                        // 0x4016:
                        // Joystick 1 + Strobe
                        return joy1Read();

                    }
                    case 2: {

                        // 0x4017:
                        // Joystick 2 + Strobe
                        if (mousePressed && nes.getPpu() != null && nes.getPpu().getBuffer() != null) {

                            // Check for white pixel nearby:

                            int sx, sy, ex, ey, w;
                            sx = Math.max(0, mouseX - 4);
                            ex = Math.min(256, mouseX + 4);
                            sy = Math.max(0, mouseY - 4);
                            ey = Math.min(240, mouseY + 4);
                            w = 0;

                            for (int y = sy; y < ey; y++) {
                                for (int x = sx; x < ex; x++) {
                                    if ((nes.getPpu().getBuffer()[(y << 8) + x] & 0xFFFFFF) == 0xFFFFFF) {
                                        w = 0x1 << 3;
                                        break;
                                    }
                                }
                            }

                            w |= (mousePressed ? (0x1 << 4) : 0);
                            return (short) (joy2Read() | w);

                        } else {
                            return joy2Read();
                        }

                    }
                }

                break;

            }
        }

        return 0;

    }

    protected void regWrite(int address, short value) {

        switch (address) {
            case PPU_REGISTER_CONTROL1: {
                // PPU Control register 1
                nes.getCpuMemory().write(address, value);
                nes.getPpu().updateControlReg1(value);
                break;

            }
            case PPU_REGISTER_CONTROL2: {
                // PPU Control register 2
                nes.getCpuMemory().write(address, value);
                nes.getPpu().updateControlReg2(value);
                break;
            }
            case PPU_SPRITE_ADDRESS: {
                // Set Sprite RAM address:
                nes.getPpu().writeSRAMAddress(value);
                break;
            }
            case PPU_SPRITE_MEMORY_DATA: {

                // Write to Sprite RAM:
                nes.getPpu().sramWrite(value);
                break;

            }
            case PPU_SCREEN_SCROLL_OFFSET: {

                // Screen Scroll offsets:
                nes.getPpu().scrollWrite(value);
                break;

            }
            case PPU_MEMORY_ADDRESS: {

                // Set VRAM address:
                nes.getPpu().writeVRAMAddress(value);
                break;

            }
            case PPU_MEMORY_DATA: {

                // Write to VRAM:
                nes.getPpu().vramWrite(value);
                break;

            }
            case PPU_DMA_ACCESS_SPRINT: {

                // Sprite Memory DMA Access
                nes.getPpu().sramDMA(value);
                break;

            }
            case PPU_SOUND_CHANNEL_SWITCH: {

                // Sound Channel Switch, DMC Status
                nes.getPapu().writeReg(address, value);
                break;

            }
            case PPU_JOYSTICK1: {

                ////System.out.println("joy strobe write "+value);

                // Joystick 1 + Strobe
                if (value == 0 && joypadLastWrite == 1) {
                    ////System.out.println("Strobes reset.");
                    joy1StrobeState = 0;
                    joy2StrobeState = 0;
                }
                joypadLastWrite = value;
                break;

            }
            case PPU_JOYSTICK2: {

                // Sound channel frame sequencer:
                nes.getPapu().writeReg(address, value);
                break;

            }
            default: {

                // Sound registers
                ////System.out.println("write to sound reg");
                if (address >= ROM.ROM_SIZE && address <= PPU_JOYSTICK2) {
                    nes.getPapu().writeReg(address, value);
                }
                break;

            }
        }

    }

    public short joy1Read() {

        InputHandler in = nes.getGui().getJoy1();
        short ret;

        switch(joy1StrobeState){
        case 0:
        case 1:
        case 2:
        case 3:
        case 4:
        case 5:
        case 6:
        case 7:
        	ret=in.getKeyState(joy1StrobeState);
        	break;
        case 19:
        	ret=1;
        	break;
        default :
        	ret=0;
        }

        joy1StrobeState++;
        if (joy1StrobeState == 24) {
            joy1StrobeState = 0;
        }
        
        return ret;

    }

    public short joy2Read() {
        InputHandler in = nes.getGui().getJoy2();
        int st = joy2StrobeState;

        joy2StrobeState++;
        if (joy2StrobeState == 24) {
            joy2StrobeState = 0;
        }

        if (st == 0) {
            return in.getKeyState(InputHandler.KEY_A);
        } else if (st == 1) {
            return in.getKeyState(InputHandler.KEY_B);
        } else if (st == 2) {
            return in.getKeyState(InputHandler.KEY_SELECT);
        } else if (st == 3) {
            return in.getKeyState(InputHandler.KEY_START);
        } else if (st == 4) {
            return in.getKeyState(InputHandler.KEY_UP);
        } else if (st == 5) {
            return in.getKeyState(InputHandler.KEY_DOWN);
        } else if (st == 6) {
            return in.getKeyState(InputHandler.KEY_LEFT);
        } else if (st == 7) {
            return in.getKeyState(InputHandler.KEY_RIGHT);
        } else if (st == 16) {
            return (short) 0;
        } else if (st == 17) {
            return (short) 0;
        } else if (st == 18) {
            return (short) 1;
        } else if (st == 19) {
            return (short) 0;
        } else {
            return 0;
        }
    }

    public void loadROM(ROM rom) {

        if (!rom.isValid() || rom.getRomBankCount() < 1) {
            //System.out.println("NoMapper: Invalid ROM! Unable to load.");
            return;
        }

        // Load ROM into memory:
        loadPRGROM();

        // Load CHR-ROM:
        loadCHRROM();

        // Load Battery RAM (if present):
        loadBatteryRam();

        // Reset IRQ:
        //nes.getCpu().doResetInterrupt();
        nes.getCpu().requestIrq(CPU6502.IRQ_RESET);

    }

    protected void loadPRGROM() {
    	loadRomBank(0, MemoryMapper.CPU_MEM_BANK_LOWER);
        if (nes.getRom().getRomBankCount() > 1) {
            // Load the two first banks into memory.
            loadRomBank(1, MemoryMapper.CPU_MEM_BANK_UPPER);
        } else {
            // Load the one bank into both memory locations:
            loadRomBank(0, MemoryMapper.CPU_MEM_BANK_UPPER);
        }

    }

    protected void loadCHRROM() {
    	Globals.info("Loading CHR ROM..");
    	ROM rom=nes.getRom();
        if (rom.getVromBankCount() > 0) {
        	loadVromBank(0, MemoryMapper.CPU_MEM_RAM_INTERNAL);
            if (rom.getVromBankCount() == 1) {
                loadVromBank(0, ROM.VROM_SIZE);
            } else {
                loadVromBank(1, ROM.VROM_SIZE);
            }
        } else {
        	Globals.info("There aren't any CHR-ROM banks..");
        }
    }

    public void loadBatteryRam() {
    	ROM rom=nes.getRom();
        if (rom.hasBatteryRam()) {
            short[] ram = rom.getBatteryRam();
            if (ram != null && ram.length == ROM.BATTERYROM_SIZE) {
                // Load Battery RAM into memory:
                System.arraycopy(ram, 0, nes.getCpuMemory().getMem(), CPU_MEM_RAM_CARTRIDGE, ROM.BATTERYROM_SIZE);
            }
        }
    }
    
    protected void loadRomBank(int bank, int address) {
        // Loads a ROM bank into the specified address.
    	ROM rom=nes.getRom();
        bank %= rom.getRomBankCount();
//        short[] data = nes.getRom().getRomBank(bank);
        //cpuMem.write(address,data,data.length);
        short[] bankData=rom.getRomBank(bank);
        Memory cpuMemory=nes.getCpuMemory();
        short[] cpuMemoryData=cpuMemory.getMem();
        System.arraycopy(bankData, 0, cpuMemoryData, address, ROM.ROM_SIZE);
    }

    protected void loadVromBank(int bank, int address) {
    	ROM rom=nes.getRom();
        if (rom.getVromBankCount() == 0) {
            return;
        }
        nes.getPpu().triggerRendering();

        System.arraycopy(rom.getVromBank(bank % rom.getVromBankCount()), 0, nes.getPpuMemory().getMem(), address, 4096);

        Tile[] vromTile = rom.getVromBankTiles(bank % rom.getVromBankCount());
        System.arraycopy(vromTile, 0, nes.getPpu().getPtTile(), address >> 4, 256);

    }

    protected void load32kRomBank(int bank, int address) {
    	ROM rom=nes.getRom();
        loadRomBank((bank * 2) % rom.getRomBankCount(), address);
        loadRomBank((bank * 2 + 1) % rom.getRomBankCount(), address + ROM.ROM_SIZE);

    }

    protected void load8kVromBank(int bank4kStart, int address) {

        if (nes.getRom().getVromBankCount() == 0) {
            return;
        }
        nes.getPpu().triggerRendering();

        loadVromBank((bank4kStart) % nes.getRom().getVromBankCount(), address);
        loadVromBank((bank4kStart + 1) % nes.getRom().getVromBankCount(), address + 4096);

    }

    protected void load1kVromBank(int bank1k, int address) {
    	ROM rom=nes.getRom();
        if (rom.getVromBankCount() == 0) {
            return;
        }
        nes.getPpu().triggerRendering();

        int bank4k = (bank1k / 4) % rom.getVromBankCount();
        int bankoffset = (bank1k % 4) * 1024;
        System.arraycopy(rom.getVromBank(bank4k), 0, nes.getPpuMemory().getMem(), bankoffset, 1024);

        // Update tiles:
        Tile[] vromTile = rom.getVromBankTiles(bank4k);
        int baseIndex = address >> 4;
        for (int i = 0; i < 64; i++) {
            nes.getPpu().getPtTile()[baseIndex + i] = vromTile[((bank1k % 4) << 6) + i];
        }

    }

    protected void load2kVromBank(int bank2k, int address) {
    	ROM rom=nes.getRom();
    	PPU ppu=nes.getPpu();
        if (rom.getVromBankCount() == 0) {
            return;
        }
        ppu.triggerRendering();

        int bank4k = (bank2k / 2) % rom.getVromBankCount();
        int bankoffset = (bank2k % 2) * 2048;
        System.arraycopy(rom.getVromBank(bank4k), bankoffset, nes.getPpuMemory().getMem(), address, 2048);

        // Update tiles:
        Tile[] vromTile = rom.getVromBankTiles(bank4k);
        int baseIndex = address >> 4;
        for (int i = 0; i < 128; i++) {
            ppu.getPtTile()[baseIndex + i] = vromTile[((bank2k % 2) << 7) + i];
        }

    }

    protected void load8kRomBank(int bank8k, int address) {
    	ROM rom=nes.getRom();
        int bank16k = (bank8k / 2) % rom.getRomBankCount();
        int offset = (bank8k % 2) * 8192;

        short[] bank = rom.getRomBank(bank16k);
        nes.getCpuMemory().write(address, bank, offset, 8192);

    }

    public void clockIrqCounter() {
        // Does nothing. This is used by the MMC3 mapper.
    }

    public void latchAccess(int address) {
        // Does nothing. This is used by MMC2.
    }

//    public int syncV() {
//        return 0;
//    }
//
//    public int syncH(int scanline) {
//        return 0;
//      }

//    public void setCRC(long crc) {
//    }

    public void setMouseState(boolean pressed, int x, int y) {

        mousePressed = pressed;
        mouseX = x;
        mouseY = y;

    }

    public void reset() {
        joy1StrobeState = 0;
        joy2StrobeState = 0;
        joypadLastWrite = 0;
        mousePressed = false;

    }
}