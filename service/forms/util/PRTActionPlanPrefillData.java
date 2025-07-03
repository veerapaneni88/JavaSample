/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Apr 4, 2018- 5:05:45 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.forms.util;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.service.alternativeresponse.dto.EventValueDto;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.lookup.dao.LookupDao;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.prt.dto.PRTConnectionDto;
import us.tx.state.dfps.service.prt.dto.PRTEventLinkDto;
import us.tx.state.dfps.service.prt.dto.PRTParticipantDto;
import us.tx.state.dfps.service.prt.dto.PRTPermGoalDto;
import us.tx.state.dfps.service.prt.dto.PRTPermStatusLookupDto;
import us.tx.state.dfps.service.prt.dto.PRTPersonLinkDto;
import us.tx.state.dfps.service.prt.dto.PRTStrategyDto;
import us.tx.state.dfps.service.prt.dto.PRTTaskDto;
import us.tx.state.dfps.service.prt.dto.PRTTaskPersonLinkDto;
import us.tx.state.dfps.service.prt.service.PRTActionPlanService;
import us.tx.state.dfps.service.subcare.dto.PRTActionPlanDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;
import us.tx.state.dfps.service.workload.dto.StageDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:PRTActionPlanPrefillData will implemented returnPrefillData
 * operation defined in DocumentServiceUtil Interface to populate the
 * prefilldata for form prtcap. Apr 4, 2018- 5:05:45 PM © 2017 Texas Department
 * of Family and Protective Services
 */
@Component
public class PRTActionPlanPrefillData extends DocumentServiceUtil {

	@Autowired
	private LookupDao lookupDao;

	@Autowired
	private StageDao stage;

	@Autowired
	private PRTActionPlanService pRTActionPlanService;

	@Autowired
	PersonDao person;

	public static final String TRACE_TAG = "PrtActionPlanForm";
	public static final String EMPTY_STRING = "";
	public static final String COMMA_SPACE = ", ";
	public static final String SPACE = " ";
	public static final String PERIOD = ".";
	public static final String YEARS = "y";
	public static final String MONTHS = "m";
	public static final String YES = "Yes";
	public static final String NO = "No";
	public static final String NO_CONCURRENT_GOALS = "No Concurrent Goals";

