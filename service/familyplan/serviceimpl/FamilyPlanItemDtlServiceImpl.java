/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Mar 8, 2018- 3:31:47 PM
 *
 */
package us.tx.state.dfps.service.familyplan.serviceimpl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.dto.ErrorDto;
import us.tx.state.dfps.common.dto.ServiceReqHeaderDto;
import us.tx.state.dfps.service.admin.dto.ApprovalCommonInDto;
import us.tx.state.dfps.service.admin.service.ApprovalCommonService;
import us.tx.state.dfps.service.admin.service.PostEventService;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.EventDao;
import us.tx.state.dfps.service.common.response.FamilyPlanItemDtlRes;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.familyplan.dao.FamilyPlanItemDtlDao;
import us.tx.state.dfps.service.familyplan.dto.FamilyPlanEvalItemDto;
import us.tx.state.dfps.service.familyplan.service.FamilyPlanItemDtlService;
import us.tx.state.dfps.service.familyplan.service.FamilyPlanService;
import us.tx.state.dfps.service.forms.dto.FamilyPlanDto;
import us.tx.state.dfps.service.forms.dto.FamilyPlanEvalDto;
import us.tx.state.dfps.service.forms.dto.FamilyPlanGoalDto;
import us.tx.state.dfps.service.forms.dto.FamilyPlanItemDto;
import us.tx.state.dfps.service.forms.dto.FamilyPlanTaskDto;
import us.tx.state.dfps.service.workload.dto.EventDto;
import us.tx.state.dfps.service.workload.dto.PostEventIPDto;
import us.tx.state.dfps.service.workload.dto.PostEventOPDto;

@Service
@Transactional
public class FamilyPlanItemDtlServiceImpl implements FamilyPlanItemDtlService {

	private static final Long FGDM_CONSTANTS_OLD = 2L;

	private static final Long FGDM_CONSTANTS_NEW = 1L;
	public static final String UPDATE_ID_ADDRESSED_EVENT_AND_IND_ADDRESSED_FAM_PLAN = "UPDATE_ID_ADDRESSED_EVENT_AND_IND_ADDRESSED_FAM_PLAN";
	public static final String UPDATE_WITHOUT_ID_ADDRESSED_EVENT_AND_IND_ADDRESSED_FAM_PLAN = "UPDATE_WITHOUT_ID_ADDRESSED_EVENT_AND_IND_ADDRESSED_FAM_PLAN";

	@Autowired
	private FamilyPlanItemDtlDao familyPlanItemDtlDao;
	@Autowired
	private EventDao eventDao;
	@Autowired
	ApprovalCommonService approvalCommonService;
	@Autowired
	PostEventService postEventService;
	@Autowired
	FamilyPlanService familyPlanService;

	private static final Long FGDM_VERSION = 2L;

