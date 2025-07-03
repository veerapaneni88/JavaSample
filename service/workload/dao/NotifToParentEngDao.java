package us.tx.state.dfps.service.workload.dao;

import java.util.Date;
import java.util.List;

import us.tx.state.dfps.common.dto.PersonAddressDto;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.workload.dto.AdminReviewDto;
import us.tx.state.dfps.service.workload.dto.PrimaryWorkerDto;
import us.tx.state.dfps.service.workload.dto.StageReviewDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Mar 5, 2018- 12:05:00 PM Â© 2017 Texas Department of
 * Family and Protective Services
 */
public interface NotifToParentEngDao {

	/**
	 * Method Name: getStageReviewed Method Description:This dam will retrieve
	 * all records from the Nam and Admin Allegation tables that are for a
	 * specified Admin Review stage. Dam Method: CLSC65D
	 * 
	 * @param notifToParentEngReq
	 * @return StageReviewDto
	 * @throws DataNotFoundException
	 */
	public List<StageReviewDto> getStageReviewed(Long idStage, Date dtNameEndDate, String allegPrior);

	/**
	 * Method Name: getPrimaryWorker Method Description: This DAM retrieves the
	 * Primary Worker (or Historical Primary if the stage is closed) for the
	 * input ID_STAGe. Dam Method: CSEC53D
	 * 
	 * @param notifToParentEngReq
	 * @return PrimaryWorkerDto
	 * @throws DataNotFoundException
	 */

	public PrimaryWorkerDto getPrimaryWorker(Long idStage);

	/**
	 * Method Name: getAdminReview Method Description: Retrieves admin review
	 * information Dam Method: CSES65D
	 * 
	 * @param idStage
	 * @return
	 * @throws DataNotFoundException
	 */
	public AdminReviewDto getAdminReview(Long idStage);

	/**
	 * Method Name: getPersonAddress Method Description:
	 * 
	 * @param idPerson
	 * @param cdPersAddrLinkType
	 * @param dtScrDtCurrentDate
	 * @return PersonAddressDto @
	 */
	public PersonAddressDto getPersonAddress(Long idPerson, String cdPersAddrLinkType, Date dtScrDtCurrentDate);
}
