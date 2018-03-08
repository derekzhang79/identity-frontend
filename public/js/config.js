const ERR_MISSING_KEY = 'Missing configuration part';

const config = {}

try {
  const configElem = document.getElementById('id_config').innerHTML;
  const parsed = JSON.parse(configElem);
  Object.assign(config,parsed);
} catch(err) {
  console.warn(err)
  throw(err);
}

const get = key => {
  if(config[key]) return config[key]
  else throw new Error(ERR_MISSING_KEY);
}

const route = route => {
  if(config['routes'] && config['routes'][route]) return config['routes'][route];
  else throw new Error(ERR_MISSING_KEY);
}

export {get, route}
