package com.example.syahril.yourtaskapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;
import com.example.syahril.yourtaskapp.Model.Data;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HomeActivity extends BaseActivity {

    private Toolbar toolbar;
    private FloatingActionButton fabBtn;

    //firebase

    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseSpinner;
    private FirebaseAuth mAuth;

    //Recycler..

    private RecyclerView recyclerView;

    //Update input field..
    private EditText titleupdate;
    private EditText noteupdate;
    private Spinner spinnerList;
    private Spinner spinnerupdate;
    private Button btndelete;
    private Button btnupdate;
    private LinearLayout root;

    //variable

    private String title;
    private String note;
    private String staff;
    private String status;
    private String post_key;

    List<String> spinnerListString = new ArrayList<>();
    ArrayAdapter<String> spinnerListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar=findViewById(R.id.toolbar_home);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Your Task App");

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uId = mUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("TaskNote").child(uId);
        mDatabase.keepSynced(true);

        //firebase spinner data
        mDatabaseSpinner = FirebaseDatabase.getInstance().getReference().child("staff");

        //Recycler..

        recyclerView = findViewById(R.id.recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);



        root=findViewById(R.id.root);
        fabBtn = findViewById(R.id.fab_btn);
        fabBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setupDialogSave();
            }
        });
        setupMainList();
    }



    @Override
    protected void onStart() {
        super.onStart();

    }

    private void setupMainList(){
        showProgressDialog();
        FirebaseRecyclerOptions<Data> options =
                new FirebaseRecyclerOptions.Builder<Data>()
                        .setQuery(mDatabase, Data.class)
                        .build();

        FirebaseRecyclerAdapter<Data, MyViewHolder> adapter=
                new FirebaseRecyclerAdapter<Data, MyViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull MyViewHolder holder, final int position, @NonNull final Data model) {
                        holder.setTitle(model.getTitle());
                        holder.setNote(model.getNote());
                        holder.setStaff(model.getStaff());

                        holder.seDate(convertDateBasedOnDay(model.getDate()));

                        holder.myview.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                post_key = getRef(position).getKey();
                                title = model.getTitle();
                                note = model.getNote();
                                staff = model.getStaff();

                                setupDialogUpdate();
                            }
                        });
                    }
                    @NonNull
                    @Override
                    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
                    {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_data , viewGroup , false);
                        MyViewHolder myViewHolder = new MyViewHolder(view);
                        return myViewHolder;
                    }

                };
        dismissProgressDialog();
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void setupSpinnerList(Spinner spinner){
        spinnerListString.clear();
        spinnerListAdapter = new ArrayAdapter<>(this, R.layout.spinner_item,spinnerListString);
        //spinnerListAdapter.clear();
        spinnerListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(spinnerListAdapter);
        spinner.setPrompt("Staff");
        mDatabaseSpinner.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data:dataSnapshot.getChildren()
                        ) {
                    spinnerListString.add(data.getValue().toString());
                }
                spinnerListAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                //TODO
                if(pos <= spinnerListString.size()-1){
                    //System.out.println(spinnerListString.get(pos));
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                //TODO
            }
        });

    }



    public static class MyViewHolder extends RecyclerView.ViewHolder {

        View myview;

        public MyViewHolder(View itemView) {
            super(itemView);
            myview = itemView;
        }

        public void setTitle(String title) {
            TextView mTitle = myview.findViewById(R.id.title);
            mTitle.setText(title);
        }


        public void setNote(String note) {
            TextView mNote = myview.findViewById(R.id.note);
            mNote.setText(note);


        }

        public void setStaff(String staff) {
            TextView mStaff = myview.findViewById(R.id.staff);
            mStaff.setText("Assigned to: " +staff);


        }



        public void seDate(String date){
            TextView mDate=myview.findViewById(R.id.date);
            mDate.setText(date);
        }
    }




    //codding ini akan fokuskan kpd layout (file xml) iaitu menu overflow
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_overflow, menu);
        return super.onCreateOptionsMenu(menu);
    }
     //function untuk log out
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_logout:
                mAuth.signOut();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    public void setupDialogUpdate(){

        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.updateinputfield, null);


        titleupdate = alertLayout.findViewById(R.id.edit_title_update);
        noteupdate = alertLayout.findViewById(R.id.edit_note_update);
        spinnerupdate  = alertLayout.findViewById(R.id.spinner_update);
        spinnerupdate.setAdapter(spinnerListAdapter);
        titleupdate.setText(title);
        titleupdate.setSelection(title.length());



        noteupdate.setText(note);
        noteupdate.setSelection(note.length());

        //Spinner

        setupSpinnerList(spinnerupdate);


        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Update Task");
        // this is set the view from XML inside AlertDialog
        alert.setView(alertLayout);
        // disallow cancel of AlertDialog on click of back button and outside touch
        alert.setCancelable(true);

        alert.setNegativeButton("Delete", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                mDatabase.child(post_key).removeValue();
                showSnackBar(root,"Data Removed!");
            }
        });


        alert.setPositiveButton("Update", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(final DialogInterface dialog, int which) {

                showProgressDialog();
                title = titleupdate.getText().toString().trim();
                note  = noteupdate.getText().toString().trim();
                staff = spinnerupdate.getSelectedItem().toString().trim();
                String mDate = DateFormat.getDateInstance().format(new Date());

                Data data = new Data(title,note, staff ,mDate,post_key);
                mDatabase.child(post_key).setValue(data, new DatabaseReference.CompletionListener() {
                    public void onComplete(DatabaseError error, DatabaseReference ref) {
//                            System.out.println("Value was set. Error = "+error);
                        if(error==null){
                            showSnackBar(root,"Data Updated!");
                        }else{
                            showSnackBar(root,error.toString());
                        }

                        dismissProgressDialog();
                        dialog.dismiss();
                    }
                });




            }
        });

        final AlertDialog dialog = alert.create();
        dialog.show();




    }


    public void setupDialogSave(){

        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.custominputfield, null);

        final EditText title = alertLayout.findViewById(R.id.edit_title);
        final EditText note = alertLayout.findViewById(R.id.edit_note);
        final Spinner staff = alertLayout.findViewById(R.id.spinner);
        spinnerList = alertLayout.findViewById(R.id.spinner);
        //Spinner

        setupSpinnerList(spinnerList);


        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Add Task");
        // this is set the view from XML inside AlertDialog
        alert.setView(alertLayout);
        // disallow cancel of AlertDialog on click of back button and outside touch
        alert.setCancelable(true);

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(final DialogInterface dialog, int which) {



                String mTitle = title.getText().toString().trim();
                String mNote = note.getText().toString().trim();
                String mStaff = staff.getSelectedItem().toString().trim();


                if (TextUtils.isEmpty(mTitle) ) {
                    title.setError("Required Field");

                }else if (TextUtils.isEmpty(mNote)) {
                    note.setError("Required Field");
                }else{
                    showProgressDialog();
                    String id = mDatabase.push().getKey();

                    String date = DateFormat.getDateInstance().format(new Date());

                    Data data = new Data(mTitle, mNote, mStaff , date, id);


                    mDatabase.child(id).setValue(data, new DatabaseReference.CompletionListener() {
                        public void onComplete(DatabaseError error, DatabaseReference ref) {
//                            System.out.println("Value was set. Error = "+error);
                            if(error==null){
                                showSnackBar(root,"Data Inserted!");
                            }else{
                                showSnackBar(root,error.toString());
                            }

                            dismissProgressDialog();
                            dialog.dismiss();
                        }
                    });
                }
//




            }
        });

        final AlertDialog dialog = alert.create();
        dialog.show();




    }

}
