package com.github.flycatzly.sqlparsing;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.github.flycatzly.sqlparsing.engine.DynamicSqlEngine;
import com.github.flycatzly.sqlparsing.engine.SqlEngineUtil;
import com.github.flycatzly.sqlparsing.engine.SqlMeta;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.Test;

import java.util.*;


public class SqlParsingTest {

    @Ignore
    @Test
    public void test() {
        StringBuilder builder = new StringBuilder();
        String a = null;
        builder.append("abc").append(a).append("333");
        System.out.println(builder.toString());
    }

    @Ignore
    @Test
    public void testIf() {
        String sql = "id &lt;= #{maxId}";
        Map<String, Object> sqlParam = new HashMap<>();
        sqlParam.put("maxId", 10);

        SqlMeta sqlMeta =  SqlEngineUtil.getEngine().parse(sql, sqlParam);
        System.out.println(sqlMeta.getSql());
        sqlMeta.getJdbcParamValues().forEach(System.out::println);

    }

    @Ignore
    @Test
    public void testTrim() {
/*        DynamicSqlEngine engine = new DynamicSqlEngine();
        String sql = "<trim prefix='(' suffix=')' suffixesToOverride=',' prefixesToOverride='and' ><foreach collection='list' index='idx' open='(' separator=',' close=')'>#{item.name}== #{idx}</foreach><if test='id!=null'>  and xyz.,</if></trim>";
        Map<String, Object> map = new HashMap<>();
        map.put("id", 2);
        ArrayList<User> arrayList = new ArrayList<>();
        arrayList.add(new User(10, "tom"));
        arrayList.add(new User(11, "jerry"));
        map.put("list", arrayList);

        SqlMeta sqlMeta = engine.parse(sql, map);
        System.out.println(sqlMeta.getSql());
        sqlMeta.getJdbcParamValues().forEach(System.out::println);*/
    }

    @Ignore
    @Test
    public void testWhere() {
/*        DynamicSqlEngine engine = new DynamicSqlEngine();
        String sql = "<where><if test='id!=null'>  and id = #{id}</if><if test='id!=null'>  and id = #{id}</if><if test=\"ids!=null and ids!=''\">  and ids = #{id}</if></where>";
        Map<String, Object> map = new HashMap<>();
        map.put("id", 2);
        map.put("ids", 41);
        ArrayList<User> arrayList = new ArrayList<>();
        arrayList.add(new User(10, "tom"));
        arrayList.add(new User(11, "jerry"));
        arrayList.add(new User(12, "jerry111"));
        map.put("list", arrayList);

        SqlMeta sqlMeta = engine.parse(sql, map);
        System.out.println(sqlMeta.getSql());
        sqlMeta.getJdbcParamValues().forEach(System.out::println);*/
    }

   // @Ignore
    @Test
    public void testMultiForeach() {
        DynamicSqlEngine engine = new DynamicSqlEngine();
        String sql = ("<foreach collection='list' open='(' separator=',' close=')'>#{item}</foreach><foreach collection='list2' open='{' separator=',' close='}'>#{item}</foreach>");
        Map<String, Object> map = new HashMap<>();

        ArrayList<String> list = new ArrayList<String>() {{
            add("a");
            add("b");
        }};

        map.put("list", list);

        ArrayList<String> list2 = new ArrayList<String>() {{
            add("c");
            add("d");
        }};

        map.put("list2", list2.toArray());

        SqlMeta sqlMeta = engine.parse(sql, map);
        System.out.println(sqlMeta.getSql());
        sqlMeta.getJdbcParamValues().forEach(System.out::println);
    }
    @Ignore
    @Test
    public void testMultiForeach2() {
    StringBuilder script  = new StringBuilder(" select data_date,doctor_id,add_friend_num from  ads.ads_doctor_rhythm_d ");
        script.append(" <where>")
                .append("<if test=\"data_date!=null and data_date != ''\">")
                .append(" and data_date =${data_date}</if>")
                .append(" <if test=\"doctor_id!=null\"> ")
                .append(" and doctor_id in ")
                .append(" <foreach collection=\"doctor_id\" open=\"(\" close=\")\" separator=\",\" item=\"type\">")
                .append(" #{type} ")
                .append(" </foreach></if> ")
                .append(" </where> order by doctor_id desc");
        DynamicSqlEngine engine = new DynamicSqlEngine();
        Map<String,Object> params = new HashMap<>();
        params.put("doctor_id",Arrays.asList("210000183470146","210000003030151","210000227920110"));
        params.put("data_date","2021-07-21");
        SqlMeta sqlMeta = engine.parse(script.toString(), params);
        System.out.println(sqlMeta.getSql());
        System.out.println(sqlMeta.getJdbcParamValues());
    }

    /**
     * update SET id = ? ,  age = ?
     * [10, 30]
     */
    //@Ignore
    @Test
    public void testSet() {
        DynamicSqlEngine engine = new DynamicSqlEngine();
        String sql = ("update<set><if test='id !=null'> id = #{id} ,</if><if test='age !=null'> age = #{age} </if></set>");
        Map<String, Object> map = new HashMap<>();
        map.put("id",10);
        map.put("age",30);
        SqlMeta sqlMeta = engine.parse(sql, map);
        System.out.println(sqlMeta.getSql());
        System.out.println(sqlMeta.getJdbcParamValues());
        //sqlMeta.getJdbcParamValues().forEach(System.out::println);

    }

    /**
     * update tab_a  SET name = ?    age = ?  WHERE id=?   age = ?    name = ?
     * [zly, 20, 10, 20, zly]
     */
    @Test
    public void testUpdate() {
        DynamicSqlEngine engine = new DynamicSqlEngine();
        String sql = ("update tab_a <set><if test='name !=null'> name = #{name} </if><if test='age !=null'>  age = #{age}  </if></set> <where> id=#{id}<if test='age !=null'>  age = #{age} </if><if test='name !=null'>  name = #{name} </if></where>");
        Map<String, Object> map = new HashMap<>();
        map.put("id",10);
        map.put("name","zly");
        map.put("age",20);
        SqlMeta sqlMeta = engine.parse(sql, map);
        System.out.println(sqlMeta.getSql());
        System.out.println(sqlMeta.getJdbcParamValues());

        //sqlMeta.getJdbcParamValues().forEach(System.out::println);

    }

    /**
     * update tab_a  SET name = ? ,id = ? where id=?
     * [zly, 10, 10]
     */
    @Test
    public void testUpdate1() {
        DynamicSqlEngine engine = new DynamicSqlEngine();
        String sql = ("update tab_a <set> name = #{name} ,id = #{id} </set> where id=#{id}");
        Map<String, Object> map = new HashMap<>();
        map.put("id",10);
        map.put("name","zly");
        map.put("age",20);
        SqlMeta sqlMeta = engine.parse(sql, map);
        System.out.println(sqlMeta.getSql());
        System.out.println(sqlMeta.getJdbcParamValues());
        //sqlMeta.getJdbcParamValues().forEach(System.out::println);

    }

