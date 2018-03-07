/*eslint-env node*/

var webpack = require( 'webpack' );
var path = require( 'path' );

module.exports = {
  entry: {
    main: './public/main'
  },
  output: {
    path: path.resolve( __dirname, 'target/web/public/main/' ),
    publicPath: '/static/',
    filename: '[name].bundle.js'
  },
  devtool: '#source-map',
  module: {
    loaders: [
      {
        test: /\.js$/,
        exclude: /node_modules/,
        loaders: [ 'babel?presets[]=es2015' ]
      }
    ]
  },
  plugins: [
    new webpack.optimize.OccurrenceOrderPlugin(),
    new webpack.optimize.UglifyJsPlugin( {
      mangle: false
    } )
  ],
  resolve: {
    alias: {
      'intl-tel': 'intl-tel-input/build/js/intlTelInput',
      'intl-tel-utils': 'intl-tel-input/build/js/utils.js'
    }
  }
};
