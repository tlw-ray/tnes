/**
 * 
 */
/**
 * @author liwei
 * 测试线程模型执行序列
 * 保证线程以（服务线）-->（读、写线程）-->（服务线程）-->（读、写线程）...
 * 这样的顺序运行
 */
package tlw.nes.server.tst01_order;