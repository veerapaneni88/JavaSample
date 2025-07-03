package us.tx.state.dfps.service.workload.daoimpl;

import java.util.Date;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.FamilyAssmt;
import us.tx.state.dfps.common.dto.ServiceReqHeaderDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataLayerException;
import us.tx.state.dfps.service.workload.dao.FamilyAssmtDao;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name:CCMN19S Class
 * Description: This Method extends BaseDao and implements ApprovalDao. This is
 * used to perform the add, update & delete operations in table. Apr 3 , 2017 -
 * 3:50:30 PM
 */
@Repository
public class FamilyAssmtDaoImpl implements FamilyAssmtDao {

	@Autowired
	private SessionFactory sessionFactory;

	public FamilyAssmtDaoImpl() {

	}

	/**
	 * 
	 * Method Description: This Method is used to perform AUD operation based on
	 * approvalDto Service Name: CCMN19S
	 * 
	 * @param archInputDto
	 * @param idEvent
	 * @param idStage
	 * @return ServiceResHeaderDto @
	 */
	public String getFamilyAssmtAUD(ServiceReqHeaderDto ServiceReqHeaderDto, Long idEvent, Long idStage) {
		FamilyAssmt familyAssmt = new FamilyAssmt();
		String retMsg = "";
		Date date = new Date();
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toLong(idEvent))
				&& !TypeConvUtil.isNullOrEmpty(TypeConvUtil.toLong(idStage))
				&& ServiceReqHeaderDto.getReqFuncCd().equalsIgnoreCase(ServiceConstants.REQ_IND_AUD_ADD)) {
			familyAssmt.setIdEvent(idEvent);
			// familyAssmt.setStage(idStage);
			familyAssmt.setDtFamAssmtComplt(date);
			familyAssmt.setDtLastUpdate(date);
			sessionFactory.getCurrentSession().persist(familyAssmt);
			retMsg = ServiceConstants.SUCCESS;
		} else if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toLong(idEvent))
				&& !TypeConvUtil.isNullOrEmpty(TypeConvUtil.toLong(idStage))
				&& ServiceReqHeaderDto.getReqFuncCd().equalsIgnoreCase(ServiceConstants.REQ_IND_AUD_UPDATE)) {
			familyAssmt.setIdEvent(idEvent);
			// familyAssmt.setStage(idStage);
			familyAssmt.setDtFamAssmtComplt(date);
			sessionFactory.getCurrentSession().saveOrUpdate(sessionFactory.getCurrentSession().merge(familyAssmt));
		} else {
			throw new DataLayerException(ServiceConstants.NOAUDDOP);
		}
		return retMsg;
	}
}
