package com.gbicc.kafka.api090;

import java.util.ResourceBundle;

/**
 * Created by root on 2017/4/11.
 */
public class CanalKafkaMain {
    public static ResourceBundle bundle = ResourceBundle.getBundle("config");

    public static void main(String[] args) {
        String topic = bundle.getString("topic");
        String canalURL1 = bundle.getString("canalURL");
        int port1 = Integer.parseInt(bundle.getString("canalPort").trim());
        String destination1 = bundle.getString("destination");
        String filter1 = "crmsdb.crms_abs_finance_detail,crmsdb.crms_busi_apply,crmsdb.crms_abs_finance_plan,crmsdb.crms_abs_finance_repayment_plan,crmsdb.crms_abs_finance_repayment";
        //-------     第二个canal的信息      -------------------
        String canalURL2 = bundle.getString("canalURL2");
        int port2 = Integer.parseInt(bundle.getString("canalPort2").trim());
        String destination2 = bundle.getString("destination2");
        String filter2 = "cgidb.prpcmain,cgidb.prpcmainloan,cgidb.prplcompensate,cgidb.prplloss";


        CanalKafka c1 = new CanalKafka(canalURL1, port1, destination1, filter1, topic);
        CanalKafka c2 = new CanalKafka(canalURL2, port2, destination2, filter2, topic);
        c1.run();
        c2.run();

    }
}
