const fonts = [
  {
    name: 'GuardianEgyptianWeb',
    url: localStorage[`gu-font-GuardianEgyptianWeb-url`],
    hash: '03012018'
  }
]

const getFontCss = (font) => {

  const localStorageName = `gu-font-${font.name}-cached-${font.hash}`;

  if(localStorage[localStorageName]) {
    return Promise.resolve(JSON.parse(localStorage[localStorageName]).css)
  }
  else if (localStorage[`gu-font-${font.name}-url`]) {
    return fetch(localStorage[`gu-font-${font.name}-url`]).then(response=> {
      if (!response.ok) throw new Error('invalid');
      return response.text()
    }).then(text => {
      const fontJson = text.replace('guFont(','').slice(0, -2);
      localStorage[localStorageName] = fontJson;
      return JSON.parse(fontJson).css;
    })
  }
}

const loadFonts = () => {
  fonts.forEach(font => getFontCss(font).then(css => {
    const style = document.createElement('style');
    style.innerText = css;
    document.body.appendChild(style);
  }));
}

export {loadFonts}
