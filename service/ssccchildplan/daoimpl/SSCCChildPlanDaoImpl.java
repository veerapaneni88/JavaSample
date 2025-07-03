package us.tx.state.dfps.service.ssccchildplan.daoimpl;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;

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
import org.springframework.util.ObjectUtils;

import pkware.DCL.InvalidDictionarySizeException;
import pkware.DCL.InvalidModeException;
import us.tx.state.dfps.common.domain.ChildPlan;
import us.tx.state.dfps.common.domain.ChildPlanParticip;
import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.common.domain.SsccChildPlan;
import us.tx.state.dfps.common.domain.SsccChildPlanParticip;
import us.tx.state.dfps.common.domain.SsccChildPlanTopic;
import us.tx.state.dfps.service.childplan.dao.ChildPlanBeanDao;
import us.tx.state.dfps.service.childplan.dto.SSCCChildPlanDto;
import us.tx.state.dfps.service.childplan.dto.SSCCChildPlanGuideTopicDto;
import us.tx.state.dfps.service.childplan.dto.SSCCChildPlanParticipDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.common.util.pkware.DCL.Base64;
import us.tx.state.dfps.service.exception.DataLayerException;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.lookup.dao.LookupDao;
import us.tx.state.dfps.service.person.daoimpl.CompressionHelper;
import us.tx.state.dfps.service.ssccchildplan.dao.SSCCChildPlanDao;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:SSCCChildPlanDaoImpl - Performs some of the database activities
 * the Child Plan Conversation. Nov 1, 2017- 1:18:22 PM © 2017 Texas Department
 * of Family and Protective Services
 */
