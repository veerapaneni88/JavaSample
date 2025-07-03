package us.tx.state.dfps.service.contacts.daoimpl;
/**
 * service-business - IMPACT PHASE 2 MODERNIZATION Class Description: this class is used for implementing contact Date related DB transaction
 * Jul 31, 2017- 1:04:37 PM © 2017 Texas Department of
 * Family and Protective Services
 */

import java.util.Date;
import java.util.Locale;

import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.contacts.dao.ContactDateDao;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.workload.dto.ContactDto;
import us.tx.state.dfps.xmlstructs.inputstructs.ApsInvStageDto;
import us.tx.state.dfps.xmlstructs.outputstructs.ContactDateDto;

@Repository
public class ContactDateDaoImpl implements ContactDateDao {

	@Value("${Csys20DaoImpl.getEarliestContactDate}")
	private transient String getEarliestContactDate;

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	MessageSource messageSource;

	/**
	 * 
	 * Method Name: getEarliestContactDate Method Description:this method
	 * retrieves the earliest 24HR non-NULL DT CONTACT OCCURRED for a given APS
	 * INV Stage
	 * 
	 * @param apsInvStageDto
	 * @return ContactDateDto @
	 */
	@Override
	public ContactDateDto getEarliestContactDate(ApsInvStageDto apsInvStageDto) {

		Date MAX_CASTOR_DATE = ServiceConstants.NULL_CASTOR_DATE_DATE;
		SQLQuery sQLQuery1 = (SQLQuery) (sessionFactory.getCurrentSession().createSQLQuery(getEarliestContactDate)
				.addScalar("dtContactOccured", StandardBasicTypes.DATE)
				.setParameter("idContactStage", apsInvStageDto.getUlIdStage())
				.setParameter("cdContactType", apsInvStageDto.getSzCdContactType())
				.setResultTransformer(Transformers.aliasToBean(ContactDto.class)));
		ContactDto contactDto = (ContactDto) sQLQuery1.uniqueResult();
		if (TypeConvUtil.isNullOrEmpty(contactDto)) {
			throw new DataNotFoundException(messageSource.getMessage("Common.noRecordFound", null, Locale.US));
		}
		ContactDateDto contactDateDto = new ContactDateDto();
		Date dtContact = contactDto.getDtContactOccurred();
		contactDateDto.setDtDTContactOccurred(
				!TypeConvUtil.isNullOrEmpty(dtContact) ? DateUtils.toCastorDate(dtContact) : MAX_CASTOR_DATE);
		return contactDateDto;
	}

}
