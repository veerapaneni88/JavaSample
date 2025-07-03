import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { AuthService } from 'dfps-web-lib';
import { Observable } from 'rxjs';
import { map, take } from 'rxjs/operators';

@Injectable()
export class CanActivateInvoiceSearch implements CanActivate {
    constructor(private authService: AuthService, private router: Router) {}

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
                    canActivate = this.checkInvoiceSearchPrivileges(userAuthorities);
                }
                return canActivate ? true : this.router.createUrlTree(['/unauthorized']);
            })
        );
    }

    checkInvoiceSearchPrivileges(authorities: string[]): boolean {
        return (
            authorities.includes('ROLE_SEC_FIN_BROWSE_INVOICE') ||
            authorities.includes('ROLE_SEC_FIN_MODIFY_INVOICE') ||
            authorities.includes('ROLE_SEC_EP_BROWSE_FC') ||
            authorities.includes('ROLE_SEC_EP_BROWSE_POS') ||
            authorities.includes('ROLE_SEC_EP_MOD_FC_INV') ||
            authorities.includes('ROLE_SEC_EP_MOD_POS_INV')
        );
    }
}
