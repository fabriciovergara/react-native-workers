# react-native-workers (RN 0.43^)
Do heavy data process outside of your UI JS thread.

Before using this kind of solution you should check if [InteractionManager.runAfterInteractions](https://facebook.github.io/react-native/docs/interactionmanager.html) is not enough for your needs, because creating a aditional worker can considerably increase app memory usage. 

I mostly use this library for a personal project, that wrap a native database with a graphql api. So the updates may follow my needs, but any PR is welcome. 

### Automatic Instalation
```
npm install --save rn-workers
react-native link rn-workers
```
Or [Install manually](https://github.com/fabriciovergal/react-native-workers/blob/master/MANUAL_INSTALATION.md)

Prepare your project following this [SETUP GUIDE](https://github.com/fabriciovergal/react-native-workers/blob/master/SETUP.md)

### App side

```javascript 
    //index.ios.js 
    import { Worker } from 'rn-workers'

    export default class rnapp extends React.Component {

        componentDidMount () {
            //Create using default worker port (8082)
            this.worker = new Worker();
            
            //Create worker pointing to custom one
            this.worker2 = new Worker(8083);
            
            
            //Add listener to receve messages
            this.worker.onmessage = message => this.setState({
                 text: message,
                 count: this.state.count + 1
            });

            //Send message to worker (Only strings is allowed for now)
            this.worker.postMessage("Hey Worker!")
        }

        componentWillUnmount () {
            //Terminate worker
            this.worker.terminate();
        }
        
        (...)
     }
 ```
 
### Worker side

```javascript 
    //index.worker.js
    import { WorkerService } from 'rn-workers'
    
    const worker = new WorkerService();
    worker.onmessage = message => {
        //Reply the message back to app
        worker.postMessage("Hello from the other side (" + message + ")")
    };

 ```
 
## Aditional Information

* [Observation](https://github.com/fabriciovergal/react-native-workers/blob/master/OBSERVATIONS.md)
* [Other features](https://github.com/fabriciovergal/react-native-workers/blob/master/EXTRA_FEATURES.md)
* [Npm useful scripts](https://github.com/fabriciovergal/react-native-workers/blob/master/NPM_SCRIPTS.md) 
 
# License
~~Cancer~~ GPL ..... just kindind, its Apache 2.0
