// @flow

const getUrlErrors = (url: string): string[] => {
  const parsedUrl = new URL(url);
  const errorParams = parsedUrl.searchParams.get('error');

  if (errorParams) {
    return errorParams.split(',');
  }
  return [];
};

export { getUrlErrors };
export default getUrlErrors;
