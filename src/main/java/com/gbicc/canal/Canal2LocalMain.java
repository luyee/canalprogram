package com.gbicc.canal;

import com.gbicc.kafka.api090.CanalKafkaMain;

import java.util.ResourceBundle;

/**
 * Created by root on 2017/4/12.
 */
public class Canal2LocalMain {
    public static void main(String[] args) {
        ResourceBundle bundle = CanalKafkaMain.bundle;
        String canalURL1 = bundle.getString("canalURL");
        int port1 = Integer.parseInt(bundle.getString("canalPort").trim());
        String destination1 = bundle.getString("destination");
        String filter1 = "crmsdb.crms_abs_finance_detail,crmsdb.crms_busi_apply,crmsdb.crms_abs_finance_plan,crmsdb.crms_abs_finance_repayment_plan,crmsdb.crms_abs_finance_repayment";
        //-------     第二个canal的信息      -------------------
        String canalURL2 = bundle.getString("canalURL2");
        int port2 = Integer.parseInt(bundle.getString("canalPort2").trim());
        String destination2 = bundle.getString("destination2");
        String filter2 = "cgidb.prpcmain,cgidb.prpcmainloan,cgidb.prplcompensate,cgidb.prplloss";


        Canal2Local c1 = new Canal2Local(canalURL1, port1, destination1, filter1);
        c1.run();
        Canal2Local c2 = new Canal2Local(canalURL2, port2, destination2, filter2);
        c2.run();
    }
}
