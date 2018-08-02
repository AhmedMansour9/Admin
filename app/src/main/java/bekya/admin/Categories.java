package bekya.admin;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;


import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class Categories extends Fragment implements  SwipeRefreshLayout.OnRefreshListener {


    public Categories() {
        // Required empty public constructor
    }
     View v;
    SwipeRefreshLayout mSwipeRefreshLayout;
    FirebaseDatabase database;
    DatabaseReference category;
    List<Category> listcatgory;
    GridView gridView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         v= inflater.inflate(R.layout.catogries, container, false);
        listcatgory=new ArrayList<>();
        String token = SharedPrefManager.getInstance(getContext()).getDeviceToken();

        database = FirebaseDatabase.getInstance();
        category = database.getReference("category");



        gridView = (GridView)v.findViewById(R.id.grid_view);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                SharedPreferences.Editor share= getActivity().getSharedPreferences("cat", MODE_PRIVATE).edit();
                share.putString("Category",listcatgory.get(position).getCatogories());
                share.putString("categoryadmin",listcatgory.get(position).getCategory());
                share.commit();
                startActivity(new Intent(getContext(),ProductList.class));

            }
        });


        SwipRefresh();
        return v;
    }
    public void SwipRefresh(){
        mSwipeRefreshLayout = v.findViewById(R.id.swipe_cont);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                RetriveCategories();
            }
        });
    }
    public void RetriveCategories(){
        listcatgory.clear();

        mSwipeRefreshLayout.setRefreshing(true);
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("Category");
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Category c=dataSnapshot.getValue(Category.class);
                listcatgory.add(c);
                gridView.setAdapter(new ImageAdapterGride(getActivity(),listcatgory));
                mSwipeRefreshLayout.setRefreshing(false);
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

    @Override
    public void onRefresh() {
        RetriveCategories();
    }
}
