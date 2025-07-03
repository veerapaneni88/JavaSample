package us.tx.state.dfps.service.conservatorship.daoimpl;

import java.util.Date;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.RemovalReason;
import us.tx.state.dfps.common.domain.RemovalReasonId;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.conservatorship.dao.RemovalReasonInsUpdDelDao;
import us.tx.state.dfps.service.cvs.dto.RemovalReasonInsUpdDelInDto;
import us.tx.state.dfps.service.cvs.dto.RemovalReasonInsUpdDelOutDto;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This will
 * add, update, & delete a row from the REMOVAL REASON table Aug 13, 2017-
 * 12:59:44 PM © 2017 Texas Department of Family and Protective Services
 */
@Repository
public class RemovalReasonInsUpdDelDaoImpl implements RemovalReasonInsUpdDelDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${RemovalReasonInsUpdDelDaoImpl.updateRemovalReason}")
	private transient String updateRemovalReason;

	@Value("${RemovalReasonInsUpdDelDaoImpl.deleteRemovalReason}")
	private transient String deleteRemovalReason;

	private static final Logger log = Logger.getLogger(RemovalReasonInsUpdDelDaoImpl.class);

	/**
	 *
	 * @param removalReasonInsUpdDelInDto
	 * @return Caud30doDto @
	 */
	@Override
	public RemovalReasonInsUpdDelOutDto removalReasonInsUpdDel(
			RemovalReasonInsUpdDelInDto removalReasonInsUpdDelInDto) {
		log.debug("Entering method removalReasonInsUpdDel in RemovalReasonInsUpdDelDaoImpl");
		RemovalReasonInsUpdDelOutDto caud30doDto = new RemovalReasonInsUpdDelOutDto();
		switch (removalReasonInsUpdDelInDto.getReqFuncCd()) {
		case ServiceConstants.REQ_FUNC_CD_ADD:
			saveRemovalReason(removalReasonInsUpdDelInDto);
			break;
		case ServiceConstants.REQ_FUNC_CD_UPDATE:
			updateRemovalReason(removalReasonInsUpdDelInDto);
			break;
		case ServiceConstants.REQ_FUNC_CD_DELETE:
			deleteRemovalReason(removalReasonInsUpdDelInDto);
			break;
		}
		log.debug("Exiting method removalReasonInsUpdDel in RemovalReasonInsUpdDelDaoImpl");
		return caud30doDto;
	}

	/**
	 * Method Name: deleteRemovalPerson Method Description:Delete REMOVAL REASON
	 * table
	 * 
	 * @param removalReasonInsUpdDelInDto
	 */
	public void deleteRemovalReason(RemovalReasonInsUpdDelInDto removalReasonInsUpdDelInDto) {
		log.debug("Entering method deleteRemovalReason in RemovalReasonInsUpdDelDaoImpl");
		Query query = sessionFactory.getCurrentSession().createSQLQuery(deleteRemovalReason)
				.setParameter("hI_szCdRemovalReason", removalReasonInsUpdDelInDto.getCdRemovalReason())
				.setParameter("hI_tsLastUpdate", TypeConvUtil.formatDate(removalReasonInsUpdDelInDto.getTsLastUpdate()))
				.setParameter("hI_ulIdEvent", removalReasonInsUpdDelInDto.getIdEvent());
		int rowCount = query.executeUpdate();
		if (rowCount <= 0) {
			throw new DataNotFoundException(messageSource.getMessage("Caud30dDaoImpl.delete.failed", null, Locale.US));
		}
		log.debug("Exiting method deleteRemovalReason in RemovalReasonInsUpdDelDaoImpl");
	}

	/**
	 * Method Name: updateRemovalPerson Method Description:Update REMOVAL REASON
	 * table
	 * 
	 * @param removalReasonInsUpdDelInDto
	 */
	public void updateRemovalReason(RemovalReasonInsUpdDelInDto removalReasonInsUpdDelInDto) {
		log.debug("Entering method updateRemovalReason in RemovalReasonInsUpdDelDaoImpl");
		Query query = sessionFactory.getCurrentSession().createSQLQuery(updateRemovalReason)
				.setParameter("hI_szCdRemovalReason", removalReasonInsUpdDelInDto.getCdRemovalReason())
				.setParameter("hI_tsLastUpdate", TypeConvUtil.formatDate(removalReasonInsUpdDelInDto.getTsLastUpdate()))
				.setParameter("hI_ulIdEvent", removalReasonInsUpdDelInDto.getIdEvent());
		int rowCount = query.executeUpdate();
		if (rowCount <= 0) {
			throw new DataNotFoundException(messageSource.getMessage("Caud30dDaoImpl.update.failed", null, Locale.US));
		}
		log.debug("Exiting method updateRemovalReason in RemovalReasonInsUpdDelDaoImpl");
	}

	/**
	 * Method Name: saveRemovalPerson Method Description: Insert REMOVAL REASON
	 * table
	 * 
	 * @param removalReasonInsUpdDelInDto
	 */
	public void saveRemovalReason(RemovalReasonInsUpdDelInDto removalReasonInsUpdDelInDto) {
		log.debug("Entering method saveRemovalReason in RemovalReasonInsUpdDelDaoImpl");
		if (!TypeConvUtil.isNullOrEmpty(removalReasonInsUpdDelInDto)) {
			RemovalReason removalReason = new RemovalReason();
			RemovalReasonId removalReasonId = new RemovalReasonId();
			if (!TypeConvUtil.isNullOrEmpty(removalReasonInsUpdDelInDto.getCdRemovalReason())) {
				removalReasonId.setCdRemovalReason(removalReasonInsUpdDelInDto.getCdRemovalReason());
			}
			if (!TypeConvUtil.isNullOrEmpty(removalReasonInsUpdDelInDto.getIdEvent())) {
				removalReasonId.setIdRemovalEvent(removalReasonInsUpdDelInDto.getIdEvent());
			}
			removalReasonId.setDtLastUpdate(new Date());
			removalReason.setId(removalReasonId);
			sessionFactory.getCurrentSession().save(removalReason);
			if (TypeConvUtil.isNullOrEmpty(removalReason.getId().getIdRemovalEvent())) {
				throw new DataNotFoundException(
						messageSource.getMessage("Caud30dDaoImpl.RemovalReason.insert.failed", null, Locale.US));
			}
		}
		log.debug("Exiting method saveRemovalReason in RemovalReasonInsUpdDelDaoImpl");
	}
}
