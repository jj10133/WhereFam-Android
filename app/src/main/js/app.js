const { IPC } = BareKit
IPC.setEncoding('utf8');

setTimeout(() => {
  IPC.write("This message will be displayed after 2 seconds");
}, 2000);

setTimeout(() => {
  IPC.write("This message will be displayed after 5 seconds");
}, 5000);

setTimeout(() => {
  IPC.write("This message will be displayed after 13 seconds");
}, 13000);