    @Ignore
    @Test
    public void testParseParam() {
        DynamicSqlEngine engine = new DynamicSqlEngine();
        String sql = ("<foreach collection='list' open='(' separator=',' close=')'>#{item.name} #{item} #{id} ${indexName} </foreach><where><if test='id!=null'>  and id = #{mid}</if> ${name}</where>");
        Set<String> set = engine.parseParameter(sql);
        set.stream().forEach(System.out::println);
    }
    @Ignore
    @Test
    public void testParseParam1() {
        DynamicSqlEngine engine = new DynamicSqlEngine();
        StringBuilder sql  = new StringBuilder(" SELECT ")
                .append("      operator_no,")
                .append("      patient_id,")
                .append("      tag_key,")
                .append("      tag_value,")
                .append("      created_by,")
                .append("      date_format(start_time,'%Y-%m-%d %h:%i:%s') as start_time")
                .append("    FROM api_patient_base_record_info")
                .append("    WHERE tag_status='usable' ")
                .append("    AND relation_type= 'RELA_MYSELF'")
                .append("    <if test=\"operator_no != null\">")
                .append("        AND operator_no IN ")
                .append("        <foreach collection=\"operator_no\" open=\"(\" close=\")\" separator=\",\" item=\"item\">")
                .append("            #{item}")
                .append("        </foreach>")
                .append("    </if>")
                .append("    <if test=\"tag_key != null and tag_key != ''\">")
                .append("     AND tag_key= #{tag_key}")
                .append("    </if>")
                .append("    <choose>")
                .append("<when test=\"is_new\"> ")
                .append("        AND tag_flag ='Y'")
                .append("      </when>")
                .append("      <otherwise>")
                .append("        AND tag_flag ='N'")
                .append("      </otherwise>")
                .append("    </choose>")
                .append("    ORDER BY start_time DESC");
        Map<String,Object> params = new HashMap<>();
        List<Long> list =new ArrayList<Long>();
        list.add(210001908920111L);
        params.put("operator_no", list);
        params.put("tag_key", "VipUser");
        params.put("is_new",true);
        params.put("pageSize",10);
        params.put("pageNum",1);

        SqlMeta sqlMeta = engine.parse(sql.toString(), params);
        System.out.println(SQLUtils.format(sqlMeta.getSql(), DbType.mysql));
        System.out.println(sqlMeta.getJdbcParamValues());
    }
    //@Ignore
    @Test
    public void testParseParam2() {
        DynamicSqlEngine engine = new DynamicSqlEngine();
        StringBuilder sql  = new StringBuilder(" SELECT ")
                .append("        a.doctor_id,")
                .append("        a.user_id,")
                .append("        a.patient_id,")
                .append("        b.person_name AS patient_name,")
                .append("        a.task_type,")
                .append("        a.task_name,")
                .append("        a.task_status,")
                .append("        a.task_sub_type,")
                .append("        a.plan_task_num,")
                .append("        a.task_order_no,")
                .append("        a.task_level,")
                .append("        a.task_resume,")
                .append("        a.task_toast,")
                .append("        a.task_content_json,")
                .append("        a.plan_no,")
                .append("        a.plan_name,")
                .append("        a.task_alarm_type,")
                .append("        a.task_alarm_executor,")
                .append("        a.task_set_doctor,")
                .append("        a.task_execute_time,")
                .append("        a.task_start_time,")
                .append("        a.task_end_time,")
                .append("        a.task_effectiveness")
                .append("    FROM api_crm_user_plan a LEFT JOIN api_doctor_dialog_user b ON a.doctor_id =b.doctor_id ")
                .append("    AND a.user_id= b.user_id AND a.patient_id =b.person_id")
                .append("    <WHERE>")
                .append("        <IF test=\"task_type!=null and task_type != ''\">")
                .append("            AND a.task_type=#{task_type}")
                .append("        </IF>")
                .append("        <IF test=\"doctor_id!=null and doctor_id != ''\">")
                .append("            AND a.doctor_id=#{doctor_id}")
                .append("        </IF>")
                .append("        <IF test=\"user_id != null\">")
                .append("            AND a.user_id IN")
                .append("            <foreach collection=\"user_id\" OPEN=\"(\" CLOSE=\")\" SEPARATOR=\",\" item=\"item\">")
                .append("                #{item} ")
                .append("            </foreach>")
                .append("        </IF>")
                .append("        <IF test=\"patient_name!=null and patient_name != ''\">")
                .append("            AND b.person_name LIKE CONCAT('%',CONCAT(#{patient_name},'%'))")
                .append("        </IF>")
                .append("        <IF test=\"task_status!=null and task_status != ''\">")
                .append("            AND a.task_status=#{task_status}")
                .append("        </IF>")
                .append("        <IF test=\"date_start!=null and date_start != ''\">")
                .append("            AND a.task_execute_time>=#{date_start}")
                .append("        </IF>")
                .append("        <IF test=\"date_end!=null and date_end != ''\">")
                .append("            <![CDATA[ AND a.task_execute_time<=#{date_end} ]]>")
                .append("        </IF>")
                .append("        <IF test=\"task_sub_type!=null and task_sub_type != ''\">")
                .append("            AND a.task_sub_type=#{task_sub_type}")
                .append("        </IF>")
                .append("        <IF test=\"task_level!=null and task_level != ''\">")
                .append("            AND a.task_level=#{task_level}")
                .append("        </IF>")
                .append("        <IF test=\"task_plan_name!=null and task_plan_name != ''\">")
                .append("            AND a.plan_name LIKE CONCAT('%',CONCAT(#{task_plan_name},'%'))")
                .append("        </IF>")
                .append("        <IF test=\"task_alarm_type!=null and task_alarm_type != ''\">")
                .append("            AND a.task_alarm_type =#{task_alarm_type}")
                .append("        </IF>")
                .append("        <IF test=\"keywords!=null and keywords != ''\">")
                .append("            AND a.task_content_json LIKE CONCAT('%',CONCAT(#{keywords},'%'))")
                .append("        </IF>")
                .append("    </WHERE>")
                .append("    ORDER BY a.task_create_time DESC");
        Map<String,Object> params = new HashMap<>();
        params.put("doctor_id", "210000324550118");
        params.put("date_start", "2022-01-17");
        params.put("date_end", "2022-01-17");
        params.put("task_type", "NOTICE_TASK");
        params.put("task_sub_type", "NOTICE_TASK");
        params.put("task_alarm_type", "ASSISTANT");
        params.put("pageSize",10);
        params.put("pageNum",1);

        SqlMeta sqlMeta = engine.parse(sql.toString(), params);
        System.out.println(SQLUtils.format(sqlMeta.getSql(), DbType.mysql));
        System.out.println(sqlMeta.getJdbcParamValues());
    }

    @Ignore
    @Test
    public void test1(){
        StringBuilder script  = new StringBuilder("select a1.service_type,")
                .append("    case when a1.service_type = 'A001' THEN '家庭医生'")
                .append("    when a1.service_type = 'E001' THEN '送药到家'")
                .append("    when a1.service_type = 'B006' THEN '专家问诊'")
                .append("    when a1.service_type = 'A003' THEN '视频问诊'")
                .append("    when a1.service_type = 'B002' THEN '专家门诊预约服务' end as service_name,")
                .append("    round(a1.average_number_group_used,1) as average_number_group_used,")
                .append("    round(95 + a2.family_use_times /a1.family_total * 5,1) as percentage_used_groups,")
                .append("    trunc((a1.average_number_group_used+1) * a1.market_price) as average_savings,")
                .append("        ((a1.average_number_group_used+1) * a1.save_time_unit) as average_save_time,")
                .append("    a1.market_price,")
                .append("    a1.save_time_unit,")
                .append("    a1.month")
                .append("    from(")
                .append("            select t1.service_type,month,")
                .append("        (case")
                .append("            when service_type = 'A001' then 50")
                .append("                    when service_type = 'B002' then 800")
                .append("                    when service_type = 'B006' then 2000")
                .append("                    when service_type = 'A003' then 100")
                .append("                    else 0")
                .append("                    end) as market_price,")
                .append("        (case")
                .append("    when service_type = 'B006' then 300")
                .append("    when service_type = 'B002' then 300")
                .append("    when service_type = 'E001' then 120")
                .append("            else 0")
                .append("    end) as save_time_unit,")
                .append("    cast(t1.month_service_times as numeric) as month_service_times,")
                .append("               (select count(distinct group_id) as family_total from ods.active_center_ac_active_family_doctor where status in('04','05','06','07')) as family_total,")
                .append("    round(cast(t1.month_service_times as numeric) /cast(")
                .append("              (select count(distinct group_id) as family_total from ods.active_center_ac_active_family_doctor where status in('04','05','06','07')) as numeric),1) as average_number_group_used  ")
                .append("    from (")
                .append("            select max(to_char(date(confirm_date),'yyyymm')) as month,")
                .append("    left(interests_code, 4) as service_type,")
                .append("    count(1) as month_service_times")
                .append("    from ods.equity_center_ec_user_interests_use")
                .append("    where use_status = 3")
                .append("    and to_char(date(confirm_date),'yyyymm')= #{month}")
                .append("    group by left(interests_code, 4)")
                .append("        )t1 where 1=1 and t1.service_type in ('A001','E001','B006','A003','B002')")
                .append("    <if test = 'service_type != null'>")
                .append("    and service_type in")
                .append("     <foreach collection=\"service_type\" open=\"(\" close=\")\" separator=\",\" item=\"type\">     ")
                .append("    #{type}")
                .append("        </foreach>")
                .append("    </if>")
                .append("            )a1 left join (")
                .append("            select distinct service_type,cast(count(family_no) as numeric) as family_use_times")
                .append("    from (")
                .append("            select distinct group_id as family_no,left(interests_code, 4) as service_type")
                .append("    from ods.equity_center_ec_user_interests_use")
                .append("    where use_status = 3 and group_id is not null")
                .append("    and to_char(date(confirm_date),'yyyymm')=#{month}")
                .append("    union all")
                .append("    select c.family_no,a.service_type")
                .append("    from (")
                .append("                    select user_id,left(interests_code, 4) as service_type")
                .append("    from ods.equity_center_ec_user_interests_use")
                .append("    where use_status = 3 and group_id is null")
                .append("    and to_char(date(confirm_date),'yyyymm')=#{month}")
                .append("                )a left join ods.cif_patient_product_t_info_sufferer_rela_operation b")
                .append("    on a.user_id = b.operator_id")
                .append("    and b.usable_flag = 'USABLE'")
                .append("    and b.rela_type = 'RELA_MYSELF'")
                .append("    left join ods.cif_patient_product_t_info_family_member c")
                .append("    on b.patient_id = c.patient_no and c.usable_flag = 'USABLE'")
                .append("            )tt group by service_type")
                .append(")a2 on a1.service_type=a2.service_type");
        DynamicSqlEngine engine = new DynamicSqlEngine();
        Map<String,Object> params = new HashMap<>();
        params.put("service_type", Arrays.asList("A001","B001"));
        params.put("month","202106");

        SqlMeta sqlMeta = engine.parse(script.toString(), params);
        System.out.println(SQLUtils.format(sqlMeta.getSql(), DbType.mysql));
        System.out.println(sqlMeta.getJdbcParamValues());

    }


