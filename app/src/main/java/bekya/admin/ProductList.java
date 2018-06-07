package bekya.admin;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ProductList extends AppCompatActivity  {

    RecyclerView recyclerView;
    CardView rootlayout;
    String Nameone;
    Button btnUpload;
    Button btnselect;
    SwipeRefreshLayout mSwipeRefreshLayout;
EditText name,descrip , discount, price;
    GridView gridGallery;
    Handler handler;
    Button finish;
    GalleryAdapter adapter;
    List<Retrivedata> array;
    ArrayList<String> listimages=new ArrayList<>();
    ViewSwitcher viewSwitcher;
    ImageLoader imageLoader;
    DatabaseReference data;
    StorageReference storageRef;
    SharedPreferences.Editor editor;
    SharedPreferences.Editor editt;
    String imgOne,imgTwo,imgThree,imgFour;
    private Uri filePathone,filePathtwo,filePaththree,filePathfour;
    FirebaseStorage storage;
    StorageReference storageReference;
    public static String token;
    ArrayList<CustomGallery> dataT;
    String Name,Discrption,Discount,Price;
    String child;
    Dialog update_items_layout;
    public ChildEventListener mListener;
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Cairo-Bold.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        setContentView(R.layout.layout_add_product);
          token = SharedPrefManager.getInstance(getApplicationContext()).getDeviceToken();

        handler = new Handler();
        array=new ArrayList<>();
        rootlayout = findViewById(R.id.rootlayout);
        SharedPreferences shared=getSharedPreferences("cat",MODE_PRIVATE);
         child=shared.getString("Category",null);
        data= FirebaseDatabase.getInstance().getReference().child("Products").child(child);
        editor = getApplicationContext().getSharedPreferences("Photo", MODE_PRIVATE).edit();
        editor.clear();
        editor.commit();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


      showaddFooddialog();

    }
    private void initImageLoader() {
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisc().imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
        ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(
                this).defaultDisplayImageOptions(defaultOptions).memoryCache(
                new WeakMemoryCache());

        ImageLoaderConfiguration config = builder.build();
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);
    }
    private void showaddFooddialog() {
        initImageLoader();
       name = findViewById(R.id.Name);
       descrip = findViewById(R.id.descrip);
        discount = findViewById(R.id.discount);
        price = findViewById(R.id.price);

        gridGallery =findViewById(R.id.gridGallery);
        gridGallery.setFastScrollEnabled(true);
        adapter = new GalleryAdapter(getApplicationContext(), imageLoader);
        adapter.setMultiplePick(false);
        gridGallery.setAdapter(adapter);

        viewSwitcher =findViewById(R.id.viewSwitcher);
        viewSwitcher.setDisplayedChild(1);

        btnUpload = findViewById(R.id.btnupload);
       btnselect = findViewById(R.id.btnselect);

        btnselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadimage();
            }
        });


    }

    private void uploadimage() {

        Name = name.getText().toString().trim();
        Discrption = descrip.getText().toString().trim();
        Discount = discount.getText().toString().trim();
        Price = price.getText().toString().trim();

        if (Name.isEmpty() || Discrption.isEmpty() || Discount.isEmpty() || Price.isEmpty()) {
            Toast.makeText(getBaseContext(), "من فضلك أملآ جميع البيانات", Toast.LENGTH_SHORT).show();

        }else if(dataT==null){
            SavedSahredPrefrenceSwitch(Name, Discrption, Discount, Price);
        }
        else {
            if (dataT != null) {
                for (int i = 0; i < dataT.size(); i++) {
                    final ProgressDialog progressDialog = new ProgressDialog(this);
                    progressDialog.setTitle("Uploading...");
                    progressDialog.show();
                    storageRef = storage.getReferenceFromUrl("gs://bekya-5f805.appspot.com/");
                    Uri file = Uri.fromFile(new File(dataT.get(i).sdcardPath));
                    StorageReference imageRef = storageRef.child("images" + "/" + file + ".jpg");
                    imageRef.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Log.e("ss", "onSuccess: " + taskSnapshot);
                            progressDialog.dismiss();
                            Uri u = taskSnapshot.getDownloadUrl();
                            Gson i = new Gson();
                            listimages.add(u.toString());
                            String jsonFavorites = i.toJson(listimages);
                            editor.putString("img", jsonFavorites);
                            editor.commit();
                            final int pos = dataT.size();
                            int y = listimages.size();

                            if (pos == y) {
                                progressDialog.dismiss();
                                SavedSahredPrefrenceSwitch(Name, Discrption, Discount, Price);

                                Snackbar.make(rootlayout, "تم إضافة منجك بنجاح", Snackbar.LENGTH_SHORT)
                                        .show();
                            }
                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                                }
                            });
                }
            }
        }
    }

    private void chooseImage() {
        Intent i = new Intent(Action.ACTION_MULTIPLE_PICK);
        startActivityForResult(i, 200);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

         if (requestCode == 200 && resultCode == Activity.RESULT_OK) {
            String[] all_path = data.getStringArrayExtra("all_path");


            if (all_path.length > 4) {
                Intent i = new Intent(Action.ACTION_MULTIPLE_PICK);
                startActivityForResult(i, 200);
                Toast.makeText(this, "Choose 4 images Only", Toast.LENGTH_SHORT).show();
            }

            dataT = new ArrayList<CustomGallery>();

            for (String string : all_path) {
                CustomGallery item = new CustomGallery();
                item.sdcardPath = string;


                dataT.add(item);
            }
            viewSwitcher.setDisplayedChild(0);
            adapter.addAll(dataT);

        }


        }

