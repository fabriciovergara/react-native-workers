import React, { Component } from 'react';
import { worker }from './lib'
import { AppRegistry, StyleSheet, Text, View, TouchableOpacity } from 'react-native-workers';

export default class rnapp extends Component {

    constructor (props, context) {
        super(props, context)
        this.state = {
            text: "",
            count: 0
        }
    }

    componentDidMount () {
        this.subscription = worker.subscribe(message => this.setState({ text: message, count: this.state.count + 1 }))
    }

    componentWillUnmount () {
        this.subscription()
    }

    render () {
        return (
            <View style={styles.container}>
                <TouchableOpacity onPress={() => worker.sendMessage("" + this.state.count)}>
                    <Text style={styles.clickMe}>
                        {"Send Message (" + this.state.count + ")"}
                    </Text>
                </TouchableOpacity>
                <Text style={styles.text}>
                    {this.state.text}
                </Text>
            </View>
        );
    }
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
        backgroundColor: '#F5FCFF',
    },
    clickMe: {
        fontSize: 20,
        textAlign: 'center',
        margin: 10,
    },
    text: {
        textAlign: 'center',
        color: '#333333',
        marginBottom: 5,
    },
});

AppRegistry.registerComponent('rnapp', () => rnapp);
