package us.tx.state.dfps.service.caretakerinfo.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.caretaker.dto.CaretkrInfoCaretakerDto;
import us.tx.state.dfps.service.caretaker.dto.CaretkrInfoResourceDto;

@Repository
public interface CareTakerInfoDao {

	public List<CaretkrInfoCaretakerDto> getCareTakerInfo(Long idResource);

	public CaretkrInfoResourceDto getCareTakerInfoFromResource(Long idResource);

	public CaretkrInfoCaretakerDto deleteCareTaker(Long idCaretaker);

	// public CareTakerFetchDto saveCareTakerInfo(Long idResource, String
	// cdHomeMaritalStatus);

	// public CareTakerFetchDto saveCareTakerInCapsCaretaker(CareTakerInfoReq
	// careTakerInfoReq);

	// public CareTakerFetchDto
	// saveCareTakerInCapsCaretaker(CaretakerInformationReq careTakerInfoReq) ;

}
