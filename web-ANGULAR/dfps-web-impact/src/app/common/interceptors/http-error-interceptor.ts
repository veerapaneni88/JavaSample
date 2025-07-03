import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Inject, Injectable } from '@angular/core';
import { select, Store } from '@ngrx/store';
import { ADD, ENVIRONMENT_SETTINGS, ERROR_RESET, GlobalError, LoaderService, SUCCESS_RESET } from 'dfps-web-lib';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

@Injectable()
export class HttpErrorResponseInterceptor implements HttpInterceptor {
    error$: Observable<GlobalError>;
    nextIndex = 0;

    constructor(
        private store: Store<{ error: GlobalError }>,
        @Inject(ENVIRONMENT_SETTINGS) private envSettings: any,
        private loaderService: LoaderService,
    ) {
        this.error$ = store.pipe(select('error'));
    }

    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        if(request.url.indexOf('/oauth2/v2.0/authorize') !== -1) {
            return next.handle(request).pipe(
                catchError((err) => {
                this.loaderService.removeRequest(request);
                return throwError(err);
            }))
        }
        return next.handle(request).pipe(
            catchError((err) => {
                if ([401, 403, 409].indexOf(err.status) !== 0) {
                    const url = request.url.split(this.envSettings.apiUrl)[1];
                    this.deleteMessages();
                    this.createErrorMessage(err, url);
                }
                this.loaderService.removeRequest(request);
                setTimeout(() => {
                    try {
                        document.getElementById('errorMessageId').focus();
                    } catch (e) {}
                }, 100);
                return throwError(err);
            }),
        );
    }

    createErrorMessage(err: any, url: string) {
        const errorMessages = [];
        if (err && err.error && err.error.errors) {
            err.error.errors.forEach((el) => {
                if (el.length > 0) {
                    errorMessages.push(el);
                }
            });
        } else {
            let errMsg = 'Page API service is down or not available.';
            if (err && err.status === 400) {
                errMsg = err.error.error_description;
            } else if (err && (err.status === 500 || err.status === 401 || !err.status)) {
                errMsg = 'Error while authenticating the user from DFPS Security OAuth';
            } else  if (err && err.error && err.error.error) {
                errMsg = err.headers.get('x-correlation-id') + ' : ' + err.error.error;
            }
            errorMessages.push(errMsg);
        }

        const errorObj: GlobalError = {
            statusCode: err.status,
            errorMessage: errorMessages,
            urlPath: url,
            index: this.nextIndex,
        };

        const lastObj = this.error$.subscribe((x) => {
            if (x.length > 0) {
                this.nextIndex = x.length;
            }
        });

        if (errorObj.errorMessage.length > 0) {
            this.store.dispatch(ADD({ error: errorObj }));
        }

        setTimeout(() => {
            this.deleteMessages();
            lastObj.unsubscribe();
        }, 100000);
    }

    deleteMessages() {
        this.store.dispatch(ERROR_RESET(null));
        this.store.dispatch(SUCCESS_RESET(null));
        this.nextIndex = 0;
    }
}