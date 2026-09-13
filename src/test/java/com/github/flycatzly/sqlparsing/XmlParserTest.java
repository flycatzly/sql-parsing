package com.github.flycatzly.sqlparsing;

import jdk.nashorn.internal.ir.annotations.Ignore;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.junit.Test;

public class XmlParserTest{
    @Ignore
    @Test
    public void parseXml2SqlNode() {
        Document document = null;

        String text="<root>select\n" +
                "\ta.user_id,\n" +
                "\ta.person_id,\n" +
                "\ta.person_name,\n" +
                "\ta.sex,\n" +
                "    a.age,\n" +
                "\ta.date_birth,\n" +
                "\ta.doctor_id,\n" +
                "\ta.doctor_name,\n" +
                "\ta.group_num,\n" +
                "\ta.mobile_no,\n" +
                "\ta.bind_time,\n" +
                "\ta.case_summary,\n" +
                "    a.instrest_status,\n" +
                "    a.is_add_doctor is_bind_doctor,\n" +
                "    group_concat(b.tag_value) as tag_value,\n" +
                "    a.task_type,\n" +
                "    a.task_status,\n" +
                "    a.after_diagnosis\n" +
                "from\n" +
                "\tapi_doctor_dialog_user a\n" +
                "    left join ads_user_disease_interface b on a.person_id=b.patient_id and b.status in ('INSERT', 'NOT_CHANGE')\n" +
                "where coalesce(a.invalid_scene,'') not in ('30','20','40')\n" +
                "and is_bind_doctor=1\n" +
                "<if test = 'doctor_list != null'>\n" +
                "    and a.doctor_id in \n" +
                "    <foreach collection=\"doctor_list\" open=\"(\" close=\")\" separator=\",\" item=\"type\">\n" +
                "        #{type}\n" +
                "    </foreach>\n" +
                "</if>\n" +
                "<if test=\"user_id != null\">\n" +
                "    and a.user_id in\n" +
                "    <foreach collection=\"user_id\" open=\"(\" close=\")\" separator=\",\" item=\"item\">\n" +
                "        #{item}\n" +
                "    </foreach>\n" +
                "</if>\n" +
                "<if test=\"person_name != null and person_name != ''\">\n" +
                "    and a.person_name like concat('%', #{person_name}, '%')\n" +
                "</if>\n" +
                "<if test=\"mobile_no != null and mobile_no != ''\">\n" +
                "    and a.mobile_no =#{mobile_no}\n" +
                "</if>\n" +
                "<if test=\"expire_start_time!=null and expire_start_time != ''\">\n" +
                "    and a.expire_time>=#{expire_start_time}\n" +
                "</if>\n" +
                "<if test=\"expire_end_time!=null and expire_end_time != ''\">\n" +
                "    <![CDATA[ and a.expire_time<=#{expire_end_time} ]]>\n" +
                "</if>\n" +
                "<if test=\"birth_begin!=null and birth_begin != ''\">\n" +
                "    and a.date_birth >=#{birth_begin}\n" +
                "</if>\n" +
                "<if test=\"birth_end!=null and birth_end != ''\">\n" +
                "    <![CDATA[ and a.date_birth<=#{birth_end} ]]>\n" +
                "</if>\n" +
                "<if test = 'instrest_status != null'>\n" +
                "    and a.instrest_status in \n" +
                "    <foreach collection=\"instrest_status\" open=\"(\" close=\")\" separator=\",\" item=\"item\">\n" +
                "        #{item}\n" +
                "    </foreach>\n" +
                "</if>\n" +
                "<if test = 'tag_value != null'>\n" +
                "    and b.tag_value in \n" +
                "    <foreach collection=\"tag_value\" open=\"(\" close=\")\" separator=\",\" item=\"item\">\n" +
                "        #{item}\n" +
                "    </foreach>\n" +
                "</if>\n" +
                "<if test=\"is_add_doctor !=null\">\n" +
                "    and a.is_add_doctor = #{is_add_doctor}\n" +
                "</if>\n" +
                "group by 1\n" +
                "order by a.expire_time desc</root>";

        try {
            document = DocumentHelper.parseText(text);
        } catch (DocumentException e) {
            throw new RuntimeException(e.getMessage());
        }
        System.out.println(document);
    }

}
