package us.tx.state.dfps.service.SDM.daoimpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import org.springframework.util.StringUtils;
import us.tx.state.dfps.common.domain.AssessmentHouseholdLink;
import us.tx.state.dfps.common.domain.CpsRa;
import us.tx.state.dfps.common.domain.CpsRaAnswerLookup;
import us.tx.state.dfps.common.domain.CpsRaAssmtLookup;
import us.tx.state.dfps.common.domain.CpsRaFollowupLookup;
import us.tx.state.dfps.common.domain.CpsRaFollowupResponse;
import us.tx.state.dfps.common.domain.CpsRaQstnLookup;
import us.tx.state.dfps.common.domain.CpsRaResponse;
import us.tx.state.dfps.common.domain.CpsRaSecondFollowupLkp;
import us.tx.state.dfps.common.domain.CpsRaSecondFollowupResp;
import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.domain.Stage;
import us.tx.state.dfps.common.domain.StagePersonLink;
import us.tx.state.dfps.common.dto.CpsRaResponseDto;
import us.tx.state.dfps.common.dto.RiskAssmtDtlDto;
import us.tx.state.dfps.common.dto.StagePersonValueDto;
import us.tx.state.dfps.service.SDM.dao.SDMRiskAssessmentDao;
import us.tx.state.dfps.service.admin.dto.ApprovalCommonInDto;
import us.tx.state.dfps.service.admin.service.ApprovalCommonService;
import us.tx.state.dfps.service.alternativeresponse.dto.EventValueDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.SafetyAssessmentReq;
import us.tx.state.dfps.service.common.response.SDMRiskAssessmentRes;
import us.tx.state.dfps.service.common.response.SafetyAssessmentRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataLayerException;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.investigation.dto.SDMRiskAssessmentAnswerDto;
import us.tx.state.dfps.service.investigation.dto.SDMRiskAssessmentDto;
import us.tx.state.dfps.service.investigation.dto.SDMRiskAssessmentFollowupDto;
import us.tx.state.dfps.service.investigation.dto.SDMRiskAssessmentQuestionDto;
import us.tx.state.dfps.service.investigation.dto.SDMRiskAssmtSecondaryFollowupDto;
import us.tx.state.dfps.service.lookup.dao.LookupDao;
import us.tx.state.dfps.service.person.dto.IntakeAllegationDto;
import us.tx.state.dfps.service.safetyassessment.dto.SafetyAssessmentDto;
import us.tx.state.dfps.service.safetyassessment.dto.SafetyFactorDto;
import us.tx.state.dfps.service.sdmriskassessment.dto.OptionDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;
import us.tx.state.dfps.service.workload.dto.StageDto;
import us.tx.state.dfps.service.workload.dto.StagePersonLinkDto;

/**
 * ;
 * service-business- IMPACT PHASE 2 MODERNIZATION
 * Class Description:SDMRiskAssessmentDaoImpl will
 * implemented all operation defined in SDMRiskAssessmentDao Interface related SDMRiskAssessment module..
 * March 9, 2018- 2:02:51 PM
 * © 2017 Texas Department of Family and Protective Services
 */

/**
 * Dao implementation for Risk Assessment Process Change History: Date Fixer
 * Artf# Description 09/19/2018 Pindigp 72034 Changes were made to take stage
 * the risk assesment is created into account while adding a new one for the
 * case in a particular stage.
 *
 *
 */
