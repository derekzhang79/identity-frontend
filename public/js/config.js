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

const get = (key: string) => {
  if (config[key]) return config[key];
  throw new Error(ERR_MISSING_KEY);
};

const route = (routeToGet: string) => {
  if (config.routes && config.routes[routeToGet])
    return config.routes[routeToGet];
  throw new Error(ERR_MISSING_KEY);
};

const localisedError = (localisedErrorToGet: string) => {
  if (config.localisedErrors && config.localisedErrors[localisedErrorToGet])
    return config.localisedErrors[localisedErrorToGet];
  throw new Error(ERR_MISSING_KEY);
};

export { get, route, localisedError };
