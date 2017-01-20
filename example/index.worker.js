import { AppRegistry } from 'react-native';
import { workerService } from 'rn-workers'

workerService.subscribe(message => {
  const now = Date.now()
  while (Date.now() < now + 2000);
  workerService.sendMessage("Hello from the other side (" + message + ")")
})