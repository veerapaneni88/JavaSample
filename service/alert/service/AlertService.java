/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:This interface is used to create alerts.
 *Aug 13, 2018- 12:23:47 PM © 2017 Texas Department of Family and
 * Protective Services
 *
 */
package us.tx.state.dfps.service.alert.service;

import java.util.Date;

public interface AlertService {
	/**
	 * 
	 * Method Name: createAlert Method Description: This method is used to
	 * Create New Alert
	 * 
	 * @return
	 */
	public Long createAlert(Long idStage, Long idPersonAssigned, Long idPerson, Long idCase, String alertType,
			Date dueDate);

	/**
	 *
	 * @param idStage
	 * @param idPersonAssigned
	 * @param userId
	 * @param idCase
	 * @param longDesText
	 * @param desText
	 * @return
	 */
	Long createFbssAlert(Long idStage, Long idPersonAssigned, Long userId, Long idCase, String longDesText,
								String desText);
}
