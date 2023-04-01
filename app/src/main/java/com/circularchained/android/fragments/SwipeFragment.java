package com.circularchained.android.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.circularchained.android.ProductDetail;
import com.circularchained.android.R;
import com.circularchained.android.adapters.ProductAdapter;
import com.circularchained.android.constants.Constants;
import com.circularchained.android.listeners.ProductItemClickListener;
import com.circularchained.android.models.Product;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class SwipeFragment extends Fragment  implements ProductItemClickListener {

    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private ListenerRegistration registration;
    private ProductAdapter adapter;
    private final List<Product> objectList = new ArrayList<>();

    public static final String ARGS_KEY = "count";
    public static String ARGS_TITLE = "topicTitle";
    private String title;

    public SwipeFragment() {
        // Required empty public constructor
    }

    public static SwipeFragment getInstance(int count, String title){
        Bundle args = new Bundle();
        args.putInt(ARGS_KEY, count);
        args.putString(ARGS_TITLE, title);
        SwipeFragment swipeFragment = new SwipeFragment();
        swipeFragment.setArguments(args);
        return swipeFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assert getArguments() != null;
        title = getArguments().getString(ARGS_TITLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_swipe, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        adapter = new ProductAdapter(requireContext(), objectList, this);
        recyclerView.setAdapter(adapter);
        fetchObjects(title);
        return view;
    }

    private void fetchObjects(String objectID){
        Query query = firebaseFirestore.collection(Constants.PRODUCTS).orderBy("id", Query.Direction.ASCENDING).whereArrayContains("tags",objectID);
        registration = query.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (queryDocumentSnapshots != null){
                for (DocumentChange documentChange: queryDocumentSnapshots.getDocumentChanges()){
                    if (documentChange.getType() == DocumentChange.Type.ADDED) {
                        Product object = documentChange.getDocument().toObject(Product.class);
                        if (!objectList.contains(object)){
                            objectList.add(object);
                            adapter.notifyDataSetChanged();
                        }
                    }else if (documentChange.getType()==DocumentChange.Type.MODIFIED){
                        Product object = documentChange.getDocument().toObject(Product.class);
                        if (objectList.contains(object)){
                            objectList.set(objectList.indexOf(object),object);
                            adapter.notifyItemChanged(objectList.indexOf(object));
                        }
                    }else if (documentChange.getType()==DocumentChange.Type.REMOVED){
                        Product object = documentChange.getDocument().toObject(Product.class);
                        if (objectList.contains(object)){
                            objectList.remove(object);
                            adapter.notifyItemRemoved(objectList.indexOf(object));
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchObjects(title);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (registration != null){
            registration.remove();
        }
    }




    @Override
    public void onProductItemClick(Product product, ImageView imageView) {
        Intent intent = new Intent(requireContext(), ProductDetail.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("object", product);
        intent.putExtras(bundle);
        ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity(), Pair.create(imageView, product.getId()));
        startActivity(intent,activityOptionsCompat.toBundle());
    }
}