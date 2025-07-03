/**
 *service-ejb-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Nov 15, 2017- 3:12:26 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.fsna.dao;

import java.util.List;

import us.tx.state.dfps.common.domain.CpsFsna;
import us.tx.state.dfps.common.dto.UserProfileDto;
import us.tx.state.dfps.fsna.dto.CpsFsnaDomainLookupDto;
import us.tx.state.dfps.fsna.dto.CpsFsnaDto;
import us.tx.state.dfps.service.common.request.SdmFsnaReq;
import us.tx.state.dfps.service.common.response.FSNAAssessmentDtlGetRes;

/**
 * 
 * service-business - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: Class
 * Description: May 3, 2018 - 12:57:20 PM
 */
public interface FSNADao {

	/**
	 * This method is to load Blank FSNA assessment record Method Description:
	 * 
	 * @param getFSNAAssessmentDtlReq
	 * @return
	 */
	List<CpsFsnaDomainLookupDto> queryFsna(String cdStage);

	/**
	 * This method is to load existing FSNA assessment Method Description:
	 * 
	 * @param getFSNAAssessmentDtlReq
	 * @return
	 */
	CpsFsna getFSNAAsmt(Long idEvent);

	/**
	 * This method is to save or update FSNA with responses and Strengths and
	 * needs if applicable Method Description:
	 * 
	 * @param cpsFsnaDto
	 * @return
	 */
	Long saveOrUpdateCpsFsna(SdmFsnaReq sdmFsnaReq);

	/**
	 * 
	 * Method Description:
	 * 
	 * @param cpsFsnaDto
	 */
	FSNAAssessmentDtlGetRes deleteSdmFsna(Long idEvent);

	/**
	 * 
	 * Method Description:
	 * 
	 * @param idCpsFsnaDomainLookup
	 * @return
	 */
	List<CpsFsnaDomainLookupDto> getDominLookUp(String cdStage);

	/**
	 * 
	 * Method Description:
	 * 
	 * @param cpsFsnaDto
	 * @param userProfileDB
	 * @return
	 */
	CpsFsnaDto completeAssessment(CpsFsnaDto cpsFsnaDto, UserProfileDto userProfileDB);

	/**
	 * 
	 * Method Description:
	 * 
	 * @param idStage
	 * @return
	 */
	List<CpsFsnaDto> getAllAssmnts(Long idStage);

	CpsFsnaDto getPersonFSNA(Long personId, Long stageId);
	/**
	 * 
	 * Method Description: this method is to get previous TxtDngrWorry
	 * 
	 * @param idStage
	 * @return
	 */
	CpsFsnaDto getPerviousStatements(Long idStage);

	/**
	 * Method Description: This method is to get previous Danger/Worry and Goal
	 * Statement Gets previous value from same stage which is not current
	 * assessment
	 * 
	 * @param cpsFsnaDto
	 * @param idUser
	 * @return cpsFsnaDto
	 */
	CpsFsnaDto getPerviousStatements(Long idStage, CpsFsna idCpsFsna);

	/**
	 * Method Name: getFSNAAsmtById Method Description:This method loads cps
	 * fsna using id.
	 * 
	 * @param idCpsFsna
	 * @return
	 */
	CpsFsna getFSNAAsmtById(Long idCpsFsna);

	/**
	 * Method Name: getMostRecentAprvInitialFsna Method Description:This method
	 * is used to retrieve the most recent approved FSNA event.
	 * 
	 * @param idStage
	 * @return Long
	 */
	public Long getMostRecentAprvInitialFsna(Long idStage);
		/**
	 * Method written for defect 16193
	 * this method will retrieve the latest record for primary care givers
	 * */
	List<CpsFsnaDto> getLatestAssmntsForPrimCaregiver(Long idStage);
}
