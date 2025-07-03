package us.tx.state.dfps.service.pcaappandbackground.dao;

import java.util.List;

import us.tx.state.dfps.service.alternativeresponse.dto.EventValueDto;
import us.tx.state.dfps.service.common.request.PcaAppAndBackgroundReq;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.pca.dto.PcaAppAndBackgroundDto;
import us.tx.state.dfps.service.pca.dto.PcaEligDeterminationDto;
import us.tx.state.dfps.service.workload.dto.StageDto;

/**
 * service-ejb-business- IMPACT PHASE 2 MODERNIZATION Class Description: This
 * interface will fetch,save and submit PCA details into respective database
 * details
 * 
 * Nov 14, 2017- 2:31:19 PM © 2017 Texas Department of Family and Protective
 * Services
 */

public interface PcaAppAndBackgroundDao {

	/**
	 * Method Name: selectPcaEligAppFromEvent Method Description:This method
	 * fetches data from PCA_ELIG_APPLICATION table using idPcaEligApplication
	 * 
	 * @param idAppEvent
	 * @return PcaAppAndBackgroundDto
	 */
	public PcaAppAndBackgroundDto selectPcaEligAppFromEvent(Long idAppEvent);

	/**
	 * Method Name: fetchPcaAppEvents Method Description:
	 * 
	 * @param idPcaEligApplication
	 * @return
	 */
	public List<EventValueDto> fetchPcaAppEvents(Long idPcaEligApplication);

	/**
	 * Method Name: insertPcaEligApplication Method Description:
	 * 
	 * @param appDto
	 * @return
	 */
	public Long insertPcaEligApplication(PcaAppAndBackgroundDto appDto);

	/**
	 * Method Name: updatePcaEligApplication Method Description:This method
	 * updates PCA_ELIG_APPLICATION table
	 * 
	 * @param appDto
	 * @return Long
	 * @throws DataNotFoundException
	 */
	public Long updatePcaEligApplication(PcaAppAndBackgroundDto appDto);

	/**
	 * Method Name: fetchPriorPlcmts Method Description:
	 * 
	 * @param idPlcmtEvent
	 * @return
	 */
	public List fetchPriorPlcmts(Long idPlcmtEvent);

	/**
	 * @param idQualSibPerson
	 * @param statusArray
	 * @return
	 */
	public PcaAppAndBackgroundDto selectLatestApplication(Long idQualSibPerson, String[] statusArray)
			throws DataNotFoundException;

	public PcaAppAndBackgroundDto selectLatestAppForStage(Long idStage);

	/**
	 * Method Name: markTodoComplete Method Description:This method marks all
	 * the Todos associate with the given event Complete by setting the end
	 * date.
	 * 
	 * @param idEvent
	 * @return Long
	 * @throws DataNotFoundException
	 */
	public Long markTodoComplete(Long idEvent);

	/**
	 * 
	 * Method Name: insertPcaAppEventLink Method Description: This method
	 * inserts record into PCA_APP_EVENT_LINK table.
	 * 
	 * @param idPcaEligApplication
	 * @param idPcaEligRecert
	 * @param idEvent
	 * @param idCase
	 * @param idLastUpdatePerson
	 * @return
	 */
	public Long insertPcaAppEventLink(Long idPcaEligApplication, Long idPcaEligRecert, Long idEvent, Long idCase,
			Long idLastUpdatePerson);

	/**
	 * Method Name: findLinkedSubStage Method Description:
	 * 
	 * @param idSubStage
	 * @param cstagesSub
	 * @return
	 */
	public StageDto findLinkedSubStage(Long idSubStage, String cstagesSub);

	/**
	 * Method Name: selectEligFromIdPcaApp Method Description:This method
	 * fetches data from PCA_ELIG_DETERM table using idPcaEligApplication
	 * 
	 * @param idPcaEligApplication
	 * @return
	 */
	public PcaEligDeterminationDto selectEligFromIdPcaApp(Long idPcaEligApplication);

	/**
	 * Method Name: selectPcaEligApplication Method Description:This method
	 * returns Pca Application And Background information with Date of Birth.
	 * 
	 * @param pcaAppAndBackgroundReq
	 * @return
	 */
	public PcaAppAndBackgroundDto selectPcaEligApplication(PcaAppAndBackgroundReq pcaAppAndBackgroundReq);

	/**
	 * Method Name: selectPrevApplication Method Description:
	 * 
	 * @param pcaAppAndBackgroundReq
	 * @return
	 */
	public PcaAppAndBackgroundDto selectPrevApplication(PcaAppAndBackgroundReq pcaAppAndBackgroundReq);

}
