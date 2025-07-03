package us.tx.state.dfps.service.admin.daoimpl;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.admin.dao.ReferralPersonLinkDao;
import us.tx.state.dfps.service.admin.dto.ReferralPersonLinkInDto;
import us.tx.state.dfps.service.admin.dto.ReferralPersonLinkOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This
 * Retrieves a full row from the Person Referral Link table. Aug 5, 2017-4:03:26
 * PM © 2017 Texas Department of Family and Protective Services
 */
@Repository
public class ReferralPersonLinkDaoImpl implements ReferralPersonLinkDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${ReferralPersonLinkDaoImpl.getPersonReferralLink}")
	private String getPersonReferralLink;

	private static final Logger log = Logger.getLogger(ReferralPersonLinkDaoImpl.class);

	public ReferralPersonLinkDaoImpl() {
		super();
	}

	/**
	 * 
	 * Method Name: getPersonReferralLink Method Description: This method will
	 * get data from REFERRAL_PERSON_LINK table. Cses47d
	 * 
	 * @param referralPersonLinkInDto
	 * @return List<ReferralPersonLinkOutDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ReferralPersonLinkOutDto> getPersonReferralLink(ReferralPersonLinkInDto referralPersonLinkInDto) {
		log.debug("Entering method getPersonReferralLink in ReferralPersonLinkDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getPersonReferralLink)
				.addScalar("idEvent", StandardBasicTypes.LONG).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("tsLastUpdate", StandardBasicTypes.STRING)
				.addScalar("indReferPersReferred", StandardBasicTypes.STRING)
				.setResultTransformer(Transformers.aliasToBean(ReferralPersonLinkOutDto.class)));
		sQLQuery1.setParameter("hI_ulIdPerson", referralPersonLinkInDto.getIdPerson());
		List<ReferralPersonLinkOutDto> referralPersonLinkOutDtos = (List<ReferralPersonLinkOutDto>) sQLQuery1.list();
		log.debug("Exiting method getPersonReferralLink in ReferralPersonLinkDaoImpl");
		return referralPersonLinkOutDtos;
	}
}
