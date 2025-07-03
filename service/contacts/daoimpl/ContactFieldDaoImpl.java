package us.tx.state.dfps.service.contacts.daoimpl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import us.tx.state.dfps.service.contacts.dao.ContactFieldDao;
import us.tx.state.dfps.xmlstructs.inputstructs.ContactFieldDiDto;
import us.tx.state.dfps.xmlstructs.outputstructs.ContactFieldDoDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:ContactFieldDaoImpl Nov 1, 2017- 5:23:09 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@Repository
public class ContactFieldDaoImpl implements ContactFieldDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${ContactFieldDaoImpl.getContactDetails}")
	private String getContactDetailsSql;

	@Autowired
	MessageSource messageSource;

	/**
	 * Method Name: getContactDetails Method Description:This populates a number
	 * of fields about the contact. If it gets a SQL_NOT_FOUND error, it will
	 * throw it.
	 *
	 * @param contactFieldDiDto
	 * @return ContactFieldDoDto
	 */
	@Override
	public ContactFieldDoDto getContactDetails(ContactFieldDiDto contactFieldDiDto) {
		ContactFieldDoDto contactFieldDoDto = null;

		SQLQuery sQLQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getContactDetailsSql)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("nmPersonFull", StandardBasicTypes.STRING)
				.addScalar("cdContactLocation", StandardBasicTypes.STRING)
				.addScalar("cdContactMethod", StandardBasicTypes.STRING)
				.addScalar("cdContactOthers", StandardBasicTypes.STRING)
				.addScalar("cdContactPurpose", StandardBasicTypes.STRING)
				.addScalar("cdContactType", StandardBasicTypes.STRING)
				.addScalar("dtContactOccurred", StandardBasicTypes.TIMESTAMP)
				.addScalar("indContactAttempted", StandardBasicTypes.STRING)
				.addScalar("dtMonthlySummBegin", StandardBasicTypes.DATE)
				.addScalar("dtMonthlySummEnd", StandardBasicTypes.DATE)
				.addScalar("dtLastUpdateSecond", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtLastUpdateThird", StandardBasicTypes.TIMESTAMP)
				.addScalar("cdReasonScreenOut", StandardBasicTypes.STRING)
				.addScalar("indKinRecmd", StandardBasicTypes.STRING).addScalar("nmKnCgvr", StandardBasicTypes.STRING)
				.addScalar("cdRsnNotNeed", StandardBasicTypes.STRING).addScalar("amtNeeded", StandardBasicTypes.LONG)
				.addScalar("indSiblingVisit", StandardBasicTypes.STRING)
				.addScalar("cdChildSafety", StandardBasicTypes.STRING)
				.addScalar("cdPendLegalAction", StandardBasicTypes.STRING)
				.addScalar("indPrinInterview", StandardBasicTypes.STRING)
				.addScalar("cdProfCollateral", StandardBasicTypes.STRING)
				.addScalar("cdAdministrative", StandardBasicTypes.STRING)
				.addScalar("Comments", StandardBasicTypes.STRING).addScalar("indAnnounced", StandardBasicTypes.STRING)
				.addScalar("indSafPlnCompleted", StandardBasicTypes.STRING)
				.addScalar("indFamPlnCompleted", StandardBasicTypes.STRING)
				.addScalar("indSafConResolved", StandardBasicTypes.STRING)
				.addScalar("nbrHours", StandardBasicTypes.LONG).addScalar("nbrMins", StandardBasicTypes.LONG)
				.addScalar("cdFtfExceptionRsn", StandardBasicTypes.STRING)
				.addScalar("cdReqextReason", StandardBasicTypes.STRING)
				.addScalar("txtClosureDesc", StandardBasicTypes.STRING)
				.addScalar("indCourtOrdrSvcs", StandardBasicTypes.STRING)
        .addScalar("dtNotification", StandardBasicTypes.TIMESTAMP)
        .addScalar("txtSummDiscuss", StandardBasicTypes.STRING)
        .addScalar("txtIdentfdSafetyConc", StandardBasicTypes.STRING)
        .addScalar("txtPlansFutureActions", StandardBasicTypes.STRING)
        .addScalar("idCaseWorker", StandardBasicTypes.LONG)
        .addScalar("cdJobCaseworker", StandardBasicTypes.STRING)
        .addScalar("idSupervisor", StandardBasicTypes.LONG)
        .addScalar("cdJobSupervisor", StandardBasicTypes.STRING)
        .addScalar("idDirector", StandardBasicTypes.LONG)
        .addScalar("cdJobDirector", StandardBasicTypes.STRING)
        .addScalar("createdOn", StandardBasicTypes.TIMESTAMP)
				.setParameter("idEvent", (contactFieldDiDto.getIdEvent()))
				.setResultTransformer(Transformers.aliasToBean(ContactFieldDoDto.class));
		List<ContactFieldDoDto> contactFieldDoDtoList = new ArrayList<>();
		contactFieldDoDtoList = (List<ContactFieldDoDto>) sQLQuery.list();

		if (!CollectionUtils.isEmpty(contactFieldDoDtoList)) {
			contactFieldDoDto = contactFieldDoDtoList.get(0);
		}

		return contactFieldDoDto;
	}
}
