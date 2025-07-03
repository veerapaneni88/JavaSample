package us.tx.state.dfps.service.contacts.daoimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.FacilityInvstDtl;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.contacts.dao.FacilityInvstDtlUpdateDao;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.xmlstructs.inputstructs.ContactsDto;

@Repository
public class FacilityInvstDtlUpdateDaoImpl implements FacilityInvstDtlUpdateDao {
	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	/**
	 * 
	 * Method Name: updateFacilityInvestigationDetail Method Description:This
	 * DAM nulls out the CD_FACIL_INVST_ORIG_DISP, CD_FACIL_INVST_ORIG_CLS_RSN,
	 * DT_FACIL_INVST_ORIG_COMPL when a Request for Review is Deleted.
	 * 
	 * @param contactsDto
	 * @return long @
	 */
	@Override
	public long updateFacilityInvestigationDetail(ContactsDto cinvc2di) {

		List<FacilityInvstDtl> facilityInvstDtls = new ArrayList<FacilityInvstDtl>();
		if (ServiceConstants.REQ_FUNC_CD_UPDATE.equals(cinvc2di.getArchInputStruct().getCreqFuncCd())) {
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(FacilityInvstDtl.class);
			criteria.add(Restrictions.eq("stage.idStage", cinvc2di.getUlIdStage()));
			facilityInvstDtls = criteria.list();
			for (FacilityInvstDtl dtl : facilityInvstDtls) {
				dtl.setCdFacilInvstOrigDisp(ServiceConstants.NULL_CASTOR_DATE);
				dtl.setCdFacilInvstOrigClsRsn(ServiceConstants.NULL_CASTOR_DATE);
				dtl.setDtFacilInvstOrigCompl(ServiceConstants.NULL_CASTOR_DATE_DATE);
			}
		} else {
			throw new DataNotFoundException(messageSource.getMessage("ArcErrBadFuncCd", null, Locale.US));
		}
		return facilityInvstDtls.size();
	}

}
