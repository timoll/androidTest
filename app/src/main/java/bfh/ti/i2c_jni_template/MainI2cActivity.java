/*
 ***************************************************************************
 * \brief   Embedded Android I2C Exercise 5.2
 *	        This sample program shows how to use the I2C library.
 *			The program reads the temperature from the MCP9802 sensor
 *			and show the value on the display  
 *
 *	        Only a minimal error handling is implemented.
 * \file    MainI2cActivity.java
 * \version 1.0
 * \date    06.03.2014
 * \author  Martin Aebersold
 *
 * \remark  Last Modifications:
 * \remark  V1.0, AOM1, 06.03.2014
 ***************************************************************************
 */

package bfh.ti.i2c_jni_template;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

//import java.util.logging.Handler;

public class MainI2cActivity extends Activity {

	/* MCP9800 Register pointers */
	private static final char MCP9800_TEMP = 0x00;      /* Ambient Temperature Register */
	private static final char MCP9800_CONFIG = 0x01;    /* Sensor Configuration Register */

	private static final char TCS3414_COLOR = 0x00;
	private static final char TCS3414_CONFIG = 0x00;

	/* Sensor Configuration Register Bits */
	private static final char MCP9800_12_BIT = 0x60;

	private static final char TCS3414_POWER = 0x03;

	/* i2c Address of MCP9802 device */
	private static final char MCP9800_I2C_ADDR = 0x48;

	private static final char TCS3414_I2C_ADDR = 0x39;

	/* i2c device file name */
	private static final String MCP9800_FILE_NAME = "/dev/i2c-3";

	private static final String TCS3414_FILE_NAME = "/dev/i2c-3";

	I2C i2c;
	int[] i2cCommBuffer = new int[16];
	int fileHandle;

	double TempC;
	int Temperature;

	char COLORBUF1;
	char COLORBUF2;

	int red;
	int green;
	int blue;
	int index=0;

	/*----------LED und TASTER--------------*/
	final String LED_L1 = "61";
	final String LED_L2 = "44";
	final String LED_L3 = "68";
	final String LED_L4 = "67";
    final String LEDS[] = {LED_L1, LED_L2, LED_L3, LED_L4};

	final String BUTTON_T1 = "49";
	final String BUTTON_T2 = "112";
	final String BUTTON_T3 = "51";
	final String BUTTON_T4 = "7";

	/*
     * Define some useful constants
     */
	final char ON = '0';
	final char OFF = '1';

	public final String PRESSED = "0";
	final SysfsFileGPIO gpio = new SysfsFileGPIO();

    // Lauflichtzüg
    private Handler LEDRunHandler = new Handler();
    boolean runOnLED = false;

	/*------------------------------------*/
	/*--------ADC    --------------------*/
	  /*
   * Define all ADC channels
   * The Potentiometer is connected to the ADC Channel-4
   */
	static final String ADC_IN0 = "in_voltage0_raw";
	static final String ADC_IN1 = "in_voltage1_raw";
	static final String ADC_IN2 = "in_voltage2_raw";
	static final String ADC_IN3 = "in_voltage3_raw";
	static final String ADC_IN4 = "in_voltage4_raw";
	static final String ADC_IN5 = "in_voltage5_raw"; // Channel-4 is for the potentiometer
	static final String ADC_IN6 = "in_voltage6_raw";
	static final String ADC_IN7 = "in_voltage7_raw";

	ADC adcReader;
	/*-----------------------------------*/

	boolean blinkOnOff = true;
	boolean toggleFlag = false;
	int fillState=0;
	/* Define the widgets vars */
	CheckBox optSingleShot;

	long counter = 0;

	/* Timer task */
	Timer timer;
	MyTimerTask myTimerTask;


	/* Define widgets */
	TextView textViewHeight, tvPoti;
	Button refreshButton;

	RadioButton mode1RBTN, mode2RBTN;

	boolean direction = true;
	int speed = 0;


	public static MainI2cActivity Instance;

	/* Temperature Degrees Celsius text symbol */
	private static final String DEGREE_SYMBOL = "\u2103";