public void SavedSahredPrefrenceSwitch(String name,String discroption,String discount,String phone){

    SharedPreferences sharedPref =getSharedPreferences("Photo", MODE_PRIVATE);
    String jsonFavorit = sharedPref.getString("img", null);
    Gson gson3 = new Gson();
    String[] favoriteIte = gson3.fromJson(jsonFavorit,String[].class);
    Retrivedata r=new Retrivedata();
    String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

    if(jsonFavorit!=null){

    int postion = favoriteIte.length;
    if(postion==4) {
        r.setImg1(favoriteIte[0]);
        r.setImg2(favoriteIte[1]);
        r.setImg3(favoriteIte[2]);
        r.setImg4(favoriteIte[3]);
        r.setName(name);
        r.setDiscrption(discroption);
        r.setDiscount(discount);
        r.setPhone(phone);
        r.setDate(date);
        r.setToken(token);
        r.setAdmin(true);
        data.push().setValue(r);
        Snackbar.make(rootlayout, "تم إضافة منجك بنجاح", Snackbar.LENGTH_SHORT)
                .show();

    }
    if (postion == 3) {
        r.setImg1(favoriteIte[0]);
        r.setImg2(favoriteIte[1]);
        r.setImg3(favoriteIte[2]);
        r.setName(name);
        r.setDiscrption(discroption);
        r.setDiscount(discount);
        r.setPhone(phone);
        r.setDate(date);
        r.setToken(token);
        r.setAdmin(true);
        data.push().setValue(r);
        Snackbar.make(rootlayout, "تم إضافة منجك بنجاح", Snackbar.LENGTH_SHORT)
                .show();

    }
    if (postion == 2){
        r.setImg1(favoriteIte[0]);
        r.setImg2(favoriteIte[1]);
        r.setName(name);
        r.setDiscrption(discroption);
        r.setDiscount(discount);
        r.setPhone(phone);
        r.setDate(date);
        r.setToken(token);
        r.setAdmin(true);
        data.push().setValue(r);
        Snackbar.make(rootlayout, "تم إضافة منجك بنجاح", Snackbar.LENGTH_SHORT)
                .show();

    }
    if(postion==1) {
        r.setImg1(favoriteIte[0]);
        r.setName(name);
        r.setDiscrption(discroption);
        r.setDiscount(discount);
        r.setPhone(phone);
        r.setDate(date);
        r.setToken(token);
        r.setAdmin(true);
        data.push().setValue(r);
        Snackbar.make(rootlayout, "تم إضافة منجك بنجاح", Snackbar.LENGTH_SHORT)
                .show();

    }}else {
        r.setName(name);
        r.setDiscrption(discroption);
        r.setDiscount(discount);
        r.setPhone(phone);
        r.setDate(date);
        r.setToken(token);
        r.setAdmin(true);
        data.push().setValue(r);
        Snackbar.make(rootlayout, "تم إضافة منجك بنجاح", Snackbar.LENGTH_SHORT)
                .show();
    }

}



}
