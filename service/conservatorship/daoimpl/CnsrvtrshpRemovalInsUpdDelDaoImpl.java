package us.tx.state.dfps.service.conservatorship.daoimpl;

import java.util.Locale;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.CnsrvtrshpRemoval;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.conservatorship.dao.CnsrvtrshpRemovalInsUpdDelDao;
import us.tx.state.dfps.service.cvs.dto.CnsrvtrshpRemovalInsUpdDelInDto;
import us.tx.state.dfps.service.cvs.dto.CnsrvtrshpRemovalInsUpdDelOutDto;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: This will
 * add, update, & delete a row from the CNSRVTRSHP REMOVAL table. Aug 12, 2017-
 * 2:50:16 PM © 2017 Texas Department of Family and Protective Services
 */
@Repository
public class CnsrvtrshpRemovalInsUpdDelDaoImpl implements CnsrvtrshpRemovalInsUpdDelDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${CnsrvtrshpRemovalInsUpdDelDaoImpl.deleteCnsrvtrshpRemoval}")
	private transient String deleteCnsrvtrshpRemoval;

	@Value("${CnsrvtrshpRemovalInsUpdDelDaoImpl.updateCnsrvtrshpRemoval}")
	private transient String updateCnsrvtrshpRemoval;

	@Value("${CnsrvtrshpRemovalInsUpdDelDaoImpl.insertCnsrvtrshpRemoval}")
	private transient String insertCnsrvtrshpRemoval;

	private static final Logger log = Logger.getLogger(CnsrvtrshpRemovalInsUpdDelDaoImpl.class);

	/**
	 *
	 * @param cnsrvtrshpRemovalInsUpdDelInDto
	 * @return CnsrvtrshpRemovalInsUpdDelOutDto @
	 */
	@Override
	public CnsrvtrshpRemovalInsUpdDelOutDto cnsrvtrshpRemovalInsUpdDel(
			CnsrvtrshpRemovalInsUpdDelInDto cnsrvtrshpRemovalInsUpdDelInDto) {
		log.debug("Entering method cnsrvtrshpRemovalInsUpdDel in CnsrvtrshpRemovalInsUpdDelDaoImpl");
		CnsrvtrshpRemovalInsUpdDelOutDto caud29doDto = new CnsrvtrshpRemovalInsUpdDelOutDto();
		switch (cnsrvtrshpRemovalInsUpdDelInDto.getReqFuncCd()) {
		case ServiceConstants.REQ_FUNC_CD_ADD:
			saveCnsrvtrshpRemoval(cnsrvtrshpRemovalInsUpdDelInDto);
			break;
		case ServiceConstants.REQ_FUNC_CD_UPDATE:
			updateCnsrvtrshpRemoval(cnsrvtrshpRemovalInsUpdDelInDto);
			break;
		case ServiceConstants.REQ_FUNC_CD_DELETE:
			deleteCnsrvtrshpRemoval(cnsrvtrshpRemovalInsUpdDelInDto);
			break;
		}
		log.debug("Exiting method cnsrvtrshpRemovalInsUpdDel in CnsrvtrshpRemovalInsUpdDelDaoImpl");
		return caud29doDto;
	}

	/**
	 * 
	 * Method Name: saveCnsrvtrshpRemoval Method Description:
	 * saveCnsrvtrshpremoval Method Description:Insert CNSRVTRSHP REMOVAL table
	 * 
	 * @param cnsrvtrshpRemovalInsUpdDelInDto
	 * @
	 */
	public void saveCnsrvtrshpRemoval(CnsrvtrshpRemovalInsUpdDelInDto cnsrvtrshpRemovalInsUpdDelInDto) {
		log.debug("Entering method saveCnsrvtrshpRemoval in CnsrvtrshpRemovalInsUpdDelDaoImpl");
		if (!TypeConvUtil.isNullOrEmpty(cnsrvtrshpRemovalInsUpdDelInDto)) {
			CnsrvtrshpRemoval cnsrvtrshpRemoval = new CnsrvtrshpRemoval();
			if (!TypeConvUtil.isNullOrEmpty(cnsrvtrshpRemovalInsUpdDelInDto.getIdEvent())) {
				cnsrvtrshpRemoval.setIdRemovalEvent(cnsrvtrshpRemovalInsUpdDelInDto.getIdEvent());
			}
			if (!TypeConvUtil.isNullOrEmpty(cnsrvtrshpRemovalInsUpdDelInDto.getIdVictim())) {
				Person person = (Person) sessionFactory.getCurrentSession().get(Person.class,
						cnsrvtrshpRemovalInsUpdDelInDto.getIdVictim());
				if (TypeConvUtil.isNullOrEmpty(person)) {
					throw new DataNotFoundException(messageSource
							.getMessage("Caud29dDaoImpl.cnsrvtrshpRemoval.insert.victim.not.found", null, Locale.US));
				}
				cnsrvtrshpRemoval.setPerson(person);
			}
			if (!TypeConvUtil.isNullOrEmpty(cnsrvtrshpRemovalInsUpdDelInDto.getDtRemoval())) {
				cnsrvtrshpRemoval.setDtRemoval(cnsrvtrshpRemovalInsUpdDelInDto.getDtRemoval());
			}
			if (!TypeConvUtil.isNullOrEmpty(cnsrvtrshpRemovalInsUpdDelInDto.getIndRemovalNACare())) {
				cnsrvtrshpRemoval.setIndRemovalNaCare(cnsrvtrshpRemovalInsUpdDelInDto.getIndRemovalNACare());
			}
			if (!TypeConvUtil.isNullOrEmpty(cnsrvtrshpRemovalInsUpdDelInDto.getIndRemovalNaChild())) {
				cnsrvtrshpRemoval.setIndRemovalNaChild(cnsrvtrshpRemovalInsUpdDelInDto.getIndRemovalNaChild());
			}
			if (!TypeConvUtil.isNullOrEmpty(cnsrvtrshpRemovalInsUpdDelInDto.getTmLNbrRemovalAgeMo())) {
				cnsrvtrshpRemoval.setNbrRemovalAgeMo(cnsrvtrshpRemovalInsUpdDelInDto.getTmLNbrRemovalAgeMo());
			}
			if (!TypeConvUtil.isNullOrEmpty(cnsrvtrshpRemovalInsUpdDelInDto.getTmLNbrRemovalAgeYr())) {
				cnsrvtrshpRemoval.setNbrRemovalAgeYr(cnsrvtrshpRemovalInsUpdDelInDto.getTmLNbrRemovalAgeYr());
			}
			/*
			 * sessionFactory.getCurrentSession().saveOrUpdate(cnsrvtrshpRemoval
			 * ); if
			 * (TypeConvUtil.isNullOrEmpty(cnsrvtrshpRemoval.getIdRemovalEvent()
			 * )) { throw new DataNotFoundException( messageSource.getMessage(
			 * "Caud29dDaoImpl.cnsrvtrshpRemoval.insert.fail", null,
			 * Locale.US)); }
			 */

			Query sQLQuery1 = (sessionFactory.getCurrentSession().createSQLQuery(insertCnsrvtrshpRemoval)
					.setParameter("hI_ulIdVictim", cnsrvtrshpRemovalInsUpdDelInDto.getIdVictim())
					.setParameter("hI_lNbrRemovalAgeYr", cnsrvtrshpRemovalInsUpdDelInDto.getTmLNbrRemovalAgeYr())
					.setParameter("hI_cIndRemovalNaChild", cnsrvtrshpRemovalInsUpdDelInDto.getIndRemovalNaChild())
					.setParameter("hI_tsLastUpdate", cnsrvtrshpRemovalInsUpdDelInDto.getTsLastUpdate())
					.setParameter("hI_cIndRemovalNACare", cnsrvtrshpRemovalInsUpdDelInDto.getIndRemovalNACare())
					.setParameter("hI_dtDtRemoval", cnsrvtrshpRemovalInsUpdDelInDto.getDtRemoval())
					.setParameter("hI_ulIdEvent", cnsrvtrshpRemovalInsUpdDelInDto.getIdEvent())
					.setParameter("hI_lNbrRemovalAgeMo", cnsrvtrshpRemovalInsUpdDelInDto.getTmLNbrRemovalAgeMo()));
			sQLQuery1.executeUpdate();
			log.debug("Exiting method saveCnsrvtrshpRemoval in CnsrvtrshpRemovalInsUpdDelDaoImpl");
		}
	}

	/**
	 * 
	 * Method Name: deleteCnsrvtrshpRemoval Method Description:
	 * deleteCnsrvtrshpRemoval Method Description:Delete CNSRVTRSHP REMOVAL
	 * table
	 * 
	 * @param cnsrvtrshpRemovalInsUpdDelInDto
	 * @
	 */
	public void deleteCnsrvtrshpRemoval(CnsrvtrshpRemovalInsUpdDelInDto cnsrvtrshpRemovalInsUpdDelInDto) {
		log.debug("Entering method deleteCnsrvtrshpRemoval in CnsrvtrshpRemovalInsUpdDelDaoImpl");
		Query query = (sessionFactory.getCurrentSession().createSQLQuery(deleteCnsrvtrshpRemoval)
				.setParameter("hI_tsLastUpdate",
						TypeConvUtil.formatDate(cnsrvtrshpRemovalInsUpdDelInDto.getTsLastUpdate()))
				.setParameter("hI_ulIdEvent", cnsrvtrshpRemovalInsUpdDelInDto.getIdEvent()));
		int rowCount = query.executeUpdate();
		if (rowCount <= 0) {
			throw new DataNotFoundException(messageSource.getMessage("Caud29dDaoImpl.delete.failed", null, Locale.US));
		}
		log.debug("Exiting method deleteCnsrvtrshpRemoval in CnsrvtrshpRemovalInsUpdDelDaoImpl");
	}

	/**
	 * 
	 * Method Name: updateCnsrvtrshpRemoval Method
	 * Description:updateCnsrvtrshpRemoval Method Description:Update CNSRVTRSHP
	 * REMOVAL table
	 * 
	 * @param cnsrvtrshpRemovalInsUpdDelInDto
	 * @
	 */
	public void updateCnsrvtrshpRemoval(CnsrvtrshpRemovalInsUpdDelInDto cnsrvtrshpRemovalInsUpdDelInDto) {
		log.debug("Entering method updateCnsrvtrshpRemoval in CnsrvtrshpRemovalInsUpdDelDaoImpl");
		Query query = (sessionFactory.getCurrentSession().createSQLQuery(updateCnsrvtrshpRemoval)
				.setParameter("hI_ulIdVictim", cnsrvtrshpRemovalInsUpdDelInDto.getIdVictim())
				.setParameter("hI_lNbrRemovalAgeYr", cnsrvtrshpRemovalInsUpdDelInDto.getTmLNbrRemovalAgeYr())
				.setParameter("hI_cIndRemovalNaChild", cnsrvtrshpRemovalInsUpdDelInDto.getIndRemovalNaChild())
				.setParameter("hI_cIndRemovalNACare", cnsrvtrshpRemovalInsUpdDelInDto.getIndRemovalNACare())
				.setParameter("hI_tsLastUpdate",
						TypeConvUtil.formatDate(cnsrvtrshpRemovalInsUpdDelInDto.getTsLastUpdate()))
				.setParameter("hI_dtDtRemoval",
						TypeConvUtil.formDateFormat(cnsrvtrshpRemovalInsUpdDelInDto.getDtRemoval()))
				.setParameter("hI_ulIdEvent", cnsrvtrshpRemovalInsUpdDelInDto.getIdEvent())
				.setParameter("hI_lNbrRemovalAgeMo", cnsrvtrshpRemovalInsUpdDelInDto.getTmLNbrRemovalAgeMo()));
		int rowCount = query.executeUpdate();
		if (rowCount <= 0) {
			throw new DataNotFoundException(messageSource.getMessage("Caud29dDaoImpl.update.failed", null, Locale.US));
		}
		log.debug("Entering method updateCnsrvtrshpRemoval in CnsrvtrshpRemovalInsUpdDelDaoImpl");
	}
}