    /***
     * <![CDATA[ rn<4 ]]> 支持
     */
   @Ignore
    @Test
    public void testVar4() {
        StringBuilder script  = new StringBuilder(" select t1.patient_id, ")
                .append("        t1.last_consultation_time,")
                .append("        t1.last_three_diagnoses, ")
                .append("        t2.diagnosis")
                .append(" from (")
                .append("        select patient_id,")
                .append("                max(consult_time) as last_consultation_time,")
                .append("                string_agg(distinct consult_name,'、') as last_three_diagnoses")
                .append("        from(")
                .append("                select consult_order_id,max(consult_time) as consult_time,patient_id,string_agg(consult_name,',') as consult_name,")
                .append("                row_number() over (order by max(consult_time) desc,consult_order_id) rn ")
                .append("                from  ads.ads_consult_record_diagnosis")
                .append("                where patient_id=#{patient_id}")
                .append("                group by consult_order_id,patient_id")
                .append("        )a where <![CDATA[ rn<4 ]]> ")
                .append("        group by patient_id")
                .append(" )t1 left join (")
                .append("    select patient_id,")
                .append("    jsonb_agg(jsonb_build_object('orgJson',diagnosis,'name',consult_name,'consult_num',consult_num)) as diagnosis")
                .append("    from (")
                .append("        select patient_id,consult_name,diagnosis,consult_num,")
                .append("            row_number() over (order by consult_num desc) rn ")
                .append("            from ads.ads_consult_record_diagnosis")
                .append("            where patient_id=#{patient_id}")
                .append("    )a where <![CDATA[ rn<4 ]]> ")
                .append("    group by patient_id ")
                .append(" )t2 on t1.patient_id =t2.patient_id ");


        Map<String,Object> params = new HashMap<>();
        params.put("patient_id","260000217650018");
        DynamicSqlEngine engine = new DynamicSqlEngine();
        SqlMeta sqlMeta = engine.parse(script.toString(), params);
        System.out.println(SQLUtils.format(sqlMeta.getSql(), DbType.mysql));
        System.out.println(sqlMeta.getJdbcParamValues());

    }
    @Ignore
    @Test
    public void testVar5() {
        StringBuilder script  = new StringBuilder(" select to_char(date(create_time), 'yyyy.mm.dd')|| ' 创建' as data_date")
                .append("     from ads.ads_agent_enjoy_group ")
                .append("     where agent_no is not null")
                .append("     <if test=\"agent_no != null and agent_no != ''\">")
                .append("         and agent_no= #{agent_no}")
                .append("     </if>")
                .append("     group by agent_no,date(create_time)")
                .append("     order by date(create_time)")
                .append("     <choose>")
                .append("             <when test=\"orderby\">")
                .append("                     asc")
                .append("             </when>")
                .append("             <otherwise>")
                .append("             desc")
                .append("             </otherwise>")
                .append("     </choose>");
        Map<String,Object> params = new HashMap<>();
        params.put("orderby",true);
        params.put("agent_no","HF-001");
        ///BoundSql boundSql =new BoundSql(script.toString(), params, null);
        ///log.info("\ntestVar:{},\nbuildParams：{},\nparams:{},\nsql:{}",script, JsonUtils.toJsonString(boundSql),boundSql.getParameters(),boundSql.getSql());

        DynamicSqlEngine engine = new DynamicSqlEngine();
        SqlMeta sqlMeta = engine.parse(script.toString(), params);
        System.out.println(SQLUtils.format(sqlMeta.getSql(), DbType.mysql));
        System.out.println(sqlMeta.getJdbcParamValues());
       // sqlMeta.getJdbcParamValues().forEach(System.out::println);
    }

    @Ignore
    @Test
    public void test01(){
        String str ="        SELECT\n" +
                "          ID, AREA_CODE, AREA_NAME, BRANCH_CODE, BRANCH_NAME\n" +
                "        FROM ACW.T_OMR_5KM_AREA_BRANCH\n" +
                "        <where>\n" +
                "            <if test=\"id != null and id != ''\">\n" +
                "                AND ID = #{id}\n" +
                "            </if>\n" +
                "            <if test=\"areaCode != null and areaCode != ''\">\n" +
                "                AND AREA_CODE = #{areaCode}\n" +
                "            </if>\n" +
                "            <if test=\"areaName != null and areaName != ''\">\n" +
                "                AND AREA_NAME = #{areaName}\n" +
                "            </if>\n" +
                "            <if test=\"branchCode != null and branchCode != ''\">\n" +
                "                AND BRANCH_CODE = #{branchCode}\n" +
                "            </if>\n" +
                "            <if test=\"branchName != null and branchName != ''\">\n" +
                "                AND BRANCH_NAME = #{branchName}\n" +
                "            </if>\n" +
                "        </where>";
        Map<String,Object> params = new HashMap<>();
        params.put("areaName","");
        params.put("branchName","HF-001");
        ///BoundSql boundSql =new BoundSql(script.toString(), params, null);
        ///log.info("\ntestVar:{},\nbuildParams：{},\nparams:{},\nsql:{}",script, JsonUtils.toJsonString(boundSql),boundSql.getParameters(),boundSql.getSql());

        DynamicSqlEngine engine = new DynamicSqlEngine();
        SqlMeta sqlMeta = engine.parse(str, params);
        System.out.println(SQLUtils.format(sqlMeta.getSql(), DbType.mysql));
        System.out.println(sqlMeta.getJdbcParamValues());
    }

