import { HttpErrorResponse, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Inject, Injectable, Injector } from '@angular/core';
import { CookieService } from 'ngx-cookie-service';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { catchError, filter, finalize, switchMap, take } from 'rxjs/operators';
import { v4 as uuid } from 'uuid';

import { AuthService, ENVIRONMENT_SETTINGS, LoaderService } from 'dfps-web-lib';

@Injectable()
export class HttpRequestInterceptor implements HttpInterceptor {
    isRefreshingToken = false;
    tokenSubject: BehaviorSubject<string> = new BehaviorSubject<string>(null);
    excludeUrls: Array<string>;
    authService: AuthService;

    constructor(
        private injector: Injector,
        @Inject(ENVIRONMENT_SETTINGS) private envSettings: any,
        private cookieService: CookieService,
        private loaderService: LoaderService,
    ) {
        this.excludeUrls = [this.envSettings.apiUrl + this.envSettings.refreshTokenUrl];
    }

    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<any> {
        if (!this.authService) {
            this.authService = this.injector.get(AuthService);
        }
        if(request.url.indexOf('/oauth/token') !== -1) {
            return next.handle(request);
        }
        if (this.excludeUrls.indexOf(request.url) >= 0) {
            return next.handle(this.addTokenToRequest(request));
        } else {
            return next.handle(this.addTokenToRequest(request)).pipe(
                catchError((error) => {
                    if (error instanceof HttpErrorResponse && error.status !== 0) {
                        switch (error.status) {
                            case 401:
                                return this.handle401Error(request, next);
                            case 400:
                            case 404:
                                return this.handle400And404Error(error);
                            case 500:
                                return this.handle500Error(error);
                        }
                    } else {
                        return this.handleError(error);
                    }
                }),
                finalize(() => {}),
            );
        }
    }

    private addTokenToRequest(request: HttpRequest<any>): HttpRequest<any> {
        if (this.excludeUrls.indexOf(request.url) >= 0) {
            return this.addHeadersForRefreshToken(request);
        } else {
            this.loaderService.addRequest(request);
            const token = this.authService.getBearerToken();
            return request.clone({
                setHeaders: {
                    Authorization: 'Bearer ' + token,
                    //'Content-Type': 'application/json', // Commented the Content Type to accept other content type for File Upload
                    cacheKey:
                        this.envSettings.environmentName == 'Local'
                            ? this.cookieService.get('cacheKey')
                            : sessionStorage.getItem('cacheKey'),
                    'X-Correlation-Id': uuid(),
                },
            });
        }
    }

    private addHeadersForRefreshToken(request: HttpRequest<any>): HttpRequest<any> {
        return request.clone({
            setHeaders: {
                'Content-Type': 'text/plain',
                'X-Correlation-Id': uuid(),
                Authorization: 'Basic ' + this.envSettings.impactRefreshTokenAuth,
            },
        });
    }

    private handle401Error(request: HttpRequest<any>, next: HttpHandler) {
        if (!this.isRefreshingToken) {
            this.isRefreshingToken = true;
            // Reset here so that the following requests wait until the token
            // comes back from the refreshToken call.
            this.tokenSubject.next(null);
            return this.authService.refershTokens().pipe(
                switchMap((newAccessToken: string) => {
                    if (newAccessToken) {
                        this.tokenSubject.next(newAccessToken);
                        return next.handle(this.addTokenToRequest(request));
                    }
                    // If we don't get a new token, we are in trouble so logout.
                    return this.authService.logoutAuthFlow();
                }),
                catchError((error) => {
                    return this.authService.logoutAuthFlow();
                }),
                finalize(() => {
                    this.isRefreshingToken = false;
                }),
            );
        } else {
            return this.tokenSubject.pipe(
                filter((token) => token != null),
                take(1),
                switchMap((token) => {
                    this.isRefreshingToken = false;
                    return next.handle(this.addTokenToRequest(request));
                }),
            );
        }
    }

    private handle400And404Error(error) {
        // If we get a 400 and the error message is 'invalid_grant', the token is no longer valid so logout.
        if (error && error.status === 400 && error.error && error.error.error === 'invalid_grant') {
            return this.authService.logoutAuthFlow();
        }
        return throwError(error);
    }

    private handle500Error(error) {
        return this.handleError(error);
    }

    private handleError(error) {
        return throwError(error);
    }
}