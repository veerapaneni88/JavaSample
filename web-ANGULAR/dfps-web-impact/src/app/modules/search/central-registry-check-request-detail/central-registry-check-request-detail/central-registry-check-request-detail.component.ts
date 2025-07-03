import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CrpRequestDetailsDto } from '@shared/model/CentralRegistryRequestDetail';
import { SearchService } from '@shared/service/search.service';
import { DataTable, NavigationService } from 'dfps-web-lib';
import { HelpService } from '../../../../common/impact-help.service';

@Component({
  selector: 'central-registry-check-request-detail',
  templateUrl: './central-registry-check-request-detail.component.html'
})
export class CentralRegistryCheckRequestDetailComponent {

  primReqId: string;
  displayRequestDetail: CrpRequestDetailsDto;
  previousCities: any;
  race: any;
  ssnInd: string;

  tableBody: any[];
  tableColumn: any[];
  otherNamesListTable: DataTable;

  constructor(private route: ActivatedRoute,
    private searchService: SearchService,
    private helpService: HelpService,
    private navigationService: NavigationService) { 
      const reqId = this.route.snapshot.paramMap.get('primReqId') ? this.route.snapshot.paramMap.get('primReqId') : JSON.parse(localStorage.getItem('primReqId'));
      this.navigationService.setUserDataValue('idCrpRequest', reqId );
    }

  ngOnInit(): void {
    this.intializeScreen();
    this.navigationService.setTitle('Central Registry Request Detail');
    this.helpService.loadHelp('Search');
  }

  intializeScreen() {
    this.navigationService.setTitle('Central Registry Request Detail');
    this.primReqId = this.route.snapshot.paramMap.get('primReqId') ? this.route.snapshot.paramMap.get('primReqId') : JSON.parse(localStorage.getItem('primReqId'));
    localStorage.setItem('primReqId', this.primReqId);
    this.searchService.getCrpCheckDetail(this.primReqId).subscribe((response) => {
      this.displayRequestDetail = response;
      this.getOtherNamesDetails();
      this.previousCities = this.displayRequestDetail?.crpPersonAddrCityList?.join(', ');
      this.race = this.displayRequestDetail?.crpPersonRaceList?.join(', ');
      this.ssnInd = this.displayRequestDetail?.ssnInd === 'Y' ? 'Yes' : 'No';
    });
  }

  getOtherNamesDetails() {
    this.tableColumn = [
      { field: 'nameFirst', header: 'Alternate First Name', isHidden: false, isLink: false, width: 50 },
      { field: 'nameMiddle', header: 'Alternate Middle Name', isHidden: false, isLink: false, width: 50 },
      { field: 'nameLast', header: 'Alternate Last Name', isHidden: false, isLink: false, width: 50 },
    ];
    this.otherNamesListTable = {
      tableColumn: this.tableColumn,
      tableBody: this.displayRequestDetail.crpPersonNameList,
      isPaginator: false,
    };
  }

}
