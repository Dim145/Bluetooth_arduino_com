#include <SoftwareSerial.h>

int const Rx= 11;
int const Tx= 12;
SoftwareSerial BT (Tx,Rx);

const int buttonPin = 4;
const int relayPin  = 2;

int  octet     = -944215;

void readBT();

void setup() {
  // put your setup code here, to run once:
  BT.begin(9600);
  Serial.begin(9600);
  
  pinMode(relayPin , OUTPUT);
  pinMode(buttonPin, INPUT );
}

void loop() {
  // put your main code here, to run repeatedly:

  // Button if have error with Bluetooth
  if( digitalRead(buttonPin) == HIGH )
  {
    digitalWrite(relayPin, !digitalRead(relayPin) );

    delay(500);
  }

  readBT();
  
  delay(100);
}

void readBT()
{
  if( BT.available() )
  {
    octet = BT.read();
    Serial.print("Reception: ");
    Serial.println(octet);

    if( octet == 1 )
      digitalWrite(relayPin, HIGH ); // off because the default position is on
    else if( octet == 2 )
      digitalWrite(relayPin, LOW ); // on
    else if( octet == 3 )
    {
      for(int cpt = 0; cpt < 20; cpt++)
      {
          digitalWrite(relayPin, HIGH );
          delay(250);
          digitalWrite(relayPin, LOW );
          delay(250);
      }
    }
        
  }
  
}
