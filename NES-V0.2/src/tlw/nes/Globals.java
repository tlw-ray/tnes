package tlw.nes;
public class Globals{
	
	public static double CPU_FREQ_NTSC = 1789772.5d;
	public static double CPU_FREQ_PAL  = 1773447.4d;
	
	public static int preferredFrameRate = 60;
	// Microseconds per frame:
	public static int frameTimeM =17;//1000/60
	public static int frameTimeN =16666667;
	public static final int COLOR_BG=0;
	
	// What value to flush memory with on power-up:
//	public static short memoryFlushValue = 0xFF;
	
	// «∑Ò…˘“Ù
	public static boolean enableSound = true;
	
	//////////// Rendering Options://///////
	//œ‘ æ…˘“Ùª∫≥Â
	public static boolean showSoundBuffer = true;
	//Àı∑≈µΩµÁ ”≥ﬂ¥Á
	public static boolean clipToTvSize = true;
	//showSpr0Hit
	public static boolean showSpr0Hit = false;
	
	public static boolean disableSprites = false;
	public static boolean palEmulation=true;
}