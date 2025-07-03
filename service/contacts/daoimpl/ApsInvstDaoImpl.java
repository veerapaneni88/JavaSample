package us.tx.state.dfps.service.contacts.daoimpl;

import java.util.List;
import java.util.Locale;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.ApsInvstDetail;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.contacts.dao.ApsInvstDao;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.xmlstructs.inputstructs.APSInvestigationDto;

@Repository
public class ApsInvstDaoImpl implements ApsInvstDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	/**
	 * 
	 * Method Name: updateAPSInvestigationDetail Method Description:long
	 * 
	 * @param apsInvestigationDto
	 * @return long @
	 */
	@Override
	public long updateAPSInvestigationDetail(APSInvestigationDto apsInvestigationDto) {
		int rowCount = 0;
		if (ServiceConstants.REQ_FUNC_CD_UPDATE.equals(apsInvestigationDto.getArchInputStruct().getCreqFuncCd())) {
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ApsInvstDetail.class);
			criteria.add(Restrictions.eq("stage.idStage", apsInvestigationDto.getIdApsStage()));
			List<ApsInvstDetail> apsInvstDetailList = criteria.list();
			if (TypeConvUtil.isNullOrEmpty(apsInvstDetailList)) {
				throw new DataNotFoundException(messageSource.getMessage("Common.noRecordFound", null, Locale.US));
			}
			rowCount = apsInvstDetailList.size();

			if (apsInvstDetailList.size() > 0 && !apsInvstDetailList.isEmpty()) {
				for (ApsInvstDetail apsInvstDetail : apsInvstDetailList) {

					apsInvstDetail.setDtApsInvstBegun(apsInvestigationDto.getDtDtApsInvstBegun());

				}
			}
		}
		return rowCount;
	}
}
