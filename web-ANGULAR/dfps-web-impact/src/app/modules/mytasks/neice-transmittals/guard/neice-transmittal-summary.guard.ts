import { Injectable } from '@angular/core';
import { CanActivate, Router, UrlTree } from '@angular/router';
import { AuthService } from 'dfps-web-lib';
import { Observable } from 'rxjs';
import { map, take } from 'rxjs/operators';

@Injectable()
export class CanActivateNeiceTransmittalSummary implements CanActivate {
    constructor(private authService: AuthService, private router: Router) { }

    canActivate(): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
        return this.authService.getUserInfo().pipe(
            take(1),
            map((userInfo) => {
                let canActivate = false;
                const userAuthorities = userInfo.authorities;
                if (userAuthorities) {
                    canActivate = this.checkNeiceTransmittalSummaryPrivileges(userAuthorities);
                }
                return canActivate ? true : this.router.createUrlTree(['/unauthorized']);
            })
        );
    }

    checkNeiceTransmittalSummaryPrivileges(authorities: string[]): boolean {
        return (((authorities.includes('ROLE_ICPC_USER'))
        ));
    }

}
