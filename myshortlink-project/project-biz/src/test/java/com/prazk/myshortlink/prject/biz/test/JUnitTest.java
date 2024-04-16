package com.prazk.myshortlink.prject.biz.test;


import org.junit.jupiter.api.Test;

public class JUnitTest {
    private String sql = "CREATE TRIGGER `t_link_%d_BEFORE_DELETE`  \n" +
            "    BEFORE DELETE  \n" +
            "    ON `t_link_%d`  \n" +
            "    FOR EACH ROW  \n" +
            "BEGIN  \n" +
            "    INSERT INTO `t_link_deleted_archive` (domain, short_uri, full_short_uri,  \n" +
            "     origin_uri, total_pv, total_uv, total_uip, gid, enable_status, created_type,  \n" +
            "     valid_date_type, valid_date, description, create_time, update_time,  \n" +
            "     del_time, del_table)  \n" +
            "    VALUES (OLD.domain,  \n" +
            "            OLD.short_uri,  \n" +
            "            OLD.full_short_uri,  \n" +
            "            OLD.origin_uri,  \n" +
            "            OLD.total_pv,  \n" +
            "            OLD.total_uv,  \n" +
            "            OLD.total_uip,  \n" +
            "            OLD.gid,  \n" +
            "            OLD.enable_status,  \n" +
            "            OLD.created_type,  \n" +
            "            OLD.valid_date_type,  \n" +
            "            OLD.valid_date,  \n" +
            "            OLD.description,  \n" +
            "            OLD.create_time,  \n" +
            "            OLD.update_time,  \n" +
            "            CURRENT_TIMESTAMP(),  \n" +
            "            't_link_%d');  \n" +
            "END;";
    @Test
    public void test() {
        for (int i = 0; i < 4; i++) {
            System.out.printf(sql, i,i,i);
        }
    }
}
