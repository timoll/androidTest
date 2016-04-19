/*
 ***************************************************************************
 * \brief   Embedded Android I2C Exercise 5.2
 *	        Native basic i2c communication interface
 *	        Only a minimal error handling is implemented.
 * \file    I2C.java
 * \version 1.0
 * \date    06.03.2014
 * \author  Martin Aebersold
 *
 * \remark  Last Modifications:
 * \remark  V1.0, AOM1, 06.03.2014
 ***************************************************************************
 */

package bfh.ti.i2c_jni_template;

/***************************************************************************
 * This is an I2C operation class
 **************************************************************************/

public class I2C
{
    /**
     * @param deviceName
     *
     * @return return file handler else return <0 on fail
     */
    public native int open(String deviceName);


    /**
     * @param fileHandler
     * @param i2c_adr
     *
     * @return return file handler else return <0 on fail
     */
    public native int SetSlaveAddress(int fileHandler, int i2c_adr);

    /**
     * @param fileHandler
     * @param buffer
     * @param length
     *
     * @return Number of bytes read
     */
    public native int read(int fileHandler, int buffer[], int length);

    /**
     * @param fileHandler
     * @param buffer
     * @param length
     *
     * @return Number of bytes written
     */
    public native int write(int fileHandler, int buffer[], int length);


    /**
     * @param fileHandler
     *
     * @return -
     */
    public native void close(int fileHandler);

    static
    {
        System.loadLibrary("i2c");
    }
}