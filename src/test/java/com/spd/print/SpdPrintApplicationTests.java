package com.spd.print;

import com.alibaba.excel.EasyExcel;
import com.spd.pojo.DemoData;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SpringBootTest
class SpdPrintApplicationTests {

    @Test
    void contextLoads() {
        String str = "IFACE_ID\tOPERATING_UNIT\tINVOICE_TYPE\tVENDOR_NUMBER\tVENDOR_NAME\tVENDOR_SITE_CODE\tINVOICE_DATE\tGL_DATE\tINVOICE_NUM\tSUPPLIER_TAX_INVOICE_NUMBER\tATTACHMENT_NUM\tINVOICE_AMOUNT\tACTION_TYPE\tOPERATING_BY\tSYNC_DATE\tPROCESS_FLAG\tPROCESS_DATE\tERROR_MESSAGE\tATTRIBUTE1\tATTRIBUTE2\tATTRIBUTE3\tATTRIBUTE4\tATTRIBUTE5\tATTRIBUTE6\tATTRIBUTE7\tATTRIBUTE8\tATTRIBUTE9\tATTRIBUTE10\tATTRIBUTE11\tATTRIBUTE12\tATTRIBUTE13\tATTRIBUTE14\tATTRIBUTE15\tSPD_CREATE_TIME\tSPD_CREATE_MAN\tSPD_STATE\tSPD_SEND_TIME\tSPD_SEND_NAME";
        System.out.println(str.replace("\t",","));
    }


    @Test
    public void simpleWrite() {
        // 注意 simpleWrite在数据量不大的情况下可以使用（5000以内，具体也要看实际情况），数据量大参照 重复多次写入

        // 写法1 JDK8+
        // since: 3.0.0-beta1
        // 准备数据
        List<DemoData> userList = new ArrayList<>();
        userList.add(new DemoData("1", new Date(), 25.00,"12"));
        userList.add(new DemoData("2", new Date(), 26.00,"123"));
        userList.add(new DemoData("3", new Date(), 27.00,"124"));

        String fileName = "品种" + "simpleWrite" + System.currentTimeMillis() + ".xlsx";
        // 这里 需要指定写用哪个class去写，然后写到第一个sheet，名字为模板 然后文件流会自动关闭
        // 如果这里想使用03 则 传入excelType参数即可
        EasyExcel.write(fileName, DemoData.class)
                .sheet("模板")
                .doWrite(userList);
    }

}
