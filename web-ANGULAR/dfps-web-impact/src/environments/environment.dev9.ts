export const environment = {
  production: false,
  environmentName: 'Dev9',
  disableConsole: true,
  idleSessionDuration: 1800,
  apiUrl: 'https://p2dev9.dfps.texas.gov/dfps-pageapi-impact',
  impactLoginUrl: 'https://p2dev9.dfps.texas.gov/web/login',
  impactP2WebUrl: 'https://p2dev9.dfps.texas.gov/web',
  impactRefreshTokenAuth: 'SU1QQUNUUEgzOklNUEFDVC1QSDMtUkVGUkVTSA==',
  formsUrl: 'https://p2dev9.dfps.texas.gov/web/document/launch',
  helpUrl: 'https://p2dev9.dfps.texas.gov/web/resources/help/index.htm#t=Page_Descriptions/Finance/',
  logApiUrl: '/v1/log/client',
  primaryNavigationUrl: '/v1/navigation/primary',
  secondaryNavigationUrl: '/v1/navigation/secondary/',
  ternaryNavigationUrl: '/v1/navigation/ternary/',
  userInfoUrl: '/v1/users/info',
  logoutUrl: '/v1/auth/logout',
  refreshTokenUrl: '/v1/auth/refresh-tokens',
  reportServiceUrl: '/v1/reports/launch',
  primaryNavigation: ['financial'],
  secondaryNavigation: ['contract', 'invoice', 'financialsPaymentApproval', 'financialsPaymentHistory',
    'financial-account', 'payments', 'homeSearch', 'serviceArea', 'central-registry-person-search',
    'service-level-error'],
  ternaryNavigation: ['authProvider', 'contract', 'invoice', 'financialAccntSearch', 'financialAccntDetail',
    'financialAccntRegister', 'regionalAccntDetail', 'reconciliation', 'processPayment', 'paymentGroup',
    'injuryDetail', 'safetyPlan', 'dangerIndicators',
    'financialAccntReport', 'home-information', 'placementLog', 'homeHistory', 'paymentInfo', 'home-assessment-addendum-list','servicePackageTab',
    'centralRegistryRequestDetail', 'centralRegistryRecordCheckDetail'],
  isMPSEnvironment: false,
  jwtTokenEndpoint: 'https://p2dev9.dfps.texas.gov/dfps-security-oauth2/oauth/token',
    enableImpersonation: true,
    config: {
        auth: {
            clientId: '7b1219a6-9cf0-4bb2-b0d3-0c0bd7eb2ee4',
            authority: 'https://login.microsoftonline.com/057119a8-cdc3-47b9-91d4-8bd4e013a246',
            validateAuthority: true,
            redirectUri: 'https://p2dev9.dfps.texas.gov/impact3/',
            postLogoutRedirectUri: 'https://p2dev9.dfps.texas.gov/web/login/',
            navigateToLoginRequestUrl: true,
        },
        cache: {
            cacheLocation: 'localStorage',
        },
        scopes: {
            loginRequest: ['openid', 'profile', 'email', 'offline_access'],
        },
    },
};

