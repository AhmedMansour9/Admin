package bekya.admin;


import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class company extends Fragment {
    private final int PICK_IMAGE_REQUEST = 91;
     Button btnimgone,btnimgtwo;
     ImageView imgone,imgtwo;
     Uri filePath;
    StorageReference storageReference;
    FirebaseStorage storage;
    Companymodel companymodel;
    String img1;
    String img2;
    public company() {
        // Required empty public constructor
    }

       View v;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         v=inflater.inflate(R.layout.companyfragment, container, false);
         btnimgone=v.findViewById(R.id.btnimg1);
         btnimgtwo=v.findViewById(R.id.btnimg2);
         imgone=v.findViewById(R.id.img1);
         imgtwo=v.findViewById(R.id.img2);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        companymodel=new Companymodel();
         btnimgone.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                         MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                 startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);

             }
         });

        btnimgtwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, 90);

            }
        });
      GetImages(new firebasecallback() {
          @Override
          public void Company(Companymodel comp) {
              if(comp.getImg1()!=null){
                  Picasso.with(getContext())
                          .load(comp.getImg1())
                          .fit()
                          .into(imgone);
              }
              if(comp.getImg2()!=null){
                  Picasso.with(getContext())
                          .load(comp.getImg2())
                          .fit()
                          .into(imgtwo);
              }
          }
      });


         return v;
    }
    public void GetImages(final firebasecallback fire){
        DatabaseReference data=FirebaseDatabase.getInstance().getReference("Company");
        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("img1")){
                     img1=dataSnapshot.child("img1").getValue().toString();
                    companymodel.setImg1(img1);

                }
                if(dataSnapshot.hasChild("img2")){
                     img2=dataSnapshot.child("img2").getValue().toString();
                    companymodel.setImg2(img2);
                }
                fire.Company(companymodel);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public interface firebasecallback{
        void Company(Companymodel comp);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            if(filePath != null)
            {
                Toast.makeText(getContext(), ""
                        +String.valueOf(filePath), Toast.LENGTH_SHORT).show();
                    final ProgressDialog progressDialog = new ProgressDialog(getContext());
                    progressDialog.setTitle("Uploading...");
                    progressDialog.show();

                    storageReference = storage.getReferenceFromUrl("gs://bekya-5f805.appspot.com");

                StorageReference imageRef = storageReference.child("images" + "/" + filePath + ".jpg");
                    imageRef.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Log.e("ss", "onSuccess: " + taskSnapshot);
                            progressDialog.dismiss();
                            Uri u = taskSnapshot.getDownloadUrl();
                      DatabaseReference data=FirebaseDatabase.getInstance().getReference("Company");
                      data.child("img1").setValue(String.valueOf(u));


                        }}).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");

                        }
                    });

            }
        }else  if(requestCode == 90 && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            if(filePath != null)
            {

                final ProgressDialog progressDialog = new ProgressDialog(getContext());
                progressDialog.setTitle("Uploading...");
                progressDialog.show();

                storageReference = storage.getReferenceFromUrl("gs://bekya-5f805.appspot.com");

                StorageReference imageRef = storageReference.child("images" + "/" + filePath + ".jpg");
                imageRef.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.e("ss", "onSuccess: " + taskSnapshot);
                        progressDialog.dismiss();
                        Uri u = taskSnapshot.getDownloadUrl();
                        DatabaseReference data=FirebaseDatabase.getInstance().getReference().child("Company");
                        data.child("img2").setValue(String.valueOf(u));


                    }}).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                .getTotalByteCount());
                        progressDialog.setMessage("Uploaded "+(int)progress+"%");

                    }
                });

            }
        }


    }

    @Override
    public void onPause() {
        super.onPause();

    }
}