  /*  @Ignore
    @Test
    public void test02(){
        String sql="<if test=\"getSubordinatesDept != null and getSubordinatesDept != ''\">\n" +
                "        WITH DATA2 AS(\n" +
                "            SELECT DISTINCT A.LEVEL_FOUR_CODE, A.LEVEL_FOUR_NAME\n" +
                "            FROM ACW.T_OMR_EMP_ORG A\n" +
                "            WHERE 1=1\n" +
                "            <if test=\"getSubordinatesDept =='yyq'\">\n" +
                "                AND A.LEVEL_THREE_CODE=#{getSubordinatesDeptCode}\n" +
                "            </if>\n" +
                "            <if test=\"getSubordinatesDept =='dq'\">\n" +
                "                AND A.LEVEL_TWO_CODE=#{getSubordinatesDeptCode}\n" +
                "            </if>\n" +
                "            <if test=\"getSubordinatesDept =='syb'\">\n" +
                "                AND A.LEVEL_ONE_CODE=#{getSubordinatesDeptCode}\n" +
                "            </if>\n" +
                "        )\n" +
                "        </if>\n" +
                "        SELECT /*+INDEX(C IDX01_CUSTCODE)*/ /* A.FOFFERTEL,\n" +
   /*             "               A.FCUSTNUMBER,\n" +
                "               A.FCUSTNAME,\n" +
                "               A.FCREATETIME,\n" +
                "               DECODE(A.FCLUESTAGE,NULL,'','1','线索阶段','2','机会阶段','3','合同阶段','4','走货阶段','') AS FCLUESTAGE,\n" +
                "               A.FDELIVERYPOTENTIAL,\n" +
                "               A.FVOLUME_UPPER_LIMIT,\n" +
                "               A.FVOLUME_LOWER_LIMIT,\n" +
                "               A.FVISIT_TIME,\n" +
                "               A.FOLLOWUP_DAYS,\n" +
                "               A.FAREA,\n" +
                "               A.FDEPTCODE,\n" +
                "               C.LONGITUDE,\n" +
                "               C.LATITUDE,\n" +
                "               C.AREA_CENTER_FLAG,\n" +
                "               A.LIGHT_MONTH_FLAG,\n" +
                "               A.LIGHT_LASTMONTH_FLAG,\n" +
                "               A.PLANED_CLUE_FLAG,\n" +
                "               A.COLOR_MONTH,\n" +
                "               A.COLOR_LASTMONTH,\n" +
                "               A.CLUECHANNEL,\n" +
                "                <if test=\"visitedMonthFlag != null and visitedMonthFlag != ''\">\n" +
                "                    A.VISITED_MONTH_FLAG AS VISIT_TASK_FLAG,\n" +
                "                </if>\n" +
                "                <if test=\"visitedLastmonthFlag != null and visitedLastmonthFlag != ''\">\n" +
                "                    A.VISITED_LASTMONTH_FLAG AS VISIT_TASK_FLAG,\n" +
                "                </if>\n" +
                "               DECODE(A.FNTENTIONAL_CUST,NULL,'','0','非意向客户','1','意向客户','') AS FNTENTIONAL_CUST,\n" +
                "               DECODE(B.ID,NULL,'关注','','关注','NULL','关注','已关注') AS IFFOLLOW\n" +
                "        FROM ACW.T_OMR_5KM_AGILE_CLUES A\n" +
                "          <if test=\"getSubordinatesDept != null and getSubordinatesDept != ''\">\n" +
                "            JOIN DATA2 D ON A.FDEPTCODE =D.LEVEL_FOUR_CODE\n" +
                "          </if>\n" +
                "          LEFT JOIN ACW.T_OMR_5KM_COMMON_CONTACT B\n" +
                "            ON A.FCUSTNUMBER = B.CUSTNUMBER AND B.EMPCODE =#{empCodeTwo} AND B.TYPE='1'\n" +
                "          LEFT JOIN ACW.T_OMR_5KM_CUSTPOINT C ON A.FCUSTNUMBER =C.CUSTCODE AND C.ISCLUE ='0'\n" +
                "         WHERE 1=1\n" +
                "        <if test=\"empCode != null and empCode != ''\">\n" +
                "            AND A.FFOLLOWUPEMPCODE =#{empCode}\n" +
                "        </if>\n" +
                "        <if test=\"fcustnumber != null and fcustnumber != ''\">\n" +
                "            AND A.FCUSTNUMBER =#{fcustnumber}\n" +
                "        </if>\n" +
                "        <if test=\"fmobiletel != null and fmobiletel != ''\">\n" +
                "            AND A.FMOBILETEL =#{fmobiletel}\n" +
                "        </if>\n" +
                "        <if test=\"foffertel != null and foffertel != ''\">\n" +
                "            AND A.FOFFERTEL =#{foffertel}\n" +
                "        </if>\n" +
                "        <if test=\"fcustname != null and fcustname != ''\">\n" +
                "            AND A.FCUSTNAME like CONCAT('%',CONCAT(#{fcustname},'%'))\n" +
                "        </if>\n" +
                "        <if test=\"faddress != null and faddress != ''\">\n" +
                "            AND A.FADDRESS like CONCAT('%',CONCAT(#{faddress},'%'))\n" +
                "        </if>\n" +
                "        <if test=\"lightMonthFlag != null and lightMonthFlag != ''\">\n" +
                "            AND A.LIGHT_MONTH_FLAG =#{lightMonthFlag}\n" +
                "        </if>\n" +
                "        <if test=\"lightLastmonthFlag != null and lightLastmonthFlag != ''\">\n" +
                "            AND A.LIGHT_LASTMONTH_FLAG =#{lightLastmonthFlag}\n" +
                "        </if>\n" +
                "        <if test=\"planedMonthFlag != null and planedMonthFlag != ''\">\n" +
                "            AND A.PLANED_MONTH_FLAG =#{planedMonthFlag}\n" +
                "        </if>\n" +
                "        <if test=\"planedLastmonthFlag != null and planedLastmonthFlag != ''\">\n" +
                "            AND A.PLANED_LASTMONTH_FLAG =#{planedLastmonthFlag}\n" +
                "        </if>\n" +
                "        <if test=\"visitedMonthFlag != null and visitedMonthFlag != ''\">\n" +
                "            AND A.VISITED_MONTH_FLAG =#{visitedMonthFlag}\n" +
                "        </if>\n" +
                "        <if test=\"visitedLastmonthFlag != null and visitedLastmonthFlag != ''\">\n" +
                "            AND A.VISITED_LASTMONTH_FLAG =#{visitedLastmonthFlag}\n" +
                "        </if>\n" +
                "        <if test=\"fvisitTime != null and fvisitTime != ''\">\n" +
                "            <choose>\n" +
                "                <when test=\"fvisitTime ==4\">\n" +
                "                    AND A.FVISIT_TIME >= #{fvisitTime}\n" +
                "                </when>\n" +
                "                <otherwise>\n" +
                "                    AND A.FVISIT_TIME = #{fvisitTime}\n" +
                "                </otherwise>\n" +
                "            </choose>\n" +
                "        </if>\n" +
                "        <if test=\"fntentionalCust != null and fntentionalCust != ''\">\n" +
                "            AND A.FNTENTIONAL_CUST = #{fntentionalCust}\n" +
                "        </if>\n" +
                "        <if test=\"fdeliverypotential != null and fdeliverypotential != ''\">\n" +
                "            <choose>\n" +
                "                <when test=\"fdeliverypotential =='syb'\">\n" +
                "                    <![CDATA[\n" +
                "                        AND A.FVOLUME_UPPER_LIMIT > 100000\n" +
                "                    ]]>\n" +
                "                </when>\n" +
                "                <when test=\"fdeliverypotential =='qgsybdq'\">\n" +
                "                    <![CDATA[\n" +
                "                        AND A.FVOLUME_UPPER_LIMIT > 50000\n" +
                "                    ]]>\n" +
                "                </when>\n" +
                "                <when test=\"fdeliverypotential =='yyq'\">\n" +
                "                    <![CDATA[\n" +
                "                        AND A.FVOLUME_UPPER_LIMIT > 10000\n" +
                "                    ]]>\n" +
                "                </when>\n" +
                "                <otherwise>\n" +
                "                    AND A.FDELIVERYPOTENTIAL = #{fdeliverypotential}\n" +
                "                </otherwise>\n" +
                "            </choose>\n" +
                "        </if>\n" +
                "        <if test=\"findustry != null and findustry != ''\">\n" +
                "            AND (A.FINDUSTRY IN\n" +
                "            <foreach item=\"item\" index=\"index\" collection=\"getFindustryList\" open=\"(\" separator=\",\" close=\")\">\n" +
                "                #{item}\n" +
                "            </foreach>\n" +
                "            <if test=\"findustryTwo != null and findustryTwo != ''\">\n" +
                "                OR A.FINDUSTRY IS NULL\n" +
                "            </if>\n" +
                "            )\n" +
                "        </if>\n" +
                "        <if test=\"farea != null and farea != ''\">\n" +
                "            AND A.FAREA IN\n" +
                "            <foreach item=\"item\" index=\"index\" collection=\"getFareaList\" open=\"(\" separator=\",\" close=\")\">\n" +
                "                #{item}\n" +
                "            </foreach>\n" +
                "        </if>\n" +
                "        <if test=\"fuseLogistics != null and fuseLogistics != ''\">\n" +
                "            AND A.FUSE_LOGISTICS LIKE CONCAT('%',#{fuseLogistics})\n" +
                "        </if>\n" +
                "        <if test=\"fdeptcode != null and fdeptcode != ''\">\n" +
                "            AND A.FDEPTCODE =#{fdeptcode}\n" +
                "        </if>\n" +
                "        <if test=\"fcluestage != null and fcluestage != ''\">\n" +
                "            AND A.FCLUESTAGE =#{fcluestage}\n" +
                "        </if>\n" +
                "        <if test=\"clueChannel != null and clueChannel != ''\">\n" +
                "            AND A.CLUECHANNEL =#{clueChannel}\n" +
                "        </if>\n" +
                "        <if test=\"fmarkettheme != null and fmarkettheme != ''\">\n" +
                "            AND A.FMARKETTHEME =#{fmarkettheme}\n" +
                "        </if>\n" +
                "        <if test=\"fdateDim != null and fdateDim != ''\">\n" +
                "            <choose>\n" +
                "                <when test=\"fdateDim =='lastMonth'\">\n" +
                "                    <![CDATA[\n" +
                "                        AND A.FCREATETIME >= TO_DATE(TO_CHAR(TRUNC(ADD_MONTHS(SYSDATE,-1),'MM'),'YYYY-MM-DD'),'YYYY-MM-DD')\n" +
                "                        AND A.FCREATETIME < TO_DATE(TO_CHAR(LAST_DAY(ADD_MONTHS(SYSDATE, -1)),'YYYY-MM-DD'),'YYYY-MM-DD')\n" +
                "                    ]]>\n" +
                "                </when>\n" +
                "                <when test=\"fdateDim =='month'\">\n" +
                "                    <![CDATA[\n" +
                "                        AND A.FCREATETIME >= TO_DATE(TO_CHAR(TRUNC(ADD_MONTHS(LAST_DAY(SYSDATE), -1) + 1), 'YYYY-MM-DD'), 'YYYY-MM-DD')\n" +
                "                        AND A.FCREATETIME < SYSDATE\n" +
                "                    ]]>\n" +
                "                </when>\n" +
                "                <otherwise>\n" +
                "                    AND 1 = 1\n" +
                "                </otherwise>\n" +
                "            </choose>\n" +
                "        </if>\n" +
                "        <choose>\n" +
                "            <when test=\"orderBy == 1\">\n" +
                "                <choose>\n" +
                "                    <when test=\"ascOrDesc != null and ascOrDesc != ''\">\n" +
                "                        ORDER BY A.FIRST_LIGHT_DATE DESC,A.FNTENTIONAL_CUST DESC,A.FCREATETIME DESC,A.FVOLUME_UPPER_LIMIT DESC,A.FOLLOWUP_DAYS DESC,A.FVISIT_TIME DESC\n" +
                "                    </when>\n" +
                "                    <otherwise>\n" +
                "                        ORDER BY A.FIRST_LIGHT_DATE DESC,A.FNTENTIONAL_CUST ASC,A.FCREATETIME DESC,A.FVOLUME_UPPER_LIMIT DESC,A.FOLLOWUP_DAYS DESC,A.FVISIT_TIME DESC\n" +
                "                    </otherwise>\n" +
                "                </choose>\n" +
                "            </when>\n" +
                "            <when test=\"orderBy == 2\">\n" +
                "                <choose>\n" +
                "                    <when test=\"ascOrDesc != null and ascOrDesc != ''\">\n" +
                "                        ORDER BY A.FVOLUME_UPPER_LIMIT DESC,A.FIRST_LIGHT_DATE DESC,A.FNTENTIONAL_CUST DESC,A.FCREATETIME DESC,A.FOLLOWUP_DAYS DESC,A.FVISIT_TIME DESC\n" +
                "                    </when>\n" +
                "                    <otherwise>\n" +
                "                        ORDER BY A.FVOLUME_UPPER_LIMIT ASC,A.FIRST_LIGHT_DATE DESC,A.FNTENTIONAL_CUST DESC,A.FCREATETIME DESC,A.FOLLOWUP_DAYS DESC,A.FVISIT_TIME DESC\n" +
                "                    </otherwise>\n" +
                "                </choose>\n" +
                "            </when>\n" +
                "            <when test=\"orderBy == 3\">\n" +
                "                <choose>\n" +
                "                    <when test=\"ascOrDesc != null and ascOrDesc != ''\">\n" +
                "                        ORDER BY A.FOLLOWUP_DAYS DESC,A.FIRST_LIGHT_DATE DESC,A.FNTENTIONAL_CUST DESC,A.FCREATETIME DESC,A.FVOLUME_UPPER_LIMIT DESC,A.FOLLOWUP_DAYS DESC,A.FVISIT_TIME DESC\n" +
                "                    </when>\n" +
                "                    <otherwise>\n" +
                "                        ORDER BY A.FOLLOWUP_DAYS ASC,A.FIRST_LIGHT_DATE DESC,A.FNTENTIONAL_CUST DESC,A.FCREATETIME DESC,A.FVOLUME_UPPER_LIMIT DESC,A.FOLLOWUP_DAYS DESC,A.FVISIT_TIME DESC\n" +
                "                    </otherwise>\n" +
                "                </choose>\n" +
                "            </when>\n" +
                "            <when test=\"orderBy == 4\">\n" +
                "                <choose>\n" +
                "                    <when test=\"ascOrDesc != null and ascOrDesc != ''\">\n" +
                "                        ORDER BY A.FVISIT_TIME DESC,A.FIRST_LIGHT_DATE DESC,A.FNTENTIONAL_CUST DESC,A.FCREATETIME DESC,A.FVOLUME_UPPER_LIMIT DESC,A.FOLLOWUP_DAYS DESC\n" +
                "                    </when>\n" +
                "                    <otherwise>\n" +
                "                        ORDER BY A.FVISIT_TIME ASC,A.FIRST_LIGHT_DATE DESC,A.FNTENTIONAL_CUST DESC,A.FCREATETIME DESC,A.FVOLUME_UPPER_LIMIT DESC,A.FOLLOWUP_DAYS DESC\n" +
                "                    </otherwise>\n" +
                "                </choose>\n" +
                "            </when>\n" +
                "            <when test=\"orderBy == 5\">\n" +
                "                <choose>\n" +
                "                    <when test=\"ascOrDesc != null and ascOrDesc != ''\">\n" +
                "                        ORDER BY A.FCREATETIME DESC,A.FIRST_LIGHT_DATE DESC,A.FNTENTIONAL_CUST DESC,A.FVOLUME_UPPER_LIMIT DESC,A.FOLLOWUP_DAYS DESC,A.FVISIT_TIME DESC\n" +
                "                    </when>\n" +
                "                    <otherwise>\n" +
                "                        ORDER BY A.FCREATETIME ASC,A.FIRST_LIGHT_DATE DESC,A.FNTENTIONAL_CUST DESC,A.FVOLUME_UPPER_LIMIT DESC,A.FOLLOWUP_DAYS DESC,A.FVISIT_TIME DESC\n" +
                "                    </otherwise>\n" +
                "                </choose>\n" +
                "            </when>\n" +
                "            <otherwise>\n" +
                "                ORDER BY A.FNTENTIONAL_CUST DESC,A.FCREATETIME DESC,A.FVOLUME_UPPER_LIMIT DESC,A.FOLLOWUP_DAYS DESC,A.FVISIT_TIME DESC\n" +
                "            </otherwise>\n" +
                "        </choose>";
        Map<String,Object> params = new HashMap<>();
        params.put("ascOrDesc","1");
        params.put("empCode","050058");
        params.put("empCodeTwo","22223a");
        params.put("fdateDim","month");
        params.put("fdeptcode","W011302");
        params.put("fdeliverypotential","yyq");
        params.put("orderBy","1");
        params.put("type",2);

        DynamicSqlEngine engine = new DynamicSqlEngine();
        SqlMeta sqlMeta = engine.parse(sql, params);
        System.out.println(SQLUtils.format(sqlMeta.getSql(), DbType.oracle));
        System.out.println(sqlMeta.getJdbcParamValues());
    }*/