@Repository
public class SDMRiskAssessmentDaoImpl implements SDMRiskAssessmentDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    LookupDao lookupDao;

    @Autowired
    ApprovalCommonService approvalService;


    @Value("${SDMRiskAssessmentDaoImpl.getHouseHoldName}")
    private String getHouseHoldNameSql;

    @Value("${SDMRiskAssessmentDaoImpl.getCareGiverNames}")
    private String getCareGiverNamesSql;

    @Value("${SDMRiskAssessmentDaoImpl.getRiskAssmtForHousehold}")
    private String getRiskAssmtForHouseholdSql;
    @Value("${SDMRiskAssessmentDaoImpl.getHouseHoldForRiskAssmt}")
    private String getHouseHoldForRiskAssmtSql;

    @Value("${SDMRiskAssessmentDaoImpl.getPersonId}")
    private String getPersonIdSql;

    @Value("${SDMRiskAssessmentDaoImpl.getCareGiverHistoryList}")
    private String getCareGiverHistoryListSql;

    @Value("${SDMRiskAssessmentDaoImpl.getPriorNeglectList}")
    private String getPriorNeglectListSql;

    @Value("${SDMRiskAssessmentDaoImpl.getPriorAbuseList}")
    private String getPriorAbuseListSql;

    @Value("${SDMRiskAssessmentDaoImpl.getRiskAssemment}")
    private String getRiskAssemmentSql;

    @Value("${SDMRiskAssessmentDaoImpl.getRiskAssmtRspn}")
    private String getRiskAssmtRspnSql;

    @Value("${SDMRiskAssessmentDaoImpl.getqueryRiskAssmtExists}")
    private String getqueryRiskAssmtExistsSql;

    @Value("${SDMRiskAssessmentDaoImpl.primaryPersDetailsSql}")
    private String primaryPersDetailsSql;

    @Value("${SDMRiskAssessmentDaoImpl.getAbuseSql}")
    private String getAbuseSql;

    @Value("${SDMRiskAssessmentDaoImpl.getNeglectSql}")
    private String getNeglectSql;

    @Value("${SDMRiskAssessmentDaoImpl.getNumChildrenInAllegationSql}")
    private String getNumChildrenInAllegationSql;

    @Value("${SDMRiskAssessmentDaoImpl.getPriorStageSql}")
    private String getPriorStageSql;

    @Value("${SDMRiskAssessmentDaoImpl.getNumOfYoungChildren}")
    private String getNumOfYoungChildren;

    @Value("${SDMRiskAssessmentDaoImpl.getNumChildWithPriorInjurySql}")
    private String getNumChildWithPriorInjurySql;

    @Value("${SDMRiskAssessmentDaoImpl.queryPageDataSql}")
    private String queryPageDataSql;

    @Value("${SDMRiskAssessmentDaoImpl.sdmRiskAssessmentQuery}")
    private String sdmRiskAssessmentQuery;

    @Value("${SDMRiskAssessmentDaoImpl.getPrimaryCreGivrHistory}")
    private String getPrimaryCreGivrHistory;

    @Value("${SafetyAssessment.retrieveSafetyAssmtData}")
    private String getRetrieveSafetyAssmtData;

    @Value("${SafetyAssessment.getSubStageOpen}")
    private String getSubStageOpen;

    @Value("${SafetyAssessment.getCurrentEventStatus}")
    private String getCurrentEventStatus;

    @Value("${SDMRiskAssessmentDaoImpl.sdmRAEventForStageHouseholdQuery}")
    private String sdmRAEventForStageHouseholdQuery;

    public SDMRiskAssessmentDaoImpl() {

    }

    @Override
    public long updateEventStatus(EventValueDto eventValueDto) {
        long Result = 0;

        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Event.class);
        criteria.add(Restrictions.eq("idEvent", (eventValueDto.getIdEvent())));

        Event event = (Event) criteria.uniqueResult();

        Person person = new Person();
        person.setIdPerson(eventValueDto.getIdPerson());

        if (!ObjectUtils.isEmpty(event) && event.getDtLastUpdate().compareTo(eventValueDto.getDtLastUpdate()) <= 0) {

            if (!ObjectUtils.isEmpty(eventValueDto.getCdEventStatus())) {
                event.setCdEventStatus(eventValueDto.getCdEventStatus());
            }

            if (eventValueDto.getIdPerson() > 0) {
                event.setPerson(person);
            }
            event.setDtLastUpdate(new Date());
            sessionFactory.getCurrentSession().saveOrUpdate(event);
            Result++;

        }
        return Result;
    }

    // This method is used to insert completedDate for the CPS_RA table
    @Override
    public long addAssessmentCompletedDate(SDMRiskAssessmentDto sdmRiskAssessmentDto) {
        long Result = 0;
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CpsRa.class);
        criteria.add(Restrictions.eq("idCpsRa", Long.valueOf(sdmRiskAssessmentDto.getId())));

        CpsRa cpsRa = (CpsRa) criteria.uniqueResult();

        if (!ObjectUtils.isEmpty(cpsRa)
                && cpsRa.getDtLastUpdate().compareTo(sdmRiskAssessmentDto.getDateLastUpdate()) <= 0) {

            cpsRa.setDtAssmtCompleted(new Date());

            if (sdmRiskAssessmentDto.getLoggedInUser() > 0) {
                cpsRa.setIdLastUpdatePerson((long) sdmRiskAssessmentDto.getLoggedInUser());
            }
            cpsRa.setDtLastUpdate(new Date());
            sessionFactory.getCurrentSession().saveOrUpdate(cpsRa);
            Result++;
        }
        return Result;

    }

    // This method is used to insert cpsRaAssmtLookup for the CPS_RA table
    @Override
    public SDMRiskAssessmentDto addRiskAssessment(SDMRiskAssessmentDto sDMRiskAssessmentdto) {
        CpsRa cpsRa = new CpsRa();
        Event event = (Event) sessionFactory.getCurrentSession().load(Event.class, sDMRiskAssessmentdto.getIdEvent());
        Stage stage = (Stage) sessionFactory.getCurrentSession().load(Stage.class, sDMRiskAssessmentdto.getIdStage());
        event.setIdEvent((long) sDMRiskAssessmentdto.getIdEvent());
        cpsRa.setEvent(event);
        stage.setIdStage((long) sDMRiskAssessmentdto.getIdStage());
        cpsRa.setStage(stage);
        cpsRa.setNbrFaScore(sDMRiskAssessmentdto.getFutureAbuseScore());
        cpsRa.setNbrFnScore(sDMRiskAssessmentdto.getFutureNeglectScore());
        cpsRa.setCdFnRiskLevel(sDMRiskAssessmentdto.getFutureNeglectRiskLevelCode());
        cpsRa.setCdFaRiskLevel(sDMRiskAssessmentdto.getFutureAbuseRiskLevelCode());
        cpsRa.setCdScoredRiskLevel(sDMRiskAssessmentdto.getScoredRiskLevelCode());
        cpsRa.setCdOverride(sDMRiskAssessmentdto.getOverrideCode());
        cpsRa.setTxtDiscOverrideReason(sDMRiskAssessmentdto.getOverrideReason());
        cpsRa.setCdFinalRiskLevel(sDMRiskAssessmentdto.getFinalRiskLevelCode());
        if (!ObjectUtils.isEmpty(sDMRiskAssessmentdto.getIndPOChildDeath())
                && sDMRiskAssessmentdto.getIndPOChildDeath().equals(true)) {
            cpsRa.setIndPoChildDeath(ServiceConstants.Y);
        }
        if ((!ObjectUtils.isEmpty(sDMRiskAssessmentdto.getIndPOChildLessThanSixteenInjured())
                && sDMRiskAssessmentdto.getIndPOChildLessThanSixteenInjured().equals(true))) {
            cpsRa.setIndPoInjuryChild16(ServiceConstants.Y);
        }
        if ((!ObjectUtils.isEmpty(sDMRiskAssessmentdto.getIndPOChildLessThanThreeInjured())
                && sDMRiskAssessmentdto.getIndPOChildLessThanThreeInjured().equals(true))) {
            cpsRa.setIndPoInjuryChild3(ServiceConstants.Y);
        }
        if ((!ObjectUtils.isEmpty(sDMRiskAssessmentdto.getIndPOChildSexualAbuse())
                && sDMRiskAssessmentdto.getIndPOChildSexualAbuse().equals(true))) {
            cpsRa.setIndPoSxab(ServiceConstants.Y);
        }
        CpsRaAssmtLookup cpsRaAssmtLookup = (CpsRaAssmtLookup) sessionFactory.getCurrentSession()
                .load(CpsRaAssmtLookup.class, ServiceConstants.ONE_QUESTION_LOOKUP_ID);
        cpsRa.setCpsRaAssmtLookup(cpsRaAssmtLookup);
        cpsRa.setDtCreated(new Date());
        cpsRa.setIdCreatedPerson(sDMRiskAssessmentdto.getLoggedInUser());
        cpsRa.setDtLastUpdate(new Date());
        cpsRa.setIdLastUpdatePerson(sDMRiskAssessmentdto.getLoggedInUser());
        cpsRa.setDtAssmtCompleted(sDMRiskAssessmentdto.getDateAssessmentCompleted());
        Set<CpsRaResponse> cpsRaResponses = new HashSet<>();
        sDMRiskAssessmentdto.getQuestions().forEach(q -> {

            q.getAnswers().forEach(a -> {
                if (!ObjectUtils.isEmpty(q.getAnswers())) {
                    CpsRaResponse cpsRaResponse = new CpsRaResponse();
                    cpsRaResponse.setCdRaAnswer(a.getResponseCode());

                    CpsRaAnswerLookup cpsRaAnswerLookup = (CpsRaAnswerLookup) sessionFactory.getCurrentSession()
                            .load(CpsRaAnswerLookup.class, (long) a.getAnswerLookupId());
                    cpsRaResponse.setCpsRaAnswerLookup(cpsRaAnswerLookup);
                    CpsRaQstnLookup cpsRaQstnLookup = (CpsRaQstnLookup) sessionFactory.getCurrentSession()
                            .load(CpsRaQstnLookup.class, (long) a.getQuestionLookupId());
                    cpsRaResponse.setCpsRaQstnLookup(cpsRaQstnLookup);
                    cpsRaResponse.setDtCreated(new Date());
                    cpsRaResponse.setDtLastUpdate(new Date());
                    cpsRaResponse.setIdCreatedPerson(sDMRiskAssessmentdto.getLoggedInUser());
                    cpsRaResponse.setIdLastUpdatePerson(sDMRiskAssessmentdto.getLoggedInUser());
                    cpsRaResponse.setCpsRa(cpsRa);
                    if (!ObjectUtils.isEmpty(a.getFollowupQuestions())) {
                        Set<CpsRaFollowupResponse> cpsRaFollowupResponseList = new HashSet<>();
                        a.getFollowupQuestions().forEach(f -> {
                            CpsRaFollowupResponse cpsRaFollowupResponse = new CpsRaFollowupResponse();
                            long followUpLookupId = f.getFollowupId() == 0 ? f.getFollowupLookupId() : f.getFollowupId();
                            CpsRaFollowupLookup cpsRaFollowupLookup = (CpsRaFollowupLookup) sessionFactory
                                    .getCurrentSession().load(CpsRaFollowupLookup.class, followUpLookupId);
                            cpsRaFollowupResponse.setCpsRaFollowupLookup(cpsRaFollowupLookup);
                            cpsRaFollowupResponse.setDtCreated(new Date());
                            cpsRaFollowupResponse.setDtLastUpdate(new Date());
                            cpsRaFollowupResponse.setIdCreatedPerson(sDMRiskAssessmentdto.getLoggedInUser());
                            cpsRaFollowupResponse.setIdLastUpdatePerson(sDMRiskAssessmentdto.getLoggedInUser());
                            cpsRaFollowupResponse.setIndCpsRaFollowup(f.getIndRaFollowup());
                            cpsRaFollowupResponse.setCpsRaResponse(cpsRaResponse);
                            if (!ObjectUtils.isEmpty(f.getSecondaryFollowupQuestions())) {
                                Set<CpsRaSecondFollowupResp> cpsRaSecondFollowupResps = new HashSet<>();
                                f.getSecondaryFollowupQuestions().forEach(s -> {
                                    CpsRaSecondFollowupResp cpsRaSecondFollowupResp = new CpsRaSecondFollowupResp();
                                    CpsRaSecondFollowupLkp cpsRaSecondFollowupLkp = (CpsRaSecondFollowupLkp) sessionFactory
                                            .getCurrentSession()
                                            .load(CpsRaSecondFollowupLkp.class, (long) s.getSecondaryFollowupLkpId());
                                    cpsRaSecondFollowupResp.setCpsRaSecondFollowupLkp(cpsRaSecondFollowupLkp);
                                    cpsRaSecondFollowupResp.setDtCreated(new Date());
                                    cpsRaSecondFollowupResp.setDtLastUpdate(new Date());
                                    cpsRaSecondFollowupResp.setIdCreatedPerson(sDMRiskAssessmentdto.getLoggedInUser());
                                    cpsRaSecondFollowupResp
                                            .setIdLastUpdatePerson(sDMRiskAssessmentdto.getLoggedInUser());
                                    cpsRaSecondFollowupResp.setIndCpsRaSecondaryFollowup(s.getIndSecFollowupLookup());
                                    cpsRaSecondFollowupResps.add(cpsRaSecondFollowupResp);
                                    cpsRaSecondFollowupResp.setCpsRaFollowupResponse(cpsRaFollowupResponse);
                                    cpsRaFollowupResponse.setCpsRaSecondFollowupResps(cpsRaSecondFollowupResps);
                                });
                            }
                            cpsRaFollowupResponseList.add(cpsRaFollowupResponse);
                            cpsRaResponse.setCpsRaFollowupResponses(cpsRaFollowupResponseList);

                        });

                    }
                    cpsRaResponses.add(cpsRaResponse);
                }
            });

        });
        cpsRa.setCpsRaResponses(cpsRaResponses);

        Long idCpsRa = (Long) sessionFactory.getCurrentSession().save(cpsRa);
        sDMRiskAssessmentdto.setId(idCpsRa.intValue());
        sDMRiskAssessmentdto.setIdEvent(cpsRa.getEvent().getIdEvent());
        return sDMRiskAssessmentdto;

    }

    // This method is used to insert IdCpsRaAnswerLookup for the
    // CpsRaResponse table
    @Override
    public long addRiskResponse(SDMRiskAssessmentAnswerDto answerDB) {
        CpsRaResponse cpsRaResponse = new CpsRaResponse();

        cpsRaResponse.setIdCpsRaResponse(answerDB.getRiskAssessmentId());

        CpsRaAnswerLookup cpsRaAnswerLookup = new CpsRaAnswerLookup();
        cpsRaAnswerLookup.setIdCpsRaAnswerLookup(answerDB.getAnswerLookupId());
        cpsRaResponse.setCpsRaAnswerLookup(cpsRaAnswerLookup);

        CpsRaQstnLookup cpsRaQstnLookup = new CpsRaQstnLookup();
        cpsRaQstnLookup.setIdCpsRaQstnLookup(answerDB.getQuestionLookupId());
        cpsRaResponse.setCpsRaQstnLookup(cpsRaQstnLookup);

        cpsRaResponse.setCdRaAnswer(answerDB.getResponseCode());
        cpsRaResponse.setDtCreated(new Date());
        cpsRaResponse.setIdCreatedPerson(answerDB.getLoggedInUser());
        cpsRaResponse.setDtLastUpdate(answerDB.getDateLastUpdate());
        cpsRaResponse.setIdLastUpdatePerson(answerDB.getLoggedInUser());

        return (long) sessionFactory.getCurrentSession().save(cpsRaResponse);

    }

    // This method is used to update CpsRa table
    @Override
    public long updateScoresAndRiskLevels(SDMRiskAssessmentDto sDMRiskAssessmentdto) {
        long Result = 0;
        Criteria criteria1 = sessionFactory.getCurrentSession().createCriteria(CpsRa.class);
        Event event = new Event();
        event.setIdEvent((long) sDMRiskAssessmentdto.getEventId());

        criteria1.add(Restrictions.eq("event.idEvent", event.getIdEvent()));
        criteria1.add(Restrictions.le("dtLastUpdate", sDMRiskAssessmentdto.getDateLastUpdate()));

        List<CpsRa> eventPersonLinks = criteria1.list();

        for (CpsRa eventPersonLink : eventPersonLinks) {
            eventPersonLink.setIdLastUpdatePerson(sDMRiskAssessmentdto.getLoggedInUser());
            ;
            eventPersonLink.setNbrFnScore((long) sDMRiskAssessmentdto.getFutureNeglectScore());
            eventPersonLink.setNbrFaScore((long) sDMRiskAssessmentdto.getFutureAbuseScore());
            if (!ObjectUtils.isEmpty(sDMRiskAssessmentdto.getFutureNeglectRiskLevelCode())) {
                eventPersonLink.setCdFnRiskLevel(sDMRiskAssessmentdto.getFutureNeglectRiskLevelCode());
            }
            if (!ObjectUtils.isEmpty(sDMRiskAssessmentdto.getFutureAbuseRiskLevelCode())) {
                eventPersonLink.setCdFaRiskLevel(sDMRiskAssessmentdto.getFutureAbuseRiskLevelCode());
            }
            if (!ObjectUtils.isEmpty(sDMRiskAssessmentdto.getScoredRiskLevelCode())) {
                eventPersonLink.setCdScoredRiskLevel(sDMRiskAssessmentdto.getScoredRiskLevelCode());
            }
            if (!ObjectUtils.isEmpty(sDMRiskAssessmentdto.getOverrideCode())) {
                eventPersonLink.setCdOverride(sDMRiskAssessmentdto.getOverrideCode());
            }
            if (!ObjectUtils.isEmpty(sDMRiskAssessmentdto.getOverrideReason())) {
                eventPersonLink.setTxtDiscOverrideReason(sDMRiskAssessmentdto.getOverrideReason());
            }
            if (!ObjectUtils.isEmpty(sDMRiskAssessmentdto.getIndPOChildLessThanThreeInjured())) {
                eventPersonLink.setIndPoInjuryChild3(
                        Boolean.toString(sDMRiskAssessmentdto.getIndPOChildLessThanThreeInjured()));
            }
            if (!ObjectUtils.isEmpty(sDMRiskAssessmentdto.getIndPOChildSexualAbuse())) {
                eventPersonLink.setIndPoSxab(Boolean.toString(sDMRiskAssessmentdto.getIndPOChildSexualAbuse()));
            }
            if (!ObjectUtils.isEmpty(sDMRiskAssessmentdto.getIndPOChildLessThanSixteenInjured())) {
                eventPersonLink.setIndPoInjuryChild16(
                        Boolean.toString(sDMRiskAssessmentdto.getIndPOChildLessThanSixteenInjured()));
            }
            if (!ObjectUtils.isEmpty(sDMRiskAssessmentdto.getIndPOChildDeath())) {
                eventPersonLink.setIndPoChildDeath(Boolean.toString(sDMRiskAssessmentdto.getIndPOChildDeath()));
            }
            if (!ObjectUtils.isEmpty(sDMRiskAssessmentdto.getFinalRiskLevelCode())) {
                eventPersonLink.setCdFinalRiskLevel(sDMRiskAssessmentdto.getFinalRiskLevelCode());
            }

            sessionFactory.getCurrentSession().saveOrUpdate(eventPersonLink);
            Result++;
        }
        return Result;

    }

    // This method is used to update CpsRaResponse table
    @Override
    public long updateRiskResponse(SDMRiskAssessmentAnswerDto answerDB) {
        long Result = 0;
        Criteria criteria1 = sessionFactory.getCurrentSession().createCriteria(CpsRaResponse.class);
        CpsRa cpsRa = new CpsRa();
        cpsRa.setIdCpsRa(answerDB.getRiskAssessmentId());
        criteria1.add(Restrictions.eq("cpsRa.idCpsRa", cpsRa.getIdCpsRa()));
        criteria1.add(Restrictions.eq("idCpsRaResponse", answerDB.getResponseId()));
        criteria1.add(Restrictions.eq("dtLastUpdate", answerDB.getDateLastUpdate()));

        List<CpsRaResponse> cpsRaResponseList = criteria1.list();

        for (CpsRaResponse cpsRaResponse : cpsRaResponseList) {

            cpsRaResponse.setCdRaAnswer(answerDB.getResponseCode());
            cpsRaResponse.setIdLastUpdatePerson(answerDB.getLoggedInUser());

            sessionFactory.getCurrentSession().saveOrUpdate(cpsRaResponse);
            Result++;
        }
        return Result;

    }

    // This method is used to insert IdCpsRaFollowupLookup for the
    // CpsRaFollowupResponse table
    @Override
    public long addRiskFollowupResponse(SDMRiskAssessmentFollowupDto followupDB) {
        CpsRaFollowupResponse cpsRaFollowupResponse = new CpsRaFollowupResponse();

        CpsRaFollowupLookup cpsRaFollowupLookup = new CpsRaFollowupLookup();
        cpsRaFollowupLookup.setIdCpsRaFollowupLookup(followupDB.getFollowupLookupId());
        cpsRaFollowupResponse.setCpsRaFollowupLookup(cpsRaFollowupLookup);

        CpsRaResponse cpsRaResponse = new CpsRaResponse();
        cpsRaResponse.setIdCpsRaResponse(followupDB.getResponseId());
        cpsRaFollowupResponse.setCpsRaResponse(cpsRaResponse);

        cpsRaFollowupResponse.setIndCpsRaFollowup(followupDB.getIndRaFollowup());
        cpsRaFollowupResponse.setDtCreated(new Date());
        cpsRaFollowupResponse.setIdCreatedPerson(followupDB.getLoggedInUser());
        cpsRaFollowupResponse.setDtLastUpdate(followupDB.getDateLastUpdate());
        cpsRaFollowupResponse.setIdLastUpdatePerson(followupDB.getLoggedInUser());

        return (long) sessionFactory.getCurrentSession().save(cpsRaFollowupResponse);

    }

    // This method is used to insert IdCpsRaSecondFollowupLkp for the
    // CpsRaSecondFollowupResp table
    @Override
    public long addRiskSecondFollowupResponse(SDMRiskAssmtSecondaryFollowupDto secFollowupDB) {

        CpsRaSecondFollowupResp cpsRaSecondFollowupResp = new CpsRaSecondFollowupResp();

        CpsRaSecondFollowupLkp cpsRaSecondFollowupLkp = new CpsRaSecondFollowupLkp();
        cpsRaSecondFollowupLkp.setIdCpsRaSecondFollowupLkp(secFollowupDB.getSecondaryFollowupLkpId());
        ;
        cpsRaSecondFollowupResp.setCpsRaSecondFollowupLkp(cpsRaSecondFollowupLkp);

        CpsRaFollowupResponse cpsRaFollowupResponse = new CpsRaFollowupResponse();
        cpsRaFollowupResponse.setIdCpsRaFollowupResponse(secFollowupDB.getSecondFollowupResponseId());
        cpsRaSecondFollowupResp.setCpsRaFollowupResponse(cpsRaFollowupResponse);

        cpsRaSecondFollowupResp.setIndCpsRaSecondaryFollowup(secFollowupDB.getIndSecFollowupLookup());
        cpsRaSecondFollowupResp.setDtCreated(new Date());
        cpsRaSecondFollowupResp.setIdCreatedPerson(secFollowupDB.getLoggedInUser());
        cpsRaSecondFollowupResp.setDtLastUpdate(secFollowupDB.getDateLastUpdate());
        cpsRaSecondFollowupResp.setIdLastUpdatePerson(secFollowupDB.getLoggedInUser());

        return (long) sessionFactory.getCurrentSession().save(cpsRaSecondFollowupResp);

    }

    // This method is used to insert IdCpsRaFollowupResponse for the
    // CpsRaSecondFollowupResp table
    @Override
    public long updateRiskSecondFollowupResponse(SDMRiskAssmtSecondaryFollowupDto secFollowupDB) {
        long Result = 0;
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CpsRaSecondFollowupResp.class);
        criteria.add(Restrictions.eq("idCpsRaSecondFollowupResp", secFollowupDB.getSecondFollowupResponseId()));

        CpsRaFollowupResponse cpsRaFollowupResponse = new CpsRaFollowupResponse();
        cpsRaFollowupResponse.setIdCpsRaFollowupResponse(secFollowupDB.getSecondFollowupResponseId());
        criteria.add(Restrictions.eq("cpsRaFollowupResponse.idCpsRaSecondFollowupResp",
                cpsRaFollowupResponse.getIdCpsRaFollowupResponse()));

        criteria.add(Restrictions.le("dtLastUpdate", secFollowupDB.getDateLastUpdate()));

        List<CpsRaSecondFollowupResp> cpsRaSecondFollowupRespList = criteria.list();

        for (CpsRaSecondFollowupResp cpsRaSecondFollowupResp : cpsRaSecondFollowupRespList) {

            cpsRaSecondFollowupResp.setIndCpsRaSecondaryFollowup(secFollowupDB.getIndSecFollowupLookup());
            cpsRaSecondFollowupResp.setIdLastUpdatePerson(secFollowupDB.getLoggedInUser());
            sessionFactory.getCurrentSession().saveOrUpdate(cpsRaFollowupResponse);
            Result++;
        }
        return Result;

    }

    // This method is used to insert IndCpsRaFollowup, IdLastUpdatePerson
    // for the
    // CpsRaFollowupResponse table
    @Override
    public long updateRiskFollowupResponse(SDMRiskAssessmentFollowupDto followupDB) {
        long Result = 0;
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CpsRaFollowupResponse.class);
        criteria.add(Restrictions.eq("idCpsRaFollowupResponse", followupDB.getFollowupResponseId()));

        CpsRaResponse cpsRaResponse = new CpsRaResponse();
        cpsRaResponse.setIdCpsRaResponse(followupDB.getResponseId());
        criteria.add(Restrictions.eq("cpsRaResponse.idCpsRaResponse", followupDB.getResponseId()));

        criteria.add(Restrictions.le("dtLastUpdate", followupDB.getDateLastUpdate()));

        List<CpsRaFollowupResponse> cpsRaFollowupResponseList = criteria.list();

        for (CpsRaFollowupResponse cpsRaFollowupResponse : cpsRaFollowupResponseList) {

            cpsRaFollowupResponse.setIndCpsRaFollowup(followupDB.getIndRaFollowup());
            cpsRaFollowupResponse.setIdLastUpdatePerson(followupDB.getLoggedInUser());
            sessionFactory.getCurrentSession().saveOrUpdate(cpsRaFollowupResponse);
            Result++;
        }
        return Result;

    }

    // This method is used to insert IndPrCaregiver, IndSeCaregiver for the
    // StagePersonLink table
    @Override
    public long updatePrimarySecondaryCaretaker(StagePersonValueDto stageValueBean, long idStage) {
        long Result = 0;
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(StagePersonLink.class);

        criteria.add(Restrictions.eq("idStage", idStage));
        criteria.add(Restrictions.eq("idPerson", stageValueBean.getIdPerson()));
        // criteria.add(Restrictions.le("dtLastUpdate",
        // stageValueBean.getDtLastUpdate()));

        List<StagePersonLink> eventPersonLinks = criteria.list();

        for (StagePersonLink eventPersonLink : eventPersonLinks) {

            eventPersonLink.setIndPrCaregiver(stageValueBean.getIndPrimaryCaretaker());
            eventPersonLink.setIndSeCaregiver(stageValueBean.getIndSecCaretaker());
            sessionFactory.getCurrentSession().saveOrUpdate(eventPersonLink);
            Result++;
        }
        return Result;

    }

    @Override
    public String deleteRiskAssessment(SDMRiskAssessmentDto sDMRiskAssessmentdto) {
        String message = "";

        //[artf171228] ALM-16546: Pending approvals are not invalidating, if risk assesment is deleted.
        if (!TypeConvUtil.isNullOrEmpty(sDMRiskAssessmentdto)
                && !TypeConvUtil.isNullOrEmpty(sDMRiskAssessmentdto.getApprovalEvent())
                && !TypeConvUtil.isNullOrEmpty(sDMRiskAssessmentdto.getApprovalEvent().getIdEvent())) {

            ApprovalCommonInDto approvalCommonInDto = new ApprovalCommonInDto();
            approvalCommonInDto.setIdEvent(sDMRiskAssessmentdto.getApprovalEvent().getIdEvent());
            approvalService.callCcmn05uService(approvalCommonInDto);

        }

        CpsRa cpsRa = (CpsRa) sessionFactory.getCurrentSession().load(CpsRa.class,
                Long.valueOf(sDMRiskAssessmentdto.getId()));
        Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(getHouseHoldForRiskAssmtSql)
                .setParameter("id", cpsRa.getIdCpsRa());
        Object houseHoldId = query.uniqueResult();
        if (null != houseHoldId) {
            AssessmentHouseholdLink householdLink = (AssessmentHouseholdLink) sessionFactory.getCurrentSession()
                    .load(AssessmentHouseholdLink.class, ((BigDecimal) houseHoldId).longValue());
            if (!TypeConvUtil.isNullOrEmpty(householdLink)) {
                sessionFactory.getCurrentSession().delete(householdLink);
            }
        }

        if (!TypeConvUtil.isNullOrEmpty(cpsRa) && !TypeConvUtil.isNullOrEmpty(cpsRa.getEvent().getIdEvent())) {
            Event event = (Event) sessionFactory.getCurrentSession().load(Event.class,
                    Long.valueOf(cpsRa.getEvent().getIdEvent()));
            if (!TypeConvUtil.isNullOrEmpty(event)) {
                sessionFactory.getCurrentSession().delete(event);
            }
            message = ServiceConstants.SUCCESS;
        }
        return message;
    }

    @Override
    public SDMRiskAssessmentDto getHouseholdName(SDMRiskAssessmentDto sDMRiskAssessmentdto) {
        SDMRiskAssessmentDto sDMRiskAssessmentDto = new SDMRiskAssessmentDto();
        Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(getHouseHoldNameSql)
                .addScalar("nmHouseHoldPersonFull", StandardBasicTypes.STRING)
                .addScalar("idHouseHoldPerson", StandardBasicTypes.LONG)
                .addScalar("idPrimaryCaregiver", StandardBasicTypes.LONG)
                .addScalar("idSecondaryCaregiver", StandardBasicTypes.LONG)
                .addScalar("idCreatedPerson", StandardBasicTypes.LONG)
                .setParameter("idAsmntCpsRa", Long.valueOf(sDMRiskAssessmentdto.getId()))
                .setResultTransformer(Transformers.aliasToBean(SDMRiskAssessmentDto.class));
        sDMRiskAssessmentDto = (SDMRiskAssessmentDto) query.uniqueResult();
        return sDMRiskAssessmentDto;
    }

    public SDMRiskAssessmentDto getCaregiverNames(int idAsmntCpsRa) {
        Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(getCareGiverNamesSql)
                .addScalar("nmPrimaryCaregiver", StandardBasicTypes.STRING)
                .addScalar("nmSecondaryCaregiver", StandardBasicTypes.STRING)
                .setParameter("idAsmntCpsRa", idAsmntCpsRa)
                .setResultTransformer(Transformers.aliasToBean(SDMRiskAssessmentDto.class));
        SDMRiskAssessmentDto sdmRiskAssessmentDto = (SDMRiskAssessmentDto) query.uniqueResult();
        return sdmRiskAssessmentDto;
    }

    @Override
    public SDMRiskAssessmentRes saveHouseholdDtl(SDMRiskAssessmentDto sDMRiskAssessmentdto) {

        SDMRiskAssessmentRes res = new SDMRiskAssessmentRes();
        AssessmentHouseholdLink assessmentHouseholdLink = new AssessmentHouseholdLink();
        if (!TypeConvUtil.isNullOrEmpty(sDMRiskAssessmentdto.getId())
                && !TypeConvUtil.isNullOrEmpty(sDMRiskAssessmentdto.getIdHouseHoldPerson())) {
            CpsRa cpsRa = (CpsRa) sessionFactory.getCurrentSession().load(CpsRa.class,
                    Long.valueOf(sDMRiskAssessmentdto.getId()));
            Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(getHouseHoldForRiskAssmtSql)
                    .setParameter("id", cpsRa.getIdCpsRa());
            Object houseHoldId = query.uniqueResult();
            if (null != houseHoldId) {
                assessmentHouseholdLink = (AssessmentHouseholdLink) sessionFactory.getCurrentSession()
                        .load(AssessmentHouseholdLink.class, ((BigDecimal) houseHoldId).longValue());

                assessmentHouseholdLink.setIdAsmntCpsRa(cpsRa.getIdCpsRa());
                Person person = (Person) sessionFactory.getCurrentSession().get(Person.class,
                        sDMRiskAssessmentdto.getIdHouseHoldPerson());
                assessmentHouseholdLink.setIdHshldPerson(person);
                assessmentHouseholdLink.setDtLastUpdate(new Date());
                if (!TypeConvUtil.isNullOrEmpty(sDMRiskAssessmentdto.getIdPrimaryCaregiver())) {
                    assessmentHouseholdLink.setIdPrmryCrgvr(sDMRiskAssessmentdto.getIdPrimaryCaregiver());
                }
                if (!TypeConvUtil.isNullOrEmpty(sDMRiskAssessmentdto.getIdSecondaryCaregiver())) {
                    assessmentHouseholdLink.setIdSecndryCrgvr(sDMRiskAssessmentdto.getIdSecondaryCaregiver());
                } else {
                    assessmentHouseholdLink.setIdSecndryCrgvr(null);
                }

                assessmentHouseholdLink.setIdLastUpdatePerson(Long.valueOf(sDMRiskAssessmentdto.getLoggedInUser()));
                sessionFactory.getCurrentSession().update(assessmentHouseholdLink);
            } else {
                assessmentHouseholdLink.setIdAsmntCpsRa(cpsRa.getIdCpsRa());
                Person person = (Person) sessionFactory.getCurrentSession().get(Person.class,
                        sDMRiskAssessmentdto.getIdHouseHoldPerson());
                assessmentHouseholdLink.setIdHshldPerson(person);
                assessmentHouseholdLink.setIdCreatedPerson(Long.valueOf(sDMRiskAssessmentdto.getLoggedInUser()));
                assessmentHouseholdLink.setDtCreated(new Date());
                assessmentHouseholdLink.setDtLastUpdate(new Date());
                if (!TypeConvUtil.isNullOrEmpty(sDMRiskAssessmentdto.getIdPrimaryCaregiver())) {
                    assessmentHouseholdLink.setIdPrmryCrgvr(sDMRiskAssessmentdto.getIdPrimaryCaregiver());
                }
                if (!TypeConvUtil.isNullOrEmpty(sDMRiskAssessmentdto.getIdSecondaryCaregiver())) {
                    assessmentHouseholdLink.setIdSecndryCrgvr(sDMRiskAssessmentdto.getIdSecondaryCaregiver());
                }

                assessmentHouseholdLink.setIdLastUpdatePerson(new Long(sDMRiskAssessmentdto.getLoggedInUser()));
                sessionFactory.getCurrentSession().saveOrUpdate(assessmentHouseholdLink);
            }

            res.setMessage(ServiceConstants.SUCCESS);
        }
        return res;
    }

    /*
     * (non-Javadoc)
     *
     * @see us.tx.state.dfps.service.SDM.dao.SDMRiskAssessmentDao#
     * getExistingRAForHousehold(us.tx.state.dfps.service.investigation.dto.
     * SDMRiskAssessmentDto)
     *
     */
    @SuppressWarnings("unchecked")
    @Override
    public SDMRiskAssessmentRes getExistingRAForHousehold(SDMRiskAssessmentDto retrieveHouseHoldForCaseReq) {
        SDMRiskAssessmentRes response = new SDMRiskAssessmentRes();
        Long stageId = new Long(retrieveHouseHoldForCaseReq.getIdStage());
        List<SDMRiskAssessmentDto> riskAssessmentDtoList = null;
        riskAssessmentDtoList = (List<SDMRiskAssessmentDto>) ((SQLQuery) sessionFactory.getCurrentSession()
                .createSQLQuery(getRiskAssmtForHouseholdSql)
                .setParameter("idCase", retrieveHouseHoldForCaseReq.getCaseId())
                .setParameter("idHouseHoldPerson", retrieveHouseHoldForCaseReq.getIdHouseHoldPerson())
                .setParameter("idStage", stageId.intValue())).addScalar("id", StandardBasicTypes.INTEGER)
                .addScalar("idStage", StandardBasicTypes.LONG)
                .setResultTransformer(Transformers.aliasToBean(SDMRiskAssessmentDto.class)).list();
        if (!riskAssessmentDtoList.isEmpty()) {
            response.setsDMRiskAssessmentdto(riskAssessmentDtoList.get(0));
        }
        return response;
    }

    /**
     *
     * Method Name: queryRiskAssessment Method Description:Returns Risk
     * Assessment DataBean based on Risk Assessment Event Id and Stage Id It
     * pulls back the questions, answers, followups, secondary followups and
     * responses
     *
     * @param idEvent
     * @param idStage
     * @return
     */
    @SuppressWarnings({"unchecked", "unused", "rawtypes"})
    @Override
    public SDMRiskAssessmentDto queryRiskAssessment(Long idEvent, Long idStage) {
        Long idPrimary = ServiceConstants.ZERO_VAL;
        Long idSecondary = ServiceConstants.ZERO_VAL;
        Long secFlpLkpId = ServiceConstants.ZERO_VAL;
        Long flpLkpId = ServiceConstants.ZERO_VAL;
        Long questionLkpId = ServiceConstants.ZERO_VAL;
        Long answerLkpId = ServiceConstants.ZERO_VAL;
        Long previousQuestionLkpId = ServiceConstants.ZERO_VAL;
        Long previousAnswerLkpId = ServiceConstants.ZERO_VAL;
        Long prevFlwUp = ServiceConstants.ZERO_VAL;
        List<SDMRiskAssessmentQuestionDto> questions = new ArrayList<SDMRiskAssessmentQuestionDto>();
        SDMRiskAssessmentDto riskDto = null;
        new ArrayList<SDMRiskAssessmentFollowupDto>();
        new ArrayList<SDMRiskAssmtSecondaryFollowupDto>();
        SDMRiskAssessmentQuestionDto questionDto = null;
        SDMRiskAssessmentAnswerDto answerDto = null;
        SDMRiskAssessmentFollowupDto followupDto = null;
        SDMRiskAssmtSecondaryFollowupDto secFollowupDto = null;
        Map<?, ?> prePopAnswersMap = new HashMap<Object, Object>();

        Map<String, List<IntakeAllegationDto>> idStageListMap = new HashMap<String, List<IntakeAllegationDto>>();

        Query query = sessionFactory.getCurrentSession().createSQLQuery(getPersonIdSql)
                .addScalar("indPrCaregiver", StandardBasicTypes.STRING)
                .addScalar("indSeCaregiver", StandardBasicTypes.STRING).addScalar("idPerson", StandardBasicTypes.LONG)
                .setParameter("idStage", idStage)
                .setResultTransformer(Transformers.aliasToBean(StagePersonLinkDto.class));

        List<StagePersonLinkDto> stagePersonLinkDtoList = query.list();

        for (StagePersonLinkDto stagePersonLinkDto : stagePersonLinkDtoList) {
            if (ServiceConstants.STRING_IND_Y.equalsIgnoreCase(stagePersonLinkDto.getIndPrCaregiver())) {
                idPrimary = stagePersonLinkDto.getIdPerson();
            }

            if (ServiceConstants.STRING_IND_Y.equalsIgnoreCase(stagePersonLinkDto.getIndSeCaregiver())) {
                idSecondary = stagePersonLinkDto.getIdPerson();
            }
        }

        idStageListMap = getStageIdList(idStage, idPrimary, idSecondary);

        Query riskAssmtQuery = sessionFactory.getCurrentSession().createSQLQuery(getRiskAssemmentSql)
                .addScalar("idCpsRa", StandardBasicTypes.LONG).addScalar("idStage", StandardBasicTypes.LONG)
                //[artf171227] ALM-16545: Risk Assessment Created By time stamp is is not displaying.
                .addScalar("dtCreadted", StandardBasicTypes.TIMESTAMP).addScalar("idCreatedPerson", StandardBasicTypes.LONG)
                .addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP)
                .addScalar("idLastUpdatePerson", StandardBasicTypes.LONG)
                .addScalar("createdByName", StandardBasicTypes.STRING)
                .addScalar("updatedByName", StandardBasicTypes.STRING).addScalar("nbrVersion", StandardBasicTypes.LONG)
                .addScalar("idCpsRaAssmtLookup", StandardBasicTypes.LONG).addScalar("idCase", StandardBasicTypes.LONG)
                .addScalar("idEvent", StandardBasicTypes.LONG).addScalar("cdEventStatus", StandardBasicTypes.STRING)
                .addScalar("dtLastUpdateEvent", StandardBasicTypes.TIMESTAMP)
                .addScalar("cdTask", StandardBasicTypes.STRING).addScalar("nmCase", StandardBasicTypes.STRING)
                .addScalar("dtEventOccurred", StandardBasicTypes.DATE)
                .addScalar("dtAssmtCompleted", StandardBasicTypes.DATE).addScalar("nbrFnScore", StandardBasicTypes.LONG)
                .addScalar("dtStageClosure",StandardBasicTypes.DATE)
                .addScalar("nbrFaScore", StandardBasicTypes.LONG).addScalar("cdFnRiskLevel", StandardBasicTypes.STRING)
                .addScalar("cdFaRiskLevel", StandardBasicTypes.STRING)
                .addScalar("cdScoredRiskLevel", StandardBasicTypes.STRING)
                .addScalar("cdOverride", StandardBasicTypes.STRING)
                .addScalar("txtDiscOverrideReason", StandardBasicTypes.STRING)
                .addScalar("cdFinalRiskLevel", StandardBasicTypes.STRING)
                .addScalar("indPoInjuryChild3", StandardBasicTypes.STRING)
                .addScalar("indPoSxab", StandardBasicTypes.STRING)
                .addScalar("indPoInjuryChild16", StandardBasicTypes.STRING)
                .addScalar("indPoChildDeath", StandardBasicTypes.STRING)
                .addScalar("nbrFnLowMin", StandardBasicTypes.LONG).addScalar("nbrFnLowMax", StandardBasicTypes.LONG)
                .addScalar("nbrFnModMin", StandardBasicTypes.LONG).addScalar("nbrFnModMax", StandardBasicTypes.LONG)
                .addScalar("nbrFnHighMin", StandardBasicTypes.LONG).addScalar("nbrFnHighMax", StandardBasicTypes.LONG)
                .addScalar("nbrFaLowMin", StandardBasicTypes.LONG).addScalar("nbrFaLowMax", StandardBasicTypes.LONG)
                .addScalar("nbrFaModMin", StandardBasicTypes.LONG).addScalar("nbrFaModMax", StandardBasicTypes.LONG)
                .addScalar("nbrFaHighMin", StandardBasicTypes.LONG).addScalar("nbrFaHighMax", StandardBasicTypes.LONG)
                .setParameter("idEvent", idEvent).setResultTransformer(Transformers.aliasToBean(RiskAssmtDtlDto.class));

        RiskAssmtDtlDto riskAssmtDtlDto = (RiskAssmtDtlDto) riskAssmtQuery.uniqueResult();

        if (ObjectUtils.isEmpty(riskAssmtDtlDto)) {
            throw new DataLayerException("No SDM Risk Assessment Data found for event: " + idEvent);
        }

        Query riskAssmtRspnQuery = sessionFactory.getCurrentSession().createSQLQuery(getRiskAssmtRspnSql)
                .addScalar("idCpsRaAssmtLookup", StandardBasicTypes.LONG)
                .addScalar("idCpsRaQstnLookup", StandardBasicTypes.LONG)
                .addScalar("cdRaQuestion", StandardBasicTypes.STRING)
                .addScalar("txtQuestion", StandardBasicTypes.STRING)
                .addScalar("nmDefinition", StandardBasicTypes.STRING)
                .addScalar("idCpsRaAnswerLookup", StandardBasicTypes.LONG)
                .addScalar("cralCdRaAnswer", StandardBasicTypes.STRING)
                .addScalar("cralNbrFnIndexValue", StandardBasicTypes.LONG)
                .addScalar("cralNbrFaIndexValue", StandardBasicTypes.LONG)
                .addScalar("txtAnswer", StandardBasicTypes.STRING).addScalar("idCpsRa", StandardBasicTypes.LONG)
                .addScalar("idCpsRaResponse", StandardBasicTypes.LONG)
                .addScalar("crrCdRaAnswer", StandardBasicTypes.STRING)
                .addScalar("crrDtCreated", StandardBasicTypes.DATE)
                .addScalar("crrIdCreatedPerson", StandardBasicTypes.LONG)
                .addScalar("crrDtLastUpdate", StandardBasicTypes.DATE)
                .addScalar("crrIdLastUpdatePerson", StandardBasicTypes.LONG)
                .addScalar("idCpsRaFollowupLookup", StandardBasicTypes.LONG)
                .addScalar("txtFollowupQstn", StandardBasicTypes.STRING)
                .addScalar("crflNbrFnIndexValue", StandardBasicTypes.LONG)
                .addScalar("crflNbrFaIndexValue", StandardBasicTypes.LONG)
                .addScalar("cdFollowup", StandardBasicTypes.STRING)
                .addScalar("idCpsRaFollowupResponse", StandardBasicTypes.LONG)
                .addScalar("indCpsRaFollowup", StandardBasicTypes.STRING)
                .addScalar("crfrDtLastUpdate", StandardBasicTypes.DATE)
                .addScalar("crfrIdLastUpdatePerson", StandardBasicTypes.LONG)
                .addScalar("idCpsRaSecondFollowupLkp", StandardBasicTypes.LONG)
                .addScalar("txtSecondaryFollowupQstn", StandardBasicTypes.STRING)
                .addScalar("cdSecFollowup", StandardBasicTypes.STRING)
                .addScalar("idCpsRaSecondFollowupResp", StandardBasicTypes.LONG)
                .addScalar("indCpsRaSecondaryFollowup", StandardBasicTypes.STRING)
                .addScalar("crsfDtLastUpdate", StandardBasicTypes.DATE)
                .addScalar("crsfIdLastUpdatePerson", StandardBasicTypes.LONG)
                .addScalar("crsfDtCreated", StandardBasicTypes.DATE)
                .addScalar("crsfIdCreatedPerson", StandardBasicTypes.LONG)
                .setParameter("idCpsra", riskAssmtDtlDto.getIdCpsRa())
                .setResultTransformer(Transformers.aliasToBean(CpsRaResponseDto.class));

        List<CpsRaResponseDto> cpsRaResponseDtoList = riskAssmtRspnQuery.list();
        for (CpsRaResponseDto cpsRaResponseDto : cpsRaResponseDtoList) {
            questionLkpId = cpsRaResponseDto.getIdCpsRaQstnLookup();
            answerLkpId = cpsRaResponseDto.getIdCpsRaAnswerLookup();
            flpLkpId = cpsRaResponseDto.getIdCpsRaFollowupLookup();
            secFlpLkpId = cpsRaResponseDto.getIdCpsRaSecondFollowupLkp();

            if (questionLkpId.intValue() != previousQuestionLkpId.intValue()) {
                questionDto = new SDMRiskAssessmentQuestionDto();
                questionDto.setIdQuestion(cpsRaResponseDto.getIdCpsRaQstnLookup().intValue());
                questionDto.setQuestionCode(cpsRaResponseDto.getCdRaQuestion());
                questionDto.setQuestionText(cpsRaResponseDto.getTxtQuestion());
                questionDto.setNameDefinition(cpsRaResponseDto.getNmDefinition());

                answerDto = new SDMRiskAssessmentAnswerDto();
                setValuesAnswerDto(cpsRaResponseDto, answerDto, true);

                // add the prepopulated responses and the stageID
                answerDto.addPrepulatedAnswers(prePopAnswersMap);
                answerDto.addStageIdList(idStageListMap);
                questionDto.getAnswers().add(answerDto);
                questions.add(questionDto);
                flpLkpId = cpsRaResponseDto.getIdCpsRaFollowupLookup();

                // if there is a follow-up the flpLkpId won't be 0
                if (!ObjectUtils.isEmpty(flpLkpId)) {
                    followupDto = new SDMRiskAssessmentFollowupDto();
                    followupDto.setFollowupLookupId(flpLkpId.intValue());
                    // set values to the followupDB
                    setValuesFollowupDto(cpsRaResponseDto, followupDto, true);
                    answerDto.getFollowupQuestions().add(followupDto);

                    // if there is a secondary follow-up the secFlpLkpId won't
                    // be 0
                    if (!ObjectUtils.isEmpty(secFlpLkpId)) {
                        SDMRiskAssmtSecondaryFollowupDto secFlpDto = new SDMRiskAssmtSecondaryFollowupDto();
                        // set values to the secondaryfollowupDB
                        setValuesSecFollowupDto(cpsRaResponseDto, secFlpDto, true);
                        followupDto.getSecondaryFollowupQuestions().add(secFlpDto);

                    }
                }
            } else if (previousAnswerLkpId.intValue() != answerLkpId.intValue()) {
                answerDto = new SDMRiskAssessmentAnswerDto();
                setValuesAnswerDto(cpsRaResponseDto, answerDto, true);
                // add the prepopulated responses and the stageID
                answerDto.addPrepulatedAnswers(prePopAnswersMap);
                answerDto.addStageIdList(idStageListMap);
                // if there is a follow-up the flpLkpId won't be 0
                if (!ObjectUtils.isEmpty(flpLkpId)) {
                    followupDto = new SDMRiskAssessmentFollowupDto();
                    followupDto.setFollowupLookupId(cpsRaResponseDto.getIdCpsRaFollowupLookup().intValue());

                    // set values to the followupDto
                    setValuesFollowupDto(cpsRaResponseDto, followupDto, true);
                    answerDto.getFollowupQuestions().add(followupDto);

                    // if there is a secondary follow-up the secFlpLkpId won't
                    // be 0
                    if (!ObjectUtils.isEmpty(secFlpLkpId)) {

                        SDMRiskAssmtSecondaryFollowupDto secFlpDto = new SDMRiskAssmtSecondaryFollowupDto();
                        // set values to the followupDB
                        setValuesSecFollowupDto(cpsRaResponseDto, secFlpDto, true);
                        followupDto.getSecondaryFollowupQuestions().add(secFlpDto);

                    }

                }
                questionDto.getAnswers().add(answerDto);
            }
            /*
             * If the follow-up code changed, then create the follow-up and
             * secondary follow-up (if necessary) objects
             */
            else if (prevFlwUp.intValue() != flpLkpId.intValue()) {
                if (!ObjectUtils.isEmpty(flpLkpId)) {
                    followupDto = new SDMRiskAssessmentFollowupDto();
                    followupDto.setFollowupLookupId(cpsRaResponseDto.getIdCpsRaFollowupLookup().intValue());

                    // set values to the followupDto
                    setValuesFollowupDto(cpsRaResponseDto, followupDto, true);
                    answerDto.getFollowupQuestions().add(followupDto);

                    // if there is a secondary follow-up the secFlpLkpId won't
                    // be 0
                    if (!ObjectUtils.isEmpty(secFlpLkpId)) {
                        secFollowupDto = new SDMRiskAssmtSecondaryFollowupDto();
                        // set values to the secondary followupDB
                        setValuesSecFollowupDto(cpsRaResponseDto, secFollowupDto, true);
                        followupDto.getSecondaryFollowupQuestions().add(secFollowupDto);

                    }
                }

            }
            /*
             * Create the secondary follow-up objects
             */
            else {
                SDMRiskAssmtSecondaryFollowupDto secFlpDto = new SDMRiskAssmtSecondaryFollowupDto();
                // set values to the secondary followupDto
                setValuesSecFollowupDto(cpsRaResponseDto, secFlpDto, true);
                followupDto.getSecondaryFollowupQuestions().add(secFlpDto);
            }

            previousQuestionLkpId = questionLkpId;
            previousAnswerLkpId = answerLkpId;
            prevFlwUp = flpLkpId;
        }

        riskDto = new SDMRiskAssessmentDto(riskAssmtDtlDto, questions, true);

        if (idPrimary != 0L) {
            riskDto.setIdPrimaryCaregiver(idPrimary);
        }
        if (idSecondary != 0L) {
            riskDto.setIdSecondaryCaregiver(idSecondary);
        }
        //Warranty Defect - 12431 - To get the HouseHold Person Id
        SDMRiskAssessmentDto householdDtls = getHouseholdName(riskDto);
        //ALM ID : 13182 : sometimes (old) SDM Risk Assessments does not have house hold person id
        if (householdDtls != null)
            riskDto.setIdHouseHoldPerson(householdDtls.getIdHouseHoldPerson());
        return riskDto;

    }

    /**
     * Method Name: setValuesSecFollowupDto Method Description:
     *
     * @param cpsRaResponseDto
     * @param secFlpDto
     * @param isExistingRA
     */
    private void setValuesSecFollowupDto(CpsRaResponseDto cpsRaResponseDto, SDMRiskAssmtSecondaryFollowupDto secFlpDto,
                                         boolean isExistingRA) {
        if (!TypeConvUtil.isNullOrEmpty(cpsRaResponseDto.getIdCpsRaSecondFollowupLkp())) {
            secFlpDto.setSecondaryFollowupLkpId(cpsRaResponseDto.getIdCpsRaSecondFollowupLkp().intValue());
        }
        if (!TypeConvUtil.isNullOrEmpty(cpsRaResponseDto.getIdCpsRaFollowupLookup())) {
            secFlpDto.setFollowupLookupId(cpsRaResponseDto.getIdCpsRaFollowupLookup().intValue());
        }
        if (!TypeConvUtil.isNullOrEmpty(cpsRaResponseDto.getTxtSecondaryFollowupQstn())) {
            secFlpDto.setSecondaryFollowupQnText(cpsRaResponseDto.getTxtSecondaryFollowupQstn());
        }
        if (!TypeConvUtil.isNullOrEmpty(cpsRaResponseDto.getCdSecFollowup())) {
            secFlpDto.setFollowupCode(cpsRaResponseDto.getCdSecFollowup());
        }
        if (isExistingRA) {
            secFlpDto.setSecondFollowupResponseId(cpsRaResponseDto.getIdCpsRaSecondFollowupResp().intValue());

            secFlpDto.setFollowupResponseId(cpsRaResponseDto.getIdCpsRaFollowupResponse().intValue());

            secFlpDto.setDateLastUpdate(cpsRaResponseDto.getCrsfDtLastUpdate());

            secFlpDto.setIndSecFollowupLookup(cpsRaResponseDto.getIndCpsRaSecondaryFollowup());

        }

    }

    /**
     * Method Name: setValuesFollowupDto Method Description:
     *
     * @param cpsRaResponseDto
     * @param followupDto
     * @param isExistingRA
     */
    private void setValuesFollowupDto(CpsRaResponseDto cpsRaResponseDto, SDMRiskAssessmentFollowupDto followupDto,
                                      boolean isExistingRA) {

        if (!TypeConvUtil.isNullOrEmpty(cpsRaResponseDto.getIdCpsRaAnswerLookup())) {
            followupDto.setAnswerLookupId(cpsRaResponseDto.getIdCpsRaAnswerLookup().intValue());
        }
        if (!TypeConvUtil.isNullOrEmpty(cpsRaResponseDto.getTxtFollowupQstn())) {
            followupDto.setFollowupQuestionText(cpsRaResponseDto.getTxtFollowupQstn());
        }
        if (!TypeConvUtil.isNullOrEmpty(cpsRaResponseDto.getCrflNbrFnIndexValue())) {
            followupDto.setFutureNeglectIndexValue(cpsRaResponseDto.getCrflNbrFnIndexValue().intValue());
        }
        //[artf179548] Defect: 17149 - Risk Assesment Calculations
        if (!TypeConvUtil.isNullOrEmpty(cpsRaResponseDto.getCrflNbrFaIndexValue())) {
            followupDto.setFutureAbuseIndexValue(cpsRaResponseDto.getCrflNbrFaIndexValue().intValue());
        }
        if (!TypeConvUtil.isNullOrEmpty(cpsRaResponseDto.getCdFollowup())) {
            followupDto.setFollowupCode(cpsRaResponseDto.getCdFollowup());
        }
        if (isExistingRA) {
            followupDto.setIndRaFollowup(cpsRaResponseDto.getIndCpsRaFollowup());

            followupDto.setResponseId(cpsRaResponseDto.getIdCpsRaResponse().intValue());

            followupDto.setFollowupResponseId(cpsRaResponseDto.getIdCpsRaFollowupResponse().intValue());

            followupDto.setDateLastUpdate(cpsRaResponseDto.getCrfrDtLastUpdate());

        }

    }

    /**
     * Method Name: setValuesAnswerDto Method Description:
     *
     * @param cpsRaResponseDto
     * @param answerDto
     * @param isExistingRA
     */
    private void setValuesAnswerDto(CpsRaResponseDto cpsRaResponseDto, SDMRiskAssessmentAnswerDto answerDto,
                                    boolean isExistingRA) {
        answerDto.setAnswerLookupId(cpsRaResponseDto.getIdCpsRaAnswerLookup().intValue());

        answerDto.setQuestionLookupId(cpsRaResponseDto.getIdCpsRaQstnLookup().intValue());

        answerDto.setAnswerText(cpsRaResponseDto.getTxtAnswer());

        answerDto.setFutureAbuseIndexValue(cpsRaResponseDto.getCralNbrFaIndexValue().intValue());

        answerDto.setFutureNeglectIndexValue(cpsRaResponseDto.getCralNbrFnIndexValue().intValue());

        answerDto.setQuestionCode(cpsRaResponseDto.getCdRaQuestion());

        if (isExistingRA) {
            answerDto.setAnswerCode(cpsRaResponseDto.getCralCdRaAnswer());

            answerDto.setResponseCode(cpsRaResponseDto.getCrrCdRaAnswer());

            answerDto.setDateLastUpdate(cpsRaResponseDto.getCrrDtLastUpdate());

            answerDto.setResponseId(cpsRaResponseDto.getIdCpsRaResponse().intValue());

            answerDto.setRiskAssessmentId(cpsRaResponseDto.getIdCpsRa().intValue());

        } else {
            answerDto.setAnswerCode(cpsRaResponseDto.getCdRaAnswer());

        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * us.tx.state.dfps.service.SDM.dao.SDMRiskAssessmentDao#getidStageList(
     * java. lang.Long, java.lang.Long, java.lang.Long)
     */
    @Override
    public Map<String, List<IntakeAllegationDto>> getStageIdList(Long idStage, Long idPrimary, Long idSecondary) {
        String responseCode = null;
        Long priorNeglectCount = ServiceConstants.ZERO_VAL;
        Long priorAbuse = ServiceConstants.ZERO_VAL;
        Long primaryCareGiverHisCount = ServiceConstants.ZERO_VAL;
        Long secCareGiverHisCount = ServiceConstants.ZERO_VAL;
        HashMap<String, List<IntakeAllegationDto>> stageMap = new HashMap<String, List<IntakeAllegationDto>>();
        primaryCareGiverHisCount = Long.valueOf(getCareGiverHistoryList(idStage, idPrimary).size());
        secCareGiverHisCount = Long.valueOf(getCareGiverHistoryList(idStage, idSecondary).size());
        priorNeglectCount = Long.valueOf(getPriorNeglectList(idStage).size());
        priorAbuse = Long.valueOf(getPriorAbuseList(idStage).size());

        if (ServiceConstants.ONE_INT == priorNeglectCount.intValue()) {
            responseCode = ServiceConstants.FOURAB;
            stageMap.put(responseCode, getPriorNeglectList(idStage));
        }
        if (ServiceConstants.TWO_INT.intValue() == priorNeglectCount.intValue()) {
            responseCode = ServiceConstants.FOURAC;
            stageMap.put(responseCode, getPriorNeglectList(idStage));
        }
        if (priorNeglectCount.intValue() >= ServiceConstants.Three) {
            responseCode = ServiceConstants.FOURAD;
            stageMap.put(responseCode, getPriorNeglectList(idStage));
        }

        if (ServiceConstants.ONE_INT == priorAbuse.intValue()) {
            responseCode = ServiceConstants.FOURBB;
            stageMap.put(responseCode, getPriorAbuseList(idStage));
        }
        if (priorAbuse.intValue() >= ServiceConstants.INT_TWO) {
            responseCode = ServiceConstants.FOURBC;
            stageMap.put(responseCode, getPriorAbuseList(idStage));
        }

        if (primaryCareGiverHisCount.intValue() > ServiceConstants.Zero_INT) {
            responseCode = ServiceConstants.EIGHTB;
            stageMap.put(responseCode, getCareGiverHistoryList(idStage, idPrimary));
        } else if (secCareGiverHisCount > ServiceConstants.Zero_INT) {
            responseCode = ServiceConstants.FOURTEENC;
            stageMap.put(responseCode, getCareGiverHistoryList(idStage, idSecondary));
        }

        return stageMap;

    }

    /*
     * (non-Javadoc)
     *
     * @see us.tx.state.dfps.service.SDM.dao.SDMRiskAssessmentDao#
     * getCareGiverHistoryList (java.lang.Long, java.lang.Long)
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<IntakeAllegationDto> getCareGiverHistoryList(Long idStage, Long idPerson) {
        Query query = sessionFactory.getCurrentSession().createSQLQuery(getCareGiverHistoryListSql)
                .addScalar("idStage", StandardBasicTypes.LONG).addScalar("cdStage", StandardBasicTypes.STRING)
                .setParameter("idStage", idStage).setParameter("idPerson", idPerson)
                .setResultTransformer(Transformers.aliasToBean(StageDto.class));

        return query.list();
    }

    /*
     * (non-Javadoc)
     *
     * @see us.tx.state.dfps.service.SDM.dao.SDMRiskAssessmentDao#
     * getPriorNeglectList( java.lang.Long)
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<IntakeAllegationDto> getPriorNeglectList(Long idStage) {
        Long intStage = getIntakeStageId(idStage).getIdStage();
        Query query = sessionFactory.getCurrentSession().createSQLQuery(getPriorNeglectListSql)
                .addScalar("idStage", StandardBasicTypes.LONG).addScalar("cdStage", StandardBasicTypes.STRING)
                .setParameter("idStage", idStage).setParameter("idIntakeStage", intStage)
                .setResultTransformer(Transformers.aliasToBean(StageDto.class));
        return query.list();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * us.tx.state.dfps.service.SDM.dao.SDMRiskAssessmentDao#getPriorAbuseList(
     * java. lang.Long)
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<IntakeAllegationDto> getPriorAbuseList(Long idStage) {
        Long intStage = getIntakeStageId(idStage).getIdStage();
        Query query = sessionFactory.getCurrentSession().createSQLQuery(getPriorAbuseListSql)
                .addScalar("idStage", StandardBasicTypes.LONG).addScalar("cdStage", StandardBasicTypes.STRING)
                .setParameter("idStage", idStage).setParameter("idIntakeStage", intStage)
                .setResultTransformer(Transformers.aliasToBean(StageDto.class));
        return query.list();
    }


    /**
     * Method Name: getRiskAssessment. Method Description: This method gets the
     * cpa_ra record for input idStage
     *
     * @param idStage
     * @return SDMRiskAssessmentDto
     */
    @SuppressWarnings("unchecked")
    @Override
    public SDMRiskAssessmentDto getRiskAssessment(Long idStage) {
        SDMRiskAssessmentDto sdmRiskAssessmentDto = new SDMRiskAssessmentDto();
        List<SDMRiskAssessmentDto> sdmRiskAssessmentDtoList = sessionFactory.getCurrentSession()
                .createCriteria(CpsRa.class).createAlias("event", "event").createAlias("stage", "stage")
                .setProjection(Projections.projectionList().add(Projections.property("event.idEvent"), "idEvent")
                        .add(Projections.property("event.cdEventStatus"), "eventStatus")
                        .add(Projections.property("cdFinalRiskLevel"), "finalRiskLevelCode"))
                .add(Restrictions.eq("stage.idStage", idStage))
                .add(Restrictions.eq("event.cdTask", ServiceConstants.SDM_RISK_ASSMT_TASK))
                .setResultTransformer(Transformers.aliasToBean(SDMRiskAssessmentDto.class)).list();
        if (!ObjectUtils.isEmpty(sdmRiskAssessmentDtoList)) {
            sdmRiskAssessmentDto = sdmRiskAssessmentDtoList.get(ServiceConstants.Zero_INT);
        }

        return sdmRiskAssessmentDto;
    }

    /**
     * Method Name: queryRiskAssessmentDtls. Method Description: This service is
     * to retrieve if Risk Assessment details exists and the event status of
     * Risk Assessment.
     *
     * @param sdmRiskAssessmentDto
     * @return RiskAssmtValueDto
     */
    public SDMRiskAssessmentDto queryRiskAssessmentExists(SDMRiskAssessmentDto sdmRiskAssessmentDto) {
        SDMRiskAssessmentDto sdmRiskAssessmentResultDto = (SDMRiskAssessmentDto) ((SQLQuery) sessionFactory
                .getCurrentSession().createSQLQuery(getqueryRiskAssmtExistsSql)
                .setParameter("idCase", sdmRiskAssessmentDto.getCaseId())
                .setParameter("idStage", sdmRiskAssessmentDto.getIdStage()))
                .addScalar("idEvent", StandardBasicTypes.LONG)
                .addScalar("formVersionNumber", StandardBasicTypes.LONG)
                .addScalar("eventStatus", StandardBasicTypes.STRING)
                .setResultTransformer(Transformers.aliasToBean(SDMRiskAssessmentDto.class)).uniqueResult();
        return sdmRiskAssessmentResultDto;
    }

    /**
     * Method Name: getPersonDetails Method Description:Retrieves the list of
     * person names from the PERSON table
     *
     * @param sdmRiskAssessmentDto
     * @param idStage
     * @return SDMRiskAssessmentDto
     */
    @Override
    public SDMRiskAssessmentDto getPersonDetails(SDMRiskAssessmentDto sdmRiskAssessmentDto, Long idStage) {

        List<StagePersonValueDto> stagePersonValueList = new ArrayList<StagePersonValueDto>();
        SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(primaryPersDetailsSql);
        // Defect Fix# 13766: Check if the idEventStage of the SDM Risk assessment event(i.e.
        // stageId) is same as the current idStage passed. If yes load the PRNs of current stage
        // else load the PRNs of the SDMRiskAssessment's stage.
        // This is done to resolve the person mismatch if the selected household is merged in any
        // later stage.
        query.setParameter("idStage", !idStage.equals(sdmRiskAssessmentDto.getStageId()) ? sdmRiskAssessmentDto.getStageId() : idStage);
        query.addScalar("nmPersonFull", StandardBasicTypes.STRING).addScalar("nmPersonFirst", StandardBasicTypes.STRING)
                .addScalar("nmPersonLast", StandardBasicTypes.STRING)
                .addScalar("nmPersonMiddle", StandardBasicTypes.STRING)
                .addScalar("cdPersonSuffix", StandardBasicTypes.STRING).addScalar("idPerson", StandardBasicTypes.LONG)
                .addScalar("dtPersonBirth", StandardBasicTypes.DATE)
                .addScalar("cdStagePersRole", StandardBasicTypes.STRING)
                .addScalar("cdStagePersType", StandardBasicTypes.STRING)
                .addScalar("cdPersonRelationship", StandardBasicTypes.STRING)
                .addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP)
                .addScalar("indPrCaregiver", StandardBasicTypes.STRING)
                .addScalar("indSeCaregiver", StandardBasicTypes.STRING);
        List<PersonDto> primaryPersDetailsDtoList = query
                .setResultTransformer(Transformers.aliasToBean(PersonDto.class)).list();
        List<OptionDto> personList = new ArrayList<OptionDto>();
        for (PersonDto personDto : primaryPersDetailsDtoList) {
            // Set optionDto in PersonList
            StagePersonValueDto stagePersonValueDto = new StagePersonValueDto();
            String firstName = null;
            String lastName = null;
            String middleName = null;
            String suffix = null;

            if (personDto.getNmPersonFirst() != null) {
                firstName = personDto.getNmPersonFirst();
            }
            if (personDto.getNmPersonLast() != null) {
                lastName = personDto.getNmPersonLast();
            }
            if (personDto.getNmPersonMiddle() != null) {
                middleName = personDto.getNmPersonMiddle();
            }
            if (personDto.getCdPersonSuffix() != null) {
                suffix = personDto.getCdPersonSuffix();
            }
            StringBuilder caregiver = new StringBuilder();
            caregiver.append(lastName);
            caregiver.append(ServiceConstants.COMMA_SPACE);
            caregiver.append(firstName);
            if (null != middleName) {
                char[] cg1Mname = middleName.toCharArray();
                caregiver.append(ServiceConstants.SPACE);
                caregiver.append(cg1Mname[0]);
                caregiver.append(ServiceConstants.PERIOD);
            }
            if (null != suffix) {
                caregiver.append(ServiceConstants.COMMA_SPACE);
                String cg1Sname = suffix;
                caregiver.append(lookupDao.simpleDecodeSafe(ServiceConstants.CSUFFIX, cg1Sname));
            }
            String caretakerName = caregiver.toString();
            OptionDto option = new OptionDto();
            option.setCode(personDto.getIdPerson().toString());
            option.setDecode(caretakerName);
            option.setHashCodeSet(false);

            // Adding values in stagePersonValueDto
            // (idPerson,DateLastUpdate,PrimaryCareTakerInd,SecCareTakerInd)
            stagePersonValueDto.setIdPerson(personDto.getIdPerson());
            stagePersonValueDto.setDtLastUpdate(personDto.getDtLastUpdate());
            stagePersonValueDto.setIndPrimaryCaretaker(personDto.getIndPrCaregiver());
            stagePersonValueDto.setIndSecCaretaker(personDto.getIndSeCaregiver());

            // Set the PersonList and PrimaryCareTakerList
            stagePersonValueList.add(stagePersonValueDto);
            sdmRiskAssessmentDto.setPersonList(stagePersonValueList);
            /*
             * List<OptionDto> optionDtoList = new ArrayList<OptionDto>();
             * optionDtoList.add(option);
             */
            personList.add(option);
            sdmRiskAssessmentDto.setPrimaryCareTakerList(personList);
        }
        return sdmRiskAssessmentDto;
    }

    /**
     * Method Name: getPrePopulationAnswers Method Description:To prepopulate
     * the answers for questions 1, 2,3,4, 5
     *
     * @param stageId
     * @return Map
     */
    public Map getPrePopulationAnswers(Long stageId) throws DataNotFoundException {
        HashMap map = new HashMap();
        HashMap<String, List> stageMap = new HashMap<String, List>();
        long abuseCount;
        long neglectcount;
        long childrenAllegationCount;
        long youngChildrenCount;
        long childwithPriorInjuryCount;
        String responseCode = null;
        long priorNeglectCount;
        long priorAbuse;
        abuseCount = getAbuse(stageId);
        neglectcount = getNeglect(stageId);
        childrenAllegationCount = getNumChildrenInAllegation(stageId);
        youngChildrenCount = getNumOfYoungChildren(stageId);
        childwithPriorInjuryCount = getNumChildWithPriorInjury(stageId);
        priorNeglectCount = (long) getPriorNeglectList(stageId).size();
        priorAbuse = (long) getPriorAbuseList(stageId).size();

        if (abuseCount > ServiceConstants.ZERO_VAL) {
            responseCode = ServiceConstants.ONEA;
        }
        if (neglectcount > ServiceConstants.ZERO_VAL) {
            responseCode = ServiceConstants.ONEB;
        }
        if (abuseCount > ServiceConstants.ZERO_VAL && neglectcount > ServiceConstants.ZERO_VAL) {
            responseCode = ServiceConstants.ONEC;
        }
        map.put(ServiceConstants.ONE_QUESTION_LOOKUP_ID, responseCode);

        if (childrenAllegationCount > ServiceConstants.ZERO_VAL
                && childrenAllegationCount < ServiceConstants.FOUR_VAL) {
            responseCode = ServiceConstants.TWOA;
            map.put(ServiceConstants.TWO_QUESTION_LOOKUP_ID, responseCode);
        }
        if (childrenAllegationCount >= ServiceConstants.FOUR_VAL) {
            responseCode = ServiceConstants.TWOB;
            map.put(ServiceConstants.TWO_QUESTION_LOOKUP_ID, responseCode);
        }
        if (youngChildrenCount == ServiceConstants.ZERO_VAL) {
            responseCode = ServiceConstants.THREEA;
            map.put(ServiceConstants.THREE_QUESTION_LOOKUP_ID, responseCode);
        }

        if (youngChildrenCount > ServiceConstants.ZERO_VAL) {
            responseCode = ServiceConstants.THREEB;
            map.put(ServiceConstants.THREE_QUESTION_LOOKUP_ID, responseCode);
        }
        if (childwithPriorInjuryCount > ServiceConstants.ZERO_VAL) {
            responseCode = ServiceConstants.FIVEB;
            map.put(ServiceConstants.SEVEN_QUESTION_LOOKUP_ID, responseCode);

        }
        if ((priorNeglectCount == ServiceConstants.ZERO_VAL) && priorAbuse == ServiceConstants.ZERO_VAL) {
            responseCode = ServiceConstants.FOURA;
            map.put(ServiceConstants.FOUR_QUESTION_LOOKUP_ID, responseCode);
        }
        if ((priorNeglectCount != ServiceConstants.ZERO_VAL) || priorAbuse != ServiceConstants.ZERO_VAL) {
            responseCode = ServiceConstants.FOURB;
            map.put(ServiceConstants.FOUR_QUESTION_LOOKUP_ID, responseCode);
        }
        if (priorNeglectCount == ServiceConstants.ZERO_VAL) {
            responseCode = ServiceConstants.FOURAA;
            map.put(ServiceConstants.FIVE_QUESTION_LOOKUP_ID, responseCode);

        }
        if (priorNeglectCount == ServiceConstants.ONE_VAL) {
            responseCode = ServiceConstants.FOURAB;
            map.put(ServiceConstants.FIVE_QUESTION_LOOKUP_ID, responseCode);
            stageMap.put(responseCode, getPriorNeglectList(stageId));
        }
        if (priorNeglectCount == ServiceConstants.TWO_VAL) {
            responseCode = ServiceConstants.FOURAC;
            map.put(ServiceConstants.FIVE_QUESTION_LOOKUP_ID, responseCode);
            stageMap.put(responseCode, getPriorNeglectList(stageId));
        }
        if (priorNeglectCount >= ServiceConstants.THREE_VAL) {
            responseCode = ServiceConstants.FOURAD;
            map.put(ServiceConstants.FIVE_QUESTION_LOOKUP_ID, responseCode);
            stageMap.put(responseCode, getPriorNeglectList(stageId));
        }
        if (priorAbuse == ServiceConstants.ZERO_VAL) {
            responseCode = ServiceConstants.FOURBA;
            map.put(ServiceConstants.SIX_QUESTION_LOOKUP_ID, responseCode);
            stageMap.put(responseCode, getPriorAbuseList(stageId));
        }
        if (priorAbuse == ServiceConstants.ONE_VAL) {
            responseCode = ServiceConstants.FOURBB;
            map.put(ServiceConstants.SIX_QUESTION_LOOKUP_ID, responseCode);
            stageMap.put(responseCode, getPriorAbuseList(stageId));
        }
        if (priorAbuse >= ServiceConstants.TWO_VAL) {
            responseCode = ServiceConstants.FOURBC;
            map.put(ServiceConstants.SIX_QUESTION_LOOKUP_ID, responseCode);
            stageMap.put(responseCode, getPriorAbuseList(stageId));
        }

        return map;
    }

    /**
     * Method Name: getAbuse Method Description:To get prior abuse count
     *
     * @param stageId
     * @return Long
     * @throws DataNotFoundException
     */
    public Long getAbuse(Long stageId) throws DataNotFoundException {
        return getRecordExistsUsingStageID(getAbuseSql, stageId);
    }

    /**
     * Method Name: getRecordExistsUsingStageID Method Description:Method to get
     * Record count for the SQL query executed in a Stage
     *
     * @param sql
     * @param stageId
     * @return Long
     */
    public Long getRecordExistsUsingStageID(String sql, Long stageId) {
        SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(sql);
        query.setParameter("idStage", stageId);
        BigDecimal countB = (BigDecimal) query.uniqueResult();
        return countB.longValue();
    }

    /**
     * Method Name: getNeglect Method Description:to get prior neglect count
     *
     * @param stageId
     * @return Long
     */
    public Long getNeglect(Long stageId) throws DataNotFoundException {
        return getRecordExistsUsingStageID(getNeglectSql, stageId);
    }

    /**
     * Method Name: getNumChildrenInAllegation Method Description:To get the
     * number of children involved in the allegation
     *
     * @param stageId
     * @return Long
     */
    public Long getNumChildrenInAllegation(Long stageId) throws DataNotFoundException {
        return getRecordCountUsingStageID(getNumChildrenInAllegationSql, stageId);
    }

    /**
     * Method Name: getRecordCountUsingStageID Method Description:Method to get
     * Record count for the SQL query executed in a Stage
     *
     * @param sql
     * @param stageId
     * @return Long
     */
    public Long getRecordCountUsingStageID(String sql, Long stageId) {
        SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(sql);
        query.setParameter("idStage", stageId);
        BigDecimal countB = (BigDecimal) query.uniqueResult();
        return countB.longValue();
    }

    /**
     * Method Name: getNumOfYoungChildren Method Description:Returns the number
     * of young children
     *
     * @param stageId
     * @return Long
     */
    public Long getNumOfYoungChildren(Long stageId) throws DataNotFoundException {
        Long intStage = getIntakeStageId(stageId).getIdStage();
        SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(getNumOfYoungChildren);
        query.setParameter("idStageOut", stageId).setParameter("idStageIn", intStage);
        BigDecimal countB = (BigDecimal) query.uniqueResult();
        return countB.longValue();
    }

    /**
     * Method Name: getIntakeStageId Method Description:To get intake stage Id
     *
     * @param stageId
     * @return StageDto
     */
    public StageDto getIntakeStageId(Long stageId) throws DataNotFoundException {
        StageDto stage = getPriorStage(stageId);
        if (ServiceConstants.INTAKE.equalsIgnoreCase(stage.getCdStage())) {
            return stage;
        } else {
            return getPriorStage(stage.getIdStage());
        }
    }

    /**
     * Method Name: getPriorStage Method Description:Returns details for the
     * stage prior to the given stage as indicated by the STAGE_LINK table.
     *
     * @param stageId
     * @return StageDto
     */
    private StageDto getPriorStage(Long stageId) {
        SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(getPriorStageSql);
        query.setLong("idStage", stageId);
        query.addScalar("idStage", StandardBasicTypes.LONG).addScalar("idCase", StandardBasicTypes.LONG)
                .addScalar("idSituation", StandardBasicTypes.LONG).addScalar("nmStage", StandardBasicTypes.STRING)
                .addScalar("cdStage", StandardBasicTypes.STRING).addScalar("cdStageType", StandardBasicTypes.STRING)
                .addScalar("cdStageProgram", StandardBasicTypes.STRING)
                .addScalar("cdStageClassification", StandardBasicTypes.STRING)
                .addScalar("dtStageStart", StandardBasicTypes.DATE)
                .addScalar("indStageClose", StandardBasicTypes.STRING)
                .addScalar("cdStageReasonClosed", StandardBasicTypes.STRING)
                .addScalar("idUnit", StandardBasicTypes.LONG).addScalar("nmCase", StandardBasicTypes.STRING)
                .addScalar("dtStageClose", StandardBasicTypes.DATE);
        List<StageDto> stageDtos = (List<StageDto>) query.setResultTransformer(Transformers.aliasToBean(StageDto.class))
                .list();
        StageDto stageDto = new StageDto();
        if (!stageDtos.isEmpty()) {
            stageDto = stageDtos.get(0);
        }
        return stageDto;
    }

    /**
     * Method Name: getNumChildWithPriorInjury Method Description:number of
     * children with prior child abuse/neglect
     *
     * @param stageId
     * @return Long
     */
    public Long getNumChildWithPriorInjury(Long stageId) throws DataNotFoundException {
        return getRecordExistsUsingStageID(getNumChildWithPriorInjurySql, stageId);
    }

    /**
     * Method Name: addPrepulatedAnswers Method Description:This method is used
     * to set the prpopulated answers based on the history.
     *
     * @param sdmAnswerDBDto
     * @param prePopAnswersMap
     */
    private void addPrepulatedAnswers(SDMRiskAssessmentAnswerDto sdmAnswerDBDto, Map prePopAnswersMap) {
        String answerCode = null;
        Set set = prePopAnswersMap.entrySet();
        Iterator i = set.iterator();

        while (i.hasNext()) {
            Map.Entry me = (Map.Entry) i.next();

            answerCode = (String) me.getValue();
            if (!TypeConvUtil.isNullOrEmpty(sdmAnswerDBDto.getAnswerCode())) {
                if (sdmAnswerDBDto.getAnswerCode().equalsIgnoreCase(answerCode)
                        && sdmAnswerDBDto.getResponseCode() == null) {

                    sdmAnswerDBDto.setResponseCode(answerCode);
                }
            }
        }
    }

    /**
     * Method Name: addStageIdList Method Description:This method is used to add
     * list of stageId to each answer in order to show the hyperlink
     *
     * @param sdmAnswerDBDto
     * @param stageIdListMap
     */
    private void addStageIdList(SDMRiskAssessmentAnswerDto sdmAnswerDBDto, Map stageIdListMap) {
        String answerCode = null;
        Set set = stageIdListMap.entrySet();
        Iterator i = set.iterator();
        while (i.hasNext()) {

            Map.Entry me = (Map.Entry) i.next();
            answerCode = (String) me.getKey();
            List stageIdList = (List) me.getValue();
            if (sdmAnswerDBDto.getAnswerCode().equalsIgnoreCase(answerCode)) {
                sdmAnswerDBDto.setStageIdList(stageIdList);
            }
        }

    }

    /**
     * Method Name: setValuesQuestionDB Method Description:This method is used
     * to set values for the questionDB using query results
     *
     * @param cpsResponseDto
     * @param riskQuestionDto
     */
    private void setValuesQuestionDB(CpsRaResponseDto cpsResponseDto, SDMRiskAssessmentQuestionDto riskQuestionDto) {
        riskQuestionDto.setIdQuestion(cpsResponseDto.getIdCpsRaQstnLookup().intValue());
        riskQuestionDto.setQuestionCode(cpsResponseDto.getCdRaQuestion());
        riskQuestionDto.setQuestionText(cpsResponseDto.getTxtQuestion());
        riskQuestionDto.setNameDefinition(cpsResponseDto.getNmDefinition());
    }

    /**
     * Method Name:getPrimaryCreGivrHistoryCount Method Description:This method
     * is used to get Primary CareGiver History Count
     *
     * @param idStage
     * @param idPrimaryCaregiver
     * @return List<StagePersonValueDto>
     */
    @Override
    public List<StagePersonValueDto> getPrimaryCreGivrHistoryCount(Long idPrimaryCaregiver, Long idStage) {
        @SuppressWarnings("unchecked")
        List<StagePersonValueDto> stagePersValueDto = ((SQLQuery) sessionFactory.getCurrentSession()
                .createSQLQuery(getPrimaryCreGivrHistory).setParameter("idPrimaryWorker", idPrimaryCaregiver)
                .setParameter("idStage", idStage)).addScalar("idAllegationStage", StandardBasicTypes.LONG)
                .addScalar("cdStage", StandardBasicTypes.STRING)
                .setResultTransformer(Transformers.aliasToBean(StagePersonValueDto.class)).list();
        return stagePersValueDto;
    }

    /**
     * Method Name:getSecondaryCreGivrHistoryCount Method Description:This
     * method is used to get Secondary CareGiver History Count
     *
     * @param idSecondaryCaregiver
     * @param idStage
     * @return List<StagePersonValueDto>
     */
    @Override
    public List<StagePersonValueDto> getSecondaryCreGivrHistoryCount(Long idSecondaryCaregiver, Long idStage) {
        @SuppressWarnings("unchecked")
        List<StagePersonValueDto> stagePersValueDto = ((SQLQuery) sessionFactory.getCurrentSession()
                .createSQLQuery(getPrimaryCreGivrHistory).setParameter("idPrimaryWorker", idSecondaryCaregiver)
                .setParameter("idStage", idStage)).addScalar("idAllegationStage", StandardBasicTypes.LONG)
                .addScalar("cdStage", StandardBasicTypes.STRING)
                .setResultTransformer(Transformers.aliasToBean(StagePersonValueDto.class)).list();
        return stagePersValueDto;
    }

    /**
     * Method Name:retrieveSafetyAssmtData Method Description:This method is
     * used to set values for the secfollowupDB using query results
     *
     * @param safetyAssessmentReq
     * @return SafetyAssessmentRes
     */
    @Override
    public SafetyAssessmentRes retrieveSafetyAssmtData(SafetyAssessmentReq safetyAssessmentReq) {
        List<SafetyAssessmentDto> safetyAssessmentDto = (List<SafetyAssessmentDto>) sessionFactory.getCurrentSession()
                .createSQLQuery(getRetrieveSafetyAssmtData).addScalar("cdSafetyDecision", StandardBasicTypes.STRING)
                .addScalar("indAbuseNeglHistCompl", StandardBasicTypes.BOOLEAN)
                .addScalar("abuseNeglHistNotCompl", StandardBasicTypes.STRING)
                .addScalar("indInsufficientSafety", StandardBasicTypes.BOOLEAN)
                .addScalar("decisionRationale", StandardBasicTypes.STRING)
                .addScalar("indChildPresentDanger", StandardBasicTypes.BOOLEAN)
                .addScalar("dtLastUpdate", StandardBasicTypes.DATE)
                .addScalar("cdSafetyFactor", StandardBasicTypes.STRING)
                .addScalar("dtFactorsLastUpdate", StandardBasicTypes.DATE)
                .addScalar("cdSafetyArea", StandardBasicTypes.STRING)
                .addScalar("discussyFactors", StandardBasicTypes.STRING)
                .addScalar("dtLastUpdateSafetyArea", StandardBasicTypes.DATE)
                .addScalar("cdSafetyFactorCategory", StandardBasicTypes.STRING)
                .addScalar("cdSafetyFactorResponse", StandardBasicTypes.STRING)
                .addScalar("area", StandardBasicTypes.STRING).addScalar("txtArea", StandardBasicTypes.STRING)
                .addScalar("category", StandardBasicTypes.STRING).addScalar("txtCategory", StandardBasicTypes.STRING)
                .addScalar("factor", StandardBasicTypes.STRING).addScalar("txtFactor", StandardBasicTypes.STRING)
                .addScalar("idSafetyArea", StandardBasicTypes.LONG).addScalar("idSafetyFactor", StandardBasicTypes.LONG)
                .addScalar("idSafetyFactorArea", StandardBasicTypes.LONG)
                .setParameter("idCase", safetyAssessmentReq.getIdCase())
                .setParameter("idStage", safetyAssessmentReq.getIdStage())
                .setParameter("idEvent", safetyAssessmentReq.getIdEvent())
                .setResultTransformer(Transformers.aliasToBean(SafetyAssessmentDto.class)).list();
        SafetyAssessmentRes safetyAssessmentRes = new SafetyAssessmentRes();
        List<SafetyFactorDto> factorsList = new ArrayList<SafetyFactorDto>();
        for (SafetyAssessmentDto safetyFactorValueBean : safetyAssessmentDto) {
            SafetyFactorDto dto = new SafetyFactorDto();
            dto.setCdSafetyArea(safetyFactorValueBean.getCdSafetyArea());
            dto.setCdSafetyCategory(safetyFactorValueBean.getCategory());
            dto.setCdSafetyFactor(safetyFactorValueBean.getCdSafetyFactor());
            dto.setCdSafetyFactorCategory(safetyFactorValueBean.getCdSafetyFactorCategory());
            dto.setCdSafetyFactorResponse(safetyFactorValueBean.getCdSafetyFactorResponse());
            dto.setDtLastUpdateArea(safetyFactorValueBean.getDtLastUpdateSafetyArea());
            dto.setDtLastUpdateFactor(safetyFactorValueBean.getDtFactorsLastUpdate());
            dto.setIdCase(safetyFactorValueBean.getIdCase());
            dto.setIdEvent(safetyFactorValueBean.getIdEvent());
            dto.setIdPerson(safetyFactorValueBean.getIdPerson());
            dto.setIdSafetyArea(safetyFactorValueBean.getIdSafetyArea());
            dto.setIdSafetyFactor(safetyFactorValueBean.getIdSafetyFactor());
            dto.setIdSafetyFactorArea(safetyFactorValueBean.getIdSafetyFactorArea());
            dto.setIdStage(safetyFactorValueBean.getIdStage());
            dto.setArea(safetyFactorValueBean.getTxtArea());
            dto.setCategory(safetyFactorValueBean.getTxtCategory());
            dto.setDiscussyFactors(safetyFactorValueBean.getDiscussyFactors());
            dto.setFactors(safetyFactorValueBean.getTxtFactor());
            factorsList.add(dto);
        }
        SafetyAssessmentDto safetyDto = new SafetyAssessmentDto();
        safetyDto.setFactors(factorsList);
        safetyDto.setCdSafetyDecision(safetyAssessmentDto.get(0).getCdSafetyDecision());
        safetyDto.setDecisionRationale(safetyAssessmentDto.get(0).getDecisionRationale());
        safetyDto.setDtLastUpdate(safetyAssessmentDto.get(0).getDtLastUpdate());
        safetyDto.setIdCase(safetyAssessmentDto.get(0).getIdCase());
        safetyDto.setIdEvent(safetyAssessmentDto.get(0).getIdEvent());
        safetyDto.setIdPerson(safetyAssessmentDto.get(0).getIdPerson());
        safetyDto.setIdTodo(safetyAssessmentDto.get(0).getIdTodo());
        safetyDto.setIndAbuseNeglHistCompl(safetyAssessmentDto.get(0).getIndAbuseNeglHistCompl());
        safetyDto.setIndChildPresentDanger(safetyAssessmentDto.get(0).getIndChildPresentDanger());
        safetyDto.setIndInsufficientSafety(safetyAssessmentDto.get(0).getIndInsufficientSafety());
        safetyAssessmentRes.setSafetyAssessmentDto(safetyDto);
        return safetyAssessmentRes;
    }

    /**
     * Method Name:getsubStageOpen Method Description:Returns if a Sub Stage is
     * open, with a given StageId
     *
     * @param idCase
     * @return boolean
     */
    @Override
    public boolean getsubStageOpen(Long idCase) {
        SafetyAssessmentRes safetyAssessmentRes = new SafetyAssessmentRes();
        safetyAssessmentRes = (SafetyAssessmentRes) sessionFactory.getCurrentSession().createSQLQuery(getSubStageOpen)
                .addScalar("cdStage", StandardBasicTypes.STRING).setParameter("idCase", idCase)
                .setResultTransformer(Transformers.aliasToBean(SafetyAssessmentRes.class)).uniqueResult();

        boolean subStageOpen = false;
        if (!TypeConvUtil.isNullOrEmpty(safetyAssessmentRes.getCdStage())) {
            subStageOpen = true;
        } else {
            subStageOpen = false;
        }

        return subStageOpen;
    }

    @Override
    public String getCurrentEventStatus(Long idStage, Long idCase) {
        SafetyAssessmentRes safetyResponse = new SafetyAssessmentRes();
        safetyResponse = (SafetyAssessmentRes) sessionFactory.getCurrentSession().createSQLQuery(getCurrentEventStatus)
                .addScalar("eventStatus", StandardBasicTypes.STRING).setParameter("idStage", idStage)
                .setParameter("idCase", idCase)
                .setResultTransformer(Transformers.aliasToBean(SafetyAssessmentRes.class)).uniqueResult();
        return safetyResponse.getEventStatus();
    }

    @Override
    public SDMRiskAssessmentDto queryPageData(Long idStage) {
        SDMRiskAssessmentDto riskDto = null;
        List<SDMRiskAssessmentQuestionDto> riskQuestionDtoList = new ArrayList<SDMRiskAssessmentQuestionDto>();
        SDMRiskAssessmentQuestionDto riskQuestionDto = null;
        SDMRiskAssessmentAnswerDto riskAnswerDto = null;
        SDMRiskAssessmentFollowupDto riskFollowUpDto = null;
        SDMRiskAssmtSecondaryFollowupDto riskSecondaryFollowUpDto = null;
        long questionId = 0l;
        long previousQuestionId = 0l;
        long answerId = 0l;
        long previousAnswerId = 0l;
        long followUpId = 0l;
        long previousFollowUpId = 0l;
        long secondaryFollowUpId = 0l;
        Map prePopAnswersMap = new HashMap();
        Map stageIdListMap = new HashMap();

        prePopAnswersMap = getPrePopulationAnswers(idStage);
        stageIdListMap = getStageIdList(idStage, 0L, 0L);

        SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(queryPageDataSql);
        query.setParameter("idStage", idStage);
        query.addScalar("idCpsRa", StandardBasicTypes.LONG).addScalar("formVersionId", StandardBasicTypes.LONG)
                .addScalar("nbrVersion", StandardBasicTypes.LONG).addScalar("idCase", StandardBasicTypes.LONG)
                .addScalar("idStage", StandardBasicTypes.LONG).addScalar("nbrFnLowMin", StandardBasicTypes.LONG)
                .addScalar("nbrFnLowMax", StandardBasicTypes.LONG).addScalar("nbrFnModMin", StandardBasicTypes.LONG)
                .addScalar("nbrFnModMax", StandardBasicTypes.LONG).addScalar("nbrFnHighMin", StandardBasicTypes.LONG)
                .addScalar("nbrFnHighMax", StandardBasicTypes.LONG).addScalar("nbrFaLowMin", StandardBasicTypes.LONG)
                .addScalar("nbrFaLowMax", StandardBasicTypes.LONG).addScalar("nbrFaModMin", StandardBasicTypes.LONG)
                .addScalar("nbrFaModMax", StandardBasicTypes.LONG).addScalar("nbrFaHighMin", StandardBasicTypes.LONG)
                .addScalar("nbrFaHighMax", StandardBasicTypes.LONG);

        List<RiskAssmtDtlDto> sdmRiskAssessmentDtoList = query
                .setResultTransformer(Transformers.aliasToBean(RiskAssmtDtlDto.class)).list();
        RiskAssmtDtlDto sdmRiskAssessmentDto = new RiskAssmtDtlDto();
        if (!TypeConvUtil.isNullOrEmpty(sdmRiskAssessmentDtoList)) {
            sdmRiskAssessmentDto = sdmRiskAssessmentDtoList.get(0);
        }

        // This query that pulls back all questions, answers, follow-ups and
        // secondary follow-ups.
        query = sessionFactory.getCurrentSession().createSQLQuery(sdmRiskAssessmentQuery);
        query.setParameter("idCpsRa", sdmRiskAssessmentDto.getFormVersionId());
        query.addScalar("idCpsRaAssmtLookup", StandardBasicTypes.LONG)
                .addScalar("idCpsRaQstnLookup", StandardBasicTypes.LONG)
                .addScalar("cdRaQuestion", StandardBasicTypes.STRING)
                .addScalar("txtQuestion", StandardBasicTypes.STRING)
                .addScalar("nmDefinition", StandardBasicTypes.STRING)
                .addScalar("idCpsRaAnswerLookup", StandardBasicTypes.LONG)
                .addScalar("cdRaAnswer", StandardBasicTypes.STRING)
                .addScalar("cralNbrFnIndexValue", StandardBasicTypes.LONG)
                .addScalar("cralNbrFaIndexValue", StandardBasicTypes.LONG)
                .addScalar("txtAnswer", StandardBasicTypes.STRING)
                .addScalar("idCpsRaFollowupLookup", StandardBasicTypes.LONG)
                .addScalar("txtFollowupQstn", StandardBasicTypes.STRING)
                .addScalar("crflNbrFnIndexValue", StandardBasicTypes.LONG)
                .addScalar("crflNbrFaIndexValue", StandardBasicTypes.LONG)
                .addScalar("cdFollowup", StandardBasicTypes.STRING)
                .addScalar("idCpsRaSecondFollowupLkp", StandardBasicTypes.LONG)
                .addScalar("txtSecondaryFollowupQstn", StandardBasicTypes.STRING)
                .addScalar("cdSecFollowup", StandardBasicTypes.STRING);
        List<CpsRaResponseDto> cpsRaResponseDtoList = query
                .setResultTransformer(Transformers.aliasToBean(CpsRaResponseDto.class)).list();

        for (CpsRaResponseDto cpsResponse : cpsRaResponseDtoList) {

            if (!TypeConvUtil.isNullOrEmpty(cpsResponse.getIdCpsRaQstnLookup())) {
                questionId = cpsResponse.getIdCpsRaQstnLookup();
            }
            if (!TypeConvUtil.isNullOrEmpty(cpsResponse.getIdCpsRaAnswerLookup())) {
                answerId = cpsResponse.getIdCpsRaAnswerLookup();
            }
            if (!TypeConvUtil.isNullOrEmpty(cpsResponse.getIdCpsRaFollowupLookup())) {
                followUpId = cpsResponse.getIdCpsRaFollowupLookup();
            }
            if (!TypeConvUtil.isNullOrEmpty(cpsResponse.getIdCpsRaSecondFollowupLkp())) {
                secondaryFollowUpId = cpsResponse.getIdCpsRaSecondFollowupLkp();
            }

            /*
             * If the question code changed, then create the question, answer,
             * follow-up (if necessary) and secondary follow-up (if necessary)
             * objects
             */
            if (questionId != previousQuestionId) {
                riskQuestionDto = new SDMRiskAssessmentQuestionDto();
                riskAnswerDto = new SDMRiskAssessmentAnswerDto();
                setValuesQuestionDB(cpsResponse, riskQuestionDto);
                setValuesAnswerDto(cpsResponse, riskAnswerDto, false);
                addPrepulatedAnswers(riskAnswerDto, prePopAnswersMap);
                addStageIdList(riskAnswerDto, stageIdListMap);

                riskQuestionDto.getAnswers().add(riskAnswerDto);
                riskQuestionDtoList.add(riskQuestionDto);
                if (!TypeConvUtil.isNullOrEmpty(cpsResponse.getIdCpsRaFollowupLookup())) {
                    followUpId = cpsResponse.getIdCpsRaFollowupLookup();
                }
                // if there is a follow-up the followUpId won't be 0
                if (followUpId != 0) {
                    riskFollowUpDto = new SDMRiskAssessmentFollowupDto();

                    riskFollowUpDto.setFollowupId((int) followUpId);
                    setValuesFollowupDto(cpsResponse, riskFollowUpDto, false);
                    if (!TypeConvUtil.isNullOrEmpty(riskFollowUpDto.getFollowupCode())) {
                        riskAnswerDto.getFollowupQuestions().add(riskFollowUpDto);

                        // if there is a secondary follow-up the secFollowUpId
                        // won't be 0
                        if (secondaryFollowUpId != 0) {
                            SDMRiskAssmtSecondaryFollowupDto riskSecondaryFollowUp = new SDMRiskAssmtSecondaryFollowupDto();
                            setValuesSecFollowupDto(cpsResponse, riskSecondaryFollowUp, false);
                            if (!TypeConvUtil.isNullOrEmpty(riskSecondaryFollowUp.getFollowupCode())) {
                                riskFollowUpDto.getSecondaryFollowupQuestions().add(riskSecondaryFollowUp);
                            }
                        }
                    }
                }
            }
            /*
             * If the answer code changed, then create the answer, follow-up (if
             * necessary) and secondary follow-up (if necessary) objects
             */
            else if (previousAnswerId != answerId) {
                riskAnswerDto = new SDMRiskAssessmentAnswerDto();
                setValuesAnswerDto(cpsResponse, riskAnswerDto, false);
                addPrepulatedAnswers(riskAnswerDto, prePopAnswersMap);
                addStageIdList(riskAnswerDto, stageIdListMap);

                // if there is a follow-up the followUpId won't be 0
                if (followUpId != 0) {
                    riskFollowUpDto = new SDMRiskAssessmentFollowupDto();
                    riskFollowUpDto.setFollowupId((int) followUpId);
                    setValuesFollowupDto(cpsResponse, riskFollowUpDto, false);
                    if (!TypeConvUtil.isNullOrEmpty(riskFollowUpDto.getFollowupCode())) {
                        riskAnswerDto.getFollowupQuestions().add(riskFollowUpDto);

                        // if there is a secondary follow-up the secFollowUpId
                        // won't be 0
                        if (secondaryFollowUpId != 0) {
                            SDMRiskAssmtSecondaryFollowupDto riskSecondaryFollowUp = new SDMRiskAssmtSecondaryFollowupDto();
                            setValuesSecFollowupDto(cpsResponse, riskSecondaryFollowUp, false);
                            if (!TypeConvUtil.isNullOrEmpty(riskSecondaryFollowUp.getFollowupCode())) {
                                riskFollowUpDto.getSecondaryFollowupQuestions().add(riskSecondaryFollowUp);
                            }
                        }
                    }
                }
                riskQuestionDto.getAnswers().add(riskAnswerDto);
            }
            /*
             * If the follow-up code changed, then create the follow-up and
             * secondary follow-up (if necessary) objects
             */
            else if (previousFollowUpId != followUpId) {

                // if there is a follow-up the followUpId won't be 0
                if (followUpId != 0) {
                    riskFollowUpDto = new SDMRiskAssessmentFollowupDto();
                    riskFollowUpDto.setFollowupId((int) followUpId);
                    setValuesFollowupDto(cpsResponse, riskFollowUpDto, false);
                    if (!TypeConvUtil.isNullOrEmpty(riskFollowUpDto.getFollowupCode())) {
                        riskAnswerDto.getFollowupQuestions().add(riskFollowUpDto);
                        if (secondaryFollowUpId != 0) {
                            riskSecondaryFollowUpDto = new SDMRiskAssmtSecondaryFollowupDto();
                            setValuesSecFollowupDto(cpsResponse, riskSecondaryFollowUpDto, false);
                            if (!TypeConvUtil.isNullOrEmpty(riskSecondaryFollowUpDto.getFollowupCode())) {
                                riskFollowUpDto.getSecondaryFollowupQuestions().add(riskSecondaryFollowUpDto);
                            }
                        }
                    }
                }
            }
            /*
             * Create the secondary follow-up objects
             */
            else {
                SDMRiskAssmtSecondaryFollowupDto riskSecondaryFollowUp = new SDMRiskAssmtSecondaryFollowupDto();
                setValuesSecFollowupDto(cpsResponse, riskSecondaryFollowUp, false);
                riskFollowUpDto.getSecondaryFollowupQuestions().add(riskSecondaryFollowUp);
            }
            previousAnswerId = answerId;
            previousFollowUpId = followUpId;
            previousQuestionId = questionId;
        }
        riskDto = new SDMRiskAssessmentDto(sdmRiskAssessmentDto, riskQuestionDtoList, false);

        return riskDto;
    }
    // This method is used to insert cpsRaAssmtLookup for the CPS_RA table

    @Override
    public long updateRiskAsmnt(SDMRiskAssessmentDto sDMRiskAssessmentdto) {
        CpsRa cpsRa = (CpsRa) sessionFactory.getCurrentSession().load(CpsRa.class, (long) sDMRiskAssessmentdto.getId());
        Event event = cpsRa.getEvent();
        event.setIdEvent((long) sDMRiskAssessmentdto.getIdEvent());
        event.setCdEventStatus(sDMRiskAssessmentdto.getEventStatus());
        event.setDtLastUpdate(sDMRiskAssessmentdto.getDateLastUpdate());
        Stage stage = cpsRa.getStage();
        stage.setIdStage(sDMRiskAssessmentdto.getIdStage());
        stage.setDtLastUpdate(sDMRiskAssessmentdto.getDateLastUpdate());
        cpsRa.setEvent(event);
        cpsRa.setNbrFaScore(sDMRiskAssessmentdto.getFutureAbuseScore());
        cpsRa.setNbrFnScore(sDMRiskAssessmentdto.getFutureNeglectScore());
        cpsRa.setCdFnRiskLevel(sDMRiskAssessmentdto.getFutureNeglectRiskLevelCode());
        cpsRa.setCdFaRiskLevel(sDMRiskAssessmentdto.getFutureAbuseRiskLevelCode());
        cpsRa.setCdScoredRiskLevel(sDMRiskAssessmentdto.getScoredRiskLevelCode());
        cpsRa.setCdOverride(sDMRiskAssessmentdto.getOverrideCode());
        cpsRa.setTxtDiscOverrideReason(sDMRiskAssessmentdto.getOverrideReason());
        cpsRa.setCdFinalRiskLevel(sDMRiskAssessmentdto.getFinalRiskLevelCode());
        if (!ObjectUtils.isEmpty(sDMRiskAssessmentdto.getIndPOChildDeath())
                && sDMRiskAssessmentdto.getIndPOChildDeath().equals(true)) {
            cpsRa.setIndPoChildDeath(ServiceConstants.Y);
        }
        if ((!ObjectUtils.isEmpty(sDMRiskAssessmentdto.getIndPOChildLessThanSixteenInjured())
                && sDMRiskAssessmentdto.getIndPOChildLessThanSixteenInjured().equals(true))) {
            cpsRa.setIndPoInjuryChild16(ServiceConstants.Y);
        }
        if ((!ObjectUtils.isEmpty(sDMRiskAssessmentdto.getIndPOChildLessThanThreeInjured())
                && sDMRiskAssessmentdto.getIndPOChildLessThanThreeInjured().equals(true))) {
            cpsRa.setIndPoInjuryChild3(ServiceConstants.Y);
        }
        if ((!ObjectUtils.isEmpty(sDMRiskAssessmentdto.getIndPOChildSexualAbuse())
                && sDMRiskAssessmentdto.getIndPOChildSexualAbuse().equals(true))) {
            cpsRa.setIndPoSxab(ServiceConstants.Y);
        }

        //[artf171226] ALM-16544: Risk Assessment Created By is getting updated by 'Updated By'
        if(StringUtils.isEmpty(sDMRiskAssessmentdto.getCreatedBy())) {
            cpsRa.setIdCreatedPerson(sDMRiskAssessmentdto.getLoggedInUser());
        }
        cpsRa.setDtLastUpdate(new Date());
        cpsRa.setIdLastUpdatePerson(sDMRiskAssessmentdto.getLoggedInUser());
        cpsRa.setDtAssmtCompleted(sDMRiskAssessmentdto.getDateAssessmentCompleted());
        Set<CpsRaResponse> cpsRaResponses = cpsRa.getCpsRaResponses();
        sDMRiskAssessmentdto.getQuestions().forEach(q -> {
            q.getAnswers().forEach(a -> {
                if (!ObjectUtils.isEmpty(q.getAnswers())) {
                    CpsRaResponse cpsRaResponse = cpsRaResponses.stream()
                            .filter(resp -> Long.valueOf(a.getResponseId()).equals(resp.getIdCpsRaResponse()))
                            .findFirst().get();
                    // for(CpsRaResponse cpsRaResponse: cpsRaResponses){
                    cpsRaResponse.setCdRaAnswer(a.getResponseCode());
                    CpsRaAnswerLookup cpsRaAnswerLookup = cpsRaResponse.getCpsRaAnswerLookup();
                    cpsRaResponse.setCpsRaAnswerLookup(cpsRaAnswerLookup);

                    cpsRaResponse.setDtLastUpdate(new Date());
                    cpsRaResponse.setIdCreatedPerson(sDMRiskAssessmentdto.getLoggedInUser());
                    cpsRaResponse.setIdLastUpdatePerson(sDMRiskAssessmentdto.getLoggedInUser());
                    cpsRaResponse.setCpsRa(cpsRa);
                    Set<CpsRaFollowupResponse> cpsRaFollowupResponseList = cpsRaResponse.getCpsRaFollowupResponses();
                    if (!ObjectUtils.isEmpty(a.getFollowupQuestions())) {
                        a.getFollowupQuestions().forEach(f -> {
                            CpsRaFollowupResponse cpsRaFollowupResponse = cpsRaFollowupResponseList.stream()
                                    .filter(resp -> Long.valueOf(f.getFollowupResponseId())
                                            .equals(resp.getIdCpsRaFollowupResponse()))
                                    .findFirst().get();

                            cpsRaFollowupResponse.setDtLastUpdate(new Date());
                            cpsRaFollowupResponse.setIdCreatedPerson(sDMRiskAssessmentdto.getLoggedInUser());
                            cpsRaFollowupResponse.setIdLastUpdatePerson(sDMRiskAssessmentdto.getLoggedInUser());
                            cpsRaFollowupResponse.setIndCpsRaFollowup(f.getIndRaFollowup());
                            // cpsRaFollowupResponse.setCpsRaResponse(cpsRaResponse);
                            Set<CpsRaSecondFollowupResp> cpsRaSecondFollowupResps = cpsRaFollowupResponse
                                    .getCpsRaSecondFollowupResps();
                            if (!ObjectUtils.isEmpty(f.getSecondaryFollowupQuestions())) {
                                f.getSecondaryFollowupQuestions().forEach(s -> {
                                    CpsRaSecondFollowupResp cpsRaSecondFollowupResp = cpsRaSecondFollowupResps.stream()
                                            .filter(resp -> Long.valueOf(s.getSecondFollowupResponseId())
                                                    .equals(resp.getIdCpsRaSecondFollowupResp()))
                                            .findFirst().get();
                                    cpsRaSecondFollowupResp.setDtLastUpdate(new Date());
                                    cpsRaSecondFollowupResp.setIdCreatedPerson(sDMRiskAssessmentdto.getLoggedInUser());
                                    cpsRaSecondFollowupResp
                                            .setIdLastUpdatePerson(sDMRiskAssessmentdto.getLoggedInUser());
                                    cpsRaSecondFollowupResp.setIndCpsRaSecondaryFollowup(s.getIndSecFollowupLookup());
                                    cpsRaSecondFollowupResps.add(cpsRaSecondFollowupResp);
                                    cpsRaSecondFollowupResp.setCpsRaFollowupResponse(cpsRaFollowupResponse);
                                    // cpsRaFollowupResponse.setCpsRaSecondFollowupResps(cpsRaSecondFollowupResps);

                                });
                            }
                            cpsRaFollowupResponseList.add(cpsRaFollowupResponse);
                            cpsRaResponse.setCpsRaFollowupResponses(cpsRaFollowupResponseList);

                        });

                    }
                    // cpsRaResponses.add(cpsRaResponse);
                    // }
                }
            });

        });
        // cpsRa.setCpsRaResponses(cpsRaResponses);

        sessionFactory.getCurrentSession().update(cpsRa);
        return cpsRa.getEvent().getIdEvent();

    }

    /*Defect # 9159 Code changes for the right Risk Assessment to be pulled */
    //ALM ID : 14365 : modified query to look only for risk assessment associated to prior stage 
    //         that led to FPR stage and handling null pointer exception when there is no Risk Assessment.
    @Override
    public Long getStageHouseholdSDMRAEvent(long idStage) {
        SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(sdmRAEventForStageHouseholdQuery);
        query.setParameter("idStage", idStage);
        BigDecimal idEvent = (BigDecimal) query.uniqueResult();
        return idEvent != null ? idEvent.longValue() : null;
    }

}
