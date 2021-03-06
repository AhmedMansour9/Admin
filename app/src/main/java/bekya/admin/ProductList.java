package bekya.admin;

import android.*;
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
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Duration;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;


import ru.dimorinny.floatingtextbutton.FloatingTextButton;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ProductList extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener,itemViewinterface,imgclick{

    RecyclerView recyclerView;
    RelativeLayout rootlayout;
    String Nameone;
    Button btnUpload;
    Button btnselect;
    SwipeRefreshLayout mSwipeRefreshLayout;
    EditText govern, name, descrip, phone, price;
    private RequestPermissionListener mRequestPermissionHandler;
    GridView gridGallery;
    Handler handler;
    ImageView imgone, imgtwo, imgthree, imgfour;
    ImageView deltone, deletetwo, deletethree, deletefour;
    ImageView cameraone, cameratwo, camerathree, camerafour;
    EditText editname, editdiscrp, editdiscount, editphone;
    Button finish;
    GalleryAdapter adapter;
    ArrayList<Retrivedata> arrayadmin;
    ArrayList<String> listimages = new ArrayList<>();
    ViewSwitcher viewSwitcher;
    ImageLoader imageLoader;
    DatabaseReference data,dataadmin;
    private Adapteritems mAdapter;
    StorageReference storageRef;
    SharedPreferences.Editor editor;
    SharedPreferences.Editor editt;
    String imgOne, imgTwo, imgThree, imgFour;
    private Uri filePathone, filePathtwo, filePaththree, filePathfour;
    FirebaseStorage storage;
    StorageReference storageReference;
    public static String token;
    ArrayList<CustomGallery> dataT;
    String Govern, Name, Discrption, Phone, Price;
    String child,childadmin;
    Dialog update_items_layout;
    Dialog update_info_layout;
    public ChildEventListener mListener;
    public static String date2;
    EditText product;

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
        setContentView(R.layout.activity_product_list);
        token = SharedPrefManager.getInstance(getApplicationContext()).getDeviceToken();
        date2 = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(new Date());
        product = findViewById(R.id.findyourproduct);
        handler = new Handler();
        arrayadmin = new ArrayList<>();
        SharedPreferences shared = getSharedPreferences("cat", MODE_PRIVATE);
        child = shared.getString("Category", null);
        childadmin=shared.getString("categoryadmin",null);
        data = FirebaseDatabase.getInstance().getReference().child("Products").child(child);
        mRequestPermissionHandler = new RequestPermissionListener();
          dataadmin= FirebaseDatabase.getInstance().getReference().child("Products").child(childadmin);
        storage = FirebaseStorage.getInstance();
        rootlayout = findViewById(R.id.rootlayout);
        editor = getApplicationContext().getSharedPreferences("Photo", MODE_PRIVATE).edit();
        editor.clear();
        editor.commit();
        storageReference = storage.getReference();


        Recyclview();
        SwipRefresh();
        FloatingTextButton floatingTextButton = findViewById(R.id.fabbutton);
        floatingTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showaddFooddialog();
            }
        });

        RecycleviewSerach();
    }
    private void handleButtonClicked(){
        mRequestPermissionHandler.requestPermission(this, new String[] {
                android.Manifest.permission.READ_EXTERNAL_STORAGE
        }, 200, new RequestPermissionListener.RequestPermissionListene() {
            @Override
            public void onSuccess() {
                // Toast.makeText(ProductList.this, "request permission success", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(Action.ACTION_MULTIPLE_PICK);
                startActivityForResult(i, 200);

            }

            @Override
            public void onFailed() {
                Toast.makeText(ProductList.this, "request permission failed", Toast.LENGTH_SHORT).show();
            }
        });

    }


    public void SwipRefresh() {
        mSwipeRefreshLayout = findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                arrayadmin.clear();
                mAdapter.notifyDataSetChanged();
                Retrivedatauser();
                Retrivedataadmin();


            }
        });
    }

    public void Recyclview() {
        recyclerView = findViewById(R.id.recycler_product);
        recyclerView.setHasFixedSize(true);
        mAdapter = new Adapteritems(arrayadmin, ProductList.this);
        mAdapter.setClickListener(this);
        mAdapter.setClickButton(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

    }

    private boolean hasId(String idc) {
        if (!TextUtils.isEmpty(idc)) {
            for (Retrivedata fr : arrayadmin) {
                if (fr.getName().equals(idc)) {
                    return true;
                }
                break;
            }
        }
        return false;
    }
    @Override
    public void Callback(View v, int poistion) {

        if(!Adapteritems.filteredList.isEmpty()){
            Intent inty=new Intent(ProductList.this,ActivityOneItem.class);
            inty.putExtra("child",child);
            inty.putExtra("childadmin",childadmin);
            inty.putExtra("key", Adapteritems.filteredList.get(poistion).getImg1());
            inty.putExtra("name", Adapteritems.filteredList.get(poistion).getName());
            inty.putExtra("discrp", Adapteritems.filteredList.get(poistion).getDiscrption());
            inty.putExtra("discount", Adapteritems.filteredList.get(poistion).getDiscount());
            inty.putExtra("phone", Adapteritems.filteredList.get(poistion).getPhone());
            inty.putExtra("date", Adapteritems.filteredList.get(poistion).getDate());
            inty.putExtra("govern", Adapteritems.filteredList.get(poistion).getGovern());

            startActivity(inty);

        }else if(Adapteritems.filteredList.isEmpty()){
            Intent inty=new Intent(ProductList.this,ActivityOneItem.class);
            inty.putExtra("child",child);
            inty.putExtra("childadmin",childadmin);
            inty.putExtra("key", arrayadmin.get(poistion).getImg1());
            inty.putExtra("name", arrayadmin.get(poistion).getName());
            inty.putExtra("discrp", arrayadmin.get(poistion).getDiscrption());
            inty.putExtra("discount", arrayadmin.get(poistion).getDiscount());
            inty.putExtra("phone", arrayadmin.get(poistion).getPhone());
            inty.putExtra("date", arrayadmin.get(poistion).getDate());
            inty.putExtra("govern", arrayadmin.get(poistion).getGovern());

            startActivity(inty);

        }


    }
    public void RecycleviewSerach() {
        product.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                mAdapter.getFilter().filter(charSequence);
                mAdapter.notifyDataSetChanged();

            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    public void Retrivedatauser() {
        mSwipeRefreshLayout.setRefreshing(true);
     data.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()) {
                    Retrivedata r = dataSnapshot.getValue(Retrivedata.class);

                    String Date = r.getDate();
                    int days = GetDays(Date, ProductList.date2);
                    if (days > 90) {
                        mSwipeRefreshLayout.setRefreshing(false);
                    } else {
                        if (r != null && !hasId(r.getName())) {
                                arrayadmin.add( r);

                            mAdapter.notifyDataSetChanged();
                        }


                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                } else {
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void Retrivedataadmin() {
        mSwipeRefreshLayout.setRefreshing(true);
        mListener=dataadmin.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()) {
                    Retrivedata r = dataSnapshot.getValue(Retrivedata.class);

                    String Date = r.getDate();
                    int days = GetDays(Date, ProductList.date2);
                    if (days > 7) {
                        mSwipeRefreshLayout.setRefreshing(false);
                    } else {
                        if (r != null && !hasId(r.getName())) {
                                arrayadmin.add( r);
                             mAdapter.notifyDataSetChanged();

                        }

                        mSwipeRefreshLayout.setRefreshing(false);

                    }
                } else {
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
        update_info_layout = new Dialog(ProductList.this);
        update_info_layout.requestWindowFeature(Window.FEATURE_NO_TITLE);
        initImageLoader();
        update_info_layout.setContentView(R.layout.layout_add_product);
        name = update_info_layout.findViewById(R.id.Name);
        descrip = update_info_layout.findViewById(R.id.descrip);
        phone = update_info_layout.findViewById(R.id.phone);
        price = update_info_layout.findViewById(R.id.price);
        govern = update_info_layout.findViewById(R.id.govern);


        gridGallery = update_info_layout.findViewById(R.id.gridGallery);
        gridGallery.setFastScrollEnabled(true);
        adapter = new GalleryAdapter(getApplicationContext(), imageLoader);
        adapter.setMultiplePick(false);
        gridGallery.setAdapter(adapter);

        viewSwitcher = update_info_layout.findViewById(R.id.viewSwitcher);
        viewSwitcher.setDisplayedChild(1);

        btnUpload = update_info_layout.findViewById(R.id.btnupload);
        btnselect = update_info_layout.findViewById(R.id.btnselect);

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

        update_info_layout.show();
    }

    private void uploadimage() {

        Name = name.getText().toString().trim();
        Govern = govern.getText().toString().trim();

        Discrption = descrip.getText().toString().trim();
        Phone = phone.getText().toString().trim();
        Price = price.getText().toString().trim();

        if (Name.isEmpty() || Discrption.isEmpty() || Phone.isEmpty() || Price.isEmpty() || Govern.isEmpty()) {
            Toast.makeText(getBaseContext(), "من فضلك أملآ جميع البيانات", Toast.LENGTH_SHORT).show();

        } else if (dataT == null) {
            SavedSahredPrefrenceSwitch(Name, Discrption, Phone, Price,Govern);
        } else {
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
                                SavedSahredPrefrenceSwitch(Name, Discrption, Phone, Price,Govern);
                                update_info_layout.dismiss();
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
        handleButtonClicked();

//        Intent i = new Intent(Action.ACTION_MULTIPLE_PICK);
//        startActivityForResult(i, 200);

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

    public void SavedSahredPrefrenceSwitch(String name, String discroption, String phone, String discount,String govern) {

        SharedPreferences sharedPref = getSharedPreferences("Photo", MODE_PRIVATE);
        String jsonFavorit = sharedPref.getString("img", null);
        Gson gson3 = new Gson();
        String[] favoriteIte = gson3.fromJson(jsonFavorit, String[].class);
        Retrivedata r = new Retrivedata();
        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(new Date());

        if (jsonFavorit != null) {

            int postion = favoriteIte.length;
            if (postion == 4) {
                r.setImg1(favoriteIte[0]);
                r.setImg2(favoriteIte[1]);
                r.setImg3(favoriteIte[2]);
                r.setImg4(favoriteIte[3]);
                r.setName(name);
                r.setGovern(govern);
                r.setDiscrption(discroption);
                r.setDiscount(discount);
                r.setPhone(phone);
                r.setDate(date);
                r.setToken(token);
                r.setAdmin(true);
                dataadmin.push().setValue(r);
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
                r.setGovern(govern);
                r.setPhone(phone);
                r.setDate(date);
                r.setToken(token);
                r.setAdmin(true);
                dataadmin.push().setValue(r);
                Snackbar.make(rootlayout, "تم إضافة منجك بنجاح", Snackbar.LENGTH_SHORT)
                        .show();

            }
            if (postion == 2) {
                r.setImg1(favoriteIte[0]);
                r.setImg2(favoriteIte[1]);
                r.setName(name);
                r.setDiscrption(discroption);
                r.setDiscount(discount);
                r.setPhone(phone);
                r.setGovern(govern);
                r.setDate(date);
                r.setToken(token);
                r.setAdmin(true);
                dataadmin.push().setValue(r);
                Snackbar.make(rootlayout, "تم إضافة منجك بنجاح", Snackbar.LENGTH_SHORT)
                        .show();

            }
            if (postion == 1) {
                r.setImg1(favoriteIte[0]);
                r.setName(name);
                r.setDiscrption(discroption);
                r.setDiscount(discount);
                r.setPhone(phone);
                r.setGovern(govern);
                r.setDate(date);
                r.setToken(token);
                r.setAdmin(true);
                dataadmin.push().setValue(r);
                Snackbar.make(rootlayout, "تم إضافة منجك بنجاح", Snackbar.LENGTH_SHORT)
                        .show();

            }
        } else {
            r.setName(name);
            r.setDiscrption(discroption);
            r.setDiscount(discount);
            r.setPhone(phone);
            r.setGovern(govern);
            r.setDate(date);
            r.setToken(token);
            r.setAdmin(true);
            dataadmin.push().setValue(r);
            Snackbar.make(rootlayout, "تم إضافة منجك بنجاح", Snackbar.LENGTH_SHORT)
                    .show();
        }

    }



    @Override
    public void onRefresh() {
        arrayadmin.clear();
        mAdapter.notifyDataSetChanged();
        Retrivedatauser();
        Retrivedataadmin();


    }


    @Override
    public void onClickCallback(View view, int adapterPosition) {


    }

    @Override
    public void onClickdelete(View view, final int adapterPosition) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do You Want to Delete This Post ?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(Adapteritems.filteredList.isEmpty()) {
                     Name = arrayadmin.get(adapterPosition).getName();
                }else {
                     Name = Adapteritems.filteredList.get(adapterPosition).getName();
                }
                if(arrayadmin.get(adapterPosition).getAdmin()){

                    DeletetePost(childadmin,Name);
                }else {
                    DeletetePost(child,Name);
                }
                if(Adapteritems.filteredList.isEmpty()){
                    arrayadmin.remove(adapterPosition);
                    mAdapter.notifyDataSetChanged();
                }else {
                    Adapteritems.filteredList.remove(adapterPosition);
                    mAdapter.notifyDataSetChanged();

                }

                dialog.cancel();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();



    }
   public void DeletetePost(String child,String Name){
       data.removeEventListener(mListener);
       DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Products").child(child);
       databaseReference.orderByChild("name").equalTo(Name).addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {
               for (DataSnapshot data:dataSnapshot.getChildren()){
                   data.getRef().removeValue();
               }
           }

           @Override
           public void onCancelled(DatabaseError databaseError) {

           }
       });

   }
    public int GetDays(String dateone,String datetwo){
        String date1 = dateone;
        String date2 =datetwo;
        DateTimeFormatter formatter =  DateTimeFormat.forPattern("dd-MM-yyyy").withLocale(Locale.ENGLISH);
        DateTime d1 = formatter.parseDateTime(date1);
        DateTime d2 = formatter.parseDateTime(date2);
        long diffInMillis = d2.getMillis() - d1.getMillis();

        Duration duration = new Duration(diffInMillis);
        int days = (int) duration.getStandardDays();

        return days;
    }


}
