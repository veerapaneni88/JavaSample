package us.tx.state.dfps.service.prt.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.prt.dao.PRTActPlanFollowUpDao;
import us.tx.state.dfps.service.prt.dao.PRTActionPlanDao;
import us.tx.state.dfps.service.prt.dao.PRTActionPlanDataFetchDao;
import us.tx.state.dfps.service.prt.dto.PRTConnectionDto;
import us.tx.state.dfps.service.prt.dto.PRTPersonLinkDto;
import us.tx.state.dfps.service.prt.service.PRTActPlanFollowUpService;
import us.tx.state.dfps.service.prt.service.PRTActionPlanService;
import us.tx.state.dfps.service.subcare.dto.PRTActPlanFollowUpDto;
import us.tx.state.dfps.service.subcare.dto.PRTActionPlanDto;

@Service
@Transactional
public class PRTActPlanFollowUpUtil {

	@Autowired
	MessageSource messageSource;

	@Autowired
	PRTActionPlanDataFetchDao prtActionPlanDataFetchDao;

	@Autowired
	PRTActPlanFollowUpDao prtActPlanFollowUpDao;

	@Autowired
	PRTActionPlanUtils prtActionPlanUtils;

	@Autowired
	PRTActionPlanDao prtActionPlanDao;

	@Autowired
	PRTActPlanFollowUpService prtActPlanFollowUpService;

	@Autowired
	PRTActionPlanService prtActionPlanService;

	/**
	 * Method Name: populateFollowUpUnitInfo Method Description:This method
	 * populates Unit Info of the Worker for the Follow Up.
	 * 
	 * @param actionPlan
	 *            - The dto will hold the input paramters for fetching the unit
	 *            and region.
	 * @param followUp
	 *            - The dto holds the PRT Follow-Up details.
	 */
	public void populateFollowUpUnitInfo(PRTActionPlanDto actionPlan, PRTActPlanFollowUpDto followUp) {
		if (!ObjectUtils.isEmpty(actionPlan.getIdUnitWorker()) && actionPlan.getIdUnitWorker() != 0) {
			prtActionPlanDataFetchDao.fetchAndPopulatePRUnitUsingIdUnit(actionPlan);
			// Fetch Unit Number of the Primary Worker of the Stage.

			followUp.setIdUnitWorker(actionPlan.getIdUnitWorker());
			followUp.setCdRegionWorker(actionPlan.getCdRegionWorker());
			followUp.setUnitWorker(actionPlan.getUnitWorker());
		}

	}

	/**
	 * Method Name: populateFollowUpConnectionsInPROC Method Description:This
	 * method is used to populate the connections for each child in a PRT
	 * Follow-Up in PROC status.
	 * 
	 * @param followUp
	 *            - The dto will hold the PRT Follow-Up details.
	 */
	public void populateFollowUpConnectionsInPROC(PRTActPlanFollowUpDto followUp) {

		List<PRTPersonLinkDto> prtPersonList = followUp.getChildren();

		for (PRTPersonLinkDto child : prtPersonList) {

			List<PRTConnectionDto> prtConnections = new ArrayList<PRTConnectionDto>();

			// Follow-Up after a child exits the PRT
			if (child.getFollowUpStatus() != PRTPersonLinkDto.ChildFollowUpStatus.APFU_AFTER_CHILDEXITEDPRT) {

				prtConnections = prtActPlanFollowUpDao.selectPRTConnections(child.getIdPrtPersonLink(),
						child.getIdChildSUBStage());
			}

			// If the Child has already NOT Exited the Care,
			// get the connections form SPL and merge them.
			if (child.getFollowUpStatus() != PRTPersonLinkDto.ChildFollowUpStatus.APFU_AFTER_CHILDEXITEDPRT) {
				// Then Get People from Stage Person Link.
				List<PRTConnectionDto> connFromSpl = prtActionPlanDataFetchDao.fetchConnectionsForStage(
						ObjectUtils.isEmpty(child.getIdChildSUBStage()) ? 0l : child.getIdChildSUBStage());

				// Merge both of the above.
				for (PRTConnectionDto conn : connFromSpl) {
					if (!prtActionPlanUtils.isPersonInConnList(prtConnections, conn.getIdPerson())) {
						prtConnections.add(conn);
					}
				}
			}
			// set the connections to the child.
			child.setPrtConnectionValueDtoList(prtConnections);
		}
	}

