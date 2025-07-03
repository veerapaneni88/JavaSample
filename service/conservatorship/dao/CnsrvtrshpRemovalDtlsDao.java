package us.tx.state.dfps.service.conservatorship.dao;

import java.util.List;

import us.tx.state.dfps.service.cvs.dto.CnsrvtrshpRemovalInDto;
import us.tx.state.dfps.service.cvs.dto.CnsrvtrshpRemovalOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This class
 * is the interface for Cses20dDaoImpl Aug 10, 2017- 6:59:27 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface CnsrvtrshpRemovalDtlsDao {

	public List<CnsrvtrshpRemovalOutDto> getrmvldtls(CnsrvtrshpRemovalInDto cnsrvtrshpRemovalInDto);
}