	/**
	 * Method Description: This method is used to prefill the data from the
	 * different Dao by passing Dao output Dtos and bookmark and form group
	 * bookmark Dto as objects as input request
	 * 
	 * @param parentDtoobj
	 * @param bookmarkDtoObj
	 * @return PreFillData
	 */
	@Override
	public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {
		PRTActionPlanDto prtActionPlan = (PRTActionPlanDto) parentDtoobj;
		Long idEvent = prtActionPlan.getIdCurrentEvent();
		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
		if (null == prtActionPlan.getEventValueDto()) {
			prtActionPlan.setEventValueDto(new EventValueDto());
		}
		if (null == prtActionPlan.getChildren()) {
			prtActionPlan.setChildren(new ArrayList<PRTPersonLinkDto>());
		}
		if (null == prtActionPlan.getPrtStrategiesDto()) {
			prtActionPlan.setPrtStrategiesDto(new ArrayList<PRTStrategyDto>());
		}
		if (null == prtActionPlan.getPrtEventLinkDto()) {
			prtActionPlan.setPrtEventLinkDto(new ArrayList<PRTEventLinkDto>());
		}
		if (null == prtActionPlan.getPrtParticipantDto()) {
			prtActionPlan.setPrtParticipantDto(new ArrayList<PRTParticipantDto>());
		}
		if (null == prtActionPlan.getPrtPermStatusLookupDto()) {
			prtActionPlan.setPrtPermStatusLookupDto(new ArrayList<PRTPermStatusLookupDto>());
		}

		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();
		List<BookmarkDto> bookmarkNonFrmGrpList = new ArrayList<BookmarkDto>();

		if (!ObjectUtils.isEmpty(prtActionPlan.getIdCurrentCase())
				&& !ObjectUtils.isEmpty(prtActionPlan.getIdCurrentEvent())
				&& !ObjectUtils.isEmpty(prtActionPlan.getIdCurrentStage())) {
			StageDto stageName = stage.getStageById(prtActionPlan.getIdCurrentStage());
			BookmarkDto bookmarIdStage = createBookmark(BookmarkConstants.TITLE_STAGENAME, stageName.getNmStage());
			bookmarkNonFrmGrpList.add(bookmarIdStage);
			BookmarkDto bookmarIdCase = createBookmark(BookmarkConstants.TITLE_CASENUMBER,
					prtActionPlan.getIdCurrentCase());
			bookmarkNonFrmGrpList.add(bookmarIdCase);
			prtActionPlan = pRTActionPlanService.fetchActionPlan(prtActionPlan.getIdCurrentEvent(),
					prtActionPlan.getIdCurrentStage(), prtActionPlan.getIdCurrentCase());
		}

		if (!ObjectUtils.isEmpty(prtActionPlan.getUnitWorker())) {
			BookmarkDto bookmarkUnitNbr = createBookmark(BookmarkConstants.TITLE_UNITNUMBER,
					prtActionPlan.getUnitWorker());
			bookmarkNonFrmGrpList.add(bookmarkUnitNbr);
		}

		if (!ObjectUtils.isEmpty(prtActionPlan.getIdPrtActionPlan()) && 0 < prtActionPlan.getIdPrtActionPlan()) {
			if (!ObjectUtils.isEmpty(prtActionPlan.getDtComplete())) {
				PersonDto person1 = person.getPersonById(prtActionPlan.getIdLastUpdatePerson());
				StringBuilder prtCompletor = new StringBuilder();
				prtCompletor.append(person1.getNmPersonFirst());
				prtCompletor.append(SPACE);
				prtCompletor.append(person1.getNmPersonLast());
				BookmarkDto bookmarkCompBody = createBookmark(BookmarkConstants.TITLE_COMPLETEDBY,
						prtCompletor.toString());
				bookmarkNonFrmGrpList.add(bookmarkCompBody);
				String dateString = getMmmmDdYyyyDate(prtActionPlan.getDtComplete());
				BookmarkDto bookmarDtPrt = createBookmark(BookmarkConstants.TITLE_PRTDATE, dateString);
				bookmarkNonFrmGrpList.add(bookmarDtPrt);
			}
		}

		/** CHILD INFORMATION **/
		if (!ObjectUtils.isEmpty(prtActionPlan.getChildren()) && !idEvent.equals(0L)) {
			for (PRTPersonLinkDto child : prtActionPlan.getChildren()) {
				//List<FormDataGroupDto> formDataChildInfoList = new ArrayList<FormDataGroupDto>();
				FormDataGroupDto formDataChildInfoProg = createFormDataGroup(FormGroupsConstants.TMPLAT_CHILDINFO,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkChildInfoList = new ArrayList<BookmarkDto>();
				PersonDto person1 = person.getPersonById(child.getIdPerson());
				StringBuilder childName = new StringBuilder();
				childName.append(person1.getNmPersonFirst());
				if (!ObjectUtils.isEmpty(person1.getNmPersonMiddle())) {
					char[] cg1Name = person1.getNmPersonMiddle().toCharArray();
					childName.append(SPACE);
					childName.append(cg1Name[0]);
					childName.append(PERIOD);
				}
				childName.append(SPACE);
				childName.append(person1.getNmPersonLast());
				if (!ObjectUtils.isEmpty(person1.getCdPersonSuffix())) {
					childName.append(COMMA_SPACE);
					String cg1Sname = ServiceConstants.NULL_STRING;
					cg1Sname=person1.getCdPersonSuffix();
					childName.append(lookupDao.decode(CodesConstant.CSUFFIX, cg1Sname));
				}
				BookmarkDto bookmarkChildName = createBookmark(BookmarkConstants.CHILDINFO_NAME, childName.toString());
				bookmarkChildInfoList.add(bookmarkChildName);
				BookmarkDto bookmarChildDOB = createBookmark(BookmarkConstants.CHILDINFO_DOB, child.getDtBirth());
				bookmarkChildInfoList.add(bookmarChildDOB);
				StringBuilder childAge = new StringBuilder();
				if (!ObjectUtils.isEmpty(child.getDtBirth())) {
					Integer nbrAgeMonths = DateUtils.getAgeInMonths(child.getDtBirth(), new Date());
					childAge.append(nbrAgeMonths / 12);
					childAge.append(YEARS);
					childAge.append(nbrAgeMonths % 12);
					childAge.append(MONTHS);					
				}
				BookmarkDto bookmarkChildAge = createBookmark(BookmarkConstants.CHILDINFO_AGE, childAge.toString());
				bookmarkChildInfoList.add(bookmarkChildAge);
				BookmarkDto bookmarkInfoAge = createBookmark(BookmarkConstants.CHILDINFO_STATUS,
						child.getIdPrtPermStatusLookup());
				bookmarkChildInfoList.add(bookmarkInfoAge);
				BookmarkDto bookmarkPervPermStatus = createBookmark(BookmarkConstants.CHILDINFO_STATUS,
						child.getIdPrevPermStatus());
				bookmarkChildInfoList.add(bookmarkPervPermStatus);
				BookmarkDto bookmarkChildInfoDiss = createBookmarkWithCodesTable(BookmarkConstants.CHILDINFO_DISCUSSION,
						child.getCdLastPermDsc(), CodesConstant.CLPRMC);
				bookmarkChildInfoList.add(bookmarkChildInfoDiss);
				BookmarkDto bookmarkChildInfoType = createBookmarkWithCodesTable(BookmarkConstants.CHILDINFO_PTYPE,
						child.getCdPlcmtType(), CodesConstant.CPLMNTYP);
				bookmarkChildInfoList.add(bookmarkChildInfoType);
				BookmarkDto bookmarkChildPname = createBookmark(BookmarkConstants.CHILDINFO_PNAME,
						child.getNmPlacement());
				bookmarkChildInfoList.add(bookmarkChildPname);
				BookmarkDto bookmarkChildIntent = createBookmark(BookmarkConstants.CHILDINFO_INTENT,
						!DateUtils.isNull(child.getDtPlcmtEffectivePerm()) ? YES : NO);
				bookmarkChildInfoList.add(bookmarkChildIntent);
				formDataChildInfoProg.setBookmarkDtoList(bookmarkChildInfoList);
				formDataGroupList.add(formDataChildInfoProg);
			}
		}

		/** PRT CONNECTIONS FOR THE CHILD **/
		if (!ObjectUtils.isEmpty(prtActionPlan.getChildren()) && !idEvent.equals(0L)) {
			for (PRTPersonLinkDto child : prtActionPlan.getChildren()) {
				List<FormDataGroupDto> formDataConnectionsList = new ArrayList<FormDataGroupDto>();
				FormDataGroupDto formDataConnectionsProg = createFormDataGroup(FormGroupsConstants.TMPLAT_CONNECTION,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkConnectionsList = new ArrayList<BookmarkDto>();
				PersonDto child1 = person.getPersonById(child.getIdPerson());
				StringBuilder connectChildName = new StringBuilder();
				connectChildName.append(child1.getNmPersonFirst());
				connectChildName.append(SPACE);
				connectChildName.append(child1.getNmPersonLast());
				BookmarkDto bookmarkConnChildName = createBookmark(BookmarkConstants.CONNECTION_CHILDNAME,
						connectChildName.toString());
				bookmarkConnectionsList.add(bookmarkConnChildName);
				if (!ObjectUtils.isEmpty(child.getPrtConnectionValueDtoList())) {
					for (PRTConnectionDto prtConnectionDto : child.getPrtConnectionValueDtoList()) {
						if (!ObjectUtils.isEmpty(prtConnectionDto.getIdPrtConnection())
								&& 0 < prtConnectionDto.getIdPrtConnection()) {
							FormDataGroupDto formDataConnectionsRelProg = createFormDataGroup(
									FormGroupsConstants.TMPLAT_CONNECTION_RELATION,
									FormGroupsConstants.TMPLAT_CONNECTION);
							List<BookmarkDto> bookmarkConnectionsRelList = new ArrayList<BookmarkDto>();
							BookmarkDto bookmarkFullName = createBookmark(BookmarkConstants.CONNECTION_RELATION_NAME,
									prtConnectionDto.getNmPersonFull());
							bookmarkConnectionsRelList.add(bookmarkFullName);
							BookmarkDto bookmarkConnRelDecode = createBookmark(
									BookmarkConstants.CONNECTION_RELATION_RELINT,
									prtConnectionDto.getConnRellongDecode());
							bookmarkConnectionsRelList.add(bookmarkConnRelDecode);
							formDataConnectionsRelProg.setBookmarkDtoList(bookmarkConnectionsRelList);
							formDataConnectionsList.add(formDataConnectionsRelProg);
						}
					}
				}
				formDataConnectionsProg.setBookmarkDtoList(bookmarkConnectionsList);
				formDataConnectionsProg.setFormDataGroupList(formDataConnectionsList);
				formDataGroupList.add(formDataConnectionsProg);
			}
		}

		if (!idEvent.equals(0L)) {
			FormDataGroupDto formDataConnectionsProg = createFormDataGroup(
					FormGroupsConstants.TMPLAT_TXTADTNLCONNECTIONS, FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkConnectionsList = new ArrayList<BookmarkDto>();
			if (!ObjectUtils.isEmpty(prtActionPlan.getAdtnlConnections())) {
				BookmarkDto bookmarkAddConns = createBookmark(BookmarkConstants.TXTADTNLCONNECTIONS,
						prtActionPlan.getAdtnlConnections());
				bookmarkConnectionsList.add(bookmarkAddConns);
				formDataConnectionsProg.setBookmarkDtoList(bookmarkConnectionsList);				
			}
			formDataGroupList.add(formDataConnectionsProg);
		}
		// Moved the formatTextValue method to TypeConvUtil class for common
		// Implementation for Warranty defect 10906
		/** BRAINSTORMING QUESTIONS **/
		if (!idEvent.equals(0L)) {
			FormDataGroupDto formDataBrainStormProg = createFormDataGroup(FormGroupsConstants.TMPLAT_BRAINSTORMING,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkBrainStormList = new ArrayList<BookmarkDto>();
			if (!ObjectUtils.isEmpty(prtActionPlan.getBrsAchPerm())) {
				BookmarkDto bookmarkBrsAchPrem = createBookmark(BookmarkConstants.TXTBRSACHPERM,
						TypeConvUtil.formatTextValue(prtActionPlan.getBrsAchPerm()));
				bookmarkBrainStormList.add(bookmarkBrsAchPrem);
			}
			if (!ObjectUtils.isEmpty(prtActionPlan.getBrsTriedBefore())) {
				BookmarkDto bookmarkBrsTiredBefore = createBookmark(BookmarkConstants.TXTBRSTRIEDBEFORE,
						TypeConvUtil.formatTextValue(prtActionPlan.getBrsTriedBefore()));
				bookmarkBrainStormList.add(bookmarkBrsTiredBefore);
			}
			if (!ObjectUtils.isEmpty(prtActionPlan.getBrsNotTriedBefore())) {
				BookmarkDto bookmarkBrsNotTired = createBookmark(BookmarkConstants.TXTBRSNOTTRIEDBEFORE,
						TypeConvUtil.formatTextValue(prtActionPlan.getBrsNotTriedBefore()));
				bookmarkBrainStormList.add(bookmarkBrsNotTired);
			}
			if (!ObjectUtils.isEmpty(prtActionPlan.getBrsTryConcr())) {						
				BookmarkDto bookmarkBrsTryConcr = createBookmark(BookmarkConstants.TXTBRSTRYCONCR,
						TypeConvUtil.formatTextValue(prtActionPlan.getBrsTryConcr()));
				bookmarkBrainStormList.add(bookmarkBrsTryConcr);
			}
			if (!ObjectUtils.isEmpty(prtActionPlan.getBrsYthPlnPerm())) {
				BookmarkDto bookmarkBrsYthPln = createBookmark(BookmarkConstants.TXTBRSYTHPLNPERM,
						TypeConvUtil.formatTextValue(prtActionPlan.getBrsYthPlnPerm()));
				bookmarkBrainStormList.add(bookmarkBrsYthPln);
			}
			formDataBrainStormProg.setBookmarkDtoList(bookmarkBrainStormList);
			formDataGroupList.add(formDataBrainStormProg);
		}
		/** PERMANENCY GOALS AND RECOMMENDED PERMANENCY GOALS **/
		if (!ObjectUtils.isEmpty(prtActionPlan.getChildren()) && !idEvent.equals(0L)) {
			for (PRTPersonLinkDto child : prtActionPlan.getChildren()) {
				FormDataGroupDto formDataPermGoalsProg = createFormDataGroup(FormGroupsConstants.TMPLAT_CHILDPLAN,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkPermGoalsList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkChildPlan = createBookmark(BookmarkConstants.CHILDPLAN_CHILD,
						child.getNmPersonFull());
				bookmarkPermGoalsList.add(bookmarkChildPlan);
				String primaryGoal = ServiceConstants.NULL_STRING;
				// Goals.
				List<PRTPermGoalDto> currentChildPlanGoals = child.getPrtPermGoalValueDtoList();
				for (PRTPermGoalDto goal : currentChildPlanGoals) {
					if (CodesConstant.CPRMGLTY_10.equals(goal.getCdType())) {
						primaryGoal = goal.getCdGoal();
						break;
					}
				}
				BookmarkDto bookmarkPlnPermGoal = createBookmark(BookmarkConstants.CHILDPLAN_CDCSPPLANPERMGOAL,
						lookupDao.decode(CodesConstant.CCPPRMGL, primaryGoal));
				bookmarkPermGoalsList.add(bookmarkPlnPermGoal);
				if (!ObjectUtils.isEmpty(child.getIndNoConGoal()) && "Y".equalsIgnoreCase(child.getIndNoConGoal())) {
					BookmarkDto bookmarkPlanConGoal = createBookmark(BookmarkConstants.CHILDPLAN_CDCSPPLANCONCGOAL,
							NO_CONCURRENT_GOALS);
					bookmarkPermGoalsList.add(bookmarkPlanConGoal);
				} else {
					StringBuilder concurrentGoals = new StringBuilder();
					// Goals.
					for (PRTPermGoalDto goal : child.getPrtPermGoalValueDtoList()) {
						if (CodesConstant.CPRMGLTY_20.equals(goal.getCdType())) {
							String goalDecode = lookupDao.decode(CodesConstant.CCPPRMGL, goal.getCdGoal());
							concurrentGoals.append(goalDecode).append(",");
						}
					}
					// Remove Last comma..
					if (concurrentGoals.length() > 0) {
						concurrentGoals.setLength(concurrentGoals.length() - 1);
					}
					BookmarkDto bookmarkPlanConGoal2 = createBookmark(BookmarkConstants.CHILDPLAN_CDCSPPLANCONCGOAL,
							concurrentGoals.toString());
					bookmarkPermGoalsList.add(bookmarkPlanConGoal2);
				}
				formDataPermGoalsProg.setBookmarkDtoList(bookmarkPermGoalsList);
				formDataGroupList.add(formDataPermGoalsProg);
			}
		}

		/** RECOMMENDED PERMANENCY GOALS **/
		if (!ObjectUtils.isEmpty(prtActionPlan.getChildren()) && !idEvent.equals(0L)) {
			for (PRTPersonLinkDto child : prtActionPlan.getChildren()) {
				FormDataGroupDto formDataRecPermGoalsProg = createFormDataGroup(FormGroupsConstants.TMPLAT_PERSONLINK,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkRecPermGoalsList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkPlanConGoal2 = createBookmark(BookmarkConstants.PERSONLINK_CHILD,
						child.getNmPersonFull());
				bookmarkRecPermGoalsList.add(bookmarkPlanConGoal2);
				BookmarkDto bookmarkPrimGoal = createBookmarkWithCodesTable(BookmarkConstants.PERSONLINK_CDPRIMARYGOAL,
						child.getCdRcmndPrimaryGoal(), CodesConstant.CCPPRMGL);
				bookmarkRecPermGoalsList.add(bookmarkPrimGoal);
				BookmarkDto bookmarkCurrentGoal = createBookmarkWithCodesTable(
						BookmarkConstants.PERSONLINK_CDCONCURRENTGOAL, child.getCdRcmndConcurrentGoal(),
						CodesConstant.CCPPRMGL);
				bookmarkRecPermGoalsList.add(bookmarkCurrentGoal);
				formDataRecPermGoalsProg.setBookmarkDtoList(bookmarkRecPermGoalsList);
				formDataGroupList.add(formDataRecPermGoalsProg);
			}
		}

		/** STRATEGIES AND TASKS **/
		if (!ObjectUtils.isEmpty(prtActionPlan.getPrtStrategiesDto()) && !idEvent.equals(0L)) {
			for (PRTStrategyDto strategyList : prtActionPlan.getPrtStrategiesDto()) {
				List<FormDataGroupDto> formDataStrategyList = new ArrayList<FormDataGroupDto>();
				FormDataGroupDto formDataStrategyProg = createFormDataGroup(FormGroupsConstants.TMPLAT_STRATEGY,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkStrategyList = new ArrayList<BookmarkDto>();
				if (CodesConstant.CPRTSTR_080.equalsIgnoreCase(strategyList.getCdStrategy())) {
					BookmarkDto bookmarkCDStrategy = createBookmark(BookmarkConstants.STRATEGY_CDSTRATEGY,
							strategyList.getTxtOtherDesc());
					bookmarkStrategyList.add(bookmarkCDStrategy);
				} else {
					BookmarkDto bookmarkCDStrategy2 = createBookmarkWithCodesTable(
							BookmarkConstants.STRATEGY_CDSTRATEGY, strategyList.getCdStrategy(), CodesConstant.CPRTSTR);
					bookmarkStrategyList.add(bookmarkCDStrategy2);
				}
				for (PRTTaskDto task : strategyList.getPrtTaskValueDtoList()) {
					FormDataGroupDto formDataStratTasksProg = createFormDataGroup(
							FormGroupsConstants.TMPLAT_STRATEGY_TASK, FormGroupsConstants.TMPLAT_STRATEGY);
					List<BookmarkDto> bookmarkStratTasksList = new ArrayList<BookmarkDto>();
					BookmarkDto bookmarkCDStrategy = createBookmark(BookmarkConstants.STRATEGY_TASK_DESC,
							task.getTxtDesc());
					bookmarkStratTasksList.add(bookmarkCDStrategy);
					if (!ObjectUtils.isEmpty(task.getDtTargetComplete())) {
						String dateString = getMmmmDdYyyyDate(task.getDtTargetComplete());
						BookmarkDto bookmarkdtTargComp = createBookmark(
								BookmarkConstants.STRATEGY_TASK_DTTARGETCOMPLETE, dateString);
						bookmarkStratTasksList.add(bookmarkdtTargComp);
					}
					BookmarkDto bookmarkCDBarrier = createBookmarkWithCodesTable(
							BookmarkConstants.STRATEGY_TASK_CDBARRIER, task.getCdBarrier(), CodesConstant.CPRTBRS);
					bookmarkStratTasksList.add(bookmarkCDBarrier);
					BookmarkDto bookmarkPlnOverComes = createBookmark(BookmarkConstants.STRATEGY_TASK_TXTPLANOVRCMBRS,
							task.getTxtPlanOvrcmBrs());
					bookmarkStratTasksList.add(bookmarkPlnOverComes);
					if (!ObjectUtils.isEmpty(task.getIdPersonAssigned()) && task.getIdPersonAssigned() > 0) {
						PersonDto taskName = person.getPersonById(task.getIdPersonAssigned());
						StringBuilder taskPersonName = new StringBuilder();
						taskPersonName.append(taskName.getNmPersonFirst());
						taskPersonName.append(SPACE);
						taskPersonName.append(taskName.getNmPersonLast());
						BookmarkDto bookmarkTaskPersonName = createBookmark(BookmarkConstants.STRATEGY_TASK_PERSON,
								taskPersonName.toString());
						bookmarkStratTasksList.add(bookmarkTaskPersonName);
					} else {
						BookmarkDto bookmarkTaskPerson = createBookmarkWithCodesTable(
								BookmarkConstants.STRATEGY_TASK_PERSON, task.getCdAssignedToType(),
								CodesConstant.CPRTBRS);
						bookmarkStratTasksList.add(bookmarkTaskPerson);
					}
					boolean needsComma = false;
					StringBuffer sb = new StringBuffer();
					for (PRTTaskPersonLinkDto taskPersonList : task.getPrtTaskPersonLinkValueDtoList()) {
						for (PRTPersonLinkDto taskChild : prtActionPlan.getChildren()) {
							if (taskChild.getIdPrtPersonLink().equals(taskPersonList.getIdPrtPersonLink())) {
								if (needsComma) {
									sb.append(COMMA_SPACE);
								}
								needsComma = true;
								PersonDto taskChildBean = person.getPersonById(taskChild.getIdPerson());
								sb.append(taskChildBean.getNmPersonFirst());
								sb.append(SPACE);
								sb.append(taskChildBean.getNmPersonLast());
							}
						}
					}
					BookmarkDto bookmarkTaskPersonName = createBookmark(BookmarkConstants.STRATEGY_TASK_CHILDREN,
							sb.toString());
					bookmarkStratTasksList.add(bookmarkTaskPersonName);
					formDataStratTasksProg.setBookmarkDtoList(bookmarkStratTasksList);
					formDataStrategyList.add(formDataStratTasksProg);

				}
				
				formDataStrategyProg.setBookmarkDtoList(bookmarkStrategyList);
				formDataStrategyProg.setFormDataGroupList(formDataStrategyList);
				formDataGroupList.add(formDataStrategyProg);
			}
		}

		/** DEBRIEF OF ROUNDTABLE **/
		if (!idEvent.equals(0L)) {
			FormDataGroupDto formDataRoundTableProg = createFormDataGroup(FormGroupsConstants.TMPLAT_DEBRIEFQUESTIONS,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkRoundTableList = new ArrayList<BookmarkDto>();
			if (!ObjectUtils.isEmpty(prtActionPlan.getDbrfExplToFam())) {
				BookmarkDto bookmarkDbrfRecom = createBookmark(BookmarkConstants.TXTDBRFRECOMEND,
						TypeConvUtil.formatTextValue(prtActionPlan.getDbrfRecomend()));
				bookmarkRoundTableList.add(bookmarkDbrfRecom);
			}
			if (!ObjectUtils.isEmpty(prtActionPlan.getDbrfRecomend())) {
				BookmarkDto bookmarkDbrfExpl = createBookmark(BookmarkConstants.TXTDBRFEXPLTOFAM,
						TypeConvUtil.formatTextValue(prtActionPlan.getDbrfExplToFam()));
				bookmarkRoundTableList.add(bookmarkDbrfExpl);
			}
			if (!ObjectUtils.isEmpty(prtActionPlan.getDbrfUnanswrQstn())) {
				BookmarkDto bookmarkDbrfUnanswr = createBookmark(BookmarkConstants.TXTDBRFUNANSWRQSTN,
						TypeConvUtil.formatTextValue(prtActionPlan.getDbrfUnanswrQstn()));
				bookmarkRoundTableList.add(bookmarkDbrfUnanswr);
			}
			if (!ObjectUtils.isEmpty(prtActionPlan.getDbrfOthrCase())) {
				BookmarkDto bookmarkDbrfOthr = createBookmark(BookmarkConstants.TXTDBRFOTHRCASE,
						TypeConvUtil.formatTextValue(prtActionPlan.getDbrfOthrCase()));
				bookmarkRoundTableList.add(bookmarkDbrfOthr);
			}
			formDataRoundTableProg.setBookmarkDtoList(bookmarkRoundTableList);
			formDataGroupList.add(formDataRoundTableProg);
		}

		/** PARTICIPANT DOCUMENTATION **/
		if (!ObjectUtils.isEmpty(prtActionPlan.getPrtParticipantDto()) && !idEvent.equals(0L)) {
			for (PRTParticipantDto participant : prtActionPlan.getPrtParticipantDto()) {
				FormDataGroupDto formDataParticipantProg = createFormDataGroup(FormGroupsConstants.TMPLAT_PARTICIPANT,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkParticipantList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkPartRole = createBookmarkWithCodesTable(BookmarkConstants.PARTICIPANT_CDPRTROLE,
						participant.getCdPrtRole(), CodesConstant.CPRTROLE);
				bookmarkParticipantList.add(bookmarkPartRole);
				BookmarkDto bookmarkPartName = createBookmark(BookmarkConstants.PARTICIPANT_NAME,
						participant.getNmPersonFull());
				bookmarkParticipantList.add(bookmarkPartName);
				BookmarkDto bookmarkPartPhn = createBookmark(BookmarkConstants.PARTICIPANT_PHONE,
						getPhoneWithFormat(participant));
				bookmarkParticipantList.add(bookmarkPartPhn);
				BookmarkDto bookmarkPartEmail = createBookmark(BookmarkConstants.PARTICIPANT_EMAIL,
						participant.getStrEmail());
				bookmarkParticipantList.add(bookmarkPartEmail);
				formDataParticipantProg.setBookmarkDtoList(bookmarkParticipantList);
				formDataGroupList.add(formDataParticipantProg);
			}
		}
		if (!ObjectUtils.isEmpty(prtActionPlan.getPrtParticipantDto()) && !idEvent.equals(0L)) {
			for (PRTParticipantDto signatory : prtActionPlan.getPrtParticipantDto()) {
				FormDataGroupDto formDataSignatoryProg = createFormDataGroup(FormGroupsConstants.TMPLAT_SIGNATURES,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkSignatoryList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkSigPartRole = createBookmarkWithCodesTable(BookmarkConstants.SIGNATURES_CDPRTROLE,
						signatory.getCdPrtRole(), CodesConstant.CPRTROLE);
				bookmarkSignatoryList.add(bookmarkSigPartRole);
				BookmarkDto bookmarkSigName = createBookmark(BookmarkConstants.SIGNATURES_NAME,
						signatory.getNmPersonFull());
				bookmarkSignatoryList.add(bookmarkSigName);
				BookmarkDto bookmarkSigPhn = createBookmark(BookmarkConstants.SIGNATURES_PHONE,
						getPhoneWithFormat(signatory));
				bookmarkSignatoryList.add(bookmarkSigPhn);
				BookmarkDto bookmarkSigEmail = createBookmark(BookmarkConstants.SIGNATURES_EMAIL,
						signatory.getStrEmail());
				bookmarkSignatoryList.add(bookmarkSigEmail);
				formDataSignatoryProg.setBookmarkDtoList(bookmarkSignatoryList);
				formDataGroupList.add(formDataSignatoryProg);
			}
		}

		
		preFillData.setFormDataGroupList(formDataGroupList);
		preFillData.setBookmarkDtoList(bookmarkNonFrmGrpList);
	
	return preFillData;
	}

	/**
	 * This method gets a date and returns a formatted date string
	 * 
	 */
	private String getMmmmDdYyyyDate(Date date) {
		SimpleDateFormat sdfDestination = new SimpleDateFormat("MMMM dd, yyyy");
		String dateString = sdfDestination.format(date);
		return dateString;
	}

	/**
	 * This method uses a PRTParticipantValueBean to return a formatted phone
	 * string
	 * 
	 */
	private String getPhoneWithFormat(PRTParticipantDto participant) {
		// declare a phone format of 3-3-4
		MessageFormat phoneMsgFmt = new MessageFormat("({0}) {1}-{2}");
		String phoneString = ""; // SIR if phone is null return empty string
		if (null != participant.getNbrPersonPhone()) {
			String[] participantPhoneNumArr = { participant.getNbrPersonPhone().substring(0, 3),
					participant.getNbrPersonPhone().substring(3, 6), participant.getNbrPersonPhone().substring(6) };
			phoneString = phoneMsgFmt.format(participantPhoneNumArr);
		}
		return phoneString;
	}
}
