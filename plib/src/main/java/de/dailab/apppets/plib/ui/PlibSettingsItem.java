package de.dailab.apppets.plib.ui;

/**
 * Created by arik on 27.02.2017.
 */

final class PlibSettingsItem {

    private String title = "";
    private String subTitle = "";
    private int iconLeft = -1;
    private int iconRight = -1;
    private int action = -1;
    private Object content = null;

    protected PlibSettingsItem(String title, String subTitle, int action, Integer iconLeft) {

        this.title = title;
        this.subTitle = subTitle;
        this.action = action;
        if (iconLeft != null) {
            this.iconLeft = iconLeft;
        }
    }

    protected PlibSettingsItem(String title, String subTitle, int action, Integer iconLeft,
                               Integer iconRight) {

        this.title = title;
        this.subTitle = subTitle;
        this.action = action;
        if (iconLeft != null) {
            this.iconLeft = iconLeft;
        }
        if (iconRight != null) {
            this.iconRight = iconRight;
        }
    }

    protected Object getContent() {

        return content;
    }

    protected void setContent(Object content) {

        this.content = content;
    }

    protected int getAction() {

        return action;
    }

    protected void setAction(int action) {

        this.action = action;
    }

    protected String getTitle() {

        return title;
    }

    protected void setTitle(String title) {

        this.title = title;
    }

    protected String getSubTitle() {

        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    protected int getIconLeft() {

        return iconLeft;
    }


    protected int getIconRight() {

        return iconRight;
    }


}
