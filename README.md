# ESP-Pedal
### Audio Effect Configured and Controlled by ESP32 and Mobile Application

The ESP Pedal is an audio effect pedal with customizable settings available through a mobile app. The project consists of two main parts: a mobile application and hardware based on the ESP32 platform.

The mobile app features a Bluetooth connectivity screen where users register the ESP32's MAC (Media Access Control) address and establish a connection via Bluetooth. Once connected, the app displays a screen with three Preset buttons. Each Preset button directs the user to a new screen with eight buttons, where they can toggle each button on or off to save its state. These buttons activate the digital outputs, or GPIOs (General Pin Input/Output), on the ESP32.

The hardware includes an analog audio circuit with key components switched through integrated circuits, activated by the digital outputs of the ESP32. A physical switch is also connected to a digital input on the ESP32, configured as a Latching Switch, allowing the user to navigate between Presets and activate the stored physical outputs from the app.

The ESP Pedal is an Overdrive, a mild distortion tailored for electric guitars, with adjustable equalization bandwidth and different waveform clipping modes for diverse tonal options.

## Hardware Concept
<div align="center">
  <img src="https://github.com/andresima0/ESP-Pedal/assets/111400782/b8e48f17-a602-4b2e-b559-7b2bd06203ca"
    alt="Concept" width="450">
</div>

## Software Demonstration
<div align="center">
  <video src="https://github.com/andresima0/ESP-Pedal/assets/111400782/5dc937ab-4dac-4efc-8266-a585ba58c51c"
    alt="ESP-Pedal">
</div>

## And everything working together...Turn up the volume!
<div align="center">
  <video src="https://github.com/andresima0/ESP-Pedal/assets/111400782/b621e8cf-fad7-4fbe-9e6e-c1bed93e702f"
    alt="ESP-Pedal">
</div>
