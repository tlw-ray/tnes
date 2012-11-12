package tlw.nes;
public class Globals{
	
	public static double CPU_FREQ_NTSC = 1789772.5d;
	public static double CPU_FREQ_PAL  = 1773447.4d;
	
	public static int preferredFrameRate = 60;
	// Microseconds per frame:
	public static int frameTimeM =17;//1000/60
	public static int frameTimeN =16666667;
	public static final int PIXEL_X=256;
	public static final int PIXEL_Y=240;
	public static final int COLOR_BG=0;
	
	// What value to flush memory with on power-up:
//	public static short memoryFlushValue = 0xFF;
	
	public static final boolean debug = false;
	public static final boolean fsdebug = false;
	
	// «∑ÒÕºœÒ
	public static boolean appletMode = true;
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
	public static void error(String msg){
		System.err.println(msg);
	}
	public static void info(String msg){
		System.out.println(msg);
	}
}