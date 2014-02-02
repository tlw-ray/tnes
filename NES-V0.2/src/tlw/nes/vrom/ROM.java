package tlw.nes.vrom;
import java.util.logging.Logger;
import java.util.zip.CRC32;

import tlw.nes.NES;
import tlw.nes.vcpu.CpuInfo;
import tlw.nes.vppu.Tile;

/**
 * 
 * @author tlw_ray
 * ROM:
 * 		Head:
 * 			Head Format(length 16 byte):
 * 				00-03 byte is 'N', 'E', 'S', (1A) sign the nes format.
 * 				04-07 byte is the ROM infomations.
 * 				08-11 byte is zero
 * 				12-15 byte is zero
 * 		ROM:
 * 				ROM length is 0x4000; ROM count define in head.
 * 		VROM:
 * 				VROM length 0x100; VROM count define in head.
 */
public class ROM{
	// Mirroring types:
	public static final int MIRRORING_VERTICAL     	= 0;
	public static final int MIRRORING_HORIZONTAL   	= 1;
	public static final int MIRRORING_FOUR   		= 2;
	public static final int MIRRORING_SINGLE 		= 3;
	public static final int MIRRORING_SINGLE2		= 4;
	public static final int MIRRORING_SINGLE3		= 5;
	public static final int MIRRORING_SINGLE4		= 6;
	public static final int MIRRORING_CHRROM      	= 7;
	
	public static final int ROM_SIZE		=0x4000;
	public static final int BATTERYROM_SIZE	=0x2000;
	public static final int VROM_SIZE		=0x1000;
	public static final int TILE_COUNT		=0x100;
	public static final int HEAD_LENGTH		=0x10;
	
	private static String[] MAPPER_NAMES;
	private static boolean[] MAPPER_SUPPORTED;
	
	private boolean failedSaveFile=false;
	private boolean saveRamUpToDate=true;
	
	private short[] header;
	private short[][] rom;
	private short[][] vrom;
	private short[] saveRam;
	private Tile[][] vromTile;
	
	private NES nes;
	private int romCount;
	private int vromCount;
	private int mirroring;
	private boolean batteryRam;
	private boolean trainer;
	private boolean fourScreen;
	private int mapperType;
	private String fileName;

	private long crc32 = 0;
	
