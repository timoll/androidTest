/*
 ***************************************************************************
 * \brief   Embedded-Android (BTE5484)
 *          Accessing the GPIOs via sysfs with file i/o commands
 *
 * \file    SysfsFileGpio.java
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
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class SysfsFileGPIO
 {
	
	public String gpio;
	public char   value;

	/* Define some useful constants */
    static final String GPIO_OUT = "out";  
    static final String GPIO_IN = "in";
	static final String GPIO_HIGH = "1";  
	static final String GPIO_LOW = "0";  

	static final String SYSFS_GPIO = "/sys/class/gpio/gpio";
	static final String SYSFS_GPIO_DIRECTION = "/direction";
	static final String SYSFS_GPIO_VALUE = "/value";
	static final String SYSFS_GPIO_EXPORT = "/sys/class/gpio/export";
	static final String SYSFS_GPIO_UNEXPORT = "/sys/class/gpio/unexport";
	
	/*
	 *  Export a gpio
	 */
	public boolean export(String gpio)
	 {
	  try {
           /*
            *  Open file handle to export a GPIO
            */
		  	FileWriter unexportFile = new FileWriter(SYSFS_GPIO_UNEXPORT);  
		  	FileWriter exportFile =  new FileWriter(SYSFS_GPIO_EXPORT);  
       
		  	/*
		  	 *  Clear the port, if needed  
		  	 */
		  	File exportFileCheck = new File(SYSFS_GPIO + gpio);
      
		  	if (exportFileCheck.exists())
		  	 {    
		  	  unexportFile.write(gpio);  
		  	  unexportFile.flush();
		  	  unexportFile.close();
		  	 }  
       
		  	/*
		  	 *  Set the port for use  
		  	 */
		  	exportFile.write(gpio);    
		  	exportFile.flush();
		  	exportFile.close();
	        return true;
	      }
        catch (Exception exception)
         {  
	      exception.printStackTrace();  
	      return false;
	     }  	  
	 }
	
	/*
	 * Unexport a gpio
	 */
	public boolean unexport(String gpio)
	 {
	  try {
           /*
            *  Open file handles to unexport a GPIO
            */
		  	FileWriter unexportFile = new FileWriter(SYSFS_GPIO_UNEXPORT);       
		  	unexportFile.write(gpio);  
		  	unexportFile.flush();
		  	unexportFile.close();
	        return true;
	      }
        catch (Exception exception)
         {  
	      exception.printStackTrace();  
	      return false;
	     }  	  
	 }

	/*
	 * Set gpio direction to output
	 */
	public boolean set_direction_out(String gpio)
	 {
	  try
	   {
        /*
         *  Open file handle to set the GPIO direction as output
         */
	     FileWriter directionFile = new FileWriter(SYSFS_GPIO + gpio + SYSFS_GPIO_DIRECTION);  
	         
	     /*
	      *  Set port for output  
	      */
	      directionFile.write(GPIO_OUT);  
	      directionFile.flush();  
	      directionFile.close();
		  return true;
	   }
      catch (Exception exception)
       {  
	    exception.printStackTrace();  
	    return false;
	   }  	  		
	 }

	/*
	 * Set gpio direction to input
	 */
	public boolean set_direction_in(String gpio)
	 {
	  try
	   {
        /*
         *  Open file handle to set the GPIO direction as input
         */
	     FileWriter directionFile = new FileWriter(SYSFS_GPIO + gpio + SYSFS_GPIO_DIRECTION);  
	         
	     /*
	      *  Set port for output  
	      */
	      directionFile.write(GPIO_IN);  
	      directionFile.flush();  
	      directionFile.close();
		  return true;
	   }
      catch (Exception exception)
       {  
	    exception.printStackTrace();  
	    return false;
	   }  	  		
	 }
	
	/*
	 * Write a gpio value 
	 */
	public boolean write_value(String gpio, char value)
	 {
	  try
	   {
        /*
         *  Set up File I/O to write a value to the GPIO
         */
	     FileWriter gpioNumber = new FileWriter(SYSFS_GPIO + gpio + SYSFS_GPIO_VALUE);  

	     gpioNumber.write(value);  
	     gpioNumber.flush();
	     gpioNumber.close();     
		 return true;
	   }
     catch (Exception exception)
      {  
	    exception.printStackTrace();  
	    return false;
	   }  	  		
	 }
		
	/*
	 * Read a gpio value  
	 */
	public String read_value(String gpio)
	 {
      /*
       *  Set up File I/O read a value from the GPIO
       */
	   String value;
	   try
		{
		 BufferedReader br = new BufferedReader(new FileReader(SYSFS_GPIO + gpio + SYSFS_GPIO_VALUE));
		 value = br.readLine();
		 br.close();
		}	   
	   catch (IOException ex)
	    {
		 return "-1";
		}
	   return value;
	 }	
 } 
 