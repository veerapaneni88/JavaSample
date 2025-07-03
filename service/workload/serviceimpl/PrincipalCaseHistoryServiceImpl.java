package us.tx.state.dfps.service.workload.serviceimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.service.casepackage.dto.CaseListDto;
import us.tx.state.dfps.service.casepackage.dto.PrincipalListDto;
import us.tx.state.dfps.service.common.request.PrincipalCaseHistoryReq;
import us.tx.state.dfps.service.common.response.PrincipalCaseHistoryRes;
import us.tx.state.dfps.service.workload.dao.PrincipalCaseHistoryDao;
import us.tx.state.dfps.service.workload.service.PrincipalCaseHistoryService;

/**
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION EJB Name: PrincipalCaseHistoryBean
 * Class Description: This class is doing service Implementation for
 * PrincipalCaseHistoryService Mar 23, 2017 - 3:23:07 PM.
 */

@Service
@Transactional
public class PrincipalCaseHistoryServiceImpl implements PrincipalCaseHistoryService {

	/** The principal case history dao. */
	@Autowired
	PrincipalCaseHistoryDao principalCaseHistoryDao;

	/**
	 * Instantiates a new principal case history service impl.
	 */
	public PrincipalCaseHistoryServiceImpl() {

	}

	/**
	 * Method Description: Method is implemented in PrincipalCaseHistoryDaoImpl
	 * to perform insert operations. This method will insert Parent Child
	 * relationship Checked Linked case information into CASE_LINK Table. EJB
	 * Name: PrincipalCaseHistoryBean
	 *
	 * @param caseID
	 * @return PrincipalCaseHistoryRes
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW)
	public PrincipalCaseHistoryRes caseList(PrincipalCaseHistoryReq principalCaseHistoryReq) {
		PrincipalCaseHistoryRes principalCaseHistoryRes = new PrincipalCaseHistoryRes();

		List<CaseListDto> caseListDtos = principalCaseHistoryDao.caseList(principalCaseHistoryReq.getIdCase());
		principalCaseHistoryRes.setCaseListDtoList(caseListDtos);

		return principalCaseHistoryRes;
	}

	/**
	 * Method Name: selectPrincipalList Method Description: This method will be
	 * called when the radio button for a case is selected on the
	 * PrincipalCaseHistory page. Also the Principal List section will include
	 * the Stage Id, Stage Type and Overall Disposition for the INV stage and
	 * all of the principals in the stage. For each principal, the Name, Person
	 * ID, Age, DOB, Gender, Role, and Rel/Int will be displayed. The Principal
	 * List section will be sorted by Stage ID descending, and then by ID Person
	 * ascending order.
	 * 
	 * @param PrincipalCaseHistoryReq
	 * @return PrincipalCaseHistoryRes
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW)
	public PrincipalCaseHistoryRes selectPrincipalList(PrincipalCaseHistoryReq principalCaseHistoryReq) {
		PrincipalCaseHistoryRes principalCaseHistoryRes = new PrincipalCaseHistoryRes();

		List<PrincipalListDto> principalListDtos = principalCaseHistoryDao
				.selectPrincipalList(principalCaseHistoryReq.getIdCase(), principalCaseHistoryReq.getIdCase());
		principalCaseHistoryRes.setPrincipalListDtoList(principalListDtos);

		return principalCaseHistoryRes;
	}

	/**
	 * Method Description: Method is implemented in PrincipalCaseHistoryDaoImpl
	 * to perform insert operations. This method will insert Parent Child
	 * relationship Checked Linked case information into CASE_LINK Table. EJB
	 * Name: PrincipalCaseHistoryBean
	 *
	 * @param principalCaseHistoryReq
	 * @return PrincipalCaseHistoryRes
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public PrincipalCaseHistoryRes insertCaseInfo(PrincipalCaseHistoryReq principalCaseHistoryReq) {
		PrincipalCaseHistoryRes principalCaseHistoryRes = new PrincipalCaseHistoryRes();
		principalCaseHistoryDao.insertCaseInfo(principalCaseHistoryReq.getIdUser(), principalCaseHistoryReq.getIdCase(),
				principalCaseHistoryReq.getIdLinkCase(), principalCaseHistoryReq.getIndicator());
		return principalCaseHistoryRes;
	}

	/**
	 * Method Description: Method is implemented in PrincipalCaseHistoryDaoImpl
	 * to perform update operations. This method will update Parent to Child
	 * Information. This method updates the Unchecked Linked Case Information
	 * into the Case Link. Users with the Merger Case security attribute will be
	 * able to update linking even if the case is closed and not in their chain
	 * of command. All other users will only be able to use it if the case is
	 * open. EJB Name: PrincipalCaseHistoryBean
	 *
	 * @param principalCaseHistoryReq
	 * @return PrincipalCaseHistoryRes
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public PrincipalCaseHistoryRes updateCaseInfo(PrincipalCaseHistoryReq principalCaseHistoryReq) {
		PrincipalCaseHistoryRes principalCaseHistoryRes = new PrincipalCaseHistoryRes();
		principalCaseHistoryDao.updateCaseInfo(principalCaseHistoryReq.getIdUser(), principalCaseHistoryReq.getIdCase(),
				principalCaseHistoryReq.getIdLinkCase(), principalCaseHistoryReq.getIndicator());
		return principalCaseHistoryRes;
	}

}
