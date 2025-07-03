import { ENVIRONMENT_SETTINGS } from 'dfps-web-lib';
import { Inject, Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class HelpService {
    constructor(
        @Inject(ENVIRONMENT_SETTINGS) private environmentSettings: any) {
    }

    loadHelp(moduleName: string) {
        const helpUrl: string = this.environmentSettings.helpUrl;
        console.log(this.environmentSettings.helpUrl);
        if (!helpUrl.endsWith(moduleName.concat('/'))) {
            if (helpUrl.endsWith('Page_Descriptions/')) {
                this.environmentSettings.helpUrl = helpUrl.concat(moduleName.concat('/'));
            } else {
                this.environmentSettings.helpUrl = helpUrl.split('Page_Descriptions/')[0]
                    .concat('Page_Descriptions/'.concat(moduleName).concat('/'));
            }
        }
        console.log(this.environmentSettings.helpUrl);
    }

}