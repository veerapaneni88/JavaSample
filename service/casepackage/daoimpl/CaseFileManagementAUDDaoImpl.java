package us.tx.state.dfps.service.casepackage.daoimpl;

import java.util.Date;

import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.common.domain.CapsCase;
import us.tx.state.dfps.common.domain.CaseFileManagement;
import us.tx.state.dfps.common.domain.Office;
import us.tx.state.dfps.common.domain.Unit;
import us.tx.state.dfps.common.dto.ErrorDto;
import us.tx.state.dfps.service.casepackage.dao.CaseFileManagementAUDDao;
import us.tx.state.dfps.service.casepackage.dto.CaseFileManagementInDto;
import us.tx.state.dfps.service.casepackage.dto.CaseFileManagementOutDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.response.CommonHelperRes;

@Repository
public class CaseFileManagementAUDDaoImpl implements CaseFileManagementAUDDao {
	@Autowired
	MessageSource messageSource;

	@Value("${CaseFileManagementAUDDaoImpl.caseFileManagementAUD}")
	private String caseFileManagementAUD;

	@Value("${CaseFileManagementAUDDaoImpl.caseFileManagementUpdate}")
	private String caseFileManagementUpdate;

	@Value("${CaseFileManagementAUDDaoImpl.caseFileManagementDelete}")
	private String caseFileManagementDelete;

	@Autowired
	private SessionFactory sessionFactory;

	/**
	 * Method Name: caseFileManagementAUD Method Description:save
	 * caseFileManagement
	 * 
	 * @param caseFileManagementInDto
	 * @param caseFileManagementOutDto
	 * @return
	 */
	@Override
	public CommonHelperRes caseFileManagementAUD(CaseFileManagementInDto caseFileManagementInDto,
			CaseFileManagementOutDto caseFileManagementOutDto) {
		CommonHelperRes commonHelperRes = new CommonHelperRes();
		switch (caseFileManagementInDto.getReqFuncCd()) {
		case ServiceConstants.REQ_FUNC_CD_ADD:
			CaseFileManagement caseFileManagement = new CaseFileManagement();
			Unit unit = new Unit();
			Office office = new Office();
			CapsCase capsCase = new CapsCase();
			caseFileManagement.setTxtAddSkpTrn(caseFileManagementInDto.getTxtAddSkpTrnInfo2());
			caseFileManagement.setNmCaseFileOffice(caseFileManagementInDto.getNmCaseFileOffice());
			caseFileManagement.setAddrCaseFileStLn2(caseFileManagementInDto.getAddrCaseFileStLn2());
			caseFileManagement.setAddrCaseFileStLn1(caseFileManagementInDto.getAddrCaseFileStLn1());
			caseFileManagement.setDtLastUpdate(new Date());
			caseFileManagement.setCdCaseFileOfficeType(caseFileManagementInDto.getCdCaseFileOfficeType());
			unit.setIdUnit(caseFileManagementInDto.getIdUnit());
			caseFileManagement.setTxtCaseFileLocateInfo(caseFileManagementInDto.getTxtCaseFileLocateInfo());
			caseFileManagement.setAddrCaseFileCity(caseFileManagementInDto.getAddrCaseFileCity());
			office.setIdOffice(caseFileManagementInDto.getIdOffice());
			if (!ObjectUtils.isEmpty(office) && office.getIdOffice() != 0) {
				caseFileManagement.setOffice(office);
				caseFileManagement.setDtCaseFileArchCompl(caseFileManagementInDto.getDtCaseFileArchCompl());
				caseFileManagement.setDtCaseFileArchElig(caseFileManagementInDto.getDtCaseFileArchElig());
			}
			capsCase.setIdCase(caseFileManagementInDto.getIdCase());
			if (!ObjectUtils.isEmpty(unit) && unit.getIdUnit() != 0l) {
				caseFileManagement.setUnit(unit);
			}
			caseFileManagement.setCapsCase(capsCase);

			sessionFactory.getCurrentSession().save(caseFileManagement);

			break;
		case ServiceConstants.REQ_FUNC_CD_UPDATE:
			try {
				SQLQuery sQLQuery2 = ((SQLQuery) sessionFactory.getCurrentSession()
						.createSQLQuery(caseFileManagementUpdate)
						.setParameter("txtAddSkpTrnInfo2", caseFileManagementInDto.getTxtAddSkpTrnInfo2())
						.setParameter("nmCaseFileOffice", caseFileManagementInDto.getNmCaseFileOffice())
						.setParameter("addrCaseFileStLn2", caseFileManagementInDto.getAddrCaseFileStLn2())
						.setParameter("addrCaseFileStLn1", caseFileManagementInDto.getAddrCaseFileStLn1())
						.setParameter("dtCaseFileArchCompl", caseFileManagementInDto.getDtCaseFileArchCompl())
						.setParameter("dtCaseFileArchElig", caseFileManagementInDto.getDtCaseFileArchElig())
						.setParameter("tsLastUpdate", caseFileManagementInDto.getTsLastUpdate())
						.setParameter("cdCaseFileOfficeType", caseFileManagementInDto.getCdCaseFileOfficeType())
						.setParameter("idUnit", caseFileManagementInDto.getIdUnit())
						.setParameter("txtCaseFileLocateInfo", caseFileManagementInDto.getTxtCaseFileLocateInfo())
						.setParameter("addrCaseFileCity", caseFileManagementInDto.getAddrCaseFileCity())
						.setParameter("idCase", caseFileManagementInDto.getIdCase())
						.setParameter("idOffice", caseFileManagementInDto.getIdOffice()));
				sQLQuery2.executeUpdate();
			} catch (HibernateException e) {
				ErrorDto errorDto = new ErrorDto();
				errorDto.setErrorMsg(ServiceConstants.FAIL);
				commonHelperRes.setErrorDto(errorDto);
				return commonHelperRes;
			}

			break;
		case ServiceConstants.REQ_FUNC_CD_DELETE:
			SQLQuery sQLQuery3 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(caseFileManagementDelete)
					.setParameter("tsLastUpdate", caseFileManagementInDto.getTsLastUpdate())
					.setParameter("idCase", caseFileManagementInDto.getIdCase()));
			sQLQuery3.executeUpdate();

			break;
		}
		return commonHelperRes;
	}

}
