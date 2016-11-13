const int pin = A5;
float average = 0; // Average taken when controller is turned on

// Threshold for weighted average of inputs
float threshold = 50;
float alpha = 0.8f; // Weight to determine how quickly average changes

long sweep_begin = 0, detected_at = -1;
float x = 0, y = 0;

float average_read = 0;

void setup() {
  pinMode(pin, INPUT_PULLUP);
  Serial.begin(9600);
  
  const float n = 100;
  for (int i=0; i<n; i++) {
    average += analogRead(pin)/n;
  }
}

void loop() {
  char c = Serial.read();
  
  if (c == 'h') {
    sweep_begin = millis();
    detected_at = -1;
    average_read = average;
    
    while (true) {
      set_detected();
      if (Serial.available()) {
         if (Serial.read() != 'i') return; //Invalid value sent
         if (detected_at < 0) return;
         
         x = ((float)detected_at-sweep_begin)/(millis()-sweep_begin);
         sendPosition();
         return;
      }
    }
  }
  else if (c == 'v') {
    sweep_begin = millis();
    detected_at = -1;
    average_read = average;
    
    while (true) {
      set_detected();
      if (Serial.available()) {
         if (Serial.read() != 'w') return; //Invalid value sent
         if (detected_at < 0) return;
         
         y = ((float)detected_at-sweep_begin)/(millis()-sweep_begin);
         sendPosition();
         return;
      }
    }
  }
}

void sendPosition() {
  Serial.print(x);
  Serial.print(",");
  Serial.println(y);
}

void set_detected() {
  if (detected_at > 0) return;
  if (average-threshold > (average_read = average_read*alpha + (1-alpha)*analogRead(pin)))
    detected_at = millis();
  
  Serial.print("Must be: ");
  Serial.print(average-threshold);
  Serial.print("Is: ");
  Serial.println(average_read);
}