	/**
	 * Method Name: getFamilyPlanItemDtl Method Description:This method is used
	 * to get the family plan item detail based on the selected family plan .
	 * 
	 * @param familyPlanDto
	 * @param pageMode
	 * @return FamilyPlanItemDto
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW)
	public FamilyPlanItemDto getFamilyPlanItemDtl(FamilyPlanDto familyPlanDto, String pageMode) {
		Long fpVersionNbr = getFamilyPlanVersion(familyPlanDto);
		FamilyPlanItemDto selectedFmlyPlanItemDto = null;
		String selectedAreaOfConcernCode = familyPlanDto.getCdSelectedAreaOfConcern();
		if (fpVersionNbr != FGDM_VERSION) {
			// Determine which item (area of concern) the user selected.
			if (!ObjectUtils.isEmpty(familyPlanDto.getFamilyPlanItemList())
					&& !ObjectUtils.isEmpty(selectedAreaOfConcernCode)) {
				selectedFmlyPlanItemDto = familyPlanDto.getFamilyPlanItemList().stream()
						.filter(f -> selectedAreaOfConcernCode.equalsIgnoreCase(f.getCdAreaConcern())).findFirst()
						.get();
				if (!ObjectUtils.isEmpty(selectedFmlyPlanItemDto)) {
					selectedFmlyPlanItemDto = queryFamilyPlanItem(selectedFmlyPlanItemDto, fpVersionNbr);
				}

			}

			selectedFmlyPlanItemDto = addEmptyTaskIfNeeded(selectedFmlyPlanItemDto);
			selectedFmlyPlanItemDto = addEmptyEvalItemIfNeeded(familyPlanDto, selectedFmlyPlanItemDto, pageMode);

		} else {

			Long idCurrentEvalEvent = 0L;
			if (!ObjectUtils.isEmpty(familyPlanDto.getFamilyPlanEvaluationList())) {
				idCurrentEvalEvent = familyPlanDto.getFamilyPlanEvaluationList().get(0).getIdEvent();
			}

			if (!ObjectUtils.isEmpty(familyPlanDto.getFamilyPlanItemList())
					&& !ObjectUtils.isEmpty(selectedAreaOfConcernCode)) {
				selectedFmlyPlanItemDto = familyPlanDto.getFamilyPlanItemList().stream()
						.filter(f -> selectedAreaOfConcernCode.equalsIgnoreCase(f.getCdAreaConcern())).findFirst()
						.get();
				selectedFmlyPlanItemDto.setNbrVersion(fpVersionNbr);
				selectedFmlyPlanItemDto.setIdCurrentEvalEvent(idCurrentEvalEvent);
				if (!ObjectUtils.isEmpty(selectedFmlyPlanItemDto)) {
					selectedFmlyPlanItemDto = queryFamilyPlanItem(selectedFmlyPlanItemDto, fpVersionNbr);
				}

			}
			if (familyPlanDto.isIndFamilyPlanEval()) {
				selectedFmlyPlanItemDto = addEmptyFgdmEvalItemIfNeeded(familyPlanDto, selectedFmlyPlanItemDto,
						pageMode);
			}
			selectedFmlyPlanItemDto = filterGoals(familyPlanDto, selectedFmlyPlanItemDto, pageMode);
			if (idCurrentEvalEvent > 0 && !ObjectUtils.isEmpty(selectedFmlyPlanItemDto.getFamilyPlanGoalDtoList())) {
				selectedFmlyPlanItemDto = filterTasks(familyPlanDto, selectedFmlyPlanItemDto, pageMode);

			}

		}

		return selectedFmlyPlanItemDto;
	}

	/**
	 * Method Name: addEmptyFgdmEvalItemIfNeeded Method Description:This method
	 * is used to add empty evaluation item based on the conditions .
	 * 
	 * @param familyPlanDto
	 * @param selectedFmlyPlanItemDto
	 * @param pageMode
	 * @return FamilyPlanItemDto
	 */
	private FamilyPlanItemDto addEmptyFgdmEvalItemIfNeeded(FamilyPlanDto familyPlanDto,
			FamilyPlanItemDto selectedFmlyPlanItemDto, String pageMode) {

		Date mostRecentApprovalDate = getMostRecentApprovalDate(familyPlanDto);
		EventDto evalEventOfMostRecentEvalItem = getEvalEventOfMostRecentEvalItem(familyPlanDto,
				selectedFmlyPlanItemDto);
		List<FamilyPlanEvalItemDto> evalItems = null;
		if (!ObjectUtils.isEmpty(selectedFmlyPlanItemDto)) {
			evalItems = selectedFmlyPlanItemDto.getFamilyPlanEvalItemDtoList();
		}

		boolean isFamilyPlanPage = ObjectUtils.isEmpty(familyPlanDto.getFamilyPlanEvaluationList()) ? true : false;

		if (!isFamilyPlanPage && ServiceConstants.WINDOW_MODE_INQUIRE.equals(pageMode)) {
			pageMode = ServiceConstants.WINDOW_MODE_MODIFY;
			mostRecentApprovalDate = getAppDtOfMostRecentEvalButOne(familyPlanDto);
		}
		
		if(!ServiceConstants.CEVTSTAT_APRV.equals(familyPlanDto.getEventStatus())){
			if (ObjectUtils.isEmpty(evalItems) && !ObjectUtils.isEmpty(selectedFmlyPlanItemDto)
					&& !ObjectUtils.isEmpty(selectedFmlyPlanItemDto.getDtInitiallyAddressed())
					&& !ObjectUtils.isEmpty(mostRecentApprovalDate)
					&& !selectedFmlyPlanItemDto.getDtInitiallyAddressed().after(mostRecentApprovalDate)) {
				evalItems = new ArrayList<>();
				evalItems.add(0, new FamilyPlanEvalItemDto());
				selectedFmlyPlanItemDto.setFamilyPlanEvalItemDtoList(evalItems);
	
			} else {
				if (!ObjectUtils.isEmpty(mostRecentApprovalDate)
						&& !ObjectUtils.isEmpty(selectedFmlyPlanItemDto.getDtInitiallyAddressed())
						&& !selectedFmlyPlanItemDto.getDtInitiallyAddressed().after(mostRecentApprovalDate)) {
					if (ServiceConstants.CEVTSTAT_APRV.equals(evalEventOfMostRecentEvalItem != null
							? evalEventOfMostRecentEvalItem.getCdEventStatus() : null)) {
						if (!ServiceConstants.WINDOW_MODE_INQUIRE.equals(pageMode)) {
							if (evalItems == null) {
								evalItems = new ArrayList<>();
							}
							evalItems.add(0, new FamilyPlanEvalItemDto());
							selectedFmlyPlanItemDto.setFamilyPlanEvalItemDtoList(evalItems);
						}
					}
				}
	
			}
		}
		selectedFmlyPlanItemDto.setMostRecentApprovalDate(mostRecentApprovalDate);
		return selectedFmlyPlanItemDto;
	}

	/**
	 * Method Name: getAppDtOfMostRecentEvalButOne Method Description: Get the
	 * Approval date of most recent evaluation but one(Not the current one
	 * viewing)
	 * 
	 * @param familyPlanBean
	 * @return Date
	 */
	private Date getAppDtOfMostRecentEvalButOne(FamilyPlanDto familyPlanBean) {
		Date approvalDt = null;
		if (!ObjectUtils.isEmpty(familyPlanBean.getFamilyPlanEvaluationList())) {
			if (familyPlanBean.getFamilyPlanEvaluationList().size() == 1) {
				if (!ObjectUtils.isEmpty(familyPlanBean.getFamilyPlanEvent())
						&& !ObjectUtils.isEmpty(familyPlanBean.getFamilyPlanEvent().getDtLastUpdate())) {
					approvalDt = familyPlanBean.getFamilyPlanEvent().getDtLastUpdate();
				}
			} else {
				Iterator<FamilyPlanEvalDto> iter = familyPlanBean.getFamilyPlanEvaluationList().iterator();
				int i = 0;
				while (iter.hasNext()) {
					FamilyPlanEvalDto evalBean = (FamilyPlanEvalDto) iter.next();
					if (ServiceConstants.CEVTSTAT_APRV.equals(evalBean.getEvalEvent().getCdEventStatus())) {
						i++;
						approvalDt = evalBean.getEvalEvent().getDtLastUpdate();
						if (i == 2)
							break;
					}
				}
			}
		}
		return approvalDt;
	}