    @Ignore
    @Test
    public void test03(){
        String str="SELECT A.DEPTCODE, B.DEPTNAME,\n" +
                "        NVL2(A.CENTER_POINT,A.CENTER_POINT,NVL2(A.LATITUDE,CONCAT(A.LONGITUDE,CONCAT(',',A.LATITUDE)),'')) AS CENTER_POINT\n" +
                "        FROM ACW.T_OMR_5KM_DEPT_LATANDLNG A\n" +
                "        LEFT JOIN ACW.T_ORG_DEPARTMENT B ON A.DEPTCODE = B.DEPTCODE\n" +
                "        AND B.STATUS != '2'\n" +
                "        AND B.PRINCIPAL IS NOT NULL\n" +
                "        WHERE B.DEPTNAME NOT LIKE '%组' AND B.DEPTNAME NOT LIKE '%站'\n" +
                "        AND B.DEPTNAME NOT LIKE '%【%' AND B.DEPTNAME NOT LIKE '%运作部'\n" +
                "        AND B.DEPTNAME NOT LIKE '%（待撤销）%' AND B.DEPTNAME NOT LIKE '%(待撤销)%'\n" +
                "        <if test=\"moreDeptCode != null and moreDeptCode.size() > 0\">\n" +
                "            AND A.DEPTCODE IN\n" +
                "            <foreach item=\"item\" index=\"index\" collection=\"moreDeptCode\" open=\"(\" separator=\",\" close=\")\">\n" +
                "                #{item}\n" +
                "            </foreach>\n" +
                "        </if>";
        Map<String,Object> params = new HashMap<>();
        List<String> list =new ArrayList<>();
        list.add("test001");
        list.add("test002");
        list.add("test003");
        list.add("test004");
        params.put("moreDeptCode",list);

        DynamicSqlEngine engine = new DynamicSqlEngine();
        SqlMeta sqlMeta = engine.parse(str, params);
        System.out.println(SQLUtils.format(sqlMeta.getSql(), DbType.mysql));
        System.out.println(sqlMeta.getJdbcParamValues());
    }

    @Ignore
    @Test
    public void test04() {
        String script  = "select \n" +
                "consult_id,\n" +
                "diagnosis \n" +
                "from dwd.dwd_consult_info\n" +
                "where gmt_start_consult is not null\n" +
                "<if test=\"user_id != null and user_id != ''\">\n" +
                " and operator_no =#{user_id}\n" +
                "</if>\n" +
                "<if test = 'consult_id != null'>\n" +
                "    and consult_id in \n" +
                "    <foreach collection=\"consult_id\" open=\"(\" close=\")\" separator=\",\" item=\"item\">\n" +
                "        #{item}\n" +
                "    </foreach>\n" +
                "</if>\n" +
                "<if test=\"date_start!=null and date_start != ''\">\n" +
                " and to_char(gmt_create,'yyyy-mm-dd')>=#{date_start}\n" +
                "</if>\n" +
                "<if test=\"date_end!=null and date_end != ''\">\n" +
                "    <![CDATA[ and to_char(gmt_create,'yyyy-mm-dd')<=#{date_end} ]]>\n" +
                "</if>\n" +
                "order by gmt_create";
        Map<String,Object> params = new HashMap<>();
        params.put("user_id","210001573730115");
        params.put("date_start","2021-12-14");
        params.put("date_end","2021-12-15");
        ///BoundSql boundSql =new BoundSql(script.toString(), params, null);
        ///log.info("\ntestVar:{},\nbuildParams：{},\nparams:{},\nsql:{}",script, JsonUtils.toJsonString(boundSql),boundSql.getParameters(),boundSql.getSql());

        DynamicSqlEngine engine = new DynamicSqlEngine();
        SqlMeta sqlMeta = engine.parse(script, params);
        System.out.println(SQLUtils.format(sqlMeta.getSql(), DbType.mysql));
        System.out.println(sqlMeta.getJdbcParamValues());
        // sqlMeta.getJdbcParamValues().forEach(System.out::println);
    }
    @Ignore
    @Test
    public void test06(){
        String str="select c.agent_no,\n" +
                "a.order_times>0 as order_flag,\n" +
                "b.operator_no,\n" +
                "b.open_id,\n" +
                "b.union_id,\n" +
                "a.view_times,\n" +
                "a.order_times,\n" +
                "a.actual_amt as order_amt,\n" +
                "a.active_times,\n" +
                "a.service_times,\n" +
                "e.nick_name as user_name,\n" +
                "b.head_portrait\n" +
                "from (select distinct agent_no,operator_no,max(create_time) create_time from dwd.dwd_agent_customer_relation group by agent_no,operator_no) c\n" +
                "inner join ads.ads_user_behavior a on a.operator_no=c.operator_no\n" +
                "<choose>\n" +
                "    <when test=\"order_flag\">\n" +
                "        and a.order_times >0\n" +
                "    </when>\n" +
                "    <otherwise>\n" +
                "        <![CDATA[ and a.order_times <= 0 ]]>\n" +
                "    </otherwise>\n" +
                "</choose>\n" +
                "left join dwd.dwd_user_info b on a.operator_no=b.operator_no\n" +
                "left join dwd.dwd_user_di e on a.operator_no=e.operator_no\n" +
                "where c.agent_no= #{agent_no}\n" +
                "<if test=\"user_name != null and user_name != ''\">\n" +
                " and b.user_name= #{user_name}\n" +
                "</if>\n" +
                "order by c.create_time,c.operator_no desc";

        Map<String,Object> params = new HashMap<>();
        params.put("agent_no","TEST001");
        params.put("order_flag",false);
        params.put("user_name","");
        DynamicSqlEngine engine = new DynamicSqlEngine();
        SqlMeta sqlMeta = engine.parse(str, params);
        System.out.println(SQLUtils.format(sqlMeta.getSql(), DbType.mysql));
        System.out.println(sqlMeta.getJdbcParamValues());
    }

