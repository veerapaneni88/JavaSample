import { Component, OnInit } from '@angular/core';
import { CaseService } from '@case/service/case.service';
import { FormParams, FormValue, NarrativeService, NavigationService } from 'dfps-web-lib';
import { Subscription } from 'rxjs';
import { HelpService } from '../../../../common/impact-help.service';

@Component({
  selector: 'app-outcome-matrix',
  templateUrl: './outcome-matrix.component.html',
  styleUrls: ['./outcome-matrix.component.css']
})
export class OutcomeMatrixComponent implements OnInit {
  outcomeMatrixData: any;
  narrativeSubscription: Subscription;
  clientCategory: boolean;
  formValues: FormValue[];
  caseIdNo: any;
  eventIdNo: any;

  constructor(private caseService: CaseService,
    private narrativeService: NarrativeService,
    private helpService: HelpService,
    private navigationService: NavigationService) { }

  ngOnInit(): void {
    this.navigationService.setTitle('Outcome Matrix');
    this.helpService.loadHelp('Case');
    this.initializeScreen();
  }

  initializeScreen() {
    this.caseService.getOutcomeMatrix().subscribe(res => {
      this.outcomeMatrixData = res;
      this.caseIdNo = this.outcomeMatrixData?.caseId;
      this.eventIdNo = this.outcomeMatrixData?.eventId;
      this.narrativeSubscription = this.narrativeService.generateNarrativeEvent.subscribe(data => {
        this.generateNarrative(data);
      });
      this.formValues = this.getForms();
    });
  }

  generateNarrative(narrativeData: any) {
    if (narrativeData) {
      this.narrativeService.callNarrativeService(narrativeData);
    }
  }

  getNarrative(): FormParams {
    return {
      docType: 'omnarr',
      protectDocument: 'true',
      docExists: this.outcomeMatrixData?.narrativeIndicator ?
        String(this.outcomeMatrixData?.narrativeIndicator) : '',
      sEvent: this.eventIdNo ?
        String(this.eventIdNo) : '',
      sCase: this.caseIdNo ?
        String(this.caseIdNo) : '',
    };
  }

  getForms(): FormValue[] {
    return [{
      formName: 'Outcome Matrix',
      formParams: {
        docType: 'civ34o00',
        docExists: 'false',
        protectDocument: 'true',
        checkForNewMode: 'true',
        pCase: this.caseIdNo ?
          String(this.caseIdNo) : ''
      }
    }
    ];
  }

}