	private FamilyPlanItemDto addEmptyEvalItemIfNeeded(FamilyPlanDto familyPlanDto,
			FamilyPlanItemDto selectedFmlyPlanItemDto, String pageMode) {

		Date mostRecentApprovalDate = getMostRecentApprovalDate(familyPlanDto);
		EventDto evalEventOfMostRecentEvalItem = getEvalEventOfMostRecentEvalItem(familyPlanDto,
				selectedFmlyPlanItemDto);
		List<FamilyPlanEvalItemDto> evalItems = null;

		if (!ObjectUtils.isEmpty(selectedFmlyPlanItemDto)) {
			evalItems = selectedFmlyPlanItemDto.getFamilyPlanEvalItemDtoList();

		}

		if (!ObjectUtils.isEmpty(familyPlanDto.getFamilyPlanEvent())
				&& !ServiceConstants.CEVTSTAT_APRV.equals(familyPlanDto.getFamilyPlanEvent().getCdEventStatus())
				|| familyPlanDto.getDtInitiallyAddressed() == null || evalItems == null
				|| (mostRecentApprovalDate != null
						&& !selectedFmlyPlanItemDto.getDtInitiallyAddressed().after(mostRecentApprovalDate)
						&& ServiceConstants.CEVTSTAT_APRV.equals(evalEventOfMostRecentEvalItem != null
								? evalEventOfMostRecentEvalItem.getCdEventStatus() : null)
						&& !ServiceConstants.WINDOW_MODE_INQUIRE.equals(pageMode))) {
			if (ObjectUtils.isEmpty(evalItems)) {
				evalItems = new ArrayList<>();
			}
			evalItems.add(0, new FamilyPlanEvalItemDto());
			selectedFmlyPlanItemDto.setFamilyPlanEvalItemDtoList(evalItems);
		}
		selectedFmlyPlanItemDto.setMostRecentApprovalDate(mostRecentApprovalDate);
		return selectedFmlyPlanItemDto;
	}

	/**
	 * Method Name:getEvalEventOfMostRecentEvalItem
	 * 
	 * Method Description:Get the Event of the most recent evaluation item
	 *
	 * @param familyPlanDto
	 * @param selectedFmlyPlanItemDto
	 * @return EventDto
	 */
	private EventDto getEvalEventOfMostRecentEvalItem(FamilyPlanDto familyPlanDto,
			FamilyPlanItemDto selectedFmlyPlanItemDto) {
		EventDto evalEventOfMostRecentEvalItem = null;
		FamilyPlanEvalItemDto mostRecentEvalItem;
		if (!ObjectUtils.isEmpty(selectedFmlyPlanItemDto)
				&& !ObjectUtils.isEmpty(selectedFmlyPlanItemDto.getFamilyPlanEvalItemDtoList())) {
			List<FamilyPlanEvalItemDto> evalItems = selectedFmlyPlanItemDto.getFamilyPlanEvalItemDtoList();
			mostRecentEvalItem = evalItems.get(0);
			FamilyPlanEvalDto mostRecentEval = null;
			List<FamilyPlanEvalDto> familyPlanEvals = familyPlanDto.getFamilyPlanEvaluationList();
			if (!ObjectUtils.isEmpty(familyPlanEvals) && !ObjectUtils.isEmpty(mostRecentEvalItem.getIdEvent())) {
				mostRecentEval = familyPlanEvals.stream()
						.filter(evalDto -> (mostRecentEvalItem.getIdEvent().equals(evalDto.getIdEvent()))).findFirst()
						.get();
			}

			evalEventOfMostRecentEvalItem = mostRecentEval != null ? mostRecentEval.getEvalEvent() : null;
		}
		return evalEventOfMostRecentEvalItem;
	}

	/**
	 * Method Name:getMostRecentApprovalDate
	 * 
	 * Method Description:Returns the most recent approval date for the family
	 * plan.
	 *
	 * @param familyPlanDto
	 *            The FamilyPlanDto containing the family plan details.
	 * @return mostRecentApprovalDate The most recent approval date for the
	 *         family plan.
	 */
	private Date getMostRecentApprovalDate(FamilyPlanDto familyPlanDto) {

		Date mostRecentApprovalDate = null;
		if (!ObjectUtils.isEmpty(familyPlanDto.getFamilyPlanEvent())
				&& ServiceConstants.CEVTSTAT_APRV.equals(familyPlanDto.getFamilyPlanEvent().getCdEventStatus())) {
			mostRecentApprovalDate = familyPlanDto.getFamilyPlanEvent().getDtLastUpdate();
		}
		if (!ObjectUtils.isEmpty(familyPlanDto.getFamilyPlanEvaluationList())) {
			Optional<FamilyPlanEvalDto> matchingObject = familyPlanDto.getFamilyPlanEvaluationList().stream()
					.filter(evalDto -> (ServiceConstants.CEVTSTAT_APRV.equals(evalDto.getCdEventStatus()))).findFirst();
			if (matchingObject.isPresent()) {
				/* if (!ObjectUtils.isEmpty(matchingObject.get())) { */
				mostRecentApprovalDate = matchingObject.get().getEvalEvent().getDtLastUpdate();
				/* } */
			}

		}
		return mostRecentApprovalDate;
	}

