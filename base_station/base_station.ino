int V = 0, H = 1;
// Lasers
int output[] = {3, 2};

// Photoresistors to detect begining/end of revolution
int start[] = {A5, A4};
int finish[] = {A5, A4};

// Values recorded when base is turned on
float start_average[] = {0,0};
float finish_average[] = {0,0};

// Threshold for weighted average of inputs
float threshold = 200;
float alpha = 0.8f; // Weight to determine how quickly average changes

int delay_start = 500; //Time to wait before assuming a revolution has occured: TODO Add second set of photoresistors for finish instead of reusing ones for start

void setup() {
  pinMode(output[H], OUTPUT);
  pinMode(output[V], OUTPUT);
  
  pinMode(start[H], INPUT_PULLUP);
  pinMode(finish[V], INPUT_PULLUP);
  pinMode(start[H], INPUT_PULLUP);
  pinMode(finish[V], INPUT_PULLUP);
  
  digitalWrite(output[V], LOW);
  digitalWrite(output[H], LOW);
  Serial.begin(9600);
  
  Serial.println("Base station: Gathering Samples");
  const float n = 100;
  for (int i=0; i<n; i++) {
    start_average[V] += analogRead(start[V])/n;
    start_average[H] += analogRead(start[H])/n;
    finish_average[V] += analogRead(finish[V])/n;
    finish_average[H] += analogRead(finish[H])/n;
  }
  Serial.println("Base station: Done");
}

void loop() {
  digitalWrite(output[V], HIGH);
  wait_start(V); //Wait for laser to reach begining of section
  
  Serial.println("v");
  delay(delay_start);
  
  wait_finish(V); // Reach end
  Serial.println("w");
  digitalWrite(output[V], LOW);
  
  digitalWrite(output[H], HIGH);  
  wait_start(H);
  
  Serial.println("h");
  delay(delay_start);
  
  wait_finish(H);
  Serial.println("i");
  digitalWrite(output[H], LOW);
}

void wait_start(int axis) {
  float avg = start_average[axis];
  while (start_average[axis]-threshold <= (avg = avg*alpha + (1-alpha)*analogRead(start[axis])))
  { /* Do Nothing */ }
}

void wait_finish(int axis) {
  float avg = finish_average[axis];
  while (finish_average[axis]-threshold <= (avg = avg*alpha + (1-alpha)*analogRead(finish[axis])))
  { /* Do Nothing */ }
}
