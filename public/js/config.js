// @flow

const ERR_MISSING_KEY: string = 'Missing configuration part';
const ERR_MISSING_CONFIG: string = 'Missing #id_config';

const config = (() => {
  try {
    const $idConfig = document.getElementById('id_config');
    if (!$idConfig) throw new Error(ERR_MISSING_CONFIG);
    const configText = $idConfig.innerHTML;
    return JSON.parse(configText);
  } catch (err) {
    console.error(err);
    return {};
  }
})();

const get = (key: string): any => {
  if (config[key]) return config[key];
  throw new Error(ERR_MISSING_KEY);
};

const route = (routeToGet: string): string => {
  if (config.routes && config.routes[routeToGet])
    return config.routes[routeToGet];
  throw new Error(ERR_MISSING_KEY);
};

const text = (textKey: string, ...replacers: string[]): string => {
  if (config.text && config.text[textKey])
    return replacers.reduce(
      (returnableText, replacer, key) =>
        returnableText.replace(`{${key}}`, replacer),
      config.text[textKey]
    );
  throw new Error([ERR_MISSING_KEY, textKey]);
};

const localisedError = (localisedErrorToGet: string): string => {
  if (config.localisedErrors && config.localisedErrors[localisedErrorToGet])
    return config.localisedErrors[localisedErrorToGet];
  throw new Error(ERR_MISSING_KEY);
};

export { get, route, localisedError, text };
