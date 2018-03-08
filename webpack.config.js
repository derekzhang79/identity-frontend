/*eslint-env node*/

const webpack = require( 'webpack' );
const path = require( 'path' );
const { CheckerPlugin } = require('awesome-typescript-loader')

module.exports = {
  entry: {
    main: './public/main'
  },
  output: {
    path: path.resolve( __dirname, 'target/web/build-npm/' ),
    publicPath: '/static/',
    filename: '[name].bundle.js'
  },
  module: {
    rules: [
      {
        test: /\.tsx?$/,
        exclude: /node_modules/,
        use: ['awesome-typescript-loader'],
      },
      {
        test: /\.js$/,
        exclude: /node_modules/,
        use: ['babel-loader'],
      }
    ]
  },
  plugins: [
    new webpack.LoaderOptionsPlugin({
      minimize: true
    }),
    new CheckerPlugin(),
  ],
  resolve: {
    extensions: ['.ts', '.js', '.json'],
    alias: {
      'intl-tel': 'intl-tel-input/build/js/intlTelInput',
      'intl-tel-utils': 'intl-tel-input/build/js/utils.js'
    }
  }
};
