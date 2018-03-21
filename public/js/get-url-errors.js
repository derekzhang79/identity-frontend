// @flow

import { localisedError as getLocalisedError } from './config';

const getUrlErrors = (url: string): string[] => {
  const parsedUrl = new URL(url);
  const errorParams = parsedUrl.searchParams.get('error');

  if (errorParams) {
    return errorParams.split(',').map(getLocalisedError);
  }
  return [];
};

export { getUrlErrors };
export default getUrlErrors;