	/**
	 * Method Name:addEmptyTaskIfNeeded
	 * 
	 * Method Description: This method is used to add empty task based on the
	 * conditions
	 *
	 * @param familyPlanDto
	 *            The FamilyPlanDto containing the family plan details.
	 * @return mostRecentApprovalDate The most recent approval date for the
	 *         family plan.
	 */
	private FamilyPlanItemDto addEmptyTaskIfNeeded(FamilyPlanItemDto familyPlanItemDto) {
		// If the item has no tasks/services, add an empty one for display
		if (ObjectUtils.isEmpty(familyPlanItemDto.getFamilyPlanTaskDtoList())) {
			List<FamilyPlanTaskDto> familyPlanTaskDtoList = new ArrayList<>();
			FamilyPlanTaskDto familyPlanTaskDto = new FamilyPlanTaskDto();
			familyPlanTaskDtoList.add(familyPlanTaskDto);
			familyPlanItemDto.setFamilyPlanTaskDtoList(familyPlanTaskDtoList);
		}
		return familyPlanItemDto;

	}

	/**
	 * Method Name: queryFamilyPlanItem
	 * 
	 * Method Description: This method is used to get the family plan item
	 * detail based n the family plan item version.
	 *
	 * @param familyPlanDto
	 *            The FamilyPlanDto containing the family plan details.
	 * @param fpVersionNbr
	 *            The version number of the family plan detail.
	 * @return mostRecentApprovalDate The most recent approval date for the
	 *         family plan.
	 */
	private FamilyPlanItemDto queryFamilyPlanItem(FamilyPlanItemDto familyPlanItemDto, Long fpVersionNbr) {
		if (fpVersionNbr != FGDM_VERSION) {
			return familyPlanItemDtlDao.getFamilyPlanItemDtlLegacy(familyPlanItemDto);
		} else {
			return familyPlanItemDtlDao.getFamilyPlanItemDtl(familyPlanItemDto);
		}
	}

	/**
	 * Method Name: Common function to get the Family Plan Version.
	 * 
	 * @param FamilyPlanDto
	 * @return Long - version number
	 */
	private Long getFamilyPlanVersion(FamilyPlanDto familyPlanDto) {
		Long fpVersionNbr = 0L;
		if (ObjectUtils.isEmpty(familyPlanDto.getNbrFamPlanVersion())
				|| familyPlanDto.getNbrFamPlanVersion().equalsIgnoreCase(ServiceConstants.STR_ZERO_VAL)) {
			fpVersionNbr = FGDM_VERSION;
		} else {
			fpVersionNbr = Long.valueOf(familyPlanDto.getNbrFamPlanVersion());
		}
		return fpVersionNbr;
	}

	/**
	 * Method Description :After the Evaluation is APPROVED we should not show
	 * the goals created in the current evaluation.So we take Approval date
	 * (last update date) of the most recent evaluation but one (not the current
	 * one viewing) and compare with the Approval date of the Goals.
	 *
	 * @param familyPlanBean
	 * @param familyPlanItemBean
	 * @param pageMode
	 */
	public FamilyPlanItemDto filterGoals(FamilyPlanDto familyPlanDto, FamilyPlanItemDto familyPlanItemBean,
			String pageMode) {
		boolean isFamilyPlanPage = ObjectUtils.isEmpty(familyPlanDto.getFamilyPlanEvaluationList()) ? true : false;

		List<FamilyPlanGoalDto> goalsColl = null;
		if (!ObjectUtils.isEmpty(familyPlanItemBean)) {
			goalsColl = familyPlanItemBean.getFamilyPlanGoalDtoList();

			List<FamilyPlanGoalDto> goalsList = new ArrayList<>();
			List<FamilyPlanGoalDto> newGoalsList = new ArrayList<>();
			List<FamilyPlanGoalDto> oldGoalsList = new ArrayList<>();

			if (isFamilyPlanPage && ServiceConstants.WINDOW_MODE_INQUIRE.equals(pageMode)) {
				if (!ObjectUtils.isEmpty(goalsColl)) {
					Iterator<FamilyPlanGoalDto> iter = goalsColl.iterator();
					while (iter.hasNext()) {
						FamilyPlanGoalDto familyPlanGoalvalueBean = (FamilyPlanGoalDto) iter.next();
						familyPlanGoalvalueBean.setCdStatus(FGDM_CONSTANTS_NEW);
						newGoalsList.add(familyPlanGoalvalueBean);
					}
				}
			} else if (!isFamilyPlanPage && ServiceConstants.WINDOW_MODE_INQUIRE.equals(pageMode)) {
				Date approvalDt = getAppDtOfMostRecentEvalButOne(familyPlanDto);

				if (!ObjectUtils.isEmpty(goalsColl)) {
					Iterator<FamilyPlanGoalDto> iter = goalsColl.iterator();
					while (iter.hasNext()) {
						FamilyPlanGoalDto familyPlanGoalvalueBean = (FamilyPlanGoalDto) iter.next();
						/*
						 * If familyPlanGoalvalueBean.getDateApproved is null
						 * assign 1 to compareTo (set status code to 1)
						 */
						if (!ObjectUtils.isEmpty(approvalDt)) {
							int compareTo = null == familyPlanGoalvalueBean.getDtApproved() ? 1
									: familyPlanGoalvalueBean.getDtApproved().compareTo(approvalDt);
							if (compareTo <= 0) {
								familyPlanGoalvalueBean.setCdStatus(FGDM_CONSTANTS_OLD);
								oldGoalsList.add(familyPlanGoalvalueBean);
							} else {
								familyPlanGoalvalueBean.setCdStatus(FGDM_CONSTANTS_NEW);
								newGoalsList.add(familyPlanGoalvalueBean);
							}
						}

					}

				}
			} else {
				if (!ObjectUtils.isEmpty(goalsColl)) {
					Iterator<FamilyPlanGoalDto> iter = goalsColl.iterator();
					while (iter.hasNext()) {
						FamilyPlanGoalDto familyPlanGoalvalueBean = (FamilyPlanGoalDto) iter.next();
						if (ObjectUtils.isEmpty(familyPlanGoalvalueBean.getDtApproved())) {
							familyPlanGoalvalueBean.setCdStatus(FGDM_CONSTANTS_NEW);
							newGoalsList.add(familyPlanGoalvalueBean);
						} else {
							familyPlanGoalvalueBean.setCdStatus(FGDM_CONSTANTS_OLD);
							oldGoalsList.add(familyPlanGoalvalueBean);
						}
					}
				}
			}

			oldGoalsList.sort(new Comparator<FamilyPlanGoalDto>() {
				@Override
				public int compare(FamilyPlanGoalDto o1, FamilyPlanGoalDto o2) {
					return o2.getIdFamilyPlanGoal().intValue() - o1.getIdFamilyPlanGoal().intValue();
				}
			});

			newGoalsList.sort(new Comparator<FamilyPlanGoalDto>() {
				@Override
				public int compare(FamilyPlanGoalDto o1, FamilyPlanGoalDto o2) {
					return o2.getIdFamilyPlanGoal().intValue() - o1.getIdFamilyPlanGoal().intValue();
				}
			});

			oldGoalsList.addAll(newGoalsList);
			goalsList.addAll(oldGoalsList);
			familyPlanItemBean.setFamilyPlanGoalDtoList(goalsList);
		}
		return familyPlanItemBean;
	}

