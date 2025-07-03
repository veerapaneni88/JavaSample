import { Component, OnInit } from '@angular/core';
import { CaseService } from '@case/service/case.service';
import {FormValue, NarrativeService, DataTable, NavigationService} from 'dfps-web-lib';
import { BsModalService } from 'ngx-bootstrap/modal';
import { FormBuilder, FormGroup, FormArray, FormControl, Validators, NG_VALUE_ACCESSOR } from '@angular/forms';
import { Subscription } from 'rxjs';
import { ActivatedRoute } from '@angular/router';
import { DisplayContactStandardDetails } from '@case/model/contactStandardDetails';
import {HelpService} from "../../../common/impact-help.service";

@Component({
  selector: 'app-contact-standard-details',
  templateUrl: './contact-standard-details.component.html',
  styleUrls: ['./contact-standard-details.component.css']
})
export class ContactStandardDetailsComponent implements OnInit {
  paramSub: Subscription;
  apsSpMonitoringPlanId: any;
  displayContactStandardDetails :DisplayContactStandardDetails;
  caseId: any;
  caseName: any;
  numberOfContactsReqd : any;
  numberOfFaceToFaceContacts : any;

  constructor(private caseService: CaseService,private modalService: BsModalService,private helpService: HelpService,
              private navigationService: NavigationService,private narrativeService: NarrativeService, private route: ActivatedRoute, private fb: FormBuilder,) { }

  ngOnInit(): void {
    this.navigationService.setTitle('Contact Detail Page - APS');
    this.helpService.loadHelp('Contacts');
    this.paramSub = this.route.params.subscribe((params) => {
      this.apsSpMonitoringPlanId =  params["apsSpMonitoringPlanId"];
    });

    this.initializeScreen();
  }

  initializeScreen() {
    this.caseService.getSPContactStandardDetails(this.apsSpMonitoringPlanId).subscribe(res => {
      this.displayContactStandardDetails = res;
      if (this.displayContactStandardDetails) {
        if (this.displayContactStandardDetails.apsServicePlanMonitoringDto) {
          this.caseId = res.caseId;
          this.caseName = res.caseName;
          this.numberOfContactsReqd = [{"code":this.displayContactStandardDetails.apsServicePlanMonitoringDto.numberOfContactsReqd,"decode":this.displayContactStandardDetails.apsServicePlanMonitoringDto.numberOfContactsReqd}];
          this.numberOfFaceToFaceContacts = [{"code":this.displayContactStandardDetails.apsServicePlanMonitoringDto.numberOfFaceToFaceContacts,"decode":this.displayContactStandardDetails.apsServicePlanMonitoringDto.numberOfFaceToFaceContacts}]
          }}

      });

  }
}
