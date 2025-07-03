package us.tx.state.dfps.service.person.daoimpl;

import java.util.Date;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.domain.RecordsCheck;
import us.tx.state.dfps.common.domain.RecordsCheckNotif;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.forms.dto.RecordsCheckNotifDto;
import us.tx.state.dfps.service.person.dao.RecordsCheckNotifDao;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This class
 * will implement the methods to do the CRUD operation in RECORD_CHCEK_NOTIG
 * table March 13, 2018- 5:06:12 PM © 2017 Texas Department of Family and
 * Protective Services
 *  * **********  Change History *********************************
 * 04/22/2022 thompswa artf205247,artf148743 : Letterhead Notification shows as TEST
 */
@Repository
public class RecordsCheckNotifDaoImpl implements RecordsCheckNotifDao {

	private static final Logger logger = Logger.getLogger(RecordsCheckNotifDaoImpl.class);


	@Autowired
	private SessionFactory sessionFactory;

	/**
	 * Method Description: This method will fetch the Record check notification
	 * details by passing idRecordsCheckNotif as input
	 * 
	 * @param idRecordsCheckNotif
	 * @return RecordsCheckNotifDto @
	 */
	@Override
	public RecordsCheckNotifDto getRecordsCheckNotification(Long idRecordsCheckNotif) {
		RecordsCheckNotifDto recordsCheckNotifDto = new RecordsCheckNotifDto();
		recordsCheckNotifDto = (RecordsCheckNotifDto) sessionFactory.getCurrentSession()
				.createCriteria(RecordsCheckNotif.class)
				.setProjection(Projections.projectionList()
						.add(Projections.property("idRecordsCheckNotif"), "idRecordsCheckNotif")
						.add(Projections.property("recordsCheck.idRecCheck"), "idRecCheck")
						.add(Projections.property("dtCreated"), "dtCreated")
						.add(Projections.property("idCreatedPerson"), "idCreatedPerson")
						.add(Projections.property("dtLastUpdate"), "dtLastUpdate")
						.add(Projections.property("idLastUpdatePerson"), "idLastUpdatePerson")
						.add(Projections.property("cdNotifType"), "cdNotifType")
						.add(Projections.property("cdNotifctnStat"), "cdNotifctnStat")
						.add(Projections.property("dtNotifctnSent"), "dtNotifctnSent")
						.add(Projections.property("idRecpntPerson"), "idRecpntPerson")
						.add(Projections.property("txtRecpntEmail"), "txtRecpntEmail")
						.add(Projections.property("txtSndrEmail"), "txtSndrEmail")
						.add(Projections.property("person.idPerson"), "idSndrPerson"))
				.add(Restrictions.eq("idRecordsCheckNotif", idRecordsCheckNotif))
				.setResultTransformer(Transformers.aliasToBean(RecordsCheckNotifDto.class)).uniqueResult();
		return recordsCheckNotifDto;
	}

