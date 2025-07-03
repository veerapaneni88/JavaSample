package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.FacilityServiceTypeAudDto;
import us.tx.state.dfps.service.facilityservicetype.dto.FacilityServiceTypeInDto;
import us.tx.state.dfps.service.facilityservicetype.dto.FacilityServiceTypeOutDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * FacilityServiceTypeDao Jun 27, 2018- 11:05:12 AM © 2017 Texas Department of
 * Family and Protective Services.
 */
public interface FacilityServiceTypeDao {

	/**
	 * Gets the facility service type out dto list.
	 *
	 * @param facilityServiceTypeInDto
	 *            the facility service type in dto
	 * @return the facility service type out dto list
	 */
	public List<FacilityServiceTypeOutDto> getFacilityServiceTypeOutDtoList(
			FacilityServiceTypeInDto facilityServiceTypeInDto);

	/**
	 * Gets the facility service type.
	 *
	 * @param facilityServiceTypeInDto
	 *            the facility service type in dto
	 * @return the facility service type
	 */
	public List<FacilityServiceTypeOutDto> getFacilityServiceType(FacilityServiceTypeInDto facilityServiceTypeInDto);

	/**
	 * Gets the ed facility service type count.
	 *
	 * @param facilityServiceTypeInDto
	 *            the facility service type in dto
	 * @return the ed facility service type count
	 */
	public Integer getEdFacilityServiceTypeCount(FacilityServiceTypeInDto facilityServiceTypeInDto);

	/**
	 * Facility service type aud.
	 *
	 * @param facilityServiceTypeAudDto
	 *            the facility service type aud dto
	 */
	public void facilityServiceTypeAud(FacilityServiceTypeAudDto facilityServiceTypeAudDto);

}
