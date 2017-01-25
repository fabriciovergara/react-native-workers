import { AppRegistry } from 'react-native';
import { WorkerService } from 'rn-workers'

const worker = new WorkerService();
worker.onmessage = message => {
  const now = Date.now();
  while (Date.now() < now + 2000);
  worker.postMessage("Hello from the other side (" + message + ")")
};