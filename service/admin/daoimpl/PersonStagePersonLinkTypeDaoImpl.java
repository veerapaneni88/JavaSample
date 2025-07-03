package us.tx.state.dfps.service.admin.daoimpl;

import java.util.ArrayList;
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

import us.tx.state.dfps.service.admin.dao.PersonStagePersonLinkTypeDao;
import us.tx.state.dfps.service.admin.dto.PersonStagePersonLinkTypeInDto;
import us.tx.state.dfps.service.admin.dto.PersonStagePersonLinkTypeOutDto;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<DAO Impl
 * for fetching Person details> Aug 4, 2017- 12:00:38 PM © 2017 Texas Department
 * of Family and Protective Services
 */
@Repository
public class PersonStagePersonLinkTypeDaoImpl implements PersonStagePersonLinkTypeDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	private static final Logger log = Logger.getLogger(PersonStagePersonLinkTypeDaoImpl.class);

	@Value("${PersonStagePersonLinkTypeDaoImpl.getPersonDetails}")
	private transient String getPersonDetails;

	public PersonStagePersonLinkTypeDaoImpl() {
		super();
	}

	/**
	 * 
	 * Method Name: getPersonDetails Method Description: This method will get
	 * data from PERSON and STAGE_PERSON_LINK table.
	 * 
	 * @param pInputDataRec
	 * @return List<PersonStagePersonLinkTypeOutDto> @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PersonStagePersonLinkTypeOutDto> getPersonDetails(PersonStagePersonLinkTypeInDto pInputDataRec) {
		log.debug("Entering method PersonStagePersonLinkTypeQUERYdam in PersonStagePersonLinkTypeDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getPersonDetails)
				.setResultTransformer(Transformers.aliasToBean(PersonStagePersonLinkTypeOutDto.class)));
		sQLQuery1.addScalar("nmPersonFull", StandardBasicTypes.STRING)
				.addScalar("cdPersonSuffix", StandardBasicTypes.STRING).addScalar("personAge", StandardBasicTypes.SHORT)
				.addScalar("personSex", StandardBasicTypes.STRING).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("indPersonDobApprox", StandardBasicTypes.STRING)
				.addScalar("dtPersonBirth", StandardBasicTypes.TIMESTAMP)
				.addScalar("cdPersonLivArr", StandardBasicTypes.STRING)
				.addScalar("cdPersonChar", StandardBasicTypes.STRING)
				.addScalar("cdPersonMaritalStatus", StandardBasicTypes.STRING)
				.addScalar("dtPersonDeath", StandardBasicTypes.TIMESTAMP)
				.addScalar("nmPersonFirst", StandardBasicTypes.STRING)
				.addScalar("nmPersonLast", StandardBasicTypes.STRING)
				.addScalar("indCdStagePersSearch", StandardBasicTypes.STRING)
				.addScalar("cdStagePersType", StandardBasicTypes.STRING)
				.addScalar("cdStagePersRelInt", StandardBasicTypes.STRING)
				.addScalar("cdStagePersRole", StandardBasicTypes.STRING)
				.addScalar("indStagePersReporter", StandardBasicTypes.STRING)
				.addScalar("tsLastUpdate", StandardBasicTypes.TIMESTAMP)
				.addScalar("idStagePerson", StandardBasicTypes.LONG)
				.addScalar("cdEthnicGroup", StandardBasicTypes.STRING)
				.setParameter("hI_ulIdStage", pInputDataRec.getIdStage())
				.setParameter("hI_szCdStagePersType", pInputDataRec.getCdStagePersType());
		List<PersonStagePersonLinkTypeOutDto> liCinv34doDto = new ArrayList<>();
		liCinv34doDto = (List<PersonStagePersonLinkTypeOutDto>) sQLQuery1.list();
		if (TypeConvUtil.isNullOrEmpty(liCinv34doDto)) {
			throw new DataNotFoundException(
					messageSource.getMessage("Cinv34dDaoImpl.not.found.person", null, Locale.US));
		}
		log.debug("Exiting method PersonStagePersonLinkTypeQUERYdam in PersonStagePersonLinkTypeDaoImpl");
		return liCinv34doDto;
	}
}
