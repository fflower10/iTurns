package com.android.francesco.rotations;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AllUsersActivity extends Activity {

    private static final String TAG = "AllUsersActivity";
    ListView usersListView;
    List<User> users = new ArrayList<User>();
    int longClickedItemIndex;
    DbHandler dbHandler;
    ArrayAdapter<User> userAdapter;
    Uri imageUri = Uri.parse("android.resource://org.intracode.contactmanager/drawable/no_user_logo.png");
    private static final String PHP_url = "http://192.168.1.8:8008/android_connect/get_all_users.php";
    // Progress Dialog
    private ProgressDialog pDialog;
    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_USERS = "users";
    private static final String TAG_PID = "id";
    private static final String TAG_NAME = "name";
    private static final String TAG_SURNAME = "surname";
    private static final String TAG_USERNAME = "username";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_users);

        // Loading products in Background Thread
        //new LoadAllUsers().execute();

        usersListView = (ListView) findViewById(R.id.listViewUsers);
        Intent intent = getIntent();
        Log.i(TAG, "In AllUserActivity...");
        String text = intent.getStringExtra(LoginActivity.EXTRA_MESSAGE);
        Toast.makeText(getApplicationContext(), "Print " + text, Toast.LENGTH_SHORT).show();

        dbHandler = new DbHandler(getApplicationContext());

        // Load users from local android db
        if (dbHandler.getUsersCount() != 0)
            users.addAll(dbHandler.getAllUsers());

        // Loading products in Background Thread
        new LoadAllUsers().execute();

        populateList();

        registerForContextMenu(usersListView);

        usersListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                longClickedItemIndex = position;
                Toast.makeText(getApplicationContext(), "Selected item n " + longClickedItemIndex, Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        usersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                longClickedItemIndex = position;
                Toast.makeText(getApplicationContext(), "One click! Selected item n " + longClickedItemIndex, Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), "Id " + id, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getBaseContext(), AllExerciseActivity.class);
                intent.putExtra("EXTRA_SESSION_ID", id);
                startActivity(intent);
            }
        });
    }

    private void populateList() {
        userAdapter = new UserListAdapter();
        usersListView.setAdapter(userAdapter);

    }

    private class UserListAdapter extends ArrayAdapter<User> {
        public UserListAdapter() {

            super (AllUsersActivity.this, R.layout.list_item, users);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if (view == null)
                view = getLayoutInflater().inflate(R.layout.list_item, parent, false);

            User currentUser = users.get(position);

            TextView username = (TextView) view.findViewById(R.id.username);
            username.setText(currentUser.getUsername());
            TextView name = (TextView) view.findViewById(R.id.name);
            name.setText(currentUser.getName());
            TextView surname = (TextView) view.findViewById(R.id.surname);
            surname.setText(currentUser.getSurname());
            ImageView userImgView = (ImageView) findViewById(R.id.imgUser);
            //ImageView ivContactImage = (ImageView) view.findViewById(R.id.ivContactImage);
            //ivContactImage.setImageURI(imageUri);
            //ImageView ivContactImage = (ImageView) view.findViewById(R.id.ivContactImage);
            //ivContactImage.setImageURI(currentUser.getImageURI());

            return view;
        }
    }


    private class LoadAllUsers extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AllUsersActivity.this);
            pDialog.setMessage("Loading users. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            //String uri = params[0];

            BufferedReader bufferedReader = null;
            try {
                //URL url = new URL(uri);

                JSONArray jsonUsers = null;
                URL url = new URL(PHP_url);

                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                StringBuilder sb = new StringBuilder();

                bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                String json;
                while((json = bufferedReader.readLine())!= null){
                    sb.append(json+"\n");
                }

                con.disconnect();
                Log.i(TAG, "json file: " + sb.toString().trim());

                // return sb.toString().trim();

                // getting JSON string from URL
                JSONObject jsonObj = new JSONObject(sb.toString().trim());

                // Check your log cat for JSON reponse
                Log.d("All Users: ", jsonObj.toString());

                try {
                    // Checking for SUCCESS TAG
                    int success = jsonObj.getInt(TAG_SUCCESS);

                    if (success == 1) {
                        // products found
                        // Getting Array of Products
                        jsonUsers = jsonObj.getJSONArray("users");

                        // looping through All Products
                        for (int i = 0; i < jsonUsers.length(); i++) {
                            JSONObject c = jsonUsers.getJSONObject(i);

                            User currUser = new User();

                            // Storing each json item in variable
                            String id = c.getString(TAG_PID);
                            String name = c.getString(TAG_NAME);
                            String surname = c.getString(TAG_SURNAME);
                            String username = c.getString(TAG_USERNAME);

                            currUser.setId(Integer.parseInt(id));
                            currUser.setName(name);
                            currUser.setSurname(surname);
                            currUser.setUsername(surname);

                            users.add(currUser);
                            //add current user to users;

                            /*
                            // creating new HashMap
                            HashMap<String, String> map = new HashMap<String, String>();

                            // adding each child node to HashMap key => value
                            map.put(TAG_PID, id);
                            map.put(TAG_NAME, name);

                            // adding HashList to ArrayList
                            productsList.add(map);*/
                        }
                    } else {
                        Log.d(TAG, "Users not found in database...");
                        /* // no products found
                        // Launch Add New product Activity
                        Intent i = new Intent(getApplicationContext(),
                                NewProductActivity.class);
                        // Closing all previous activities
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);*/
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return null;
            } catch (MalformedURLException e){
                e.printStackTrace();
                Log.e(TAG, "malformed URL...!!!");
            } catch(Exception e){
                e.printStackTrace();
                Log.e(TAG, "General error!!");
                return null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.dismiss();
        }


    }
}