	/**
	 * Method Description: This method will update the Record check notification
	 * table by passing required input
	 * 
	 * @param recordsCheckNotifDto
	 * @return Long @
	 */
	@Override
	public String updateRecordsCheckNotif(RecordsCheckNotifDto recordsCheckNotifDto) {
		logger.info("update recordsCheckNotifDto : secure = " + recordsCheckNotifDto.getIndSecureNotification());
		String rtnMsg = ServiceConstants.EMPTY_STRING;
		RecordsCheckNotif recordsCheckNotifEntity = (RecordsCheckNotif) sessionFactory.getCurrentSession()
				.get(RecordsCheckNotif.class, recordsCheckNotifDto.getIdRecordsCheckNotif());
		if (!TypeConvUtil.isNullOrEmpty(recordsCheckNotifDto.getIdLastUpdatePerson()))
			recordsCheckNotifEntity.setIdLastUpdatePerson(recordsCheckNotifDto.getIdLastUpdatePerson());
		if (!TypeConvUtil.isNullOrEmpty(recordsCheckNotifDto.getCdNotifctnStat())) {
			if (!CodesConstant.CNOTSTAT_RESENT.equals(recordsCheckNotifDto.getCdNotifctnStat())) {
				if (CodesConstant.CNOTSTAT_NEW.equals(recordsCheckNotifDto.getCdNotifctnStat())) {
					recordsCheckNotifEntity.setCdNotifctnStat(CodesConstant.CNOTSTAT_DRFT);
				} else {
					recordsCheckNotifEntity.setCdNotifctnStat(CodesConstant.CNOTSTAT_SENT);
				}
			}
			if (!CodesConstant.CNOTSTAT_NEW.equals(recordsCheckNotifDto.getCdNotifctnStat())) {
				if (!CodesConstant.CNOTSTAT_RESENT.equals(recordsCheckNotifDto.getCdNotifctnStat())) {
					recordsCheckNotifEntity.setDtNotifctnSent(new Date());
				}
				if (!TypeConvUtil.isNullOrEmpty(recordsCheckNotifDto.getIdRecpntPerson()))
					recordsCheckNotifEntity.setIdRecpntPerson(recordsCheckNotifDto.getIdRecpntPerson());
				if (!TypeConvUtil.isNullOrEmpty(recordsCheckNotifDto.getTxtRecpntEmail()))
					recordsCheckNotifEntity.setTxtRecpntEmail(recordsCheckNotifDto.getTxtRecpntEmail().trim());
				if (!TypeConvUtil.isNullOrEmpty(recordsCheckNotifDto.getIdSndrPerson())) {
					Person personEntity = (Person) sessionFactory.getCurrentSession().load(Person.class,
							recordsCheckNotifDto.getIdSndrPerson());
					recordsCheckNotifEntity.setDtLastUpdate(new Date());
					recordsCheckNotifEntity.setPerson(personEntity);
				}
			}

		}
		if (!TypeConvUtil.isNullOrEmpty(recordsCheckNotifDto.getTxtSndrEmail()))
			recordsCheckNotifEntity.setTxtSndrEmail(recordsCheckNotifDto.getTxtSndrEmail());
		if (!TypeConvUtil.isNullOrEmpty(recordsCheckNotifDto.getIndSecureNotification()))
			recordsCheckNotifEntity.setIndSecureNotification(recordsCheckNotifDto.getIndSecureNotification()); // artf205247,artf148743
		sessionFactory.getCurrentSession().saveOrUpdate(recordsCheckNotifEntity);
		rtnMsg = ServiceConstants.SUCCESS;
		return rtnMsg;
	}

	/**
	 * Method Description: This method will create the new Record check
	 * notification detail.
	 * 
	 * @param recordsCheckNotifDto
	 * @return Long
	 */
	public Long insertRecordsCheckNotif(RecordsCheckNotifDto recordsCheckNotifDto) {

		Long idRecordsCheckNotif = ServiceConstants.ZERO;
		RecordsCheckNotif recordsCheckNotifEntity = new RecordsCheckNotif();
		if (!TypeConvUtil.isNullOrEmpty(recordsCheckNotifDto.getIdLastUpdatePerson()))
			recordsCheckNotifEntity.setIdLastUpdatePerson(recordsCheckNotifDto.getIdLastUpdatePerson());
		if (!TypeConvUtil.isNullOrEmpty(recordsCheckNotifDto.getIdRecCheck())) {
			RecordsCheck recordsCheckEntity = (RecordsCheck) sessionFactory.getCurrentSession().get(RecordsCheck.class,
					recordsCheckNotifDto.getIdRecCheck());
			recordsCheckNotifEntity.setRecordsCheck(recordsCheckEntity);
		}
		recordsCheckNotifEntity.setDtCreated(new Date());
		recordsCheckNotifEntity.setDtLastUpdate(new Date());
		if (!TypeConvUtil.isNullOrEmpty(recordsCheckNotifDto.getIdCreatedPerson()))
			recordsCheckNotifEntity.setIdCreatedPerson(recordsCheckNotifDto.getIdCreatedPerson());
		if (!TypeConvUtil.isNullOrEmpty(recordsCheckNotifDto.getIdLastUpdatePerson()))
			recordsCheckNotifEntity.setIdLastUpdatePerson(recordsCheckNotifDto.getIdLastUpdatePerson());
		if (!TypeConvUtil.isNullOrEmpty(recordsCheckNotifDto.getCdNotifType()))
			recordsCheckNotifEntity.setCdNotifType(recordsCheckNotifDto.getCdNotifType());
		if (!TypeConvUtil.isNullOrEmpty(recordsCheckNotifDto.getCdNotifctnStat()))
			recordsCheckNotifEntity.setCdNotifctnStat(recordsCheckNotifDto.getCdNotifctnStat());
		if (!TypeConvUtil.isNullOrEmpty(recordsCheckNotifDto.getDtNotifctnSent()))
			recordsCheckNotifEntity.setDtNotifctnSent(recordsCheckNotifDto.getDtNotifctnSent());
		idRecordsCheckNotif = (Long) sessionFactory.getCurrentSession().save(recordsCheckNotifEntity);
		return idRecordsCheckNotif;
	}

}
