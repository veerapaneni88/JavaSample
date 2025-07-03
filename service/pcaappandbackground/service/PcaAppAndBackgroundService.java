package us.tx.state.dfps.service.pcaappandbackground.service;

import java.util.List;

import us.tx.state.dfps.service.alternativeresponse.dto.EventValueDto;
import us.tx.state.dfps.service.common.request.CommonEventIdReq;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.PcaAppAndBackgroundReq;
import us.tx.state.dfps.service.common.request.PcaApplAndDetermReq;
import us.tx.state.dfps.service.common.response.CommonBooleanRes;
import us.tx.state.dfps.service.common.response.CommonIdRes;
import us.tx.state.dfps.service.common.response.PcaAppAndBackgroundRes;
import us.tx.state.dfps.service.common.response.StageRes;
import us.tx.state.dfps.service.pca.dto.PcaAppAndBackgroundDto;

/**
 * service-ejb-business- IMPACT PHASE 2 MODERNIZATION Class Description: This
 * interface will have business logic for fetch,save and submit PCA details
 * 
 * Nov 14, 2017- 2:31:19 PM © 2017 Texas Department of Family and Protective
 * Services
 */
public interface PcaAppAndBackgroundService {

	/**
	 * Method Name: fetchApplicationDetails
	 * 
	 * @param pcaAppAndBackgroundReq
	 * @return
	 */
	PcaAppAndBackgroundRes fetchApplicationDetails(PcaAppAndBackgroundReq pcaAppAndBackgroundReq);

	/**
	 * 
	 * Method Name: saveApplAndBackgroundInfo Method Description:This method
	 * saves Pca Application And Background information. It checks if the
	 * primary key is 0, if it is creates new Application, if not updates
	 * existing Application.
	 * 
	 * @param pcaApplAndDetermReq
	 * @return @
	 */
	CommonIdRes saveApplAndBackgroundInfo(PcaApplAndDetermReq pcaApplAndDetermReq);

	/**
	 * Method Name: createPCACICAStage Method Description:This method creates
	 * new PCA Stage for C-ICA Stage Type.
	 * 
	 * @param commonHelperReq
	 * @return
	 */
	StageRes createPCACICAStage(CommonHelperReq commonHelperReq);

	/**
	 * Method Name: findPriorPlcmtIds Method Description:This method returns
	 * prior placement ids for the same child and facility as the currently
	 * selected placement.
	 * 
	 * @param commonEventIdReq
	 * @return
	 */
	CommonIdRes findPriorPlcmtIds(CommonEventIdReq commonEventIdReq);

	/**
	 * Method Name: fetchPcaAppEvents Method Description:This method returns all
	 * the Events associated with PCA Application. (There can have two
	 * Application events one is SUB stage and another one in PCA stage for the
	 * same PCA Application.)
	 * 
	 * @param pcaAppAndBackgroundReq
	 * @return
	 */
	List<EventValueDto> fetchPcaAppEvents(PcaAppAndBackgroundReq pcaAppAndBackgroundReq);

	/**
	 * Method Name: fetchAppAndBackgound Method Description:This method returns
	 * Pca Application And Background information with Date of Birth.
	 * 
	 * @param pcaAppAndBackgroundReq
	 * @return
	 */
	PcaAppAndBackgroundRes fetchAppAndBackgound(PcaAppAndBackgroundReq pcaAppAndBackgroundReq);

	/**
	 * Method Name: selectLatestAppForStage Method Description:
	 * 
	 * @param pcaAppAndBackgroundReq
	 * @return
	 */
	PcaAppAndBackgroundRes selectLatestAppForStage(PcaAppAndBackgroundReq pcaAppAndBackgroundReq);

	/**
	 * Method Name: selectPrevApplication Method Description:
	 * 
	 * @param pcaAppAndBackgroundReq
	 * @return
	 */
	PcaAppAndBackgroundRes selectPrevApplication(PcaAppAndBackgroundReq pcaAppAndBackgroundReq);

	/**
	 * Method Name: selectLatestValidApplication Method Description:This method
	 * retrieves latest Valid Pca Application for the Child. (Application Status
	 * in ('PEND', 'COMP', 'APRV') and Eligibility Determination is not Child
	 * Disqualified or Child Not Qualified).
	 * 
	 * @param pcaAppAndBackgroundReq
	 * @return
	 */
	PcaAppAndBackgroundRes selectLatestValidApplication(PcaAppAndBackgroundReq pcaAppAndBackgroundReq);

	/**
	 * Method Name: selectLatestApplication Method Description:
	 * 
	 * @param pcaAppAndBackgroundReq
	 * @return
	 */
	PcaAppAndBackgroundRes selectLatestApplication(PcaAppAndBackgroundReq pcaAppAndBackgroundReq);

	/**
	 * Method Name: determineQualification Method Description:
	 * 
	 * @param pcaApplAndDetermReq
	 * @return @
	 */
	CommonBooleanRes determineQualification(PcaApplAndDetermReq pcaApplAndDetermReq);

	/**
	 * Method Name: submitPCAApplication Method Description:
	 * 
	 * @param pcaAppAndBackgroundReq
	 * @return @
	 */
	PcaAppAndBackgroundRes submitPCAApplication(PcaAppAndBackgroundReq pcaAppAndBackgroundReq);

	/**
	 * Method Name: updateAppEvent Method Description:
	 * 
	 * @param pcaAppAndBackgroundReq
	 * @return @
	 */
	Long updateAppEvent(PcaAppAndBackgroundDto pcaAppAndBackgroundDto, EventValueDto appEvent, Long idLastUpdatePerson,
			String evtStatus, String eventDesc);

	/**
	 * Method Name: findPriorSubStageId Method Description:
	 * 
	 * @param pcaAppAndBackgroundReq
	 * @return @
	 */
	Long findPriorSubStageId(PcaAppAndBackgroundReq pcaAppAndBackgroundReq);

}