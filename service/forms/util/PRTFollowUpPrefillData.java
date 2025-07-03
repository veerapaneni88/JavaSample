/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Mar 30, 2018- 5:34:42 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.forms.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import us.tx.state.dfps.service.alternativeresponse.dto.EventValueDto;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.lookup.dao.LookupDao;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.prt.dto.PRTConnectionDto;
import us.tx.state.dfps.service.prt.dto.PRTEventLinkDto;
import us.tx.state.dfps.service.prt.dto.PRTPermGoalDto;
import us.tx.state.dfps.service.prt.dto.PRTPermStatusLookupDto;
import us.tx.state.dfps.service.prt.dto.PRTPersonLinkDto;
import us.tx.state.dfps.service.prt.dto.PRTPersonLinkDto.ChildFollowUpStatus;
import us.tx.state.dfps.service.prt.dto.PRTStrategyDto;
import us.tx.state.dfps.service.prt.dto.PRTTaskDto;
import us.tx.state.dfps.service.prt.dto.PRTTaskPersonLinkDto;
import us.tx.state.dfps.service.prt.service.PRTActPlanFollowUpService;
import us.tx.state.dfps.service.prt.service.PRTActionPlanService;
import us.tx.state.dfps.service.subcare.dto.PRTActPlanFollowUpDto;
import us.tx.state.dfps.service.subcare.dto.PRTActionPlanDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;
import us.tx.state.dfps.service.workload.dto.StageDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * PRTFollowUpPrefillData will implemented returnPrefillData operation defined
 * in DocumentServiceUtil Interface to populate the prefilldata for form prtcfu.
 * Mar 30, 2018- 5:34:42 PM © 2017 Texas Department of Family and Protective
 * Services
 */
@Component
public class PRTFollowUpPrefillData extends DocumentServiceUtil {

	public static final String TRACE_TAG = "PrtFollowUpForm";
	public static final String EMPTY_STRING = "";
	public static final String COMMA_SPACE = ", ";
	public static final String SPACE = " ";
	public static final String PERIOD = ".";
	public static final String YEARS = "y";
	public static final String MONTHS = "m";
	public static final String YES = "Yes";
	public static final String NO = "No";
	public static final String BIANNUAL = "30";
	public static final String NO_CONCURRENT_GOAL = "No Concurrent Goal";

	@Autowired
	private StageDao stage;

	@Autowired
	private LookupDao lookupDao;

	@Autowired
	private PRTActionPlanService pRTActionPlanService;

	@Autowired
	PRTActPlanFollowUpService prtActPlanFollowUpService;

	@Autowired
	PersonDao person;

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
		PRTActPlanFollowUpDto prtFollowUp = (PRTActPlanFollowUpDto) parentDtoobj;

		if (null == prtFollowUp.getFollowUpEvent()) {
			prtFollowUp.setFollowUpEvent(new EventValueDto());
		}
		if (null == prtFollowUp.getChildren()) {
			prtFollowUp.setChildren(new ArrayList<PRTPersonLinkDto>());
		}
		if (null == prtFollowUp.getStrategies()) {
			prtFollowUp.setStrategies(new ArrayList<PRTStrategyDto>());
		}
		if (null == prtFollowUp.getFollowUpEvtLinks()) {
			prtFollowUp.setFollowUpEvtLinks(new ArrayList<PRTEventLinkDto>());
		}
		if (null == prtFollowUp.getPermStatusList()) {
			prtFollowUp.setPermStatusList(new ArrayList<PRTPermStatusLookupDto>());
		}

		Long connectionsAP = 0l;
		Long connectionsFU = 0l;
		Long connectionsAdded = 0l;
		SimpleDateFormat sdfTasks = new SimpleDateFormat("MM/dd/yyyy");
		SimpleDateFormat sdfDtFuComp = new SimpleDateFormat("MMMM dd, yyyy");
		SimpleDateFormat sdfDtExit = new SimpleDateFormat("yyyy-MM-dd");
		boolean exitingPRT = false;
		boolean exitedPRT = false;
		boolean connGroup = false;
		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();
		List<BookmarkDto> bookmarkNonFrmGrpList = new ArrayList<BookmarkDto>();
		PRTActionPlanDto actionPlan = new PRTActionPlanDto();
		PRTPersonLinkDto personLink = new PRTPersonLinkDto();
		Long idEvent = prtFollowUp.getIdCurrentEvent();

		if (!ObjectUtils.isEmpty(prtFollowUp.getIdCurrentCase())
				&& !ObjectUtils.isEmpty(prtFollowUp.getIdCurrentEvent())
				&& !ObjectUtils.isEmpty(prtFollowUp.getIdCurrentStage())) {
			StageDto stageName = stage.getStageById(prtFollowUp.getIdCurrentStage());
			BookmarkDto bookmarIdStage = createBookmark(BookmarkConstants.TITLE_STAGENAME, stageName.getNmStage());
			bookmarkNonFrmGrpList.add(bookmarIdStage);
			BookmarkDto bookmarIdCase = createBookmark(BookmarkConstants.TITLE_CASENUMBER,
					prtFollowUp.getIdCurrentCase());
			bookmarkNonFrmGrpList.add(bookmarIdCase);
			prtFollowUp = prtActPlanFollowUpService.fetchActPlanFollowUp(prtFollowUp.getIdCurrentEvent(),
					prtFollowUp.getIdCurrentStage(), prtFollowUp.getIdCurrentCase());
		}

