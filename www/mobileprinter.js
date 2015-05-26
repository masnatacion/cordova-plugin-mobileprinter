

//
//  MobilePrinter.js
//
//  Phonegap MobilePrinter Plugin
//  Copyright (c) Senecore, LLC 2012
//

var MobilePrinter = function() {
    
};

//-------------------------------------------------------------------
MobilePrinter.prototype.printLabel = function(successCallback, errorCallback, params) {
    // alert("Inside MobilePrinter.printLabel");
    if (errorCallback == null) { errorCallback = function() {}}

    if (typeof errorCallback != "function")  {
        console.log("MobilePrinter.printLabel failure: failure parameter not a function");
        return
    }

    if (typeof successCallback != "function") {
        console.log("MobilePrinter.printLabel failure: success callback parameter must be a function");
        return
    }
    cordova.exec(successCallback, errorCallback, 'MobilePrinter', 'printLabel', params);
};

//-------------------------------------------------------------------
cordova.addConstructor(function() {
  cordova.addPlugin('mobilePrinter', new MobilePrinter());
  // PluginManager.addService('MobilePrinter', 'com.phonegap.plugins.mobileprinter');
});


// var mobileprinter = {

//     send: function(successCallback, errorCallback, subject, body, sender, password, recipients, attachment){
//         cordova.exec(successCallback,
//             errorCallback,
//             "SendMail",
//             "send",
//             [{
//                  "subject":subject,
//                  "body":body,
//                  "sender":sender,
//                  "password":password,
//                  "recipients":recipients,
//                  "attachment": attachment,
//             }]
//         );
//     }
// }

// module.exports = sendmail;