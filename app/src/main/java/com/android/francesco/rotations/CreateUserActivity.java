package com.android.francesco.rotations;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CreateUserActivity extends Activity {

    private static final String TAG = "LoginActivity";
    private EditText usernameTxt, passwordTxt, nameTxt, surnameTxt;
    private TextView testoProva;
    private Button createBtn;
    DbHandler dbHandler;
    // Progress Dialog
    private ProgressDialog pDialog;

    // url to create new product
    private static String url_create_user = "http://192.168.1.8:8008/android_connect/create_user.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";

    JSONParser jsonParser = new JSONParser();

    public static final String EXTRA_MESSAGE = "com.android.francesco.rotations.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_user);

        Intent intent = getIntent();
        String text = intent.getStringExtra(LoginActivity.EXTRA_MESSAGE);
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();

        usernameTxt = (EditText) findViewById(R.id.username);
        passwordTxt = (EditText) findViewById(R.id.password);
        nameTxt = (EditText) findViewById(R.id.name);
        surnameTxt = (EditText) findViewById(R.id.surname);
        createBtn = (Button) findViewById(R.id.createButton);
        testoProva = (TextView) findViewById(R.id.testoProva);

        dbHandler = new DbHandler(getApplicationContext());

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClicked with username" + usernameTxt.getText());
                testoProva.setText(usernameTxt.getText());
                Toast.makeText(getApplicationContext(), "Saving new profile for " + String.valueOf(usernameTxt.getText()), Toast.LENGTH_SHORT).show();
                Log.d(TAG, String.valueOf(dbHandler.getUsersCount()));
                List<User> users = dbHandler.getAllUsers();
                for(User user : users){
                    if(user.getUsername().equalsIgnoreCase(usernameTxt.getText().toString().trim())){
                        Toast.makeText(getApplicationContext(), "Username " + String.valueOf(usernameTxt.getText()) +" already exists" , Toast.LENGTH_SHORT).show();
                    }else{
                        User newUser = new User();
                        newUser.setId(dbHandler.getUsersCount() + 1);
                        newUser.setUsername(usernameTxt.getText().toString().trim());
                        newUser.setPassword(passwordTxt.getText().toString().trim());
                        newUser.setName(nameTxt.getText().toString().trim());
                        newUser.setSurname(surnameTxt.getText().toString().trim());
                        // Creazione user in db locale, momentaneamente commentata
                        //dbHandler.createUser(user);

                        // Testare nuovo sviluppo di creazione nuovo utente
                        String name = nameTxt.getText().toString();
                        String surname = surnameTxt.getText().toString();
                        String username = usernameTxt.getText().toString();
                        String password = passwordTxt.getText().toString();

                        new CreateNewUser().execute(name, surname, username, password);

                        Toast.makeText(getApplicationContext(), String.valueOf(usernameTxt.getText()) + " has been added to your contacts!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, String.valueOf(dbHandler.getUsersCount()));
                        sendMessage(view);
                    }
                }
                /*Intent intent = new Intent();
                intent.setClass(getApplicationContext(), MainActivity.class);
                intent.putExtra("username", usernameTxt.getText().toString());
                startActivity(intent); */

                /*Contact contact = new Contact(dbHandler.getContactsCount(), String.valueOf(nameTxt.getText()), String.valueOf(phoneTxt.getText()), String.valueOf(emailTxt.getText()), String.valueOf(addressTxt.getText()), imageUri);
                if (!contactExists(contact)) {
                    dbHandler.createContact(contact);
                    contacts.add(contact);
                    contactAdapter.notifyDataSetChanged();
                    Toast.makeText(getApplicationContext(), String.valueOf(nameTxt.getText()) + " has been added to your contacts!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(getApplicationContext(), String.valueOf(nameTxt.getText()) + " already exists. Please use a different name.", Toast.LENGTH_SHORT).show();
                */
            }
        });

    }

    /**
     * Background Async Task to Create new product
     * */
    class CreateNewUser extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(CreateUserActivity.this);
            pDialog.setMessage("Creating User..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Creating product
         * */
        protected String doInBackground(String... args) {

            Map<String, String> urlParams = new HashMap<String, String>();
            urlParams.put("name", args[0]);
            urlParams.put("surname", args[1]);
            urlParams.put("username", args[2]);
            urlParams.put("password", args[3]);


            HttpURLConnection conn;
            BufferedReader in;
            String line;
            StringBuilder chaine = new StringBuilder();
            StringBuilder postData = new StringBuilder();
            byte[] postDataBytes = new byte[0];

            // urlParams = Map<String, Object>
            for (Map.Entry<String, String> param : urlParams.entrySet()) {
                if (postData.length() != 0) postData.append('&');
                try {
                    postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                    postData.append('=');
                    postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                    postDataBytes = postData.toString().getBytes("UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            }
            try {
                URL url = new URL(url_create_user);

                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));

                conn.setDoOutput(true);
                conn.getOutputStream().write(postDataBytes);
                in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                while ((line = in.readLine()) != null) {
                    chaine.append(line);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            JSONArray result = null;
            try {
                result = new JSONArray(chaine.toString());
                int success = result.getInt(Integer.valueOf(TAG_SUCCESS));

                if (success == 1) {
                    // successfully created product
                    Intent i = new Intent(getApplicationContext(), AllUsersActivity.class);
                    startActivity(i);

                    // closing this screen
                    finish();
                } else {
                    // failed to create product
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.e("DATABASE_REST Result", result.toString());
            return result.toString();
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            pDialog.dismiss();
        }

    }

    /** Called when the user taps the Send button */
    public void sendMessage(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        //EditText editText = (EditText) findViewById(R.id.editText);
        String message = usernameTxt.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    /*private static final int EDIT = 0, DELETE = 1;

    EditText nameTxt, phoneTxt, emailTxt, addressTxt;
    ImageView contactImageImgView;
    List<Contact> contacts = new ArrayList<Contact>();
    ListView contactListView;
    Uri imageUri = Uri.parse("android.resource://org.intracode.contactmanager/drawable/no_user_logo.png");
    DatabaseHandler dbHandler;
    int longClickedItemIndex;
    ArrayAdapter<Contact> contactAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nameTxt = (EditText) findViewById(R.id.txtName);
        phoneTxt = (EditText) findViewById(R.id.txtPhone);
        emailTxt = (EditText) findViewById(R.id.txtEmail);
        addressTxt = (EditText) findViewById(R.id.txtAddress);
        contactListView = (ListView) findViewById(R.id.listView);
        contactImageImgView = (ImageView) findViewById(R.id.imgViewContactImage);
        dbHandler = new DatabaseHandler(getApplicationContext());

        registerForContextMenu(contactListView);

        contactListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                longClickedItemIndex = position;
                return false;
            }
        });

        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);

        tabHost.setup();

        TabHost.TabSpec tabSpec = tabHost.newTabSpec("creator");
        tabSpec.setContent(R.id.tabCreator);
        tabSpec.setIndicator("Creator");
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("list");
        tabSpec.setContent(R.id.tabContactList);
        tabSpec.setIndicator("List");
        tabHost.addTab(tabSpec);

        final Button addBtn = (Button) findViewById(R.id.btnAdd);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Contact contact = new Contact(dbHandler.getContactsCount(), String.valueOf(nameTxt.getText()), String.valueOf(phoneTxt.getText()), String.valueOf(emailTxt.getText()), String.valueOf(addressTxt.getText()), imageUri);
                if (!contactExists(contact)) {
                    dbHandler.createContact(contact);
                    contacts.add(contact);
                    contactAdapter.notifyDataSetChanged();
                    Toast.makeText(getApplicationContext(), String.valueOf(nameTxt.getText()) + " has been added to your contacts!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(getApplicationContext(), String.valueOf(nameTxt.getText()) + " already exists. Please use a different name.", Toast.LENGTH_SHORT).show();
            }
        });

        nameTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                addBtn.setEnabled(String.valueOf(nameTxt.getText()).trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        contactImageImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Contact Image"), 1);
            }

        });

        if (dbHandler.getContactsCount() != 0)
            contacts.addAll(dbHandler.getAllContacts());

        populateList();
    }

    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);

        //menu.setHeaderIcon(R.drawable.pencil_icon);
        menu.setHeaderTitle("Contact Options");
        menu.add(Menu.NONE, EDIT, menu.NONE, "Edit Contact");
        menu.add(Menu.NONE, DELETE, menu.NONE, "Delete Contact");
    }

    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case EDIT:
                // TODO: Implement editing a contact
                break;
            case DELETE:
                dbHandler.deleteContact(contacts.get(longClickedItemIndex));
                contacts.remove(longClickedItemIndex);
                contactAdapter.notifyDataSetChanged();
                break;
        }

        return super.onContextItemSelected(item);
    }

    private boolean contactExists(Contact contact) {
        String name = contact.getName();
        int contactCount = contacts.size();

        for (int i = 0; i < contactCount; i++) {
            if (name.compareToIgnoreCase(contacts.get(i).getName()) == 0)
                return true;
        }
        return false;
    }

    public void onActivityResult(int reqCode, int resCode, Intent data) {
        if (resCode == RESULT_OK) {
            if (reqCode == 1) {
                imageUri = data.getData();
                contactImageImgView.setImageURI(data.getData());
            }
        }
    }

    private void populateList() {
        //contactAdapter = new ContactListAdapter();
        //contactListView.setAdapter(contactAdapter);

    }*/

    /*private class ContactListAdapter extends ArrayAdapter<Contact> {
        public ContactListAdapter() {
            super (MainActivity.this, R.layout.listview_item, contacts);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if (view == null)
                view = getLayoutInflater().inflate(R.layout.listview_item, parent, false);

            Contact currentContact = contacts.get(position);

            TextView name = (TextView) view.findViewById(R.id.contactName);
            name.setText(currentContact.getName());
            TextView phone = (TextView) view.findViewById(R.id.phoneNumber);
            phone.setText(currentContact.getPhone());
            TextView email = (TextView) view.findViewById(R.id.emailAddress);
            email.setText(currentContact.getEmail());
            TextView address = (TextView) view.findViewById(R.id.cAddress);
            address.setText(currentContact.getAddress());
            ImageView ivContactImage = (ImageView) view.findViewById(R.id.ivContactImage);
            ivContactImage.setImageURI(currentContact.getImageURI());

            return view;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }*/

}