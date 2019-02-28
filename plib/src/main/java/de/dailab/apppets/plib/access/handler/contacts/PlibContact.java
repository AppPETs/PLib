package de.dailab.apppets.plib.access.handler.contacts;

import android.net.Uri;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

/**
 * Created by arik on 05.07.2017.
 */

public class PlibContact {

    private boolean selected = false;
    private String name = null;
    private String number = null;
    private Uri url = null;
    private Properties properties = null;
    private boolean toStringWithEverything = false;

    public Properties getProperties() {

        return properties;
    }

    public void setProperties(Properties properties) {

        this.properties = properties;
    }

    @Override
    public String toString() {

        StringBuffer s = new StringBuffer();
        s.append(name + ", " + number);

        if (toStringWithEverything) {
            s.append(" [");

            if (properties != null) {
                List<Object> keys = new ArrayList<>();
                keys.addAll(properties.keySet());
                Collections.sort(keys, new Comparator<Object>() {

                    @Override
                    public int compare(Object o1, Object o2) {

                        return ("" + o1).compareToIgnoreCase(("" + o2));
                    }
                });
                for (int i = 0; i < keys.size(); i++) {
                    Object k = keys.get(i);
                    Object content = properties.get(k);
                    if (i > 0) {
                        s.append(", ");
                    }
                    s.append(k + ": " + content);
                }
            }
            s.append("]");
        }
        return s.toString();
    }

    public boolean isSelected() {

        return selected;
    }

    public void setSelected(boolean selected) {

        this.selected = selected;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public String getNumber() {

        return number;
    }

    public void setNumber(String number) {

        this.number = number;
    }

    public Uri getUrl() {

        return url;
    }

    public void setUrl(Uri url) {

        this.url = url;
    }

}
