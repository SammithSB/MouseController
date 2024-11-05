# server.py
import asyncio
import websockets
import pyautogui
import json

async def handle_client(websocket, path):
    async for message in websocket:
        data = json.loads(message)
        
        if data["type"] == "move":
            x, y = data["x"], data["y"]
            pyautogui.move(x, y)
        elif data["type"] == "click":
            button = data["button"]
            if button == "left":
                pyautogui.click()
            elif button == "right":
                pyautogui.click(button="right")

async def main():
    async with websockets.serve(handle_client, "<your-ip>", 6789):
        print("Server started on ws://<your-ip>:6789")
        await asyncio.Future()  # run forever

asyncio.run(main())