	static{
		MAPPER_NAMES = new String[TILE_COUNT];
		MAPPER_SUPPORTED = new boolean[TILE_COUNT];
		for(int i=0;i<TILE_COUNT;i++){
			MAPPER_NAMES[i] = "Unknown Mapper";
		}

		MAPPER_NAMES[ 0] = "NROM";
		MAPPER_NAMES[ 1] = "Nintendo MMC1";
		MAPPER_NAMES[ 2] = "UxROM";
		MAPPER_NAMES[ 3] = "CNROM";
		MAPPER_NAMES[ 4] = "Nintendo MMC3";
		MAPPER_NAMES[ 5] = "Nintendo MMC5";
		MAPPER_NAMES[ 6] = "FFE F4xxx";
		MAPPER_NAMES[ 7] = "AxROM";
		MAPPER_NAMES[ 8] = "FFE F3xxx";
		MAPPER_NAMES[ 9] = "Nintendo MMC2";
		MAPPER_NAMES[10] = "Nintendo MMC4";
		MAPPER_NAMES[11] = "Color Dreams";
		MAPPER_NAMES[12] = "FFE F6xxx";
		MAPPER_NAMES[13] = "CPROM";
        MAPPER_NAMES[15] = "iNES Mapper #015";
		MAPPER_NAMES[16] = "Bandai";
		MAPPER_NAMES[17] = "FFE F8xxx";
		MAPPER_NAMES[18] = "Jaleco SS8806";
		MAPPER_NAMES[19] = "Namcot 106";
		MAPPER_NAMES[20] = "(Hardware) Famicom Disk System";
		MAPPER_NAMES[21] = "Konami VRC4a, VRC4c";
		MAPPER_NAMES[22] = "Konami VRC2a";
		MAPPER_NAMES[23] = "Konami VRC2b, VRC4e, VRC4f";
		MAPPER_NAMES[24] = "Konami VRC6a";
		MAPPER_NAMES[25] = "Konami VRC4b, VRC4d";
		MAPPER_NAMES[26] = "Konami VRC6b";
		MAPPER_NAMES[32] = "Irem G-101";
		MAPPER_NAMES[33] = "Taito TC0190, TC0350";
		MAPPER_NAMES[34] = "BxROM, NINA-001";
        MAPPER_NAMES[41] = "Caltron 6-in-1";
		MAPPER_NAMES[46] = "Rumblestation 15-in-1";
		MAPPER_NAMES[47] = "Nintendo MMC3 Multicart (Super Spike V'Ball + Nintendo World Cup)";
		MAPPER_NAMES[48] = "iNES Mapper #048";
		MAPPER_NAMES[64] = "Tengen RAMBO-1";
		MAPPER_NAMES[65] = "Irem H-3001";
		MAPPER_NAMES[66] = "GxROM";
		MAPPER_NAMES[67] = "Sunsoft 3";
		MAPPER_NAMES[68] = "Sunsoft 4";
		MAPPER_NAMES[69] = "Sunsoft FME-7";
		MAPPER_NAMES[70] = "iNES Mapper #070";
		MAPPER_NAMES[71] = "Camerica";
		MAPPER_NAMES[72] = "iNES Mapper #072";
		MAPPER_NAMES[73] = "Konami VRC3";
		MAPPER_NAMES[75] = "Konami VRC1";
		MAPPER_NAMES[76] = "iNES Mapper #076 (Digital Devil Monogatari - Megami Tensei)";
		MAPPER_NAMES[77] = "iNES Mapper #077 (Napoleon Senki)";
		MAPPER_NAMES[78] = "Irem 74HC161/32";
		MAPPER_NAMES[79] = "American Game Cartridges";
		MAPPER_NAMES[80] = "iNES Mapper #080";
		MAPPER_NAMES[82] = "iNES Mapper #082";
		MAPPER_NAMES[85] = "Konami VRC7a, VRC7b";
		MAPPER_NAMES[86] = "iNES Mapper #086 (Moero!! Pro Yakyuu)";
		MAPPER_NAMES[87] = "iNES Mapper #087";
		MAPPER_NAMES[88] = "iNES Mapper #088";
		MAPPER_NAMES[89] = "iNES Mapper #087 (Mito Koumon)";
        MAPPER_NAMES[92] = "iNES Mapper #092";
		MAPPER_NAMES[93] = "iNES Mapper #093 (Fantasy Zone)";
		MAPPER_NAMES[94] = "iNES Mapper #094 (Senjou no Ookami)";
		MAPPER_NAMES[95] = "iNES Mapper #095 (Dragon Buster) [MMC3 Derived]";
		MAPPER_NAMES[96] = "(Hardware) Oeka Kids Tablet";
		MAPPER_NAMES[97] = "iNES Mapper #097 (Kaiketsu Yanchamaru)";
		MAPPER_NAMES[105] = "NES-EVENT [MMC1 Derived]";
        MAPPER_NAMES[113] = "iNES Mapper #113";
		MAPPER_NAMES[115] = "iNES Mapper #115 (Yuu Yuu Hakusho Final) [MMC3 Derived]";
		MAPPER_NAMES[118] = "iNES Mapper #118 [MMC3 Derived]";
		MAPPER_NAMES[119] = "TQROM";
		MAPPER_NAMES[140] = "iNES Mapper #140 (Bio Senshi Dan)";
		MAPPER_NAMES[152] = "iNES Mapper #152";
		MAPPER_NAMES[154] = "iNES Mapper #152 (Devil Man)";
		MAPPER_NAMES[159] = "Bandai (Alternate of #016)";
		MAPPER_NAMES[180] = "(Hardware) Crazy Climber Controller";
        MAPPER_NAMES[182] = "iNES Mapper #182";
		MAPPER_NAMES[184] = "iNES Mapper #184";
		MAPPER_NAMES[185] = "iNES Mapper #185";
		MAPPER_NAMES[207] = "iNES Mapper #185 (Fudou Myouou Den)";
		MAPPER_NAMES[228] = "Active Enterprises";
		MAPPER_NAMES[232] = "Camerica (Quattro series)";
		
		// The mappers supported:
		MAPPER_SUPPORTED[ 0] = true; // No Mapper
		MAPPER_SUPPORTED[ 1] = true; // MMC1
		MAPPER_SUPPORTED[ 2] = true; // UNROM
		MAPPER_SUPPORTED[ 3] = true; // CNROM
		MAPPER_SUPPORTED[ 4] = true; // MMC3
		MAPPER_SUPPORTED[ 7] = true; // AOROM
		MAPPER_SUPPORTED[ 9] = true; // MMC2
		MAPPER_SUPPORTED[10] = true; // MMC4
		MAPPER_SUPPORTED[11] = true; // ColorDreams
        MAPPER_SUPPORTED[15] = true;
        MAPPER_SUPPORTED[18] = true;
        MAPPER_SUPPORTED[21] = true;
        MAPPER_SUPPORTED[22] = true;
        MAPPER_SUPPORTED[23] = true;
        MAPPER_SUPPORTED[32] = true;
        MAPPER_SUPPORTED[33] = true;
		MAPPER_SUPPORTED[34] = true; // BxROM
        MAPPER_SUPPORTED[48] = true;
        MAPPER_SUPPORTED[64] = true;
		MAPPER_SUPPORTED[66] = true; // GNROM
		MAPPER_SUPPORTED[68] = true; // SunSoft4 chip
		MAPPER_SUPPORTED[71] = true; // Camerica
        MAPPER_SUPPORTED[72] = true;
        MAPPER_SUPPORTED[75] = true;
        MAPPER_SUPPORTED[78] = true;
        MAPPER_SUPPORTED[79] = true;
        MAPPER_SUPPORTED[87] = true;
        MAPPER_SUPPORTED[94] = true;
        MAPPER_SUPPORTED[105] = true;
        MAPPER_SUPPORTED[140] = true;
        MAPPER_SUPPORTED[182] = true;
		MAPPER_SUPPORTED[232] = true; // Camerica /Quattro
	}
	public void reload(){
		FileLoader loader = new FileLoader();
		short[] b = loader.loadFile(fileName);
		
		// Read header:
		header = new short[HEAD_LENGTH];
		for(int i=0;i<HEAD_LENGTH;i++){
			header[i] = b[i];
		}
		
		if(!isValid()){
			return ;
		}
		
		// Read header:
		romCount = header[4];
		vromCount = header[5]*2; // Get the number of 4kB banks, not 8kB
		mirroring = ((header[6]&1)!=0?1:0);
		batteryRam = (header[6]&2)!=0;
		trainer =    (header[6]&4)!=0;
		fourScreen = (header[6]&8)!=0;
		mapperType = (header[6]>>4)|(header[7]&0xF0);
		
		// Battery RAM?
		if(batteryRam){
			loadBatteryRam("temp");
		}
		
		// Check whether byte 8-15 are zero's:
		boolean foundError = false;
		for(int i=8;i<HEAD_LENGTH;i++){
			if(header[i]!=0){
				foundError = true;
				break;
			}
		}
		if(foundError){
			// Ignore byte 7.
			mapperType&=0xF;
		}
		
		rom = new short[romCount][ROM_SIZE];
		vrom = new short[vromCount][VROM_SIZE];
		vromTile = new Tile[vromCount][TILE_COUNT];
		
		//try{
			
			// Load PRG-ROM banks:
			int offset = 16;
			for(int i=0;i<romCount;i++){
				for(int j=0;j<ROM_SIZE;j++){
					if(offset+j >= b.length){
						break;
					}
					rom[i][j] = b[offset+j];
				}
				offset+=ROM_SIZE;
			}
			
			// Load CHR-ROM banks:
			for(int i=0;i<vromCount;i++){
				for(int j=0;j<VROM_SIZE;j++){
					if(offset+j >= b.length){
						break;
					}
					vrom[i][j] = b[offset+j];
				}
				offset+=VROM_SIZE;
			}
			
			// Create VROM tiles:
			for(int i=0;i<vromCount;i++){
				for(int j=0;j<TILE_COUNT;j++){
					vromTile[i][j] = new Tile();
				}
			}
			
			// Convert CHR-ROM banks to tiles:
			//System.out.println("Converting CHR-ROM image data..");
			//System.out.println("VROM bank count: "+vromCount);
			int tileIndex;
			int leftOver;
			for(int v=0;v<vromCount;v++){
				for(int i=0;i<VROM_SIZE;i++){
					tileIndex = i>>4;
					leftOver = i%16;
					if(leftOver<8){
						vromTile[v][tileIndex].setScanline(leftOver,vrom[v][i],vrom[v][i+8]);
					}else{
						vromTile[v][tileIndex].setScanline(leftOver-8,vrom[v][i-8],vrom[v][i]);
					}
				}
			}
			
			/*
			tileIndex = (address+i)>>4;
			leftOver = (address+i) % 16;
			if(leftOver<8){
				ptTile[tileIndex].setScanline(leftOver,value[offset+i],ppuMem.load(address+8+i));
			}else{
				ptTile[tileIndex].setScanline(leftOver-8,ppuMem.load(address-8+i),value[offset+i]);
			}
			*/
			
		/*}catch(Exception e){
			//System.out.println("Error reading ROM & VROM banks. Corrupt file?");
			valid = false;
			return;
		}*/

        // Record CRC32 for Cartridge
		CRC32 crc = new CRC32();
		byte[] tempArray = new byte[rom.length + vrom.length];

		crc.update(tempArray);
		crc32 = crc.getValue();
		tempArray = null;

		System.out.println("CRC Value: " +crc32+ "");

	}
	public void load(String fileName, NES nes){
		this.fileName = fileName;
		this.nes=nes;
		reload();
	}
	
