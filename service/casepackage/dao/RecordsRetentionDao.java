package us.tx.state.dfps.service.casepackage.dao;

import us.tx.state.dfps.service.casepackage.dto.RecordsRetentionDto;
import us.tx.state.dfps.service.casepackage.dto.RecordsRetnDestDtlsDto;
import us.tx.state.dfps.service.common.response.RecordsRetentionRtrvRes;

/**
 * ImpactWebServices - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: SSCC
 * EJB Class Description: RecordsRetentionDao Mar 26, 2017 - 8:58:10 PM
 */
public interface RecordsRetentionDao {

	/**
	 * This DAM will retrieve a full row from RECORDS RETENTION table and will
	 * take as input ID_CASE
	 * 
	 * Service Name - CCFC19S, Dam Name - CSES56D
	 * 
	 * @param ulIdCase
	 * @return @
	 */
	public RecordsRetentionRtrvRes getRecordsRetentionByCaseId(Long ulIdCase);
	
	/**
	 * Method Name: getDestructionDate Method Description: This method will call
	 * the stored procedure proc_calcrecretn from the package PKG_RECRETN to
	 * calculate the destruction date and retention type
	 * 
	 * @param idCase
	 * @return RecordRetentionDataInDto
	 */
	public RecordsRetnDestDtlsDto getDestructionDate (Long idCase);

	public RecordsRetentionDto getRecordsRetentionDestructionDate (Long idCase);

	public void insertRecordsRetention(RecordsRetentionDto retentionDto);
}
