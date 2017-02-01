import React, { Component } from 'react';
import { Worker }from 'rn-workers'
import { AppRegistry, StyleSheet, Text, View, TouchableOpacity } from 'react-native';

export default class rnapp extends Component {

  constructor(props, context) {
    super(props, context);
    this.state = {
      text: "",
      count: 0,
      increment: 0,
    }
  }

  componentDidMount() {
    this.worker = new Worker();
    this.worker.onmessage = message => this.setState({
      text: message,
      count: this.state.count + 1
    });

    this.interval = setInterval(() => this.setState({
      increment: this.state.increment + 1
    }), 100);
  }

  componentWillUnmount() {
    this.worker.terminate();
    clearInterval(this.interval)
  }

  render() {
    return (
      <View style={styles.container}>
        <TouchableOpacity onPress={() => this.worker.postMessage("" + this.state.count)}>
          <Text style={styles.clickMe}>
            {"Send Message (" + this.state.count + ")"}
          </Text>
        </TouchableOpacity>
        <Text style={styles.text}>
          {this.state.text}
        </Text>
        <Text style={styles.text}>
          {"Should not freeze: " + this.state.increment}
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