	public boolean isValid(){
		// Check first four bytes:
		String fcode = new String(new byte[]{(byte)header[0],(byte)header[1],(byte)header[2],(byte)header[3]});
		if(!fcode.equals("NES"+new String(new byte[]{0x1A}))){
			return false;
		}else{
			return true;
		}
	}
	
	public int getRomBankCount(){
		return romCount;
	}
	
	// Returns number of 4kB VROM banks.
	public int getVromBankCount(){
		return vromCount;
	}
	
	public short[] getRomBank(int bank){
		return rom[bank];
	}
	
	public short[] getVromBank(int bank){
		return vrom[bank];
	}
	
	public Tile[] getVromBankTiles(int bank){
		return vromTile[bank];
	}
	
	public int getMirroringType(){
		if(fourScreen){
			return MIRRORING_FOUR;
		}
		if(mirroring == 0){
			return MIRRORING_HORIZONTAL;
		}else{
			return MIRRORING_VERTICAL;
		}
	}
	
	public boolean hasBatteryRam(){
		return batteryRam;
	}
	
	public MemoryMapper createMapper(){
        if (mapperType<MAPPER_SUPPORTED.length && mapperType>=0 && MAPPER_SUPPORTED[mapperType]) {
            switch (mapperType) {
                case 0:
                    return new MapperDefault();
                case 1:
                    return new Mapper001();
                case 2:
                    return new Mapper002();
                case 3:
                    return new Mapper003();
                case 4:
                    return new Mapper004();
                case 7:
                    return new Mapper007();
                case 9:
                    return new Mapper009();
                case 10:
                    return new Mapper010();
                case 11:
                    return new Mapper011();
                case 15:
                    return new Mapper015();
                case 18:
                    return new Mapper018();
                case 21:
                    return new Mapper021();
                case 22:
                    return new Mapper022();
                case 23:
                    return new Mapper023();
                case 32: 
                    return new Mapper032();
                case 33:
                    return new Mapper033();
                case 34:
                    return new Mapper034();
                case 48:
                    return new Mapper048();
                case 64:
                    return new Mapper064();
                case 66:
                    return new Mapper066();
                case 68:
                    return new Mapper068();
                case 71:
                    return new Mapper071();
                case 72:
                    return new Mapper072();
                case 75:
                    return new Mapper075();
                case 78:
                    return new Mapper078();
                case 79:
                    return new Mapper079();
                case 87:
                    return new Mapper087();
                case 94:
                    return new Mapper094();
                case 105:
                    return new Mapper105();
                case 140:
                    return new Mapper140();
                case 182:
                    return new Mapper182();
                case 232:
                    return new Mapper232();
            }
        }

		// If the mapper wasn't supported, create the standard one:
        Logger.getAnonymousLogger().severe("Warning: Mapper not supported yet.");
		return new MapperDefault();
		
	}
	