    @Test
    public void test07(){
        String str="select\n" +
                "    a.user_id,\n" +
                "    a.person_id,\n" +
                "    a.person_name,\n" +
                "    a.sex,\n" +
                "    a.age,\n" +
                "    a.date_birth,\n" +
                "    a.doctor_id,\n" +
                "    a.doctor_name,\n" +
                "    a.group_num,\n" +
                "    a.mobile_no,\n" +
                "    a.bind_time,\n" +
                "    a.case_summary,\n" +
                "    a.instrest_status,\n" +
                "    a.is_add_doctor is_bind_doctor,\n" +
                "    group_concat(b.tag_value) as tag_value,\n" +
                "    a.task_type,\n" +
                "    a.task_status,\n" +
                "    a.after_diagnosis,\n" +
                "    if(c.tag_value is null,'N',c.tag_value) as prescribe\n" +
                "from\n" +
                "    api_doctor_dialog_user a\n" +
                "    <choose>\n" +
                "      <when test=\"person_name != null and person_name != ''\">\n" +
                "        force index(idx_dialog_user_t3)\n" +
                "      </when>\n" +
                "      <when test=\"mobile_no != null and mobile_no != ''\">\n" +
                "        force index(idx_dialog_user_t2)\n" +
                "      </when>\n" +
                "      <when test=\"user_id != null\">\n" +
                "        force index(idx_dialog_user_t1)\n" +
                "      </when>\n" +
                "      <otherwise>\n" +
                "        force index(idx_dialog_user_t4)\n" +
                "      </otherwise>\n" +
                "    </choose>\n" +
                "    left join ads_user_disease_interface b on a.person_id=b.patient_id and b.status in ('INSERT', 'NOT_CHANGE')\n" +
                "    left join api_tag_user_dtl c on a.user_id=c.user_id and c.tag_key='prescribe'\n" +
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
                "    and a.person_name like concat(#{person_name},'%')\n" +
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
                "    <choose>\n" +
                "     <when test=\"prescribe ==&quot;Y&quot;\">"+
                "        and c.tag_value='Y'\n" +
                "      </when>\n" +
                "      <otherwise>\n" +
                "        and (c.tag_value='N' OR c.tag_value is null)\n" +
                "      </otherwise>\n" +
                "    </choose>\n" +
                "group by 1";

        List aaa =new ArrayList();
        aaa.add("22223");
        Map<String,Object> params = new HashMap<>();
        params.put("doctor_list",aaa);
        params.put("person_name","ttt");
        params.put("prescribe","N");
        DynamicSqlEngine engine = new DynamicSqlEngine();
        SqlMeta sqlMeta = engine.parse(str, params, String.valueOf(DbType.mysql));
        System.out.println(SQLUtils.format(sqlMeta.getSql(), DbType.mysql));
        System.out.println(sqlMeta.getJdbcParamValues());
    }

    @Test
    public void test08(){
        String str ="select\n" +
                "  a.patient_id,\n" +
                "  a.tag_key,\n" +
                "  coalesce(a.record_date, b.yearmd) as record_date,\n" +
                "  coalesce(a.tag_value, '0') as tag_value\n" +
                "from\n" +
                "  dim_date b\n" +
                "left join\n" +
                "(select patient_id,tag_key,date_format(date(start_time),'%Y-%m-%d') as record_date,max(tag_value) tag_value\n" +
                "from api_patient_blood_record_info where relation_type='RELA_MYSELF'\n" +
                "<if test = 'tag_key != null'>\n" +
                "   and tag_key in\n" +
                "    <foreach collection=\"tag_key\" open=\"(\" close=\")\" separator=\",\" item=\"type\">\n" +
                "        #{type}\n" +
                "    </foreach>\n" +
                "</if>\n" +
                "<if test=\"patient_id != null and patient_id != ''\">\n" +
                " and patient_id= #{patient_id}\n" +
                "</if>\n" +
                "group by patient_id,date_format(date(start_time),'%Y-%m-%d'),tag_key\n" +
                ")a on date(a.record_date) = b.yearmd\n" +
                "<where>\n" +
                "  <if test=\"dim_date!=null and dim_date != ''\">\n" +
                "    <choose>\n" +
                "      <when test=\"dim_date=='week'\">\n" +
                "        and b.yearmd between date(DATE_SUB(NOW(), INTERVAL 6 day)) and date(current_timestamp)\n" +
                "      </when>\n" +
                "        <when test=\"dim_date=='month'\">\n" +
                "        and b.yearmd between date(DATE_SUB(NOW(), INTERVAL 1 month)) and date(current_timestamp)\n" +
                "      </when>\n" +
                "      <when test=\"dim_date=='year'\">\n" +
                "        b.yearmd between date(DATE_SUB(NOW(), INTERVAL 1 year)) and date(current_timestamp)\n" +
                "      </when>\n" +
                "    </choose>\n" +
                "  </if>\n" +
                "</where>\n" +
                "order by b.yearmd";


        Map<String,Object> params = new HashMap<>();

        params.put("dim_date","month");
        DynamicSqlEngine engine = new DynamicSqlEngine();
        SqlMeta sqlMeta = engine.parse(str, params, String.valueOf(DbType.mysql));
        System.out.println(SQLUtils.format(sqlMeta.getSql(), DbType.mysql));
        System.out.println(sqlMeta.getJdbcParamValues());
    }


    @Test
    public void testChoose() {
        String sql = " select id,last_name,email,salary\n" +
                "        from tbl_employee\n" +
                "        <where>\n" +
                "            <choose>\n" +
                "                <when test=\"id != null\">\n" +
                "                    and id = #{id}\n" +
                "                </when>\n" +
                "                <when test=\"lastName != null\">\n" +
                "                    and last_name = #{lastName}\n" +
                "                </when>\n" +
                "                <when test=\"email != null\">\n" +
                "                    and email = #{email}\n" +
                "                </when>\n" +
                "                <when test=\"count != null\">\n" +
                "                    and `count` = #{count}\n" +
                "                </when>\n" +
                "                <otherwise>\n" +
                "                    and 1=1\n" +
                "                </otherwise>\n" +
                "            </choose>\n" +
                "        </where>";

        DynamicSqlEngine engine = new DynamicSqlEngine();
        Map<String,Object> params = new HashMap<>();
        //params.put("id", 1001);
        //params.put("lastName", "su");
        //params.put("email", "su@163.com");
        //params.put("count", 234.56);
        SqlMeta sqlMeta = engine.parse(sql, params);
        System.out.println(sqlMeta.getSql());
        System.out.println(sqlMeta.getJdbcParamValues());

    }

