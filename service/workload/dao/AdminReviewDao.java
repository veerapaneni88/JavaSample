package us.tx.state.dfps.service.workload.dao;

import us.tx.state.dfps.common.domain.AdminAllegation;
import us.tx.state.dfps.common.domain.AdminReview;
import us.tx.state.dfps.service.person.dto.AllegationDto;
import us.tx.state.dfps.service.workload.dto.AdminReviewDto;

import java.util.List;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: CCMN03U Class
 * Description: AdminReview DAO Interface Apr 3, 2017 - 3:45:39 PM
 */

public interface AdminReviewDao {

	/**
	 * This dam will update rows on the Admin Review table.
	 * 
	 * Service Name: CCMN03U, DAM Name : CAUDA3D
	 * 
	 * @param adminReview
	 * @
	 */
	public void saveAdminReview(AdminReview adminReview);

	/**
	 * This dam will update rows on the Admin Review table.
	 * 
	 * Service Name: CCMN03U, DAM Name : CAUDA3D
	 * 
	 * @param adminReview
	 * @
	 */
	public void updateAdminReview(AdminReview adminReview);

	/**
	 * This dam will update rows on the Admin Review table.
	 * 
	 * Service Name: CCMN03U, DAM Name : CAUDA3D
	 * 
	 * @param adminReview
	 * @
	 */
	public void deleteAdminReview(AdminReview adminReview);

	/**
	 * This dam will update rows on the Admin Review table.
	 * 
	 * Service Name: CCMN03U, DAM Name : CAUDA3D
	 * 
	 * @param idAdminReview
	 * @
	 */
	public AdminReview getAdminReviewById(Long idAdminReview);

	/**
	 * This dam will get Admin Review stage is open for this person in this investigation.
	 *
	 * DAM Name : CSEC62D
	 *
	 * @param personId,stageId
	 * @
	 */
	public List<Long> getAdminReviewOpenStagesByPerson(Long personId, Long stageId);

	/**
	 * This dam will get open Admin Review already exists .
	 *
	 * DAM Name : CSEC64D
	 *
	 * @param stageId
	 * @
	 */
	public List<Long> getAdminReviewOpenExists(Long stageId);

	/**
	 * This dam will get Admin Review stage is open for this stage in this investigation.
	 *
	 * DAM Name : CSEC63D
	 *
	 * @param stageId
	 * @
	 */
	public AdminReviewDto getAdminReviewOpenStagesByStageId(Long stageId);

	public List<AllegationDto> getAllegationsByStageIdPersonId(Long idStage, Long idPerson) ;

	public void saveAdminAllegation(AdminAllegation adminAllegation);

}
