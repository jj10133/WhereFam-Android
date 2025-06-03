const { IPC } = BareKit
const Hyperswarm = require('hyperswarm')
const Hyperbee = require('hyperbee')
const Hypercore = require('hypercore')
const Corestore = require('corestore')
const BlobServer = require('hypercore-blob-server')
const sodium = require('sodium-native')
const b4a = require('b4a')
const EventEmitter = require('bare-events')

let swarm = null
let db = null
const conns = []
let mapLink = null
const emitter = new EventEmitter()

let ipcBuffer = ''
IPC.setEncoding('utf8');


IPC.on('data', async (chunck) => {
  ipcBuffer += chunck

  let newLineIndex
  while ((newLineIndex = ipcBuffer.indexOf('\n')) !== -1) {
    const line = ipcBuffer.substring(0, newLineIndex).trim() // Get one line
    ipcBuffer = ipcBuffer.substring(newLineIndex + 1) // Remove the line from buffer

    if (line.length === 0) {
      continue // Skip empty lines
    }

    try {
      const message = JSON.parse(line)
      emitter.emit(message.action, message.data)
    } catch (error) {
      console.error('Error handling IPC message: ', error)
    }
  }
})

emitter.on('start', async (data) => await start(data['path']))
emitter.on('fetchMaps', async (data) => await getMaps(data['path']))
emitter.on('requestPublicKey', async () => await getPublicKey())
emitter.on('joinPeer', async (data) => await addPeer(data))
emitter.on('locationUpdate', async (data) => await sendUserLocation(data))
emitter.on('requestMapLink', async () => await getMapLink())

async function getPublicKey() {
  const publicKeyBase64 = await db.get('publicKey')
  const message = {
    action: 'requestPublicKey',
    data: {
      publicKey: publicKeyBase64.value
    }
  }

  IPC.write(JSON.stringify(message))
}

async function start(documentsPath) {
  try {
    const core = new Hypercore(documentsPath, { valueEncoding: 'json' })
    db = new Hyperbee(core, { keyEncoding: 'utf-8', valueEncoding: 'json' })

    const { publicKey, secretKey } = await getOrCreateKeys()

    if (!swarm) {
      swarm = new Hyperswarm({
        keyPair: { publicKey, secretKey }
      })
      swarm.listen()
      swarm.on('connection', handleConnection)
    }
  } catch (error) {
    console.error('Error initializing Hyperswarm:', error)
  }
}

async function handleConnection(conn) {
  conns.push(conn)
  conn.once('close', () => conns.splice(conns.indexOf(conn), 1))

  conn.on('data', (data) => {
    IPC.write(data)
  })

  conn.on('error', (e) => console.log(`Connection error: ${e}`))
}

async function getOrCreateKeys() {
  try {
    const publicKeyBase64 = await db.get('publicKey')
    const secretKeyBase64 = await db.get('secretKey')

    if (publicKeyBase64 && secretKeyBase64) {
      return {
        publicKey: b4a.from(publicKeyBase64.value, 'base64'),
        secretKey: b4a.from(secretKeyBase64.value, 'base64')
      }
    }

    const publicKey = b4a.alloc(32) // 32-byte public key
    const secretKey = b4a.alloc(64) // 64-byte secret key
    sodium.crypto_sign_keypair(publicKey, secretKey)

    await db.put('publicKey', b4a.toString(publicKey, 'base64'))
    await db.put('secretKey', b4a.toString(secretKey, 'base64'))

    return { publicKey, secretKey }  } catch (error) {
    console.error('Error retrieving or generating keys:', error)
  }
}

async function addPeer(peerPublicKey) {
  swarm.joinPeer(b4a.from(peerPublicKey, 'base64'));
}

async function sendUserLocation(locationData) {
  for (const conn of conns) {
    conn.write(locationData);
  }
}

async function getMaps(documentsPath) {
  const key = b4a.from(
    '1bfbdb63cf530380fc9ad1a731766d597c3915e32187aeecea36d802bda2c51d',
    'hex'
  )
  const store = new Corestore(documentsPath + '/maps')

  const mapSwarm = new Hyperswarm()
  mapSwarm.on('connection', (conn) => {
    store.replicate(conn)
  })
  const server = new BlobServer(store)
  await server.listen()

  const filenameOpts = {
    filename: '/20250512.pmtiles'
  }
  const link = server.getLink(key, filenameOpts)
  mapLink = link
  console.log('link', link)

  const monitor = server.monitor(key, filenameOpts)
  monitor.on('update', () => {
    console.log('monitor', monitor.stats.downloadStats)
  })

  const topic = Hypercore.discoveryKey(key)
  mapSwarm.join(topic)
}

async function getMapLink() {
  if (mapLink) {
    const message = {
      action: 'requestLink',
      data: {
        url: mapLink
      }
    }

    IPC.write(JSON.stringify(message))
  } else {
    console.log('Map link is not ready yet.')
  }
}