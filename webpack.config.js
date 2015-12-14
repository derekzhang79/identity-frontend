var webpack = require('webpack');
var path = require('path');

module.exports = {
  entry: {
    main: './public/main'
  },
  output: {
    path: path.resolve(__dirname, 'target/web/build/'),
    publicPath: "/static/",
    filename: "[name].bundle.js"
  },
  devtool: "#source-map",
  module: {
    loaders: [
      {
        test: /\.js$/,
        exclude: /node_modules/,
        loaders: ['babel?presets[]=es2015']
      }
    ]
  }
};
