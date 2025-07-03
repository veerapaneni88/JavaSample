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

import us.tx.state.dfps.service.admin.dao.StagePersonLinkPersonStgTypeDao;
import us.tx.state.dfps.service.admin.dto.StagePersonLinkPersonStgTypeInDto;
import us.tx.state.dfps.service.admin.dto.StagePersonLinkPersonStgTypeOutDto;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: This class
 * gets the person details using stage ID and stage person type. Aug 6, 2017-
 * 7:04:47 PM © 2017 Texas Department of Family and Protective Services
 */
@Repository
public class StagePersonLinkPersonStgTypeDaoImpl implements StagePersonLinkPersonStgTypeDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${StagePersonLinkPersonStgTypeDaoImpl.personDtls}")
	private transient String personDtls;

	private static final Logger log = Logger.getLogger(StagePersonLinkPersonStgTypeDaoImpl.class);

	public StagePersonLinkPersonStgTypeDaoImpl() {
		super();
	}

	/**
	 * 
	 * Method Name: getPersonDtls Method Description:This method will get data
	 * from Stage Person Link and Person tables.
	 * 
	 * @param pInputDataRec
	 * @return List<StagePersonLinkPersonStgTypeOutDto> @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<StagePersonLinkPersonStgTypeOutDto> getPersonDtls(StagePersonLinkPersonStgTypeInDto pInputDataRec) {
		log.debug("Entering method StagePersonLinkPersonStgTypeQUERYdam in StagePersonLinkPersonStgTypeDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(personDtls)
				.setResultTransformer(Transformers.aliasToBean(StagePersonLinkPersonStgTypeOutDto.class)));
		sQLQuery1.addScalar("idStagePerson", StandardBasicTypes.LONG);
		sQLQuery1.addScalar("tsLastUpdate", StandardBasicTypes.TIMESTAMP);
		sQLQuery1.addScalar("idStage", StandardBasicTypes.LONG);
		sQLQuery1.addScalar("idPerson", StandardBasicTypes.LONG);
		sQLQuery1.addScalar("cdStagePersRole", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("indStagePersInLaw", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("cdStagePersType", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("indCdStagePersSearch", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("stagePersNotes", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("dtStagePersLink", StandardBasicTypes.TIMESTAMP);
		sQLQuery1.addScalar("cdStagePersRelInt", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("indStagePersReporter", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("indStagePersEmpNew", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("personSex", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("nmPersonFull", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("indPersCancelHist", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("personAge", StandardBasicTypes.SHORT);
		sQLQuery1.addScalar("dtPersonDeath", StandardBasicTypes.TIMESTAMP);
		sQLQuery1.addScalar("dtPersonBirth", StandardBasicTypes.TIMESTAMP);
		sQLQuery1.addScalar("cdPersonReligion", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("cdPersonChar", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("indPersonDobApprox", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("cdPersonLivArr", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("cdPersGuardCnsrv", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("cdPersonStatus", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("cdPersonDeath", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("cdPersonMaritalStatus", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("occupation", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("cdPersonLanguage", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("cdPersonEthnicGroup", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("nmPersonFirst", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("nmPersonLast", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("cdNameSuffix", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("cdDeathRsnCps", StandardBasicTypes.STRING);
		sQLQuery1.setParameter("hI_ulIdStage", pInputDataRec.getIdStage());
		sQLQuery1.setParameter("hI_szCdStagePersType", pInputDataRec.getCdStagePersType());
		List<StagePersonLinkPersonStgTypeOutDto> liClsc18doDto = (List<StagePersonLinkPersonStgTypeOutDto>) sQLQuery1
				.list();
		if (TypeConvUtil.isNullOrEmpty(liClsc18doDto)) {
			throw new DataNotFoundException(
					messageSource.getMessage("Clsc18dDaoImpl.not.found.ulIdStage", null, Locale.US));
		}
		log.debug("Exiting method StagePersonLinkPersonStgTypeQUERYdam in StagePersonLinkPersonStgTypeDaoImpl");
		return liClsc18doDto;
	}
}
