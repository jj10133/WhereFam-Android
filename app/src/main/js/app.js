const { IPC } = BareKit
const Hyperswarm = require('hyperswarm')
const Hyperbee = require('hyperbee')
const Hypercore = require('hypercore')
const Corestore = require('corestore')
const BlobServer = require('hypercore-blob-server')
const sodium = require('sodium-native')
const b4a = require('b4a')

let swarm = null
let db = null
const conns = []
let mapLink = null

IPC.setEncoding('utf8');


IPC.on('data', async (data) => {
  try {
    const message = JSON.parse(data)

    switch (message.action) {
      case 'start':
        await start(message.data)
        break
      case 'fetchMaps':
        await getMaps(message.data)
        break
      case 'requestPublicKey':
        await getPublicKey()
        break
      case 'joinPeer':
        await addPeer(message.data)
        break
      case 'locationUpdate':
        await sendUserLocation(data)
        break
      case 'requestMapLink':
        await getMapLink()
    }
  } catch (error) {
    console.error('Erorr handling IPC message: ', error)
  }
})

async function getPublicKey() {
  const publicKeyBase64 = await db.get('publicKey')
  const message = {
    action: 'requestPublicKey',
    data: {
      publicKey: publicKeyBase64.value
    }
  }

  console.log('Sent public key response')

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

    console.log'Generated new key pair.''
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

  console.log(documentsPath)

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
  mapSwarm.join(topic, { client: true, server: false })
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