	/**
	 * After the Evaluation is APPROVED we should not show the tasks created in
	 * the current evaluation.So we take Approval date (last update date) of the
	 * most recent evaluation but one (not the current one viewing) and compare
	 * with the Approval date of the Goals.
	 *
	 * @param familyPlanBean
	 * @param familyPlanItemBean
	 * @param pageMode
	 */
	public FamilyPlanItemDto filterTasks(FamilyPlanDto familyPlanDto, FamilyPlanItemDto familyPlanItemBean,
			String pageMode) {
		boolean isFamilyPlanPage = ObjectUtils.isEmpty(familyPlanDto.getFamilyPlanEvaluationList()) ? true : false;
		List<FamilyPlanTaskDto> tasksColl = familyPlanItemBean.getFamilyPlanTaskDtoList();

		List<FamilyPlanTaskDto> tasksList = new ArrayList<>();
		List<FamilyPlanTaskDto> newTasksList = new ArrayList<>();
		List<FamilyPlanTaskDto> oldTasksList = new ArrayList<>();

		if (!isFamilyPlanPage && ServiceConstants.WINDOW_MODE_INQUIRE.equals(pageMode)) {
			Date approvalDt = getAppDtOfMostRecentEvalButOne(familyPlanDto);
			if (!ObjectUtils.isEmpty(tasksColl)) {
				Iterator<FamilyPlanTaskDto> iter = tasksColl.iterator();
				while (iter.hasNext()) {
					FamilyPlanTaskDto taskGoalValueBean = (FamilyPlanTaskDto) iter.next();
					/*
					 * SIR 1014770 If taskGoalValueBean.getDateApproval is null
					 * assign 1 to compareTo (set status code to 1)
					 */
					int compareTo = null == taskGoalValueBean.getDtApproved() ? 1
							: taskGoalValueBean.getDtApproved().compareTo(approvalDt);
					if (compareTo <= 0) {
						taskGoalValueBean.setCdStatus(FGDM_CONSTANTS_OLD);
						oldTasksList.add(taskGoalValueBean);
					} else {
						taskGoalValueBean.setCdStatus(FGDM_CONSTANTS_NEW);
						newTasksList.add(taskGoalValueBean);
					}
				}
			}
		} else {
			if (!ObjectUtils.isEmpty(tasksColl)) {
				Iterator<FamilyPlanTaskDto> iter = tasksColl.iterator();
				while (iter.hasNext()) {
					FamilyPlanTaskDto taskGoalValueBean = (FamilyPlanTaskDto) iter.next();
					if (ObjectUtils.isEmpty(taskGoalValueBean.getDtApproved())) {
						taskGoalValueBean.setCdStatus(FGDM_CONSTANTS_NEW);
						newTasksList.add(taskGoalValueBean);
					} else {
						taskGoalValueBean.setCdStatus(FGDM_CONSTANTS_OLD);
						oldTasksList.add(taskGoalValueBean);
					}
				}
			}
		}

		oldTasksList.sort(new Comparator<FamilyPlanTaskDto>() {
			@Override
			public int compare(FamilyPlanTaskDto o1, FamilyPlanTaskDto o2) {
				return o2.getIdFamilyPlanTask().intValue() - o1.getIdFamilyPlanTask().intValue();
			}
		});

		newTasksList.sort(new Comparator<FamilyPlanTaskDto>() {
			@Override
			public int compare(FamilyPlanTaskDto o1, FamilyPlanTaskDto o2) {
				return o2.getIdFamilyPlanTask().intValue() - o1.getIdFamilyPlanTask().intValue();
			}
		});
		oldTasksList.addAll(newTasksList);
		tasksList.addAll(oldTasksList);
		familyPlanItemBean.setFamilyPlanTaskDtoList(tasksList);
		return familyPlanItemBean;
	}

