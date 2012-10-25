This code is used with Arduino software to program the Arduino boards.
The main difference between the Bluetooth and USB boards is the baud rate at which they transmit data.
The Arduino Uno (USB) transmits at 4800 or 9600 bps, whereas the Arduino BT (Bluetooth) transmits at 115200 bps.
Ensure that the baud rate corresponds to the board and to the baud rate specified in the receiving software.

====================
     IMPORTANT
====================
This code was written for use with Arduino 1.0. It makes use of the SoftwareSerial and TinyGPS libraries.
Currently, SoftwareSerial is included with Arduino 1.0, but you may need to download TinyGPS from here:

http://arduiniana.org/libraries/tinygps/