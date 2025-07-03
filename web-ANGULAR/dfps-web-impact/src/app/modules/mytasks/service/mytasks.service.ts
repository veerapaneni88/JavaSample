import { Inject, Injectable, LOCALE_ID } from '@angular/core';
import { ApiService, GlobalMessageServcie } from 'dfps-web-lib';
import { CookieService } from 'ngx-cookie-service';
import { Observable } from 'rxjs';

@Injectable()
export class MytasksService {
    
    readonly SESSION_URL = '/v1/session/';
    
    constructor(
        private cookieService: CookieService,
        private apiService: ApiService,
        private globalMessageService: GlobalMessageServcie,
        @Inject(LOCALE_ID) private locale: string
    ) {
    }

    getUserData(): Observable<any> {
        return this.apiService.get(this.SESSION_URL + this.cookieService.get('impactUserSession'));
    }
}
