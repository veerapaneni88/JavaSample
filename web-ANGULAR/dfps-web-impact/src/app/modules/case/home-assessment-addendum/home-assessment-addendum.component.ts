import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { DisplayHomeAssessmentListAdd } from '../model/homeAssessment';
import { NarrativeService, DirtyCheck, SET, DfpsFormValidationDirective, NavigationService} from 'dfps-web-lib';
import { Subscription } from 'rxjs';
import { CaseService } from '../service/case.service';
import { Router } from '@angular/router';
import { DisplayHomeAssessmentDetail } from '../model/homeAssessmentDetail';
import { Store } from '@ngrx/store';
import { HelpService } from 'app/common/impact-help.service';

@Component({
  selector: 'home-assessment-addendum',
  templateUrl: './home-assessment-addendum.component.html'
})
export class HomeAssessmentAddendumComponent extends DfpsFormValidationDirective implements OnInit {

  homeAddendumForm: FormGroup;
  addendumDetailTypes: any;
  narrativeSubscription: Subscription;
  displayHomeAssessmentListAdd: DisplayHomeAssessmentListAdd;
  formsDto: any;
  displayHomeAssessmentDetail: DisplayHomeAssessmentDetail;

  constructor(
    private navigationService: NavigationService,
    private formBuilder: FormBuilder,
    private narrativeService: NarrativeService,
    private caseService: CaseService,
    private router: Router,
    private helpService: HelpService,
    public store: Store<{ dirtyCheck: DirtyCheck }>
  )  {
    super(store);
  }

  createForm() {
    this.homeAddendumForm = this.formBuilder.group(
      {
        addendumDetail: ['']
      });
  }

  ngOnInit(): void {
    this.navigationService.setTitle('Home Assessment Addendum Detail');
    this.helpService.loadHelp('Case');
    this.createForm();
    this.addendumDetailTypes = [
      { label: 'CPS Home Assessment', value: '1' },
      { label: 'Contract Home Assessment', value: '2' },
      { label: 'Kinship Safety Evaluation', value: '3' },
    ];
    this.homeAddendumForm.get('addendumDetail').setValue('1');
    this.intializeScreen();
  }

  intializeScreen() {
    this.caseService.getKinHomeAssessmentListAdd().subscribe((response) => {
      this.displayHomeAssessmentListAdd = response;
      this.narrativeSubscription = this.narrativeService.generateNarrativeEvent.subscribe(data => {
        this.generateNarrative();
      });
      
    this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
    })
  }

  generateNarrative() {

    const addendumDetail = this.homeAddendumForm.get('addendumDetail').value;
    let selectedType;
    if (this.displayHomeAssessmentListAdd.eventMap.cpsHomeAssessment && addendumDetail === '1') {
      selectedType = this.displayHomeAssessmentListAdd.eventMap.cpsHomeAssessment;
    } else if (this.displayHomeAssessmentListAdd.eventMap.contractHomeAssessment && addendumDetail === '2') {
      selectedType = this.displayHomeAssessmentListAdd.eventMap.contractHomeAssessment;
    } else if (this.displayHomeAssessmentListAdd.eventMap.kinshipSafetyEvaluation && addendumDetail === '3') {
      selectedType = this.displayHomeAssessmentListAdd.eventMap.kinshipSafetyEvaluation;
    }
    const payload = { eventType: selectedType };
    this.caseService.saveKinHomeAssessmentEvent(payload).subscribe(
      response => {
        this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
        const responseObject = response;
      
        if (responseObject) {
          this.narrativeService.callNarrativeService(responseObject.formTagDto);
        }

        setTimeout(() => {
          this.router.navigate(['/case/home-assessment-addendum-list/' + responseObject.event.eventId]);
        }, 3000);

      }
    );
  }

}
