package tlw.nes.server.tst;
/**
@author liwei.tang@magustek.com
@since 2014��2��8�� ����4:31:28
call pre second
���ô���ͳ��
 */
public class CallStat {
	//��������
	int pc=0;
	int pc_pack=0;
	static final int LOG_COUNT=10000;
	
	long start=System.currentTimeMillis();
	
	public void call(){
		pc++;
		if(pc>LOG_COUNT){
			System.out.println("running ... "+(pc_pack++)+"*"+LOG_COUNT+" times spend "+(System.currentTimeMillis()-start)+" ms.");
			pc=0;
			start=System.currentTimeMillis();
		}
	}
}
