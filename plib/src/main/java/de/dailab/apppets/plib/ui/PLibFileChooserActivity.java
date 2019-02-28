package de.dailab.apppets.plib.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import apppets.plib.R;
import de.dailab.apppets.plib.data.Constants;

/**
 * File chooser for selecting/storing a file.
 * <p>
 * Created by arik on 02.03.2017.
 */

final public class PLibFileChooserActivity extends FragmentActivity {

    /**
     * Constant ACCESS_READ
     */
    public static final int ACCESS_READ = 1;
    /**
     * Constant ACCESS_WRITE
     */
    public static final int ACCESS_WRITE = 2;


    /**
     * Key for title to show
     */
    public static final String KEY_INTENT_FILE_CHOOSER_TITLE = "KEY_INTENT_FILE_CHOOSER_TITLE";
    /**
     * Key for sub title to show
     */
    public static final String KEY_INTENT_FILE_CHOOSER_SUB_TITLE
            = "KEY_INTENT_FILE_CHOOSER_SUB_TITLE";
    /**
     * Key for intention to read or to write a file
     */
    public static final String KEY_INTENT_FILE_CHOOSER_READ_OR_WRITE
            = "KEY_INTENT_FILE_CHOOSER_READ_OR_WRITE";
    /**
     * Key for data to export/import
     */
    public static final String KEY_INTENT_FILE_CHOOSER_DATA
            = "KEY_INTENT_FILE_CHOOSER_DATA";
    /**
     * Key for selected path
     */
    public static final String KEY_INTENT_FROM_FILECHOSER_SELECTED_PATH
            = "KEY_INTENT_FROM_FILECHOOSER_SELECTED_PATH";
    /**
     * Key for mode
     */
    public static final String KEY_INTENT_FROM_FILECHOSER_MODE = "KEY_INTENT_FROM_FILECHOSER_MODE";
    /**
     * Key for master key
     */
    public static final String KEY_INTENT_FROM_FILECHOSER_MASTER_KEY
            = "KEY_INTENT_FROM_FILECHOSER_MASTER_KEY";

    private TextView tvMain, tvSubMain, tvPath;
    private ImageView ivUp, ivAdd, ivSort;
    private ListView lv;

    private String theData = null;
    private int mode = -1;
    private boolean read = true;

    private PLibFileChooserAdapter fileAdapter = null;
    private File currentFolder;
    private long time = 0;
    private int sortMethod = 1;
    private SharedPreferences prefs;

    private Comparator<? super File> comparatorSort = new Comparator<File>() {

        @Override
        public int compare(File l, File r) {

            if (sortMethod == 1) {// sort by name
                return l.getName().compareToIgnoreCase(r.getName());
            }
            if (sortMethod == 2) {// sort by last modification
                long lm = l.lastModified();
                long rm = r.lastModified();
                int i = Long.compare(lm, rm);
                if (i != 0) {
                    return i;
                }
                return l.getName().compareToIgnoreCase(r.getName());
            }
            if (sortMethod == 3) {// sort by size
                long lm = l.length();
                long rm = r.length();
                int i = Long.compare(lm, rm);
                if (i != 0) {
                    return i;
                }
                return l.getName().compareToIgnoreCase(r.getName());
            }
            return l.getName().compareToIgnoreCase(r.getName());
        }
    };


    private OnClickListener cl = new OnClickListener() {

        @Override
        public void onClick(View v) {

            int i = v.getId();
            if (i == R.id.up) {
                if (currentFolder != null) {
                    File parent = currentFolder.getParentFile();
                    if (parent != null) {
                        currentFolder = parent;
                    }
                    tvPath.setText(currentFolder.getAbsolutePath());
                    currentFolder = new File(currentFolder.getAbsolutePath());
                    fileAdapter = new PLibFileChooserAdapter(getApplicationContext(), R.id.listview,
                            new ArrayList<File>());
                    lv.setAdapter(fileAdapter);
                    handleCurrentFolder();
                }

            } else {
                if (i == R.id.add && !read) {
                    askForNewFile();

                } else {
                    if (i == R.id.sort) {
                        askForSortMethod(v);

                    }
                }
            }
        }
    };


