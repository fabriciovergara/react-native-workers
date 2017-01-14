import { NativeModules } from 'react-native';
import creator from './src/workerBridgeCreator'

const { RNWorkers } = NativeModules

export { default  as Component } from './src/Component'
export const workerService = creator("RNWorkers", message => RNWorkers.sendMessageToApp(message))
export const worker = creator("RNWorkersApp", message => RNWorkers.sendMessageToWorker(message))
