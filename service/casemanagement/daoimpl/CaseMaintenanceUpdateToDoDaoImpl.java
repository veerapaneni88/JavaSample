package us.tx.state.dfps.service.casemanagement.daoimpl;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.casemanagement.dao.CaseMaintenanceUpdateToDoDao;
import us.tx.state.dfps.service.casepackage.dto.CompleteToDoInDto;
import us.tx.state.dfps.service.casepackage.dto.CompleteToDoOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:CaseMaintenanceUpdateToDoDaoImpl Feb 7, 2018- 5:52:37 PM © 2017
 * Texas Department of Family and Protective Services
 */
@Repository
public class CaseMaintenanceUpdateToDoDaoImpl implements CaseMaintenanceUpdateToDoDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${CaseMaintenanceUpdateToDoDaoImpl.strQuery1}")
	private transient String strQuery1;

	private static final Logger log = Logger.getLogger(CaseMaintenanceUpdateToDoDaoImpl.class);

	/**
	 * DAM: Ccmnh3d
	 * 
	 * @param completeToDoInDto
	 * @param completeToDoOutDto
	 * @
	 */
	@Override
	public void updateTodo(CompleteToDoInDto completeToDoInDto, CompleteToDoOutDto completeToDoOutDto) {
		log.debug("Entering method updateTodo in CaseMaintenanceUpdateToDoDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(strQuery1)
				.setParameter("idTodo", completeToDoInDto.getIdTodo()));
		sQLQuery1.executeUpdate();

		log.debug("Exiting method updateTodo in CaseMaintenanceUpdateToDoDaoImpl");
	}

}
