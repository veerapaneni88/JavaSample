package us.tx.state.dfps.service.basepcasession.service;

import java.util.Date;

import us.tx.state.dfps.service.admin.dto.TodoCreateOutDto;
import us.tx.state.dfps.service.alternativeresponse.dto.EventValueDto;
import us.tx.state.dfps.service.pca.dto.PcaAppAndBackgroundDto;
import us.tx.state.dfps.service.pca.dto.PcaApplAndDetermDBDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * BasePcaSessionService for BasePcaSession Oct 6, 2017- 3:03:42 PM © 2017 Texas
 * Department of Family and Protective Services
 */

public interface BasePcaSessionService {

	/**
	 * Method Name: updateAppEvent Method Description: This method updates PCA
	 * Application Event Status. There can have two Application events one is
	 * SUB stage and another one in PCA stage for the same PCA Application. It
	 * first Queries PCA_APP_EVENT_LINK table to find all the events associated
	 * with Application and then updates all the events with new status.
	 * 
	 * @param idPcaEligApplication
	 * @param eventValueDto
	 * @param idUpdatePerson
	 * @param evtStatus
	 * @param eventDesc
	 * @return long @
	 */
	/*
	 * public long updateAppEvent(long idPcaEligApplication, EventValueDto
	 * eventValueDto, long idUpdatePerson, String evtStatus, String eventDesc) ;
	 */

	/**
	 * method name: updateEventStatus Description: This is helper function to
	 * update Event Status by calling PostEventBean.
	 * 
	 * @param eventValueDto
	 * @param idUpdatePerson
	 * @param evtStatus
	 * @param eventDesc
	 * @return long @
	 */
	/*
	 * public long updateEventStatus(EventValueDto eventValueDto, long
	 * idUpdatePerson, String evtStatus, String eventDesc) ;
	 */

	/**
	 * method name:createPcaRecertTodo description: This method creates Pca
	 * Recertification Todo for Primary Worker.
	 * 
	 * @param eventValueDto
	 * @param cdTodoInfoType
	 * @param dtToDoDue
	 * @param idPersonPC
	 * @
	 */
	public TodoCreateOutDto createPcaRecertTodo(EventValueDto eventValueDto, String cdTodoInfoType, Date dtToDoDue,
			long idPersonPC);

	/**
	 * method name: withdrawPCAApplication description: This method is called to
	 * withdraw the PCA Application process. It updates all the Events
	 * associated with the Application.
	 * 
	 * @param idPcaEligApplication
	 * @param eventValueDto
	 * @return long @
	 */
	public Long withdrawPCAApplication(PcaApplAndDetermDBDto pcaAppDB, long idPcaEligApplication,
			EventValueDto eventValueDto);

	/**
	 * method name: isChildSibling1 description: This method has the logic to
	 * find out if the Current child is Sibling 1.
	 * 
	 * @param pcaAppAndBackgroundDto
	 * @return boolean @
	 */
	public boolean isChildSibling1(PcaAppAndBackgroundDto pcaAppAndBackgroundDto);

	/**
	 * method name:findEligibilityOrPrimayWorkerForStage description: This
	 * function return if Eligibility Specialist that is assigned to stage. If
	 * None assigned returns Primary worker for Stage.
	 * 
	 * @param idStage
	 * @return long @
	 */
	public long findEligibilityOrPrimayWorkerForStage(long idStage);
}
