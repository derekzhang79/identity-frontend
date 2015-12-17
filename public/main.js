import { logPageView } from './components/analytics/analytics';

import { init as initSignin } from './components/signin/signin';

logPageView();

initSignin();
