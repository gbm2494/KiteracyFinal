#include <SoftwareSerial.h>
#include <avr/pgmspace.h>

void setup() {
  
  Serial.begin(9600);
}

void loop()
{
  Serial.print("Hola");

    delay(500);
}
