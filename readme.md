# Mouse Controller App

This project consists of an Android app and a Python server that together function as a remote mouse controller. Using a WebSocket connection, the Android app sends mouse movement and click commands to the Python server, which then translates these actions to control the mouse on the server's machine.

## Features

- **Mouse Movement**: Control the mouse cursor by dragging on the app's touchpad area.
- **Left and Right Clicks**: Tap buttons in the app to simulate left and right mouse clicks.

## Setup Instructions

### 1. Server Setup (Python)

1. Install required Python packages:
   ```bash
   pip install pyautogui websockets

2. Update <your-ip> in server.py with the IP address of your server machine.

3. Start the server by running:
   ```bash
    python server.py


### 2. Android App Setup
Open the project in Android Studio.
Update <your-ip> in MainActivity with the IP address of the server machine.
Run the app on an Android device.