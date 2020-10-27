# cordova-plugin-prevent-screenshot-coffice

This is a cordova plugin to enable/disable screenshots in android and ios 

## Supported Platforms

- Android API all versions('Detect and prevent' screenshot functionality)*
- IOS all versions (Only 'detect screenshot' functionality)

*For android the detect functionality is based on an Observer that keeps checking if a file with name 'Screenshot' was created while the app was opened.
It was taken from https://stackoverflow.com/questions/31360296/listen-for-screenshot-action-in-android
And was the only option I found, personally I didn't liked

## Installation

Cordova local build:
    cordova plugin add <GIT URL PATH>




## Usage in javascript

```js
document.addEventListener("deviceready", onDeviceReady, false);
// Enable
function onDeviceReady() {
  window.plugins.preventscreenshot.enable(successCallback, errorCallback);
}
// Disable
function onDeviceReady() {
  window.plugins.preventscreenshot.disable(successCallback, errorCallback);
}

function successCallback(result) {
  console.log(result); // true - enabled, false - disabled
}

function errorCallback(error) {
  console.log(error);
}


document.addEventListener("onTookScreenshot",function(){
// Receive notification when screenshot is ready;
});

document.addEventListener("onGoingBackground",function(){
// Receive notification when control center or app going in background.
});



//Activate Detect functionality for android
function EnableDetect() {
  window.plugins.preventscreenshot.activateDetectAndroid(successActivateCallback, errorActivateCallback);
}

function successActivateCallback(result) {
  console.log(result); // true activate - enabled
}

function errorActivateCallback(error) {
  console.log(error);
}

EnableDetect();

```



## Usage in typescript

```ts

// Enable
  (<any>window).plugins.preventscreenshot.enable((a) => this.successCallback(a), (b) => this.errorCallback(b));

// Disable
  (<any>window).plugins.preventscreenshot.disable((a) => this.successCallback(a), (b) => this.errorCallback(b));

  successCallback(result) {
    console.log(result); // true - enabled, false - disabled
  }

  errorCallback(error) {
    console.log(error);
  }

```