	/**
	 * Method Name: populateFollowUpConnectionsInCOMP Method Description:This
	 * method is used to populate the connections for each child in a PRT
	 * Follow-Up in COMP status.
	 * 
	 * @param followUp
	 *            - The dto will hold the PRT Follow-Up details.
	 */

	public void populateFollowUpConnectionsInCOMP(PRTActPlanFollowUpDto followUp) {

		// Get Person List.
		List<PRTPersonLinkDto> prtPersonList = followUp.getChildren();

		for (PRTPersonLinkDto child : prtPersonList) {
			List<PRTConnectionDto> prtConnections = new ArrayList<PRTConnectionDto>();

			// Follow-Up after a child exits the PRT
			if (child.getFollowUpStatus() == PRTPersonLinkDto.ChildFollowUpStatus.APFU_AFTER_CHILDEXITEDPRT) {
				// Do not list the child in Connections Section after they exit
				// the PRT.
			}
			// Follow-Up in which a child exits the PRT
			// Subsequent Follow-Ups.
			// First Follow Up
			else {
				prtConnections = prtActPlanFollowUpDao.selectPRTConnections(child.getIdPrtPersonLink(),
						child.getIdChildSUBStage());
			}

			// set the connections to the child.
			child.setPrtConnectionValueDtoList(prtConnections);
		}

	}

	/**
	 * Method Name: calculateFollowUpMonth Method Description:The system shall
	 * calculate the PRT Action Plan follow-up month number as follows: Month of
	 * PRT Action Plan Follow-Up = Number of calendar months since the PRT
	 * Action Plan was completed.
	 * 
	 * @param followUp
	 *            - The dto will hold the PRT Follow-Up details.
	 */
	public void calculateFollowUpMonth(PRTActPlanFollowUpDto followUp) {

		// Get Action Plan associated with Follow Up
		PRTActionPlanDto actionPlan = prtActionPlanDao.selectActionPlan(followUp.getIdPrtActionPlan());
		if (actionPlan.getIdPrtActionPlan() != 0 && !ObjectUtils.isEmpty(actionPlan.getDtComplete())) {
			Calendar startCalendar = new GregorianCalendar();
			startCalendar.setTime(actionPlan.getDtComplete());
			Calendar endCalendar = new GregorianCalendar();
			endCalendar.setTime(new Date());

			Long diffYear = (long) (endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR));
			Long diffMonth = diffYear * 12 + endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH);

