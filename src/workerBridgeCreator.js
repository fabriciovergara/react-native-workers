import { DeviceEventEmitter } from 'react-native';
import { isValidMessage }from './Util'

export  default  (eventName, sendMessage) => {
     class Worker {
        subscribe (listener) {
            if (typeof listener !== 'function') {
                throw new Error('Expected listener to be a function.')
            }

            let isSubscribed = true
            const subscription = DeviceEventEmitter.addListener(eventName, message => listener(message));
            return () => {
                if (!isSubscribed) {
                    return
                }

                subscription.remove()
            }
        }

        sendMessage (message) {
            if (!isValidMessage(message)) {
                console.warn("Only strings is allowed")
                return;
            }

            sendMessage(message)
        }
    }

    const worker = new Worker()
    return worker
}