@Repository
public class SSCCChildPlanDaoImpl implements SSCCChildPlanDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	LookupDao lookupDao;

	@Value("${SSCCChildPlanDaoImpl.querySSCCChildPlanSql}")
	private String querySSCCChildPlanSql;

	@Value("${SSCCChildPlanDaoImpl.querySSCCChildPlanByPKSql}")
	private String querySSCCChildPlanByPKSql;

	@Value("${SSCCChildPlanDaoImpl.queryLastUpdateParticipSql}")
	private String queryLastUpdateParticipSql;

	@Value("${SSCCChildPlanDaoImpl.updateChildPlanTopicSql}")
	private String updateChildPlanTopicSql;

	@Value("${SSCCChildPlanDaoImpl.getActiveSSCCReferralSql}")
	private String getActiveSSCCReferralSql;

	@Value("${SSCCChildPlanDaoImpl.getChildPlanParticipListSql}")
	private String getChildPlanParticipListSql;

	@Value("${SSCCChildPlanDaoImpl.queryAssignedTopicsSql}")
	private String queryAssignedTopicsSql;

	@Value("${SSCCChildPlanDaoImpl.querySSCCChildPlanParticipantsSql}")
	private String querySSCCChildPlanParticipantsSql;

	@Value("${SSCCChildPlanDaoImpl.querySSCCChildPlanTopicDataForDeletedCPSql}")
	private String querySSCCChildPlanTopicDataForDeletedCPSql;

	@Value("${SSCCChildPlanDaoImpl.querySSCCChildPlanTopicDataSql}")
	private String querySSCCChildPlanTopicDataSql;

	@Value("${SSCCChildPlanDaoImpl.querySSCCChildPlanTopicSql}")
	private String querySSCCChildPlanTopicSql;

	@Value("${SSCCChildPlanDaoImpl.selectNewUsingChildPlanTopicSql}")
	private String selectNewUsingChildPlanTopicSql;

	@Value("${SSCCChildPlanDaoImpl.clearChildPlanTopicSql}")
	private String clearChildPlanTopicSql;

	@Autowired
	ChildPlanBeanDao childPlanBeanDao;

	private static final Logger LOG = Logger.getLogger("SSCCChildPlanDaoImpl-SSCCChildPlanDaoImplLog");

	/**
	 * Method Name: deleteSSCCParticipant Method Description: Deletes
	 * participant from the database.
	 * 
	 * @param inSSCCChildPlanParticipDto
	 * @return Long
	 */
	@Override
	public Long deleteSSCCParticipant(SSCCChildPlanParticipDto inSSCCChildPlanParticipDto) {

		LOG.debug("Entering method deleteSSCCParticipant in SSCCChildPlanDaoImpl");

		Long updateResult = 0l;

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(SsccChildPlanParticip.class);

		criteria.add(
				Restrictions.eq("idSsccChildPlanParticip", inSSCCChildPlanParticipDto.getIdSSCCChildPlanParticipant()));

		SsccChildPlanParticip ssccchildplanparticip = (SsccChildPlanParticip) criteria.uniqueResult();

		if (!ObjectUtils.isEmpty(ssccchildplanparticip)) {
			sessionFactory.getCurrentSession().delete(ssccchildplanparticip);
			updateResult++;
		}

		LOG.debug("Exiting method deleteSSCCParticipant in SSCCChildPlanDaoImpl");

		return updateResult;
	}

	/**
	 * Method Name: deleteSSCCTopicForPlan Method Description: Deletes
	 * associated topics from the database.
	 * 
	 * @param inSSCCChildPlanDto
	 * @param next
	 * @return Long
	 * @throws DataNotFoundException
	 */
	@Override
	public Long deleteSSCCTopicForPlan(SSCCChildPlanDto inSSCCChildPlanDto, String cdTopic) {

		LOG.debug("Entering method deleteSSCCTopicForPlan in SSCCChildPlanDaoImpl");

		Long updateResult = 0l;

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(SsccChildPlanTopic.class);

		criteria.add(Restrictions.eq("idSsccChildPlan", inSSCCChildPlanDto.getIdSsccChildPlan()));
		criteria.add(Restrictions.eq("cdCpTopic", cdTopic));

		SsccChildPlanTopic ssccchildplantopic = (SsccChildPlanTopic) criteria.uniqueResult();

		if (!ObjectUtils.isEmpty(ssccchildplantopic)) {
			sessionFactory.getCurrentSession().delete(ssccchildplantopic);
			updateResult++;
		}

		LOG.debug("Exiting method deleteSSCCTopicForPlan in SSCCChildPlanDaoImpl");

		return updateResult;
	}

	/**
	 * Method Name: unlinkSSCCChildPlan Method Description: Unlinks the sscc
	 * child plan data from the child plan event.
	 * 
	 * @param inSSCCChildPlanDto
	 * @return Long
	 */
	@Override
	public Long unlinkSSCCChildPlan(SSCCChildPlanDto inSSCCChildPlanDto) {

		LOG.debug("Entering method unlinkSSCCChildPlan in SSCCChildPlanDaoImpl");

		Long updateResult = 0l;

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(SsccChildPlan.class);

		criteria.add(Restrictions.eq("idSsccChildPlan", inSSCCChildPlanDto.getIdSsccChildPlan()));

		SsccChildPlan childplan = (SsccChildPlan) criteria.uniqueResult();

		if (!ObjectUtils.isEmpty(childplan)) {

			childplan.setIdChildPlanEvent(null);

			sessionFactory.getCurrentSession().saveOrUpdate(childplan);
			updateResult++;
		}

		LOG.debug("Exiting method unlinkSSCCChildPlan in SSCCChildPlanDaoImpl");

		return updateResult;
	}

	/**
	 * Method Name: insertChildPlanParticip Method Description: Inserts the sscc
	 * child plan participant data into the child plan participant table on
	 * approval.
	 * 
	 * @param next
	 * @param ulIdEvent
	 * @param idCase
	 */
	@Override
	public Long insertChildPlanParticip(SSCCChildPlanParticipDto inSSCCChildPlanParticipDto, Long idEvent,
			Long idCase) {

		LOG.debug("Entering method insertChildPlanParticip in SSCCChildPlanDaoImpl");

		long updateResult = 0;

		ChildPlanParticip childplanparticip = new ChildPlanParticip();

		if (!TypeConvUtil.isNullOrEmpty(inSSCCChildPlanParticipDto.getIdPerson())) {

			childplanparticip.setIdPerson(inSSCCChildPlanParticipDto.getIdPerson());

		}

		if (!TypeConvUtil.isNullOrEmpty(idEvent)) {

			Event event = (Event) sessionFactory.getCurrentSession().get(Event.class, idEvent);

			if (!TypeConvUtil.isNullOrEmpty(event)) {
				childplanparticip.setEvent(event);
			} else {
				throw new DataNotFoundException(messageSource
						.getMessage("SSCCChildPlanDao.insertChildPlanParticip.data.not.found", null, Locale.US));
			}

		}

		if (!TypeConvUtil.isNullOrEmpty(idCase)) {
			childplanparticip.setIdCase(idCase);
		}

		if (!TypeConvUtil.isNullOrEmpty(inSSCCChildPlanParticipDto.getCdCspPartNotifType())) {
			childplanparticip.setCdCspPartNotifType(inSSCCChildPlanParticipDto.getCdCspPartNotifType());
		}

		if (!TypeConvUtil.isNullOrEmpty(inSSCCChildPlanParticipDto.getCdCspPartType())) {
			childplanparticip.setCdCspPartType(inSSCCChildPlanParticipDto.getCdCspPartType());
		}

		if (!TypeConvUtil.isNullOrEmpty(inSSCCChildPlanParticipDto.getDtCspNotif())) {
			childplanparticip.setDtCspDateNotified(inSSCCChildPlanParticipDto.getDtCspNotif());
		}

		if (!TypeConvUtil.isNullOrEmpty(inSSCCChildPlanParticipDto.getDtCspPartCopyGiven())) {
			childplanparticip.setDtCspPartCopyGiven(inSSCCChildPlanParticipDto.getDtCspPartCopyGiven());
		}

		if (!TypeConvUtil.isNullOrEmpty(inSSCCChildPlanParticipDto.getDtCspPartParticipate())) {
			childplanparticip.setDtCspPartParticipate(inSSCCChildPlanParticipDto.getDtCspPartParticipate());
		}

		if (!TypeConvUtil.isNullOrEmpty(inSSCCChildPlanParticipDto.getNmCspPartFulls())) {
			childplanparticip.setNmCspPartFull(inSSCCChildPlanParticipDto.getNmCspPartFulls());
		}

		if (!TypeConvUtil.isNullOrEmpty(inSSCCChildPlanParticipDto.getDsCspPartRel())) {
			childplanparticip.setSdsCspPartRelationship(inSSCCChildPlanParticipDto.getDsCspPartRel());
		}

		childplanparticip.setDtLastUpdate(new Date());

		sessionFactory.getCurrentSession().save(childplanparticip);

		updateResult++;

		if (updateResult <= 0) {
			throw new DataNotFoundException(messageSource
					.getMessage("SSCCChildPlanDao.insertChildPlanParticip.data.not.found", null, Locale.US));
		}

		LOG.debug("Exiting method insertChildPlanParticip in SSCCChildPlanDaoImpl");

		return updateResult;
	}

	/**
	 * Method Name: insertSSCCChildPlan Method Description: Inserts the sscc
	 * child plan data into the database.
	 * 
	 * @param inSSCCChildPlanDto
	 * @return Long
	 */
	@Override
	public Long insertSSCCChildPlan(SSCCChildPlanDto inSSCCChildPlanDto) {

		LOG.debug("Entering method insertSSCCChildPlan in SSCCChildPlanDaoImpl");

		long updateResult = 0;

		SsccChildPlan ssccchildplan = new SsccChildPlan();

		ssccchildplan.setDtCreated(new Date());

		if (!ObjectUtils.isEmpty(inSSCCChildPlanDto.getIdCreatedPerson())) {
			ssccchildplan.setIdCreatedPerson(inSSCCChildPlanDto.getIdCreatedPerson());
		}

		if (!ObjectUtils.isEmpty(inSSCCChildPlanDto.getIdLastUpdatePerson())) {
			ssccchildplan.setIdLastUpdatePerson(inSSCCChildPlanDto.getIdLastUpdatePerson());
		}

		if (!ObjectUtils.isEmpty(inSSCCChildPlanDto.getIdSSCCReferral())) {
			ssccchildplan.setIdSsccReferral(inSSCCChildPlanDto.getIdSSCCReferral());
		}

		if (!ObjectUtils.isEmpty(inSSCCChildPlanDto.getIdChildPlanEvent())) {
			ssccchildplan.setIdChildPlanEvent(inSSCCChildPlanDto.getIdChildPlanEvent());
		}

		if (!ObjectUtils.isEmpty(inSSCCChildPlanDto.getIdEventNewUsed())) {
			ssccchildplan.setIdCpEventNewUsed(inSSCCChildPlanDto.getIdEventNewUsed());
		}

		ssccchildplan.setCdStatus("20");
		ssccchildplan.setIndReadyReview("N");
		ssccchildplan.setDtDatetimeRecorded(new Date());
		ssccchildplan.setCdParticipStatus("20");

		sessionFactory.getCurrentSession().save(ssccchildplan);
		updateResult++;

		LOG.debug("Exiting method insertSSCCChildPlan in SSCCChildPlanDaoImpl");

		return updateResult;
	}

	/**
	 * Method Name: insertSSCCChildPlanParticip Method Description: inserts the
	 * sscc child plan participant.
	 * 
	 * @param inSSCCChildPlanParticipDto
	 * @return Long
	 */
	@Override
	public Long insertSSCCChildPlanParticip(SSCCChildPlanParticipDto inSSCCChildPlanParticipDto) {

		LOG.debug("Entering method insertSSCCChildPlanParticip in SSCCChildPlanDaoImpl");

		long updateResult = 0;

		SsccChildPlanParticip ssccchildplanparticip = new SsccChildPlanParticip();
		ssccchildplanparticip.setDtCreated(new Date());
		ssccchildplanparticip.setDtLastUpdate(new Date());

		if (!ObjectUtils.isEmpty(inSSCCChildPlanParticipDto.getIdCreatedPerson())) {
			ssccchildplanparticip.setIdCreatedPerson(inSSCCChildPlanParticipDto.getIdCreatedPerson());
		}

		if (!ObjectUtils.isEmpty(inSSCCChildPlanParticipDto.getIdLastUpdatePerson())) {
			ssccchildplanparticip.setIdLastUpdatePerson(inSSCCChildPlanParticipDto.getIdLastUpdatePerson());
		}

		if (!ObjectUtils.isEmpty(inSSCCChildPlanParticipDto.getIdSCCCChildPlan())) {
			ssccchildplanparticip.setIdSsccChildPlan(inSSCCChildPlanParticipDto.getIdSCCCChildPlan());
		}

		if (!ObjectUtils.isEmpty(inSSCCChildPlanParticipDto.getIdPerson())) {
			ssccchildplanparticip.setIdPerson(inSSCCChildPlanParticipDto.getIdPerson());
		}

		if (!ObjectUtils.isEmpty(inSSCCChildPlanParticipDto.getNmCspPartFulls())) {
			ssccchildplanparticip.setNmCspPartFulls(inSSCCChildPlanParticipDto.getNmCspPartFulls());
		}

		if (!ObjectUtils.isEmpty(inSSCCChildPlanParticipDto.getDsCspPartRel())) {
			ssccchildplanparticip.setDsCspPartRelationship(inSSCCChildPlanParticipDto.getDsCspPartRel());
		}

		ssccchildplanparticip.setCdCspPartNotifType(ServiceConstants.SPACE);
		if (!ObjectUtils.isEmpty(inSSCCChildPlanParticipDto.getCdCspPartNotifType())) {
			ssccchildplanparticip.setCdCspPartNotifType(inSSCCChildPlanParticipDto.getCdCspPartNotifType());
		}

		if (!ObjectUtils.isEmpty(inSSCCChildPlanParticipDto.getCdCspPartType())) {
			ssccchildplanparticip.setCdCspPartType(inSSCCChildPlanParticipDto.getCdCspPartType());
		}

		if (!ObjectUtils.isEmpty(inSSCCChildPlanParticipDto.getDtCspNotif())) {
			ssccchildplanparticip.setDtCspDateNotified(inSSCCChildPlanParticipDto.getDtCspNotif());
		}

		if (!ObjectUtils.isEmpty(inSSCCChildPlanParticipDto.getDtCspPartCopyGiven())) {
			ssccchildplanparticip.setDtCspPartCopyGiven(inSSCCChildPlanParticipDto.getDtCspPartCopyGiven());
		}

		if (!ObjectUtils.isEmpty(inSSCCChildPlanParticipDto.getDtCspPartParticipate())) {
			ssccchildplanparticip.setDtCspPartParticipate(inSSCCChildPlanParticipDto.getDtCspPartParticipate());
		}

		sessionFactory.getCurrentSession().save(ssccchildplanparticip);

		updateResult++;

		LOG.debug("Exiting method insertSSCCChildPlanParticip in SSCCChildPlanDaoImpl");

		return updateResult;
	}

	/**
	 * Method Name: updateChildPlanPermGoals Method Description: Inserts the
	 * details of SSCCChildPlanValueBean perm goals into CHILD_PLAN on approval.
	 *
	 * @param inSSCCChildPlanDto
	 * @return Long
	 */
	@Override
	public Long updateChildPlanPermGoals(SSCCChildPlanDto inSSCCChildPlanDto) {

		LOG.debug("Entering method updateChildPlanPermGoals in SSCCChildPlanDaoImpl");

		Long updateResult = 0l;

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ChildPlan.class);

		criteria.add(Restrictions.eq("idChildPlanEvent", inSSCCChildPlanDto.getIdChildPlanEvent()));

		ChildPlan childplan = (ChildPlan) criteria.uniqueResult();

		if (!ObjectUtils.isEmpty(childplan)) {

			if (!ObjectUtils.isEmpty(inSSCCChildPlanDto.getDtCspPermGoalTarget())) {
				childplan.setDtCspPermGoalTarget(inSSCCChildPlanDto.getDtCspPermGoalTarget());
			}

			// txtCspLengthOfStay
			if (!ObjectUtils.isEmpty(inSSCCChildPlanDto.getLengthOfStay())) {
				childplan.setTxtCspLengthOfStay(inSSCCChildPlanDto.getLengthOfStay());
			}

			// txtCspLosDiscrepancy
			if (!ObjectUtils.isEmpty(inSSCCChildPlanDto.getDiscrepancy())) {
				childplan.setTxtCspLosDiscrepancy(inSSCCChildPlanDto.getDiscrepancy());
			}

			// indParentsParticipated
			if (!ObjectUtils.isEmpty(inSSCCChildPlanDto.getIndParentsParticipated())) {
				childplan.setIndParentsParticipated(inSSCCChildPlanDto.getIndParentsParticipated());
			}

			// txtCspParticipComment
			if (!ObjectUtils.isEmpty(inSSCCChildPlanDto.getParticipComment())) {
				childplan.setTxtCspParticipComment(inSSCCChildPlanDto.getParticipComment());
			}

			sessionFactory.getCurrentSession().saveOrUpdate(childplan);
			updateResult++;

		}

		LOG.debug("Exiting method updateChildPlanPermGoals in SSCCChildPlanDaoImpl");

		return updateResult;
	}

	/**
	 * Method Name: updateChildPlanTopic Method Description: Inserts the sscc
	 * child plan topic data into the corresponding child plan topic table on
	 * approval.
	 * 
	 * @param inSSCCChildPlanDto
	 * @param inSSCCChildPlanGuideTopicDto
	 * @return Long
	 */
	@Override
	public Long updateChildPlanTopic(SSCCChildPlanDto inSSCCChildPlanDto,
			SSCCChildPlanGuideTopicDto inSSCCChildPlanGuideTopicDto) {

		LOG.debug("Entering method updateChildPlanTopic in SSCCChildPlanDaoImpl");

		String table = lookupDao.simpleDecodeSafe(ServiceConstants.CCPTPTBL, inSSCCChildPlanGuideTopicDto.getStCode());
		String sql = MessageFormat.format(updateChildPlanTopicSql, new Object[] { table });

		SQLQuery queryUpdateRecords = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(sql)

				.setParameter("mBlob", inSSCCChildPlanGuideTopicDto.getStBlob())
				.setParameter("mUlIdEvent", inSSCCChildPlanDto.getIdEvent());

		long rowCountOne = queryUpdateRecords.executeUpdate();

		if (TypeConvUtil.isNullOrEmpty(rowCountOne)) {
			throw new DataNotFoundException(
					messageSource.getMessage("SSCCChildPlanDao.updateChildPlanTopic.data.not.found", null, Locale.US));
		}

		LOG.debug("Exiting method updateChildPlanTopic in SSCCChildPlanDaoImpl");

		return rowCountOne;
	}

	/**
	 * Method Name: updateSSCCChildPlan Method Description: Saves the sscc child
	 * plan data to the database.
	 * 
	 * @param inSSCCChildPlanDto
	 * @return Long
	 */
	@Override
	public Long updateSSCCChildPlan(SSCCChildPlanDto inSSCCChildPlanDto) {

		LOG.debug("Entering method updateSSCCChildPlan in SSCCChildPlanDaoImpl");

		Long updateResult = 0l;

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(SsccChildPlan.class);

		criteria.add(Restrictions.eq("idChildPlanEvent", inSSCCChildPlanDto.getIdChildPlanEvent()));

		SsccChildPlan ssccchildplan = (SsccChildPlan) criteria.uniqueResult();

		if (!ObjectUtils.isEmpty(ssccchildplan)) {

			// idLastUpdatePerson
			if (!ObjectUtils.isEmpty(inSSCCChildPlanDto.getIdLastUpdatePerson())) {
				ssccchildplan.setIdLastUpdatePerson(inSSCCChildPlanDto.getIdLastUpdatePerson());
			}

			// dtCspPermGoalTarget
			if (!ObjectUtils.isEmpty(inSSCCChildPlanDto.getDtCspPermGoalTarget())) {
				ssccchildplan.setDtCspPermGoalTarget(inSSCCChildPlanDto.getDtCspPermGoalTarget());
			}

			// txtCspLengthOfStay
			if (!ObjectUtils.isEmpty(inSSCCChildPlanDto.getLengthOfStay())) {
				ssccchildplan.setTxtCspLengthOfStay(inSSCCChildPlanDto.getLengthOfStay());
			}

			// txtCspLosDiscrepency
			if (!ObjectUtils.isEmpty(inSSCCChildPlanDto.getDiscrepancy())) {
				ssccchildplan.setTxtCspLosDiscrepency(inSSCCChildPlanDto.getDiscrepancy());
			}

			// cdCspPlanPermGoal
			if (!ObjectUtils.isEmpty(inSSCCChildPlanDto.getCdCspPlanPermGoal())) {
				ssccchildplan.setCdCspPlanPermGoal(inSSCCChildPlanDto.getCdCspPlanPermGoal());
			}

			// cdCspPlanType
			if (!ObjectUtils.isEmpty(inSSCCChildPlanDto.getCdCspPlanType())) {
				ssccchildplan.setCdCspPlanType(inSSCCChildPlanDto.getCdCspPlanType());
			}

			// dtCspNextReview
			if (!ObjectUtils.isEmpty(inSSCCChildPlanDto.getDtCspNextReview())) {
				ssccchildplan.setDtCspNextReview(inSSCCChildPlanDto.getDtCspNextReview());
			}

			// txtCspParticipComment
			if (!ObjectUtils.isEmpty(inSSCCChildPlanDto.getParticipComment())) {
				ssccchildplan.setTxtCspParticipComment(inSSCCChildPlanDto.getParticipComment());
			}

			// dtCspPlanCompleted
			if (!ObjectUtils.isEmpty(inSSCCChildPlanDto.getDtCspPlanCompleted())) {
				ssccchildplan.setDtCspPlanCompleted(inSSCCChildPlanDto.getDtCspPlanCompleted());
			}

			// dtInitialTransitionPlan
			if (!ObjectUtils.isEmpty(inSSCCChildPlanDto.getDtInitialTransPlan())) {
				ssccchildplan.setDtInitialTransitionPlan(inSSCCChildPlanDto.getDtInitialTransPlan());
			}

			// indParentsParticipated
			if (!ObjectUtils.isEmpty(inSSCCChildPlanDto.getIndParentsParticipated())) {
				ssccchildplan.setIndParentsParticipated(inSSCCChildPlanDto.getIndParentsParticipated());
			}

			// indReadyReview
			if (!ObjectUtils.isEmpty(inSSCCChildPlanDto.getIndReadyForReview())) {
				ssccchildplan.setIndReadyReview(inSSCCChildPlanDto.getIndReadyForReview());
			}

			// cdStatus
			if (!ObjectUtils.isEmpty(inSSCCChildPlanDto.getCdStatus())) {
				ssccchildplan.setCdStatus(inSSCCChildPlanDto.getCdStatus());
			}

			// txtInfoNotAvail
			if (!ObjectUtils.isEmpty(inSSCCChildPlanDto.getInfoNotAvail())) {
				ssccchildplan.setTxtInfoNotAvail(inSSCCChildPlanDto.getInfoNotAvail());
			}

			// txtOtherAssmt
			if (!ObjectUtils.isEmpty(inSSCCChildPlanDto.getOtherAssmt())) {
				ssccchildplan.setTxtOtherAssmt(inSSCCChildPlanDto.getOtherAssmt());
			}

			// indNoConGoal
			if (!ObjectUtils.isEmpty(inSSCCChildPlanDto.getIndNoConGoal())) {
				ssccchildplan.setIndNoConGoal(inSSCCChildPlanDto.getIndNoConGoal());
			}

			// txtNoConGoal
			if (!ObjectUtils.isEmpty(inSSCCChildPlanDto.getNoConGoal())) {
				ssccchildplan.setTxtNoConGoal(inSSCCChildPlanDto.getNoConGoal());
			}

			sessionFactory.getCurrentSession().saveOrUpdate(ssccchildplan);

			updateResult++;

		}

		LOG.debug("Exiting method updateSSCCChildPlan in SSCCChildPlanDaoImpl");

		return updateResult;
	}

	/**
	 * Method Name: updateSSCCChildPlanParticip Method Description: inserts the
	 * sscc child plan participant.
	 * 
	 * @param inSSCCChildPlanParticipDto
	 * @return Long
	 */
	@Override
	public Long updateSSCCChildPlanParticip(SSCCChildPlanParticipDto inSSCCChildPlanParticipDto) {

		LOG.debug("Entering method updateSSCCChildPlanParticip in SSCCChildPlanDaoImpl");

		Long updateResult = 0l;

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(SsccChildPlanParticip.class);

		criteria.add(
				Restrictions.eq("idSsccChildPlanParticip", inSSCCChildPlanParticipDto.getIdSSCCChildPlanParticipant()));

		SsccChildPlanParticip ssccchildplanparticip = (SsccChildPlanParticip) criteria.uniqueResult();

		if (!ObjectUtils.isEmpty(ssccchildplanparticip)) {

			// idLastUpdatePerson
			ssccchildplanparticip.setIdLastUpdatePerson(inSSCCChildPlanParticipDto.getIdLastUpdatePerson());

			// idPerson
			ssccchildplanparticip.setIdPerson(inSSCCChildPlanParticipDto.getIdPerson());

			// nmCspPartFulls
			ssccchildplanparticip.setNmCspPartFulls(inSSCCChildPlanParticipDto.getNmCspPartFulls());

			// dsCspPartRelationship
			ssccchildplanparticip.setDsCspPartRelationship(inSSCCChildPlanParticipDto.getDsCspPartRel());

			// cdCspPartNotifType
			ssccchildplanparticip.setCdCspPartNotifType(ServiceConstants.SPACE);
			if(!ObjectUtils.isEmpty(inSSCCChildPlanParticipDto.getCdCspPartNotifType()))
				ssccchildplanparticip.setCdCspPartNotifType(inSSCCChildPlanParticipDto.getCdCspPartNotifType());

			// cdCspPartType
			ssccchildplanparticip.setCdCspPartType(inSSCCChildPlanParticipDto.getCdCspPartType());

			// dtCspDateNotified
			ssccchildplanparticip.setDtCspDateNotified(inSSCCChildPlanParticipDto.getDtCspNotif());

			// dtCspPartCopyGiven
			ssccchildplanparticip.setDtCspPartCopyGiven(inSSCCChildPlanParticipDto.getDtCspPartCopyGiven());

			// dtCspPartParticipate
			ssccchildplanparticip.setDtCspPartParticipate(inSSCCChildPlanParticipDto.getDtCspPartParticipate());

			sessionFactory.getCurrentSession().saveOrUpdate(ssccchildplanparticip);
			updateResult++;

		}

		LOG.debug("Exiting method updateSSCCChildPlanParticip in SSCCChildPlanDaoImpl");

		return updateResult;
	}

	/**
	 * Method Name: updateSSCCChildPlanParticipStatus Method Description:
	 * updates the sscc child plan participant status.
	 * 
	 * @param inSSCCChildPlanDto
	 * @return Long
	 */
	@Override
	public Long updateSSCCChildPlanParticipStatus(SSCCChildPlanDto inSSCCChildPlanDto) {

		LOG.debug("Entering method updateSSCCChildPlanParticipStatus in SSCCChildPlanDaoImpl");

		Long updateResult = 0l;

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(SsccChildPlan.class);

		criteria.add(Restrictions.eq("idChildPlanEvent", inSSCCChildPlanDto.getIdEvent()));

		SsccChildPlan ssccchildplan = (SsccChildPlan) criteria.uniqueResult();

		if (!ObjectUtils.isEmpty(ssccchildplan)) {

			// idLastUpdatePerson
			if (!ObjectUtils.isEmpty(inSSCCChildPlanDto.getIdLastUpdatePerson())) {
				ssccchildplan.setIdLastUpdatePerson(inSSCCChildPlanDto.getIdLastUpdatePerson());
			}

			// cdParticipStatus
			if (!ObjectUtils.isEmpty(inSSCCChildPlanDto.getCdParticipStatus())) {
				ssccchildplan.setCdParticipStatus(inSSCCChildPlanDto.getCdParticipStatus());
			}

			sessionFactory.getCurrentSession().saveOrUpdate(ssccchildplan);
			updateResult++;

		}

		LOG.debug("Exiting method updateSSCCChildPlanParticipStatus in SSCCChildPlanDaoImpl");

		return updateResult;
	}

	/**
	 * Method Name: updateSSCCChildPlanStatus Method Description: Updates sscc
	 * child plan status in the database.
	 * 
	 * @param inSSCCChildPlanDto
	 * @return Long
	 */
	@Override
	public Long updateSSCCChildPlanStatus(SSCCChildPlanDto inSSCCChildPlanDto) {

		LOG.debug("Entering method updateSSCCChildPlanStatus in SSCCChildPlanDaoImpl");

		Long updateResult = 0l;

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(SsccChildPlan.class);

		criteria.add(Restrictions.eq("idChildPlanEvent", inSSCCChildPlanDto.getIdChildPlanEvent()));

		SsccChildPlan ssccchildplan = (SsccChildPlan) criteria.uniqueResult();

		if (!ObjectUtils.isEmpty(ssccchildplan)) {

			// idLastUpdatePerson
			if (!ObjectUtils.isEmpty(inSSCCChildPlanDto.getIdLastUpdatePerson())) {
				ssccchildplan.setIdLastUpdatePerson(inSSCCChildPlanDto.getIdLastUpdatePerson());
			}

			// cdStatus
			if (!ObjectUtils.isEmpty(inSSCCChildPlanDto.getCdStatus())) {
				ssccchildplan.setCdStatus(inSSCCChildPlanDto.getCdStatus());
			}

			// indReadyReview
			if (!ObjectUtils.isEmpty(inSSCCChildPlanDto.getIndReadyForReview())) {
				ssccchildplan.setIndReadyReview(inSSCCChildPlanDto.getIndReadyForReview());
			}

			sessionFactory.getCurrentSession().saveOrUpdate(ssccchildplan);
			updateResult++;

		}

		LOG.debug("Exiting method updateSSCCChildPlanStatus in SSCCChildPlanDaoImpl");

		return updateResult;
	}

	/**
	 * Method Name: updateSSCCCTopic Method Description: Saves the updated sscc
	 * child plan topic status to the database.
	 * 
	 * @param inSSCCChildPlanGuideTopicDto
	 * @return Long
	 */
	@Override
	public Long updateSSCCCTopic(SSCCChildPlanGuideTopicDto inSSCCChildPlanGuideTopicDto) {

		LOG.debug("Entering method updateSSCCCTopic in SSCCChildPlanDaoImpl");

		Long updateResult = 0l;

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(SsccChildPlanTopic.class);

		criteria.add(Restrictions.eq("cdCpTopic", inSSCCChildPlanGuideTopicDto.getStCode()));
		criteria.add(Restrictions.eq("idSsccChildPlan", inSSCCChildPlanGuideTopicDto.getIdSCCCChildPlan()));

		SsccChildPlanTopic ssccchildplantopic = (SsccChildPlanTopic) criteria.uniqueResult();

		if (!TypeConvUtil.isNullOrEmpty(ssccchildplantopic)) {

			if (!TypeConvUtil.isNullOrEmpty(inSSCCChildPlanGuideTopicDto.getCdStatus())) {
				ssccchildplantopic.setCdStatus(inSSCCChildPlanGuideTopicDto.getCdStatus());
			}

			if (!TypeConvUtil.isNullOrEmpty(inSSCCChildPlanGuideTopicDto.getIndReadyForReview())) {
				ssccchildplantopic.setIndReadyForReview(inSSCCChildPlanGuideTopicDto.getIndReadyForReview());
			}

			if (null != inSSCCChildPlanGuideTopicDto.getStBlob()) {
				try {
					Blob blob = wrapBlob(inSSCCChildPlanGuideTopicDto.getStBlob());
					ssccchildplantopic.setNarrative(blob);
				} catch (Exception e) {
					LOG.error(e.getMessage());
				}
			}

			if (!TypeConvUtil.isNullOrEmpty(inSSCCChildPlanGuideTopicDto.getIdLastUpdatePerson())) {
				ssccchildplantopic.setIdLastUpdatePerson(inSSCCChildPlanGuideTopicDto.getIdLastUpdatePerson());
			}

			sessionFactory.getCurrentSession().saveOrUpdate(ssccchildplantopic);
			updateResult++;

		}

		if (updateResult <= 0) {
			throw new DataNotFoundException(messageSource
					.getMessage("SSCCChildPlanDao.updateSSCCCTopicStatus.data.not.found", null, Locale.US));
		}

		LOG.debug("Exiting method updateSSCCCTopic in SSCCChildPlanDaoImpl");

		return updateResult;
	}

	/**
	 * Method Name: updateSSCCCTopicStatus Method Description: Saves the updated
	 * sscc child plan topic status to the database.
	 * 
	 * @param inSSCCChildPlanGuideTopicDto
	 * @return Long
	 */
	@Override
	public Long updateSSCCCTopicStatus(SSCCChildPlanGuideTopicDto inSSCCChildPlanGuideTopicDto) {

		LOG.debug("Entering method updateSSCCCTopicStatus in SSCCChildPlanDaoImpl");

		Long updateResult = 0l;

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(SsccChildPlanTopic.class);

		criteria.add(Restrictions.eq("cdCpTopic", inSSCCChildPlanGuideTopicDto.getStCode()));
		criteria.add(Restrictions.eq("idSsccChildPlan", inSSCCChildPlanGuideTopicDto.getIdSCCCChildPlan()));

		List<SsccChildPlanTopic> listSSccchildplantopic = (List<SsccChildPlanTopic>) criteria.list();

		for (SsccChildPlanTopic inSsccChildPlanTopic : listSSccchildplantopic) {
			if (!ObjectUtils.isEmpty(inSsccChildPlanTopic)) {

				if (!ObjectUtils.isEmpty(inSSCCChildPlanGuideTopicDto.getCdStatus())) {
					inSsccChildPlanTopic.setCdStatus(inSSCCChildPlanGuideTopicDto.getCdStatus());
				}

				if (!ObjectUtils.isEmpty(inSSCCChildPlanGuideTopicDto.getIndReadyForReview())) {
					inSsccChildPlanTopic.setIndReadyForReview(inSSCCChildPlanGuideTopicDto.getIndReadyForReview());
				}

				if (!ObjectUtils.isEmpty(inSSCCChildPlanGuideTopicDto.getIdLastUpdatePerson())) {
					inSsccChildPlanTopic.setIdLastUpdatePerson(inSSCCChildPlanGuideTopicDto.getIdLastUpdatePerson());
				}

				sessionFactory.getCurrentSession().saveOrUpdate(inSsccChildPlanTopic);
				updateResult++;

			}
		}

		LOG.debug("Exiting method updateSSCCCTopicStatus in SSCCChildPlanDaoImpl");

		return updateResult;
	}

	/**
	 * Method Name: getActiveSSCCReferral Method Description: Uses the given
	 * stage id to retrieve any active SSCC referral for the primary child of
	 * that stage.
	 *
	 * @param stageId
	 * @return Long
	 */
	@Override
	public Long getActiveSSCCReferral(Long stageId) {

		LOG.debug("Entering method getActiveSSCCReferral in SSCCChildPlanDaoImpl");

		Long idSSCCReferral = null;
		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getActiveSSCCReferralSql)

				.setParameter("idStage", stageId);

		List<BigDecimal> idReferralList = (List<BigDecimal>) query.list();

		if (TypeConvUtil.isNullOrEmpty(idReferralList)) {
			throw new DataNotFoundException(messageSource.getMessage(
					"SSCCChildPlanDao.getActiveSSCCReferral.idReferralList.data.not.found", null, Locale.US));
		}

		for (BigDecimal idReferral : idReferralList) {
			idSSCCReferral = idReferral.longValue();
		}

		LOG.debug("Exiting method getActiveSSCCReferral in SSCCChildPlanDaoImpl");

		return idSSCCReferral;
	}

	/**
	 * Method Name: getChildPlanParticipList Method Description:
	 * 
	 * @param ssccChildPlanDto
	 * @return List<SSCCChildPlanDto>
	 */
	@Override
	public List<SSCCChildPlanDto> getChildPlanParticipList(SSCCChildPlanDto ssccChildPlanDto) {

		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getChildPlanParticipListSql)

				.addScalar("idSsccChildPlan", StandardBasicTypes.LONG)
				.addScalar("dtCreated", StandardBasicTypes.TIMESTAMP)
				.addScalar("idCreatedPerson", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP)
				.addScalar("idLastUpdatePerson", StandardBasicTypes.LONG)
				.addScalar("idSSCCReferral", StandardBasicTypes.LONG)
				.addScalar("idChildPlanEvent", StandardBasicTypes.LONG)
				.addScalar("idEventNewUsed", StandardBasicTypes.LONG).addScalar("cdStatus", StandardBasicTypes.STRING)
				.addScalar("cdParticipStatus", StandardBasicTypes.STRING)
				.addScalar("indReadyForReview", StandardBasicTypes.STRING)
				.addScalar("dtRecorded", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtCspPermGoalTarget", StandardBasicTypes.TIMESTAMP)
				.addScalar("lengthOfStay", StandardBasicTypes.STRING)
				.addScalar("discrepancy", StandardBasicTypes.STRING)
				.addScalar("cdCspPlanPermGoal", StandardBasicTypes.STRING)
				.addScalar("cdCspPlanType", StandardBasicTypes.STRING)
				.addScalar("dtEventOccurred", StandardBasicTypes.DATE)
				.addScalar("cdCspPlanTypeForOther", StandardBasicTypes.STRING)
				.addScalar("dtCspNextReview", StandardBasicTypes.TIMESTAMP)
				.addScalar("participComment", StandardBasicTypes.STRING)
				.addScalar("dtCspPlanCompleted", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtInitialTransPlan", StandardBasicTypes.TIMESTAMP)
				.addScalar("indParentsParticipated", StandardBasicTypes.STRING)
				.addScalar("infoNotAvail", StandardBasicTypes.STRING).addScalar("otherAssmt", StandardBasicTypes.STRING)
				.addScalar("indNoConGoal", StandardBasicTypes.STRING).addScalar("noConGoal", StandardBasicTypes.STRING)
				.addScalar("eventDescr", StandardBasicTypes.STRING)
				.addScalar("cidSSCCReferral", StandardBasicTypes.LONG)
				.addScalar("nmPersonProposed", StandardBasicTypes.STRING)

				.setParameter("idCase", ssccChildPlanDto.getIdCase())
				.setParameter("idStage", ssccChildPlanDto.getIdStage())

				.setResultTransformer(Transformers.aliasToBean(SSCCChildPlanDto.class));

		List<SSCCChildPlanDto> ssccChildPlanDtoList = (List<SSCCChildPlanDto>) query.list();

		if(!ObjectUtils.isEmpty(ssccChildPlanDtoList)) {
			ssccChildPlanDtoList.forEach(ssccChildPlan->{
				if(!ObjectUtils.isEmpty(ssccChildPlan.getCdCspPlanTypeForOther()) && ObjectUtils.isEmpty(ssccChildPlan.getCdCspPlanType())) {
					ssccChildPlan.setCdCspPlanType(ssccChildPlan.getCdCspPlanTypeForOther());
				}
			});
		}

		return ssccChildPlanDtoList;
	}

	/**
	 * Method Name: queryAssignedTopics Method Description: Retrieves the
	 * assigned topics from the database.
	 * 
	 * @param idRsrc
	 * @param cdPlanType
	 * @return List<String>
	 */
	@Override
	public List<String> queryAssignedTopics(Long idRsrc, String cdPlanType) {

		LOG.debug("Entering method queryAssignedTopics in SSCCChildPlanDaoImpl");
		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(queryAssignedTopicsSql)
				.setParameter("idRsrc", idRsrc).setParameter("cdCspPlanType", cdPlanType);
		List<String> listOfTopics = (List<String>) query.list();

		LOG.debug("Exiting method queryAssignedTopics in SSCCChildPlanDaoImpl");

		return listOfTopics;
	}

	/**
	 * Method Name: queryLastUpdateParticip Method Description: Retrieve most
	 * recent dt_last_update for sscc particpants.
	 * 
	 * @param ssccChildPlanDto
	 * @return SSCCChildPlanDto
	 */
	@Override
	public SSCCChildPlanDto queryLastUpdateParticip(SSCCChildPlanDto ssccChildPlanDto) {

		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(queryLastUpdateParticipSql)

				.addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP)
				.setParameter("idSSCCChildPlan", ssccChildPlanDto.getIdSsccChildPlan())
				.setResultTransformer(Transformers.aliasToBean(SSCCChildPlanDto.class));

		SSCCChildPlanDto outSSCCChildPlanDto = (SSCCChildPlanDto) query.uniqueResult();

		if (!ObjectUtils.isEmpty(outSSCCChildPlanDto)) {
			ssccChildPlanDto.setDtLastUpdateParticip(outSSCCChildPlanDto.getDtLastUpdate());
		}
		return ssccChildPlanDto;
	}

	/**
	 * Method Name: querySSCCChildPlan Method Description: Retrieves the sscc
	 * child plan details from the database using idevent.
	 * 
	 * @param ssccChildPlanDto
	 * @return SSCCChildPlanDto
	 */
	@Override
	public SSCCChildPlanDto querySSCCChildPlan(SSCCChildPlanDto ssccChildPlanDto) {

		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(querySSCCChildPlanSql)

				.addScalar("idSsccChildPlan", StandardBasicTypes.LONG)
				.addScalar("dtCreated", StandardBasicTypes.TIMESTAMP)
				.addScalar("idCreatedPerson", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP)
				.addScalar("idLastUpdatePerson", StandardBasicTypes.LONG)
				.addScalar("idSSCCReferral", StandardBasicTypes.LONG)
				.addScalar("idChildPlanEvent", StandardBasicTypes.LONG)
				.addScalar("idEventNewUsed", StandardBasicTypes.LONG).addScalar("cdStatus", StandardBasicTypes.STRING)
				.addScalar("cdParticipStatus", StandardBasicTypes.STRING)
				.addScalar("indReadyForReview", StandardBasicTypes.STRING)
				.addScalar("dtRecorded", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtCspPermGoalTarget", StandardBasicTypes.TIMESTAMP)
				.addScalar("lengthOfStay", StandardBasicTypes.STRING)
				.addScalar("discrepancy", StandardBasicTypes.STRING)
				.addScalar("cdCspPlanPermGoal", StandardBasicTypes.STRING)
				.addScalar("cdCspPlanType", StandardBasicTypes.STRING)
				.addScalar("dtCspNextReview", StandardBasicTypes.TIMESTAMP)
				.addScalar("participComment", StandardBasicTypes.STRING)
				.addScalar("dtCspPlanCompleted", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtInitialTransPlan", StandardBasicTypes.TIMESTAMP)
				.addScalar("indParentsParticipated", StandardBasicTypes.STRING)
				.addScalar("infoNotAvail", StandardBasicTypes.STRING).addScalar("otherAssmt", StandardBasicTypes.STRING)
				.addScalar("indNoConGoal", StandardBasicTypes.STRING).addScalar("noConGoal", StandardBasicTypes.STRING)
				.addScalar("idRsrc", StandardBasicTypes.LONG).addScalar("cdRegion", StandardBasicTypes.STRING)

				.setParameter("idChildPlanEvent", ssccChildPlanDto.getIdEvent())

				.setResultTransformer(Transformers.aliasToBean(SSCCChildPlanDto.class));

		SSCCChildPlanDto outSSCCChildPlanDto = (SSCCChildPlanDto) query.uniqueResult();

		if(!ObjectUtils.isEmpty(outSSCCChildPlanDto)) outSSCCChildPlanDto.setIdEvent(ssccChildPlanDto.getIdEvent());
		
		return outSSCCChildPlanDto;
	}

	/**
	 * Method Name: querySSCCChildPlanByPK Method Description: Retrieves the
	 * sscc child plan details from the database using id_sscc_child_plan.
	 * 
	 * @param ssccChildPlanDto
	 * @return SSCCChildPlanDto
	 */
	@Override
	public SSCCChildPlanDto querySSCCChildPlanByPK(SSCCChildPlanDto ssccChildPlanDto) {

		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(querySSCCChildPlanByPKSql)

				.addScalar("idSsccChildPlan", StandardBasicTypes.LONG)
				.addScalar("dtCreated", StandardBasicTypes.TIMESTAMP)
				.addScalar("idCreatedPerson", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP)
				.addScalar("idLastUpdatePerson", StandardBasicTypes.LONG)
				.addScalar("idSSCCReferral", StandardBasicTypes.LONG)
				.addScalar("idChildPlanEvent", StandardBasicTypes.LONG)
				.addScalar("idEventNewUsed", StandardBasicTypes.LONG).addScalar("cdStatus", StandardBasicTypes.STRING)
				.addScalar("cdParticipStatus", StandardBasicTypes.STRING)
				.addScalar("indReadyForReview", StandardBasicTypes.STRING)
				.addScalar("dtRecorded", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtCspPermGoalTarget", StandardBasicTypes.TIMESTAMP)
				.addScalar("lengthOfStay", StandardBasicTypes.STRING)
				.addScalar("discrepancy", StandardBasicTypes.STRING)
				.addScalar("cdCspPlanPermGoal", StandardBasicTypes.STRING)
				.addScalar("cdCspPlanType", StandardBasicTypes.STRING)
				.addScalar("dtCspNextReview", StandardBasicTypes.TIMESTAMP)
				.addScalar("participComment", StandardBasicTypes.STRING)
				.addScalar("dtCspPlanCompleted", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtInitialTransPlan", StandardBasicTypes.TIMESTAMP)
				.addScalar("indParentsParticipated", StandardBasicTypes.STRING)
				.addScalar("infoNotAvail", StandardBasicTypes.STRING).addScalar("otherAssmt", StandardBasicTypes.STRING)
				.addScalar("indNoConGoal", StandardBasicTypes.STRING).addScalar("noConGoal", StandardBasicTypes.STRING)
				.addScalar("idRsrc", StandardBasicTypes.LONG).addScalar("cdRegion", StandardBasicTypes.STRING)

				.setParameter("idSsccChildPlan", ssccChildPlanDto.getIdSsccChildPlan())

				.setResultTransformer(Transformers.aliasToBean(SSCCChildPlanDto.class));

		SSCCChildPlanDto outSSCCChildPlanDto = (SSCCChildPlanDto) query.uniqueResult();

		return outSSCCChildPlanDto;
	}

	/**
	 * Method Name: querySSCCChildPlanParticipants Method Description: Retrieve
	 * SSCC child plan participant info.
	 * 
	 * @param inSSCCChildPlanDto
	 * @return List<SSCCChildPlanParticipDto>
	 */
	@Override
	public List<SSCCChildPlanParticipDto> querySSCCChildPlanParticipants(SSCCChildPlanDto inSSCCChildPlanDto) {

		LOG.debug("Entering method querySSCCChildPlanParticipants in SSCCChildPlanDaoImpl");

		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(querySSCCChildPlanParticipantsSql)

				.addScalar("idSSCCChildPlanParticipant", StandardBasicTypes.LONG)
				.addScalar("dtCreated", StandardBasicTypes.TIMESTAMP)
				.addScalar("idCreatedPerson", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP)
				.addScalar("idLastUpdatePerson", StandardBasicTypes.LONG)
				.addScalar("idSCCCChildPlan", StandardBasicTypes.LONG).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("nmCspPartFulls", StandardBasicTypes.STRING)
				.addScalar("dsCspPartRel", StandardBasicTypes.STRING)
				.addScalar("cdCspPartNotifType", StandardBasicTypes.STRING)
				.addScalar("cdCspPartType", StandardBasicTypes.STRING)
				.addScalar("dtCspNotif", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtCspPartCopyGiven", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtCspPartParticipate", StandardBasicTypes.TIMESTAMP)

				.setParameter("idSCCCChildPlan", inSSCCChildPlanDto.getIdSsccChildPlan())

				.setResultTransformer(Transformers.aliasToBean(SSCCChildPlanParticipDto.class));

		List<SSCCChildPlanParticipDto> ssccChildPlanParticipDtoList = (List<SSCCChildPlanParticipDto>) query.list();

		LOG.debug("Exiting method querySSCCChildPlanParticipants in SSCCChildPlanDaoImpl");

		return ssccChildPlanParticipDtoList;
	}

	/**
	 * Method Name: querySSCCChildPlanTopic Method Description: Retrieve SSCC
	 * child plan topic info for a single topic.
	 * 
	 * @param ssccChildPlanGuideTopicDto
	 * @return SSCCChildPlanGuideTopicDto
	 */
	@Override
	public SSCCChildPlanGuideTopicDto querySSCCChildPlanTopic(SSCCChildPlanGuideTopicDto inSsccChildPlanGuideTopicDto) {

		LOG.debug("Entering method querySSCCChildPlanTopic in SSCCChildPlanDaoImpl");
		SSCCChildPlanGuideTopicDto outSSCCChildPlanGuideTopicDto = new SSCCChildPlanGuideTopicDto();
		
		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(querySSCCChildPlanTopicSql)

				.addScalar("dtCreated", StandardBasicTypes.TIMESTAMP)
				.addScalar("idCreatedPerson", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP)
				.addScalar("idLastUpdatePerson", StandardBasicTypes.LONG)
				.addScalar("idSCCCChildPlan", StandardBasicTypes.LONG).addScalar("cdStatus", StandardBasicTypes.STRING)
				.addScalar("indReadyForReview", StandardBasicTypes.STRING)
				.addScalar("stCode", StandardBasicTypes.STRING).addScalar("narrative", StandardBasicTypes.BLOB)

				.setParameter("idSCCCChildPlan", inSsccChildPlanGuideTopicDto.getIdSCCCChildPlan())
				.setParameter("stCode", inSsccChildPlanGuideTopicDto.getStCode())

				.setResultTransformer(Transformers.aliasToBean(SSCCChildPlanGuideTopicDto.class));

		outSSCCChildPlanGuideTopicDto = (SSCCChildPlanGuideTopicDto) query.uniqueResult();
		
		Blob narrative = outSSCCChildPlanGuideTopicDto.getNarrative();
		if (!ObjectUtils.isEmpty(narrative)) {
			byte[] bytes = null;
			try {
				bytes = narrative.getBytes(1, (int) narrative.length());
			} catch (SQLException e) {
				throw new DataLayerException(e.getMessage());
			}
			outSSCCChildPlanGuideTopicDto.setStBlob(childPlanBeanDao.unwrapBlob(bytes));
			outSSCCChildPlanGuideTopicDto.setNarrative(null);
		}

		LOG.debug("Exiting method querySSCCChildPlanTopic in SSCCChildPlanDaoImpl");

		return outSSCCChildPlanGuideTopicDto;
	}

	/**
	 * Method Name: querySSCCChildPlanTopicData Method Description: Retrieve
	 * SSCC child plan topic info.
	 * 
	 * @param ssccChildPlanDto
	 * @return List<SSCCChildPlanGuideTopicDto>
	 */
	@Override
	public List<SSCCChildPlanGuideTopicDto> querySSCCChildPlanTopicData(SSCCChildPlanDto ssccChildPlanDto) {

		LOG.debug("Entering method querySSCCChildPlanTopicData in SSCCChildPlanDaoImpl");

		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(querySSCCChildPlanTopicDataSql)

				.addScalar("dtCreated", StandardBasicTypes.TIMESTAMP)
				.addScalar("idCreatedPerson", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP)
				.addScalar("idLastUpdatePerson", StandardBasicTypes.LONG)
				.addScalar("idSCCCChildPlan", StandardBasicTypes.LONG).addScalar("cdStatus", StandardBasicTypes.STRING)
				.addScalar("indReadyForReview", StandardBasicTypes.STRING)
				.addScalar("stCode", StandardBasicTypes.STRING).addScalar("narrative", StandardBasicTypes.BLOB)

				.setParameter("idSCCCChildPlan", ssccChildPlanDto.getIdSsccChildPlan())

				.setResultTransformer(Transformers.aliasToBean(SSCCChildPlanGuideTopicDto.class));

		List<SSCCChildPlanGuideTopicDto> listOfSSCCChildPlanGuideTopicDto = (List<SSCCChildPlanGuideTopicDto>) query
				.list();

		if (!ObjectUtils.isEmpty(listOfSSCCChildPlanGuideTopicDto)) {
			listOfSSCCChildPlanGuideTopicDto.forEach(ssccChildPlanGuide -> {
				Blob narrative = ssccChildPlanGuide.getNarrative();
				if (!ObjectUtils.isEmpty(narrative)) {
					byte[] bytes = null;
					try {
						bytes = narrative.getBytes(1, (int) narrative.length());
					} catch (SQLException e) {
						throw new DataLayerException(e.getMessage());
					}
					ssccChildPlanGuide.setStBlob(childPlanBeanDao.unwrapBlob(bytes));
					ssccChildPlanGuide.setNarrative(null);
				}
			});
		}

		LOG.debug("Exiting method querySSCCChildPlanTopicData in SSCCChildPlanDaoImpl");

		return listOfSSCCChildPlanGuideTopicDto;
	}

	/**
	 * Method Name: querySSCCChildPlanTopicDataForDeletedCP Method Description:
	 * Retrieve SSCC child plan topic info.
	 * 
	 * @param ssccChildPlanDto
	 * @return List<SSCCChildPlanGuideTopicDto>
	 */
	@Override
	public List<SSCCChildPlanGuideTopicDto> querySSCCChildPlanTopicDataForDeletedCP(SSCCChildPlanDto ssccChildPlanDto) {

		LOG.debug("Entering method querySSCCChildPlanTopicData in SSCCChildPlanDaoImpl");

		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(querySSCCChildPlanTopicDataForDeletedCPSql)

				.addScalar("dtCreated", StandardBasicTypes.TIMESTAMP)
				.addScalar("idCreatedPerson", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP)
				.addScalar("idLastUpdatePerson", StandardBasicTypes.LONG)
				.addScalar("idSCCCChildPlan", StandardBasicTypes.LONG).addScalar("cdStatus", StandardBasicTypes.STRING)
				.addScalar("indReadyForReview", StandardBasicTypes.STRING)
				.addScalar("stCode", StandardBasicTypes.STRING).addScalar("narrative", StandardBasicTypes.BLOB)
				.addScalar("cpTopicInst", StandardBasicTypes.STRING)

				.setParameter("idSCCCChildPlan", ssccChildPlanDto.getIdSsccChildPlan())
				.setParameter("codeType", ssccChildPlanDto.getCodeType())

				.setResultTransformer(Transformers.aliasToBean(SSCCChildPlanGuideTopicDto.class));

		List<SSCCChildPlanGuideTopicDto> listOfSSCCChildPlanDto = (List<SSCCChildPlanGuideTopicDto>) query.list();

		if (!ObjectUtils.isEmpty(listOfSSCCChildPlanDto)) {
			listOfSSCCChildPlanDto.forEach(ssccChildPlanGuide -> {
				Blob narrative = ssccChildPlanGuide.getNarrative();
				if (!ObjectUtils.isEmpty(narrative)) {
					byte[] bytes = null;
					try {
						bytes = narrative.getBytes(1, (int) narrative.length());
					} catch (SQLException e) {
						throw new DataLayerException(e.getMessage());
					}
					ssccChildPlanGuide.setStBlob(childPlanBeanDao.unwrapBlob(bytes));
					ssccChildPlanGuide.setNarrative(null);
				}
			});
		}
		LOG.debug("Exiting method querySSCCChildPlanTopicData in SSCCChildPlanDaoImpl");

		return listOfSSCCChildPlanDto;
	}

	/**
	 * Method Name: selectNewUsingChildPlanTopic Method Description: Retrieves
	 * the sscc child plan new used topics from the database.
	 * 
	 * @param ssccChildPlanDto
	 * @param topic
	 * @return SSCCChildPlanGuideTopicDto
	 */
	@Override
	public SSCCChildPlanGuideTopicDto selectNewUsingChildPlanTopic(SSCCChildPlanDto ssccChildPlanDto, String topic) {

		LOG.debug("Entering method selectNewUsingChildPlanTopic in SSCCChildPlanDaoImpl");

		String table = lookupDao.simpleDecodeSafe(ServiceConstants.CCPTPTBL, topic);

		String dynamicSelectNewUsingChildPlanTopicSql = new String();
		dynamicSelectNewUsingChildPlanTopicSql = MessageFormat.format(selectNewUsingChildPlanTopicSql,
				new Object[] { table });

		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(dynamicSelectNewUsingChildPlanTopicSql)
				.addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP).addScalar("narrative", StandardBasicTypes.BLOB)
				.setParameter("idEvent", ssccChildPlanDto.getIdEventNewUsed())
				.setResultTransformer(Transformers.aliasToBean(SSCCChildPlanGuideTopicDto.class));

		SSCCChildPlanGuideTopicDto ssccChildPlanGuideTopicDto = (SSCCChildPlanGuideTopicDto) query.uniqueResult();

		if (!ObjectUtils.isEmpty(ssccChildPlanGuideTopicDto)) {
			ssccChildPlanGuideTopicDto.setStCode(topic);

			Blob narrative = ssccChildPlanGuideTopicDto.getNarrative();
			if (!ObjectUtils.isEmpty(narrative)) {
				byte[] bytes = null;
				try {
					bytes = narrative.getBytes(1, (int) narrative.length());
				} catch (SQLException e) {
					throw new DataLayerException(e.getMessage());
				}
				ssccChildPlanGuideTopicDto.setStBlob(childPlanBeanDao.unwrapBlob(bytes));
				ssccChildPlanGuideTopicDto.setNarrative(null);
			}
		}
		LOG.debug("Exiting method selectNewUsingChildPlanTopic in SSCCChildPlanDaoImpl");

		return ssccChildPlanGuideTopicDto;
	}

	/**
	 * Method Name: insertSSCCChildPlanTopic Method Description: Inserts the
	 * sscc child plan topics into the database.
	 * 
	 * @param ssccChildPlanBean
	 * @param topic
	 * @return Long
	 */
	@Override
	public Long insertSSCCChildPlanTopic(SSCCChildPlanDto inSSCCChildPlanDto, String topic) {

		LOG.debug("Entering method insertSSCCChildPlanTopic in SSCCChildPlanDaoImpl");

		long updateResult = 0;

		SsccChildPlanTopic ssccchildplantopic = new SsccChildPlanTopic();

		if (!TypeConvUtil.isNullOrEmpty(inSSCCChildPlanDto.getDtCreated())) {
			ssccchildplantopic.setDtCreated(inSSCCChildPlanDto.getDtCreated());
		}

		if (!TypeConvUtil.isNullOrEmpty(inSSCCChildPlanDto.getIdCreatedPerson())) {
			ssccchildplantopic.setIdCreatedPerson(inSSCCChildPlanDto.getIdCreatedPerson());
		}

		if (!TypeConvUtil.isNullOrEmpty(inSSCCChildPlanDto.getIdLastUpdatePerson())) {
			ssccchildplantopic.setIdLastUpdatePerson(inSSCCChildPlanDto.getIdLastUpdatePerson());
		}

		if (!TypeConvUtil.isNullOrEmpty(inSSCCChildPlanDto.getIdSsccChildPlan())) {
			ssccchildplantopic.setIdSsccChildPlan(inSSCCChildPlanDto.getIdSsccChildPlan());
		}

		ssccchildplantopic.setCdStatus("20");

		ssccchildplantopic.setIndReadyForReview("N");

		if (!TypeConvUtil.isNullOrEmpty(inSSCCChildPlanDto.getCdCpTopic())) {
			ssccchildplantopic.setCdCpTopic(inSSCCChildPlanDto.getCdCpTopic());
		}

		ssccchildplantopic.setIdDocumentTemplate(Integer.valueOf(29));

		sessionFactory.getCurrentSession().save(ssccchildplantopic);
		updateResult++;

		LOG.debug("Exiting method insertSSCCChildPlanTopic in SSCCChildPlanDaoImpl");

		return updateResult;
	}

	/**
	 * Method Name: clearChildPlanTopic Method Description: Clears narrative
	 * field of associated child plan narrative table for SSCC assigned toics
	 * 
	 * @param inSSCCChildPlanDto
	 * @param topic
	 * @return Long
	 * @throws DataNotFoundException
	 */
	@Override
	public Long clearChildPlanTopic(SSCCChildPlanDto inSSCCChildPlanDto, String topic) {

		LOG.debug("Entering method clearChildPlanTopic in SSCCChildPlanDaoImpl");

		String table = lookupDao.simpleDecodeSafe(ServiceConstants.CCPTPTBL, topic);
		String sql = MessageFormat.format(clearChildPlanTopicSql, new Object[] { table });

		SQLQuery queryUpdateRecords = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(sql)
				.setParameter("narrative", inSSCCChildPlanDto.getNarrative())
				.setParameter("ulIdEvent", inSSCCChildPlanDto.getIdEvent());
		long rowCountOne = queryUpdateRecords.executeUpdate();

		LOG.debug("Exiting method clearChildPlanTopic in SSCCChildPlanDaoImpl");

		return rowCountOne;
	}

	/**
	 * Method Name: wrapBlob Method Description: Method to wrap BLOB data
	 * 
	 * @param text
	 * @return Blob
	 * @throws SQLException
	 * @throws SerialException
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws InvalidDictionarySizeException
	 * @throws InvalidModeException
	 */
	private Blob wrapBlob(String text) throws SerialException, SQLException, InvalidModeException,
			InvalidDictionarySizeException, IOException, InterruptedException {
		byte[] data = null;
		text = Base64.encode(text.getBytes(ServiceConstants.CHARACTER_ENCODING));
		text = ServiceConstants.XML_HEADER
				+ "<data><userEdits><userEdit><fieldName>txtBlankNarrative</fieldName><fieldValue>" + text
				+ "</fieldValue></userEdit></userEdits></data>";
		data = CompressionHelper.compressData(text.getBytes(ServiceConstants.CHARACTER_ENCODING)).toByteArray();
		Blob blob = new SerialBlob(data);
		return blob;
	}
}
