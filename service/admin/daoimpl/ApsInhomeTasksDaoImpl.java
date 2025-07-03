package us.tx.state.dfps.service.admin.daoimpl;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import us.tx.state.dfps.common.domain.ApsInHomeTasks;
import us.tx.state.dfps.common.domain.ApsInHomeTasksId;
import us.tx.state.dfps.service.admin.dao.ApsInhomeTasksDao;
import us.tx.state.dfps.service.admin.dto.ApsInHomeTasksDto;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: CLSS25D
 * Description: Do the CRUD operation for Employee related data Apr 12, 2017 -
 * 3:00:33 PM
 */

@Repository
public class ApsInhomeTasksDaoImpl implements ApsInhomeTasksDao {

    @Autowired
    MessageSource messageSource;

    @Autowired
    private SessionFactory sessionFactory;

    @Value("${ApsInhomeTasksDaoImpl.getApsInhomeTasksSql}")
    private String getApsInhomeTasksSql;

    private static final Logger log = Logger.getLogger(ApsInhomeTasksDaoImpl.class);

    public ApsInhomeTasksDaoImpl() {
        super();
    }

    /**
     *
     * Method Description:getApsInhomeTasks
     *
     * @param IdSvcAuth
     * @return @ @
     */
    @Override
    public List<ApsInHomeTasksDto> getInhomeTasks(Long IdSvcAuth){
        List<ApsInHomeTasksDto>  apsInHomeTasksDto= null;

        Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(getApsInhomeTasksSql)
                .addScalar("inHomeSvcAuthTask", StandardBasicTypes.STRING)
                .addScalar("dtLastUpdate", StandardBasicTypes.DATE)
                .addScalar("dtPurged", StandardBasicTypes.DATE)
                .addScalar("idSvcAuth", StandardBasicTypes.LONG)
                .setResultTransformer(Transformers.aliasToBean(ApsInHomeTasksDto.class));
        query.setParameter("IdSvcAuth", IdSvcAuth);
        apsInHomeTasksDto = query.list();
        return apsInHomeTasksDto;
    }

    /**
     *
     * @param IdSvcAuth
     * @return
     *
     * retrieve the existing In home tasks list for APS Details
     */
    @Override
    public List<ApsInHomeTasksDto> getInhomeTasksBySrvicsId(Long IdSvcAuth) {
        List<ApsInHomeTasksDto>  apsInHomeTasksDtoList= null;

        Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(getApsInhomeTasksSql)
                .addScalar("inHomeSvcAuthTask", StandardBasicTypes.STRING)
                .addScalar("dtLastUpdate", StandardBasicTypes.DATE)
                .addScalar("dtPurged", StandardBasicTypes.DATE)
                .addScalar("idSvcAuth", StandardBasicTypes.LONG)
                .setResultTransformer(Transformers.aliasToBean(ApsInHomeTasksDto.class));
        query.setParameter("IdSvcAuth", IdSvcAuth);
        apsInHomeTasksDtoList = query.list();
        return apsInHomeTasksDtoList;
    }

    /**
     *
     * @param cbxInhomeList
     * @param idSvcAuth
     */
    @Override
    public void saveAPSInhomeTasks(List<String> cbxInhomeList, Long idSvcAuth) {

        ApsInHomeTasks apsInHomeTasks = null;
        ApsInHomeTasksId apsInHomeTasksId = null;
        List<ApsInHomeTasksDto> dbList = getInhomeTasksBySrvicsId(idSvcAuth);
        Map<String, Date> existedInHomeTaskMap = new HashMap<>();
        dbList.forEach(dto -> existedInHomeTaskMap.put(dto.getInHomeSvcAuthTask(), dto.getDtLastUpdate()));
         for (Object cbxIncome : cbxInhomeList) {
             if (null == existedInHomeTaskMap.get(cbxIncome)) {
                 apsInHomeTasks = new ApsInHomeTasks();
                 apsInHomeTasksId = new ApsInHomeTasksId();
                 apsInHomeTasksId.setIdApsInhomeSvcAuth(idSvcAuth);
                 apsInHomeTasks.setId(apsInHomeTasksId);
                 apsInHomeTasks.getId().setIdApsInhomeSvcAuth(idSvcAuth);
                 apsInHomeTasks.getId().setCdApsInhomeTask(cbxIncome.toString());
                 apsInHomeTasks.setDtLastUpdate(new Date());
                 sessionFactory.getCurrentSession().saveOrUpdate(apsInHomeTasks);

             }else{
               existedInHomeTaskMap.remove(cbxIncome);
            }
        }
        if(!existedInHomeTaskMap.isEmpty()){
            for(Map.Entry<String, Date> deleteInHome: existedInHomeTaskMap.entrySet()) {
                apsInHomeTasks = new ApsInHomeTasks();
                apsInHomeTasksId = new ApsInHomeTasksId();
                apsInHomeTasksId.setCdApsInhomeTask(deleteInHome.getKey());
                apsInHomeTasks.setDtLastUpdate(deleteInHome.getValue());
                apsInHomeTasksId.setIdApsInhomeSvcAuth(idSvcAuth);
                apsInHomeTasks.setId(apsInHomeTasksId);
                sessionFactory.getCurrentSession().delete(apsInHomeTasks);
            }
        }
    }

}