	public void setSaveState(boolean enableSave){
		if(enableSave && !batteryRam){
			loadBatteryRam("temp");
		}
	}
	
	public short[] getBatteryRam(){
		return saveRam;
	}
	
	private void loadBatteryRam(String encodedData){
		if(batteryRam){
			try{
				saveRam = new short[0x2000];
				saveRamUpToDate = true;
				
				// Get hex-encoded memory string from user:
	//			String encodedData = JOptionPane.showInputDialog("Returning players insert Save Code here.");
	//			if (encodedData==null){
					// User cancelled the dialog.
	//				return;
	//			}
				
				// Remove all garbage from encodedData:
				encodedData = encodedData.replaceAll("[^\\p{XDigit}]", "");
				if (encodedData.length()!=saveRam.length*2){
					// Either too few or too many digits.
					return;
				}
				
				// Convert hex-encoded memory string to bytes:
				for (int i=0;i<saveRam.length;i++){
					String hexByte = encodedData.substring(i*2,i*2+2);
					saveRam[i] = Short.parseShort(hexByte, 16);
				}
				
				Logger.getAnonymousLogger().info("Battery RAM loaded.");
				if(nes.getMemoryMapper()!=null){
					nes.getMemoryMapper().loadBatteryRam();
				}
			}catch(Exception e){
				//System.out.println("Unable to get battery RAM from user.");
				failedSaveFile = true;
				e.printStackTrace();
			}
		}
	}
	
