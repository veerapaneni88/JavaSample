package us.tx.state.dfps.service.populateform.daoImpl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.person.dto.AllegationDto;
import us.tx.state.dfps.service.person.dto.AllegationWithVicDto;
import us.tx.state.dfps.service.person.dto.PersonEmailDto;
import us.tx.state.dfps.service.person.dto.PersonGenderSpanishDto;
import us.tx.state.dfps.service.populateform.dao.PopulateFormDao;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name:CINV38S Class
 * Description: This Method implements PopulateFormDao. This is used to retrieve
 * get form details from database. Jan 11, 2018 -10:10:30 AM © 2017 Texas
 * Department of Family and Protective Services
 */

@Repository
public class PopulateFormDaoImpl implements PopulateFormDao {

	private static final Logger log = Logger.getLogger(PopulateFormDaoImpl.class);
	@Autowired
	private SessionFactory sessionFactory;

	@Value("${PopulateFormDaoImpl.getAllegationWithVictims}")
	private String getAllegationWithVictimsSql;

	@Value("${PopulateFormDaoImpl.getUniqueDispositions}")
	private String getUniqueDispositionsSql;

	@Value("${PopulateFormDaoImpl.getUniqueAllegations}")
	private String getUniqueAllegationsSql;

	@Value("${PopulateFormDaoImpl.getDispositionFromAllegation}")
	private String getDispositionFromAllegationSql;

	@Value("${PopulateFormDaoImpl.getSpanishGenderTrans}")
	private String getSpanishGenderTransSql;

	@Value("${PopulateFormDaoImpl.getEmailReturned}")
	private String getEmailReturnedSql;

	/**
	 * This Dao is used to return all of the allegations along with the victims
	 * for a given id_stage.
	 * 
	 * Service Name - CINV38S, DAM Name - CLSC05D
	 * 
	 * @param idStage
	 * @return List<AllegationWithVicDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<AllegationWithVicDto> getAllegationById(Long idStage) {
		List<AllegationWithVicDto> allegationWithVicDto = new ArrayList<AllegationWithVicDto>();
		allegationWithVicDto = (ArrayList<AllegationWithVicDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getAllegationWithVictimsSql).setParameter("idStage", idStage))
						.addScalar("bNmPersonFull", StandardBasicTypes.STRING)
						.addScalar("cNmPersonFull", StandardBasicTypes.STRING)
						.addScalar("aIdVictim", StandardBasicTypes.LONG)
						.addScalar("aCdAllegType", StandardBasicTypes.STRING)
						.addScalar("aIdAllegedPerpetrator", StandardBasicTypes.LONG)
						.addScalar("aCdAllegDisposition", StandardBasicTypes.STRING)
						.addScalar("aCdAllegIncidentStage", StandardBasicTypes.STRING)
						.addScalar("aIdAllegation", StandardBasicTypes.LONG)
						.addScalar("aDtLastUpdate", StandardBasicTypes.DATE)
						.addScalar("fCdFacilAllegClss", StandardBasicTypes.STRING)
						.addScalar("aCdAllegSeverity", StandardBasicTypes.STRING)
						.addScalar("aIdAllegationStage", StandardBasicTypes.LONG)
						.addScalar("bNmPersonFirst", StandardBasicTypes.STRING)
						.addScalar("bNmPersonLast", StandardBasicTypes.STRING)
						.addScalar("bNmPersonMiddle", StandardBasicTypes.STRING)
						.addScalar("bCdPersonSuffix", StandardBasicTypes.STRING)
						.addScalar("cNmPersonFirst", StandardBasicTypes.STRING)
						.addScalar("cNmPersonLast", StandardBasicTypes.STRING)
						.addScalar("cNmPersonMiddle", StandardBasicTypes.STRING)
						.addScalar("cCdPersonSuffix", StandardBasicTypes.STRING)
						.addScalar("aIndFatality", StandardBasicTypes.STRING)
						.addScalar("fCdFacilAllegSrc", StandardBasicTypes.STRING)
						.addScalar("fCdFacilAllegSrcSupr", StandardBasicTypes.STRING)
						.addScalar("bDtPersonDeath", StandardBasicTypes.DATE)
						.setResultTransformer(Transformers.aliasToBean(AllegationWithVicDto.class)).list();

		return allegationWithVicDto;
	}

	/**
	 * This Dao is used to retrieve all of the unique dispositions for a given
	 * ulIdAllegationStage and ulIdAllegedPerpetrator.
	 * 
	 * Service Name - CINV38S, DAM Name - CLSSB0D
	 * 
	 * @param idStage,
	 *            idPerpetrator
	 * @return AllegationWithVicDto
	 */
	
	// Changed the Return Type to List - Warranty Defect 10787

