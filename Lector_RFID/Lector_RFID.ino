#include <SoftwareSerial.h>
#include <avr/pgmspace.h>

SoftwareSerial rfidReader(2,3); // Digital pins 2 and 3 connect to pins 1 and 2 of the RMD6300
String tagString;
char tagNumber[14];
boolean receivedTag;

void setup() {
  
  Serial.begin(9600);
  rfidReader.begin(9600); // the RDM6300 runs at 9600bps
  Serial.println("\n\n\nRFID Reader...ready!");
 
}

void loop()
{
  receivedTag=false;
  while (rfidReader.available()){
    int BytesRead = rfidReader.readBytesUntil(3, tagNumber, 13);//EOT (3) is the last character in tag 
    receivedTag=true;
  }  
 
  if (receivedTag){
    tagString=tagNumber;
    Serial.println();
    Serial.print("Numero de tag: ");
    Serial.println(tagString);
    memset(tagNumber,0,sizeof(tagNumber)); //erase tagNumber
  }

    delay(500);
}
