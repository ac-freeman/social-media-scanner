package com.acfreeman.socialmediascanner.AzureDB;


import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.http.NextServiceFilterCallback;
import com.microsoft.windowsazure.mobileservices.http.OkHttpClientFactory;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilter;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterRequest;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.query.Query;
import com.microsoft.windowsazure.mobileservices.table.query.QueryOperations;
import com.microsoft.windowsazure.mobileservices.table.sync.MobileServiceSyncContext;
import com.microsoft.windowsazure.mobileservices.table.sync.MobileServiceSyncTable;
import com.microsoft.windowsazure.mobileservices.table.sync.localstore.ColumnDataType;
import com.microsoft.windowsazure.mobileservices.table.sync.localstore.MobileServiceLocalStoreException;
import com.microsoft.windowsazure.mobileservices.table.sync.localstore.SQLiteLocalStore;
import com.microsoft.windowsazure.mobileservices.table.sync.synchandler.SimpleSyncHandler;
import com.squareup.okhttp.OkHttpClient;

import static com.microsoft.windowsazure.mobileservices.table.query.QueryOperations.*;
import static com.microsoft.windowsazure.mobileservices.table.query.QueryOperations.val;
/**
 * Created by jianziyu on 2017/11/13.
 */
public class AzureDB extends Activity {

    /**
     * Mobile Service Client reference
     */
    private MobileServiceClient mClient;

    /**
     * Mobile Service Table used to access data
     */
    private MobileServiceTable<CONTACTS> mCONTACTSTable;
    private MobileServiceTable<EMAILS> mEMAILSTable;
    private MobileServiceTable<Owner> mOwnerTable;
    private MobileServiceTable<PHONES> mPHONESTable;
    private MobileServiceTable<SOCIAL> mSOCIALTable;

    //Offline Sync
    /**
     * Mobile Service Table used to access and Sync data
     */
    //private MobileServiceSyncTable<ToDoItem> mToDoTable;

    /**
     * Adapter to sync the items list with the view
     */
    private CONTACTSAdapter mCONTACTSAdapter;
    private EMAILSAdapter mEMAILSAdapter;
    private OwnerAdapter mOwnerAdapter;
    private PHONESAdapter mPHONESAdapter;
    private SOCIALAdapter mSOCIALAdapter;

    /**
     * EditText containing the "New To Do" text
     */
    private EditText mTextNewCONTACTS;
    private EditText mTextNewEMAILS;
    private EditText mTextNewEmailType;
    private EditText mTextNewOwner;
    private EditText mTextNewPHONES;
    private EditText mTextNewPhoneType;
    private EditText mTextNewSOCIAL;
    private EditText mTextNewSocialType;

    /**
     * Progress spinner to use for table operations
     */
    private ProgressBar mProgressBar;

