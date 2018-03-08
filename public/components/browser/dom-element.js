/*global window, document*/

/**
 * Basic abstraction around Browser dom elements.
 *
 * Note: May replace this with a micro-library when the project grows.
 *
 * @param {Element} elem
 */
export function domElement(elem) {
  return Object.freeze({
    elem: elem,
    select: domElementFunctionProxy.bind(null, elem, "select"),
    on: domElementEventHandler.bind(null, elem),
    value: domElementValueExtractor.bind(null, elem, "value"),
    setValue: domElementValueSetter.bind(null, elem, "value"),
    innerHTML: domElementValueExtractor.bind(null, elem, "innerHTML")
  });
}

function domElementFunctionProxy(elem, functionName) {
  if (typeof elem[functionName] === "function") {
    const args = Array.prototype.slice.call(arguments, 2);

    return elem[functionName].apply(elem, args);
  }

  throw new Error(functionName + " is not a function on " + elem);
}

function domElementEventHandler(elem, eventType, listener) {
  return elem.addEventListener(eventType, listener);
}

function domElementValueExtractor(elem, valueName) {
  return elem[valueName];
}

function domElementValueSetter(elem, valueName, value) {
  elem[valueName] = value;
}
