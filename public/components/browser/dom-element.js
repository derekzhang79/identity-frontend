/*global window, document*/

/**
 * Basic abstraction around Browser dom elements.
 *
 * Note: May replace this with a micro-library when the project grows.
 *
 * @param {Element} elem
 */
export function domElement( elem ) {
  return Object.freeze({
    on: domElementEventHandler.bind( null, elem ),
    value: domElementValueExtractor.bind( null, elem, 'value' ),
    setValue: domElementValueSetter.bind( null, elem, 'value' )
  });
}

function domElementEventHandler( elem, eventType, listener ) {
  return elem.addEventListener( eventType, listener );
}

function domElementValueExtractor( elem, valueName ) {
  return elem[ valueName ];
}

function domElementValueSetter( elem, valueName, value ) {
  elem[ valueName ] = value;
}