    TextSpeech TS;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_i2c);
		//ShellExecGPIO gpio = new ShellExecGPIO();
		//gpio.export("65");
		//gpio.gpio_set_direction_out("65");
		//gpio.write_value("65", '1');
		Instance = this;
        TS = new TextSpeech(this);
		adcReader = new ADC();



		tvPoti = (TextView) findViewById(R.id.tvPoti);

		mode1RBTN = (RadioButton) findViewById(R.id.modeRBTN1);
		mode2RBTN = (RadioButton) findViewById(R.id.modeRBTN2);




        /*
       * Start button has been pressed
       */


    /*
     *  Stop button has been pressed
     */
		CompoundButton.OnCheckedChangeListener OCL = new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (buttonView.getText().equals("L1")) {
					if (isChecked)
						gpio.write_value(LED_L1, ON);
					else
						gpio.write_value(LED_L1, OFF);
				}

				if (buttonView.getText().equals("L2")) {
					if (isChecked)
						gpio.write_value(LED_L2, ON);
					else
						gpio.write_value(LED_L2, OFF);
				}

				if (buttonView.getText().equals("L3")) {
					if (isChecked)
						gpio.write_value(LED_L3, ON);
					else
						gpio.write_value(LED_L3, OFF);
				}

				if (buttonView.getText().equals("L4")) {
					if (isChecked)
						gpio.write_value(LED_L4, ON);
					else
						gpio.write_value(LED_L4, OFF);
				}
			}
		};

		timer = new Timer();
		myTimerTask = new MyTimerTask();

        /*
         * Delay 0ms, repeat in 200ms
         */
		timer.schedule(myTimerTask, 0, 100);


		textViewHeight = (TextView) findViewById(R.id.textViewheight);

		refreshButton = (Button) findViewById(R.id.buttonRefresh);
		refreshButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DoI2C();
                TS.SpeakOut("Hello World!");
			}
		});

		gpio.write_value(LED_L1, OFF);
		gpio.write_value(LED_L2, OFF);
		gpio.write_value(LED_L3, OFF);
		gpio.write_value(LED_L4, OFF);

		DoI2C();
	}

	public void DoI2C() {


	 /* Open the i2c device */
		i2c = new I2C();
		fileHandle = i2c.open(MCP9800_FILE_NAME);

	 /* Set the I2C slave address for all subsequent I2C device transfers */
		i2c.SetSlaveAddress(fileHandle, MCP9800_I2C_ADDR);

	 /* Setup i2c buffer for the configuration register */
		i2cCommBuffer[0] = MCP9800_CONFIG;
		i2cCommBuffer[1] = MCP9800_12_BIT;
		i2c.write(fileHandle, i2cCommBuffer, 2);

	 /* Setup mcp9800 register to read the temperature */
		i2cCommBuffer[0] = MCP9800_TEMP;
		i2c.write(fileHandle, i2cCommBuffer, 1);

	 /* Read the current temperature from the mcp9800 device */
		i2c.read(fileHandle, i2cCommBuffer, 2);

	 /* Assemble the temperature values */
		Temperature = ((i2cCommBuffer[0] << 8) | i2cCommBuffer[1]);
		Temperature = Temperature >> 4;

	 /* Convert current temperature to float */
		TempC = 1.0 * Temperature * 0.0625;

     /* Display actual temperature */
		//textViewTemperature.setText("Temperature: " + String.format("%3.2f", TempC) + DEGREE_SYMBOL);


		i2c.SetSlaveAddress(fileHandle, TCS3414_I2C_ADDR);

		//i2cCommBuffer[0] = TCS3414_CONFIG;
		i2cCommBuffer[0] = TCS3414_POWER;
		i2c.write(fileHandle, i2cCommBuffer, 1);

		// Data CH1 low
		i2cCommBuffer[0] = 0x90;
		i2c.write(fileHandle, i2cCommBuffer, 1);
		i2c.read(fileHandle, i2cCommBuffer, 1);
		COLORBUF1 = (char) i2cCommBuffer[0];

		// Data CH1 high
		i2cCommBuffer[0] = 0x91;
		i2c.write(fileHandle, i2cCommBuffer, 1);
		i2c.read(fileHandle, i2cCommBuffer, 1);
		COLORBUF2 = (char) i2cCommBuffer[0];
		green = 256 * COLORBUF2 + COLORBUF1;

		// Data CH2 low
		i2cCommBuffer[0] = 0x92;
		i2c.write(fileHandle, i2cCommBuffer, 1);
		i2c.read(fileHandle, i2cCommBuffer, 1);
		COLORBUF1 = (char) i2cCommBuffer[0];

		// Data CH2 high
		i2cCommBuffer[0] = 0x93;
		i2c.write(fileHandle, i2cCommBuffer, 1);
		i2c.read(fileHandle, i2cCommBuffer, 1);
		COLORBUF2 = (char) i2cCommBuffer[0];
		red = 256 * COLORBUF2 + COLORBUF1;

		// Data CH3 low
		i2cCommBuffer[0] = 0x94;
		i2c.write(fileHandle, i2cCommBuffer, 1);
		i2c.read(fileHandle, i2cCommBuffer, 1);
		COLORBUF1 = (char) i2cCommBuffer[0];

		// Data CH3 high
		i2cCommBuffer[0] = 0x95;
		i2c.write(fileHandle, i2cCommBuffer, 1);
		i2c.read(fileHandle, i2cCommBuffer, 1);
		COLORBUF2 = (char) i2cCommBuffer[0];
		blue = 256 * COLORBUF2 + COLORBUF1;

		//textViewTemperature.setTextColor(Color.rgb(red, green, blue));
		onTimerChangedValues(String.format("%3.2f", TempC) + DEGREE_SYMBOL, Color.rgb(red, green, blue));

	 /* Close the i2c file */


		i2c.close(fileHandle);
	}

	public void onTimerChangedValues(String TempStr, int color) {
		textViewHeight.setText("Fillheight: " + adcReader.read_adc("in_voltage5_raw") +"mm");
		textViewHeight.setTextColor(color);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_i2c, menu);
		return true;
	}

	/*
     * 	(non-Javadoc)
     * @see android.app.Activity#onStop()
     */
	protected void onStop() {
		android.os.Process.killProcess(android.os.Process.myPid());
		finish();
		Cancel();
		super.onStop();
	}


	public void setTaster(int nbr, Boolean value) {
		if (nbr > 3) return;

	}

	void LEDTaster1() {
		if (gpio.read_value(BUTTON_T1).equals(PRESSED)) {
			TS.SpeakOut(textViewHeight.getText().toString());
			setTaster(0, true);
		}
		else
			setTaster(0, false);

		if (gpio.read_value(BUTTON_T2).equals(PRESSED)) {
			setTaster(1, true);

		}
		else
			setTaster(1, false);

		if (gpio.read_value(BUTTON_T3).equals(PRESSED))
			setTaster(2, true);
		else
			setTaster(2, false);

		if (gpio.read_value(BUTTON_T4).equals(PRESSED)) {
			setTaster(3, true);
			finish();
		}
		else
			setTaster(3, false);
	}

	void LEDTaster2() {

		if (gpio.read_value(BUTTON_T1).equals(PRESSED))
			direction = !direction;



	}

	/*
       * Called when app will be destroyed
       * @see android.app.Activity#onDestroy()
       */
	@Override
	protected void onDestroy() {
		Cancel();
		super.onDestroy();
	}

	private void Cancel() {
		if (timer != null) {
			timer.cancel();
			timer.purge();
			timer = null;
		}

		if (myTimerTask != null) {
			myTimerTask.cancel();
			myTimerTask = null;
		}

    	/* Turn LED-1 and LED-4 off */
		gpio.write_value(LED_L1, OFF);
		gpio.write_value(LED_L2, OFF);
		gpio.write_value(LED_L3, OFF);
		gpio.write_value(LED_L4, OFF);
	}

	class MyTimerTask extends TimerTask {
		@Override
		public void run() {

			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (mode1RBTN.isChecked()) {
                        if (runOnLED)
                            runOnLED = false;
                        LEDTaster1();
                    }
					else if (mode2RBTN.isChecked()) {
                        if(!runOnLED) {
                            runOnLED = true;
                            LEDRunHandler.postDelayed(LEDRun,100);
                        }
                        LEDTaster2();
					}
					tvPoti.setText(adcReader.read_adc("in_voltage5_raw"));
           /*
            * Update the counter and view it
            */
					counter++;

           /*
            * If Button T4 is pressed, turn LED L4 on
            */
				/*	if (gpio.read_value(BUTTON_T4).equals(PRESSED))
						gpio.write_value(LED_L4, ON);
					else
						gpio.write_value(LED_L4, OFF);*/

	        /*
	         * Blinks LED L1
	         */
					textViewHeight.setText("Fillheight: " + adcReader.read_adc("in_voltage5_raw") +"mm");
					index++;
					Integer fillHeight=Integer.parseInt(adcReader.read_adc("in_voltage5_raw").toString());
					if (500<fillHeight&&fillHeight<3000) {
						if (index%10>5) {
							gpio.write_value(LED_L1, ON);
						} else {
							gpio.write_value(LED_L1, OFF);
						}
					}
					if(fillHeight<=500){
						if (index%10==1) {
							gpio.write_value(LED_L1, ON);
						} else {
							gpio.write_value(LED_L1, OFF);
						}
					}
					if(fillHeight>=3000){
							gpio.write_value(LED_L1, ON);
					}
				}
			});
		}
	}

    private void ledOnOff(int ledPattern)
    {
        int i;
        int mask = 0x01;

            for (i=0; i<4; i++)
    {
        if ((ledPattern & mask) != 0)
            gpio.write_value(LEDS[i], ON);
        else
            gpio.write_value(LEDS[i], OFF);
        mask = mask << 1;
    }
    }

    private Runnable LEDRun = new Runnable() {
        int adcValue = 0;
        int shiftRegister = 0x01;
        @Override
        public void run() {
            if (LEDRunHandler == null) return;
            adcValue = Integer.parseInt(adcReader.read_adc("in_voltage5_raw"));

            if(runOnLED) {
                if (adcValue <= 500) {
                    LEDRunHandler.postDelayed(this, 50);
                } else if (adcValue > 500 && adcValue <= 1000)
                    LEDRunHandler.postDelayed(this, 250);
                else
                    LEDRunHandler.postDelayed(this, 1000);

            }
            if (direction)
            {
                ledOnOff(shiftRegister);
                shiftRegister = shiftRegister << 1;
                if (shiftRegister == 0x10)
                    shiftRegister = 0x01;
            }
            else
            {
                ledOnOff(shiftRegister);
                shiftRegister = shiftRegister >> 1;
                if (shiftRegister == 0x00)
                    shiftRegister = 0x08;
            }

        }
    };

}


