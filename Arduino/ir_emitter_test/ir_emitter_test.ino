#include <IRremote.h>

IRsend irsend;
int ir_led = 5;
int ir_khz = 38;

void setup() {
  pinMode(ir_led, OUTPUT);
}

void loop() {
  unsigned int irSignal[] = {9000, 4500, 560, 560, 560, 560, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 1690, 560, 560, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 560, 560, 560, 560, 560, 560, 560, 560, 1690, 560, 1690, 560, 1690, 560, 560, 560, 1690, 560, 1690, 560, 1690, 560, 1690, 560, 39416, 9000, 2210, 560};
  
  irsend.sendRaw(irSignal, sizeof(irSignal) / sizeof(irSignal[0]), ir_khz);

  delay(1000);
}
