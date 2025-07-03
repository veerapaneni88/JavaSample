package us.tx.state.dfps.service.casemanagement.daoimpl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.casemanagement.dao.CaseMaintenanceUpdateEventPersonLinkDao;
import us.tx.state.dfps.service.casepackage.dto.EventPersonLinkUpdateInDto;
import us.tx.state.dfps.service.casepackage.dto.EventPersonLinkUpdateOutDto;
import us.tx.state.dfps.service.common.ServiceConstants;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:CaseMaintenanceUpdateEventPersonLinkDaoImpl Feb 7, 2018- 5:52:04
 * PM © 2017 Texas Department of Family and Protective Services
 */
@Repository
public class CaseMaintenanceUpdateEventPersonLinkDaoImpl implements CaseMaintenanceUpdateEventPersonLinkDao {
	@Autowired
	private SessionFactory sessionFactory;

	@Value("${CaseMaintenanceUpdateEventPersonLinkDaoImpl.strQuery1}")
	private transient String strQuery1;

	@Value("${CaseMaintenanceUpdateEventPersonLinkDaoImpl.strQuery2}")
	private transient String strQuery2;

	@Value("${CaseMaintenanceUpdateEventPersonLinkDaoImpl.strQuery3}")
	private transient String strQuery3;

	private static final Logger log = Logger.getLogger(CaseMaintenanceUpdateEventPersonLinkDaoImpl.class);

	/**
	 * Method Name: updateEventPersonLink Method Description:This Method is used
	 * to update Event person link DAM: Ccmn68d
	 * 
	 * @param eventPersonLinkUpdateInDto
	 * @param eventPersonLinkUpdateOutDto
	 * @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void updateEventPersonLink(EventPersonLinkUpdateInDto eventPersonLinkUpdateInDto,
			EventPersonLinkUpdateOutDto eventPersonLinkUpdateOutDto) {
		log.debug("Entering method updateEventPersonLink in CaseMaintenanceUpdateEventPersonLinkDaoImpl");
		switch (eventPersonLinkUpdateInDto.getReqFuncCd()) {
		case ServiceConstants.REQ_FUNC_CD_ADD:
		case ServiceConstants.REQ_FUNC_CD_ADD_KIN:
			SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(strQuery1)
					.addScalar("idEventPersLink", StandardBasicTypes.LONG)
					.setResultTransformer(Transformers.aliasToBean(EventPersonLinkUpdateOutDto.class)));
			List<EventPersonLinkUpdateOutDto> eventPersonLinkUpdateOutDtos = new ArrayList<>();
			eventPersonLinkUpdateOutDtos = (List<EventPersonLinkUpdateOutDto>) sQLQuery1.list();

			SQLQuery sQLQuery2 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(strQuery2)
					.setParameter("hO_ulIdEventPersLink", eventPersonLinkUpdateOutDtos.get(0).getIdEventPersLink())
					.setParameter("hI_ulIdPerson", eventPersonLinkUpdateInDto.getUlIdPerson())
					.setParameter("hI_ulIdEvent", eventPersonLinkUpdateInDto.getUlIdEvent()));
			sQLQuery2.executeUpdate();

			break;
		case ServiceConstants.REQ_FUNC_CD_DELETE:
			SQLQuery sQLQuery3 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(strQuery3)
					.setParameter("hI_ulIdPerson", eventPersonLinkUpdateInDto.getUlIdPerson())
					.setParameter("hI_tsLastUpdate", eventPersonLinkUpdateInDto.getTsLastUpdate())
					.setParameter("hI_ulIdEvent", eventPersonLinkUpdateInDto.getUlIdEvent()));
			sQLQuery3.executeUpdate();

			break;
		}

		log.debug("Exiting method updateEventPersonLink in CaseMaintenanceUpdateEventPersonLinkDaoImpl");
	}

}
