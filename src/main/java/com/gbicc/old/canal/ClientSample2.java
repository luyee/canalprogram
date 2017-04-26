package com.gbicc.old.canal;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.CanalEntry.*;
import com.alibaba.otter.canal.protocol.Message;
import com.gbicc.kafka.api090.KafkaUtils;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.*;


public class ClientSample2 {
    private static final Logger log= LoggerFactory.getLogger(ClientSample2.class);
	private static RowChange rowChange; 
    private static ResourceBundle rb = ResourceBundle.getBundle("config");
    private static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
    static int i=0;
    private static Set<String> set=new HashSet<>();
   /**
    *  @main 主函数入口类
    */
    public static void start(){
    	//canal的配置文件信息
		String canalURL = rb.getString("canalURL");
		String canalPort = rb.getString("canalPort");
		String destination = rb.getString("destination");
        String filter="crmsdb.crms_abs_finance_detail,crmsdb.crms_busi_apply,crmsdb.crms_abs_finance_plan,crmsdb.crms_abs_finance_repayment_plan,crmsdb.crms_abs_finance_repayment";
    	//创建canal链接，需要输入端口号，地址和destination目标地，instance用户名密码如果未设置可以不输入
    	CanalConnector connector = CanalConnectors.newSingleConnector(new InetSocketAddress(canalURL,  
    			Integer.parseInt(canalPort)), destination, "", "");
        int batchSize = 1000;  
        int emptyCount = 0;
        for (String s : filter.toLowerCase().trim().split(",")) {
            set.add(s.split("\\.")[1]);
        }
        //   链接canal，设置参数   
        try {  
            connector.connect();  
            connector.subscribe(filter);
            //回滚
            connector.rollback();
            while (true) {  
            	// 获取指定数量的数据 
                Message message = connector.getWithoutAck(batchSize); 
                int size = message.getEntries().size();
                //如果没有数据变动
                if ( size == 0) {  
                    emptyCount++;  
                    System.out.println("empty count : " + emptyCount);  
                    try {  
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {  
                        e.printStackTrace();  
                    }  
                //如果有数据变动
                } else {  
                    emptyCount = 0;  
                    // System.out.printf("message[batchId=%s,size=%s] \n", batchId, size);  
                    printEntry(message.getEntries());  
                }
            }  
        } finally {
        	//最后关闭canal链接
            connector.disconnect();
        }
	}
    /**
     * @param entrys
     */
    private static void printEntry( List<Entry> entrys) {
        Producer producer = KafkaUtils.getInstance();
    	
		String topic = rb.getString("topic");

    	//遍历数据库操作信息
        for (Entry entry : entrys) {  
        	//如果实在事务开头或者是事务结束则跳过循环
            if (entry.getEntryType() == EntryType.TRANSACTIONBEGIN || entry.getEntryType() == EntryType.TRANSACTIONEND) {  
                continue;  
            }
            CanalEntry.Header header = entry.getHeader();
            String tableName = header.getTableName().toLowerCase();
            if(!set.contains(tableName)){
                log.warn("当前库所订阅的表里不包含{}表",tableName);
                continue;
            }else {
                try {
            	//rowchange中有mysql变动的行数据
            	rowChange = RowChange.parseFrom(entry.getStoreValue());  
            } catch (Exception e) {  
                throw new RuntimeException("ERROR ## parser of eromanga-event has an error , data:" + entry.toString(),  
                        e);  
            }
            // eventType是mysql操作的四种类型 select,update，insert，delete
            EventType eventType = rowChange.getEventType(); 
            //kafka配置信息
            //打印mysql binlog信息
            if(eventType==EventType.INSERT || eventType==EventType.UPDATE){
            	//打印bin-log信息
            	System.out.println(String.format("================> binlog[%s:%s] , name[%s,%s] , eventType : %s",  
                        entry.getHeader().getLogfileName(), entry.getHeader().getLogfileOffset(),  
                        entry.getHeader().getSchemaName(), entry.getHeader().getTableName(),  
                        eventType)); 
            }
            //遍历数据库表数据变动信息，并将变动信息拼成json格式数据
            
            for (RowData rowData : rowChange.getRowDatasList()) {  
                	//如果是数据库insert操作
            	if (eventType == EventType.INSERT){ 
            		i++;
                	System.out.println("-------> insert");
                	//new一个list 调用printColum方法，接受方法的返回值
                	String insert=printColumn(rowData.getAfterColumnsList());
            		//拼最终json字符串，拼出来为{"new1":{"eventType":"INSERT","id":"21","update":"true"}}格式
            		String insertJson="{\""+entry.getHeader().getTableName()+"\":"+insert+",\"insert\":"+"\""+eventType+"\"}";
            		try {
                        producer.send(new ProducerRecord(topic,insertJson));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						System.out.println("kafka发送消息失败");
					}
            		//获取当前时间
            		//输出新增的json格式数据
            		System.out.println(df.format(new Date())+":"+insertJson);
            		System.out.println("一共检测到："+i+"条数据");
                	//如果是数据库update操作
                } else if(eventType == EventType.UPDATE){  
                	i++;
                	System.out.println("-------> update after"); 
                    //new一个list 调用printColum方法，接受方法的返回值
                    String updateAfter=printColumn(rowData.getAfterColumnsList()); 
                	//拼最终json
                	String updateAfterJson="{\""+entry.getHeader().getTableName()+"\":"+updateAfter+",\"update\":"+"\""+eventType+"\"}";
                	try {
                        producer.send(new ProducerRecord(topic,updateAfterJson));
					} catch (Exception e) {
						e.printStackTrace();
						// TODO Auto-generated catch block
						System.out.println("kafka发送消息失败");
					}
                	//输出更新后的json格式数据
                	System.out.println(df.format(new Date())+":"+updateAfterJson);
                	System.out.println("一共检测到："+i+"条数据");
                    
                }  
            }
            }

        }  
    }  
  /**
   * @return  List<String>
   */
  private static String printColumn( List<Column> columns) { 
    	JSONObject json=new JSONObject();
		for (Column column : columns) { 
			//每一行数据转json格式
			json.put(column.getName(), column.getValue());
        }
		//System.out.println(json.toJSONString());
		return json.toJSONString();
    }  
}  