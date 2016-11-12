int output_v = 2;
int output_h = 3;

void setup() {
  pinMode(output_h, OUTPUT);
  pinMode(output_v, OUTPUT);
}

void loop() {
  digitalWrite(output_v, HIGH);
  digitalWrite(output_h, LOW);
  delay(16);
  digitalWrite(output_h, HIGH);
  digitalWrite(output_v, LOW);
  delay(16);
}
