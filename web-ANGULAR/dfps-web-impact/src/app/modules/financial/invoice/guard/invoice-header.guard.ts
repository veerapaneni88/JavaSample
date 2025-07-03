import { InvoiceService } from './../service/invoice.service';
import { Injectable } from '@angular/core';
import { ActivatedRoute, ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { AuthService } from 'dfps-web-lib';
import { Observable } from 'rxjs';
import { map, take } from 'rxjs/operators';

@Injectable()
export class CanActivateInvoiceHeader implements CanActivate {
    constructor(
        private authService: AuthService,
        private invoiceService: InvoiceService,
        private router: Router,
        private route: ActivatedRoute,
    ) { }
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

    checkInvoiceSearchPrivileges(authorities: string[]) {
        return this.isExternalUser(authorities) && this.canViewInvoice(authorities);
    }

    isExternalUser(authorities: string[]): boolean {
        return (
            authorities.includes('ROLE_SEC_EP_MOD_FC_INV') ||
            authorities.includes('ROLE_SEC_EP_BROWSE_FC') ||
            authorities.includes('ROLE_SEC_EP_MOD_POS_INV') ||
            authorities.includes('ROLE_SEC_EP_BROWSE_POS'));
    }

    canViewInvoice(authorities: string[]): boolean {
        return (((authorities.includes('ROLE_SEC_EP_MOD_FC_INV') || authorities.includes('ROLE_SEC_EP_BROWSE_FC')) &&
            'FSC' === this.getInvoiceType())
            ||
            ((authorities.includes('ROLE_SEC_EP_MOD_POS_INV') || authorities.includes('ROLE_SEC_EP_BROWSE_POS')) &&
                ('DCR' === this.getInvoiceType()) || 'DSB' === this.getInvoiceType() ||
                'DUR' === this.getInvoiceType()));
    }

    getInvoiceType(): any {
        let type = null;
        const invoiceId = +this.route.snapshot.paramMap.get('invoiceId');
        this.invoiceService.displayInvoiceHeaderDetail(invoiceId).subscribe(res => {
            type = res.invoiceResult.type;
        });
        return type;
    }

}
