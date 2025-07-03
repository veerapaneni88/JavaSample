import { Injectable } from '@angular/core';
import {
    ActivatedRouteSnapshot,
    CanActivate,
    Router,
    RouterStateSnapshot,
    UrlTree,
    ActivatedRoute,
} from '@angular/router';
import { AuthService } from 'dfps-web-lib';
import { Observable } from 'rxjs';
import { map, take } from 'rxjs/operators';

@Injectable()
export class CanActivateFinancialAccount implements CanActivate {
    constructor(private authService: AuthService, private router: Router, private activateRoute: ActivatedRoute) {}

    canActivate(
        route: ActivatedRouteSnapshot,
        state: RouterStateSnapshot
    ): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
        return this.authService.getUserInfo().pipe(
            take(1),
            map((userInfo) => {
                let canActivate = false;
                const userAuthorities = userInfo.authorities;
                if (userAuthorities) {
                    canActivate = this.checkFinancialAccountPrivileges(userAuthorities, route.params.accountId);
                }
                return canActivate ? true : this.router.createUrlTree(['/unauthorized']);
            })
        );
    }

    checkFinancialAccountPrivileges(authorities: string[], accountId: string): boolean {
        if (accountId === '0') {
            return (
                authorities.includes('ROLE_SEC_MODIFY_FIN_ACCT_APS') ||
                authorities.includes('ROLE_SEC_FIN_MODIFY_FIN_ACCT')
            );
        } else {
            return (
                authorities.includes('ROLE_SEC_BROWSE_FIN_ACCT_APS') ||
                authorities.includes('ROLE_SEC_FIN_BROWSE_FIN_ACCT') ||
                authorities.includes('ROLE_SEC_FIN_MODIFY_FIN_ACCT') ||
                authorities.includes('ROLE_SEC_MODIFY_FIN_ACCT_APS')
            );
        }
    }
}