	public void writeBatteryRam(int address, short value){
		if(!failedSaveFile && !batteryRam){
			loadBatteryRam("temp");//TODO 这里需要用户输入信息
		}
		//System.out.println("Trying to write to battery RAM. batteryRam="+batteryRam+" enableSave="+enableSave);
		if(batteryRam && !failedSaveFile){
			saveRam[address-CpuInfo.MM_EXPANSION_MODULES] = value;
			saveRamUpToDate = false;
		}
	}

	public void close(){
		if(batteryRam && !saveRamUpToDate){
			try{
				// Convert bytes to hex-encoded memory string:
				StringBuilder sb = new StringBuilder(saveRam.length*2 + saveRam.length/38);
				for (int i=0;i<saveRam.length;i++){
					String hexByte = String.format("%02x", saveRam[i] & 0xFF);
					if (i%38==0 && i!=0){
						// Put spacing in so that word wrap will work.
						sb.append(" ");
					}
					sb.append(hexByte);
				}
//				String encodedData = sb.toString();
				
				// Send hex-encoded memory string to user:
//				JOptionPane.showInputDialog("Save Code for Resuming Game.", encodedData);
				
				saveRamUpToDate = true;
				//System.out.println("Battery RAM sent to user.");
			}catch(Exception e){
				//System.out.println("Trouble sending battery RAM to user.");
				e.printStackTrace();
			}
		}
	}
	public boolean isTrainer() {
		return trainer;
	}
	public NES getNes() {
		return nes;
	}
	public String getFileName() {
		return fileName;
	}
	public Tile[][] getVromTile() {
		return vromTile;
	}
	public int getMirroring() {
		return mirroring;
	}
	public String getMapperTypeDescription() {
	    if(mapperType>=0 && mapperType<MAPPER_NAMES.length){
			return MAPPER_NAMES[mapperType];
		}
		return "Unknown Mapper, "+mapperType;
	}
	public boolean isFourScreen() {
		return fourScreen;
	}
	public short[][] getRom() {
		return rom;
	}
	
}