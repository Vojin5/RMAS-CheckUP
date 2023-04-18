package com.example.checkup;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.module.AppGlideModule;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.ktx.Firebase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    private static final String PHONE_DB_NAME = "PhoneNumbers";
    private static final String PROFILE_IMG_STORAGE = "ProfileImages";

    MaterialButton addPhoneBtn;
    FirebaseAuth auth;

    FirebaseDatabase firebaseDatabase;

    FirebaseStorage storage;

    TextView nameTxt;
    TextView emailTxt;
    TextView phoneTxt;
    TextView pointsTxt;
    ImageView profileIMG;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment(){
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        setFirebase();
        FirebaseUser user = auth.getCurrentUser();
        findViews(view);
        setUI(user);



        addPhoneBtn.setOnClickListener(this::showPhoneDialog);
        return view;
    }

    private void setUI(FirebaseUser user)
    {
        String name = user.getDisplayName();
        String mail = user.getEmail();
        StorageReference storage_reference = storage.getReference().child(PROFILE_IMG_STORAGE).child(name+".jpg");

        storage_reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(ProfileFragment.this)
                        .load(uri)
                        .error(R.drawable.ic_launcher_background)
                        .circleCrop()
                        .into(profileIMG);
            }
        });

        nameTxt.setText(name);
        emailTxt.setText(mail);

        DatabaseReference reference = firebaseDatabase.getReference();
        reference.child(PHONE_DB_NAME)
                .child(user.getDisplayName())
                .get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot snapshot) {
                        if(snapshot.exists())
                        {
                            PhoneNumbers phone = snapshot.getValue(PhoneNumbers.class);
                            phoneTxt.setText(phone.getPhone());
                        }
                        else{
                            phoneTxt.setText("not added yet");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Error getting number", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void findViews(View view)
    {
        addPhoneBtn = view.findViewById(R.id.add_number_btn);
        nameTxt = view.findViewById(R.id.name_display);
        emailTxt = view.findViewById(R.id.email_display);
        phoneTxt = view.findViewById(R.id.phone_display);
        pointsTxt = view.findViewById(R.id.point_display);
        profileIMG = view.findViewById(R.id.profile_image);
    }

    private void showPhoneDialog(View view)
    {
        Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.phone_dialog);

        TextInputEditText phoneTxt = dialog.findViewById(R.id.phone_input);
        MaterialButton applyBtn = dialog.findViewById(R.id.dialog_apply);
        MaterialButton cancelBtn = dialog.findViewById(R.id.dialog_cancel);

        applyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = phoneTxt.getText().toString();
                FirebaseUser user = auth.getCurrentUser();
                String name = user.getDisplayName();
                String id = user.getUid();
                PhoneNumbers newPhone = new PhoneNumbers(phone,id);
                DatabaseReference reference = firebaseDatabase.getReference();
                reference.child(PHONE_DB_NAME).child(name).setValue(newPhone);
                Toast.makeText(getContext(), "Phone added", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    private void setFirebase()
    {
        firebaseDatabase = FirebaseDatabase.getInstance("https://checkup-f6ce4-default-rtdb.europe-west1.firebasedatabase.app");
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
    }


}