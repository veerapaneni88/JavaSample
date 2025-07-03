package us.tx.state.dfps.service.admin.daoimpl;

import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.admin.dao.ServiceDlvryClosureStageDao;
import us.tx.state.dfps.service.admin.dto.ServiceDlvryClosureStageInDto;
import us.tx.state.dfps.service.admin.dto.ServiceDlvryClosureStageOutDto;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This Class
 * Retrieves The SVC DEVL and Stage details Aug 21, 2017- 3:32:13 PM © 2017
 * Texas Department of Family and Protective Services
 */
@Repository
public class ServiceDlvryClosureStageDaoImpl implements ServiceDlvryClosureStageDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${ServiceDlvryClosureStageDaoImpl.retrvDecisionDate}")
	private String retrvDecisionDate;

	@Value("${ServiceDlvryClosureStageDaoImpl.retrvDecisionDateAps}")
	private String retrvDecisionDateAps;

	private static final Logger log = Logger.getLogger("ServiceBusiness-SvcdelvdtlStageDao");

	/**
	 * @method Description :This method Retrieves The SVC DEVL and Stage details
	 * @param svcdelvdtlStageInDto
	 * @return liCsvc21doDto
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ServiceDlvryClosureStageOutDto> retrvDecisionDate(ServiceDlvryClosureStageInDto svcdelvdtlStageInDto) {
		log.debug("Entering method retrvDecisionDate in SvcdelvdtlStageDaoImpl");
		SQLQuery sQLQuery = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(retrvDecisionDate)
				.setResultTransformer(Transformers.aliasToBean(ServiceDlvryClosureStageOutDto.class)));
		sQLQuery.addScalar("dtDtSvcDelvDecision", StandardBasicTypes.DATE);
		sQLQuery.addScalar("lastUpdate", StandardBasicTypes.DATE);
		sQLQuery.addScalar("indECS", StandardBasicTypes.STRING);
		sQLQuery.addScalar("cdClientAdvised", StandardBasicTypes.STRING);
		sQLQuery.addScalar("indECSVer", StandardBasicTypes.STRING);
		sQLQuery.addScalar("dtClientAdvised", StandardBasicTypes.DATE);
		sQLQuery.setParameter("hI_ulIdStage", svcdelvdtlStageInDto.getIdStage());
		List<ServiceDlvryClosureStageOutDto> liCsvc21doDto = (List<ServiceDlvryClosureStageOutDto>) sQLQuery.list();
		if (TypeConvUtil.isNullOrEmpty(liCsvc21doDto)) {
			throw new DataNotFoundException(messageSource.getMessage("Csvc21d.not.found.UlIdStage", null, Locale.US));
		}
		log.debug("Exiting method retrvDecisionDate in SvcdelvdtlStageDaoImpl");
		return liCsvc21doDto;
	}


	@Override
	public List<ServiceDlvryClosureStageOutDto> retrvDecisionDateAps(ServiceDlvryClosureStageInDto svcdelvdtlStageInDto) {
		log.debug("Entering method retrvDecisionDateAps in SvcdelvdtlStageDaoImpl");
		SQLQuery sQLQuery = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(retrvDecisionDateAps)
				.setResultTransformer(Transformers.aliasToBean(ServiceDlvryClosureStageOutDto.class)));
		sQLQuery.addScalar("dtDtSvcDelvDecision", StandardBasicTypes.DATE);
		sQLQuery.addScalar("lastUpdate", StandardBasicTypes.DATE);
		sQLQuery.addScalar("indECS", StandardBasicTypes.STRING);
		sQLQuery.addScalar("cdClientAdvised", StandardBasicTypes.STRING);
		sQLQuery.addScalar("indECSVer", StandardBasicTypes.STRING);
		sQLQuery.addScalar("dtClientAdvised", StandardBasicTypes.DATE);
		sQLQuery.setParameter("hI_ulIdStage", svcdelvdtlStageInDto.getIdStage());
		List<ServiceDlvryClosureStageOutDto> liCsvc21doDto = (List<ServiceDlvryClosureStageOutDto>) sQLQuery.list();
		if (TypeConvUtil.isNullOrEmpty(liCsvc21doDto)) {
			throw new DataNotFoundException(messageSource.getMessage("Csvc21d.not.found.UlIdStage", null, Locale.US));
		}
		log.debug("Exiting method retrvDecisionDate in SvcdelvdtlStageDaoImpl");
		return liCsvc21doDto;
	}
}
