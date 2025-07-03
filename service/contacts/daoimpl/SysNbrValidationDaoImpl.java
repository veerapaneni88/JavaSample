package us.tx.state.dfps.service.contacts.daoimpl;

import java.math.BigDecimal;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.contacts.dao.SysNbrValidationDao;
import us.tx.state.dfps.xmlstructs.inputstructs.NbrValidationDto;
import us.tx.state.dfps.xmlstructs.outputstructs.ServiceOutputDto;
import us.tx.state.dfps.xmlstructs.outputstructs.ValidationMsgDto;

@Repository
public class SysNbrValidationDaoImpl implements SysNbrValidationDao {

	@Value("${Cmsc14dDaoImpl.getUlSysNbrValidationMsg}")
	private String getUlSysNbrValidationMsgSql;

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public ValidationMsgDto getUlSysNbrValidationMsg(NbrValidationDto cmsc14diDto) {

		Query query = sessionFactory.getCurrentSession().createSQLQuery(getUlSysNbrValidationMsgSql);
		query.setParameter("ulIdStage", cmsc14diDto.getIdStage());

		query.setParameter("cpalsvccTrn", ServiceConstants.CPALSVCC_TRN);

		query.setParameter("ulIdPerson", cmsc14diDto.getIdPerson());

		String[] csvcCode = new String[6];

		csvcCode[0] = ServiceConstants.CSVCCODE_18A;
		csvcCode[1] = ServiceConstants.CSVCCODE_18B;
		csvcCode[2] = ServiceConstants.CSVCCODE_18C;
		csvcCode[3] = ServiceConstants.CSVCCODE_18D;
		csvcCode[4] = ServiceConstants.CSVCCODE_18E;
		csvcCode[5] = ServiceConstants.CSVCCODE_18F;

		query.setParameter("csvcCodeList", csvcCode);

		int rowsAffected = ((BigDecimal) query.uniqueResult()).intValueExact();

		ValidationMsgDto cmsc14DoDto = new ValidationMsgDto();
		ServiceOutputDto archOutputStructDto = new ServiceOutputDto();
		archOutputStructDto.setRowQtySize(rowsAffected);

		cmsc14DoDto.setArchOutputStruct(archOutputStructDto);

		cmsc14DoDto.setUlSysNbrValidationMsg(rowsAffected >= ServiceConstants.NUM_ROWS_NEEDED
				? ServiceConstants.NUM_TRUE : ServiceConstants.NUM_FALSE);
		return cmsc14DoDto;

	}
}
