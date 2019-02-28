package de.dailab.apppets.plib.access.handler;

/**
 * Created by arik on 03.07.2017.
 */

public interface IAccessHandler<T> {

    /**
     * Request the string representation of the data type.
     *
     * @return
     */
    String getDataTypeInfo();

    /**
     * Informs the caller if the data type is encryptable.
     *
     * @return true if possible, otherwise false.
     */
    boolean isEncryptAble();


    /**
     * Informs the caller if the data type is anonymizable.
     *
     * @return true if possible, otherwise false.
     */
    boolean isAnonymizeAble();

    /**
     * Informs the caller if the data type is pseudonymizable.
     *
     * @return true if possible, otherwise false.
     */
    boolean isPseudonymizeAble();

    /**
     * Defines if the data type is adaptable as a string
     *
     * @return true if possible, otherwise false.
     */
    boolean isStringAble();

    /**
     * Here the data is accessed and requested
     *
     * @return The requested data
     */
    T getRequestedData();

    /**
     * The defined data type.
     *
     * @return The data type
     */
    String getDataType();

    /**
     * Returns an anomyized form of the data type if possible, otherwise <code>null</code>
     *
     * @return
     */
    T getAnonymized();

    /**
     * Returns an encrypted form of the data type if possible, otherwise <code>null</code>
     *
     * @return
     */
    T getEncrypted();

    /**
     * Returns a pseudonymized form of the data type if possible, otherwise <code>null</code>.
     * Furthermore, it we can reconstruct the pseudonym if needed.
     *
     * @return
     */
    T getPseudonymized();


}
