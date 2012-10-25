#include <SoftwareSerial.h>
#include <TinyGPS.h>

TinyGPS gps;

SoftwareSerial gpsUnit(5,4); 
SoftwareSerial openLog(2,3);

const int xpin = A3;                  // x-axis of the accelerometer
const int ypin = A2;                  // y-axis
const int zpin = A1;                  // z-axis 

void getGPS(TinyGPS &gps);

String data[9]; // where the data will be stored during each loop

// global versions of alt and spd to be double checked to prevent printing wild data results that occur
// commonly during the first two or three readings from the GPS unit upon each reset.
long altCheck; 
long spdCheck;

void setup() 
{
  Serial.begin(4800);	// the module runs at 4800 bps for USB and 115200 bps for Bluetooth
  gpsUnit.begin(4800);
  openLog.begin(9600);
  Serial.println("Waiting for lock...");
  delay(5000);
}

void loop() 
{ 
  delay(500);
  
  // To avoid errors, only capture the GPS-related values when there is a GPS lock.
  // Otherwise, set them to 0 and capture the acceleration only.

  if (gpsUnit.available())
  {
    int c = gpsUnit.read();  // read from gps
    if(gps.encode(c)) // if the result is legible
    {
      getAccelWithGPS();   // call get values from accelerometer
      getGPS(gps);  // call get gps
    }
  }
  else
  {
    getAccelWithoutGPS();
    data[3] = "0";
    data[4] = "0";
    data[5] = "0";
    data[6] = "0";
    data[7] = "0";
    data[8] = "0";
  }
  
  printData();  // call print command to write to SD card
}

//
//
//

void getAccelWithGPS() // normalize values from accelerometer and store them in the data[] string 
                // the math here is subject to change
{
  int x = (analogRead(xpin)); 
  int y = (analogRead(ypin));
  int z = (analogRead(zpin));
  data[0] = (String)((x + 9) * 10);
  data[1] = (String)((y + 9) * 10);
  data[2] = (String)((z + 9) * 10);
}
  
//
//
//

void getAccelWithoutGPS() // normalize values from accelerometer and store them in the data[] string 
                // the math here is subject to change
{
  int x = (analogRead(xpin)); 
  int y = (analogRead(ypin));
  int z = (analogRead(zpin));
  data[0] = (String)((x - 502) * 10);
  data[1] = (String)((y - 502) * 10);
  data[2] = (String)((z - 502) * 10);
}
  
//
//
//

void getGPS(TinyGPS &gps)
{
  long latitude, longitude; // declare variables for lat and lon
  
  unsigned long fix_age, date, time;  // this gps module uses unsigned long for a few data types
  
  gps.get_position(&latitude, &longitude, &fix_age); // get position and store as lat, lon
  gps.get_datetime(&date, &time);                    // get date and time
  long alt = (gps.f_altitude())*3.2808399;           //convert meters to ft (subject to change)
  long spd = (gps.f_speed_kmph())*0.621371192;       //convert kph to mph (subject to change)
  
  altCheck = alt;                                    // store globally
  spdCheck = spd;                                    // store globally
  
  // store all data from gps to be printed
  data[3] = (String)latitude;
  data[4] = (String)longitude;
  data[5] = (String)alt;
  data[6] = (String)spd;
  data[7] = (String)date;
  data[8] = (String)time;
}

//
//
//

void printData()  // Serial.print is included for debugging purposes
{ 
  if(altCheck < 100000   // prevent unreasonablly high results from being printed 
  && spdCheck < 10000){  //  ... a commonallity during the first few readings from gps upon each reset
  
  String dataLine = "";
  
  // print each value from data[] array with a comma seperating each one
  for(int i = 0; i < 9; i++){   
    dataLine = dataLine + data[i];
    
    // don't print a comma at the end
    if(i<8){ 
    dataLine = dataLine + ",";
    }
  }
  
  Serial.println(dataLine);  // print \n after each loop of data
  openLog.println(dataLine); // print \n after each loop of data
  }
}
  
  
