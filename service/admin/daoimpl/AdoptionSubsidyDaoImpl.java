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

import us.tx.state.dfps.service.admin.dao.AdoptionSubsidyDao;
import us.tx.state.dfps.service.admin.dto.AdoptionSubsidyInDto;
import us.tx.state.dfps.service.admin.dto.AdoptionSubsidyOutDto;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This DAO
 * will select a full row from the Adoption Subsidy Table for each open subsidy
 * for the Id Person passed in. Aug 10, 2017- 4:02:03 PM © 2017 Texas Department
 * of Family and Protective Services
 */
@Repository
public class AdoptionSubsidyDaoImpl implements AdoptionSubsidyDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${AdoptionSubsidyDaoImpl.getAdoptionSubsidyRecord}")
	private String getAdoptionSubsidyRecord;

	private static final Logger log = Logger.getLogger(AdoptionSubsidyDaoImpl.class);

	public AdoptionSubsidyDaoImpl() {
		super();
	}

	/**
	 * Method Name: getAdoptionSubsidyRecord Method Description: This fetches
	 * row from adoption for person Subsidy
	 * 
	 * @param Clss69diDto
	 * @return List<Clss69doDto> @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<AdoptionSubsidyOutDto> getAdoptionSubsidyRecord(AdoptionSubsidyInDto pInputDataRec) {
		log.debug("Entering method AdoptionSubsidyQUERYdam in AdoptionSubsidyDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getAdoptionSubsidyRecord)
				.addScalar("idAdptSub", StandardBasicTypes.LONG).addScalar("idAdptSubPayee", StandardBasicTypes.LONG)
				.addScalar("adptSubPerson", StandardBasicTypes.LONG)
				.addScalar("tsLastUpdate", StandardBasicTypes.TIMESTAMP)
				.addScalar("idPlcmtEvent", StandardBasicTypes.LONG).addScalar("amtAdptSub", StandardBasicTypes.FLOAT)
				.addScalar("cdAdptSubCloseRsn", StandardBasicTypes.STRING)
				.addScalar("cdAdptSubDeterm", StandardBasicTypes.STRING)
				.addScalar("dtAdptSubAgreeRetn", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtAdptSubAgreeSent", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtAdptSubAppReturned", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtAdptSubAppSent", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtAdptSubApprvd", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtAdptSubEffective", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtAdptSubEnd", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtAdptSubLastInvc", StandardBasicTypes.TIMESTAMP)
				.addScalar("indAdptSubProcess", StandardBasicTypes.STRING)
				.addScalar("indAdptSubThirdParty", StandardBasicTypes.STRING)
				.addScalar("adptSubRsn", StandardBasicTypes.STRING)
				.setParameter("hI_ulAdptSubPerson", pInputDataRec.getAdptSubPerson())
				.setParameter("hI_dtDtPersonDeath", pInputDataRec.getDtPersonDeath())
				.setResultTransformer(Transformers.aliasToBean(AdoptionSubsidyOutDto.class)));
		List<AdoptionSubsidyOutDto> liClss69doDto = (List<AdoptionSubsidyOutDto>) sQLQuery1.list();
		if (TypeConvUtil.isNullOrEmpty(liClss69doDto)) {
			throw new DataNotFoundException(
					messageSource.getMessage("Clss69dDaoImpl.no.adoption.subsidy.record", null, Locale.US));
		}
		log.debug("Exiting method AdoptionSubsidyQUERYdam in AdoptionSubsidyDaoImpl");
		return liClss69doDto;
	}
}
