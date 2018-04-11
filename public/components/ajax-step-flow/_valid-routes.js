import { route } from 'js/config';

type ValidRouteList = string[];

const formRoutes: ValidRouteList = [
  route('twoStepSignInAction'),
  route('signInSecondStepCurrentAction')
];

const linkRoutes: ValidRouteList = [route('twoStepSignIn')];

export { formRoutes, linkRoutes };
