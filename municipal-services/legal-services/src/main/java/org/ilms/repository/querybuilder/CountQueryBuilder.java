package org.ilms.repository.querybuilder;

import org.springframework.stereotype.Component;

@Component
public class CountQueryBuilder {

    private final String Query = "select count(DISTINCT id)" +
            "from " +
            "( select id from eg_wf_processinstance_v2 pi_outer WHERE  pi_outer.lastmodifiedTime = " +
            " (SELECT max(lastmodifiedTime) from eg_wf_processinstance_v2 as pi_inner where pi_inner.businessid = pi_outer.businessid and tenantid = 'mp' )" +
            " AND id in" +
            " (select processinstanceid from eg_wf_assignee_v2 asg_inner where asg_inner.assignee in" +
            " ( select uuid from eg_user where id IN" +
            "  (select user_id from eg_userrole_v1 where role_code in (?)))" +
            "  AND pi_outer.tenantid = 'mp'  AND pi_outer.businessservice ='iLMS'" +
            " ORDER BY pi_outer.lastModifiedTime DESC )) as count";



     public String getCountQuery(){
         return Query;
     }
}

