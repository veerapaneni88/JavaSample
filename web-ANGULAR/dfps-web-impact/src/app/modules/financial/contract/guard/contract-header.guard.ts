import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { AuthService } from 'dfps-web-lib';
import { Observable } from 'rxjs';
import { map, take } from 'rxjs/operators';

@Injectable()
export class CanActivateContractHeader implements CanActivate {

    constructor(private authService: AuthService, private router: Router) { }

    canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot)
        : Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
        const urlArray = state.url.split('/');
        const contractId = +urlArray[urlArray.length - 1];
        if (contractId === 0) {
            return this.authService.getUserInfo().pipe(take(1), map(userInfo => {
                let canActivate = false;
                const userAuthorities = userInfo.authorities;
                if (userAuthorities) {
                    canActivate = this.checkContractAddPrivileges(userAuthorities);
                }
                return canActivate ? true : this.router.createUrlTree(['/unauthorized']);
            }));
        } else {
            return true;
        }
    }

    checkContractAddPrivileges(authorities: string[]): boolean {
        return ((authorities.includes('ROLE_SEC_CPS_POS_CONTRACT') ||
            authorities.includes('ROLE_SEC_FAC_CONTRACT') ||
            authorities.includes('ROLE_SEC_FAD_CONTRACT') ||
            authorities.includes('ROLE_SEC_APS_POS_CONTRACT')) &&
            (authorities.includes('ROLE_SEC_MNTN_REGION_00') ||
                authorities.includes('ROLE_SEC_MNTN_REGION_01') ||
                authorities.includes('ROLE_SEC_MNTN_REGION_03') ||
                authorities.includes('ROLE_SEC_MNTN_REGION_04') ||
                authorities.includes('ROLE_SEC_MNTN_REGION_05') ||
                authorities.includes('ROLE_SEC_MNTN_REGION_06') ||
                authorities.includes('ROLE_SEC_MNTN_REGION_07') ||
                authorities.includes('ROLE_SEC_MNTN_REGION_08') ||
                authorities.includes('ROLE_SEC_MNTN_REGION_09') ||
                authorities.includes('ROLE_SEC_MNTN_REGION_10') ||
                authorities.includes('ROLE_SEC_MNTN_REGION_11') ||
                authorities.includes('ROLE_SEC_MNTN_REGION_12') ||
                authorities.includes('ROLE_SEC_MNTN_REGION_13') ||
                authorities.includes('ROLE_SEC_MNTN_REGION_14') ||
                authorities.includes('ROLE_SEC_MNTN_REGION_99')));
    }
}
