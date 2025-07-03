package us.tx.state.dfps.service.casemanagement.daoimpl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import us.tx.state.dfps.service.casemanagement.dao.CaseMaintenanceFetchEventDtlDao;
import us.tx.state.dfps.service.casepackage.dto.EventRtrvTaskInDto;
import us.tx.state.dfps.service.casepackage.dto.EventRtrvTaskOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:CaseMaintenanceFetchEventDtlDaoImpl Feb 7, 2018- 5:50:27 PM ©
 * 2017 Texas Department of Family and Protective Services
 */
@Repository
public class CaseMaintenanceFetchEventDtlDaoImpl implements CaseMaintenanceFetchEventDtlDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${CaseMaintenanceFetchEventDtlDaoImpl.strCLSC71D_CURSORQuery}")
	private transient String strCLSC71D_CURSORQuery;

	private static final Logger log = Logger.getLogger(CaseMaintenanceFetchEventDtlDaoImpl.class);

	/**
	 *
	 * Method Name: fetchEventDtl Method Description:This Method is used to
	 * fetch the event dtl DAM: CLSC71D
	 * 
	 * @param eventRtrvTaskInDto
	 * @param eventRtrvTaskOutDto
	 * @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void fetchEventDtl(EventRtrvTaskInDto eventRtrvTaskInDto, EventRtrvTaskOutDto eventRtrvTaskOutDto) {
		log.debug("Entering method fetchEventDtl in CaseMaintenanceFetchEventDtlDaoImpl");

		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(strCLSC71D_CURSORQuery)
				.addScalar("idEvent", StandardBasicTypes.LONG).addScalar("tsLastUpdate", StandardBasicTypes.TIMESTAMP)
				.addScalar("idStage", StandardBasicTypes.LONG).addScalar("cdEventType", StandardBasicTypes.STRING)
				.addScalar("idEventPerson", StandardBasicTypes.LONG).addScalar("cdTask", StandardBasicTypes.STRING)
				.addScalar("txtEventDescr", StandardBasicTypes.STRING)
				.addScalar("dtEventOccurred", StandardBasicTypes.TIMESTAMP)
				.addScalar("cdEventStatus", StandardBasicTypes.STRING)
				.setParameter("idStage", eventRtrvTaskInDto.getUlIdStage())
				.setParameter("cdTask", eventRtrvTaskInDto.getSzCdTask())
				.setResultTransformer(Transformers.aliasToBean(EventRtrvTaskOutDto.class)));

		List<EventRtrvTaskOutDto> eventRtrvTaskOutDtos = new ArrayList<>();
		eventRtrvTaskOutDtos = (List<EventRtrvTaskOutDto>) sQLQuery1.list();
		if (!CollectionUtils.isEmpty(eventRtrvTaskOutDtos)) {
			BeanUtils.copyProperties(eventRtrvTaskOutDtos.get(0), eventRtrvTaskOutDto);
		}

		log.debug("Exiting method fetchEventDtl in CaseMaintenanceFetchEventDtlDaoImpl");
	}

}
