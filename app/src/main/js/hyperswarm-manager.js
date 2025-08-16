// src/hyperswarm-manager.js
const Hyperswarm = require('hyperswarm')
const b4a = require('b4a')
const ipc = require('./ipc')
const Protomux = require('protomux')

let swarm = null
let activeConnections = []
const protocolHandlers = new Map()
let tempLeaveCache = new Set()

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

function handleConnection(conn, info) {
  const peerPublicKey = b4a.toString(info.publicKey, 'base64')
  console.log('New connection established.')

  if (tempLeaveCache.has(peerPublicKey)) {
    conn.destroy()
    return
  }

  const mux = new Protomux(conn)
  activeConnections.push({ mux, publicKey: peerPublicKey })

  for (const handler of protocolHandlers.values()) {
    handler(mux, peerPublicKey)
  }

  conn.once('close', () => {
    activeConnections = activeConnections.filter((m) => m !== mux)
    ipc.send('peerDisconnected', { peerKey: peerPublicKey })
    closeConnection(peerPublicKey)
    tempLeaveCache.add(peerPublicKey)
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

function registerProtocol(protocolName, handler) {
  protocolHandlers.set(protocolName, handler)
}

async function closeConnection(peerPublicKey) {
  const connectionIndex = activeConnections.findIndex(
    (c) => c.publicKey === peerPublicKey
  )
  if (connectionIndex !== -1) {
    const connection = connections[connectionIndex]
    connection.mux.stream.destroy()
  } else {
    console.log(`No active connection found for peer: ${peerPublicKey}`)
  }
}

function leavePeer(peerPublicKey) {
  if (!swarm) {
    console.error('Hyperswarm not initialized. Cannot join peer.')
    return
  }
  try {
    tempLeaveCache.add(peerPublicKey)
    swarm.leavePeer(b4a.from(peerPublicKey, 'base64'))
  } catch (error) {
    console.error('Error leaving peer:', error)
  }
}

function getSwarm() {
  if (!swarm) {
    throw new Error('Hyperswarm is not initialized yet.')
  }
  return swarm
}


module.exports = {
  initializeHyperswarm,
  joinPeer,
  leavePeer,
  getSwarm,
  registerProtocol,
  activeConnections,
  closeConnection
}
