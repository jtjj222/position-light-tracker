const int NUM_SAMPLES = 60;
const int framesPerSecond = 60; //Frame is one data point in history, corresponding to frame in desktop application
const int framesPerSample = 120; //Sample is one set of flashes

const float msPerFrame = (1.0/framesPerSecond)*1000.0;

const int pin = A5;
int history[NUM_SAMPLES];
long int avg = 0, dev = 0;
long previousRecording = 0;


void setup() {
  pinMode(pin, INPUT_PULLUP);
  Serial.begin(9600);
}

// Add a sample to the history
void addSample(int sample) {
  avg = 0;
  for (int i=1; i<NUM_SAMPLES; i++) {
    avg += history[i];
    history[i-1] = history[i];
  }
  history[NUM_SAMPLES-1] = sample;
  avg += sample;  
  avg /= NUM_SAMPLES;
  
  dev = 0;
  for (int i=0; i<NUM_SAMPLES; i++) {
    dev += abs(history[i]-avg);
  }
  dev /= NUM_SAMPLES;
}

// Look at the history. If we detect a a pattern
// corresponding to a large, long flash, followed by a shorter flash,
// then we measure the time between the end of the large flash and the start of the small
// flash, output it to serial, and then erase the history up to the end of the small flash
void detectPattern() {
  outputDebugInfo();
}

int counter = 0;
void outputDebugInfo() {
  counter++;
  if (counter > 60) counter = 0;
  if (counter != 0) return;
  
  Serial.print("Avg: ");
  Serial.print(avg);
  Serial.print(" Dev: ");
  Serial.print(dev);
  
  Serial.print(" Samples: [");
  for (int i=0; i<NUM_SAMPLES; i++) {
//    if (history[i]-avg<=dev && dev > 5) Serial.print("1");
//    else if (history[i]-avg>=dev && dev > 5) Serial.print("-1");
//    else Serial.print("0");
    Serial.print(history[i]);
    
    Serial.print(", ");
  }
  Serial.println("]");
}


void loop() {
  if (millis()-previousRecording < msPerFrame) return;
  previousRecording = millis();
  //addSample(analogRead(pin));
  //detectPattern();
  Serial.println(analogRead(pin));
}
