package de.dailab.apppets.plib.access.handler;

import android.content.Context;
import android.util.Base64;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.Set;

import de.dailab.apppets.plib.access.handler.contacts.PlibContact;
import de.dailab.apppets.plib.common.PLibCommon;
import de.dailab.apppets.plib.crypt.PLibCrypt;
import de.dailab.apppets.plib.crypt.PLibSimpleStringAnonymizer;
import de.dailab.apppets.plib.random.SecureSecureRandom;
import de.dailab.apppets.plib.stuff.wrapper.PhoneNumber;

/**
 * Created by arik on 04.07.2017.
 */

public class ContactsHandler extends PLibAbstractAccessHandler {

    private List<PlibContact> contacts;

    public ContactsHandler(Context context, List<PlibContact> contacts) {

        super(context, CONTACTS);
        this.contacts = contacts;
        setAnonymizable(true);
        setEncryptable(true);
        setPseudonymizable(true);
        setStringable(true);
    }

    @Override
    public List<PlibContact> getRequestedData() {
        return contacts;
    }

    @Override
    public List<PlibContact> getAnonymized() {

        List<PlibContact> contactsAno = new ArrayList<>();
        PLibSimpleStringAnonymizer anonText = new PLibSimpleStringAnonymizer(
                PLibSimpleStringAnonymizer.TYPE_TEXT);
        PLibSimpleStringAnonymizer anonNumber = new PLibSimpleStringAnonymizer(
                PLibSimpleStringAnonymizer.TYPE_NUMBERS);
        PLibSimpleStringAnonymizer anonAll = new PLibSimpleStringAnonymizer(
                PLibSimpleStringAnonymizer.TYPE_TEXT_AND_DIGITS);
        Random r = SecureSecureRandom.get();

        for (PlibContact pc : contacts) {
            PlibContact pca = new PlibContact();
            pca.setName(anonText.nextString(1 + r.nextInt(19)));
            pca.setNumber(anonNumber.nextString(3 + r.nextInt(17)));
            Properties p = pc.getProperties();
            Properties pnew = new Properties();
            pca.setProperties(pnew);
            Set<Object> keys = p.keySet();
            for (Object k : keys) {
                pnew.put(k, anonAll.nextString(15));
            }
            contactsAno.add(pca);
        }
        return contactsAno;
    }

    @Override
    public Object getEncrypted() {

        List<PlibContact> contactsAno = new ArrayList<>();
        for (PlibContact pc : contacts) {
            PlibContact pca = new PlibContact();
            String name = pc.getName();
            name = PLibCrypt.encryptString(context, name);
            pca.setName(name);
            String phoneNumber = pc.getNumber();
            phoneNumber = PLibCrypt.encryptString(context, phoneNumber);
            pca.setNumber(phoneNumber);

            Properties p = pc.getProperties();
            Properties pnew = new Properties();
            pca.setProperties(pnew);
            Set<Object> keys = p.keySet();
            for (Object k : keys) {
                pnew.put(k, PLibCrypt.encryptString(context, (String) p.get(k)));
            }
            contactsAno.add(pca);
        }
        return contactsAno;
    }

    @Override
    public Object getPseudonymized() {

        List<PlibContact> contactsAno = new ArrayList<>();

        for (PlibContact pc : contacts) {
            PlibContact pca = new PlibContact();
            String name = pc.getName();
            byte[] bytesName = PLibCommon.getUTFBytes(name);
            bytesName = PLibCrypt.pseudonymizeBytes(context, bytesName);
            pca.setName(Base64.encodeToString(bytesName, Base64.NO_WRAP));
            PhoneNumber phoneNumber = new PhoneNumber(pc.getNumber());
            BigInteger temp = phoneNumber.getCleanedPhoneNumber();

            if (temp != null) {
                byte[] bytesNum = temp.toByteArray();
                bytesNum = PLibCrypt.pseudonymizeBytes(context, bytesNum);
                temp = new BigInteger(bytesNum);
            } else {
                temp = BigInteger.ZERO;
            }

            phoneNumber.setCleanedPhoneNumber(temp);
            pca.setNumber(phoneNumber.toString());
            Properties p = pc.getProperties();
            Properties pnew = new Properties();
            pca.setProperties(pnew);
            Set<Object> keys = p.keySet();

            for (Object k : keys) {
                Object o = p.get(k);
                if (o != null) {
                    String s = (String) o;
                    byte[] b = PLibCommon.getUTFBytes(s);
                    b = PLibCrypt.pseudonymizeBytes(context, b);
                    pnew.put(k, Base64.encodeToString(b, Base64.NO_WRAP));
                }


            }
            contactsAno.add(pca);
        }
        return contactsAno;
    }

    @Override
    public String getDataType() {

        StringBuffer sb = new StringBuffer();
        sb.append(super.getDataType()).append("\n");
        for (PlibContact pc : contacts) {
            sb.append(pc).append("\n");
        }
        return sb.toString();
    }
}

