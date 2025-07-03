package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.servicedelivery.dto.ServiceDeliveryRtrvDtlsInDto;
import us.tx.state.dfps.service.servicedelivery.dto.ServiceDeliveryRtrvDtlsOutDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * ServiceDeliveryRtrvDtlsDao Jun 27, 2018- 11:02:45 AM © 2017 Texas Department
 * of Family and Protective Services.
 */
@Repository
public interface ServiceDeliveryRtrvDtlsDao {

	/**
	 * Method Name: getNarrExists Method Description:Retrieves Narrative blob
	 * and dtLastUpdate from database when Narrative is present. Csys06s
	 *
	 * @param serviceDeliveryRtrvDtlsInDto
	 *            the service delivery rtrv dtls in dto
	 * @return the narr exists
	 */
	public List<ServiceDeliveryRtrvDtlsOutDto> getNarrExists(ServiceDeliveryRtrvDtlsInDto serviceDeliveryRtrvDtlsInDto);

	/**
	 * Gets the service delivery dtls.
	 *
	 * @param serviceDeliveryRtrvDtlsInDto
	 *            the service delivery rtrv dtls in dto
	 * @return the service delivery dtls
	 */
	public List<ServiceDeliveryRtrvDtlsOutDto> getServiceDeliveryDtls(
			ServiceDeliveryRtrvDtlsInDto serviceDeliveryRtrvDtlsInDto);

	/**
	 * Gets the decode table name.
	 *
	 * @param eventType
	 *            the event type
	 * @param codeTableName
	 *            the code table name
	 * @return the decode table name
	 */
	public String getDecodeTableName(String eventType, String codeTableName);
}
