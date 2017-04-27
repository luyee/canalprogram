package com.gbicc.canal;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by root on 2017/4/19.
 */
public class Test5 {
    @Test
    public void b8(){
        Set<String> set=new HashSet<>();
        String destination="cgidb.PrpLrelatePerson,cgidb.PrpLscheduleNew,cgidb.PrpLscheduleMain,cgidb.PrpLscheduleItem,cgidb.PrpLscheduleObject,cgidb.PrpLcheckItem,cgidb.PrpLcheck,cgidb.PrpLcheckExt,cgidb.PrpLcheckLoss,cgidb.PrpLclaimfee,cgidb.PrpLthirdparty,cgidb.PrpLthirdCarLoss,cgidb.PrpLthirdProp,cgidb.PrpLdoc,cgidb.PrpLcertifyCollect,cgidb.PrpLcertifyImg,cgidb.PrpLassure,cgidb.PrpLassureDetail,cgidb.PrpLverifyLoss,cgidb.PrpLprop,cgidb.PrpLarrearage,cgidb.PrpLclaimagent,cgidb.PrpLcaseno,cgidb.PrpLendor,cgidb.PrpLpersonloss,cgidb.PrpLmedicine,cgidb.PrpLafterward,cgidb.PrpLreclaim,cgidb.PrpLreplevy,cgidb.PrpLreplevyDetail,cgidb.PrpLCMain,cgidb.PrpLCItemCar,cgidb.PrpLCitemKind,cgidb.PrpLclaimApprov,cgidb.PrpLinvestigate,cgidb.PrpLbackVisit,cgidb.PrpLbackVisitQue,cgidb.PrpLbackVisitText,cgidb.prplrecoverymain,cgidb.prplrecprojectdetail,cgidb.prplregisttext,cgidb.prplreport".toLowerCase();
        for (String s : destination.split(",")) {
            set.add(s.split("\\.")[1]);
        }
//        set.forEach(s-> System.out.println(s));
        String tableName="PrpLrelatePerson".toLowerCase();
        if(set.contains(tableName)){
            System.out.println(tableName);
        }
    }

    @Test
    public void bb(){
        String a="B";
        switch (a){
            default:
                System.out.println("default");
            case "B":
                System.out.println("B");
                break;
            case "A":
                System.out.println("A");
                break;
        }
    }

}
