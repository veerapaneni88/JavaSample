import { formatDate } from '@angular/common'; 
import { Inject, Injectable, LOCALE_ID } from '@angular/core';
import { ApiService, GlobalMessageServcie } from 'dfps-web-lib';
import { Observable } from 'rxjs';
import { map} from 'rxjs/operators';
import { PersonTrainingRes, PersonTraining } from './../model/persontraining'; 

@Injectable()
export class PersonTrainingService {
    readonly PERSON_TRAINING_URL: string = '/v1/person/'; 

    constructor( 
        private apiService: ApiService,
        private globalMessageService: GlobalMessageServcie,
        @Inject(LOCALE_ID) private locale: string
        ) {
    } 

    getPersonTrainingRes(personId: any, trainingId: any): Observable<PersonTrainingRes> {
        return this.apiService.get(this.PERSON_TRAINING_URL 
            + personId + '/training/' + trainingId
            ).pipe(
                map((res: PersonTrainingRes) => {
                    if (res.personTraining) {
                        if (res.personTraining.trainingDate) {
                            res.personTraining.trainingDate = formatDate(
                                res.personTraining.trainingDate.split(' ')[0], 'MM/dd/yyyy', this.locale);
                        }
                        if (res.personTraining.purgedDate) {
                            res.personTraining.purgedDate = formatDate(
                                res.personTraining.purgedDate.split(' ')[0], 'MM/dd/yyyy', this.locale);
                        }
                    }
                    return res;
                })
            )
    }

    deletePersonTrainingRes(personId: any, trainingId: any): Observable<any> {
        return this.apiService
            .delete(this.PERSON_TRAINING_URL + personId + '/training/' + trainingId)
            .pipe(
                map((res) => {
                    return { ...res, response: res == null ? { res: 'success' } : {} };
                })
            );
    } 

    savePersonTraining(personId: any, personTraining: PersonTraining) {
        const personTrainingClone = Object.assign({}, personTraining);
        personTrainingClone.id = personTraining.id;
        if (personTrainingClone.id == null) {
            personTrainingClone.id = '0';
        }
        
        if (personTraining.trainingDate) {
            personTrainingClone.trainingDate = formatDate(personTrainingClone.trainingDate.split(' ')[0],
                'yyyy-MM-dd HH:mm:ss', this.locale);
        } 

        if (personTraining.evaluationComponent) {
            personTrainingClone.evaluationComponent = 'Y';
        }else{
            personTrainingClone.evaluationComponent = 'N';
        } 

        if (personTraining.kinTrainingCompleted) {
            personTrainingClone.kinTrainingCompleted = 'Y';
        }else{
            personTrainingClone.kinTrainingCompleted = 'N';
        }

        return this.apiService
            .post(this.PERSON_TRAINING_URL + personId + '/training', personTrainingClone);
    }

} 