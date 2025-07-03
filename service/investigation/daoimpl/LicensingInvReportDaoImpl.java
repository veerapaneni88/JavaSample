package us.tx.state.dfps.service.investigation.daoimpl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.investigation.dao.LicensingInvReportDao;
import us.tx.state.dfps.service.investigation.dto.LicInvRepPrincipalsDto;
import us.tx.state.dfps.service.person.dto.PersonListAlleDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Makes
 * database calls and sends data to service Apr 13, 2018- 12:01:07 PM © 2017
 * Texas Department of Family and Protective Services
 */
@Repository
public class LicensingInvReportDaoImpl implements LicensingInvReportDao {

	@Autowired
	SessionFactory sessionFactory;

	@Value("${LicensingInvReportDaoImpl.getPrincipals}")
	private transient String getPrincipalsSql;

	@Value("${LicensingInvReportDaoImpl.getPersonAllegationHist}")
	private transient String getPersonAllegationHistSql;

	@Value("${LicensingInvReportDaoImpl.getAllVicitimAllegationHist}")
	private transient String getAllVicitimAllegationHistSql;

	/**
	 * Method Name: getPrincipals Method Description: Principal Information
	 * retrieval (DAM: CLSCE1D)
	 * 
	 * @param idStage
	 * @return List<LicInvSumPrincipalsDto>
	 */
	@Override
	public List<LicInvRepPrincipalsDto> getPrincipals(Long idStage) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getPrincipalsSql)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("nmPersonFull", StandardBasicTypes.STRING)
				.addScalar("nbrPersonAge", StandardBasicTypes.LONG).addScalar("dtPersonBirth", StandardBasicTypes.DATE)
				.addScalar("cdPersonSex", StandardBasicTypes.STRING).addScalar("dtPersonDeath", StandardBasicTypes.DATE)
				.addScalar("cdPersonDeath", StandardBasicTypes.STRING)
				.addScalar("cdPersonLanguage", StandardBasicTypes.STRING)
				.addScalar("cdPersonEthnicGroup", StandardBasicTypes.STRING)
				.addScalar("cdPersonMaritalStatus", StandardBasicTypes.STRING)
				.addScalar("race", StandardBasicTypes.STRING).addScalar("cdEthnicity", StandardBasicTypes.STRING)
				.addScalar("idStage", StandardBasicTypes.LONG).addScalar("cdStagePersRole", StandardBasicTypes.STRING)
				.addScalar("txtStagePersNotes", StandardBasicTypes.STRING)
				.addScalar("cdStagePersRelInt", StandardBasicTypes.STRING)
				.addScalar("indNmStage", StandardBasicTypes.STRING).addScalar("cdPersonChar", StandardBasicTypes.STRING)
				.addScalar("indPersonDobApprox", StandardBasicTypes.STRING)
				.addScalar("cdPersonLivArr", StandardBasicTypes.STRING)
				.addScalar("cdPersGuardCnsrv", StandardBasicTypes.STRING)
				.addScalar("cdPersonStatus", StandardBasicTypes.STRING)
				.addScalar("txtPersonOccupation", StandardBasicTypes.STRING)
				.addScalar("indPersCancelHist", StandardBasicTypes.STRING)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("nmPersonFirst", StandardBasicTypes.STRING)
				.addScalar("nmPersonLast", StandardBasicTypes.STRING)
				.addScalar("nmPersonMiddle", StandardBasicTypes.STRING)
				.addScalar("cdPersonSuffix", StandardBasicTypes.STRING)
				.addScalar("addrPersonStLn1", StandardBasicTypes.STRING)
				.addScalar("addrPersonCity", StandardBasicTypes.STRING)
				.addScalar("cdPersonState", StandardBasicTypes.STRING)
				.addScalar("nbrPersonIdNumber", StandardBasicTypes.STRING)
				.addScalar("addrPersonZip", StandardBasicTypes.STRING).setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(LicInvRepPrincipalsDto.class));
		List<LicInvRepPrincipalsDto> resultList = (List<LicInvRepPrincipalsDto>) query.list();
		return resultList;
	}

	/**
	 * Method Name: getPersonAllegationHistDtls Method Description: This method
	 * is retrieve all person id's in the alleged_perpetrator column on the
	 * allegation_history window for each of the allegations on the allegation
	 * table for the stage_id passed in if the case program is AFC. DAM Name:
	 * CLSC89D
	 * 
	 * @param idStage
	 * @return List<PersonListAlleDto>
	 */
	@SuppressWarnings("unchecked")
	public List<PersonListAlleDto> getPersonAllegationHistDtls(Long idStage) {
		List<PersonListAlleDto> personListAlle = new ArrayList<PersonListAlleDto>();
		personListAlle = (List<PersonListAlleDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getPersonAllegationHistSql).setParameter("idStage", idStage))
						.addScalar("idAllePerpetrator", StandardBasicTypes.LONG)
						.addScalar("personFull", StandardBasicTypes.STRING)
						.addScalar("nmPersonFirst", StandardBasicTypes.STRING)
						.addScalar("nmPersonMiddle", StandardBasicTypes.STRING)
						.addScalar("nmPersonLast", StandardBasicTypes.STRING)
						.addScalar("cdPersonSuffix", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(PersonListAlleDto.class)).list();
		return personListAlle;
	}

	/**
	 * Method Name: getAllVicitimAllegationHist. Method Description: This method
	 * will retrieve all victims for a case from the ALLEGATION_HISTORY table.
	 * DAM Name: CLSC90D
	 * 
	 * @param idStage
	 * @return List<PersonListAlleDto>
	 */
	@SuppressWarnings("unchecked")
	public List<PersonListAlleDto> getAllVicitimAllegationHist(Long idStage) {
		List<PersonListAlleDto> personListAlle = new ArrayList<PersonListAlleDto>();
		personListAlle = (List<PersonListAlleDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getAllVicitimAllegationHistSql).setParameter("idStage", idStage))
						.addScalar("personFull", StandardBasicTypes.STRING)
						.addScalar("nmPersonFirst", StandardBasicTypes.STRING)
						.addScalar("nmPersonLast", StandardBasicTypes.STRING)
						.addScalar("nmPersonMiddle", StandardBasicTypes.STRING)
						.addScalar("cdPersonSuffix", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(PersonListAlleDto.class)).list();
		return personListAlle;
	}
}
