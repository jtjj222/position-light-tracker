int output = 2;

void setup() {
  pinMode(output, OUTPUT);
}

void loop() {
  digitalWrite(output, HIGH);
  delay(16);
  digitalWrite(output, LOW);
  delay(16);
}