	@Override
	public List<AllegationWithVicDto> getUqDispositonById(Long idStage, Long idPerson) {
		
		List<AllegationWithVicDto> allegationWithVicDto = new ArrayList<AllegationWithVicDto>();
		allegationWithVicDto = (List<AllegationWithVicDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getUniqueDispositionsSql).setParameter("idStage", idStage)
				.setParameter("idPerson", idPerson)).addScalar("aIdAllegationStage", StandardBasicTypes.LONG)
						.addScalar("aCdAllegDisposition", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(AllegationWithVicDto.class)).list();

		return allegationWithVicDto;
	}

	/**
	 * This Dao is used to retrieve all of the unique allegations for a given
	 * id_stage.
	 * 
	 * Service Name - CINV38S, DAM Name - CLSSAAD
	 * 
	 * @param idStage,
	 *            idPerpetrator
	 * @return AllegationDto
	 */

	@SuppressWarnings("unchecked")
	@Override
	public List<AllegationDto> getUqAllegationById(Long idStage, Long idPerson) {
		List<AllegationDto> allegationDto = new ArrayList<AllegationDto>();
		allegationDto = (List<AllegationDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getUniqueAllegationsSql).setParameter("idStage", idStage)
				.setParameter("idPerson", idPerson)).addScalar("idAllegationStage", StandardBasicTypes.LONG)
						.addScalar("cdAllegType", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(AllegationDto.class)).list();
		return allegationDto;
	}

	/**
	 * This Dao is used to retrieves disposition from ALLEGATION in join with
	 * STAGE PERSON LINK and STAGE for a given ID_PERSON and ID_STAGE. DISTINCT.
	 * 
	 * Service Name - CINV38S, DAM Name - CLSCE7D
	 * 
	 * @param idStage,
	 *            idPerpetrator
	 * @return AllegationWithVicDto
	 */
	// Changed the Return Type to List - Warranty Defect 10787
	@Override
	public List<AllegationWithVicDto> getUqStageById(Long idStage, Long idPerson) {
		
		List<AllegationWithVicDto> allegationWithVicDto=new ArrayList<AllegationWithVicDto>();
		allegationWithVicDto = (List<AllegationWithVicDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getDispositionFromAllegationSql).setParameter("idStage", idStage)
				.setParameter("idPerson", idPerson)).addScalar("algCdAllegDisposition", StandardBasicTypes.STRING)
						.addScalar("algIdAllegedPerpetrator", StandardBasicTypes.LONG)
						.addScalar("cdOverAllDisposition", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(AllegationWithVicDto.class)).list();

		return allegationWithVicDto;
	}

	/**
	 * This Dao is used to retrieve gender for spanish translation.
	 * 
	 * Service Name - CINV38S, DAM Name - CINV81D
	 * 
	 * @param idPerson
	 * @return PersonGenderSpanishDto
	 */

	@Override
	public PersonGenderSpanishDto isSpanGender(Long idPerson) {
		PersonGenderSpanishDto personGenderSpanishDto = new PersonGenderSpanishDto();
		personGenderSpanishDto = (PersonGenderSpanishDto) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getSpanishGenderTransSql).setParameter("idPerson", idPerson))
						.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
						.addScalar("cdPersonSex", StandardBasicTypes.STRING)
						.addScalar("nmPersonFull", StandardBasicTypes.STRING)
						.addScalar("nbrPersonAge", StandardBasicTypes.LONG)
						.addScalar("dtPersonDeath", StandardBasicTypes.DATE)
						.addScalar("dtPersonBirth", StandardBasicTypes.DATE)
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
						.setResultTransformer(Transformers.aliasToBean(PersonGenderSpanishDto.class)).uniqueResult();

		return personGenderSpanishDto;
	}

	/**
	 * This Dao is used to return email of letter recipient.
	 * 
	 * Service Name - CINV38S, DAM Name - CSES0BD
	 * 
	 * @param idPerson
	 * @return List<PersonEmailDto>
	 */

	@SuppressWarnings("unchecked")
	@Override
	public List<PersonEmailDto> returnEmailById(Long idPerson) {
		List<PersonEmailDto> personEmailDto = new ArrayList<PersonEmailDto>();
		personEmailDto = (ArrayList<PersonEmailDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getEmailReturnedSql).setParameter("idPerson", idPerson))
						.addScalar("idPersonEmail", StandardBasicTypes.LONG)
						.addScalar("cdType", StandardBasicTypes.STRING)
						.addScalar("indPrimary", StandardBasicTypes.STRING)
						.addScalar("indInvalid", StandardBasicTypes.STRING)
						.addScalar("txtEmail", StandardBasicTypes.STRING).addScalar("dtStart", StandardBasicTypes.DATE)
						.addScalar("dtEnd", StandardBasicTypes.DATE).addScalar("txtComments", StandardBasicTypes.STRING)
						.addScalar("dtCreated", StandardBasicTypes.DATE)
						.addScalar("idCreatedPerson", StandardBasicTypes.LONG)
						.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
						.addScalar("idLastUpdatePerson", StandardBasicTypes.LONG)

						.setResultTransformer(Transformers.aliasToBean(PersonEmailDto.class)).list();

		return personEmailDto;
	}
}
