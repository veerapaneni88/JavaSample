package us.tx.state.dfps.service.workload.dao;

import java.util.List;

import org.springframework.stereotype.Component;

import us.tx.state.dfps.service.common.request.AssignWorkloadReq;
import us.tx.state.dfps.service.workload.dto.AssignWorkloadDto;
import us.tx.state.dfps.service.workload.dto.GetSijsStatusFormDto;
import us.tx.state.dfps.service.workload.dto.MrefCssContactDtlsDto;
import us.tx.state.dfps.service.workload.dto.MrefDtlsDto;
import us.tx.state.dfps.service.workload.dto.MrefSecondaryDtlsDto;
import us.tx.state.dfps.service.workload.dto.SijsDtlsDto;
import us.tx.state.dfps.service.workload.dto.SijsEventContactDtlsDto;
import us.tx.state.dfps.service.workload.dto.SijsEventIdDtlsDto;
import us.tx.state.dfps.service.workload.dto.SijsLegalDtlsDto;
import us.tx.state.dfps.service.workload.dto.SijsSecondaryDtlsDto;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: CCMN14S Tuxedo
 * DAM Name: CCMN37D Class Description: Assign Workload DAO Interface Mar 23,
 * 2017 - 3:45:39 PM
 */

@Component
public interface AssignWorkloadDao {
	/**
	 * 
	 * Method Description: Method is implemented in AssignWorkloadDaoImpl to get
	 * workload details Tuxedo Servive Name:CCMN14S Tuxedo DAM Name: CCMN37D
	 * 
	 * @param assignWorkloadReq
	 * @return
	 * @throws Exception
	 */
	public List<AssignWorkloadDto> getAssignWorkloadDetails(AssignWorkloadReq assignWorkloadReq);

	/**
	 * 
	 * Method Description: Method is implemented in AssignWorkloadDaoImpl to get
	 * M-Ref details Tuxedo Servive Name:CINV69S Tuxedo DAM Name: CLSC0AD
	 * 
	 * @param ulIdPerson
	 * @param yesIndicator
	 * @return MrefDtlsDto
	 * @throws Exception
	 */

	public List<MrefDtlsDto> getMrefDtls(Long ulIdPerson, String yesIndicator);

	/**
	 * 
	 * Method Description: Method is implemented in AssignWorkloadDaoImpl to get
	 * Secondary Staff Assign details for M-Ref details Tuxedo Servive
	 * Name:CINV69S Tuxedo DAM Name: CLSC0BD
	 * 
	 * @param uIIdStage
	 * @param szCdRole
	 * @return MrefSecondaryDtlsDto
	 * @throws Exception
	 */

	public List<MrefSecondaryDtlsDto> getMrefSecondaryDtls(Long uIIdStage, String szCdRole);

	/**
	 * 
	 * Method Description: Method is implemented in AssignWorkloadDaoImpl to get
	 * CSS contact details for M-Ref details Tuxedo Servive Name:CINV69S Tuxedo
	 * DAM Name: CLSC0CD
	 * 
	 * @param uIIdStage
	 * @param szCdCssReviewFull
	 * @param szCdCssReviewOther
	 * @param szCdCssReviewScreened
	 * @return MrefCssContactDtlsDto
	 * @throws Exception
	 */
	public List<MrefCssContactDtlsDto> getMrefCssContactDtls(Long uIIdStage, String szCdCssReviewFull,
			String szCdCssReviewOther, String szCdCssReviewScreened);

	/**
	 * 
	 * Method Description: Method is implemented in AssignWorkloadDaoImpl to get
	 * SIJS details for cdStage IN AND NOT IN
	 * ('01','02','03','04','05','06','07','08','09','10','11'); Tuxedo Servive
	 * Name:CINV88S Tuxedo DAM Name: CLSC1CD
	 * 
	 * @param ulIdPerson
	 * @param szCdRegion
	 * @param szCdPersonCitizenship
	 * @return SijsDtlsDto
	 * @throws Exception
	 */

	public List<SijsDtlsDto> getSijsDtls(Long ulIdPerson, String szCdRegion, String szCdPersonCitizenship);

	/**
	 * 
	 * Method Description: Method is implemented in AssignWorkloadDaoImpl to get
	 * Assignees and their job class for the SIJS rows Tuxedo Servive
	 * Name:CINV88S Tuxedo DAM Name: CLSC1DD
	 * 
	 * @param ulIdStage
	 * @param szCdRole
	 * @return SijsSecondaryDtlsDto
	 * @throws Exception
	 */
	public List<SijsSecondaryDtlsDto> getSijsSecondaryDtls(Long ulIdStage, String szCdRole);

	/**
	 * 
	 * Method Description: Method is implemented in AssignWorkloadDaoImpl to get
	 * legal status information Tuxedo Servive Name:CINV88S Tuxedo DAM Name:
	 * CLSC1CD
	 * 
	 * @param ulIdPerson
	 * @return SijsLegalDtlsDto
	 * @throws Exception
	 */

	public SijsLegalDtlsDto getSijsLegalDtls(Long ulIdPerson);

	/**
	 * 
	 * Method Description: Method is implemented in AssignWorkloadDaoImpl to get
	 * eventIDs for the contact type Tuxedo Servive Name:CINV88S Tuxedo DAM
	 * Name: CLSS1AD
	 * 
	 * @param ulIdPerson
	 * @param contactStage
	 * @param contactPurpose
	 * @return SijsEventIdDtlsDto
	 * @throws Exception
	 */

	public List<SijsEventIdDtlsDto> getSijsEventIdDtls(Long ulIdStage, String contactStage, String contactPurpose);

	/**
	 * 
	 * Method Description: Method is implemented in AssignWorkloadDaoImpl to get
	 * Names and Others Contacted for all the events in a particular stage
	 * Tuxedo Servive Name:CINV88S Tuxedo DAM Name: CLSCDCD
	 * 
	 * @param ulIdPerson
	 * @param contactStage
	 * @param contactPurpose
	 * @return SijsEventContactDtlsDto
	 * @throws Exception
	 */
	public List<SijsEventContactDtlsDto> getSijsEventContactDtls(Long ulIdStage, String contactStage,
			String contactPurpose);

	/**
	 * 
	 * Method Description: Method is implemented in AssignWorkloadDaoImpl to get
	 * Status whether to display SIJS Status Form Tuxedo Servive Name:CINV88S
	 * 
	 * @param ulIdPerson
	 * @return GetSijsStatusFormDto
	 * @throws Exception
	 */
	public GetSijsStatusFormDto getSijsStatus(Long ulIdPerson);
}
