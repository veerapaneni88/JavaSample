package us.tx.state.dfps.service.admin.daoimpl;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.admin.dao.NameSeqInsUpdDelDao;
import us.tx.state.dfps.service.admin.dto.NameSeqInsUpdDelInDto;
import us.tx.state.dfps.service.admin.dto.NameSeqInsUpdDelOutDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Update name
 * record. Aug 11, 2017- 4:32:55 PM © 2017 Texas Department of Family and
 * Protective Services
 */
@Repository
public class NameSeqInsUpdDelDaoImpl implements NameSeqInsUpdDelDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${NameSeqInsUpdDelDaoImpl.insertNameNoEndDate}")
	private String insertNameNoEndDate;

	@Value("${NameSeqInsUpdDelDaoImpl.insertNameHaveEndDate}")
	private String insertNameHaveEndDate;

	@Value("${NameSeqInsUpdDelDaoImpl.getEndDateDB}")
	private String getEndDateDB;

	@Value("${NameSeqInsUpdDelDaoImpl.updateNameNoEndDate}")
	private String updateNameNoEndDate;

	@Value("${NameSeqInsUpdDelDaoImpl.updateNameHaveEndDate}")
	private String updateNameHaveEndDate;

	@Value("${NameSeqInsUpdDelDaoImpl.deleteNameNoEndDate}")
	private String deleteNameNoEndDate;

	private static final Logger log = Logger.getLogger(NameSeqInsUpdDelDaoImpl.class);

	public NameSeqInsUpdDelDaoImpl() {
		super();
	}

	/**
	 * 
	 * Method Name: updateNameRecord Method Description: Inserts/Update/Delete a
	 * record in NAME table.
	 * 
	 * @param nameSeqInsUpdDelInDto
	 * @return int
	 */
	@Override
	public int updateNameRecord(NameSeqInsUpdDelInDto nameSeqInsUpdDelInDto) {
		log.debug("Entering method NameSeqInsUpdDelQUERYdam in NameSeqInsUpdDelDaoImpl");
		int rowCount = 0;
		// dtEndDate=-1;
		switch (nameSeqInsUpdDelInDto.getReqFuncCd()) {
		case ServiceConstants.REQ_FUNC_CD_ADD:
			rowCount = saveName(nameSeqInsUpdDelInDto);
			break;
		case ServiceConstants.REQ_FUNC_CD_UPDATE:
			rowCount = updateName(nameSeqInsUpdDelInDto);
			break;
		case ServiceConstants.REQ_FUNC_CD_DELETE:
			rowCount = deleteName(nameSeqInsUpdDelInDto);
			break;
		}
		if (rowCount == 0) {
			throw new DataNotFoundException(
					messageSource.getMessage("Cinv32dDaoImpl.person.name.not.inserted", null, Locale.US));
		}
		log.debug("Exiting method NameSeqInsUpdDelQUERYdam in NameSeqInsUpdDelDaoImpl");
		return rowCount;
	}

	/**
	 * 
	 * Method Name: deleteName Method Description: Delete name record
	 * 
	 * @param nameSeqInsUpdDelInDto
	 * @return int
	 */
	private int deleteName(NameSeqInsUpdDelInDto nameSeqInsUpdDelInDto) {
		int rowCount;
		SQLQuery sQLQuery7 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(deleteNameNoEndDate)
				.setParameter("hI_ulIdName", nameSeqInsUpdDelInDto.getIdName())
				.setParameter("hI_tsLastUpdate", nameSeqInsUpdDelInDto.getTsLastUpdate()));
		rowCount = sQLQuery7.executeUpdate();
		return rowCount;
	}

	/**
	 * 
	 * Method Name: updateName Method Description: Update name record
	 * 
	 * @param pInputDataRec
	 * @return int
	 */
	private int updateName(NameSeqInsUpdDelInDto pInputDataRec) {
		int rowCount;
		Date dtEndDate = null;
		if (null == pInputDataRec.getDtNameEnd()) {
			SQLQuery sQLQuery4 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getEndDateDB)
					.addScalar("dtEndDate").setParameter("hI_ulIdName", pInputDataRec.getIdName())
					.setResultTransformer(Transformers.aliasToBean(NameSeqInsUpdDelOutDto.class)));
			List<NameSeqInsUpdDelOutDto> liCinv32doDto1 = (List<NameSeqInsUpdDelOutDto>) sQLQuery4.list();
			if (null != liCinv32doDto1 && liCinv32doDto1.size() > 0) {
				dtEndDate = liCinv32doDto1.get(0).getDtEndDate();
			}
		}
		if (null == pInputDataRec.getDtNameEnd() && null == dtEndDate) {
			SQLQuery sQLQuery5 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(updateNameNoEndDate)
					.setParameter("hI_szNmNameFirst", pInputDataRec.getNmNameFirst())
					.setParameter("hI_szCdNameSuffix", pInputDataRec.getCdNameSuffix())
					.setParameter("hI_bIndNameInvalid", pInputDataRec.getIndNameInvalid())
					.setParameter("hI_bIndNamePrimary", pInputDataRec.getIndNamePrimary())
					.setParameter("hI_ulIdName", pInputDataRec.getIdName())
					.setParameter("hI_ulIdPerson", pInputDataRec.getIdPerson())
					.setParameter("hI_tsLastUpdate", pInputDataRec.getTsLastUpdate())
					.setParameter("hI_szNmNameMiddle", pInputDataRec.getNmNameMiddle())
					.setParameter("hI_szNmNameLast", pInputDataRec.getNmNameLast()));
			rowCount = sQLQuery5.executeUpdate();
		} else {
			/*
			 ** end-date already exists, so don't update the end_date
			 */
			SQLQuery sQLQuery6 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(updateNameHaveEndDate)
					.setParameter("hI_szNmNameFirst", pInputDataRec.getNmNameFirst())
					.setParameter("hI_szCdNameSuffix", pInputDataRec.getCdNameSuffix())
					.setParameter("hI_bIndNameInvalid", pInputDataRec.getIndNameInvalid())
					.setParameter("hI_bIndNamePrimary", pInputDataRec.getIndNamePrimary())
					.setParameter("hI_ulIdName", pInputDataRec.getIdName())
					.setParameter("hI_ulIdPerson", pInputDataRec.getIdPerson())
					.setParameter("hI_tsLastUpdate", pInputDataRec.getTsLastUpdate())
					.setParameter("hI_szNmNameMiddle", pInputDataRec.getNmNameMiddle())
					.setParameter("hI_szNmNameLast", pInputDataRec.getNmNameLast()));
			rowCount = sQLQuery6.executeUpdate();
		}
		return rowCount;
	}

	/**
	 * 
	 * Method Name: saveName Method Description: Insert a new name record.
	 * 
	 * @param pInputDataRec
	 * @return int
	 */
	private int saveName(NameSeqInsUpdDelInDto pInputDataRec) {
		int rowCount;
		/**
		 * DT_NAME_END_DATE will be NULL_DATE when passed into the dao. This is
		 * fine since a trigger will set it to MAX_DATE for us. However, it is
		 * possible for us to have enddated the name at the same time it was
		 * created. In this case, we want to set DT_NAME_END_DATE to SYSDATE.
		 */
		if (null != pInputDataRec.getDtNameEnd()) {
			SQLQuery sQLQuery2 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(insertNameHaveEndDate)
					.setParameter("hI_szNmNameFirst", pInputDataRec.getNmNameFirst())
					.setParameter("hI_szCdNameSuffix", pInputDataRec.getCdNameSuffix())
					.setParameter("hI_dtDtNameEndDate", pInputDataRec.getDtNameEnd())
					.setParameter("hI_bIndNameInvalid", pInputDataRec.getIndNameInvalid())
					.setParameter("hI_bIndNamePrimary", pInputDataRec.getIndNamePrimary())
					.setParameter("hI_ulIdPerson", pInputDataRec.getIdPerson())
					.setParameter("hI_szNmNameMiddle", pInputDataRec.getNmNameMiddle())
					.setParameter("hI_szNmNameLast", pInputDataRec.getNmNameLast()));
			rowCount = sQLQuery2.executeUpdate();
		} else {
			SQLQuery sQLQuery3 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(insertNameNoEndDate)
					.setParameter("hI_szNmNameFirst", pInputDataRec.getNmNameFirst())
					.setParameter("hI_szCdNameSuffix", pInputDataRec.getCdNameSuffix())
					.setParameter("hI_bIndNameInvalid", pInputDataRec.getIndNameInvalid())
					.setParameter("hI_bIndNamePrimary", pInputDataRec.getIndNamePrimary())
					.setParameter("hI_ulIdPerson", pInputDataRec.getIdPerson())
					.setParameter("hI_szNmNameMiddle", pInputDataRec.getNmNameMiddle())
					.setParameter("hI_szNmNameLast", pInputDataRec.getNmNameLast()));
			rowCount = sQLQuery3.executeUpdate();
		}
		return rowCount;
	}
}
