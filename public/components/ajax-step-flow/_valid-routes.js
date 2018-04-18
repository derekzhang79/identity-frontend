import { route } from 'js/config';

type ValidRouteList = string[];

const formRoutes: ValidRouteList = [
  route('twoStepSignInAction'),
  route('signInSecondStepCurrentAction'),
  route('sendResubLinkAction')
];

const linkRoutes: ValidRouteList = [route('twoStepSignIn')];

export { formRoutes, linkRoutes };
