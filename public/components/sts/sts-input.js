const getErrorWrapper = ($input) => {
  const $parent = $input.parentNode;
  if($parent.classList.contains('sts-input-wrap')) {
    return $parent;
  }
  else {
    const $newWrapper = document.createElement('div');
    $newWrapper.classList.add('sts-input-wrap')
    $parent.insertBefore($newWrapper, $input);
    $newWrapper.appendChild($input);
    return $newWrapper;
  }
}

const resetErrorWrapper = ($wrapper) => {
  $wrapper.classList.remove('sts-input-wrap--error');
  [...$wrapper.querySelectorAll('.sts-input-wrap__info')].forEach(_=>_.remove());
}

const appendError = ($wrapper, text) => {
  const $error = document.createElement('div');
  $error.classList.add('sts-input-wrap__info');
  $error.innerText = text;
  $wrapper.classList.add('sts-input-wrap--error');
  $wrapper.appendChild($error);
}

export const addError = ($input, text) => {
  const $wrapper = getErrorWrapper($input);
  appendError($wrapper, text)
  const onChange = () => {
    resetErrorWrapper($wrapper);
    ['blur','change'].forEach(_=>$input.removeEventListener(_, onChange))
  }
  ['blur','change'].forEach(_=>$input.addEventListener(_, onChange))

}
