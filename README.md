[<img src="https://app-pets.org/img/AppPETS-Logo-de.png">](https://app-pets.org/home/)

# Privacy Library PLib


The __PLib__ is a framework for Android that provides functionality to handle personal information appropriately.

- Repository: https://github.com/AppPETs/PLib
- Example-App: https://github.com/AppPETs/<>
- Issues: https://github.com/AppPETs/PLib/issues

Several technologies can be used to enhance privacy, also known as privacy-enhancing technologies (PETs). Many PETs are known in research, but are not easily available to developers. The goal of this project is to make PETs accessible to app developers. The following functionality has been implemented in a way that it can be easily used by application developers.

## Functionality

The Privacy Library __PLib__ grants users full control over their data and provides possibilities of encoding, anonymizing and pseudomizing all data exchange. With the intuitive UI provided by this library, 
the users are able view and modify their data security settings at any given time. On the other side this library is also designed to support novice Android developers who want to implement advanced 
data and privacy security to their applications/projects.
For each user the __PLib__ creates a Master-key which is then used to generate all other keys, seeds, etc. in a deterministic way. It's also possible to import and export the Master-key!

## Prerequisites

* Ideally Android Studio (This library can of course be used with other IDEs)
* minSdkVersion 21
* [libsodium-jni](https://github.com/joshjdevl/libsodium-jni)

## Getting Started

You can either download the library directly from github or clone it with the following command:

```
git clone https://github.com/AppPETs/PLib
``` 

To use the Privacy library's code in your app module, proceed as follows in Android Studio:

#### __1. Import the library module to your project (the library source becomes part of your project):__

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;a. Click __File > New > Import Module__.

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;b. Enter the location of the library module directory then click __Finish__.

This way the library module is copied to your project, so you can actually view and if needed even edit the library code.  
The next step should be done automatically by Android Studio, but make sure everything's configured adequately.

#### __2. Add PLib to your `settings.gradle` file__


Make sure the PLib is listed at the top of your `settings.gradle` file, as shown here:

```gradle
include ':app', ':plib'
project(':plib').projectDir = new File('../Android_PLib/plib')
```

#### __3. Add PLib dependency__

Add a new line to the dependencies block in the app module's `build.gradle` file:

```gradle
dependencies {
    implementation project(":plib")
}
```

This can also be done by right-clicking __app__ in the Project view and choosing __Open Module Settings__.  
Next, use the __Dependencies__ tab and click on the __+__ and select 
__Module dependency__.  
Choose __plib__ and finish the process by clicking __Next__.

#### __4. Add flatDir__

In your projects `build.gradle` file, add the following code snippet to the __allprojects__ block:

```gradle
flatDir {
    dirs 'src/main/libs'
}
``` 

#### __5. Add libsodium-jni-debug.aar to your app's libs file__

Copy the __libsodium-jni-debug.aar__ file from the plib folder `src/main/libs` to your app's libs folder.  
This step will be fixed in the future, so it won't be necessary. But for now it's required.

## Implementation

#### PLib-UI

Implementing the PLib-UI is straigthforward by using `PLibApiUi.showPlibUi(MainActivity.this, null)`. The example below uses a `MenuItem` to let the users access the PLib-UI.  

```java
import de.dailab.apppets.plib.api.PLibApiUi;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_plib:
                PLibApiUi.showPlibUi(MainActivity.this, null);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
```
Make sure to place the entry-point for the PLib-UI visible and reachable at any time for the user.

#### Access Requests

The PLib let's you easily inform users when your app is about access their personal data. In that case, the user is prompted to
allow or deny the access. The code below shows an exemplary `AndroidID` access. The user's decision will also be saved by the PLib
to avoid repeated requests. The class `PLibGrantAccess` contains many more methods which can be used to access different sorts of 
private data.

```java
    public static void getAndroidId(final Activity activity, final EditText tv,
                                    final boolean send) {
        PLibAccessCallback<String> cb = new PLibAccessCallback<String>() {
            @Override
            public void grantedData(String grantedData) {
                tv.setText(grantedData == null ? "NULL" : grantedData);
                if (send) {
                    //Send AndroidID
                }
            }
        };
        PLibGrantAccess.getAndroidId(activity, "Please grant access to the android id", cb, true);
    }
```

---