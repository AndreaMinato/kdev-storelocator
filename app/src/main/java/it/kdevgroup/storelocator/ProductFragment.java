package it.kdevgroup.storelocator;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A fragment representing a list of Items.
 * <p/>
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class ProductFragment extends Fragment {

    public static final String STORE_KEY_FOR_BUNDLE = "Store";
    private Store currentStore = null;
    private RecyclerView recyclerView;
    private ProductListAdapter adapter;
    private OnFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ProductFragment() {
    }


    public static ProductFragment newInstance(Store store) {
        ProductFragment productFragment = new ProductFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(STORE_KEY_FOR_BUNDLE, store);
        productFragment.setArguments(bundle);
        return productFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.product_navigation, container, false);//super.onCreateView(inflater, container, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.product_recyclerView);

        if (getArguments() != null) {
            currentStore = getArguments().getParcelable(STORE_KEY_FOR_BUNDLE);
            adapter = new ProductListAdapter(currentStore.getProducts(), getContext());
            recyclerView.setAdapter(adapter);
        }

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnFragmentInteractionListener) context;
        } catch (ClassCastException e) {
//            throw new ClassCastException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(String id);
    }

}
