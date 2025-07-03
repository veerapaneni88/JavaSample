import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CaseService } from '@case/service/case.service';
import { Subscription } from 'rxjs';
import { HelpService } from '../../../../common/impact-help.service';
import { NavigationService } from 'dfps-web-lib';

@Component({
  selector: 'app-outcome-matrix-details',
  templateUrl: './outcome-matrix-details.component.html',
  styleUrls: ['./outcome-matrix-details.component.css']
})
export class OutcomeMatrixDetailsComponent implements OnInit, OnDestroy {

  paramSub: Subscription;
  detailsSub: Subscription;
  outcomeMatrixId: string;
  outcomeMatrixDetails: any;
  care: boolean;
  stageName: string;
  actionCategoryValue: string;
  actionSubCategoryValue: string;
  actionDateValue: Date;
  actionCommentsValue: string;
  outcomeSubCategoryValue: string;
  outcomeDateValue: Date;
  outcomeCommentsValue: string;


  constructor(
    private route: ActivatedRoute, 
    private caseService: CaseService,
    private navigationService: NavigationService,
    private helpService: HelpService) { }

  ngOnInit(): void {
    this.navigationService.setTitle('APS Outcome Matrix Detail Page');
    this.helpService.loadHelp('Case');
    this.paramSub = this.route.params.subscribe((params) => {
      this.outcomeMatrixId =  params.id;
      this.getOutcomeMatrixDetails();
    });
  }

  getOutcomeMatrixDetails(): void {
    if (this.outcomeMatrixId) {
      this.detailsSub = this.caseService.getOutcomeMatrixDetails(this.outcomeMatrixId).subscribe((res) => {
        this.stageName = res.stageName;
        this.outcomeMatrixDetails = res.omResponses.find((x) => x.careFactorId.toString() === this.outcomeMatrixId);
        this.care = this.outcomeMatrixDetails?.cdCareDomain ? true : false;
        this.populateDropdownValues();
      });
    }
  }

  populateDropdownValues() {
    this.actionCategoryValue = this.outcomeMatrixDetails?.apsOutcomeActnCategCode;
    this.actionSubCategoryValue = this.outcomeMatrixDetails?.apsOutcomeActionCode;
    this.actionDateValue = this.outcomeMatrixDetails?.dtApsOutComeAction;
    this.actionCommentsValue = this.outcomeMatrixDetails?.apsOutcomeActnTxt;
    this.outcomeSubCategoryValue = this.outcomeMatrixDetails?.cdApsOutComeResult;
    this.outcomeDateValue = this.outcomeMatrixDetails?.dtApsOutComeRecord;
    this.outcomeCommentsValue = this.outcomeMatrixDetails?.apsOutcomeResultTxt;
  }


  ngOnDestroy(): void {
    this.paramSub?.unsubscribe();
    this.detailsSub?.unsubscribe();
  }

}
