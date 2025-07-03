package us.tx.state.dfps.service.admin.daoimpl;

import java.util.Date;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.Stage;
import us.tx.state.dfps.service.admin.dao.StageUpdateDao;
import us.tx.state.dfps.service.admin.dto.ContactDiDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;

@Repository
public class StageUpdateDaoImpl implements StageUpdateDao {
	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${Cinvc4dDaoImpl.setStgDetails}")
	private String setStgDetails;

	@Value("${StageUpdateDaoImpl.getIntakeStageStartDt}")
	private String getIntakeStageStartDt;

	private static final Logger log = Logger.getLogger(StageUpdateDaoImpl.class);

	/**
	 * Description:Method to Update Stage details
	 * 
	 * @param pInputDataRec
	 * @return void @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public int setStgDetails(ContactDiDto pInputDataRec) {
		log.debug("Entering method cinvc4dAUDdam in Cinvc4dDaoImpl");

		Query Query1 = (sessionFactory.getCurrentSession().createQuery(setStgDetails)
				.setParameter("hI_dtDtStageStart", pInputDataRec.getDtDtStageStart())
				.setParameter("hI_ulIdStage", pInputDataRec.getUlIdStage()));
		int rowCount = Query1.executeUpdate();

		if (rowCount <= 0) {
			throw new DataNotFoundException(messageSource.getMessage("Cinvc4d.not.updated", null, Locale.US));
		}
		log.debug("Exiting method cinvc4dAUDdam in Cinvc4dDaoImpl");
		return rowCount;
	}

	/**
	 * 
	 * Method Name: updateStage Method Description:This method updates the stage
	 * 
	 * @param contactDiDto
	 * @return long @
	 */
	@Override
	public long updateStage(ContactDiDto contactDiDto) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Stage.class);
		if (ServiceConstants.REQ_FUNC_CD_UPDATE.equals(contactDiDto.getServiceInputDto().getCreqFuncCd())) {
			criteria.add(Restrictions.eq("idStage", contactDiDto.getUlIdStage()));
			Stage stage = (Stage) criteria.uniqueResult();
			if (TypeConvUtil.isNullOrEmpty(stage)) {
				throw new DataNotFoundException(messageSource.getMessage("Common.noRecordFound", null, Locale.US));
			}
			stage.setDtStageStart(contactDiDto.getDtDtStageStart());
			sessionFactory.openSession().saveOrUpdate(stage);
		}

		return criteria.list().size();

	}

	@Override
	public Date getIntakeStageStartDt(Long iDStage) {
		Date dtStageStart = (Date) sessionFactory.getCurrentSession().createSQLQuery(getIntakeStageStartDt)
					.addScalar("dtStageStart", StandardBasicTypes.TIMESTAMP)
					.setParameter("idStage", iDStage).uniqueResult();
			return dtStageStart;
		}

}
