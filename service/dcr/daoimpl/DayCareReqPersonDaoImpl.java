package us.tx.state.dfps.service.dcr.daoimpl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.internal.SessionImpl;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.DaycarePersonFacilLink;
import us.tx.state.dfps.common.domain.DaycarePersonLink;
import us.tx.state.dfps.common.domain.DaycareRequest;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.dcr.dao.DayCareReqPersonDao;
import us.tx.state.dfps.service.dcr.dto.DayCareFacilityDto;
import us.tx.state.dfps.service.dcr.dto.DayCarePersonDto;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:this class
 * has method implementations of DayCareReqPersonDao Nov 1, 2017- 7:19:07 PM ©
 * 2017 Texas Department of Family and Protective Services
 */
@Repository
public class DayCareReqPersonDaoImpl implements DayCareReqPersonDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${DayCareReqPersonDaoImpl.hasChangedSystemResponses}")
	public String hasChangedSystemResponsesSql;

	@Value("${DayCareReqPersonDaoImpl.retrieveDayCarePersonLink}")
	private String retrieveDayCarePersonLinkSql;

	@Value("${DayCareReqPersonDaoImpl.getDayCarePersonsInfoSql}")
	private String getDayCarePersonsInfoSql;

	@Value("${DayCareReqPersonDaoImpl.populateAddress}")
	private String populateAddress;

	@Value("${DayCareReqPersonDaoImpl.deleteDayCarePersonLink}")
	private String deleteDayCarePersonLink;

	@Value("${DayCareReqPersonDaoImpl.retrieveSvcAuthPersonCount}")
	private String retrieveSvcAuthPersonCount;

	/**
	 * Method Name: insertDayCarePersonLink Method Description:This method
	 * inserts single record into DAYCARE_PERSON_LINK table.
	 * 
	 * @param dayCarePersonDto
	 * @return Long @
	 */
	@Override
	public Long insertDayCarePersonLink(DayCarePersonDto dayCarePersonDto) {

		Long primaryKey = ServiceConstants.LongZero;
		DaycarePersonLink daycarepersonlink = new DaycarePersonLink();
		Person person = new Person();

		DaycareRequest daycareRequest = new DaycareRequest();
		if (!TypeConvUtil.isNullOrEmpty(dayCarePersonDto.getIdDayCareRequest())) {
			daycareRequest.setIdDaycareRequest(dayCarePersonDto.getIdDayCareRequest());
			daycarepersonlink.setDaycareRequest(daycareRequest);
		}

		if (!TypeConvUtil.isNullOrEmpty(dayCarePersonDto.getIdPerson())) {
			person.setIdPerson(dayCarePersonDto.getIdPerson());
			daycarepersonlink.setPerson(person);
		}

		if (!TypeConvUtil.isNullOrEmpty(dayCarePersonDto.getCdPersonType())) {
			daycarepersonlink.setCdPersonType(dayCarePersonDto.getCdPersonType());
		}

		if (!TypeConvUtil.isNullOrEmpty(dayCarePersonDto.getCdDetermService())) {
			daycarepersonlink.setCdDetermService(dayCarePersonDto.getCdDetermService());
		}

		if (!TypeConvUtil.isNullOrEmpty(dayCarePersonDto.getCdRequestType())) {
			daycarepersonlink.setCdRequestType(dayCarePersonDto.getCdRequestType());
		}

		if (!TypeConvUtil.isNullOrEmpty(dayCarePersonDto.getCdDayCareType())) {
			daycarepersonlink.setCdDaycareType(dayCarePersonDto.getCdDayCareType());
		}

		if (!TypeConvUtil.isNullOrEmpty(dayCarePersonDto.getDtBegin())) {
			daycarepersonlink.setDtBegin(dayCarePersonDto.getDtBegin());
		}

		if (!TypeConvUtil.isNullOrEmpty(dayCarePersonDto.getDtEnd())) {
			daycarepersonlink.setDtEnd(dayCarePersonDto.getDtEnd());
		}

		if (!TypeConvUtil.isNullOrEmpty(dayCarePersonDto.getIdFacilityActive())) {
			daycarepersonlink.setIdFacility(dayCarePersonDto.getIdFacilityActive());
		}

		if (!TypeConvUtil.isNullOrEmpty(dayCarePersonDto.getTxtDayHoursNeeded())) {
			daycarepersonlink.setTxtHoursNeeded(dayCarePersonDto.getTxtDayHoursNeeded());
		}

		if (!TypeConvUtil.isNullOrEmpty(dayCarePersonDto.getIdSSCCReferral())) {
			daycarepersonlink.setIdSsccReferral(dayCarePersonDto.getIdSSCCReferral());
		}

		daycarepersonlink
				.setIndSun(ObjectUtils.isEmpty(dayCarePersonDto.getIndSun()) ? "N" : dayCarePersonDto.getIndSun());
		daycarepersonlink
				.setIndMon(ObjectUtils.isEmpty(dayCarePersonDto.getIndMon()) ? "N" : dayCarePersonDto.getIndMon());
		daycarepersonlink
				.setIndTue(ObjectUtils.isEmpty(dayCarePersonDto.getIndTue()) ? "N" : dayCarePersonDto.getIndTue());
		daycarepersonlink
				.setIndWed(ObjectUtils.isEmpty(dayCarePersonDto.getIndWed()) ? "N" : dayCarePersonDto.getIndWed());
		daycarepersonlink
				.setIndThu(ObjectUtils.isEmpty(dayCarePersonDto.getIndThu()) ? "N" : dayCarePersonDto.getIndThu());
		daycarepersonlink
				.setIndFri(ObjectUtils.isEmpty(dayCarePersonDto.getIndFri()) ? "N" : dayCarePersonDto.getIndFri());
		daycarepersonlink
				.setIndSat(ObjectUtils.isEmpty(dayCarePersonDto.getIndSat()) ? "N" : dayCarePersonDto.getIndSat());

		if (!TypeConvUtil.isNullOrEmpty(dayCarePersonDto.getIndVarSch())) {
			daycarepersonlink.setIndVarSch(dayCarePersonDto.getIndVarSch());
		}

		if (!TypeConvUtil.isNullOrEmpty(dayCarePersonDto.getCdVarSchMaxDays())) {
			daycarepersonlink.setCdVarSchMaxDays(dayCarePersonDto.getCdVarSchMaxDays());
		}

		if (!TypeConvUtil.isNullOrEmpty(dayCarePersonDto.getCdSummerType())) {
			daycarepersonlink.setCdSummerType(dayCarePersonDto.getCdSummerType());
		}

		if (!TypeConvUtil.isNullOrEmpty(dayCarePersonDto.getCdWeekendType())) {
			daycarepersonlink.setCdWeekendType(dayCarePersonDto.getCdWeekendType());
		}

		if (!TypeConvUtil.isNullOrEmpty(dayCarePersonDto.getTxtComments())) {
			daycarepersonlink.setTxtComments(dayCarePersonDto.getTxtComments());
		}

		if (!TypeConvUtil.isNullOrEmpty(dayCarePersonDto.getIdlastUpdatePerson())) {
			daycarepersonlink.setIdLastUpdatePerson(dayCarePersonDto.getIdlastUpdatePerson());
		}

		if (!TypeConvUtil.isNullOrEmpty(dayCarePersonDto.getIdCreatedPerson())) {
			daycarepersonlink.setIdCreatedPerson(dayCarePersonDto.getIdCreatedPerson());
		}

		primaryKey = (Long) sessionFactory.getCurrentSession().save(daycarepersonlink);

		if (TypeConvUtil.isNullOrEmpty(primaryKey)) {
			throw new DataNotFoundException(
					messageSource.getMessage("dayCareRequestDao.insertDayCarePersonLink.NotFound", null, Locale.US));
		}
		return primaryKey;
	}

	/**
	 * Method Name: updateDayCarePersonLink Method Description:This method
	 * updates records into DAYCARE_PERSON_LINK table.
	 * 
	 * @param dayCarePersonDto
	 * @return Long @
	 */
	@Override
	public Long updateDayCarePersonLink(DayCarePersonDto dayCarePersonDto) {

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DaycarePersonLink.class);
		criteria.add(Restrictions.eq("idDaycarePersonLink", dayCarePersonDto.getIdDaycarePersonLink()));

		DaycarePersonLink daycarepersonlink = (DaycarePersonLink) criteria.uniqueResult();
		if (TypeConvUtil.isNullOrEmpty(daycarepersonlink)) {
			throw new DataNotFoundException(
					messageSource.getMessage("dayCareRequestDao.updateDayCarePersonLink.NotFound", null, Locale.US));
		}

		if (!TypeConvUtil.isNullOrEmpty(dayCarePersonDto.getCdDetermService())) {
			daycarepersonlink.setCdDetermService(dayCarePersonDto.getCdDetermService());
		}

		if (!TypeConvUtil.isNullOrEmpty(dayCarePersonDto.getCdRequestType())) {
			daycarepersonlink.setCdRequestType(dayCarePersonDto.getCdRequestType());
		}

		if (!TypeConvUtil.isNullOrEmpty(dayCarePersonDto.getCdDayCareType())) {
			daycarepersonlink.setCdDaycareType(dayCarePersonDto.getCdDayCareType());
		}

		if (!TypeConvUtil.isNullOrEmpty(dayCarePersonDto.getDtBegin())) {
			daycarepersonlink.setDtBegin(dayCarePersonDto.getDtBegin());
		}

		if (!TypeConvUtil.isNullOrEmpty(dayCarePersonDto.getDtEnd())) {
			daycarepersonlink.setDtEnd(dayCarePersonDto.getDtEnd());
		}

		if (!TypeConvUtil.isNullOrEmpty(dayCarePersonDto.getIdFacilityActive())) {
			daycarepersonlink.setIdFacility(dayCarePersonDto.getIdFacilityActive());
		}

		if (!TypeConvUtil.isNullOrEmpty(dayCarePersonDto.getTxtDayHoursNeeded())) {
			daycarepersonlink.setTxtHoursNeeded(dayCarePersonDto.getTxtDayHoursNeeded());
		}

		daycarepersonlink
				.setIndSun(ObjectUtils.isEmpty(dayCarePersonDto.getIndSun()) ? "N" : dayCarePersonDto.getIndSun());
		daycarepersonlink
				.setIndMon(ObjectUtils.isEmpty(dayCarePersonDto.getIndMon()) ? "N" : dayCarePersonDto.getIndMon());
		daycarepersonlink
				.setIndTue(ObjectUtils.isEmpty(dayCarePersonDto.getIndTue()) ? "N" : dayCarePersonDto.getIndTue());
		daycarepersonlink
				.setIndWed(ObjectUtils.isEmpty(dayCarePersonDto.getIndWed()) ? "N" : dayCarePersonDto.getIndWed());
		daycarepersonlink
				.setIndThu(ObjectUtils.isEmpty(dayCarePersonDto.getIndThu()) ? "N" : dayCarePersonDto.getIndThu());
		daycarepersonlink
				.setIndFri(ObjectUtils.isEmpty(dayCarePersonDto.getIndFri()) ? "N" : dayCarePersonDto.getIndFri());
		daycarepersonlink
				.setIndSat(ObjectUtils.isEmpty(dayCarePersonDto.getIndSat()) ? "N" : dayCarePersonDto.getIndSat());

		//Modified the code to remove the null check for warranty defect 12258
		daycarepersonlink.setIndVarSch(dayCarePersonDto.getIndVarSch());
		
		//Modified the code to remove the null check for warranty defect 12476		
		daycarepersonlink.setCdVarSchMaxDays(dayCarePersonDto.getCdVarSchMaxDays());

		if (!TypeConvUtil.isNullOrEmpty(dayCarePersonDto.getCdSummerType())) {
			daycarepersonlink.setCdSummerType(dayCarePersonDto.getCdSummerType());
		}

		if (!TypeConvUtil.isNullOrEmpty(dayCarePersonDto.getCdWeekendType())) {
			daycarepersonlink.setCdWeekendType(dayCarePersonDto.getCdWeekendType());
		}

		// artf125518 whatever the user input that will be saved, even null or ""
		daycarepersonlink.setTxtComments(dayCarePersonDto.getTxtComments());

		if (!TypeConvUtil.isNullOrEmpty(dayCarePersonDto.getIdlastUpdatePerson())) {
			daycarepersonlink.setIdLastUpdatePerson(dayCarePersonDto.getIdlastUpdatePerson());
		}

		if (!TypeConvUtil.isNullOrEmpty(dayCarePersonDto.getIdCreatedPerson())) {
			daycarepersonlink.setIdCreatedPerson(dayCarePersonDto.getIdCreatedPerson());
		}
		sessionFactory.getCurrentSession().saveOrUpdate(daycarepersonlink);
		return daycarepersonlink.getIdDaycarePersonLink();
	}

	/**
	 * Method Name: deleteDayCarePersonFacilLink Method Description:This method
	 * deletes all the records from DAYCARE_PERSON_FACIL_LINK table for the
	 * Child.
	 * 
	 * @param idDaycarePersonLink
	 * @return Long @
	 */
	@Override
	public Long deleteDayCarePersonFacilLink(Long idDaycarePersonFacilLink) {

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DaycarePersonFacilLink.class);
		criteria.add(Restrictions.eq("idDaycarePersonFacilLink", idDaycarePersonFacilLink));

		List<DaycarePersonFacilLink> daycarePersonFacilLinkList = criteria.list();
		if (TypeConvUtil.isNullOrEmpty(daycarePersonFacilLinkList)) {
			throw new DataNotFoundException(messageSource
					.getMessage("dayCareRequestDao.deleteDayCarePersonFacilLink.NotFound", null, Locale.US));
		}
		if (!CollectionUtils.isEmpty(daycarePersonFacilLinkList)) {
			for (DaycarePersonFacilLink daycarePersonFacilLink : daycarePersonFacilLinkList) {
				sessionFactory.getCurrentSession().delete(daycarePersonFacilLink);
			}
		}
		return (long) daycarePersonFacilLinkList.size();
	}

	/**
	 * Method Name: retrieveDayCarePersonFacilLink Method Description:This
	 * method fetches data from DAYCARE_PERSON_FACIL_LINK for the Child.
	 * 
	 * @param idDaycarePersonLink
	 * @param trueval
	 * @return List<DayCareFacilityDto> @
	 */
	@Override
	public List<DayCareFacilityDto> retrieveDayCarePersonFacilLink(Long idDaycarePersonLink, Boolean trueval) {

		List<DayCareFacilityDto> dayCareFacilityDtoList = new ArrayList<>();
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DaycarePersonFacilLink.class);
		criteria.add(Restrictions.eq("daycarePersonLink.idDaycarePersonLink", idDaycarePersonLink));
		if (trueval == ServiceConstants.FALSEVAL) {
			criteria.add(Restrictions.isNull("dtEnd"));
		}

		List<DaycarePersonFacilLink> daycarePersonFacilLinkList = criteria.list();
		if (TypeConvUtil.isNullOrEmpty(daycarePersonFacilLinkList)) {
			throw new DataNotFoundException(messageSource
					.getMessage("dayCareRequestDao.retrieveDayCarePersonFacilLink.NotFound", null, Locale.US));
		}
		for (DaycarePersonFacilLink daycarePersonFacilLink : daycarePersonFacilLinkList) {
			DayCareFacilityDto dayCareFacilityDto = new DayCareFacilityDto();
			dayCareFacilityDto.setIdDaycarePersonFacilLink(daycarePersonFacilLink.getIdDaycarePersonFacilLink());
			if (!TypeConvUtil.isNullOrEmpty(daycarePersonFacilLink.getDaycareRequest().getIdDaycareRequest())) {
				dayCareFacilityDto
						.setIdDayCareRequest(daycarePersonFacilLink.getDaycareRequest().getIdDaycareRequest());
			}
			if (!TypeConvUtil.isNullOrEmpty(daycarePersonFacilLink.getDaycarePersonLink().getIdDaycarePersonLink())) {
				dayCareFacilityDto
						.setIdDaycarePersonLink(daycarePersonFacilLink.getDaycarePersonLink().getIdDaycarePersonLink());
			}
			dayCareFacilityDto.setDtBegin(daycarePersonFacilLink.getDtBegin());
			dayCareFacilityDto.setDtEnd(daycarePersonFacilLink.getDtEnd());
			dayCareFacilityDto.setIdFacility(daycarePersonFacilLink.getIdFacility());
			dayCareFacilityDtoList.add(dayCareFacilityDto);
		}
		return dayCareFacilityDtoList;
	}

	/**
	 * Method Name: deleteDayCarePersonFacilLink Method Description:This method
	 * deletes the given Facility from DAYCARE_PERSON_FACIL_LINK table for the
	 * Child.
	 * 
	 * @param idFacility
	 * @param idDaycarePersonLink
	 * @return Long @
	 */
	@Override
	public Long deleteDayCarePersonFacilLink(Long idFacility, Long idDaycarePersonLink) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DaycarePersonFacilLink.class);
		criteria.add(Restrictions.eq("daycarePersonLink.idDaycarePersonLink", idDaycarePersonLink));
		criteria.add(Restrictions.eq("idFacility", idFacility));

		List<DaycarePersonFacilLink> daycarePersonFacilLinkList = criteria.list();
		if (TypeConvUtil.isNullOrEmpty(daycarePersonFacilLinkList)) {
			throw new DataNotFoundException(messageSource
					.getMessage("dayCareRequestDao.deleteDayCarePersonFacilLink.NotFound", null, Locale.US));
		}
		for (DaycarePersonFacilLink daycarePersonFacilLink : daycarePersonFacilLinkList) {
			sessionFactory.getCurrentSession().delete(daycarePersonFacilLink);
		}
		return (long) daycarePersonFacilLinkList.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.tx.state.dfps.service.dcr.dao.DayCareReqPersonDao#deleteTypeOfService(
	 * us.tx.state.dfps.service.dcr.dto.DayCarePersonValueDto)
	 */
	@Override
	public Long deleteTypeOfService(DayCarePersonDto dayCareRequestValueDto) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.tx.state.dfps.service.dcr.dao.DayCareReqPersonDao#
	 * insertDayCarePersonFacilLink(us.tx.state.dfps.service.dcr.dto.
	 * DayCareFacilityDto)
	 */
	@Override
	public Long insertDayCarePersonFacilLink(DayCareFacilityDto dayCareFacility) {

		Long primaryKey = ServiceConstants.LongZero;
		DaycarePersonFacilLink daycarePersonFacilLink = new DaycarePersonFacilLink();

		DaycareRequest daycareRequest = new DaycareRequest();

		DaycarePersonLink daycarePersonLink = new DaycarePersonLink();

		if (!ObjectUtils.isEmpty(daycareRequest)) {
			daycareRequest.setIdDaycareRequest(dayCareFacility.getIdDayCareRequest());
			daycarePersonFacilLink.setDaycareRequest(daycareRequest);
		}

		if (!ObjectUtils.isEmpty(daycarePersonLink)) {
			daycarePersonLink.setIdDaycarePersonLink(dayCareFacility.getIdDaycarePersonLink());
			daycarePersonFacilLink.setDaycarePersonLink(daycarePersonLink);
		}

		if (!TypeConvUtil.isNullOrEmpty(dayCareFacility.getIdFacility())) {
			daycarePersonFacilLink.setIdFacility(dayCareFacility.getIdFacility());
		}

		if (!TypeConvUtil.isNullOrEmpty(dayCareFacility.getDtBegin())) {
			daycarePersonFacilLink.setDtBegin(dayCareFacility.getDtBegin());
		}

		if (!TypeConvUtil.isNullOrEmpty(dayCareFacility.getDtEnd())) {
			daycarePersonFacilLink.setDtEnd(dayCareFacility.getDtEnd());
		}

		if (!TypeConvUtil.isNullOrEmpty(dayCareFacility.getIdlastUpdatePerson())) {
			daycarePersonFacilLink.setIdLastUpdatePerson(dayCareFacility.getIdlastUpdatePerson());
		}

		if (!TypeConvUtil.isNullOrEmpty(dayCareFacility.getIdCreatedPerson())) {
			daycarePersonFacilLink.setIdCreatedPerson(dayCareFacility.getIdCreatedPerson());
		}

		daycarePersonFacilLink.setDtCreated(new Date());
		daycarePersonFacilLink.setDtLastUpdate(new Date());
		primaryKey = (Long) sessionFactory.getCurrentSession().save(daycarePersonFacilLink);
		if (TypeConvUtil.isNullOrEmpty(primaryKey)) {
			throw new DataNotFoundException(messageSource
					.getMessage("dayCareRequestDao.insertDayCarePersonFacilLink.NotFound", null, Locale.US));
		}
		return primaryKey;
	}

	/**
	 * 
	 * Method Name: retrieveDayCarePersonLink Method Description: This is Dao
	 * implementation layer for retrieving Day care Person by day care request
	 * id
	 * 
	 * @param idDayCareRequest
	 * @return List<DayCarePersonDto>
	 */
	@SuppressWarnings({ "unchecked" })
	public List<DayCarePersonDto> retrieveDayCarePersonLink(Long idDayCareRequest) {

		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(retrieveDayCarePersonLinkSql);
		query.setParameter("idDayCareRequest", idDayCareRequest);
		query.addScalar("nmPersonFullName", StandardBasicTypes.STRING).addScalar("dtBirth", StandardBasicTypes.DATE)
				.addScalar("idDayCareRequest", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP)
				.addScalar("idlastUpdatePerson", StandardBasicTypes.LONG)
				.addScalar("dtCreated", StandardBasicTypes.DATE).addScalar("idCreatedPerson", StandardBasicTypes.LONG)
				.addScalar("idDaycarePersonLink", StandardBasicTypes.LONG)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("cdPersonType", StandardBasicTypes.STRING)
				.addScalar("cdDetermService", StandardBasicTypes.STRING)
				.addScalar("cdRequestType", StandardBasicTypes.STRING)
				.addScalar("cdDayCareType", StandardBasicTypes.STRING).addScalar("dtBegin", StandardBasicTypes.DATE)
				.addScalar("dtEnd", StandardBasicTypes.DATE).addScalar("idFacilityActive", StandardBasicTypes.LONG)
				.addScalar("txtDayHoursNeeded", StandardBasicTypes.STRING)
				.addScalar("idSSCCReferral", StandardBasicTypes.LONG).addScalar("indSun", StandardBasicTypes.STRING)
				.addScalar("indMon", StandardBasicTypes.STRING).addScalar("indTue", StandardBasicTypes.STRING)
				.addScalar("indWed", StandardBasicTypes.STRING).addScalar("indThu", StandardBasicTypes.STRING)
				.addScalar("indFri", StandardBasicTypes.STRING).addScalar("indSat", StandardBasicTypes.STRING)
				.addScalar("indVarSch", StandardBasicTypes.STRING)
				.addScalar("cdVarSchMaxDays", StandardBasicTypes.STRING)
				.addScalar("cdSummerType", StandardBasicTypes.STRING)
				.addScalar("cdWeekendType", StandardBasicTypes.STRING)
				.addScalar("txtComments", StandardBasicTypes.STRING).addScalar("streetLine1", StandardBasicTypes.STRING)
				.addScalar("streetLine2", StandardBasicTypes.STRING).addScalar("city", StandardBasicTypes.STRING)
				.addScalar("cdState", StandardBasicTypes.STRING).addScalar("county", StandardBasicTypes.STRING)
				.addScalar("zip", StandardBasicTypes.STRING).addScalar("personStreet", StandardBasicTypes.STRING)
				.addScalar("personCity", StandardBasicTypes.STRING).addScalar("personCounty", StandardBasicTypes.STRING)
				.addScalar("cdPersonRelInt", StandardBasicTypes.STRING);
		List<DayCarePersonDto> dayCarePersonDtos = query
				.setResultTransformer(Transformers.aliasToBean(DayCarePersonDto.class)).list();

		return dayCarePersonDtos;

	}

	/**
	 * artf263275 : based on the defect it was decided to change the query to match legacy impact
	 * Method Name: getDayCarePersonsInfo Method Description: This is Dao
	 * implementation layer for retrieving Day care Person by day care request
	 * id
	 *
	 * @param idDayCareRequest
	 * @return List<DayCarePersonDto>
	 */
	@SuppressWarnings({ "unchecked" })
	public List<DayCarePersonDto> getDayCarePersonsInfo(Long idDayCareRequest) {

		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getDayCarePersonsInfoSql);
		query.setParameter("idDayCareRequest", idDayCareRequest);
		query.addScalar("nmPersonFullName", StandardBasicTypes.STRING).addScalar("dtBirth", StandardBasicTypes.DATE)
				.addScalar("idDayCareRequest", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP)
				.addScalar("idlastUpdatePerson", StandardBasicTypes.LONG)
				.addScalar("dtCreated", StandardBasicTypes.DATE).addScalar("idCreatedPerson", StandardBasicTypes.LONG)
				.addScalar("idDaycarePersonLink", StandardBasicTypes.LONG)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("cdPersonType", StandardBasicTypes.STRING)
				.addScalar("cdDetermService", StandardBasicTypes.STRING)
				.addScalar("cdRequestType", StandardBasicTypes.STRING)
				.addScalar("cdDayCareType", StandardBasicTypes.STRING).addScalar("dtBegin", StandardBasicTypes.DATE)
				.addScalar("dtEnd", StandardBasicTypes.DATE).addScalar("idFacilityActive", StandardBasicTypes.LONG)
				.addScalar("txtDayHoursNeeded", StandardBasicTypes.STRING)
				.addScalar("idSSCCReferral", StandardBasicTypes.LONG).addScalar("indSun", StandardBasicTypes.STRING)
				.addScalar("indMon", StandardBasicTypes.STRING).addScalar("indTue", StandardBasicTypes.STRING)
				.addScalar("indWed", StandardBasicTypes.STRING).addScalar("indThu", StandardBasicTypes.STRING)
				.addScalar("indFri", StandardBasicTypes.STRING).addScalar("indSat", StandardBasicTypes.STRING)
				.addScalar("indVarSch", StandardBasicTypes.STRING)
				.addScalar("cdVarSchMaxDays", StandardBasicTypes.STRING)
				.addScalar("cdSummerType", StandardBasicTypes.STRING)
				.addScalar("cdWeekendType", StandardBasicTypes.STRING)
				.addScalar("txtComments", StandardBasicTypes.STRING).addScalar("streetLine1", StandardBasicTypes.STRING)
				.addScalar("streetLine2", StandardBasicTypes.STRING).addScalar("city", StandardBasicTypes.STRING)
				.addScalar("cdState", StandardBasicTypes.STRING).addScalar("county", StandardBasicTypes.STRING)
				.addScalar("zip", StandardBasicTypes.STRING).addScalar("personStreet", StandardBasicTypes.STRING)
				.addScalar("personCity", StandardBasicTypes.STRING).addScalar("personCounty", StandardBasicTypes.STRING);
		List<DayCarePersonDto> dayCarePersonDtos = query
				.setResultTransformer(Transformers.aliasToBean(DayCarePersonDto.class)).list();

		return dayCarePersonDtos;

	}

	/**
	 * Method Name: hasChangedSystemResponses Method Description:.
	 *
	 * @param idPerson
	 *            the id person
	 * @param idDayCareRequest
	 *            the id day care request
	 * @return true, if successful
	 */
	public boolean hasChangedSystemResponses(int idPerson, int idDayCareRequest) {

		SessionImpl sessionImpl = (SessionImpl) sessionFactory.getCurrentSession();
		Connection connection = sessionImpl.connection();
		CallableStatement callStatement = null;
		int hasChanged = 0;
		int errorCode = 0;
		String errorMessage = "";

		try {
			callStatement = connection.prepareCall(hasChangedSystemResponsesSql);
			callStatement.setInt(1, idPerson);
			callStatement.setInt(2, idDayCareRequest);

			callStatement.registerOutParameter(3, java.sql.Types.INTEGER);
			callStatement.registerOutParameter(4, java.sql.Types.VARCHAR);
			callStatement.registerOutParameter(5, java.sql.Types.BIGINT);

			callStatement.execute();

			hasChanged = callStatement.getInt(3);
			errorMessage = callStatement.getString(4);
			errorCode = callStatement.getInt(5);

			if (errorCode != 0) {
				throw new SQLException(errorMessage);
			}
		} catch (SQLException sqlExp) {
		} finally {
			try {
				callStatement.close();
			} catch (SQLException e) {

			}
		}

		return (hasChanged == 0) ? false : true;
	}

	/**
	 * Method Name: populateAddress Method Description: Populating the address
	 *
	 * @param idStage
	 *            the id stage
	 * @return the list
	 */
	@Override
	public List<DayCarePersonDto> populateAddress(Long idStage) {
		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(populateAddress);
		query.setParameter("idStage", idStage);
		query.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("streetLine1", StandardBasicTypes.STRING)
				.addScalar("streetLine2", StandardBasicTypes.STRING).addScalar("city", StandardBasicTypes.STRING)
				.addScalar("cdState", StandardBasicTypes.STRING).addScalar("county", StandardBasicTypes.STRING)
				.addScalar("zip", StandardBasicTypes.STRING).addScalar("personStreet", StandardBasicTypes.STRING)
				.addScalar("personCity", StandardBasicTypes.STRING)
				.addScalar("personCounty", StandardBasicTypes.STRING);

		List<DayCarePersonDto> dayCarePersonDtos = query
				.setResultTransformer(Transformers.aliasToBean(DayCarePersonDto.class)).list();

		return dayCarePersonDtos;
	}

	/**
	 * Method Name: deleteDayCarePersonLink Method Description:This method
	 * deletes the given idPerson from DAYCARE_PERSON_LINK table for the Child.
	 * 
	 * @param idDayCareRequest
	 * @param idPerson
	 * @return int
	 */
	@Override
	public int deleteDayCarePersonLink(Long idDayCareRequest, Long idPerson) {
		int value = 0;
		SQLQuery sQLQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(deleteDayCarePersonLink)
				.setParameter("idDayCareRequest", idDayCareRequest).setParameter("idPerson", idPerson);
		value = (int) sQLQuery.executeUpdate();
		return value;
	}

	/**
	 * Method Name: retrieveSvcAuthPersonCount Method Description: Retrieve the
	 * list of persons from dayCare related Service Auth
	 */
	@Override
	public Long retrieveSvcAuthPersonCount(Long idEvent) {
		Query query = sessionFactory.getCurrentSession().createQuery(retrieveSvcAuthPersonCount).setParameter("idEvent",
				idEvent);
		return (Long) query.uniqueResult();
	}
}
