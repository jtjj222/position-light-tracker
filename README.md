## About
Idea: Make and track a VR controller in 3d space using google cardboard. Goal: 60% of the desired effect, for 10% of the cost.

These are just my notes for now. This is not even close to being ready.

Lighthouse-like tracking:
- One photoresistor + arduno for controller, pattern drawn on laptop screen to test
- Calculate + visualize 3d position of ardunio
- Add second synchronized screen + notify arduino of which screen is on, visualize position in each plane separately
- Calculate position in 3d space using calibration data
- Add second photoresistor + handle occlusion
- Add second controller

- Use laser + hard drive or speakers to create light field - diy laser show galvo

Using sound (stereo speakers) to track position?

Camera tracking:
- Use floppy disk to cover camera + ir leds
- Detect and visualize all leds in 3d space
- Make two hand trackers using four leds, separated by known distance to calculate scale
- Track head in same way

Idea with tracking: Track two controllers + head, but don't worry about poses or orientation

- Make VR demo with sphere model of each controller + head tracking, with pluggable support for both methods

- Add support for wireless controller with esp8266
- Make it run doom
- Integrate with unreal engine