	/**
	 * 
	 * Method Name: saveFamilyPlanItemDtl Method Description:Saves the family
	 * plan item details to the database.
	 * 
	 * @param familyPlanItemDto
	 * @param familyPlanDto
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public FamilyPlanItemDtlRes saveFamilyPlanItemDtl(FamilyPlanItemDto familyPlanItemDto,
			FamilyPlanDto familyPlanDto) {
		FamilyPlanItemDtlRes familyPlanItemDtlRes = new FamilyPlanItemDtlRes();
		Date latestLastUpdate = familyPlanItemDtlDao
				.getFamilyPlanItemLastUpdateTime(familyPlanItemDto.getIdFamilyPlanItem());
		if (!familyPlanItemDto.getDtLastUpdate().equals(latestLastUpdate)) {
			ErrorDto errorDto = new ErrorDto();
			errorDto.setErrorCode(2046);
			familyPlanItemDtlRes.setErrorDto(errorDto);
		} else {
			boolean savingEvaluationData = false;
			EventDto mostRecentEvent = new EventDto();
			if (!ObjectUtils.isEmpty(familyPlanDto.getFamilyPlanEvaluationList())) {
				savingEvaluationData = true;
				// The first eval event in the list is the most recent event.
				FamilyPlanEvalDto mostRecentFamilyPlanEval = familyPlanDto.getFamilyPlanEvaluationList().get(0);
				mostRecentEvent = mostRecentFamilyPlanEval.getEvalEvent();

			} else {
				mostRecentEvent = eventDao.getEventByid(familyPlanDto.getFamilyPlanEvent().getIdEvent());
			}
			// If the event is pending approval and the user did not access the
			// family plan via an approval todo, or if the user accessed the
			// family
			// plan after navigating a stage closure approval todo, call
			// InvalidateApproval common function to invalidate the approval and
			// demote all related events. If the user accessed the family plan
			// after
			// navigating a stage closure approval todo, we want to invalidate
			// the
			// pending family plan so the user can close the stage without
			// leaving
			// a pending family plan. This will no longer be an issue if SIR
			// 19659
			// is accepted and implemented.
			if (CodesConstant.CEVTSTAT_PEND.equals(mostRecentEvent.getCdEventStatus())
					&& (!familyPlanDto.isApprovalMode() || familyPlanDto.isApprovalModeForStageClosure())) {
				ApprovalCommonInDto approvalCommonInDto = new ApprovalCommonInDto();
				approvalCommonInDto.setIdEvent(mostRecentEvent.getIdEvent());
				approvalCommonService.callCcmn05uService(approvalCommonInDto);

				callPostEvent(mostRecentEvent, familyPlanDto);
			}
			/*
			 * Set the family plan or eval event id based on the whatever
			 * current event is present in the mostRecentEvent event value bean.
			 * If current family plan is family plan evaluation then
			 * bSavingEvaluationData will be true and isAddressedInFamilyPlan
			 * will be false and vice versa.
			 */
			familyPlanItemDto.setIdEvent(mostRecentEvent.getIdEvent());
			if (savingEvaluationData) {
				familyPlanItemDto.setIndAddressedFamPlan(ServiceConstants.N);
			} else {
				familyPlanItemDto.setIndAddressedFamPlan(ServiceConstants.Y);
			}
			/*
			 * Update ID_ADDRESSED_EVENT and IND_ADDRESSED_FAM_PLAN of
			 * FAMILY_PLAN_ITEM table only if if flags
			 * isAddressedInFamilyPlanColNull and isIdAddressedEventColNull are
			 * both set to N implying both columns are null and should be
			 * updated.
			 */
			if (ServiceConstants.Y.equals(familyPlanItemDto.getIndAddressedInFamilyPlanColNull())
					&& ServiceConstants.Y.equals(familyPlanItemDto.getIndIdAddressedEventColNull())) {
				familyPlanItemDtlDao.updateFamilyPlanItem(familyPlanItemDto,
						UPDATE_ID_ADDRESSED_EVENT_AND_IND_ADDRESSED_FAM_PLAN);
			} else {
				familyPlanItemDtlDao.updateFamilyPlanItem(familyPlanItemDto,
						UPDATE_WITHOUT_ID_ADDRESSED_EVENT_AND_IND_ADDRESSED_FAM_PLAN);
			}
			// Save the evaluation item if:
			// 1) the user is saving evaluation data, and
			// 2) the evaluation item has data, and
			// 3) if evaluation item's eval event has not yet been approved
			List<FamilyPlanEvalItemDto> evalItemsList = familyPlanItemDto.getFamilyPlanEvalItemDtoList();
			if (savingEvaluationData && !ObjectUtils.isEmpty(evalItemsList)) {
				FamilyPlanEvalItemDto evalItem = evalItemsList.get(0);
				if (!CodesConstant.CEVTSTAT_APRV.equals(mostRecentEvent.getCdEventStatus())) {
					if (!ObjectUtils.isEmpty(evalItem.getOperation())
							&& ServiceConstants.REQ_FUNC_CD_DELETE.equals(evalItem.getOperation())
							&& !ObjectUtils.isEmpty(evalItem.getIdFamilyPlanEvalItem())
							&& evalItem.getIdFamilyPlanEvalItem() > 0L
							&& mostRecentEvent.getIdEvent().equals(evalItem.getIdEvent())) {
						familyPlanItemDtlDao.deleteFamilyPlanEvalItem(evalItem);
					} else if (!ObjectUtils.isEmpty(evalItem.getIdFamilyPlanEvalItem())
							&& evalItem.getIdFamilyPlanEvalItem() > 0L
							&& mostRecentEvent.getIdEvent().equals(evalItem.getIdEvent())) {
						familyPlanItemDtlDao.updateFamilyPlanEvalItem(evalItem);
					} else if (ObjectUtils.isEmpty(evalItem.getIdFamilyPlanEvalItem())
							&& (!ObjectUtils.isEmpty(evalItem.getTxtNewConcerns())
									|| !ObjectUtils.isEmpty(evalItem.getTxtItemEvaluation()))) {
						evalItem.setIdCase(familyPlanItemDto.getIdCase());
						evalItem.setIdFamilyPlanItem(familyPlanItemDto.getIdFamilyPlanItem());
						evalItem.setIdEvent(familyPlanItemDto.getIdEvent());
						familyPlanItemDtlDao.updateFamilyPlanEvalItem(evalItem);
					}
				}
			}
			if (savingEvaluationData && !ObjectUtils.isEmpty(familyPlanItemDto.getFamilyPlanTaskDtoList())
					&& !familyPlanItemDto.getFamilyPlanTaskDtoList().isEmpty()) {
				familyPlanItemDtlDao.updateFamilPlanTasks(familyPlanItemDto.getFamilyPlanTaskDtoList(),
						familyPlanDto.getFamilyPlanEvaluationList().get(0).getIdEvent());
			}
			// If the stage closure event is pending approval, and the user did
			// not
			// access the stage via an approval todo, invalidate it. Then set
			// the
			// stage closure event status to COMP.
			if (!ObjectUtils.isEmpty(familyPlanDto.getStageClosureEvent())
					&& CodesConstant.CEVTSTAT_PEND.equals(familyPlanDto.getStageClosureEvent().getCdEventStatus())
					&& (!familyPlanDto.isApprovalMode())) {
				ApprovalCommonInDto approvalCommonInDto = new ApprovalCommonInDto();
				approvalCommonInDto.setIdEvent(familyPlanDto.getStageClosureEvent().getIdEvent());
				approvalCommonService.callCcmn05uService(approvalCommonInDto);
				familyPlanItemDtlDao.updateEventStatusWithoutTimestamp(
						familyPlanDto.getStageClosureEvent().getCdEventStatus(),
						familyPlanDto.getStageClosureEvent().getIdEvent());
			}
			familyPlanItemDtlRes.setIdFamilyPlanItem(familyPlanItemDto.getIdFamilyPlanItem());
		}
		return familyPlanItemDtlRes;
	}

	/**
	 * resets the values from family_plan_item table.
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long deleteFamilyPlanItem(FamilyPlanItemDto familyPlanItemDto, FamilyPlanDto familyPlanDto) {
		// Get the most recent event for the family plan. It will either be the
		// family plan event (if it has not been approved), or it will be the
		// most recent family plan evaluation event.
		EventDto mostRecentEvent;
		if (!CodesConstant.CEVTSTAT_APRV.equals(familyPlanDto.getFamilyPlanEvent().getCdEventStatus())) {
			mostRecentEvent = eventDao.getEventByid(familyPlanDto.getFamilyPlanEvent().getIdEvent());
		} else {
			// The first eval event in the list is the most recent event.
			FamilyPlanEvalDto mostRecentFamilyPlanEval = familyPlanDto.getFamilyPlanEvaluationList().get(0);
			mostRecentEvent = mostRecentFamilyPlanEval.getEvalEvent();
		}
		// If the event is pending approval and the user did not access the
		// family plan via an approval todo, or if the user accessed the family
		// plan after navigating a stage closure approval todo, call
		// InvalidateApproval common function to invalidate the approval and
		// demote all related events. If the user accessed the family plan after
		// navigating a stage closure approval todo, we want to invalidate the
		// pending family plan so the user can close the stage without leaving
		// a pending family plan. This will no longer be an issue if SIR 19659
		// is accepted and implemented.
		if (CodesConstant.CEVTSTAT_PEND.equals(mostRecentEvent.getCdEventStatus())
				&& (!familyPlanDto.isApprovalMode() || familyPlanDto.isApprovalModeForStageClosure())) {
			ApprovalCommonInDto approvalCommonInDto = new ApprovalCommonInDto();
			approvalCommonInDto.setIdEvent(mostRecentEvent.getIdEvent());
			approvalCommonService.callCcmn05uService(approvalCommonInDto);
			callPostEvent(mostRecentEvent, familyPlanDto);
		}
		familyPlanItemDtlDao.deleteFamilyPlanItem(familyPlanItemDto);
		return familyPlanItemDto.getIdFamilyPlanItem();
	}

	/**
	 * 
	 * Method Name: callPostEvent Method Description:
	 * 
	 * @param eventDto
	 * @param familyPlanDto
	 * @return
	 */
	private Long callPostEvent(EventDto eventDto, FamilyPlanDto familyPlanDto) {
		PostEventIPDto postEventIPDto = new PostEventIPDto();
		postEventIPDto.setCdEventType(CodesConstant.CEVNTTYP_PLN);
		postEventIPDto.setCdTask(eventDto.getCdTask());
		postEventIPDto.setIdPerson(familyPlanDto.getIdUser());
		postEventIPDto.setIdStage(eventDto.getIdStage());
		if (!ObjectUtils.isEmpty(eventDto.getDtEventOccurred())) {
			postEventIPDto.setDtEventOccurred(eventDto.getDtEventOccurred());
		} else {
			postEventIPDto.setDtEventOccurred(new Date());
		}
		postEventIPDto.setCdEventStatus(CodesConstant.CEVTSTAT_PROC);
		String eventDesc = null;
		if (ServiceConstants.CD_TASK_FPR_FAM_PLAN.equals(eventDto.getCdTask())
				|| ServiceConstants.CD_TASK_FRE_FAM_PLAN.equals(eventDto.getCdTask())
				|| ServiceConstants.CD_TASK_FSU_FAM_PLAN.equals(eventDto.getCdTask())) {
			eventDesc = familyPlanDto.getCdStage() + ServiceConstants.CONSTANT_SPACE + familyPlanDto.getCdStageType()
					+ ServiceConstants.CONSTANT_SPACE + ServiceConstants.FAMILY_PLAN;
			// If the 'Date Plan Completed' field is not empty, the status of
			// the
			// family plan event should be COMP (or possibly PEND).
			if (!ObjectUtils.isEmpty(familyPlanDto.getDtCompleted())) {
				eventDesc += ServiceConstants.CONSTANT_SPACE + ServiceConstants.COMPLETED_LOWERCASE
						+ ServiceConstants.CONSTANT_SPACE + familyPlanDto.getDtCompleted();
				postEventIPDto.setCdEventStatus(CodesConstant.CEVTSTAT_COMP);
				// If the supervisor accessed the family plan via a family plan
				// approval
				// todo, and the status of the family plan event was PEND, it
				// should
				// remain PEND. The supervisor is allowed to make changes
				// without
				// invalidating the pending approval. If the supervisor accessed
				// the
				// family plan via a stage closure approval todo, however, the
				// status of
				// the family plan event should be set to COMP because the
				// family plan
				// approval will be invalidated. This will enable the supervisor
				// to
				// close the stage without leaving a pending family plan behind.
				if (familyPlanDto.isApprovalMode() && !familyPlanDto.isApprovalModeForStageClosure()) {
					postEventIPDto.setCdEventStatus(CodesConstant.CEVTSTAT_PEND);
				}
			}
		} else if (ServiceConstants.CD_TASK_FPR_FAM_PLAN_EVAL.equals(eventDto.getCdTask())
				|| ServiceConstants.CD_TASK_FRE_FAM_PLAN_EVAL.equals(eventDto.getCdTask())
				|| ServiceConstants.CD_TASK_FSU_FAM_PLAN_EVAL.equals(eventDto.getCdTask())) {
			// Create the event description.
			if (ServiceConstants.CD_TASK_FPR_FAM_PLAN.equals(familyPlanDto.getFamilyPlanEvent().getCdEventTask())) {
				eventDesc = CodesConstant.CSTAGES_FPR;
			} else if (ServiceConstants.CD_TASK_FRE_FAM_PLAN
					.equals(familyPlanDto.getFamilyPlanEvent().getCdEventTask())) {
				eventDesc = CodesConstant.CSTAGES_FRE;
			} else if (ServiceConstants.CD_TASK_FSU_FAM_PLAN
					.equals(familyPlanDto.getFamilyPlanEvent().getCdEventTask())) {
				eventDesc = CodesConstant.CSTAGES_FSU;
			}
			eventDesc += ServiceConstants.CONSTANT_SPACE + familyPlanDto.getCdStageType()
					+ ServiceConstants.CONSTANT_SPACE + ServiceConstants.FAMILY_PLAN_EVENT
					+ ServiceConstants.CONSTANT_SPACE + familyPlanDto.getFamilyPlanEvent().getIdEvent()
					+ ServiceConstants.CONSTANT_SPACE + ServiceConstants.COMPLETED_LOWERCASE
					+ ServiceConstants.CONSTANT_SPACE + ServiceConstants.EVALUATED + ServiceConstants.CONSTANT_SPACE;
			if (!ObjectUtils.isEmpty(familyPlanDto.getDtCompleted())) {
				eventDesc += DateUtils.stringDt(familyPlanDto.getDtCompleted());
				postEventIPDto.setCdEventStatus(CodesConstant.CEVTSTAT_COMP);
				// If the supervisor accessed the family plan via a family plan
				// approval
				// todo, and the status of the family plan event was PEND, it
				// should
				// remain PEND. The supervisor is allowed to make changes
				// without
				// invalidating the pending approval. If the supervisor accessed
				// the
				// family plan via a stage closure approval todo, however, the
				// status of
				// the family plan event should be set to COMP because the
				// family plan
				// approval will be invalidated. This will enable the supervisor
				// to
				// close the stage without leaving a pending family plan behind.
				if (familyPlanDto.isApprovalMode() && !familyPlanDto.isApprovalModeForStageClosure()) {
					postEventIPDto.setCdEventStatus(CodesConstant.CEVTSTAT_PEND);
				}
			}
		}
		postEventIPDto.setEventDescr(eventDesc);
		ServiceReqHeaderDto serviceReqHeaderDto = new ServiceReqHeaderDto();
		serviceReqHeaderDto.setReqFuncCd(ServiceConstants.ADD);
		if (!ObjectUtils.isEmpty(eventDto.getIdEvent()) && eventDto.getIdEvent() > 0L) {
			serviceReqHeaderDto.setReqFuncCd(ServiceConstants.UPDATE);
			postEventIPDto.setIdEvent(eventDto.getIdEvent());
			postEventIPDto.setTsLastUpdate(eventDto.getDtLastUpdate());
		}
		// Update EVENT record
		PostEventOPDto postEventOPDto = postEventService.checkPostEventStatus(postEventIPDto, serviceReqHeaderDto);
		return postEventOPDto.getIdEvent();
	}
}
