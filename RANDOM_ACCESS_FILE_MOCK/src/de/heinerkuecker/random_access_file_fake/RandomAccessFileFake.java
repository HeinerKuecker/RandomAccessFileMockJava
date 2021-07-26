package de.heinerkuecker.random_access_file_fake;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.UTFDataFormatException;
import java.util.Arrays;

/**
 * Fake implementation of {@link RandomAccessFileInterface}
 * to fake {@link RandomAccessFile}
 * for test in RAM without disk access.
 *
 * Diese Variante vergrössert das innere Speicher-Array
 * {@link #fakeMemory} bei Bedarf ohne Pufferung,
 * also nur auf die unbedingt notwendige Größe.
 */
public class RandomAccessFileFake
implements RandomAccessFileInterface
{
    /**
     * Closed state.
     *
     * Public for test.
     *
     * Reset this state on creation or open.
     */
    public boolean closed = true;

    /**
     * Memory.
     *
     * Public for test.
     */
    public byte[] fakeMemory = new byte[ 0 ];

    /**
     * File pointer.
     * Current position in file.
     *
     * Public for test.
     *
     * Reset this position on reopen.
     */
    public int filePointer;

    //public byte[] getFakeMemory()
    //{
    //    return this.fakeMemory;
    //}
    //public void setFakeMemory(
    //        final byte[] fakeMemoryToSet )
    //{
    //    this.fakeMemory = fakeMemoryToSet;
    //}

    /**
     * Only for test, (re)open this.
     *
     * @throws IOException
     */
    public void open()
            throws IOException
    {
        if ( ! closed )
        {
            throw new IOException( "file already/concurrent open" );
        }

        // reopen
        closed = false;

        // navigate to begin of file
        filePointer = 0;
    }

    /**
     * Reads a byte of data from this file. The byte is returned as an
     * integer in the range 0 to 255 ({@code 0x00-0x0ff}). This
     * method blocks if no input is yet available.
     * <p>
     * Although {@code RandomAccessFile} is not a subclass of
     * {@code InputStream}, this method behaves in exactly the same
     * way as the {@link InputStream#read()} method of
     * {@code InputStream}.
     *
     * @return     the next byte of data, or {@code -1} if the end of the
     *             file has been reached.
     * @exception  IOException  if an I/O error occurs. Not thrown if
     *                          end-of-file has been reached.
     */
    @Override
    public int read()
            throws IOException
    {
        if ( closed )
        {
            throw new IOException( "already closed" );
        }

        return fakeMemory[ filePointer++ ] & 0xFF;
    }

    /**
     * Reads up to {@code len} bytes of data from this file into an
     * array of bytes. This method blocks until at least one byte of input
     * is available.
     * <p>
     * Although {@code RandomAccessFile} is not a subclass of
     * {@code InputStream}, this method behaves in exactly the
     * same way as the {@link InputStream#read(byte[], int, int)} method of
     * {@code InputStream}.
     *
     * @param      b     the buffer into which the data is read.
     * @param      off   the start offset in array {@code b}
     *                   at which the data is written.
     * @param      len   the maximum number of bytes read.
     * @return     the total number of bytes read into the buffer, or
     *             {@code -1} if there is no more data because the end of
     *             the file has been reached.
     * @exception  IOException If the first byte cannot be read for any reason
     * other than end of file, or if the random access file has been closed, or if
     * some other I/O error occurs.
     * @exception  NullPointerException If {@code b} is {@code null}.
     * @exception  IndexOutOfBoundsException If {@code off} is negative,
     * {@code len} is negative, or {@code len} is greater than
     * {@code b.length - off}
     */
    @Override
    public int read(
            final byte[] b ,
            final int off ,
            final int len )
                    throws IOException
    {
        if ( closed )
        {
            throw new IOException( "already closed" );
        }

        final int possibleLengthToRead =
                Math.min(
                        len ,
                        this.fakeMemory.length -
                        filePointer );

        if ( possibleLengthToRead < 1 )
        {
            return -1;
        }

        System.arraycopy(
                //src
                this.fakeMemory ,
                //srcPos
                this.filePointer ,
                //dest
                b ,
                //destPos
                off ,
                //length
                possibleLengthToRead );

        filePointer += possibleLengthToRead;
        return possibleLengthToRead;
    }

    /**
     * Reads a byte of data from this file. The byte is returned as an
     * integer in the range 0 to 255 ({@code 0x00-0x0ff}). This
     * method blocks if no input is yet available.
     * <p>
     * Although {@code RandomAccessFile} is not a subclass of
     * {@code InputStream}, this method behaves in exactly the same
     * way as the {@link InputStream#read()} method of
     * {@code InputStream}.
     *
     * @return     the next byte of data, or {@code -1} if the end of the
     *             file has been reached.
     * @exception  IOException  if an I/O error occurs. Not thrown if
     *                          end-of-file has been reached.
     */
    @Override
    public int read(
            final byte[] b )
            throws IOException
    {
        if ( closed )
        {
            throw new IOException( "already closed" );
        }

        return read(b, 0, b.length);
    }

    /**
     * Reads {@code b.length} bytes from this file into the byte
     * array, starting at the current file pointer. This method reads
     * repeatedly from the file until the requested number of bytes are
     * read. This method blocks until the requested number of bytes are
     * read, the end of the stream is detected, or an exception is thrown.
     *
     * @param   b   the buffer into which the data is read.
     * @throws  NullPointerException if {@code b} is {@code null}.
     * @throws  EOFException  if this file reaches the end before reading
     *              all the bytes.
     * @throws  IOException   if an I/O error occurs.
     */
    @Override
    public void readFully(
            final byte[] b )
                    throws IOException
    {
        if ( closed )
        {
            throw new IOException( "already closed" );
        }

        //if ( b.length > fakeMemory.length - filePointer )
        //{
        //    throw new EOFException();
        //}

        read( b, 0, b.length);
    }

    /**
     * Reads exactly {@code len} bytes from this file into the byte
     * array, starting at the current file pointer. This method reads
     * repeatedly from the file until the requested number of bytes are
     * read. This method blocks until the requested number of bytes are
     * read, the end of the stream is detected, or an exception is thrown.
     *
     * @param   b     the buffer into which the data is read.
     * @param   off   the start offset into the data array {@code b}.
     * @param   len   the number of bytes to read.
     * @throws  NullPointerException if {@code b} is {@code null}.
     * @throws  IndexOutOfBoundsException if {@code off} is negative,
     *                {@code len} is negative, or {@code len} is greater than
     *                {@code b.length - off}.
     * @throws  EOFException  if this file reaches the end before reading
     *                all the bytes.
     * @throws  IOException   if an I/O error occurs.
     */
    @Override
    public void readFully(
            final byte[] b ,
            final int off ,
            final int len )
            throws IOException
    {
        if ( closed )
        {
            throw new IOException( "already closed" );
        }

        final int lengthToRead = len - off;

        if ( lengthToRead > fakeMemory.length - filePointer )
        {
            throw new EOFException();
        }

        read( b , off , len );
    }

    /**
     * Attempts to skip over {@code n} bytes of input discarding the
     * skipped bytes.
     * <p>
     *
     * This method may skip over some smaller number of bytes, possibly zero.
     * This may result from any of a number of conditions; reaching end of
     * file before {@code n} bytes have been skipped is only one
     * possibility. This method never throws an {@code EOFException}.
     * The actual number of bytes skipped is returned.  If {@code n}
     * is negative, no bytes are skipped.
     *
     * @param      n   the number of bytes to be skipped.
     * @return     the actual number of bytes skipped.
     * @exception  IOException  if an I/O error occurs.
     */
    @Override
    public int skipBytes(
            final int n)
                    throws IOException
    {
        if ( closed )
        {
            throw new IOException( "already closed" );
        }

        //if ( n < 1 )
        //{
        //    return 0;
        //}
        //final int possibleNumberOfBytesToSkip =
        //        Math.min(
        //                n ,
        //                ( this.fakeMemory.length - this.filePointer ) - 1 );
        //
        //this.filePointer += possibleNumberOfBytesToSkip;
        //return possibleNumberOfBytesToSkip;
        long pos;
        long len;
        long newpos;

        if (n <= 0) {
            return 0;
        }
        pos = getFilePointer();
        len = length();
        newpos = pos + n;
        if (newpos > len) {
            newpos = len;
        }
        seek(newpos);

        /* return the actual number of bytes skipped */
        return (int) (newpos - pos);
    }

    /**
     * Writes the specified byte to this file. The write starts at
     * the current file pointer.
     *
     * @param      b   the {@code byte} to be written.
     * @exception  IOException  if an I/O error occurs.
     */
    @Override
    public void write(
            final int b )
            throws IOException
    {
        if ( closed )
        {
            throw new IOException( "already closed" );
        }

        if ( this.fakeMemory.length < filePointer + 1 )
        {
            this.fakeMemory =
                    Arrays.copyOf(
                            //original
                            this.fakeMemory ,
                            //newLength
                            filePointer + 1 );
        }

        this.fakeMemory[ filePointer++ ] = (byte) ( b & 0xFF );
    }

    /**
     * Writes {@code b.length} bytes from the specified byte array
     * to this file, starting at the current file pointer.
     *
     * @param      b   the data.
     * @exception  IOException  if an I/O error occurs.
     */
    @Override
    public void write(
            final byte[] b )
            throws IOException
    {
        if ( closed )
        {
            throw new IOException( "already closed" );
        }

        //if ( this.fakeMemory.length < filePointer + b.length )
        //{
        //    this.fakeMemory =
        //            Arrays.copyOf(
        //                    //original
        //                    this.fakeMemory ,
        //                    //newLength
        //                    ( filePointer + 1 ) + b.length );
        //}
        write( b , 0 , b.length );
    }

    /**
     * Writes {@code len} bytes from the specified byte array
     * starting at offset {@code off} to this file.
     *
     * @param      b     the data.
     * @param      off   the start offset in the data.
     * @param      len   the number of bytes to write.
     * @exception  IOException  if an I/O error occurs.
     */
    @Override
    public void write(
            final byte[] b ,
            final int off ,
            final int len )
            throws IOException
    {
        if ( closed )
        {
            throw new IOException( "already closed" );
        }

        if ( this.fakeMemory.length <= filePointer + len )
        {
            this.fakeMemory =
                    Arrays.copyOf(
                            //original
                            this.fakeMemory ,
                            //newLength
                            ( filePointer + 1 ) + len );
        }

        System.arraycopy(
                //src
                b ,
                //srcPos
                off ,
                //dest
                this.fakeMemory ,
                //destPos
                filePointer ,
                //length
                len );

        filePointer += len;
    }

    /**
     * Returns the current offset in this file.
     *
     * @return     the offset from the beginning of the file, in bytes,
     *             at which the next read or write occurs.
     * @exception  IOException  if an I/O error occurs.
     */
    @Override
    public long getFilePointer()
            throws IOException
    {
        if ( closed )
        {
            throw new IOException( "already closed" );
        }

        return this.filePointer;
    }

    /**
     * Sets the file-pointer offset, measured from the beginning of this
     * file, at which the next read or write occurs.  The offset may be
     * set beyond the end of the file. Setting the offset beyond the end
     * of the file does not change the file length.  The file length will
     * change only by writing after the offset has been set beyond the end
     * of the file.
     *
     * @param      pos   the offset position, measured in bytes from the
     *                   beginning of the file, at which to set the file
     *                   pointer.
     * @exception  IOException  if {@code pos} is less than
     *                          {@code 0} or if an I/O error occurs.
     */
    @Override
    public void seek(long pos) throws IOException
    {
        if ( closed )
        {
            throw new IOException( "already closed" );
        }

        if ( pos < 0L )
        {
            throw new IOException("Negative seek offset");
        }

        if ( pos > Integer.MAX_VALUE )
        {
            throw new ArithmeticException( String.valueOf( pos ) );
        }

        filePointer = (int) pos;
    }

    /**
     * Returns the length of this file.
     *
     * @return     the length of this file, measured in bytes.
     * @exception  IOException  if an I/O error occurs.
     */
    @Override
    public long length()
            throws IOException
    {
        if ( closed )
        {
            throw new IOException( "already closed" );
        }

        return this.fakeMemory.length;
    }

    /**
     * Sets the length of this file.
     *
     * <p> If the present length of the file as returned by the
     * {@code length} method is greater than the {@code newLength}
     * argument then the file will be truncated.  In this case, if the file
     * offset as returned by the {@code getFilePointer} method is greater
     * than {@code newLength} then after this method returns the offset
     * will be equal to {@code newLength}.
     *
     * <p> If the present length of the file as returned by the
     * {@code length} method is smaller than the {@code newLength}
     * argument then the file will be extended.  In this case, the contents of
     * the extended portion of the file are not defined.
     *
     * @param      newLength    The desired length of the file
     * @exception  IOException  If an I/O error occurs
     * @since      1.2
     */
    @Override
    public void setLength(
            final long newLength )
            throws IOException
    {
        if ( closed )
        {
            throw new IOException( "already closed" );
        }

        if ( newLength < 0L )
        {
            throw new IOException("Negative length " + newLength );
        }

        if ( newLength > Integer.MAX_VALUE )
        {
            throw new ArithmeticException( String.valueOf( newLength ) );
        }

        final int newLengthInt = (int) newLength;

        if ( newLength != this.fakeMemory.length )
        {
            this.fakeMemory =
                    Arrays.copyOf(
                            this.fakeMemory ,
                            newLengthInt );
        }

        if ( this.filePointer > newLengthInt )
        {
            this.filePointer = newLengthInt;
        }
    }

    /**
     * Closes this random access file stream and releases any system
     * resources associated with the stream. A closed random access
     * file cannot perform input or output operations and cannot be
     * reopened.
     *
     * <p> If this file has an associated channel then the channel is closed
     * as well.
     *
     * @exception  IOException  if an I/O error occurs.
     *
     * @revised 1.4
     * @spec JSR-51
     */
    @Override
    public void close() throws IOException
    {
        //new Exception().printStackTrace( System.out );
        this.closed = true;
    }

    /**
     * Reads a {@code boolean} from this file. This method reads a
     * single byte from the file, starting at the current file pointer.
     * A value of {@code 0} represents
     * {@code false}. Any other value represents {@code true}.
     * This method blocks until the byte is read, the end of the stream
     * is detected, or an exception is thrown.
     *
     * @return     the {@code boolean} value read.
     * @exception  EOFException  if this file has reached the end.
     * @exception  IOException   if an I/O error occurs.
     */
    @Override
    public boolean readBoolean()
            throws IOException
    {
        if ( closed )
        {
            throw new IOException( "already closed" );
        }

        int ch = this.read();
        if (ch < 0)
            throw new EOFException();
        return (ch != 0);
    }

    /**
     * Reads a signed eight-bit value from this file. This method reads a
     * byte from the file, starting from the current file pointer.
     * If the byte read is {@code b}, where
     * <code>0&nbsp;&lt;=&nbsp;b&nbsp;&lt;=&nbsp;255</code>,
     * then the result is:
     * <blockquote><pre>
     *     (byte)(b)
     * </pre></blockquote>
     * <p>
     * This method blocks until the byte is read, the end of the stream
     * is detected, or an exception is thrown.
     *
     * @return     the next byte of this file as a signed eight-bit
     *             {@code byte}.
     * @exception  EOFException  if this file has reached the end.
     * @exception  IOException   if an I/O error occurs.
     */
    @Override
    public byte readByte()
            throws IOException
    {
        if ( closed )
        {
            throw new IOException( "already closed" );
        }

        int ch = this.read();
        if (ch < 0)
            throw new EOFException();
        return (byte)(ch);
    }

    /**
     * Reads an unsigned eight-bit number from this file. This method reads
     * a byte from this file, starting at the current file pointer,
     * and returns that byte.
     * <p>
     * This method blocks until the byte is read, the end of the stream
     * is detected, or an exception is thrown.
     *
     * @return     the next byte of this file, interpreted as an unsigned
     *             eight-bit number.
     * @exception  EOFException  if this file has reached the end.
     * @exception  IOException   if an I/O error occurs.
     */
    @Override
    public int readUnsignedByte()
            throws IOException
    {
        if ( closed )
        {
            throw new IOException( "already closed" );
        }

        int ch = this.read();
        if (ch < 0)
            throw new EOFException();
        return ch;
    }

    /**
     * Reads a signed 16-bit number from this file. The method reads two
     * bytes from this file, starting at the current file pointer.
     * If the two bytes read, in order, are
     * {@code b1} and {@code b2}, where each of the two values is
     * between {@code 0} and {@code 255}, inclusive, then the
     * result is equal to:
     * <blockquote><pre>
     *     (short)((b1 &lt;&lt; 8) | b2)
     * </pre></blockquote>
     * <p>
     * This method blocks until the two bytes are read, the end of the
     * stream is detected, or an exception is thrown.
     *
     * @return     the next two bytes of this file, interpreted as a signed
     *             16-bit number.
     * @exception  EOFException  if this file reaches the end before reading
     *               two bytes.
     * @exception  IOException   if an I/O error occurs.
     */
    @Override
    public short readShort()
            throws IOException
    {
        if ( closed )
        {
            throw new IOException( "already closed" );
        }

        int ch1 = this.read();
        int ch2 = this.read();
        if ((ch1 | ch2) < 0)
            throw new EOFException();
        return (short)((ch1 << 8) + (ch2 << 0));
    }

    /**
     * Reads an unsigned 16-bit number from this file. This method reads
     * two bytes from the file, starting at the current file pointer.
     * If the bytes read, in order, are
     * {@code b1} and {@code b2}, where
     * <code>0&nbsp;&lt;=&nbsp;b1, b2&nbsp;&lt;=&nbsp;255</code>,
     * then the result is equal to:
     * <blockquote><pre>
     *     (b1 &lt;&lt; 8) | b2
     * </pre></blockquote>
     * <p>
     * This method blocks until the two bytes are read, the end of the
     * stream is detected, or an exception is thrown.
     *
     * @return     the next two bytes of this file, interpreted as an unsigned
     *             16-bit integer.
     * @exception  EOFException  if this file reaches the end before reading
     *               two bytes.
     * @exception  IOException   if an I/O error occurs.
     */
    @Override
    public int readUnsignedShort()
            throws IOException
    {
        if ( closed )
        {
            throw new IOException( "already closed" );
        }

        int ch1 = this.read();
        int ch2 = this.read();
        if ((ch1 | ch2) < 0)
            throw new EOFException();
        return (ch1 << 8) + (ch2 << 0);
    }

    /**
     * Reads a character from this file. This method reads two
     * bytes from the file, starting at the current file pointer.
     * If the bytes read, in order, are
     * {@code b1} and {@code b2}, where
     * <code>0&nbsp;&lt;=&nbsp;b1,&nbsp;b2&nbsp;&lt;=&nbsp;255</code>,
     * then the result is equal to:
     * <blockquote><pre>
     *     (char)((b1 &lt;&lt; 8) | b2)
     * </pre></blockquote>
     * <p>
     * This method blocks until the two bytes are read, the end of the
     * stream is detected, or an exception is thrown.
     *
     * @return     the next two bytes of this file, interpreted as a
     *                  {@code char}.
     * @exception  EOFException  if this file reaches the end before reading
     *               two bytes.
     * @exception  IOException   if an I/O error occurs.
     */
    @Override
    public char readChar()
            throws IOException
    {
        if ( closed )
        {
            throw new IOException( "already closed" );
        }

        int ch1 = this.read();
        int ch2 = this.read();
        if ((ch1 | ch2) < 0)
            throw new EOFException();
        return (char)((ch1 << 8) + (ch2 << 0));
    }

    /**
     * Reads a signed 32-bit integer from this file. This method reads 4
     * bytes from the file, starting at the current file pointer.
     * If the bytes read, in order, are {@code b1},
     * {@code b2}, {@code b3}, and {@code b4}, where
     * <code>0&nbsp;&lt;=&nbsp;b1, b2, b3, b4&nbsp;&lt;=&nbsp;255</code>,
     * then the result is equal to:
     * <blockquote><pre>
     *     (b1 &lt;&lt; 24) | (b2 &lt;&lt; 16) + (b3 &lt;&lt; 8) + b4
     * </pre></blockquote>
     * <p>
     * This method blocks until the four bytes are read, the end of the
     * stream is detected, or an exception is thrown.
     *
     * @return     the next four bytes of this file, interpreted as an
     *             {@code int}.
     * @exception  EOFException  if this file reaches the end before reading
     *               four bytes.
     * @exception  IOException   if an I/O error occurs.
     */
    @Override
    public int readInt()
            throws IOException
    {
        if ( closed )
        {
            throw new IOException( "already closed" );
        }

        int ch1 = this.read();
        int ch2 = this.read();
        int ch3 = this.read();
        int ch4 = this.read();
        if ((ch1 | ch2 | ch3 | ch4) < 0)
            throw new EOFException();
        return ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0));
    }

    /**
     * Reads a signed 64-bit integer from this file. This method reads eight
     * bytes from the file, starting at the current file pointer.
     * If the bytes read, in order, are
     * {@code b1}, {@code b2}, {@code b3},
     * {@code b4}, {@code b5}, {@code b6},
     * {@code b7}, and {@code b8,} where:
     * <blockquote><pre>
     *     0 &lt;= b1, b2, b3, b4, b5, b6, b7, b8 &lt;=255,
     * </pre></blockquote>
     * <p>
     * then the result is equal to:
     * <blockquote><pre>
     *     ((long)b1 &lt;&lt; 56) + ((long)b2 &lt;&lt; 48)
     *     + ((long)b3 &lt;&lt; 40) + ((long)b4 &lt;&lt; 32)
     *     + ((long)b5 &lt;&lt; 24) + ((long)b6 &lt;&lt; 16)
     *     + ((long)b7 &lt;&lt; 8) + b8
     * </pre></blockquote>
     * <p>
     * This method blocks until the eight bytes are read, the end of the
     * stream is detected, or an exception is thrown.
     *
     * @return     the next eight bytes of this file, interpreted as a
     *             {@code long}.
     * @exception  EOFException  if this file reaches the end before reading
     *               eight bytes.
     * @exception  IOException   if an I/O error occurs.
     */
    @Override
    public long readLong()
            throws IOException
    {
        if ( closed )
        {
            throw new IOException( "already closed" );
        }

        return ((long)(readInt()) << 32) + (readInt() & 0xFFFFFFFFL);
    }

    /**
     * Reads a {@code float} from this file. This method reads an
     * {@code int} value, starting at the current file pointer,
     * as if by the {@code readInt} method
     * and then converts that {@code int} to a {@code float}
     * using the {@code intBitsToFloat} method in class
     * {@code Float}.
     * <p>
     * This method blocks until the four bytes are read, the end of the
     * stream is detected, or an exception is thrown.
     *
     * @return     the next four bytes of this file, interpreted as a
     *             {@code float}.
     * @exception  EOFException  if this file reaches the end before reading
     *             four bytes.
     * @exception  IOException   if an I/O error occurs.
     * @see        java.io.RandomAccessFile#readInt()
     * @see        java.lang.Float#intBitsToFloat(int)
     */
    @Override
    public float readFloat()
            throws IOException
    {
        if ( closed )
        {
            throw new IOException( "already closed" );
        }

        return Float.intBitsToFloat(readInt());
    }

    /**
     * Reads a {@code double} from this file. This method reads a
     * {@code long} value, starting at the current file pointer,
     * as if by the {@code readLong} method
     * and then converts that {@code long} to a {@code double}
     * using the {@code longBitsToDouble} method in
     * class {@code Double}.
     * <p>
     * This method blocks until the eight bytes are read, the end of the
     * stream is detected, or an exception is thrown.
     *
     * @return     the next eight bytes of this file, interpreted as a
     *             {@code double}.
     * @exception  EOFException  if this file reaches the end before reading
     *             eight bytes.
     * @exception  IOException   if an I/O error occurs.
     * @see        java.io.RandomAccessFile#readLong()
     * @see        java.lang.Double#longBitsToDouble(long)
     */
    @Override
    public double readDouble()
            throws IOException
    {
        if ( closed )
        {
            throw new IOException( "already closed" );
        }

        return Double.longBitsToDouble(readLong());
    }

    /**
     * Reads the next line of text from this file.  This method successively
     * reads bytes from the file, starting at the current file pointer,
     * until it reaches a line terminator or the end
     * of the file.  Each byte is converted into a character by taking the
     * byte's value for the lower eight bits of the character and setting the
     * high eight bits of the character to zero.  This method does not,
     * therefore, support the full Unicode character set.
     *
     * <p> A line of text is terminated by a carriage-return character
     * ({@code '\u005Cr'}), a newline character ({@code '\u005Cn'}), a
     * carriage-return character immediately followed by a newline character,
     * or the end of the file.  Line-terminating characters are discarded and
     * are not included as part of the string returned.
     *
     * <p> This method blocks until a newline character is read, a carriage
     * return and the byte following it are read (to see if it is a newline),
     * the end of the file is reached, or an exception is thrown.
     *
     * @return     the next line of text from this file, or null if end
     *             of file is encountered before even one byte is read.
     * @exception  IOException  if an I/O error occurs.
     */
    @Override
    public String readLine()
            throws IOException
    {
        if ( closed )
        {
            throw new IOException( "already closed" );
        }

        StringBuilder input = new StringBuilder();
        int c = -1;
        boolean eol = false;

        while (!eol) {
            switch (c = read()) {
            case -1:
            case '\n':
                eol = true;
                break;
            case '\r':
                eol = true;
                long cur = getFilePointer();
                if ((read()) != '\n') {
                    seek(cur);
                }
                break;
            default:
                input.append((char)c);
                break;
            }
        }

        if ((c == -1) && (input.length() == 0)) {
            return null;
        }
        return input.toString();
    }

    /**
     * Reads in a string from this file. The string has been encoded
     * using a
     * <a href="DataInput.html#modified-utf-8">modified UTF-8</a>
     * format.
     * <p>
     * The first two bytes are read, starting from the current file
     * pointer, as if by
     * {@code readUnsignedShort}. This value gives the number of
     * following bytes that are in the encoded string, not
     * the length of the resulting string. The following bytes are then
     * interpreted as bytes encoding characters in the modified UTF-8 format
     * and are converted into characters.
     * <p>
     * This method blocks until all the bytes are read, the end of the
     * stream is detected, or an exception is thrown.
     *
     * @return     a Unicode string.
     * @exception  EOFException            if this file reaches the end before
     *               reading all the bytes.
     * @exception  IOException             if an I/O error occurs.
     * @exception  UTFDataFormatException  if the bytes do not represent
     *               valid modified UTF-8 encoding of a Unicode string.
     * @see        java.io.RandomAccessFile#readUnsignedShort()
     */
    @Override
    public String readUTF()
            throws IOException
    {
        if ( closed )
        {
            throw new IOException( "already closed" );
        }

        return DataInputStream.readUTF(this);
    }

    /**
     * Writes a {@code boolean} to the file as a one-byte value. The
     * value {@code true} is written out as the value
     * {@code (byte)1}; the value {@code false} is written out
     * as the value {@code (byte)0}. The write starts at
     * the current position of the file pointer.
     *
     * @param      v   a {@code boolean} value to be written.
     * @exception  IOException  if an I/O error occurs.
     */
    @Override
    public void writeBoolean(boolean v)
            throws IOException
    {
        if ( closed )
        {
            throw new IOException( "already closed" );
        }

        write(v ? 1 : 0);
    }

    /**
     * Writes a {@code byte} to the file as a one-byte value. The
     * write starts at the current position of the file pointer.
     *
     * @param      v   a {@code byte} value to be written.
     * @exception  IOException  if an I/O error occurs.
     */
    @Override
    public void writeByte(int v)
            throws IOException
    {
        if ( closed )
        {
            throw new IOException( "already closed" );
        }

        write(v);
    }

    /**
     * Writes a {@code short} to the file as two bytes, high byte first.
     * The write starts at the current position of the file pointer.
     *
     * @param      v   a {@code short} to be written.
     * @exception  IOException  if an I/O error occurs.
     */
    @Override
    public void writeShort(int v)
            throws IOException
    {
        if ( closed )
        {
            throw new IOException( "already closed" );
        }

        write((v >>> 8) & 0xFF);
        write((v >>> 0) & 0xFF);
    }

    /**
     * Writes a {@code char} to the file as a two-byte value, high
     * byte first. The write starts at the current position of the
     * file pointer.
     *
     * @param      v   a {@code char} value to be written.
     * @exception  IOException  if an I/O error occurs.
     */
    @Override
    public void writeChar(int v)
            throws IOException
    {
        if ( closed )
        {
            throw new IOException( "already closed" );
        }

        write((v >>> 8) & 0xFF);
        write((v >>> 0) & 0xFF);
    }

    /**
     * Writes an {@code int} to the file as four bytes, high byte first.
     * The write starts at the current position of the file pointer.
     *
     * @param      v   an {@code int} to be written.
     * @exception  IOException  if an I/O error occurs.
     */
    @Override
    public void writeInt(int v)
            throws IOException
    {
        if ( closed )
        {
            throw new IOException( "already closed" );
        }

        write((v >>> 24) & 0xFF);
        write((v >>> 16) & 0xFF);
        write((v >>>  8) & 0xFF);
        write((v >>>  0) & 0xFF);

    }

    /**
     * Writes a {@code long} to the file as eight bytes, high byte first.
     * The write starts at the current position of the file pointer.
     *
     * @param      v   a {@code long} to be written.
     * @exception  IOException  if an I/O error occurs.
     */
    @Override
    public void writeLong(long v)
            throws IOException
    {
        if ( closed )
        {
            throw new IOException( "already closed" );
        }

        write((int)(v >>> 56) & 0xFF);
        write((int)(v >>> 48) & 0xFF);
        write((int)(v >>> 40) & 0xFF);
        write((int)(v >>> 32) & 0xFF);
        write((int)(v >>> 24) & 0xFF);
        write((int)(v >>> 16) & 0xFF);
        write((int)(v >>>  8) & 0xFF);
        write((int)(v >>>  0) & 0xFF);
    }

    /**
     * Converts the float argument to an {@code int} using the
     * {@code floatToIntBits} method in class {@code Float},
     * and then writes that {@code int} value to the file as a
     * four-byte quantity, high byte first. The write starts at the
     * current position of the file pointer.
     *
     * @param      v   a {@code float} value to be written.
     * @exception  IOException  if an I/O error occurs.
     * @see        java.lang.Float#floatToIntBits(float)
     */
    @Override
    public void writeFloat(float v)
            throws IOException
    {
        if ( closed )
        {
            throw new IOException( "already closed" );
        }

        writeInt(Float.floatToIntBits(v));
    }

    /**
     * Converts the double argument to a {@code long} using the
     * {@code doubleToLongBits} method in class {@code Double},
     * and then writes that {@code long} value to the file as an
     * eight-byte quantity, high byte first. The write starts at the current
     * position of the file pointer.
     *
     * @param      v   a {@code double} value to be written.
     * @exception  IOException  if an I/O error occurs.
     * @see        java.lang.Double#doubleToLongBits(double)
     */
    @Override
    public void writeDouble(double v)
            throws IOException
    {
        if ( closed )
        {
            throw new IOException( "already closed" );
        }

        writeLong(Double.doubleToLongBits(v));
    }

    /**
     * Writes the string to the file as a sequence of bytes. Each
     * character in the string is written out, in sequence, by discarding
     * its high eight bits. The write starts at the current position of
     * the file pointer.
     *
     * @param      s   a string of bytes to be written.
     * @exception  IOException  if an I/O error occurs.
     */
    @SuppressWarnings("deprecation")
    @Override
    public void writeBytes(String s)
            throws IOException
    {
        if ( closed )
        {
            throw new IOException( "already closed" );
        }

        int len = s.length();
        byte[] b = new byte[len];
        s.getBytes(0, len, b, 0);
        //writeBytes(b, 0, len);
        write(b, 0, len);
    }

    /**
     * Writes a string to the file as a sequence of characters. Each
     * character is written to the data output stream as if by the
     * {@code writeChar} method. The write starts at the current
     * position of the file pointer.
     *
     * @param      s   a {@code String} value to be written.
     * @exception  IOException  if an I/O error occurs.
     * @see        java.io.RandomAccessFile#writeChar(int)
     */
    @Override
    public void writeChars(String s)
            throws IOException
    {
        if ( closed )
        {
            throw new IOException( "already closed" );
        }

        int clen = s.length();
        int blen = 2*clen;
        byte[] b = new byte[blen];
        char[] c = new char[clen];
        s.getChars(0, clen, c, 0);
        for (int i = 0, j = 0; i < clen; i++) {
            b[j++] = (byte)(c[i] >>> 8);
            b[j++] = (byte)(c[i] >>> 0);
        }
        //writeBytes(b, 0, blen);
        write(b, 0, blen);
    }

    /**
     * Writes a string to the file using
     * <a href="DataInput.html#modified-utf-8">modified UTF-8</a>
     * encoding in a machine-independent manner.
     * <p>
     * First, two bytes are written to the file, starting at the
     * current file pointer, as if by the
     * {@code writeShort} method giving the number of bytes to
     * follow. This value is the number of bytes actually written out,
     * not the length of the string. Following the length, each character
     * of the string is output, in sequence, using the modified UTF-8 encoding
     * for each character.
     *
     * @param      str   a string to be written.
     * @exception  IOException  if an I/O error occurs.
     */
    @Override
    public void writeUTF(String str)
            throws IOException
    {
        if ( closed )
        {
            throw new IOException( "already closed" );
        }

        //DataOutputStream.writeUTF(str, this);
        final int strlen = str.length();
        int utflen = strlen; // optimized for ASCII

        for (int i = 0; i < strlen; i++) {
            int c = str.charAt(i);
            if (c >= 0x80 || c == 0)
                utflen += (c >= 0x800) ? 2 : 1;
        }

        if (utflen > 65535 || /* overflow */ utflen < strlen)
            throw new UTFDataFormatException(tooLongMsg(str, utflen));

        final byte[] bytearr;
        //if (/*out*/this instanceof DataOutputStream) {
        //    DataOutputStream dos = (DataOutputStream)/*out*/this;
        //    if (dos.bytearr == null || (dos.bytearr.length < (utflen + 2)))
        //        dos.bytearr = new byte[(utflen*2) + 2];
        //    bytearr = dos.bytearr;
        //} else {
            bytearr = new byte[utflen + 2];
        //}

        int count = 0;
        bytearr[count++] = (byte) ((utflen >>> 8) & 0xFF);
        bytearr[count++] = (byte) ((utflen >>> 0) & 0xFF);

        int i = 0;
        for (i = 0; i < strlen; i++) { // optimized for initial run of ASCII
            int c = str.charAt(i);
            if (c >= 0x80 || c == 0) break;
            bytearr[count++] = (byte) c;
        }

        for (; i < strlen; i++) {
            int c = str.charAt(i);
            if (c < 0x80 && c != 0) {
                bytearr[count++] = (byte) c;
            } else if (c >= 0x800) {
                bytearr[count++] = (byte) (0xE0 | ((c >> 12) & 0x0F));
                bytearr[count++] = (byte) (0x80 | ((c >>  6) & 0x3F));
                bytearr[count++] = (byte) (0x80 | ((c >>  0) & 0x3F));
            } else {
                bytearr[count++] = (byte) (0xC0 | ((c >>  6) & 0x1F));
                bytearr[count++] = (byte) (0x80 | ((c >>  0) & 0x3F));
            }
        }
        /*out*/this.write(bytearr, 0, utflen + 2);
        //return utflen + 2;
    }

    private static String tooLongMsg(String s, int bits32) {
        int slen = s.length();
        String head = s.substring(0, 8);
        String tail = s.substring(slen - 8, slen);
        // handle int overflow with max 3x expansion
        long actualLength = (long)slen + Integer.toUnsignedLong(bits32 - slen);
        return "encoded string (" + head + "..." + tail + ") too long: "
            + actualLength + " bytes";
    }

}
