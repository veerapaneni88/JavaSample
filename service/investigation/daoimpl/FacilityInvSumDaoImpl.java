package us.tx.state.dfps.service.investigation.daoimpl;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.facilityinvcnclsn.dto.FacilityAllegationInfoDto;
import us.tx.state.dfps.service.investigation.dao.FacilityInvSumDao;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Dao for
 * cinv68s Mar 16, 2018- 11:44:02 AM © 2017 Texas Department of Family and
 * Protective Services
 */
@Repository
public class FacilityInvSumDaoImpl implements FacilityInvSumDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	MessageSource messageSource;

	@Value("${FacilityInvSumDaoImpl.getContactDate}")
	private String getContactDateSql;

	@Value("${FacilityInvSumDaoImpl.getAllegationInfo}")
	private String getAllegationInfoSql;

	@Value("${FacilityInvSumDaoImpl.getAllegationType}")
	private String getAllegationTypeSql;

	private static final Logger log = Logger.getLogger("ServiceBusiness-FacilityInvSumDaoLog");

	public FacilityInvSumDaoImpl() {
		super();
	}

	/**
	 * Method Name: getContactDate Method Description: Retrieve the Date the
	 * first Request for Review contact occurred given a specific id_stage. DAM
	 * : CINVB8D
	 * 
	 * @return Date
	 */
	@Override
	public Date getContactDate(Long idStage) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getContactDateSql);

		query.setParameter("idStage", idStage);
		return (Date) query.uniqueResult();
	}

	/**
	 * Method Name: getAllegationInfo Method Description: This DAM retrieves a
	 * full row from the Allegation ,Facility_Injury, and Person tables. DAM :
	 * CLSC17D
	 * 
	 * @return FacilityAllegationInfoDto
	 */
	@Override
	public FacilityAllegationInfoDto getAllegationInfo(Long idStage) {

		Query query = sessionFactory.getCurrentSession().createSQLQuery(getAllegationInfoSql)
				.addScalar("idAllegation", StandardBasicTypes.LONG).addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("idAllegationStage", StandardBasicTypes.LONG).addScalar("idVictim", StandardBasicTypes.LONG)
				.addScalar("idAllegedPerpetrator", StandardBasicTypes.STRING)
				.addScalar("cdAllegIncidentStage", StandardBasicTypes.STRING)
				.addScalar("txtAllegDuration", StandardBasicTypes.STRING)
				.addScalar("cdAllegType", StandardBasicTypes.STRING)
				.addScalar("cdAllegDisposition", StandardBasicTypes.STRING)
				.addScalar("cdAllegSeverity", StandardBasicTypes.STRING)
				.addScalar("indAllegCancelHist", StandardBasicTypes.STRING)
				.addScalar("idFacilityInjury", StandardBasicTypes.LONG)
				.addScalar("cdFacilInjury", StandardBasicTypes.STRING)
				.addScalar("cdFacilInjuryBody", StandardBasicTypes.STRING)
				.addScalar("cdFacilInjurySide", StandardBasicTypes.STRING)
				.addScalar("txtFacilInjuryCmnts", StandardBasicTypes.STRING)
				.addScalar("cdFacilInjuryCause", StandardBasicTypes.STRING)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("cdPersonSex", StandardBasicTypes.STRING)
				.addScalar("nmPersonFull", StandardBasicTypes.STRING).addScalar("nbrPersonAge", StandardBasicTypes.LONG)
				.addScalar("dtPersonDeath", StandardBasicTypes.DATE).addScalar("dtPersonBirth", StandardBasicTypes.DATE)
				.addScalar("cdPersonReligion", StandardBasicTypes.STRING)
				.addScalar("cdPersonChar", StandardBasicTypes.STRING)
				.addScalar("indPersonDobApprox", StandardBasicTypes.STRING)
				.addScalar("cdPersonLivArr", StandardBasicTypes.STRING)
				.addScalar("cdPersGuardCnsrv", StandardBasicTypes.STRING)
				.addScalar("cdPersonStatus", StandardBasicTypes.STRING)
				.addScalar("cdPersonDeath", StandardBasicTypes.STRING)
				.addScalar("cdPersonMaritalStatus", StandardBasicTypes.STRING)
				.addScalar("txtPersonOccupation", StandardBasicTypes.STRING)
				.addScalar("cdPersonLanguage", StandardBasicTypes.STRING)
				.addScalar("cdPersonEthnicGroup", StandardBasicTypes.STRING)
				.addScalar("indPersCancelHist", StandardBasicTypes.STRING)
				.addScalar("nmPersonFirst", StandardBasicTypes.STRING)
				.addScalar("nmPersonMiddle", StandardBasicTypes.STRING)
				.addScalar("nmPersonLast", StandardBasicTypes.STRING)
				.addScalar("cdPersonSuffix", StandardBasicTypes.STRING)
				.setResultTransformer(Transformers.aliasToBean(FacilityAllegationInfoDto.class));
		query.setParameter("idStage", idStage);
		return (FacilityAllegationInfoDto) query.uniqueResult();
	}

	/**
	 * Method Name: getAllegationInfo Method Description: This DAO retrieves a
	 * list of ALLEGATION type
	 * 
	 * @return List<FacilityAllegationInfoDto>
	 */
	@Override
	public List<FacilityAllegationInfoDto> getAllegationType(Long idStage) {

		Query query = sessionFactory.getCurrentSession().createSQLQuery(getAllegationTypeSql)
				.addScalar("nmPersonFull", StandardBasicTypes.STRING)
				.addScalar("cdAllegType", StandardBasicTypes.STRING)
				.setResultTransformer(Transformers.aliasToBean(FacilityAllegationInfoDto.class));
		query.setParameter("idStage", idStage);
		return (List<FacilityAllegationInfoDto>) query.list();
	}

}
