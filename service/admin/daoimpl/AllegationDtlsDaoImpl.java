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
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.service.admin.dao.AllegationDtlsDao;
import us.tx.state.dfps.service.admin.dto.AllegationDtlsInDto;
import us.tx.state.dfps.service.admin.dto.AllegationDtlsOutDto;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.riskassesment.dto.AllegationNameDtlDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:AllegationDtlsDaoImpl Aug 6, 2017- 5:46:55 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@Repository
public class AllegationDtlsDaoImpl implements AllegationDtlsDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${AllegationDtlsDaoImpl.getAllegationDtls}")
	private transient String getAllegationDtlsSql;

	@Value("${AllegationDtlsDaoImpl.getAllegationNameDtls}")
	private transient String getAllegationNameDtlsSql;

	private static final Logger log = Logger.getLogger(AllegationDtlsDaoImpl.class);

	public AllegationDtlsDaoImpl() {
		super();
	}

	/**
	 * 
	 * Method Name: getAllegationDtls Method Description: Get data from
	 * Allegation table.
	 * 
	 * @param pInputDataRec
	 * @return List<AllegationDtlsOutDto> @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<AllegationDtlsOutDto> getAllegationDtls(AllegationDtlsInDto pInputDataRec) {
		log.debug("Entering method AllegationDtlsQUERYdam in AllegationDtlsDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getAllegationDtlsSql)
				.setResultTransformer(Transformers.aliasToBean(AllegationDtlsOutDto.class)));
		sQLQuery1.addScalar("sysNbrIdAllgtn", StandardBasicTypes.LONG);
		sQLQuery1.setParameter("hI_ulIdStage", pInputDataRec.getIdStage());
		List<AllegationDtlsOutDto> liCinvd3doDto = (List<AllegationDtlsOutDto>) sQLQuery1.list();
		if (TypeConvUtil.isNullOrEmpty(liCinvd3doDto)) {
			throw new DataNotFoundException(
					messageSource.getMessage("AllegationDtlsDaoImpl.not.found.ulIdStage", null, Locale.US));
		}
		log.debug("Exiting method AllegationDtlsQUERYdam in AllegationDtlsDaoImpl");
		return liCinvd3doDto;
	}

	/**
	 * 
	 * Method Name: getAllegationNameDtls Method Description: This method
	 * retrieves data from Allegation and Name Tables. CSES90D
	 * 
	 * @param idStage
	 * @return List<AllegationNameDtlDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<AllegationNameDtlDto> getAllegationNameDtls(Long idStage) {

		log.debug("Entering method getAllegationNameDtls in AllegationDtlsDaoImpl");
		SQLQuery sQLQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getAllegationNameDtlsSql)
				.addScalar("idAllegation", StandardBasicTypes.LONG).addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("idAllegationStage", StandardBasicTypes.LONG).addScalar("idVictim", StandardBasicTypes.LONG)
				.addScalar("idAllegedPerpetrator", StandardBasicTypes.LONG).addScalar("idCase", StandardBasicTypes.LONG)
				.addScalar("cdAllegedIncidentStage", StandardBasicTypes.STRING)
				.addScalar("txtAllegDuration", StandardBasicTypes.STRING)
				.addScalar("cdAllegeType", StandardBasicTypes.STRING)
				.addScalar("cdAlledgeDisposition", StandardBasicTypes.STRING)
				.addScalar("cdAllegSeverity", StandardBasicTypes.STRING)
				.addScalar("indAllegCancelHist", StandardBasicTypes.STRING)
				.addScalar("txtDispstnSeverity", StandardBasicTypes.STRING)
				.addScalar("indFatality", StandardBasicTypes.STRING).addScalar("personDeath", StandardBasicTypes.DATE)

				.addScalar("perpetratorNmFirst", StandardBasicTypes.STRING)
				.addScalar("perpetratorNmMiddle", StandardBasicTypes.STRING)
				.addScalar("perpetratorNmLast", StandardBasicTypes.STRING)
				.addScalar("perpetratorCdNmSuffix", StandardBasicTypes.STRING)

				.addScalar("victimNmFirst", StandardBasicTypes.STRING)
				.addScalar("victimNmMiddle", StandardBasicTypes.STRING)
				.addScalar("victimNmLast", StandardBasicTypes.STRING)
				.addScalar("victimCdNmSuffix", StandardBasicTypes.STRING)

				.setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(AllegationNameDtlDto.class));

		List<AllegationNameDtlDto> riskAssessmentFactorDtoList = (List<AllegationNameDtlDto>) sQLQuery.list();
		if (ObjectUtils.isEmpty(riskAssessmentFactorDtoList)) {
			throw new DataNotFoundException(
					messageSource.getMessage("AllegationDtls.not.found.idStage", null, Locale.US));
		}
		log.debug("Exiting method getAllegationNameDtls in AllegationDtlsDaoImpl");
		return riskAssessmentFactorDtoList;
	}
}
