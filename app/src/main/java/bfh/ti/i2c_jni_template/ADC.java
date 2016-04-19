/*
 ***************************************************************************
 * \brief   Embedded-Android (BTE5484)
 *	    	ADC Example 
 *          Accessing the ADC via sysfs by a shell process.
 *
 * \file    ADC.java
 * \version 1.0
 * \date    24.01.2014
 * \author  Martin Aebersold
 *
 * \remark  Last Modifications:
 * \remark  V1.0, AOM1, 24.01.2014   Initial release
 ***************************************************************************
 */
package bfh.ti.i2c_jni_template;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/*
 * Read a value from one of the eight ADC channels AIN0 ... AIN7
 */
public class ADC
 {  
  public String read_adc(String adc_channel)
   {
	Process p;
	String[] shellCmd = {"/system/bin/sh","-c", String.format("cat /sys/bus/iio/devices/iio\\:device0/" + adc_channel)};
	try
	 {
	  p = Runtime.getRuntime().exec(shellCmd);
	  BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
	  StringBuilder text = new StringBuilder();
	  String line; 
	  while((line = reader.readLine()) != null)
	   {
		text.append(line);
		//text.append("\n");
	   }
	  return text.toString();
	 }
	catch (IOException e)
	 {
	  return "";
	 }
   }
 }
