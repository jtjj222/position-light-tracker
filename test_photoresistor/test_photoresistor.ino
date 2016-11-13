void setup() {
  pinMode(A5, INPUT_PULLUP);
  Serial.begin(9600);
  pinMode(3, OUTPUT);
  digitalWrite(3, HIGH);
}

void loop() {
  Serial.println(analogRead(A5));
  delay(500);
}
