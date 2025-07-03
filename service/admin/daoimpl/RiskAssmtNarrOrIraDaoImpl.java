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

import us.tx.state.dfps.service.admin.dao.RiskAssmtNarrOrIraDao;
import us.tx.state.dfps.service.admin.dto.RiskAssmtNarrOrIraInDto;
import us.tx.state.dfps.service.admin.dto.RiskAssmtNarrOrIraOutDto;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:DAO
 * Interface for fetching Event Dtls Aug 6, 2017- 4:37:55 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@Repository
public class RiskAssmtNarrOrIraDaoImpl implements RiskAssmtNarrOrIraDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${RiskAssmtNarrOrIraDaoImpl.getLastUpdate}")
	private transient String getLastUpdate;

	@Value("${RiskAssmtNarrOrIraDaoImpl.eventId}")
	private transient String eventId;

	private static final Logger log = Logger.getLogger(RiskAssmtNarrOrIraDaoImpl.class);

	public RiskAssmtNarrOrIraDaoImpl() {
		super();
	}

	/**
	 * 
	 * Method Name: getEventDtls Method Description: This method will retrieves
	 * Event Details.
	 * 
	 * @param pInputDataRec
	 * @return List<RiskAssmtNarrOrIraOutDto> @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<RiskAssmtNarrOrIraOutDto> getEventDtls(RiskAssmtNarrOrIraInDto pInputDataRec) {
		log.debug("Entering method RiskAssmtNarrOrIraQUERYdam in RiskAssmtNarrOrIraDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getLastUpdate + " " + pInputDataRec.getSysTxtTablename() + " " + eventId)
				.setResultTransformer(Transformers.aliasToBean(RiskAssmtNarrOrIraOutDto.class)));
		sQLQuery1.addScalar("tsLastUpdate", StandardBasicTypes.TIMESTAMP);
		sQLQuery1.setParameter("hI_ulIdEvent", pInputDataRec.getIdEvent());
		List<RiskAssmtNarrOrIraOutDto> liCsys13doDto = (List<RiskAssmtNarrOrIraOutDto>) sQLQuery1.list();
		if (TypeConvUtil.isNullOrEmpty(liCsys13doDto)) {
			throw new DataNotFoundException(
					messageSource.getMessage("RiskAssmtNarrOrIraDaoImpl.not.found.ulIdEvent", null, Locale.US));
		}
		log.debug("Exiting method RiskAssmtNarrOrIraDaodQUERYdam in RiskAssmtNarrOrIraDaoImpl");
		return liCsys13doDto;
	}
}