		BookmarkDto bookmarFUType = createBookmark(BookmarkConstants.TITLE_FUTYPE,
				lookupDao.decode(CodesConstant.CPRTFLTY, prtFollowUp.getCdType()));
		bookmarkNonFrmGrpList.add(bookmarFUType);
		BookmarkDto bookmarFUMonth = createBookmark(BookmarkConstants.TITLE_FUMONTH, prtFollowUp.getFollowupMonth());
		bookmarkNonFrmGrpList.add(bookmarFUMonth);
		BookmarkDto bookmarUntNbr = createBookmark(BookmarkConstants.TITLE_UNITNUMBER, prtFollowUp.getUnitWorker());
		bookmarkNonFrmGrpList.add(bookmarUntNbr);

		// get action plan bean for connection calculations
		if (!ObjectUtils.isEmpty(prtFollowUp.getIdPrtActionPlan()) && 0 < prtFollowUp.getIdPrtActionPlan()) {
			actionPlan = pRTActionPlanService.fetchActionPlan(prtFollowUp.getIdPrtActionPlan());
		}
		if (!ObjectUtils.isEmpty(prtFollowUp.getIdPrtActplnFollowup()) && 0 < prtFollowUp.getIdPrtActplnFollowup()) {
			// Get the Person EJB for multiple uses.
			if (!ObjectUtils.isEmpty(prtFollowUp.getDtComplete())) {
				PersonDto person1 = person.getPersonById(prtFollowUp.getIdLastUpdatePerson());
				StringBuilder prtCompletor = new StringBuilder();
				prtCompletor.append(person1.getNmPersonFirst());
				prtCompletor.append(SPACE);
				prtCompletor.append(person1.getNmPersonLast());
				BookmarkDto bookmarkCompBody = createBookmark(BookmarkConstants.TITLE_COMPLETEDBY,
						prtCompletor.toString());
				bookmarkNonFrmGrpList.add(bookmarkCompBody);
				BookmarkDto bookmarDtPrt = createBookmark(BookmarkConstants.TITLE_PRTDATE,
						sdfDtFuComp.format(prtFollowUp.getDtComplete()));
				bookmarkNonFrmGrpList.add(bookmarDtPrt);
			}
		}

