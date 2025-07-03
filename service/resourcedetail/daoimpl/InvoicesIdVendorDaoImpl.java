package us.tx.state.dfps.service.resourcedetail.daoimpl;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.resource.detail.dto.ResourceDetailInDto;
import us.tx.state.dfps.service.resourcedetail.dao.InvoicesIdVendorDao;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Update the
 * invoice table Feb 2, 2018- 3:54:17 PM © 2017 Texas Department of Family and
 * Protective Services
 */
@Repository
public class InvoicesIdVendorDaoImpl implements InvoicesIdVendorDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${InvoicesIdVendorDaoImpl.updateInvoice}")
	private String updateInvoice;

	private static final Logger log = Logger.getLogger(InvoicesIdVendorDaoImpl.class);

	/**
	 * 
	 * Method Name: updateInvoice Method Description: This method used for
	 * updating the Invoice resourceDetailInDto request
	 * 
	 * @param resourceDetailInDto
	 * @return resourceId @
	 */
	@Override
	public int updateInvoice(ResourceDetailInDto resourceDetailInDto) {
		log.debug("Entering method updateInvoice in InvoicesIdVendorDaoImpl");
		int rowCount = 0;
		switch (resourceDetailInDto.getCdScrDataAction()) {
		case ServiceConstants.REQ_FUNC_CD_UPDATE:
			SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(updateInvoice)
					.setString("nbrRsrcAddrVid", resourceDetailInDto.getNbrRsrcAddrVid())
					.setLong("idRsrcAddress", resourceDetailInDto.getIdRsrcAddress()));
			rowCount = sQLQuery1.executeUpdate();
			break;
		}
		log.debug("Exiting method updateInvoice in InvoicesIdVendorDaoImpl");
		return rowCount;
	}
}
