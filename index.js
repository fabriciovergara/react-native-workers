import { NativeModules } from 'react-native';
import creator from './src/createWorker';

const { RNWorkers } = NativeModules;

export const WorkerService = creator("RNWorkers", message => RNWorkers.sendMessageToApp(message));
export const Worker = creator("RNWorkersApp", (message, port) => RNWorkers.sendMessageToWorker(port, message));
export default Worker