		/** CHILD INFORMATION **/
		if (!ObjectUtils.isEmpty(prtFollowUp.getChildren()) && !idEvent.equals(0L)) {
			// get child list from action plan for connections added
			// calculations
			for (PRTPersonLinkDto child : prtFollowUp.getChildren()) {
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
					cg1Sname.equalsIgnoreCase(person1.getCdPersonSuffix());
					childName.append(lookupDao.decode(CodesConstant.CSUFFIX, cg1Sname));
				}
				BookmarkDto bookmarkChildName = createBookmark(BookmarkConstants.CHILDINFO_NAME, childName.toString());
				bookmarkChildInfoList.add(bookmarkChildName);
				BookmarkDto bookmarChildDOB = createBookmark(BookmarkConstants.CHILDINFO_DOB,
						sdfDtExit.format(child.getDtBirth()));
				bookmarkChildInfoList.add(bookmarChildDOB);
				StringBuilder childAge = new StringBuilder();
				if (!ObjectUtils.isEmpty(child.getDtBirth())) {
					Integer x = DateUtils.getAgeInMonths(child.getDtBirth(), new Date());
					Long nbrAgeMonths = x.longValue();
					childAge.append(nbrAgeMonths / 12);
					childAge.append(YEARS);
					childAge.append(nbrAgeMonths % 12);
					childAge.append(MONTHS);
					BookmarkDto bookmarkChildAge = createBookmark(BookmarkConstants.CHILDINFO_AGE, childAge.toString());
					bookmarkChildInfoList.add(bookmarkChildAge);
				}
				if (!ObjectUtils.isEmpty(child.getFollowUpStatus())) {
					personLink.getFollowUpStatus();
					exitedPRT = (child.getFollowUpStatus().equals(ChildFollowUpStatus.APFU_AFTER_CHILDEXITEDPRT));
					personLink.getFollowUpStatus();
					exitingPRT = (child.getFollowUpStatus().equals(ChildFollowUpStatus.APFU_INWHICH_CHILDEXITEDPRT));
				}

				if (!ObjectUtils.isEmpty(child.getIdPrtPermStatusLookup()) && child.getIdPrtPermStatusLookup() > 0
						&& !exitedPRT) {
					BookmarkDto bookmarkInfoAge = createBookmark(BookmarkConstants.CHILDINFO_STATUS,
							child.getIdPrtPermStatusLookup());
					bookmarkChildInfoList.add(bookmarkInfoAge);
				} else if (!exitedPRT) {
					BookmarkDto bookmarkPervPermStatus = createBookmark(BookmarkConstants.CHILDINFO_STATUS,
							child.getIdPrevPermStatus());
					bookmarkChildInfoList.add(bookmarkPervPermStatus);
				}
				BookmarkDto bookmarkPervPermStatus = createBookmarkWithCodesTable(BookmarkConstants.CHILDINFO_STATUS,
						child.getCdLastPermDsc(), CodesConstant.CLPRMC);
				bookmarkChildInfoList.add(bookmarkPervPermStatus);
				if (exitedPRT || exitingPRT) {
					BookmarkDto bookmarkDtExit = createBookmark(BookmarkConstants.CHILDINFO_EXIT_DATE,
							sdfDtExit.format(child.getDtPrtExit()));
					bookmarkChildInfoList.add(bookmarkDtExit);
					BookmarkDto bookmarkExtRsn = createBookmark(BookmarkConstants.CHILDINFO_EXIT_REASON,
							lookupDao.decode(CodesConstant.CPRTEXRN, child.getCdExitReason()));
					bookmarkChildInfoList.add(bookmarkExtRsn);
				}
				if (!exitedPRT) {
					BookmarkDto bookmarkChildInfoType = createBookmarkWithCodesTable(BookmarkConstants.CHILDINFO_PTYPE,
							child.getCdPlcmtType(), CodesConstant.CPLMNTYP);
					bookmarkChildInfoList.add(bookmarkChildInfoType);
					BookmarkDto bookmarkChildPname = createBookmark(BookmarkConstants.CHILDINFO_PNAME,
							child.getNmPlacement());
					bookmarkChildInfoList.add(bookmarkChildPname);
					BookmarkDto bookmarkChildIntent = createBookmark(BookmarkConstants.CHILDINFO_INTENT,
							!DateUtils.isNull(child.getDtPlcmtEffectivePerm()) ? YES : NO);
					bookmarkChildInfoList.add(bookmarkChildIntent);
				}
				formDataChildInfoProg.setBookmarkDtoList(bookmarkChildInfoList);
				formDataGroupList.add(formDataChildInfoProg);
			}
		}

		/** PRT CONNECTIONS FOR THE CHILD **/
		if (!ObjectUtils.isEmpty(prtFollowUp.getChildren()) && !idEvent.equals(0L)) {
			for (PRTPersonLinkDto child : prtFollowUp.getChildren()) {
				if (!ObjectUtils.isEmpty(child.getFollowUpStatus())) {
					personLink.getFollowUpStatus();
					exitedPRT = (child.getFollowUpStatus().equals(ChildFollowUpStatus.APFU_AFTER_CHILDEXITEDPRT));
				}
				if (!exitedPRT) {
					List<FormDataGroupDto> formDataConnectionsList = new ArrayList<FormDataGroupDto>();
					FormDataGroupDto formDataConnectionsProg = createFormDataGroup(
							FormGroupsConstants.TMPLAT_CONNECTION, FormConstants.EMPTY_STRING);
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
						connectionsFU = 0l;
						connectionsAdded = 0l;
						for (PRTConnectionDto prtConnectionDto : child.getPrtConnectionValueDtoList()) {
							FormDataGroupDto formDataConnectionsRelProg = createFormDataGroup(
									FormGroupsConstants.TMPLAT_CONNECTION_RELATION,
									FormGroupsConstants.TMPLAT_CONNECTION);
							List<BookmarkDto> bookmarkConnectionsRelList = new ArrayList<BookmarkDto>();
							PRTConnectionDto connection = prtConnectionDto;
							if (!ObjectUtils.isEmpty(connection) && connection.isSelectedByUser()) {
								BookmarkDto bookmarkFullName = createBookmark(
										BookmarkConstants.CONNECTION_RELATION_NAME, connection.getNmPersonFull());
								bookmarkConnectionsRelList.add(bookmarkFullName);
								BookmarkDto bookmarkConnRelDecode = createBookmark(
										BookmarkConstants.CONNECTION_RELATION_RELINT,
										connection.getConnRellongDecode());
								bookmarkConnectionsRelList.add(bookmarkConnRelDecode);
								BookmarkDto bookmarkConnIdPerson = createBookmark(
										BookmarkConstants.CONNECTION_RELATION_PID, connection.getIdPerson());
								bookmarkConnectionsRelList.add(bookmarkConnIdPerson);
								connectionsFU++;
							}
							connectionsAdded.equals(connection.getNbrOfConnAdd());
							formDataConnectionsRelProg.setBookmarkDtoList(bookmarkConnectionsRelList);
							formDataConnectionsList.add(formDataConnectionsRelProg);
						}
					}
					if (!ObjectUtils.isEmpty(prtFollowUp.getChildren())) {
						for (PRTPersonLinkDto connChild : prtFollowUp.getChildren()) {
							for (PRTPersonLinkDto connChildAP : actionPlan.getChildren()) {
								if (connChild.getIdPerson().equals(connChildAP.getIdPerson())) {
									connectionsAP = 0l;
									for (PRTConnectionDto prtConnectionsAP : connChildAP
											.getPrtConnectionValueDtoList()) {
										if (!ObjectUtils.isEmpty(prtConnectionsAP.getIdPrtConnection())
												|| !ObjectUtils.isEmpty(prtConnectionsAP.getIdSrcPersonLink())) {
											if (prtConnectionsAP.getIdPrtConnection() > 0
													|| prtConnectionsAP.getIdSrcPersonLink() > 0) {
												connectionsAP++;
											}
										}
									}
								}
							}
						}
					}
					if (connectionsAdded > 0) {
						BookmarkDto bookmarkConnAdded = createBookmark(BookmarkConstants.CONNECTIONS_ADDED,
								connectionsAdded);
						bookmarkConnectionsList.add(bookmarkConnAdded);
					} else {
						BookmarkDto bookmarkConnAdded2 = createBookmark(BookmarkConstants.CONNECTIONS_ADDED,
								connectionsFU - connectionsAP);
						bookmarkConnectionsList.add(bookmarkConnAdded2);
						connGroup = true;
					}
					formDataConnectionsProg.setBookmarkDtoList(bookmarkConnectionsList);
					formDataConnectionsProg.setFormDataGroupList(formDataConnectionsList);
					;
					formDataGroupList.add(formDataConnectionsProg);
				}
			}
		}

		if (connGroup) {
			FormDataGroupDto formDataChangedGrpProg = createFormDataGroup(FormGroupsConstants.TMPLAT_CIRC_CHANGED,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkChangedGrpList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkChangedGrp = createBookmark(BookmarkConstants.TXTCONNECTIONSCHANGED,
					prtFollowUp.getAdditionalConnections(), formDataChangedGrpProg);
			bookmarkChangedGrpList.add(bookmarkChangedGrp);
			formDataChangedGrpProg.setBookmarkDtoList(bookmarkChangedGrpList);
			formDataGroupList.add(formDataChangedGrpProg);
		}

		/** PERMANENCY GOALS **/
		if (!ObjectUtils.isEmpty(prtFollowUp.getChildren()) && !idEvent.equals(0L)) {
			for (PRTPersonLinkDto child : prtFollowUp.getChildren()) {
				if (!ObjectUtils.isEmpty(child.getFollowUpStatus())) {
					personLink.getFollowUpStatus();
					exitedPRT = (child.getFollowUpStatus().equals(ChildFollowUpStatus.APFU_AFTER_CHILDEXITEDPRT));
				}
				if (!exitedPRT) {
					FormDataGroupDto formDataPermGoalsProg = createFormDataGroup(FormGroupsConstants.TMPLAT_CHILDPLAN,
							FormConstants.EMPTY_STRING);
					List<BookmarkDto> bookmarkPermGoalsList = new ArrayList<BookmarkDto>();
					BookmarkDto bookmarkChildPlan = createBookmark(BookmarkConstants.CHILDPLAN_CHILD,
							child.getNmPersonFull());
					bookmarkPermGoalsList.add(bookmarkChildPlan);
					String primaryGoal = ServiceConstants.EMPTY_STRING;
					List<PRTPermGoalDto> currentChildPlanGoals = child.getPrtPermGoalValueDtoList();
					if (!CollectionUtils.isEmpty(currentChildPlanGoals)) {
						// Get the primary goal for a child
						primaryGoal = currentChildPlanGoals.stream()
								.filter(goal -> CodesConstant.CPRMGLTY_10.equals(goal.getCdType())).findFirst().get()
								.getCdGoal();
					}
					BookmarkDto bookmarkChildPan2 = createBookmarkWithCodesTable(BookmarkConstants.CHILDPLAN_CDCSPPLANPERMGOAL,
							primaryGoal, CodesConstant.CCPPRMGL);
					bookmarkPermGoalsList.add(bookmarkChildPan2);
					// Goals.
					StringBuilder concurrentGoals = new StringBuilder();
					for (PRTPermGoalDto goal : child.getPrtPermGoalValueDtoList()) {
						if (CodesConstant.CPRMGLTY_20.equals(goal.getCdType())) {
							String goalDecode = goal.getCdGoal();
							concurrentGoals.append(goalDecode);
						}
					}					
					if (!StringUtils.isEmpty(concurrentGoals)) {
						BookmarkDto bookmarkConGoal = createBookmarkWithCodesTable(BookmarkConstants.CHILDPLAN_CDCSPPLANCONCGOAL,
								concurrentGoals.toString(), CodesConstant.CCPPRMGL);
						bookmarkPermGoalsList.add(bookmarkConGoal);
					} else {
						BookmarkDto bookmarkNoConGoal = createBookmark(BookmarkConstants.CHILDPLAN_CDCSPPLANCONCGOAL,
								NO_CONCURRENT_GOAL);
						bookmarkPermGoalsList.add(bookmarkNoConGoal);
					}
					formDataPermGoalsProg.setBookmarkDtoList(bookmarkPermGoalsList);
					formDataGroupList.add(formDataPermGoalsProg);
				}
			}
		}

		/** CHANGE TO APPPLA REASON **/
		if (!ObjectUtils.isEmpty(prtFollowUp.getChangeToApplaReason()) && !idEvent.equals(0L)) {
			BookmarkDto bookmarAppReason = createBookmark(BookmarkConstants.TXTCHGTOAPPLAREASON,
					prtFollowUp.getChangeToApplaReason());
			bookmarkNonFrmGrpList.add(bookmarAppReason);
		}
		/** STRATEGIES AND TASKS **/
		/**
		 * The tasks are to be displayed on the form sorted by task status and
		 * then by strategy and task. Each strategy can contain tasks of any
		 * status, and the forms architecture requires that elements of a
		 * repeater group be grouped in order, e.g. if you add in process tasks
		 * and then completed tasks and then more in process tasks, the form
		 * will blow up. Therefore it's necessary to iterate through the
		 * strategies multiple times. The first time will look at each task to
		 * determine which strategies contain tasks of each status. Then each
		 * strategy will be added to the appropriate list, and then each of
		 * those lists will be iterated through for the actual building of the
		 * prefill data. This ensures that the tasks are grouped correctly by
		 * status.
		 */
		List<PRTStrategyDto> strategyListIP = new ArrayList<PRTStrategyDto>();
		List<PRTStrategyDto> strategyListCOMP = new ArrayList<PRTStrategyDto>();
		List<PRTStrategyDto> strategyListELIM = new ArrayList<PRTStrategyDto>();
		if (!ObjectUtils.isEmpty(prtFollowUp.getStrategies()) && !idEvent.equals(0L)) {
			for (PRTStrategyDto strategyList : prtFollowUp.getStrategies()) {
				for (PRTTaskDto task : strategyList.getPrtTaskValueDtoList()) {
					// if strategy contains an IN PROCESS task and has not
					// already been added to IP list, add it
					if (!ObjectUtils.isEmpty(task.getCdTaskStatus())
							&& task.getCdTaskStatus().equals(CodesConstant.CPRTSKST_10)) {
						if (!strategyListIP.contains(strategyList)) {
							strategyListIP.add(strategyList);
						}
					}
					// if strategy contains a COMPLETED task and has not already
					// been added to COMP list, add it
					if (!ObjectUtils.isEmpty(task.getCdTaskStatus())
							&& task.getCdTaskStatus().equals(CodesConstant.CPRTSKST_20)) {
						if (!strategyListCOMP.contains(strategyList)) {
							strategyListCOMP.add(strategyList);
						}
					}
					// if strategy contains an ELIMINATED task and has not
					// already been added to ELIM list, add it
					if (!strategyListELIM.contains(strategyList)) {
						strategyListELIM.add(strategyList);
					}
				}
			}
		}

		/** IN PROCESS TASKS **/
		if (!ObjectUtils.isEmpty(strategyListIP) && !idEvent.equals(0L)) {
			for (PRTStrategyDto strategy : strategyListIP) {
				boolean strategyAdded = false;
				for (PRTTaskDto task : strategy.getPrtTaskValueDtoList()) {
					if (task.getCdTaskStatus().equalsIgnoreCase(CodesConstant.CPRTSKST_10)) {
						FormDataGroupDto formDataProccessTaskProg = createFormDataGroup(
								FormGroupsConstants.TMPLAT_STRATEGY_TASK_INPROCESS, FormConstants.EMPTY_STRING);
						List<BookmarkDto> bookmarkProccessTaskList = new ArrayList<BookmarkDto>();
						if (strategyAdded == false) {
							if (CodesConstant.CPRTSTR_080.equalsIgnoreCase(strategy.getCdStrategy())) {
								BookmarkDto bookmarkStrategy80 = createBookmark(BookmarkConstants.STRATEGY_CDSTRATEGY,
										"Strategy: " + strategy.getTxtOtherDesc(), formDataProccessTaskProg);
								bookmarkProccessTaskList.add(bookmarkStrategy80);
							} else {
								BookmarkDto bookmarkStrategy = createBookmark(BookmarkConstants.STRATEGY_CDSTRATEGY,
										"Strategy: "
												+ lookupDao.decode(CodesConstant.CPRTSTR, strategy.getCdStrategy()),
										formDataProccessTaskProg);
								bookmarkProccessTaskList.add(bookmarkStrategy);
							}
							strategyAdded = true;
						}
						BookmarkDto bookmarkDesc = createBookmark(BookmarkConstants.STRATEGY_TASK_INPROCESS_DESC,
								task.getTxtDesc(), formDataProccessTaskProg);
						bookmarkProccessTaskList.add(bookmarkDesc);
						BookmarkDto bookmarkCdBarrier = createBookmarkWithCodesTable(
								BookmarkConstants.STRATEGY_TASK_INPROCESS_CDBARRIER, task.getCdBarrier(),
								CodesConstant.CPRTBRS);
						bookmarkProccessTaskList.add(bookmarkCdBarrier);
						BookmarkDto bookmarkPlanOverComes = createBookmark(
								BookmarkConstants.STRATEGY_TASK_INPROCESS_TXTPLANOVRCMBRS, task.getTxtPlanOvrcmBrs(),
								formDataProccessTaskProg);
						bookmarkProccessTaskList.add(bookmarkPlanOverComes);
						if (!DateUtils.isNull(task.getDtCreated())) {
							BookmarkDto bookmarkDtCreated = createBookmark(
									BookmarkConstants.STRATEGY_TASK_INPROCESS_DTCREATED,
									sdfTasks.format(task.getDtCreated()));
							bookmarkProccessTaskList.add(bookmarkDtCreated);
						}
						if (!DateUtils.isNull(task.getDtTargetComplete())) {
							BookmarkDto bookmarkDtTargComp = createBookmark(
									BookmarkConstants.STRATEGY_TASK_INPROCESS_DTTARGETCOMPLETE,
									sdfTasks.format(task.getDtTargetComplete()));
							bookmarkProccessTaskList.add(bookmarkDtTargComp);
						}
						if (task.getIdPersonAssigned() > 0) {
							PersonDto taskPerson = person.getPersonById(task.getIdPersonAssigned());
							StringBuilder taskPersonName = new StringBuilder();
							taskPersonName.append(taskPerson.getNmPersonFirst());
							taskPersonName.append(SPACE);
							taskPersonName.append(taskPerson.getNmPersonLast());
							BookmarkDto bookmarkTaskPersonName = createBookmark(
									BookmarkConstants.STRATEGY_TASK_INPROCESS_PERSON, taskPersonName.toString());
							bookmarkProccessTaskList.add(bookmarkTaskPersonName);
						} else {
							BookmarkDto bookmarkTaskInProc = createBookmarkWithCodesTable(
									BookmarkConstants.STRATEGY_TASK_INPROCESS_PERSON, task.getCdAssignedToType(),
									CodesConstant.CPRTSATY);
							bookmarkProccessTaskList.add(bookmarkTaskInProc);
						}
						StringBuffer sb = new StringBuffer();
						int i = ServiceConstants.Zero;
						for (PRTTaskPersonLinkDto taskPersonList : task.getPrtTaskPersonLinkValueDtoList()) {
							for (PRTPersonLinkDto taskChild : prtFollowUp.getChildren()) {
								if (!ObjectUtils.isEmpty(taskPersonList.getIdPrtPersonLink())
										&& !ObjectUtils.isEmpty(taskChild.getIdPrtPersonLink())) {
									if (taskChild.getIdPrtPersonLink().equals(taskPersonList.getIdPrtPersonLink())) {
										if (ServiceConstants.Zero !=i) {
											sb.append(COMMA_SPACE);
										}
										PersonDto taskChildName = person.getPersonById(taskChild.getIdPerson());
										sb.append(taskChildName.getNmPersonFirst());
										sb.append(SPACE);
										sb.append(taskChildName.getNmPersonLast());
									}
								}
							}
							i++;
						}
						BookmarkDto bookmarkTackChildName = createBookmark(
								BookmarkConstants.STRATEGY_TASK_INPROCESS_CHILDREN, sb.toString());
						bookmarkProccessTaskList.add(bookmarkTackChildName);
						BookmarkDto bookmarkInProcCommnets = createBookmark(
								BookmarkConstants.STRATEGY_TASK_INPROCESS_COMMENTS, task.getTxtComments());
						bookmarkProccessTaskList.add(bookmarkInProcCommnets);
						formDataProccessTaskProg.setBookmarkDtoList(bookmarkProccessTaskList);
						formDataGroupList.add(formDataProccessTaskProg);
					}
				}
			}
		}

		/** COMPLETED TASKS **/
		if (!ObjectUtils.isEmpty(strategyListCOMP) && !idEvent.equals(0L)) {
			for (PRTStrategyDto strategy : strategyListCOMP) {
				boolean strategyAdded = false;
				for (PRTTaskDto task : strategy.getPrtTaskValueDtoList()) {
					if (!ObjectUtils.isEmpty(task.getCdTaskStatus())
							&& task.getCdTaskStatus().equalsIgnoreCase(CodesConstant.CPRTSKST_20)) {
						FormDataGroupDto formDataTaskCompletedProg = createFormDataGroup(
								FormGroupsConstants.TMPLAT_STRATEGY_TASK_COMPLETED, FormConstants.EMPTY_STRING);
						List<BookmarkDto> bookmarkTaskCompletedList = new ArrayList<BookmarkDto>();
						if (strategyAdded == false) {
							if (CodesConstant.CPRTSTR_080.equalsIgnoreCase(strategy.getCdStrategy())) {
								BookmarkDto bookmarkStrategy80 = createBookmark(BookmarkConstants.STRATEGY_CDSTRATEGY,
										"Strategy: " + strategy.getTxtOtherDesc(), formDataTaskCompletedProg);
								bookmarkTaskCompletedList.add(bookmarkStrategy80);
							} else {
								BookmarkDto bookmarkStrategy = createBookmark(BookmarkConstants.STRATEGY_CDSTRATEGY,
										"Strategy: "
												+ lookupDao.decode(CodesConstant.CPRTSTR, strategy.getCdStrategy()),
										formDataTaskCompletedProg);
								bookmarkTaskCompletedList.add(bookmarkStrategy);
							}
							strategyAdded = true;
						}
						BookmarkDto bookmarkCompDesc = createBookmark(BookmarkConstants.STRATEGY_TASK_COMPLETED_DESC,
								task.getTxtDesc(), formDataTaskCompletedProg);
						bookmarkTaskCompletedList.add(bookmarkCompDesc);
						BookmarkDto bookmarkCompCdBarrier = createBookmarkWithCodesTable(
								BookmarkConstants.STRATEGY_TASK_COMPLETED_CDBARRIER, task.getCdBarrier(),
								CodesConstant.CPRTBRS);
						bookmarkTaskCompletedList.add(bookmarkCompCdBarrier);
						BookmarkDto bookmarkCompPlanOverComes = createBookmark(
								BookmarkConstants.STRATEGY_TASK_COMPLETED_TXTPLANOVRCMBRS, task.getTxtPlanOvrcmBrs(),
								formDataTaskCompletedProg);
						bookmarkTaskCompletedList.add(bookmarkCompPlanOverComes);
						if (!DateUtils.isNull(task.getDtCreated())) {
							BookmarkDto bookmarkDtCreatedComp = createBookmark(
									BookmarkConstants.STRATEGY_TASK_COMPLETED_DTCREATED,
									sdfTasks.format(task.getDtCreated()));
							bookmarkTaskCompletedList.add(bookmarkDtCreatedComp);
						}
						if (!DateUtils.isNull(task.getDtCompOrElimnated())) {
							BookmarkDto bookmarkDtCompOrElim = createBookmark(
									BookmarkConstants.STRATEGY_TASK_COMPLETED_DTCOMPLETE,
									sdfTasks.format(task.getDtCompOrElimnated()));
							bookmarkTaskCompletedList.add(bookmarkDtCompOrElim);
						}
						if (!DateUtils.isNull(task.getDtTargetComplete())) {
							BookmarkDto bookmarkDtCompOrElim = createBookmark(
									BookmarkConstants.STRATEGY_TASK_COMPLETED_DTTARGETCOMPLETE,
									sdfTasks.format(task.getDtTargetComplete()));
							bookmarkTaskCompletedList.add(bookmarkDtCompOrElim);
						}
						if (task.getIdPersonAssigned() > 0) {
							PersonDto taskPerson = person.getPersonById(task.getIdPersonAssigned());
							StringBuilder taskPersonName = new StringBuilder();
							taskPersonName.append(taskPerson.getNmPersonFirst());
							taskPersonName.append(SPACE);
							taskPersonName.append(taskPerson.getNmPersonLast());
							BookmarkDto bookmarkTaskPersonName = createBookmark(
									BookmarkConstants.STRATEGY_TASK_COMPLETED_PERSON, taskPersonName.toString());
							bookmarkTaskCompletedList.add(bookmarkTaskPersonName);
						} else {
							BookmarkDto bookmarkTaskElim = createBookmarkWithCodesTable(
									BookmarkConstants.STRATEGY_TASK_COMPLETED_PERSON, task.getCdAssignedToType(),
									CodesConstant.CPRTSATY);
							bookmarkTaskCompletedList.add(bookmarkTaskElim);
						}
						StringBuffer sb = new StringBuffer();
						for (PRTTaskPersonLinkDto taskPersonList : task.getPrtTaskPersonLinkValueDtoList()) {
							for (PRTPersonLinkDto taskChild : prtFollowUp.getChildren()) {
								if (taskChild.getIdPrtPersonLink().equals(taskPersonList.getIdPrtPersonLink())) {
									if (!task.getPrtTaskPersonLinkValueDtoList().isEmpty()) {
										sb.append(COMMA_SPACE);
									}
									PersonDto taskChildName = person.getPersonById(taskChild.getIdPerson());
									sb.append(taskChildName.getNmPersonFirst());
									sb.append(SPACE);
									sb.append(taskChildName.getNmPersonLast());
								}
							}
						}
						BookmarkDto bookmarkTaskCompComms = createBookmark(
								BookmarkConstants.STRATEGY_TASK_COMPLETED_CHILDREN, sb.toString());
						bookmarkTaskCompletedList.add(bookmarkTaskCompComms);
						BookmarkDto bookmarkTaskElimReason = createBookmark(
								BookmarkConstants.STRATEGY_TASK_COMPLETED_COMMENTS, task.getTxtComments(),
								formDataTaskCompletedProg);
						bookmarkTaskCompletedList.add(bookmarkTaskElimReason);

						formDataTaskCompletedProg.setBookmarkDtoList(bookmarkTaskCompletedList);
						formDataGroupList.add(formDataTaskCompletedProg);
					}
				}
			}
		}

		/** ELIMINATED TASKS **/
		if (!ObjectUtils.isEmpty(strategyListELIM) && !idEvent.equals(0L)) {
			for (PRTStrategyDto strategy : strategyListELIM) {
				boolean strategyAdded = false;
				for (PRTTaskDto task : strategy.getPrtTaskValueDtoList()) {
					if (!ObjectUtils.isEmpty(task.getCdTaskStatus())
							&& task.getCdTaskStatus().equalsIgnoreCase(CodesConstant.CPRTSKST_30)) {
						FormDataGroupDto formDataTaskEliminatedProg = createFormDataGroup(
								FormGroupsConstants.TMPLAT_STRATEGY_TASK_ELIMINATED, FormConstants.EMPTY_STRING);
						List<BookmarkDto> bookmarkTaskEliminatedList = new ArrayList<BookmarkDto>();
						if (strategyAdded == false) {
							if (CodesConstant.CPRTSTR_080.equalsIgnoreCase(strategy.getCdStrategy())) {
								BookmarkDto bookmarkStrategy80 = createBookmark(BookmarkConstants.STRATEGY_CDSTRATEGY,
										"Strategy: " + strategy.getTxtOtherDesc(), formDataTaskEliminatedProg);
								bookmarkTaskEliminatedList.add(bookmarkStrategy80);
							} else {
								BookmarkDto bookmarkStrategy = createBookmark(BookmarkConstants.STRATEGY_CDSTRATEGY,
										"Strategy: "
												+ lookupDao.decode(CodesConstant.CPRTSTR, strategy.getCdStrategy()),
										formDataTaskEliminatedProg);
								bookmarkTaskEliminatedList.add(bookmarkStrategy);
							}
							strategyAdded = true;
						}
						BookmarkDto bookmarkElimDesc = createBookmark(BookmarkConstants.STRATEGY_TASK_ELIMINATED_DESC,
								task.getTxtDesc(), formDataTaskEliminatedProg);
						bookmarkTaskEliminatedList.add(bookmarkElimDesc);
						BookmarkDto bookmarkElimCdBarrier = createBookmarkWithCodesTable(
								BookmarkConstants.STRATEGY_TASK_ELIMINATED_CDBARRIER, task.getCdBarrier(),
								CodesConstant.CPRTBRS);
						bookmarkTaskEliminatedList.add(bookmarkElimCdBarrier);
						BookmarkDto bookmarkElimPlanOverComes = createBookmark(
								BookmarkConstants.STRATEGY_TASK_ELIMINATED_TXTPLANOVRCMBRS, task.getTxtPlanOvrcmBrs(),
								formDataTaskEliminatedProg);
						bookmarkTaskEliminatedList.add(bookmarkElimPlanOverComes);
						if (!DateUtils.isNull(task.getDtCreated())) {
							BookmarkDto bookmarkDtCreatedElim = createBookmark(
									BookmarkConstants.STRATEGY_TASK_ELIMINATED_DTCREATED,
									sdfTasks.format(task.getDtCreated()));
							bookmarkTaskEliminatedList.add(bookmarkDtCreatedElim);
						}
						if (!DateUtils.isNull(task.getDtCompOrElimnated())) {
							BookmarkDto bookmarkDtCompOrElim = createBookmark(
									BookmarkConstants.STRATEGY_TASK_ELIMINATED_DTELIMINATED,
									sdfTasks.format(task.getDtCompOrElimnated()));
							bookmarkTaskEliminatedList.add(bookmarkDtCompOrElim);
						}
						if (task.getIdPersonAssigned() > 0) {
							PersonDto taskPerson = person.getPersonById(task.getIdPersonAssigned());
							StringBuilder taskPersonName = new StringBuilder();
							taskPersonName.append(taskPerson.getNmPersonFirst());
							taskPersonName.append(SPACE);
							taskPersonName.append(taskPerson.getNmPersonLast());
							BookmarkDto bookmarkTaskPersonName = createBookmark(
									BookmarkConstants.STRATEGY_TASK_ELIMINATED_PERSON, taskPersonName.toString());
							bookmarkTaskEliminatedList.add(bookmarkTaskPersonName);
						} else {
							BookmarkDto bookmarkTaskElim = createBookmarkWithCodesTable(
									BookmarkConstants.STRATEGY_TASK_ELIMINATED_PERSON, task.getCdAssignedToType(),
									CodesConstant.CPRTSATY);
							bookmarkTaskEliminatedList.add(bookmarkTaskElim);
						}
						StringBuffer sb = new StringBuffer();
						for (PRTTaskPersonLinkDto taskPersonList : task.getPrtTaskPersonLinkValueDtoList()) {
							for (PRTPersonLinkDto taskChild : prtFollowUp.getChildren()) {
								if (taskChild.getIdPrtPersonLink().equals(taskPersonList.getIdPrtPersonLink())) {
									if (!task.getPrtTaskPersonLinkValueDtoList().isEmpty()) {
										sb.append(COMMA_SPACE);
									}
									PersonDto taskChildName = person.getPersonById(taskChild.getIdPerson());
									sb.append(taskChildName.getNmPersonFirst());
									sb.append(SPACE);
									sb.append(taskChildName.getNmPersonLast());
								}
							}
						}
						BookmarkDto bookmarkTaskChildName = createBookmark(
								BookmarkConstants.STRATEGY_TASK_ELIMINATED_CHILDREN, sb.toString());
						bookmarkTaskEliminatedList.add(bookmarkTaskChildName);
						BookmarkDto bookmarkTaskElimReason = createBookmarkWithCodesTable(
								BookmarkConstants.STRATEGY_TASK_ELIMINATED_REASON, task.getCdEliminationReason(),
								CodesConstant.CPRTSELR);
						bookmarkTaskEliminatedList.add(bookmarkTaskElimReason);
						BookmarkDto bookmarkElimDts = createBookmark(BookmarkConstants.STRATEGY_TASK_ELIMINATED_DETAILS,
								task.getCdEliminationReason(), formDataTaskEliminatedProg);
						bookmarkTaskEliminatedList.add(bookmarkElimDts);
						BookmarkDto bookmarkElimComms = createBookmark(
								BookmarkConstants.STRATEGY_TASK_ELIMINATED_COMMENTS, task.getTxtComments(),
								formDataTaskEliminatedProg);
						bookmarkTaskEliminatedList.add(bookmarkElimComms);
						formDataTaskEliminatedProg.setBookmarkDtoList(bookmarkTaskEliminatedList);
						formDataGroupList.add(formDataTaskEliminatedProg);
					}
				}
			}
		}

		/** DEBRIEF OF ROUNDTABLE **/
		if (!idEvent.equals(0L)) {
			FormDataGroupDto formDataRoundTableProg = createFormDataGroup(FormGroupsConstants.TMPLAT_DEBRIEF,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkRoundTableList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkProgMade = createBookmark(BookmarkConstants.TXTDBRFPROGRESSMADE,
					prtFollowUp.getDebriefProgressMade(), formDataRoundTableProg);
			bookmarkRoundTableList.add(bookmarkProgMade);
			BookmarkDto bookmarkChildID = createBookmark(BookmarkConstants.TXTDBRFCHLGIDENTIFIED,
					prtFollowUp.getDebriefChlgIdentified(), formDataRoundTableProg);
			bookmarkRoundTableList.add(bookmarkChildID);
			BookmarkDto bookmarkSolnID = createBookmark(BookmarkConstants.TXTDBRFSOLNIDENTIFIED,
					prtFollowUp.getDebriefSolnIdentified(), formDataRoundTableProg);
			bookmarkRoundTableList.add(bookmarkSolnID);
			formDataRoundTableProg.setBookmarkDtoList(bookmarkRoundTableList);
			formDataGroupList.add(formDataRoundTableProg);
		}
		/** DID PD PARTICIPATE IN BIANNUAL FOLLOWUP **/
		if (!idEvent.equals(0L)) {
			if (!StringUtils.isEmpty(prtFollowUp.getCdType()) && prtFollowUp.getCdType().contains(BIANNUAL)) {
				FormDataGroupDto formDataBiannualProg = createFormDataGroup(FormGroupsConstants.TMPLAT_PD_PARTICIP,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkBiannualList = new ArrayList<BookmarkDto>();
				if (!StringUtils.isEmpty(prtFollowUp.getIndPdParticipBiannual())
						&& prtFollowUp.getIndPdParticipBiannual().equalsIgnoreCase("y")) {
					BookmarkDto bookmarkParticiapateYes = createBookmark(BookmarkConstants.INDPDPARTICIPATE, YES);
					bookmarkBiannualList.add(bookmarkParticiapateYes);
				} else if (!StringUtils.isEmpty(prtFollowUp.getIndPdParticipBiannual())
						&& prtFollowUp.getIndPdParticipBiannual().equalsIgnoreCase("n")) {
					BookmarkDto bookmarkParticiapateNo = createBookmark(BookmarkConstants.INDPDPARTICIPATE, NO);
					bookmarkBiannualList.add(bookmarkParticiapateNo);
				} else {
					BookmarkDto bookmarkParticipateNA = createBookmark(BookmarkConstants.INDPDPARTICIPATE,
							EMPTY_STRING);
					bookmarkBiannualList.add(bookmarkParticipateNA);
				}
				formDataBiannualProg.setBookmarkDtoList(bookmarkBiannualList);
				formDataGroupList.add(formDataBiannualProg);
			}
		}
		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
		preFillData.setFormDataGroupList(formDataGroupList);
		preFillData.setBookmarkDtoList(bookmarkNonFrmGrpList);
		return preFillData;
	}
}
