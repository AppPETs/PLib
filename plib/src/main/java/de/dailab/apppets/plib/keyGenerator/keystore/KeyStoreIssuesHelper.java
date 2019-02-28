package de.dailab.apppets.plib.keyGenerator.keystore;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by arik on 12.06.2017.
 */

class KeyStoreIssuesHelper {

    protected static String getNewAlias(KeyStore keyStore) {

        if (keyStore == null) {
            return null;
        }
        Enumeration<String> aliasesEnum = null;
        String alias = "" + new Date().getTime();
        try {
            aliasesEnum = keyStore.aliases();
        } catch (KeyStoreException e) {
            e.printStackTrace();
            return null;
        }
        List<String> aliases = new ArrayList<>();
        while (aliasesEnum.hasMoreElements()) {
            aliases.add(aliasesEnum.nextElement());
        }
        while (true) {
            if (aliases.contains(alias)) {
                alias = "" + new Date().getTime();
            } else {
                break;
            }
        }
        return alias;
    }


}
