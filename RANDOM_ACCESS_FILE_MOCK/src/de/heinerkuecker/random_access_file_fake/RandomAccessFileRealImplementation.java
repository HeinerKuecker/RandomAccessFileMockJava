package de.heinerkuecker.random_access_file_fake;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;

/**
 * Extension of {@link RandomAccessFile} with interface.
 * <br/><br/>
 * Implementation of {@link RandomAccessFileInterface}.
 */
public class RandomAccessFileRealImplementation
extends RandomAccessFile
implements RandomAccessFileInterface
{

    /**
     * Constructor.
     *
     * @param file
     * @param mode
     * @throws FileNotFoundException
     */
    public RandomAccessFileRealImplementation(
            final File file,
            final String mode)
            throws FileNotFoundException
    {
        super(file, mode);
    }

    /**
     * Constructor.
     *
     * @param name
     * @param mode
     * @throws FileNotFoundException
     */
    public RandomAccessFileRealImplementation(
            final String name,
            final String mode)
            throws FileNotFoundException
    {
        super(name, mode);
    }

}