    @Test
    public void testFunction2() {
        String sql="SELECT\n" +
                "  -- 第一次产前随访ID业务主键,系统业务主键【必填】\n" +
                "  t1.PREFIRSTVISIT_ID AS PREFIRSTVISIT_ID,\n" +
                "  -- 城乡居民健康档案ID,关联个人基本信息健康档案表HEALTH_RECORD_ID\n" +
                "  t1.HEALTH_RECORD_ID AS HEALTH_RECORD_ID,\n" +
                "  -- 孕产妇管理卡ID，关联妇女保健-孕产妇管理卡\n" +
                "  t1.MANAGE_ID AS MANAGE_ID,\n" +
                "  -- 产前随访记录表单编号按照某一特定编码规则赋予孕妇产前随访记录表单的顺序号【必填】\n" +
                "  t1.PREN_VISIT_FORM_NO AS PREN_VISIT_FORM_NO,\n" +
                "  -- 本人姓名本人在公安管理部门正式登记注册的姓氏和名称\n" +
                "  t1.NAME AS NAME,\n" +
                "  -- 年龄，年龄\n" +
                "  t1.AGE AS AGE,\n" +
                "  -- 填写日期填写记录表单时的公元纪年日期\n" +
                "  t1.FILL_IN_DATE AS FILL_IN_DATE,\n" +
                "  -- 参加免费孕检 CT01.00.002是否代码\n" +
                "  t1.PREGNANCY_TEST AS PREGNANCY_TEST,\n" +
                "  -- 是否服用叶酸 CT01.00.002是否代码\n" +
                "  t1.TAKING_FOLIC_ACID AS TAKING_FOLIC_ACID,\n" +
                "  -- 开始服用叶酸时间\n" +
                "  t1.FOLIC_ACID_TIME AS FOLIC_ACID_TIME,\n" +
                "  -- 孕周(d)产前随访时孕妇的妊娠时长,计量单位为d\n" +
                "  t1.GEST_WEEKS AS GEST_WEEKS,\n" +
                "  -- 孕天\n" +
                "  t1.GEST_DAYS AS GEST_DAYS,\n" +
                "  -- 孕次妊娠次数的累计值，包括异位妊娠，计量单位为次\n" +
                "  t1.GRAVIDITY AS GRAVIDITY,\n" +
                "  -- 产次(次)育龄妇女已经生育的孩子次数，(指孕周达28周，包括28周后的引产，要计人本次分娩)双多胎分娩算一孕次、二或n产次，计量单位为次\n" +
                "  t1.PARITY AS PARITY,\n" +
                "  -- 阴道分娩次数(次)阴道分娩次数的累计值\n" +
                "  t1.VAGINAL_DELIVERY_NUM AS VAGINAL_DELIVERY_NUM,\n" +
                "  -- 剖宫产次数(次)剖宫产次数的累计值\n" +
                "  t1.SECT_DELIVERY_NUM AS SECT_DELIVERY_NUM,\n" +
                "  -- 末次月经日期明确标志标识孕妇对末次月经日期是否明确CT01.00.002是否代码\n" +
                "  t1.LAST_MENSES_MARK AS LAST_MENSES_MARK,\n" +
                "  -- 末次月经日期末次月经首日的公元纪年日期\n" +
                "  t1.MENSES_LAST_DATE AS MENSES_LAST_DATE,\n" +
                "  -- 预产期根据孕妇末次月经来潮第一天推算的预产期的公元纪年日期\n" +
                "  t1.EXPECT_CONFINE AS EXPECT_CONFINE,\n" +
                "  -- 妇科手术史标志标识育龄妇女既往是否接受过妇科手术/操作CT01.00.002是否代码\n" +
                "  t1.GYN_OP_HISTORY_MARK AS GYN_OP_HISTORY_MARK,\n" +
                "  -- 妇科手术史对育龄妇女既往接受手术/操作详细情况的描述\n" +
                "  t1.GYN_OP_HISTORY AS GYN_OP_HISTORY,\n" +
                "  -- 妊娠合并症/并发症史既往妊娠合并/并发其他疾病史的详细描述GD02.11.036常见妊娠合并症/并发症代码\n" +
                "  t1.GEST_COMPLICATION AS GEST_COMPLICATION,\n" +
                "  -- 家族疾病史类别代码,家族疾病史类别代码,GD05.03.005家族疾病史类别\n" +
                "  t1.FADISHIS_TYPE_CODE AS FADISHIS_TYPE_CODE,\n" +
                "  -- 流产总次数(次)育龄妇女人工流产和自然流产次数的累计值\n" +
                "  t1.ABORTION_TIMES AS ABORTION_TIMES,\n" +
                "  -- 自然流产次数\n" +
                "  t1.SPONTANEOUS_NUM AS SPONTANEOUS_NUM,\n" +
                "  -- 人工流产次数\n" +
                "  t1.ABORTION_NUM AS ABORTION_NUM,\n" +
                "  -- 死产例数育龄妇女既往分娩胎儿在分娩过程中死亡的累计例数\n" +
                "  t1.STILLBIRTH_NUM AS STILLBIRTH_NUM,\n" +
                "  -- 死胎例数育龄妇女分娩死胎的累计例数\n" +
                "  t1.DEAD_FETUS_NUM AS DEAD_FETUS_NUM,\n" +
                "  -- 新生儿死亡例数育龄妇女分娩的新生儿死亡的累计例数\n" +
                "  t1.NEWBORN_DEATH_NUM AS NEWBORN_DEATH_NUM,\n" +
                "  -- 出生缺陷儿例数育龄妇女分娩缺陷儿的累计例数\n" +
                "  t1.BIRTH_DEFECT_COUNT AS BIRTH_DEFECT_COUNT,\n" +
                "  -- 高危级别(GD02.11.035)\n" +
                "  t1.HIGH_RISK_LEVEL AS HIGH_RISK_LEVEL,\n" +
                "  -- 是否有紫色高危因素(CT01.00.002是否代码)\n" +
                "  t1.PURPLE_FLAG AS PURPLE_FLAG,\n" +
                "  -- 身高(cm)身高的测量值，计量单位为cm\n" +
                "  t1.HEIGHT AS HEIGHT,\n" +
                "  -- 体重(kg)体重的测量值，计量单位为kg\n" +
                "  t1.WEIGHT AS WEIGHT,\n" +
                "  -- 体质指数根据体重(kg)除以身高平方(cm2)计算出的指数\n" +
                "  t1.BMI AS BMI,\n" +
                "  -- 收缩压(mmHg)收缩压的测量值，计量单位为mmHg\n" +
                "  t1.SBP AS SBP,\n" +
                "  -- 舒张压(mmHg)舒张压的测量值，计量单位为mmHg\n" +
                "  t1.DBP AS DBP,\n" +
                "  -- 主诉孕妇向医师描述的对自身本次随访的相关感受的主要记录\n" +
                "  t1.CHIEF_COMPLAINT AS CHIEF_COMPLAINT,\n" +
                "  -- 肝功检测异常标志\n" +
                "  t1.LIVER_EXAM_MARK AS LIVER_EXAM_MARK,\n" +
                "  -- 肝功检测异常结果描述\n" +
                "  t1.LIVER_EXAM_DESCR AS LIVER_EXAM_DESCR,\n" +
                "  -- 肾功检测异常标志\n" +
                "  t1.KIDNEY_EXAM_MARK AS KIDNEY_EXAM_MARK,\n" +
                "  -- 肾功检测异常结果描述\n" +
                "  t1.KIDNEY_EXAM_DESCR AS KIDNEY_EXAM_DESCR,\n" +
                "  -- 肺部听诊异常标志标识肺部听诊是否存在异常CT01.00.002是否代码\n" +
                "  t1.LUNG_ABNORM_MARK AS LUNG_ABNORM_MARK,\n" +
                "  -- 肺部听诊异常结果描述肺部听诊异常结果的详细描述\n" +
                "  t1.LUNG_ABNORM_DESCR AS LUNG_ABNORM_DESCR,\n" +
                "  -- 心脏听诊异常标志标识心脏听诊是否存在异常CT01.00.002是否代码\n" +
                "  t1.HEART_ABNORM_MARK AS HEART_ABNORM_MARK,\n" +
                "  -- 心脏听诊异常结果描述心脏听诊异常结果的详细描述\n" +
                "  t1.HEART_ABNORM_DESCR AS HEART_ABNORM_DESCR,\n" +
                "  -- 外阴异常标志标识外阴检查是否存在异常CT01.00.002是否代码\n" +
                "  t1.VULVA_ABNORM_MARK AS VULVA_ABNORM_MARK,\n" +
                "  -- 外阴异常描述外阴检查异常情况的详细描述\n" +
                "  t1.VULVA_ABNORM_DESCR AS VULVA_ABNORM_DESCR,\n" +
                "  -- 阴道异常标志标识阴道检查是否存在异常CT01.00.002是否代码\n" +
                "  t1.VAGINA_ABNORM_MARK AS VAGINA_ABNORM_MARK,\n" +
                "  -- 阴道异常描述阴道检查异常情况的详细描述\n" +
                "  t1.VAGINA_ABNORM_DESCR AS VAGINA_ABNORM_DESCR,\n" +
                "  -- 宫颈异常标志标识宫颈检查是否存在异常CT01.00.002是否代码\n" +
                "  t1.CERVIX_ABNORM_MARK AS CERVIX_ABNORM_MARK,\n" +
                "  -- 宫颈异常描述宫颈检查异常情况的详细描述\n" +
                "  t1.CERVIX_ABNORM_DESCR AS CERVIX_ABNORM_DESCR,\n" +
                "  -- 宫体异常标志标识宫体检查是否存在异常CT01.00.002是否代码\n" +
                "  t1.CORPUS_ABNORM_MARK AS CORPUS_ABNORM_MARK,\n" +
                "  -- 宫体异常描述宫体检查异常情况的具体描述\n" +
                "  t1.CORPUS_ABNORM_DESCR AS CORPUS_ABNORM_DESCR,\n" +
                "  -- 附件异常标志标识附件检查是否存在异常CT01.00.002是否代码\n" +
                "  t1.ADNEXA_ABNORM_MARK AS ADNEXA_ABNORM_MARK,\n" +
                "  -- 附件异常描述附件检查异常情况的详细描述\n" +
                "  t1.ADNEXA_ABNORM_DESCR AS ADNEXA_ABNORM_DESCR,\n" +
                "  -- 腹围(cm)腹部周长的测量值，计量单位为cm\n" +
                "  t1.ABDOMEN_CIRCUM AS ABDOMEN_CIRCUM,\n" +
                "  -- 胎心率(次/min)单位时间内胎儿胎心搏动的次数，计量单位为次/min\n" +
                "  t1.FETAL_HEART_RATE AS FETAL_HEART_RATE,\n" +
                "  -- 血红蛋白值(g/L),受检者单位容积血液中血红蛋白的含量值,计量单位为g/L\n" +
                "  t1.HGB AS HGB,\n" +
                "  -- 白细胞计数值(G/L)受检者单位容积血液内血小板的数值，计量单位为G/L\n" +
                "  t1.WBC AS WBC,\n" +
                "  -- 血小板计数值(G/L)受检者单位容积血液内血小板的数量值，计量单位为G/L\n" +
                "  t1.PLT AS PLT,\n" +
                "  -- 血常规其他\n" +
                "  t1.BLOOD_ROUTINE_OTHER AS BLOOD_ROUTINE_OTHER,\n" +
                "  -- 尿蛋白定量检测值(mg/24h)采用定量检测方法测得的24小时尿蛋白含量，计量单位为mg/24h\n" +
                "  t1.PRO_QUAN_TEST_VALUE AS PRO_QUAN_TEST_VALUE,\n" +
                "  -- 尿蛋白定性检测结果代码尿蛋白定性检测结果的代码CV04.50.015尿实验室定性检测结果代码\n" +
                "  t1.PRO_QUAL_RESULT_CODE AS PRO_QUAL_RESULT_CODE,\n" +
                "  -- 尿蛋白定性检测结果尿蛋白定性检测结果\n" +
                "  t1.PRO_QUAL_RESULT_NAME AS PRO_QUAL_RESULT_NAME,\n" +
                "  -- 尿糖定性检测结果代码尿糖定性检测结果代码CV04.50.015尿实验室定性检测结果代码\n" +
                "  t1.GLU_QUAL_RESULT_CODE AS GLU_QUAL_RESULT_CODE,\n" +
                "  -- 尿糖定性检测结果尿糖定性检测结果\n" +
                "  t1.GLU_QUAL_RESULT_NAME AS GLU_QUAL_RESULT_NAME,\n" +
                "  -- 尿糖定量检测(mmol/L)尿糖定量检测的结果，计算单位为(mmol/L)\n" +
                "  t1.GLU_QUAN_TEST_VALUE AS GLU_QUAN_TEST_VALUE,\n" +
                "  -- 尿酮体定性检测结果代码尿酮体定性检测结果代码CV04.50.015尿实验室定性检测结果代码\n" +
                "  t1.KET_QUAL_RESULT_CODE AS KET_QUAL_RESULT_CODE,\n" +
                "  -- 尿酮体定性检测结果尿酮体定性检测结果\n" +
                "  t1.KET_QUAL_RESULT_NAME AS KET_QUAL_RESULT_NAME,\n" +
                "  -- 尿潜血检测结果代码尿潜血检测结果的代码CV04.50.015尿实验室定性检测结果代码\n" +
                "  t1.UOB_TEST_RESULT_CODE AS UOB_TEST_RESULT_CODE,\n" +
                "  -- 尿潜血检测结果尿潜血检测结果\n" +
                "  t1.UOB_QUAL_RESULT_NAME AS UOB_QUAL_RESULT_NAME,\n" +
                "  -- 尿常规其他\n" +
                "  t1.URINE_ROUTINE_OTHER AS URINE_ROUTINE_OTHER,\n" +
                "  -- ABO血型代码居民本人的ABO血型类别代码CV04.50.005ABO血型代码\n" +
                "  t1.ABO_CODE AS ABO_CODE,\n" +
                "  -- Rh血型标志居民本人血型Rh类别标志CV04.50.020Rh（D）血型代码\n" +
                "  t1.RH_CODE AS RH_CODE,\n" +
                "  -- 血糖检测值(mmol/L)空腹时血液中葡萄糖定值检测结果值\n" +
                "  t1.GLU AS GLU,\n" +
                "  -- 血清谷丙转氨酶值(U/L)受检者单位容积血清中谷丙转氨酶的含量值，计量单位为U/L\n" +
                "  t1.GPT_SGPT AS GPT_SGPT,\n" +
                "  -- 血清谷草转氨酶值(U/L)单位容积血清中谷草转氨酶的含量值，计量单位为U/L\n" +
                "  t1.AST AS AST,\n" +
                "  -- 白蛋白浓度(g/L)肝功能检查血清白蛋白的检测结果值，计量单位为\n" +
                "  t1.ALB AS ALB,\n" +
                "  -- 总胆红素值(umol/L)总胆红素的检测结果值,计量单位为umol/L\n" +
                "  t1.TBI AS TBI,\n" +
                "  -- 结合胆红素值(umol/L)结合胆红素的检测结果值,计量单位为umol/L\n" +
                "  t1.DBIL AS DBIL,\n" +
                "  -- 血尿素氮检测值(mmol／L)受检者单位容积血清中尿素氮的含量，计量单位为mmol／L\n" +
                "  t1.BUN AS BUN,\n" +
                "  -- 血肌酐值(umol/L)血肌酐的检测结果值,计量单位为umol/L\n" +
                "  t1.SCR AS SCR,\n" +
                "  -- 阴道分泌物清洁度代码阴道分泌物清洁度的分级代码CV04.50.010阴道分泌物清洁度代码\n" +
                "  t1.WVD_CLEAN_CODE AS WVD_CLEAN_CODE,\n" +
                "  -- 乙型肝炎病毒e抗体检测结果代码乙型肝炎病毒e抗体定性检测结果的分类代码GD99.01.232受检者检测结果代码\n" +
                "  t1.HBEAB_TEST_ID AS HBEAB_TEST_ID,\n" +
                "  -- 乙型肝炎病毒e抗原检测结果代码乙型肝炎病毒e抗原定性检测结果的分类代码GD99.01.232受检者检测结果代码\n" +
                "  t1.HBEAG_TEST_ID AS HBEAG_TEST_ID,\n" +
                "  -- 乙型肝炎病毒表面抗体检测结果代码乙型肝炎病毒表面抗体定性检测结果的分类代码GD99.01.232受检者检测结果代码\n" +
                "  t1.HBSAB_TEST_ID AS HBSAB_TEST_ID,\n" +
                "  -- 乙型肝炎表面抗原检测结果代码乙型肝炎病毒表面抗原定性检测结果的分类代码GD99.01.232受检者检测结果代码\n" +
                "  t1.HBSAG_TEST_ID AS HBSAG_TEST_ID,\n" +
                "  -- 乙型肝炎病毒核心抗体检测结果代码乙型肝炎病毒核心抗体定性检测结果的分类代码GD99.01.232受检者检测结果代码\n" +
                "  t1.HBCAB_TEST_ID AS HBCAB_TEST_ID,\n" +
                "  -- 梅毒血清学试验结果代码梅毒血清学试验结果的分类代码GD99.01.232受检者检测结果代码\n" +
                "  t1.STS_RESULT_CODE AS STS_RESULT_CODE,\n" +
                "  -- HIV抗体检测结果代码HIV抗体检测结果的代码GD99.01.225检测结果代码HIV抗体\n" +
                "  t1.HIV_ANTI_RESULT_CODE AS HIV_ANTI_RESULT_CODE,\n" +
                "  -- B超检查结果B超检查结果的详细描述\n" +
                "  t1.BSCAN_RESULT_DESCR AS BSCAN_RESULT_DESCR,\n" +
                "  -- 辅助检查其他\n" +
                "  t1.AUXILIARY_CHECK_OTHER AS AUXILIARY_CHECK_OTHER,\n" +
                "  -- 孕产妇健康评估异常标志标识孕产妇健康评估结论是否异常CT01.00.002是否代码\n" +
                "  t1.PREG_HEALTH_EXE_FLAG AS PREG_HEALTH_EXE_FLAG,\n" +
                "  -- 孕产妇健康评估异常结果描述孕产妇健康评估异常结果的详细描述\n" +
                "  t1.PREG_HEALTH_EXE_DESC AS PREG_HEALTH_EXE_DESC,\n" +
                "  -- 是否增加服务标识\n" +
                "  t1.ADDITIONAL_SERVICES_REMARK AS ADDITIONAL_SERVICES_REMARK,\n" +
                "  -- 转诊标志标识孕妇是否转诊CT01.00.002是否代码\n" +
                "  t1.REFERRAL_MARK AS REFERRAL_MARK,\n" +
                "  -- 转入医疗机构名称孕妇转诊转入的医疗卫生机构的组织机构名称\n" +
                "  t1.REFERTO_ORG_NAME AS REFERTO_ORG_NAME,\n" +
                "  -- 转入机构科室名称孕妇转诊转入的医疗机构所属科室名称\n" +
                "  t1.REFERTO_DEPT_NAME AS REFERTO_DEPT_NAME,\n" +
                "  -- 转诊原因对孕妇转诊原因的简要描述\n" +
                "  t1.REFERRAL_REASON AS REFERRAL_REASON,\n" +
                "  -- 随访医生工号\n" +
                "  t1.FOLLOW_DOCTOR_CODE AS FOLLOW_DOCTOR_CODE,\n" +
                "  -- 访视医师姓名访视医师在公安户籍管理部门正式登记注册的姓氏和名称\n" +
                "  t1.VISIT_DOCTOR_NAME AS VISIT_DOCTOR_NAME,\n" +
                "  -- 本次访视日期对孕产妇进行本次医学访视当日的公元纪年日期\n" +
                "  t1.THIS_VISIT_DATE AS THIS_VISIT_DATE,\n" +
                "  -- 下次访视日期对孕产妇进行下次医学访视的公元纪年日期\n" +
                "  t1.NEXT_VISIT_DATE AS NEXT_VISIT_DATE,\n" +
                "  -- 下次访视地点对孕产妇进行下次医学访视的地点描述\n" +
                "  t1.NEXT_VISIT_PLACE AS NEXT_VISIT_PLACE,\n" +
                "  t1.GONOCOCCUS ,\n" +
                "  t1.WVD_EXAM_RESULT,\n" +
                "  -- 产前检查机构编码组织机构统一社会信用代码\n" +
                "  t1.VISIT_ORG_CODE AS VISIT_ORG_CODE,\n" +
                "  t1.COR_MENSES_LAST_DATE,t1.LIVE_BIRTHS,t1.BLOOD_ROUTINE_RESULT,t1.PRO_QUAL_RESULT,t1.ABO_RESULT,t1.RH_RESULT,t1.GLU_RESULT,t1.LIVER_RESULT,t1.KIDNEY_RESULT\n" +
                ",t1.HBCAB_RESULT,t1.BSCAN_RESULT_MARK,t1.SCREEN_RESULT,t1.SCREEN_DATE,t1.NONINVASIVE_RESULT,t1.NONINVASIVE_DATE\n" +
                ",t1.PREDIAGONSIS_DATE,t1.PREDIAGONSIS_RESULT,t1.IMMED_TRANSFER,t1.VISIT_RAMARKS,t1.VISIT_ORG_NAME,\n" +
                "  t1.ORG_ID,\n" +
                "  t1.ORG_NAME,\n" +
                "  t1.OPER_DATE,\n" +
                "  t1.OPER_CODE,\n" +
                "  t1.OPER_NAME\n" +
                "FROM ${databaseName}.FB_PREFIRSTVISIT AS t1\n" +
                "   <where>\n" +
                "<if test=\"MANAGE_ID != null and MANAGE_ID != ''\">\n" +
                " and t1.MANAGE_ID =#{MANAGE_ID}\n" +
                "</if>\n" +
                "<if test=\"PREFIRSTVISIT_ID != null and PREFIRSTVISIT_ID != ''\">\n" +
                " and t1.PREFIRSTVISIT_ID =#{PREFIRSTVISIT_ID}\n" +
                "</if>\n" +
                " </where>\n" +
                "LIMIT\n" +
                "  0, 1000";
        Map<String, Object> map = new HashMap<>();
        map.put("databaseName","gognwei");
        map.put("MANAGE_ID","111");
        map.put("PREFIRSTVISIT_ID","222");
        DynamicSqlEngine engine = new DynamicSqlEngine();
        SqlMeta sqlMeta = engine.parse(sql, map);
        System.out.println(sqlMeta.getSql());
        System.out.println(sqlMeta.getJdbcParamValues());

    }

}
