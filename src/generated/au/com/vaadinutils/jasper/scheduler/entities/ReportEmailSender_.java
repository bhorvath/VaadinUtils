/** 
 *  Generated by OpenJPA MetaModel Generator Tool.
**/

package au.com.vaadinutils.jasper.scheduler.entities;

import java.lang.Boolean;
import java.lang.Long;
import java.lang.String;
import javax.persistence.metamodel.SingularAttribute;

@javax.persistence.metamodel.StaticMetamodel
(value=au.com.vaadinutils.jasper.scheduler.entities.ReportEmailSender.class)
@javax.annotation.Generated
(value="org.apache.openjpa.persistence.meta.AnnotationProcessor6",date="Tue May 13 17:45:29 EST 2014")
public class ReportEmailSender_ {
    public static volatile SingularAttribute<ReportEmailSender,String> emailAddress;
    public static volatile SingularAttribute<ReportEmailSender,Long> iID;
    public static volatile SingularAttribute<ReportEmailSender,Boolean> isAdmin;
    public static volatile SingularAttribute<ReportEmailSender,String> username;
}
