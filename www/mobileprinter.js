

//
//  MobilePrinter.js
//
//  Phonegap MobilePrinter Plugin
//  Copyright (c) Senecore, LLC 2012
//

// var MobilePrinter = function() {
    
// };

// //-------------------------------------------------------------------
// MobilePrinter.prototype.printLabel = function(successCallback, errorCallback, params) {
//     // alert("Inside MobilePrinter.printLabel");
//     if (errorCallback == null) { errorCallback = function() {}}

//     if (typeof errorCallback != "function")  {
//         console.log("MobilePrinter.printLabel failure: failure parameter not a function");
//         return
//     }

//     if (typeof successCallback != "function") {
//         console.log("MobilePrinter.printLabel failure: success callback parameter must be a function");
//         return
//     }
//     cordova.exec(successCallback, errorCallback, 'MobilePrinter', 'printLabel', params);
// };

// //-------------------------------------------------------------------
// cordova.addConstructor(function() {
//   cordova.addPlugin('mobilePrinter', new MobilePrinter());
//   // PluginManager.addService('MobilePrinter', 'com.phonegap.plugins.mobileprinter');
// });

var exec = require('cordova/exec');

var mobileprinter = {

    printLabel: function(successCallback, errorCallback, params){
        exec(successCallback, errorCallback, 'MobilePrinter', 'printLabel', params);
    }
}

module.exports = mobileprinter;