    /**
     * Initializes the activity
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressBar = (ProgressBar) findViewById(R.id.loadingProgressBar);

        // Initialize the progress bar
        mProgressBar.setVisibility(ProgressBar.GONE);

        try {
            // Create the Mobile Service Client instance, using the provided

            // Mobile Service URL and key
            mClient = new MobileServiceClient(
                    "https://socialmediascanner.azurewebsites.net",
                    this).withFilter(new ProgressFilter());

            // Extend timeout from default of 10s to 20s
            mClient.setAndroidHttpClientFactory(new OkHttpClientFactory() {
                @Override
                public OkHttpClient createOkHttpClient() {
                    OkHttpClient client = new OkHttpClient();
                    client.setReadTimeout(20, TimeUnit.SECONDS);
                    client.setWriteTimeout(20, TimeUnit.SECONDS);
                    return client;
                }
            });

            // Get the Mobile Service Table instance to use

            mCONTACTSTable = mClient.getTable(CONTACTS.class);
            mEMAILSTable = mClient.getTable(EMAILS.class);
            mOwnerTable = mClient.getTable(Owner.class);
            mPHONESTable = mClient.getTable(PHONES.class);
            mSOCIALTable = mClient.getTable(SOCIAL.class);

            // Offline Sync
            //mToDoTable = mClient.getSyncTable("ToDoItem", ToDoItem.class);

            //Init local storage
            initLocalStore().get();

            mTextNewCONTACTS = (EditText) findViewById(R.id.textNewCONTACTS);
            mTextNewEMAILS = (EditText) findViewById(R.id.textNewEMAILS);
            mTextNewOwner = (EditText) findViewById(R.id.textNewOwner);
            mTextNewPHONES = (EditText) findViewById(R.id.textNewPHONES);
            mTextNewSOCIAL = (EditText) findViewById(R.id.textNewSOCIAL);

            // Create an adapter to bind the items with the view !!!! Layout source id needs
            mCONTACTSAdapter = new CONTACTSAdapter(this, R.layout.row_list_to_do);
            ListView listViewCONTACT = (ListView) findViewById(R.id.listViewCONTACT);
            listViewCONTACT.setAdapter(mCONTACTSAdapter);

            mEMAILSAdapter = new EMAILSAdapter(this, R.layout.row_list_to_do);
            ListView listViewEMAILS = (ListView) findViewById(R.id.listViewEMAILS);
            listViewEMAILS.setAdapter(mEMAILSAdapter);

            mOwnerAdapter = new OwnerAdapter(this, R.layout.row_list_to_do);
            ListView listViewOwner = (ListView) findViewById(R.id.listViewOwner);
            listViewOwner.setAdapter(mOwnerAdapter);

            mPHONESAdapter = new PHONESAdapter(this, R.layout.row_list_to_do);
            ListView listViewPHONES = (ListView) findViewById(R.id.listViewPHONES);
            listViewPHONES.setAdapter(mPHONESAdapter);

            mSOCIALAdapter = new SOCIALAdapter(this, R.layout.row_list_to_do);
            ListView listViewSOCIAL = (ListView) findViewById(R.id.listViewSOCIAL);
            listViewSOCIAL.setAdapter(mSOCIALAdapter);


            // Load the items from the Mobile Service
            refreshItemsFromTable();

        } catch (MalformedURLException e) {
            createAndShowDialog(new Exception("There was an error creating the Mobile Service. Verify the URL"), "Error");
        } catch (Exception e){
            createAndShowDialog(e, "Error");
        }
    }

    /**
     * Initializes the activity menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    /**
     * Select an option from the menu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_refresh) {
            refreshItemsFromTable();
        }

        return true;
    }

    /**
     * Mark an item as completed
     *
     * @param owner
     *            The item to mark
     */
    public void checkOwner(final Owner item) {
        if (mClient == null) {
            return;
        }

        // Set the item as completed and update it in the table
        item.setComplete(true);

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {

                    checkOwnerInTable(item);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (item.isComplete()) {
                                mOwnerAdapter.remove(item);
                            }
                        }
                    });
                } catch (final Exception e) {
                    createAndShowDialogFromTask(e, "Error");
                }

                return null;
            }
        };

        runAsyncTask(task);

    }

    /**
     * Mark an item as completed
     *
     * @param item
     *            The item to mark
     */
    public void checkCONTACTS(final CONTACTS item) {
        if (mClient == null) {
            return;
        }

        // Set the item as completed and update it in the table
        item.setComplete(true);

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {

                    checkCONTACTSInTable(item);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (item.isComplete()) {
                                mCONTACTSAdapter.remove(item);
                            }
                        }
                    });
                } catch (final Exception e) {
                    createAndShowDialogFromTask(e, "Error");
                }

                return null;
            }
        };

        runAsyncTask(task);

    }

    /**
     * Mark an item as completed
     *
     * @param item
     *            The item to mark
     */
    public void checkEMAILS(final EMAILS item) {
        if (mClient == null) {
            return;
        }

        // Set the item as completed and update it in the table
        item.setComplete(true);

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {

                    checkEMAILSInTable(item);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (item.isComplete()) {
                                mEMAILSAdapter.remove(item);
                            }
                        }
                    });
                } catch (final Exception e) {
                    createAndShowDialogFromTask(e, "Error");
                }

                return null;
            }
        };

        runAsyncTask(task);

    }

    /**
     * Mark an item as completed
     *
     * @param item
     *            The item to mark
     */
    public void checkPHONES(final PHONES item) {
        if (mClient == null) {
            return;
        }

        // Set the item as completed and update it in the table
        item.setComplete(true);

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {

                    checkPHONESInTable(item);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (item.isComplete()) {
                                mPHONESAdapter.remove(item);
                            }
                        }
                    });
                } catch (final Exception e) {
                    createAndShowDialogFromTask(e, "Error");
                }

                return null;
            }
        };

        runAsyncTask(task);

    }

    /**
     * Mark an item as completed
     *
     * @param item
     *            The item to mark
     */
    public void checkSOCIAL(final SOCIAL item) {
        if (mClient == null) {
            return;
        }

        // Set the item as completed and update it in the table
        item.setComplete(true);

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {

                    checkSOCIALInTable(item);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (item.isComplete()) {
                                mSOCIALAdapter.remove(item);
                            }
                        }
                    });
                } catch (final Exception e) {
                    createAndShowDialogFromTask(e, "Error");
                }

                return null;
            }
        };

        runAsyncTask(task);

    }

    /**
     * Mark an item as completed in the Mobile Service Table
     *
     * @param item
     *            The item to mark
     */
    public void checkCONTACTSInTable(CONTACTS item) throws ExecutionException, InterruptedException {
        mCONTACTSTable.update(item).get();
    }
    public void checkEMAILSInTable(EMAILS item) throws ExecutionException, InterruptedException {
        mEMAILSTable.update(item).get();
    }
    public void checkOwnerInTable(Owner item) throws ExecutionException, InterruptedException {
        mOwnerTable.update(item).get();
    }
    public void checkPHONESInTable(PHONES item) throws ExecutionException, InterruptedException {
        mPHONESTable.update(item).get();
    }
    public void checkSOCIALInTable(SOCIAL item) throws ExecutionException, InterruptedException {
        mSOCIALTable.update(item).get();
    }

    /**
     * Add a new item
     *
     * @param view
     *            The view that originated the call
     */
    public void addCONTACTS(View view) {
        if (mClient == null) {
            return;
        }

        // Create a new item
        final CONTACTS contacts = new CONTACTS();

        contacts.setName(mTextNewCONTACTS.getText().toString());
        contacts.setComplete(false);

        // Insert the new item
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    final CONTACTS entity = addCONTACTSInTable(contacts);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(!entity.isComplete()){
                                mCONTACTSAdapter.add(entity);
                            }
                        }
                    });
                } catch (final Exception e) {
                    createAndShowDialogFromTask(e, "Error");
                }
                return null;
            }
        };

        runAsyncTask(task);

        mTextNewCONTACTS.setText("");
    }


    public void addEMAILS(View view) {
        if (mClient == null) {
            return;
        }

        // Create a new item
        final EMAILS emails = new EMAILS();

        emails.setEmail(mTextNewEMAILS.getText().toString());
        emails.setEmail_type(mTextNewEmailType.getText().toString());
        emails.setComplete(false);

        // Insert the new item
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    final EMAILS entity = addEMAILSInTable(emails);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(!entity.isComplete()){
                                mEMAILSAdapter.add(entity);
                            }
                        }
                    });
                } catch (final Exception e) {
                    createAndShowDialogFromTask(e, "Error");
                }
                return null;
            }
        };

        runAsyncTask(task);

        mTextNewEMAILS.setText("");
        mTextNewEmailType.setText("");
    }

    public void addOwner(View view) {
        if (mClient == null) {
            return;
        }

        // Create a new item
        final Owner item = new Owner();

        item.setName(mTextNewOwner.getText().toString());
        item.setComplete(false);

        // Insert the new item
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    final Owner entity = addOwnerInTable(item);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(!entity.isComplete()){
                                mOwnerAdapter.add(entity);
                            }
                        }
                    });
                } catch (final Exception e) {
                    createAndShowDialogFromTask(e, "Error");
                }
                return null;
            }
        };

        runAsyncTask(task);

        mTextNewOwner.setText("");
    }

    public void addPHONES(View view) {
        if (mClient == null) {
            return;
        }

        // Create a new item
        final PHONES item = new PHONES();

        item.setNumber(mTextNewPHONES.getText().toString());
        item.setComplete(false);

        // Insert the new item
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    final PHONES entity = addPHONESInTable(item);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(!entity.isComplete()){
                                mPHONESAdapter.add(entity);
                            }
                        }
                    });
                } catch (final Exception e) {
                    createAndShowDialogFromTask(e, "Error");
                }
                return null;
            }
        };

        runAsyncTask(task);

        mTextNewPHONES.setText("");
        mTextNewPhoneType.setText("");
    }

    public void addSOCIAL(View view) {
        if (mClient == null) {
            return;
        }

        // Create a new item
        final SOCIAL item = new SOCIAL();

        item.setUsername(mTextNewSOCIAL.getText().toString());
        item.setComplete(false);

        // Insert the new item
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    final SOCIAL entity = addSOCIALInTable(item);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(!entity.isComplete()){
                                mSOCIALAdapter.add(entity);
                            }
                        }
                    });
                } catch (final Exception e) {
                    createAndShowDialogFromTask(e, "Error");
                }
                return null;
            }
        };

        runAsyncTask(task);

        mTextNewSOCIAL.setText("");
        mTextNewSocialType.setText("");
    }
    /**
     * Add an item to the Mobile Service Table
     *
     * @param item
     *            The item to Add
     */
    public CONTACTS addCONTACTSInTable(CONTACTS item) throws ExecutionException, InterruptedException {
        CONTACTS entity = mCONTACTSTable.insert(item).get();
        return entity;
    }

    public EMAILS addEMAILSInTable(EMAILS item) throws ExecutionException, InterruptedException {
        EMAILS entity = mEMAILSTable.insert(item).get();
        return entity;
    }

    public Owner addOwnerInTable(Owner item) throws ExecutionException, InterruptedException {
        Owner entity = mOwnerTable.insert(item).get();
        return entity;
    }

    public PHONES addPHONESInTable(PHONES item) throws ExecutionException, InterruptedException {
        PHONES entity = mPHONESTable.insert(item).get();
        return entity;
    }

    public SOCIAL addSOCIALInTable(SOCIAL item) throws ExecutionException, InterruptedException {
        SOCIAL entity = mSOCIALTable.insert(item).get();
        return entity;
    }



    /**
     * Refresh the list with the items in the Table
     */
    private void refreshItemsFromTable() {

        // Get the items that weren't marked as completed and add them in the
        // adapter

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {

                try {
                    final List<CONTACTS> resultsCONTACTS = refreshCONTACTSFromMobileServiceTable();
                    final List<EMAILS> resultsEMAILS = refreshEMAILSFromMobileServiceTable();
                    final List<Owner> resultsOwner = refreshOwnerFromMobileServiceTable();
                    final List<PHONES> resultsPHONES = refreshPHONESFromMobileServiceTable();
                    final List<SOCIAL> resultsSOCIAL = refreshSOCIALFromMobileServiceTable();

                    //Offline Sync
                    //final List<ToDoItem> results = refreshItemsFromMobileServiceTableSyncTable();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mCONTACTSAdapter.clear();
                            mEMAILSAdapter.clear();
                            mOwnerAdapter.clear();
                            mPHONESAdapter.clear();
                            mSOCIALAdapter.clear();

                            for (CONTACTS item : resultsCONTACTS) {
                                mCONTACTSAdapter.add(item);
                            }
                            for (EMAILS item : resultsEMAILS) {
                                mEMAILSAdapter.add(item);
                            }
                            for (Owner item : resultsOwner) {
                                mOwnerAdapter.add(item);
                            }
                            for (PHONES item : resultsPHONES) {
                                mPHONESAdapter.add(item);
                            }
                            for (SOCIAL item : resultsSOCIAL) {
                                mSOCIALAdapter.add(item);
                            }
                        }
                    });
                } catch (final Exception e){
                    createAndShowDialogFromTask(e, "Error");
                }

                return null;
            }
        };

        runAsyncTask(task);
    }

    /**
     * Refresh the list with the items in the Mobile Service Table
     */

    private List<CONTACTS> refreshCONTACTSFromMobileServiceTable() throws ExecutionException, InterruptedException {
        return mCONTACTSTable.where().field("complete").
                eq(val(false)).execute().get();
    }

    private List<EMAILS> refreshEMAILSFromMobileServiceTable() throws ExecutionException, InterruptedException {
        return mEMAILSTable.where().field("complete").
                eq(val(false)).execute().get();
    }

    private List<Owner> refreshOwnerFromMobileServiceTable() throws ExecutionException, InterruptedException {
        return mOwnerTable.where().field("complete").
                eq(val(false)).execute().get();
    }

    private List<PHONES> refreshPHONESFromMobileServiceTable() throws ExecutionException, InterruptedException {
        return mPHONESTable.where().field("complete").
                eq(val(false)).execute().get();
    }

    private List<SOCIAL> refreshSOCIALFromMobileServiceTable() throws ExecutionException, InterruptedException {
        return mSOCIALTable.where().field("complete").
                eq(val(false)).execute().get();
    }

    //Offline Sync
    /**
     * Refresh the list with the items in the Mobile Service Sync Table
     */
    /*private List<ToDoItem> refreshItemsFromMobileServiceTableSyncTable() throws ExecutionException, InterruptedException {
        //sync the data
        sync().get();
        Query query = QueryOperations.field("complete").
                eq(val(false));
        return mToDoTable.read(query).get();
    }*/

    /**
     * Initialize local storage
     * @return
     * @throws MobileServiceLocalStoreException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    private AsyncTask<Void, Void, Void> initLocalStore() throws MobileServiceLocalStoreException, ExecutionException, InterruptedException {

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {

                    MobileServiceSyncContext syncContext = mClient.getSyncContext();

                    if (syncContext.isInitialized())
                        return null;

                    SQLiteLocalStore localStore = new SQLiteLocalStore(mClient.getContext(), "OfflineStore", null, 1);

                    Map<String, ColumnDataType> CONTACTStableDefinition = new HashMap<String, ColumnDataType>();
                    CONTACTStableDefinition.put("person_Id", ColumnDataType.Integer);
                    CONTACTStableDefinition.put("name", ColumnDataType.String);
                    CONTACTStableDefinition.put("complete", ColumnDataType.Boolean);

                    localStore.defineTable("CONTACTS", tableDefinition);

                    Map<String, ColumnDataType> EMAILStableDefinition = new HashMap<String, ColumnDataType>();
                    EMAILStableDefinition.put("person_Id", ColumnDataType.Integer);
                    EMAILStableDefinition.put("email", ColumnDataType.String);
                    EMAILStableDefinition.put("email_type", ColumnDataType.String);
                    EMAILStableDefinition.put("complete", ColumnDataType.Boolean);

                    localStore.defineTable("EMAILS", tableDefinition);

                    Map<String, ColumnDataType> OwnertableDefinition = new HashMap<String, ColumnDataType>();
                    OwnertableDefinition.put("person_Id", ColumnDataType.Integer);
                    OwnertableDefinition.put("name", ColumnDataType.String);
                    OwnertableDefinition.put("complete", ColumnDataType.Boolean);

                    localStore.defineTable("Owner", tableDefinition);

                    Map<String, ColumnDataType> PHONEStableDefinition = new HashMap<String, ColumnDataType>();
                    PHONEStableDefinition.put("person_Id", ColumnDataType.Integer);
                    PHONEStableDefinition.put("number", ColumnDataType.String);
                    PHONEStableDefinition.put("type", ColumnDataType.String);
                    PHONEStableDefinition.put("complete", ColumnDataType.Boolean);

                    localStore.defineTable("PHONES", tableDefinition);

                    Map<String, ColumnDataType> SOCIALtableDefinition = new HashMap<String, ColumnDataType>();
                    SOCIALtableDefinition.put("person_Id", ColumnDataType.Integer);
                    SOCIALtableDefinition.put("username", ColumnDataType.String);
                    SOCIALtableDefinition.put("social_type", ColumnDataType.String);
                    SOCIALtableDefinition.put("complete", ColumnDataType.Boolean);

                    localStore.defineTable("SOCIAL", tableDefinition);

                    SimpleSyncHandler handler = new SimpleSyncHandler();

                    syncContext.initialize(localStore, handler).get();

                } catch (final Exception e) {
                    createAndShowDialogFromTask(e, "Error");
                }

                return null;
            }
        };

        return runAsyncTask(task);
    }

    //Offline Sync
    /**
     * Sync the current context and the Mobile Service Sync Table
     * @return
     */
    /*
    private AsyncTask<Void, Void, Void> sync() {
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    MobileServiceSyncContext syncContext = mClient.getSyncContext();
                    syncContext.push().get();
                    mToDoTable.pull(null).get();
                } catch (final Exception e) {
                    createAndShowDialogFromTask(e, "Error");
                }
                return null;
            }
        };
        return runAsyncTask(task);
    }
    */

    /**
     * Creates a dialog and shows it
     *
     * @param exception
     *            The exception to show in the dialog
     * @param title
     *            The dialog title
     */
    private void createAndShowDialogFromTask(final Exception exception, String title) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                createAndShowDialog(exception, "Error");
            }
        });
    }


    /**
     * Creates a dialog and shows it
     *
     * @param exception
     *            The exception to show in the dialog
     * @param title
     *            The dialog title
     */
    private void createAndShowDialog(Exception exception, String title) {
        Throwable ex = exception;
        if(exception.getCause() != null){
            ex = exception.getCause();
        }
        createAndShowDialog(ex.getMessage(), title);
    }

    /**
     * Creates a dialog and shows it
     *
     * @param message
     *            The dialog message
     * @param title
     *            The dialog title
     */
    private void createAndShowDialog(final String message, final String title) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(message);
        builder.setTitle(title);
        builder.create().show();
    }

    /**
     * Run an ASync task on the corresponding executor
     * @param task
     * @return
     */
    private AsyncTask<Void, Void, Void> runAsyncTask(AsyncTask<Void, Void, Void> task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            return task.execute();
        }
    }

    private class ProgressFilter implements ServiceFilter {

        @Override
        public ListenableFuture<ServiceFilterResponse> handleRequest(ServiceFilterRequest request, NextServiceFilterCallback nextServiceFilterCallback) {

            final SettableFuture<ServiceFilterResponse> resultFuture = SettableFuture.create();


            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (mProgressBar != null) mProgressBar.setVisibility(ProgressBar.VISIBLE);
                }
            });

            ListenableFuture<ServiceFilterResponse> future = nextServiceFilterCallback.onNext(request);

            Futures.addCallback(future, new FutureCallback<ServiceFilterResponse>() {
                @Override
                public void onFailure(Throwable e) {
                    resultFuture.setException(e);
                }

                @Override
                public void onSuccess(ServiceFilterResponse response) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if (mProgressBar != null) mProgressBar.setVisibility(ProgressBar.GONE);
                        }
                    });

                    resultFuture.set(response);
                }
            });

            return resultFuture;
        }
    }
}