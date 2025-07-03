package us.tx.state.dfps.service.riskreasmnt.daoimpl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.CpsFsna;
import us.tx.state.dfps.common.domain.Message;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.domain.ReunfctnAsmnt;
import us.tx.state.dfps.common.domain.RiskReasmnt;
import us.tx.state.dfps.common.domain.RiskReasmntAnsLookup;
import us.tx.state.dfps.common.domain.RiskReasmntFlwupLookup;
import us.tx.state.dfps.common.domain.RiskReasmntFolupRspn;
import us.tx.state.dfps.common.domain.RiskReasmntLookup;
import us.tx.state.dfps.common.domain.RiskReasmntQstnLookup;
import us.tx.state.dfps.common.domain.RiskReasmntRspn;
import us.tx.state.dfps.common.domain.StageLink;
import us.tx.state.dfps.fsna.dto.CpsFsnaDto;
import us.tx.state.dfps.phoneticsearch.IIRHelper.Messages;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.SDMRiskReassessmentReq;
import us.tx.state.dfps.service.common.response.SDMRiskReassessmentRes;
import us.tx.state.dfps.service.exception.DataLayerException;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.reunificationAssessment.dto.ReunificationAssessmentDto;
import us.tx.state.dfps.service.riskreasmnt.SDMRiskReasmntAnswerDto;
import us.tx.state.dfps.service.riskreasmnt.SDMRiskReasmntDto;
import us.tx.state.dfps.service.riskreasmnt.SDMRiskReasmntFollowupDto;
import us.tx.state.dfps.service.riskreasmnt.SDMRiskReasmntQstnDto;
import us.tx.state.dfps.service.riskreasmnt.dao.SDMRiskReassessmentDao;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Jun 14, 2018- 4:01:52 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Repository
public class SDMRiskReassessmentDaoImpl implements SDMRiskReassessmentDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	MessageSource messageSource;

	@Autowired
	PersonDao personDao;

	@Value("${SDMRiskReasmnt.checkRiskReasmntExists}")
	private String checkRiskReasmntExistsSql;

	@Value("${SDMRiskReasmnt.householdCondition}")
	private String householdConditionSql;

	@Value("${SDMRiskReasmnt.primaryCondition}")
	private String primaryConditionSql;

	@Value("${SDMRiskReasmnt.secondaryCondition}")
	private String secondaryConditionSql;

	@Value("${SDMRiskReasmnt.getPreviousReasmntDate}")
	private String getPreviousReasmntDateSql;

	@Value("${PRTActionPlanDaoImpl.delEventPersonLink}")
	private String delEventPersonLink;

	private static final String String_S = "S";

	private static final String String_P = "P";

	private static final String String_H = "H";

	private static final String PARENT_STRING = "Parent";

	private static final String PARENT_STRING_SMALL = "parent";

	private static final String CAREGIVER_STRING = "Caregiver";

	private static final String CAREGIVER_STRING_SMALL = "caregiver";

	private static final String ANSWER_6C = "6C";
	
	private static final String ANSWER_6D = "6D";

	private static final String STRING_D = "D";

	private static final String STRING_C = "C";

	/**
	 * Method Name: getNewReassessmentData Method Description:This method is
	 * used to retrieve the details for a new SDM Risk Reassessment.
	 * 
	 * @param idRiskAsmntLkp
	 *            - The id of the risk reassessment lookup.
	 * @param sdmRiskReasmntDto
	 *            - This dto will hold the input parameters for getting the risk
	 *            reassessment details.
	 * @return SDMRiskReasmntDto - This dto will hold the details of the new
	 *         risk reassessment.
	 */
	@SuppressWarnings({ "unchecked" })
	@Override
	public SDMRiskReasmntDto getNewReassessmentData(Long idRiskAsmntLkp, SDMRiskReasmntDto sdmRiskReasmntDto) {
		List<RiskReasmntQstnLookup> riskReasmntQstnLookupsList = new ArrayList<RiskReasmntQstnLookup>();
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(RiskReasmntQstnLookup.class)
				.add(Restrictions.eq("riskReasmntLookup.idRiskReasmntLookup", idRiskAsmntLkp));
		criteria.addOrder(Order.asc("nbrOrder"));
		riskReasmntQstnLookupsList = criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
		List<SDMRiskReasmntQstnDto> questions = new ArrayList<SDMRiskReasmntQstnDto>();
		if (!CollectionUtils.isEmpty(riskReasmntQstnLookupsList)) {
			for (RiskReasmntQstnLookup riskReasmntQstnLookup : riskReasmntQstnLookupsList) {
				SDMRiskReasmntQstnDto sdmRiskReasmntQstnDto = new SDMRiskReasmntQstnDto();
				sdmRiskReasmntQstnDto
						.setIdRiskReasmntLookUp(riskReasmntQstnLookup.getRiskReasmntLookup().getIdRiskReasmntLookup());
				sdmRiskReasmntQstnDto.setIdQstn(riskReasmntQstnLookup.getIdRiskReasmntQstnLookup());
				sdmRiskReasmntQstnDto.setNbrOrder(riskReasmntQstnLookup.getNbrOrder());
				sdmRiskReasmntQstnDto.setCdQstn(riskReasmntQstnLookup.getCdRiskReasmntQstn());
				sdmRiskReasmntQstnDto.setTxtQstn(riskReasmntQstnLookup.getTxtQstn());

				/*
				 * Check if the question text has Parent word in it , if so
				 * replace it with care-giver for FPR stage.
				 */
				if (CodesConstant.CSTAGES_FPR.equals(sdmRiskReasmntDto.getCdStage())) {
					if (sdmRiskReasmntQstnDto.getTxtQstn().contains(PARENT_STRING)) {
						sdmRiskReasmntQstnDto.setTxtQstn(
								sdmRiskReasmntQstnDto.getTxtQstn().replaceAll(PARENT_STRING, CAREGIVER_STRING));
					} else if (sdmRiskReasmntQstnDto.getTxtQstn().contains(PARENT_STRING_SMALL)) {
						sdmRiskReasmntQstnDto.setTxtQstn(sdmRiskReasmntQstnDto.getTxtQstn()
								.replaceAll(PARENT_STRING_SMALL, CAREGIVER_STRING_SMALL));
					}
				}

				if (!CollectionUtils.isEmpty(riskReasmntQstnLookup.getRiskReasmntAnsLookups())) {
					List<SDMRiskReasmntAnswerDto> answers = new ArrayList<SDMRiskReasmntAnswerDto>();

					riskReasmntQstnLookup.getRiskReasmntAnsLookups().stream()
							.sorted(Comparator.comparing(RiskReasmntAnsLookup::getNbrOrder))
							.forEach(riskReasmntAnsLookup -> {
								SDMRiskReasmntAnswerDto sdmRiskReasmntAnswerDto = new SDMRiskReasmntAnswerDto();
								sdmRiskReasmntAnswerDto.setCdAnswer(riskReasmntAnsLookup.getCdRiskReasmntAnswr());
								sdmRiskReasmntAnswerDto.setTxtAnswer(riskReasmntAnsLookup.getTxtAnswr());
								sdmRiskReasmntAnswerDto
										.setIdAnswerLookup(riskReasmntAnsLookup.getIdRiskReasmntAnsLookup());
								sdmRiskReasmntAnswerDto.setIdQstnLookup(
										riskReasmntAnsLookup.getRiskReasmntQstnLookup().getIdRiskReasmntQstnLookup());
								sdmRiskReasmntAnswerDto.setNbrOrder(riskReasmntAnsLookup.getNbrOrder());
								if (!CollectionUtils.isEmpty(riskReasmntAnsLookup.getRiskReasmntFlwupLookups())) {
									List<SDMRiskReasmntFollowupDto> followUps = new ArrayList<SDMRiskReasmntFollowupDto>();
									List<SDMRiskReasmntFollowupDto> followUpSec = new ArrayList<SDMRiskReasmntFollowupDto>();
									riskReasmntAnsLookup.getRiskReasmntFlwupLookups().stream()
											.sorted(Comparator.comparing(RiskReasmntFlwupLookup::getNbrOrder))
											.forEach(followUp -> {
												SDMRiskReasmntFollowupDto sdmRiskReasmntFollowupDto = new SDMRiskReasmntFollowupDto();
												SDMRiskReasmntFollowupDto sdmRiskReasmntFollowupDto2 = new SDMRiskReasmntFollowupDto();
												sdmRiskReasmntFollowupDto.setCdFollowup(followUp.getCdFlwup());
												sdmRiskReasmntFollowupDto
														.setFollowupQuestionOrder(followUp.getNbrOrder());
												sdmRiskReasmntFollowupDto
														.setTxtFollowupQuestion(followUp.getTxtFollowupQstn());
												sdmRiskReasmntFollowupDto
														.setIdFollowupLookup(followUp.getIdRiskReasmntFlwupLookup());
												sdmRiskReasmntFollowupDto.setIdAnswerLookup(
														followUp.getRiskReasmntAnsLookup().getIdRiskReasmntAnsLookup());
												BeanUtils.copyProperties(sdmRiskReasmntFollowupDto,
														sdmRiskReasmntFollowupDto2);
												followUps.add(sdmRiskReasmntFollowupDto);
												followUpSec.add(sdmRiskReasmntFollowupDto2);
											});
									sdmRiskReasmntAnswerDto.setFollowupQuestions(followUps);
									if (ServiceConstants.SIX_QUESTION_LOOKUP_ID
											.equals(riskReasmntQstnLookup.getIdRiskReasmntQstnLookup())) {
										/*
										 * List<SDMRiskReasmntFollowupDto>
										 * followUpSecondaryList = new
										 * ArrayList<SDMRiskReasmntFollowupDto>(
										 * ); followUpSecondaryList=followUps;
										 */
										sdmRiskReasmntAnswerDto.setFollowupQuestionSec(followUpSec);
									}

								}
								answers.add(sdmRiskReasmntAnswerDto);

							});
					sdmRiskReasmntQstnDto.setAnswers(answers);
				}

				questions.add(sdmRiskReasmntQstnDto);
			}
		}
		sdmRiskReasmntDto.setQuestions(questions);

		return sdmRiskReasmntDto;
	}

	/**
	 * Method Name: saveSDMRiskReassessmentDtls Method Description:This method
	 * is used to save the SDM Risk Reassessment details.
	 * 
	 * @param sdmRiskReassessmentReq
	 *            - This dto will hold the SDM Risk reassessment details to be
	 *            saved.
	 * @return SDMRiskReassessmentRes - This dto will hold the saved SDM Risk
	 *         reassessment details.
	 */
	@Override
	public SDMRiskReassessmentRes saveSDMRiskReassessmentDtls(SDMRiskReassessmentReq sdmRiskReassessmentReq) {
		SDMRiskReassessmentRes response = new SDMRiskReassessmentRes();
		String message = ServiceConstants.FAIL;
		SDMRiskReasmntDto sdmRiskReasmntDto = sdmRiskReassessmentReq.getSdmRiskReasmntDto();

		switch (sdmRiskReassessmentReq.getReqFuncCd()) {
		case ServiceConstants.REQ_FUNC_CD_ADD:
			sdmRiskReasmntDto = addRiskReassessment(sdmRiskReassessmentReq);
			message = ServiceConstants.SUCCESS;
			break;

		case ServiceConstants.REQ_FUNC_CD_UPDATE:

			sdmRiskReasmntDto = updateRiskReassessment(sdmRiskReassessmentReq);
			message = ServiceConstants.SUCCESS;
			break;
		// Delete Risk ReAssessment
		case ServiceConstants.REQ_FUNC_CD_DELETE:
			deleteRiskReassessment(sdmRiskReasmntDto.getIdRiskReasmnt());

			message = ServiceConstants.SUCCESS;
			break;
		}
		response.setSdmRiskReasmntDto(sdmRiskReasmntDto);
		response.setMessage(message);
		return response;

	}

	/**
	 * Method Name: deleteRiskReassessment Method Description:
	 */
	private void deleteRiskReassessment(Long idRiskReasmnt) {
		RiskReasmnt riskReasmt = (RiskReasmnt) sessionFactory.getCurrentSession().get(RiskReasmnt.class, idRiskReasmnt);
		sessionFactory.getCurrentSession().delete(riskReasmt);

	}

	/**
	 * Method Name: updateRiskReassessment Method Description:
	 * 
	 * @param sdmRiskReassessmentReq
	 */
	private SDMRiskReasmntDto updateRiskReassessment(SDMRiskReassessmentReq sdmRiskReassessmentReq){
		RiskReasmnt riskReasmt = new RiskReasmnt();
		SDMRiskReasmntDto sdmRiskReasmntDto = sdmRiskReassessmentReq.getSdmRiskReasmntDto();
		if (!ObjectUtils.isEmpty(sdmRiskReassessmentReq.getSdmRiskReasmntDto())
				&& !ObjectUtils.isEmpty(sdmRiskReassessmentReq.getSdmRiskReasmntDto().getIdRiskReasmnt())
				&& !ObjectUtils.isEmpty(sdmRiskReasmntDto.getDtLastUpdate())) {
			riskReasmt = (RiskReasmnt) sessionFactory.getCurrentSession().createCriteria(RiskReasmnt.class)
					.add(Restrictions.eq("idRiskReasmnt", sdmRiskReasmntDto.getIdRiskReasmnt()))
					.add(Restrictions.eq("dtLastUpdate", sdmRiskReasmntDto.getDtLastUpdate())).uniqueResult();

		}
		if (!ObjectUtils.isEmpty(riskReasmt)) {
			Set<RiskReasmntRspn> responseList = riskReasmt.getRiskReasmntRspns();
			/*
			 * If the question list is not empty , then iterate over the
			 * questions to check if those are answered.
			 */
			if (!CollectionUtils.isEmpty(sdmRiskReasmntDto.getQuestions())) {
				for (SDMRiskReasmntQstnDto sdmRiskReasmntQstnDto : sdmRiskReasmntDto.getQuestions()) {
					/*
					 * for questions other than 6 and 10 , the answer would come
					 * from the cdAnswer field in the question dto.
					 */
					if (ServiceConstants.STRING_SIX.equals(sdmRiskReasmntQstnDto.getCdQstn())
							|| ServiceConstants.STRING_TEN.equals(sdmRiskReasmntQstnDto.getCdQstn())) {

						// check if the question is answered for the primary
						if (!ObjectUtils.isEmpty(sdmRiskReasmntQstnDto.getCdAnsPrimary())) {
							SDMRiskReasmntAnswerDto sdmRiskReasmntAnswerDto = sdmRiskReasmntQstnDto.getAnswers()
									.stream().filter(answer -> answer.getCdAnswer()
											.equals(sdmRiskReasmntQstnDto.getCdAnsPrimary()))
									.findFirst().orElse(null);
							String parentProgressWithFPOSgoals = null;
							if (ServiceConstants.STRING_TEN.equalsIgnoreCase(sdmRiskReasmntQstnDto.getCdQstn())
									&& !ObjectUtils.isEmpty(sdmRiskReasmntDto.getReasonForQstn10())) {
								parentProgressWithFPOSgoals = sdmRiskReasmntDto.getReasonForQstn10();
							}
							// If none of the questions were answered during the
							// first save , then create
							if (!CollectionUtils.isEmpty(responseList)) {
								RiskReasmntRspn reasmntRspn = responseList.stream()
										.filter(response -> (response.getRiskReasmntQstnLookup()
												.getIdRiskReasmntQstnLookup().equals(sdmRiskReasmntQstnDto.getIdQstn())
												&& ServiceConstants.Y.equals(response.getIndPrmry())))
										.findFirst().orElse(null);

								// If there is no answer for the question in the
								// db
								if (ObjectUtils.isEmpty(reasmntRspn)) {
									// Create response
									riskReasmt.addRiskReasmntRspn(
											formResponseForPrimarySecondary(sdmRiskReasmntDto, sdmRiskReasmntAnswerDto,
													riskReasmt, ServiceConstants.Y, parentProgressWithFPOSgoals));

								} else {
									// check if the response has any follow-up
									// responses
									
									reasmntRspn.setTxtDescRsnPrntGoals(parentProgressWithFPOSgoals);
									reasmntRspn.setCdRiskReasmntAnswr(sdmRiskReasmntQstnDto.getCdAnsPrimary());
									reasmntRspn.setDtLastUpdate(
											Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
									reasmntRspn.setIdLastUpdatePerson(sdmRiskReasmntDto.getIdUser());
									// Setting the set of followUp responses to
									// the response object.
									reasmntRspn.getRiskReasmntFolupRspns().addAll(processFollowUpSelection(reasmntRspn,
											sdmRiskReasmntAnswerDto, sdmRiskReasmntDto, ServiceConstants.Y));

								}
							} else {

								riskReasmt.addRiskReasmntRspn(
										formResponseForPrimarySecondary(sdmRiskReasmntDto, sdmRiskReasmntAnswerDto,
												riskReasmt, ServiceConstants.Y, parentProgressWithFPOSgoals));
							}
						}

						if (!ObjectUtils.isEmpty(sdmRiskReasmntQstnDto.getCdAnsSecondary())) {
							SDMRiskReasmntAnswerDto sdmRiskReasmntAnswerDto = sdmRiskReasmntQstnDto.getAnswers()
									.stream()
									.filter(answer -> answer.getCdAnswer()
											.equals(sdmRiskReasmntQstnDto.getCdAnsSecondary()))
									.findFirst().orElse(null);
							String parentProgressWithFPOSgoals = null;
							if (ServiceConstants.STRING_TEN.equalsIgnoreCase(sdmRiskReasmntQstnDto.getCdQstn())
									&& !ObjectUtils.isEmpty(sdmRiskReasmntDto.getReasonForQstn10())) {
								parentProgressWithFPOSgoals = sdmRiskReasmntDto.getReasonForQstn10();
							}
							// If none of the questions were answered during the
							// first save , then create
							if (!CollectionUtils.isEmpty(responseList)) {
								RiskReasmntRspn reasmntRspn = responseList.stream()
										.filter(response -> (response.getRiskReasmntQstnLookup()
												.getIdRiskReasmntQstnLookup().equals(sdmRiskReasmntQstnDto.getIdQstn())
												&& ServiceConstants.N.equals(response.getIndPrmry())))
										.findFirst().orElse(null);

								// If there is no answer for the question in the
								// db
								if (ObjectUtils.isEmpty(reasmntRspn)) {
									// Create response
									riskReasmt.addRiskReasmntRspn(
											formResponseForPrimarySecondary(sdmRiskReasmntDto, sdmRiskReasmntAnswerDto,
													riskReasmt, ServiceConstants.N, parentProgressWithFPOSgoals));

								} else {
									// check if the response has any follow-up
									// responses
									reasmntRspn.setCdRiskReasmntAnswr(sdmRiskReasmntQstnDto.getCdAnsSecondary());
									reasmntRspn.setDtLastUpdate(
											Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
									reasmntRspn.setIdLastUpdatePerson(sdmRiskReasmntDto.getIdUser());
									// Setting the set of followUp responses to
									// the response object.
									reasmntRspn.getRiskReasmntFolupRspns().addAll(processFollowUpSelection(reasmntRspn,
											sdmRiskReasmntAnswerDto, sdmRiskReasmntDto, ServiceConstants.N));

								}

							} else {

								riskReasmt.addRiskReasmntRspn(
										formResponseForPrimarySecondary(sdmRiskReasmntDto, sdmRiskReasmntAnswerDto,
												riskReasmt, ServiceConstants.N, parentProgressWithFPOSgoals));
							}
						}
					} else {
						// If the question is answered ,then proceed
						if (!ObjectUtils.isEmpty(sdmRiskReasmntQstnDto.getCdAnswer())) {
							SDMRiskReasmntAnswerDto sdmRiskReasmntAnswerDto = sdmRiskReasmntQstnDto.getAnswers()
									.stream()
									.filter(answer -> answer.getCdAnswer().equals(sdmRiskReasmntQstnDto.getCdAnswer()))
									.findFirst().orElse(null);
							String parentProgressWithFPOSgoals = null;
							if (ServiceConstants.STRING_TEN.equalsIgnoreCase(sdmRiskReasmntQstnDto.getCdQstn())
									&& !ObjectUtils.isEmpty(sdmRiskReasmntDto.getReasonForQstn10())) {
								parentProgressWithFPOSgoals = sdmRiskReasmntDto.getReasonForQstn10();
							}
							/*
							 * Check in the response list , if for the
							 * particular question ,a row is present.
							 */
							if (!CollectionUtils.isEmpty(responseList)) {
								RiskReasmntRspn reasmntRspn = responseList.stream()
										.filter(response -> response.getRiskReasmntQstnLookup()
												.getIdRiskReasmntQstnLookup().equals(sdmRiskReasmntQstnDto.getIdQstn()))
										.findFirst().orElse(null);
								// If a row exists in the database already for
								// the question , then update the
								// answer to the question.
								if (!ObjectUtils.isEmpty(reasmntRspn)) {

									reasmntRspn.setCdRiskReasmntAnswr(sdmRiskReasmntQstnDto.getCdAnswer());
									reasmntRspn.setDtLastUpdate(
											Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
									reasmntRspn.setIdLastUpdatePerson(sdmRiskReasmntDto.getIdUser());

									// Setting the set of followUp responses to
									// the response object.
									reasmntRspn.getRiskReasmntFolupRspns().addAll(processFollowUpSelection(reasmntRspn,
											sdmRiskReasmntAnswerDto, sdmRiskReasmntDto, null));
								}
								/*
								 * If a answer to a question does not exist in
								 * the database and the user has answered it in
								 * the front-end now.
								 */
								else {
									reasmntRspn = new RiskReasmntRspn();
									reasmntRspn.setRiskReasmntFolupRspns(new HashSet<>());
									// Setting the values in the response entity
									// object
									reasmntRspn.setCdRiskReasmntAnswr(sdmRiskReasmntAnswerDto.getCdAnswer());
									reasmntRspn.setDtCreated(
											Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
									reasmntRspn.setDtLastUpdate(
											Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
									reasmntRspn.setIdCreatedPerson(sdmRiskReasmntDto.getIdUser());
									reasmntRspn.setIdLastUpdatePerson(sdmRiskReasmntDto.getIdUser());
									RiskReasmntAnsLookup riskReasmntAnsLookup = (RiskReasmntAnsLookup) sessionFactory
											.getCurrentSession().get(RiskReasmntAnsLookup.class,
													sdmRiskReasmntAnswerDto.getIdAnswerLookup());
									reasmntRspn.setRiskReasmntAnsLookup(riskReasmntAnsLookup);
									RiskReasmntQstnLookup riskReasmntQstnLookup = (RiskReasmntQstnLookup) sessionFactory
											.getCurrentSession()
											.get(RiskReasmntQstnLookup.class, sdmRiskReasmntQstnDto.getIdQstn());
									reasmntRspn.setRiskReasmntQstnLookup(riskReasmntQstnLookup);
									reasmntRspn.getRiskReasmntFolupRspns().addAll(formFollowUpResponseSetForAdd(
											reasmntRspn, sdmRiskReasmntAnswerDto, sdmRiskReasmntDto.getIdUser(), null));
									reasmntRspn.setRiskReasmnt(riskReasmt);
									riskReasmt.addRiskReasmntRspn(reasmntRspn);
								}
							}
							/*
							 * If the user has not answered any of the questions
							 * during the first save, then create the new
							 * response for the particular question.
							 */
							else {
								riskReasmt.getRiskReasmntRspns().add(formResponseForPrimarySecondary(sdmRiskReasmntDto,
										sdmRiskReasmntAnswerDto, riskReasmt, null, parentProgressWithFPOSgoals));
							}
						}
					}
				}

			}

			riskReasmt.setIdEvent(sdmRiskReasmntDto.getIdEvent());
			riskReasmt.setIdStage(sdmRiskReasmntDto.getIdStage());
			riskReasmt.setIdHshldPerson(sdmRiskReasmntDto.getIdHshldAssessed());
			riskReasmt.setDtAsmntCmpltd(sdmRiskReasmntDto.getDtAsmnt());
			riskReasmt.setIdPrmryPerson(sdmRiskReasmntDto.getIdPrmryPrsn());
			riskReasmt.setIdSecndryPerson(sdmRiskReasmntDto.getIdScndryPrsn());

			RiskReasmntLookup riskReasmntLookup = (RiskReasmntLookup) sessionFactory.getCurrentSession()
					.get(RiskReasmntLookup.class, 1l);
			if (!ObjectUtils.isEmpty(riskReasmntLookup)) {
				riskReasmt.setRiskReasmntLookup(riskReasmntLookup);
			}
			riskReasmt.setCdFinalRiskLvl(sdmRiskReasmntDto.getCdFinalRiskLevel());
			riskReasmt.setCdOvride(sdmRiskReasmntDto.getCdOverride());
			if (!ObjectUtils.isEmpty(sdmRiskReasmntDto.getRiskScore())) {

				riskReasmt.setNbrTotalScore(Long.valueOf(sdmRiskReasmntDto.getRiskScore()));

			}
			riskReasmt.setCdRiskLvl(sdmRiskReasmntDto.getCdScoredRiskLevel());
			riskReasmt.setIndPoInjryChild3(sdmRiskReasmntDto.getIndPOChildLessThanThreeInjured());
			riskReasmt.setIndPoSxab(sdmRiskReasmntDto.getIndPOChildSexualAbuse());
			riskReasmt.setIndPoInjryChild16(sdmRiskReasmntDto.getIndPOChildLessThanSixteenInjured());
			riskReasmt.setIndPoChildDeath(sdmRiskReasmntDto.getIndPOChildDeath());
			riskReasmt.setCdDiscOvrideRlvl(sdmRiskReasmntDto.getCdRiskLevelDiscOvrride());
			riskReasmt.setTxtDiscOvrideRsn(sdmRiskReasmntDto.getTxtOverrideReason());
			riskReasmt.setCdFinalRiskLvl(sdmRiskReasmntDto.getCdFinalRiskLevel());
			riskReasmt.setCdPlannedActn(sdmRiskReasmntDto.getCdPlndActn());
			riskReasmt.setTxtRsnRecmndtnNotMatch(sdmRiskReasmntDto.getTxtRsnRecmndtnNotMatch());
			riskReasmt.setDtLastUpdate(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
			riskReasmt.setIdLastUpdatePerson(sdmRiskReasmntDto.getIdUser());
			sessionFactory.getCurrentSession().saveOrUpdate(riskReasmt);

		} else {
			String message = ((Message) sessionFactory.getCurrentSession().createCriteria(Message.class)
					.add(Restrictions.eq("nbrMessage", Messages.MSG_CMN_TMSTAMP_MISMATCH)).uniqueResult())
							.getTxtMessage();
			throw new DataLayerException(message);
		}
		return sdmRiskReasmntDto;
	}

	/**
	 * Method Name: getReunificationAssessmentList Method Description:This
	 * method is used to retrieve the SDM Re-unification assessment details for
	 * a particular stage.
	 * 
	 * @param idStage
	 *            - The id of the stage.
	 * @return List<ReunificationAssessmentDto> - This collection will hold the
	 *         SDM Re-unification assessment details.
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<ReunificationAssessmentDto> getReunificationAssessmentList(Long idStage) {
		/*
		 * Query the SDM Reunification Assessment for getting the Household ,
		 * Primary Parent and Secondary Parent
		 */
		List<ReunfctnAsmnt> reunificationAssessmentList = sessionFactory.getCurrentSession()
				.createCriteria(ReunfctnAsmnt.class).add(Restrictions.eq("idStage", idStage))
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
		List<ReunificationAssessmentDto> reunificationList = new ArrayList<ReunificationAssessmentDto>();
		if (!CollectionUtils.isEmpty(reunificationAssessmentList)) {
			reunificationAssessmentList.forEach(reunification -> {
				ReunificationAssessmentDto reunificationAssessmentDto = new ReunificationAssessmentDto();
				BeanUtils.copyProperties(reunification, reunificationAssessmentDto);
				reunificationList.add(reunificationAssessmentDto);
			});

		}
		return reunificationList;
	}

	/**
	 * Method Name: getFamilySubstituteCareStageId Method Description:This
	 * method is used to get the id stage of the FSU stage which was created
	 * from an INV stage.
	 * 
	 * @param idPriorStage
	 *            - The id of the INV stage.
	 * @param idStage
	 *            - The id of the SUB stage which gets created from an INV
	 *            stage.
	 * @return Long - The id of the FSU stage.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Long getFamilySubstituteCareStageId(Long idPriorStage, Long idStage) {
		Long idFamilySubstituteCare = null;
		List<StageLink> stageLinkList = sessionFactory.getCurrentSession().createCriteria(StageLink.class)
				.add(Restrictions.eq("idPriorStage", idPriorStage)).list();

		if (!CollectionUtils.isEmpty(stageLinkList)) {
			StageLink stageLink = stageLinkList.stream().filter(stage -> !stage.getIdStage().equals(idStage))
					.findFirst().orElse(null);
			if (!ObjectUtils.isEmpty(stageLink)) {
				idFamilySubstituteCare = stageLink.getIdStage();
			}
		}
		return idFamilySubstituteCare;
	}

	/**
	 * Method Name: checkRiskReasmntExists Method Description:This method is
	 * used to check if a risk reassessment exists for a person which is in PROC
	 * or PEND status.
	 * 
	 * @param idPerson
	 *            - The id of the person.
	 * @param indHshldPrmryScndry
	 *            - The indicator to indicate if the person is a household
	 *            person , primary person or secondary person.
	 * @param eventStatusList
	 *            - The list of event status.
	 * @param idStage
	 *            - The id of the stage.
	 * @return Long - The id of the risk reasmnt for a particular person if it
	 *         exists.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Long checkRiskReasmntExists(Long idPerson, String indHshldPrmryScndry, List<String> eventStatusList,
			Long idStage) {
		Long idReasmnt = null;
		StringBuffer sql = new StringBuffer(checkRiskReasmntExistsSql);
		String placeholder = null;
		switch (indHshldPrmryScndry) {

		case String_H:
			placeholder = householdConditionSql;
			break;

		case String_P:
			placeholder = primaryConditionSql;
			break;

		case String_S:
			placeholder = secondaryConditionSql;
			break;
		}
		sql.append(placeholder);

		List<SDMRiskReasmntDto> riskReasmntList = sessionFactory.getCurrentSession().createSQLQuery(sql.toString())
				.addScalar("idRiskReasmnt", StandardBasicTypes.LONG)
				.setResultTransformer(Transformers.aliasToBean(SDMRiskReasmntDto.class))
				.setParameter("idPerson", idPerson).setParameter("idStage", idStage)
				.setParameterList("eventStatus", eventStatusList).list();
		if (!CollectionUtils.isEmpty(riskReasmntList)) {
			idReasmnt = riskReasmntList.get(0).getIdRiskReasmnt();
		}
		return idReasmnt;
	}

	/**
	 * Method Name: getPreviousReasmntDate Method Description:This method is
	 * used to retrieve the previous risk reassessment created date in a
	 * particular stage.
	 * 
	 * @param idStage
	 *            - The id of the stage.
	 * @return Date - The date of the previous reassessment.
	 */
	@Override
	public Date getPreviousReasmntDate(Long idStage) {
		Date dtPreviousReasmnt = null;
		SDMRiskReasmntDto sdmRiskReasmntDto = (SDMRiskReasmntDto) sessionFactory.getCurrentSession()
				.createSQLQuery(getPreviousReasmntDateSql).addScalar("dtAsmnt", StandardBasicTypes.TIMESTAMP)
				.setResultTransformer(Transformers.aliasToBean(SDMRiskReasmntDto.class))
				.setParameter("idStage", idStage).uniqueResult();
		if (!ObjectUtils.isEmpty(sdmRiskReasmntDto)) {
			dtPreviousReasmnt = sdmRiskReasmntDto.getDtAsmnt();
		}
		return dtPreviousReasmnt;
	}

	/**
	 * Method Name: getExistingReassessmentData Method Description:This method
	 * is used to retrieve an existing SDM Risk Reassessment details.
	 * 
	 * @param sdmRiskReasmntDto
	 *            - This dto will hold the input parameters for getting the risk
	 *            reassessment details.
	 * @return SDMRiskReasmntDto - This dto will hold the details of the new
	 *         risk reassessment.
	 * 
	 */
	@Override
	public SDMRiskReasmntDto getExistingReassessmentData(SDMRiskReasmntDto sdmRiskReasmntDto) {
		RiskReasmnt riskReasmnt = (RiskReasmnt) sessionFactory.getCurrentSession().createCriteria(RiskReasmnt.class)
				.add(Restrictions.eq("idEvent", sdmRiskReasmntDto.getIdEvent())).uniqueResult();
		List<SDMRiskReasmntQstnDto> qstnList = getNewReassessmentData(
				riskReasmnt.getRiskReasmntLookup().getIdRiskReasmntLookup(), sdmRiskReasmntDto).getQuestions();
		String parentProgressForFamilyGoals = null;
		if (!CollectionUtils.isEmpty(qstnList) && !CollectionUtils.isEmpty(riskReasmnt.getRiskReasmntRspns())) {

			/*
			 * If the response is for Qstn 6 or 10 ,since 2 rows can be created
			 * in the RISK_REASMNT_RESPN table , checking explicitly if the
			 * response is for primary or secondary.
			 */
			for (RiskReasmntRspn response : riskReasmnt.getRiskReasmntRspns()) {
				if (response.getCdRiskReasmntAnswr().charAt(0) == ServiceConstants.CHAR_SIX
						|| (response.getCdRiskReasmntAnswr().charAt(0) == ServiceConstants.CHAR_ONE
								&& response.getCdRiskReasmntAnswr().charAt(1) == ServiceConstants.CHAR_ZERO)) {
					/*
					 * If the indicator in IND_PRIMARY is Y then the response is
					 * for the primary parent/care-giver.
					 */
					if (ServiceConstants.Y.equals(response.getIndPrmry())) {
						SDMRiskReasmntQstnDto sdmRiskReasmntQstnDto = qstnList.stream()
								.filter(qstn -> qstn.getIdQstn()
										.equals(response.getRiskReasmntQstnLookup().getIdRiskReasmntQstnLookup()))
								.findFirst().orElse(null);
						if (!ObjectUtils.isEmpty(sdmRiskReasmntQstnDto)) {
							sdmRiskReasmntQstnDto.setCdAnsPrimary(response.getCdRiskReasmntAnswr());
						}
						if (!ObjectUtils.isEmpty(response.getTxtDescRsnPrntGoals())) {
							parentProgressForFamilyGoals = response.getTxtDescRsnPrntGoals();
						}
						processFollowUpSelectionForQstn6(response, sdmRiskReasmntQstnDto);
					}
					/*
					 * If the indicator in IND_PRIMARY is N then the response is
					 * for the secondary parent/care-giver
					 */
					else if (ServiceConstants.N.equals(response.getIndPrmry())) {
						/*
						 * Get the question details based on the idQuestion so
						 * that the answer selected can be set to the question.
						 */
						SDMRiskReasmntQstnDto sdmRiskReasmntQstnDto = qstnList.stream()
								.filter(qstn -> qstn.getIdQstn()
										.equals(response.getRiskReasmntQstnLookup().getIdRiskReasmntQstnLookup()))
								.findFirst().orElse(null);
						// If the question is found , then updating the selected
						// answer which was saved
						if (!ObjectUtils.isEmpty(sdmRiskReasmntQstnDto)) {
							sdmRiskReasmntQstnDto.setCdAnsSecondary(response.getCdRiskReasmntAnswr());
						}
						if (!ObjectUtils.isEmpty(response.getTxtDescRsnPrntGoals())) {
							parentProgressForFamilyGoals = response.getTxtDescRsnPrntGoals();
						}
						processFollowUpSelectionForQstn6(response, sdmRiskReasmntQstnDto);
					}
				}
				/*
				 * For responses to questions other than question 6 and 10 ,
				 * checking if a question can be found out using idQuestion in
				 * the RISK_REASMNT_RSPNS table.
				 */
				else {
					/*
					 * Get the question details based on the idQuestion so that
					 * the answer selected can be set to the question.
					 */
					SDMRiskReasmntQstnDto sdmRiskReasmntQstnDto = qstnList.stream()
							.filter(qstn -> qstn.getIdQstn()
									.equals(response.getRiskReasmntQstnLookup().getIdRiskReasmntQstnLookup()))
							.findFirst().orElse(null);
					// If the question is found , then updating the selected
					// answer which was saved
					if (!ObjectUtils.isEmpty(sdmRiskReasmntQstnDto)) {
						sdmRiskReasmntQstnDto.setCdAnswer(response.getCdRiskReasmntAnswr());

						/*
						 * If follow-up was saved for any answer then setting
						 * the selected follow-up option in the dto.
						 */
						if (!CollectionUtils.isEmpty(response.getRiskReasmntFolupRspns())) {
							List<SDMRiskReasmntAnswerDto> sdmRiskReasmntAnswerDtoList = sdmRiskReasmntQstnDto
									.getAnswers();
							/*
							 * Iterating over the list of follow-up saved for a
							 * particular answer to get the followUpQstnDto in
							 * which the selected follow-up value has to be
							 * updated.
							 */
							for (RiskReasmntFolupRspn riskReasmntFolupRspn : response.getRiskReasmntFolupRspns()) {
								/*
								 * Filtering the list of followUpDto based on
								 * the idFollowUpLookUp which is also available
								 * in the RISK_REASMNT_FOLUP_RSPNS
								 */
								SDMRiskReasmntFollowupDto sdmRiskReasmntFollowupDto = sdmRiskReasmntAnswerDtoList
										.stream()
										.flatMap(answer -> answer.getFollowupQuestions().stream()
												.filter(followUp -> followUp.getIdFollowupLookup()
														.equals(riskReasmntFolupRspn.getIdRiskReasmntFolUpLookUp())))
										.findFirst().orElse(null);
								// If the followUpDto is found then updated the
								// saved value to the dto.
								if (!ObjectUtils.isEmpty(sdmRiskReasmntFollowupDto)) {
									RiskReasmntFlwupLookup riskReasmntFlwupLookup = (RiskReasmntFlwupLookup) sessionFactory
											.getCurrentSession().get(RiskReasmntFlwupLookup.class,
													riskReasmntFolupRspn.getIdRiskReasmntFolUpLookUp());
									sdmRiskReasmntFollowupDto.setCdAnswerSelected(riskReasmntFlwupLookup.getCdFlwup());
								}

							}
						}
					}
				}

			}
		}
		sdmRiskReasmntDto.setIdFormVersion(riskReasmnt.getRiskReasmntLookup().getIdRiskReasmntLookup());
		// finally setting the question list with the answers for each question
		// in the
		// dto.
		sdmRiskReasmntDto.setQuestions(qstnList);
		// Setting the other values from the RISK_REASMNT table.
		sdmRiskReasmntDto.setIdRiskReasmnt(riskReasmnt.getIdRiskReasmnt());
		sdmRiskReasmntDto.setIdHshldAssessed(riskReasmnt.getIdHshldPerson());
		sdmRiskReasmntDto.setIdPrmryPrsn(riskReasmnt.getIdPrmryPerson());
		if (!ObjectUtils.isEmpty(riskReasmnt.getIdSecndryPerson())) {
			sdmRiskReasmntDto.setIdScndryPrsn(riskReasmnt.getIdSecndryPerson());
		}
		sdmRiskReasmntDto.setDtAsmnt(riskReasmnt.getDtAsmntCmpltd());
		if (!ObjectUtils.isEmpty(riskReasmnt.getCdFinalRiskLvl())) {
			sdmRiskReasmntDto.setCdFinalRiskLevel(riskReasmnt.getCdFinalRiskLvl());
		}
		if (!ObjectUtils.isEmpty(riskReasmnt.getCdOvride())) {
			sdmRiskReasmntDto.setCdOverride(riskReasmnt.getCdOvride());
		}
		if (!ObjectUtils.isEmpty(riskReasmnt.getCdPlannedActn())) {
			sdmRiskReasmntDto.setCdPlndActn(riskReasmnt.getCdPlannedActn());
		}
		if (!ObjectUtils.isEmpty(riskReasmnt.getCdPlannedActn())) {
			sdmRiskReasmntDto.setCdScoredRiskLevel(riskReasmnt.getCdPlannedActn());
		}
		if (!ObjectUtils.isEmpty(riskReasmnt.getCdPlannedActn())) {
			sdmRiskReasmntDto.setIndPOChildDeath(riskReasmnt.getIndPoChildDeath());
		}
		if (!ObjectUtils.isEmpty(riskReasmnt.getIndPoInjryChild16())) {
			sdmRiskReasmntDto.setIndPOChildLessThanSixteenInjured(riskReasmnt.getIndPoInjryChild16());
		}
		if (!ObjectUtils.isEmpty(riskReasmnt.getIndPoInjryChild3())) {
			sdmRiskReasmntDto.setIndPOChildLessThanThreeInjured(riskReasmnt.getIndPoInjryChild3());
		}
		if (!ObjectUtils.isEmpty(riskReasmnt.getIndPoSxab())) {
			sdmRiskReasmntDto.setIndPOChildSexualAbuse(riskReasmnt.getIndPoSxab());
		}
		if (!ObjectUtils.isEmpty(riskReasmnt.getNbrTotalScore())) {
			sdmRiskReasmntDto.setRiskScore(riskReasmnt.getNbrTotalScore());
		}
		if (!ObjectUtils.isEmpty(riskReasmnt.getCdRiskLvl())) {
			sdmRiskReasmntDto.setCdScoredRiskLevel(riskReasmnt.getCdRiskLvl());
		}
		if (!ObjectUtils.isEmpty(riskReasmnt.getCdDiscOvrideRlvl())) {
			sdmRiskReasmntDto.setCdRiskLevelDiscOvrride(riskReasmnt.getCdDiscOvrideRlvl());
		}
		if (!ObjectUtils.isEmpty(riskReasmnt.getTxtDiscOvrideRsn())) {
			sdmRiskReasmntDto.setTxtOverrideReason(riskReasmnt.getTxtDiscOvrideRsn());
		}
		if (!ObjectUtils.isEmpty(riskReasmnt.getTxtRsnRecmndtnNotMatch())) {
			sdmRiskReasmntDto.setTxtRsnRecmndtnNotMatch(riskReasmnt.getTxtRsnRecmndtnNotMatch());
		}
		if (!ObjectUtils.isEmpty(riskReasmnt.getIdCreatedPerson())) {
			Person person = personDao.getPersonByPersonId(riskReasmnt.getIdCreatedPerson());
			sdmRiskReasmntDto.setCreatedBy(person.getNmPersonFull());
		}
		if (!ObjectUtils.isEmpty(riskReasmnt.getIdLastUpdatePerson())) {
			Person person = personDao.getPersonByPersonId(riskReasmnt.getIdLastUpdatePerson());
			sdmRiskReasmntDto.setUpdatedBy(person.getNmPersonFull());
		}
		if (!ObjectUtils.isEmpty(riskReasmnt.getDtCreated())) {
			sdmRiskReasmntDto.setDtCreatedOn(riskReasmnt.getDtCreated());
		}
		if (!ObjectUtils.isEmpty(riskReasmnt.getDtLastUpdate())) {
			sdmRiskReasmntDto.setDtLastUpdate(riskReasmnt.getDtLastUpdate());
		}
		if (!ObjectUtils.isEmpty(parentProgressForFamilyGoals)) {
			sdmRiskReasmntDto.setReasonForQstn10(parentProgressForFamilyGoals);
		}
		if (!ObjectUtils.isEmpty(riskReasmnt.getIdStage())) {
			sdmRiskReasmntDto.setIdStage(riskReasmnt.getIdStage());
		}
		return sdmRiskReasmntDto;
	}

	private SDMRiskReasmntDto addRiskReassessment(SDMRiskReassessmentReq sdmRiskReassessmentReq) {

		RiskReasmnt riskReasmt = new RiskReasmnt();
		Long idRiskReasmnt = 0L;
		SDMRiskReasmntDto sdmRiskReasmntDto = sdmRiskReassessmentReq.getSdmRiskReasmntDto();
		riskReasmt.setRiskReasmntRspns(formResponseSetForAdd(sdmRiskReasmntDto, riskReasmt));
		riskReasmt.setIdEvent(sdmRiskReasmntDto.getIdEvent());
		riskReasmt.setIdStage(sdmRiskReasmntDto.getIdStage());
		riskReasmt.setIdHshldPerson(sdmRiskReasmntDto.getIdHshldAssessed());
		riskReasmt.setDtAsmntCmpltd(sdmRiskReasmntDto.getDtAsmnt());
		riskReasmt.setIdPrmryPerson(sdmRiskReasmntDto.getIdPrmryPrsn());
		riskReasmt.setIdSecndryPerson(sdmRiskReasmntDto.getIdScndryPrsn());

		RiskReasmntLookup riskReasmntLookup = (RiskReasmntLookup) sessionFactory.getCurrentSession()
				.get(RiskReasmntLookup.class, 1l);
		if (!ObjectUtils.isEmpty(riskReasmntLookup)) {
			riskReasmt.setRiskReasmntLookup(riskReasmntLookup);
		}

		riskReasmt.setCdOvride(sdmRiskReasmntDto.getCdOverride());
		if (!ObjectUtils.isEmpty(sdmRiskReasmntDto.getRiskScore())) {
			riskReasmt.setNbrTotalScore(Long.valueOf(sdmRiskReasmntDto.getRiskScore()));
		}

		riskReasmt.setCdRiskLvl(sdmRiskReasmntDto.getCdScoredRiskLevel());
		riskReasmt.setIndPoInjryChild3(sdmRiskReasmntDto.getIndPOChildLessThanThreeInjured());
		riskReasmt.setIndPoSxab(sdmRiskReasmntDto.getIndPOChildSexualAbuse());
		riskReasmt.setIndPoInjryChild16(sdmRiskReasmntDto.getIndPOChildLessThanSixteenInjured());
		riskReasmt.setIndPoChildDeath(sdmRiskReasmntDto.getIndPOChildDeath());
		riskReasmt.setCdDiscOvrideRlvl(sdmRiskReasmntDto.getCdFinalRiskLevel());
		riskReasmt.setTxtDiscOvrideRsn(sdmRiskReasmntDto.getTxtOverrideReason());
		riskReasmt.setCdDiscOvrideRlvl(sdmRiskReasmntDto.getCdFinalRiskLevel());
		riskReasmt.setCdPlannedActn(sdmRiskReasmntDto.getCdPlndActn());
		riskReasmt.setTxtRsnRecmndtnNotMatch(sdmRiskReasmntDto.getTxtRsnRecmndtnNotMatch());
		riskReasmt.setIdCreatedPerson(sdmRiskReasmntDto.getIdUser());
		riskReasmt.setDtLastUpdate(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
		riskReasmt.setIdLastUpdatePerson(sdmRiskReasmntDto.getIdUser());
		riskReasmt.setDtCreated(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
		idRiskReasmnt = (Long) sessionFactory.getCurrentSession().save(riskReasmt);
		sdmRiskReasmntDto.setIdRiskReasmnt(idRiskReasmnt);

		return sdmRiskReasmntDto;
	}

	private Set<RiskReasmntRspn> formResponseSetForAdd(SDMRiskReasmntDto sdmRiskReasmntDto, RiskReasmnt riskReasmnt) {
		String cdAnswer = ServiceConstants.EMPTY_STR;
		Long idAnswer = 0L;

		Set<RiskReasmntRspn> riskReasmntRspn = new HashSet<>();
		for (SDMRiskReasmntQstnDto questionDto : sdmRiskReasmntDto.getQuestions()) {
			boolean indQstn6orQstn10 = false;
			RiskReasmntQstnLookup riskReasmntQstnLookup = new RiskReasmntQstnLookup();
			SDMRiskReasmntAnswerDto sdmRiskReasmntAnswerDto = null;

			/*
			 * Check if the question is answered before proceeding with the
			 * creation of response entity object. If the question has been
			 * answered then the cdAnswer field would be populated. For question
			 * 6 and 10 , the cdAnsPrimary or cdAnsSecondary would be populated.
			 */
			if (!ObjectUtils.isEmpty(questionDto.getCdAnswer())) {
				sdmRiskReasmntAnswerDto = questionDto.getAnswers().stream()
						.filter(x -> x.getCdAnswer().equals(questionDto.getCdAnswer())).findAny().orElse(null);
			} else if (!ObjectUtils.isEmpty(questionDto.getCdAnsPrimary())
					|| !ObjectUtils.isEmpty(questionDto.getCdAnsSecondary())) {
				indQstn6orQstn10 = true;
			}

			if (!ObjectUtils.isEmpty(sdmRiskReasmntAnswerDto) || indQstn6orQstn10) {
				RiskReasmntAnsLookup riskReasmntAnsLookup = new RiskReasmntAnsLookup();
				RiskReasmntRspn riskReasmntRspnObject = new RiskReasmntRspn();

				/*
				 * If the question is 6 or 10 , then 2 rows has to be created in
				 * the RISK_REASMNT_RSPNS table if question is answered for both
				 * primary and secondary.
				 */
				if (questionDto.getCdQstn().equalsIgnoreCase(ServiceConstants.STRING_SIX)
						|| questionDto.getCdQstn().equalsIgnoreCase(ServiceConstants.STRING_TEN)) {
					// Check if the question is answered for primary.
					if (!ObjectUtils.isEmpty(questionDto.getCdAnsPrimary())) {
						sdmRiskReasmntAnswerDto = questionDto.getAnswers().stream()
								.filter(x -> x.getCdAnswer().equals(questionDto.getCdAnsPrimary())).findAny()
								.orElse(null);
						String parentProgressWithFPOSgoals = null;
						if (ServiceConstants.STRING_TEN.equalsIgnoreCase(questionDto.getCdQstn())
								&& !ObjectUtils.isEmpty(sdmRiskReasmntDto.getReasonForQstn10())) {
							parentProgressWithFPOSgoals = sdmRiskReasmntDto.getReasonForQstn10();
						}
						riskReasmntRspn.add(formResponseForPrimarySecondary(sdmRiskReasmntDto, sdmRiskReasmntAnswerDto,
								riskReasmnt, ServiceConstants.Y, parentProgressWithFPOSgoals));
					}
					// Check if the question is answered for secondary.
					if (!ObjectUtils.isEmpty(questionDto.getCdAnsSecondary())) {
						riskReasmntRspnObject = new RiskReasmntRspn();
						cdAnswer = questionDto.getCdAnsSecondary();
						sdmRiskReasmntAnswerDto = questionDto.getAnswers().stream()
								.filter(x -> x.getCdAnswer().equals(questionDto.getCdAnsSecondary())).findAny()
								.orElse(null);
						String parentProgressWithFPOSgoals = null;
						if (ServiceConstants.STRING_TEN.equalsIgnoreCase(questionDto.getCdQstn())
								&& !ObjectUtils.isEmpty(sdmRiskReasmntDto.getReasonForQstn10())) {
							parentProgressWithFPOSgoals = sdmRiskReasmntDto.getReasonForQstn10();
						}
						riskReasmntRspn.add(formResponseForPrimarySecondary(sdmRiskReasmntDto, sdmRiskReasmntAnswerDto,
								riskReasmnt, ServiceConstants.N, parentProgressWithFPOSgoals));

					}
				} else {
					// Setting the values in the response entity object
					cdAnswer = sdmRiskReasmntAnswerDto.getCdAnswer();
					idAnswer = sdmRiskReasmntAnswerDto.getIdAnswerLookup();

					riskReasmntRspnObject.setCdRiskReasmntAnswr(cdAnswer);
					riskReasmntRspnObject
							.setDtCreated(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
					riskReasmntRspnObject
							.setDtLastUpdate(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
					riskReasmntRspnObject.setIdCreatedPerson(sdmRiskReasmntDto.getIdUser());
					riskReasmntRspnObject.setIdLastUpdatePerson(sdmRiskReasmntDto.getIdUser());

					riskReasmntAnsLookup = (RiskReasmntAnsLookup) sessionFactory.getCurrentSession()
							.get(RiskReasmntAnsLookup.class, idAnswer);
					riskReasmntRspnObject.setRiskReasmntAnsLookup(riskReasmntAnsLookup);
					riskReasmntQstnLookup = (RiskReasmntQstnLookup) sessionFactory.getCurrentSession()
							.get(RiskReasmntQstnLookup.class, questionDto.getIdQstn());
					riskReasmntRspnObject.setRiskReasmntQstnLookup(riskReasmntQstnLookup);
					riskReasmntRspnObject.setRiskReasmntFolupRspns(formFollowUpResponseSetForAdd(riskReasmntRspnObject,
							sdmRiskReasmntAnswerDto, sdmRiskReasmntDto.getIdUser(), null));
					riskReasmntRspnObject.setRiskReasmnt(riskReasmnt);
					riskReasmntRspn.add(riskReasmntRspnObject);

				}
			}
		}
		return riskReasmntRspn;
	}

	private Set<RiskReasmntFolupRspn> formFollowUpResponseSetForAdd(RiskReasmntRspn riskReasmntRspn,
			SDMRiskReasmntAnswerDto sdmRiskReasmntAnswerDto, Long idUser, String indPrimary) {
		Set<RiskReasmntFolupRspn> riskReasmntFolupRspns = new HashSet<>();
		List<SDMRiskReasmntFollowupDto> followUpQstsnsList = new ArrayList<SDMRiskReasmntFollowupDto>();
		if (ServiceConstants.N.equals(indPrimary)) {
			followUpQstsnsList = sdmRiskReasmntAnswerDto.getFollowupQuestionSec();

		} else {
			followUpQstsnsList = sdmRiskReasmntAnswerDto.getFollowupQuestions();
		}

		if (!ObjectUtils.isEmpty(sdmRiskReasmntAnswerDto) && !CollectionUtils.isEmpty(followUpQstsnsList)) {
			for (SDMRiskReasmntFollowupDto followupDto : followUpQstsnsList) {
				/*
				 * If the followUp is selected then create the followUp response
				 * object to be saved in the database.
				 */
				if (!ObjectUtils.isEmpty(followupDto.getCdAnswerSelected())) {

					// Follow up dto code
					RiskReasmntFolupRspn riskReasmntFolupRspnsObject = new RiskReasmntFolupRspn();
					RiskReasmntFlwupLookup riskReasmntFlwupLookup = (RiskReasmntFlwupLookup) sessionFactory
							.getCurrentSession().createCriteria(RiskReasmntFlwupLookup.class)
							.add(Restrictions.eq("cdFlwup", followupDto.getCdAnswerSelected())).uniqueResult();
					riskReasmntFolupRspnsObject
							.setIdRiskReasmntFolUpLookUp(riskReasmntFlwupLookup.getIdRiskReasmntFlwupLookup());
					riskReasmntFolupRspnsObject
							.setDtLastUpdate(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
					riskReasmntFolupRspnsObject.setIdCreatedPerson(idUser);
					riskReasmntFolupRspnsObject.setIdLastUpdatePerson(idUser);
					riskReasmntFolupRspnsObject.setIndPrimary(indPrimary);
					riskReasmntFolupRspnsObject.setIndRiskReasmntFollowup(ServiceConstants.Y);
					if(ServiceConstants.YES.equals(indPrimary) &&  !ObjectUtils.isEmpty(followupDto.getTxtSbstncAbusePrmry())) {
						riskReasmntFolupRspnsObject.setTxtSubstncAbuseDesc(followupDto.getTxtSbstncAbusePrmry());
					}
					else if(ServiceConstants.N.equals(indPrimary) &&  !ObjectUtils.isEmpty(followupDto.getTxtSbstncAbuseSec())) {
						riskReasmntFolupRspnsObject.setTxtSubstncAbuseDesc(followupDto.getTxtSbstncAbuseSec());
					}
					
					riskReasmntFolupRspnsObject.setRiskReasmntRspn(riskReasmntRspn);
					riskReasmntFolupRspns.add(riskReasmntFolupRspnsObject);

				}
			}
		}
		return riskReasmntFolupRspns;
	}

	private RiskReasmntRspn formResponseForPrimarySecondary(SDMRiskReasmntDto sdmRiskReasmntDto,
			SDMRiskReasmntAnswerDto sdmRiskReasmntAnswerDto, RiskReasmnt riskReasmnt, String indPrimary,
			String parentProgressWithFPOSgoals) {
		RiskReasmntRspn riskReasmntRspnObject = new RiskReasmntRspn();

		riskReasmntRspnObject.setIndPrmry(indPrimary);
		riskReasmntRspnObject.setCdRiskReasmntAnswr(sdmRiskReasmntAnswerDto.getCdAnswer());
		riskReasmntRspnObject.setDtCreated(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
		riskReasmntRspnObject
				.setDtLastUpdate(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
		riskReasmntRspnObject.setIdCreatedPerson(sdmRiskReasmntDto.getIdUser());
		riskReasmntRspnObject.setIdLastUpdatePerson(sdmRiskReasmntDto.getIdUser());

		if (!ObjectUtils.isEmpty(parentProgressWithFPOSgoals)) {
			riskReasmntRspnObject.setTxtDescRsnPrntGoals(parentProgressWithFPOSgoals);
		}
		RiskReasmntAnsLookup riskReasmntAnsLookup = (RiskReasmntAnsLookup) sessionFactory.getCurrentSession()
				.get(RiskReasmntAnsLookup.class, sdmRiskReasmntAnswerDto.getIdAnswerLookup());
		riskReasmntRspnObject.setRiskReasmntAnsLookup(riskReasmntAnsLookup);
		RiskReasmntQstnLookup riskReasmntQstnLookup = (RiskReasmntQstnLookup) sessionFactory.getCurrentSession()
				.get(RiskReasmntQstnLookup.class, sdmRiskReasmntAnswerDto.getIdQstnLookup());
		riskReasmntRspnObject.setRiskReasmntQstnLookup(riskReasmntQstnLookup);

		riskReasmntRspnObject.setRiskReasmntFolupRspns(formFollowUpResponseSetForAdd(riskReasmntRspnObject,
				sdmRiskReasmntAnswerDto, sdmRiskReasmntDto.getIdUser(), indPrimary));
		riskReasmntRspnObject.setRiskReasmnt(riskReasmnt);
		return riskReasmntRspnObject;
	}

	private Set<RiskReasmntFolupRspn> processFollowUpSelection(RiskReasmntRspn reasmntRspn,
			SDMRiskReasmntAnswerDto sdmRiskReasmntAnswerDto, SDMRiskReasmntDto sdmRiskReasmntDto,
			String indPrimarySecondary) {
		Set<RiskReasmntFolupRspn> riskReasmntFolupRspns = new HashSet<RiskReasmntFolupRspn>();
		List<SDMRiskReasmntFollowupDto> followUpQstsnsList = new ArrayList<SDMRiskReasmntFollowupDto>();
		if (ServiceConstants.N.equals(indPrimarySecondary)) {
			followUpQstsnsList = sdmRiskReasmntAnswerDto.getFollowupQuestionSec();
		} else {
			followUpQstsnsList = sdmRiskReasmntAnswerDto.getFollowupQuestions();
		}
		/*
		 * Editing/adding entries to the existing follow-up available in the
		 * database. Check if for the particular answer, there is any follow-up
		 * present in the database.
		 */
		if (!CollectionUtils.isEmpty(reasmntRspn.getRiskReasmntFolupRspns())) {
			Set<RiskReasmntFolupRspn> followUpResponse = reasmntRspn.getRiskReasmntFolupRspns();
			if (!CollectionUtils.isEmpty(followUpQstsnsList)) {
				/*
				 * Iterate over the follow-up response available in the db, then
				 * check them against the follow-up selected from the front-end.
				 */
				for (SDMRiskReasmntFollowupDto sdmRiskReasmntFollowupDto : followUpQstsnsList) {

					RiskReasmntFolupRspn reasmntFolupRspn = null;
					RiskReasmntFlwupLookup riskReasmntFlwupLookup = (RiskReasmntFlwupLookup) sessionFactory
							.getCurrentSession().createCriteria(RiskReasmntFlwupLookup.class)
							.add(Restrictions.eq("cdFlwup", sdmRiskReasmntFollowupDto.getCdFollowup())).uniqueResult();

					for (RiskReasmntFolupRspn riskReasmntFolupRspn : followUpResponse) {
						if (riskReasmntFolupRspn.getIdRiskReasmntFolUpLookUp()
								.equals(riskReasmntFlwupLookup.getIdRiskReasmntFlwupLookup())) {
							reasmntFolupRspn = riskReasmntFolupRspn;
							break;
						}
					}

					/*
					 * If a followUp response exists in the db but the followUp
					 * is not selected in the front end , then remove from the
					 * set of follow-up responses so that it is deleted.
					 */
					if (!ObjectUtils.isEmpty(reasmntFolupRspn)
							&& ObjectUtils.isEmpty(sdmRiskReasmntFollowupDto.getCdAnswerSelected())) {
						reasmntRspn.removeRiskReasmntFolupRspn(reasmntFolupRspn);
					}
					/*
					 * If a followUp response does not exists in the db but the
					 * followUp is selected in the front end , then add a row to
					 * the set of follow-up responses so that it is created in
					 * the db.
					 *
					 */

					else if (ObjectUtils.isEmpty(reasmntFolupRspn)
							&& !ObjectUtils.isEmpty(sdmRiskReasmntFollowupDto.getCdAnswerSelected())) {
						// Follow up dto code
						RiskReasmntFolupRspn riskReasmntFolupRspnsObject = new RiskReasmntFolupRspn();
						riskReasmntFolupRspnsObject
								.setIdRiskReasmntFolUpLookUp(riskReasmntFlwupLookup.getIdRiskReasmntFlwupLookup());
						riskReasmntFolupRspnsObject.setDtLastUpdate(
								Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
						riskReasmntFolupRspnsObject.setIdCreatedPerson(sdmRiskReasmntDto.getIdUser());
						riskReasmntFolupRspnsObject.setIdLastUpdatePerson(sdmRiskReasmntDto.getIdUser());
						riskReasmntFolupRspnsObject.setIndRiskReasmntFollowup(ServiceConstants.Y);
						riskReasmntFolupRspnsObject.setIndPrimary(indPrimarySecondary);
						if(ServiceConstants.YES.equals(indPrimarySecondary) &&  !ObjectUtils.isEmpty(sdmRiskReasmntFollowupDto.getTxtSbstncAbusePrmry())) {
							riskReasmntFolupRspnsObject.setTxtSubstncAbuseDesc(sdmRiskReasmntFollowupDto.getTxtSbstncAbusePrmry());
						}
						else if(ServiceConstants.N.equals(indPrimarySecondary) &&  !ObjectUtils.isEmpty(sdmRiskReasmntFollowupDto.getTxtSbstncAbuseSec())) {
							riskReasmntFolupRspnsObject.setTxtSubstncAbuseDesc(sdmRiskReasmntFollowupDto.getTxtSbstncAbuseSec());
						}
						riskReasmntFolupRspnsObject.setRiskReasmntRspn(reasmntRspn);
						riskReasmntFolupRspns.add(riskReasmntFolupRspnsObject);
					}/* else if (ObjectUtils.isEmpty(reasmntFolupRspn)
							&& ObjectUtils.isEmpty(sdmRiskReasmntFollowupDto.getCdAnswerSelected())) {
						reasmntRspn.getRiskReasmntFolupRspns().clear();
					}*/
				}
			} else {
				reasmntRspn.getRiskReasmntFolupRspns().clear();
			}
		}
		/*
		 * Creating new follow-up entries in the table- else check if for the
		 * question, the answer selected has any follow-up options.
		 */
		else if (!CollectionUtils.isEmpty(followUpQstsnsList)) {
			for (SDMRiskReasmntFollowupDto sdmRiskReasmntFollowupDto : followUpQstsnsList) {
				// Check if any of the follow-up has been selected by the user
				if (!ObjectUtils.isEmpty(sdmRiskReasmntFollowupDto.getCdAnswerSelected())) {

					// Follow up dto code
					RiskReasmntFolupRspn riskReasmntFolupRspnsObject = new RiskReasmntFolupRspn();
					riskReasmntFolupRspnsObject
							.setIdRiskReasmntFolUpLookUp(sdmRiskReasmntFollowupDto.getIdFollowupLookup());
					riskReasmntFolupRspnsObject
							.setDtLastUpdate(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
					riskReasmntFolupRspnsObject.setIdCreatedPerson(sdmRiskReasmntDto.getIdUser());
					riskReasmntFolupRspnsObject.setIdLastUpdatePerson(sdmRiskReasmntDto.getIdUser());
					riskReasmntFolupRspnsObject.setIndRiskReasmntFollowup(ServiceConstants.Y);
					riskReasmntFolupRspnsObject.setIndPrimary(indPrimarySecondary);
					if(ServiceConstants.YES.equals(indPrimarySecondary)) {
						riskReasmntFolupRspnsObject.setTxtSubstncAbuseDesc(sdmRiskReasmntFollowupDto.getTxtSbstncAbusePrmry());
					}
					else if(ServiceConstants.N.equals(indPrimarySecondary)) {
						riskReasmntFolupRspnsObject.setTxtSubstncAbuseDesc(sdmRiskReasmntFollowupDto.getTxtSbstncAbuseSec());
					}
					riskReasmntFolupRspnsObject.setRiskReasmntRspn(reasmntRspn);
					riskReasmntFolupRspns.add(riskReasmntFolupRspnsObject);
				}

			}
		}
		return riskReasmntFolupRspns;
	}

	/**
	 * Method Name: getReunificationAssessmentList Method Description:This
	 * method is used to retrieve the SDM FSNA assessment details for a
	 * particular stage.
	 * 
	 * @param idStage
	 *            - The id of the stage.
	 * @return List<CpsFsnaDto> - This collection will hold the SDM FSNA
	 *         assessment details.
	 */
	@Override
	public List<CpsFsnaDto> getFSNAAssessmentList(Long idStage) {
		/*
		 * Query the SDM FSNA Assessment for getting the Household , Primary
		 * Care-giver and Secondary Care-giver
		 */
		@SuppressWarnings("unchecked")
		List<CpsFsna> fsnaAssessmentList = sessionFactory.getCurrentSession().createCriteria(CpsFsna.class)
				.add(Restrictions.eq("idStage", idStage)).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
		List<CpsFsnaDto> sdmFsnaList = new ArrayList<CpsFsnaDto>();
		if (!CollectionUtils.isEmpty(fsnaAssessmentList)) {
			fsnaAssessmentList.forEach(sdmFsna -> {
				CpsFsnaDto cpsFsnaDto = new CpsFsnaDto();
				BeanUtils.copyProperties(sdmFsna, cpsFsnaDto);
				sdmFsnaList.add(cpsFsnaDto);
			});

		}
		return sdmFsnaList;

	}

	/**
	 * Method Name: deleteEventPersonLink Method Description:This method is used
	 * to delete the event person link.
	 * 
	 * @param idEevent
	 *            - The id of the event.
	 */

	@Override
	public void deleteEventPersonLink(Long idEevent) {

		SQLQuery deleteQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(delEventPersonLink);
		deleteQuery.setParameter("eventId", idEevent);
		deleteQuery.executeUpdate();
	}

	private void processFollowUpSelectionForQstn6(RiskReasmntRspn response,
			SDMRiskReasmntQstnDto sdmRiskReasmntQstnDto) {
		// process the follow-up for question 6 if 6C or 6D was selected

		if (response.getCdRiskReasmntAnswr().charAt(0) == ServiceConstants.CHAR_SIX) {
			// If follow-up are present in the db for question 6
			if (!CollectionUtils.isEmpty(response.getRiskReasmntFolupRspns())) {
				for (RiskReasmntFolupRspn riskReasmntFolupRspn : response.getRiskReasmntFolupRspns()) {
					/*
					 * Filtering the list of followUpDto based on the
					 * idFollowUpLookUp which is also available in the
					 * RISK_REASMNT_FOLUP_RSPNS
					 */
					SDMRiskReasmntFollowupDto sdmRiskReasmntFollowupDto = new SDMRiskReasmntFollowupDto();
					if (ServiceConstants.N.equals(response.getIndPrmry())) {
						sdmRiskReasmntFollowupDto = sdmRiskReasmntQstnDto.getAnswers().stream()
								.flatMap(answer -> answer.getFollowupQuestionSec().stream()
										.filter(followUp -> followUp.getIdFollowupLookup()
												.equals(riskReasmntFolupRspn.getIdRiskReasmntFolUpLookUp())))
								.findFirst().orElse(null);
					} else {

						sdmRiskReasmntFollowupDto = sdmRiskReasmntQstnDto.getAnswers().stream()
								.flatMap(answer -> answer.getFollowupQuestions().stream()
										.filter(followUp -> followUp.getIdFollowupLookup()
												.equals(riskReasmntFolupRspn.getIdRiskReasmntFolUpLookUp())))
								.findFirst().orElse(null);

					}
					// If the followUpDto is found then updated the saved value
					// to the dto.
					if (!ObjectUtils.isEmpty(sdmRiskReasmntFollowupDto)) {
						RiskReasmntFlwupLookup riskReasmntFlwupLookup = (RiskReasmntFlwupLookup) sessionFactory
								.getCurrentSession()
								.get(RiskReasmntFlwupLookup.class, riskReasmntFolupRspn.getIdRiskReasmntFolUpLookUp());
						sdmRiskReasmntFollowupDto.setCdAnswerSelected(riskReasmntFlwupLookup.getCdFlwup());
						
						if (ANSWER_6C.equals(response.getCdRiskReasmntAnswr())) {
							String value = sdmRiskReasmntFollowupDto.getCdAnswerSelected().replaceAll(STRING_C,
									STRING_D);
							// sdmRiskReasmntFollowupDto.setCdAnswerSelected(value);
							if (ServiceConstants.YES.equals(riskReasmntFolupRspn.getIndPrimary())) {
								SDMRiskReasmntFollowupDto followUpDto = sdmRiskReasmntQstnDto.getAnswers().get(3)
										.getFollowupQuestions().stream()
										.filter(followUp -> value.equals(followUp.getCdFollowup())).findFirst()
										.orElse(null);
								followUpDto.setCdAnswerSelected(value);

								followUpDto.setTxtSbstncAbusePrmry(riskReasmntFolupRspn.getTxtSubstncAbuseDesc());
							} else if (ServiceConstants.N.equals(riskReasmntFolupRspn.getIndPrimary())) {
								SDMRiskReasmntFollowupDto followUpDto = sdmRiskReasmntQstnDto.getAnswers().get(3)
										.getFollowupQuestionSec().stream()
										.filter(followUp -> value.equals(followUp.getCdFollowup())).findFirst()
										.orElse(null);
								followUpDto.setTxtSbstncAbuseSec(riskReasmntFolupRspn.getTxtSubstncAbuseDesc());
								followUpDto.setCdAnswerSelected(value);
							}
						}
						else if (ANSWER_6D.equals(response.getCdRiskReasmntAnswr())){
							if (ServiceConstants.YES.equals(riskReasmntFolupRspn.getIndPrimary())) {
								sdmRiskReasmntFollowupDto.setTxtSbstncAbusePrmry(riskReasmntFolupRspn.getTxtSubstncAbuseDesc());
							}
							else if(ServiceConstants.N.equals(riskReasmntFolupRspn.getIndPrimary())) {
								sdmRiskReasmntFollowupDto.setTxtSbstncAbuseSec(riskReasmntFolupRspn.getTxtSubstncAbuseDesc());
							}
						}

					}
				}

			}
		}

	}
}
