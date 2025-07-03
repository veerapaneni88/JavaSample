package us.tx.state.dfps.service.familyplan.daoimpl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.CapsCase;
import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.common.domain.FamPlnAocGoalLink;
import us.tx.state.dfps.common.domain.FamPlnTaskGoalLink;
import us.tx.state.dfps.common.domain.FamilyPlanGoal;
import us.tx.state.dfps.common.domain.FamilyPlanItem;
import us.tx.state.dfps.common.dto.ErrorDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.response.FamilyPlanRes;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.familyTree.bean.FamilyPlanGoalValueDto;
import us.tx.state.dfps.service.familyplan.dao.FamilyPlanGoalDao;
import us.tx.state.dfps.service.forms.dto.FamilyPlanItemDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This class
 * is used to query the Family Plan Goal Oct 30, 2017- 3:08:13 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@Repository
public class FamilyPlanGoalDaoImpl implements FamilyPlanGoalDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	MessageSource messageSource;

	private static final Logger LOG = Logger.getLogger("ServiceBusiness-FamilyPlanGoalDaoImplLog");

	@Value("${FamilyPlanGoalDaoImpl.queryFGDMGoal}")
	private String queryFGDMGoal;

	@Value("${FamilyPlanGoalDaoImpl.getAreaOfConcernList}")
	private String getAreaOfConcernList;

	/**
	 * Method Name: queryFGDMFamilyGoal Method Description: This method is used
	 * to query the Family Plan Goal
	 *
	 * @param idCase
	 * @param idEvent
	 * @return List<FamilyPlanGoalValueDto>
	 */
	@Override
	public List<FamilyPlanGoalValueDto> queryFGDMGoal(Long idCase, Long idEvent) {

		LOG.debug("Entering method queryFGDMGoal in FamilyPlanGoalDaoImpl");

		SQLQuery sql = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(queryFGDMGoal)
				.setResultTransformer(Transformers.aliasToBean(FamilyPlanGoalValueDto.class));

		sql.addScalar("familyPlanGoalId", StandardBasicTypes.LONG);
		sql.addScalar("dateLastUpdate", StandardBasicTypes.TIMESTAMP);
		sql.addScalar("eventId", StandardBasicTypes.LONG);
		sql.addScalar("caseId", StandardBasicTypes.LONG);
		sql.addScalar("goalTxt", StandardBasicTypes.STRING);
		sql.addScalar("dateApproved", StandardBasicTypes.DATE);

		sql.setParameter("idEvent", idEvent);
		sql.setParameter("idCase", idCase);

		List<FamilyPlanGoalValueDto> familyPlanGoalValueDtos = sql.list();

		if (TypeConvUtil.isNullOrEmpty(familyPlanGoalValueDtos)) {
			throw new DataNotFoundException(messageSource
					.getMessage("FamilyPlanGoalDaoImpl.queryFGDMGoal.eventId.caseId.mandatory", null, Locale.US));
		}

		for (FamilyPlanGoalValueDto familyPlanGoalValueDto : familyPlanGoalValueDtos) {
			familyPlanGoalValueDto.setTaskAssociated(isTaskAssociated(familyPlanGoalValueDto.getFamilyPlanGoalId()));
		}

		LOG.debug("Exiting method queryFGDMGoal in FamilyPlanGoalDaoImpl");

		return familyPlanGoalValueDtos;

	}

	/**
	 * Method Name: isTaskAssociated Method Description: To check if the
	 * associated task is present or not
	 * 
	 * @param familyPlanGoalId
	 * @return Boolean
	 */
	private Boolean isTaskAssociated(Long familyPlanGoalId) {

		LOG.debug("Entering method isTaskAssociated in FamilyPlanGoalDaoImpl");

		Boolean taskPresent = Boolean.FALSE;

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(FamPlnTaskGoalLink.class);
		criteria.add(Restrictions.eq("idFamilyPlanGoal", familyPlanGoalId));

		List<FamPlnTaskGoalLink> famPlnTaskGoalLinks = criteria.list();

		if (!CollectionUtils.isEmpty(famPlnTaskGoalLinks)) {
			taskPresent = Boolean.TRUE;
		}

		LOG.debug("Exiting method isTaskAssociated in FamilyPlanGoalDaoImpl");

		return taskPresent;
	}

	/**
	 * Method Name: getAreaOfConcernList Method Description: This method returns
	 * a list of Area of Concern for a family plan goal
	 *
	 * @param idFamilyPlanGoal
	 * @return List<String>
	 */
	@Override
	public List<String> getAreaOfConcernList(Long idFamilyPlanGoal) {

		LOG.debug("Entering method getAreaOfConcernList in FamilyPlanGoalDaoImpl");

		List<String> areaOfConcernList = new ArrayList<String>();

		SQLQuery sql = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getAreaOfConcernList)
				.setResultTransformer(Transformers.aliasToBean(FamilyPlanItemDto.class));

		sql.addScalar("areaOfConcernCode", StandardBasicTypes.STRING);

		sql.setParameter("idFamilyPlanGoal", idFamilyPlanGoal);

		List<FamilyPlanItemDto> FamilyPlanItemDtos = sql.list();

		if (TypeConvUtil.isNullOrEmpty(FamilyPlanItemDtos)) {
			throw new DataNotFoundException(messageSource.getMessage(
					"FamilyPlanGoalDaoImpl.getAreaOfConcernList.idFamilyPlanGoal.mandatory", null, Locale.US));
		}

		for (FamilyPlanItemDto familyPlanItemDto : FamilyPlanItemDtos) {
			areaOfConcernList.add(familyPlanItemDto.getAreaOfConcernCode().trim());
		}

		LOG.debug("Exiting method getAreaOfConcernList in FamilyPlanGoalDaoImpl");

		return areaOfConcernList;
	}

	/**
	 * Method Name: deleteFGDMGoal Method Description: This method is used to
	 * delete a family plan goal from FAMILY_PLAN_TABLE, also entry in the
	 * FAM_PLN_AOC_GOAL_LINK and FAM_PLN_TASK_GOAL_LINK is deleted.
	 * 
	 * @param familyPlanGoalValueDto
	 * @return Long
	 */
	@Override
	public Long deleteFGDMGoal(FamilyPlanGoalValueDto familyPlanGoalValueDto) {

		LOG.debug("Entering method deleteFGDMGoal in FamilyPlanGoalDaoImpl");

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(FamPlnAocGoalLink.class);
		criteria.add(Restrictions.eq("idFamilyPlanGoal", familyPlanGoalValueDto.getFamilyPlanGoalId()));

		List<FamPlnAocGoalLink> famPlnAocGoalLinkList = criteria.list();

		if (TypeConvUtil.isNullOrEmpty(famPlnAocGoalLinkList)) {
			throw new DataNotFoundException(messageSource
					.getMessage("FamilyPlanGoalDaoImpl.deleteFGDMGoal.idFamilyPlanGoal.mandatory", null, Locale.US));
		}

		for (FamPlnAocGoalLink famPlnAocGoalLink : famPlnAocGoalLinkList) {
			sessionFactory.getCurrentSession().delete(famPlnAocGoalLink);
		}

		Criteria criteria1 = sessionFactory.getCurrentSession().createCriteria(FamPlnTaskGoalLink.class);
		criteria1.add(Restrictions.eq("idFamilyPlanGoal", familyPlanGoalValueDto.getFamilyPlanGoalId()));

		List<FamPlnTaskGoalLink> famPlnTaskGoalLinkList = criteria1.list();

		if (TypeConvUtil.isNullOrEmpty(famPlnTaskGoalLinkList)) {
			throw new DataNotFoundException(messageSource
					.getMessage("FamilyPlanGoalDaoImpl.deleteFGDMGoal.idFamilyPlanGoal.mandatory", null, Locale.US));
		}

		for (FamPlnTaskGoalLink famPlnTaskGoalLink : famPlnTaskGoalLinkList) {
			sessionFactory.getCurrentSession().delete(famPlnTaskGoalLink);
		}

		Criteria criteria2 = sessionFactory.getCurrentSession().createCriteria(FamilyPlanGoal.class);
		criteria2.add(Restrictions.eq("idFamilyPlanGoal", familyPlanGoalValueDto.getFamilyPlanGoalId()));

		List<FamilyPlanGoal> familyPlanGoalList = criteria2.list();

		if (TypeConvUtil.isNullOrEmpty(familyPlanGoalList)) {
			throw new DataNotFoundException(messageSource
					.getMessage("FamilyPlanGoalDaoImpl.deleteFGDMGoal.idFamilyPlanGoal.mandatory", null, Locale.US));
		}

		for (FamilyPlanGoal familyPlanGoal : familyPlanGoalList) {
			sessionFactory.getCurrentSession().delete(familyPlanGoal);
		}

		LOG.debug("Exiting method deleteFGDMGoal in FamilyPlanGoalDaoImpl");

		return familyPlanGoalValueDto.getFamilyPlanGoalId();
	}

	/**
	 * Method Name: saveOrUpdateFamilyPlanGoal Method Description: save the
	 * family goals with goal txt and area of concerns associated with a
	 * particular Family Plan event.
	 * 
	 * @param familyPlanGoalValueDtos
	 * @param idEvent
	 * @param idCase
	 * @return String
	 */
	@Override
	public FamilyPlanRes saveOrUpdateFamilyPlanGoal(List<FamilyPlanGoalValueDto> familyPlanGoalValueDtos, Long idEvent,
			Long idCase) {

		String returnMsg = "FAILED";
		FamilyPlanRes familyPlanRes = new FamilyPlanRes();

		// Loop through the family goals
		for (FamilyPlanGoalValueDto familyPlanGoalValueDto : familyPlanGoalValueDtos) {

			Set<FamPlnAocGoalLink> famPlnRemAocGoalLinks = new HashSet<>();
			FamilyPlanGoal familyPlanGoal = null;
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CapsCase.class)
					.add(Restrictions.eq("idCase", idCase));
			CapsCase capsCase = (CapsCase) criteria.uniqueResult();
			criteria = sessionFactory.getCurrentSession().createCriteria(Event.class)
					.add(Restrictions.eq("idEvent", idEvent));
			Event event = (Event) criteria.uniqueResult();

			criteria = sessionFactory.getCurrentSession().createCriteria(FamilyPlanGoal.class)
					.add(Restrictions.eq("idFamilyPlanGoal", familyPlanGoalValueDto.getFamilyPlanGoalId()));
			familyPlanGoal = (FamilyPlanGoal) criteria.uniqueResult();
			if (!ObjectUtils.isEmpty(familyPlanGoal)) {
				Timestamp newLastUpdatedTime = DateUtils
						.getDateyyyyMMddHHmmss(familyPlanGoalValueDto.getDateLastUpdate());
				if (!familyPlanGoal.getDtLastUpdate().equals(newLastUpdatedTime)) {
					ErrorDto errorDto = new ErrorDto();
					errorDto.setErrorCode(ServiceConstants.MSG_CMN_TMSTAMP_MISMATCH);
					familyPlanRes.setFamPlanGoalSave(returnMsg);
					familyPlanRes.setErrorDto(errorDto);
					return familyPlanRes;
				}

			} else {
				familyPlanGoal = new FamilyPlanGoal();
			}

			// Create Family Plan AOC Goal Links if one doesnt exist already
			if (null == familyPlanGoal.getFamPlnAocGoalLinks()) {
				familyPlanGoal.setFamPlnAocGoalLinks(new HashSet<FamPlnAocGoalLink>());
			}
			Set<FamPlnAocGoalLink> famPlnAocGoalLinks = familyPlanGoal.getFamPlnAocGoalLinks();
			familyPlanGoal.setCapsCase(capsCase);
			familyPlanGoal.setEvent(event);
			familyPlanGoal.setTxtGoal(familyPlanGoalValueDto.getGoalTxt());
			familyPlanGoal.setDtLastUpdate(new Date());

			criteria = sessionFactory.getCurrentSession().createCriteria(FamilyPlanItem.class)
					.add(Restrictions.eq("event.idEvent", idEvent));
			criteria.add(Restrictions.eq("capsCase.idCase", idCase));
			List<FamilyPlanItem> fpItemList = (List<FamilyPlanItem>) criteria.list();

			List<String> areaOfConcerns = familyPlanGoalValueDto.getAreaOfConcern();

			// removes the area of concerns that are not selected as goals
			// starts
			for (FamPlnAocGoalLink famPlnAocGoalLink : famPlnAocGoalLinks) {
				String cdArea = famPlnAocGoalLink.getFamilyPlanItem().getCdAreaConcern();
				boolean isExist = false;
				for (String cdAreas : areaOfConcerns) {
					if (cdAreas.equals(cdArea)) {
						isExist = true;
						break;
					}
				}
				if (!isExist) {
					famPlnRemAocGoalLinks.add(famPlnAocGoalLink);
				}
			}

			if (!(ObjectUtils.isEmpty(famPlnRemAocGoalLinks))) {
				for (FamPlnAocGoalLink string : famPlnRemAocGoalLinks) {
					famPlnAocGoalLinks.remove(string);
				}
			}
			// removes the area of concerns that are not selected as goals ends
			// insert the new are area of concerns that are selected for the
			// first time as goals starts
			for (String areaOfConcern : areaOfConcerns) {
				boolean isExist = false;
				for (FamPlnAocGoalLink famPlnAocGoalLink : famPlnAocGoalLinks) {
					String cdArea = famPlnAocGoalLink.getFamilyPlanItem().getCdAreaConcern();
					if (areaOfConcern.equals(cdArea)) {
						isExist = true;
						break;
					}
				}
				if (!isExist) {
					FamilyPlanItem familyPlan = fpItemList.stream()
							.filter(familyPlanItem -> areaOfConcern.equals(familyPlanItem.getCdAreaConcern())).findAny()
							.orElse(null);
					if (!ObjectUtils.isEmpty(familyPlan)) {
						FamPlnAocGoalLink famPlnAocGoalLink = new FamPlnAocGoalLink();
						famPlnAocGoalLink.setFamilyPlanGoal(familyPlanGoal);
						famPlnAocGoalLink.setCapsCase(capsCase);
						famPlnAocGoalLink.setEvent(event);
						famPlnAocGoalLink.setDtLastUpdate(new Date());
						famPlnAocGoalLink.setFamilyPlanItem(familyPlan);
						famPlnAocGoalLinks.add(famPlnAocGoalLink);
					}
				}
			}
			// insert the new are area of concerns that are selected for the
			// first time as goals ends
			sessionFactory.getCurrentSession().saveOrUpdate(familyPlanGoal);

			returnMsg = "SUCCESS";
		}

		familyPlanRes.setFamPlanGoalSave(returnMsg);
		return familyPlanRes;
	}

	/**
	 * Method Name: deleteFamPlanGoal Method Description: delete the family goal
	 * 
	 * @param idGoal
	 * @return String
	 */
	@Override
	public String deleteFamPlanGoal(Long idGoal) {

		String returnMsg = "FAILED";
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(FamilyPlanGoal.class)
				.add(Restrictions.eq("idFamilyPlanGoal", idGoal));
		FamilyPlanGoal familyPlanGoal = (FamilyPlanGoal) criteria.uniqueResult();
		if (!ObjectUtils.isEmpty(familyPlanGoal)) {
			sessionFactory.getCurrentSession().delete(familyPlanGoal);
			returnMsg = "SUCCESS";
		}

		return returnMsg;
	}

}
