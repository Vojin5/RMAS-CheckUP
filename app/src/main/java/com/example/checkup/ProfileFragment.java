package com.example.checkup;

import static android.app.Activity.RESULT_OK;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {
    private static final String PHONE_DB_NAME = "PhoneNumbers";
    private static final String PROFILE_IMG_STORAGE = "ProfileImages";
    public static final int PICK_IMAGE = 2;

    MaterialButton addPhoneBtn;
    FirebaseAuth auth;

    FirebaseDatabase firebaseDatabase;

    FirebaseStorage storage;

    TextView nameTxt;
    TextView emailTxt;
    TextView phoneTxt;
    TextView pointsTxt;
    CircleImageView profileIMG;
    MaterialButton AddPhotoBtn;
    Uri imageURI;

    public ProfileFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


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
        AddPhotoBtn.setOnClickListener(this::addPhoto);
        return view;
    }

    private void setUI(FirebaseUser user)
    {
        String name = user.getDisplayName();
        String mail = user.getEmail();
        StorageReference storage_reference = storage.getReference().child(PROFILE_IMG_STORAGE).child(name+".jpg");
        if(getActivity() == null)
        {
            return;
        }

        storage_reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                if(getActivity() == null)
                {
                    return;
                }
                Glide.with(ProfileFragment.this)
                        .load(uri)
                        .error(R.drawable.ic_launcher_background)
                        .circleCrop()
                        .into(profileIMG);

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
                                Toast.makeText(getContext(), "Please add number", Toast.LENGTH_SHORT).show();
                            }
                        });

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
        AddPhotoBtn = view.findViewById(R.id.add_photo_btn);
    }

    private void addPhoto(View view)
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            imageURI = data.getData();
            if(imageURI == null)
            {
                Toast.makeText(getActivity(), "Error adding photo", Toast.LENGTH_SHORT).show();
                return;
            }
            profileIMG.setImageURI(imageURI);

            storage.getReference()
                    .child(PROFILE_IMG_STORAGE)
                    .child(auth.getCurrentUser().getDisplayName()+".jpg")
                    .putFile(imageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            Toast.makeText(getActivity(), "Photo added successfully", Toast.LENGTH_SHORT).show();
                        }
                    });

        }
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
        firebaseDatabase = FirebaseDatabase.getInstance("https://checkup-f6ce4-default-rtdb.europe-west1.firebasedatabase.app/");
        //firebaseDatabase = FirebaseDatabase.getInstance("https://checkup-f6ce4-default-rtdb.europe-west1.firebasedatabase.app");
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
    }


}