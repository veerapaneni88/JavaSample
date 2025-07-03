package us.tx.state.dfps.service.prt.dao;

import java.util.List;

import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.legal.dto.LegalStatusDto;
import us.tx.state.dfps.service.prt.dto.PRTConnectionDto;
import us.tx.state.dfps.service.prt.dto.PRTPersonLinkDto;
import us.tx.state.dfps.service.subcare.dto.PRTActionPlanDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Oct 31, 2017- 5:56:44 PM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface PRTActionPlanDataFetchDao {

	/**
	 * Method Name: selectPCForStagesWithPMCLegalStatus Method Description:This
	 * method fetches Primary Children for Stages with PMC Legal Status
	 * 
	 * @param stageIdList
	 * @return List<PRTPersonLinkValueDto>
	 * @throws DataNotFoundException
	 */
	public List<PRTPersonLinkDto> selectPCForStagesWithPMCLegalStatus(List<Long> stageIdList);

	/**
	 * Method Name: fetchAndPopulateLatestChildPlans Method Description:Thiss
	 * method fetches Latest Child Plan with Primary and Concurrent Goals for
	 * all the Children.
	 * 
	 * @param stageIdList
	 * @param children
	 * @return Long
	 * @throws DataNotFoundException
	 */
	public List<PRTPersonLinkDto> fetchAndPopulateLatestChildPlans(List<Long> stageIdList,
			List<PRTPersonLinkDto> children);

	/**
	 * Method Name: selectAndPopulateLatestPlacement Method Description:This
	 * method fetches Latest Placement for all the Children.
	 * 
	 * @param linkValueDtos
	 * @return
	 * @throws DataNotFoundException
	 */
	public Long selectAndPopulateLatestPlacement(List<PRTPersonLinkDto> linkValueDtos);

	/**
	 * Method Name: fetchOpenActionPlan Method Description:This method gets Open
	 * Action Plan Id for the given Person.
	 * 
	 * @param idPerson
	 * @return Long
	 * @throws DataNotFoundException
	 */
	public Long fetchOpenActionPlan(long idPerson);

	/**
	 * Method Name: fetchAndPopulatePRUnitUsingIdUnit Method Description:
	 * 
	 * @param prtActionPlanDto
	 */
	public Long fetchAndPopulatePRUnitUsingIdUnit(PRTActionPlanDto prtActionPlanDto);

	/**
	 * Method Name: fetchConnectionsForStage Method Description:This method
	 * fetches Connections for the given Stage.
	 * 
	 * @param idChildSUBStage
	 * @return List<PRTConnectionValueDto>
	 */
	public List<PRTConnectionDto> fetchConnectionsForStage(long idChildSUBStage);

	/**
	 * Method Name: fetchActionPlanforPerson Method Description:This method gets
	 * Action Plan for the Child.
	 * 
	 * @param idPerson
	 * @param cevtstatProc
	 * @return Long
	 * @throws DataNotFoundException
	 */
	public Long fetchActionPlanforPerson(long idPerson, String eventStatus);

	/**
	 * This method fetches Placement for all the Children.
	 *
	 * @param children
	 *            the children
	 * @return the long
	 * @throws DataNotFoundException
	 *             the data not found exception
	 */
	Long selectAndPopulatePlcmtUsingIdPlcmt(List<PRTPersonLinkDto> children);

	/**
	 * Method Name: fetchAndPopulatePRUnit Method Description:This method
	 * fetches Unit Number of the Primary Worker of the Stage.
	 *
	 * @param pRtActionPlanDto
	 *            the rt action plan dto
	 * @param idStage
	 *            the id stage
	 * @return Long
	 */
	Long fetchAndPopulatePRUnit(PRTActionPlanDto pRtActionPlanDto, Long idStage);

	/**
	 * Method Name: fetchLatestLegalStatus Method Description: This method gets
	 * latest Legal Status for the Child.
	 * 
	 * @param Long
	 *            idPerson
	 * @return LegalStatusDto latestLegalStatus
	 */
	public LegalStatusDto fetchLatestLegalStatus(Long idPerson);

}
