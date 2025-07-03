package us.tx.state.dfps.service.person.service;

import us.tx.state.dfps.service.person.dto.MedicaidUpdateDto;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: CCMN44S Class
 * Description: This class is use for actions for MedicaidUpdate table Apr 9,
 * 2017 - 4:19:51PM
 */
public interface MedicaidUpdateService {

	/**
	 * This dam will add rows to the Medicaid Update table Service Name-
	 * CCMN44S, DAM Name-CAUD99D
	 * 
	 * @param medicaidUpdateDto
	 * @param action
	 * @
	 */
	void editMedicaidUpdate(MedicaidUpdateDto medicaidUpdateDto, String action);

}
