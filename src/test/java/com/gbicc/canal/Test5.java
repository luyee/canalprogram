package com.gbicc.canal;

import com.gbicc.util.DateUtils;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;

/**
 * Created by root on 2017/4/19.
 */
public class Test5 {

    @Test
    public void b2() {
        String s = "e110_test1_20170419164828.txt.1492681491771.txt";
        System.out.println(s.split(".txt")[0].split("_")[1]);
    }

    @Test
    public void aa() {
        Date date = DateUtils.stringToDate("2017-04-20 16:25:00", DateUtils.DATE_TO_STRING_DETAIAL_PATTERN);
        System.out.println(date.getTime());
    }

    @Test
    public void b1() {
        System.out.println(DateUtils.DateToString(new Date(1492676700000L), DateUtils.DATE_TO_STRING_DETAIAL_PATTERN));
    }

    @Test
    public void a1() {
        String s = "als.user_info,als.impower_to_user,als.customer_belong,als.customer_contacts,als.customer_special,als.customer_info,als.customer_relative,als.customer_partner,als.ent_info,als.ind_info,als.insure_loan_paymen_plan,als.insured_list,als.insure_special_agreement,als.insure_payment_plan,als.flow_record,als.insuretype,als.insure_message,als.business_type,als.business_typeset,als.policy_request,als.assetlist,als.guaranty_info,als.business_apply,als.business_contract,als.org_info,als.acct_payment_log,als.guaranty_contract,als.acct_trans_payment,als.loan_info,als.insuranceman_info,als.insured_info,als.project_info,als.cms_collateraltype_info,als.insure_info,als.acct_payment_schedule,als.acct_loan,als.acct_loan_change,als.acct_ahead,als.user_list,als.business_return,als.fund_strench_people,als.fee_infomation,als.customer_creditmmm\n";
        Arrays.asList(s.split(","))
                .forEach(f -> System.out.println(f));
    }
}
