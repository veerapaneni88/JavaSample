package us.tx.state.dfps.service.conservatorship.daoimpl;

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

import us.tx.state.dfps.service.conservatorship.dao.NameStagePersonLinkPersonDao;
import us.tx.state.dfps.service.cvs.dto.NameStagePersonLinkPersonInDto;
import us.tx.state.dfps.service.cvs.dto.NameStagePersonLinkPersonOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:Implementation for NameStagePersonLinkPersonDaoImpl Aug 2, 2017-
 * 8:05:25 PM © 2017 Texas Department of Family and Protective Services
 */
@Repository
public class NameStagePersonLinkPersonDaoImpl implements NameStagePersonLinkPersonDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${NameStagePersonLinkPersonDaoImpl.getStagePersonLinkDetails}")
	private transient String getStagePersonLinkDetails;

	private static final Logger log = Logger.getLogger(NameStagePersonLinkPersonDaoImpl.class);

	/**
	 * 
	 * Method Name: getStagePersonLinkDetails Method Description: This method
	 * will get data from NAME, STAGE_PERSON_LINK and PERSON P tables.
	 * 
	 * @param nameStagePersonLinkPersonInDto
	 * @return List<NameStagePersonLinkPersonOutDto> @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<NameStagePersonLinkPersonOutDto> getStagePersonLinkDetails(
			NameStagePersonLinkPersonInDto nameStagePersonLinkPersonInDto) {
		log.debug("Entering method getStagePersonLinkDetails in NameStagePersonLinkPersonDaoImpl");

		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getStagePersonLinkDetails)
				.addScalar("idName", StandardBasicTypes.LONG).addScalar("tsLastUpdate", StandardBasicTypes.STRING)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("indNameInvalid", StandardBasicTypes.STRING)
				.addScalar("nameFirst", StandardBasicTypes.STRING).addScalar("nameMiddle", StandardBasicTypes.STRING)
				.addScalar("nameLast", StandardBasicTypes.STRING).addScalar("indNamePrimary", StandardBasicTypes.STRING)
				.addScalar("cdNameSuffix", StandardBasicTypes.STRING)
				.addScalar("dtNameStartDate", StandardBasicTypes.DATE)
				.addScalar("dtNameEndDate", StandardBasicTypes.DATE).addScalar("idStagePerson", StandardBasicTypes.LONG)
				.addScalar("idStage", StandardBasicTypes.LONG).addScalar("cdStagePersRole", StandardBasicTypes.STRING)
				.addScalar("indStagePersInLaw", StandardBasicTypes.STRING)
				.addScalar("cdStagePersType", StandardBasicTypes.STRING)
				.addScalar("cdStagePersSearchInd", StandardBasicTypes.STRING)
				.addScalar("txtStagePersNotes", StandardBasicTypes.STRING)
				.addScalar("dtStagePersLink", StandardBasicTypes.DATE)
				.addScalar("cdStagePersRelInt", StandardBasicTypes.STRING)
				.addScalar("indStagePersReporter", StandardBasicTypes.STRING)
				.addScalar("indStagePersEmpNew", StandardBasicTypes.STRING)
				.addScalar("cdPersonSex", StandardBasicTypes.STRING)
				.addScalar("nmPersonFull", StandardBasicTypes.STRING)
				.addScalar("nbrPersonAge", StandardBasicTypes.SHORT).addScalar("dtPersonDeath", StandardBasicTypes.DATE)
				.addScalar("dtPersonBirth", StandardBasicTypes.DATE)
				.addScalar("cdPersonReligion", StandardBasicTypes.STRING)
				.addScalar("cdPersonChar", StandardBasicTypes.STRING)
				.addScalar("indPersonDobApprox", StandardBasicTypes.STRING)
				.addScalar("cdPersonLivArr", StandardBasicTypes.STRING)
				.addScalar("cdPersGuardCnsrv", StandardBasicTypes.STRING)
				.addScalar("cdPersonStatus", StandardBasicTypes.STRING)
				.addScalar("cdPersonDeath", StandardBasicTypes.STRING)
				.addScalar("cdPersonMaritalStatus", StandardBasicTypes.STRING)
				.addScalar("txtOccupation", StandardBasicTypes.STRING)
				.addScalar("cdPersonLanguage", StandardBasicTypes.STRING)
				.addScalar("cdPersonEthnicGroup", StandardBasicTypes.STRING)
				.addScalar("indPersCancelHist", StandardBasicTypes.STRING)
				.setParameter("idStage", nameStagePersonLinkPersonInDto.getIdStage())
				.setParameter("cdStagePersType", nameStagePersonLinkPersonInDto.getCdStagePersType())
				.setResultTransformer(Transformers.aliasToBean(NameStagePersonLinkPersonOutDto.class)));
		List<NameStagePersonLinkPersonOutDto> liClsc10doDto = (List<NameStagePersonLinkPersonOutDto>) sQLQuery1.list();
		log.debug("Exiting method getStagePersonLinkDetails in NameStagePersonLinkPersonDaoImpl");
		return liClsc10doDto;
	}
}
