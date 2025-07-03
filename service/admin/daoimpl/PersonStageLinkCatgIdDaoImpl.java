package us.tx.state.dfps.service.admin.daoimpl;

import java.util.*;
import java.util.concurrent.TimeUnit;


import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.*;
import us.tx.state.dfps.mobile.IncomingPersonMpsDto;
import us.tx.state.dfps.mobile.PersonRaceMpsDto;
import us.tx.state.dfps.service.admin.dao.PersonStageLinkCatgIdDao;
import us.tx.state.dfps.service.admin.dto.*;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.common.util.mobile.MobileUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.person.dto.EditPersonAddressDto;
import us.tx.state.dfps.service.person.dto.PersonIdDto;

/**
 *
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This dao
 * will update all tables associated with the Investigation Person Detail window
 * Aug 12, 2017- 10:28:19 AM © 2017 Texas Department of Family and Protective
 * Services
 */
@Repository
public class PersonStageLinkCatgIdDaoImpl implements PersonStageLinkCatgIdDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${PersonStageLinkCatgIdDaoImpl.insertPersonRecord}")
	private String insertPersonRecord;

	@Value("${PersonStageLinkCatgIdDaoImpl.insertStagePersonLink}")
	private String insertStagePersonLink;

	@Value("${PersonStageLinkCatgIdDaoImpl.insertPersonCategory}")
	private String insertPersonCategory;

	@Value("${PersonStageLinkCatgIdDaoImpl.getPersonId}")
	private String getPersonId;

	@Value("${PersonStageLinkCatgIdDaoImpl.insertPersonID}")
	private String insertPersonID;

	@Value("${PersonStageLinkCatgIdDaoImpl.updatePersonRecord}")
	private String updatePersonRecord;

	@Value("${PersonStageLinkCatgIdDaoImpl.updateStagePersonLink}")
	private String updateStagePersonLink;

	@Value("${PersonStageLinkCatgIdDaoImpl.updatePersonStatus}")
	private String updatePersonStatus;

	@Value("${PersonStageLinkCatgIdDaoImpl.deleteStagePersonLink}")
	private String deleteStagePersonLink;

	@Value("${PersonStageLinkCatgIdDaoImpl.updateWindowModePerson}")
	private String updateWindowModePerson;

	@Value("${PersonStageLinkCatgIdDaoImpl.insertStagePersonLinkWindowLower}")
	private String insertStagePersonLinkWindowLower;

	@Value("${PersonStageLinkCatgIdDaoImpl.updatePersonWindowLower}")
	private String updatePersonWindowLower;

	@Value("${PersonStageLinkCatgIdDaoImpl.getPersonCategoryCount}")
	private String getPersonCategoryCount;

	@Value("${PersonStageLinkCatgIdDaoImpl.insertWindowPersonCategory}")
	private String insertWindowPersonCategory;

	@Value("${PersonStageLinkCatgIdDaoImpl.getIdPersonId}")
	private String getIdPerosnId;

	@Value("${PersonStageLinkCatgIdDaoImpl.getPersonIdForMPS}")
	private String getPersonIdForMPS;

	@Value("${PersonStageLinkCatgIdDaoImpl.insertPersonRecordForMPS}")
	private String insertPersonRecordForMPS;

	@Value("${PersonStageLinkCatgIdDaoImpl.deletePersonRecordForMPS}")
	private String deletePersonRecordForMPS;

	@Value("${PersonStageLinkCatgIdDaoImpl.updatePersonAddressForMPS}")
	private String updatePersonAddressForMPS;

	@Value("${PersonStageLinkCatgIdDaoImpl.deletePersonRaceRecordForMPS}")
	private String deletePersonRaceRecordForMPS;

	@Value("${PersonStageLinkCatgIdDaoImpl.updatePersonRecordForMPS}")
	private String updatePersonRecordForMPS;

	@Value("${PersonStageLinkCatgIdDaoImpl.updateMPSPersonUsedInd}")
	private String updateMPSPersonUsedInd;

	@Value("${PersonDaoImpl.getPersonRaceMps}")
	private String getPersonRaceMPs;


	private static final Logger log = Logger.getLogger(PersonStageLinkCatgIdDaoImpl.class);
	@Autowired
	private MobileUtil mobileUtil;

	public PersonStageLinkCatgIdDaoImpl() {
		super();
	}

	/**
	 *
	 * Method Name: updateInvestigationPersonDetail Method Description:updates
	 * all table related with investigation person. Cinv41d
	 *
	 * @param personStageLinkCatgIdInDto
	 * @return PersonStageLinkCatgIdOutDto
	 */
	@Override
	public PersonStageLinkCatgIdOutDto updateInvestigationPersonDetail(
			PersonStageLinkCatgIdInDto personStageLinkCatgIdInDto) {
		log.debug("Entering method updateInvestigationPersonDetail in PersonStageLinkCatgIdDaoImpl");
		PersonStageLinkCatgIdOutDto personStageLinkCatgIdOutDto = new PersonStageLinkCatgIdOutDto();
		switch (personStageLinkCatgIdInDto.getReqFuncCd()) {
			case ServiceConstants.REQ_FUNC_CD_ADD:
				personStageLinkCatgIdOutDto = insertRecords(personStageLinkCatgIdInDto, personStageLinkCatgIdOutDto);
				break;
			case ServiceConstants.REQ_FUNC_CD_UPDATE:
				personStageLinkCatgIdOutDto = updateRecords(personStageLinkCatgIdInDto, personStageLinkCatgIdOutDto);
				break;
			case ServiceConstants.REQ_FUNC_CD_DELETE:
					personStageLinkCatgIdOutDto = deleteRecords(personStageLinkCatgIdInDto, personStageLinkCatgIdOutDto);
				break;
			case ServiceConstants.WINDOW_MODE_LOWER:
				personStageLinkCatgIdOutDto = windowModeLower(personStageLinkCatgIdInDto, personStageLinkCatgIdOutDto);
				break;
			case ServiceConstants.WINDOW_MODE_PERSON:
				personStageLinkCatgIdOutDto = windowModePerson(personStageLinkCatgIdInDto, personStageLinkCatgIdOutDto);
				break;
			case ServiceConstants.REQ_FUNC_CD_NO_ACTION:
				// return ARC_SUCCESS;
				break;
		}
		log.debug("Exiting method updateInvestigationPersonDetail in PersonStageLinkCatgIdDaoImpl");
		return personStageLinkCatgIdOutDto;
	}



	/**
	 * Method Name: updateRecords Method Description: Always update PERSON and
	 * STAGE PERSON LINK tables. Cinv41d
	 *
	 * @param personStageLinkCatgIdInDto,personStageLinkCatgIdOutDto
	 * @return PersonStageLinkCatgIdOutDto
	 */
	private PersonStageLinkCatgIdOutDto updateRecords(PersonStageLinkCatgIdInDto personStageLinkCatgIdInDto,
													  PersonStageLinkCatgIdOutDto personStageLinkCatgIdOutDto) {
		if (null != personStageLinkCatgIdInDto) {
			updatePerson(personStageLinkCatgIdInDto);
			updateStagePerson(personStageLinkCatgIdInDto);
			personStageLinkCatgIdOutDto.setIdPerson(personStageLinkCatgIdInDto.getIdPerson());
		}
		return personStageLinkCatgIdOutDto;
	}

	/**
	 * Method Name: deleteRecords Method Description: Always Delete from STAGE
	 * PERSON LINK, Update the Status on the PERSON table.
	 *
	 * @param pInputDataRec,objCinv41doDto
	 * @return Cinv41doDto
	 */
	private PersonStageLinkCatgIdOutDto deleteRecords(PersonStageLinkCatgIdInDto pInputDataRec,
													  PersonStageLinkCatgIdOutDto objCinv41doDto) {
		if (pInputDataRec != null) {
			updatePersonStatus(pInputDataRec);
			deleteStagePersonLink(pInputDataRec);
			objCinv41doDto.setIdPerson(pInputDataRec.getIdPerson());
		}
		return objCinv41doDto;
	}

	/**
	 * Method Name: windowModePerson Method Description: A full row update of
	 * the PERSON TABLE
	 *
	 * @param pInputDataRec,objCinv41doDto
	 * @return Cinv41doDto
	 */
	private PersonStageLinkCatgIdOutDto windowModePerson(PersonStageLinkCatgIdInDto pInputDataRec,
														 PersonStageLinkCatgIdOutDto objCinv41doDto) {
		SQLQuery sQLQuery = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(updateWindowModePerson)
				.setParameter("hI_szTxtOccupation", pInputDataRec.getOccupation())
				.setParameter("hI_bIndEducationPortfolio", pInputDataRec.getIndEducationPortfolio())
				.setParameter("hI_szCdTribeEligible", pInputDataRec.getCdTribeEligible())
				.setParameter("hI_szCdDeathCause", pInputDataRec.getCdDeathCause())
				.setParameter("hI_szTxtFatalityDetails", pInputDataRec.getFatalityDetails())
				.setParameter("hI_szNmPersonFull", pInputDataRec.getNmPersonFull())
				.setParameter("hI_szCdPersonEthnicGroup", pInputDataRec.getCdPersonEthnicGroup())
				.setParameter("hI_CdPersonStatus", pInputDataRec.getCdPersonStatus())
				.setParameter("hI_szCdMannerDeath", pInputDataRec.getCdMannerDeath())
				.setParameter("hI_bIndPersonDobApprox", pInputDataRec.getIndPersonDobApprox())
				.setParameter("hI_szCdPersonLanguage", pInputDataRec.getCdPersonLanguage())
				.setParameter("hI_tsSysTsLastUpdate2", pInputDataRec.getTsSysTsLastUpdate2())
				.setParameter("hI_szCdDeathFinding", pInputDataRec.getCdDeathFinding())
				.setParameter("hI_cCdPersonSex", pInputDataRec.getPersonSex())
				.setParameter("hI_dtDtPersonBirth", TypeConvUtil.isDateNullCheck(pInputDataRec.getDtPersonBirth()))
				.setParameter("hI_szCdPersonDeath", pInputDataRec.getCdPersonDeath())
				.setParameter("hI_bCdPersonChar", pInputDataRec.getCdPersonChar())
				.setParameter("hI_szCdPersonMaritalStatus", pInputDataRec.getCdPersonMaritalStatus())
				.setParameter("hI_lNbrPersonAge", pInputDataRec.getNbrPersonAge())
				.setParameter("hI_szCdDeathRsnCps", pInputDataRec.getCdDeathRsnCps())
				.setParameter("hI_szCdOccupation", pInputDataRec.getCdOccupation())
				.setParameter("hI_szCdPersonLivArr", pInputDataRec.getCdPersonLivArr())
				.setParameter("hI_szCdPersonReligion", pInputDataRec.getCdPersonReligion())
				.setParameter("hI_ulIdPerson", pInputDataRec.getIdPerson())
				.setParameter("hI_szCdDeathAutpsyRslt", pInputDataRec.getCdDeathAutpsyRslt())
				.setParameter("hI_szCdDisasterRlf", pInputDataRec.getCdDisasterRlf())
				.setParameter("hI_dtDtPersonDeath", TypeConvUtil.isDateNullCheck(pInputDataRec.getDtPersonDeath())));
		int rowCount = sQLQuery.executeUpdate();
		if (rowCount == 0) {
			throw new DataNotFoundException(
					messageSource.getMessage("Cinv41dDaoImpl.window.mode.person.not.updated", null, Locale.US));
		}
		objCinv41doDto.setIdPerson(pInputDataRec.getIdPerson());
		return objCinv41doDto;
	}

	/**
	 * Method Name: windowModeLower Method Description:(A person is being
	 * related to the Stage.) Add to the STAGE_PERSON LINK table. Update the
	 * CATEGORY table only if the Client Sends a category.
	 *
	 * @param pInputDataRec,objCinv41doDto
	 * @return Cinv41doDto
	 */
	@SuppressWarnings("unchecked")
	private PersonStageLinkCatgIdOutDto windowModeLower(PersonStageLinkCatgIdInDto pInputDataRec,
														PersonStageLinkCatgIdOutDto objCinv41doDto) {
		SQLQuery sQLQuery = ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(insertStagePersonLinkWindowLower)
				.setParameter("hI_szCdStagePersSearchInd", pInputDataRec.getIndCdStagePersSearch())
				.setParameter("hI_ulIdStage", pInputDataRec.getIdStage())
				.setParameter("hI_szCdStagePersRelInt", pInputDataRec.getCdStagePersRelInt())
				.setParameter("hI_dtDtStagePersLink", TypeConvUtil.isDateNullCheck(pInputDataRec.getDtStagePersLink()))
				.setParameter("hI_szCdStagePersType", pInputDataRec.getCdStagePersType())
				.setParameter("hI_bIndNytdDesgContact", pInputDataRec.getIndNytdDesgContact())
				.setParameter("hI_bIndStagePersReporter", pInputDataRec.getIndStagePersReporter())
				.setParameter("hI_bIndCaringAdult", pInputDataRec.getIndCaringAdult())
				.setParameter("hI_bIndNytdPrimary", pInputDataRec.getIndNytdPrimary())
				.setParameter("hI_ulIdPerson", pInputDataRec.getIdPerson())
				.setParameter("hI_szCdStagePersRole", pInputDataRec.getCdStagePersRole())
				.setParameter("hI_bIndStagePersInLaw", pInputDataRec.getIndStagePersInLaw()));
		int rowCount = sQLQuery.executeUpdate();
		if (rowCount == 0) {
			throw new DataNotFoundException(
					messageSource.getMessage("Cinv41dDaoImpl.window.lower.stageperson.not.inserted", null, Locale.US));
		}
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(updatePersonWindowLower)
				.setParameter("hI_szTxtOccupation", pInputDataRec.getOccupation())
				.setParameter("hI_bIndEducationPortfolio", pInputDataRec.getIndEducationPortfolio())
				.setParameter("hI_szCdTribeEligible", pInputDataRec.getCdTribeEligible())
				.setParameter("hI_szCdPersonEthnicGroup", pInputDataRec.getCdPersonEthnicGroup())
				.setParameter("hI_szCdDeathCause", pInputDataRec.getCdDeathCause())
				.setParameter("hI_szTxtFatalityDetails", pInputDataRec.getFatalityDetails())
				.setParameter("hI_szNmPersonFull", pInputDataRec.getNmPersonFull())
				.setParameter("hI_CdPersonStatus", pInputDataRec.getCdPersonStatus())
				.setParameter("hI_szCdMannerDeath", pInputDataRec.getCdMannerDeath())
				.setParameter("hI_bIndPersonDobApprox", pInputDataRec.getIndPersonDobApprox())
				.setParameter("hI_szCdPersonLanguage", pInputDataRec.getCdPersonLanguage())
				.setParameter("hI_tsSysTsLastUpdate2", pInputDataRec.getTsSysTsLastUpdate2())
				.setParameter("hI_szCdDeathFinding", pInputDataRec.getCdDeathFinding())
				.setParameter("hI_cCdPersonSex", pInputDataRec.getPersonSex())
				.setParameter("hI_dtDtPersonBirth", TypeConvUtil.isDateNullCheck(pInputDataRec.getDtPersonBirth()))
				.setParameter("hI_szCdPersonDeath", pInputDataRec.getCdPersonDeath())
				.setParameter("hI_szCdPersonMaritalStatus", pInputDataRec.getCdPersonMaritalStatus())
				.setParameter("hI_lNbrPersonAge", pInputDataRec.getNbrPersonAge())
				.setParameter("hI_szCdDeathRsnCps", pInputDataRec.getCdDeathRsnCps())
				.setParameter("hI_szCdOccupation", pInputDataRec.getCdOccupation())
				.setParameter("hI_szCdPersonLivArr", pInputDataRec.getCdPersonLivArr())
				.setParameter("hI_szCdPersonReligion", pInputDataRec.getCdPersonReligion())
				.setParameter("hI_ulIdPerson", pInputDataRec.getIdPerson())
				.setParameter("hI_szCdDeathAutpsyRslt", pInputDataRec.getCdDeathAutpsyRslt())
				.setParameter("hI_szCdDisasterRlf", pInputDataRec.getCdDisasterRlf())
				.setParameter("hI_dtDtPersonDeath", TypeConvUtil.isDateNullCheck(pInputDataRec.getDtPersonDeath())));
		int rowCount2 = sQLQuery1.executeUpdate();
		if (rowCount2 == 0) {
			throw new DataNotFoundException(
					messageSource.getMessage("Cinv41dDaoImpl.window.lower.person.not.updated", null, Locale.US));
		}
		if (null != pInputDataRec.getCdCategoryCategory()) {
			SQLQuery sQLQuery3 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getPersonCategoryCount)
					.addScalar("ulcategorycount", StandardBasicTypes.LONG)
					.setParameter("hI_szCdCategoryCategory", pInputDataRec.getCdCategoryCategory())
					.setParameter("hI_ulIdPerson", pInputDataRec.getIdPerson())
					.setResultTransformer(Transformers.aliasToBean(PersonStageLinkCatgIdOutDto.class)));
			List<PersonStageLinkCatgIdOutDto> liCinv41doDto1 = (List<PersonStageLinkCatgIdOutDto>) sQLQuery3.list();
			if (!TypeConvUtil.isNullOrEmpty(liCinv41doDto1)) {
				if (0 == liCinv41doDto1.get(0).getCategoryCount()) {
					SQLQuery sQLQuery4 = ((SQLQuery) sessionFactory.getCurrentSession()
							.createSQLQuery(insertWindowPersonCategory)
							.setParameter("hI_szCdCategoryCategory", pInputDataRec.getCdCategoryCategory())
							.setParameter("hI_ulIdPerson", pInputDataRec.getIdPerson()));
					int rowCount3 = sQLQuery4.executeUpdate();
					if (rowCount3 == 0) {
						throw new DataNotFoundException(messageSource.getMessage(
								"Cinv41dDaoImpl.window.lower.personcategory.not.inserted", null, Locale.US));
					}
				}
			}
		}
		objCinv41doDto.setIdPerson(pInputDataRec.getIdPerson());
		return objCinv41doDto;
	}

	/**
	 * Method Name: deleteStagePersonLink Method Description: Delete record from
	 * Stage Person Link
	 *
	 * @param pInputDataRec
	 */
	private void deleteStagePersonLink(PersonStageLinkCatgIdInDto pInputDataRec) {
		if (!TypeConvUtil.isNullOrEmpty(pInputDataRec)) {
			Date minDate = pInputDataRec.getTsLastUpdate();
			Date maxDate = new Date(minDate.getTime() + TimeUnit.DAYS.toMillis(1));
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(StagePersonLink.class);
			criteria.add(Restrictions.eq("idStagePersonLink", pInputDataRec.getIdStagePerson()));
			criteria.add(Restrictions.ge("dtLastUpdate", minDate));
			criteria.add(Restrictions.lt("dtLastUpdate", maxDate));
			StagePersonLink stagePersonLink = (StagePersonLink) criteria.uniqueResult();
			if (TypeConvUtil.isNullOrEmpty(stagePersonLink)) {
				throw new DataNotFoundException(
						messageSource.getMessage("Cinv41dDaoImpl.stage.person.record.not.found", null, Locale.US));
			}
			sessionFactory.getCurrentSession().delete(stagePersonLink);
		}
	}

	/**
	 * Method Name: updatePersonStatus Method Description: Update the Person
	 * status when deleting the stage person link
	 *
	 * @param pInputDataRec
	 */
	private void updatePersonStatus(PersonStageLinkCatgIdInDto pInputDataRec) {
		if (pInputDataRec != null) {
			Date minDate = pInputDataRec.getTsSysTsLastUpdate2();
			Date maxDate = new Date(minDate.getTime() + TimeUnit.DAYS.toMillis(1));
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Person.class);
			criteria.add(Restrictions.eq("idPerson", pInputDataRec.getIdPerson()));
			criteria.add(Restrictions.ge("dtLastUpdate", pInputDataRec.getTsSysTsLastUpdate2()));
			criteria.add(Restrictions.lt("dtLastUpdate", maxDate));
			Person person = (Person) criteria.uniqueResult();
			if (TypeConvUtil.isNullOrEmpty(person)) {
				throw new DataNotFoundException(
						messageSource.getMessage("Cinv41dDaoImpl.person.record.not.found", null, Locale.US));
			}
			if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getCdPersonStatus())) {
				person.setCdPersonStatus(pInputDataRec.getCdPersonStatus());
			}
			sessionFactory.getCurrentSession().saveOrUpdate(person);
			sessionFactory.getCurrentSession().flush();
		}
	}

	/**
	 *
	 * Method Name: updateStagePerson Method Description: Update Stage Person
	 * record
	 *
	 * @param pInputDataRec
	 */
	private void updateStagePerson(PersonStageLinkCatgIdInDto pInputDataRec) {
		if (pInputDataRec != null) {
			Date minDate = pInputDataRec.getTsLastUpdate();
			Date maxDate = new Date(minDate.getTime() + TimeUnit.DAYS.toMillis(1));
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(StagePersonLink.class);
			criteria.add(Restrictions.eq("idStagePersonLink", pInputDataRec.getIdStagePerson()));
			criteria.add(Restrictions.ge("dtLastUpdate", minDate));
			criteria.add(Restrictions.lt("dtLastUpdate", maxDate));
			StagePersonLink stagePersonLink = (StagePersonLink) criteria.uniqueResult();
			if (TypeConvUtil.isNullOrEmpty(stagePersonLink)) {
				throw new DataNotFoundException(
						messageSource.getMessage("Cinv41dDaoImpl.stage.person.record.not.found", null, Locale.US));
			}
			if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getIndCdStagePersSearch())) {
				stagePersonLink.setCdStagePersSearchInd(pInputDataRec.getIndCdStagePersSearch());
			}
			if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getIndStagePersInLaw())) {
				stagePersonLink.setIndStagePersInLaw(pInputDataRec.getIndStagePersInLaw());
			}
			if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getCdStagePersRelInt())) {
				stagePersonLink.setCdStagePersRelInt(pInputDataRec.getCdStagePersRelInt());
			}
			if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getCdStagePersType())) {
				stagePersonLink.setCdStagePersType(pInputDataRec.getCdStagePersType());
			}
			if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getIndNytdDesgContact())) {
				stagePersonLink.setIndNytdContact(pInputDataRec.getIndNytdDesgContact());
			}
			if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getIndStagePersReporter())) {
				stagePersonLink.setIndStagePersReporter(pInputDataRec.getIndStagePersReporter());
			}
			if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getIndCaringAdult())) {
				stagePersonLink.setIndCaringAdult(pInputDataRec.getIndCaringAdult());
			}
			if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getIndNytdPrimary())) {
				stagePersonLink.setIndNytdContactPrimary(pInputDataRec.getIndNytdPrimary());
			}
			if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getCdStagePersRole())) {
				stagePersonLink.setCdStagePersRole(pInputDataRec.getCdStagePersRole());
			}
			stagePersonLink.setDtLastUpdate(new Date());
			sessionFactory.getCurrentSession().saveOrUpdate(stagePersonLink);
		}
	}

	/**
	 * Method Name: updatePerson Method Description: Update Person record
	 *
	 * @param pInputDataRec
	 */
	private void updatePerson(PersonStageLinkCatgIdInDto pInputDataRec) {
		if (pInputDataRec != null) {
			Date minDate = pInputDataRec.getTsSysTsLastUpdate2();
			Date maxDate = new Date(minDate.getTime() + TimeUnit.DAYS.toMillis(1));
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Person.class);
			criteria.add(Restrictions.eq("idPerson", pInputDataRec.getIdPerson()));
			criteria.add(Restrictions.ge("dtLastUpdate", pInputDataRec.getTsSysTsLastUpdate2()));
			criteria.add(Restrictions.lt("dtLastUpdate", maxDate));
			Person person = (Person) criteria.uniqueResult();
			if (TypeConvUtil.isNullOrEmpty(person)) {
				throw new DataNotFoundException(
						messageSource.getMessage("Cinv41dDaoImpl.person.record.not.found", null, Locale.US));
			}
			person.setTxtPersonOccupation(pInputDataRec.getOccupation());
			if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getIndEducationPortfolio())) {
				person.setIndEducationPortfolio(pInputDataRec.getIndEducationPortfolio());
			}
			if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getCdTribeEligible())) {
				person.setCdTribeEligible(pInputDataRec.getCdTribeEligible());
			}
			person.setCdDeathCause(pInputDataRec.getCdDeathCause());
			person.setTxtFatalityDetails(pInputDataRec.getFatalityDetails());
			if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getNmPersonFull())) {
				person.setNmPersonFull(pInputDataRec.getNmPersonFull());
			}
			if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getCdPersonEthnicGroup())) {
				person.setCdPersonEthnicGroup(pInputDataRec.getCdPersonEthnicGroup());
			}
			if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getCdPersonStatus())) {
				person.setCdPersonStatus(pInputDataRec.getCdPersonStatus());
			}
			person.setCdMannerDeath(pInputDataRec.getCdMannerDeath());
			if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getIndPersonDobApprox())) {
				person.setIndPersonDobApprox(pInputDataRec.getIndPersonDobApprox());
			}
			person.setCdPersonLanguage(pInputDataRec.getCdPersonLanguage());
			person.setCdDeathFinding(pInputDataRec.getCdDeathFinding());
			if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getPersonSex())) {
				person.setCdPersonSex(pInputDataRec.getPersonSex());
			}
			if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getDtPersonBirth())) {
				person.setDtPersonBirth(pInputDataRec.getDtPersonBirth());
			}
			person.setCdPersonDeath(pInputDataRec.getCdPersonDeath());
			person.setCdPersonMaritalStatus(pInputDataRec.getCdPersonMaritalStatus());
			if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getNbrPersonAge())) {
				person.setNbrPersonAge(pInputDataRec.getNbrPersonAge().shortValue());
			}
			person.setCdDeathRsnCps(pInputDataRec.getCdDeathRsnCps());
			person.setCdOccupation(pInputDataRec.getCdOccupation());
			person.setCdPersonLivArr(pInputDataRec.getCdPersonLivArr());
			person.setCdPersonReligion(pInputDataRec.getCdPersonReligion());
			person.setCdDeathAutpsyRslt(pInputDataRec.getCdDeathAutpsyRslt());
			person.setCdDisasterRlf(pInputDataRec.getCdDisasterRlf());
			person.setDtPersonDeath(pInputDataRec.getDtPersonDeath());
			person.setDtLastUpdate(new Date());
			sessionFactory.getCurrentSession().saveOrUpdate(person);
		}
	}

	/**
	 *
	 * Method Name: insertRecords Method Description: Always Add to PERSON
	 * table, CATEGORY STAGE PERSON LINK Table. If a person has a a full name of
	 * Unknown and is a Principle in the Case, concatenate the ID PERSON to the
	 * end
	 *
	 * @param pInputDataRec,objCinv41doDto
	 * @return Cinv41doDto
	 */
	@SuppressWarnings("unchecked")
	private PersonStageLinkCatgIdOutDto insertRecords(PersonStageLinkCatgIdInDto pInputDataRec,
													  PersonStageLinkCatgIdOutDto objCinv41doDto) {
		long personId = 0L;
		SQLQuery sQLQuery = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getPersonId)
				.addScalar("idPerson", StandardBasicTypes.LONG)
				.setResultTransformer(Transformers.aliasToBean(PersonStageLinkCatgIdOutDto.class)));
		List<PersonStageLinkCatgIdOutDto> liCinv41doDto = (List<PersonStageLinkCatgIdOutDto>) sQLQuery.list();
		if (!TypeConvUtil.isNullOrEmpty(liCinv41doDto)) {
			personId = liCinv41doDto.get(0).getIdPerson();
			if ((pInputDataRec.getNmPersonFull().equalsIgnoreCase(ServiceConstants.LT_UNKNOWN))
					&& (pInputDataRec.getCdStagePersType().equalsIgnoreCase(ServiceConstants.LT_PRINCIPLE))) {
				pInputDataRec.setNmPersonFull(ServiceConstants.LT_UNKNOWN + personId);
			}
			//Modified the query to add SSN number filed in insert query for warranty defect 12499
			SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(insertPersonRecord)
					.setParameter("hI_szTxtOccupation", pInputDataRec.getOccupation())
					.setParameter("hI_bIndEducationPortfolio", pInputDataRec.getIndEducationPortfolio())
					.setParameter("hI_szCdTribeEligible", pInputDataRec.getCdTribeEligible())
					.setParameter("hI_szCdDeathCause", pInputDataRec.getCdDeathCause())
					.setParameter("hI_szTxtFatalityDetails", pInputDataRec.getFatalityDetails())
					.setParameter("nmPersonFirst", pInputDataRec.getNameFirst())
					.setParameter("nmPersonMiddle", pInputDataRec.getNameMiddle())
					.setParameter("nmPersonLast", pInputDataRec.getNameLast())
					.setParameter("cdPersonSuffix", pInputDataRec.getCdPersonSuffix())
					.setParameter("hI_szNmPersonFull", pInputDataRec.getNmPersonFull())
					.setParameter("hI_szCdPersonEthnicGroup", pInputDataRec.getCdPersonEthnicGroup())
					.setParameter("hI_CdPersonStatus", pInputDataRec.getCdPersonStatus())
					.setParameter("hI_szCdMannerDeath", pInputDataRec.getCdMannerDeath())
					.setParameter("hI_bIndPersonDobApprox", pInputDataRec.getIndPersonDobApprox())
					.setParameter("hI_szCdPersonLanguage", pInputDataRec.getCdPersonLanguage())
					.setParameter("hI_szCdDeathFinding", pInputDataRec.getCdDeathFinding())
					.setParameter("hI_cCdPersonSex", pInputDataRec.getPersonSex())
					.setParameter("hI_dtDtPersonBirth", TypeConvUtil.isDateNullCheck(pInputDataRec.getDtPersonBirth()))
					.setParameter("hI_szCdPersonDeath", pInputDataRec.getCdPersonDeath())
					.setParameter("hI_bCdPersonChar", pInputDataRec.getCdPersonChar())
					.setParameter("hI_szCdPersonMaritalStatus", pInputDataRec.getCdPersonMaritalStatus())
					.setParameter("hI_lNbrPersonAge", pInputDataRec.getNbrPersonAge())
					.setParameter("hI_szCdDeathRsnCps", pInputDataRec.getCdDeathRsnCps())
					.setParameter("hI_szCdOccupation", pInputDataRec.getCdOccupation())
					.setParameter("hI_szCdPersonReligion", pInputDataRec.getCdPersonReligion())
					.setParameter("hI_szCdPersonLivArr", pInputDataRec.getCdPersonLivArr())
					.setParameter("hI_ulIdPerson", personId)
					.setParameter("hI_szCdDeathAutpsyRslt", pInputDataRec.getCdDeathAutpsyRslt())
					.setParameter("hI_szCdDisasterRlf", pInputDataRec.getCdDisasterRlf()).setParameter(
							"hI_dtDtPersonDeath", TypeConvUtil.isDateNullCheck(pInputDataRec.getDtPersonDeath()))
					.setParameter("hI_sznbrPersonIdNumber", pInputDataRec.getNbrPersonIdNumber()));
			int rowCount = sQLQuery1.executeUpdate();
			if (rowCount == 0) {
				throw new DataNotFoundException(
						messageSource.getMessage("Cinv41dDaoImpl.person.record.no.insert", null, Locale.US));
			}
			SQLQuery sQLQuery2 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(insertStagePersonLink)
					.setParameter("hI_szCdStagePersSearchInd", pInputDataRec.getIndCdStagePersSearch())
					.setParameter("hI_ulIdStage", pInputDataRec.getIdStage())
					.setParameter("hI_szCdStagePersRelInt", pInputDataRec.getCdStagePersRelInt())
					.setParameter("hI_dtDtStagePersLink",
							TypeConvUtil.isDateNullCheck(pInputDataRec.getDtStagePersLink()))
					.setParameter("hI_szCdStagePersType", pInputDataRec.getCdStagePersType())
					.setParameter("hI_bIndNytdDesgContact", pInputDataRec.getIndNytdDesgContact())
					.setParameter("hI_bIndStagePersReporter", pInputDataRec.getIndStagePersReporter())
					.setParameter("hI_bIndCaringAdult", pInputDataRec.getIndCaringAdult())
					.setParameter("hI_bIndNytdPrimary", pInputDataRec.getIndNytdPrimary())
					.setParameter("hI_ulIdPerson", personId)
					.setParameter("hI_szCdStagePersRole", pInputDataRec.getCdStagePersRole())
					.setParameter("hI_bIndStagePersInLaw", pInputDataRec.getIndStagePersInLaw()));
			int rowCount2 = sQLQuery2.executeUpdate();
			if (rowCount2 == 0) {
				throw new DataNotFoundException(
						messageSource.getMessage("Cinv41dDaoImpl.stageperson.record.no.insert", null, Locale.US));
			}
			//Added the code to insert the record in Person_Id table for warranty defect 12499
			//Defect# 12942 - Added a check to not insert record without SSN number into Person_Id table
			if (!ObjectUtils.isEmpty(pInputDataRec.getNbrPersonIdNumber())) {
				SQLQuery sQLQuery3 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getIdPerosnId)
						.addScalar("idPersonId", StandardBasicTypes.LONG)
						.setResultTransformer(Transformers.aliasToBean(PersonIdDto.class)));
				List<PersonIdDto> personIdDtoLst = (List<PersonIdDto>) sQLQuery3.list();
				if (!ObjectUtils.isEmpty(personIdDtoLst)) {
					Long idPersonId = personIdDtoLst.get(ServiceConstants.Zero_INT).getIdPersonId();
					SQLQuery sQLQuery4 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(insertPersonID)
							.setParameter("hI_ulIdPerson", personId)
							.setParameter("hI_szTxtNbrPersonIdNumber", pInputDataRec.getNbrPersonIdNumber())
							.setParameter("hI_ulIdPersonId", idPersonId));
					int rowCount3 = sQLQuery4.executeUpdate();
					if (rowCount3 == 0) {
						throw new DataNotFoundException(
								messageSource.getMessage("Cinv41dDaoImpl.personId.record.no.insert", null, Locale.US));
					}
				}
			}
		} else {
			throw new DataNotFoundException(
					messageSource.getMessage("Cinv41dDaoImpl.personid.sequence.not.generated", null, Locale.US));
		}
		objCinv41doDto.setIdPerson(personId);
		return objCinv41doDto;
	}

	public PersonDtlUpdateDto updateInvestigationPersonDetailForMPS(PersonDtlUpdateDto personDetailUpdateoDto){
		PersonDtlUpdateDto personDetailUpdateDto = insertRecordsForMPS(personDetailUpdateoDto);
		return personDetailUpdateDto;

	}


	private PersonDtlUpdateDto insertRecordsForMPS(PersonDtlUpdateDto pInputDataRec) {
		IncomingPersonMps incomingPersonMps = new IncomingPersonMps();
		incomingPersonMps.setCdGender(pInputDataRec.getPersonSex());
		incomingPersonMps.setDtPersonAdded(new Date());
		incomingPersonMps.setDtPersonDeath(pInputDataRec.getDtPersonDeath());
		incomingPersonMps.setDtBirth(pInputDataRec.getDtPersonBirth());
		incomingPersonMps.setCdPersonStatus(pInputDataRec.getPersonStatus());
		incomingPersonMps.setCdMaritalStatus(pInputDataRec.getCdPersonMaritalStatus());
		incomingPersonMps.setCdLanguage(pInputDataRec.getCdPersonLanguage());
		incomingPersonMps.setIndDobApprox(pInputDataRec.getIndPersonDobApprox());
		incomingPersonMps.setNbrAge(pInputDataRec.getNbrPersonAge());
		incomingPersonMps.setNmFirst(pInputDataRec.getNmNameFirst());
		incomingPersonMps.setNmMiddle(pInputDataRec.getNmNameMiddle());
		incomingPersonMps.setNmLast(pInputDataRec.getNmNameLast());
		incomingPersonMps.setNmFull(pInputDataRec.getNmPersonFull());
		incomingPersonMps.setCdSuffix(pInputDataRec.getCdNameSuffix());
		incomingPersonMps.setCdEthnicity(pInputDataRec.getPersonEthnicityDtoList().get(0).getCdPersonEthnicity());
		incomingPersonMps.setIdStage(pInputDataRec.getIdStage());
		incomingPersonMps.setIdCase(pInputDataRec.getIdCase());
		incomingPersonMps.setCdPersRole(pInputDataRec.getCdStagePersRole());
		incomingPersonMps.setIndPersInLaw(pInputDataRec.getIndStagePersInLaw());
		incomingPersonMps.setCdPersType(pInputDataRec.getCdStagePersType());
		incomingPersonMps.setCdPersSearchInd(ServiceConstants.IND_STAGE_PERSON_SEARCH_MPS);
		incomingPersonMps.setCdPersRelInt(pInputDataRec.getCdStagePersRelInt());
		incomingPersonMps.setIndStagePersRelated(ServiceConstants.STRING_IND_Y);
		incomingPersonMps.setIndMpsPersUsed(ServiceConstants.STRING_IND_N);
		log.info("MPS Person:"+ incomingPersonMps.getNmFull()+ " Setting indMpsPersUsed to "+incomingPersonMps.getIndMpsPersUsed() );
		incomingPersonMps.setNbrPersonIdNumber(pInputDataRec.getNbrPersonIdNumber());
		incomingPersonMps.setDtLastUpdate(new Date());
		Set<PersonRaceMps> personRaceMpsList = new HashSet<>();
		for (PersonRaceDto raceDto:pInputDataRec.getPersonRaceDtoList()) {
			PersonRaceMps personRaceMps = new PersonRaceMps();
			personRaceMps.setIncomingPersonMps(incomingPersonMps);
			personRaceMps.setCdRace(raceDto.getCdPersonRace());
			personRaceMps.setDtLastUpdate(new Date());
			personRaceMpsList.add(personRaceMps);
			//sessionFactory.getCurrentSession().save(personRaceMps);
		}
		incomingPersonMps.setPersonRaceMpses(personRaceMpsList);
		sessionFactory.getCurrentSession().save(incomingPersonMps);


		pInputDataRec.setIdPerson(incomingPersonMps.getIdIncomingPersonMps());
		return pInputDataRec;
	}

	private PersonStageLinkCatgIdOutDto deleteRecordsForMPS(PersonStageLinkCatgIdInDto pInputDataRec, PersonStageLinkCatgIdOutDto personStageLinkCatgIdOutDto) {
		deleteMPSPersonRace(pInputDataRec.getIdPerson());
		deleteMPSPerson(pInputDataRec.getIdPerson());
		personStageLinkCatgIdOutDto.setIdPerson(pInputDataRec.getIdPerson());
		return personStageLinkCatgIdOutDto;
	}

	private void deleteMPSPerson(Long idPerson) {
		SQLQuery sQLQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(deletePersonRecordForMPS)
				.setParameter("idPerson", idPerson);
		int row = sQLQuery.executeUpdate();
	}

	private void deleteMPSPersonRace(Long idPerson) {
		SQLQuery sQLQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(deletePersonRaceRecordForMPS)
				.setParameter("idPerson", idPerson);
		int row = sQLQuery.executeUpdate();

	}

	public EditPersonAddressDto updateMPSPersonAddress(EditPersonAddressDto personAddressDto){
		SQLQuery sQLQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(updatePersonAddressForMPS)
				.setParameter("hI_addrStLn1",personAddressDto.getAddrPersAddrStLn1())
				.setParameter("hI_addrStLn2",personAddressDto.getAddrPersAddrStLn2())
				.setParameter("hI_addrCity",personAddressDto.getAddrCity())
				.setParameter("hI_addrZip",personAddressDto.getAddrZip())
				.setParameter("hI_addrState",personAddressDto.getCdAddrState())
				.setParameter("hI_addrCounty",personAddressDto.getCdAddrCounty())
				.setParameter("hI_addrType", personAddressDto.getCdPersAddrLinkType())
				.setParameter("idPerson", personAddressDto.getIdPerson());
		int row = sQLQuery.executeUpdate();
		return personAddressDto;

	}

	@Override
	public IncomingPersonMpsDto updateMPSPersonDetail(IncomingPersonMpsDto incomingPersonMpsDto) {
		IncomingPersonMps incomingPersonMps = new IncomingPersonMps();
		IncomingPersonMps person = (IncomingPersonMps) sessionFactory.getCurrentSession().get(IncomingPersonMps.class, incomingPersonMpsDto.getIdIncomingPersonMps());
		if(person.getIdIncomingPersonMps()>0){
			incomingPersonMps = person;
			incomingPersonMps.setNmFirst(incomingPersonMpsDto.getNmFirst());
			incomingPersonMps.setNmLast(incomingPersonMpsDto.getNmLast());
			incomingPersonMps.setNmMiddle(incomingPersonMpsDto.getNmMiddle());
			incomingPersonMps.setCdSuffix(incomingPersonMpsDto.getCdSuffix());
			incomingPersonMps.setCdGender(incomingPersonMpsDto.getCdGender());
			incomingPersonMps.setNmFull(incomingPersonMpsDto.getNmFull());
			incomingPersonMps.setCdMaritalStatus(incomingPersonMpsDto.getCdMaritalStatus());
			incomingPersonMps.setDtBirth(incomingPersonMpsDto.getDtBirth());
			incomingPersonMps.setNbrAge(Integer.valueOf(incomingPersonMpsDto.getNbrAge()));
			incomingPersonMps.setIndDobApprox(incomingPersonMpsDto.getIndDobApprox());
			incomingPersonMps.setCdLanguage(incomingPersonMpsDto.getCdLanguage());
			incomingPersonMps.setNbrPersonIdNumber(incomingPersonMpsDto.getNbrPersonIdNumber());
			incomingPersonMps.setCdPersType(incomingPersonMpsDto.getCdPersType());
			incomingPersonMps.setCdPersRole(incomingPersonMpsDto.getCdPersRole());

			incomingPersonMps.setIndPersInLaw(incomingPersonMpsDto.getIndPersInLaw());
			incomingPersonMps.setCdEthnicity(incomingPersonMpsDto.getCdEthnicity());


		} else {
			BeanUtils.copyProperties(incomingPersonMpsDto, incomingPersonMps);
		}
		incomingPersonMps.setNbrAge(incomingPersonMpsDto.getNbrAge().intValue());
		incomingPersonMps.setDtLastUpdate(new Date());
		sessionFactory.getCurrentSession().saveOrUpdate(incomingPersonMps);

		deleteMPSPersonRace(incomingPersonMpsDto.getIdIncomingPersonMps());

		for (PersonRaceMpsDto raceDto:incomingPersonMpsDto.getPersonRaceMpsDtoSet()) {
			PersonRaceMps personRaceMps = new PersonRaceMps();
			personRaceMps.setIncomingPersonMps(incomingPersonMps);
			personRaceMps.setCdRace(raceDto.getCdRace());
			personRaceMps.setDtLastUpdate(new Date());
			sessionFactory.getCurrentSession().saveOrUpdate(personRaceMps);
		}
		return incomingPersonMpsDto;
	}

	/**
	 * This method will save the person details from MPS Person to Person table.
	 * @param idMPSPerson
	 * @return
	 */
	@Override
	public PersonIdDto addMPSPersonDetails(Long idMPSPerson) {
		PersonIdDto personDto = new PersonIdDto();
		IncomingPersonMps incomingPersonMps = (IncomingPersonMps) sessionFactory.getCurrentSession().get(IncomingPersonMps.class, idMPSPerson);
		if(incomingPersonMps.getIdIncomingPersonMps()>0){
			PersonStageLinkCatgIdInDto person = new PersonStageLinkCatgIdInDto();
			PersonStageLinkCatgIdOutDto outDto = new PersonStageLinkCatgIdOutDto();
			person.setNameFirst(incomingPersonMps.getNmFirst());

			person.setCdPersonDeath(incomingPersonMps.getCdPersonDeath());
			//person.setCdDeathRsnCps(incomingPersonMps.getCdPersonDeath());
			person.setCdDeathCause(incomingPersonMps.getCdPersonDeath());
			person.setCdPersonLanguage(incomingPersonMps.getCdLanguage());
			person.setCdPersonMaritalStatus(incomingPersonMps.getCdMaritalStatus());
			person.setPersonSex(incomingPersonMps.getCdGender());
			person.setCdPersonStatus(incomingPersonMps.getCdPersonStatus());
			person.setDtPersonBirth(incomingPersonMps.getDtBirth());
			person.setDtPersonDeath(incomingPersonMps.getDtPersonDeath());
			person.setNbrPersonAge(incomingPersonMps.getNbrAge());
			person.setNmPersonFull(incomingPersonMps.getNmFull());
			person.setIndPersonDobApprox(incomingPersonMps.getIndDobApprox());
			person.setNameMiddle(incomingPersonMps.getNmMiddle());
			person.setNameLast(incomingPersonMps.getNmLast());
			person.setCdPersonSuffix(incomingPersonMps.getCdSuffix());
			//person.setLastUpdate(incomingPersonMps.getDtLastUpdate());
			person.setIdStage(incomingPersonMps.getIdStage());
			person.setCdStagePersRelInt(incomingPersonMps.getCdPersRelInt());
			person.setDtStagePersLink(new Date());
			person.setCdStagePersType(incomingPersonMps.getCdPersType());
			person.setCdStagePersRole(incomingPersonMps.getCdPersRole());
			person.setIndStagePersInLaw(incomingPersonMps.getIndPersInLaw());
			person.setCdPersonEthnicGroup(incomingPersonMps.getCdEthnicity());
			PersonStageLinkCatgIdOutDto dto = insertRecords(person, outDto);
			personDto.setIdPerson(dto.getIdPerson());
		}
		return personDto;
	}

	/**
	 * This method will retrieve the Person record for passed in idPerson.
	 * @param idPerson
	 * @return
	 */
	public Person getPersonDtl(long idPerson){
		Person personDtl = (Person) sessionFactory.getCurrentSession().get(Person.class, idPerson);
		return personDtl;
	}

	/**
	 * This method will save the race detail from MPS Person to Person Race table.
	 * @param idMPSPerson
	 * @param personDtl
	 */
	public void saveMPSPersonRace(long idMPSPerson, Person personDtl){
		Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(getPersonRaceMPs)
				.addScalar("idPersonRaceMps", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("cdRace", StandardBasicTypes.STRING)
				.setParameter("idIncomingPPersonMPS", idMPSPerson)
				.setResultTransformer(Transformers.aliasToBean(PersonRaceMps.class));;
		List<PersonRaceMps> personRaceMpsList = (List<PersonRaceMps>)query.list();


		for (PersonRaceMps personRaceMps: personRaceMpsList) {
			PersonRace personRace = new PersonRace();
			personRace.setPerson(personDtl);
			personRace.setCdRace(personRaceMps.getCdRace());
			personRace.setDtLastUpdate(personRaceMps.getDtLastUpdate());
			sessionFactory.getCurrentSession().save(personRace);
		}
	}

	/**
	 * This method will save the Ethnicity detail from MPS Person to Person Ethnicity table.
	 * @param personDtl
	 * @param incomingPersonMps
	 */
	public void saveMPSPersonEthniciy(Person personDtl, IncomingPersonMps incomingPersonMps ){
		PersonEthnicity personEthnicity = new PersonEthnicity();
		if (incomingPersonMps.getCdEthnicity() != null) {
			personEthnicity.setPerson(personDtl);
			personEthnicity.setCdEthnicity(incomingPersonMps.getCdEthnicity());
			personEthnicity.setDtLastUpdate(new Date());
			sessionFactory.getCurrentSession().save(personEthnicity);
		}
	}


	/**
	 * This method will retrieve the Incoming_MPS_Person record for passed in idMPSPerson.
	 * @param idMPSPerson
	 * @return
	 */
	public IncomingPersonMps getIncomingMPSPersonDetail(long idMPSPerson){
		IncomingPersonMps incomingPersonMps = (IncomingPersonMps) sessionFactory.getCurrentSession().get(IncomingPersonMps.class, idMPSPerson);
		return incomingPersonMps;
	}

	/**
	 * This method will save the address details from MPS Person to Person Address table and Address Person Link table.
	 * @param idPerson
	 * @param incomingPersonMps
	 */
	public void saveMPSPersonAddress(long idPerson, IncomingPersonMps incomingPersonMps ){
		//1. Insert into PERSON_ADDRESS
		PersonAddress personAddress = new PersonAddress();
		personAddress.setAddrPersAddrStLn1(incomingPersonMps.getAddrStLn1());
		personAddress.setAddrPersAddrStLn2(incomingPersonMps.getAddrStLn2());
		personAddress.setAddrPersonAddrZip(incomingPersonMps.getAddrZip());
		personAddress.setAddrPersonAddrCity(incomingPersonMps.getAddrCity());
		personAddress.setCdPersonAddrState(incomingPersonMps.getCdAddrState());
		personAddress.setCdPersonAddrCounty(incomingPersonMps.getCdAddrCounty());
		personAddress.setDtLastUpdate(new Date());
		long idAddrPerson = (long) sessionFactory.getCurrentSession().save(personAddress);
		//2. Insert into ADDRESS_PERSON_LINK
		AddressPersonLink addrPersonLink = new AddressPersonLink();
		addrPersonLink.setIdPersonAddr(idAddrPerson);
		addrPersonLink.setIdPerson(idPerson);
		addrPersonLink.setDtLastUpdate(new Date());
		addrPersonLink.setCdPersAddrLinkType(incomingPersonMps.getCdAddrType());
		addrPersonLink.setIndPersAddrLinkInvalid(ServiceConstants.NO);
		addrPersonLink.setIndPersAddrLinkPrimary(ServiceConstants.NO);
		addrPersonLink.setDtPersAddrLinkStart(new Date());
		addrPersonLink.setDtLastUpdate(new Date());
		sessionFactory.getCurrentSession().save(addrPersonLink);
	}

	/**
	 * This method will save the phone details from MPS Person to Person Person table.
	 * @param personDtl
	 * @param incomingPersonMps
	 */
	public void saveMPSPhone (Person personDtl, IncomingPersonMps incomingPersonMps ){
		//Phone
		PersonPhone personPhone = new PersonPhone();
		personPhone.setPerson(personDtl);
		personPhone.setNbrPersonPhone(incomingPersonMps.getNbrPhone());
		// Start date will be Todays date.
		personPhone.setDtPersonPhoneStart(new Date());
		personPhone.setNbrPersonPhone(incomingPersonMps.getNbrPhone());
		personPhone.setNbrPersonPhoneExtension(incomingPersonMps.getNbrPhoneExtension());
		personPhone.setCdPersonPhoneType(incomingPersonMps.getCdPhoneType());
		personPhone.setDtLastUpdate(new Date());
		personPhone.setIndPersonPhoneInvalid(ServiceConstants.NO);
		sessionFactory.getCurrentSession().save(personPhone);
	}

	/**
	 * This method will save the email details from MPS Person to Person Email table.
	 * @param personDtl
	 * @param incomingPersonMps
	 */
	public void saveMPSEmail(Person personDtl,  IncomingPersonMps incomingPersonMps ){
		//Email
		PersonEmail personEmail = new PersonEmail();
		personEmail.setPerson(personDtl);
		personEmail.setCdType(incomingPersonMps.getCdEmailType());
		personEmail.setTxtEmail(incomingPersonMps.getTxtEmail());
		personEmail.setDtLastUpdate(new Date());
		personEmail.setDtCreated(new Date());
		personEmail.setDtStart(new Date());
		sessionFactory.getCurrentSession().save(personEmail);
	}

	/**
	 * This method will update the MPS Person Used Indicator to Y.
	 * @param personDtl
	 * @param incomingPersonMps
	 */
	public void saveMPSPersonInd(Person personDtl,  IncomingPersonMps incomingPersonMps ){
		incomingPersonMps.setIndMpsPersUsed(ServiceConstants.Y);
		incomingPersonMps.setPerson(personDtl);
		sessionFactory.getCurrentSession().update(incomingPersonMps);
	}


}
