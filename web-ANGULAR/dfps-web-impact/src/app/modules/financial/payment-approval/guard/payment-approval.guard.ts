import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { AuthService } from 'dfps-web-lib';
import { Observable } from 'rxjs';
import { map, take } from 'rxjs/operators';

@Injectable()
export class CanActivatePaymentApproval implements CanActivate {

    constructor(private authService: AuthService, private router: Router) { }

    canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot)
        : Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
        return this.authService.getUserInfo().pipe(take(1), map(userInfo => {
            let canActivate = false;
            const userAuthorities = userInfo.authorities;
            if (userAuthorities) {
                canActivate = this.checkPaymentApprovalPrivileges(userAuthorities);
            }
            return canActivate ? true : this.router.createUrlTree(['/unauthorized']);
        }));
    }


    checkPaymentApprovalPrivileges(authorities: string[]): boolean {
        return (authorities.includes('ROLE_SEC_FIN_MODIFY_APS_PAY_APPVL') ||
            authorities.includes('ROLE_SEC_FIN_MODIFY_CPS_PAY_APPVL'));
    }
}
