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

import us.tx.state.dfps.service.admin.dao.LegalStatusPersonMaxStatusDtDao;
import us.tx.state.dfps.service.admin.dto.LegalStatusPersonMaxStatusDtInDto;
import us.tx.state.dfps.service.admin.dto.LegalStatusPersonMaxStatusDtOutDto;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Retrieves
 * latest legal status for a person Aug 11, 2017- 8:42:50 AM © 2017 Texas
 * Department of Family and Protective Services
 */
@Repository
public class LegalStatusPersonMaxStatusDtDaoImpl implements LegalStatusPersonMaxStatusDtDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${LegalStatusPersonMaxStatusDtDaoImpl.getRecentLegelStatusRecord}")
	private String getRecentLegelStatusRecord;

	private static final Logger log = Logger.getLogger(LegalStatusPersonMaxStatusDtDaoImpl.class);

	public LegalStatusPersonMaxStatusDtDaoImpl() {
		super();
	}

	/**
	 * Method Name: getRecentLegelStatusRecord Method Description: Fetch recent
	 * legal status for the person
	 * 
	 * @param pInputDataRec
	 * @return List<Cses32doDto>
	 * @,DataNotFoundException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<LegalStatusPersonMaxStatusDtOutDto> getRecentLegelStatusRecord(
			LegalStatusPersonMaxStatusDtInDto pInputDataRec) {
		log.debug("Entering method LegalStatusPersonMaxStatusDtQUERYdam in LegalStatusPersonMaxStatusDtDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getRecentLegelStatusRecord)
				.addScalar("idLegalStatEvent", StandardBasicTypes.LONG)
				.addScalar("tsLastUpdate", StandardBasicTypes.TIMESTAMP).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("cdLegalStatCnty", StandardBasicTypes.STRING)
				.addScalar("cdLegalStatStatus", StandardBasicTypes.STRING)
				.addScalar("dtLegalStatStatusDt", StandardBasicTypes.TIMESTAMP)
				.addScalar("legalStatCauseNbr", StandardBasicTypes.STRING)
				.addScalar("legalStatCourtNbr", StandardBasicTypes.STRING)
				.setParameter("hI_ulIdPerson", pInputDataRec.getIdPerson())
				.setResultTransformer(Transformers.aliasToBean(LegalStatusPersonMaxStatusDtOutDto.class)));
		List<LegalStatusPersonMaxStatusDtOutDto> liCses32doDto = (List<LegalStatusPersonMaxStatusDtOutDto>) sQLQuery1
				.list();
		if (TypeConvUtil.isNullOrEmpty(liCses32doDto)) {
			throw new DataNotFoundException(
					messageSource.getMessage("Cses32dDaoImpl.no.legal.record", null, Locale.US));
		}
		log.debug("Exiting method LegalStatusPersonMaxStatusDtQUERYdam in LegalStatusPersonMaxStatusDtDaoImpl");
		return liCses32doDto;
	}
}
