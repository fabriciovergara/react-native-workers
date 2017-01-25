import { DeviceEventEmitter } from 'react-native';

export default (eventName, sendMessage) => {

  class Worker {

    constructor(port = 8082) {
      //Read only property
      Object.defineProperty(this, "port", { value: port, writable: false });
      this.onmessage = null;
      this.subscription = DeviceEventEmitter.addListener(eventName, message =>
        this.onmessage && typeof this.onmessage  === 'function' && this.onmessage(message)
      );
    }

    terminate(){
      this.subscription();
    }

    postMessage(message){
      if (typeof message !== 'string') {
        console.warn("Only strings is allowed");
        return;
      }

      sendMessage(message, this.port);
    }
  }

  return Worker;
}