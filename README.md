# react-native-workers (RN 0.41^)
Do heavy data process outside of your UI JS thread.

Before using this kind of solution you should check if [InteractionManager.runAfterInteractions](https://facebook.github.io/react-native/docs/interactionmanager.html) is not enough for your needs, because creating a aditional worker can considerably increase app memory usage. 

### Automatic Instalation
```
npm install --save rn-workers
react-native link rn-workers
```
Or [Install manually](https://github.com/fabriciovergal/react-native-workers/blob/master/MANUAL_INSTALATION.md)

## Usage
  
  1. Create a index.worker.js in the react-native root project (same level of index.ios.js and index.android.js)
  2. import a worker.jsbundle in iOS Project and index.worker.jsbundle on Android Project

### App side

```javascript 
   
    import { Worker } from 'rn-workers'

    export default class rnapp extends React.Component {

        constructor (props, context) {
            super(props, context)
        }

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
   
    import { WorkerService } from 'rn-workers'
    
    const worker = new WorkerService();
    worker.onmessage = message => {
        //Reply the message back to app
        worker.postMessage("Hello from the other side (" + message + ")")
    };

 ```
 
## Aditional Information

* [Other features](https://github.com/fabriciovergal/react-native-workers/blob/master/OBSERVATIONS.md)
* [Other features](https://github.com/fabriciovergal/react-native-workers/blob/master/EXTRA_FEATURES.md)
* [Npm useful scripts](https://github.com/fabriciovergal/react-native-workers/blob/master/NPM_SCRIPTS.md)
* [Changelog](https://github.com/fabriciovergal/react-native-workers/edit/master/CHANGE_LOG.md)

 
 
 
 
# License
~~Cancer~~ GPL ..... just kindind, its Apache 2.0
