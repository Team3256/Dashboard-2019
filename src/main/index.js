import { app, BrowserWindow, ipcMain } from "electron";
import store from "../renderer/store";

/**
 * Set `__static` path to static files in production
 * https://simulatedgreg.gitbooks.io/electron-vue/content/en/using-static-assets.html
 */
if (process.env.NODE_ENV !== "development") {
  global.__static = require("path")
    .join(__dirname, "/static")
    .replace(/\\/g, "\\\\");
}

let consoleWin = null;

ipcMain.on("openInteractiveConsole", function(e, data) {
  const consolePath =
    process.env.NODE_ENV === "development"
      ? "http://localhost:9080/#/console"
      : `file://${__dirname}/index.html#console`;
  consoleWin = new BrowserWindow({
    width: 1000,
    height: 600,
    minWidth: 1000,
    minHeight: 600,
    frame: false,
    webPreferences: { webSecurity: false }
  });
  consoleWin.on("close", function() {
    consoleWin = null;
  });
  consoleWin.loadURL(consolePath);
});

let mainWindow;
const winURL =
  process.env.NODE_ENV === "development"
    ? `http://localhost:9080/#/main`
    : `file://${__dirname}/index.html#main`;

import * as ntClient from "wpilib-nt-client";

var client = null;
var ntListener = null;

const ntConnect = () => {
  if (ntListener) {
    client.removeListener(ntListener);
    ntListener = null;
  }

  if (client) {
    client.stop();
    client.destroy();
    client = null;
  }

  client = new ntClient.Client();

  client.start((isConnected, err) => {
    // Displays the error and the state of connection
    console.log({ isConnected, err });
    if (isConnected) {
      store.dispatch("ntConnect");
    } else {
      store.dispatch("ntDisconnect");
      setTimeout(() => {
        ntConnect();
      }, 1000);
    }
  }, "172.16.100.200");

  ntListener = client.addListener((key, val, type, id) => {
    //console.log({ key, val, type, id });
    console.log(store.state.NetworkTables.connected);
    store.dispatch("updateValue", { key, val, type, id });
  });
};

ntConnect();

function createWindow() {
  /**
   * Initial window options
   */
  mainWindow = new BrowserWindow({
    minWidth: 1920,
    minHeight: 840,
    width: 1920,
    height: 840,
    useContentSize: true,
    darkTheme: true,
    devTools: false,
    frame: false,
    resizable: false
  });

  mainWindow.setMenu(null);

  mainWindow.loadURL(winURL);

  mainWindow.on("closed", () => {
    mainWindow = null;
  });
}

app.on("ready", createWindow);

app.on("window-all-closed", () => {
  if (process.platform !== "darwin") {
    app.quit();
  }
});

app.on("activate", () => {
  if (mainWindow === null) {
    createWindow();
  }
});

/**
 * Auto Updater
 *
 * Uncomment the following code below and install `electron-updater` to
 * support auto updating. Code Signing with a valid certificate is required.
 * https://simulatedgreg.gitbooks.io/electron-vue/content/en/using-electron-builder.html#auto-updating
 */

/*
import { autoUpdater } from 'electron-updater'

autoUpdater.on('update-downloaded', () => {
  autoUpdater.quitAndInstall()
})

app.on('ready', () => {
  if (process.env.NODE_ENV === 'production') autoUpdater.checkForUpdates()
})
 */