    private void askForSortMethod(View v) {

        PopupMenu popup = new PopupMenu(PLibFileChooserActivity.this, v);
        popup.getMenuInflater().inflate(R.menu.plib_menu_fc_resort, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {

                int itemId = item.getItemId();
                int approach = 0;
                if (itemId == R.id.sortAlphabetical) {
                    approach = 1;

                } else {
                    if (itemId == R.id.sortLastModified) {
                        approach = 2;

                    } else {
                        if (itemId == R.id.sortSize) {
                            approach = 3;

                        }
                    }
                }
                if (approach == 0) {
                    return true;
                }
                if (approach == sortMethod) {
                    toast(R.string.theplib_files_already_sorted);
                    return true;
                }
                sortMethod = approach;
                handleCurrentFolder();
                return true;
            }
        });

        popup.show();
    }

    private void askForNewFile() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(PLibFileChooserActivity.this);
        alertDialog.setTitle(R.string.theplib_export);
        alertDialog.setMessage(R.string.theplib_fc_please_enter_file_name);

        final EditText input = new EditText(PLibFileChooserActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);
        alertDialog.setIcon(R.mipmap.apppets);

        alertDialog.setPositiveButton(R.string.theplib_ok, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {

                String content = input.getText().toString();
                selectNewFile(content);

            }
        });

        alertDialog
                .setNegativeButton(R.string.theplib_cancel, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }

    private void selectNewFile(String fileName) {

        if (fileName.equals("")) {
            toast(getString(R.string.theplib_cant_create_file_illegal_name));
            return;
        }
        final File f = new File(currentFolder, fileName);
        if (f.exists() && f.isDirectory()) {
            toast(getString(R.string.theplib_cant_create_file_folder_available));
        } else {
            if (f.exists()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PLibFileChooserActivity.this);
                builder.setMessage(
                        getString(R.string.theplib_fc_warning_overide_file2) + " (" + fileName +
                                ")").setIcon(R.mipmap.apppets).setTitle(R.string.theplib_export)
                        .setPositiveButton(R.string.theplib_ok,
                                new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int id) {

                                        dialog.cancel();
                                        setSelectedPathAndFinish(f);
                                    }
                                }).setNegativeButton(R.string.theplib_cancel,
                        new DialogInterface.OnClickListener() {

                            public void onClick(
                                    DialogInterface dialog,
                                    int id) {

                                dialog.cancel();
                            }
                        });
                builder.show();
            } else {
                setSelectedPathAndFinish(f);
            }
        }
    }

    private void toast(String msg) {

        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private void setSelectedPathAndFinish(File f) {

        String selectedPath = f.getAbsolutePath();
        Intent resultIntent = new Intent();
        resultIntent.putExtra(KEY_INTENT_FROM_FILECHOSER_SELECTED_PATH, selectedPath);
        resultIntent.putExtra(KEY_INTENT_FROM_FILECHOSER_MODE, mode);
        if (mode == ACCESS_WRITE && theData != null) {
            resultIntent.putExtra(KEY_INTENT_FROM_FILECHOSER_MASTER_KEY, theData);
        }
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public void onBackPressed() {

        long now = new Date().getTime();
        long diff = now - time;
        time = now;
        if (diff < 2000) {
            finish();
        } else {
            toast(R.string.theplib_fc_press_again_to_leave);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.plib_activity_fc);

        Intent intent = getIntent();
        String title = intent.getStringExtra(KEY_INTENT_FILE_CHOOSER_TITLE);
        String subTitle = intent.getStringExtra(KEY_INTENT_FILE_CHOOSER_SUB_TITLE);
        mode = intent.getIntExtra(KEY_INTENT_FILE_CHOOSER_READ_OR_WRITE, -1);
        theData = intent.getStringExtra(KEY_INTENT_FILE_CHOOSER_DATA);
        if (title == null) {
            title = "";
        }
        if (subTitle == null) {
            subTitle = "";
        }
        if (mode != ACCESS_READ && mode != ACCESS_WRITE) {
            Toast.makeText(getApplicationContext(), R.string.theplib_fc_illegal_parameters,
                    Toast.LENGTH_LONG).show();
            setResult(RESULT_CANCELED);
            finish();
            return;
        }

        tvMain = findViewById(R.id.main_title);
        tvSubMain = findViewById(R.id.sub_title);
        tvPath = findViewById(R.id.list_title);
        ivUp = findViewById(R.id.up);
        ivAdd = findViewById(R.id.add);
        ivSort = findViewById(R.id.sort);
        lv = findViewById(R.id.listview);
        if (mode == ACCESS_WRITE) {
            read = false;
            ivAdd.setVisibility(View.VISIBLE);
            if (theData == null) {
                Toast.makeText(getApplicationContext(), R.string.theplib_fc_illegal_parameters,
                        Toast.LENGTH_LONG).show();
                setResult(RESULT_CANCELED);
                finish();
                return;
            }
        } else {
            ivAdd.setVisibility(View.GONE);
        }
        tvMain.setText(title);
        tvSubMain.setText(subTitle);

        ivUp.setOnClickListener(cl);
        ivAdd.setOnClickListener(cl);
        ivSort.setOnClickListener(cl);

        tvMain.setSelected(true);
        tvSubMain.setSelected(true);

        prefs = getSharedPreferences(Constants.PREF_NAME_FILE_CHOOSER, Context.MODE_PRIVATE);
        sortMethod = prefs.getInt(Constants.PREF_KEY_FILE_CHOOSER_SORT, 1);

        String startPath = Environment.getExternalStorageDirectory().getAbsolutePath();

        tvPath.setText(startPath);
        currentFolder = new File(startPath);

        fileAdapter = new PLibFileChooserAdapter(getApplicationContext(), R.id.listview,
                new ArrayList<File>());
        lv.setAdapter(fileAdapter);
        time = new Date().getTime();
        handleCurrentFolder();
    }

    private void toast(int id) {

        Toast.makeText(getApplicationContext(), id, Toast.LENGTH_SHORT).show();
    }

    private void handleCurrentFolder() {

        if (currentFolder == null) {
            currentFolder = Environment.getExternalStorageDirectory();
        }
        tvPath.setText(currentFolder.getAbsolutePath());
        tvPath.setSelected(true);

        List<File> list = new ArrayList<>();
        if (currentFolder != null) {
            File[] files = currentFolder.listFiles();
            if (files != null) {
                list.addAll(Arrays.asList(files));
            }
        }
        list = sort(list);
        lv.setAdapter(new PLibFileChooserAdapter(getApplicationContext(), R.id.listview, list));
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> a, final View v, int position, long id) {

                Object o = lv.getItemAtPosition(position);
                final File theFile = (File) o;
                if (theFile != null) {
                    clickFile(theFile, v);
                }

            }
        });

        if (mode == ACCESS_WRITE) {
            lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View v, int position,
                                               long id) {

                    Object o = lv.getItemAtPosition(position);
                    final File theFile = (File) o;
                    if (theFile != null) {
                        longClickFile(theFile, v);
                    }
                    return true;
                }


            });
        }

    }

    private List<File> sort(List<File> list) {

        if (list == null || list.isEmpty()) {
            return list;
        }
        ArrayList<File> res = new ArrayList<>();
        ArrayList<File> dirs = new ArrayList<>();
        ArrayList<File> files = new ArrayList<>();
        while (!list.isEmpty()) {
            File f = list.remove(0);
            if (f.isDirectory()) {
                dirs.add(f);
            } else {
                files.add(f);
            }
        }
        Collections.sort(dirs, comparatorSort);
        Collections.sort(files, comparatorSort);
        res.addAll(dirs);
        dirs.clear();
        res.addAll(files);
        files.clear();
        return res;
    }

    private void longClickFile(final File f, View v) {

        if (f == null || f.isDirectory()) {
            return;
        }

    }

    private void clickFile(final File f, View v) {

        if (f == null) {
            return;
        }
        if (f.isDirectory()) {
            currentFolder = new File(f.getPath());
            tvPath.setText(currentFolder.getAbsolutePath());
            currentFolder = new File(currentFolder.getAbsolutePath());
            fileAdapter = new PLibFileChooserAdapter(getApplicationContext(), R.id.listview,
                    new ArrayList<File>());
            lv.setAdapter(fileAdapter);
            handleCurrentFolder();
            return;
        }
        if (f.isFile() && read) {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(
                    PLibFileChooserActivity.this);
            builder.setIcon(R.mipmap.apppets);
            builder.setMessage(getString(R.string.theplib_import_sure) + " (" + f.getName() + ")");
            builder.setCancelable(true);
            builder.setTitle(R.string.theplib_import);

            builder.setPositiveButton(R.string.theplib_yes, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int id) {

                    dialog.cancel();
                    setSelectedPathAndFinish(f);
                }
            });

            builder.setNegativeButton(R.string.theplib_no, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int id) {

                    dialog.cancel();
                }
            });


            builder.show();
        }


    }


}
