module.exports = {
  extends: ['airbnb', 'prettier'],
  plugins: ['prettier','import','flow'],
  env: {
    browser: true,
    es6: true
  },
  parserOptions: {
    ecmaVersion: 9,
  },
  parser: 'babel-eslint',
  settings : {
    'import/resolver': {
      webpack: {
        config: "webpack.config.js"
      }
    }
  },
  rules: {
    'import/no-extraneous-dependencies': 'off',
    'prettier/prettier': ['error'],
    'no-extend-native': 'error',
    'func-style': ['error', 'expression', { allowArrowFunctions: true }],

    'prefer-destructuring': 'off',
  },
  // don't look for eslintrcs above here
  root: true,
};
