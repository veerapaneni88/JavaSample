import { ENVIRONMENT_SETTINGS } from 'dfps-web-lib';
import { Inject, Injectable } from '@angular/core';

const isIE = window.navigator.userAgent.indexOf("MSIE ") > -1 || window.navigator.userAgent.indexOf("Trident/") > -1;

@Injectable({ providedIn: 'root' })
export class MsalConfiguration {

  constructor(
    @Inject(ENVIRONMENT_SETTINGS) private environmentSettings: any) {
  }

  getConfig() {
    if (this.environmentSettings.environmentName.startsWith('BSL')) {
      return null;
    }
    return {
      auth: {
        clientId: this.environmentSettings.config.auth.clientId,
        authority: this.environmentSettings.config.auth.authority,
        redirectUri: this.environmentSettings.config.auth.redirectUri,
        postLogoutRedirectUri: this.environmentSettings.config.auth.postLogoutRedirectUri,
        navigateToLoginRequestUrl: true
      },
      cache: {
        cacheLocation: this.environmentSettings.config.cache.cacheLocation,
        //storeAuthStateInCookie: isIE,
      },
      system: {
        loggerOptions: {
          loggerCallback: () => { },
          piiLoggingEnabled: false
        }
      }
    };
  }

}