/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description: This class is used to create new alert.
 *Aug 13, 2018- 12:30:07 PM © 2017 Texas Department of Family and
 * Protective Services
 *
 */
package us.tx.state.dfps.service.alert.serviceimpl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.service.alert.dao.AlertDao;
import us.tx.state.dfps.service.alert.service.AlertService;

@Service
@Transactional
public class AlertServiceImpl implements AlertService {
	@Autowired
	MessageSource messageSource;

	@Autowired
	AlertDao alertDao;

	/**
	 * 
	 * Method Name: createAlert Method Description: This method is used to
	 * Create New Alert
	 * 
	 * @return
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public Long createAlert(Long idStage, Long idPersonAssigned, Long idPerson, Long idCase, String alertType,
			Date dueDate) {
		// Calling the Dao method to save the Alert in DB.
		Long idTodo = alertDao.createAlert(idStage, idPersonAssigned, idPerson, idCase, alertType, dueDate);
		return idTodo;
	}
	

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public Long createFbssAlert(Long idStage, Long idPersonAssigned, Long idPerson, Long idCase, String longDesText,
								String desText) {
			
		// TODO Auto-generated method stub
		Long idTodo = alertDao.createFbssAlert(idStage, idPersonAssigned, idPerson, idCase, longDesText, desText);
		return idTodo;
	}
	
	

}
