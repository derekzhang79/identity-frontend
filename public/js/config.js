const ERR_MISSING_KEY = 'Missing configuration part';

const config = (() => {
  try {
    const configElem = document.getElementById('id_config').innerHTML;
    return JSON.parse(configElem);
  } catch (err) {
    console.error(err);
    return {};
  }
})();

const get = key => {
  if (config[key]) return config[key];
  throw new Error(ERR_MISSING_KEY);
};

const route = routeToGet => {
  if (config.routes && config.routes[routeToGet])
    return config.routes[routeToGet];
  throw new Error(ERR_MISSING_KEY);
};

const localisedError = localisedErrorToGet => {
  if (config.localisedErrors && config.localisedErrors[localisedErrorToGet])
    return config.localisedErrors[localisedErrorToGet];
  throw new Error(ERR_MISSING_KEY);
};

export { get, route, localisedError };
