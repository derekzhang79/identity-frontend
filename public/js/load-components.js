const components = [];

const initOnceList = [];

const loadComponents = $root => {
  components.forEach(component => {
    [...$root.querySelectorAll(`.${component[1]}`)]
      .filter($target => !$target.dataset.enhanced)
      .forEach($target => {
        if (component[2] && !initOnceList.includes(component[1])) {
          component[2]();
          initOnceList.push(component[1]);
        }
        $target.dataset.enhanced = true;
        component[0]($target);
      });
  });
};

export { loadComponents };
export default loadComponents;
