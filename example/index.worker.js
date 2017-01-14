import { AppRegistry } from 'react-native';
import { Component, workerService } from 'react-native-workers'

export default class rnapp extends Component {

    componentDidMount () {
        this.subscription = workerService.subscribe(message => {
            this.sleep(2000)
            workerService.sendMessage("Hello from the other side (" + message + ")")
        })
    }

    componentWillUnmount () {
        this.subscription()
    }

    sleep (time) {
        const now = Date.now()
        while (Date.now() < now + time);
    }
}

AppRegistry.registerComponent('rnapp', () => rnapp);