			followUp.setFollowupMonth(diffMonth);
		}
	}

	/**
	 * Method Name: getChildrenForMostRecentFollowUpWithPermStatus Method
	 * Description:This method reads Children for Most Recent Followup With Perm
	 * Status not null for the Action Plan.
	 * 
	 * @param idActionPlan
	 *            - The id Actio Plan.
	 * @return List - The list of children in the PRT Action Plan.
	 */
	public List<PRTPersonLinkDto> getChildrenForMostRecentFollowUpWithPermStatus(Long idActionPlan) {

		List<PRTPersonLinkDto> followUpChildrenWithPermstatus = new ArrayList<PRTPersonLinkDto>();

		// The system will populate the previous permanency status field with
		// the permanency status from the most recent Follow-Up where the
		// permanency status is not null.
		Long idLatestFollowUpWithPermstatus = prtActPlanFollowUpDao.selectLatestFollowUpWithPermStatus(idActionPlan);
		if (idLatestFollowUpWithPermstatus != 0) {
			// Get the Children for this follow Up.
			followUpChildrenWithPermstatus = prtActPlanFollowUpDao.selectPrtChildren(idLatestFollowUpWithPermstatus);
		}
		// If there is no latest follow up with Perm Status populated then, get
		// the values from Action Plan
		else {
			// Get the Children for this follow Up.
			followUpChildrenWithPermstatus = prtActPlanFollowUpDao.selectPrtChildren(idActionPlan);
		}

		return followUpChildrenWithPermstatus;
	}

	/**
	 * Method Name: getFollowUpChildRec Method Description:This method gets the
	 * FollowUp Child Record with idPerson.
	 * 
	 * @param latestFollowUp
	 *            - The dto which holds the latest PRT Follow-Up detail.
	 * @param idChild
	 *            - The id person of the child.
	 * @return PRTPersonLinkDto - The dto holds the child details.
	 */
	public PRTPersonLinkDto getFollowUpChildRec(PRTActPlanFollowUpDto latestFollowUp, Long idChild) {

		PRTPersonLinkDto latestFollowUpChild = new PRTPersonLinkDto();

		if (!ObjectUtils.isEmpty(latestFollowUp.getIdPrtActplnFollowup())
				&& latestFollowUp.getIdPrtActplnFollowup() != ServiceConstants.ZERO_VAL) {
			for (PRTPersonLinkDto child : latestFollowUp.getChildren()) {
				if (child.getIdPerson().equals(idChild)) {
					latestFollowUpChild = child;
					break;
				}
			}
		}

		return latestFollowUpChild;

	}

	/**
	 * Method Name: populateChildFollowUpStatus Method Description:This method
	 * Populates Child's Follow Up Status
	 * 
	 * @param idLatestFollowUp
	 * @param latestFollowUpChild
	 * @param newChild
	 * @param currentFollowUp
	 */
	public void populateChildFollowUpStatus(Long idLatestFollowUp, PRTPersonLinkDto latestFollowUpChild,
			PRTPersonLinkDto newChild, PRTActPlanFollowUpDto currentFollowUp) {
		// If Child has PRT Exit Date set in Most Recent Follow Up then,
		// followUpAfterChildExitedPRT = true.
		if (!ObjectUtils.isEmpty(idLatestFollowUp) && idLatestFollowUp != 0
				&& !ObjectUtils.isEmpty(latestFollowUpChild.getDtPrtExit())) {
			// Follow-Up after a child exits the PRT
			newChild.setFollowUpStatus(PRTPersonLinkDto.ChildFollowUpStatus.APFU_AFTER_CHILDEXITEDPRT);
		} else {
			// Follow-Up in which a child exits the PRT
			boolean childExitingPRT = prtActPlanFollowUpService.isChildExitingPRT(newChild.getIdPerson());
			if (childExitingPRT || CodesConstant.CPRTFLTY_40.equals(currentFollowUp.getCdType())) {
				newChild.setFollowUpStatus(PRTPersonLinkDto.ChildFollowUpStatus.APFU_INWHICH_CHILDEXITEDPRT);
			} else if (!ObjectUtils.isEmpty(idLatestFollowUp) && idLatestFollowUp != 0) {
				// Subsequent Follow-Ups.
				newChild.setFollowUpStatus(PRTPersonLinkDto.ChildFollowUpStatus.SUBSEQUENT_APFU);
			} else {
				// First Follow Up
				newChild.setFollowUpStatus(PRTPersonLinkDto.ChildFollowUpStatus.FIRST_APFU);
			}
		}
	}

	/**
	 * Method Name: getPrevPermStatus Method Description:This method reads Perm
	 * Status of Previous Action Plan or Follow Up.
	 * 
	 * @param followUpChildrenWithPermstatus
	 * @param actPlnChild
	 * @param newChild
	 * @return
	 */
	public Long getPrevPermStatus(List<PRTPersonLinkDto> followUpChildrenWithPermstatus, PRTPersonLinkDto actPlnChild,
			PRTPersonLinkDto newChild) {
		// Previous Permanency Status - 1st FollowUp = Action Plan
		// Subsequent Follow-Ups = Previous Follow-Up
		// Follow-Up in which a child exits the PRT = Previous Follow-Up
		// Follow-Up after a child exits the PRT = Do Not Display
		Long permStatus = 0l;

		// Follow-Up after a child exits the PRT
		if (newChild.getFollowUpStatus() == PRTPersonLinkDto.ChildFollowUpStatus.APFU_AFTER_CHILDEXITEDPRT) {
			permStatus = 0l;
		}
		// Follow-Up in which a child exits the PRT
		// or
		// Subsequent Follow-Ups.

		else if ((!CollectionUtils.isEmpty(followUpChildrenWithPermstatus) && followUpChildrenWithPermstatus.size() > 0)
				&& (newChild.getFollowUpStatus() == PRTPersonLinkDto.ChildFollowUpStatus.APFU_INWHICH_CHILDEXITEDPRT
						|| newChild.getFollowUpStatus() == PRTPersonLinkDto.ChildFollowUpStatus.SUBSEQUENT_APFU)) {
			// permanency status from the most recent Follow-Up where the
			// permanency status is not null.
			for (PRTPersonLinkDto followUpChildWithPermstatus : followUpChildrenWithPermstatus) {
				if (!ObjectUtils.isEmpty(followUpChildWithPermstatus.getIdPerson())
						&& followUpChildWithPermstatus.getIdPerson().equals(newChild.getIdPerson())) {
					permStatus = followUpChildWithPermstatus.getIdPrtPermStatusLookup();
					break;
				}
			}
		} else // First FollowUp
		{
			permStatus = actPlnChild.getIdPrtPermStatusLookup();
		}

		return permStatus;
	}

	/**
	 * Method Name: initiateNewFollowUpConnections Method Description:This
	 * method retrieves all PRT Connections.
	 * 
	 * @param newChild
	 * @param latestFollowUpChild
	 * @param actPlnChild
	 * @return
	 */
	public List<PRTConnectionDto> initiateNewFollowUpConnections(PRTPersonLinkDto newChild,
			PRTPersonLinkDto latestFollowUpChild, PRTPersonLinkDto actPlnChild)

	{
		List<PRTConnectionDto> prtConnections = new ArrayList<PRTConnectionDto>();

		Long idCurrentSUBStage = newChild.getIdChildSUBStage();

		// Follow-Up after a child exits the PRT
		if (newChild.getFollowUpStatus() == PRTPersonLinkDto.ChildFollowUpStatus.APFU_AFTER_CHILDEXITEDPRT) {
		}
		// Follow-Up in which a child exits the PRT
		else if (newChild.getFollowUpStatus() == PRTPersonLinkDto.ChildFollowUpStatus.APFU_INWHICH_CHILDEXITEDPRT) {
			if (!ObjectUtils.isEmpty(latestFollowUpChild.getIdPrtPersonLink())
					&& latestFollowUpChild.getIdPrtPersonLink() != 0) {
				// Saved Connections only from previous Follow-Up
				prtConnections = prtActPlanFollowUpDao.selectPRTConnections(latestFollowUpChild.getIdPrtPersonLink(),
						idCurrentSUBStage);
			} else {
				// Saved Connections only from Action Plan
				prtConnections = prtActPlanFollowUpDao.selectPRTConnections(actPlnChild.getIdPrtPersonLink(),
						idCurrentSUBStage);
			}
		}
		// Subsequent Follow-Ups.
		else if (newChild.getFollowUpStatus() == PRTPersonLinkDto.ChildFollowUpStatus.SUBSEQUENT_APFU) {
			// Saved Connections only from previous Follow-Up
			prtConnections = prtActPlanFollowUpDao.selectPRTConnections(latestFollowUpChild.getIdPrtPersonLink(),
					idCurrentSUBStage);
		}
		// First Follow Up
		else if (newChild.getFollowUpStatus() == PRTPersonLinkDto.ChildFollowUpStatus.FIRST_APFU) {
			// Saved Connections only from Action Plan
			prtConnections = prtActPlanFollowUpDao.selectPRTConnections(actPlnChild.getIdPrtPersonLink(),
					idCurrentSUBStage);
		}

		// Remove Id Person Link from Connections.
		for (PRTConnectionDto conn : prtConnections) {
			conn.setIdSrcPersonLink(conn.getIdPrtPersonLink());
			conn.setSelectedByUser(true);
			conn.setIdPrtPersonLink(0l);
			conn.setIdPrtConnection(0l);
		}

		// For Subsequent Follow-Ups, First FollowUps, get the connections form
		// SPL and
		// merge them.
		if (newChild.getFollowUpStatus() == PRTPersonLinkDto.ChildFollowUpStatus.APFU_INWHICH_CHILDEXITEDPRT
				|| newChild.getFollowUpStatus() == PRTPersonLinkDto.ChildFollowUpStatus.SUBSEQUENT_APFU
				|| newChild.getFollowUpStatus() == PRTPersonLinkDto.ChildFollowUpStatus.FIRST_APFU) {
			// Then Get People from Stage Person Link.
			List<PRTConnectionDto> connFromSpl = prtActionPlanDataFetchDao.fetchConnectionsForStage(idCurrentSUBStage);

			// Merge both of the above.
			for (PRTConnectionDto conn : connFromSpl) {
				if (!prtActionPlanUtils.isPersonInConnList(prtConnections, conn.getIdPerson())) {
					prtConnections.add(conn);
				}
			}
		}

		return prtConnections;
	}

	/**
	 * Method Name: refreshPlacementAndChildPlan Method Description:This method
	 * loads Latest Placement and Child Plan for all the kids in the PRT.
	 * 
	 * @param idActionPlan
	 * @param children
	 */
	public void refreshPlacementAndChildPlan(Long idActionPlan, List<PRTPersonLinkDto> children) {

		// First Get the Stage Ids for Action Plan.
		List<Long> stageIdList = prtActionPlanService.getStageIdsForActPlan(idActionPlan);

		// Populate Latest Placements for the given stages.
		prtActionPlanDataFetchDao.selectAndPopulateLatestPlacement(children);

		// Get Latest Child Plan for the given stages and populate Children.
		prtActionPlanDataFetchDao.fetchAndPopulateLatestChildPlans(stageIdList, children);
	}

	/**
	 * Method Name: populateChildFollowUpStatusInCOMP Method Description: This
	 * method Populates Child's Follow Up Status
	 * 
	 * @param idLatestFollowUp
	 * @param latestFollowUpChild
	 * @param newChild
	 * @param currentFollowUp
	 */
	public void populateChildFollowUpStatusInCOMP(Long idLatestFollowUp, PRTPersonLinkDto latestFollowUpChild,
			PRTPersonLinkDto newChild, PRTActPlanFollowUpDto currentFollowUp) {
		// If Child has PRT Exit Date set in Most Recent Follow Up then,
		// followUpAfterChildExitedPRT = true.
		if (!ObjectUtils.isEmpty(idLatestFollowUp) && idLatestFollowUp != 0
				&& !ObjectUtils.isEmpty(latestFollowUpChild.getDtPrtExit())) {
			// Follow-Up after a child exits the PRT
			newChild.setFollowUpStatus(PRTPersonLinkDto.ChildFollowUpStatus.APFU_AFTER_CHILDEXITEDPRT);
		} else {
			// Follow-Up in which a child exits the PRT

			if (!ObjectUtils.isEmpty(newChild.getDtPrtExit())
					|| CodesConstant.CPRTFLTY_40.equals(currentFollowUp.getCdType())) {
				newChild.setFollowUpStatus(PRTPersonLinkDto.ChildFollowUpStatus.APFU_INWHICH_CHILDEXITEDPRT);
			} else if (!ObjectUtils.isEmpty(idLatestFollowUp) && idLatestFollowUp != 0) {
				// Subsequent Follow-Ups.
				newChild.setFollowUpStatus(PRTPersonLinkDto.ChildFollowUpStatus.SUBSEQUENT_APFU);
			} else {
				// First Follow Up
				newChild.setFollowUpStatus(PRTPersonLinkDto.ChildFollowUpStatus.FIRST_APFU);
			}
		}
	}
}
