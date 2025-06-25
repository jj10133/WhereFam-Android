// src/hyperswarm-manager.js
const Hyperswarm = require('hyperswarm')
const b4a = require('b4a')
const ipc = require('./ipc') // We need to send IPC messages from here

let swarm = null
const activeConnections = [] // Renamed from 'conns' for clarity

async function initializeHyperswarm(keyPair) {
  if (swarm) {
    console.warn('Hyperswarm already initialized.')
    return swarm
  }
  try {
    swarm = new Hyperswarm({
      keyPair: keyPair
    })
    swarm.listen()
    swarm.on('connection', handleConnection)
    console.log('Hyperswarm initialized and listening.')
    return swarm
  } catch (error) {
    console.error('Error initializing Hyperswarm:', error)
    throw error
  }
}

function handleConnection(conn) {
  activeConnections.push(conn)
  console.log('New connection established.')

  conn.once('close', () => {
    activeConnections.splice(activeConnections.indexOf(conn), 1)
    console.log(
      'Connection closed. Remaining connections:',
      activeConnections.length
    )
  })

  conn.on('data', (data) => {
    // Assuming 'data' from other peers is a location update
    try {
      const message = JSON.parse(data.toString())
      if (message.action === 'locationUpdate' && message.data) {
        ipc.send('peerLocationUpdate', message.data) // Send peer's location via IPC
      } else {
        console.log('Received unknown data from peer:', data.toString())
      }
    } catch (e) {
      console.error('Error parsing data from peer:', e)
    }
  })

  conn.on('error', (e) => console.log(`Connection error: ${e}`))
}

function joinPeer(peerPublicKey) {
  if (!swarm) {
    console.error('Hyperswarm not initialized. Cannot join peer.')
    return
  }
  try {
    swarm.joinPeer(b4a.from(peerPublicKey, 'base64'))
    console.log('Attempting to join peer:', peerPublicKey)
  } catch (error) {
    console.error('Error joining peer:', error)
  }
}

function sendUserLocationToPeers(locationData) {
  const message = {
    action: 'locationUpdate',
    data: locationData
  }
  const buffer = b4a.from(JSON.stringify(message))
  for (const conn of activeConnections) {
    conn.write(buffer)
  }
  console.log('Sent user location to peers.')
}

module.exports = {
  initializeHyperswarm,
  joinPeer,
  sendUserLocationToPeers
}
