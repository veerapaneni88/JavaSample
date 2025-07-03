package us.tx.state.dfps.service.kinpayment.dao;

import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.kin.dto.KinMonthlyExtPaymentDto;
import us.tx.state.dfps.service.kin.dto.KinPaymentCareGiverDto;
import us.tx.state.dfps.service.kin.dto.KinPaymentChildDto;
import java.util.Date;
import java.util.List;

@Repository
public interface KinshipPaymentDao {

    KinPaymentCareGiverDto getKinCareGiverDetailsSql(Long idStage, Long idResource);

    KinPaymentChildDto getKinChildDetailsSql(Long idResource, Long idPerson);

    Date getKinLegalOutcomeDateSql(Long idCase, Long idPerson);

    List<KinMonthlyExtPaymentDto> getMonthlyExtensionSql(Long eventId);

    public void updateMonthlyPayment(Long svcAuthDtlId, Long eventId);
}
